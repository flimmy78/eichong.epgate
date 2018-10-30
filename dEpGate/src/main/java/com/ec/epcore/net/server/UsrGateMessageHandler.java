package com.ec.epcore.net.server;

import com.ec.common.net.U2ECmdConstants;
import com.ec.epcore.net.codec.UsrGateDecoder;
import com.ec.epcore.service.UsrGateService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.ec.epcore.net.server.EpMessageHandler.bizExecutorService;


/**
 * 接受客户端数据并处理
 * @author lwz
 * 2015.10.11
 */
public class UsrGateMessageHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(UsrGateMessageHandler.class);
	
	
	public static void processFrame(Channel channel,int cmd,byte[] msg)
			throws IOException {
		
		if(!UsrGateService.isValidCmd(channel,cmd))
			return ;
	
		ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
		
		
		try{
		switch(cmd)
		{
		case U2ECmdConstants.EP_LOGIN://101;//网关登录程序
			UsrGateDecoder.decodeLogin(channel,byteBuffer);
			break;
		case U2ECmdConstants.EP_ACK://102	ACK响应
			UsrGateDecoder.decodeAck(channel,byteBuffer);
			break;
		case U2ECmdConstants.EP_HEART://103	心跳
			UsrGateDecoder.decodeHeart(channel,byteBuffer);
			
			break;
		case U2ECmdConstants.EP_ONLINE://202	电桩在线
			UsrGateDecoder.deEpOnline(channel,byteBuffer);
			
			break;
		case U2ECmdConstants.PHONE_ONLINE://203	手机在线

			bizExecutorService.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.decodePhoneOnline");
					UsrGateDecoder.decodePhoneOnline(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("UsrGateDecoder.decodePhoneOnline:{}", e);
				}
			});
			break;
		case U2ECmdConstants.PHONE_CONNECT_INIT://1001	手机连接初始化(带部分充电逻辑)
			bizExecutorService.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.decodeCharge");
					UsrGateDecoder.decodePhoneInit(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("UsrGateDecoder.decodeCharge:{}", e);
				}
			});
			
			break;
		case U2ECmdConstants.EP_CHARGE://1002	充电
//			UsrGateDecoder.decodeCharge(channel,byteBuffer);
			bizExecutorService.execute(()->{
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.decodeCharge");
					UsrGateDecoder.decodeCharge(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				}catch (Exception e){
					logger.error("UsrGateDecoder.decodeCharge:{}",e);
				}
			});

			break;
		/*case EpGateConstant.EP_CHARGE_EVENT://1003	充电事件
			UsrGateDecoder.decodeChargeEvent(channel,byteBuffer);
			
			break;*/
		case U2ECmdConstants.EP_STOP_CHARGE://1004	停止充电
			bizExecutorService.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.decodeStopCharge");
					UsrGateDecoder.decodeStopCharge(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("UsrGateDecoder.decodeStopCharge:{}", e);
				}
			});
			break;
		//case EpGateConstant.EP_CONSUME_RECODE://= 1005;//1005	消费记录
		//	UsrGateService.handleConsumeRecord(channel,byteBuffer);
			
		//	break;
		case U2ECmdConstants.CCZC_QUERY_ORDER://= 1008;//1008	操专车查询订单
			bizExecutorService.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.deQueryOrder");
					UsrGateDecoder.deQueryOrder(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("UsrGateDecoder.deQueryOrder:{}", e);
				}
			});
			break;
		case U2ECmdConstants.EP_4COMMON_REALDATA: //1204  通用实时数据

			bizExecutorService.execute(() -> {
				try {
					String currentThreadName = Thread.currentThread().getName();
					Thread.currentThread().setName("UsrGateDecoder.deQueryData4Common");
					UsrGateDecoder.deQueryData4Common(channel, byteBuffer);
					Thread.currentThread().setName(currentThreadName);
				} catch (Exception e) {
					logger.error("UsrGateDecoder.deQueryData4Common:{}", e);
				}
			});
			break;
		
		default:
			
			break;
		}
		}catch (Exception e) {
			logger.error("processFrame exception,channel:{}"+channel.toString());
		}
	}
	
	public static void handleMessage(Channel channel, UsrGateMessage message) throws  IOException
	{
		UsrGateClient usrGate = UsrGateService.getClient(channel);
		if(usrGate==null)
		{
			logger.error("handleMessage error! not find PhoneClient:"+channel.toString());
			return ;
		}
		
		byte[] msg = message.getBytes();
    	
    	 
    	 try {
    	     processFrame(channel,message.getCmd(),msg);
    	 }catch (IOException e) {
				e.printStackTrace();
		}
	}
}
   

