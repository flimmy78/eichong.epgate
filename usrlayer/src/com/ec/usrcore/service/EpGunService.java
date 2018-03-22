package com.ec.usrcore.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.GunConstants;
import com.ec.constants.UserConstants;
import com.ec.usrcore.cache.AuthUserCache;
import com.ec.usrcore.cache.EpGunCache;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblElectricPileGun;

public class EpGunService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGunService.class.getName()));
	
	public static TblElectricPileGun getDbEpGun(int pkEpId,int epGunNo)
	{
		TblElectricPileGun tblEpGun= new TblElectricPileGun();
		tblEpGun.setPkEpId(pkEpId);
		tblEpGun.setEpGunNo(epGunNo);
		
		TblElectricPileGun dbEpGun=null;
		List<TblElectricPileGun> epGunList=DB.epGunDao.findEpGunInfo(tblEpGun);
		
		if(epGunList==null)
		{
			logger.error(LogUtil.addExtLog("not find dbEpGun,epGunList is null!pkEpId|epGunNo"),pkEpId,epGunNo);
			return null;
		}
			
		if(epGunList.size()!=1)
		{
			logger.error(LogUtil.addExtLog("not find dbEpGun,pkEpId|epGunList.size"),pkEpId,epGunList.size());
			return null;
		}
		
		dbEpGun = epGunList.get(0);
		return dbEpGun;
	}
	
	public static EpGunCache getEpGunCache(int pkEpId,String epCode,int epGunNo)
	{
		EpGunCache epGunCache = CacheService.getEpGunCache(epCode, epGunNo);
		
		TblElectricPileGun tblEpGun= getDbEpGun(pkEpId,epGunNo);
		if(tblEpGun==null)
		{
			logger.error(LogUtil.addExtLog("init error!did not find gun,pkEpId|epGunNo"),pkEpId,epGunNo);
			return null;
		}

		if (epGunCache == null) epGunCache= new EpGunCache(pkEpId,epCode,epGunNo);
		int epGunStatusInDb= tblEpGun.getEpState();
		//以数据库最后枪头状态为准
		epGunCache.modifyStatus(epGunStatusInDb, false);
		
		epGunCache.setPkEpGunId(tblEpGun.getPkEpGunId());
		
		CacheService.putEpGunCache(epGunCache);
		
		return epGunCache;

		
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
		
			return false;
		
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void checkTimeout()
	{
		
		Iterator iter = CacheService.getMapGun().entrySet().iterator();
		
		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();
			
			EpGunCache epGunCache=(EpGunCache) entry.getValue();
			if(null == epGunCache )
			{
				logger.info(LogUtil.getExtLog("checkTimeout: epGunCache is null"));
				continue;
			}
			
			
		}
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

	public static void updateGunState(int pkEpGunId,int status)
	{
		TblElectricPileGun info= new TblElectricPileGun();
		info.setPkEpGunId(pkEpGunId);
		info.setEpState(status);
		DB.epGunDao.updateGunState(info);
	}

	public static void unUseEpGun(String epCode,int epGunNo,int orgNo,String userIdentity)
	{
		EpGunCache gunCache = CacheService.getEpGunCache(epCode, epGunNo);
		if(gunCache==null)
			return ;
		AuthUserCache auth =  gunCache.getAuth();
		if(auth==null)
			return ;
		
		if(orgNo != UserConstants.ORG_I_CHARGE)
			return ;
		
		if(userIdentity.equals(auth.getAccount()))
		{
			logger.info(LogUtil.getBaseLog(),new Object[]{epCode,epGunNo,orgNo,userIdentity});
			gunCache.setAuth(null);
		}
	}
	  
}
