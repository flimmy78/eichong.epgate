package com.cooperate.TCEC.nanrui;

import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.Changan.TCECChangAnPush;
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

public class TCECENanRuiService {
    private static final Logger logger = LoggerFactory.getLogger(TCECENanRuiService.class);
    /**
     * 实时数据key:epCode+epGun|time|send_url
     * 消费记录key:epCode+epGun|time|send_url
     */
    private static Map<String, Map<String, Object>> mapRealData = new ConcurrentHashMap<>();

    public static Map<String, Object> getRealData(String key) {
        return mapRealData.get(key);
    }

    public static void addRealData(String key, Map<String, Object> pointMap) {
        mapRealData.put(key, pointMap);
    }

    public static void removeRealData(String key) {
        mapRealData.remove(key);
    }

    private static void send2TCECNanRui(String url, Map<String, Object> params) {
        TokenModel tokenModel = getTokenModel(UserConstants.ORG_TCEC_NANRUI);
        CommonService.send2TCEC(tokenModel, url, params);
    }

    public static void startPushTimeout(long initDelay) {
        CheckTCECNanRuiPushTask checkTask = new CheckTCECNanRuiPushTask();
        TaskPoolFactory.scheduleAtFixedRate("TCEC_NANRUI_PUSH_TIMEOUT_TASK", checkTask, initDelay,
                CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getPeriod(), TimeUnit.SECONDS);
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
                        send2TCECNanRui(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getStatusChangeUrl(), pointMap);
                    } else if (KeyConsts.ORDER_URL.equals(val[2])) {
                        send2TCECNanRui(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getOrderUrl(), pointMap);
                    } else if (KeyConsts.STOP_CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECNanRui(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getStopchargeRespUrl(), pointMap);
                    } else if (KeyConsts.CHARGE_RESP_URL.equals(val[2])) {
                        send2TCECNanRui(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getChargeRespUrl(), pointMap);
                    } else if (KeyConsts.REAL_DATA_URL.equals(val[2])) {
                        send2TCECNanRui(CooperateFactory.getCoPush(UserConstants.ORG_TCEC_NANRUI).getRealDataUrl(), pointMap);
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
        tokenModel.setOrg(UserConstants.ORG_TCEC_NANRUI);
        tokenModel.setStaticToken("");
        tokenModel.setTokenAvailableTime(new Date());
        tokenModel.setOperatorId(TCECNanRuiPush.OPERATOR_ID);
        tokenModel.setOperatorSecret(TCECNanRuiPush.OPERATOR_SECRET);
        tokenModel.setDataSecret(TCECNanRuiPush.DATA_SECRET);
        tokenModel.setDataSecretIv(TCECNanRuiPush.DATA_SECRET_IV);
        tokenModel.setSigSecret(TCECNanRuiPush.SIG_SECRET);
        return tokenModel;
    }


}
