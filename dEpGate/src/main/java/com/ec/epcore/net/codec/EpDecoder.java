package com.ec.epcore.net.codec;

import com.ec.constants.TimingChargeConstants;
import com.ec.constants.UserConstants;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.MsgWhiteList;
import com.ec.epcore.cache.RealChargeInfo;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.proto.*;
import com.ec.epcore.net.server.EpMessage;
import com.ec.epcore.service.*;
import com.ec.net.proto.ByteBufferUtil;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.SingleInfo;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.*;
import com.ormcore.dao.DB;
import com.ormcore.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ec.epcore.net.server.EpMessageHandler.bizExecutorService;

/**
 * 收消息，解码
 * 
 * 消息结构：
 * 
 * @author lwz
 * Mar 27, 2015 12:11:06 PM
 */
public class EpDecoder extends ByteToMessageDecoder {
	
	private byte[] lenBytes = new byte[ApciHeader.NUM_LEN_FIELD];
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpDecoder.class.getName()));
	
		
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext,
			ByteBuf byteBuf, List<Object> list) throws Exception {
		
		String errorMsg="";
		int readableBytes= byteBuf.readableBytes();
		if(readableBytes<7)//如果长度小于APCI长度,不读
		{
			logger.debug(LogUtil.addFuncExtLog("readableBytes| < apci header 7,channel"), readableBytes,channelHandlerContext.channel());
			return;
		}
		byte bFlag104 = 0x68;
		int pos= byteBuf.bytesBefore(bFlag104);//找到0x68的位置
		int discardLen=0;
		if(pos < 0 )//没找到，全部读掉
		{
			discardLen = readableBytes;

			logger.debug(LogUtil.addFuncExtLog("not find flag header 0x68,discardLen|channel"),discardLen,channelHandlerContext.channel());

			
		}
		if(pos>0)
		{
			discardLen = pos;

			logger.debug(LogUtil.addFuncExtLog("find flag header 0x68 at pos|channel"),pos,channelHandlerContext.channel());

		}
		if(discardLen>0)
		{
			byte[] dicardBytes= new byte[discardLen];
			byteBuf.readBytes(dicardBytes);//	
			if(GameConfig.printEpMsg==1)
			{
				logger.info(LogUtil.addFuncExtLog("discard msg|channel"),WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			else
			{
				logger.debug(LogUtil.addFuncExtLog("discard msg|channel"),WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			}
			if(discardLen == readableBytes)
			{
				//没有数据可对，还回
				return;
			}
		}
		
		readableBytes= byteBuf.readableBytes();
		if(readableBytes<7)
		{
			logger.debug(LogUtil.addFuncExtLog("1 readableBytes|channel"),readableBytes,channelHandlerContext.channel());
			return;
		}
		
		//1、先标记读索引（必须）
		byteBuf.markReaderIndex();
		byteBuf.readByte();
		
		//byte[] lenBytes = new byte[ApciHeader.NUM_LEN_FIELD];
		byteBuf.readBytes(lenBytes);
		int msg_len= 0;
		for(int i=0;i<ApciHeader.NUM_LEN_FIELD;i++)
		{
			byte tempb = lenBytes[i];
			int nn1=tempb&0x0FF;
			if((nn1==255 ||nn1==253) && i==1)
			{
				nn1=0;
			}
			if(0 ==i)
			{
				msg_len += nn1;
			}
			else
			{
				msg_len += nn1 <<(i*8);
			}
		}
		int remain_len = byteBuf.readableBytes();
		byte Msg[]=null;
		
		if(remain_len<msg_len )
		{

			logger.debug(LogUtil.addFuncExtLog("ep remain_len<msg_len,remain_len|channel"),remain_len,channelHandlerContext.channel());
				
			byteBuf.resetReaderIndex();
			return ;
		}

		Msg= new byte[msg_len];
    	byteBuf.readBytes(Msg);
    	
		EpMessage gameMessage = handleIec104Msg(Msg,msg_len);
		
		list.add(gameMessage);
		
	}
		
	private EpMessage handleIec104Msg(byte msg[],int len)
	{
    	int ProtoFlag1 = msg[0]&0x0FF;
    	int ProtoFlag2 = msg[1]&0x0FF;
    	
    	short nFrameType=0;
    	
    	EpMessage gameMessage = new EpMessage();
		
		if (ProtoFlag1 ==255 && (ProtoFlag2==2 || ProtoFlag2 == 3 ) ) {
			//协议侦
			nFrameType=1;
            	
			
			byte[] bb = new byte[len+ApciHeader.NUM_HEAD+ ApciHeader.NUM_LEN_FIELD];
			bb[0]=0x68;
			bb[ApciHeader.NUM_HEAD]=0x0C;
			System.arraycopy(msg,0,bb,ApciHeader.NUM_HEAD+ ApciHeader.NUM_LEN_FIELD,len);
			
			gameMessage.setBytes(bb);
		} 
		else if(ProtoFlag1 ==253&& ProtoFlag2 >= 4)
		{
			//协议侦
			nFrameType=1;
			byte[] bb = new byte[len+ApciHeader.NUM_HEAD+ ApciHeader.NUM_LEN_FIELD];
			bb[0]=0x68;
			bb[ApciHeader.NUM_HEAD]=0x0d;
            System.arraycopy(msg,0,bb,ApciHeader.NUM_HEAD+ ApciHeader.NUM_LEN_FIELD,len);
			gameMessage.setBytes(bb);
		}
		else {
			gameMessage.setBytes(msg);
			byte FormatType = (byte) (msg[0] & 0x03);
			
			
			if (FormatType == 0 || FormatType == 2)
			{
				nFrameType=2; // I
			} 
			else 
			{
				if (FormatType == 1) 
					nFrameType=3;// S
				else {
					
					nFrameType=4;// U	
				}
			}
		}
		gameMessage.setFrameType(nFrameType);

		return gameMessage;
	}
	
	
	public static void decodeAcRealInfo(int  commVersion,int record_type,ByteBuffer byteBuffer) 			
			throws IOException 
	{
	    switch (record_type) 
	    {
	        case 1: 
		        {
		        	decodeWholeAcRealInfo1(commVersion, byteBuffer);
		        }  
		        break;
	        case 3: 
	        {
	        	decodeWholeAcRealInfo3(commVersion, byteBuffer);
	        }  
	        break;
	       
	        default:
	        	break;

	     }
	}
	
	public static void decodeWholeAcRealInfo1(int  commVersion,ByteBuffer in) throws IOException {
		
		if(commVersion>=3)
		{
			if(in.remaining()<56)
			{
				logger.debug(LogUtil.addExtLog("realData,msg.length<56,commVersion|msg"),commVersion,WmIce104Util.ConvertHex(in.array(),1));
				return;
				
			}
		}
		else
		{
			if(in.remaining()<40)
			{
				logger.debug(LogUtil.addExtLog("realData,msg.length<40,commVersion|msg"),commVersion,WmIce104Util.ConvertHex(in.array(),1));
				return;
				
			}
		}
		ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL+AsduHeader.H_LEN+1);
		//1	终端机器编码	BCD码	8Byte	16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(in, 8);
		//2
		int epGunNo=(int) in.get();
		
		Map<Integer, SingleInfo> pointMapOneYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapTwoYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapYc = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapVarYc = new ConcurrentHashMap<Integer,SingleInfo>();
		
		//3//0:关,1:开
		int linked_status = (int) in.get(); //
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_LINKED_CAR, linked_status, "", 0);
		
		//4	工作状态	11:M_ME_NB_1	BIN码	1Byte	0:离线,1:故障,2待机;3工作,4欠压故障;5,过压故障,6过电流故障
		int working_status = (int)in.get(); //
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_WORKSTATUS, working_status, "", 0);
		
		
		//5.收枪成功
		short gun_close_status = (short)in.get(); //
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_SIT, gun_close_status, "", 0);
		
		//6.充电枪盖关闭状态
		short gun_lid_status = (short)in.get(); //
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_LID, gun_lid_status, "", 0);
		
		//7.车与桩建立通信信号
		short gun2car_comm_status=(short)in.get(); //
		//5	交流输入过压告警	1:M_SP_NA_1	BIN码	1Byte	布尔型,变化上传
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_COMM_WITH_CAR, gun2car_comm_status, "", 0);
		
		//8
		int value = (int) in.get(); //0:不过压，1:过压
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		
		//9	交流输入欠压告警	1:M_SP_NA_1	BIN码	1Byte	布尔型,变化上传
		value = (int) in.get();//0:不欠压，1:欠压
		if(value==1)
			value=2;
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		
		
		//10	交流电流过负荷告警	1:M_SP_NA_1	BIN码	1Byte	布尔型,变化上传
		int loaded_warn = (int) in.get();//0:不过负荷，1:过负荷
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_CURRENT_LOAD_WARN, loaded_warn, "", 0);
		
		
		//11	充电输出电压	11:M_ME_NB_1	BIN码	2Byte	精确到小数点后一位
		int nVol=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_VOL, nVol, "", 0);
		
		
		//12	充电输出电流	11:M_ME_NB_1	BIN码	2Byte	精确到小数点后二位
		int nCurrent=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_CURRENT, nCurrent, "", 0);
		
		
		
		
		//13	输出继电器状态	1:M_SP_NA_1	BIN码	1Byte	布尔型,变化上传://0:关,1:开
		int out_relay_status = (int) in.get();
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_OUT_RELAY_STATUS, out_relay_status, "", 0);
		
		
		//14	有功总电度	132:M_MD_NA_1	BIN码	4Byte	精确到小数点后二位
		int nDbNum= ByteBufferUtil.readInt(in);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM, nDbNum, "", 0);
	
		//15	累计充电时间	11:M_ME_NB_1	BIN码	2Byte	单位:min
		int total_cd_time = (int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_TOTAL_TIME, total_cd_time, "", 0);
		
		
		if(commVersion >=3)
		{
			//车占位
			short car_place_status= (short)in.get();
				
			RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CAR_PLACE, car_place_status, "", 0);
			
			//
			int chargeCost= ByteBufferUtil.readInt(in);
			
				
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, chargeCost, "", 0);
			//18
			int chargePrice= ByteBufferUtil.readInt(in)*10;
			
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_PRICE, chargePrice, "", 0);
			
			int chargedMeterNum= ByteBufferUtil.readInt(in);
			
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, chargedMeterNum, "", 0);
			
			int carPlaceLock= (int)in.get();
			
			RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_CAR_PLACE_LOCK, carPlaceLock, "", 0);
			
		}
		else
		{
			RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CAR_PLACE, 0, "", 0);
			RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_CAR_PLACE_LOCK, 0, "", 0);
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, 0, "", 0);
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_PRICE, 0, "", 0);
			RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, 0, "", 0);
			
		}
		
		EpGunCache gunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(gunCache == null)
		{
			logger.error("handleWholeAcRealInfo1,receive realData,epCode:{},epGunNo:{},gunCache is null",epCode, epGunNo);
		}
		else
		{
			logger.debug("handleWholeAcRealInfo1,receive realData,epCode:{}, epGunNo:{}",epCode, epGunNo);
			gunCache.onRealDataChange(pointMapYc,11);
			gunCache.onRealDataChange(pointMapOneYx,1);
			gunCache.onRealDataChange(pointMapTwoYx,3);
			gunCache.onRealDataChange(pointMapVarYc,132);
		}
	}
	public static void decodeWholeAcRealInfo3(int  commVersion,ByteBuffer in) throws IOException {
		logger.debug(LogUtil.addExtLog("msg"),WmIce104Util.ConvertHex(in.array(),1));
		
		ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL+AsduHeader.H_LEN+1);
		//1  充电机编号
		String epCode = ByteBufferUtil.readBCDWithLength(in, 8);
		
		int epGunNo=(int)in.get();
		
		Map<Integer, SingleInfo> pointMapOneYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapTwoYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapYc = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapVarYc = new ConcurrentHashMap<Integer,SingleInfo>();
		
		
		//3  充电机输出电压//11：M_ME_NB_1  BIN 码  2Byte
		int nVol=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 3:{}",nVol);
	
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_VOL, nVol, "", 0);
		
		//4  充电机输出电流
		//11：M_ME_NB_1  BIN 码  2Byte
		//精确到小数点后二位
		int nCurrent=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 4:{}",nCurrent);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_CURRENT, nCurrent, "", 0);
		

		/*5  充电机状态
		11：M_ME_NB_1  压缩 BCD 码  2Byte
		变化上传，0001- 告警 0002-待机 0003- 工作  0004- 离线
		0005-完成*/
		int value=(int) in.get();
		//logger.debug("field 5:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_WORKSTATUS, value, "", 0);
		
		/*6  地锁
		11：M_ME_NB_1  压缩 BCD 码  2Byte
		变化上传，0001- 告警 0002-待机 0003- 工作  0004- 离线
		0005-完成*/
		value=(int) in.get();
		//logger.debug("field 6:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_CAR_PLACE_LOCK, value, "", 0);
		/*7 有功总电度
		132：M_MD_NA_1  BIN 码  4Byte
		精确到小数点后一位*/
		value=(int) ByteBufferUtil.readInt(in);
		//logger.debug("field 7:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM, value, "", 0);
		
		//8.已充金额 BIN 码 4Byte 
		int chargeCost= ByteBufferUtil.readInt(in);	
		//logger.debug("field 8:{}",chargeCost);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, chargeCost, "", 0);
		
		//9.电价BIN 码 4Byte 
		int chargePrice= ByteBufferUtil.readInt(in)*10;
		//logger.debug("field 9:{}",chargePrice);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_PRICE, chargePrice, "", 0);
		
		/*10已充总度数 BIN 码 4Byte*/
		int chargedMeterNum= ByteBufferUtil.readInt(in);
		//logger.debug("field 10:{}",chargedMeterNum);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, chargedMeterNum, "", 0);
		
		//11  累计充电时间
		//11：M_ME_NB_1  BIN 码  2Byte
		//单位：min
		value=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 11:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_TOTAL_TIME, value, "", 0);
		
		int value8bit = (int) in.get()&0xff;
		//logger.debug("field value8bit:{}",value8bit);
		/*13  是否连接电池
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传*/
		value = value8bit%2;
		//logger.debug("field 13:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_LINKED_CAR, value, "", 0);
		/*	14枪座状态
			1：M_SP_NA_1  BIN 码  1Byte
			布尔型,  变化上传； 0 正常， 1
			异常*/
		value = (value8bit>>>1)%2;
		//logger.debug("field 14:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_SIT, value, "", 0);
		
		/*15充电枪盖状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>2)%2;
		//logger.debug("field 15:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_LID, value, "", 0);
		
		/*16车与桩建立通信信号
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>3)%2;
		//logger.debug("field 16:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_COMM_WITH_CAR, value, "", 0);
		
		/*17车位占用状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>4)%2;
		//logger.debug("field 17:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CAR_PLACE, value, "", 0);
	
		
		value8bit = (int) in.get()&0xff;
		//logger.debug("field value8bit:{}",value8bit);
		/*18读卡器通讯异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = value8bit%2;
		//logger.debug("field 18:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CARD_READER_FAULT, value, "", 0);
		
		/*19急停按钮故障
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>1)%2;
		//logger.debug("field 19:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_URGENT_STOP_FAULT, value, "", 0);
		/*20避雷器故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>2)%2;
		//logger.debug("field 20:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_ARRESTER_EXCEPTION, value, "", 0);
		/*21绝缘检测故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>3)%2;
		//logger.debug("field 21:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_INSULATION_EXCEPTION, value, "", 0);
		
		/*22充电枪未连接告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>4)%2;
		//logger.debug("field 22:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_UNCONNECT_WARN, value, "", 0);
		/*23交易记录已满告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>5)%2;
		//logger.debug("field 23:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_TRANSRECORD_FULL_WARN, value, "", 0);
		/*24电度表异常
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>6)%2;
		//logger.debug("field 24:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_METER_ERROR, value, "", 0);
		/*25交流输入电压过压/欠压
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value8bit = (int) in.get()&0xff;
		value = value8bit%4;
		//logger.debug("field 25:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		//
		/*26充电机过温故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>2)%4;
		//logger.debug("field 26:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YX_2_CHARGE_OVER_TEMP, value, "", 0);
		
		/*27交流电流过负荷告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>4)%4;
		//logger.debug("field 27:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YX_2_AC_CURRENT_LOAD_WARN, value, "", 0);
		
		/*28输出继电器状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>6)%4;
		//logger.debug("field 28:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YX_2_OUT_RELAY_STATUS, value, "", 0);
	
		EpGunCache gunCache = EpGunService.getEpGunCache(epCode, epGunNo);	
		if(gunCache == null)
		{
			logger.error(LogUtil.addExtLog("receive realData,gunCache is null,epcode|gunno"),epCode,epGunNo);
			return;
		}
		logger.debug(LogUtil.addExtLog("receive realData,epCode|epGunNo"),epCode, epGunNo);
		gunCache.onRealDataChange(pointMapYc,11);
		gunCache.onRealDataChange(pointMapOneYx,1);
		gunCache.onRealDataChange(pointMapTwoYx,3);
		gunCache.onRealDataChange(pointMapVarYc,132);
	}
	
	public static void decodeWholeDcRealInfo(int commVersion,int record_type,ByteBuffer byteBuffer) throws IOException
	{
		if(record_type==2)
			decodeWholeDCRealInfo2(commVersion, byteBuffer);
		else
			decodeWholeDCRealInfo4(commVersion, byteBuffer);
			
	}
	@SuppressWarnings("unchecked")
	public static void decodeWholeDCRealInfo4(int commVersion,ByteBuffer in) throws IOException
	{
		if(commVersion>=3)
		{
			if(in.remaining()<56)
			{
				return;
			}
		}
		ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL+AsduHeader.H_LEN+1);
		//1  充电机编号
		String epCode = ByteBufferUtil.readBCDWithLength(in, 8);
		
		int epGunNo=(int) in.get();
		
		Map<Integer, SingleInfo> pointMapOneYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapTwoYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapYc = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapVarYc = new ConcurrentHashMap<Integer,SingleInfo>();
		
		
		//3  充电机输出电压//11：M_ME_NB_1  BIN 码  2Byte
		int nVol=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 3:{}",nVol);
	
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_VOL, nVol, "", 0);
		
		//4  充电机输出电流
		//11：M_ME_NB_1  BIN 码  2Byte
		//精确到小数点后二位
		int nCurrent=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 4:{}",nCurrent);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_CURRENT, nCurrent, "", 0);
		

		/*5  充电机状态
		11：M_ME_NB_1  压缩 BCD 码  2Byte
		变化上传，0001- 告警 0002-待机 0003- 工作  0004- 离线
		0005-完成*/
		int value=(int) in.get();
		//logger.debug("field 5:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_WORKSTATUS, value, "", 0);
		
		/*6  地锁
		11：M_ME_NB_1  压缩 BCD 码  2Byte
		变化上传，0001- 告警 0002-待机 0003- 工作  0004- 离线
		0005-完成*/
		value=(int) in.get();
		//logger.debug("field 6:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_CAR_PLACE_LOCK, value, "", 0);
		/*7 有功总电度
		132：M_MD_NA_1  BIN 码  4Byte
		精确到小数点后一位*/
		value=(int) ByteBufferUtil.readInt(in);
		//logger.debug("field 7:{}",value);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM, value, "", 0);
		
		//8.已充金额 BIN 码 4Byte 
		int chargeCost= ByteBufferUtil.readInt(in);	
		//logger.debug("field 8:{}",chargeCost);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, chargeCost, "", 0);
		
		//9.电价BIN 码 4Byte 
		int chargePrice= ByteBufferUtil.readInt(in)*10;
		//logger.debug("field 9:{}",chargePrice);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_PRICE, chargePrice, "", 0);
		
		/*10已充总度数 BIN 码 4Byte*/
		int chargedMeterNum= ByteBufferUtil.readInt(in);
		//logger.debug("field 10:{}",chargedMeterNum);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, chargedMeterNum, "", 0);
		
		//11  累计充电时间
		//11：M_ME_NB_1  BIN 码  2Byte
		//单位：min
		value=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 11:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_TOTAL_TIME, value, "", 0);
		
		//12 剩余时间充电时间
				//11：M_ME_NB_1  BIN 码  2Byte
		//单位：min
		value=(int) ByteBufferUtil.readUB2(in);
		
		//logger.debug("field 12:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_REMAIN_TIME, value, "", 0);
		
		int value8bit = (int) in.get()&0xff;
		//logger.debug("field value8bit:{}",value8bit);
		/*13  是否连接电池
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传*/
		value = value8bit%2;
		//logger.debug("field 13:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_LINKED_CAR, value, "", 0);
		/*	14枪座状态
			1：M_SP_NA_1  BIN 码  1Byte
			布尔型,  变化上传； 0 正常， 1
			异常*/
		value = (value8bit>>>1)%2;
		//logger.debug("field 14:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_SIT, value, "", 0);
		
		/*15充电枪盖状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>2)%2;
		//logger.debug("field 15:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_LID, value, "", 0);
		
		/*16车与桩建立通信信号
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>3)%2;
		//logger.debug("field 16:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_COMM_WITH_CAR, value, "", 0);
		
		/*17车位占用状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>4)%2;
		//logger.debug("field 17:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CAR_PLACE, value, "", 0);
	
		/*18读卡器通讯异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value8bit = (int) in.get()&0xff;
		//logger.debug("field value8bit:{}",value8bit);
		value = value8bit%2;
		//logger.debug("field 18:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CARD_READER_FAULT, value, "", 0);
		
		/*19急停按钮故障
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>1)%2;
		//logger.debug("field 19:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_URGENT_STOP_FAULT, value, "", 0);
		/*20避雷器故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>2)%2;
		//logger.debug("field 20:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_ARRESTER_EXCEPTION, value, "", 0);
		/*21绝缘检测故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>3)%2;
		//logger.debug("field 21:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_INSULATION_EXCEPTION, value, "", 0);
		
		/*22充电枪未连接告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>4)%2;
		//logger.debug("field 22:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_UNCONNECT_WARN, value, "", 0);
		/*23交易记录已满告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>5)%2;
		//logger.debug("field 23:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_TRANSRECORD_FULL_WARN, value, "", 0);
		/*24电度表异常
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>6)%2;
		//logger.debug("field 24:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_METER_ERROR, value, "", 0);
		/*25交流输入电压过压/欠压
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value8bit = (int) in.get()&0xff;
		value = value8bit%4;
		//logger.debug("field 25:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		//
		/*26充电机过温故障
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>2)%4;
		//logger.debug("field 26:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_CHARGE_OVER_TEMP, value, "", 0);
		
		/*27交流电流过负荷告警
		 1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>4)%4;
		//logger.debug("field 27:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_CURRENT_LOAD_WARN, value, "", 0);
		
		/*28输出继电器状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>6)%4;
		//logger.debug("field 28:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_OUT_RELAY_STATUS, value, "", 0);
		
		//29  SOC
				//11：M_ME_NB_1  BIN 码  2Byte
				//整型
		value=(int)in.get()&0xff;
		//logger.debug("field 29:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_SOC, value, "", 0);
		
		
		//30  电池组最低温度
		//11：M_ME_NB_1  BIN 码  2Byte
		//精确到小数点后一位
		value=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 30:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_BATTRY_LOWEST_TEMP, value, "", 0);
		
		//31  电池组最高温度
		//11：M_ME_NB_1  BIN 码  2Byte
		value=(int) ByteBufferUtil.readUB2(in);
		//logger.debug("field 31:{}",value);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_BATTRY_HIGHEST_TEMP, value, "", 0);
		
		//32电池反接故障
		value8bit = (int) in.get()&0xff;
		value = value8bit%2;
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_BATTRY_ERROR_LINK, value, "", 0);
		
		//logger.debug("field 32:{}",value);
		
		//33烟雾报警故障
		value = (value8bit>>>1)%2;
		//logger.debug("field 33:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_FOGS_WARN, value, "", 0);
		
		
		
		//
		
		/*34  BMS 通信异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (value8bit>>>2)%2;
		//logger.debug("field 34:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_BMS_ERROR, value, "", 0);
		
		/*35直流电度表异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传；0：不过
		压，1 过压*/
		value = (value8bit>>>3)%2;
		//logger.debug("field 35:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_DCMETER_ERROR, value, "", 0);
		
		value = (value8bit>>>4)%2;
		//logger.debug("field 35:{}",value);
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_DC_OUT_OVER_CURRENT_WARN, value, "", 0);
		
		value8bit = (int) in.get()&0xff;
		//充电模式
		value = value8bit%4;
		//logger.debug("field 36:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_DC_SUPPLY_CHARGE_STYLE, value, "", 0);
		//整车动力蓄电池SOC告警
		value = (value8bit>>>2)%4;
		//logger.debug("field 37:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BATTRY_SOC_WARN, value, "", 0);
		//蓄电池模块采样点过温告警
		value = (value8bit>>>4)%4;
		//logger.debug("field 38:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BATTRY_SAMPLE_OVER_TEMP, value, "", 0);
		//输出连接器过温
		value = (value8bit>>>6)%4;
		//logger.debug("field 39:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_OUT_LINKER_OVER_TEMP, value, "", 0);
		//整车动力蓄电池组输出连接器连接状态
		value8bit = (int) in.get()&0xff;
		value = value8bit%4;
		//logger.debug("field 40:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_OUT_LINKER_STATUS, value, "", 0);
		//整车蓄电池充电过流告警
		value = (value8bit>>>2)%4;
		//logger.debug("field 41:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BATTRY_CHARGE_OVER_CURRENT, value, "", 0);
		//直流母线输出过压/欠压
		value = (value8bit>>>4)%4;
		//logger.debug("field 42:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_DC_OUT_VOL_WARN, value, "", 0);
		
		value = (value8bit>>>6)%4;
		//logger.debug("field 42:{}",value);
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BMS_VOL_WARN, value, "", 0);
		
		EpGunCache gunCache = EpGunService.getEpGunCache(epCode, epGunNo);	
		if(gunCache == null)
		{
			logger.error("handleWholeDcRealInfo4,receive realData,epcode{},gunno{} gunCache=NULL",epCode,epGunNo);
			return;
		}
		logger.debug("handleWholeDcRealInfo4,receive realData,epCode:{}, epGunNo:{}",epCode, epGunNo);
		gunCache.onRealDataChange(pointMapYc,11);
		gunCache.onRealDataChange(pointMapOneYx,1);
		gunCache.onRealDataChange(pointMapTwoYx,3);
		gunCache.onRealDataChange(pointMapVarYc,132);
			
	}
	@SuppressWarnings("unchecked")
	public static void decodeWholeDCRealInfo2(int commVersion,ByteBuffer in) throws IOException
	{
		if(commVersion>=3)
		{
			if(in.remaining()<56)
			{
				return;
				
			}
		}
		ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL+AsduHeader.H_LEN+1);
		//1  充电机编号
		String epCode = ByteBufferUtil.readBCDWithLength(in, 8);
		
		int epGunNo=(int) in.get();
		
		Map<Integer, SingleInfo> pointMapOneYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapTwoYx = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapYc = new ConcurrentHashMap<Integer,SingleInfo>();
		Map<Integer, SingleInfo> pointMapVarYc = new ConcurrentHashMap<Integer,SingleInfo>();
		
		
		//2  充电机输出电压//11：M_ME_NB_1  BIN 码  2Byte
		int nVol=(int) ByteBufferUtil.readUB2(in);
	
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_VOL, nVol, "", 0);
		
		//3  充电机输出电流
		//11：M_ME_NB_1  BIN 码  2Byte
		//精确到小数点后二位
		int nCurrent=(int) ByteBufferUtil.readUB2(in);
		
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_OUT_CURRENT, nCurrent, "", 0);
		
		//4  SOC
		//11：M_ME_NB_1  BIN 码  2Byte
		//整型
		int nSoc=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_SOC, nSoc, "", 0);
		
		
		//5  电池组最低温度
		//11：M_ME_NB_1  BIN 码  2Byte
		//精确到小数点后一位
		int value=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_BATTRY_LOWEST_TEMP, value, "", 0);
		
		//6  电池组最高温度
		//11：M_ME_NB_1  BIN 码  2Byte
		value=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_BATTRY_HIGHEST_TEMP, value, "", 0);
		
		
		//7  累计充电时间
		//11：M_ME_NB_1  BIN 码  2Byte
		//单位：min
		value=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_TOTAL_TIME, value, "", 0);
		
		/*8  充电机状态
		11：M_ME_NB_1  压缩 BCD 码  2Byte
		变化上传，0001- 告警 0002-待机 0003- 工作  0004- 离线
		0005-完成*/
		value=(int) in.get();
		
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_WORKSTATUS, value, "", 0);
		
		
		/*9  BMS 通信异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_BMS_ERROR, value, "", 0);
		
		/*10  直流母线输出过压告警
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传；0：不过
		压，1 过压*/
		value = (int) in.get();
		
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		
		/*11  直流母线输出欠压告警
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传；0：不欠
		压，1 欠压*/
		
		value = (int) in.get();
		if(value==1)
			value=2;
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_AC_IN_VOL_WARN, value, "", 0);
		/*12  蓄电池充电过流告警
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传；0：不过
		流，1 过流*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BATTRY_CHARGE_OVER_CURRENT, value, "", 0);
		
		/*13  蓄电池模块采样点过温告警
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传；0：不过
		温，1 过温*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapTwoYx, YXCConstants.YX_2_BATTRY_SAMPLE_OVER_TEMP, value, "", 0);
		
		
		/*14  有功总电度
		132：M_MD_NA_1  BIN 码  4Byte
		精确到小数点后一位*/
		value=(int) ByteBufferUtil.readInt(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM, value, "", 0);
		
		
		/*15  是否连接电池
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_LINKED_CAR, value, "", 0);
		//chargeInfo.setConnect_battry(value);
		
		/*16  单体电池最高电压
		11：M_ME_NB_1  BIN 码  2Byte
		精确到小数点后三位*/
		value=(int) ByteBufferUtil.readUB2(in);
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP, value, "", 0);
		
		
		/*17  单体电池最低电压
		11：M_ME_NB_1  BIN 码  2Byte
		精确到小数点后三位*/
		value=(int) ByteBufferUtil.readUB2(in);
		//RealChargeInfo.AddPoint(pointMap, YXCConstants.YC_S, value, "", 0);
		
		/*18枪座状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_SIT, value, "", 0);
		/*19充电枪盖状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_GUN_LID, value, "", 0);
		
		/*20车与桩建立通信信号
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_COMM_WITH_CAR, value, "", 0);
		
		/*21车位占用状态
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CAR_PLACE, value, "", 0);
		/*22交易记录已满告警
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_TRANSRECORD_FULL_WARN, value, "", 0);
		/*23读卡器通讯异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_CARD_READER_FAULT, value, "", 0);
		/*24电度表异常
		1：M_SP_NA_1  BIN 码  1Byte
		布尔型,  变化上传； 0 正常， 1
		异常*/
		value = (int) in.get();
		RealChargeInfo.AddPoint(pointMapOneYx, YXCConstants.YX_1_METER_ERROR, value, "", 0);
		
	
		//25已充金额 BIN 码 4Byte 
		int chargeCost= ByteBufferUtil.readInt(in);	
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_COST, chargeCost, "", 0);
		
		//26电价BIN 码 4Byte 
		int chargePrice= ByteBufferUtil.readInt(in)*10;
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_PRICE, chargePrice, "", 0);
		
		//27已充总度数 BIN 码 4Byte
		int chargedMeterNum= ByteBufferUtil.readInt(in);
		RealChargeInfo.AddPoint(pointMapVarYc, YXCConstants.YC_VAR_CHARGED_METER_NUM, chargedMeterNum, "", 0);
		
		//28 车位地锁状态 BIN 码 1Byte
		int carPlaceLock= (int)in.get();
		RealChargeInfo.AddPoint(pointMapYc, YXCConstants.YC_CAR_PLACE_LOCK, carPlaceLock, "", 0);
		
			
		EpGunCache gunCache = EpGunService.getEpGunCache(epCode, epGunNo);	
		if(gunCache == null)
		{
			logger.error("handleWholeDcRealInfo2,receive realData,epcode{},gunno{} gunCache=NULL",epCode,epGunNo);
			return;
		}
		logger.debug("handleWholeDcRealInfo2,receive realData,epCode:{}, epGunNo:{}",epCode, epGunNo);
		
		gunCache.onRealDataChange(pointMapYc,11);
		gunCache.onRealDataChange(pointMapOneYx,1);
		gunCache.onRealDataChange(pointMapTwoYx,3);
		gunCache.onRealDataChange(pointMapVarYc,132);
		
	}
	@SuppressWarnings("unchecked")
	public static void decodeOneBitYx(Channel ch, ByteBuffer in) throws IOException
	{
		EpCommClient epCommClient = EpCommClientService.getCommClient(ch);
		if (null == epCommClient)
		{
			logger.error("receive realData dataType:1=oneBitYx,fail--did not find EpCommClient:{}:",ch);
		}
		if(epCommClient.getStatus() !=2) {
			logger.error("receive realData dataType:1=oneBitYx,fail--is not init,CommStatus:{}",epCommClient.getStatus());
			// 没有发协议侦的客户端都关闭
			
			return;
		}
	
		byte[] NRs =ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL);
		
		byte[] asduBytes= ByteBufferUtil.readWithLength(in,6);
		AsduHeader asduHeader = new AsduHeader(asduBytes);
		
		if(epCommClient.getVersion()< YXCConstants.PROTOCOL_VERSION_V4)
		{
			if (epCommClient.getMode()==2) {
				ByteBufferUtil.readWithLength(in,4);
			} else {
				byte[] time = ByteBufferUtil.readWithLength(in, 7);
			}
		}

		int nLimit = asduHeader.getLimit()&0xff;
		int vsq = nLimit>> 7;
		int vCount = nLimit - (vsq<<7);
	
		Vector singleInfos = new Vector(vCount);
			
		String epCode = epCommClient.getIdentity();
		int gunno = 0;
		if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4 || epCommClient.getMode()==2)
		{
			 epCode = ByteBufferUtil.readBCDWithLength(in, 8);
			 gunno = in.get();
		}
		
