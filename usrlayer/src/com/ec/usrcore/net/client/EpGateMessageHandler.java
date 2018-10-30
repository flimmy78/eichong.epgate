package com.ec.usrcore.net.client;

import com.ec.common.net.U2ECmdConstants;
import com.ec.usrcore.net.codec.EpGateDecoder;
import com.ec.usrcore.service.EpGateService;
import com.ec.utils.LogUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EpGateMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGateMessageHandler.class.getName()));

	public static BlockingQueue<Runnable> workQueue4HtmlBizExecutor = new LinkedBlockingQueue<>(10000);
	public static ThreadPoolExecutor bizExecutor4Html = new ThreadPoolExecutor(150, 200, 30L, TimeUnit.SECONDS, workQueue4HtmlBizExecutor, new ThreadPoolExecutor.CallerRunsPolicy());

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

		if(!EpGateService.isValidCmd(cmd))
			return ;
		ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
		int msgLen = msg.length;
		logger.debug(LogUtil.addExtLog("cmd|msgLen"), cmd,msgLen);

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
			//EpGateDecoder.decodeChargeReal(channel, byteBuffer);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decodeChargeReal");
					EpGateDecoder.decodeChargeReal(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decodeChargeReal exception:{}", e);
				}
			});
		}
		break;
		case U2ECmdConstants.EP_REALINFO_4HTML://1205	 给所有html 其他渠道发起的充电实时数据 @hm
		{
			//EpGateDecoder.decodeChargeReal4Html(channel, byteBuffer);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decodeChargeReal4Html");
					EpGateDecoder.decodeChargeReal4Html(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decodeChargeReal4Html exception:{}", e);
				}
			});
		}
		break;
		case U2ECmdConstants.EP_CONSUME_RECODE://1006	消费记录
		{
//			boolean chargeFlag = true;
//			if (msgLen <= 103 || msgLen > 200) chargeFlag = false;
//
//			final boolean finalChargeFlag = chargeFlag;
			//EpGateDecoder.decodeConsumeRecord(channel, byteBuffer, chargeFlag);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decodeConsumeRecord");
					EpGateDecoder.decodeConsumeRecord(channel, byteBuffer, false);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decodeConsumeRecord exception:{}", e);
				}
			});

		}
		break;
		case U2ECmdConstants.EP_GUN_CAR_STATUS://1007	枪与车连接状态变化通知
		{
			EpGateDecoder.decodeStatusChangeEvent(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.CCZC_QUERY_ORDER://1008	订单详情查询
		{
			//EpGateDecoder.decodeOrderInfo(channel, byteBuffer);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decodeOrderInfo");
					EpGateDecoder.decodeOrderInfo(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decodeOrderInfo exception:{}", e);
				}
			});
		}
		break;
		case U2ECmdConstants.EP_GUN_WORK_STATUS://1009	枪工作状态变化通知
		{
			 EpGateDecoder.decodeWorkStatusEvent(channel, byteBuffer);
		}
		break;
		case U2ECmdConstants.EP_4COMMON_REALDATA: //1204  通用实时数据
		{
			//EpGateDecoder.decode4CommonRealDataEvent(channel, byteBuffer);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decode4CommonRealDataEvent");
					EpGateDecoder.decode4CommonRealDataEvent(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decode4CommonRealDataEvent exception:{}", e);
				}
			});
		}
		break;
		case U2ECmdConstants.EP_GUN_STATUS_CHANGE_DATA: //1206 发送状态变化数据给html
		{
			//EpGateDecoder.decodeGunStatusChangeData4Html(channel, byteBuffer);
			bizExecutor4Html.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("decodeGunStatusChangeData4Html");
					EpGateDecoder.decodeGunStatusChangeData4Html(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("decodeGunStatusChangeData4Html exception:{}", e);
				}
			});
		}
		break;
		default:
		break;
		}

	}
}

