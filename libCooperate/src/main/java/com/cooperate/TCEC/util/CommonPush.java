package com.cooperate.TCEC.util;

import com.cooperate.CooperateFactory;
import com.cooperate.config.ConfigManager;
import com.cooperate.constant.KeyConsts;
import com.cooperate.utils.AesCBC;
import com.cooperate.utils.HttpUtils;
import com.cooperate.utils.SigTool;
import com.cooperate.utils.Strings;
import com.ec.config.Global;
import com.ec.constants.Symbol;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by zangyaoyi on 2017/11/6.
 */
public class CommonPush {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPush.class);
    private static String COMMON_TOKEN_URL = "";

    static {
        ConfigManager conf = ConfigManager.getMessageManager();
        COMMON_TOKEN_URL = conf.getEChongProperties("common_token_url");
    }

    public static Map<String, Object> getOnChargeEpRespByExtra(String extra) {
        Map<String, Object> resMap = new HashMap<>();
        String[] val = extra.split(Symbol.SHUXIAN_REG);
        resMap.put("StartChargeSeq", val[0]);
        resMap.put("StartChargeSeqStat", Integer.valueOf(val[1]));
        resMap.put("ConnectorID", val[2]);
        resMap.put("StartTime", val[3]);
        resMap.put("IdentCode", "123456");
        return resMap;
    }

    public static Map<String, Object> getOnStopChargeEpRespByExtra(String epCode, int epGunNo, String extra) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("StartChargeSeq", extra);
        resMap.put("StartChargeSeqStat", 4);
        resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
        resMap.put("SuccStat", 0);
        resMap.put("FailReason", 0);
        return resMap;
    }

    public static Map<String, Object> getOnEpStatusChange(String connectorID, Map<String, Object> realData) {
        HashMap<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("ConnectorID", connectorID);//pile_code 是 string 充电桩编码
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

        return resMap;
    }


    public static Map<String, Object> getOnRealData(String epCode, int epGunNo, Map<String, Object> realData, float servicePrice, String token) {
        HashMap<String, Object> resMap = new HashMap<>();
        int status_code = Strings.getIntValue(realData, "3_1");
        if (3 == status_code) {
            resMap.put("StartChargeSeqStat", 2);
            resMap.put("ConnectorStatus", 3);
        } else {
            return resMap;
        }
        resMap.put("StartChargeSeq", token);
        resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码


        int v = Strings.getIntValue(realData, "3_4");
        //A相电流
        resMap.put("CurrentA", intToBigDecimalbyBit(v, Global.Dec2));
        v = Strings.getIntValue(realData, "3_3");
        //A相电压
        resMap.put("VoltageA", intToBigDecimalbyBit(v, Global.Dec1));

        v = Strings.getIntValue(realData, "3_5");
        //剩余电量（ 汽车电量的百分比）
        resMap.put("Soc", v);
        v = Strings.getIntValue(realData, "3_6");
        //开始充电时间
        resMap.put("StartTime", DateUtil.StringYourDate(DateUtil.getAddMinute(new Date(), -v)));
        resMap.put("EndTime", DateUtil.StringYourDate(new Date()));

        v = Strings.getIntValue(realData, "4_4");
        BigDecimal TotalPower = intToBigDecimalbyBit(v, Global.Dec3);
        //当前已经充电的电量
        resMap.put("TotalPower", TotalPower);
      /*  v = Strings.getIntValue(realData, "4_3");
        BigDecimal ElecMoney = intToBigDecimal3(v);
        resMap.put("ElecMoney", ElecMoney.multiply(TotalPower));//当前电费
        resMap.put("ServiceMoney", NumUtil.intToBigDecimal2(v).multiply(new BigDecimal(String.valueOf(servicePrice))));//当前服务费金额

        v = Strings.getIntValue(realData, "4_2");
        resMap.put("TotalMoney", v);//当前充电总金额*/

        return resMap;
    }

    public static Map<String, Object> getOnChargeOrder(String epCode, int epGunNo, String token, int start_time, int end_time, float money,
                                                       float elect_money, float service_money, float elect) {
        HashMap<String, Object> resMap = new HashMap<String, Object>();

        resMap.put("StartChargeSeq", token);//充电订单号
        //充电桩编码
        resMap.put("ConnectorID", String.format("%s%02d", epCode, epGunNo));//pile_code 是 string 充电桩编码
        //start_time 是 int 充电开始时间（ 秒格式 Unix 时间戳）
        resMap.put("StartTime", DateUtil.longDateToString(new Long(start_time) * 1000));

        //end_time 是 int 充电结束时间（ 秒格式 Unix 时间戳）
        resMap.put("EndTime", DateUtil.longDateToString(new Long(end_time) * 1000));

        resMap.put("TotalPower", new BigDecimal(String.valueOf(elect)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        //elect_money 是 float 本次充电电费金额
        resMap.put("TotalElecMoney", new BigDecimal(String.valueOf(elect_money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        //service_money 是 float 本次充电服务费金额
        resMap.put("TotalSeviceMoney", new BigDecimal(String.valueOf(service_money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        //money 是 float 本次充电消费总金额（ 电费+服务费）
        resMap.put("TotalMoney", new BigDecimal(String.valueOf(money)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        resMap.put("StopReason", 1);

        resMap.put("WorkDate", DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_SHORT01));

        return resMap;
    }

    public static void main(String[] args) {

        String decryptToken = "{\"Ret\":\"0\", \"Msg\":\"操作成功\", \"Data\":\"dOpiViiWNMwmfjTC2Yj56hmY6dBD/WrZBnu5u53S1u5tVnKE9oHETihDtHQTo4pK7UBpHrSytl9HzPUAzvLJfvyZx6PdnC8s+8qrkpRnlcLy/bRjSSI3wNhP8XESRj8rfmqMkDZP74a0xuvYmwNoEqsDT8Vc15bRgIkIoBcRPe0=\", \"Sig\":\"929CB2E67E9E19638F7EE98FD5DD7D36\"}\n";
//        decryptToken=decryptToken.replaceAll("\r\n","");
//        decryptToken=decryptToken.replaceAll("\n","");
        JSONObject retMap = JSONObject.fromObject(decryptToken);
        String aa = "";
        try {
            aa = AesCBC.getInstance().decrypt(retMap.getString("Data"), "utf-8", "QWMtl2NyLPVJFbhy", "pYz6W6VP12FDSQIk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(aa);
    }

    public static String getToken(int org, String operatorID, String operatorSecret,
                                  String dataSecret, String dataSecretIv, String sigSecret) {
        HashMap<String, String> dataParam = new HashMap<>();
        LOGGER.info("getToken answerStr is begin ");
        dataParam.put("OperatorID", operatorID);
        dataParam.put("OperatorSecret", operatorSecret);
        JSONObject jsonObject = JSONObject.fromObject(dataParam);
        String data;
        String decryptToken;
        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", dataSecret, dataSecretIv);
            //生成签名
            LOGGER.info("getToken answerStr is 2 data={}", data);
            HashMap<String, String> map = SigTool.makeSig(data, operatorID, sigSecret);
            LOGGER.info("getToken answerStr is 3 ,map:{}", map);
            //发送请求
            String answerStr = HttpUtils.httpJSONPost(CooperateFactory.getCoPush(org).getTokenUrl(), map, null);
            LOGGER.info("getToken answerStr:{}", answerStr);
            //将String转成Map
            JSONObject retMap = JSONObject.fromObject(answerStr);
            LOGGER.info("getToken retMap:{}|dataSecret:{}|dataSecretIv:{}", new Object[]{retMap, dataSecret, dataSecretIv});
            decryptToken = AesCBC.getInstance().decrypt(retMap.getString("Data"), "utf-8", dataSecret, dataSecretIv);
            //记录返回结果
            LOGGER.info("getToken decryptToken:{}", decryptToken);
        } catch (Exception e) {
            decryptToken = "";
            LOGGER.info("getToken exception:{}", e.getMessage());
        }

        return decryptToken;
    }

    public static String getToken(TokenModel tokenModel) {
        HashMap<String, String> dataParam = new HashMap<>();
        LOGGER.info("getToken answerStr is begin ");
        dataParam.put("OperatorID", tokenModel.getOperatorId());
        dataParam.put("OperatorSecret", tokenModel.getOperatorSecret());
        JSONObject jsonObject = JSONObject.fromObject(dataParam);
        String data;
        String decryptToken;
        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", tokenModel.getDataSecret(), tokenModel.getDataSecretIv());
            //生成签名
            LOGGER.info("getToken answerStr is 2 data={}", data);
            HashMap<String, String> map = SigTool.makeSig(data, tokenModel.getOperatorId(), tokenModel.getSigSecret());
            LOGGER.info("getToken answerStr is 3 ,map:{}", map);
            //发送请求
            String answerStr = HttpUtils.httpJSONPost(CooperateFactory.getCoPush(tokenModel.getOrg()).getTokenUrl(), map, null);
            LOGGER.info("getToken answerStr:{}", answerStr);
            //将String转成Map
            JSONObject retMap = JSONObject.fromObject(answerStr);
            LOGGER.info("getToken retMap:{}|dataSecret:{}|dataSecretIv:{}", new Object[]{retMap, tokenModel.getDataSecret(), tokenModel.getDataSecretIv()});
            decryptToken = AesCBC.getInstance().decrypt(retMap.getString("Data"), "utf-8", tokenModel.getDataSecret(), tokenModel.getDataSecretIv());
            //记录返回结果
            LOGGER.info("getToken decryptToken:{}", decryptToken);
        } catch (Exception e) {
            decryptToken = "";
            LOGGER.info("getToken exception:{}", e.getMessage());
        }

        return decryptToken;
    }

    public static BigDecimal intToBigDecimalbyBit(int i, BigDecimal bit) {
        BigDecimal value = (new BigDecimal(i)).multiply(bit);
        value = value.setScale(2, 4);
        return value;
    }

    public static void handleToken(String decryptToken, TokenModel tokenModel) {
        JSONObject retTokenValue = JSONObject.fromObject(decryptToken);
        Iterator<String> keys = retTokenValue.keys();
        String key, errorCode, errorMsg = "", accessToken = "";
        boolean wongFlag = false;
        while (keys.hasNext()) {
            key = keys.next();
            if ("SuccStat".equals(key) && !"0".equals(retTokenValue.get(key).toString())) {
                wongFlag = true;
            }
            if ("AccessToken".equals(key)) {
                accessToken = retTokenValue.get(key).toString();
            }
            if ("FailReason".equals(key)) {
                errorCode = retTokenValue.get(key).toString();
                if (errorCode.equals("0")) {
                    errorMsg = "无";
                } else if (errorCode.equals("1")) {
                    errorMsg = "无此运营商";
                } else if (errorCode.equals("2")) {
                    errorMsg = "密钥错误";
                } else {
                    errorMsg = "未知";
                }
            }
            if ("TokenAvailableTime".equals(key)) {
                int availTime = Integer.parseInt(retTokenValue.get(key).toString());
                Calendar calObject = Calendar.getInstance();
                calObject.setTime(new Date());
                calObject.add(Calendar.SECOND, availTime - 10);
                tokenModel.setTokenAvailableTime(calObject.getTime());
            }
        }

        if (wongFlag && errorMsg.length() > 0) {
            LOGGER.error(LogUtil.addExtLog("response token is wrong; msg:{}"),
                    new Object[]{errorMsg});
        }
        if (accessToken.length() < 0) {
            LOGGER.error(LogUtil.addExtLog("response accessToken is null"));
            tokenModel.setStaticToken(null);
        } else {
            accessToken = KeyConsts.AUTH_TOKEN + accessToken;
            tokenModel.setStaticToken(accessToken);
        }
        LOGGER.info("handleToken  is ,tokenModel:{}", tokenModel);
    }

    public static String getTokenModelByHtml(String operatorID) {
        try {
            return HttpUtils.httpGet(COMMON_TOKEN_URL, operatorID);
        } catch (IOException e) {
            LOGGER.error(LogUtil.addExtLog("getTokenModelByHtml is error"), new Object[]{COMMON_TOKEN_URL, operatorID});
        }
        return null;
    }

}
