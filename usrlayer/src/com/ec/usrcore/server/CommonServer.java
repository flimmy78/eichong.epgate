package com.ec.usrcore.server;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.logs.LogConstants;
import com.ec.netcore.conf.CoreConfig;
import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.model.conf.ServerConfigs;
import com.ec.netcore.netty.httpserver.AbstractHttpServer;
import com.ec.netcore.util.TimeUtil;
import com.ec.usrcore.config.GameBaseConfig;
import com.ec.usrcore.service.CacheService;
import com.ec.usrcore.service.EpChargeService;
import com.ec.usrcore.service.EpGateService;
import com.ec.utils.LogUtil;
import com.ormcore.cache.GameContext;

public class CommonServer {
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(CommonServer.class.getName()));

	private static Object lock = new Object();
	private static CommonServer commonServer;
	private IEventCallBack evt;
	private int severType;
	
	/**临时的监控长链接服务*/
	public static AbstractHttpServer watchHttpServer=null;

	private CommonServer() {
	}

	public static CommonServer getInstance() {
		synchronized(lock){
			if(commonServer==null){
				commonServer = new CommonServer();
			}
		}
		return commonServer;
	}

	public void initLog4j(String path) {
		String time = "【"+TimeUtil.getFormatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")+"】";
		System.out.println(time + "开始初始化log4j..." );
		DOMConfigurator.configure(path + "/conf/log4j.xml");
		
	}	

	/**
	 * 初始化
	 * @param severType（1:API(包括爱充提供协议的第三方),2:手机直接连接的API,3:第三方提供协议的第三方）
	 * @param gateId
	 * @param evt
	 */
	public void init(int severType, String path, IEventCallBack evt) {
		this.severType = severType;
		this.evt = evt;
		if (StringUtils.isNotEmpty(path)) initLog4j(path);

		
		new GameBaseConfig(path);//初始化服务器基础配置
		new GameContext(path);//初始化spring,加载数据库全局数据
		EpGateService.startScanEpGate(10);

		// 检查电桩通讯状态
        CacheService.startEpGateCommTimer(10);
        // 监控启动
        startWatch();
	}

	private void startWatch() {
		//创建netty服务器
		ServerConfigs serverConfigs = CoreConfig.serverConfigs;
		if (serverConfigs != null) {
			
			//内部监控http服务
			ServerConfig watchHttpSvrCfg = serverConfigs.get("data-centre-montior-server");
			if (watchHttpSvrCfg != null) {
				
				watchHttpServer = new WatchHttpServer(watchHttpSvrCfg);
				//nettyHttpServerList.add(watchHttpServer);
			}
			else
			{
				String errMsg = "【Gate服务器】缺少【内部监控】访问配置...服务器强行退出！";
				logger.error(LogUtil.getExtLog(errMsg));
			}
		}
	}

	public int initClientConnect(int orgNo,String userIdentity,
			String epCode, int epGunNo, String checkCode, String extra)
	{
		logger.info(LogUtil.getBaseLog(), new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,orgNo,userIdentity});
		return EpChargeService.initClientConnect(String.valueOf(extra), orgNo, userIdentity, epCode, epGunNo, checkCode);
	}

	/**
	 * 链路离线
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 */
	public void phoneDisconnect(int orgNo,String userIdentity, String epCode,int epGunNo)
	{
		logger.info(LogUtil.getBaseLog(), new Object[]{LogConstants.FUNC_PHONE_DISCONNECT,epCode,epGunNo,orgNo,userIdentity});
		EpChargeService.phoneDisconnect(orgNo, userIdentity, epCode, epGunNo);
	}
	
	/**
	 * 开始充电
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param startChargeStyle
	 * @param chargingAmt
	 * @param payMode
	 * @param carCode
	 * @param vinCode
	 * @param watchPrice
	 * @param token
	 * @return
	 */
	public int startChange(int orgNo,String userIdentity,String epCode,int epGunNo,
			short startChargeStyle,int chargingAmt,int payMode,
			String carCode, String vinCode, int watchPrice, String extra)
	{
		logger.info(LogUtil.addBaseExtLog("startChargeStyle|chargingAmt|payMode"),
				new Object[]{LogConstants.FUNC_START_CHARGE,epCode,epGunNo,orgNo,userIdentity,startChargeStyle,chargingAmt,payMode});

		int ret = EpChargeService.apiStartElectric(extra, orgNo, userIdentity,epCode, epGunNo,
				startChargeStyle, chargingAmt, payMode, watchPrice, carCode, vinCode, severType);
		if (ret != 0)
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_START_CHARGE,epCode,epGunNo,orgNo,userIdentity,ret});

		return ret;
	}

	/**
	 * 停止充电
	 * @param epCode
	 * @param epGunNo
	 * @param orgNo
	 * @param userId
	 * @param token
	 * @return
	 */
	public int stopChange(String epCode,int epGunNo,int orgNo,String userIdentity, String extra)
	{
		logger.info(LogUtil.addBaseExtLog("extra"),
				new Object[]{LogConstants.FUNC_STOP_CHARGE,epCode,epGunNo,orgNo,userIdentity,extra});

		int ret = EpChargeService.apiStopElectric(extra, orgNo, userIdentity, epCode, epGunNo);
		if (ret != 0)
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_STOP_CHARGE,epCode,epGunNo,orgNo,userIdentity,ret});

		return ret;
	}

	/**
	 * 根据订单号查询订单详情
	 * @param orgNo
	 * @param extra
	 */
	public int queryOrderInfo(String epCode,int epGunNo,int orgNo,String userIdentity, String extra)
	{
		logger.info(LogUtil.addBaseExtLog("extra"),
				new Object[]{LogConstants.FUNC_QUERY_ORDER,epCode,epGunNo,orgNo,userIdentity,extra});

		int ret = EpChargeService.queryOrderInfo(extra, orgNo, userIdentity, epCode, epGunNo);
		if (ret != 0)
			logger.info(LogUtil.addBaseExtLog("errorCode"),
					new Object[]{LogConstants.FUNC_QUERY_ORDER,epCode,epGunNo,orgNo,userIdentity,ret});

		return ret;
	}

	/**
	 * 充电命令应答(to EpGate)
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param ret
	 * @param errorCode
	 */
	public void onStartChange(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int ret,int errorCode) {
		if(evt==null)
			return;
		evt.onStartCharge(orgNo,userIdentity,epCode,epGunNo,extra,ret,errorCode);
	}

	/**
	 * 停止充电反馈(to EpGate)
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param ret
	 * @param errorCode
	 */
	public void onStopCharge(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int ret,int errorCode) {
		if(evt==null)
			return;
		evt.onStopCharge(orgNo,userIdentity,epCode,epGunNo,extra,ret,errorCode);
	}
	

	public void onQueryOrderInfo(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int ret,int errorCode) {
		if(evt==null)
			return;
		evt.onQueryOrderInfo(orgNo,userIdentity,epCode,epGunNo,extra,ret,errorCode);
	}
	
	/**
	 * 实时数据
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param extraData
	 */
	public void onChargeReal(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,Map<String, Object> extraData) {
		if(evt==null)
			return ;
		evt.onRealData(orgNo,userIdentity,epCode,epGunNo,extra,extraData);
	}

	/**
	 * 消费记录
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param extraData
	 */
	public void onConsumeRecord(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,Map<String, Object> extraData) {
		if(evt!=null) evt.onChargeOrder(orgNo,userIdentity,epCode,epGunNo,extra,extraData);
	}

	/**
	 * 爱充手机用户能否使用电桩
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param account
	 * @param ret
	 * @param errorCode
	 * @param status
	 */
	public void onClientConnect(int orgNo,String userIdentity,String epCode,int epGunNo,String account,int ret,int errorCode,int status,int currentType) {
		if(evt==null)
			return;
		evt.onCanUseEp(orgNo,userIdentity,epCode,epGunNo,account,ret,errorCode,status,currentType);
		
	}

	/**
	 * 充电事件
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param status
	 */
	public void onChargeEvent(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int status) {
		if(evt==null)
			return;
		evt.onChargeEvent(orgNo,userIdentity,epCode,epGunNo,extra,status);
	}

	/**
	 * 枪与车连接状态变化通知
	 * @param token
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param status
	 */
	public void onGunLinkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int status) {
		if(evt==null)
			return;
		evt.onGunLinkStatusChange(orgNo,userIdentity,epCode,epGunNo,extra,status);
	}

	/**
	 * 枪工作状态变化通知
	 * @param token
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param status
	 */
	public void onGunWorkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo, String extra,int status) {
		if(evt==null)
			return;
		evt.onGunWorkStatusChange(orgNo,userIdentity,epCode,epGunNo,extra,status);
	}
	
	public CommonServer getCommonServer() {
		return commonServer;
	}

	public IEventCallBack getEvt() {
		return evt;
	}

	public void setEvt(IEventCallBack evt) {
		this.evt = evt;
	}

	public int getSeverType() {
		return severType;
	}

	public void setSeverType(int severType) {
		this.severType = severType;
	}

}