//		logger.debug("receive realData dataType:1=oneBitYx,epCode:{},epGunNo:{},Identity:{},vCount:{}",
//				new Object[]{epCode,gunno,epCommClient.getIdentity(),vCount});
		
		//发送数据给epdata
		//EpDataClientService epDataClientService = EpDataClientService.getInstance();

		if(vsq == 0) //后面地址不是连续的
		{
			for(int i=0;i< vCount ;i++)
			{
				byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
				int address = WmIce104Util.bytes2int(infoAddress);
				int value = (int)in.get();
//				logger.debug("receive realData dataType:1=oneBitYx,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==0",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address,value});
				
				//发送数据给epdata
				//epDataClientService.recordPileStatusValue(epCode, gunno, 1,address, value);
				
				SingleInfo loopSingleInfo= new SingleInfo();
				loopSingleInfo.setAddress(address);
				loopSingleInfo.setIntValue(value);
				singleInfos.add(loopSingleInfo);
			}
		}
		else
		{
			byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
			int address = WmIce104Util.bytes2int(infoAddress);
			for(int i=0;i< vCount ;i++)
			{
				int value = (int)in.get();
				SingleInfo loopSingleInfo= new SingleInfo();
				loopSingleInfo.setAddress(address+i);
//				logger.debug("receive realData dataType:1=oneBitYx,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==1",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address+i,value});
				//发送数据给epdata
				//epDataClientService.recordPileStatusValue(epCode, gunno, 1,address+i, value);
				
				
				loopSingleInfo.setIntValue(value);
				singleInfos.add(loopSingleInfo);
			}
			
		}
		if(epCommClient.getMode() ==0)//集中器
		{
			if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4) 
			{
			    EpConcentratorService.handleOneBitYxInfo_v4(epCode,gunno,epCommClient.getIdentity(), singleInfos);
			}
			else
				EpConcentratorService.handleOneBitYxInfo(epCommClient.getIdentity(), singleInfos);
		}
		else
		{
		    
		    if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4) 
			{
		    	 EpService.handleOneBitYxInfo_v4(epCode,gunno,singleInfos);
			}
		    else
			    EpService.handleOneBitYxInfo(epCode, singleInfos);
			
		}
		
	}
	@SuppressWarnings("unchecked")
	public static void decodeTwoBitYx(Channel ch, ByteBuffer in) throws IOException
	{
		EpCommClient epCommClient = EpCommClientService.getCommClient(ch);
		if (null == epCommClient)
		{
			logger.error("receive realData dataType:2=twoBitYx,fail--did not find EpCommClient:{}:",ch);
		}
		if(epCommClient.getStatus() !=2) {
			logger.error("receive realData dataType:2=twoBitYx,Identity:{},fail--is not init,commStatus:{}",epCommClient.getIdentity(),epCommClient.getStatus());
			// 没有发协议侦的客户端都关闭
			
			return;
		}
		
		byte[] NRs =ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL);
	
		byte[] asduBytes= ByteBufferUtil.readWithLength(in,6);
		AsduHeader asduHeader = new AsduHeader(asduBytes);
		if(epCommClient.getVersion()< YXCConstants.PROTOCOL_VERSION_V4)
		{
			if (epCommClient.getMode()==2) {
				ByteBufferUtil.readWithLength(in,4);
			} else {
				byte[] time = ByteBufferUtil.readWithLength(in, 7);
			}
		}
		
		int nLimit = asduHeader.getLimit()&0xff;
		int vsq = nLimit>> 7;
		int vCount = nLimit - (vsq<<7);
	
		Vector singleInfos = new Vector(vCount);
		
		//发送数据给epdata
		//EpDataClientService epDataClientService = EpDataClientService.getInstance();
		
		String epCode = epCommClient.getIdentity();
		int gunno = 0;
		if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4 || epCommClient.getMode()==2)
		{
			 epCode = ByteBufferUtil.readBCDWithLength(in, 8);
			 gunno = in.get();
		}
		
