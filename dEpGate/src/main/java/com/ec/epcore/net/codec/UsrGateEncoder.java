package com.ec.epcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.common.net.U2ECmdConstants;
import com.ec.epcore.net.proto.ConsumeRecord;
import com.ec.epcore.net.proto.PhoneConstant;
import com.ec.epcore.service.ChargingInfo;
import com.ec.netcore.netty.buffer.DynamicByteBuffer;


public class UsrGateEncoder extends MessageToByteEncoder{


	private static final Logger logger = LoggerFactory.getLogger(UsrGateEncoder.class);
	
	
	/**
	 * 不管channel.write(arg0)发送的是什么类型，
	 * 最终都要组装成 ByteBuf 发送,
	 * 所以encode需要返回 ByteBuf 类型的对象
	 
	 * @param chc
	 * @param bb   (Message)
	 * @param byteBuf   (Byte)
	 * @return
	 * @throws Exception
	 */
	@Override
	protected void encode(ChannelHandlerContext chc, Object msg, ByteBuf byteBuf)
			throws Exception {
		
		if(msg instanceof ByteBuf){
			
			ByteBuf byteBufIn = (ByteBuf)msg;
			byte[] bb = new byte[byteBufIn.readableBytes()];
			byteBufIn.getBytes(0, bb);
			
			byteBuf.writeBytes(bb);
			
		}else if(msg instanceof byte[]){
			
			byte[] bb = (byte[])msg;
			byteBuf.writeBytes(bb);
			
		}else{
			
			logger.debug("usrGate 未知的消息类型:{}",chc.channel().toString());
			
		}
	}
	
	public static  byte[] Package(int cmd,byte[] msgBody) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate(msgBody.length+4);
		
		short len = (short)(msgBody.length+2);
		byteBuffer.put(PhoneConstant.HEAD_FLAG1);
		byteBuffer.put(PhoneConstant.HEAD_FLAG2);
		
		
		byteBuffer.putShort(len);
		byteBuffer.putShort((short)cmd);
		
		byteBuffer.put(msgBody);
		
