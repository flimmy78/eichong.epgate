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
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zangyaoyi on 2017/11/6.
 */
public class CommonPush {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPush.class);
    private static String COMMON_TOKEN_URL = "";

    private static ConcurrentHashMap<Integer, TokenModel> cacheData = new ConcurrentHashMap<>();

    public static TokenModel getCacheData(Integer key) {
        return cacheData.get(key);
    }

    public static void addCacheData(Integer key, TokenModel pointMap) {
        cacheData.put(key, pointMap);
    }

    public static void removeCacheData(Integer key) {
        cacheData.remove(key);
    }

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
        LOGGER.debug("实时状态:status:{}|realData:{}", status,realData);
        resMap.put("Status", status);
        if (status == 1 || status > 30) {
            resMap.put("Status", 255);
        } else if (status == 2) {
            resMap.put("Status", 1);
        } else if (status == 9 || status == 10 || status == 11 || status == 12) {
            resMap.put("Status", 2);
        } else if (status == 8) {
            resMap.put("Status", 4);
        } else if (status == 3) {
            resMap.put("Status", 3);
        } else if (status == 0) {
            resMap.put("Status", 0);
        } else {
            resMap.put("Status", 1);
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
        resMap.put("CurrentA", intToBigDecimalByBit(v, Global.Dec2));
        v = Strings.getIntValue(realData, "3_3");
        //A相电压
        resMap.put("VoltageA", intToBigDecimalByBit(v, Global.Dec1));

        v = Strings.getIntValue(realData, "3_5");
        //剩余电量（ 汽车电量的百分比）
        resMap.put("Soc", v);
        v = Strings.getIntValue(realData, "3_6");
        //开始充电时间
        resMap.put("StartTime", DateUtil.StringYourDate(DateUtil.getAddMinute(new Date(), -v)));
        resMap.put("EndTime", DateUtil.StringYourDate(new Date()));

        v = Strings.getIntValue(realData, "4_4");
        BigDecimal TotalPower = intToBigDecimalByBit(v, Global.Dec3);
        //当前已经充电的电量
        resMap.put("TotalPower", TotalPower);
        v = Strings.getIntValue(realData, "4_3");
        BigDecimal ElecMoney = intToBigDecimalByBit(v, Global.Dec3);
        DecimalFormat fum = new DecimalFormat("##0.00");
        //当前电费
        BigDecimal servicePrices = new BigDecimal(servicePrice);
        String ElecMoneys = fum.format((TotalPower.multiply(ElecMoney)).subtract(servicePrices.multiply(TotalPower)));
        resMap.put("ElecMoney", ElecMoneys);//当前电费
        //当前服务费
        String format = fum.format(servicePrices.multiply(TotalPower));
        resMap.put("SeviceMoney", format);//当前服务费
        //当前充电总金额
        v = Strings.getIntValue(realData, "4_2");
        BigDecimal TotalMoney = intToBigDecimalByBit(v, Global.Dec2);
        resMap.put("TotalMoney", TotalMoney);//当前充电总金额
        LOGGER.debug("实时数据:resMap:{}", resMap);
        return resMap;
    }

    public static Map<String, Object> getOnChargeOrder(String epCode, int epGunNo, String token, int start_time, int end_time, float money,
                                                       float elect_money, float service_money, float elect, String stop_reason) {
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
        int cause;//通讯上传数据
        int reason = 0;//向第三方推送停止充电原因标识
        /**
         * 1:根据协议文档电桩停止原因分为两部分 一:老协议:电桩上报格式分为不带|分割,二:新协议:电桩上报格式带|分割,截取下标第一个为电桩停止原因
         *
         * 2:关于电桩停止原因值cause值:
         *   老协议值:1:正常结束,2:用户发起停止,3:急停,(4,5,6,7,8,9,11)电桩故障,10:金额不足,12:自动充满,13:BMS故障,14:违规拔枪,15:电桩断电,16:ccu故障,17:显示屏故障,18:电源模块故障,19:车主动停止,20:绝缘检测,21:过温,22:欠压,23:vin码鉴权失败
         *   新协议值:0:正常结束,1:自检错误,(8,16):BMS故障,(2,4,16,32,65)电桩故障,64:用户主动停止,66:卡余额不足,249:启动失败,255:其他
         *
         * 3:返回第三方停止原因reason值:0:用户手动停止充电,1:客户归属运营商平台停止充电,2:BMS停止充电,3:电桩故障,4:连接器断开(老协议与新协议冲突,暂为老协议), 5:余额不足,6:电桩断电,7:违规拔枪,8:ccu故障,9:显示屏故障,10:电源模块故障,11:车主动停止,12:绝缘检测,13:过温,14:欠压,15:vin码鉴权失败,16:自动充满,255:其他
         * */
        if (stop_reason.indexOf("|") > 0) {
            cause = Integer.valueOf(stop_reason.split(Symbol.SHUXIAN_REG)[0]);
        } else {
            cause = Integer.valueOf(stop_reason);
        }
        if (cause == 0 || cause == 2) {
            reason = 1;
        } else if (cause == 3 || cause == 64) {
            reason = 0;
        } else if (cause == 8 || cause == 16 || cause == 13) {
            reason = 2;
        } else if (cause == 15 || cause == 249 || cause == 255) {
            reason = 255;
        } else if (cause == 4) {
            reason = 4;
        } else if (cause == 10 || cause == 66) {
            reason = 5;
        } else if (cause > 3 && cause < 12) {
            reason = 3;
        } else if (cause == 15) {
            reason = 6;
        } else if (cause == 14) {
            reason = 7;
        } else if (cause == 16) {
            reason = 8;
        } else if (cause == 17) {
            reason = 9;
        } else if (cause == 18) {
            reason = 10;
        } else if (cause == 19) {
            reason = 11;
        } else if (cause == 20) {
            reason = 12;
        } else if (cause == 21) {
            reason = 13;
        } else if (cause == 22) {
            reason = 14;
        } else if (cause == 23) {
            reason = 15;
        } else if (cause == 12) {
            reason = 16;
        }
        resMap.put("StopReason", reason);
        resMap.put("WorkDate", DateUtil.toString(new Date(end_time * 1000L), DateUtil.DATE_FORMAT_SHORT01));
        LOGGER.info("StopReason reason:{}", resMap);
        return resMap;
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
            data = data.replace("\n", "").replace("\r", "");
            LOGGER.debug("getToken answerStr is 2 data={}", data);
            HashMap<String, String> map = SigTool.makeSig(data, operatorID, sigSecret);
            //发送请求
            String answerStr = HttpUtils.httpJSONPost(CooperateFactory.getCoPush(org).getTokenUrl(), map, null);
            LOGGER.info("getToken answerStr:{}", answerStr);
            //将String转成Map
            JSONObject retMap = JSONObject.fromObject(answerStr);
            LOGGER.debug("getToken retMap:{}|dataSecret:{}|dataSecretIv:{}", new Object[]{retMap, dataSecret, dataSecretIv});
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
        LOGGER.info("getToken ORG:" + tokenModel.getOrg() + " answerStr is begin ");
        dataParam.put("OperatorID", tokenModel.getOperatorId());
        dataParam.put("OperatorSecret", tokenModel.getOperatorSecret());
        JSONObject jsonObject = JSONObject.fromObject(dataParam);
        String data;
        String decryptToken;
        try {
            data = AesCBC.getInstance().encrypt(jsonObject.toString(), "utf-8", tokenModel.getDataSecret(), tokenModel.getDataSecretIv());
            data = data.replace("\n", "").replace("\r", "");
            //生成签名
            LOGGER.info("getToken ORG:" + tokenModel.getOrg() + " answerStr is 2 data={}", data);
            HashMap<String, String> map = SigTool.makeSig(data, tokenModel.getOperatorId(), tokenModel.getSigSecret());
            LOGGER.debug("getToken ORG:" + tokenModel.getOrg() + " answerStr is 3 ,map:{}", map);
            //发送请求
            LOGGER.debug("getToken ORG:" + tokenModel.getOrg() + " TokenUrl:{}", CooperateFactory.getCoPush(tokenModel.getOrg()).getTokenUrl());
            String answerStr = HttpUtils.httpJSONPost(CooperateFactory.getCoPush(tokenModel.getOrg()).getTokenUrl(), map, null);
            LOGGER.info("getToken ORG:" + tokenModel.getOrg() + " answerStr:{}", answerStr);
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

    public static BigDecimal intToBigDecimalByBit(int i, BigDecimal bit) {
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
        if (accessToken.length() <= 0) {
            LOGGER.error(LogUtil.addExtLog("response accessToken is null"));
            tokenModel.setStaticToken(null);
        } else {
            accessToken = KeyConsts.AUTH_TOKEN + accessToken;
            tokenModel.setStaticToken(accessToken);
        }
        LOGGER.debug("handleToken  is ,tokenModel:{}", JSONObject.fromObject(tokenModel));
    }

    public static String getTokenModelByHtml(String operatorID) {
        try {
            return HttpUtils.httpGet(COMMON_TOKEN_URL, operatorID);
        } catch (IOException e) {
            LOGGER.error(LogUtil.addExtLog("getTokenModelByHtml is error"), new Object[]{COMMON_TOKEN_URL, operatorID});
        }
        return null;
    }


    public static void updateToken(int org, TokenModel tokenModel) {
        addCacheData(org, tokenModel);
    }
}
