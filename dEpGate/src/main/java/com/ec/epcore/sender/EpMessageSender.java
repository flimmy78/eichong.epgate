package com.ec.epcore.sender;

import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.service.EpCommClientService;
import com.ec.netcore.client.ITcpClient;
import com.ec.netcore.queue.RepeatMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpMessageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(EpMessageSender.class);
	
	public static ChannelFuture  sendMessage(ITcpClient commClient,int cos,int common_address,int recordType,byte[] msg
			,byte []time,int commVersion) {
		
		EpCommClient  epCommClient= (EpCommClient)commClient;
		int sendINum = epCommClient.getSendINum2();
		int recvINum = epCommClient.getRevINum();
		
		
		byte[] sendData= EpEncoder.Package(sendINum,recvINum,
				cos,common_address,(byte)recordType,msg,time,commVersion);
		
		
		return sendMessage(commClient, sendData);
		
	}
	public static ChannelFuture  sendRepeatMessage(ITcpClient commClient,String repeatMsgKey,int cos,int common_address,int recordType,byte[] msg
			,byte []time,int commVersion) {
		
		EpCommClient  epCommClient= (EpCommClient)commClient;
		int sendINum = epCommClient.getSendINum2();
		int recvINum = epCommClient.getRevINum();
		
		
		byte[] sendData= EpEncoder.Package(sendINum,recvINum,
				cos,common_address,(byte)recordType,msg,time,commVersion);
      	//1.����������
		sendMessage(epCommClient,sendData);
		logger.debug("resendEpMsgFlag:{},version:{}",GameConfig.resendEpMsgFlag,epCommClient.getVersion());
		if(GameConfig.resendEpMsgFlag ==1 && epCommClient.getVersion()>=4)
		{			
	    	RepeatMessage chargeMsg= new RepeatMessage(epCommClient.getChannel(),3,GameConfig.resendEpMsgTime,repeatMsgKey,sendData);
		    EpCommClientService.putSendMsg(chargeMsg);
		}
		return null;
		
	}
	
	public static ChannelFuture  sendMessage(ITcpClient commClient,Object object) {
		
		Channel channel = commClient.getChannel();
		if(channel == null)
		{
			logger.info("sendMessage  channel  is null\n");
			return null;
		}
		
		if (!channel.isWritable()) {
			logger.info("sendMessage  is not Writable  channe:{}\n",channel);
			return null;
		}
		
		channel.writeAndFlush(object);
		
		return null;
	}


}
