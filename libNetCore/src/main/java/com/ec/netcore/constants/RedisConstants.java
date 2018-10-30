package com.ec.netcore.constants;

public class RedisConstants {
	public static final String NormalChannel = "NORMAL";
	public final static String NAME_PROP = "param.properties";
	
	public final static String PATH_REDISM = "redis.master"; //redis主服务
	public final static String PATH_REDISS = "redis.slaves";   //从地址列表
	public final static String PASSWD_REDIS = "redis.passwd";   //redis密码
	
	public final static Double RATE_DEFAULT = 0.5;//
	public final static Double DISCOUNT_DEFAULT = 1.0;//
	
//
//	//--------------Task
//	public static final Map<String,Map<String,String>> SERVICE_MAP = new HashMap<String,Map<String,String>>();
//
//	static{
//		Map<String,String> params = new HashMap<String,String>();
//		params.put("plan", "planService");
//		params.put("plan_putawayAd", "java.lang.String");
//		SERVICE_MAP.put("dsp", params);
//	}
}
