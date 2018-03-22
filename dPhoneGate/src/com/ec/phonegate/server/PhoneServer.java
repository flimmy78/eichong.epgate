package com.ec.phonegate.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.server.AbstractNettyServer;
import com.ec.netcore.util.IPUtil;
import com.ec.phonegate.client.PhoneClient;
import com.ec.phonegate.service.CachePhoneService;
import com.ec.phonegate.service.PhoneService;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;

public class PhoneServer extends AbstractNettyServer{

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(PhoneServer.class.getName()));
	
	public PhoneServer(ServerConfig serverConfig,ByteToMessageDecoder decoder, MessageToByteEncoder encoder,int btCount,int wtCount) {
		super(serverConfig, decoder, encoder,btCount,wtCount);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx) {
		
		//手机断线
		Channel channel = ctx.channel();
	
		PhoneService.offLine(channel);
		
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
	
		PhoneClient	phoneClient = new PhoneClient();
		
		phoneClient.setChannel(ctx.channel());
	    phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
	    
	    CachePhoneService.addPhoneClient(phoneClient);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug(LogUtil.addExtLog("cause|memssage"),cause.getCause(),cause.getMessage());
		
		Channel channel = ctx.channel();
		PhoneService.offLine(channel);
		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		//logger.info("server receive...");
		
		PhoneMessage message = (PhoneMessage)obj;
		
		/*if(isStop){
			logger.error("服务器已经停止，不再处理消息！忽略来自【{}】的消息:【{}】", new Object[]{ name, message });
			return;
		}*/
		try{
			PhoneMessageHandler.handleMessage(channel, message);
		}
		catch(IOException e)
		{
			logger.error(LogUtil.addFuncExtLog("Channel|exception"), channel, e.getStackTrace());
		}
		
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info(LogUtil.getExtLog("PhoneNettyServer server stop..."));
		
	}

}
