package com.cooperate.TCEC.qingxiang;

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

public class TCECEQingXiangService {
    private static final Logger logger = LoggerFactory.getLogger(TCECEQingXiangService.class);

    /**
     * 凭证有效期
     */
    private static Date tokenAvailableTime;
    /**
     * token值
     */
    private static String staticToken = null;
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

    public static void sendChargeResp(Map<String, Object> params) {

        send2TCECQingXiang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getChargeRespUrl(), params);
    }

    public static void sendStopChargeResp(Map<String, Object> params) {

        send2TCECQingXiang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getStopchargeRespUrl(), params);
    }

    public static void sendRealData(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(UserConstants.ORG_EC).getRealDataUrl());

        send2TCECQingXiang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getRealDataUrl(), params);
    }

    public static void sendEpStatusChange(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getStatusChangeUrl());

        send2TCECQingXiang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getStatusChangeUrl(), params);
    }

    public static void sendOrderInfo(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getOrderUrl());

        send2TCECQingXiang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_BEIQI).getOrderUrl(), params);
    }

    private static void send2TCECQingXiang(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("send2TCECQingXiang url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});

        //token值
        String token = staticToken;
        Date now = new Date();
        if (null == token || now.after(tokenAvailableTime)) {
            token = getQingXiangToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECEQingXiangPush.DATA_SECRET, TCECEQingXiangPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECEQingXiangPush.OPERATOR_ID, TCECEQingXiangPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCECQingXiang answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getQingXiangToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getQingXiangToken() {
        String operatorID = TCECEQingXiangPush.OPERATOR_ID;
        String operatorSecret = TCECEQingXiangPush.OPERATOR_SECRET;
        String dataSecret = TCECEQingXiangPush.DATA_SECRET;
        String dataSecret_iv = TCECEQingXiangPush.DATA_SECRET_IV;
        String sigSecret = TCECEQingXiangPush.SIG_SECRET;
        HashMap<String, String> dataParam = new HashMap<>();
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        logger.info("getQingXiangToken decryptToken is begin");
        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_BEIQI,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getQingXiangToken decryptToken:{}", decryptToken);
        TokenModel tokenModel = new TokenModel();
        CommonPush.handleToken(decryptToken, tokenModel);
        tokenAvailableTime = tokenModel.getTokenAvailableTime();
        logger.info("getQingXiangToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getQingXiangToken StaticToken:{}", tokenModel.getStaticToken());
        return tokenModel.getStaticToken();
    }


    public static void startPushTimeout(long initDelay) {

        CheckTCECQingXiangPushTask checkTask = new CheckTCECQingXiangPushTask();

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
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        sendOrderInfo(pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        sendStopChargeResp(pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        sendChargeResp(pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        sendRealData(pointMap);
                    }
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }


}
