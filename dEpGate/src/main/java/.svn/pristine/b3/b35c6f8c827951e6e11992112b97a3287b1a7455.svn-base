package com.ec.epcore.service;

import io.netty.channel.Channel;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.common.net.A2ECmdConstantsAPI;
import com.ec.config.Global;
import com.ec.constants.EventConstant;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.BomListInfo;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.UserOrigin;
import com.ec.epcore.net.codec.ApiEncoder;
import com.ec.epcore.net.server.InnerApiMessageSender;
import com.ec.logs.LogConstants;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.util.ByteUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import com.ec.utils.NumUtil;

/**
 * App后台服务器相关服务
 * @author 
 * 2015-3-18 下午4:59:13
 */
public class AppApiService {

	private static final Logger logger =  LoggerFactory.getLogger(LogUtil.getLogName(AppApiService.class.getName()));
	
	/**
	 * 预约充电
	 * @author 
	 * 2015-3-18
	 * @param channel
	 * @param byteBuffer
	 */
	public static void bespoke(Channel channel,ByteBuffer byteBuffer){
		logger.info("bespoke..channel:{}", new Object[]{channel} );
		
		//预约号	
		String bespNo = ByteUtil.getString(byteBuffer);
		//电桩编号
		int pkEpId = byteBuffer.getInt();
		String epCode = ByteUtil.getString(byteBuffer);
		//预约买断时间,单位分钟
		Short buyOutTime = byteBuffer.getShort();
		//预约开始时间
		long clientBespSt = byteBuffer.getLong();
		
		//续约标致（0：第一次预约，1：续约）
		int redo = byteBuffer.getInt();
		//用户ID
		int userid=byteBuffer.getInt();
		//用户账号	
		String accountNo = ByteUtil.getString(byteBuffer);
		
		//电桩枪口
		int epGunNo=byteBuffer.getInt();
		
		//电桩枪口主键
		long pkEpGunNo=byteBuffer.getLong();
		
		int orgNo = byteBuffer.getInt();
		int payMode = byteBuffer.getInt();
		
		logger.info("bespoke accept from api accountId:{},bespNo:{},buyOutTime:{},epCode:{},epGunNo:{}",
	    		new Object[]{userid,bespNo,buyOutTime,epCode,epGunNo});
		
	    int errorCode = EpBespokeService.apiBespoke(epCode,epGunNo,pkEpId ,bespNo,buyOutTime,clientBespSt,
				 redo, userid, accountNo, pkEpGunNo,payMode, orgNo,1,NetUtils.getClientIp(channel));
	    
		
	    if(errorCode>0)
		{
	    	logger.info("bespoke fail, errorCode:{},accountId:{},bespNo:{},buyOutTime:{},epCode:{},epGunNo:{}",
	  	    		new Object[]{errorCode,userid,bespNo,buyOutTime,epCode,epGunNo});
	  	 
	    	byte[] msg = ApiEncoder.bespokeProtocol(bespNo,(short)0,errorCode,userid,(short)redo);
			
			InnerApiMessageSender.gateSendToGame(channel, A2ECmdConstantsAPI.G2A_BESPOKE, (Integer)0,  msg);
		}
	}
	
