package com.ec.epcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.ec.netcore.netty.buffer.DynamicByteBuffer;

public class ApiEncoder extends MessageToByteEncoder{


	private static final Logger logger = LoggerFactory.getLogger(ApiEncoder.class);
	
	
	/**
	 * 不管channel.write(arg0)发送的是什么类型，
	 * 最终都要组装成 ByteBuf 发送,
	 * 所以encode需要返回 ByteBuf 类型的对象
	 * @author haojian
	 * Mar 27, 2013 5:18:00 PM
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
			
			logger.debug("未知的消息类型... channel:{}",chc.toString());
			
		}
		
		
	}
	
	/**
	 * 预约充电消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] bespokeProtocol(String bespNo,short bespSuccess,int bespErrorCode,
			int userId,Short redo){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		//byteBuffer.putLong(pkBespId);//App业务处理Id,预约主键
		byteBuffer.putString(bespNo);
		byteBuffer.putShort(bespSuccess);//预约成功标识（0：失败，1：成功）
		byteBuffer.putInt(bespErrorCode);//错误原因编码
		byteBuffer.putInt(userId);
		byteBuffer.putShort(redo);//续约标识（0：预约，1：续约）
		
		
		return byteBuffer.getBytes();
	}
	
	/**
	 * 取消预约消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] cancelBespokeProtocol(String bespokeNo, short successFlag,int bespErrorCode){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		
		byteBuffer.putString(bespokeNo);//预约编号
		byteBuffer.putShort(successFlag);
		byteBuffer.putInt(bespErrorCode);//错误码
		
		return byteBuffer.getBytes();
	}
	//取消预约事件
	public static byte[] bespokeRespEvent(double Amt,double userRemainAmt,String Account)
	{//分离这个协议和取消预约,是因为可以在桩上取消预约
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		byteBuffer.putDouble(Amt);
		byteBuffer.putDouble(userRemainAmt);
		byteBuffer.putString(Account);
		
		return byteBuffer.getBytes();
	}
	
	/**
	 * 开始充电消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] startElectricizeProtocol(String epCode,int cdq_no,short successFlag,int errorCode){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putShort(successFlag);//处理状态（0：失败，1：成功）
		byteBuffer.putInt(errorCode);//错误编码
		byteBuffer.putString(epCode);//电桩编号		
		byteBuffer.putInt(cdq_no);//
	
		return byteBuffer.getBytes();
	}
	
	
	/**
	 * 开始充电消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] startElectricizeEventProtocol(int userId,String Account){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();	

		byteBuffer.putString(Account);//电桩编号
		byteBuffer.putInt(userId);//电桩编号
		
		return byteBuffer.getBytes();
	}
	
	/**
	 * 停止充电消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] stopElectricizeProtocol(String epCode,int epGunNo,short successFlag,int errorCode){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putShort(successFlag);//成功标识
		byteBuffer.putInt(errorCode);//错误码
		byteBuffer.putString(epCode);//电桩编号
		byteBuffer.putInt(epGunNo);//充电枪编号
	
		return byteBuffer.getBytes();
	}
	

	
	
   	public static byte[] epIpChange(String epCode,int gateId){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();	

		byteBuffer.putString(epCode);//电桩编号
		byteBuffer.putInt(gateId);//
		
		return byteBuffer.getBytes();
	}
	/**
	 * 停止充电消息组装
	 * @author 
	 * 2015-3-18
	 * @param user
	 * @return
	 */
	public static byte[] stopElectricizeEventProtocol(int chargeTime,short exceptionRet,int exceptionCode,int userId ){
		
	
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		//1.充电时长
		byteBuffer.putInt(chargeTime);//单位是分钟
		//是否异常，0:异常停止,1:正常停止
		byteBuffer.putShort(exceptionRet);//单位是分钟
		//异常停止原因,当exceptionRet=1，exceptionCode=0
		byteBuffer.putInt(exceptionCode);//单位是分钟
		//帐号
		byteBuffer.putInt(userId);//用户主键
	
		return byteBuffer.getBytes();
	}
	
	//消费记录
	public static byte[] consumeRecordProtocol(int pkUserId,String userPhone,double totalAmt,int chargeTime,short exceptionRet,int exceptionCode){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putString(userPhone);
		//2.用户主键,INT
		byteBuffer.putInt(pkUserId);
		//3.电费和服务费,double(精度两位)
		byteBuffer.putDouble(totalAmt);
		//4.充电时间(分钟)
		byteBuffer.putInt(chargeTime);
		//5.是否异常，0:异常停止,1:正常停止
		byteBuffer.putShort(exceptionRet);//单位是分钟
		//6.异常停止原因,当exceptionRet=1，exceptionCode=0
		byteBuffer.putInt(exceptionCode);//单位是分钟
		
		return byteBuffer.getBytes();
	}
	
	//消费记录
	public static byte[] BalanceWarning(String electricCode,String Account){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		byteBuffer.putString(Account);//交易流水号
		byteBuffer.putString(electricCode);//电桩编号
		
		return byteBuffer.getBytes();
	}
	public static byte[] notifyEpGate(String electricCode,int gateId){
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putString(electricCode);//电桩编号
		byteBuffer.putInt(gateId);//枪头编号
		
		return byteBuffer.getBytes();
	}
}
