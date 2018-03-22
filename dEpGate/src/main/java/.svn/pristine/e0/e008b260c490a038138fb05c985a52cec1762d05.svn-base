package com.ec.epcore.net.sender;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
/**
 * 发送消息必须通过此类
 */
public class MonitorMessageSender {
	
	
	public static ChannelFuture  sendMessage(Channel channel,Object object) {
		if(channel == null)
			return null;
		 
		if (channel == null || !channel.isWritable()) {
			return null;
		}

		ChannelFuture future = channel.writeAndFlush(object);
		return future;
	}
	
	
	

	
}
