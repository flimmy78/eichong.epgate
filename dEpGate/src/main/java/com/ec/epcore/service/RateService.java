package com.ec.epcore.service;

import com.ec.constants.EpConstants;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.RateInfoCache;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.task.CheckRateTask;
import com.ec.net.proto.Iec104Constant;
import com.ec.netcore.core.pool.TaskPoolFactory;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.FavRecord;
import com.ormcore.model.RateInfo;
import com.ormcore.model.TblPurchaseHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateService {
	private static final Logger logger = LoggerFactory
			.getLogger(RateService.class);
	
	//费率缓存
	public static Map<Integer, RateInfoCache> rateMap = new ConcurrentHashMap<Integer,RateInfoCache>();
	
	
	public static void startCheckRateTimer(long initDelay) {
		
		CheckRateTask checkTask =  new CheckRateTask();
				
		TaskPoolFactory.scheduleAtFixedRate("CHECK_RATE_UPDATE_TASK", checkTask, initDelay, 24*60*60, TimeUnit.SECONDS);
		
		//TaskPoolFactory.scheduleAtFixedRate("CHECK_NET_TIMEOUT_TASK", checkTask, initDelay, 300, TimeUnit.SECONDS);
	}
	
	public static RateInfoCache convertFromDb(RateInfo rateInfo)
	{
		if(rateInfo ==null)
			return null;
		RateInfoCache rateInfoCache = new RateInfoCache();
	    rateInfoCache.setRateInfo(rateInfo);
		
		return rateInfoCache;
	}
	
	public static void parseRates(List<RateInfo> rateList)
	{
		int rateListSize= rateList.size();
		logger.info("[Rate]parseRates rateList,size:{}",rateListSize);
		
		if(rateListSize<1)
			return ;
		
		for(int i = 0; i < rateListSize; i++)  
		{  
	       RateInfo rateInfo= rateList.get(i);  
	       RateInfoCache rateInfoCache = convertFromDb(rateInfo);
	       if(rateInfoCache.parseStage())
	       {
	    	   logger.debug("add rate,id:{}",rateInfo.getId());
	    	   RateService.AddRate(rateInfo.getId(),rateInfoCache);
	       }
	       else
	       {
	    	   logger.error("[Rate]parseRate fail,rateId:{},rateInfo:{}",rateInfo.getId(),rateInfo.getQuantumDate());
	       }
		}
	}
	
	public static void apiUpdateRateAction(String epCodes, int rateId)
	{
		RateInfo rateInfo = DB.rateInfoDao.findRateInfofromId(rateId);
		
		if(rateInfo ==null)
		{
			logger.info("[Rate]RateCmd send to ep fail,not find rateinfo in db,rateId:{}",rateId);
			return ;
		}
		RateInfoCache rateInfoCache = convertFromDb(rateInfo);
	    if(!rateInfoCache.parseStage())
	    {
	    	rateInfoCache = RateService.getRateById(rateId);
	    }
	    if(rateInfoCache == null)
	    {
	    	logger.info("[Rate]RateCmd send to ep fail,not find rateinfo in memory,rateId:{}",rateId);
	 	     
	    	return ;
	    }
	    RateService.AddRate(rateId, rateInfoCache);
	    
	    //内存里面有，等待更新的桩和现在的费率不同的下发到桩
	    EpService.updateEpsRate(epCodes,rateId,rateInfoCache);
	     
		
	}
	
	public static void init()
	{
		long initDelay = getRateInitDelayTime();
		
		logger.info("[Rate]startCheckRateTimer {} sec after",initDelay);
	
		startCheckRateTimer(initDelay);
		
		//取得全部费率
		List<RateInfo> rateList= DB.rateInfoDao.getAll();
		
		parseRates(rateList);
	}
	/**
	 * 取最近一天更新过的费率
	 */
	public static void checkModifyRate()
	{
		List<RateInfo> rateList= DB.rateInfoDao.getLastUpdate();
	
		parseRates(rateList);
	}
	
	/*
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
		    	   logger.debug("add rate,id:{}",rateInfo.getId());
		    	   RateService.AddRate(rateInfo.getId(),rateInfoCache);
		       }
		       else
		       {
		    	   logger.error("init rate info fail,rate id{},rateInfo:{}",rateInfo.getId(),rateInfo.getQuantumDate());
		       }
			} 
			
			logger.debug("rateList,size:",rateList.size());
			
			long initDelay = getRateUpdateTime();
			
			logger.debug("startCheckRateTimer {} sec after",initDelay);
		
			startCheckRateTimer(initDelay);
			
		}
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
	    	   logger.debug("add rate,id:{}",rateInfo.getId());
	    	   RateService.AddRate(rateInfo.getId(),rateInfoCache);
	       }
		} 
	}
	
	*/
	
	public static RateInfoCache getRateInfo(String epCode)
	{
		ElectricPileCache electricUser =  EpService.getEpByCode(epCode);
		if(electricUser == null)
		{
			logger.error("[Rate]getRateInfo from epCache fail,dont find ElectricPileClient:{} ",epCode);
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
			logger.error("[Rate]AddRate to rateMap fail,because of,rateId:{},rate:{}",Id,rate);
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
		if(realBespTime<0)
		{
			logger.error("endBespoke calcBespTime error,realBespTime:{}",realBespTime);
			return 0;
		}
		long minUnit= GameConfig.minBespTimeUnit;//按三十分钟计价，不足三十分钟按三十分钟算
		long n1 = (int)realBespTime%minUnit;
		long n2 = (int)realBespTime/minUnit;
		long retRealBespTime=0;
		if(n2==0||(n2>=1 &&n1>GameConfig.freeBespTime))
		{
			//1.第一个30分钟，不足三十分钟按照三十分钟计算
			//2.后面的三十分钟,小于5分钟不收钱,5-30按照三十分钟收钱
			retRealBespTime = ((realBespTime/minUnit)+1)*minUnit /60;
		}
		else
		{
			if(n1<=GameConfig.freeBespTime)//如果小于5分钟，扔掉
			{
				realBespTime = realBespTime-n1;
			}
			retRealBespTime = realBespTime /60;
		}
		logger.info("endBespoke calcBespTime,realBespTime:{},retRealBespTime:{}",realBespTime,retRealBespTime);
		return retRealBespTime;
	}
	
	public static long calcBespTime(long st,long et,long user_cancel_t){
		if(et<st)
		{
			logger.debug("calcBespTime,st:{},et:{}",st,et);
			return 0;
		}
		if(st<10000)
		{
			logger.debug("calcBespTime,st:{}",st);
			return 0;
		}
		if(user_cancel_t>0)
		{
			long diff = user_cancel_t-st;

            et = st + diff;
		}
		
		long realBespTime = et- st;
		logger.debug("calcBespTime,st:{},et:{}",st,et);
		logger.debug("calcBespTime,user_cancel_t:{}",user_cancel_t);
		
		return calcBespTime(realBespTime);
	}
	
	public static BigDecimal calcBespAmt(BigDecimal bespokeRate,long bespTime)
	{
		java.math.BigDecimal bespAmt=new BigDecimal(bespTime) ;
		
		bespAmt = bespAmt.multiply(bespokeRate);
		bespAmt = bespAmt.setScale(2,BigDecimal.ROUND_HALF_UP); 
		
		return bespAmt;
		
	}
	public static void handleConsumeModelReq(EpCommClient CommClient,String epCode,byte [] time) 
	{
		RateInfoCache rateInfo = RateService.getRateInfo(epCode);
		if (rateInfo == null) {
			int rateInfoId = EpService.getEpByCode(epCode).getRateid();
			RateInfo newRateInfo = DB.rateInfoDao.findRateInfofromId(rateInfoId);

			if(newRateInfo ==null) {
				logger.error("[Rate]handleConsumeModelReq fail,rateInfo is null,epCode:{},rateInfoId:{}",epCode,rateInfoId);
				return;
			}
			rateInfo = RateService.convertFromDb(newRateInfo);
			if(!rateInfo.parseStage())
			{
				rateInfo = RateService.getRateById(rateInfoId);
			}
			if(rateInfo == null) {
				logger.error("[Rate]handleConsumeModelReq fail,rateInfo is null,epCode:{},rateInfoId:{}",epCode,rateInfoId);
				return;
			}
			RateService.AddRate(rateInfoId, rateInfo);
		}

		if (null != rateInfo) 
		{
			byte[] bConsumeModelProtoData = EpEncoder.do_consume_model(epCode, rateInfo);
			if(bConsumeModelProtoData==null)
			{
				logger.error("[Rate]handleConsumeModelReq exception,epCode:{}",epCode);
			}
			int modelId = Iec104Constant.C_CONSUME_MODEL;
			if (rateInfo.getRateInfo().getModelId() == 2) {
                modelId = Iec104Constant.C_CONSUME_MODEL4;
            } else if (rateInfo.getRateInfo().getModelId() == 3) {
                modelId = Iec104Constant.C_CONSUME_MODEL6;
            }
			EpMessageSender.sendMessage(CommClient,0,0,modelId, bConsumeModelProtoData,time,CommClient.getVersion());
		} else {
			logger.info("[Rate]handleConsumeModelReq fail,not found rate info from epCache:{}", epCode);
		}
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

	public static void addPurchaseHistoryToDB(BigDecimal cost,int type,int userId,int userOrigin,
											  String content,String epCode,String serialNo,String bespokeNo)
	{
		addPurchaseHistoryToDB(cost, type, userId, userOrigin, content, epCode, serialNo, bespokeNo, 0);
	}

	public static int addPurchaseHistoryToDB(BigDecimal cost,int type,int userId,int userOrigin,
			String content,String epCode,String serialNo,String bespokeNo,int accountId)
	{
		int payMode = EpConstants.P_M_FIRST;
		java.util.Date phNow = new java.util.Date();
		if(Math.abs(userOrigin)>9999)
		{
			logger.info("userOrigin:{}",userOrigin);
			userOrigin=0;
		}
		TblPurchaseHistory phInfo = new TblPurchaseHistory(phNow,cost,"",type,userId,userOrigin,content,epCode,
				serialNo,bespokeNo,accountId);
		try
		{
			int count = DB.phDao.getCount(phInfo);
			if (count > 0) return 0;
			int account_id = DB.phDao.getAccountId(phInfo);
			if (account_id != 0) {
				accountId = account_id;
			}
			if (accountId != 0) {
				phInfo.setAccount_id(accountId);
				DB.phDao.insertPurchaseRecord(phInfo);
				payMode = DB.phDao.getPayMode(accountId);
			} else {
				logger.error(LogUtil.addFuncExtLog("purchase","fail,accountId=0, epCode|serialNo"),
						new Object[] { epCode, serialNo });
			}
		}
		catch(Exception e)
		{
			logger.error("addPurchaseHistoryToDB execption,getStackTrace:{}",e.getStackTrace());
		}
		return payMode;
	}

	public static void addCouponToDB(BigDecimal cost,int userId,int userOrigin,
			String content,String epCode,String serialNo,int accountId)
	{
		if(Math.abs(userOrigin)>9999)
		{
			logger.info("userOrigin:{}",userOrigin);
			userOrigin=0;
		}
		try
		{
			if (accountId != 0) {
				FavRecord favRecord = new FavRecord();
				favRecord.setOrderCode(serialNo);
				favRecord.setAccountId(Long.valueOf(accountId));
				favRecord.setCpyId(Long.valueOf(userOrigin));
				favRecord.setBillAccountId(1l);
				favRecord.setFavourableId(Long.valueOf(content));
				favRecord.setFavourableMoney(String.valueOf(cost));
				favRecord.setUserId(Long.valueOf(userId));
				DB.favRecordDao.FavRecord_insert(favRecord);
			} else {
				logger.error(LogUtil.addFuncExtLog("purchase","fail,accountId=0, epCode|serialNo"),
						new Object[] { epCode, serialNo });
			}
		}
		catch(Exception e)
		{
			logger.error("addCouponToDB execption,getStackTrace:{}",e.getStackTrace());
		}
	}

	public static long getRateInitDelayTime()
	{
		long remainSecondsOfCurDay = DateUtil.getRemainSecondsOfCurDay();
		long secondsOfCurDay = 24 * 3600 - remainSecondsOfCurDay ;
		long rateUpdateTime = GameConfig.rateUpdateTime;
		
		//如果在10分钟之内,那么定时
		if(Math.abs(secondsOfCurDay -rateUpdateTime) <GameConfig.rateUpdateTimediff)
		{
			return DateUtil.getRemainSecondsOfCurDay() + rateUpdateTime;
		}
		if(secondsOfCurDay>rateUpdateTime)
		{
			return DateUtil.getRemainSecondsOfCurDay() + rateUpdateTime;
		}
		else
		{
			return rateUpdateTime- secondsOfCurDay;
		}
	}

}