//			logger.debug("receive realData dataType:2=twoBitYx,epCode:{},epGunNo:{},Identity:{},vCount:{}",
//				new Object[]{epCode,gunno,epCommClient.getIdentity(),vCount});
		
		if(vsq == 0) //后面地址不是连续的
		{
			for(int i=0;i< vCount ;i++)
			{
				byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
				int address = WmIce104Util.bytes2int(infoAddress);
				int value = (int)in.get();

//					logger.debug("receive realData dataType:2=twoBitYx,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==0",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address,value});
					//发送数据给epdata
					//epDataClientService.recordPileStatusValue(epCode, gunno , 2, address , value);
					
				
				SingleInfo loopSingleInfo= new SingleInfo();
				loopSingleInfo.setAddress(address);
				loopSingleInfo.setIntValue(value);
				singleInfos.add(loopSingleInfo);
			}
		}
		else
		{
			byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
			int address = WmIce104Util.bytes2int(infoAddress);
			for(int i=0;i< vCount ;i++)
			{
				int value = (int)in.get();
				SingleInfo loopSingleInfo= new SingleInfo();
				loopSingleInfo.setAddress(address+i);
				
//				logger.debug("receive realData dataType:2=twoBitYx,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==1",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address+i,value});
				//发送数据给epdata
				//epDataClientService.recordPileStatusValue(epCode, gunno , 2, address+i , value);
				
				loopSingleInfo.setIntValue(value);
				singleInfos.add(loopSingleInfo);
			}
			
		}
		if(epCommClient.getMode() ==0)//集中器
		{
			if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4) 
			{
			    EpConcentratorService.handleTwoBitYxInfo_v4(epCode,gunno,epCommClient.getIdentity(), singleInfos);
			}
			else
				EpConcentratorService.handleTwoBitYxInfo(epCommClient.getIdentity(), singleInfos);
		}
		else
		{
		    if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4)
			{
		    	 EpService.handleTwoBitYxInfo_v4(epCode,gunno,singleInfos);
			}
		    else
			   EpService.handleTwoBitYxInfo(epCode, singleInfos);
			
		}
		
	}
	public static void decodeYc(Channel ch, ByteBuffer in) throws IOException
	{
		EpCommClient epCommClient = EpCommClientService.getCommClient(ch);
		if (null == epCommClient)
		{
			logger.error("receive realData dataType:3=yc,fail--did not find EpCommClient:{}:",ch);
		}
		if(epCommClient.getStatus() !=2) {
			logger.error("receive realData dataType:3=yc,Identity:{},fail--is not init,commStatus:{}",epCommClient.getIdentity(),epCommClient.getStatus());
			// 没有发协议侦的客户端都关闭
			
			return;
		}
		byte[] NRs =ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL);
		byte[] asduBytes= ByteBufferUtil.readWithLength(in,6);
		AsduHeader asduHeader = new AsduHeader(asduBytes);
		if(epCommClient.getVersion()< YXCConstants.PROTOCOL_VERSION_V4)
		{
			if (epCommClient.getMode()==2) {
				ByteBufferUtil.readWithLength(in,4);
			} else {
				byte[] time = ByteBufferUtil.readWithLength(in, 7);
			}
		}
		
		int nLimit = asduHeader.getLimit() &0xff;
		int vsq = nLimit>> 7;
		int vCount = nLimit - (vsq<<7);
		
		Vector singleInfos = new Vector(vCount);
		
		//发送数据给epdata
		//EpDataClientService epDataClientService = EpDataClientService.getInstance();
		
		String epCode = epCommClient.getIdentity();
		int gunno = 0;
		if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4 || epCommClient.getMode()==2)
		{
			 epCode = ByteBufferUtil.readBCDWithLength(in, 8);
			 gunno = in.get();
		}
