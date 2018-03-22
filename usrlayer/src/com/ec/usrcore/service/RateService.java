package com.ec.usrcore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.EpConstants;
import com.ec.usrcore.cache.ElectricPileCache;
import com.ec.usrcore.cache.RateInfoCache;
import com.ec.usrcore.config.GameBaseConfig;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.RateInfo;
import com.ormcore.model.TblPurchaseHistory;

public class RateService {
	private static final Logger logger = LoggerFactory
			.getLogger(LogUtil.getLogName(RateService.class.getName()));
	
	//费率缓存
	public static Map<Integer, RateInfoCache> rateMap = new ConcurrentHashMap<Integer,RateInfoCache>();
	
	private static long lastFetchRate=0;
	
	
	
	public static void init()
	{
		if(lastFetchRate ==0)
		{
			//取得全部费率
			List<RateInfo> rateList= DB.rateInfoDao.getAll();
			int rateListSize= rateList.size();
			for(int i = 0; i < rateListSize; i++)  
			{  
		       RateInfo rateInfo= rateList.get(i);
		       RateInfoCache rateInfoCache = convertFromDb(rateInfo);
		       
		       if(rateInfoCache.parseStage())
		       {
		    	   logger.debug(LogUtil.addExtLog("add rate,id"),rateInfo.getId());
		    	   RateService.AddRate(rateInfo.getId(),rateInfoCache);
		       }
		       else
		       {
		    	   logger.error(LogUtil.addExtLog("rate info fail,rate id|rateInfo"),rateInfo.getId(),rateInfo.getQuantumDate());
		       }
			} 
			
			logger.debug(LogUtil.addExtLog("rateList,size"),rateList.size());
			
			long initDelay = getRateUpdateTime();
			
			logger.debug(LogUtil.addExtLog("startCheckRateTimer sec after initDelay"),initDelay);
		}
	}
	
	public static RateInfoCache convertFromDb(RateInfo rateInfo)
	{
		if(rateInfo ==null)
			return null;
		RateInfoCache rateInfoCache = new RateInfoCache();
	    rateInfoCache.setRateInfo(rateInfo);
		
		return rateInfoCache;
	}
	
	public static void checkModifyRate()
	{
		List<RateInfo> rateList= DB.rateInfoDao.getLastUpdate();
		int rateListSize= rateList.size();
		for(int i = 0; i < rateListSize; i++)  
		{  
	       RateInfo rateInfo= rateList.get(i);  
	       RateInfoCache rateInfoCache = convertFromDb(rateInfo);
	       
	       if(rateInfoCache.parseStage())
	       {
	    	   logger.debug(LogUtil.addExtLog("add rate,id"),rateInfo.getId());
	    	   RateService.AddRate(rateInfo.getId(),rateInfoCache);
	       }
		} 
	}
	
	
	public static RateInfoCache getRateInfo(String epCode)
	{
		ElectricPileCache electricUser = CacheService.getEpCache(epCode);
		if(electricUser == null)
		{
			logger.error(LogUtil.addExtLog("dont find getRateInfo,epCode"),epCode);
			return null;
		}
		
		int rateInfoId = electricUser.getRateid();
		return RateService.getRateById(rateInfoId);
	}
	
	public static synchronized RateInfoCache getRateById(Integer Id){
		RateInfoCache rate = rateMap.get(Id);
		return rate;
	}
	public static synchronized void AddRate(Integer Id,RateInfoCache rate)
	{
		if(Id>0&&rate!=null)
		{
			rateMap.put(Id, rate);
		}
		else
		{
			logger.error(LogUtil.addExtLog("fail,because of,Id|rate"),Id,rate);
		}
	}
	//小于30分钟或第小于5分钟不收钱
	/**
	 * 1.第一个30分钟，不足三十分钟按照三十分钟计算
	 * 2.后面的三十分钟,小于5分钟不收钱,5-30按照三十分钟收钱
	 * @param realBespTime
	 * @return
	 */
	public static long calcBespTime(long realBespTime){
		if (realBespTime<0) {
			logger.error(LogUtil.addExtLog("realBespTime"),realBespTime);
			return 0;
		}
		long minUnit= EpConstants.MIN_BESP_TIME_UINT;//按三十分钟计价，不足三十分钟按三十分钟算
		long n1 = (int)realBespTime%minUnit;
		long n2 = (int)realBespTime/minUnit;
		long retRealBespTime=0;
		if(n2==0||(n2>=1 &&n1>EpConstants.FREE_BESP_TIME))
		{
			//1.第一个30分钟，不足三十分钟按照三十分钟计算
			//2.后面的三十分钟,小于5分钟不收钱,5-30按照三十分钟收钱
			retRealBespTime = ((realBespTime/minUnit)+1)*minUnit /60;
		}
		else
		{
			if(n1<=EpConstants.FREE_BESP_TIME)//如果小于5分钟，扔掉
			{
				realBespTime = realBespTime-n1;
			}
			retRealBespTime = realBespTime /60;
		}
		logger.info(LogUtil.addExtLog("realBespTime|retRealBespTime"),realBespTime,retRealBespTime);
		return retRealBespTime;
	}
	
	public static long calcBespTime(long st,long et,long user_cancel_t){
		if(et<st)
		{
			logger.debug(LogUtil.addExtLog("st|et"),st,et);
			return 0;
		}
		if(st<10000)
		{
			logger.debug(LogUtil.addExtLog("st"),st);
			return 0;
		}
		if(user_cancel_t>0)
		{
			long diff = user_cancel_t-st;

            et = st + diff;
		}
		
		long realBespTime = et- st;
		logger.debug(LogUtil.addExtLog("st|et|user_cancel_t"),new Object[]{st,et,user_cancel_t});
		
		return calcBespTime(realBespTime);
	}
	
	public static BigDecimal calcBespAmt(BigDecimal bespokeRate,long bespTime)
	{
		java.math.BigDecimal bespAmt=new BigDecimal(bespTime) ;
		
		bespAmt = bespAmt.multiply(bespokeRate);
		bespAmt = bespAmt.setScale(2,BigDecimal.ROUND_HALF_UP); 
		
		return bespAmt;
	}

	
	public  static String getPayModeDesc(int pmMode)
	{
		String desc="";
		switch(pmMode)
		{
		case 1:
			desc="先付费";
			break;
		case 2:
			desc="后付费";
			break;
		
		default:
			desc="未知状态";
			break;
		}
		return desc;
	}

	public static long getRateUpdateTime()
	{
		long now = DateUtil.getCurrentSeconds();
		long rateUpdateTime = GameBaseConfig.rateUpdateTime;
		
		if(now>(rateUpdateTime+600))//10分钟之内就等到第二天更新
			return DateUtil.getRemainSecondsOfCurDay() + rateUpdateTime;
		else
		{
			return now - rateUpdateTime;
		}
	}

}
