package com.ec.epcore.service;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ec.common.net.A2ECmdConstantsAPI;
import com.ec.epcore.net.server.InnerApiMessageSender;
import com.ec.utils.NetUtils;

/**
 * 服务服相关服务
 * @author 
 * 2014-12-1 上午9:13:55
 */
public class AppClientService {
	
	//后台API长链接IP，ch映射,一个IP只能有一个
	public static ConcurrentHashMap<String,Channel>  mapAppClients=new ConcurrentHashMap<String,Channel>();
	
	public static String getCacheSize()
	{
		return "AppClientService:\nmapAppClients.size():"+ mapAppClients.size()+"\n\n";
	}
	
	
	public static void addAppClient(Channel ch){
		String appClientIp= NetUtils.getClientIp(ch);
		
		if(ch!=null&&appClientIp!=null && appClientIp.length()>7)
		{
			//如果已经有通道，那么关闭
			Channel oldCh = mapAppClients.get(appClientIp);
			if(oldCh !=null && oldCh != ch)
			{
				oldCh.close();
			}
			//
			mapAppClients.put(appClientIp, ch);
		}
	}
	
	
	public static Channel getAppChannel(String appClientIp){
		Channel channel= null;
		if(appClientIp==null)
		{
			return channel;
		}
		channel = mapAppClients.get(appClientIp);
		return channel;
	}
	
	
	
	
	@SuppressWarnings("rawtypes")
	public static void broadcastMsg(short protocolNum, int senderId, byte[] bb)
	{
		Iterator iter = mapAppClients.entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			Channel ch=(Channel) entry.getValue();
			if(null == ch)
			{
				continue;
			}
			InnerApiMessageSender.gateSendToGame(ch, protocolNum, senderId,  bb);
		}
	}
	
	public static void notifyEpGate(byte[] msg)
	{
		Iterator iter = mapAppClients.entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Map.Entry) iter.next();
			Channel ch=(Channel) entry.getValue();
			if(null == ch)
			{
				continue;
			}
			InnerApiMessageSender.gateSendToGame(ch, A2ECmdConstantsAPI.G2A_EP_CHANGE_GATE, (Integer)0,  msg);
		}
	}
		
	public static void login(Channel ch,ByteBuffer buf){
		
	}
	
	
	
}
