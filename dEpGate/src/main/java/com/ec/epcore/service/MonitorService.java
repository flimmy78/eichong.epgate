package com.ec.epcore.service;


import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.EventConstant;
import com.ec.epcore.cache.UserOrigin;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.MonitorConnect;
import com.ec.epcore.net.codec.MonitorEncoder;
import com.ec.epcore.net.sender.MonitorMessageSender;
import com.ec.epcore.task.CheckMonitorCommTask;
import com.ec.net.proto.SingleInfo;
import com.ec.netcore.constants.CommStatusConstant;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.DateUtil;



public class MonitorService{

	private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);
	
	public static MonitorConnect  commClient;
	
	public static void startMonitorCommTimer(long initDelay) {
		
		CheckMonitorCommTask checkTask =  new CheckMonitorCommTask();
				
		TaskPoolFactory.scheduleAtFixedRate("CHECK_MONITOR_TIMEOUT_TASK", checkTask, initDelay, 10, TimeUnit.SECONDS);
		
	}

	
	

	public static void setCommClient(MonitorConnect commClient) {
		MonitorService.commClient = commClient;
	}
	public static MonitorConnect getCommClient() {
		return commClient;
	}
	
	public static void handleHeart(Channel channel)
	{
    	if(commClient != null)
    	{
    	    commClient.setLastUseTime(DateUtil.getCurrentSeconds());
    	    //commClient.setRevDataNum(revNum);
    	}
	}
	
	public static int onEvent(int type,UserOrigin userOrigin,int ret,int cause, Object srcParams,Object extraData)
	{
		logger.debug("onEvent,realData,type:{},UserOrigin:{}",type,userOrigin);
		if(commClient==null)
		{
			logger.error("onEvent,realData fail,commClient==null");
			return 0;
		}
		if(commClient.getChannel()==null)
		{
			logger.error("onEvent,realData fail,commClient.getChannel()==null");
			return 0;
		}
		if(commClient.getStatus()<CommStatusConstant.INIT_SUCCESS)
		{
			logger.error("onEvent,realData fail,commClient status:{}",commClient.getStatus());
			return 0;
		}
		try
		{
		if(type==EventConstant.EVENT_ONE_BIT_YX ||
				type==EventConstant.EVENT_TWO_BIT_YX || 
				type==EventConstant.EVENT_YC ||
				type==EventConstant.EVENT_VAR_YC)
		{
			
			if(srcParams==null)
			{
				logger.error("onEvent,realData,srcParams==null");
				return 0 ;
			}
			Map<String, Object> paramsMap = (Map<String, Object>) srcParams;
			
			String epCode  = (String)paramsMap.get("epcode");
			int epGunNo  = (int)paramsMap.get("epgunno");
			int  currentType = (int)paramsMap.get("currenttype");
			
			Map<Integer, SingleInfo> pointMap = (Map<Integer, SingleInfo>) extraData;
			
			logger.debug("onEvent yxyc type:{},pointMap.size():{}",type,pointMap.size());
			switch(type)
			{
			case EventConstant.EVENT_ONE_BIT_YX:
			{
				byte[] msg =MonitorEncoder.do_one_bit_yx(epCode, epGunNo, currentType, pointMap);
				MonitorMessageSender.sendMessage(commClient.getChannel(), msg);
			}
			break;
			case EventConstant.EVENT_TWO_BIT_YX:
			{
				byte[] msg =MonitorEncoder.do_two_bit_yx(epCode, epGunNo, currentType, pointMap);
				MonitorMessageSender.sendMessage(commClient.getChannel(), msg);
			}
			break;
			case EventConstant.EVENT_YC:
			{
				byte[] msg =MonitorEncoder.do_yc(epCode, epGunNo, currentType, pointMap);
				MonitorMessageSender.sendMessage(commClient.getChannel(), msg);
			}
			break;
			case EventConstant.EVENT_VAR_YC:
			{
				byte[] msg =MonitorEncoder.do_var_yc(epCode, epGunNo, currentType, pointMap);
				MonitorMessageSender.sendMessage(commClient.getChannel(), msg);
				
			}
			break;
			default:
				break;
			}
		}
		else
		{
			logger.error("onEvent,realData,ep invalid type:{}",type);
		}
		}
		catch (Exception e) {
			logger.error("onEvent exception,type:{},e.getMessage():{}",type,e.getMessage());
		}
		return 0;

	}
	
	@SuppressWarnings("rawtypes")
	public synchronized static void checkTimeOut() {
		

		if(commClient==null)
		{
			logger.error("AnalyzeChannelHandler, checkTimeOut commClient==null");
			return ;
		}
		
		
		
		//需要连接
		boolean bNeedReConnect=false;
		//检查连接，是否需要重连
		int  commStatus = commClient.getStatus();
		
		logger.debug("AnalyzeCommClient checkTimeOut commStatus:{}",
				new Object[]{commStatus});
		
		if(commStatus == 0 || commStatus == 1)
		{
			//检查
			long connectDiff = DateUtil.getCurrentSeconds() - commClient.getLastConnectTime();
			//连接6次后连接间隔时间依次增加
			int times = (commClient.getConnectTimes() /6) +1;
			long interval= times * GameConfig.analyzeReConnectInterval;
			if(connectDiff> interval)
			{
				logger.debug("AnalyzeCommClient checkTimeOut commStatus:{},connectDiff:{},interval:{}",
						new Object[]{commStatus,connectDiff,interval});
				bNeedReConnect  = true;
			}
		}
		else
		{
			long connectDiff = DateUtil.getCurrentSeconds() - commClient.getLastUseTime();
			
			long interval= GameConfig.analyzeKeepLiveInterval;
			if(connectDiff> interval)
			{
				logger.debug("AnalyzeCommClient checkTimeOut commStatus:{},connectDiff:{},interval:{}",
						new Object[]{commStatus,connectDiff,interval});
				bNeedReConnect  = true;
			}
			
		}
		
		if(bNeedReConnect)
		{
			logger.debug("AnalyzeCommClient reconnection");
			commClient.reconnection();
		}
		
		
		long now = DateUtil.getCurrentSeconds();
		//1.检查连接是否活动,不活动的话发送心跳侦
		long activeDiff = now - commClient.getLastSendTime();
		if (activeDiff >= GameConfig.analyzeHeartInterval) {
			byte[] msg = MonitorEncoder.do_heart();
			MonitorMessageSender.sendMessage(commClient.getChannel(), msg);
			logger.debug("MonitorMessageSender heart");
			commClient.setLastUseTime(now);
			commClient.setLastSendTime(now);
		}	
		
	}
	
	public static String stat()
	{	
		if(commClient == null)
			return "commClient = null";
		
		final StringBuilder sb = new StringBuilder();
		
        sb.append("{通讯状态status = ").append(commClient.getStatus());
        
        switch(commClient.getStatus())
        {
        case 2:
        	sb.append("连接").append("\n\n");
        	break;
        default:
        	sb.append("未连接").append("\n\n");
        	break;
		
        }
	    long connectTime = commClient.getLastConnectTime();
		sb.append("最后连接动作的时间:").append(DateUtil.StringYourDate(DateUtil.toDate(connectTime*1000))).append(" \n");
		
        long SendTime = commClient.getLastSendTime();
    	
    	sb.append("最后发送数据时间：").append(DateUtil.StringYourDate(DateUtil.toDate(SendTime*1000))).append(" \n\n");
    	
    	
    	long useTime = commClient.getLastUseTime();
    	
    	sb.append("最后接收数据时间：").append(DateUtil.StringYourDate(DateUtil.toDate(useTime*1000))).append(" \n\n");
    	
    	
		return sb.toString();
	}
}
