package com.cooperate.TCEC.util;

import com.cooperate.CooperateFactory;
import com.cooperate.TCEC.SGCC.TCECSGCCPush;
import com.cooperate.utils.AesCBC;
import com.cooperate.utils.HttpUtils;
import com.cooperate.utils.SigTool;
import com.ec.utils.LogUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyaoyi on 2018/2/24.
 */
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);


    public static TokenModel sendChargeResp(TokenModel tokenModel, Map<String, Object> params) {
        int org = tokenModel.getOrg();
        logger.debug(LogUtil.addExtLog("CHARGE_URL"), CooperateFactory.getCoPush(org).getChargeRespUrl());

        return send2TCEC(tokenModel, CooperateFactory.getCoPush(org).getChargeRespUrl(), params);
    }

    public static TokenModel sendStopChargeResp(TokenModel tokenModel, Map<String, Object> params) {
        int org = tokenModel.getOrg();
        logger.debug(LogUtil.addExtLog("STOP_CHARGE_URL"), CooperateFactory.getCoPush(org).getStopchargeRespUrl());

        return send2TCEC(tokenModel, CooperateFactory.getCoPush(org).getStopchargeRespUrl(), params);
    }

    public static TokenModel sendRealData(TokenModel tokenModel, Map<String, Object> params) {
        int org = tokenModel.getOrg();
        logger.debug(LogUtil.addExtLog("REAL_DATA_URL"), CooperateFactory.getCoPush(org).getRealDataUrl());

        return send2TCEC(tokenModel, CooperateFactory.getCoPush(org).getRealDataUrl(), params);
    }

    public static TokenModel sendEpStatusChange(TokenModel tokenModel, Map<String, Object> params) {
        int org = tokenModel.getOrg();
        logger.debug(LogUtil.addExtLog("E_EP_STATUS_CHANGE_URL"), CooperateFactory.getCoPush(org).getStatusChangeUrl());

        return send2TCEC(tokenModel, CooperateFactory.getCoPush(org).getStatusChangeUrl(), params);
    }

    public static TokenModel sendOrderInfo(TokenModel tokenModel, Map<String, Object> params) {
        int org = tokenModel.getOrg();
        logger.debug(LogUtil.addExtLog("E_ORDER_INFO_URL"), CooperateFactory.getCoPush(org).getOrderUrl());

        return send2TCEC(tokenModel, CooperateFactory.getCoPush(org).getOrderUrl(), params);
    }

    private static TokenModel send2TCEC(TokenModel tokenModel, String url, Map<String, Object> params) {

        logger.debug(LogUtil.addExtLog("send2TCEC ORG:" + tokenModel.getOrg() + " url|params2|tokenAvailableTime"), new Object[]{url, params, tokenModel.getTokenAvailableTime()});

        //token值
        String token = tokenModel.getStaticToken();
        Date now = new Date();
        if (null == token || now.after(tokenModel.getTokenAvailableTime())) {
            token = getToken(tokenModel);
        }
        JSONObject jsonObject = JSONObject.fromObject(params);
        String data;

        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", TCECSGCCPush.DATA_SECRET, TCECSGCCPush.DATA_SECRET_IV);
            HashMap<String, String> map = SigTool.makeSig(data, TCECSGCCPush.OPERATOR_ID, TCECSGCCPush.SIG_SECRET);
            String answerStr = HttpUtils.httpJSONPost(url, map, token);
            logger.debug("send2TCEC ORG:" + tokenModel.getOrg() + " answerStr:{}", answerStr);
            Map response = JSONObject.fromObject(answerStr);

            //如果Ret=4002，重新请求token后再次推送数据
            String resultCode = String.valueOf(response.get("Ret"));
            if ("4002".equals(resultCode)) {
                token = getToken(tokenModel);
                HttpUtils.httpJSONPost(url, map, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenModel;
    }

    private static String getToken(TokenModel tokenModel) {
        logger.info("getToken ORG:" + tokenModel.getOrg() + "  decryptToken is begin");
        String decryptToken = CommonPush.getToken(tokenModel);
        logger.info("getToken ORG:" + tokenModel.getOrg() + "  decryptToken:{}", decryptToken);
        CommonPush.handleToken(decryptToken, tokenModel);
        logger.info("getToken ORG:" + tokenModel.getOrg() + "  tokenAvailableTime:{}", tokenModel.getTokenAvailableTime());
        logger.info("getToken ORG:" + tokenModel.getOrg() + "  StaticToken:{}", tokenModel.getStaticToken());
        return tokenModel.getStaticToken();
    }

}