		return byteBuffer.getBytes();
		
	}
	public static  byte[] login(int usrGateId,int epGateId,int h,int m, int s,int ret,int errorCode)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.putInt(usrGateId);
		byteBuffer.putInt(epGateId);
		byteBuffer.put((byte)ret);
		byteBuffer.putShort((short)errorCode);
		
		return Package(U2ECmdConstants.EP_LOGIN,byteBuffer.getBytes());
	}
	public static  byte[] ack(short cmd,int h,int m, int s,int OrgNo,String usrLog,String token)
	{//version 1.2
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putShort(cmd);
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		byteBuffer.putInt(OrgNo);
		byteBuffer.putString(usrLog);
		byteBuffer.putString(token);
		
		return Package(U2ECmdConstants.EP_ACK,byteBuffer.getBytes());
	}
	public static  byte[] do_connect_ep_resp(int h,int m, int s,String epCode,int epGunNo,long usrId,int ret,int errorCode,byte status)
	{
        DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
        byteBuffer.putString(epCode);
		
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putLong(usrId);
		byteBuffer.put((byte)ret);
		byteBuffer.putShort((short)errorCode);
		byteBuffer.put(status);
		
		return Package(U2ECmdConstants.PHONE_CONNECT_INIT,byteBuffer.getBytes());
	}
	
	public static  byte[] phoneOnline(int h,int m, int s) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		return Package(U2ECmdConstants.PHONE_ONLINE,byteBuffer.getBytes());
	}
	
	public static byte[] charge(int h,int m, int s, String epCode,int epGunNo,int OrgNo,String usrLog,String token,int ret,int errorCode)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putInt(OrgNo);
		byteBuffer.putString(usrLog);
		byteBuffer.putString(token);
		
		byteBuffer.put((byte)ret);
		byteBuffer.putShort((short)errorCode);
		
		return Package(U2ECmdConstants.EP_CHARGE,byteBuffer.getBytes());
	}
	
	
	public static byte[] stopCharge(int h,int m, int s, String epCode,int epGunNo,int OrgNo,String usrLog,String token,int ret,int errorCode)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putInt(OrgNo);
		byteBuffer.putString(usrLog);
		byteBuffer.putString(token);
		
		byteBuffer.put((byte)ret);
		byteBuffer.putShort((short)errorCode);
		
		return Package(U2ECmdConstants.EP_STOP_CHARGE,byteBuffer.getBytes());
	}
	
	
	
	public static byte[] chargeEvent(int h,int m, int s, String epCode,int epGunNo,int OrgNo,String usrLog,String token,int status)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putInt(OrgNo);
		byteBuffer.putString(usrLog);
		byteBuffer.putString(token);

		byteBuffer.put((byte)status);
		
		return Package(U2ECmdConstants.EP_CHARGE_EVENT,byteBuffer.getBytes());
	}
	
	
	
	public static byte[] chargeRecord(int h,int m, int s, String epCode,int epGunNo,int OrgNo,String usrLog,String token,
			String chargeOrder,ConsumeRecord consumeRecord,
			int userFirst,int couPonAmt,int realCouPonAmt)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		
		byteBuffer.putInt(OrgNo);
		byteBuffer.putString(usrLog);
		byteBuffer.putString(token);
		
		byteBuffer.putString(chargeOrder);
		byteBuffer.putLong(consumeRecord.getStartTime());
		byteBuffer.putLong(consumeRecord.getEndTime());
		byteBuffer.putInt(consumeRecord.getTotalDl());
		if (consumeRecord.getType() == 1) {
			byteBuffer.putInt(consumeRecord.getTotalChargeAmt()/100);
			byteBuffer.putInt(consumeRecord.getServiceAmt()/100);
		} else {
			byteBuffer.putInt(consumeRecord.getTotalChargeAmt());
			byteBuffer.putInt(consumeRecord.getServiceAmt());
		}
		byteBuffer.putInt(0);
		byteBuffer.put((byte)userFirst);
		byteBuffer.putInt(couPonAmt);
		byteBuffer.putInt(realCouPonAmt);
		
		byteBuffer.putInt(consumeRecord.getStartMeterNum());
		byteBuffer.putInt(consumeRecord.getEndMeterNum());
		
		byteBuffer.putInt(consumeRecord.getjDl());
		byteBuffer.putInt(consumeRecord.getjPrice());
		byteBuffer.putInt(0);
		byteBuffer.putInt(consumeRecord.getjAmt());
		byteBuffer.putInt(0);
		byteBuffer.putInt(0);
		
		byteBuffer.putInt(consumeRecord.getfDl());
		byteBuffer.putInt(consumeRecord.getfPrice());
		byteBuffer.putInt(0);
		byteBuffer.putInt(consumeRecord.getfAmt());
		byteBuffer.putInt(0);
		byteBuffer.putInt(0);
		
		byteBuffer.putInt(consumeRecord.getpDl());
		byteBuffer.putInt(consumeRecord.getpPrice());
		byteBuffer.putInt(0);
		byteBuffer.putInt(consumeRecord.getpAmt());
		byteBuffer.putInt(0);
		byteBuffer.putInt(0);
		
		byteBuffer.putInt(consumeRecord.getgDl());
		byteBuffer.putInt(consumeRecord.getgPrice());
		byteBuffer.putInt(0);
		byteBuffer.putInt(consumeRecord.getgAmt());
		byteBuffer.putInt(0);
		byteBuffer.putInt(0);
		
		return Package(U2ECmdConstants.EP_CONSUME_RECODE,byteBuffer.getBytes());
	}
	
	public static byte[] do_ep_net_status(int h,int m,int s,int online,ArrayList<String> epCodes)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put((byte)h);
		byteBuffer.put((byte)m);
		byteBuffer.put((byte)s);
		
		byteBuffer.put((byte)online);
		
		byteBuffer.putShort((short)epCodes.size());
		for(int i=0;i<epCodes.size();i++)
		{
			byteBuffer.putString(epCodes.get(i));
		}
		
		return Package(U2ECmdConstants.EP_ONLINE,byteBuffer.getBytes());
		
	}
	
	public static  byte[] heard()
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		return Package(U2ECmdConstants.EP_HEART,byteBuffer.getBytes());
	}

	 public static  byte[] do_gun2car_linkstatus(int h,int m,int s,int status,String epCode,int epGunNo,int  OrgNo,String usrLog,String token) 
	 {
		 DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

		try {
			
			byteBuffer.put((byte)h);
			byteBuffer.put((byte)m);
			byteBuffer.put((byte)s);
			
			byteBuffer.putString(epCode);
			byteBuffer.put((byte)epGunNo);
			byteBuffer.putInt(OrgNo);
			byteBuffer.putString(usrLog);
			byteBuffer.putString(token);
			
			byteBuffer.put((byte)status);
			
			return Package(U2ECmdConstants.EP_GUN_CAR_STATUS,byteBuffer.getBytes());
		
		} catch (Exception e) {
				logger.error("do_gun2car_linkstatus error!e.getMessage():{}",e.getMessage());
				return null;
		}
	  }		
	 
	   public static byte[] IchargeRecord(int h,int m, int s, String epCode,int epGunNo,int  OrgNo,String usrLog,String token,int pkEpId,
				String chargeOrder,long st,long et,int totalMeterNum,int totalAmt,int serviceAmt,
				int userFirst,int couPonAmt,int realCouPonAmt)
		{//version 1.2
			DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			byteBuffer.put((byte)h);
			byteBuffer.put((byte)m);
			byteBuffer.put((byte)s);
			
			byteBuffer.putString(epCode);
			byteBuffer.put((byte)epGunNo);
			byteBuffer.putInt(OrgNo);
			byteBuffer.putString(usrLog);
			byteBuffer.putString(token);
			
			byteBuffer.putString(chargeOrder);
			byteBuffer.putLong(st);
			byteBuffer.putLong(et);
			byteBuffer.putInt(totalMeterNum);
			byteBuffer.putInt(totalAmt);
			byteBuffer.putInt(serviceAmt);
			byteBuffer.putInt(pkEpId);
			
			byteBuffer.put((byte)userFirst);
			byteBuffer.putInt(couPonAmt);
			byteBuffer.putInt(realCouPonAmt);
			
			return Package(U2ECmdConstants.EP_CONSUME_RECODE,byteBuffer.getBytes());
		}
		public static byte[] chargeRealInfo(int h,int m,int s,String epCode,int epGunNo,int OrgNo,String usrLog,
			    String token,ChargingInfo info)
		{//version 1.2
			DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			byteBuffer.put((byte)h);
			byteBuffer.put((byte)m);
			byteBuffer.put((byte)s);
			
			byteBuffer.putString(epCode);
			byteBuffer.put((byte)epGunNo);
			byteBuffer.putInt(OrgNo);
			byteBuffer.putString(usrLog);
			byteBuffer.putString(token);
			
			byteBuffer.put((byte)info.getWorkStatus());
			byteBuffer.putShort((short)info.getTotalTime());
			byteBuffer.putShort((short)info.getOutVol());
			byteBuffer.putShort((short)info.getOutCurrent());
			byteBuffer.putInt(info.getChargeMeterNum());
			byteBuffer.putShort((short)info.getRateInfo());
			byteBuffer.putInt(info.getFronzeAmt());
			byteBuffer.putInt(info.getChargeAmt());
			byteBuffer.put((byte)info.getSoc());
			byteBuffer.putInt(info.getDeviceStatus());
			byteBuffer.putInt(info.getWarns());
			
			return Package(U2ECmdConstants.EP_REALINFO,byteBuffer.getBytes());
		}
		
		public static byte[] orderInfo(int h,int m,int s,String epCode,int epGunNo,int OrgNo,String usrLog,
			    String extraData,int successFlag,int errorCode)
		{
			DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
			
			byteBuffer.put((byte)h);
			byteBuffer.put((byte)m);
			byteBuffer.put((byte)s);
			
			byteBuffer.putString(epCode);
			byteBuffer.put((byte)epGunNo);
			byteBuffer.putInt(OrgNo);
			byteBuffer.putString(usrLog);
			byteBuffer.putString(extraData);
			
			byteBuffer.put((byte)successFlag);
			byteBuffer.putShort((short)errorCode);
			
			return Package(U2ECmdConstants.CCZC_QUERY_ORDER,byteBuffer.getBytes());
		}
		
		
		public static  byte[] do_gun_workstatus(int h,int m,int s,int status,String epCode,int epGunNo,int  OrgNo,String usrLog,String token) 
		 {
			 DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

			try {
				
				byteBuffer.put((byte)h);
				byteBuffer.put((byte)m);
				byteBuffer.put((byte)s);
				
				byteBuffer.putString(epCode);
				byteBuffer.put((byte)epGunNo);
				byteBuffer.putInt(OrgNo);
				byteBuffer.putString(usrLog);
				byteBuffer.putString(token);
				
				byteBuffer.put((byte)status);
				
				return Package(U2ECmdConstants.EP_GUN_WORK_STATUS,byteBuffer.getBytes());
			
			} catch (Exception e) {
					logger.error("do_gun2car_linkstatus error!e.getMessage():{}",e.getMessage());
					return null;
			}
		  }		
}
