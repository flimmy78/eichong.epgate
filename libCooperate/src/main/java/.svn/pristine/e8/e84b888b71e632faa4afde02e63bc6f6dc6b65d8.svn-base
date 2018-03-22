package com.cooperate.TCEC;

import com.cooperate.CooperateFactory;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TCECEChongService {
    private static final Logger logger = LoggerFactory.getLogger(TCECEChongService.class);

    /**
     * 凭证有效期
     */
    private static Date tokenAvailableTime = new Date();
    /**
     * token值
     */
    private static String staticToken = "";
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
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ECHONG).getStatusChangeUrl());

        send2TCEC(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ECHONG).getStatusChangeUrl(), params);
    }

    public static void sendOrderInfo(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ECHONG).getOrderUrl());

        send2TCEC(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ECHONG).getOrderUrl(), params);
    }

    private static void send2TCEC(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("url|params2|tokenAvailableTime"), new Object[]{url, params, tokenAvailableTime});
        Map<String, Object> encData = new HashMap<>();
        encData.put("ConnectorStatusInfo", params);
        //token值
        String token = staticToken;
        Date now = new Date();
        if (now.compareTo(tokenAvailableTime) >= 0) {
            token = getToken();
        }
        JSONObject jsonObject = JSONObject.fromObject(encData);
        String data;


        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECEChongPush.DATA_SECRET, TCECEChongPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECEChongPush.OPERATOR_ID, TCECEChongPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("answerStr:", answerStr);
            Map response = net.sf.json.JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getToken();
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getToken() {
        String operatorID = TCECEChongPush.OPERATOR_ID;
        String operatorSecret = TCECEChongPush.OPERATOR_SECRET;
        HashMap<String, String> dataParam = new HashMap<>();
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        JSONObject jsonObject = JSONObject.fromObject(dataParam);
        String data;
        String key = "", errMsg = "", retCode = "", accessToken = "";
        boolean isWrongflag = false;
        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECEChongPush.DATA_SECRET, TCECEChongPush.DATA_SECRET_IV);
            //生成签名
            HashMap<String, String> map = SigTool.makeSig(data, TCECEChongPush.OPERATOR_ID, TCECEChongPush.SIG_SECRET);

            //发送请求
            String answerStr = HttpUtils.httpJSONPost(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ECHONG).getTokenUrl(), map, null);
            //将String转成Map
            JSONObject retMap = JSONObject.fromObject(answerStr);
            String decryptToken = AesCBC.getInstance().decrypt(retMap.getString("Data"), "utf-8", TCECEChongPush.DATA_SECRET, TCECEChongPush.DATA_SECRET_IV);
            //记录返回结果
            JSONObject retTokenValue = JSONObject.fromObject(decryptToken);
            Iterator<String> keys = retTokenValue.keys();

            while (keys.hasNext()) {
                key = (String) keys.next();

                if ("SuccStat".equals(key) && !"0".equals(retTokenValue.get(key).toString())) {
                    isWrongflag = true;
                }

                if ("AccessToken".equals(key)) {
                    accessToken = retTokenValue.get(key).toString();
                }

                if ("FailReason".equals(key)) {
                    retCode = retTokenValue.get(key).toString();
                    if (retCode.equals("0")) {
                        errMsg = "无";
                    } else if (retCode.equals("1")) {
                        errMsg = "无此运营商";
                    } else if (retCode.equals("2")) {
                        errMsg = "密钥错误";
                    } else {
                        errMsg = "未知";
                    }
                }

                if ("TokenAvailableTime".equals(key)) {
                    int availTime = Integer.parseInt(retTokenValue.get(key).toString());

                    Calendar calObject = Calendar.getInstance();
                    calObject.setTime(new Date());
                    calObject.add(Calendar.SECOND, (int) availTime);
                    tokenAvailableTime = calObject.getTime();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isWrongflag && errMsg.length() > 0) {
            logger.error(LogUtil.addExtLog("response token is wrong; msg:{}"),
                    new Object[]{errMsg});
            return null;
        }
        if (accessToken.length() < 0) {
            logger.error(LogUtil.addExtLog("response accessToken is null"));
            return null;
        } else {
            accessToken = KeyConsts.AUTH_TOKEN + " " + accessToken;//e充网说要加个空格
            staticToken = accessToken;
            return accessToken;
        }
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECEChongPushTask checkTask = new CheckTCECEChongPushTask();

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
                    }
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }


}
