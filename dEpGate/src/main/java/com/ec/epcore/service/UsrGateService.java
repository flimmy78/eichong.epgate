package com.ec.epcore.service;

import com.alibaba.fastjson.JSON;
import com.ec.common.net.U2ECmdConstants;
import com.ec.config.Global;
import com.ec.constants.*;
import com.ec.epcore.cache.*;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.codec.UsrGateEncoder;
import com.ec.epcore.net.proto.ConsumeRecord;
import com.ec.epcore.net.sender.UsrGateMessageSender;
import com.ec.epcore.net.server.UsrGateClient;
import com.ec.epcore.task.CheckUsrGateTask;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.client.ChannelManage;
import com.ec.netcore.conf.CoreConfig;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import com.ec.utils.NumUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargingrecord;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UsrGateService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(UsrGateService.class.getName()));
    
    private static ChannelManage cm = new ChannelManage();
	
	public static boolean isValidCmd(Channel  channel,int cmd)
	{
		UsrGateClient  usrGate = getClient(channel);
		if(usrGate==null)
		    return false;
		if((usrGate.getStatus()<CommStatusConstant.INIT_SUCCESS && cmd == U2ECmdConstants.EP_LOGIN))
			return true;
		else if(usrGate.getStatus()==CommStatusConstant.INIT_SUCCESS &&
				(cmd == U2ECmdConstants.EP_ACK||
				cmd == U2ECmdConstants.EP_HEART||
				cmd == U2ECmdConstants.EP_ONLINE||
				cmd == U2ECmdConstants.PHONE_ONLINE||
				cmd == U2ECmdConstants.PHONE_CONNECT_INIT||
				cmd == U2ECmdConstants.EP_CHARGE||
				cmd == U2ECmdConstants.EP_CHARGE_EVENT||
				cmd == U2ECmdConstants.EP_STOP_CHARGE||
				cmd == U2ECmdConstants.EP_CONSUME_RECODE||
				cmd == U2ECmdConstants.EP_GUN_CAR_STATUS||
				cmd == U2ECmdConstants.CCZC_QUERY_ORDER ||
				cmd == U2ECmdConstants.EP_4COMMON_REALDATA))
			return true;
		
		return false;
	}
	
	public static String getCacheSize()
	{
			return cm.getCacheSize();
		
	}
	public static void addConnect(UsrGateClient  client)
	{
		int ret = cm.addConnect(client);
		if(ret==0)
		{
			logger.error("addClient fail");
		}
	}
	public static void addClient(UsrGateClient  client)
	{
		int ret = cm.addClient(client);
		if(ret==0)
		{
			logger.error("addClient fail");
		}
		
	}
	public static UsrGateClient getClient(Channel  ch)
	{
		return (UsrGateClient) cm.get(ch);
	}
	public static UsrGateClient getClient(String key)
	{
		return (UsrGateClient) cm.get(key);
	}
	public static void removeClient(Channel ch)
	{
		 int ret = cm.remove(ch);
		 if(ret <1)
		 {
			 logger.error("removeUsrGate fail! ch:{},ret:{}",ch,ret);
		 }
	}
	public static void removeClient(String  key)
	{
		 int ret =cm.remove(key);
		 if(ret <1)
		 {
			 logger.error("removeUsrGate fail! key:{},ret:{}",key,ret);
		 }
		
	}
	/**
	 * 用户网关登录
	 * @param channel
	 * @param usrGateId
	 * @return
	 */
	public static int usrGateLogin(Channel  channel,int OrgType,int version)
	{
		//如果usrGateId已经有了,不能让新用户登录
		UsrGateClient  usrGate = getClient(channel);
		if(usrGate ==null)
		{
			logger.error("usrGate login fail,usrGate ==null,OrgType:{},ip:{}",
					new Object[]{OrgType,channel});
			return 7001;
		}
		String ip = NetUtils.getClientIp(channel);
		UsrGateClient usrGate2 = getClient(ip);
		if(usrGate2!=null && usrGate2.getStatus()==CommStatusConstant.INIT_SUCCESS)
		{
			logger.error("usrGate login,close oldClient,OrgType:{},newCh:{},oldCh:{}",
					new Object[]{OrgType,channel,usrGate2.getChannel()});
			usrGate2.getChannel().close();
			cm.remove(usrGate2);
		}
		
		//添加新连接
		usrGate.setIdentity(ip);
		usrGate.setStatus(CommStatusConstant.INIT_SUCCESS);
		usrGate.setVersion(version);
		//这里保存了来自哪个服务的标识 , 之后的很多事件推送跟这个有关
		usrGate.getUserOrigin().setCmdFromSource(OrgType);
		usrGate.getUserOrigin().setCmdChIdentity(ip);
		usrGate.setIp(ip);
		addClient(usrGate);
		
		logger.info("usrGate login success,OrgType:{},ip:{}",OrgType,ip);
		return 0;
		
	}
	/**
	 * 用户gate网关登录
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param OrgType
	 * @param version
	 */
	public static void handleUsrGateLogin(Channel channel,int h,int m,int s,int OrgType,int version)
	{
		logger.info("usrGate login,OrgType:{},version:{}",OrgType,version);
		
		 //判断通道是否正常
		int errorCode = usrGateLogin(channel,OrgType,version);
		
		int ret=1;
		if(errorCode>0)
		{
			logger.error("usrGate login fail,OrgType:{},version:{}",OrgType,version);
			ret =0;
		}
		
		int epGateId = CoreConfig.gameConfig.getId();
		byte[] data = UsrGateEncoder.login(0,epGateId,h,m,s,ret,errorCode);
		
		UsrGateMessageSender.sendMessage(channel,data);
		
	}
	/**
	 * 处理ACK
	 * @param channel
	 * @param cmd
	 * @param usrId
	 * @param h
	 * @param m
	 * @param s
	 */
	public static void handleAck(Channel channel,short cmd,long usrId,int h,int m,int s)
	{
		setLastUseTime(channel);
		String messagekey= ""+usrId+cmd;//+h+m+s;
		
//		UsrGateMessageSender.removeRepeatMsg(messagekey);
		EpCommClientService.removeRepeatMsg(messagekey);
	}
	/**
	 * 处理心跳
	 * @param channel
	 */
	public static void handleHeart(Channel channel)
	{
		setLastUseTime(channel);
	
		byte[] data = UsrGateEncoder.heard();
        UsrGateMessageSender.sendMessage(channel, data);
	}
	/**
	 * 处理电桩在线回复
	 * @param channel
	 */
	public static void handleEpOnlineResp(Channel channel)
	{
		setLastUseTime(channel);
	}
	/**
	 * 处理手机在线
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param usrId
	 * @param online
	 */
	public static void handlePhoneOnline(Channel channel,int h,int m,int s, long usrId,int online)
	{
		setLastUseTime(channel);
		UserCache usr = UserService.getUserCache((int)usrId);
		if(usr!=null)
		{
			logger.info("usrGate phoneOnline,accountId:{},online:{}",usrId,online);
			usr.setOnline(online);
		}
		
		byte[] data = UsrGateEncoder.phoneOnline(h,m,s);
		
		UsrGateMessageSender.sendMessage(channel,data);
		
		/*if(online == 0)
		{
			UserService.removeUserCache((int)usrId);
		}*/
	}
	/**
	 * 检查连接参数、判断
	 * @param epCode
	 * @param epGunNo
	 * @param epGunCache
	 * @param usrId
	 * @return
	 */
	public static int checkConnectEp(String epCode,int epGunNo,long usrId)
	{
		//查电桩
		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if(epCache==null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache == null || epGunCache.getEpNetObject()==null)
		{	
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if(!epGunCache.getEpNetObject().isComm())
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
				 
		UserCache userInfo = UserService.getUserCache((int)usrId);
		if(userInfo == null)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
				 
		int errorCode = epGunCache.canWatchCharge((int)usrId);
		if(errorCode>0)
		{
			return errorCode;
		}
		else if(errorCode==-1)
		{
			errorCode = epCache.canCharge(true, 0,0,0,epGunNo);
			if(errorCode > 0) return errorCode;
			errorCode = epGunCache.canCharge(EpConstants.CHARGE_TYPE_QRCODE,(int)usrId,true);
			if(errorCode>0) return errorCode;
					
			//errorCode = userInfo.canCharge(epCode+epGunNo,userInfo.getId(),OrgNo,"",0,false);
			//if(errorCode>0) return errorCode;
		}
			
		userInfo.setOnline(1); //设置手机在线
		return 0;
	}
	/**
	 * 初始化手机连接
	 * @param h
	 * @param m
	 * @param s
	 * @param epCode
	 * @param epGunNo
	 * @param usrId
	 * @return
	 */
	public static int phoneConnectEp(Channel channel,int h,int m,int s,String epCode,int epGunNo,long usrId)
	{ 
		UsrGateClient  usrGate = getClient(channel);
		if(usrGate ==null)
		{
			logger.error("usrGate phoneConnect fail,usrGate ==null,epCode:{},epGunNo:{},usrId:{},channel:{}",
					new Object[]{epCode,epGunNo,usrId,channel});
			return 7001;
		}
		
		int error = checkConnectEp(epCode, epGunNo,usrId);
		if (error > 0)
		{
			logger.error("usrGate phoneConnect fail,error:{},epCode:{},epGunNo:{},accountId:{},channel:{}",
					new Object[]{error,epCode,epGunNo,usrId,channel});
			return error;
		}

		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		short pos = getGunStatus(epGunCache);
		
		logger.info("usrGate phoneConnect success accountId:{},epCode:{},epGunNo:{},pos:{},channel:{}",
				new Object[] { usrId,epCode,epGunNo,pos,channel});

		byte ret = 0x01;
		short currentType = (short) ((epGunCache.getRealChargeInfo().getCurrentType() == EpConstants.EP_AC_TYPE) ? 1 : 2);

		byte[] respData = UsrGateEncoder.do_connect_ep_resp(h, m, s, epCode,
				epGunNo, usrId, ret, 0, (byte) pos);

		if (respData != null) {
			UsrGateMessageSender.sendMessage(channel, respData);
		}
		if (pos == 6) {
			// 卡充电在手机上显示
			if (epGunCache.getChargeCache() != null
					&& epGunCache.getChargeCache().getStartChargeStyle() == EpConstants.CHARGE_TYPE_CARD
					&& epGunCache.getChargeCache().getUserOrigin() != null) {
				epGunCache.getChargeCache().getUserOrigin().setCmdFromSource(2);
			}
			//修改用户对应的usrGate的IP地址
			if(epGunCache.getChargeCache() != null &&
					epGunCache.getCurUserId() == usrId)
			{
				//如果用户gate的IP和充电缓存中的IP不同，则修改充电缓存和数据库充电记录对应的IP
				if(usrGate.getIdentity().compareTo(epGunCache.getChargeCache().getUserOrigin().getCmdChIdentity()) !=0 )
				{
					epGunCache.getChargeCache().getUserOrigin().setCmdChIdentity(usrGate.getIdentity());
					//修改充电记录中的ip
					TblChargingrecord record = new TblChargingrecord();
					record.setChreTransactionnumber(epGunCache.getChargeCache().getChargeSerialNo());
					record.setUsrGateIp(usrGate.getIdentity());
					DB.chargingrecordDao.updateUsrGateIp(record);
				}
			}
			if(epGunCache.getCurUserId() == usrId)
			{
				logger.debug("pushFirstRealData");
			    epGunCache.pushFirstRealData();
			}
			
		}
		
		UserCache u= UserService.getUserCache((int)usrId);
		
		int orgNo=u.getCpyNumber();
		String usrLog = ""+usrId;
		String token="";
		
		
    
	    int gun2carLinkStatus = epGunCache.get_gun2carLinkStatus();
		sendGun2CarLinkStatus(gun2carLinkStatus,channel,epCode, epGunNo,
				orgNo,usrLog,token);

				
        AuthUserCache authUser = new AuthUserCache((int)usrId,u.getAccount(),DateUtil.getCurrentSeconds(),(short)1);
		  epGunCache.setAuthCache(authUser);
		return 0;
		
	}

	/**
	 * 获取电桩枪口状态
	 * 
	 * @param epGunCache
	 * @return
	 */
	private static short getGunStatus(EpGunCache epGunCache) {
		short pos = 0;
		int gunStatus = epGunCache.getStatus();
		if (gunStatus == GunConstants.EP_GUN_STATUS_CHARGE)
			pos = 6;
		else if (gunStatus == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
			pos = 3;
		else if (gunStatus == GunConstants.EP_GUN_STATUS_EP_OPER) {
			pos = 5;// 等待插枪
		} else if (gunStatus == GunConstants.EP_GUN_STATUS_WAIT_CHARGE || gunStatus == GunConstants.EP_GUN_STATUS_TIMER_CHARGE) {
			pos = 17;// 等待充电
		}
		return pos;
	}
	
	/**
	 * 初始化手机
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param epCode
	 * @param epGunNo
	 * @param usrId
	 */
	public static void handlePhoneInit(Channel channel, int h,int m,int s,String epCode,int epGunNo,long usrId)
	{
		setLastUseTime(channel);
		int errorCode = phoneConnectEp(channel,h,m,s,epCode,epGunNo,usrId);
		if(errorCode>0)
		{  
			logger.error("usrGate phoneConnect fail, errorCode:{},accountId:{},epCode:{},epGunNo:{},channel:{}",
					new Object[]{errorCode,usrId,epCode,epGunNo,channel});
			//返回失败
			byte[] respData = UsrGateEncoder.do_connect_ep_resp(h,m,s,epCode,epGunNo,usrId,0,errorCode,(byte)0);
			if(respData !=null)
			{
			    UsrGateMessageSender.sendMessage(channel, respData);
			}
		}
	}

	public static int startCharge(Channel channel,String ip,String epCode,
			int epGunNo,int orgNo,int OrgType,String usrLog,String token,int fronzeAmt,int payMode,int chargeStyle,
			int bDispPrice,String carNo,String carVin)
	{
		
	    if(orgNo!=UserConstants.ORG_I_CHARGE && payMode==EpConstants.P_M_FIRST)
			return  ErrorCodeConstants.INVALID_ACCOUNT;
	  
	    int usrId;
		UserCache u = null;
	    if(orgNo !=UserConstants.ORG_I_CHARGE)
		{
	    	if(orgNo ==UserConstants.ORG_CCZC)//曹操专车检查车与枪连接状态，断开不能充电
	    	{
	    		int error=checkCarLinkStatus( epCode, epGunNo);
	    		if(error>0)
	    			return error;
	    	}
	    	u=UserService.getUserIdByOrgNo(orgNo);
            usrId = u.getId();
	    	logger.debug("usrGate,usrId:{},OrgNo:{}",usrId,orgNo);
		}
	    else
	    {
	    	try{
	    		usrId = Integer.parseInt(usrLog);
	    	}
	    	catch(Exception e)
	    	{
	    		logger.error("usrGate startcharge exception,usrLog:{}",usrLog);
	    		return ErrorCodeConstants.INVALID_ACCOUNT;
	    	}
	    }
	    if(usrId<=0)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}

		// 查询并设置用户是否新手, 并放入map 缓存
		if(orgNo == UserConstants.ORG_I_CHARGE) u = UserService.getUserCache(usrId);
		if(u==null)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}

		byte[] cmdTimes = WmIce104Util.timeToByte();
		logger.info("usrGate accept startcharge  accountId:{},account:{},usrLog:{},chargeStyle:{}," +
				"epCode:{},epGunNo:{},carNo:{},carVin:{},OrgNo:{},OrgType:{},ip:{} from usrGate",
				new Object[]{u.getId(),u.getAccount(),usrLog,chargeStyle,
				epCode,epGunNo,carNo,carVin,orgNo,OrgType,ip});
		
	
		int errorCode = EpChargeService.apiStartElectric(epCode, 
				epGunNo, u,usrLog,null, "", (short)chargeStyle,
				fronzeAmt, payMode,orgNo!=UserConstants.ORG_I_CHARGE?orgNo:u.getCpyNumber(), OrgType,ip,token,cmdTimes);
		
		if(errorCode>0)
		{
			 logger.error("usrGate startcharge fail errorCode:{} accountId:{},account:{},chargeStyle:{},epCode:{},epGunNo:{} to phone",
	 					new Object[]{errorCode,u.getId(), u.getAccount(),1,epCode,epGunNo});
		}
		
	
		return errorCode;
	}
	/**
	 * 处理充电
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 * @param fronzeAmt
	 * @param payMode
	 * @param chargeStyle
	 * @param bDispPrice
	 * @param carNo
	 * @param carVin
	 */
	public static void  handleCharge(Channel channel,int h,int m,int s,String epCode,
			int epGunNo,int OrgNo,String usrLog,String token,int fronzeAmt,int payMode,int chargeStyle,
			int bDispPrice,String carNo,String carVin)
	{
		UsrGateClient  usrGate = getClient(channel);
	    if(usrGate == null)
	    {
	    	logger.error("usrGate startcharge fail not find usrGate,channel:{}",channel);
			 return ;
	    }
	    usrGate.setLastUseTime(DateUtil.getCurrentSeconds());
	   
	    
		int errorCode = startCharge(channel,usrGate.getIp(),epCode,epGunNo, OrgNo,usrGate.getUserOrigin().getCmdFromSource(), usrLog,token, fronzeAmt, payMode, chargeStyle,
				 bDispPrice, carNo, carVin);
		String extraData="";
		byte successflag = 0;
		byte[] data = null;
		if(OrgNo == UserConstants.ORG_CCZC || OrgNo == UserConstants.ORG_CHAT)
		 {
			if(errorCode>0)
			{
				successflag=1;
			}
			extraData =EpChargeService.getExtraData_CCZC(epCode,
					epGunNo, usrLog,
					token, 0, successflag, errorCode);
					
      		data = UsrGateEncoder.charge(h,m,s,epCode,epGunNo,OrgNo,usrLog,extraData,successflag, (short)errorCode);
		 }
		else
		{
	        if(errorCode>0)
	        {
	        	logger.error("usrGate startcharge fail errorCode:{},OrgNo:{},channel:{}",new Object[]{errorCode,OrgNo,channel});
	        	successflag = 1;
	        }
			extraData =EpChargeService.getExtraData_TCEC(epCode,
					epGunNo, token, 0, successflag, errorCode);

			data = UsrGateEncoder.charge(h,m,s,epCode,epGunNo,OrgNo,usrLog,extraData,successflag, (short)errorCode);
		}
		if(data!=null)
		{
			UsrGateMessageSender.sendMessage(channel, data);
			logger.info("startchargeRep,epCode:{},epGunNo:{},OrgNo:{},usrGate:{},extraData:{}", new Object[]{epCode, epGunNo,OrgNo, usrGate.getIp(), extraData});
		}
	} 
	
	/**
	 * 发送车与枪连接状态
	 * @param status
	 * @param channel
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 */
	public static void sendGun2CarLinkStatus(int status,Channel channel,String epCode,int epGunNo,int OrgNo,String usrLog,String token)
	{
		byte time[]  = WmIce104Util.timeToByte();
		
		byte[] pushData = UsrGateEncoder.do_gun2car_linkstatus(time[0],time[1],time[2],status,epCode,epGunNo,OrgNo,usrLog,token);
		
		if(pushData==null)
		{
			logger.error("usrGate carLinkStatus fail pushData == null,accountId:{},gun2car link status:{}",usrLog,status);
		}
		else
		{
			String messagekey = usrLog+U2ECmdConstants.EP_GUN_CAR_STATUS;//
			UsrGateMessageSender.sendRepeatMessage(channel,pushData,messagekey);
			logger.debug("usrGate carLinkStatus success accountId:{},gun2car link status:{},epCode:{},epGunNo:{}",
					new Object[]{usrLog,status,epCode,epGunNo});
		}
		
		
	}
	/**
	 * 处理枪和车连接状态
	 * @param status
	 * @param usrId
	 * @param epCode
	 * @param epGunNo
	 */
	
	public static void handleGun2CarLinkStatus(int status,long usrId,String epCode,int epGunNo)
	{
		UserCache u= UserService.getUserCache((int)usrId);
		if (u == null) return;
		int orgNo=u.getCpyNumber();
		Iterator iter = cm.getMapClients().entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			UsrGateClient  usrGate=(UsrGateClient) entry.getValue();
			//推全国
			if(null == usrGate || usrGate.getChannel()==null|| !usrGate.isComm()
					|| usrGate.getUserOrigin()==null
					|| usrGate.getUserOrigin().getCmdFromSource() != UserConstants.CMD_FROM_PHONE)
			{
				continue;
			}	
			if(usrGate.getStatus()<CommStatusConstant.INIT_SUCCESS)
			{
				continue;
			}
			if(status!=1)
		    {
			    status=2;
		    }
		    String usrLog = ""+usrId;
		    String token = "";
		
		
		    sendGun2CarLinkStatus(status,usrGate.getChannel(),epCode,epGunNo,orgNo,usrLog,token);
		}
		
	}
	/**
	 * 发送枪口工作状态
	 * @param status
	 * @param channel
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 */
	
	public static void sendGunWorkStatus(int status,Channel channel,String epCode,int epGunNo,int OrgNo,String usrLog,String token)
	{
		byte time[]  = WmIce104Util.timeToByte();
		
		byte[] pushData = UsrGateEncoder.do_gun_workstatus(time[0],time[1],time[2],status,epCode,epGunNo,OrgNo,usrLog,token);
		
		if(pushData==null)
		{
			logger.error("usrGate carLinkStatus fail pushData == null,accountId:{},gun2car link status:{}",usrLog,status);
		}
		else
		{
			String messagekey = usrLog+U2ECmdConstants.EP_GUN_WORK_STATUS;//
			UsrGateMessageSender.sendRepeatMessage(channel,pushData,messagekey);
			logger.debug("handleGunWorkStatus to usrGate success epCode:{},epGunNo:{},orgNo:{},accountId:{},gunWorkStatus:{},ip:{}",
					new Object[]{epCode, epGunNo, OrgNo,usrLog,status,channel.remoteAddress()});
		}

		
	}
	//发送状态变化数据给html 全国的
	public static void sendAllGunWorkStatus4Html(Channel channel, String epCode, int epGunNo, int currenType, Map realData) {
		byte time[] = WmIce104Util.timeToByte();
		String realDataStr = JSON.toJSONString(realData);
		byte[] pushData = UsrGateEncoder.sendAllGunWorkStatus4Html(time[0], time[1], time[2], epCode, epGunNo, currenType, realDataStr);
		UsrGateMessageSender.sendMessage(channel, pushData);
		logger.info("sendAllGunWorkStatus4Html usrGateIp:{},epCode:{},epGunNo:{},realData=3_1:{}", new Object[]{channel.remoteAddress(), epCode, epGunNo, realData.get("3_1")});


	}
	
	/**
	 * 发送枪工作状态
	 * @param status
	 * @param usrId
	 * @param epCode
	 * @param epGunNo
	 */
	public static void handleGunWorkStatus(int oldstatus, int status,long usrId,String epCode,int epGunNo)
	{
		UserCache u= UserService.getUserCache((int)usrId);
		if (u == null) return;
		int orgNo=u.getCpyNumber();
        Iterator iter = cm.getMapClients().entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			UsrGateClient  usrGate=(UsrGateClient) entry.getValue();
			if(null == usrGate || usrGate.getChannel()==null|| !usrGate.isComm()
					|| usrGate.getUserOrigin()==null
					|| usrGate.getUserOrigin().getCmdFromSource() != UserConstants.CMD_FROM_PHONE)
			{
				continue;
			}	
			if(usrGate.getStatus()<CommStatusConstant.INIT_SUCCESS)
			{
				continue;
			}
		
		    String usrLog = ""+usrId;
		    String token = ""+oldstatus;
		    sendGunWorkStatus(status,usrGate.getChannel(),epCode,epGunNo,orgNo,usrLog,token);
		}
		
	}
	//给html推送全国idle 变化状态的数据
	public static void handleAllGunWorkStatus4Html(String epCode,int epGunNo, int currentType,Map realData ){
		//把全国的空闲时的数据只发给html不发给其他usrgate 由于小程序的原因给的来源是2但是走的是html ,所以小程序的数据会漏掉,
		cm.getMapClients().forEach((k,v)->{
			UsrGateClient usrGate = (UsrGateClient) v;
			try {
				if (usrGate.getUserOrigin().getCmdFromSource() != UserConstants.CMD_FROM_PHONE && usrGate.getUserOrigin().getCmdFromSource() != UserConstants.CMD_FROM_API && usrGate.getStatus() == CommStatusConstant.INIT_SUCCESS) {
					sendAllGunWorkStatus4Html(usrGate.getChannel(), epCode, epGunNo, currentType, realData);
				}
			}catch (Exception e){
				logger.error("handleAllGunWorkStatus4Html error epCode:{},exception:{}", epCode,e.getMessage());
			}

		});

	}
	/**
	 * 处理停止充电命令
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 */
	
	public static void handleStopCharge(Channel channel,int h,int m,int s,String epCode,
			int epGunNo,int OrgNo,String usrLog,String token)
	{
		UsrGateClient  usrGate = getClient(channel);
	    if(usrGate == null)
	    {
	    	 logger.error("usrGate stopcharge fail not find usrGate,channel{}",channel);
			 return;
	    }
		setLastUseTime(channel);
		int errorCode= stopCharge(usrGate.getIp(), epCode, epGunNo, OrgNo, usrLog, token);

		String extraData="";
		byte successflag =  1;
		byte[] data=null;
		
		if(errorCode>0)
		{ //失败
			successflag = 0;
		}
		EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache!=null)
			extraData = epGunCache.getOrgNoExtra(OrgNo,1,successflag,errorCode);

		data =  UsrGateEncoder.stopCharge(h, m, s, epCode, epGunNo,OrgNo,usrLog,
				extraData, successflag, (short)errorCode);

		if(data!=null)
		{
			UsrGateMessageSender.sendMessage(channel, data);
			logger.info("onStopCharge,epCode:{},epGunNo:{},OrgNo:{},extraData:{}",new Object[]{epCode, epGunNo,OrgNo,extraData});
		}
	
	}
	/**
	 * 停止充电
	 * @param ip
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 * @return
	 */
	public static int stopCharge(String ip,String epCode,int epGunNo,int OrgNo,String usrLog,String token)
	{
		int usrId=0;
		if(OrgNo !=UserConstants.ORG_I_CHARGE)
		{
            UserCache userInfo=UserService.getUserIdByOrgNo(OrgNo);
            usrId=userInfo.getId();
		}
		else
		{
		    try{
		       usrId = Integer.parseInt(usrLog);
		    }
		    catch(Exception e)
		    {
		    	logger.error("usrGate stopcharge exception,usrLog:{}",usrLog);
		    	return ErrorCodeConstants.INVALID_ACCOUNT;
		    }
		}
		if(usrId<=0)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		UserCache u = UserService.getUserCache(usrId);
		
		int errorCode = EpChargeService.apiStopElectric(epCode,
				epGunNo, OrgNo,usrId,usrLog,OrgNo,ip);

 
       if(errorCode>0)
       {
    	   logger.error("usrGate stopcharge fail errorCode:{},accountId:{},account:{},epCode:{},epGunNo:{}",
         			 new Object[]{errorCode,usrId,u.getAccount(),epCode,epGunNo});
    	   
       }
       return errorCode;
	}
	
	
	
	/**
	 * 
	 * @param channel
	 */

	public static void offLine(Channel channel){
			
		UsrGateClient usrGate =  getClient(channel);
		
		if (usrGate != null) {
			logger.info("usrGate offLine. commClient:{},Identity:{},setStatus(0)",usrGate.getChannel(),usrGate.getIdentity());
			
			channel.close();	
		    
			usrGate.setStatus(0);
			removeClient(usrGate.getIdentity());
			removeClient(channel);
		
		}
		else
		{
			//logger.info("\n\n\noffLine,phoneClient:");
		}
			
	}
	
	public static void startCommClientTimeout(long initDelay) {
		
		CheckUsrGateTask checkTask =  new CheckUsrGateTask();
				
		TaskPoolFactory.scheduleAtFixedRate("CHECK_USRGATE_TIMEOUT_TASK", checkTask, initDelay, 10, TimeUnit.SECONDS);
	}
	

	@SuppressWarnings("rawtypes")
	public synchronized static void checkTimeOut()
	{
		String msg = cm.checkTimeOut(GameConfig.usrGateNoInitTimeout, GameConfig.usrGateTimeout);
		
		logger.info("checkTimeOut {}",msg);
	}
	
	public static boolean isComm(UserOrigin userOrigin)
	{
		String actionIdentity="";
		if(userOrigin!=null)
			actionIdentity = userOrigin.getCmdChIdentity();
		
		if(actionIdentity.length()==0)
		{
			logger.error("usrGate actionIdentity is null,userOrigin:{}",userOrigin);
			return false;
		}
		UsrGateClient usrGate = getClient(actionIdentity);
		if(usrGate==null)
		{
			logger.error("usrGate not find usrGateClient,actionIdentity:{}",actionIdentity);
			return false;
		}
		return usrGate.isComm();
		
	}
	// 非html渠道充电实时数据 推送到html 有30s的限制 @hm
	public static void realChargeData4Html(int type, UserOrigin userOrigin, int ret, int cause, Object srcParams, Object extraData){

		try {
		switch (type){
			case EventConstant.EVENT_REAL_CHARGING: {

				Iterator iter = cm.getMapClients().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					UsrGateClient usrGate = (UsrGateClient) entry.getValue();
					//CMD_FROM_third 把其他地方发起充电的实时数据推给html,并排除html自己发起的
					if (null == usrGate || usrGate.getChannel() == null || !usrGate.isComm()
							|| usrGate.getUserOrigin() == null || userOrigin.getCmdFromSource() == UserConstants.CMD_FROM_third
							|| usrGate.getUserOrigin().getCmdFromSource() != UserConstants.CMD_FROM_third) {
						continue;
					}
					if (usrGate.getStatus() < CommStatusConstant.INIT_SUCCESS) {
						continue;
					}
					//实时充电数据信息  105
					Map<String, Object> paramsMap = (Map<String, Object>) srcParams;

					String epCode = (String) paramsMap.get("epcode");
					int usrId = (int) paramsMap.get("usrId");
					int OrgNo = (int) paramsMap.get("orgn");
					int epGunNo = (int) paramsMap.get("epgunno");
					String usrLog = (String) paramsMap.get("usrLog");
					String token = (String) paramsMap.get("token");
					if (extraData == null) {
						logger.error("usrGate service onEvent realChargeData4Html error,extraData==null");
						return;
					}
					byte[] time = WmIce104Util.timeToByte();

					ChargingInfo chargingInfo = (ChargingInfo) extraData;

					byte[] data = UsrGateEncoder.chargeRealInfo(time[0], time[1], time[2], epCode, epGunNo, OrgNo, usrLog,
							token, chargingInfo, U2ECmdConstants.EP_REALINFO_4HTML);
					UsrGateMessageSender.sendMessage(usrGate.getChannel(), data);

					logger.debug("realChargeData4Html --->realDataPush,epCode:{},epGunNo:{},userOrigin:{},chargingInfo:{}",
							new Object[]{epCode, epGunNo, userOrigin, JSON.toJSONString(chargingInfo)});
				}
			}
			break;
			default:
				break;
		   }
		} catch (Exception e) {
			logger.error("realChargeData4All error,{}",e.getMessage());
		}
	}
	public static void onEvent(int type,UserOrigin userOrigin,int ret,int cause,Object srcParams, Object extraData)
	{
		try{
		String actionIdentity="";
		if(userOrigin!=null)
			actionIdentity = userOrigin.getCmdChIdentity();
		logger.debug("usrGate onEvent type:{},actionIdentity:{},extraData:{}",new Object[]{type,actionIdentity,extraData});
		
		
		UsrGateClient usrGate = getClient(actionIdentity);
		if(usrGate==null)
		{
			logger.info("usrGate onEvent fail, not find UsrGateClient,actionIdentity:{},type:{}",actionIdentity,type);
			return ;
		}
		if(!usrGate.isComm())
		{
			logger.info("usrGate onEvent fail, UsrGateClient isComm=false,actionIdentity:{},type:{}",actionIdentity,type);
			return ;
		}
		switch(type)
		{
		case EventConstant.EVENT_BESPOKE:
			break;
		case EventConstant.EVENT_CHARGE_EP_RESP:
		{
			Map<String, Object> paramsMap = (Map<String, Object>)extraData;
			
			String epCode = (String)paramsMap.get("epcode");
			int epGunNo = (int)paramsMap.get("epgunno");
			int OrgNo = (int)paramsMap.get("orgn");
			String usrLog = (String)paramsMap.get("usrLog");
			String token = (String)paramsMap.get("token");
			String extra = (String)paramsMap.get("extraData");
			logger.debug("usrGate service onEvent ,OrgNo:{}:extra:{}",OrgNo,extra);
			
			byte[] time = WmIce104Util.timeToByte();
			
			if(OrgNo == UserConstants.ORG_CCZC || OrgNo == UserConstants.ORG_CHAT)
			{
				token= extra;
			}
			
			byte[]  data = UsrGateEncoder.charge(time[0],time[1],time[2],epCode, epGunNo, OrgNo,usrLog,token,ret, (short)cause);
			
			if(data !=null)
			{
			   UsrGateMessageSender.sendMessage(usrGate.getChannel(), data);
			}
	        logger.info("onEvent---> EVENT_CHARGE_EP_RESP,epCode:{},epGunNo:{},OrgNo:{}:usrLog:{},extra:{}\n",
	        		new Object[]{epCode,epGunNo,OrgNo,usrLog,extra});
		}
			break;
		
		case EventConstant.EVENT_STOP_CHARGE_EP_RESP:
		{
			Map<String, Object> paramsMap = (Map<String, Object>)extraData;
			
			String epCode = (String)paramsMap.get("epcode");
			int epGunNo = (int)paramsMap.get("epgunno");
			int OrgNo = (int)paramsMap.get("orgn");
			//if (OrgNo == UserConstants.ORG_CHAT) return;

			String usrLog = (String)paramsMap.get("usrLog");
			String token = (String)paramsMap.get("token");
			String extra = (String)paramsMap.get("extraData");
			
			byte[] time = WmIce104Util.timeToByte();
			
			if(OrgNo == UserConstants.ORG_CCZC || OrgNo ==UserConstants.ORG_EC || OrgNo == UserConstants.ORG_CHAT)
			{
				token = extra;
			}
			
			byte[] data = UsrGateEncoder.stopCharge(time[0],time[1],time[2],epCode, epGunNo, OrgNo,usrLog,token,(byte)ret, (short)cause);
			
			if(data !=null)
			{
			    UsrGateMessageSender.sendMessage(usrGate.getChannel(), data);
			}
			logger.info("onEvent---> EVENT_STOP_CHARGE_EP_RESP,epCode:{},epGunNo:{},OrgNo:{}:usrLog:{},extra:{}\n",
					new Object[]{epCode, epGunNo, OrgNo, usrLog, extra});
		}
			break;
		case EventConstant.EVENT_CONSUME_RECORD:
		{
			Map<String, Object> paramsMap = (Map<String, Object>)srcParams;

			String epCode = (String)paramsMap.get("epcode");
			int epGunNo = (int)paramsMap.get("epgunno");

			int OrgNo = (int)paramsMap.get("orgn");
			String usrLog = (String)paramsMap.get("usrLog");
			String token = (String)paramsMap.get("token");

			int pkEpId = (int)paramsMap.get("pkEpId");

			String chargeOrder = (String)paramsMap.get("orderid");
			int usrId = (int)paramsMap.get("usrId");
			int userFirst = (int)paramsMap.get("userFirst");

			ConsumeRecord consumeRecord  = (ConsumeRecord)extraData;
			int couPonAmt = (int)paramsMap.get("couPonAmt");
			int realCouPonAmt = (int)paramsMap.get("realCouPonAmt");
			if (consumeRecord.getType() == 1) {
				couPonAmt = NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(couPonAmt).multiply(Global.DecTime2));
				realCouPonAmt = NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(realCouPonAmt).multiply(Global.DecTime2));
			}
			int chargeStyle = -1;
			int personalAmt = (int)paramsMap.get("personalAmt");
			if (paramsMap.get("chargeStyle") != null) chargeStyle = Integer.valueOf((String)paramsMap.get("chargeStyle"));

			byte[] time = WmIce104Util.timeToByte();
			byte[] data = null;
			if(OrgNo == 0)
				OrgNo = UserConstants.ORG_I_CHARGE;
			if(OrgNo == UserConstants.ORG_I_CHARGE )
			{
				if (consumeRecord.getType() == 1) {
					data = UsrGateEncoder.IchargeRecord(time[0],time[1],time[2],epCode,epGunNo,OrgNo,usrLog,token,pkEpId,
							chargeOrder,consumeRecord.getStartTime(),consumeRecord.getEndTime(),
							consumeRecord.getTotalDl(),NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(consumeRecord.getTotalChargeAmt()).multiply(Global.DecTime2)),
							NumUtil.BigDecimal2ToInt(NumUtil.intToBigDecimal42(consumeRecord.getServiceAmt()).multiply(Global.DecTime2)),
							userFirst,couPonAmt,realCouPonAmt,personalAmt,chargeStyle);
				} else {
					data = UsrGateEncoder.IchargeRecord(time[0],time[1],time[2],epCode,epGunNo,OrgNo,usrLog,token,pkEpId,
							  chargeOrder,consumeRecord.getStartTime(),consumeRecord.getEndTime(),
							  consumeRecord.getTotalDl(),consumeRecord.getTotalChargeAmt(),
							  consumeRecord.getServiceAmt(),userFirst,couPonAmt,realCouPonAmt,personalAmt,chargeStyle);
				}
			}
			else
			{
			     data = UsrGateEncoder.chargeRecord(time[0],time[1],time[2],epCode,epGunNo,OrgNo,usrLog,token,
					  chargeOrder,consumeRecord,userFirst,couPonAmt,realCouPonAmt,personalAmt);

			}
			if(data !=null)
			{
			     String messagekey = usrGate.getChannel().toString()+usrId+U2ECmdConstants.EP_CONSUME_RECODE;//+time[0]+time[1]+time[2];
				System.out.println("messagekey:"+messagekey);
				UsrGateMessageSender.sendRepeatMessage(usrGate.getChannel(),data,messagekey);
			}
			logger.info("onEvent---> EVENT_CONSUME_RECORD,epCode:{},epGunNo:{},usrId:{},chargeOrder:{},OrgNo:{},consumeRecord:{} ",new Object[]{epCode,epGunNo, usrId, chargeOrder,OrgNo,JSON.toJSONString(consumeRecord)});
		}
			break;
		case EventConstant.EVENT_REAL_CHARGING:
		{
			//实时充电数据信息  105
            Map<String, Object> paramsMap = (Map<String, Object>)srcParams;
			
			String epCode = (String)paramsMap.get("epcode");
			int epGunNo = (int)paramsMap.get("epgunno");
			int usrId = (int)paramsMap.get("usrId");
			int OrgNo = (int)paramsMap.get("orgn");
			String usrLog = (String)paramsMap.get("usrLog");
			String token = (String)paramsMap.get("token");
			logger.debug("usrGate service onEvent EVENT_REAL_CHARGING,orgn:{},usrId:{}\n",OrgNo,usrId);
			if(extraData==null)
			{
				logger.error("usrGate service onEvent EVENT_REAL_CHARGING error,extraData==null");
				return ;
			}
			/*if(OrgNo == 0)
				OrgNo = UserConstants.ORG_I_CHARGE;
			if(OrgNo != UserConstants.ORG_I_CHARGE && OrgNo != UserConstants.ORG_CHAT)
			{
				logger.error("usrGate service onEvent EVENT_REAL_CHARGING error,OrgNo:{}",OrgNo);
				return; //只有爱充的手机实时数据发送
			}*/
			UserCache usr = UserService.getUserCache((int)usrId);
			if(usr ==null)
			{  
				logger.info("usrGate service onEvent  EVENT_REAL_CHARGING fail,usr is null or usr is not online");
				break;
			}
			byte[] time = WmIce104Util.timeToByte();
			
			ChargingInfo chargingInfo = (ChargingInfo)extraData;

			byte[] data = UsrGateEncoder.chargeRealInfo(time[0],time[1],time[2],epCode,epGunNo,OrgNo,usrLog,
					    token,chargingInfo, U2ECmdConstants.EP_REALINFO);
			UsrGateMessageSender.sendMessage(usrGate.getChannel(), data);
			logger.debug("onEvent EVENT_REAL_CHARGING,epCode:{},epGunNo:{},userOriginIp:{},chargingInfo:{}",
					new Object[]{epCode, epGunNo, userOrigin.getCmdChIdentity(), JSON.toJSONString(chargingInfo)});
		}
			break;
        case EventConstant.EVENT_CHARGE:
		case EventConstant.EVENT_START_CHARGE_EVENT:
		{
            Map<String, Object> paramsMap = (Map<String, Object>)extraData;
			
			String epCode = (String)paramsMap.get("epcode");
			int epGunNo = (int)paramsMap.get("epgunno");
			int OrgNo = (int)paramsMap.get("orgn");
			String usrLog = (String)paramsMap.get("usrLog");
			String token = (String)paramsMap.get("extraData");
			byte[] time = WmIce104Util.timeToByte();
			
			byte[] data = UsrGateEncoder.chargeEvent(time[0],time[1],time[2],epCode, epGunNo,
					OrgNo,usrLog,token, ret);
			
			UsrGateMessageSender.sendMessage(usrGate.getChannel(), data);
			logger.info("onEvent--->EVENT_START_CHARGE_EVENT,epCode:{},epGunNo:{},OrgNo:{},usrGateIp:{},extraData:{}", new Object[]{epCode, epGunNo, OrgNo, usrGate.getIp(), token});
		}
			break;
		default:
			break;
		
		  }
		}
		catch (Exception e) 
		{
			logger.error("usrGate onEvent exception");
			return ;
		}
	}
	
	public static void setLastUseTime(Channel channel)
	{
		UsrGateClient  usrGate = getClient(channel);
	    if(usrGate != null)
	    {
		    usrGate.setLastUseTime(DateUtil.getCurrentSeconds());
	    }
	}
   /**
    * 广播到所有用户gate电桩状态
    * @param msg
    */
	public static void notifyUsrGate(byte[] msg)
	{
		Iterator iter = cm.getMapClients().entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			UsrGateClient  usrGate=(UsrGateClient) entry.getValue();
			if(null == usrGate || usrGate.getChannel()==null|| !usrGate.isComm())
			{
				continue;
			}	
			if(usrGate.getStatus()<CommStatusConstant.INIT_SUCCESS)
			{
				continue;
			}
			UsrGateMessageSender.sendMessage(usrGate.getChannel(), msg);
		}
	}
	
	public static String stat()
	{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("getCacheSize()：").append(getCacheSize()).append(" \n\n");
		
        Iterator iter = cm.getMapClients().entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			
			UsrGateClient usrGate=(UsrGateClient) entry.getValue();	
			if(usrGate!=null)
			{
				sb.append(usrGate.toString()).append(" \n");
			}
	     }
		 return sb.toString();
	}
	/**
	 * 曹操专车查询订单
	 * @param channel
	 * @param h
	 * @param m
	 * @param s
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 */
	
	public static void handleQueryOrder(Channel channel,int h,int m,int s,String epCode,
			int epGunNo,int OrgNo,String usrLog,String token)
	{
		UsrGateClient  usrGate = getClient(channel);
	    if(usrGate == null)
	    {
	    	 logger.error("usrGate handleQueryOrder fail not find usrGate,channel:{}",channel);
			 return;
	    }
		setLastUseTime(channel);
		
		byte data[]=null;
		String extraData="";
		int errorCode = checkQueryOrderParam(epCode,epGunNo,OrgNo,usrLog,token);
		if(errorCode>0)//查询失败
		{
			data = UsrGateEncoder.orderInfo(h, m, s, epCode, epGunNo, OrgNo, usrLog,
					extraData,0,errorCode);
			
			logger.error("usrGate handleQueryOrder fail,epCode:{},epGunNo:{},OrgNo:{},usrLog:{},token:{},errorCode:{}",
					new Object[]{epCode,epGunNo,OrgNo,usrLog,token,errorCode});
		}
		else //查询成功
		{
	        extraData=EpChargeService.getExtraData_CCZC(epCode,epGunNo,usrLog,
      				token,3,1,0);
	        data = UsrGateEncoder.orderInfo(h, m, s, epCode, epGunNo, OrgNo, usrLog,
				extraData,1,0);
		}
		if(data !=null)
		{
			UsrGateMessageSender.sendMessage(channel, data);
			logger.debug("usrGate handleQueryOrder send success,epCode:{},extraData:{}", epCode, extraData);
		}
		
		
	}
   /**
    * 曹操专车查询订单参数检查
    * @param epCode
    * @param epGunNo
    * @param OrgNo
    * @param usrLog
    * @param token
    * @return
    */
    public static int checkQueryOrderParam(String epCode,int epGunNo,int OrgNo,String usrLog,String token) 
    {
    	ElectricPileCache epCache =  EpService.getEpByCode(epCode);
		if(epCache == null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache == null )
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if(epGunCache.getChargeCache()==null)
		{
			return 1;
		}
		if(OrgNo != UserConstants.ORG_CCZC && OrgNo != UserConstants.ORG_CHAT)
		{
			 return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		if(epGunCache.getChargeCache().getUserOrigin()==null
				|| epGunCache.getChargeCache().getUserOrigin().getOrgNo() !=OrgNo)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}

        UserCache userInfo=UserService.getUserIdByOrgNo(OrgNo);
		if(userInfo==null)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		if( usrLog==null||
			!usrLog.equals(epGunCache.getChargeCache().getThirdUsrIdentity())||
				epGunCache.getChargeCache().getUserId() != userInfo.getId()  )
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		return 0;
	}

	/**
	 * 4commondataquery 查询数据参数检查
	 *
	 * @param epCode
	 * @param epGunNo
	 * @param OrgNo
	 * @param usrLog
	 * @param token
	 * @return
	 */
	public static int checkQuery4CommonDataParam(String epCode, int epGunNo) {
		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if (epCache == null) {
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if (epGunCache == null) {
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		return 0;
	}
    /**
     * 检查车与枪连接状态，曹操专车
     * @param epCode
     * @param epGunNo
     * @param OrgNo
     * @param usrLog
     * @param token
     * @return
     */
	
    public static int checkCarLinkStatus(String epCode,int epGunNo) 
    {
    	EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
	    if(epGunCache ==null)
	    {
	    	return ErrorCodeConstants.EP_UNCONNECTED;
	    }
	    int carLinkStatus = epGunCache.getRealChargeInfo().getLinkCarStatus();
	    logger.debug("checkCarLinkStatus,carLinkStatus:{},epCode:{},epGun:{}",new Object[]{carLinkStatus,
	    		epCode,epGunNo});
	    if(carLinkStatus == 0)//车与枪连接：断开
	    {
	        return ErrorCodeConstants.EPE_GUN_NOT_LINKED;//
	    }
	   
	    logger.debug("checkCarLinkStatus 0,carLinkStatus:{},epCode:{},epGun:{}",new Object[]{carLinkStatus,
	    		epCode,epGunNo});
	    return 0;
    }

    //通用实时数据
    public static void handleQueryData4Common(Channel channel, int h, int m, int s, String epCode,
                                             int epGunNo, String extra){
	    UsrGateClient usrGate = getClient(channel);
	    if (usrGate == null) {
		    logger.error("usrGate handleQueryData4Common fail not find usrGate,channel:{}", channel);
		    return;
	    }
	    setLastUseTime(channel);

	    byte data[] = null;
	    String extraData = "";
	    int errorCode = checkQuery4CommonDataParam(epCode, epGunNo);
	    if (errorCode > 0)//查询失败
	    {
		    data = UsrGateEncoder.queryData4Common(h, m, s, epCode, epGunNo,
				    extra,extraData, 0, errorCode);

		    logger.error("usrGate handleQueryData4Common fail,epCode:{},epGunNo:{},extra:{},errorCode:{}",
				    new Object[]{epCode, epGunNo, extra, errorCode});
	    } else //查询成功
	    {
		    extraData = EpChargeService.queryData4Common(epCode, epGunNo);
		    logger.info("usrGate handleQueryData4Common success,epCode:{},epGunNo:{},extra:{}",
				    new Object[]{epCode, epGunNo, extra});
		    data = UsrGateEncoder.queryData4Common(h, m, s, epCode, epGunNo,  extra,
				    extraData, 1, 0);
	    }
	    if (data != null) {
		    UsrGateMessageSender.sendMessage(channel, data);
		    logger.debug("usrGate handleQueryData4Common send success");
	    }

    }
    
    	
 }
