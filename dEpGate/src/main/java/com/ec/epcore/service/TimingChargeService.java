package com.ec.epcore.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.TimingChargeConstants;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblElectricPile;
import com.ormcore.model.TblTimingCharge;

public class TimingChargeService {
	
	private static final Logger logger =  LoggerFactory.getLogger(LogUtil.getLogName(TimingChargeService.class.getName()));
	
	/**
	* <p>Title: 查找定时充电列表</p>
	* <p>Description: </p>
	* @param
	* @author bingo
	* @date 2017-5-15下午1:58:20
	 */
	public static List<TblTimingCharge> getTimingChargeList(TblTimingCharge qryTimingCharge){
		return DB.timingChargeDao.findTimingCharge(qryTimingCharge);
	}
	
	public static TblElectricPile getDbElectricPile(String epCode)
	{
		TblElectricPile epInfo=null;
		List<TblElectricPile> epList = DB.epClientDao.findResultObject(epCode);
		if(epList ==null || epList.size() !=1)
		{
			//都没有的话断定为无效桩,强制断掉该客户连接
			logger.error(LogUtil.addExtLog("initConnect fail,have not ElectricPile in DB,epCode:"),new Object[]{epCode});
		}
		else
		{
			epInfo = epList.get(0);
		}
		return epInfo;
	}
	
	/**
	 * 下发定时充电
	 * @param epCodes 电桩编号
	 * @param time    定时时间
	 * @param timingChargeFlag  定时开关
	 * @return
	 */
	public static int sendTimingCharge(String epCodes, String time, int timingChargeStatus)
	{
		String[] epCodeArray = epCodes.split(",");
		
		for(String epCode : epCodeArray)
  	    {
			try {
				//验证电桩编号的准确性
				getDbElectricPile(epCode);
				
				ElectricPileCache epClient = EpService.getEpByCode(epCode);
				if(epClient == null)
				{
					logger.error(LogUtil.addExtLog("ElectricPileCache is null,epCode:"),new Object[]{epCode});
				}
				
				EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
				
				if(commClient == null || !commClient.isComm())
				{
					logger.error(LogUtil.addExtLog("EpCommClient is null,epCode:"),new Object[]{epCode});
				}
		   
				byte[] cmdTimes = WmIce104Util.timeToByte();
		     	
				//转换成字节码
		 		byte[] bTimingChargeData = EpEncoder.doIssuedTimingCharge(epCode, time, timingChargeStatus);
		 		 
		 		if(bTimingChargeData == null)
				{
		 			logger.error(LogUtil.addExtLog("bTimingChargeData is null exception;epCode|time|timingChargeStatus"),
	                 		 new Object[]{epCode, time, timingChargeStatus});
				}
		 		
		 		EpMessageSender.sendMessage(commClient, 0, 0, Iec104Constant.C_SET_EP_TIMINGCHARGE, bTimingChargeData, cmdTimes, commClient.getVersion());
		 		
		 		//更新定时充电列表中桩的状态
		 		TblTimingCharge updTimingCharge = new TblTimingCharge();
		 		updTimingCharge.setElpiElectricPileCode(epCode);
		 		updTimingCharge.setTimingChargeStatus(timingChargeStatus);
		 		updTimingCharge.setIssuedStatus(TimingChargeConstants.ISSUED_TIMING_CHARGE_STATUS_UNREC);
		 		updTimingCharge.setUpdateDate(DateUtil.currentDate());
		 		DB.timingChargeDao.updateTimingCharge(updTimingCharge);
			} catch (Exception e) {
				logger.error(LogUtil.addExtLog("Send timing charge error with: epCode|Msg"),
                		 new Object[]{epCode, e.getMessage()});
			}
  	    }
		
		return 0;
	}
}
