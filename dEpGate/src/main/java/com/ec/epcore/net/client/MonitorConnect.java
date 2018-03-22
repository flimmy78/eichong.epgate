package com.ec.epcore.net.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.net.codec.MonitorDecoder;
import com.ec.epcore.net.codec.MonitorEncoder;
import com.ec.epcore.net.server.MonitorMessage;
import com.ec.epcore.net.server.MonitorMessageHandler;
import com.ec.netcore.constants.CommStatusConstant;
import com.ec.netcore.model.conf.ClientConfig;
import com.ec.netcore.netty.client.AbstractNettyClient;
import com.ec.netcore.util.IPUtil;
import com.ec.utils.DateUtil;


public class MonitorConnect extends AbstractNettyClient{
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorConnect.class);
	

	
	public MonitorConnect(ClientConfig cfg,ByteToMessageDecoder decoder, MessageToByteEncoder<?> encoder) {
		super(cfg, decoder, encoder);
		
		identity = cfg.getIp()+cfg.getPort();
		
	}
	public void close()
	{
		if(channel!=null)
		{
			channel.close();
		}
		status=CommStatusConstant.NET_INIT;
		logger.info("close...");
	}


	@Override
	public Channel getChannel() {
		// TODO Auto-generated method stub
		return super.getChannel();
	}

	@Override
	public ByteToMessageDecoder getDecoder() {
		// TODO Auto-generated method stub
		return super.getDecoder();
	}

	@Override
	public MessageToByteEncoder getEncoder() {
		// TODO Auto-generated method stub
		return super.getEncoder();
	}
	@Override
	public String getIdentity() {
		// TODO Auto-generated method stub
		return super.getIdentity();
	}
	
	@Override
	public long getLastUseTime() {
		// TODO Auto-generated method stub
		return super.getLastUseTime();
	}
	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return super.getStatus();
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return super.getType();
	}

	@Override
	public boolean isComm() {
		// TODO Auto-generated method stub
		return super.isComm();
	}

	@Override
	public void setChannel(Channel channel) {
		// TODO Auto-generated method stub
		super.setChannel(channel);
	}

	@Override
	public void setIdentity(String identity) {
		// TODO Auto-generated method stub
		super.setIdentity(identity);
	}
	@Override
	public void setLastUseTime(long lastUseTime) {
		// TODO Auto-generated method stub
		super.setLastUseTime(lastUseTime);
	}

	@Override
	public void setStatus(int status) {
		// TODO Auto-generated method stub
		super.setStatus(status);
	}
	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		super.setType(type);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
	}
	private long lastSendTime=0;
	private long lastConnectTime=0;
	public static MonitorConnect getNewInstance(ClientConfig clrCfg)
	{
		ByteToMessageDecoder decoder = new MonitorDecoder();
		MessageToByteEncoder encoder = new MonitorEncoder();
		
		return new MonitorConnect(clrCfg,decoder,encoder);
	}
	
	public long getLastConnectTime() {
		return lastConnectTime;
	}
	public void setLastConnectTime(long lastConnectTime) {
		this.lastConnectTime = lastConnectTime;
	}
	
	public long getLastSendTime() {
		return lastSendTime;
	}


	public void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime;
	}


	@Override
	public int getConnectTimes() {
		// TODO Auto-generated method stub
		return super.getConnectTimes();
	}


	@Override
	public int getMaxConnectTims() {
		// TODO Auto-generated method stub
		return super.getMaxConnectTims();
	}


	@Override
	public void reconnection() {
		// TODO Auto-generated method stub
		
		super.reconnection();
	}


	@Override
	public void setConnectTimes(int connectTimes) {
		// TODO Auto-generated method stub
		super.setConnectTimes(connectTimes);
	}


	@Override
	public void setMaxConnectTims(int maxConnectTims) {
		// TODO Auto-generated method stub
		super.setMaxConnectTims(maxConnectTims);
	}



	@SuppressWarnings("rawtypes")
	@Override
	public void channelClosed(ChannelHandlerContext ctx) {
		logger.info("server close...");
		try {
			Channel channel = ctx.channel();
			setStatus(CommStatusConstant.NET_INIT);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
		logger.info("MonitorConnect server conn...");
	    //服务服连接成功
		Channel channel = ctx.channel();
		setConnectTimes(1);
		setStatus(CommStatusConstant.INIT_SUCCESS);
		setChannel(channel);

		setLastSendTime(DateUtil.getCurrentSeconds());
		setLastUseTime(DateUtil.getCurrentSeconds());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		logger.info("server exception...");
		setStatus(CommStatusConstant.NET_INIT);
		close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		//logger.info("server receive...");
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		
		MonitorMessage message = (MonitorMessage) obj;
		MonitorMessageHandler.handleMessage(channel, message);
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info("server stop...");
		
	}
	

	@Override
	public void regiest(Channel arg0) {
		// TODO Auto-generated method stub
		
	}
	
}