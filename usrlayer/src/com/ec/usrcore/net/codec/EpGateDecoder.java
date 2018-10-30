package com.ec.usrcore.net.codec;

import com.ec.common.net.U2ECmdConstants;
import com.ec.constants.UserConstants;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.util.ByteUtil;
import com.ec.usrcore.net.client.EpGateMessage;
import com.ec.usrcore.service.EpGateService;
import com.ec.utils.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ec.usrcore.net.client.EpGateMessageHandler.bizExecutor4Html;

/**
 * 收消息，解码   
 * 
 * 消息结构：2字节协议头+ 2字节长度 (小端)+ 1字节原因+2字节命令类型  + byte[] 数据内容

 */
public class EpGateDecoder extends ByteToMessageDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateDecoder.class.getName()));
	
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext,
			ByteBuf byteBuf, List<Object> list) throws Exception {
		
		int readableBytes= byteBuf.readableBytes();
		//协议头6个字节,如果没有6个字节不读
		if(readableBytes < 6)
		{
			logger.debug(LogUtil.addExtLog("1 readableBytes<6,readableBytes|channel"), readableBytes,channelHandlerContext.channel());
			return;
		}
		
		int pos= byteBuf.bytesBefore(U2ECmdConstants.HEAD_FLAG1);//找到的位置
		int pos1= byteBuf.bytesBefore(U2ECmdConstants.HEAD_FLAG2);//找到的位置
		int discardLen=0;
		if(pos < 0 || pos1<0 || (pos1-pos)!=1)//没找到，全部读掉
		{
			discardLen = readableBytes;
			logger.debug(LogUtil.addExtLog("not find flag header 0x45 0x43,readableBytes|channel"),readableBytes,channelHandlerContext.channel());
		}
		if(pos>0&&(pos1-pos)==1)
		{
			discardLen = pos;	
			logger.debug(LogUtil.addExtLog("find flag header 0x45 0x43,pos|channel"),pos,channelHandlerContext.channel());
		}
		if(discardLen>0)
		{
			byte[] dicardBytes= new byte[discardLen];
			byteBuf.readBytes(dicardBytes);//
			
			logger.debug(LogUtil.addExtLog("discard>0 msg|channel"),WmIce104Util.ConvertHex(dicardBytes, 0),channelHandlerContext.channel());
			
			if(discardLen == readableBytes)
			{
				//没有数据可对，还回
				return;
			}
		}
		
		readableBytes= byteBuf.readableBytes();
		if(readableBytes<6)
		{
			logger.debug(LogUtil.addExtLog("readableBytes<6 readableBytes|channel"),readableBytes,channelHandlerContext.channel());
			
			return;
		}
		
		//1、先标记读索引（必须）
		byteBuf.markReaderIndex();	
		
		//读取协议头
		byteBuf.readShort();
		//读长度
		int msg_len = byteBuf.readShort();
		int remain_len = byteBuf.readableBytes();

		if(remain_len<msg_len )
		{
			logger.debug(LogUtil.addExtLog("ep remain_len<msg_len,remain_len|channel"), remain_len, channelHandlerContext.channel());
			
			byteBuf.resetReaderIndex();
			return ;
		}
	    
		int cmd = byteBuf.readShort();
		
		byte Msg[]= null;
		Msg= new byte[msg_len-2];
    	byteBuf.readBytes(Msg);
    
    	EpGateMessage message = new EpGateMessage();
    			
    	message.setLength(msg_len);
    	message.setCmd(cmd);

    	message.setBytes(Msg);
    			
    	list.add(message);
	}
	
	/**
	 * 网关登录(连接初始化)
	 */
	public static void decodeLogin(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		int usrGateId = byteBuffer.getInt();
		int epGateId = byteBuffer.getInt();
		int ret = (int)byteBuffer.get();
		short errorCode = byteBuffer.getShort();
		EpGateService.handleEpGateLogin(channel, h,m,s,usrGateId,epGateId,ret,errorCode);
	}
	
	/**
	 * 心跳
	 */
	public static void decodeHeart(Channel channel,ByteBuffer byteBuffer)
	{
		EpGateService.handleHeart(channel);
	}
	
	/**
	 * ACK响应
	 */
	public static void decodeAck(Channel channel,ByteBuffer byteBuffer)
	{
		short cmd  = byteBuffer.getShort();
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		long usrId = byteBuffer.getLong();
		
		EpGateService.handleAck(channel,cmd,h,m,s,usrId);
	}

	/**
	 * 电桩在线
	 */
	public static void decodeEpOnline(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		int online = (int)byteBuffer.get();
		int epNum = byteBuffer.getShort();
		
		String[] epCode = new String[epNum];
		for(int i=0;i<epNum;i++)
		{
			epCode[i] = ByteUtil.getString(byteBuffer);
		}
		
		EpGateService.handleEpOnline(channel, h,m,s,online,epCode);	
	}

	/**
	 * 链路在线
	 */
	public static void decodeClientOnline(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		EpGateService.handleClientOnline(channel, h,m,s);
	}

	/**
	 * 客户端连接初始化
	 */
	public static void decodeClientConnect(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		long usrId = byteBuffer.getLong();

		int ret = (int)byteBuffer.get();
		short errorCode = byteBuffer.getShort();
		int status = (int)byteBuffer.get();
		bizExecutor4Html.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("usrlayer-decodeClientConnect");
				EpGateService.handleClientConnect(channel, h, m, s, epCode, epGunNo, usrId, ret, errorCode, status);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("decodeClientConnect e:{},epCode:{},usrId:{}", new Object[]{e,epCode, usrId});

			}
		});
	}

	/**
	 * 充电
	 */
	public static void decodeCharge(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);

		int ret = (int)byteBuffer.get();
		short errorCode = byteBuffer.getShort();

		bizExecutor4Html.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("usrlayer-decodeCharge");
				EpGateService.handleCharge(channel, h, m, s, epCode, epGunNo, orgNo, userIdentity, token, ret, errorCode);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("decodeCharge e:{},epCode:{},orgNo:{},token:{},userIdentity:{}", new Object[]{e, epCode, orgNo, token, userIdentity});
			}
		});
	}
	
	/**
	 * 充电事件
	 */
	public static void decodeChargeEvent(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		
		int status = (int)byteBuffer.get();

		bizExecutor4Html.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("usrlayer-decodeChargeEvent");
				EpGateService.handleChargeEvent(channel, h, m, s, epCode, epGunNo, orgNo, userIdentity, token, status);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("decodeChargeEvent e:{},epCode:{},orgNo:{},token:{},userIdentity:{}", new Object[]{e, epCode, orgNo,token, userIdentity});
			}
		});

	}

	/**
	 * 停止充电
	 */
	public static void decodeStopCharge(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);

		int ret = (int)byteBuffer.get();
		short errorCode = byteBuffer.getShort();
		bizExecutor4Html.execute(() -> {
			try {
				String currentThreadName = Thread.currentThread().getName();
				Thread.currentThread().setName("usrlayer-decodeStopCharge");
				EpGateService.handleStopCharge(channel, h, m, s, epCode, epGunNo, orgNo, userIdentity, token, ret, errorCode);
				Thread.currentThread().setName(currentThreadName);
			} catch (Exception e) {
				logger.error("decodeStopCharge e:{},epCode:{},orgNo:{},token:{},userIdentity:{}", new Object[]{e, epCode, orgNo, token, userIdentity});
			}
		});

	}

	/**
	 * 订单详情查询
	 */
	public static void decodeOrderInfo(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);

		int ret = (int)byteBuffer.get();
		short errorCode = byteBuffer.getShort();

		EpGateService.handleOrderInfo(channel, h,m,s,epCode,epGunNo,orgNo,userIdentity,token,ret,errorCode);
	}

	/**
	 * 充电实时数据
	 */
	public static void decodeChargeReal(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int) byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		Map chargingInfo = getDecodeChargingInfo(byteBuffer);
		EpGateService.handleChargeReal(channel, h,m,s,epCode,epGunNo,orgNo,userIdentity,token,chargingInfo);
	}
	public static void decodeChargeReal4Html(Channel channel, ByteBuffer byteBuffer){
		int h = (int) byteBuffer.get();
		int m = (int) byteBuffer.get();
		int s = (int) byteBuffer.get();
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int) byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		//获取chargingInfo对象数据
		Map chargingInfo = getDecodeChargingInfo(byteBuffer);

		EpGateService.handleChargeReal4Html(channel, h, m, s, epCode, epGunNo, orgNo, userIdentity, token, chargingInfo);
	}
	public static Map getDecodeChargingInfo(ByteBuffer byteBuffer){

		Map<String, Object> chargingInfo = new HashMap<String, Object>();

		chargingInfo.put("workStatus", byteBuffer.get());
		chargingInfo.put("totalTime", byteBuffer.getShort());
		chargingInfo.put("outVol", byteBuffer.getShort());
		chargingInfo.put("outCurrent", byteBuffer.getShort());
		chargingInfo.put("chargeMeterNum", byteBuffer.getInt());
		chargingInfo.put("rateInfo", byteBuffer.getShort());
		chargingInfo.put("fronzeAmt", byteBuffer.getInt());
		chargingInfo.put("chargeAmt", byteBuffer.getInt());
		chargingInfo.put("soc", byteBuffer.get());
		chargingInfo.put("deviceStatus", byteBuffer.getInt());
		chargingInfo.put("warns", byteBuffer.getInt());
		chargingInfo.put("elecAmt",byteBuffer.getFloat());
		//新增开始充电时间，服务费
		chargingInfo.put("startTime",byteBuffer.getLong());
		chargingInfo.put("servicePrice",byteBuffer.getFloat());
		chargingInfo.put("totalPower", byteBuffer.getFloat());
		return chargingInfo;
	}

	/**
	 * 消费记录
	 */
	public static void decodeConsumeRecord(Channel channel,ByteBuffer byteBuffer,boolean chargeFlag)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);

		Map<String, Object> consumeRecordMap = new HashMap<String, Object>();
		consumeRecordMap.put("orderNo",ByteUtil.getString(byteBuffer));
		consumeRecordMap.put("st",byteBuffer.getLong());
		consumeRecordMap.put("et",byteBuffer.getLong());
		consumeRecordMap.put("elect",byteBuffer.getInt());
		consumeRecordMap.put("elect_money",byteBuffer.getInt());
		consumeRecordMap.put("service_money",byteBuffer.getInt());
		consumeRecordMap.put("pkEpId",byteBuffer.getInt());
		consumeRecordMap.put("New_conpon",(int)byteBuffer.get());
		consumeRecordMap.put("Conpon_face_value",byteBuffer.getInt());
		consumeRecordMap.put("Conpon_discount_value",byteBuffer.getInt());
		consumeRecordMap.put("personalAmt",byteBuffer.getInt());//个性化优惠金额
		if (chargeFlag) consumeRecordMap.put("chargeStyle",(int)byteBuffer.get());
		if (orgNo != UserConstants.ORG_I_CHARGE) {
			consumeRecordMap.put("start_elect",byteBuffer.getInt());
			consumeRecordMap.put("end_elect",byteBuffer.getInt());
			consumeRecordMap.put("cusp_elect",byteBuffer.getInt());
			consumeRecordMap.put("cusp_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("cusp_service_price",byteBuffer.getInt());
			consumeRecordMap.put("cusp_money",byteBuffer.getInt());
			consumeRecordMap.put("cusp_elect_money",byteBuffer.getInt());
			consumeRecordMap.put("cusp_service_money",byteBuffer.getInt());
			consumeRecordMap.put("peak_elect",byteBuffer.getInt());
			consumeRecordMap.put("peak_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("peak_service_price",byteBuffer.getInt());
			consumeRecordMap.put("peak_money",byteBuffer.getInt());
			consumeRecordMap.put("peak_elect_money",byteBuffer.getInt());
			consumeRecordMap.put("peak_service_money",byteBuffer.getInt());
			consumeRecordMap.put("flat_elect",byteBuffer.getInt());
			consumeRecordMap.put("flat_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("flat_service_price",byteBuffer.getInt());
			consumeRecordMap.put("flat_money",byteBuffer.getInt());
			consumeRecordMap.put("flat_elect_money",byteBuffer.getInt());
			consumeRecordMap.put("flat_service_money",byteBuffer.getInt());
			consumeRecordMap.put("valley_elect",byteBuffer.getInt());
			consumeRecordMap.put("valley_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("valley_service_price",byteBuffer.getInt());
			consumeRecordMap.put("valley_money",byteBuffer.getInt());
			consumeRecordMap.put("valley_elect_money",byteBuffer.getInt());
			consumeRecordMap.put("valley_service_money",byteBuffer.getInt());
			consumeRecordMap.put("custom_cusp_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_cusp_service_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_flat_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_flat_service_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_peak_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_peak_service_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_valley_elect_price",byteBuffer.getInt());
			consumeRecordMap.put("custom_valley_service_price",byteBuffer.getInt());
			consumeRecordMap.put("stop_reason",ByteUtil.getString(byteBuffer));
		}

		EpGateService.handleConsumeRecord(channel, h,m,s,epCode,epGunNo,orgNo,userIdentity,token,consumeRecordMap);
	}

	/**
	 * 枪与车连接状态变化通知
	 */
	public static void decodeStatusChangeEvent(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		
		int status = (int)byteBuffer.get();

		EpGateService.handleStatusChangeEvent(channel, h,m,s,epCode,epGunNo,orgNo,userIdentity,token,status);
	}

	/**
	 * 枪工作状态变化通知
	 */
	public static void decodeWorkStatusEvent(Channel channel,ByteBuffer byteBuffer)
	{
		int h = (int)byteBuffer.get();
		int m = (int)byteBuffer.get();
		int s = (int)byteBuffer.get();
		
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int)byteBuffer.get();
		int orgNo = byteBuffer.getInt();
		String userIdentity = ByteUtil.getString(byteBuffer);
		String token = ByteUtil.getString(byteBuffer);
		
		int status = (int)byteBuffer.get();
		
		EpGateService.handleWorkStatusEvent(channel, h,m,s,epCode,epGunNo,orgNo,userIdentity,token,status);
	}

	/**
	 * 1204 通用实时数据查询
	 */
	public static void decode4CommonRealDataEvent(Channel channel, ByteBuffer byteBuffer) {
		int h = (int) byteBuffer.get();
		int m = (int) byteBuffer.get();
		int s = (int) byteBuffer.get();

		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int) byteBuffer.get();

		String extra = ByteUtil.getString(byteBuffer);
		String extraData = ByteUtil.getString(byteBuffer);

		int ret = (int) byteBuffer.get();
		short errorCode = byteBuffer.getShort();

		EpGateService.handle4CommonRealDataEvent(channel, h, m, s, epCode, epGunNo, extra, extraData, ret,errorCode);
	}

	/**
	 * 1206 枪状态变化推送 全国
	 */
	public static void decodeGunStatusChangeData4Html(Channel channel, ByteBuffer byteBuffer) {
		int h = (int) byteBuffer.get();
		int m = (int) byteBuffer.get();
		int s = (int) byteBuffer.get();

		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo = (int) byteBuffer.get();
		int currentType = (int) byteBuffer.get();
		String realDataMap = ByteUtil.getString(byteBuffer);

		EpGateService.handleGunStatusChangeData4Html(channel, h, m, s, epCode, epGunNo, currentType, realDataMap);
	}



}
