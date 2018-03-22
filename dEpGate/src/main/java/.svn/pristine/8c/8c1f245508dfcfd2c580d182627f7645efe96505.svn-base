package com.ec.epcore.net.sender;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.queue.RepeatConQueue;
import com.ec.netcore.queue.RepeatMessage;
import com.ec.utils.DateUtil;

public class UsrGateMessageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(UsrGateMessageSender.class);
	
	private static RepeatConQueue repeatMsgQue = new RepeatConQueue();
	
	public static ChannelFuture  sendMessage(Channel channel,Object object) {
		
		if(channel == null)
		{
			
			return null;
		}
		
		if (!channel.isWritable()) {
			
			return null;
		}
		
		channel.writeAndFlush(object);
		
		return null;
	}
	
	public static ChannelFuture  sendRepeatMessage(Channel channel,byte[] msg,String repeatMsgKey) {
		
			//1.先立即发送
		sendMessage(channel,msg);
		
		
		//TODO:时间配置，是否重发需要参数化
		{
			RepeatMessage repeatMsg= 
					new RepeatMessage(channel,3,30,repeatMsgKey,msg);
			
			repeatMsg.setLastSendTime( DateUtil.getCurrentSeconds());
			
			putSendMsg(repeatMsg);
		}
		
		return null;
	}
	public static void putRepeatMsg(RepeatMessage mes)
	{
		System.out.print("putRepeat,key:"+mes.getKey()+"\n");
		repeatMsgQue.put(mes);
	}
	public static void removeRepeatMsg(String key)
	{
		System.out.print("removeRepeatMsg,key:"+key+"\n");
		repeatMsgQue.remove(key);
	}
	
	public static void putSendMsg(RepeatMessage mes)
	{
		logger.debug("putSendMsg,key:{}",mes.getKey());
		logger.debug("putSendMsg repeatMsgQue,{}",repeatMsgQue.count());
		repeatMsgQue.putSend(mes);
		logger.debug("putSendMsg repeatMsgQue,{}",repeatMsgQue.count());
	}
	
	

}
