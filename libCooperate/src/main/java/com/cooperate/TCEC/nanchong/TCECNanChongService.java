package com.cooperate.TCEC.nanchong;

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

public class TCECNanChongService {
    private static final Logger logger = LoggerFactory.getLogger(TCECNanChongService.class);

     
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

  

    private static void send2TCECNanChong(String url, Map<String, Object> params) {
        TokenModel tokenModel = getTokenModel(UserConstants.ORG_TCEC_NANCHONG);
        CommonService.send2TCEC(tokenModel, url, params);


    }

    private static String getNanChongToken(TokenModel tokenModel) {
        String operatorID = TCECNanChongPush.OPERATOR_ID;
        String operatorSecret = TCECNanChongPush.OPERATOR_SECRET;
        String dataSecret = TCECNanChongPush.DATA_SECRET;
        String dataSecret_iv = TCECNanChongPush.DATA_SECRET_IV;
        String sigSecret = TCECNanChongPush.SIG_SECRET;

        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_NANCHONG,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getNanChongToken decryptToken:{}", decryptToken);
        if (Strings.isNullOrEmpty(decryptToken)) {
            return null;
        }
        CommonPush.handleToken(decryptToken, tokenModel);
        logger.debug("getNanChongToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.debug("getNanChongToken StaticToken:{}", tokenModel.getStaticToken());
        CommonPush.updateToken(UserConstants.ORG_TCEC_NANCHONG, tokenModel);
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECNanChongPushTask checkTask = new CheckTCECNanChongPushTask();
        TaskPoolFactory.scheduleAtFixedRate("TCEC_NANCHONG_PUSH_TIMEOUT_TASK", checkTask, initDelay,
                CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getPeriod(), TimeUnit.SECONDS);
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
                        send2TCECNanChong(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECNanChong(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECNanChong(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECNanChong(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECNanChong(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANCHONG).getRealDataUrl(), pointMap);
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
        tokenModel.setOrg(UserConstants.ORG_TCEC_NANCHONG);
        tokenModel.setStaticToken("");
        tokenModel.setTokenAvailableTime(new Date());
        tokenModel.setOperatorId(TCECNanChongPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECNanChongPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECNanChongPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECNanChongPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECNanChongPush.SIG_SECRET);
        return tokenModel;
    }

}
