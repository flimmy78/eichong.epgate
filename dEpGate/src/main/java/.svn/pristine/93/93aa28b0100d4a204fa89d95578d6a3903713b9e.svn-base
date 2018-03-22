package com.ec.epcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.YXCConstants;
import com.ec.epcore.net.proto.AnalyzeConstant;
import com.ec.epcore.net.proto.AnalyzeHeader;
import com.ec.net.proto.SingleInfo;
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
public class MonitorEncoder extends MessageToByteEncoder{


	private static final Logger logger = LoggerFactory.getLogger(MonitorEncoder.class);
	
	
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
			
			logger.debug("monitor 未知的消息类型:{}",chc.channel().toString());
			
		}
		
		
	}
	
    public static  byte[] Package(byte[] data,byte cos,short cmdtype) {
		
		AnalyzeHeader Header = new AnalyzeHeader();
		
		Header.setLength(3 + data.length);

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		byteBuffer.put(Header.toByteArray());	
		byteBuffer.put(cos);
		
		byte cmdtypeL = (byte)(cmdtype&0x00ff);		
		byteBuffer.put(cmdtypeL);
		
		byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
		byteBuffer.put(cmdtypeH);
		
		byteBuffer.put(data);
		
		
		return byteBuffer.getBytes();
	}

    public static  byte[] do_one_bit_yx(String epCode,int epGunNo,int currentType,Map<Integer, SingleInfo> pointMap) { 		
	
  
		assert(epCode.length()==YXCConstants.LEN_CODE);
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
	
		
		//1	终端机器编码//	BCD码	8Byte	16位编码 //
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2	枪口	bin码	1Byte	
		byteBuffer.put((byte)(epGunNo));
		byteBuffer.put((byte)currentType);
		byteBuffer.put((byte)pointMap.size());
        Iterator iter = pointMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			SingleInfo info=(SingleInfo) entry.getValue();
			int pointAddr = ((Integer)entry.getKey()).intValue();
			
			byteBuffer.put(WmIce104Util.short2Bytes((short)pointAddr));
			byteBuffer.put((byte)info.getIntValue());
			
			logger.debug("do_one_bit_yx,address:{},value:{}",pointAddr,info.getIntValue());

		} 
		
		return Package(byteBuffer.getBytes(),(byte)0,AnalyzeConstant.REAL_ONE_BIT_YX);
		
    }
    public static  byte[] do_two_bit_yx(String epCode,int epGunNo,int currentType,Map<Integer, SingleInfo> pointMap) { 		
	
		assert(epCode.length()==YXCConstants.LEN_CODE);
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		//1	终端机器编码//	BCD码	8Byte	16位编码 //
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2	枪口	bin码	1Byte	
		byteBuffer.put((byte)epGunNo);
		byteBuffer.put((byte)currentType);
		byteBuffer.put((byte)pointMap.size());
		
        Iterator iter = pointMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			//写地址
			SingleInfo info=(SingleInfo) entry.getValue();
			int pointAddr = ((Integer)entry.getKey()).intValue();
			byteBuffer.put(WmIce104Util.short2Bytes((short)pointAddr));
			byteBuffer.put((byte)info.getIntValue());
			
			logger.debug("do_two_bit_yx,address:{},value:{}",pointAddr,info.getIntValue());

		} 
		
		return Package(byteBuffer.getBytes(),(byte)0,AnalyzeConstant.REAL_TWO_BIT_YX);
		
    }
    public static  byte[] do_yc(String epCode,int epGunNo,int currentType,Map<Integer, SingleInfo> pointMap) { 		
	
  
		assert(epCode.length()==YXCConstants.LEN_CODE);
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		//1	终端机器编码//	BCD码	8Byte	16位编码 //
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2	枪口	bin码	1Byte	
		byteBuffer.put((byte)epGunNo);
		byteBuffer.put((byte)currentType);
		byteBuffer.put((byte)pointMap.size());
		
        Iterator iter = pointMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			
			SingleInfo info=(SingleInfo) entry.getValue();
			int pointAddr = ((Integer)entry.getKey()).intValue();
			byteBuffer.put(WmIce104Util.short2Bytes((short)pointAddr));
			
			byteBuffer.put(WmIce104Util.int2Bytes(info.getIntValue()));
			
			logger.debug("do_yc,address:{},value:{}",pointAddr,info.getIntValue());

		}
		return Package(byteBuffer.getBytes(),(byte)0,AnalyzeConstant.REAL_YC);
		
    }
    public static  byte[] do_var_yc(String epCode,int epGunNo,int currentType,Map<Integer, SingleInfo> pointMap) { 		
	
		assert(epCode.length()==YXCConstants.LEN_CODE);
		
		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		//1	终端机器编码//	BCD码	8Byte	16位编码 //
			
		byteBuffer.put(WmIce104Util.str2Bcd(epCode));
		//2	枪口	bin码	1Byte	
		byteBuffer.put((byte)epGunNo);
		byteBuffer.put((byte)currentType);
		byteBuffer.put((byte)pointMap.size());
        Iterator iter = pointMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			
			SingleInfo info=(SingleInfo) entry.getValue();
			int pointAddr= info.getAddress();
			byteBuffer.put(WmIce104Util.short2Bytes((short)pointAddr));
			
			String strVaule = info.getStrValue();
			//strVaule = "LA6N6GA67FB200991";
			
			if(strVaule==null || strVaule.length()==0)
			{
				byteBuffer.put((byte)4);
				byteBuffer.put(WmIce104Util.int2Bytes(info.getIntValue()));
				logger.debug("do_var_yc,address:{},value:{}",pointAddr,info.getIntValue());
			}
			else
			{
				int strLen= strVaule.length();
				
				byteBuffer.put((byte)strLen);
				byteBuffer.put(strVaule.getBytes());
				logger.debug("do_var_yc,address:{},value:{}",pointAddr,strVaule);
			}

		} 
		return Package(byteBuffer.getBytes(),(byte)0,AnalyzeConstant.REAL_VAR_YC);
		
    }
    
    
    //心跳
    public static  byte[] do_heart() {

        AnalyzeHeader Header = new AnalyzeHeader();
		
		Header.setLength( 3 );

		DynamicByteBuffer byteBuffer = DynamicByteBuffer.allocate();
		
		
		byteBuffer.put(Header.toByteArray());
		
		byteBuffer.put((byte)1);
		byte cmdtypeL = 01;		
		byteBuffer.put(cmdtypeL);
		
		byte cmdtypeH = 00;
		byteBuffer.put(cmdtypeH);

		return byteBuffer.getBytes();
	}

  

}