	/**
	 * 取消预约 
	 * @author 
	 * 2015-3-18
	 * @param channel
	 * @param byteBuffer
	 */
	public static void cancelBespoke(Channel channel,ByteBuffer byteBuffer){
		logger.info("cancelbespoke,channel：{}", new Object[]{channel} );
		
		long pkBespNo = byteBuffer.getLong();
		//预约号	
		String bespNo = ByteUtil.getString(byteBuffer);
		String epCode = ByteUtil.getString(byteBuffer);
		
		int epGunNo = 1;
		
		logger.info("cancelbespoke accept from api bespNo:{},epCode:{},epGunNo:{}",
	    		new Object[]{bespNo,epCode,epGunNo});
		int errorCode = EpBespokeService.apiStopBespoke(pkBespNo, bespNo, epCode,epGunNo,1,NetUtils.getClientIp(channel));
		
		logger.info("cancelBespoke errorCode:{},epCode:{},epGunNo:{},bespNo:{}", new Object[]{errorCode,epCode,epGunNo,bespNo});
		if(errorCode >0)
		{
			//logger.info("cancelbespoke apiStopBespoke errorCode:{},bespNo:{},epCode:{},epGunNo:{}",
		    //		new Object[]{errorCode,bespNo,epCode,epGunNo});
			
			byte[] msg = ApiEncoder.cancelBespokeProtocol(bespNo,(short)0,errorCode);
			InnerApiMessageSender.gateSendToGame(channel, A2ECmdConstantsAPI.G2A_CANCEL_BESPOKE, (Integer)0,  msg);
			
		}
	}
	
	
	public static void startElectricize2(Channel channel,ByteBuffer byteBuffer){
		
		logger.debug("charge api enter");
		//电桩编号
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo=byteBuffer.getInt();
		String bespNo= ByteUtil.getString(byteBuffer);
		int accountId=byteBuffer.getInt();
		String account = ByteUtil.getString(byteBuffer);
		short ermFlag = byteBuffer.getShort();
		
		double fronzeAmt =  byteBuffer.getDouble();
		
		BigDecimal bdFronzeAmt = new BigDecimal(fronzeAmt);
		bdFronzeAmt = bdFronzeAmt.multiply(Global.DecTime2);
		int nFronzeamt = NumUtil.BigDecimal2ToInt(bdFronzeAmt);
	

		int orgNo = byteBuffer.getInt();
		int payMode = byteBuffer.getInt();
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		
		logger.info("charge accept from api accountId:{},account:{},chargeStyle:{},epCode:{},epGunNo:{},fronzeAmt:{},nFronzeamt:{}",
				new Object[]{accountId,account,ermFlag,epCode,epGunNo,fronzeAmt,nFronzeamt});
		
		int errorCode=0;
		
		//String epCode,int epGunNo,String bespNo,
		//int accountId,String account,short ermFlag,
		//double fronzeAmt ,int orgNo,int payMode,String clientIp
		
		errorCode = EpChargeService.appApiStartElectric(epCode,epGunNo,bespNo,accountId,account,
				ermFlag,fronzeAmt,orgNo,payMode,NetUtils.getClientIp(channel));
		
		
		if(errorCode>0)
		{
			logger.info("charge apiStartElectric fail errorCode:{} accountId:{},account:{},chargeStyle:{},epCode:{},epGunNo:{} to api",
					new Object[]{errorCode,accountId,account,ermFlag,epCode,epGunNo});
			
			byte[] msg =ApiEncoder.startElectricizeProtocol(epCode,epGunNo,(short)0,errorCode);
			InnerApiMessageSender.gateSendToGame(channel, A2ECmdConstantsAPI.G2A_START_ELECTRICIZE, (Integer)0,  msg);
		}
	}
	
	/**
	 * 停止充电
	 * @param byteBuffer
	 */
	public static void stopElectricize(Channel channel,ByteBuffer byteBuffer){
		
		logger.debug("stopcharge api enter");
		
		//电桩编号
		String epCode = ByteUtil.getString(byteBuffer);
		int epGunNo=byteBuffer.getInt();
		int userId=byteBuffer.getInt();
		String account = ByteUtil.getString(byteBuffer);
		int orgNo = byteBuffer.getInt();
		logger.info("stopcharge accept from api accountId:{},epCode:{},epGunNo:{} ",
				new Object[]{userId,epCode,epGunNo});
		
		int errorCode = EpChargeService.appApiStopElectric(epCode,epGunNo,orgNo,userId,account,0,NetUtils.getClientIp(channel));
		if(errorCode>0)
		{
			logger.info("stopcharge apiStopElectric fail errorCode:{} accountId:{},epCode:{},epGunNo:{} to api",
					new Object[]{errorCode,userId,epCode,epGunNo});
			
			byte[] msg = ApiEncoder.stopElectricizeProtocol(epCode,epGunNo,(short)0,errorCode);
			InnerApiMessageSender.gateSendToGame(channel, A2ECmdConstantsAPI.G2A_STOP_ELECTRICIZE, (Integer)0,  msg);
		}
	}
	/**
	 * 费率下发
	 * @param byteBuffer
	 */
	public static void RateCmd(Channel channel,ByteBuffer byteBuffer){
		logger.info("[Rate]费率下发 ..channel是：【{}】", new Object[]{channel} );
		//电桩编号
		String epCodes = ByteUtil.getString(byteBuffer);
		
		int rateId=byteBuffer.getInt();
		
		logger.info("[Rate]accept rate update from api,rateId:{},epCodes:{}",rateId,epCodes);
		
		RateService.apiUpdateRateAction(epCodes,rateId);
	}
	

