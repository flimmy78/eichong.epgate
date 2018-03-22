package com.ec.epcore.net.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.server.AbstractNettyServer;
import com.ec.netcore.util.IPUtil;
import com.ec.utils.LogUtil;

/**
 * 内部Netty服务器
 * @author 
 * 2014-11-28 下午1:36:03
 */
public class InnerApiNettyServer extends AbstractNettyServer{
	
	private static final Logger logger = LoggerFactory.getLogger(InnerApiNettyServer.class);

	public InnerApiNettyServer(ServerConfig serverConfig,ByteToMessageDecoder decoder, MessageToByteEncoder encoder,int bossTCount,int workTCount) {
		super(serverConfig, decoder, encoder,bossTCount,workTCount);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx) {
		logger.debug("server close:{}",ctx.channel());
		//EpCache.removeGameFromGameWorld();
		
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
		logger.debug("server conn:{}",ctx.channel());
		//服务服连接成功
		Channel channel = ctx.channel();
		//EpCache.addGameToGameWorld(channel);
      //	AppClientService.addAppClient(channel);
      
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		logger.info(LogUtil.addExtLog("channel|server exception"),ctx.channel(),cause);
		ctx.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		//logger.info("server receive...");
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		InnerApiMessage message = (InnerApiMessage) obj;
		if(isStop){
			logger.error("服务器已经停止，不再处理消息！忽略来自【{}】的消息:【{}】", new Object[]{ name, message });
			return;
		}
		
		InnerApiMessageHandler.handleMessage(channel, message);
		
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info("InnerApiNettyServer server stop...");
		
	}

}