//
//		logger.debug("receive realData dataType:3=yc,epCode:{},epGunNo:{},Identity:{},vCount:{}",
//				new Object[]{epCode,gunno,epCommClient.getIdentity(),vCount});
		
		
		if(vsq == 0) //后面地址不是连续的
		{
			for(int i=0;i< vCount ;i++)
			{
				byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
				int value = (int) ByteBufferUtil.readUB2(in);
				byte qdsDesc = in.get();
				
				int address = WmIce104Util.bytes2int(infoAddress);
				
//				logger.debug("receive realData dataType:3=yc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==0",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address,value});
				//发送数据给epdata
				//epDataClientService.recordPileStatusValue(epCode, gunno , 3, address , value);
				
				SingleInfo loopSingleInfo= new SingleInfo();
				loopSingleInfo.setAddress(address);
				loopSingleInfo.setIntValue(value);
				loopSingleInfo.setQdsDesc(qdsDesc);
				singleInfos.add(loopSingleInfo);
			}
		}
		else
		{
			
			byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
			int address = WmIce104Util.bytes2int(infoAddress);
			for(int i=0;i< vCount ;i++)
			{
				SingleInfo loopSingleInfo= new SingleInfo();
				
				loopSingleInfo.setAddress(address+i);
				
				int value = ByteBufferUtil.readUB2(in);
//					logger.debug("receive realData dataType:3=yc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==1",
//						new Object[]{epCode,gunno,epCommClient.getIdentity(),address+i,value});
				
				//发送数据给epdata
				//epDataClientService.recordPileStatusValue(epCode, gunno , 3, address+i , value);
				
				loopSingleInfo.setIntValue(value);
				byte qdsDesc = in.get();
				loopSingleInfo.setQdsDesc(qdsDesc);
				
				singleInfos.add(loopSingleInfo);
			}
			
		}
		if(epCommClient.getMode() ==0)//集中器
		{
			if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4) 
			{
			    EpConcentratorService.handleYcInfo_v4(epCode,gunno,epCommClient.getIdentity(), singleInfos);
			}
			else
				EpConcentratorService.handleYcInfo(epCommClient.getIdentity(), singleInfos);
		}
		else
		{
		    if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4)
			{
		    	 EpService.handleYcInfo_v4(epCode,gunno,singleInfos);
			}
		    else
			     EpService.handleYcInfo(epCode, singleInfos);
			
		}
		
		
	}
	@SuppressWarnings("unchecked")
	public static void decodeVarYc(Channel ch, ByteBuffer in)
	{
		EpCommClient epCommClient = EpCommClientService.getCommClient(ch);
		if (null == epCommClient)
		{
			logger.error("receive realData dataType:4=varYc,fail--did not find EpCommClient:{}:",ch);
		}
		if(epCommClient.getStatus() !=2) {
			logger.error("receive realData dataType:4=varYc,Identity:{},fail--is not init,commStatus:{}",epCommClient.getIdentity(),epCommClient.getStatus());
			// 没有发协议侦的客户端都关闭
			
			return;
		}
	
		try{
		
		byte[] NRs =ByteBufferUtil.readWithLength(in,ApciHeader.NUM_CTRL);
		byte[] asduBytes= ByteBufferUtil.readWithLength(in,6);
		AsduHeader asduHeader = new AsduHeader(asduBytes);

			if(epCommClient.getVersion()< YXCConstants.PROTOCOL_VERSION_V4)
			{
				if (epCommClient.getMode()==2) {
					ByteBufferUtil.readWithLength(in,4);
				} else {
					byte[] time = ByteBufferUtil.readWithLength(in, 7);
				}
			}

			int nLimit = asduHeader.getLimit()&0xff;
			int vsq = nLimit>> 7;
			int vCount = nLimit - (vsq<<7);

			String epCode = epCommClient.getIdentity();
			int gunno = 0;
			if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4 || epCommClient.getMode()==2)
			{
				epCode = ByteBufferUtil.readBCDWithLength(in, 8);
				gunno = in.get();
			}

//			logger.debug("receive realData dataType:4=varYc,epCode:{},epGunNo:{},Identity:{},vCount:{}",
//					new Object[]{epCode,gunno,epCommClient.getIdentity(),vCount});

			Vector singleInfos = new Vector(vCount);
			
			//发送数据给epdata
			//EpDataClientService epDataClientService = EpDataClientService.getInstance();

			if(vsq == 0) //后面地址不是连续的
		{
			for(int i=0;i< vCount ;i++)
			{
				byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);

				int address = WmIce104Util.bytes2int(infoAddress);
				
				int infoLen = (int)in.get()&0xff;
			
				SingleInfo loopSingleInfo= new SingleInfo();
				if(infoLen ==4)
				{
					int value = ByteBufferUtil.readInt(in);
					loopSingleInfo.setIntValue(value);
//					logger.debug("sq=0,receive realData dataType:4=varYc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==0",
//							new Object[]{epCode,gunno,epCommClient.getIdentity(),address,value});
				}
				else
				{
					byte [] val =ByteBufferUtil.readWithLength(in,infoLen);
					String strValue = StringUtil.getByteString(val);
					
					byte [] val1=WmIce104Util.removeFFAndO(val);
					strValue ="";
					if(val1!=null)
						strValue = StringUtil.getByteString(val1);
					
					loopSingleInfo.setStrValue(strValue);
//					logger.debug("sq=0,handleVarYc,receive realData dataType:4=varYc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==0",
//							new Object[]{epCode,gunno,epCommClient.getIdentity(),address,strValue});
				}
				byte qdsDesc = in.get();			
				loopSingleInfo.setAddress(address);		
				loopSingleInfo.setQdsDesc(qdsDesc);
				singleInfos.add(loopSingleInfo);
		
			}
		}
		else
		{
			byte[] infoAddress=ByteBufferUtil.readWithLength(in,3);
			int address = WmIce104Util.bytes2int(infoAddress);
			
			for(int i=0;i< vCount ;i++)
			{
				int infoLen = (int)in.get()&0xff;		
				SingleInfo loopSingleInfo= new SingleInfo();
				if(infoLen ==4)
				{
					int value = ByteBufferUtil.readInt(in);
					loopSingleInfo.setIntValue(value);

//					logger.debug("sq=1,receive realData dataType:4=varYc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==1",
//							new Object[]{epCode,gunno,epCommClient.getIdentity(),address+i,value});
					
					//发送数据给epdata
					//epDataClientService.recordPileStatusValue(epCode, gunno , 4, address+i , value);
				}
				else
				{
					byte [] val =ByteBufferUtil.readWithLength(in,infoLen);
					String strValue = StringUtil.getByteString(val);
					loopSingleInfo.setStrValue(strValue);
//					logger.debug("sq=1,receive realData dataType:4=varYc,epCode:{},epGunNo:{},Identity:{},address:{},value:{},vsq==1",
//							new Object[]{epCode,gunno,epCommClient.getIdentity(),address+i,strValue});
					
					//发送数据给 epdata
					//epDataClientService.recordPileStatusValue(epCode, gunno , 4, address+i , strValue);
					
				}
				byte qdsDesc = in.get();
			
				loopSingleInfo.setAddress(address+i);
				
				loopSingleInfo.setQdsDesc(qdsDesc);
				singleInfos.add(loopSingleInfo);
			}
		}
		
		if(epCommClient.getMode() ==0)//集中器
		{
			if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4) 
			{
			    EpConcentratorService.handleVarYcInfo_v4(epCode,gunno,epCommClient.getIdentity(), singleInfos);
			}
			else
				EpConcentratorService.handleVarYcInfo(epCommClient.getIdentity(), singleInfos);
		}
		else
		{
		    if(epCommClient.getVersion()>= YXCConstants.PROTOCOL_VERSION_V4)
			{
		    	 EpService.handleVarYcInfo_v4(epCode,gunno,singleInfos);
			}
		    else
			     EpService.handleVarYcInfo(epCode, singleInfos);
			
		  }
		}
		catch(IOException e)
		{
			logger.error("handleVarYc exception,e.StackTrace:{}",e.getStackTrace());
		}
	}
	
	public static void decodeConsumeModelReq(EpCommClient epCommClient, ByteBuffer byteBuffer) throws IOException
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		bizExecutorService.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("handleConsumeModelReq");
				RateService.handleConsumeModelReq(epCommClient, epCode, time);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("handleConsumeModelReq exception:{},epCode:{}", e,epCode);
			}
		});

	}
	
	public static void decodeNoCardAuthByPw(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);

		int epGunNo = byteBuffer.get();
		String Account = ByteBufferUtil
				.readBCD_div_ff_WithLength(byteBuffer, 6);
		byte[] bPw = ByteBufferUtil.readWithLength(byteBuffer, 32);
		// logger.info("no card pw auth,params---epCode," + epCode + ",epGunNo"
		// + epGunNo + ",Account" + Account);
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		//handleNoCardAuthByPw(epCommClient, epCode, epGunNo, Account, bPw, time);
	}
	
	public static void decodeEpBespRet(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		logger.debug("M_BESPOKE_RET 1 msg:{}", WmIce104Util.ConvertHex(msg, 1));
		int epGunNo = byteBuffer.get();
		logger.debug("M_BESPOKE_RET gun:{}", epCode + epGunNo);
		int nRedo = byteBuffer.get();
		String bespokeNo = ByteBufferUtil.readBCDWithLength(byteBuffer, 6);

		logger.debug("M_BESPOKE_RET bespokeNo:{}", bespokeNo);

		int successFlag = byteBuffer.get();
		int errorCode = ByteBufferUtil.readUB2(byteBuffer);

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		logger.debug("M_BESPOKE_RET successFlag:{},errorCode:{}", successFlag,
				errorCode);

		EpBespResp bespResp = new EpBespResp(epCode, epGunNo, nRedo, bespokeNo,
				successFlag, errorCode);

		EpBespokeService.handleEpBespRet(epCommClient, bespResp, time);
	}
	
	public static void decodeEpCancelBespRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);

		int epGunNo = byteBuffer.get();

		String Account = ByteBufferUtil
				.readBCD_div_ff_WithLength(byteBuffer, 6);
		short LockFlag = byteBuffer.get();

		String bespokeNo = ByteBufferUtil.readBCDWithLength(byteBuffer, 6);

		// 6 执行取消预约的结果 BIN码 1Byte 1:表示取消预约成功
		short SuccessFlag = byteBuffer.get();

		// 7 时间 CP56Time2a 7Byte CP56Time2a

		byte[] sTime = ByteBufferUtil.readWithLength(byteBuffer, 7);
		long et = WmIce104Util.getP56Time2aTime(sTime);

		int errorCode = ByteBufferUtil.readUB2(byteBuffer);

		// {{电桩没对时，以服务器时间为准
		// java.util.Date dtEt = new Date();
		// et = dtEt.getTime() / 1000;
		// }}

		// System.out.print("onEpCancelBespRet,epCode:"+epCode+",epGunNo:"+epGunNo+"\n");
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		EpCancelBespResp cancelBespResp = new EpCancelBespResp(epCode, epGunNo,
				bespokeNo, LockFlag, SuccessFlag, et, errorCode);

		EpBespokeService.onEpCancelBespRet(epCommClient, cancelBespResp,
				time);
	}
	
	public static void decodeNoCardAuthByYZM(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		String YZCode = ByteBufferUtil.readBCD_div_ff_WithLength(byteBuffer, 8);
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		// handleNoCardAuthByYZM(channel,epCode,epGunNo,YZCode);
	}
	public static void decodeStartElectricizeEventV3(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
	{
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		String serialNo = ByteBufferUtil.readBCDWithLength(byteBuffer, 16);

		int meterNum = ByteBufferUtil.readInt(byteBuffer);

		byte[] bStartTimes = ByteBufferUtil.readCP56Time2a(byteBuffer);

		int remainTime = ByteBufferUtil.readInt(byteBuffer);

		int retFlag = byteBuffer.get();

		long st = WmIce104Util.getP56Time2aTime(bStartTimes);

		logger.debug(LogUtil.addExtLog("Iec104Constant.M_START_CHARGE_EVENT: st"), st);
		int errorCode = ByteBufferUtil.readUB2(byteBuffer);
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		ChargeEvent chargeEvent = new ChargeEvent(epCode, epGunNo, serialNo,
				meterNum, (int) st, remainTime, retFlag, errorCode);

		bizExecutorService.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("handleStartElectricizeEventV3");
				EpChargeService.handleStartElectricizeEventV3(epCommClient, chargeEvent, time);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("handleStartElectricizeEventV3 exception:{},epCode:{},serialNo:{}", new Object[]{e, epCode,serialNo});
			}
		});

	}
	public static void decodeStopElectricizeEvent(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 2 表低示数 BIN码 4Byte 精确到小数点后三位，单位度
		int db_value = ByteBufferUtil.readInt(byteBuffer);
		String SerialNo = ByteBufferUtil.readBCDWithLength(byteBuffer, 16);

		// 4 充电开始时间 CP56Time2a
		byte[] bEndTimes = ByteBufferUtil.readCP56Time2a(byteBuffer);

		long et = WmIce104Util.getP56Time2aTime(bEndTimes);

		// 5 充电枪编号 BIN码 1Byte
		int epGunNo = byteBuffer.get();

		short nStopRet = (short) ByteBufferUtil.readUB2(byteBuffer);

		byte flag = byteBuffer.get();

		byte offstates = byteBuffer.get();
		byte successflag = byteBuffer.get();

		// int errorCode = StreamUtil.readUB2(in);

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		int error = EpChargeService.handleStopElectricizeEvent(epCommClient,
				epCode, epGunNo, SerialNo, et, nStopRet, db_value, flag,
				offstates, successflag, bEndTimes, time);
		if(error==0)
			{
			    logger.info("stopcharge event success, epCode:{},epGunNo:{},SerialNo:{},db_value:{},nStopRet:{},cmdTime:{}",
						new Object[]{epCode,epGunNo,SerialNo,db_value,nStopRet,time});
			}
		else 
		{
			
		}
	}
	
	public static void decodeEpStartChargeResp(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();

		int ret = byteBuffer.get();
		ByteBufferUtil.readInt(byteBuffer);
		short errorCause = (short) ByteBufferUtil.readUB2(byteBuffer);
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		ChargeCmdResp chargeCmdResp = new ChargeCmdResp(epCode, epGunNo, ret,
				errorCause);
	    bizExecutorService.execute(() -> {
		    try {
			    String currentThreadName = Thread.currentThread().getName();
			    Thread.currentThread().setName("handEpStartChargeResp");
			    EpChargeService.handEpStartChargeResp(epCommClient, chargeCmdResp, time);
			    Thread.currentThread().setName(currentThreadName);
		    } catch (Exception e) {
			    logger.error("handEpStartChargeResp exception:{},epCode:{}", e,epCode);
		    }
	    });
	}

	public static void decodeEpStopChargeResp(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();

		int ret = byteBuffer.get();
		
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
	    

		EpChargeService.handEpStopChargeResp(epCode,epGunNo,ret,
				time);
	}
	
	public static void decodeConsumeRecord(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 0);
    }

	public static void decodeConsumeRecordWithVinCode(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 1);
    }

	public static void decodeConsumeRecordWithSOC(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 2);
    }

	public static void decodeConsumeRecordWithRate(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
    {
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 3);
    }

	public static void decodeConsumeRecordWithReaseon(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
	{
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 4);
	}

	public static void decodeConsumeRecordWithBat(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
	{
		decodeConsumeRecordWith(epCommClient, byteBuffer, msg, 5);
	}

	private static void decodeConsumeRecordWith(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg,int type) throws IOException
    {
		ConsumeRecord consumeRecord = new ConsumeRecord();
		int cmdNo = Iec104Constant.C_CONSUME_RECORD_CONFIRM;
		
		logger.info(LogUtil.addExtLog("Iec104Constant.M_CONSUME_RECORD|type"), WmIce104Util.ConvertHex(msg, 1), type);
		
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		
		consumeRecord.setEpCode(epCode);
		consumeRecord.setEpGunNo(epGunNo);
		

		// 2 交易流水号 BCD码 10Byte 16位交易代码
		consumeRecord.setSerialNo(ByteBufferUtil.readBCDWithLength(
				                byteBuffer,
								YXCConstants.LEN_BCD_ELECTRICIZE_SERIALNO));
		
		int accountType = (int)byteBuffer.get();
		
		consumeRecord.setAccountType(accountType);
		
		int userOrgin = (int)ByteBufferUtil.readUB2(byteBuffer);
		
		consumeRecord.setUserOrgin(userOrgin);
		
		// 3 用户编号 BCD码 8Byte 16位设备编码
		String Account = "";
		if(accountType == 1 )
		{
			byte[] bAccount = ByteBufferUtil.readWithLength(byteBuffer, 6);

			ByteBufferUtil.readWithLength(byteBuffer, 26);
			
			Account = WmIce104Util.bcd2StrDividFF(bAccount);
		}
		else
		{
			byte[] bAccount = ByteBufferUtil.readWithLength(byteBuffer, 32);
			
			Account = StringUtil.getCString(bAccount);
		}

		consumeRecord.setEpUserAccount(Account);

		// 4 离线交易类型 BIN码 1Byte 0:
		consumeRecord.setTransType((int) byteBuffer.get());

		// 5 开始时间 BIN码 7Byte CP56Time2a
		byte[] bStartTime = ByteBufferUtil.readCP56Time2a(byteBuffer);
		consumeRecord.setStartTime(WmIce104Util
                .getP56Time2aTime(bStartTime));
		// 6 结束时间 BIN码 7Byte CP56Time2a
		byte[] bEndTime = ByteBufferUtil.readCP56Time2a(byteBuffer);
		consumeRecord.setEndTime(WmIce104Util
                .getP56Time2aTime(bEndTime));
		
		// 7 尖度量
		consumeRecord.setjDl(ByteBufferUtil.readInt(byteBuffer));
		// 8 尖金额
		consumeRecord.setjAmt(ByteBufferUtil.readInt(byteBuffer));
        if (type == 3) consumeRecord.setjMoney(ByteBufferUtil.readInt(byteBuffer));
		
		// 9 峰度量
		consumeRecord.setfDl(ByteBufferUtil.readInt(byteBuffer));
		// 10 峰金额
		consumeRecord.setfAmt(ByteBufferUtil.readInt(byteBuffer));
        if (type == 3) consumeRecord.setfMoney(ByteBufferUtil.readInt(byteBuffer));

		// 11平度量
		consumeRecord.setpDl(ByteBufferUtil.readInt(byteBuffer));
		// 12 平金额
		consumeRecord.setpAmt(ByteBufferUtil.readInt(byteBuffer));
        if (type == 3) consumeRecord.setpMoney(ByteBufferUtil.readInt(byteBuffer));

		// 13谷度量
		consumeRecord.setgDl(ByteBufferUtil.readInt(byteBuffer));
		// 14 谷金额
		consumeRecord.setgAmt(ByteBufferUtil.readInt(byteBuffer));
        if (type == 3) consumeRecord.setgMoney(ByteBufferUtil.readInt(byteBuffer));

		// 15总电量
		consumeRecord.setTotalDl(ByteBufferUtil.readInt(byteBuffer));
		
		// 16总充电金额
		consumeRecord.setTotalChargeAmt(ByteBufferUtil.readInt(byteBuffer));

		// 17服务费
		consumeRecord.setServiceAmt(ByteBufferUtil.readInt(byteBuffer));	
		
		// 18开始充电总电量
		consumeRecord.setStartMeterNum(ByteBufferUtil.readInt(byteBuffer));
		// 19结束充电总电量
		consumeRecord.setEndMeterNum(ByteBufferUtil.readInt(byteBuffer));
		//20停止充电原因
		if (userOrgin == UserConstants.ORG_PXGJ) {
			consumeRecord.setStopCause(String.valueOf((int)byteBuffer.get()));
		} else {
			String stopCause = String.valueOf(ByteBufferUtil.readUB2(byteBuffer));
			if (type >= 3) {
				//结束详情
				stopCause += "|"+String.valueOf(ByteBufferUtil.readUB2(byteBuffer));
				logger.debug(LogUtil.addExtLog("stopCause"), stopCause);
			}
			consumeRecord.setStopCause(stopCause);
		}

		if (type > 0) {
			byte[] bVinCode = ByteBufferUtil.readWithLength(byteBuffer, 17);
			byte [] bVinCode2=WmIce104Util.removeFFAndO(bVinCode);
			String carVinCode = StringUtil.getByteString(bVinCode2);
			logger.debug(LogUtil.addExtLog("bVinCode|bVinCode2|carVinCode"),
					new Object[]{WmIce104Util.ConvertHex2(bVinCode),WmIce104Util.ConvertHex2(bVinCode2),carVinCode});
			consumeRecord.setCarVinCode(carVinCode);
			cmdNo = Iec104Constant.C_CONSUME_RECORD_VINCODE_CONFIRM;
			if (type > 1) {
				consumeRecord.setStartSoc(ByteBufferUtil.readUB2(byteBuffer));
				consumeRecord.setEndSoc(ByteBufferUtil.readUB2(byteBuffer));
				consumeRecord.setType(1);
				cmdNo = Iec104Constant.C_CONSUME_RECORD_SOC_CONFIRM;
                if (type == 3) {
					consumeRecord.setBatteryRatedCapacity(ByteBufferUtil.readUB2(byteBuffer));
					consumeRecord.setBatteryTotalEnergy(ByteBufferUtil.readUB2(byteBuffer));
                    cmdNo = Iec104Constant.C_CONSUME_RECORD_RATE_CONFIRM;
                } else if (type == 4) {
					cmdNo = Iec104Constant.C_CONSUME_RECORD_REASEON_CONFIRM;
				} else if (type > 4) {
					consumeRecord.setBatteryRatedCapacity(ByteBufferUtil.readUB2(byteBuffer));
					consumeRecord.setBatteryTotalEnergy(ByteBufferUtil.readUB2(byteBuffer));
					cmdNo = Iec104Constant.C_CONSUME_RECORD_BAT_CONFIRM;
				}
			}
		}
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		EpChargeService.handleConsumeRecord(cmdNo, epCommClient, consumeRecord, time);

		//保存最后接收的一条消费记录到文件
		if (MsgWhiteList.isOpen()&& MsgWhiteList.find(epCode)) 
		{
			FileUtils.writeLog(epCode + ".log", consumeRecord.toString());
			logger.debug(LogUtil.addExtLog("FileUtils.appendLog"),epCode);
		}
		//test3
    }

	public static void decodeQueryConsumeRecord(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		Logger log = LoggerFactory.getLogger("epRamLog");
		log.info("Iec104Constant.M_QUERY_CONSUME_RECORD:"+WmIce104Util.ConvertHex(msg, 1));
		
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int startPos = ByteBufferUtil.readInt(byteBuffer);
		int entryNum = ByteBufferUtil.readUB2(byteBuffer);
		if(entryNum<=0)
		{
			logger.info("Iec104Constant.M_QUERY_CONSUME_RECORD:entryNum<=0,epcode:{}",epCode);
			return;
		}
		logger.debug("Iec104Constant.M_QUERY_CONSUME_RECORD:epcode:{},entryNum:{}",epCode,entryNum);

		for(int i=0;i<entryNum;i++)	
		{
			// 1 终端机器编码 BCD码 8Byte 16位编码
			String epCode1 = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
			int epGunNo = byteBuffer.get();
		
		    ConsumeRecord consumeRecord = new ConsumeRecord();
		
		    consumeRecord.setEpCode(epCode1);
		    consumeRecord.setEpGunNo(epGunNo);
		

		    // 2 交易流水号 BCD码 10Byte 16位交易代码
		     consumeRecord.setSerialNo(ByteBufferUtil.readBCDWithLength2(
		    		            byteBuffer,
								YXCConstants.LEN_BCD_ELECTRICIZE_SERIALNO));
		
		    int accountType = (int)byteBuffer.get();
		
		    consumeRecord.setAccountType(accountType);
		
		    int userOrgin = (int)ByteBufferUtil.readUB2(byteBuffer);
		
		    consumeRecord.setUserOrgin(userOrgin);
		
		    // 3 用户编号 BCD码 8Byte 16位设备编码
		    String Account = "";
		    if(accountType == 1 )
		    {
			    byte[] bAccount = ByteBufferUtil.readWithLength(byteBuffer, 6);

			    ByteBufferUtil.readWithLength(byteBuffer, 26);
			
			    Account = WmIce104Util.bcd2StrDividFF(bAccount);
		    }
		    else
		    {
			    byte[] bAccount = ByteBufferUtil.readWithLength(byteBuffer, 32);
			
			    //System.out.print("hex bAccount:"+WmIce104Util.ConvertHex(bAccount, 1));
			    Account = StringUtil.getCString(bAccount);
		     }

		     consumeRecord.setEpUserAccount(Account);

		     // 4 离线交易类型 BIN码 1Byte 0:
		     consumeRecord.setTransType((int)byteBuffer.get());

		     // 5 开始时间 BIN码 7Byte CP56Time2a
		     byte[] bStartTime = ByteBufferUtil.readCP56Time2a(byteBuffer);
		     consumeRecord.setStartTime(WmIce104Util
				.getP56Time2aTime(bStartTime));
		     // 6 结束时间 BIN码 7Byte CP56Time2a
		     byte[] bEndTime = ByteBufferUtil.readCP56Time2a(byteBuffer);
		     consumeRecord.setEndTime(WmIce104Util
				 .getP56Time2aTime(bEndTime));
		
		     // 7 尖度量
		     consumeRecord.setjDl(ByteBufferUtil.readInt(byteBuffer));
		     // 8 尖金额
		     consumeRecord.setjAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 9 峰度量
		     consumeRecord.setfDl(ByteBufferUtil.readInt(byteBuffer));
		     // 10 峰金额
		     consumeRecord.setfAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 11平度量
		     consumeRecord.setpDl(ByteBufferUtil.readInt(byteBuffer));
		     // 12 平金额
		     consumeRecord.setpAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 13谷度量
		     consumeRecord.setgDl(ByteBufferUtil.readInt(byteBuffer));
		     // 14 谷金额
		     consumeRecord.setgAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 15总电量
		     consumeRecord.setTotalDl(ByteBufferUtil.readInt(byteBuffer));
		
		     // 16总充电金额
		     consumeRecord.setTotalChargeAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 17服务费
		     consumeRecord.setServiceAmt(ByteBufferUtil.readInt(byteBuffer));
		
		     // 18开始充电总电量
		     consumeRecord.setStartMeterNum(ByteBufferUtil.readInt(byteBuffer));
		     // 19结束充电总电量
		     consumeRecord.setEndMeterNum(ByteBufferUtil.readInt(byteBuffer));
		     //20停止充电原因
		     consumeRecord.setStopCause(String.valueOf((int)byteBuffer.get()));
		     EpChargeService.handleHisConsumeRecord(epCommClient,consumeRecord);
		
	  }
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
	  
	//  EpService.queryConsumeRecord(epCode,startPos,entryNum);	
    }
	
	public static void decodeBalanceWarning(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		logger.info("balance warning\n");
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = 1;

		String userAccount = ByteBufferUtil.readBCDWithLength(byteBuffer,
				YXCConstants.LEN_BCD_ACCOUNT);
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		//handleBalanceWarning(epCode, epGunNo, userAccount,cmdTimes);
    }
	
	public static void decodeEpHexFileSumaryReq(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 站地址
		short stationAddr = (short) ByteBufferUtil.readUB2(byteBuffer);
		// 硬件型号
		byte[] hardwarecode = ByteBufferUtil.readWithLength(byteBuffer, 10);

		String hardwareNumber = StringUtil.getAscii(hardwarecode);
		if (hardwareNumber.compareTo(" ") == 0) {
				logger.error("[upgrade],handleEpHexFileSumaryReq fail,hardwareNumber error:{}",WmIce104Util.ConvertHex(msg, 1));
			return;
		}
		// 硬件主版本号
		int hardwareM = byteBuffer.get();
		// 硬件子版本号
		int hardwareA = byteBuffer.get();
		// 分段字节大小
		int len = ByteBufferUtil.readUB2(byteBuffer);

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		// logger.debug("handleVersion,handleEpHexFileSumaryReq,epCode:" +
		// epCode+",stationAddr:"+ stationAddr+",hardwareNumber:"
		// +hardwareNumber + ",hardwareVersion:V" +hardwareM+"."+hardwareA);

		EqVersionService.handleEpHexFileSumaryReq(epCommClient, epCode, stationAddr,
				hardwareNumber, hardwareM, hardwareA, len, time);
	}
	
	public static void decodeEpHexFileDownReq(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 站地址
		short stationAddr = (short) ByteBufferUtil.readUB2(byteBuffer);

		EqVersionInfo versionInfo = new EqVersionInfo();
		// 固件型号
		byte[] firmwarecode = ByteBufferUtil.readWithLength(byteBuffer, 8);

		String firmwareNumber = StringUtil.getAscii(firmwarecode);
		if (firmwareNumber.compareTo(" ") == 0) {
			logger.error("[upgrade],handleEpHexFileDownReq fail,firmwareNumber error:{}",WmIce104Util.ConvertHex(msg, 1));
			return;
		}
		versionInfo.setSoftNumber(firmwareNumber);
		// 固件主版本号
		int firmwareM = byteBuffer.get();
		versionInfo.setSoftM(firmwareM);
		// 固件副版本号
		int firmwareA = byteBuffer.get();
		versionInfo.setSoftA(firmwareA);
		// 固件编译版本号
		int firmwareC = ByteBufferUtil.readUB2(byteBuffer);
		versionInfo.setSoftC(firmwareC);
		versionInfo.setSoftVersion(firmwareM + "." + firmwareA + "."
				+ firmwareC);
		// 段索引
		int SectionIndex = ByteBufferUtil.readUB2(byteBuffer);

		// 分段字节大小
		int len = ByteBufferUtil.readUB2(byteBuffer);

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		logger.debug("[upgrade],handleEpHexFileDownReq,epCode:" + epCode
				+ ",stationAddr:" + stationAddr + "," + firmwareNumber + ",V"
				+ firmwareM + "." + firmwareA + "." + firmwareC
				+ ",SectionIndex:" + SectionIndex);

		EqVersionService.handleEpHexFileDownReq(epCommClient, epCode,
				stationAddr, SectionIndex, versionInfo, len, time);
	}
	
	public static void decodeStatReq(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNum = byteBuffer.get();
		int totalTime = 0;
		int totalCount = 0;
		int totalDl = 0;

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		for (int i = 0; i < epGunNum; i++) {
			totalTime = totalTime + ByteBufferUtil.readInt(byteBuffer);
			totalCount = totalCount + ByteBufferUtil.readInt(byteBuffer);
			totalDl = totalDl + ByteBufferUtil.readInt(byteBuffer);
		}

		EpService.handleStatReq(epCommClient, epCode, totalTime, totalCount, totalDl,time);

	}
	
	public static void decodeCommSignal(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		short stationAddr = (short) ByteBufferUtil.readUB2(byteBuffer);
		int signal = byteBuffer.get();
		
		if (epCommClient.getVersion() >= YXCConstants.PROTOCOL_VERSION_V4) {
			
			byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
			logger.info("M_COMM_SIGNAL_RET,epCode:{},hour:{},min:{},sec:{}", new Object[]{epCode,
					time[0],time[1],time[2]});
		}

		if (stationAddr > 0) {
		} else {
			EpService.handleCommSignal(epCode, signal);
		}
	}
	
	public static void decodeEpIdentyCodeQuery(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		int hour = 0;
		int min = 0;
		int sec = 0;
		// if(epCommClient.getCommVersion()>=
		// YXCConstants.PROTOCOL_VERSION_V4)
		{
			hour = byteBuffer.get();
			min = byteBuffer.get();
			sec = byteBuffer.get();
		}

		EpService.handleEpIdentyCodeQuery(epCommClient, epCode, epGunNo, hour,
				min, sec);
	}
	public static void decodeLockGunFailWaring(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 枪口编号
		int epGunNo = byteBuffer.get();
		// 失败原因
		byte error = byteBuffer.get();

	}
	public static void decodeEpDevices(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();

		int isSupportGunLock = byteBuffer.get();
		int isSupportGunSit = byteBuffer.get();
		int isSupportBmsComm = byteBuffer.get();
		int isSupportCarPlace = byteBuffer.get();

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		EpService.handleEpDevices(epCommClient, epCode, epGunNo, isSupportGunLock,
			 isSupportGunSit, isSupportBmsComm, isSupportCarPlace);
	}
	
	public static void decodeCardFronzeAmt(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		//1	终端机器编码	BCD码	8Byte	16位编码	
		//2	充电桩接口标识	BIN码	1Byte		
		//3	内卡号	BIN码	32Byte	芯片卡号,位数不足用0x00补齐	
		//4	预冻金额	BIN码	4Byte	精确到小数点后2位，倍数100	

		logger.debug("C_CARD_FRONZE_AMT:{}",WmIce104Util.ConvertHex(msg,1));

		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		//3	内卡号	ASCII码	32Byte	长度不够,用0x00在尾部补齐	
		byte[] bCardInnerNo = ByteBufferUtil.readWithLength(byteBuffer,32);
		String  cardInnerNo = StringUtil.getCString(bCardInnerNo);
		
		int nFronzeAmt =  ByteBufferUtil.readInt(byteBuffer);
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

	    bizExecutorService.execute(() -> {
		    try {
			    String currentThreadName = Thread.currentThread().getName();
			    Thread.currentThread().setName("handleCardFronzeAmt");
			    EpChargeService.handleCardFronzeAmt(epCommClient,epCode,epGunNo,cardInnerNo,nFronzeAmt,time);
			    Thread.currentThread().setName(currentThreadName);
		    } catch (Exception e) {
			    logger.error("handleCardFronzeAmt e:{}, epCode:{},cardInnerNo:{}, nFronzeAmt:{}",new Object[]{e,epCode,cardInnerNo,nFronzeAmt});
		    }
	    });

    }

	public static void decodeVinFronzeAmt(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
	{
		//1	终端机器编码	BCD码	8Byte	16位编码
		//2	充电桩接口标识	BIN码	1Byte
		//3	内卡号	BIN码	32Byte	芯片卡号,位数不足用0x00补齐
		//4	预冻金额	BIN码	4Byte	精确到小数点后2位，倍数100

		logger.debug("C_VIN_FRONZE_AMT:{}",WmIce104Util.ConvertHex(msg,1));

		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int epGunNo = byteBuffer.get();
		//3	VIN码	17Byte	长度不够,用0x00在尾部补齐
		byte[] bVinCode = ByteBufferUtil.readWithLength(byteBuffer,17);

		String carVinCode = StringUtil.getByteString(WmIce104Util.removeFFAndO(bVinCode));

		int nFronzeAmt =  ByteBufferUtil.readInt(byteBuffer);

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		//EpChargeService.handleVinFronzeAmt(epCommClient, epCode,epGunNo,carVinCode,nFronzeAmt,time);
		bizExecutorService.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("handleVinFronzeAmt");
				EpChargeService.handleVinFronzeAmt(epCommClient, epCode, epGunNo, carVinCode, nFronzeAmt, time);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("handleVinFronzeAmt e:{},epCode:{},carVinCode:{}, nFronzeAmt:{}", new Object[]{e,epCode, carVinCode, nFronzeAmt});
			}
		});


	}

	public static void decodeUserCardAuth(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		logger.debug("M_CARD_AUTH:{}"+WmIce104Util.ConvertHex(msg,1));
		
		//1	终端机器编码	BCD码	8Byte	16位编码	
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		//2	充电桩接口标识	BIN码	1Byte	从1开始	
		int epGunNo = byteBuffer.get();
		//3	内卡号	ASCII码	32Byte	长度不够,用0x00在尾部补齐	
		byte[] bCardInnerNo = ByteBufferUtil.readWithLength(byteBuffer,32);
		
		String  cardInnerNo = StringUtil.getCString(bCardInnerNo);
		
		
		byte[] bCardPasswordMd5 = ByteBufferUtil.readWithLength(byteBuffer,32);
		
		String cardPasswordMd5 = new String(bCardPasswordMd5);
		
		int userOrigin = byteBuffer.get();
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		//UserService.handleUserCardAuth(epCommClient,epCode,epGunNo,cardInnerNo,cardPasswordMd5,userOrigin,time);
	    bizExecutorService.execute(() -> {
		    try {
			    String currentThreadName = Thread.currentThread().getName();
			    Thread.currentThread().setName("decodeUserCardAuth");
			    UserService.handleUserCardAuth(epCommClient, epCode, epGunNo, cardInnerNo, cardPasswordMd5, userOrigin, time);
			    Thread.currentThread().setName(currentThreadName);
		    } catch (Exception e) {
			    logger.error("decodeUserCardAuth e:{},epCode:{},carVinCode:{}", new Object[]{e,epCode, cardInnerNo});
		    }
	    });

    }

	public static void decodeVINAuth(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
    {
		logger.debug("M_VIN_AUTH:{}"+WmIce104Util.ConvertHex(msg,1));

		//1	终端机器编码	BCD码	8Byte	16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		//2	充电桩接口标识	BIN码	1Byte	从1开始
		int epGunNo = byteBuffer.get();
		//3	内卡号	ASCII码	32Byte	长度不够,用0x00在尾部补齐
		byte[] bCardInnerNo = ByteBufferUtil.readWithLength(byteBuffer,32);

		String  cardInnerNo = StringUtil.getCString(bCardInnerNo);

		byte[] bCardPasswordMd5 = ByteBufferUtil.readWithLength(byteBuffer,32);

		String cardPasswordMd5 = new String(bCardPasswordMd5);

		int userOrigin = byteBuffer.get();

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		//UserService.handleUserCardAuth(epCommClient,epCode,epGunNo,cardInnerNo,cardPasswordMd5,1,time);
	    bizExecutorService.execute(() -> {
		    try {
			    String currentThreadName = Thread.currentThread().getName();
			    Thread.currentThread().setName("decodeVINAuth");
			    UserService.handleUserCardAuth(epCommClient, epCode, epGunNo, cardInnerNo, cardPasswordMd5, 1, time);
			    Thread.currentThread().setName(currentThreadName);
		    } catch (Exception e) {
			    logger.error("decodeVINAuth epCode:{},carVinCode:{}", new Object[]{epCode, cardInnerNo});
			    e.printStackTrace();
		    }
	    });
    }

    // 车辆VIN 码鉴权
	public static void decodeCARVINAuth(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException
	{
		logger.debug("M_CARVIN_AUTH:{}"+WmIce104Util.ConvertHex(msg,1));

		//1	终端机器编码	BCD码	8Byte	16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		//2	充电桩接口标识	BIN码	1Byte	从1开始
		int epGunNo = byteBuffer.get();
		//3	车辆VIN码
		byte[] bVinCode = ByteBufferUtil.readWithLength(byteBuffer,17);

		String carVinCode = StringUtil.getByteString(WmIce104Util.removeFFAndO(bVinCode));

		int userOrigin = byteBuffer.get();

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);

		//UserService.handleCarVin(epCommClient,epCode,epGunNo,carVinCode,time);
		bizExecutorService.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("handleCarVin");
				UserService.handleCarVin(epCommClient, epCode, epGunNo, carVinCode, time);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("handleCarVin epCode:{},carVinCode:{}", new Object[]{epCode, carVinCode});
				e.printStackTrace();
			}
		});
	}

	public static void decodeVersionAck(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 站地址
		short stationAddr = (short) ByteBufferUtil.readUB2(byteBuffer);
		// 产品型号
		byte[] productcode = ByteBufferUtil.readWithLength(byteBuffer, 20);
		String productModule = StringUtil.getCString(productcode);

		// 硬件/固件数量
		int num = byteBuffer.get();
		Vector vecInfo = new Vector(num);
		for (int i = 0; i < num; i++) {
			EqVersionInfo versionInfo = new EqVersionInfo();
			// 硬件1型号
			byte[] hardwarecode = ByteBufferUtil.readWithLength(byteBuffer, 10);
			// String hardwareNumber = StringUtil.getCString(hardwarecode);
			String hardwareNumber = StringUtil.getAscii(hardwarecode);
			if (hardwareNumber.compareTo(" ") == 0) {
				logger.error("[upgrade],response of query ep version,hardwareNumber error:{}",WmIce104Util.ConvertHex(msg, 1));
				continue;
			}
			versionInfo.setHardwareNumber(hardwareNumber);
			// 硬件1主版本号
			int hardwareM = byteBuffer.get();
			versionInfo.setHardwareM(hardwareM);
			// 硬件1子版本号
			int hardwareA = byteBuffer.get();
			versionInfo.setHardwareA(hardwareA);
			versionInfo.setHardwareVersion(hardwareM + "." + hardwareA);
			logger.debug("[upgrade],response of query ep version, harmwareNumber:{},hardwareVer:{}",
					hardwareNumber, versionInfo.getHardwareVersion());
			// 固件1名称
			byte[] firmwarecode = ByteBufferUtil.readWithLength(byteBuffer, 8);
			String firmwareNumber = StringUtil.getCString(firmwarecode);
			versionInfo.setSoftNumber(firmwareNumber);
			// 固件1主版本号
			int firmwareM = byteBuffer.get();
			versionInfo.setSoftM(firmwareM);

			// 固件1副版本号
			int firmwareA = byteBuffer.get();
			versionInfo.setSoftA(firmwareA);
			// 固件1编译版本号

			int firmwareC = ByteBufferUtil.readUB2(byteBuffer);
			versionInfo.setSoftC(firmwareC);

			versionInfo.setSoftVersion(firmwareM + "." + firmwareA + "." + firmwareC);

			vecInfo.add(versionInfo);
		}
		EqVersionService.handleVersionAck(epCommClient, epCode, stationAddr, vecInfo);
	}
	
	public static void decodeUpdateAck(EpCommClient epCommClient,ByteBuffer byteBuffer,byte[] msg) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		// 站地址
		short stationAddr = (short) ByteBufferUtil.readUB2(byteBuffer);
		// 硬件1型号
		byte[] hardwarecode = ByteBufferUtil.readWithLength(byteBuffer, 10);
		// String hardwareNumber = StringUtil.getCString(hardwarecode);

		String hardwareNumber = StringUtil.getAscii(hardwarecode);
		if (hardwareNumber.compareTo(" ") == 0) {
			logger.error("[upgrade],response of update Over fail, hardwareNumber error:{}",WmIce104Util.ConvertHex(msg, 1));
			return;
		}

		EqVersionInfo versionInfo = new EqVersionInfo();
		versionInfo.setHardwareNumber(hardwareNumber);

		// 硬件主版本号
		int hardwareM = byteBuffer.get();
		// 硬件子版本号
		int hardwareA = byteBuffer.get();

		versionInfo.setHardwareVersion(hardwareM + "." + hardwareA);

		byte[] softcode = ByteBufferUtil.readWithLength(byteBuffer, 8);
		String softNumber = StringUtil.getCString(softcode);
		versionInfo.setSoftNumber(softNumber);

		// 固件1主版本号
		int firmwareM = byteBuffer.get();
		// 固件1副版本号
		int firmwareA = byteBuffer.get();
		// 固件1编译版本号
		int firmwareC = ByteBufferUtil.readUB2(byteBuffer);
		// 更新成功标识
		int updateFlag = byteBuffer.get();

		if (updateFlag != 1) {
			logger.info(
					"[upgrade],response of update Over fail epCode:{},stationAddr:{},hardwareNumber:{},hardwareVersion:{},updateFlag:{}",
					new Object[] { epCode, stationAddr, hardwareNumber,
							versionInfo.getHardwareVersion(), updateFlag });

		} else {
			logger.info(
					"[upgrade],response of update Over success epCode:{},stationAddr:{},hardwareNumber:{},hardwareVersion:{},updateFlag:{}",
					new Object[] { epCode, stationAddr, hardwareNumber,
							versionInfo.getHardwareVersion(), updateFlag });

			versionInfo.setSoftVersion(firmwareM + "." + firmwareA + "."
					+ firmwareC);
			EqVersionService.handleUpdateAck(stationAddr, epCode, versionInfo);

		}
    }
	
	public static void decodeConcentroterSetEPRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		  
		int stationAddr = (int)ByteBufferUtil.readUB2(byteBuffer);
		int successFlag = (int)byteBuffer.get();
		int error = (int)byteBuffer.get();
	    
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		logger.info("M_CONCENTROTER_SET_EP_RET,stationAddr:{},successFlag:{}",stationAddr,successFlag);
     
    }
	
	public static void decodeConcentroterGetEPRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		int stationAddr = (int)ByteBufferUtil.readUB2(byteBuffer);
		int epCount = (int)ByteBufferUtil.readUB2(byteBuffer);
		String epCodes="";
		for(int i=0;i<epCount;i++)
		{
			String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
			epCodes +=epCode+",";
		}
	    
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		logger.info("M_CONCENTROTER_GET_EP_RET,stationAddr:{},epCodes:{}",stationAddr,epCodes);
    }
	
	public static void decodeGetConsumeModelRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		//1.电桩编号
 	   String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
 	   RateInfo rateInfo = new RateInfo();
 	   //2.费率ID
 	   rateInfo.setId((int)byteBuffer.get());
 	   //3.生效日期
 	   byte[] timeData=ByteBufferUtil.readCP56Time2a(byteBuffer);
 	   String beginTime= DateUtil.StringYourDate(DateUtil.toDate(WmIce104Util.getP56Time2aTime(timeData)*1000));
 	   //4.失效日期
 	   timeData=ByteBufferUtil.readCP56Time2a(byteBuffer);
 	   String endTime= DateUtil.StringYourDate(DateUtil.toDate(WmIce104Util.getP56Time2aTime(timeData)*1000));
   	    //5预冻结金额
 	   int nFroneAmt = (int)ByteBufferUtil.readUB2(byteBuffer);
 	   rateInfo.setFreezingMoney(NumUtil.intToBigDecimal2(nFroneAmt));
 	   //6最小冻结金额
 	   nFroneAmt = (int)ByteBufferUtil.readUB2(byteBuffer);
 	   rateInfo.setMinFreezingMoney( NumUtil.intToBigDecimal2(nFroneAmt));
 	   int num = (int)byteBuffer.get();
 	   String quantumDate="";
 	   for(int i=0;i<num;i++)
 	   {
 		   HashMap<String,Object>  dataMap=new HashMap<String,Object>();
 		   dataMap.put("st", (int)ByteBufferUtil.readInt(byteBuffer));
 		   dataMap.put("et", (int)ByteBufferUtil.readInt(byteBuffer));
 		   dataMap.put("mark", (int)byteBuffer.get());
 		   JSONObject jsonObject = JSONObject.fromObject(dataMap);
 		   quantumDate=quantumDate+jsonObject.toString()+",";
 	   }
 	   rateInfo.setQuantumDate(quantumDate);
 	   rateInfo.setJ_Rate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
 	   rateInfo.setF_Rate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
 	   rateInfo.setP_Rate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
 	   rateInfo.setG_Rate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
 	   rateInfo.setBespokeRate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
 	   rateInfo.setServiceRate(NumUtil.intToBigDecimal3((int)ByteBufferUtil.readInt(byteBuffer)));
	   rateInfo.setWarnAmt(NumUtil.intToBigDecimal2((int) ByteBufferUtil
				.readInt(byteBuffer)));

		

		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		logger.info("M_GET_CONSUME_MODEL_RET,epCodes:{},rateInfo:{}", epCode,
				rateInfo.toString());
	}
	
	public static void decodeGetFlashRamRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		int stationAddr = (int)ByteBufferUtil.readUB2(byteBuffer);
		int type = (int)ByteBufferUtil.readUB2(byteBuffer);
		int startPos = (int)ByteBufferUtil.readInt(byteBuffer);
		int len = (int)ByteBufferUtil.readUB2(byteBuffer);
		byte []data=ByteBufferUtil.readWithLength(byteBuffer);
		
		Logger log = LoggerFactory.getLogger("epRamLog");

		log.info("epCode:{},stationAddr:{},type:{},startPos:{},len:{},data:{}",
				new Object[]{epCode,stationAddr,type,startPos,len,data});
	   
		//logger.info("M_GET_FLASH_RAM_RET,stationAddr:{},epCode:{}",stationAddr,epCode);
    }
	
	
	private static byte[] handleMsgTime(int version,ByteBuffer byteBuffer) throws IOException
	{
		int hour = 0;
		int min = 0;
		int sec = 0;
		if (version >= YXCConstants.PROTOCOL_VERSION_V4) {
			hour = byteBuffer.get();
			min = byteBuffer.get();
			sec = byteBuffer.get();
		}
		
		byte []time = WmIce104Util.timeToByte(hour,min,sec);
		return time;
	}
	
	public static void decodeSetTempChargeRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		
		int successFlag = (int)byteBuffer.get();
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		if(successFlag==1) //成功
		{
			String messagekey = String.format("%03d", Iec104Constant.C_SET_TEMPCHARGE_NUM);
			EpCommClientService.removeRepeatMsg(messagekey);
		}
		logger.info("[tempCharge]setTempChargeRet, epCode:{},successFlag：{}",
				new Object[]{epCode,successFlag});
	   
    }
	
	public static void decodeGetTempChargeRet(EpCommClient epCommClient,ByteBuffer byteBuffer) throws IOException 
    {
		// 1 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
		
		int maxNum = (int)byteBuffer.get();
		
		byte[] time = handleMsgTime(epCommClient.getVersion(), byteBuffer);
		
		logger.info("[tempCharge] getTempChargeRet, epCode:{},maxNum：{}",
				new Object[]{epCode,maxNum});
		
		EpService.handleTempChargeRet(epCode, maxNum);
    }
	
	/**
	* <p>Title: 解码电桩定时充电事件上行数据</p>
	* <p>Description: </p>
	* @author bingo
	* @date 2017-5-12下午1:55:49
	 */
	public static void decodeSetTimingChargeRet(EpCommClient epCommClient,
			ByteBuffer byteBuffer) throws IOException {
		// 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);

		byte[] times = ByteBufferUtil.readWithLength(byteBuffer, 2);
		String hour = String.valueOf((int)(times[0]));
    	String min = String.valueOf((int)(times[1]));
		// 定时充电时间
		String timingChargeTime = hour + min;
		
		// 定时充电开关
		int timingChargeStatus = (int) byteBuffer.get();

		// 电桩是否成功设置定时充电
		int successFlag = (int) byteBuffer.get();
		
		//错误代码
		byte[] codes = ByteBufferUtil.readWithLength(byteBuffer, 2);
		String errorCode = String.valueOf((int)(codes[0])) + String.valueOf((int)(codes[1]));

		logger.info(LogUtil.addExtLog("[timingCharge] the ElectricPile information, epCode|timingChargeTime|timingChargeStatus|successFlag|errorCode"),
				new Object[] { epCode, timingChargeTime, timingChargeStatus, successFlag, errorCode });
		
		if (successFlag != 1) {
			changeTimingCharge(epCode, timingChargeStatus, TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_FAIL);
			logger.error(LogUtil.addExtLog("[timingCharge] the ElectricPile return fail, epCode|timingChargeTime|timingChargeStatus|successFlag|errorCode"),
					new Object[] { epCode, timingChargeTime, timingChargeStatus, successFlag, errorCode });
		}else{
			changeTimingCharge(epCode, timingChargeStatus, TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_SUCCESS);
			logger.info(LogUtil.addExtLog("[timingCharge] the ElectricPile timing charge success, epCode|timingChargeTime|timingChargeStatus"),
					new Object[] {epCode, timingChargeTime, timingChargeStatus});
		}
	}

	/**
	* <p>Title: 修改定时充电下发状态</p>
	* <p>Description: </p>
	* @param epCode 桩体编号
	* @param timingChargeStatus 是否开启定时功能（0：开；1：关；）
	* @param issuedStatus 下发给电桩状态（0：未下发定时；1：已下发数据但未收到响应 ；2：下发定时成功；3：下发定时失败；）
	* @author bingo
	* @date 2017-5-16上午9:19:41
	 */
	private static void changeTimingCharge(String epCode, int timingChargeStatus, int issuedStatus) {
		TblTimingCharge qryTimingCharge = new TblTimingCharge();
		qryTimingCharge.setElpiElectricPileCode(epCode);
		qryTimingCharge.setTimingChargeStatus(timingChargeStatus);
		qryTimingCharge.setIssuedStatus(TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_UNREC);
		List<TblTimingCharge> timingChargeList = DB.timingChargeDao.findTimingCharge(qryTimingCharge);
		
		if (timingChargeList == null || timingChargeList.size() < 1) {
			logger.info(LogUtil.addExtLog("[timingCharge] the ElectricPile is null, epCode|timingChargeStatus"),
					new Object[] { epCode, timingChargeStatus });
		} else {
			TblTimingCharge resTimingCharge = timingChargeList.get(0);
			resTimingCharge.setIssuedStatus(issuedStatus);
			resTimingCharge.setUpdateDate(DateUtil.currentDate());
			int updateResult = DB.timingChargeDao.updateTimingCharge(resTimingCharge);
			
			if (updateResult < 1) {
				logger.info(LogUtil.addExtLog("[timingCharge] update timingCharge fail, epCode|timingChargeStatus|successFlag"),
						new Object[] { epCode, timingChargeStatus, TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_SUCCESS });
			}
		}
	}

	public static void decodeSetWorkArgRet(EpCommClient epCommClient,
			ByteBuffer byteBuffer) throws IOException {
		// 终端机器编码 BCD码 8Byte 16位编码
		String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);

		int num = (int) byteBuffer.get();
		int id,val;
		
		for (int i = 0; i < num; i++) {
			id = (int) byteBuffer.get();
			if (id == 2) {
				byte[] times = ByteBufferUtil.readWithLength(byteBuffer, 4);
		    	val = Integer.valueOf(String.format("%02d%02d", times[0] , times[1]));
			} else {
				val = (int) ByteBufferUtil.readInt(byteBuffer);
			}
			updateWorkArg(epCode, id, String.valueOf(val));
		}

		logger.info(LogUtil.addExtLog("epCode|num"), new Object[] {epCode, num});
	}
    public static void decodeSetEpInfoRet(EpCommClient epCommClient,
           ByteBuffer byteBuffer) throws IOException {
        String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
        int num = (int) byteBuffer.get();
        int id,length;
        String val;

        for (int i = 0; i < num; i++) {
            id = (int) byteBuffer.get();
            length = (int) byteBuffer.get();
            byte[] value=ByteBufferUtil.readWithLength(byteBuffer, length);
            val = StringUtil.getByteString(value);
            updateWorkArg(epCode,id,val);
        }

		logger.info(LogUtil.addExtLog("epCode|num"), new Object[] {epCode, num});
    }
	private static void updateWorkArg(String epCode, int id, String val) {
		ElectricpileWorkarg epWorkarg = new ElectricpileWorkarg();
		epWorkarg.setEpCode(epCode);
		epWorkarg.setArgId(id);
		epWorkarg.setArgValue(val);
		epWorkarg.setIssuedStatus(TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_SUCCESS);
		DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
	}

    public static void decodeGetEpOffLineRet(ByteBuffer byteBuffer) throws IOException {
        String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
        byte[] bStartOffTimes = ByteBufferUtil.readCP56Time2a(byteBuffer);
        byte[] bEndOffTimes = ByteBufferUtil.readCP56Time2a(byteBuffer);
        long st = WmIce104Util.getP56Time2aTime(bStartOffTimes);
        long et = WmIce104Util.getP56Time2aTime(bEndOffTimes);
        logger.info(LogUtil.addExtLog("epCode|bStartOffTimes|bEndOffTimes"), new Object[]{epCode, bStartOffTimes,bEndOffTimes});
        TblOffLineInfo tblOffLineInfo = new TblOffLineInfo();
        tblOffLineInfo.setElPiElectricPileCode(epCode);
        tblOffLineInfo.setBeginOfflineTime(DateUtil.longDateToString(st));
        tblOffLineInfo.setEndOfflineTime(DateUtil.longDateToString(et));
        tblOffLineInfo.setType(tblOffLineInfo.getType());
        DB.tblOffLineInfoDao.insertOffLineInfo(tblOffLineInfo);
    }
    // 空闲时，桩上传的错误码保存
    public static void decodeFaultCodeFromPile(ByteBuffer byteBuffer) throws IOException {
	    //1. 桩编号
	     String epCode = ByteBufferUtil.readBCDWithLength(byteBuffer, 8);
	    //2	充电桩接口标识	BIN码	1Byte
	    int epGunNo = byteBuffer.get();
	    //3. 错误码  两字节
	    int errorCode = ByteBufferUtil.readUB2(byteBuffer);

	    // 跟毛ben 讨论了下，tbl_chargingfaultrecord 这个表保存了充电时的停止原因，
	    // 这里保存的是桩空闲时的错误码 还是放在同一个表里，方便Ims 统一查询显示
	    // 这里的流水号保存的是空 ,如果空闲错误码与充电消费记录上传的一样，可以通过这个空来做区别，没有加字段来区分
	    // 还要确认的是，桩上传同一个桩同一个错误码的方式，是一直传呢还是只传一次
	    ElectricPileCache epCache = EpService.getEpByCode(epCode);

	    TblChargingfaultrecord faultRecord = new TblChargingfaultrecord();
	    faultRecord.setCfreUsingmachinecode(epCode);
	    faultRecord.setCfreElectricpileid(epCache.getPkEpId());
	    faultRecord.setCfreElectricpilename("");

	    faultRecord.setcFRe_EphNo(epGunNo);
	    faultRecord.setCfreTransactionnumber("");
	    faultRecord.setCfreChargingsarttime(new Date());
	    faultRecord.setCfreFaultcause(String.valueOf(errorCode));

	    // 新增空闲时错误码保存
	    DB.chargingfaultrecordDao.insertFaultRecord(faultRecord);

    }
}

