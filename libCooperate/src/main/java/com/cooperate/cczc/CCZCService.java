package com.cooperate.cczc;

import com.cooperate.CooperateFactory;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.HttpUtils;
import com.cooperate.utils.SigTool;
import com.ec.constants.Symbol;
import com.ec.constants.UserConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CCZCService {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(CCZCService.class.getName()));

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

    public static String sendChargeResp(Map<String, Object> params) {

        return send2CCZC(CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getChargeRespUrl(), params);
    }

    public static String sendStopChargeResp(Map<String, Object> params) {

        return send2CCZC(CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getStopchargeRespUrl(), params);
    }

    public static String sendRealData(Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getRealDataUrl());

        return send2CCZC(CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getRealDataUrl(), params);
    }

    public static String sendOrderInfo(Map<String, Object> params) {

        return send2CCZC(CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getOrderUrl(), params);
    }

    private static String send2CCZC(String url, Map<String, Object> params) {
        logger.debug(LogUtil.addExtLog("url|params"), url, params);

        fullParams(params);
        if (null == params) {
            logger.error(LogUtil.addExtLog("is fail;url"), url);
            return null;
        }
        String response = null;
        try {
            response = HttpUtils.httpPostObject(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(LogUtil.addExtLog("response"), response);
        return response;
    }

    private static void fullParams(Map<String, Object> map) {
        String sig = SigTool.getSignMd5(map, CooperateFactory.getCoPush(UserConstants.ORG_CCZC).getAppSecret());
        if (null == sig) {
            logger.error(LogUtil.addExtLog("sig generate is fail;map"), map);
            map = null;
        } else {
            map.put("sign", sig);
        }
    }

    public static void startCCZCPushTimeout(long initDelay) {

        CheckCCZCPushTask checkTask = new CheckCCZCPushTask();

        TaskPoolFactory.scheduleAtFixedRate("CHECK_CCZCPUSH_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void checkCCZCPushTimeout() {
        try {
            Iterator iter = mapRealData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Map<String, Object> pointMap = (Map<String, Object>) entry.getValue();
                String key = (String) entry.getKey();

                try {
                    String[] val = key.split(Symbol.SHUXIAN_REG);
                    if (val.length == 3) {
                        if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                            sendChargeResp(pointMap);
                        } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                            sendStopChargeResp(pointMap);
                        } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                            sendRealData(pointMap);
                        } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                            sendOrderInfo(pointMap);
                        }
                    }
                } catch (Exception e) {
                    logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }
}
