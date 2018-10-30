package com.ec.usrcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.common.net.U2ECmdConstants;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.netty.buffer.DynamicByteBuffer;
import com.ec.utils.LogUtil;

/**
 * 发消息，编码
 * 
 * 消息结构：byte混淆码1 + byte混淆吗2 + int长度  + short协议号  + byte是否压缩  + byte[] 数据内容 + byte混淆码3 + byte混淆码4
 * 
 * @author 
 * Mar 27, 2013 4:11:15 PM
 */
@SuppressWarnings("rawtypes")
public class EpGateEncoder extends MessageToByteEncoder{
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateEncoder.class.getName()));
	
	
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
			logger.debug(LogUtil.addExtLog("未知的消息类型"),chc.channel().toString());
		}
	}

	public static byte[] Package(int cmd,byte[] msgBody) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate(msgBody.length+4);
		
		short len = (short)(msgBody.length+2);
		byteBuffer.put(U2ECmdConstants.HEAD_FLAG1);
		byteBuffer.put(U2ECmdConstants.HEAD_FLAG2);
		byteBuffer.putShort(len);
		byteBuffer.putShort((short)cmd);
		
		byteBuffer.put(msgBody);
		
		return byteBuffer.getBytes();
	}

	public static byte[] login(int serverType,byte[] hmsTime) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		byteBuffer.put((byte)serverType);// 渠道类型
		byteBuffer.put((byte)1);//协议版本
		
		return Package(U2ECmdConstants.EP_LOGIN,byteBuffer.getBytes());
	}
	public static  byte[] do_near_call_ep(String epCode,int type,int time) {
	
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)1);//协议版本
		byte[] cmdTimes = WmIce104Util.timeToByte();
		byteBuffer.put(cmdTimes);
	
		return Package(U2ECmdConstants.EP_CHARGE,byteBuffer.getBytes());
	
	}
	
	public static byte[] ack(int cmd,byte[] hmsTime,String usrId)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		byteBuffer.putShort((short)cmd);
		byteBuffer.put(hmsTime);
		byteBuffer.putString(usrId);

		return Package(U2ECmdConstants.EP_ACK,byteBuffer.getBytes());
	}
	
	public static byte[] heart()
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

		return Package(U2ECmdConstants.EP_HEART,byteBuffer.getBytes());
	}

	public static byte[] epOnline(byte[] hmsTime) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		
		return Package(U2ECmdConstants.EP_ONLINE,byteBuffer.getBytes());
	}

	public static byte[] phoneOnline(byte[] hmsTime,long usrId,int online) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		byteBuffer.putLong(usrId);
		byteBuffer.put((byte)online);
		
		return Package(U2ECmdConstants.PHONE_ONLINE,byteBuffer.getBytes());
	}

	public static byte[] phoneConnect(byte[] hmsTime,String epCode,int epGunNo,long usrId)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putLong(usrId);
		
		return Package(U2ECmdConstants.PHONE_CONNECT_INIT,byteBuffer.getBytes());
	}
	
	public static byte[] charge(byte[] hmsTime,String epCode,int epGunNo,String usrId,int frozenAmt,int payMode,int changeMode,int showPrice,int orgNo, String tradeMark, String vinCode, String token)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		byteBuffer.putInt(orgNo);
		byteBuffer.putString(usrId);
		byteBuffer.putString(tradeMark);
		byteBuffer.putString(vinCode);
		byteBuffer.putString(token);
		byteBuffer.putInt(frozenAmt);
		byteBuffer.putShort((short)payMode);
		byteBuffer.put((byte)changeMode);
		byteBuffer.put((byte)showPrice);
		
		return Package(U2ECmdConstants.EP_CHARGE,byteBuffer.getBytes());
	}
	
	public static byte[] stopCharge(byte[] hmsTime,String epCode,int epGunNo,int orgNo,String usrMark,String token)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		
		byteBuffer.putInt(orgNo);
		byteBuffer.putString(usrMark);
		byteBuffer.putString(token);
		
		return Package(U2ECmdConstants.EP_STOP_CHARGE,byteBuffer.getBytes());
	}
	
	public static byte[] queryOrderInfo(byte[] hmsTime,String epCode,int epGunNo,int orgNo,String userIdentity,String extra)
	{
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(hmsTime);
		
		byteBuffer.putString(epCode);
		byteBuffer.put((byte)epGunNo);
		
		byteBuffer.putInt(orgNo);
		byteBuffer.putString(userIdentity);
		byteBuffer.putString(extra);
		
		return Package(U2ECmdConstants.CCZC_QUERY_ORDER,byteBuffer.getBytes());
	}

	public static byte[] query4CommonData(byte[] hmsTime, String epCode, int epGunNo,  String extra) {
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();

		byteBuffer.put(hmsTime);

		byteBuffer.putString(epCode);
		byteBuffer.put((byte) epGunNo);

		byteBuffer.putString(extra);

		return Package(U2ECmdConstants.EP_4COMMON_REALDATA, byteBuffer.getBytes());
	}
}
