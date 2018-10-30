package com.ec.usrcore.service;

import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.usrcore.cache.ElectricPileCache;
import com.ec.usrcore.net.client.EpGateNetConnect;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblElectricPile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EpService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpService.class.getName()));
	
	
	/**
	 * 包含了所有实时属性，然后覆盖
	 * @param epCode
	 * @return
	 */
	public static ElectricPileCache getEpCacheFromDB(String epCode)
	{
		List<TblElectricPile> epList = DB.epClientDao.findResultObject(epCode);
		if(epList ==null)
		{
			logger.error(LogUtil.addExtLog("epList is null,epCode"),epCode);
			return null;
		}
		int size=epList.size();
		if(size !=1)
		{
			logger.error(LogUtil.addExtLog("epList.size|epCode"),size,epCode);
			return null;
		}
		 
		ElectricPileCache epCache = convertCache( epList.get(0));
		
		CacheService.addEpCache(epCache);
			
		return epCache;
	}
	
	private static ElectricPileCache convertCache(TblElectricPile dbEp)
	{
		ElectricPileCache epCache = new ElectricPileCache();
		epCache.setPkEpId(dbEp.getPkEpId());
		epCache.setCode(dbEp.getEpCode());
		epCache.setCurrentType(dbEp.getCurrentType());
		epCache.setGunNum(dbEp.getEpGunNum());
		
		epCache.setRateid(dbEp.getRateid());
		epCache.setGateid(dbEp.getGateid());
		epCache.setCompany_number(dbEp.getCompany_number());
		epCache.setState(dbEp.getElpiState());
		epCache.setDeleteFlag(dbEp.getDeleteFlag());
		epCache.setNetStatus(dbEp.getComm_status());
       	
		return epCache;
	}
	
	public static void getEpRealAttrFormDB(ElectricPileCache epCache)
	{
		String epCode =epCache.getCode();
		List<TblElectricPile> epList = DB.epClientDao.findResultObject(epCode);
		if(epList ==null)
		{
			logger.error(LogUtil.addExtLog("epList is null,epCode"),epCode);
			return ;
		}
		int size=epList.size();
		if(size !=1)
		{
			logger.error(LogUtil.addExtLog("epList.size|epCode"),size,epCode);
			return ;
		}
		
		TblElectricPile dbEp = epList.get(0);
		epCache.setRateid(dbEp.getRateid());
		epCache.setGateid(dbEp.getGateid());
		epCache.setCompany_number(dbEp.getCompany_number());
		epCache.setState(dbEp.getElpiState());
		epCache.setDeleteFlag(dbEp.getDeleteFlag());
		
	}
   
	public static int getCurrentType(String epCode)
	{
		ElectricPileCache epClient= CacheService.getEpCache(epCode);
		if(	epClient ==null )
		{
			return -1;
		}
		return epClient.getCurrentType();
	}

    public static ElectricPileCache getEpCache(String epCode)
	{
    	ElectricPileCache epCache = CacheService.getEpCache(epCode);
    	if(epCache!=null)
    		return epCache;
    	
    	return EpService.getEpCacheFromDB(epCode);
	}
	
	public static int checkEpCache(ElectricPileCache epCache)
	{
		if (epCache == null) return ErrorCodeConstants.EP_UNCONNECTED;

		if (epCache.getState() == EpConstants.EP_STATUS_OFFLINE) {
			return ErrorCodeConstants.EP_PRIVATE;
		} else if (epCache.getState() < EpConstants.EP_STATUS_OFFLINE) {
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}
		if (epCache.getDeleteFlag() != 0) {
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}

		return 0;
	}
	
	public static int checkEpGate(EpGateNetConnect commClient)
	{
		if (commClient == null) {
			logger.error(LogUtil.addExtLog("commClient is null"),commClient);
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if (!commClient.isComm()) {
			logger.error(LogUtil.addExtLog("commClient is not connect"),commClient);
			return ErrorCodeConstants.EP_UNCONNECTED;
		}

		return 0;
	}
}
