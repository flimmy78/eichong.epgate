package com.cooperate.utils;


import com.ec.net.proto.WmIce104Util;
import com.ec.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by echongwang on 2016/10/11.
 * 用于生成签名的工具类。
 * <p>
 * 主要调用2个方法：
 * getSignString(Map<String,String> map,String app_key)，适用于任意数量的参数，参数的和值放入map中
 * getSignString(String app_id,String info,String app_key)，仅针对只有app_id和app_key参数的情况，生成签名
 * <p>
 * 根据e充网openApi接入文档，将所有参数按字典顺序拼接为字符串，使用HMAC-SHA1 算法生成签名。
 */
public class SigTool {
	private static final Logger logger =  LoggerFactory.getLogger(LogUtil.getLogName(SigTool.class.getName()));

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    //上一次自增序列
    public static long lastTimeStamp = 0;
    public static int lastSeq = 1;

    //用于多个字符串按字典升序排序的Comparator
    private static Comparator<String> comparator = new Comparator<String>() {
        public int compare(String s1, String s2) {
            if (s1 == null) {
                return -1; //null 排在最前
            } else if (s2 == null) {  //此处不考虑s1 s2均为null的情况
                return 1;
            }
            return s1.compareTo(s2);
        }
    };

    /**
     * 用于对请求中的参数按照key字典顺序排序，拼接成字符串，并生成签名
     *
     * @param map 包含要排序的key value的map
     */
    public static String getSignString(Map<String, String> map, String app_key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String data;
        if (map == null || map.size() == 0) {
            data = "";
        } else {
            List<String> keyList = new ArrayList<>(map.keySet()); //keySet转为list
            Collections.sort(keyList, comparator);
            StringBuilder sb = new StringBuilder();
            int len = keyList.size();
            for (int i = 0; i < len; i++) {
                String key = keyList.get(i);
                sb.append(key).append("=").append(map.get(key)); //参数值不进行url编码
                if (i != len - 1) sb.append("&");//最后一个参数后不添加&
            }
            data = sb.toString();
        }
        String key = app_key + "&";
        byte[] bytes = hmacSHA1Encrypt(data, key);  //HMAC-SHA1取哈希值
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String sig = base64Encoder.encode(bytes);  //base64编码得到签名
        return sig;
    }

