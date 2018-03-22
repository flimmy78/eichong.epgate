package com.ec.epcore.service;

import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.task.CheckEpCommClientTask;
import com.ec.netcore.client.ChannelManage;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.netcore.queue.RepeatConQueue;
import com.ec.netcore.queue.RepeatMessage;


public class EpCommClientService {
	
	private static final Logger logger = LoggerFactory.getLogger(EpCommClientService.class);
	
	@SuppressWarnings("unused")
	private static RepeatConQueue repeatMsgQue = new RepeatConQueue();
	
	private static ChannelManage cm = new ChannelManage();

	public static void putRepeatMsg(RepeatMessage mes)
	{
		
		logger.debug("putRepeat,key:{}",mes.getKey());
		repeatMsgQue.put(mes);
	}
	public static void putSendMsg(RepeatMessage mes)
	{
		logger.debug("putSendMsg,key:{}",mes.getKey());
		logger.debug("putSendMsg repeatMsgQue,{}",repeatMsgQue.count());
		repeatMsgQue.putSend(mes);
		logger.debug("putSendMsg repeatMsgQue,{}",repeatMsgQue.count());
	}
	public static void removeRepeatMsg(String key)
	{
		logger.debug("removeRepeatMsg,key:{}",key);
		repeatMsgQue.remove(key);
	}
	
	public static void checkRepeatMsg()
	{
		if(GameConfig.resendEpMsgFlag==1)
		{
			logger.debug("checkRepeatMsg start repeatMsgQue,{}",repeatMsgQue.count());
			repeatMsgQue.checkSend(0);
			logger.debug("checkRepeatMsg end repeatMsgQue,{}",repeatMsgQue.count());
		}
		else
		{
			logger.debug("checkRepeatMsg GameConfig.resendEpMsgFlag:{}",GameConfig.resendEpMsgFlag);
			
		}
	}
	public static boolean enableComm(String identity)
	{
		EpCommClient commClient = (EpCommClient) cm.get(identity);
		if(commClient == null)
			return false;
		return commClient.isComm();
	}
	public static boolean enableComm(int stationId,String epCode)
	{
		String identity="";
		if(stationId !=0)
		{
			identity = ""+ stationId;
		}
		else
		{
			identity = epCode;
		}
		return enableComm(identity);
	}
	
	public static void handleOldClient(EpCommClient epCommClient,String identity)
	{
		cm.handleOldClient(epCommClient, identity);
		
	}
	
	public static EpCommClient getCommClient(Channel ch)
	{
		return (EpCommClient) cm.get(ch);
	}
	/**
	 * 该函数只能用来在判断连接是否重复上
	 * @param key
	 * @return
	 */
		
	public static EpCommClient getCommClient(String key)
	{
		return (EpCommClient) cm.get(key);
	}
	
	public static void removeEpCommClient(EpCommClient commClient)
	{
		cm.remove(commClient);
		commClient=null;
	}
	
	
	 public static void removeEpCommClient(Channel ch)
	 {
		 int ret = cm.remove(ch);
		 if(ret <1)
		 {
			 logger.error("removeEpCommClient fail! ch:{},ret:{}",ch,ret);
		 }
		
		
	}
	 public static void removeEpCommClient(String  key)
	 {
		 int ret =cm.remove(key);
		 if(ret <1)
		 {
			 logger.error("removeEpCommClient fail! key:{},ret:{}",key,ret);
		 }
		
	}
	 
	public static String getCacheSize()
	{
		return cm.getCacheSize();
	}
	 
	 
	public static void addConnect(EpCommClient commClient)
	{
		int ret= cm.addConnect(commClient);
		if(ret>0)
		{
			StatService.regProtocolVersion(2, commClient.getVersion(), commClient.getIdentity());
		}
			
	}
	 
	public static void addClient(EpCommClient commClient)
	{
		int ret= cm.addClient(commClient);
		if(ret>0)
		{
			StatService.regProtocolVersion(2, commClient.getVersion(), commClient.getIdentity());
		}
			
	}
		
	   
    
		
	/**
	 * 电桩下线
	 * @author 
	 * 2014-12-1
	 * @param channel
	 */
	public static void offLine(Channel channel)
	{
		logger.debug("offLine,channel",channel);
		//获得电桩信息
		EpCommClient commClient =  getCommClient(channel);
		
		if (commClient != null) {
			
			commClient.handleNetTimeOut();
			
			
			EpCommClientService.removeEpCommClient(channel);
			
			
		}
			
	}
	
	public static void startCommClientTimeout(long initDelay) {
		
		CheckEpCommClientTask checkTask =  new CheckEpCommClientTask();
				
		TaskPoolFactory.scheduleAtFixedRate("CHECK_COMMCLIENT_TIMEOUT_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
	}
	
	
	public static void checkTimeOut()
	{
		String msg = cm.checkTimeOut(GameConfig.epNoInitConnectTimeout, GameConfig.epConnectTimeout);
	
		logger.info("checkTimeOut {}",msg);
	}
}
