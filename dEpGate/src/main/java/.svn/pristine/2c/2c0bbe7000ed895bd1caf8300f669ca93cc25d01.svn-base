package com.ec.epcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.net.proto.ShProtoConstant;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.netty.buffer.DynamicByteBuffer;

/**
 * 发消息，编码
 * 
 * 消息结构：byte混淆码1 + byte混淆吗2 + int长度  + short协议号  + byte是否压缩  + byte[] 数据内容 + byte混淆码3 + byte混淆码4
 * 
 * @author haojian
 * Mar 27, 2013 4:11:15 PM
 */

public class ShEpEncoder extends MessageToByteEncoder{


	private static final Logger logger = LoggerFactory.getLogger(ShEpEncoder.class);
	
	
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
			
			logger.debug("盛宏ep 未知的消息类型... channel:{}",chc.toString());
			
		}
		
		
	}
    
    public static  byte[] Package(byte version,byte frameNum, int cmd,byte[] msgBody) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate(msgBody.length+9);
		
		short len = (short)(msgBody.length+9);
		byteBuffer.put((byte)ShProtoConstant.HEAD_FLAG1);
		byteBuffer.put((byte)ShProtoConstant.HEAD_FLAG2);

		byteBuffer.put((byte)len);
		byteBuffer.put((byte)0);
		
		byteBuffer.put(version);
		byteBuffer.put(frameNum);
		
		byteBuffer.put((byte)cmd);
		byteBuffer.put((byte)0);
		
		byteBuffer.put(msgBody);
		short crc= WmIce104Util.CRCSum(byteBuffer.getBytes(),6,0);
		byteBuffer.put((byte)crc);
		
		return byteBuffer.getBytes();
		
	}
	
	
	
    public static byte[] do_consumeRecord_ret(byte version,byte frameNum,int epGunNo,String cardNo) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 预留
		byteBuffer.putShort((short)0);
		// 2 预留
		byteBuffer.putShort((short)0);
		// 3充电口号
		byteBuffer.put((byte)epGunNo);
		// 4 卡号
		byteBuffer.putString(cardNo);
	
		return Package(version,frameNum,ShProtoConstant.C_LAST_CHARGEINFO_RET,byteBuffer.getBytes());
	}
    public static byte[] do_epStatus_ret(byte version,byte frameNum,int epGunNo,String cardNo) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 预留
		byteBuffer.putShort((short)0);
		// 2 预留
		byteBuffer.putShort((short)0);
		// 3充电口号
		byteBuffer.put((byte)epGunNo);
		//后台计费模式需回复4～6 字段
		// 4 卡号
		//byteBuffer.putString(cardNo);
		// 5.卡余额,
		//byteBuffer.putInt(0);
		//6.当前充电金额
		//byteBuffer.putInt(0);
		
	
		return Package(version,frameNum,ShProtoConstant.C_EPSTATUS_RET,byteBuffer.getBytes());
	}
    public static byte[] do_login_ret(byte version,byte frameNum) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 预留
		byteBuffer.putShort((short)0);
		// 2 预留
		byteBuffer.putShort((short)0);
		
	
		return Package(version,frameNum,ShProtoConstant.C_LOGIN_RET,byteBuffer.getBytes());
	}
    
    public static byte[] do_heart_ret(byte version,byte frameNum,int heartNum) {
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		// 1 预留
		byteBuffer.putShort((short)0);
		// 2 预留
		byteBuffer.putShort((short)0);
		// 3 心跳应答
		byteBuffer.put((byte)heartNum);
		byteBuffer.put((byte)0);
		
	
		return Package(version,frameNum,ShProtoConstant.C_HEART_RET,byteBuffer.getBytes());
	}



}
