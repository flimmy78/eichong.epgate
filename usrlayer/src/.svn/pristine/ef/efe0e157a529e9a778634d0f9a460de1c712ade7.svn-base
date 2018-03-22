package com.ec.usrcore.service;

import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.common.net.U2ECmdConstants;
import com.ec.constants.CommStatusConstant;
import com.ec.constants.EpConstants;
import com.ec.constants.UserConstants;
import com.ec.logs.LogConstants;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.netcore.model.conf.ClientConfig;
import com.ec.usrcore.cache.AuthUserCache;
import com.ec.usrcore.cache.ElectricPileCache;
import com.ec.usrcore.cache.EpGunCache;
import com.ec.usrcore.config.EpGateConfig;
import com.ec.usrcore.net.client.EpGateNetConnect;
import com.ec.usrcore.net.codec.EpGateEncoder;
import com.ec.usrcore.net.sender.EpGateMessageSender;
import com.ec.usrcore.server.CommonServer;
import com.ec.usrcore.task.ScanEpGateTask;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import com.ormcore.dao.DB;
import com.ormcore.model.TblEpGateConfig;

public class EpGateService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateService.class.getName()));
	
	private static Map<Integer ,EpGateConfig> epGateConfs = new ConcurrentHashMap<Integer, EpGateConfig>();
	
	public static boolean isValidCmd(int cmd)
	{
		if( 
		cmd == U2ECmdConstants.EP_LOGIN||
		cmd == U2ECmdConstants.EP_ACK||
		cmd == U2ECmdConstants.EP_HEART||
		cmd == U2ECmdConstants.EP_ONLINE||
		cmd == U2ECmdConstants.PHONE_ONLINE||
		cmd == U2ECmdConstants.PHONE_CONNECT_INIT||
		cmd == U2ECmdConstants.EP_CHARGE||
		cmd == U2ECmdConstants.EP_CHARGE_EVENT||
		cmd == U2ECmdConstants.EP_STOP_CHARGE||
		cmd == U2ECmdConstants.EP_REALINFO||
		cmd == U2ECmdConstants.EP_CONSUME_RECODE||
		cmd == U2ECmdConstants.EP_GUN_CAR_STATUS||
		cmd == U2ECmdConstants.CCZC_QUERY_ORDER||
		cmd == U2ECmdConstants.EP_GUN_WORK_STATUS)
			return true;
		return false;
	}
	
	public static void startScanEpGate(long initDelay) {

		ScanEpGateTask checkTask = new ScanEpGateTask();

		TaskPoolFactory.scheduleAtFixedRate("CHECK_EPGATE_SERVICE_TASK",
				checkTask, initDelay, 30, TimeUnit.SECONDS);
	}
	public static void scanEpGate()
	{
		TblEpGateConfig cfg= new TblEpGateConfig();
		List<TblEpGateConfig> epGateCfgList = DB.epGateCfgDao.find1(cfg);
		
		logger.debug(LogUtil.addExtLog("epGateCfgList size"),epGateCfgList.size());
		
		connectAllGate(epGateCfgList);
	}
	
	public static EpGateConfig getEpGateCfg(int gateId)
	{
		return epGateConfs.get(gateId);
	}
	
	public static void addEpGateCfg(int gateId,EpGateConfig object)
	{
		if(gateId==0 || object==null)
		{
			logger.error(LogUtil.getExtLog("addGateConnectObject fail"));
			return;
		}
		epGateConfs.put(gateId,object);
	}
	public static void removeEpGateCfg(int gateId)
	{
		epGateConfs.remove(gateId);
		
	}
	public static int getSize()
	{
		return epGateConfs.size();
	}
	public static void EpGateConnect(TblEpGateConfig tblEpGateCfg)
	{
		int gateId = tblEpGateCfg.getPkGateid();
		int gateState = tblEpGateCfg.getGateState();
		
		logger.debug(LogUtil.addExtLog("gateId|gateState"),gateId,gateState);
		
		if( gateState==1 ){//创建一个Channel
			
			EpGateConfig  epGateCfg= getEpGateCfg(gateId);
			//内存中没有的话，加上
			if(epGateCfg==null)
			{
				//log.info("changeGateChannelCache gateId:"+gateId+"gateConnectionObject==null");
				epGateCfg=new EpGateConfig();
				
				epGateCfg.setEpGateId(gateId);
				epGateCfg.setIp(tblEpGateCfg.getGtseGateip());
				epGateCfg.setPort(tblEpGateCfg.getGtseGateport());
				//epGateCfg.setState(tblEpGateCfg.getGateState());
				
				addEpGateCfg(gateId, epGateCfg);
			}
			//log.info("changeGateChannelCache connect times:"+gateConnectionObject.getConnTimes());
			//失败次数大于6次不重连
			EpGateNetConnect epGateCommClient= CacheService.getEpGate(gateId);
			if(epGateCommClient == null  )
			{
	
				ClientConfig clrCfg = new ClientConfig();
				clrCfg.setIp(epGateCfg.getIp());
				clrCfg.setPort(epGateCfg.getPort());
				epGateCommClient=  EpGateNetConnect.getNewInstance(clrCfg);
				epGateCommClient.start();
				logger.info(LogUtil.addExtLog("connectAllGate ,ip|port"),clrCfg.getIp(),clrCfg.getPort());
				
				CacheService.addEpGate(epGateCfg.getEpGateId(),epGateCommClient);
			}
			//在连接函数中增加到列表里
		}
		else//所有不等于1的，都认为关闭
		//if(gateState == 2)
		{
			EpGateNetConnect  epGateCommClient= CacheService.getEpGate(gateId);
			if(epGateCommClient!=null)
			{
				//logger.info("changeGateChannelCache gateChannel:"+gateChannel);
				
				//epGateCommClient.close();//关闭连接
				CacheService.removeEpGate(gateId);//移除MAP数据
			}
			else
			{
				//logger.info("changeGateChannelCache GATE_TO_CHANNEL_INFO do not find gateId:"+gateId);
			}
			//
			EpGateConfig  epGateCfg= epGateConfs.get(gateId);
			if(epGateCfg!=null)
			{
				//logger.info("changeGateChannelCache connect times:"+gateConnectionObject.getConnTimes());
				epGateConfs.remove(gateId);
			}
		}
		
	}
	public static void connectAllGate(List<TblEpGateConfig> gateList)
	{
		//遍历内存在中有，但数据库中没有的
    	syncDb(gateList);
    	
    	int count = gateList.size();
    	
    	//判断数据库中增加和删除
		for (int i=0;i<count;i++) {//限制次数
			try {
				TblEpGateConfig tblEpGateCfg= gateList.get(i); 
				EpGateConnect(tblEpGateCfg);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("更新GATE-Channel异常：", e);
			}
		}
	}
	
	public static void syncDb(List<TblEpGateConfig> gateDbList)
	{
		logger.debug(LogUtil.addExtLog("syncDb 1 gateDbList size"),gateDbList.size());
		@SuppressWarnings("rawtypes")
		Iterator iter = epGateConfs.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			//logger.debug("syncDb 2 gateDbList size:{}",gateDbList.size());
			EpGateConfig gateObject=(EpGateConfig) entry.getValue();
			if(null == gateObject)
			{
				continue;
			}
			int  gateId1 = (int)entry.getKey();
			boolean find=false;
			for(TblEpGateConfig gate:gateDbList){
				int gateId2 = gate.getPkGateid();
				if(gateId1== gateId2)
				{
					find=true;
					break;
				}
			}
			//logger.debug("syncDb 3 gateDbList size:{}",gateDbList.size());
			if(find==false)//移除掉
			{
				EpGateNetConnect epGateCommClient =  CacheService.getEpGate(gateId1);
				if(epGateCommClient!=null)
				{
					//epGateCommClient.close();
				}
				CacheService.removeEpGate(gateId1);//移除MAP数据
				iter.remove();
				epGateConfs.remove(gateId1);      
			}
		}
		
	}

	/**
	 * 网关登录请求（usrGate->EPGate）
	 */
	public static void sendEpGateLogin(Channel channel,int serverType){
		logger.info(LogUtil.addExtLog("serverType"),serverType);
		
		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.login(serverType==1?2:serverType,hmsTime);

		String messagekey = serverType + NetUtils.timeToString(hmsTime);
		EpGateMessageSender.sendRepeatMessage(channel, reqData, messagekey);
	}

	/**
	 * 网关登录应答（EPGate->usrGate）
	 */
	public static void handleEpGateLogin(Channel channel,int h,int m,int s,int usrGateId,int epGateId,int ret,int errorCode)
	{
		logger.info(LogUtil.addExtLog("usrGateId|epGateId|ret|errorCode"),new Object[]{usrGateId,epGateId,ret,errorCode});
		
		String key = CommonServer.getInstance().getSeverType() + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);
		
		EpGateNetConnect epGateCommClient = CacheService.getEpGate(epGateId);
		if(epGateCommClient==null)
		{
			logger.info(LogUtil.addExtLog("not connected.epGateId"),epGateId);
			return;
		}
		epGateCommClient.setStatus(CommStatusConstant.INIT_SUCCESS);
		epGateCommClient.setLastUseTime(DateUtil.getCurrentSeconds());
		CacheService.addEpGateByCh(channel, epGateCommClient);
	}

	/**
	 * 心跳（usrGate->EPGate）
	 */
	public static void sendHeart(Channel channel){
		logger.info(LogUtil.addExtLog("channel"),channel);
		
		byte[] reqData = EpGateEncoder.heart();

		EpGateMessageSender.sendMessage(channel, reqData);
	}

	/**
	 * 心跳（EPGate->usrGate）
	 */
	public static void handleHeart(Channel channel){
		logger.info(LogUtil.getExtLog("receive heart"));
		
		setLastUseTime(channel);
	}

	/**
	 * ACK应答（usrGate->EPGate）
	 */
	private static void sendAck(Channel channel,int cmd,String accountId){
		logger.info(LogUtil.addExtLog("channel"),channel);
		setLastSendTime(channel);
		
		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.ack(cmd,hmsTime,accountId);

		EpGateMessageSender.sendMessage(channel, reqData);
	}

	/**
	 * ACK应答（EPGate->usrGate）
	 */
	public static void handleAck(Channel channel,int cmd,int h,int m,int s,long accountId){
		String key = accountId + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);
		logger.info(LogUtil.addExtLog("key"),key);
		setLastUseTime(channel);
	}

	/**
	 * 电桩在线通知（EPGate->usrGate）
	 */
	public static void handleEpOnline(Channel channel,int h,int m,int s,int online,String[] epCode){
		logger.info(LogUtil.addExtLog("channel|epCode[]"),channel,epCode);
		setLastUseTime(channel);

		for (String code : epCode)
		{
			ElectricPileCache epCache = CacheService.getEpCache(code);
			if (epCache != null) epCache.setState(online);
		}

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.epOnline(hmsTime);

		setLastSendTime(channel);
		EpGateMessageSender.sendMessage(channel, reqData);
	}

	/**
	 * 链路在线请求（usrGate->EPGate）
	 */
	public static void sendClientOnline(Channel channel,int accountId,int online){
		logger.info(LogUtil.addExtLog("accountId|online"),accountId,online);
		setLastSendTime(channel);

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.phoneOnline(hmsTime, accountId, online);

		EpGateMessageSender.sendMessage(channel, reqData);
	}

	/**
	 * 链路在线应答（EPGate->usrGate）
	 */
	public static void handleClientOnline(Channel channel,int h,int m,int s){
		logger.info(LogUtil.addExtLog("channel"),channel);

		setLastUseTime(channel);
	}

	/**
	 * 连接请求（usrGate->EPGate）
	 */
	public static void sendClientConnect(Channel channel,String epCode,int epGunNo,int accountId)
	{
		logger.debug(LogUtil.getBaseLog(), new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,UserConstants.ORG_I_CHARGE,accountId});

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.phoneConnect(hmsTime, epCode, epGunNo, accountId);

		String messagekey = epCode + epGunNo + NetUtils.timeToString(hmsTime);
		EpGateMessageSender.sendRepeatMessage(channel, reqData, messagekey);
	}

	/**
	 * 连接应答（EPGate->usrGate）
	 */
	public static void handleClientConnect(Channel channel,int h,int m,int s,String epCode,int epGunNo,long accountId,int ret,int errorCode,int status){
		logger.debug(LogUtil.addBaseExtLog("ret|errorCode"), new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,UserConstants.ORG_I_CHARGE,accountId,ret,errorCode});
		setLastUseTime(channel);
		
		String key = accountId + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);

		// 用户鉴权
		ElectricPileCache epCache = CacheService.getEpCache(epCode);
		String account = StringUtils.EMPTY;
		int currentType = EpConstants.EP_DC_TYPE;
		if(epCache==null)
		{
			logger.error(LogUtil.addBaseExtLog("ret|errorCode"), new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,UserConstants.ORG_I_CHARGE,accountId,ret,errorCode});
		}
		else
		{
			currentType = epCache.getCurrentType();
			account = CacheService.getUserInfo((int)accountId).getAccount();
			AuthUserCache auth =  new AuthUserCache((int)accountId, account, DateUtil.getCurrentSeconds(),(short)1);

			EpGunCache gunCache = CacheService.getEpGunCache(epCode, epGunNo);
			gunCache.setAuth(auth);
		}
		
		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onClientConnect(UserConstants.ORG_I_CHARGE, String.valueOf(accountId), epCode, epGunNo, account, ret, errorCode, status, currentType);
	}

	/**
	 * 充电请求（usrGate->EPGate）
	 */
	public static void sendCharge(Channel channel,String epCode,int epGunNo,String accountId,int changeMode,int frozenAmt, int payMode,int showPrice,int orgNo, String tradeMark, String vinCode, String transactionNo)
	{
		logger.info(LogUtil.addBaseExtLog("changeMode|frozenAmt|payMode|showPrice|tradeMark|vinCode|transactionNo"),
				new Object[]{LogConstants.FUNC_START_CHARGE,epCode,epGunNo,orgNo,accountId, changeMode, frozenAmt, payMode, showPrice, tradeMark, vinCode, transactionNo});

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.charge(hmsTime, epCode, epGunNo, accountId, frozenAmt, payMode, changeMode, showPrice, orgNo, tradeMark, vinCode, transactionNo);

		String messagekey = accountId + NetUtils.timeToString(hmsTime);
		EpGateMessageSender.sendRepeatMessage(channel, reqData, messagekey);
	}

	/**
	 * 充电应答（EPGate->usrGate）
	 */
	public static void handleCharge(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity, String extra,int ret,int errorCode)
	{
		logger.info(LogUtil.addBaseExtLog("extra|ret|errorCode"),
				new Object[]{LogConstants.FUNC_ONSTARTCHARGE,epCode,epGunNo,orgNo,userIdentity,extra,ret,errorCode});
		setLastUseTime(channel);
		
		String key = userIdentity + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);

		if (ret == 1) EpGunService.unUseEpGun(epCode, epGunNo, orgNo, userIdentity);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onStartChange(orgNo, userIdentity,epCode, epGunNo, extra, ret, errorCode);
	}

	/**
	 * 充电事件（EPGate->usrGate）
	 */
	public static void handleChargeEvent(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,int status)
	{
		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_ONCHARGEEVENT,epCode,epGunNo,orgNo,userIdentity,extra,status});
		setLastUseTime(channel);

		sendAck(channel, U2ECmdConstants.EP_CHARGE_EVENT, userIdentity);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onChargeEvent(orgNo, userIdentity,epCode, epGunNo, extra, status);
	}

	/**
	 * 停止充电请求（usrGate->EPGate）
	 */
	public static void sendStopCharge(Channel channel,String epCode,int epGunNo,int orgNo,String userIdentity,String token)
	{
		logger.info(LogUtil.addBaseExtLog("token"),
				new Object[]{LogConstants.FUNC_STOP_CHARGE,epCode,epGunNo,orgNo,userIdentity,token});

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.stopCharge(hmsTime, epCode, epGunNo, orgNo, userIdentity, token);

		String messagekey = userIdentity + NetUtils.timeToString(hmsTime);
		EpGateMessageSender.sendRepeatMessage(channel, reqData, messagekey);
	}

	/**
	 * 停止充电应答（EPGate->usrGate）
	 */
	public static void handleStopCharge(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,int ret,int errorCode){
		logger.info(LogUtil.addBaseExtLog("extra|ret|errorCode"),
				new Object[]{LogConstants.FUNC_ONSTOPCHARGE,epCode,epGunNo,orgNo,userIdentity,extra,ret,errorCode});
		setLastUseTime(channel);
		
		String key = userIdentity + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onStopCharge(orgNo, userIdentity,epCode, epGunNo, extra, ret, errorCode);
	}

	/**
	 * 订单详情查询请求（usrGate->EPGate）
	 */
	public static void sendOrderInfo(Channel channel,String epCode,int epGunNo,int orgNo,String userIdentity,String token)
	{
		logger.info(LogUtil.addBaseExtLog("token"),
				new Object[]{LogConstants.FUNC_QUERY_ORDER,epCode,epGunNo,orgNo,userIdentity,token});

		byte[] hmsTime = NetUtils.timeToByte();
		byte[] reqData = EpGateEncoder.queryOrderInfo(hmsTime, epCode, epGunNo, orgNo, userIdentity, token);

		String messagekey = userIdentity + NetUtils.timeToString(hmsTime);
		EpGateMessageSender.sendRepeatMessage(channel, reqData, messagekey);
	}

	/**
	 * 订单详情查询应答（EPGate->usrGate）
	 */
	public static void handleOrderInfo(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,int ret,int errorCode){
		logger.info(LogUtil.addBaseExtLog("extra|ret|errorCode"),
				new Object[]{LogConstants.FUNC_QUERY_ORDER,epCode,epGunNo,orgNo,userIdentity,extra,ret,errorCode});
		setLastUseTime(channel);
		
		String key = userIdentity + NetUtils.timeToString(h, m, s);
		CacheService.removeEpRepeatMsg(key);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onQueryOrderInfo(orgNo, userIdentity,epCode, epGunNo, extra, ret, errorCode);
	}

	/**
	 * 充电实时数据（EPGate->usrGate）
	 */
	public static void handleChargeReal(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,Map<String, Object> chargingInfo){
		logger.info(LogUtil.addBaseExtLog("extra|chargingInfo"),
				new Object[]{LogConstants.FUNC_ONREALDATA,epCode,epGunNo,orgNo,userIdentity,extra,chargingInfo});

		setLastUseTime(channel);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onChargeReal(orgNo, userIdentity,epCode, epGunNo, extra, chargingInfo);
	}

	/**
	 * 消费记录（EPGate->usrGate）
	 */
	public static void handleConsumeRecord(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,Map<String, Object> consumeRecordMap){
		logger.info(LogUtil.addBaseExtLog("extra|consumeRecordMap"),
				new Object[]{LogConstants.FUNC_CONSUME_RECORD,epCode,epGunNo,orgNo,userIdentity,extra,consumeRecordMap});

		setLastUseTime(channel);

		sendAck(channel, U2ECmdConstants.EP_CONSUME_RECODE, userIdentity);

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onConsumeRecord(orgNo, userIdentity,epCode, epGunNo, extra, (Map<String, Object>)consumeRecordMap);
	}

	/**
	 * 枪与车连接状态变化通知事件（EPGate->usrGate）
	 */
	public static void handleStatusChangeEvent(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,int status)
	{
		setLastUseTime(channel);
		
		//改变枪与车连接状态
		EpGunCache gunCache = CacheService.getEpGunCache(epCode, epGunNo);
		logger.debug(LogUtil.addBaseExtLog("gunCache"),
				new Object[]{LogConstants.FUNC_GUNLINK_STATUS,epCode,epGunNo,orgNo,userIdentity,gunCache});
		if(gunCache==null) return;

		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_GUNLINK_STATUS,epCode,epGunNo,orgNo,userIdentity,extra,status});

		AuthUserCache auth = gunCache.getAuth();
		if (auth == null) {
			logger.error(LogUtil.addExtLog("auth is null epCode|epGunNo"), epCode,epGunNo);
			return ;
		}

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onGunLinkStatusChange(orgNo, String.valueOf(auth.getUsrId()),epCode, epGunNo, extra, status);
		
	}

	/**
	 * 枪工作状态变化通知事件（EPGate->usrGate）
	 */
	public static void handleWorkStatusEvent(Channel channel,int h,int m,int s,String epCode,int epGunNo,int orgNo,String userIdentity,String extra,int status)
	{
		setLastUseTime(channel);
		
		//改变枪工作状态
		EpGunCache gunCache = CacheService.getEpGunCache(epCode, epGunNo);
		logger.debug(LogUtil.addBaseExtLog("gunCache"),
				new Object[]{LogConstants.FUNC_GUNWORK_STATUS,epCode,epGunNo,orgNo,userIdentity,gunCache});
		if(gunCache==null) return ;

		logger.info(LogUtil.addBaseExtLog("extra|status"),
				new Object[]{LogConstants.FUNC_GUNWORK_STATUS,epCode,epGunNo,orgNo,userIdentity,extra,status});

		String accountId = userIdentity;
		if (Integer.valueOf(userIdentity) == 0) {
			if (gunCache.getChargeCache() == null) {
				logger.error(LogUtil.addExtLog("chargeCache is null epCode|epGunNo"), epCode,epGunNo);
				return ;
			} else {
				accountId = String.valueOf(gunCache.getChargeCache().getUserId());
			}
		}

		CommonServer commonserver = CommonServer.getInstance();
		if (commonserver != null) commonserver.onGunWorkStatusChange(orgNo, accountId, epCode, epGunNo, extra, status);
		
	}

	/**
	 * EPGate最新响应时间设置
	 */
	private static void setLastUseTime(Channel channel){
		EpGateNetConnect epGateClient = CacheService.getEpGateByCh(channel);
		if (epGateClient == null) {
			logger.error(LogUtil.getExtLog("EpGateNetConnect is invalid"));
			return;
		}

		epGateClient.setLastUseTime(DateUtil.getCurrentSeconds());
	}

	/**
	 * usrGate最新发送时间设置
	 */
	private static void setLastSendTime(Channel channel){
		EpGateNetConnect epGateClient = CacheService.getEpGateByCh(channel);
		if (epGateClient == null) return;

		epGateClient.setLastSendTime(DateUtil.getCurrentSeconds());
	}
}
