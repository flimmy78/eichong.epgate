package com.ec.usrcore.net.client;

import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.common.net.U2ECmdConstants;
import com.ec.usrcore.net.codec.EpGateDecoder;
import com.ec.usrcore.service.EpGateService;
import com.ec.utils.LogUtil;

public class EpGateMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateMessageHandler.class.getName()));

	/**
	 * 接受EpGate数据并处理
	 * @param channel
	 * @param message
	 * @throws IOException 
	 */
     public static void handleMessage(Channel channel, EpGateMessage message) {
		
    	 byte[] msg = message.getBytes();
    	
    	 
    	 try {
    	     processFrame(channel,message.getCmd(),msg);
    	 }catch (IOException e) {
				e.printStackTrace();
		}
	}

	public static void processFrame(Channel channel,int cmd,byte[] msg)
			throws IOException {
		logger.debug(LogUtil.addExtLog("cmd"), cmd);

		if(!EpGateService.isValidCmd(cmd))
			return ;
		ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
		
		switch (cmd) 
		{
		case U2ECmdConstants.EP_LOGIN:
		{
			EpGateDecoder.decodeLogin(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_ACK:
		{
			EpGateDecoder.decodeAck(channel, byteBuffer);
		}
		break;
		
		case U2ECmdConstants.EP_HEART://103	心跳
		{
			EpGateDecoder.decodeHeart(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_ONLINE://202	电桩在线
		{
			EpGateDecoder.decodeEpOnline(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.PHONE_ONLINE://203	手机在线
		{
			EpGateDecoder.decodeClientOnline(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.PHONE_CONNECT_INIT://1001	手机连接初始化(带部分充电逻辑)
		{
			EpGateDecoder.decodeClientConnect(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_CHARGE://1002	充电
		{
			EpGateDecoder.decodeCharge(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_CHARGE_EVENT://1003	充电事件
		{
			EpGateDecoder.decodeChargeEvent(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_STOP_CHARGE://1004	停止充电
		{
			EpGateDecoder.decodeStopCharge(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_REALINFO://1005	充电实时数据
		{
			EpGateDecoder.decodeChargeReal(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_CONSUME_RECODE://1006	消费记录
		{
			EpGateDecoder.decodeConsumeRecord(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_GUN_CAR_STATUS://1007	枪与车连接状态变化通知
		{
			EpGateDecoder.decodeStatusChangeEvent(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.CCZC_QUERY_ORDER://1008	订单详情查询
		{
			EpGateDecoder.decodeOrderInfo(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_GUN_WORK_STATUS://1009	枪工作状态变化通知
		{
			EpGateDecoder.decodeWorkStatusEvent(channel, byteBuffer);
		}
		break;
		
		default:
		break;
		}

	}
}

