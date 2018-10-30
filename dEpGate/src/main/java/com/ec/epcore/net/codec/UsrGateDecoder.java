package com.ec.epcore.net.codec;

import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.proto.PhoneConstant;
import com.ec.epcore.net.server.UsrGateMessage;
import com.ec.epcore.service.UsrGateService;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 收消息，解码   
 * 
 * 消息结构：2字节协议头+ 2字节长度 (小端)+ 1字节原因+2字节命令类型  + byte[] 数据内容

 */
public class UsrGateDecoder extends ByteToMessageDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(UsrGateDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext,
			ByteBuf byteBuf, List<Object> list) throws Exception {
		
		String errorMsg="";
		int readableBytes= byteBuf.readableBytes();
		if(readableBytes<6)//如果长度小于长度,不读
		{
		
			logger.debug("userGate decode fail,readableBytes<6,readableBytes:{},channel:{}", readableBytes,channelHandlerContext.channel());


			return;
		}
		
		int pos= byteBuf.bytesBefore(PhoneConstant.HEAD_FLAG1);//找到的位置
		int pos1= byteBuf.bytesBefore(PhoneConstant.HEAD_FLAG2);//找到的位置
		int discardLen=0;
		if(pos < 0 || pos1<0 || (pos1-pos)!=1)//没找到，全部读掉
		{
			discardLen = readableBytes;
			logger.debug("userGate decode fail,not find flag header 0x45 0x43,readableBytes:{},channel:{}",readableBytes,channelHandlerContext.channel());
		}
		if(pos>0&&(pos1-pos)==1)
		{
			discardLen = pos;	
			logger.debug("userGate decode, find flag header 0x45 0x43,pos:{},channel:{}",pos,channelHandlerContext.channel());
		}
		if(discardLen>0)
		{
			byte[] dicardBytes= new byte[discardLen];
			byteBuf.readBytes(dicardBytes);//
			
			if(GameConfig.printPhoneMsg==1)
			{
				logger.info("[userGate],decode discard msg:{},channel:{}",WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			else
			{
				logger.debug("[userGate],decode discard msg:{},channel:{}",WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			
			if(discardLen == readableBytes)
			{
				//没有数据可对，还回
				return;
			}
		}
		
		readableBytes= byteBuf.readableBytes();
		if(readableBytes<6)
		{
			logger.debug("userGate decode fail,readableBytes<6 readableBytes:{},channel:{}",readableBytes,channelHandlerContext.channel());
			
			return;
		}
		
		//1、先标记读索引（必须）
		byteBuf.markReaderIndex();
		
		short protocolhead = byteBuf.readShort();//读取协议头	
		int msg_len = byteBuf.readShort();
		
		int remain_len = byteBuf.readableBytes();

		if(remain_len<msg_len )
		{
			logger.debug("userGate decode fail,remain_len<msg_len,remain_len:{},channel:{}", remain_len, channelHandlerContext.channel());
	
			byteBuf.resetReaderIndex();
			return ;
		}
		int cmd = byteBuf.readShort();
		
		byte Msg[]= null;
		Msg= new byte[msg_len-2];
    	byteBuf.readBytes(Msg);

    	UsrGateMessage message = new UsrGateMessage();
    			
    	message.setLength(msg_len);
    	message.setCmd(cmd);

    	message.setBytes(Msg);
    			
    	list.add(message);
		
	}
	
	
	public static void decodeLogin(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		int OrgType = byteBuffer.get();
		int vserion = (int)byteBuffer.get();
		UsrGateService.handleUsrGateLogin(channel, h,m,s,OrgType,vserion);
	}
	
	public static void decodeAck(Channel channel,ByteBuffer byteBuffer)
	{
		short cmd  = byteBuffer.getShort();
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		long usrId = byteBuffer.getLong();
		
		UsrGateService.handleAck(channel,cmd,usrId,h,m,s);
	}
	public static void decodePhoneInit(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		
		long usrId = byteBuffer.getLong();
		
		UsrGateService.handlePhoneInit(channel, h,m,s,epCode,epGunNo,usrId);	
	}
	public static void decodePhoneOnline(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		long usrId = byteBuffer.getLong();
		int online = (int)byteBuffer.get();
		
		UsrGateService.handlePhoneOnline(channel, h,m,s,usrId,online);
	}

	public static void decodeCharge(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int OrgNo = byteBuffer.getInt();
		//用户usrId
		String usrLog = ByteUtil.getString(byteBuffer);
		
		String carNo = ByteUtil.getString(byteBuffer);
		String carVin = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		if(token==null)
		{
			logger.debug("token is null");
			token="";
		}
		int amt = byteBuffer.getInt();
		int payMode = byteBuffer.getShort();
		
		int chargeStyle = (int)byteBuffer.get();
		
		int bDispPrice = (int)byteBuffer.get();
		
		logger.debug("usrGate,receive startcharge,OrgNo:{},payMode:{},amt:{},chargeStyle:{},bDispPrice:{},carNo:{},carVin:{},token:{},usrLog:{},epCode:{},epGunNo:{}",
				new Object[]{OrgNo,payMode,amt,chargeStyle,bDispPrice,carNo,carVin,token,usrLog,epCode,epGunNo});
        UsrGateService.handleCharge(channel,h,m,s,epCode,epGunNo,OrgNo,usrLog,token,amt,payMode,chargeStyle,bDispPrice,carNo,carVin);
		
	}
	public static void decodeStopCharge(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
        int OrgNo = byteBuffer.getInt();
		String usrLog = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		if(token==null)
		{
			logger.debug("token is null");
			token="";
		}
		logger.debug("usrGate,receive stopcharge,OrgNo:{},token:{},usrLog:{},epCode:{},epGunNo:{}",
				new Object[]{OrgNo,token,usrLog,epCode,epGunNo});
		
		UsrGateService.handleStopCharge(channel, h,m,s,epCode,epGunNo,OrgNo,usrLog,token);
	}
	
	public static void decodeHeart(Channel channel,ByteBuffer byteBuffer)
	{
		UsrGateService.handleHeart(channel);
	}

	public static void deEpOnline(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		UsrGateService.handleEpOnlineResp(channel);
	}
	
	public static void deQueryOrder(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
        int OrgNo = byteBuffer.getInt();
		String usrLog = ByteUtil.getString(byteBuffer);
		String extra = ByteUtil.getString(byteBuffer);
		if(extra==null)
			extra="";
		
		
		logger.debug("usrGate,receive queryOrder,OrgNo:{},usrLog:{},epCode:{},epGunNo:{},extra:{}",
				new Object[]{OrgNo,usrLog,epCode,epGunNo,extra});
		UsrGateService.handleQueryOrder(channel, h,m,s,epCode,epGunNo,OrgNo,usrLog,extra);
	}

	public static void deQueryData4Common(Channel channel, ByteBuffer byteBuffer) {
		int h = (int) byteBuffer.get();
		int m = (int) byteBuffer.get();
		int s = (int) byteBuffer.get();

		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int) byteBuffer.get();

		String extra = ByteUtil.getString(byteBuffer);
		if (extra == null)
			extra = "";

		logger.debug("usrGate,receive queryData4Common,epCode:{},epGunNo:{},extra:{}",
				new Object[]{epCode, epGunNo, extra});


		UsrGateService.handleQueryData4Common(channel, h, m, s, epCode, epGunNo, extra);
	}

}
