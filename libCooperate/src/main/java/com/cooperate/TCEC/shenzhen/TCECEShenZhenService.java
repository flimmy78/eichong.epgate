package com.cooperate.TCEC.shenzhen;

import com.alibaba.fastjson.JSON;
import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.AesCBC;
import com.cooperate.utils.HttpUtils;
import com.cooperate.utils.SigTool;
import com.ec.constants.Symbol;
import com.ec.constants.UserConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TCECEShenZhenService {
    private static final Logger logger = LoggerFactory.getLogger(TCECEShenZhenService.class);

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

    public static void sendChargeResp(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("sendChargeResp"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getChargeRespUrl());

        send2TCECShenZhen(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getChargeRespUrl(), params);
    }

    public static void sendStopChargeResp(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("sendStopChargeResp"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getStopchargeRespUrl());

        send2TCECShenZhen(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getStopchargeRespUrl(), params);
    }

    public static void sendRealData(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getRealDataUrl());

        send2TCECShenZhen(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getRealDataUrl(), params);
    }

    public static void sendEpStatusChange(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getStatusChangeUrl());

        send2TCECShenZhen(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getStatusChangeUrl(), params);
    }

    public static void sendOrderInfo(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getOrderUrl());

        send2TCECShenZhen(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_SHENZHEN).getOrderUrl(), params);
    }

    private static void send2TCECShenZhen(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("send2TCECShenZhen url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});

        //token值
        String token = staticToken;
        Date now = new Date();
        if (null == token || now.after(tokenAvailableTime)) {
            token = getShenZhenToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECEShenZhenPush.DATA_SECRET, TCECEShenZhenPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECEShenZhenPush.OPERATOR_ID, TCECEShenZhenPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCECShenZhen answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getShenZhenToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getShenZhenToken() {
        String operatorID = TCECEShenZhenPush.OPERATOR_ID;
        String operatorSecret = TCECEShenZhenPush.OPERATOR_SECRET;
        String dataSecret = TCECEShenZhenPush.DATA_SECRET;
        String dataSecret_iv = TCECEShenZhenPush.DATA_SECRET_IV;
        String sigSecret = TCECEShenZhenPush.SIG_SECRET;
        HashMap<String, String> dataParam = new HashMap<>();
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        String key, errorMsg = "", errorCode, accessToken = "";
        boolean isWrongFlag = false;
        logger.info("getShenZhenToken decryptToken is begin");
        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_SHENZHEN,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getShenZhenToken decryptToken:{}", decryptToken);
        JSONObject retTokenValue = JSONObject.fromObject(decryptToken);
        Iterator<String> keys = retTokenValue.keys();
        while (keys.hasNext()) {
            key = keys.next();

            if ("SuccStat".equals(key) && !"0".equals(retTokenValue.get(key).toString())) {
                isWrongFlag = true;
            }

            if ("AccessToken".equals(key)) {
                accessToken = retTokenValue.get(key).toString();
            }

            if ("FailReason".equals(key)) {
                errorCode = retTokenValue.get(key).toString();
                if (errorCode.equals("0")) {
                    errorMsg = "无";
                } else if (errorCode.equals("1")) {
                    errorMsg = "无此运营商";
                } else if (errorCode.equals("2")) {
                    errorMsg = "密钥错误";
                } else {
                    errorMsg = "未知";
                }
            }

            if ("TokenAvailableTime".equals(key)) {
                int availTime = Integer.parseInt(retTokenValue.get(key).toString());
                Calendar calObject = Calendar.getInstance();
                calObject.setTime(new Date());
                calObject.add(Calendar.SECOND, availTime - 10);
                tokenAvailableTime = calObject.getTime();
            }
        }

        if (isWrongFlag && errorMsg.length() > 0) {
            logger.error(LogUtil.addExtLog("response token is wrong; msg:{}"),
                    new Object[]{errorMsg});
            return null;
        }
        if (accessToken.length() < 0) {
            logger.error(LogUtil.addExtLog("response accessToken is null"));
            return null;
        } else {
            accessToken = KeyConsts.AUTH_TOKEN + accessToken;//e充网说要加个空格
            staticToken = accessToken;
            return accessToken;
        }
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECShenZhenPushTask checkTask = new CheckTCECShenZhenPushTask();

        TaskPoolFactory.scheduleAtFixedRate("CHECK_TCEC_PUSH_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        Map<String, Map<String, Object>> mapRealData = new ConcurrentHashMap<String, Map<String, Object>>();

        mapRealData.put("3301061019981728" + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, new HashMap<>());
        mapRealData.put("3301061019981729" + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, new HashMap<>());
        mapRealData.put("3301061019981721" + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, new HashMap<>());
        mapRealData.put("3301061019981722" + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, new HashMap<>());

        Iterator iter = mapRealData.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Map<String, Object> pointMap = (Map<String, Object>) entry.getValue();
            String key = (String) entry.getKey();
            String[] val = key.split(Symbol.SHUXIAN_REG);
            System.out.println(key);
            System.out.println(JSON.toJSONString(val));
            System.out.println(val.length);
        }
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
