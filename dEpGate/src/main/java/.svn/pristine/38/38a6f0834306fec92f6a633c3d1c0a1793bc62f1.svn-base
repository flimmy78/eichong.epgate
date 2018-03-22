package com.ec.epcore.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.Symbol;
import com.ec.epcore.test.ImitateConsumeService;
import com.ec.netcore.model.GameObject;
import com.ec.utils.LogUtil;

public class GameConfig extends GameObject{
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(GameConfig.class.getName()));
	
	static String fileName = System.getProperty("file.separator")
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

	
	
	/** 端口号 */
	public static int port;
	
	public static String bigAccount1002;
	
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
	
	public static long epNoInitConnectTimeout;//没有初始化成功的连接超时时间
	public static long epConnectTimeout;//初始化成功的电桩超时时间
	public static long epWaitGun; //超时判断为充电后11分钟 


	public static long analyzeReConnectInterval;//重连时间间隔周期
	public static long analyzeKeepLiveInterval;//网络连接保持心跳的活动报文周期
	public static long analyzeHeartInterval;//监控客户端发送心跳周期

	public static long chargeInfoUPTime; //充电信息更新30秒
	public static long saveRealInfoToDbInterval;//实时数据保存到数据库时间间隔3分钟
	public static long rateUpdateTimediff;//费率更新时间超过10分钟就等到第二天更新
	public static long maxChargeTime;//一次最大充电时间
	public static long maxChargeMeterNum;//一次最大充电度数
	public static long maxChargeAmt;//一次最大充电金额
	public static long maxChargeServiceAmt;//一次最大服务费金额
	public static long maxChargeCost;//一次最大消费金额
	public static long userAuthTimeOut;//卡充电鉴权超时时间30分钟
	public static long checkExpiringBesp;//预约快到期时间15分钟
	public static long identycodeTimeout1; //电桩识别码超时8分钟
	public static long identycodeTimeout2; //电桩识别码无效时间16分钟
	public static long freeBespTime;//免费预约时间
	public static long minBespTimeUnit;//最小预约时间
	public static int resendEpMsgTime;
	public static int resendEpMsgFlag;
	public static int sms; // 短信发送配置 1：发送到青云 2：发送到阿里
	
	public static int printEpMsg; // 打印电桩报文配置 0：不打印 2：打印
	public static int printPhoneMsg; // 打印手机报文配置 0：不打印 2：打印
	
	/**监控时间（秒）*/
	public static int montiorTimeInterval;
	
	public static int usrGateNoInitTimeout; //用户gate网关未注册超时时间
	public static int usrGateTimeout;//用户gate网关超时时间
	
	

	
	static {
		
		GameConfig.loadGameConfig();
	}
	
	/**
	 * 加载GameConfig.properties文件
	 * @author 
	 * 2014-11-26
	 */
	public static void loadGameConfig(){
		Properties p = getProperties(fileName);
		
		port = Integer.valueOf(p.getProperty("port","8888").trim());
		
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
		byte userNum= Byte.valueOf(p.getProperty("userNum","0").trim());
		for(int i=1;i<=userNum;i++)
		{
			String temp = "userName"+i;
			String userName = p.getProperty(temp,"").trim();
			temp = "userKey"+i;
			String userKey = p.getProperty(temp,"").trim();
			ImitateConsumeService.putConnetUserKey(userName, userKey);
		}
		
		bespokeCmdTime = Integer.valueOf(p.getProperty("bespokeCmdTime","60").trim());//预约命令超时时间
		chargeCmdTime = Integer.valueOf(p.getProperty("chargeCmdTime","60").trim());//充电命令超时时间

		logger.info("可用cpu数量【{}】",GameConfig.cpuCoreNum);
		
		bigAccount1002 = p.getProperty("big1002","").trim();
		
		rateUpdateTime = Long.valueOf(p.getProperty("rateUpdateTime","18000").trim());//每天定时扫描数据费率的时间
		
		montiorTimeInterval = Integer.valueOf(p.getProperty("montiorTimeInterval","60").trim());
	
		
		
		epNoInitConnectTimeout=Long.valueOf(p.getProperty("epNoInitConnectTimeout","10").trim());//
		epConnectTimeout=Long.valueOf(p.getProperty("epConnectTimeout","30").trim());//
		epWaitGun=Long.valueOf(p.getProperty("epWaitGun","660").trim());//


		analyzeReConnectInterval=Long.valueOf(p.getProperty("analyzeReConnectInterval","10").trim());//
		analyzeKeepLiveInterval=Long.valueOf(p.getProperty("analyzeKeepLiveInterval","60").trim());//
		analyzeHeartInterval=Long.valueOf(p.getProperty("analyzeHeartInterval","30").trim());//

		chargeInfoUPTime=Long.valueOf(p.getProperty("chargeInfoUPTime","30").trim());//
		saveRealInfoToDbInterval=Long.valueOf(p.getProperty("saveRealInfoToDbInterval","180").trim());//
		rateUpdateTimediff=Long.valueOf(p.getProperty("rateUpdateTimediff","600").trim());//
		maxChargeTime=Long.valueOf(p.getProperty("maxChargeTime","1440").trim());//
		maxChargeMeterNum=Long.valueOf(p.getProperty("maxChargeMeterNum","900000").trim());//
		maxChargeAmt=Long.valueOf(p.getProperty("maxChargeAmt","50000").trim());//
		maxChargeServiceAmt=Long.valueOf(p.getProperty("maxChargeServiceAmt","50000").trim());//
		maxChargeCost=Long.valueOf(p.getProperty("maxChargeCost","100000").trim());//
		userAuthTimeOut=Long.valueOf(p.getProperty("userAuthTimeOut","1800").trim());//
		checkExpiringBesp=Long.valueOf(p.getProperty("checkExpiringBesp","900").trim());//
		identycodeTimeout1=Long.valueOf(p.getProperty("identycodeTimeout1","480").trim());//
		identycodeTimeout2=Long.valueOf(p.getProperty("identycodeTimeout2","960").trim());//
		freeBespTime=Long.valueOf(p.getProperty("freeBespTime","300").trim());////免费预约时间
		minBespTimeUnit=Long.valueOf(p.getProperty("minBespTimeUnit","1800").trim());//最小预约时间
		
		resendEpMsgTime=Integer.valueOf(p.getProperty("resendEpMsgTime","40").trim());////免费预约时间
		resendEpMsgFlag=Integer.valueOf(p.getProperty("resendEpMsgFlag","1").trim());//最小预约时间
		
		usrGateNoInitTimeout= Integer.valueOf(p.getProperty("usrGateNoInitTimeout","20").trim());//用户gate连接超时时间
	    usrGateTimeout = Integer.valueOf(p.getProperty("usrGateTimeout","240").trim());//用户gate连接超时时间
	    
      
		sms = Integer.valueOf(p.getProperty("SMS","1").trim());
		printEpMsg = Integer.valueOf(p.getProperty("PrintEpMsg","0").trim());
        printPhoneMsg = Integer.valueOf(p.getProperty("PrintPhoneMsg","0").trim());
		
		epExePath = System.getProperty("user.dir") + File.separator + "ep" + File.separator;	
	}
	
	public static void main(String[] args) {
		System.out.println(crossDomainPolicy);
		System.out.println(cpuCoreNum);
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
			logger.error(LogUtil.addExtLog("exception"), e1.getStackTrace());
		}
		Properties properties = new Properties();
		try {
			properties.load(is);
			if(is!=null)is.close();
		} catch (Exception e) {
			logger.error(LogUtil.addExtLog("properties.load exception"), e.getStackTrace());
			throw new RuntimeException(e);
		}
		
		return properties;
	}

	
	public static String getString()
	{
		
		final StringBuilder sb = new StringBuilder();
        
        sb.append("rateUpdateTime(每天定时扫描数据费率的时间【秒】)=").append(rateUpdateTime).append("\n\n");
        sb.append("epNoInitConnectTimeout(没有初始化成功的电桩超时时间【秒】)=").append(epNoInitConnectTimeout).append("\n\n");
          
        sb.append("epConnectTimeout(初始化成功的电桩超时时间【秒】)=").append(epConnectTimeout).append("\n\n");   
        sb.append("epWaitGun(等待插枪超时判断【秒】)=").append(epWaitGun).append("\n\n");
        
        sb.append("analyzeReConnectInterval(监控重连间隔【秒】)=").append(analyzeReConnectInterval).append("\n\n");
        sb.append("analyzeKeepLiveInterval(监控超时时间【秒】)=").append(analyzeKeepLiveInterval).append("\n\n");
        sb.append("analyzeHeartInterval（监控心跳间隔【秒】）=").append(analyzeHeartInterval).append("\n\n");
        sb.append("chargeInfoUPTime(充电实时数据【秒】)=").append(chargeInfoUPTime).append("\n\n");
        sb.append("saveRealInfoToDbInterval(实时数据更新 到数据库时间间隔【秒】)=").append(saveRealInfoToDbInterval).append("\n\n");
        sb.append("rateUpdateTimediff(费率更新时间超过等到第二天更新【秒】)=").append(rateUpdateTimediff).append("\n\n");
        sb.append("maxChargeTime(一次充电最大时间【分】)=").append(maxChargeTime).append("\n\n");
        sb.append("maxChargeMeterNum(一次充电最大电量)=").append(maxChargeMeterNum).append("\n\n");
        sb.append("maxChargeAmt(一次充电最大充电金额)=").append(maxChargeAmt).append("\n\n");
        sb.append("maxChargeServiceAmt(一次充电最大服务费)=").append(maxChargeServiceAmt).append("\n\n");
        sb.append("maxChargeCost(一次充电最大消费金额)=").append(maxChargeCost).append("\n\n");
        sb.append("userAuthTimeOut(卡充电鉴权超时时间【秒】)=").append(userAuthTimeOut).append("\n\n");
        sb.append("checkExpiringBesp(预约快到期提醒时间【秒】)=").append(checkExpiringBesp).append("\n\n");
        sb.append("resendEpMsgTime(电桩重发时间间隔【秒】)=").append(resendEpMsgTime).append("\n\n");
        sb.append("resendEpMsgFlag(电桩重发标志)=").append(resendEpMsgFlag).append("\n\n");
        sb.append("usrGateNoInitTimeout(usrGate未注册超时时间【秒】)=").append(usrGateNoInitTimeout).append("\n\n");
        sb.append("usrGateTimeout(usrGate超时时间【秒】)=").append(usrGateTimeout).append("\n\n");
        
   		return sb.toString();
	  
	}
}
