package com.ec.epcore.net.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.service.EpCommClientService;
import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.server.AbstractNettyServer;
import com.ec.netcore.util.IPUtil;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;

public class EpNettyServer extends AbstractNettyServer{

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpNettyServer.class.getName()));
	
	public EpNettyServer(ServerConfig serverConfig,ByteToMessageDecoder decoder, MessageToByteEncoder encoder,int bossTCount,int workTCount) {
		super(serverConfig, decoder, encoder,bossTCount,workTCount);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx) {
		
		//电桩断线
		Channel channel = ctx.channel();
		
		logger.info(LogUtil.addExtLog("commClient recevice close,channel"),channel.toString());
		EpCommClientService.offLine(channel);
		
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
		
		EpCommClient commClient = new EpCommClient();
		
		commClient.setChannel(ctx.channel());
		commClient.setLastUseTime(DateUtil.getCurrentSeconds());
		commClient.setStatus(1);
		commClient.setIdentity("");
		EpCommClientService.addConnect(commClient);

		logger.debug(LogUtil.addExtLog("commClient connected,channel"),ctx.channel().toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		
		Channel channel = ctx.channel();
		
		logger.debug(LogUtil.addExtLog("server exception"),channel.toString());
		//TODO
		EpCommClientService.offLine(channel);
		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		
		
		EpMessage message = (EpMessage)obj;
		
		if(isStop){
			logger.error("服务器已经停止，不再处理消息！忽略来自【{}】的消息:【{}】", new Object[]{ name, message });
			return;
		}
		
		EpMessageHandler.handleMessage(channel, message);
		
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info("EpNettyServer server stop...");
		
	}

}
