package com.cooperate.TCEC.EChong;

import com.cooperate.Push;
import com.cooperate.config.ConfigManager;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.Strings;
import com.ec.constants.Symbol;
import com.ec.logs.LogConstants;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TCECEChongPush extends Push {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(TCECEChongPush.class.getName()));
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
        tokenUrl = conf.getEChongProperties(KeyConsts.TOKEN_URL);

        orgNo = Integer.valueOf(conf.getEChongProperties(KeyConsts.ORG_NO, "9012"));
        mode = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_MODE, "1"));
        period = Integer.valueOf(conf.getEChongProperties(KeyConsts.REAL_DATA_PERIOD, "30"));

        return b;
    }

    @Override
    public void onChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
        logger.info(LogUtil.addBaseExtLog("extra"), new Object[]{LogConstants.FUNC_ONSTARTCHARGE,
                epCode, epGunNo, orgNo, userIdentity, token, extra});

        Map<String, Object> resMap = new HashMap<>(5);
        String[] val = extra.split(Symbol.SHUXIAN_REG);
        resMap.put("StarChargeSeq", val[0]);
        resMap.put("StarChargeSeqStat", val[1]);
        resMap.put("ConnectorId", val[2]);
        resMap.put("StartTime", val[3]);
        resMap.put("IdentCode", 123456);

        if (resMap.size() > 0)
            TCECEChongService.addRealData(resMap.get("ConnectorId") + Symbol.SPLIT + resMap.get("timeStart")
                    + Symbol.SPLIT + KeyConsts.CHARGE_RESP_URL, resMap);
    }

    @Override
    public void onStopChargeEpResp(String token, int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
        logger.info(LogUtil.addBaseExtLog("extra"), new Object[]{LogConstants.FUNC_STOP_CHARGE,
                epCode, epGunNo, orgNo, userIdentity, token, extra});

        Map<String, Object> resMap = new HashMap<>(5);
        String[] val = extra.split(Symbol.SHUXIAN_REG);
        resMap.put("StarChargeSeq", val[0]);
        resMap.put("StarChargeSeqStat", val[1]);
        resMap.put("ConnectorId", val[2]);
        resMap.put("SuccStat", val[3]);
        resMap.put("FailReason", val[4]);

        if (resMap.size() > 0)
            TCECEChongService.addRealData(resMap.get("ConnectorId") + Symbol.SPLIT + resMap.get("timeStart")
                    + Symbol.SPLIT + KeyConsts.STOP_CHARGE_RESP_URL, resMap);
    }

    @Override
    public void onChargeEvent(int orgNo, String userIdentity, String epCode, int epGunNo, String extra, int ret, int errorCode) {
    }

    @Override
    public void onEpStatusChange(String token, int orgNo, String userIdentity, String epCode, int epGunNo
            , int inter_type, Map<String, Object> realData, String extra) {
        try {
            logger.info(LogUtil.addBaseExtLog("realData|extra"), new Object[]{LogConstants.FUNC_ONEPSTATUSCHANGE,
                    epCode, epGunNo, orgNo, userIdentity, token, realData, extra});

            HashMap<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
            int status = Strings.getIntValue(realData, "3_1");
            //操作中不推送
            resMap.put("Status", status);
            if (status == 1 || status > 30) {
                resMap.put("Status", 255);
            } else if (status == 2) {
                resMap.put("Status", 1);
            } else if (status == 9 || status == 10 || status == 11 || status == 12) {
                resMap.put("Status", 2);
            } else if (status == 8) {
                resMap.put("Status", 4);
            }

            resMap.put("ParkStatus", 0);//parking_state 否 int 车位状态:0:未知 1:空闲 2:占用 3:故障
            resMap.put("LockStatus", 0);//地锁

            TCECEChongService.addRealData(resMap.get("pile_code") + Symbol.SPLIT + resMap.get("time")
                    + Symbol.SPLIT + KeyConsts.STATUS_CHANGE_URL, resMap);
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
            logger.info(LogUtil.addBaseExtLog("servicePrice|realData|extra"), new Object[]{LogConstants.FUNC_ONREALDATA, epCode, epGunNo, orgNo, userIdentity, token, servicePrice, realData, extra});

            HashMap<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("StartChargeSeq", extra);
            resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
            resMap.put("StartChargeSeqStat", Strings.getIntValue(realData, "3_1"));
            resMap.put("ConnectorStatus", Strings.getIntValue(realData, "3_1"));

            int v = Strings.getIntValue(realData, "3_44");
            resMap.put("CurrentA", v);//A相电流
            v = Strings.getIntValue(realData, "3_45");
            resMap.put("CurrentB", v);//B相电流
            v = Strings.getIntValue(realData, "3_46");
            resMap.put("CurrentC", v);//C相电流

            v = Strings.getIntValue(realData, "3_41");
            resMap.put("VoltageA", v);//A相电压
            v = Strings.getIntValue(realData, "3_42");
            resMap.put("VoltageB", v);//B相电压
            v = Strings.getIntValue(realData, "3_43");
            resMap.put("VoltageC", v);//C相电压
            v = Strings.getIntValue(realData, "3_5");
            resMap.put("soc", NumUtil.intToBigDecimal2(v).multiply(new BigDecimal(10)));//剩余电量（ 汽车电量的百分比）
            v = Strings.getIntValue(realData, "3_6");
            resMap.put("StartTime", DateUtil.StringYourDate(DateUtil.getAddMinute(new Date(), -v)));//开始充电时间
            resMap.put("EndTime", DateUtil.currentStringDate());

            v = Strings.getIntValue(realData, "4_4");
            resMap.put("TotalPower", NumUtil.intToBigDecimal2(v));//当前已经充电的电量
            resMap.put("ServiceMoney", NumUtil.intToBigDecimal2(v).multiply(new BigDecimal(String.valueOf(servicePrice))));//当前服务费金额

            v = Strings.getIntValue(realData, "4_2");
            resMap.put("ElecMoney", NumUtil.intToBigDecimal2(v));//当前电费
            resMap.put("TotalMoney", ((BigDecimal) resMap.get("ElecMoney")).subtract((BigDecimal) resMap.get("ServiceMoney")));//当前充电电费金额

            long time = DateUtil.getCurrentSeconds();
            TCECEChongService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + time
                    + Symbol.SPLIT + KeyConsts.REAL_DATA_URL, resMap);
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

            HashMap<String, Object> resMap = new HashMap<String, Object>();

            resMap.put("StartChargeSeq", extra);//充电订单号
            //充电桩编码
            resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
            //start_time 是 int 充电开始时间（ 秒格式 Unix 时间戳）
            resMap.put("StartTime", DateUtil.longDateToString(new Long(start_time) * 1000));

            //end_time 是 int 充电结束时间（ 秒格式 Unix 时间戳）
            resMap.put("EndTime", DateUtil.longDateToString(new Long(end_time) * 1000));

            //money 是 float 本次充电消费总金额（ 电费+服务费）
            resMap.put("TotalMoney", new BigDecimal(String.valueOf(money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            //elect_money 是 float 本次充电电费金额
            resMap.put("TotalElectMoney", new BigDecimal(String.valueOf(elect_money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            //service_money 是 float 本次充电服务费金额
            resMap.put("TotalServiceMoney", new BigDecimal(String.valueOf(service_money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            resMap.put("TotalPower", new BigDecimal(String.valueOf(elect)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());


            TCECEChongService.addRealData(resMap.get("ConnectorID") + Symbol.SPLIT + resMap.get("time")
                    + Symbol.SPLIT + KeyConsts.ORDER_URL, resMap);
        } catch (Exception e) {
            logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
        }

    }

}
