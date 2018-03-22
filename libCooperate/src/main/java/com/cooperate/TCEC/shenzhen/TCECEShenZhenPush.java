package com.cooperate.TCEC.shenzhen;

import com.alibaba.fastjson.JSON;
import com.cooperate.Push;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.config.ConfigManager;
import com.cooperate.constant.KeyConsts;
import com.ec.constants.Symbol;
import com.ec.logs.LogConstants;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCECEShenZhenPush extends Push {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(TCECEShenZhenPush.class.getName()));
    public static String OPERATOR_ID = "";
    public static String OPERATOR_SECRET = "";
    public static String DATA_SECRET = "";
    public static String SIG_SECRET = "";
    public static String DATA_SECRET_IV = "";

    @Override
    public boolean init(String filename) {
        ConfigManager conf = ConfigManager.getMessageManager();
        boolean b = conf.init(filename);

        OPERATOR_ID = conf.getEChongProperties(KeyConsts.OPERATOR_ID);
        OPERATOR_SECRET = conf.getEChongProperties(KeyConsts.OPERATOR_SECRET);
        DATA_SECRET = conf.getEChongProperties(KeyConsts.DATA_SECRET);
        SIG_SECRET = conf.getEChongProperties(KeyConsts.SIG_SECRET);
        DATA_SECRET_IV = conf.getEChongProperties(KeyConsts.T_DATA_SECRET_IV);

        statusChangeUrl = conf.getEChongProperties(KeyConsts.STATUS_CHANGE_URL);
        chargeRespUrl = conf.getEChongProperties(KeyConsts.CHARGE_RESP_URL);
        stopChargeRespUrl = conf.getEChongProperties(KeyConsts.STOP_CHARGE_RESP_URL);
        statusChangeUrl = conf.getEChongProperties(KeyConsts.STATUS_CHANGE_URL);
        realDataUrl = conf.getEChongProperties(KeyConsts.REAL_DATA_URL);
        orderUrl = conf.getEChongProperties(KeyConsts.ORDER_URL);
        tokenUrl = conf.getEChongProperties(KeyConsts.TOKEN_URL);

        orgNo = Integer.valueOf(conf.getEChongProperties(KeyConsts.ORG_NO, "9013"));
        mode = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_MODE, "1"));
        period = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_PERIOD, "30"));

        return b;
    }

    @Override
    public void onChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
        logger.info(LogUtil.addBaseExtLog("extra"), new Object[]{LogConstants.FUNC_ONSTARTCHARGE,
                epCode, epGunNo, orgNo, userIdentity, token, extra});

        Map<String, Object> resMap = null;
        try {
            String connectorID = String.format("%s%02d", epCode, epGunNo);
            resMap = CommonPush.getOnChargeEpRespByExtra(extra);
            TCECEShenZhenService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + resMap.get("StartTime")
                    + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, resMap);


            logger.info("onChargeEpResp:key:{}", JSON.toJSONString(resMap.get("ConnectorID") + Symbol.SPLIT + resMap.get("StartTime")
                    + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL));

        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
        logger.info("onChargeEpResp:val:{}", JSON.toJSONString(resMap));
    }

    @Override
    public void onStopChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
        logger.info(LogUtil.addBaseExtLog("extra"), new Object[]{LogConstants.FUNC_STOP_CHARGE,
                epCode, epGunNo, orgNo, userIdentity, token, extra});


    }

    @Override
    public void onChargeEvent(int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
    }

    @Override
    public void onEpStatusChange(String token, int orgNo, String userIdentity, String epCode, int epGunNo
            , int inter_type, Map<String, Object> realData, String extra) {
        try {
            logger.info(LogUtil.addBaseExtLog("realData"), new Object[]{LogConstants.FUNC_ONEPSTATUSCHANGE,
                    epCode, epGunNo, orgNo, userIdentity, token, realData});
            String connectorID = String.format("%s%02d", epCode, epGunNo);
            //深圳平台该接口需要数组类型，不走中电联标准
            Map<String, Object> resMap = CommonPush.getOnEpStatusChange(connectorID, realData);
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(resMap);
            Map<String, Object> encData = new HashMap<>();
            encData.put("ConnectorStatusInfo", list);

            TCECEShenZhenService.addRealData(connectorID + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                    + Symbol.SPLIT + KeyConsts.STATUS_CHANGE_URL, encData);
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }

    @Override
    public void onEpNetStatusChange(int orgNo, String epCode, int netStatus) {
    }

    @Override
    public void onRealData(String token, int orgNo, String userIdentity, String epCode, int epGunNo, int inter_type, float servicePrice, Map<String, Object> realData, String extra) {
        try {
            logger.info(LogUtil.addBaseExtLog("servicePrice|realData|extra"), new Object[]{LogConstants.FUNC_ONREALDATA, epCode, epGunNo, orgNo, userIdentity, token, servicePrice, realData, extra, inter_type});

            Map<String, Object> resMap = CommonPush.getOnRealData(epCode, epGunNo, realData, servicePrice, token);
            if (resMap.size() > 0) {
                TCECEShenZhenService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                        + Symbol.SPLIT + KeyConsts.REAL_DATA_URL, resMap);
            } else {
                logger.info("onRealData:key:{没有充电不推送}");
            }
            logger.info("onRealData:key:{}", JSON.toJSONString(resMap.get("ConnectorID") + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                    + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL));
            logger.info("onRealData:val:{}", JSON.toJSONString(resMap));

        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }
    }

    @Override
    public void onChargeOrder(String token, int orgNo, String userIdentity, String epCode, int epGunNo,
                              int inter_type, float money, float elect_money, float service_money, float elect, float start_elect, float end_elect
            , float cusp_elect, float cusp_elect_price, float cusp_service_price, float cusp_money, float cusp_elect_money, float cusp_service_money
            , float peak_elect, float peak_elect_price, float peak_service_price, float peak_money, float peak_elect_money, float peak_service_money
            , float flat_elect, float flat_elect_price, float flat_service_price, float flat_money, float flat_elect_money, float flat_service_money
            , float valley_elect, float valley_elect_price, float valley_service_price, float valley_money, float valley_elect_money, float valley_service_money
            , int start_time, int end_time, int stop_model, int stop_reason, int soc, int time, String extra) {
        try {
            logger.info(LogUtil.addBaseExtLog("inter_type|money|extra|start_time|end_time|elect|start_elect|end_elect"), new Object[]{LogConstants.FUNC_ONCHARGEORDER,
                    epCode, epGunNo, orgNo, userIdentity, token, inter_type, money, extra, start_time, end_time, elect, start_elect, end_elect});


            Map<String, Object> stopCharge = CommonPush.getOnStopChargeEpRespByExtra(epCode, epGunNo, token);
            if (stopCharge.size() > 0) {
                TCECEShenZhenService.addRealData(stopCharge.get("ConnectorID") + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                        + Symbol.SPLIT + KeyConsts.STOP_CHARGE_RESP_URL, stopCharge);
            }

            Map<String, Object> order = CommonPush.getOnChargeOrder(epCode, epGunNo, token, start_time,
                    end_time, money, elect_money, service_money, elect);
            TCECEShenZhenService.addRealData(order.get("ConnectorID") + Symbol.SPLIT + DateUtil.getCurrentSeconds()
                    + Symbol.SPLIT + KeyConsts.ORDER_URL, order);
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }

    }

}
