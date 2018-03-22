package com.ec.usrcore.config;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.GameObject;
import com.ec.constants.Symbol;
import com.ec.utils.LogUtil;

public class GameBaseConfig extends GameObject{
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(GameBaseConfig.class.getName()));
	
	public static String fileName = System.getProperty("file.separator")
			+System.getProperty("user.dir")
			+System.getProperty("file.separator") + "conf"
			+System.getProperty("file.separator")
			+"GameConfig.properties";
	
	public static final String crossDomainPolicy = "<cross-domain-policy>"
			+ "<site-control permitted-cross-domain-policies=\"all\"/>"
			+ "<allow-access-from domain=\"*\" to-ports=\"*\" />"
			+ "</cross-domain-policy>"
			+ "\0";
	
	/**可用cup的数量*/
	public static int cpuCoreNum = Runtime.getRuntime().availableProcessors();
	
	/** 字符编码 */
	public static String encoding;
	/** 需要压缩的数据的最小字节数*/
	public static int minCompressByteNum;
	/**系统允许的最大连接数量*/
	public static int maxOnLineNumber;
	
	/**是否是测试版本*/
	public static boolean isTest;
	
	/**通信协议混淆参数1*/
	public static byte mask1;
	/**通信协议混淆参数2*/
	public static byte mask2;
	/**通信协议混淆参数3*/
	public static byte mask3;
	/**通信协议混淆参数4*/
	public static byte mask4;
	
	
	/**keepAlive*/
	public static boolean keepAlive;
	/**是否打开数据统计日志*/
	public static boolean isOpenLog;
	
	/**最大缓存离线电桩数量*/
	public static int maxEpNum;
	
	/**自定义密钥，支付web服务器 跟 游戏服务器 发消息用*/
	public static String mySecurity;
	/**自定义密钥2，游戏服务器登陆公共服务器用*/
	public static String mySecurity2;
	
	/**预约命令超时时间（秒）*/
	public static int bespokeCmdTime;
	
	/**充电命令超时时间（秒）*/
	public static int chargeCmdTime;
	
	/** 后台管理IP地址 */
	public static Set<String> gmIpList = new HashSet<String>();
	
	public static String epExePath;
	
	public static long rateUpdateTime;//每天定时扫描数据费率的时间

	public static int netKeepLiveInterval;//网络连接保持心跳的活动报文周期
	public static int reconnectInterval;//重连时间间隔周期
	public static int heartInterval;//监控客户端发送心跳周期

	public static int closeInterval;//链路关闭时间间隔周期

    public GameBaseConfig()
    {
		loadGameBaseConfig();
    }

    public GameBaseConfig(String path)
    {
    	if (path != null) {
    		if ("".equals(path)) path = this.getClass().getResource("/").getPath();
    		fileName = System.getProperty("file.separator")
    				+ path + "conf"
    				+ System.getProperty("file.separator")
    				+"GameConfig.properties";
    	}
		logger.info(LogUtil.addExtLog("fileName"),fileName);

		loadGameBaseConfig();
	}

	/**
	 * 加载GameConfig.properties文件
	 * @author 
	 * 2014-11-26
	 */
	public static void loadGameBaseConfig(){
		Properties p = getProperties(fileName);
		
		encoding = p.getProperty("encoding","UTF-8").trim();
		minCompressByteNum = Integer.valueOf(p.getProperty("minCompressByteNum","128").trim());
		maxOnLineNumber = Integer.valueOf(p.getProperty("maxOnLineNumber","2000").trim());
		
		isTest = Boolean.valueOf(p.getProperty("isTest","false").trim());
		
		mask1 = Byte.valueOf(p.getProperty("mask1","89").trim());
		mask2 = Byte.valueOf(p.getProperty("mask2","122").trim());
		mask3 = Byte.valueOf(p.getProperty("mask3","122").trim());
		mask4 = Byte.valueOf(p.getProperty("mask4","89").trim());
			
		
		keepAlive = Boolean.valueOf(p.getProperty("keepAlive","false").trim());
		isOpenLog = Boolean.valueOf(p.getProperty("isOpenLog","true").trim());
		
		maxEpNum = Integer.valueOf(p.getProperty("maxEpNum","5000").trim());//最大缓存数量
		
		mySecurity = p.getProperty("mySecurity", "ecngameperfect").trim();
		mySecurity2 = p.getProperty("mySecurity2", "3e0c9n4g4m4e359").trim();
			
		//获得GM后端管理IP集合
		String ipstring = p.getProperty("gmIpList","").trim();
		
		String[] ipList = ipstring.split(Symbol.DOUHAO);
		for(int i=0;i<ipList.length;i++){
			String s = ipList[i];
			gmIpList.add(s);
		}
		
		bespokeCmdTime = Integer.valueOf(p.getProperty("bespokeCmdTime","60").trim());//预约命令超时时间
		chargeCmdTime = Integer.valueOf(p.getProperty("chargeCmdTime","60").trim());//充电命令超时时间

		logger.info("可用cpu数量【{}】",GameBaseConfig.cpuCoreNum);
		
		
		rateUpdateTime = Long.valueOf(p.getProperty("rateUpdateTime","18000").trim());//每天定时扫描数据费率的时间
		
		netKeepLiveInterval = Integer.valueOf(p.getProperty("netKeepLiveInterval","240").trim());
		reconnectInterval = Integer.valueOf(p.getProperty("reconnectInterval","600").trim());
		heartInterval = Integer.valueOf(p.getProperty("heartInterval","120").trim());

		closeInterval = Integer.valueOf(p.getProperty("closeInterval","120").trim());
		
		//epExePath = System.getProperty("user.dir") + File.separator + "ep" + File.separator;	
	}
	
	/**
	 * 读取propertity文件的方法
	 * 2014-11-26
	 * @param fileName
	 * @return
	 */
	public static Properties getProperties(String fileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Properties properties = new Properties();
		try {
			properties.load(is);
			if(is!=null)is.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return properties;
	}

}
