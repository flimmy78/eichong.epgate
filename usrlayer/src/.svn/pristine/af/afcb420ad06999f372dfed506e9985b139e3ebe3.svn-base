package com.ec.usrcore.net.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.model.conf.ClientConfig;
import com.ec.netcore.netty.client.AbstractNettyClient;
import com.ec.netcore.util.IPUtil;
import com.ec.usrcore.net.codec.EpGateDecoder;
import com.ec.usrcore.net.codec.EpGateEncoder;
import com.ec.usrcore.server.CommonServer;
import com.ec.usrcore.service.CacheService;
import com.ec.usrcore.service.EpGateService;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;



public class EpGateNetConnect extends AbstractNettyClient{
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateNetConnect.class.getName()));
	
	//ClientConfig clientConfig = null;
	
	public EpGateNetConnect(ClientConfig cfg,ByteToMessageDecoder decoder, MessageToByteEncoder<?> encoder) {
		super(cfg, decoder, encoder);
		//clientConfig =  cfg;
		identity = cfg.getIp()+cfg.getPort();
		
	}
	public void close()
	{
		if(channel!=null)
		{
			channel.close();
		}
		status=0;
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
	public static EpGateNetConnect getNewInstance(ClientConfig clrCfg)
	{
		ByteToMessageDecoder decoder = new EpGateDecoder();
		MessageToByteEncoder encoder = new EpGateEncoder();
		
		return new EpGateNetConnect(clrCfg,decoder,encoder);
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
		//logger.info("server close...");
		try {
			Channel channel = ctx.channel();

			Iterator iter = CacheService.getMapEpGate().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				EpGateNetConnect epGateClient = (EpGateNetConnect) entry.getValue();
				if (null == epGateClient) continue;
				if (epGateClient.getChannel().equals(channel)) {
					CacheService.removeEpGate((int)entry.getKey());
				}
			}
			CacheService.removeEpGateByCh(channel);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx) {
		logger.info(LogUtil.getExtLog("server conn..."));
	    //服务服连接成功
		Channel channel = ctx.channel();
		//commClient.clearConnecTtimes();
		setStatus(1);
		setChannel(channel);

		setLastSendTime(DateUtil.getCurrentSeconds());
		EpGateService.sendEpGateLogin(channel, CommonServer.getInstance().getSeverType());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		logger.info(LogUtil.addExtLog("server exception..."));
		close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object obj) {
		//logger.info("server receive...");
		Channel channel = ctx.channel();
		String name = IPUtil.getNameByChannel(channel);
		
		EpGateMessage message = (EpGateMessage) obj;
		EpGateMessageHandler.handleMessage(channel, message);
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info(LogUtil.addExtLog("server stop..."));
		
	}






	@Override
	public void regiest(Channel arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
