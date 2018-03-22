package com.ec.phonegate.service;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.queue.RepeatConQueue;
import com.ec.netcore.queue.RepeatMessage;
import com.ec.phonegate.client.PhoneClient;
import com.ec.utils.LogUtil;

public class CachePhoneService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(CachePhoneService.class.getName()));
	
	private static RepeatConQueue phoneReSendMsgQue = new RepeatConQueue();
	
	public static void putPhoneRepeatMsg(RepeatMessage mes)
	{
		logger.debug(LogUtil.addExtLog("key|phoneReSendMsgQue"),mes.getKey(),phoneReSendMsgQue.count());
		phoneReSendMsgQue.put(mes);
	}
	public static void putPhoneSendMsg(RepeatMessage mes)
	{
		logger.debug(LogUtil.addExtLog("key|phoneReSendMsgQue"),mes.getKey(),phoneReSendMsgQue.count());
		phoneReSendMsgQue.putSend(mes);
	}
	public static void removePhoneRepeatMsg(String key)
	{
		logger.debug(LogUtil.addExtLog("key|phoneReSendMsgQue"),key,phoneReSendMsgQue.count());
		phoneReSendMsgQue.remove(key);
	}

	private static Map<Channel,PhoneClient> mapCh2PhoneClient = new ConcurrentHashMap<Channel, PhoneClient>();
	public static Map<Channel,PhoneClient> getMapCh2PhoneClient()
	{
		return mapCh2PhoneClient;
	}
	private static Map<Integer,PhoneClient> mapPhoneClients=new ConcurrentHashMap<Integer,PhoneClient>();
	public static Map<Integer,PhoneClient> getMapPhoneClient()
	{
		return mapPhoneClients;
	}
	
	public static int getCh2PhoneClientCount()
	{
		return mapCh2PhoneClient.size();
	}
	
	public static int getPhoneClientCount()
	{
		return mapPhoneClients.size();
	}
	
	public static void addPhoneClient(PhoneClient commClient)
	{
		 if(commClient==null || commClient.getChannel()==null)
			return;
		 mapCh2PhoneClient.put(commClient.getChannel(), commClient);
	}
	public static void addPhoneClientByAccount(PhoneClient commClient)
	{
		PhoneClient commClient1 = mapPhoneClients.get(commClient.getAccountId());
		if(commClient1!=null)
		{
			mapCh2PhoneClient.remove(commClient1.getChannel());
		}
		
		PhoneClient commClient2 = mapCh2PhoneClient.get(commClient.getChannel());
		 if(commClient2==null)
			return;
		 mapPhoneClients.put(commClient.getAccountId(), commClient);
	}
	public static PhoneClient getPhoneClientByChannel(Channel ch)
	{
		 return mapCh2PhoneClient.get(ch);
	}
	public static PhoneClient getPhoneClientByAccountId(int accountId)
	{
		 return mapPhoneClients.get(accountId);
	}
	public static PhoneClient removePhoneClientByAccountId(int accountId)
	{
		 return mapPhoneClients.remove(accountId);
	}
	public static void removePhoneClientByChannel(Channel ch)
	{
		mapCh2PhoneClient.remove(ch);
	}
}
