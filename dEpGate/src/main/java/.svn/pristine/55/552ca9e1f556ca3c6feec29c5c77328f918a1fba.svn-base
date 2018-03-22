package com.ec.epcore.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.GunConstants;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.RealChargeInfo;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.proto.ApciHeader;
import com.ec.epcore.net.proto.ConsumeRecord;
import com.ec.epcore.net.proto.ShProtoConstant;
import com.ec.epcore.net.server.ShEpMessage;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.service.EpGunService;
import com.ec.epcore.service.ShEpService;
import com.ec.net.proto.ByteBufferUtil;
import com.ec.net.proto.SingleInfo;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.DateUtil;
import com.ec.utils.StringUtil;

/**
 * 收消息，解码
 * 
 * 消息结构：
 * 
 * @author lwz
 * Mar 27, 2015 12:11:06 PM
 */
public class ShEpDecoder extends ByteToMessageDecoder {
	
	private byte[] lenBytes = new byte[ApciHeader.NUM_LEN_FIELD];
	
	private static final Logger logger = LoggerFactory.getLogger(ShEpDecoder.class);
	
		
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext,
			ByteBuf byteBuf, List<Object> list) throws Exception {
		
		String errorMsg="";
		int readableBytes= byteBuf.readableBytes();
		if(readableBytes<9)//如果长度小于长度,不读
		{
			logger.debug("shep decode fail,readableBytes<6,readableBytes:{},channel:{}", readableBytes,channelHandlerContext.channel());
			return;
		}
		
		int pos= byteBuf.bytesBefore((byte)ShProtoConstant.HEAD_FLAG1);//找到的位置
		int pos1= byteBuf.bytesBefore((byte)ShProtoConstant.HEAD_FLAG2);//找到的位置
		int discardLen=0;
		if(pos < 0 || pos1<0 || (pos1-pos)!=1)//没找到，全部读掉
		{
			discardLen = readableBytes;
			logger.debug("shep decode fail,not find flag header 0x45 0x43,readableBytes:{},channel:{}",readableBytes,channelHandlerContext.channel());
		}
		if(pos>0&&(pos1-pos)==1)
		{
			discardLen = pos;	
			logger.debug("shep decode, find flag header 0x45 0x43,pos:{},channel:{}",pos,channelHandlerContext.channel());
		}
		if(discardLen>0)
		{
			byte[] dicardBytes= new byte[discardLen];
			byteBuf.readBytes(dicardBytes);//
			
			if(GameConfig.printPhoneMsg==1)
			{
				logger.info("[shep],decode discard msg:{},channel:{}",WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			else
			{
				logger.debug("[shep],decode discard msg:{},channel:{}",WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			
			if(discardLen == readableBytes)
			{
				//没有数据可对，还回
				return;
			}
		}
		
		readableBytes= byteBuf.readableBytes();
		if(readableBytes<9)
		{
			logger.debug("shep decode fail,readableBytes<6 readableBytes:{},channel:{}",readableBytes,channelHandlerContext.channel());
			
			return;
		}
		
		//1、先标记读索引（必须）
		byteBuf.markReaderIndex();
		
		short protocolhead = byteBuf.readShort();//读取协议头	
		int lengL = byteBuf.readByte()&0x0ff;
		int lengH = byteBuf.readByte()&0x0ff;
	    
		int msg_len = lengL+lengH*0x100;	
		int remain_len = byteBuf.readableBytes();

		if(remain_len<msg_len-4 ) //2个字节长度和2个字节协议头
		{
			logger.debug("shep decode fail,remain_len<msg_len,remain_len:{},channel:{}", remain_len, channelHandlerContext.channel());
	
			byteBuf.resetReaderIndex();
			return ;
		}
		int version = byteBuf.readByte();//一个字节版本号
		int frameNum = byteBuf.readByte();//一个字节序列号域
		 lengL = byteBuf.readByte()&0x0ff;
		 lengH = byteBuf.readByte()&0x0ff;
	    
		int cmd = lengL+lengH*0x100;
		
		byte Msg[]= null;
		Msg= new byte[msg_len-8];
    	byteBuf.readBytes(Msg);

    	ShEpMessage message = new ShEpMessage();
    			
    	message.setLength(msg_len);
    	message.setCmd((short)cmd);
    	message.setSerial((byte)frameNum);
    	message.setVersion((byte)version);
    	message.setBytes(Msg);
    			
    	list.add(message);
		
	}
	
	public static void decodeLogin(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		short pre1 = byteBuffer.getShort(); //预留1
		short pre2 = byteBuffer.getShort(); //预留2
		
		byte[] bEpCode = new byte[16];
		byteBuffer.get(bEpCode);
		String epCode =  StringUtil.getByteString(bEpCode);
		byteBuffer.get(bEpCode);
		
		byte type = byteBuffer.get();//充电桩类型 0：普通类型 1：自动功率分配
		
		int epVer = ByteBufferUtil.readInt(byteBuffer);//充电桩软件版本
		short pre4 = byteBuffer.getShort();//预留
		int startNum = (int)ByteBufferUtil.readUB4(byteBuffer); //启动次数
		byte mode = byteBuffer.get(); //数据上传模式 1：应答模式 2：主动上报模式
		int timeMin = ByteBufferUtil.readUB2(byteBuffer); //签到间隔时间 
		byte runMode = byteBuffer.get();//运行内部变量,0:正常工作模式  1:IAP 模式
		byte gunNum = byteBuffer.get();//充电枪个数
		byte heartTime = byteBuffer.get();//心跳上报周期
		byte heartTimeOutNum = byteBuffer.get();//心跳包检测超时次数
		int chargeNum = (int)ByteBufferUtil.readUB4(byteBuffer);//充电记录数量
		String systemTime = ByteBufferUtil.readBCDWithLength(byteBuffer, 6);//当前充电桩系统时间
		long pre5 = byteBuffer.getLong(); //预留
		long pre6 = byteBuffer.getLong(); //预留
		long pre7 = byteBuffer.getLong(); //预留

		logger.info("sh ep login,epCode:{},充电桩类型:{},充电桩软件版本:{},启动次数:{},数据上传模式:{}," +
				"签到间隔时间:{},运行内部变量:{},充电枪个数:{},心跳上报周期:{},心跳包检测超时次数:{},充电记录数量:{}" +
				"当前充电桩系统时间:{}",new Object[]{epCode,type,epVer,startNum,mode,timeMin,runMode,gunNum,
				heartTime,heartTimeOutNum,chargeNum,systemTime});
        ShEpService.handleLogin(epCommClient.getVersion(), epCode, epCommClient);
	}
	
	public static void decodeEpStatus(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		if(epCommClient.getStatus()<2)
		{
			logger.info("sh decodeEpStatus fail, ep not login,epCode:{},channel:{}",epCommClient.getIdentity(),epCommClient.getChannel());
			return;
		}
		// 1 终端机器编码 BCD码 8Byte 16位编码
		short pre1 = byteBuffer.getShort(); //预留
		short pre2 = byteBuffer.getShort(); //预留
		
		byte[] bEpCode = new byte[16];
		byteBuffer.get(bEpCode);
		String epCode =  StringUtil.getByteString(bEpCode);
		
		byteBuffer.get(bEpCode);
		byte gunNum = byteBuffer.get();//充电枪个数
		int gunNo = byteBuffer.get();//枪口号
		
		Map<Integer, SingleInfo> pointMapOneYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapTwoYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapYc = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapVarYc = new ConcurrentHashMap<Integer,SingleInfo>();
		
		
		int epType = byteBuffer.get(); //充电枪类型
		int workStatus =ConvertWorkStatus(byteBuffer.get()) ; //工作状态
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_WORKSTATUS, workStatus, "", 0);
		
		int soc= byteBuffer.get();//当前SOC %
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_SOC, soc, "", 0);
		
		int warning = (int)ByteBufferUtil.readUB4(byteBuffer); // 告警状态
		int carLink = byteBuffer.get(); //车连接状态
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_LINKED_CAR, carLink, "", 0);
		
		int  bAmt = (int)ByteBufferUtil.readUB4(byteBuffer);//本次充电累计充电费用
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, bAmt, "", 0);
		long pre3 = (int)ByteBufferUtil.readUB4(byteBuffer); //预留
		long pre4 = (int)ByteBufferUtil.readUB4(byteBuffer); //预留
		int value = (int)ByteBufferUtil.readUB2(byteBuffer);//直流充电电压
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_VOL, value, "", 0);
		value = (int)ByteBufferUtil.readUB2(byteBuffer);//直流充电电流
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_CURRENT, value*10, "", 0);
		
		value = (int)ByteBufferUtil.readUB2(byteBuffer);//BMS 需求电压
		value = (int)ByteBufferUtil.readUB2(byteBuffer);//BMS 需求电流
		value = byteBuffer.get(); //BMS 充电模式
		
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //交流A 相充电电压
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_A_VOL, value, "", 0);
	
		value = (int)ByteBufferUtil.readUB2(byteBuffer);; //交流B 相充电电压
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_B_VOL, value, "", 0);
	    
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //交流 c 相充电电压
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_C_VOL, value, "", 0);
	   
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //交流A 相充电电流
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_A_CURRENT, value, "", 0);
	
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //交流B 相充电电流
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_B_CURRENT, value, "", 0);
	    
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //交流 c 相充电电流
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_C_CURRENT, value, "", 0);
		
		value = (int)ByteBufferUtil.readUB2(byteBuffer); //估计剩余时间
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_REMAIN_TIME, value, "", 0);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer)/60; //充电时长(秒)
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_TOTAL_TIME, value, "", 0);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer)*10; //本次充电累计充电电量
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, value, "", 0);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer)*10; //充电前电表读数
		value = (int)ByteBufferUtil.readUB4(byteBuffer)*10; //当前电表读数
		
		value = byteBuffer.get(); //充电启动方式
		value = byteBuffer.get(); //充电策略
		value = (int)ByteBufferUtil.readUB4(byteBuffer); //充电策略参数
		value = byteBuffer.get(); //预约标志

		byte [] temp = new byte[32];
        byteBuffer.get(temp); //充电/预约卡号
        value = byteBuffer.get();
        long time = (int)ByteBufferUtil.readLong(byteBuffer);//预约/开始充电开始时间
        value = (int)ByteBufferUtil.readUB4(byteBuffer);//充电前卡余额
        value = (int)ByteBufferUtil.readUB4(byteBuffer); //预留
        value = (int)ByteBufferUtil.readUB4(byteBuffer); //充电功率
        value = (int)ByteBufferUtil.readUB4(byteBuffer); //系统变量3
        value = (int)ByteBufferUtil.readUB4(byteBuffer); //系统变量4
        value = (int)ByteBufferUtil.readUB4(byteBuffer); //系统变量5
        
        EpGunCache gunCache = EpGunService.getEpGunCache(epCode, gunNo);
		if(gunCache == null)
		{
			logger.error("decodeEpStatus,receive realData,epCode:{},epGunNo:{},gunCache is null",epCode, gunNo);
		}
		else
		{
			logger.debug("decodeEpStatus,receive realData,epCode:{}, epGunNo:{}",epCode, gunNo);
			gunCache.onRealDataChange(pointMapYc,11);
			gunCache.onRealDataChange(pointMapOneYx,1);
			gunCache.onRealDataChange(pointMapTwoYx,3);
			gunCache.onRealDataChange(pointMapVarYc,132);
		}
		String cardNo=StringUtil.repeat("0", 32);
		byte[] confirmdata = ShEpEncoder.do_epStatus_ret((byte)epCommClient.getVersion(),epCommClient.getRevINum().byteValue(), gunNo,cardNo);
		EpMessageSender.sendMessage(epCommClient,confirmdata);	
	}
	
	public static void decodeChargeRecord(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
	{
		if(epCommClient.getStatus()<2)
		{
			logger.info("sh decodeChargeRecord fail,ep not login,epCode:{},channel:{}",epCommClient.getIdentity(),epCommClient.getChannel());
			return;
		}
         ConsumeRecord consumeRecord = new ConsumeRecord();
		
		logger.info("sh Iec104Constant.M_CONSUME_RECORD:"+WmIce104Util.ConvertHex(msg, 1));
		// 1 终端机器编码 BCD码 8Byte 16位编码
		short pre1 = byteBuffer.getShort(); //预留
		short pre2 = byteBuffer.getShort(); //预留
		
		byte[] bEpCode = new byte[16];
		byteBuffer.get(bEpCode);
		String epCode =  StringUtil.getByteString(bEpCode);	
		byteBuffer.get(bEpCode);
		consumeRecord.setEpCode(epCode);
		
		int epType = byteBuffer.get(); //充电枪类型
		
		int gunNo = byteBuffer.get();//枪口号
		if (gunNo == 0) gunNo = 1;
		consumeRecord.setEpGunNo(gunNo);
		
		byte [] temp = new byte[32];
		byteBuffer.get(temp); //充电卡号
		String epUserAccount = StringUtil.getByteString(temp);
		consumeRecord.setEpUserAccount(epUserAccount);
		
		String stYear = ByteBufferUtil.readBCDWithLength(byteBuffer, 2);
		String stMonth = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String stDay = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String stHout = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String stMin = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String stSec = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String sst = stYear+"/"+stMonth+"/"+stDay+" "+stHout+":"+stMin+":"+stSec;
		long st = DateUtil.toLong(DateUtil.parse(sst, DateUtil.DATE_FORMAT_FULL)); 
		consumeRecord.setStartTime(st/1000);
		
		byteBuffer.get();
		String etYear = ByteBufferUtil.readBCDWithLength(byteBuffer, 2);
		String etMonth = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String etDay = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String etHout = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String etMin = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		String etSec = ByteBufferUtil.readBCDWithLength(byteBuffer, 1);
		byteBuffer.get();
		String set = etYear+"/"+etMonth+"/"+etDay+" "+etHout+":"+etMin+":"+etSec;
		long et =DateUtil.toLong(DateUtil.parse(set, DateUtil.DATE_FORMAT_FULL)); 
		consumeRecord.setEndTime(et/1000);
		
		int chargeTime = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setChargeUseTimes(chargeTime);
		byteBuffer.get();//开始SOC
		byteBuffer.get();//结束SOC
		
		int stopCause = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setStopCause(stopCause);
		
		int value = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setTotalDl(value);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setStartMeterNum(value);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setEndMeterNum(value);
		
		value = (int)ByteBufferUtil.readUB4(byteBuffer);//
		consumeRecord.setTotalAmt(value);
		
		ShEpService.handleConsumeRecord(epCommClient,consumeRecord);
		
	}
	
	public static void decodeHeart(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		if(epCommClient.getStatus()<2)
		{
			logger.info("sh decodeHeart fail,ep not login,epCode:{},channel:{}",epCommClient.getIdentity(),epCommClient.getChannel());
			return;
		}
		short pre1 = byteBuffer.getShort(); //预留
		short pre2 = byteBuffer.getShort(); //预留
		
		byte[] bEpCode = new byte[16];
		byteBuffer.get(bEpCode);
		String epCode =  StringUtil.getByteString(bEpCode);	
		byteBuffer.get(bEpCode);
		
		int heartNum = byteBuffer.getShort();
		ShEpService.handleHeart(epCode, epCommClient);
	}
	
	public static int ConvertWorkStatus(int workStatus)
	{
		int ret = 2;
		switch(workStatus)
		{
		case 0:
		case 4:
			ret=GunConstants.EP_GUN_W_STATUS_IDLE;
			break;
		case 1:
		case 3:
			ret=GunConstants.EP_GUN_W_STATUS_USER_OPER;
			break;
		case 2:
			ret=GunConstants.EP_GUN_W_STATUS_WORK;
			break;
		case 5:
			ret=GunConstants.EP_GUN_W_STATUS_BESPOKE;
			break;
		case 6:
			ret=GunConstants.EP_GUN_W_STATUS_FAULT;
			break;
		default:
			break;
		}
		return ret;
	}
	
}

