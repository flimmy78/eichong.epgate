package com.cooperate.TCEC.chuanhua;

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

public class TCECChuanHuaService {
    private static final Logger logger = LoggerFactory.getLogger(TCECChuanHuaService.class);


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


    private static void send2TCECChuanHua(String url, Map<String, Object> params) {
        TokenModel tokenModel = getTokenModel(UserConstants.ORG_TCEC_CHUANHUA);
        CommonService.send2TCEC(tokenModel, url, params);
    }

    private static String getChuanHuaToken(TokenModel tokenModel) {
        String operatorID = TCECChuanHuaPush.OPERATOR_ID;
        String operatorSecret = TCECChuanHuaPush.OPERATOR_SECRET;
        String dataSecret = TCECChuanHuaPush.DATA_SECRET;
        String dataSecret_iv = TCECChuanHuaPush.DATA_SECRET_IV;
        String sigSecret = TCECChuanHuaPush.SIG_SECRET;

        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_CHUANHUA,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getChuanHuaToken decryptToken:{}", decryptToken);
        CommonPush.handleToken(decryptToken, tokenModel);
        if (Strings.isNullOrEmpty(decryptToken)) {
            return null;
        }
        logger.info("getChuanHuaToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getChuanHuaToken StaticToken:{}", tokenModel.getStaticToken());
        CommonPush.updateToken(UserConstants.ORG_TCEC_CHUANHUA, tokenModel);
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {
        logger.info("startPushTimeout EVC is begin;delay:{}", initDelay);
        CheckTCECChuanHuaPushTask checkTask = new CheckTCECChuanHuaPushTask();
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
                        send2TCECChuanHua(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_CHUANHUA).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECChuanHua(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_CHUANHUA).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECChuanHua(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_CHUANHUA).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECChuanHua(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_CHUANHUA).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECChuanHua(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_CHUANHUA).getRealDataUrl(), pointMap);
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
        tokenModel.setOrg(UserConstants.ORG_TCEC_CHUANHUA);
        tokenModel.setStaticToken("");
        tokenModel.setTokenAvailableTime(new Date());
        tokenModel.setOperatorId(TCECChuanHuaPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECChuanHuaPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECChuanHuaPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECChuanHuaPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECChuanHuaPush.SIG_SECRET);
        return tokenModel;
    }
}