    /**
     * 针对仅有app_id和info参数的情况，生成签名。
     * 相比于传入map的getSignString方法，效率更高（不需要排序参数名），通用性较差
     *
     * @param app_id  需计算签名的参数app_id的值
     * @param info    需计算签名的参数info的值
     * @param app_key 用于计算签名的app_key，末尾不包含&符号
     * @return 生成的签名
     */
    public static String getSignString(String app_id, String info, String app_key) {
        String sig;  //base64编码得到签名
        try {
            String data = "app_id=" + app_id + "&info=" + info;
            String key = app_key + "&";
            byte[] bytes = hmacSHA1Encrypt(data, key);  //HMAC-SHA1取哈希值
            BASE64Encoder base64Encoder = new BASE64Encoder();
            sig = base64Encoder.encode(bytes);
        } catch (Exception e) {
            sig = null;
        }
        return sig;
    }


    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     */
    private static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return mac.doFinal(text);
    }

    /**
     * 用于对请求中的参数按照key字典顺序排序，拼接成字符串，并生成签名
     *
     * @param map 包含要排序的key value的map
     */
    public static String getSignMd5(Map<String, Object> map, String appsecret) {
    	logger.debug(LogUtil.addExtLog("map|appSecret"),map,appsecret);

    	String data,sig;
        try {
	        if (map == null || map.size() == 0) {
	            data = "";
	        } else {
	            List<String> keyList = new ArrayList<>(map.keySet()); //keySet转为list
	            Collections.sort(keyList, comparator);
	            StringBuilder sb = new StringBuilder();
	            int len = keyList.size();
	            for (int i = 0; i < len; i++) {
	                String key = keyList.get(i);
	                if (Strings.isNullOrEmpty(map.get(key))) continue;
	                sb.append(key).append("=").append(map.get(key)); //参数值不进行url编码
	                if (i != len - 1) sb.append("&");//最后一个参数后不添加&
	            }
	            data = sb.toString();
	        }
	        data += appsecret;
	        sig = WmIce104Util.MD5Encode(data.getBytes());

	        logger.debug(LogUtil.addExtLog("data|sig"),data,sig);
        } catch (Exception e) {
        	logger.error(LogUtil.addExtLog("exception"), e.getMessage());
            sig = null;
        }
        return sig;
    }
    /**
     * 获取当前时间，格式为yyyyMMddHHmmss
     */
    public static long getNowTimeStamp() {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String ctime = formatter.format(new Date());
        return Long.parseLong(ctime);
    }

    /**
     * 4位自增序列，同一秒内自增，如0001
     */
    public static int getSeq(long thisTimeStamp) {
        //第一次时
        if (lastTimeStamp == thisTimeStamp) {
            lastSeq++;
            return lastSeq;
        }
        lastSeq = 1;
        lastTimeStamp = thisTimeStamp;
        return lastSeq;
    }

    //生成签名
    public static HashMap<String, String> makeSig(String Data,String operatorID,String sigSecret) {
        Long TimeStamp = getNowTimeStamp();
        int Seq = getSeq(TimeStamp);

        HashMap<String, String> map = new LinkedHashMap();
        map.put("TimeStamp", TimeStamp + "");
        String SeqStr = String.format("%04d", Seq);
        map.put("Seq", SeqStr);
        map.put("OperatorID", operatorID);
        map.put("Data", Data);

        String sig =  HMacMD5.getHmacMd5Str(sigSecret, operatorID + Data + TimeStamp + SeqStr).toUpperCase();
        map.put("Sig", sig);
        return map;
    }

    //将改main方法取消注释后，可以直接运行，查看签名工具使用示例
    public static void main(String[] args) {
        //测试当前工具类的运行入口
        try {
            System.out.println("当前为SigTool，用于生成签名的工具类。");
            System.out.println("该类根据e充网openApi接入文档，将所有参数按字典顺序拼接为字符串，使用HMAC-SHA1 算法生成签名。");
            System.out.println("getSignString(Map<String,String> map,String app_key)，传入map，生成签名。适用于任意数量的参数，参数的和值放入map中");
            System.out.println("getSignString(String app_id,String info,String app_key)，传入app_id和app_key，仅针对只有app_id和app_key参数的情况，生成签名");

            String app_id = "qjx6TcPLpgHZv4ZH5mJ2K6qj";  //e充网分配的app_id
            String app_key = "VjaOvn7zkukdmbeG8oplUj3WoJSm4TN1";  //e充网分配的app_key
            //模拟的要发送的info参数内容，json数据
            String info = "{\"pile_code\":\"1110108217001001\",\"inter_no\":0,\"inter_type\":2,\"inter_conn_state\":3,\"inter_work_state\":2,\"inter_order_state\":1,\"voltage\":5,\"current\":9,\"soc\":21,\"fault_code\":22,\"err_code\":0,\"res_time\":0,\"time\":1480417165,\"elect_address\":\"none\",\"elect_type\":2,\"elect_rate\":0,\"active_power\":16,\"reactive_power\":17,\"active_energy\":18,\"reactive_energy\":19,\"parking_state\":2}";

            System.out.println("");
            System.out.println("示例数据：");
            System.out.println("测试用 app_id=" + app_id);
            System.out.println("测试用 app_key=" + app_key);
            System.out.println("info=" + info);

            System.out.println("");
            System.out.println("使用传入map参数的getSignString方法，生成的签名：");
            Map<String, String> map = new HashMap<>();
            map.put("app_id", app_id);
            map.put("info", info);
            String sig = getSignString(map, app_key);
            System.out.println(sig);

            System.out.println("");
            System.out.println("使用传入app_id、app_key参数的getSignString方法，生成的签名：");
            String sig2 = getSignString(app_id, info, app_key);
            System.out.println(sig2);

        } catch (Exception e) {
            System.err.println("生成签名时出现异常，e=" + e);
            e.printStackTrace();
        }

    }

}
