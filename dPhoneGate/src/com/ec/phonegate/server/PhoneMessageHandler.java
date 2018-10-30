package com.ec.phonegate.server;

import com.ec.net.proto.WmIce104Util;
import com.ec.phonegate.client.PhoneClient;
import com.ec.phonegate.proto.PhoneConstant;
import com.ec.phonegate.proto.PhoneProtocol;
import com.ec.phonegate.sender.PhoneMessageSender;
import com.ec.phonegate.service.CachePhoneService;
import com.ec.phonegate.service.PhoneService;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * 接受客户端数据并处理
 * @author lwz
 * 2015.10.11
 */
public class PhoneMessageHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(PhoneMessageHandler.class.getName()));
	
	
	public static void handleMessage(Channel channel, PhoneMessage message) throws  IOException
	{
		PhoneClient phoneClient = CachePhoneService.getPhoneClientByChannel(channel);
		if(phoneClient==null)
		{
			logger.error(LogUtil.addExtLog("error! not find PhoneClient"),channel);
			return ;
		}
   	 
		int cmd = message.getCmd();
		if(!isValidCmd(cmd))
		{
			logger.error(LogUtil.addExtLog("error! invalid cmd|phoneClient"),cmd,phoneClient);
			return ;
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
		int msgLen = message.getBytes().length;
		
		switch (cmd) 
		{
			case PhoneConstant.D_CONNET_EP:// 1 连接充电桩
			case PhoneConstant.D_NEW_CONNET_EP:
			case PhoneConstant.D_VERSION_CONNET_EP:
			{
				
				byte[] bcode = new byte[16];
				byteBuffer.get(bcode);
				String epCode = new String(bcode);
				
				// 2 充电枪编号
				int epGunNo = (int)byteBuffer.get();
				
				// 3 用户ID号
				bcode = new byte[8];
				byteBuffer.get(bcode);
				long accountid = NetUtils.getLong(bcode);
				
				byte[] bCheckCode = new byte[32];
				byteBuffer.get(bCheckCode);
				
				String checkCode = new String(bCheckCode);
				
				int version = 1;
				if(cmd ==PhoneConstant.D_VERSION_CONNET_EP)
				{
					version = (int)byteBuffer.get();
				}
				
				phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				PhoneService.handleConnectEp(channel,(int)accountid,epCode,epGunNo,checkCode,version,cmd);
			}
			break;
		
             case PhoneConstant.D_START_CHARGE://10 充电
			 {
				if (msgLen != 5 && msgLen != 6) {
					 byte[] data = PhoneProtocol.do_confirm(PhoneConstant.D_START_CHARGE, (byte)0, (short)0);
			         PhoneMessageSender.sendMessage(channel, data);
					 break;
				}
				phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				//1 冻结金额
				byte[] bcode = new byte[4];
				byteBuffer.get(bcode);
				int fronzeAmt = WmIce104Util.bytes2int(bcode);
				//2 充电方式
				short chargeType = byteBuffer.get();
				short chargeStyle = -1;
				//chargeStyle =
				if (msgLen == 6) {
					chargeStyle = byteBuffer.get();
				}
				
				PhoneService.handleStartCharge(phoneClient,fronzeAmt,chargeType,chargeStyle);
			 }
			 break;
			 
             case PhoneConstant.D_HEART://2 心跳
             {
            	 phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
            	 PhoneService.handleHeart(phoneClient);
     			
             }
             break;
             case PhoneConstant.D_STOP_CHARGE://11停止
			 {
		        logger.info(LogUtil.addFuncExtLog("channel|message"),
		                channel, WmIce104Util.ConvertHex(message.getBytes(), 1));
				 if (msgLen != 0) {
					 byte[] data = PhoneProtocol.do_confirm(PhoneConstant.D_STOP_CHARGE, (byte)0, (short)0);
			         PhoneMessageSender.sendMessage(channel, data);
					 break;
				}
				 phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				
				PhoneService.handleStopCharge(phoneClient);
			 }
			 break;
             case PhoneConstant.D_CONSUME_RECORD://消费记录确认
			 {
				 if (msgLen != 22 && msgLen != 23) {
					 logger.error(LogUtil.addExtLog("type|msgLen"),PhoneConstant.D_CONSUME_RECORD,msgLen);
					 break;
				 }
				 phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				
				 byte[] order = new byte[16];
				 byteBuffer.get(order);
				 String chargeOrder = new String(order);
				 //2 充电方式
				 short result = byteBuffer.getShort();
				
				 PhoneService.handleConsumeRecordConfirm(phoneClient,chargeOrder,result);
			 }
			 break;
             case PhoneConstant.D_GUN_CAR_STATUS://枪与车连接状态确认
			 {
				
				 phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				
				 PhoneService.handleGun2CarLinkStatusResp(phoneClient);
			 }
			 break;
             case PhoneConstant.D_GUN_WORK_STATUS://枪工作状态确认
			 {
				
				 phoneClient.setLastUseTime(DateUtil.getCurrentSeconds());
				
				 PhoneService.handleGun2CarWorkStatusResp(phoneClient);
			 }
			 break;
             
			 default:
			 break;
		  }
	}
	
	private static boolean isValidCmd(int cmd)
	{
		if( 
		cmd == PhoneConstant.D_CONNET_EP||
		cmd == PhoneConstant.D_NEW_CONNET_EP||
		cmd == PhoneConstant.D_VERSION_CONNET_EP||
		cmd == PhoneConstant.D_START_CHARGE||
		cmd == PhoneConstant.D_HEART||
		cmd == PhoneConstant.D_STOP_CHARGE||
		cmd == PhoneConstant.D_CONSUME_RECORD||
		cmd == PhoneConstant.D_GUN_CAR_STATUS||
		cmd == PhoneConstant.D_GUN_WORK_STATUS)
			return true;
		return false;
	}
}
   

