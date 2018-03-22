package com.cooperate.TCEC.nanrui;

import com.alibaba.fastjson.JSON;
import com.cooperate.Push;
import com.cooperate.TCEC.util.CommonPush;
import com.cooperate.config.ConfigManager;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.Strings;
import com.ec.config.Global;
import com.ec.constants.Symbol;
import com.ec.logs.LogConstants;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class TCECENanRuiPush extends Push {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(TCECENanRuiPush.class.getName()));
    public static String OPERATOR_ID = "";
    public static String OPERATOR_SECRET = "";
    public static String DATA_SECRET = "";
    public static String SIG_SECRET = "";
    public static String DATA_SECRET_IV = "";
    public static String ALARM_INFO_URL = "";

    @Override
    public boolean init(String filename) {
        ConfigManager conf = ConfigManager.getMessageManager();
        boolean b = conf.init(filename);

        OPERATOR_ID = conf.getEChongProperties(KeyConsts.OPERATOR_ID);
        OPERATOR_SECRET = conf.getEChongProperties(KeyConsts.OPERATOR_SECRET);
        DATA_SECRET = conf.getEChongProperties(KeyConsts.DATA_SECRET);
        SIG_SECRET = conf.getEChongProperties(KeyConsts.SIG_SECRET);
        DATA_SECRET_IV = conf.getEChongProperties(KeyConsts.T_DATA_SECRET_IV);

        ALARM_INFO_URL = conf.getEChongProperties("alarm_info_url");
        statusChangeUrl = conf.getEChongProperties(KeyConsts.STATUS_CHANGE_URL);
        tokenUrl = conf.getEChongProperties(KeyConsts.TOKEN_URL);
        orgNo = Integer.valueOf(conf.getEChongProperties(KeyConsts.ORG_NO, "9020"));
        mode = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_MODE, "1"));
        period = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_PERIOD, "30"));

        return b;
    }

    @Override
    public void onChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {

    }

    @Override
    public void onStopChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
    }

    @Override
    public void onChargeEvent(int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
    }

    @Override
    public void onEpStatusChange(String token, int orgNo, String userIdentity, String epCode, int epGunNo
            , int inter_type, Map<String, Object> realData, String extra) {
        logger.info(LogUtil.addBaseExtLog("servicePrice|realData|extra"), new Object[]{LogConstants.FUNC_ONEPSTATUSCHANGE, epCode, epGunNo, orgNo, userIdentity, token, realData, extra});

        String connectorID = String.format("%s%02d", epCode, epGunNo);
        //深圳平台该接口需要数组类型，不走中电联标准
        Map<String, Object> resMap = CommonPush.getOnEpStatusChange(connectorID, realData);

        resMap.put("CurrentA", 0);//A相电流

        resMap.put("VoltageA", 0);//A相电压

        resMap.put("soc", 0);//剩余电量（ 汽车电量的百分比）
        //开始充电时间
        resMap.put("Begin_time", "");

        resMap.put("Current_kwh", 0);
        Map<String, Object> ensData = new HashMap<>();
        ensData.put("ConnectorStatusInfo", resMap);

        long time = DateUtil.getCurrentSeconds();
        logger.debug("onepstatuschange is put :{}", JSON.toJSONString(ensData));
        TCECENanRuiService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + time
                + Symbol.SPLIT + KeyConsts.STATUS_CHANGE_URL, ensData);
        logger.debug("onepstatuschange is put :{}", JSON.toJSONString(resMap.get("ConnectorID") + Symbol.SPLIT + time
                + Symbol.SPLIT + KeyConsts.STATUS_CHANGE_URL + ensData));
        if (resMap.get("Status").equals(255)) {
            if (0 == statusConver(realData)) {
                return;
            }
            Map<String, Object> errorMap = getAlarmInfo(connectorID, realData);
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(errorMap);
            Map<String, Object> encData = new HashMap<>();
            encData.put("AlarmInfos", list);
            TCECENanRuiService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + time
                    + Symbol.SPLIT + "alarm_info_url", encData);
            logger.debug("onepstatuschange AlarmInfo is put :{}", JSON.toJSONString(resMap.get("ConnectorID") + Symbol.SPLIT + time
                    + Symbol.SPLIT + "alarm_info_url" + encData));
        }

    }


    @Override
    public void onEpNetStatusChange(int orgNo, String epCode, int netStatus) {
    }

    @Override
    public void onRealData(String token, int orgNo, String userIdentity, String epCode, int epGunNo, int inter_type, float servicePrice, Map<String, Object> realData, String extra) {
        try {
            logger.info(LogUtil.addBaseExtLog("servicePrice|realData|extra"), new Object[]{LogConstants.FUNC_ONREALDATA, epCode, epGunNo, orgNo, userIdentity, token, servicePrice, realData, extra});

            HashMap<String, Object> resMap = new HashMap<>();
            resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
            int status_code = Strings.getIntValue(realData, "3_1");
            if (0 == status_code) {
                status_code = 0;
            } else if (2 == status_code) {
                status_code = 1;
            } else if (9 == status_code || 10 == status_code || 11 == status_code || 12 == status_code) {
                status_code = 2;
            } else if (3 == status_code) {
                status_code = 3;
            } else {
                status_code = 255;
            }
            resMap.put("Status", status_code);

            int v = Strings.getIntValue(realData, "3_4");
            resMap.put("CurrentA", CommonPush.intToBigDecimalbyBit(v, Global.Dec2));//A相电流

            v = Strings.getIntValue(realData, "3_3");
            resMap.put("VoltageA", CommonPush.intToBigDecimalbyBit(v, Global.Dec1));//A相电压

            v = Strings.getIntValue(realData, "1_5");
            if (v == 0) {
                v = 50;
            } else if (v == 1) {
                v = 100;
            } else {
                v = 0;
            }
            resMap.put("ParkStatus", v);//车位状态
            v = Strings.getIntValue(realData, "3_2");
            if (v == 1) {
                v = 10;
            } else if (v > 2) {
                v = 50;
            } else {
                v = 0;
            }
            resMap.put("LockStatus", v);//地锁状态

            v = Strings.getIntValue(realData, "3_5");
            resMap.put("soc", v);//剩余电量（ 汽车电量的百分比）

            v = Strings.getIntValue(realData, "3_6");
            //开始充电时间
            resMap.put("Begin_time", DateUtil.StringYourDate(DateUtil.getAddMinute(new Date(), -v)));

            v = Strings.getIntValue(realData, "4_4");
            BigDecimal TotalPower = CommonPush.intToBigDecimalbyBit(v, Global.Dec3);
            //当前已经充电的电量
            resMap.put("Current_kwh", TotalPower);
            Map<String, Object> ensData = new HashMap<>();
            ensData.put("ConnectorStatusInfo", resMap);

            long time = DateUtil.getCurrentSeconds();
            TCECENanRuiService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + time
                    + Symbol.SPLIT + KeyConsts.STATUS_CHANGE_URL, ensData);
            if (resMap.get("Status").equals(255)) {
                if (0 == statusConver(realData)) {
                    return;
                }
                Map<String, Object> errorMap = getAlarmInfo(resMap.get("ConnectorID").toString(), realData);
                List<Map<String, Object>> list = new ArrayList<>();
                list.add(errorMap);
                Map<String, Object> encData = new HashMap<>();
                encData.put("AlarmInfos", list);
                TCECENanRuiService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + time
                        + Symbol.SPLIT + "alarm_info_url", encData);
                logger.debug(LogConstants.FUNC_ONREALDATA + "AlarmInfo is put :{}", JSON.toJSONString(resMap.get("ConnectorID") + Symbol.SPLIT + time
                        + Symbol.SPLIT + "alarm_info_url" + encData));
            }
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

    }

    private Map<String, Object> getAlarmInfo(String connectorID, Map<String, Object> realData) {
        Map<String, Object> errorMap = new HashMap<>();
        int errorCode = statusConver(realData);
        errorMap.put("ConnectorID", connectorID);//pile_code 是 string 充电桩编码
        errorMap.put("Alert_time", DateUtil.StringYourDate(new Date()));
        errorMap.put("Alert_code", errorCode);
        errorMap.put("Describe", fullDescribe(connectorID, errorCode));
        errorMap.put("Status", 0);
        return errorMap;
    }

    private String fullDescribe(String connectorID, int errorCode) {
        StringBuffer info = new StringBuffer(connectorID + "充电桩故障，原因:");
        if (errorCode == 114) {
            info = info.append("充电设备电表故障");
        } else if (errorCode == 112) {
            info = info.append("充电设备急停故障");
        } else {
            info = info.append(errorCode);
        }

        return info.toString();
    }

    private int statusConver(Map<String, Object> realData) {
        int status = Strings.getIntValue(realData, "3_1");
        if (status > 30) {
            if (status == 35) {
                return 114;
            } else {
                return 112;
            }
        } else {
            status = Strings.getIntValue(realData, "2_1");
            if (status == 1) {
                return 101;
            }
            status = Strings.getIntValue(realData, "2_3");
            if (status == 1) {
                return 102;
            }
            status = Strings.getIntValue(realData, "2_15");
            if (status == 1) {
                return 103;
            }
            status = Strings.getIntValue(realData, "1_21");
            if (status == 1) {
                return 104;
            }
            status = Strings.getIntValue(realData, "1_17");
            if (status == 1) {
                return 106;
            }
            status = Strings.getIntValue(realData, "2_12");
            if (status == 1) {
                return 107;
            }
            status = Strings.getIntValue(realData, "1_7");
            if (status == 1) {
                return 112;
            }
            status = Strings.getIntValue(realData, "1_6");
            if (status == 1) {
                return 113;
            }
            status = Strings.getIntValue(realData, "1_20");
            if (status == 1) {
                return 114;
            }
            status = Strings.getIntValue(realData, "1_9");
            if (status == 1) {
                return 116;
            }
            status = Strings.getIntValue(realData, "1_19");
            if (status == 1) {
                return 118;
            }
        }
        return 0;
    }
}
