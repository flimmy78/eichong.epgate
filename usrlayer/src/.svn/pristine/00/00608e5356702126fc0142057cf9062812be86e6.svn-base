package com.ec.usrcore.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.Global;
import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.UserConstants;
import com.ec.logs.LogConstants;
import com.ec.net.proto.WmIce104Util;
import com.ec.usrcore.cache.ChargeCache;
import com.ec.usrcore.cache.ElectricPileCache;
import com.ec.usrcore.cache.EpGunCache;
import com.ec.usrcore.cache.UserOrigin;
import com.ec.usrcore.cache.UserRealInfo;
import com.ec.usrcore.net.client.EpGateNetConnect;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.RateInfo;
import com.ormcore.model.TblChargingrecord;

public class EpChargeService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpChargeService.class.getName()));
	
	public static ChargeCache convertFromDb(TblChargingrecord tblChargeRecord)
	{
		if(tblChargeRecord ==null)
			return null;
		ChargeCache chargeCache = new ChargeCache();
	
		chargeCache.setSt(tblChargeRecord.getChreStartdate().getTime()/1000);
		
		chargeCache.setStatus(tblChargeRecord.getStatus());
		
		
		//chargeCache.setAccount(tblChargeRecord.getUserPhone());
		chargeCache.setBespNo(tblChargeRecord.getChreBeginshowsnumber());
		chargeCache.setChargeSerialNo(tblChargeRecord.getChreTransactionnumber());
		
		chargeCache.setChOrCode(tblChargeRecord.getChreCode());
		
		chargeCache.setUserId(tblChargeRecord.getUserId());
		chargeCache.setPkUserCard(tblChargeRecord.getPkUserCard());
		if(chargeCache.getPkUserCard() !=0)
		{
			chargeCache.setStartChargeStyle((short)EpConstants.CHARGE_TYPE_CARD);
		}
		else
		{
			chargeCache.setStartChargeStyle((short)EpConstants.CHARGE_TYPE_QRCODE);
		}
		
		
		BigDecimal value= tblChargeRecord.getFrozenAmt().multiply(Global.DecTime2);
		chargeCache.setFronzeAmt(value.intValue());
		chargeCache.setPayMode(tblChargeRecord.getPayMode());
		
		UserOrigin userOrigin = new UserOrigin(tblChargeRecord.getUserOrgNo(),1,"");
		chargeCache.setUserOrigin(userOrigin);
		
		RateInfo rateInfo = new RateInfo();
		rateInfo.setJ_Rate(tblChargeRecord.getJPrice());
		rateInfo.setF_Rate(tblChargeRecord.getFPrice());
		rateInfo.setP_Rate(tblChargeRecord.getPPrice());
		rateInfo.setG_Rate(tblChargeRecord.getGPrice());
		rateInfo.setG_Rate(tblChargeRecord.getGPrice());
		rateInfo.setQuantumDate(tblChargeRecord.getQuantumDate());
		
		chargeCache.setRateInfo(rateInfo);
		
		return chargeCache;
	}
	
	public static ChargeCache GetUnFinishChargeCache(String epCode,int epGunNo)
	{
		TblChargingrecord tblQueryChargeRecord= new TblChargingrecord();
		tblQueryChargeRecord.setChreUsingmachinecode(epCode);
		
		tblQueryChargeRecord.setChreChargingnumber(epGunNo);
		
		List<TblChargingrecord> chargeList = DB.chargingrecordDao.getUnFinishedCharge(tblQueryChargeRecord);
		logger.debug("chargeList count:{}",chargeList.size());
		TblChargingrecord tblChargeRecord=null;
		if (chargeList != null && chargeList.size() > 0) {
			tblChargeRecord = chargeList.get(0);
		}
		
		if(tblChargeRecord==null)
			return null;
		
		return convertFromDb(tblChargeRecord);
	}

	
	public static ChargeCache GetChargeCacheFromDb(String serialNo)
	{
		TblChargingrecord tblChargeRecord=null;
		String chreTransactionnumber = serialNo;
		List<TblChargingrecord> chargeList = DB.chargingrecordDao.getByTranNumber(chreTransactionnumber);
		logger.debug("chargeList count:{}",chargeList.size());
		if (chargeList != null && chargeList.size() > 0) {
			tblChargeRecord = chargeList.get(0);
		}
		
		if(tblChargeRecord==null)
			return null;
		
		return convertFromDb(tblChargeRecord);
	}
	
	/**
	 * 参数检查
	 * @param epCode
	 * @param account
	 * @return
	 */
	private static int checkElectricDb(int orgNo,String epCode, int epGunNo, String account, short startChargeStyle,int serverType)
	{
		//检查用户
		int accountId = 0;
        if (orgNo == UserConstants.ORG_I_CHARGE) {
            accountId = UserService.findUserId(orgNo, account, serverType);
            if (accountId == 0) {
                logger.error("apiStartElectric,orgNo:{},account:{}", orgNo, account);
                return ErrorCodeConstants.INVALID_ACCOUNT;
            }
        } else {
			UserRealInfo userRealInfo = UserService.findUserRealInfo(orgNo, account);
			if (null == userRealInfo) {
				return ErrorCodeConstants.INVALID_ACCOUNT;
			}
            accountId = userRealInfo.getId();
		}

		//检查电桩
		ElectricPileCache epCache = EpService.getEpCacheFromDB(epCode);
		if (epCache == null) return ErrorCodeConstants.EP_UNCONNECTED;
		
		//检查桩和枪
		int error = epCache.canCharge(epGunNo);
		if (error > 0) return error;
		EpGateNetConnect commClient = CacheService.getEpGate(epCache.getGateid());
		error = EpService.checkEpGate(commClient);
		if (error > 0) return error;

		EpGunCache epGunCache = EpGunService.getEpGunCache(epCache.getPkEpId(),epCode, epGunNo);
		if(epGunCache==null)
		{
			logger.error("init error!did not find gun,pkEpId:{},epGunNo:{}",epCache.getPkEpId(),epGunNo);
			return ErrorCodeConstants.INVALID_EP_GUN_NO;
		}
		error = epGunCache.canCharge(startChargeStyle);
		if (error > 0) return error;

		//用户能否充电
        if (orgNo == UserConstants.ORG_I_CHARGE) {
            error = UserService.canCharge(accountId);
            if (error > 0) return error;
        }

		//实时装载未完成的任务
		epGunCache.loadUnFinishedWork(serverType);
		error = epGunCache.canCharge(startChargeStyle, accountId, true);
		if (error > 0) return error;

		return 0;
	}

	/**
	 * api开始充电
	 * @param epCode
	 * @param epGunNo
	 * @param accountId
	 * @param account
	 * @param bespNo
	 * @param ermFlag
	 * @param appClientIp
	 * @param frozenAmt
	 * @param source,但来自于爱充的用户需要收费，来自于其他合作伙伴有可能不冻结钱.只记录充电和消费记录
	 * @return
	 */
	public static int apiStartElectric(String token,int orgNo,String userIdentity,String epCode,int epGunNo,
			short startChargeStyle,int chargingAmt,int payMode, int watchPrice,
			String carCode, String vinCode, int serverType)
	{
		//1.电桩编号长度不对，不能充电
		if(epCode.length() !=16)
		{
			return ErrorCodeConstants.INVALID_EP_CODE;
		}
		
		int error = checkElectricDb(orgNo,epCode, epGunNo, userIdentity, startChargeStyle, serverType);
		
		if (error > 0) return error;

		EpGunCache epGunCache = CacheService.getEpGunCache(epCode, epGunNo);
		
		int errorCode = epGunCache.startChargeAction(token, orgNo, userIdentity,epCode, epGunNo, startChargeStyle, chargingAmt,
				payMode, watchPrice, carCode, vinCode, serverType);
		
		if (errorCode>0) return errorCode;
		
		return 0;
	}
	public static int apiStopElectric(String token,int orgNo,String userIdentity,String epCode,int epGunNo)
	{
		if(epCode.length() !=16)
		{
			return ErrorCodeConstants.INVALID_EP_CODE;
		}
		
		//检查电桩
		ElectricPileCache epCache = CacheService.getEpCache(epCode);
		if(epCache == null)
		{
			epCache = EpService.getEpCacheFromDB(epCode);
			if (epCache == null) return ErrorCodeConstants.EP_UNCONNECTED;
		}

		EpGateNetConnect commClient = CacheService.getEpGate(epCache.getGateid());
		int error = EpService.checkEpGate(commClient);
		if (error > 0) return error;
		
		if(epGunNo<1|| epGunNo> epCache.getGunNum())
		{
			return ErrorCodeConstants.INVALID_EP_GUN_NO;
		}
		
		EpGunCache epGunCache= EpGunService.getEpGunCache(epCache.getPkEpId(), epCode, epGunNo);
		//桩断线，不能结束充电
 		if(epGunCache ==null )
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		
		return epGunCache.stopChargeAction(token,orgNo,userIdentity,commClient);
	}

	public static int initClientConnect(String token,int orgNo,String userIdentity,
			String epCode, int epGunNo, String checkCode) {

		//因为电桩的实时属性,完全从桩找
		ElectricPileCache epCache = EpService.getEpCacheFromDB(epCode);
		if (epCache == null) return ErrorCodeConstants.EP_UNCONNECTED;

		//检查桩和枪
		int error = epCache.canCharge(epGunNo);
		if (error > 0) return error;
		
		EpGateNetConnect commClient = CacheService.getEpGate(epCache.getGateid());
		error = EpService.checkEpGate(commClient);
		if (error > 0) return error;
        UserRealInfo userRealInfo = UserService.findUserRealInfo(Integer.valueOf(userIdentity), Integer.valueOf(token));
		if (null == userRealInfo) {
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		/*if (userRealInfo.getLevel()!=6) {
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		token = userRealInfo.getAccount();*/
		error = userRealInfo.canCharge();
		if (error > 0) {
			return error;
		}

		// 验证码
		String src = userRealInfo.getDeviceid() + userRealInfo.getPassword()
				+ userRealInfo.getId();
		String calcCheckCode = WmIce104Util.MD5Encode(src.getBytes());

		if (calcCheckCode.compareTo(checkCode) != 0) {
			logger.info(LogUtil.addBaseExtLog("checkCode|calcCheckCode"),
					new Object[]{LogConstants.FUNC_PHONE_INIT,epCode,epGunNo,orgNo,userIdentity,checkCode,calcCheckCode});
			return ErrorCodeConstants.ERROR_PHONE_CRC_CODE;
		}
		CacheService.convertToCache(userRealInfo);

		EpGunCache epGunCache = EpGunService.getEpGunCache(epCache.getPkEpId(),epCode, epGunNo);
		if (epGunCache == null) {
			return ErrorCodeConstants.EP_UNCONNECTED;
		}

		//实时装载未完成的任务
		epGunCache.loadUnFinishedWork(Integer.valueOf(token));
		error = epGunCache.canCharge(EpConstants.CHARGE_TYPE_QRCODE, userRealInfo.getId(), true);
		if (error > 0) return error;

		commClient.setLastSendTime(DateUtil.getCurrentSeconds());
		EpGateService.sendClientConnect(commClient.getChannel(),epCode,epGunNo,userRealInfo.getId());

		return 0;
	}
	
	public static int queryOrderInfo(String token,int orgNo,String userIdentity,String epCode,int epGunNo)
	{
		if(epCode.length() !=16)
		{
			return ErrorCodeConstants.INVALID_EP_CODE;
		}
		
		//检查电桩
		ElectricPileCache epCache = EpService.getEpCache(epCode);
		if(epCache == null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}

		EpGateNetConnect commClient = CacheService.getEpGate(epCache.getGateid());
		int error = EpService.checkEpGate(commClient);
		if (error > 0) return error;
		
 		commClient.setLastSendTime(DateUtil.getCurrentSeconds());
 		EpGateService.sendOrderInfo(commClient.getChannel(), epCode, epGunNo, orgNo, userIdentity, token);
		return 0;
	}
	
	public static int phoneDisconnect(int orgNo,String userIdentity,String epCode,int epGunNo)
	{
		//检查电桩
		ElectricPileCache epCache = EpService.getEpCache(epCode);
		if(epCache == null)
		{
			logger.debug("phoneDisconnect errorCode:{}", ErrorCodeConstants.EP_UNCONNECTED);
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		EpGunService.unUseEpGun(epCode, epGunNo, orgNo, userIdentity);

		EpGateNetConnect commClient = CacheService.getEpGate(epCache.getGateid());
		int error = EpService.checkEpGate(commClient);
		if (error > 0) {
			logger.debug("phoneDisconnect errorCode:{}", error);
			return error;
		}
		
 		commClient.setLastSendTime(DateUtil.getCurrentSeconds());
 		EpGateService.sendClientOnline(commClient.getChannel(), Integer.valueOf(userIdentity), 0);
		return 0;
	}
}
	