	public static void updateQRCode(Channel channel,ByteBuffer byteBuffer){
		logger.info("二维码更新 ..channel是：【{}】", new Object[]{channel} );
		//电桩编号
		String epCode = ByteUtil.getString(byteBuffer);
		
		String epGunNo1 = ByteUtil.getString(byteBuffer);
		
		int epGunNo=Integer.parseInt(epGunNo1);
		
		String qrCodeContent = ByteUtil.getString(byteBuffer);
		
		//取枪
		EpGunCache gunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		
		if(gunCache == null)
			return;
		if(!EpCommClientService.enableComm(gunCache.getConcentratorId(),epCode))
			return;
		
		if(gunCache.getRealChargeInfo().getWorkingStatus() == YXCConstants.EP_WORK_STATUS_UPDATE)
		{
		  return;
		  
		}	
		logger.info("accept updateQRCode  from api epCode:{},epGunNo:{},qrCodeContent:{}",new Object[]{epCode,epGunNo,qrCodeContent});
		
	//	EpCache.updateQRCode(epCode,epGunNo,qrCodeContent);
		
	}
	
	public static void updateHexFile(Channel channel, ByteBuffer byteBuffer) {
		try {
			// 产品ID
			int typeSpanId = byteBuffer.getInt();
			int num = byteBuffer.getInt();
			if (num == 0 || typeSpanId == 0) {
				logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "accept API fail num:0 or typeSpanId"), 0);
				return;
			}
			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "typeSpanId|num"),
					new Object[]{typeSpanId, num});
			Map<String, BomListInfo> bomMap = new ConcurrentHashMap<String, BomListInfo>();

			for (int i = 0; i < num; i++) {
				BomListInfo bom = new BomListInfo();

				String hardwareNumber = ByteUtil.getString(byteBuffer);
				String hardwareVersion = ByteUtil.getString(byteBuffer);
				String softNumber = ByteUtil.getString(byteBuffer);
				String softVersion = ByteUtil.getString(byteBuffer);
				String updateFlag1 = ByteUtil.getString(byteBuffer);
				int updateFlag = Integer.parseInt(updateFlag1);
				String md5Value = ByteUtil.getString(byteBuffer);

				bom.setSoftNumber(softNumber);
				bom.setSoftVersion(softVersion);
				bom.setHardwareNumber(hardwareNumber);
				bom.setHardwareVersion(hardwareVersion);
				bom.setFileMd5(md5Value);
				bom.setForceUpdate(updateFlag);
				if (bom.splitSoftVersion() > 0) {
					logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "bom softVersion error,setSoftNumber|softVersion"),
							new Object[]{softNumber, softVersion});
					continue;
				}
				if (bom.splitHardwareVersion() > 0) {
					logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "bom hardVersion error,setHardNumber|HardVersion"),
							new Object[]{hardwareNumber, hardwareVersion});
					continue;
				}

				String key = hardwareNumber + hardwareVersion;
				bomMap.put(key, bom);

			}
			EqVersionService.AddBomList(typeSpanId, bomMap);
			EqVersionService.queryAllEpByTypeSpanID(typeSpanId, bomMap);
			EqVersionService.queryAllStaionByTypeSpanID(typeSpanId, bomMap);

		} catch (Exception e) {
			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "exception"),
					new Object[]{e.getStackTrace()});
		}

	}
	
	/**
	 * 私有电桩运营时间
	 * @param byteBuffer
	 */
	public static void privateElectricize(Channel channel,ByteBuffer byteBuffer){
		
		/*String businessId = ByteUtil.getString(byteBuffer);//App业务处理Id
		String epCode = ByteUtil.getString(byteBuffer);//获得电桩编号
		short pileStatus = byteBuffer.getShort();//电桩离线标识（0：离线，1：上线）
		String startTime = ByteUtil.getString(byteBuffer);//运营开始时间
		String stopTime = ByteUtil.getString(byteBuffer);//运营结束时间
*/	}
	
	public static void changeEpGate(Channel channel,ByteBuffer byteBuffer)
	{
		logger.info("移桩 ..channel是：【{}】", new Object[]{channel} );
		//电桩编号
		String epCodes = ByteUtil.getString(byteBuffer);
		String newGateIp = ByteUtil.getString(byteBuffer);
		int Port=byteBuffer.getInt();
		
		logger.info("changeEpGate params,epCodes:{},newGateIp:{},Port:{}",new Object[]{
				epCodes,newGateIp,Port});
	}
	
	public static void flashLed(Channel channel,ByteBuffer byteBuffer){
		
		logger.info("flashLed 【{}】", new Object[]{channel} );
		//电桩编号
		String epCode = ByteUtil.getString(byteBuffer);
		
		short type = byteBuffer.getShort();
		int continueTime = byteBuffer.getInt();//分钟
		int  accountId = byteBuffer.getInt();
		float lng = byteBuffer.getFloat();
		float lag = byteBuffer.getFloat();
		
		if(continueTime==0)
		{
			continueTime = 2;
		}
		
		//取枪
		int errorCode = EpService.doNearCallEpAction(epCode,type,continueTime*60, accountId,lng,lag);
		
		logger.info("flashLed,errorCode:{}",errorCode);
	}
	public static void dropCarPlaceLock(Channel channel,ByteBuffer byteBuffer)
	{
		logger.info("移桩 ..channel是：【{}】", new Object[]{channel} );
		//电桩编号
		String epCode = ByteUtil.getString(byteBuffer);
		int  epGunNo = byteBuffer.getShort();
		String carPlaceNo = ByteUtil.getString(byteBuffer);//车位号
		int  accountId = byteBuffer.getInt();
		float lng = byteBuffer.getFloat();
		float lag = byteBuffer.getFloat();
		
		
		logger.info("dropCarPlaceLock params---"+
				"epCodes:"+epCode+
				",epGunNo:"+epGunNo
		);
		
		//取枪
		int errorCode = EpGunService.dropCarPlaceLockAction(epCode, epGunNo, accountId,lng,lag);
		
		logger.info("dropCarPlaceLock,errorCode:{}",errorCode);
	}
	
	public static void sendEvent(String Identity,short protocolNum, int senderId, byte[] bb)
	{
		logger.debug("sendEvent,Identity:{}",Identity);
		if(Identity!=null && Identity.length()>0)
		{
			Channel channel = AppClientService.getAppChannel(Identity);
			if(channel!=null)
			{
				logger.info("sendEvent,channel:{},protocolNum:{},senderId:{}",
						new Object[]{channel,protocolNum,senderId});
				InnerApiMessageSender.gateSendToGame(channel,protocolNum, senderId, bb);
			}
			else
			{
				logger.error("sendEvent,not found channel of Identity:{}",Identity);
			}
		}
		else
		{
			logger.info("sendEvent,broadcastMsg,protocolNum:{}",protocolNum);
			AppClientService.broadcastMsg(protocolNum,senderId, bb);
		}
		
	}
	public static void sendStopChargeByPhoneDisconnect(String epCode,int epGunNo,int userId,int ret,int casuse,int chargeTime)
	{
		logger.debug("sendstopchargeByPhoneDisconnect,epCode:{},epGunNo:{},userId:{},casuse:{},chargeTime:{}",new Object[]{epCode,epGunNo,userId,casuse,chargeTime});
		
		byte[] msg = ApiEncoder.stopElectricizeProtocol(epCode, epGunNo, (short)ret,casuse);
		sendEvent("",A2ECmdConstantsAPI.G2A_STOP_ELECTRICIZE, (Integer) 0, msg);
			
		byte[] msgEvent = ApiEncoder.stopElectricizeEventProtocol(chargeTime, (short)ret, casuse, userId);
		sendEvent("",A2ECmdConstantsAPI.G2A_STOP_ELECTRICIZE_EVENT, (Integer) 0, msgEvent);
		
	}
	@SuppressWarnings("unchecked")
	public static void onEvent(int type,UserOrigin userOrigin,int respRet,int cause,Object srcParams,Object extraData)
	{
		try{
			logger.info("api onEvent,type:{},userOrigin:{}",type,userOrigin);
		if(extraData==null)
		{
			logger.info("AppApiService onEvent error,extraData==null");
			return;
		}
		String actionIdentity="";
		if(userOrigin!=null)
			actionIdentity = userOrigin.getCmdChIdentity();
		switch(type)
		{
			case EventConstant.EVENT_BESPOKE:
			{
				
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent!paramsMap:{}",paramsMap);
				
				int userId  = (int)paramsMap.get("usrId");
				int redo  = (int)paramsMap.get("redo");
				String bespNo  = (String)paramsMap.get("bespNo");
				
				byte[] msg = ApiEncoder.bespokeProtocol(bespNo, (short)respRet, cause, userId, (short)redo);
				
				logger.info("bespoke event send to api,bespNo:{}",bespNo);
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_BESPOKE, (Integer) 0, msg);
				
				
			}
			break;
			case EventConstant.EVENT_CANNEL_BESPOKE:
			{
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent!paramsMap:{}",paramsMap);
				String bespokeNo  = (String)paramsMap.get("bespNo");
				
				byte[] msg = ApiEncoder.cancelBespokeProtocol(bespokeNo, (short)respRet, cause);
				logger.info("cannel bespoke event send to api, respRet:{}",respRet);
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_CANCEL_BESPOKE, (Integer) 0, msg);
					
					
				logger.info("respRet:{}",respRet);
				if(respRet ==1)
				{
					double amt = (double)paramsMap.get("amt");
					double userRemainAmt = (double)paramsMap.get("remainAmt");
					String account = (String)paramsMap.get("account");
					
					logger.info("cannel bespoke event send to api,amt:{},account:{}",amt,account);
					
					byte[] bespEventData = ApiEncoder.bespokeRespEvent(amt, userRemainAmt, account);
					
					sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_BESP_EVENT, (Integer) 0, bespEventData);
				
				}
			}
			break;
			case EventConstant.EVENT_CHARGE_EP_RESP:
			{
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent!paramsMap:{}",paramsMap);
				
				String epCode = (String)paramsMap.get("epcode");
				int epGunNo = (int)paramsMap.get("epgunno");
				
				logger.info("start charge ret event send to api,epCode:{},epGunNo:{}",epCode,epGunNo);
				
				byte[] msg = ApiEncoder.startElectricizeProtocol(epCode,epGunNo,(short)respRet,cause);
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_START_ELECTRICIZE, (Integer) 0, msg);
				
			}
			break;
			case EventConstant.EVENT_STOP_CHARGE_EP_RESP:
			{
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent!paramsMap:{}",paramsMap);
				String epCode = (String)paramsMap.get("epcode");
				int epGunNo = (int)paramsMap.get("epgunno");
				
				logger.info("stop charge event send to api,epCode:{},epGunNo:{}",epCode,epGunNo);
				
				byte[] msg = ApiEncoder.stopElectricizeProtocol(epCode, epGunNo, (short)respRet,cause);
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_STOP_ELECTRICIZE, (Integer) 0, msg);
			}
			break;
			
			case EventConstant.EVENT_CONSUME_RECORD:
			{
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent!paramsMap:{}",paramsMap);
				int pkUserId  = (int)paramsMap.get("usrId");
				String userPhone  = (String)paramsMap.get("account");
				long st = (long)paramsMap.get("st");
				long et = (long)paramsMap.get("et");
				
				int totalAmt = (int)paramsMap.get("totalAmt");
				
				short chargeTime = (short)((et-st)/60);
				int couPonAmt = (int)paramsMap.get("realCouPonAmt");
				
				logger.info(" consume record event send to api,AccountId:{}",pkUserId);
				
				byte[] msg = ApiEncoder.consumeRecordProtocol(pkUserId, userPhone, totalAmt/100, chargeTime, (short)respRet, cause);
				
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_ELECTRIC_CONSUME_RECORD, (Integer) 0, msg);
			}
			break;
			case EventConstant.EVENT_START_CHARGE_EVENT:
			{
				Map<String, Object> paramsMap = (Map<String, Object>)extraData;
				logger.debug("AppApiService onEvent EVENT_START_CHARGE_EVENT!paramsMap:{}",paramsMap);
				
				String Account = (String)paramsMap.get("account");
				int userId = (int)paramsMap.get("usrId");
				
				logger.info("start charge event send to api,Account:{}",Account);
				
				byte[] msg = ApiEncoder.startElectricizeEventProtocol(userId, Account);
				sendEvent(actionIdentity,A2ECmdConstantsAPI.G2A_START_ELECTRICIZE_EVENT, (Integer) 0, msg);
	
			}
		   break;
		default:
			logger.error("AppApiService onEvent!invalid type:{}",type);
			break;
		  }
	   }
		catch (Exception e) 
		{
			logger.error("API onEvent exception type:{},e.getMessage():{}",type,e.getMessage());
			return ;
		}
	}
	
	public static void  concentratorConfig(Channel channel,ByteBuffer byteBuffer){
		logger.info("accept concentratorConfig from api channel：【{}】", new Object[]{channel} );
	  
			int concentratorId = byteBuffer.getInt();
			
			EpConcentratorService.concentratorConfig(concentratorId);
		
	}
	
	
	public static void updateTempChargeMaxNum(Channel channel,ByteBuffer byteBuffer){
		logger.info("[tempCharge] channel：【{}】", new Object[]{channel} );
	  
		try{
		int company_number = byteBuffer.getInt(); //公司组织编码标识
		int maxNum = byteBuffer.getInt(); //最大充电次数
		
		logger.info("[tempCharge],accept API updateTempChargeMaxNum,company_number:{},maxNum:{}",company_number,maxNum);
		Map<String,BomListInfo> bomMap=new ConcurrentHashMap<String,BomListInfo>();
		
		EpService.queryAllEpByCompanyNumber(company_number,maxNum);
		
		  
		}catch (Exception e) {
			logger.error("[tempCharge],accept API updateTempChargeMaxNum exception,e.getMessage():{}",e.getMessage());
			
		}
		
	}
	
	/**
	 * 定时充电
	 * @param channel
	 * @param byteBuffer
	 */
	public static void issuedTimingCharge(Channel channel, ByteBuffer byteBuffer){
		logger.info(LogUtil.addExtLog("issuedTimingCharge channel|byteBuffer"),new Object[]{channel, byteBuffer});
		//电桩编号
		String epCodes = ByteUtil.getString(byteBuffer);
		
		//定时充电时间
		String time = ByteUtil.getString(byteBuffer);
		
		//定时充电开关
		int timingChargeStatus = byteBuffer.getInt();
		
		logger.info(LogUtil.addExtLog("received timing Charge data : epCodes|time|timingChargeStatus"),new Object[]{epCodes, time, timingChargeStatus});
		
		if (epCodes == null || epCodes.length() < 1) {
			logger.info(LogUtil.addExtLog("epCodes is null, issued timing charge fail!"));
			return;
		}
		
		if (time == null || time.length() < 1) {
			logger.info(LogUtil.addExtLog("time is null, issued timing charge fail!"));
			return;
		}
		
		if (timingChargeStatus != 0 && timingChargeStatus != 1) {
			logger.info(LogUtil.addExtLog("The timingChargeStatus value is wrong, issued timing charge fail! timingChargeStatus:"), new Object[]{timingChargeStatus});
			return;
		}
		
		int resultNum = TimingChargeService.sendTimingCharge(epCodes, time, timingChargeStatus);
		if (resultNum == 0) {
			logger.info(LogUtil.addExtLog("issued timing charge success"));
		}
	}

	public static void issuedWorkArg(Channel channel, ByteBuffer byteBuffer){
		logger.info(LogUtil.addExtLog("channel|byteBuffer"),new Object[]{channel, byteBuffer});

		//电桩编号
		String epCodes = ByteUtil.getString(byteBuffer);
		
		if (epCodes == null || epCodes.length() < 1) {
			logger.info(LogUtil.addExtLog("epCodes is null!"));
			return;
		}
		
		EpService.sendWorkArg(epCodes);
	}
    public static void queryEpInfo(Channel channel, ByteBuffer byteBuffer){
        logger.info(LogUtil.addExtLog("channel|byteBuffer"),new Object[]{channel, byteBuffer});

        //电桩编号
        String epCodes = ByteUtil.getString(byteBuffer);

        if (epCodes == null || epCodes.length() < 1) {
            logger.info(LogUtil.addExtLog("epCodes is null!"));
            return;
        }

        EpService.sendQueryEpInfo(epCodes);
    }
}
