package com.ec.epcore.net.server;

import com.ec.epcore.service.UsrGateService;
import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.server.AbstractNettyServer;
import com.ec.netcore.util.IPUtil;
import com.ec.utils.DateUtil;
import com.ec.utils.NetUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UsrGateServer extends AbstractNettyServer{

	private static final Logger logger = LoggerFactory.getLogger(UsrGateServer.class);
	
	public UsrGateServer(ServerConfig serverConfig,ByteToMessageDecoder decoder, MessageToByteEncoder encoder,int btCount,int wtCount) {
		super(serverConfig, decoder, encoder,btCount,wtCount);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx) {
		
		//手机断线
		Channel channel = ctx.channel();
	
		UsrGateService.offLine(channel);
		
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
	
		UsrGateClient	client = new UsrGateClient();
		
		client.setChannel(ctx.channel());
		client.setLastUseTime(DateUtil.getCurrentSeconds());
		client.setStatus(1);
		String ip = NetUtils.getClientIp(ctx.channel());
		String ip2 = client.getIp();
		UsrGateService.addConnect(client);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug("server exception...cause:{},memssage:{}",cause.getCause(),cause.getMessage());
		
		Channel channel = ctx.channel();
		///PhoneService.offLine(channel);
		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		//logger.info("server receive...");
		
		UsrGateMessage message = (UsrGateMessage)obj;
		
		/*if(isStop){
			logger.error("服务器已经停止，不再处理消息！忽略来自【{}】的消息:【{}】", new Object[]{ name, message });
			return;
		}*/
		try{
			UsrGateMessageHandler.handleMessage(channel, message);
		}
		catch(IOException e)
		{
			
		}
		
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info("PhoneNettyServer server stop...");
		
	}

}
