package com.ec.epcore.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.EventConstant;
import com.ec.constants.GunConstants;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.BespCache;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.RealACChargeInfo;
import com.ec.epcore.cache.RealDCChargeInfo;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.task.CheckGunTask;
import com.ec.epcore.task.EpMessageTask;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.SingleInfo;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.LogUtil;
import com.ec.utils.NetUtils;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargeACInfo;
import com.ormcore.model.TblChargeDCInfo;
import com.ormcore.model.TblElectricPileGun;

public class EpGunService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGunService.class.getName()));
	
	private static Map<String, EpGunCache> mapGun = new ConcurrentHashMap<String,EpGunCache>();
	
	public static String getCacheSize()
	{
		return "EpGunService:\nmapGun.size():"+ mapGun.size()+"\n\n";
	}
	
	public static boolean checkWorkStatus(int status)
	{
		if(	status!= GunConstants.EP_GUN_W_STATUS_OFF_LINE && 
			status!= GunConstants.EP_GUN_W_STATUS_FAULT &&
			status!= GunConstants.EP_GUN_W_STATUS_IDLE &&
			status!= GunConstants.EP_GUN_W_STATUS_WORK &&
			status!= GunConstants.EP_GUN_W_STATUS_BESPOKE &&
			status!= GunConstants.EP_GUN_W_STATUS_UPGRADE &&
			status!= GunConstants.EP_GUN_W_STATUS_USER_OPER &&
			status!= GunConstants.EP_GUN_W_STATUS_SETTING &&
			status!= GunConstants.EP_GUN_W_STATUS_SELECT_CHARGE_MODE &&
			status!= GunConstants.EP_GUN_W_INIT && 
			status<(GunConstants.EP_GUN_W_STATUS_SELECT_CHARGE_MODE+1))//以后充电模式加了状态往后移
		{
			return false;
		}
		else
		{
			if(status> GunConstants.EP_GUN_W_STATUS_URGENT_STOP )
				return false;
		}
		return true;
	}
	
	public static int convertEpWorkStatus(int epWorStatus)
	{
		int ret=-1;
		switch(epWorStatus)
		{
		
		case GunConstants.EP_GUN_W_STATUS_OFF_LINE://离线
			ret = GunConstants.EP_GUN_STATUS_OFF_LINE;
			break;
		case GunConstants.EP_GUN_W_STATUS_FAULT://故障，停用
			ret = GunConstants.EP_GUN_STATUS_STOP_USE;
			break;
		case GunConstants.EP_GUN_W_STATUS_IDLE://空闲
			ret = GunConstants.EP_GUN_STATUS_IDLE;//空闲
			break;
		case GunConstants.EP_GUN_W_STATUS_WORK:// 工作(充电)
			ret = GunConstants.EP_GUN_STATUS_CHARGE;
			break;
		case GunConstants.EP_GUN_W_STATUS_BESPOKE://预约
			ret = GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED;
			break;
		case GunConstants.EP_GUN_W_STATUS_UPGRADE:// 在线升级
			ret = GunConstants.EP_GUN_STATUS_EP_UPGRADE;
			break;
		case GunConstants.EP_GUN_W_STATUS_USER_OPER:// 操作中(待定,防止用户在操作中被预约)
			ret = GunConstants.EP_GUN_STATUS_EP_OPER;//操作
		    break;
		case GunConstants.EP_GUN_W_STATUS_WAIT_CHARGE:// 等待充电
			ret = GunConstants.EP_GUN_STATUS_WAIT_CHARGE;//等待充电
		    break;
		case GunConstants.EP_GUN_W_STATUS_TIMER_CHARGE:// 定时等待
			ret = GunConstants.EP_GUN_STATUS_TIMER_CHARGE;//定时等待
		    break;
		case GunConstants.EP_GUN_W_STATUS_SETTING://设置状态
			ret = GunConstants.EP_GUN_STATUS_SETTING;//设置
			break;
			
		case GunConstants.EP_GUN_W_STATUS_SELECT_CHARGE_MODE://充电模式选择
			ret = GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE;
			break;
		default:
			if(epWorStatus>=GunConstants.EP_GUN_W_STATUS_LESS_VOL_FAULT && epWorStatus<=GunConstants.EP_GUN_W_STATUS_URGENT_STOP)
			{
				ret= GunConstants.EP_GUN_STATUS_STOP_USE;
			}
			break;
		}
		return ret;
	}
	
	public static boolean checkCarPlaceLockStatus(int status)
	{
		if(	status!= 0 && 
			status!= 1 &&
			status!= 2 &&
			status!= 3 &&
			status!= 4 )
		{
			return false;
		}
		return true;
	}
	
	public static boolean checkCardInfoAddr(int addr)
	{
		if(	(addr>= YXCConstants.YC_BATTARY_TYPE && addr<= YXCConstants.YC_BATTRY_CAN_HIGH_TEMP)
				||(addr>= YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP && addr<= YXCConstants.YC_CAR_BATTRY_TOTAL_VOL)
				|| addr== YXCConstants.YC_VAR_CAR_VIN || addr== YXCConstants.YC_VAR_BATTARY_FACTORY )
			{
				return true;
			}
		else
		{
			return false;
		}
		
	}
	
	
	
	public static EpGunCache getEpGunCache(String epCode,int epGunNo)
	{
		String combEpGunNo = epCode+ epGunNo;
		return mapGun.get(combEpGunNo);
	}
	public static void putEpGunCache(String epCode,int epGunNo,EpGunCache cache)
	{
		if(cache !=null && epCode !=null)
		{
			String combEpGunNo = epCode+ epGunNo;
			mapGun.put(combEpGunNo, cache);
			
		}
	}
	public static TblElectricPileGun getDbEpGun(int pkEpId,int epGunNo)
	{
		TblElectricPileGun tblEpGun= new TblElectricPileGun();
		tblEpGun.setPkEpId(pkEpId);
		tblEpGun.setEpGunNo(epGunNo);
		
		TblElectricPileGun dbEpGun=null;
		List<TblElectricPileGun> epGunList=DB.epGunDao.findEpGunInfo(tblEpGun);
		
		if(epGunList==null)
		{
			logger.error("getDbEpGun not find dbEpGun,epGunList==null!pkEpId:{},epGunNo:{}",pkEpId,epGunNo);
			return null;
		}
			
		if(epGunList.size()!=1)
		{
			logger.error("getDbEpGun not find dbEpGun,pkEpId:{},epGunList.size:{}",pkEpId,epGunList.size());
			return null;
		}
		
		dbEpGun = epGunList.get(0);
		return dbEpGun;
	}

	
	@SuppressWarnings("rawtypes")
	public static void checkTimeout()
	{
		
		Iterator iter = mapGun.entrySet().iterator();
		int count=0;
		while (iter.hasNext()) {

			count++;
			if(count>10)
			{
				NetUtils.sleep(10);
				count=0;
			}
			Map.Entry entry = (Map.Entry) iter.next();
			if(entry==null)
			{
				break;
			}
			
			EpGunCache epGunCache=(EpGunCache) entry.getValue();
			if(null == epGunCache )
			{
				logger.info("checkTimeout: epGunCache=null:\n");
				continue;
			}
			//TODO:这儿有点绕，以后全部挪到预约对象里去检查.
			BespCache bespCache = epGunCache.getBespCache();
			if(bespCache!=null) //判断预约
			{
				//检查倒计时短信
				checkExpireBesp(epGunCache);
			}
			checkActionTimeOut(epGunCache);
		}
	}
	
	public static void checkExpiringBesp(long now,EpGunCache epGunCache)
	{
	
		BespCache bespCache=epGunCache.getBespCache();
        if(bespCache.getStatus() == EpConstants.BESPOKE_STATUS_LOCK &&
        		!bespCache.isExpirWarn() )
		{	
        	
			long endtime = bespCache.getEndTime();
			int diff = (int)(now - endtime);
			if(diff<0 && Math.abs(diff)<GameConfig.checkExpiringBesp)//
			{
				logger.info("bespoke is Expiring warnin epCode:{},epGunNo:{}",epGunCache.getEpCode(), epGunCache.getEpGunNo());
				ElectricPileCache epCache = EpService.getEpByCode(epGunCache.getEpCode());
				bespCache.onBespokeExpiringWarn(epCache.getAddress(),bespCache.getAccount());
			}
		}
		
		
	}
	public static void checkExpireBesp(EpGunCache epGunCache)
	{
		java.util.Date dt = new Date();
		long now = dt.getTime() / 1000;
		
		
		BespCache bespCache = epGunCache.getBespCache();
        
		if(epGunCache.getStatus() == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED )
		{
			checkExpiringBesp(now,epGunCache);//检测预约到期
			
			long expireTime = EpBespokeService.expireCoolTime(bespCache);
			if(expireTime>0 )
			{
				epGunCache.forceEndBespoke();
			}
		}
		
	}
	
	public static int checkAction(int gunStatus,int gunUserId, int action,int actionUserId)
	{
		
		/**
		 * status.
		 * 0:空闲；可以接收1.电桩用户名和密码鉴权，2.前端预约，3，前端充电，状态都转为10
		 * 3：预约；可以接收1.取消预约，状态转为11,2.预约用户充电.在收到充电后转为6
		 * 6，充电，可以接收1.停止充电,状态转为0，2.故障原因自动停止，状态转为9
		 * 9：停用；不接收预约，不接收充高点
		 * 10；桩操作占用.接受二维码充电
		 * 11；预约冷却.接收同一用户充电和再预约
		 * 12:用户鉴权成功
		 */
		
		int ret=0;
		switch(action)
		{
		case EventConstant.EVENT_DROP_CARPLACE:
		{
			if(gunStatus == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
			{
				if(gunUserId!=0 && gunUserId != actionUserId)
				{
					ret= ErrorCodeConstants.BESP_NO_NOT_EXIST;
				}
				return ret;
			}
			ret= ErrorCodeConstants.BESP_NO_NOT_EXIST;
		}
		break;
		default:
			break;
		
		}
		return ret;
		
	}
	
	public synchronized static void checkActionTimeOut(EpGunCache epGunCache)
	{
		if(epGunCache == null)
			return;
		//检查预约超时
		epGunCache.checkBespokeCmdTimeOut();
		//检查充电超时
		epGunCache.checkChargeCmdTimeOut(3);
	}

	public static void updateDeviceList(int pkEpGunId,int hadLid,int hadSitSignal,int hadRadar,int hadCarPlaceLock,int hadBmsComm)
	{
		TblElectricPileGun info= new TblElectricPileGun();
		info.setPkEpGunId(pkEpGunId);
		
		info.setHadLid(hadLid);
		info.setHadSitSignal(hadSitSignal);
		
		info.setHadRadar(hadRadar);
		info.setHadCarPlaceLock(hadCarPlaceLock);
		info.setHadBmsComm(hadBmsComm);
		DB.epGunDao.updateDeviceList(info);
	}
	public static void updateGunState(int pkEpGunId,int status)
	{
		TblElectricPileGun info= new TblElectricPileGun();
		info.setPkEpGunId(pkEpGunId);
		info.setEpState(status);
		DB.epGunDao.updateGunState(info);
	}
	
	public static void startCheckTimeoutServer(long initDelay) {
		
		CheckGunTask checkTask =  new CheckGunTask();
				
		TaskPoolFactory.scheduleAtFixedRate("CHECK_BESPOKE_CHARGE_TASK", checkTask, initDelay, 5, TimeUnit.SECONDS);
	}
	
	public static String getGunStatusDesc(int status)
	{
		switch(status)
		{
		case GunConstants.EP_GUN_STATUS_IDLE:
			return "空闲";
		case GunConstants.EP_GUN_STATUS_EP_INIT:
			return "电桩初始化中";
		case GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED:
			return "预约锁定中";
		case GunConstants.EP_GUN_STATUS_CHARGE:
			return "充电中";
		case GunConstants.EP_GUN_STATUS_STOP_USE:
			return "停用";
		case GunConstants.EP_GUN_STATUS_EP_OPER:
			return "电桩有人使用中";
		case GunConstants.EP_GUN_STATUS_WAIT_CHARGE:
			return "轮充等待";
		case GunConstants.EP_GUN_STATUS_TIMER_CHARGE:
			return "定时等待";
		
		case GunConstants.EP_GUN_STATUS_USER_AUTH:
			return "用户占用";
		case GunConstants.EP_GUN_STATUS_SETTING:
			return "设置界面";
		case GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE:
			return "充电模式选择";
		case GunConstants.EP_GUN_STATUS_EP_UPGRADE:
			return "升级中";
		case GunConstants.EP_GUN_STATUS_OFF_LINE:
			return "离线状态";
			
		default:
			return "未知状态("+status +")";
		}
	}

	public static void updateChargeInfoToDbByEpCode(int currentType,String epCode,int epGunNo,BigDecimal bdMeterNum,String serialNo,BigDecimal fronzeAmt,int startTime,int userId)
	{
		
		if(currentType != EpConstants.EP_AC_TYPE && currentType !=  EpConstants.EP_DC_TYPE )
		{
			logger.error("updateChargeInfoToDbByEpCode currentType error,epCode:{},epGunNo:{},currentType:{},accountId:{}",
					new Object[]{epCode,epGunNo,currentType,userId});
			
			
			return ;
		}
		if(currentType == 14)
		{
			TblChargeACInfo chargeInfo =  new TblChargeACInfo();
			
			chargeInfo.setEp_code(epCode);
			chargeInfo.setEp_gun_no(epGunNo);
		
			chargeInfo.setChargeSerialNo(serialNo);
			chargeInfo.setChargeStartMeterNum(bdMeterNum);
			chargeInfo.setChargeStartTime(startTime);
			chargeInfo.setChargeUserId(userId);
			chargeInfo.setFronzeAmt(fronzeAmt);
			
			DB.chargeACInfoDao.updateStartChargeInfo(chargeInfo);
			
		}
		else if(currentType == 5)
		{
			TblChargeDCInfo chargeInfo = new TblChargeDCInfo();
			
			chargeInfo.setEp_code(epCode);
			chargeInfo.setEp_gun_no(epGunNo);
			
			chargeInfo.setChargeSerialNo(serialNo);
			chargeInfo.setChargeStartMeterNum(bdMeterNum);
			chargeInfo.setChargeStartTime(startTime);
			chargeInfo.setChargeUserId(userId);
			chargeInfo.setFronzeAmt(fronzeAmt);
			
			DB.chargeDCInfoDao.updateStartChargeInfo(chargeInfo);
			
		}
	}
	
	public static int dropCarPlaceLockAction(String epCode,int epGunNo, int  accountId,float lng,float lag)
	{
		EpGunCache epGunCache = getEpGunCache(epCode, epGunNo);
		
		if(epGunCache==null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		//判断附近
		
		
		//TODO:userId
		int errorCode= checkAction(epGunCache.getStatus(),0/*epGunCache.getCurUserId()*/,EventConstant.EVENT_DROP_CARPLACE,accountId);
		if(errorCode>0)
			return errorCode;
		
		return epGunCache.dropCarPlaceLockAction();
	}
	
	public static void handleCardAuth(String epCode,int epGunNo,int userOrgin,String innerCardNo,String outCardNo,byte[] cmdTimes)
	{
		Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
		respMap.put("epcode", epCode);
		respMap.put("epgunno", epGunNo);
		respMap.put("innerno", innerCardNo);
		respMap.put("outno", outCardNo);
		respMap.put("cmdTimes",cmdTimes );
		
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache!=null)
		{			
			epGunCache.handleEventExtra(EventConstant.EVENT_CARD_AUTH,5,0,0,null,(Object)respMap);
			
		}
		else
		{
			logger.error("card charge userCardAuth fail,epCode:{},epGunNo:{},inCardNo:{}  not find EpGunCache",
					new Object[]{epCode,epGunNo,innerCardNo});
		}
	}
	public static void onAuthResp(String epCode,int epGunNo,String innerNo,String outerNo,int ret,int errorCode,byte []cmdTimes)
	{
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache==null)
		{
		
			logger.info("onAuthResp,card charge userCardAuth fail,epCode:{},epGunNo:{},inCardNo:{} not find EpGunCache",
					new Object[]{epCode,epGunNo,innerNo});
		}
		else
		{
			byte[] data = EpEncoder.do_card_auth_resq(epCode,epGunNo,innerNo,outerNo,1,0,0,ret,errorCode);
			EpCommClient epCommClient = (EpCommClient)epGunCache.getEpNetObject();
			EpMessageSender.sendMessage(epCommClient, 0, 0, Iec104Constant.C_CARD_AUTH_RESP, data,cmdTimes,epCommClient.getVersion());
			
			logger.info("onAuthResp,card charge,userCardAuth resp epCode:{},epGunNo:{},inCardNo:{}",
					new Object[]{epCode,epGunNo,innerNo});
		}
	}
	
	
	public static void clearIdentyCode(int pkEpId)
	{
		 TblElectricPileGun tblGun=new TblElectricPileGun();
		 
		 tblGun.setPkEpId(pkEpId);
		 tblGun.setQrdate(0);
		 
		 tblGun.setQr_codes("");
		 //保存到数据库
		 DB.epGunDao.updateQR(tblGun);
		 
		 logger.debug("clearIdentyCode: updateQR,pkEpId:{}",pkEpId);
	}
	
	public static void modifyGunStatus(int pkEpId)
	{
		 TblElectricPileGun tblGun=new TblElectricPileGun();
		 
		 tblGun.setPkEpId(pkEpId);
		 tblGun.setQrdate(0);
		 
		 tblGun.setQr_codes("");
		 //保存到数据库
		 DB.epGunDao.updateQR(tblGun);
		 
		 logger.debug("clearIdentyCode: updateQR,pkEpId:{}",pkEpId);
	}
	
	
	
	/**
	 * 
	 * @param addr
	 * @param epType
	 * @param epGunCache
	 * @return
	 */
	public static SingleInfo getSingleInfo(int addr,int epType,EpGunCache epGunCache)
    {
		SingleInfo singInfo = null;
		if (epType == EpConstants.EP_AC_TYPE) {
			RealACChargeInfo realChargeInfo = (RealACChargeInfo) (epGunCache
					.getRealChargeInfo());
			singInfo = realChargeInfo.getFieldValue(addr);

		} else {
			RealDCChargeInfo realChargeInfo = (RealDCChargeInfo) (epGunCache
					.getRealChargeInfo());
			singInfo = realChargeInfo.getFieldValue(addr);

		}
		return singInfo;
	}
	
     public static void startRepeatSendMessage() {
		
    	 EpMessageTask checkTask =  new EpMessageTask();
				
		TaskPoolFactory.scheduleAtFixedRate("REPEAT_EP_MESSAGE_TASK", checkTask, 5, 5, TimeUnit.SECONDS);
	}
	
    
}
