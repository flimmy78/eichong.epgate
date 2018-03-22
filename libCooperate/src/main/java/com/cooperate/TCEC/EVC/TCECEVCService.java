package com.cooperate.TCEC.EVC;

import com.alibaba.fastjson.JSON;
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

public class TCECEVCService {
    private static final Logger logger = LoggerFactory.getLogger(TCECEVCService.class);

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
    private static ConcurrentHashMap<String, Map<String, Object>> mapRealData = new ConcurrentHashMap<>();

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

        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getChargeRespUrl(), params);
    }

    public static void sendStopChargeResp(Map<String, Object> params) {

        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getStopchargeRespUrl(), params);
    }

    public static void sendRealData(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(UserConstants.ORG_EVC).getRealDataUrl());

        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getRealDataUrl(), params);
    }

    public static void sendEpStatusChange(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_EVC).getStatusChangeUrl());

        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getStatusChangeUrl(), params);
    }

    public static void sendOrderInfo(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(UserConstants.ORG_EVC).getOrderUrl());

        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getOrderUrl(), params);
    }


    private static void send2TCECEVC(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("send2TCECEVC url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});

        //token值
        String token = staticToken;
        Date now = new Date();
        if (null == token || now.after(tokenAvailableTime)) {
            token = getEVCToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECEVCPush.DATA_SECRET, TCECEVCPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECEVCPush.OPERATOR_ID, TCECEVCPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCECEVC answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getEVCToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getEVCToken() {
        String operatorID = TCECEVCPush.OPERATOR_ID;
        String operatorSecret = TCECEVCPush.OPERATOR_SECRET;
        String dataSecret = TCECEVCPush.DATA_SECRET;
        String dataSecret_iv = TCECEVCPush.DATA_SECRET_IV;
        String sigSecret = TCECEVCPush.SIG_SECRET;

        logger.info("getEVCToken decryptToken is begin");
        String decryptToken = CommonPush.getToken(UserConstants.ORG_EVC,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getEVCToken decryptToken:{}", decryptToken);
        TokenModel tokenModel = new TokenModel();
        CommonPush.handleToken(decryptToken, tokenModel);
        tokenAvailableTime = tokenModel.getTokenAvailableTime();
        logger.info("getEVCToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getEVCToken StaticToken:{}", tokenModel.getStaticToken());
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {
        logger.info("startPushTimeout EVC is begin;delay:{}", initDelay);
        CheckTCECEVCPushTask checkTask = new CheckTCECEVCPushTask();
        TaskPoolFactory.scheduleAtFixedRate("CHECK_TCEC_PUSH_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void checkPushTimeout() {
        try {
            logger.info("checkPushTimeout mapRealData.size:{}", mapRealData.size());
            Iterator iter = mapRealData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Map<String, Object> pointMap = (Map<String, Object>) entry.getValue();
                String key = (String) entry.getKey();
                String[] val = key.split(Symbol.SHUXIAN_REG);
                logger.info("checkPushTimeout mapRealData.size:{}", mapRealData.size());
                if (val.length == 3) {
                    if (KeyConsts.STATUS_CHANGE_URL.equals(val[2])) {
                        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECEVC(CooperateFactory.getCoPush(UserConstants.ORG_EVC).getRealDataUrl(), pointMap);
                    }
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }
    public static void checkPushTimeoutTest() {
        logger.info("GuoWang test push is begin");
        logger.info("checkPushTimeout mapRealData:{}", mapRealData);
        TokenModel tokenModel = getTokenModel();
        logger.info("checkPushTimeout tokenModel:{}", JSON.toJSONString(tokenModel));
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
        tokenModel.setOrg(UserConstants.ORG_EVC);
        tokenModel.setStaticToken(staticToken);
        tokenModel.setTokenAvailableTime(tokenAvailableTime);
        tokenModel.setOperatorId(TCECEVCPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECEVCPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECEVCPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECEVCPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECEVCPush.SIG_SECRET);
        return tokenModel;
    }
}
