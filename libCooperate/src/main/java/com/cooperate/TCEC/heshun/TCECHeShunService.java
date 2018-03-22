package com.cooperate.TCEC.heshun;

import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.TCEC.util.TokenModel;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.AesCBC;
import com.cooperate.utils.HttpUtils;
import com.cooperate.utils.SigTool;
import com.ec.constants.Symbol;
import com.ec.constants.UserConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.LogUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TCECHeShunService {
    private static final Logger logger = LoggerFactory.getLogger(TCECHeShunService.class);

    /**
     * 凭证有效期
     */
    private static Date tokenAvailableTime = new Date();
    /**
     * token值
     */
    private static String staticToken = null;
    /**
     * 自增序列
     */
    private static String seq = "0001";
    /**
     * 实时数据key:epCode+epGun|time|send_url
     * 消费记录key:epCode+epGun|time|send_url
     */
    private static Map<String, Map<String, Object>> mapRealData = new ConcurrentHashMap<String, Map<String, Object>>();

    public static Map<String, Object> getRealData(String key) {
        return mapRealData.get(key);
    }

    public static void addRealData(String key, Map<String, Object> pointMap) {
        mapRealData.put(key, pointMap);
    }

    public static void removeRealData(String key) {
        mapRealData.remove(key);
    }


    public static void sendEpStatusChange(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HESHUN).getStatusChangeUrl());

        send2TCECHeShun(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HESHUN).getStatusChangeUrl(), params);
    }

    private static void send2TCECHeShun(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("send2TCECHeShun url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});

        //token值
        String token = staticToken;
        Date now = new Date();
        if (null == token || now.after(tokenAvailableTime)) {
            token = getHeShunToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECHeShunPush.DATA_SECRET, TCECHeShunPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECHeShunPush.OPERATOR_ID, TCECHeShunPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCECHeShun answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getHeShunToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getHeShunToken() {
        String operatorID = TCECHeShunPush.OPERATOR_ID;
        String operatorSecret = TCECHeShunPush.OPERATOR_SECRET;
        HashMap<String, String> dataParam = new HashMap<>();
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        logger.info("getHeShunToken decryptToken is begin");
        String decryptToken = CommonPush.getTokenModelByHtml(operatorID);
        logger.info("getHeShunToken decryptToken:{}", decryptToken);
        TokenModel tokenModel = new TokenModel();
        CommonPush.handleToken(decryptToken, tokenModel);
        tokenAvailableTime = tokenModel.getTokenAvailableTime();
        logger.info("getHeShunToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getHeShunToken StaticToken:{}", tokenModel.getStaticToken());
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECHeShunPushTask checkTask = new CheckTCECHeShunPushTask();

        TaskPoolFactory.scheduleAtFixedRate("CHECK_TCEC_PUSH_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void checkPushTimeout() {
        try {
            Iterator iter = mapRealData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Map<String, Object> pointMap = (Map<String, Object>) entry.getValue();
                String key = (String) entry.getKey();
                String[] val = key.split(Symbol.SHUXIAN_REG);
                if (val.length == 3) {
                    if (KeyConsts.STATUS_CHANGE_URL.equals(val[2])) {
                        sendEpStatusChange(pointMap);
                    }
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }


}
