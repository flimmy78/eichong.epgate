package com.cooperate.TCEC.hainan;

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

public class TCECHaiNanService {
    private static final Logger logger = LoggerFactory.getLogger(TCECHaiNanService.class);

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

    private static void send2TCECHaiNan(String url, Map<String, Object> params) {
        TokenModel tokenModel = getTokenModel(UserConstants.ORG_TCEC_HAINAN);
        CommonService.send2TCEC(tokenModel, url, params);


    }

    private static String getHaiNanToken(TokenModel tokenModel) {
        String operatorID = TCECHaiNanPush.OPERATOR_ID;
        String operatorSecret = TCECHaiNanPush.OPERATOR_SECRET;
        String dataSecret = TCECHaiNanPush.DATA_SECRET;
        String dataSecret_iv = TCECHaiNanPush.DATA_SECRET_IV;
        String sigSecret = TCECHaiNanPush.SIG_SECRET;

        String decryptToken = CommonPush.getToken(UserConstants.ORG_TCEC_HAINAN,
                operatorID, operatorSecret, dataSecret, dataSecret_iv, sigSecret);
        logger.info("getHaiNanToken decryptToken:{}", decryptToken);
        if (Strings.isNullOrEmpty(decryptToken)) {
            return null;
        }
        CommonPush.handleToken(decryptToken, tokenModel);
        logger.info("getHaiNanToken tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getHaiNanToken StaticToken:{}", tokenModel.getStaticToken());
        CommonPush.updateToken(UserConstants.ORG_TCEC_HAINAN, tokenModel);
        return tokenModel.getStaticToken();
    }

    public static void startPushTimeout(long initDelay) {

        CheckTCECHaiNanPushTask checkTask = new CheckTCECHaiNanPushTask();

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
                        send2TCECHaiNan(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HAINAN).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECHaiNan(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HAINAN).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECHaiNan(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HAINAN).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECHaiNan(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HAINAN).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECHaiNan(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_HAINAN).getRealDataUrl(), pointMap);
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
        tokenModel.setOrg(UserConstants.ORG_TCEC_HAINAN);
        tokenModel.setStaticToken("");
        tokenModel.setTokenAvailableTime(new Date());
        tokenModel.setOperatorId(TCECHaiNanPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECHaiNanPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECHaiNanPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECHaiNanPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECHaiNanPush.SIG_SECRET);
        return tokenModel;
    }

}
