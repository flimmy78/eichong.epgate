package com.cooperate.TCEC.alipay;

import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.TCEC.util.CommonService;
import com.cooperate.TCEC.util.TokenModel;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.Strings;
import com.ec.constants.Symbol;
import com.ec.constants.UserConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TCECAliPayService {
    private static final Logger logger = LoggerFactory.getLogger(TCECAliPayService.class);

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

    private static void send2TCECAliPay(String url, Map<String, Object> params) {
        TokenModel tokenModel = getTokenModel(UserConstants.ORG_TCEC_ALIPAY);
        CommonService.send2TCEC(tokenModel, url, params);

    }

    private static String getAliPayToken(TokenModel tokenModel) {
        String operatorID = TCECAliPayPush.OPERATOR_ID;
        String operatorSecret = TCECAliPayPush.OPERATOR_SECRET;
        String dataSecret = TCECAliPayPush.DATA_SECRET;
        String dataSecret_iv = TCECAliPayPush.DATA_SECRET_IV;
        String sigSecret = TCECAliPayPush.SIG_SECRET;

        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_ALIPAY,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getAliPayToken decryptToken:{}", decryptToken);
        CommonPush.handleToken(decryptToken, tokenModel);
        if (Strings.isNullOrEmpty(decryptToken)) {
            return null;
        }
        logger.info("getAliPayToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getAliPayToken StaticToken:{}", tokenModel.getStaticToken());
        CommonPush.updateToken(UserConstants.ORG_TCEC_ALIPAY, tokenModel);
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECAliPayPushTask checkTask = new CheckTCECAliPayPushTask();
        TaskPoolFactory.scheduleAtFixedRate("TCEC_ALIPAY_PUSH_TIMEOUT_TASK", checkTask, initDelay,
                CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getPeriod(), TimeUnit.SECONDS);
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
                        send2TCECAliPay(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECAliPay(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECAliPay(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECAliPay(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECAliPay(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_ALIPAY).getRealDataUrl(), pointMap);
                    }
                }
                removeRealData(key);
            }
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getMessage());
        }
    }

    private static TokenModel getTokenModel(int org) {

        TokenModel tokenModel = CommonPush.getCacheData(org);
        if (tokenModel != null) {
            return tokenModel;
        }
        tokenModel = new TokenModel();
        tokenModel.setOrg(UserConstants.ORG_TCEC_ALIPAY);
        tokenModel.setStaticToken("");
        tokenModel.setTokenAvailableTime(new Date());
        tokenModel.setOperatorId(TCECAliPayPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECAliPayPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECAliPayPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECAliPayPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECAliPayPush.SIG_SECRET);
        return tokenModel;
    }
}
