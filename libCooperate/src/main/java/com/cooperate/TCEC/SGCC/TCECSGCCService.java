package com.cooperate.TCEC.SGCC;

import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.TCEC.util.CommonService;
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

public class TCECSGCCService {
    private static final Logger logger = LoggerFactory.getLogger(TCECSGCCService.class);

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
    public static Map<String, Map<String, Object>> mapRealData = new ConcurrentHashMap<String, Map<String, Object>>();

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

        send2TCECGuoWang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getChargeRespUrl(), params);
    }

    public static void sendStopChargeResp(Map<String, Object> params) {

        send2TCECGuoWang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getStopchargeRespUrl(), params);
    }

    public static void sendRealData(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getRealDataUrl());

        send2TCECGuoWang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getRealDataUrl(), params);
    }

    public static void sendEpStatusChange(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getStatusChangeUrl());

        send2TCECGuoWang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getStatusChangeUrl(), params);
    }

    public static void sendOrderInfo(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getOrderUrl());

        send2TCECGuoWang(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_GUOWANG).getOrderUrl(), params);
    }

    private static void send2TCECGuoWang(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("send2TCECGuoWang url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});

        //token值
        String token = staticToken;
        Date now = new Date();
        if (null == token || now.after(tokenAvailableTime)) {
            token = getGuoWangToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECSGCCPush.DATA_SECRET, TCECSGCCPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECSGCCPush.OPERATOR_ID, TCECSGCCPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCECGuoWang answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getGuoWangToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getGuoWangToken() {
        String operatorID = TCECSGCCPush.OPERATOR_ID;
        String operatorSecret = TCECSGCCPush.OPERATOR_SECRET;
        String dataSecret = TCECSGCCPush.DATA_SECRET;
        String dataSecret_iv = TCECSGCCPush.DATA_SECRET_IV;
        String sigSecret = TCECSGCCPush.SIG_SECRET;
        HashMap<String, String> dataParam = new HashMap<>();
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        logger.info("getGuoWangToken decryptToken is begin");
        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_GUOWANG,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getGuoWangToken decryptToken:{}", decryptToken);
        TokenModel tokenModel = new TokenModel();
        CommonPush.handleToken(decryptToken, tokenModel);
        tokenAvailableTime = tokenModel.getTokenAvailableTime();
        logger.info("getGuoWangToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getGuoWangToken StaticToken:{}", tokenModel.getStaticToken());
        return tokenModel.getStaticToken();
    }


    public static void startPushTimeout(long initDelay) {

        CheckTCECSGCCPushTask checkTask = new CheckTCECSGCCPushTask();

        TaskPoolFactory.scheduleAtFixedRate("CHECK_TCEC_PUSH_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void checkPushTimeout(Map<String, Map<String, Object>> mapRealData) {
        TokenModel tokenModel = getTokenModel();
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

    public static void checkPushTimeoutTest(Map<String, Map<String, Object>> mapRealData) {
        logger.info("GuoWang test push is begin");
        TokenModel tokenModel = getTokenModel();
        try {
            Iterator iter = mapRealData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Map<String, Object> pointMap = (Map<String, Object>) entry.getValue();
                String key = (String) entry.getKey();

                String[] val = key.split(Symbol.SHUXIAN_REG);
                if (val.length == 3) {
                    if (KeyConsts.STATUS_CHANGE_URL.equals(val[2])) {
                        tokenModel = CommonService.sendEpStatusChange(tokenModel, pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        tokenModel = CommonService.sendOrderInfo(tokenModel, pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        tokenModel = CommonService.sendStopChargeResp(tokenModel, pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        tokenModel = CommonService.sendChargeResp(tokenModel, pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        tokenModel = CommonService.sendRealData(tokenModel, pointMap);
                    }
                    staticToken = tokenModel.getStaticToken();
                    tokenAvailableTime = tokenModel.getTokenAvailableTime();
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }

    private static TokenModel getTokenModel() {
        TokenModel tokenModel = new TokenModel();
        tokenModel.setOrg(UserConstants.ORG_TCEC_GUOWANG);
        tokenModel.setStaticToken(staticToken);
        tokenModel.setTokenAvailableTime(tokenAvailableTime);
        tokenModel.setOperatorId(TCECSGCCPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECSGCCPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECSGCCPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECSGCCPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECSGCCPush.SIG_SECRET);
        return tokenModel;
    }

}
