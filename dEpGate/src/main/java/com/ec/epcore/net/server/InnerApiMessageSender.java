package com.ec.epcore.net.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.netty.buffer.DynamicByteBuffer;

/**
 * 发送消息必须通过此类
 * @author 
 * 2014-11-28 上午11:32:54
 */
public class InnerApiMessageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(InnerApiMessageSender.class);
	

	/**
	 * Gate服转发消息给Server服务器进行逻辑处理
	 * @author 
	 * 2015-3-19
	 * @param channel
	 * @param protocolNum
	 * @param senderId
	 * @param bb
	 * @return
	 */
	public static ChannelFuture gateSendToGame(Channel channel, short protocolNum, int senderId, byte[] bb){
		//获得消息长度
		int len = 2 + 4 + 1 + bb.length;
		
		DynamicByteBuffer buffer = DynamicByteBuffer.allocate(4 + len);
		
		buffer.putInt(len);//数据长度
		buffer.putShort(protocolNum); //协议号
		
		buffer.putInt(senderId); //发送人id
		
		buffer.put((byte)0); //是否压缩
		buffer.put(bb); //数据内容
		
		logger.info("gate-->Server,buffer:"+buffer);
		
		ChannelFuture future = InnerApiMessageSender.sendMessage(channel, buffer.getBytes());
	
		return future;
	}
	
	/**
	 * 发送消息
	 * @author 
	 * 2014-11-28
	 * @param channel
	 * @param object
	 * @return
	 */
	public static ChannelFuture  sendMessage(final Channel channel,Object object) {
		if(channel == null)
		{
			logger.info("sendMessage  channel  is null:"+channel);
			return null;
		}
		 
		if (!channel.isWritable()) {
			logger.info("sendMessage  is not Writable  channe"
					+ "l:"+channel);
			return null;
		}
		ChannelFuture f = channel.writeAndFlush(object);
		
		f.addListener(
				new ChannelFutureListener(){
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						future.get();
						if(future.isSuccess()){
							logger.debug("sendMessage success:future",channel, future);
						}else{
							logger.info("sendMessage fail,future", future);
							
						}
						
					}
				}
			);
		
		
		
		return f;
	}
	
}
