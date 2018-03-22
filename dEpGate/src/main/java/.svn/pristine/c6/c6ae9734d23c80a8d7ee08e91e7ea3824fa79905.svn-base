package com.ec.epcore.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.ChargeOrderConstants;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.ShEpEncoder;
import com.ec.epcore.net.proto.ConsumeRecord;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.utils.DateUtil;
import com.ec.utils.NumUtil;
import com.ec.utils.StringUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargingOrder;
import com.ormcore.model.TblChargingfaultrecord;
import com.ormcore.model.TblChargingrecord;
import com.ormcore.model.TblElectricPileGun;

public class ShEpService {
	
	private static final Logger logger = LoggerFactory.getLogger(ShEpService.class);
	
	private static final String ChargeYearDateFmt = "yyyy-MM-dd HH:mm:ss";
	private static final String ChargeDayShortDateFmt = "MM-dd HH:mm";
	
	/**TODO:确认回复需要回复
	 * 消费记录电桩编号和枪口编号一定要准确，错了就发到别的地方
	 * @param channel
	 * @param consumeRecord
	 * @throws IOException
	 */
	public static  void handleConsumeRecord(EpCommClient epCommClient,ConsumeRecord consumeRecord) throws IOException
	{
		logger.info("endcharge sh enter handleNoCardConsumeRecord,consumeRecord:{}",consumeRecord);
		
		//1.检查订单参数
		int retCode= checkConsumeRecordParams(consumeRecord);
		if(retCode != 0)
		{
			logger.error("endcharge sh checkConsumeRecord error,retCode:{}",retCode);
			byte[] confirmdata = ShEpEncoder.do_consumeRecord_ret((byte)epCommClient.getVersion(),
					epCommClient.getRevINum().byteValue(),consumeRecord.getEpGunNo(),
					consumeRecord.getEpUserAccount());
			EpMessageSender.sendMessage(epCommClient,confirmdata);	
			
			return ;
		}
		
		String serialNo = consumeRecord.getEpCode() + EpChargeService.makeSerialNo();
		//2.检查桩和枪是否存在
		String epCode = consumeRecord.getEpCode();
		int epGunNo = consumeRecord.getEpGunNo();
		consumeRecord.setSerialNo(serialNo);
		
		EpGunCache  epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache==null)
		{
			logger.error("endcharge sh handleConsumeRecord did not find gun,epCode:{},epGunNo:{}",
					new Object[]{epCode,epGunNo});
			byte[] confirmdata = ShEpEncoder.do_consumeRecord_ret((byte)epCommClient.getVersion(),
					epCommClient.getRevINum().byteValue(),consumeRecord.getEpGunNo(),
					consumeRecord.getEpUserAccount());
			EpMessageSender.sendMessage(epCommClient,confirmdata);	
			return ;
		}
		retCode =1;
        logger.info("[endcharge] handleNoCardConsumeRecord over,epCode:{},epGunNo:{},serialNo:{},retCode:{} ",
				new Object[]{epCode,epGunNo,serialNo,retCode});
        retCode = endSHConsumeRecord(consumeRecord);
        
		if(retCode>=0 && retCode <=3)
		{	
			byte[] confirmdata=null;
			
			confirmdata = ShEpEncoder.do_consumeRecord_ret((byte)epCommClient.getVersion(),
					epCommClient.getRevINum().byteValue(),consumeRecord.getEpGunNo(),
					consumeRecord.getEpUserAccount());
			EpMessageSender.sendMessage(epCommClient,confirmdata);
		}
		
	}
	
	/**
	 * 检查参数
	 * @param consumeRecord
	 * @return
	 */
	
	private static int checkConsumeRecordParams(ConsumeRecord consumeRecord)
	{
	   if(consumeRecord==null)
	   {
		   logger.error("endcharge invalid consumeRecord:{}",consumeRecord);
		   return 4;
	   }
	   //1.检查流水号是否合法
	   
	   String epCode = consumeRecord.getEpCode();
	   int epGunNo = consumeRecord.getEpGunNo();
	   String zeroEpCode= StringUtil.repeat("0", 32);
	   
	   if(epCode==null || epCode.length()!=16)
		{
			logger.error("endcharge invalid epCode:{},consumeRecord:{}",epCode,consumeRecord);
			epCode = zeroEpCode;
			return 4;
		}
	   else
	   {
		   if( epCode.compareTo(zeroEpCode)==0)
		   {
			   logger.error("endcharge invalid consumeRecord:{}",consumeRecord);
			   return 4;
		   }
	   }
	   
//	   EpGunCache  epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
//		if(epGunCache==null)
//		{
//			logger.error("endcharge invalid consumeRecord,not find EpGunCache,epCode:{},epGunNo:{}",epCode,epGunNo);
//			   return 4;
//		}
	   return 0;
		
	}
	
	/**
	 * 处理盛宏消费记录
	 * @param consumeRecord
	 * @param epGunCache
	 * @return  
	 * 			2:已经处理
				1:处理成功
	 */
	
	
	public static int endSHConsumeRecord(ConsumeRecord consumeRecord )
	{
		
		int orderStatus = getChargeOrderStatus(consumeRecord.getEpCode(),consumeRecord.getEpGunNo(),
				consumeRecord.getStartTime(),consumeRecord.getEndTime());
		logger.debug("endcharge endSHConsumeRecord epCode:{},epGunNo:{},orderStatus:{}",
				new Object[]{consumeRecord.getEpCode(),consumeRecord.getEpGunNo(),orderStatus});
		
		if(orderStatus!= -1)//数据库中已经有该条记录
			return 2;
		
		EpGunCache  epGunCache = EpGunService.getEpGunCache(consumeRecord.getEpCode(), consumeRecord.getEpGunNo());
		if(epGunCache==null)
		{
			return 2;
		}
	    int pkEpId=epGunCache.getPkEpId();
	    int pkEpGunId=epGunCache.getPkEpGunId();
	    String chOrCode = EpChargeService.makeChargeOrderNo(pkEpGunId,0);
		
		int chargeTime = (int)((consumeRecord.getChargeUseTimes())/60);			
		addChargeStat(pkEpGunId,consumeRecord.getTotalDl(),chargeTime,consumeRecord.getTotalAmt());

		
		insertChargeWithConsumeRecord(pkEpId, consumeRecord,chOrCode);
		
		//4.故障记录到故障表
		insertFaultRecord(consumeRecord.getSerialNo(),Integer.valueOf(consumeRecord.getStopCause()),consumeRecord.getEpCode(),
				pkEpId,consumeRecord.getEpGunNo(),
				new Date(consumeRecord.getEndTime()*1000));

		return 1;
	}
	
	public static int getChargeOrderStatus(String epCode,int epGunNo,long st,long et)
	{
		Date dtStart = new Date(st*1000);
		Date dtEnd = new Date(et*1000);
		// 计算总电量
		String beginTime = DateUtil.toDateFormat(dtStart, "yyyy-MM-dd HH:mm:ss");
		String endTime = DateUtil.toDateFormat(dtEnd,"yyyy-MM-dd HH:mm:ss");
		TblChargingOrder order = new TblChargingOrder();
		order.setChargeBegintime(beginTime);
		order.setChargeEndtime(endTime);
		order.setChorPilenumber(epCode);
		
		String ret = DB.chargeOrderDao.getOrderStatusBytime(order);
		return (ret==null)?-1:Integer.parseInt(ret);
	}
	public static void addChargeStat(int pkEpGunId,int chargeMeterNum,int chargeTime,int chargeAmt)
	{
		TblElectricPileGun info= new TblElectricPileGun();
		info.setPkEpGunId(pkEpGunId);
		
		info.setTotalChargeMeter(NumUtil.intToBigDecimal2(chargeMeterNum));
		info.setTotalChargeTime(chargeTime);
		info.setTotalChargeAmt(NumUtil.intToBigDecimal2(chargeAmt));
	    
		DB.epGunDao.addChargeStat(info);
	}
	public static void insertChargeWithConsumeRecord(int pkEpId,ConsumeRecord consumeRecord,String chOrCode)
	{
		// 尖时段用电度数
		BigDecimal tipPower = new BigDecimal(0.0);
				
				
		BigDecimal totalPower = NumUtil.intToBigDecimal2(consumeRecord.getTotalDl());
		Date dtStart = new Date(consumeRecord.getStartTime()*1000);
		Date dtEnd = new Date(consumeRecord.getEndTime()*1000);
		// 计算总电量
		String beginTime = DateUtil.toDateFormat(dtStart, "MM-dd HH:mm");
		String endTime = DateUtil.toDateFormat(dtEnd,"MM-dd HH:mm");
		
		
		BigDecimal  chargeAmt =  NumUtil.intToBigDecimal2(consumeRecord.getTotalChargeAmt());
		BigDecimal  serviceAmt = NumUtil.intToBigDecimal2(consumeRecord.getServiceAmt());
	   
		
		// 充电消费订单
		TblChargingOrder order = new TblChargingOrder();
		order.setChorPilenumber(consumeRecord.getEpCode());
		order.setChorAppointmencode("");
		order.setChorMoeny(NumUtil.intToBigDecimal2(consumeRecord.getTotalAmt() )+ "");
		logger.debug("sh insertChargeWithConsumeRecord ChorMoeny:{}",new Object[] {order.getChorMoeny()});
		
		order.setChorQuantityelectricity(totalPower );
		order.setChorTimequantum(beginTime + " - " + endTime);
		order.setChorMuzzle(consumeRecord.getEpGunNo());
		order.setChorChargingstatus(ChargeOrderConstants.ORDER_STATUS_DONE + "");
		order.setChorTranstype(""+consumeRecord.getTransType());
		
		order.setChOr_tipPower(tipPower);
		order.setChOr_peakPower(tipPower);
		order.setChOr_usualPower(tipPower);
		order.setChOr_valleyPower(tipPower);
		order.setChorUsername(""); 
		order.setChorUserid("12355");
		order.setChorCreatedate(new Date());
		order.setChorUpdatedate(new Date());
		order.setChorUsername("");
		order.setChorTransactionnumber(consumeRecord.getSerialNo());
		order.setChorCode(chOrCode);
		order.setChorChargemoney(chargeAmt);
		order.setChorServicemoney(serviceAmt);
		order.setChorOrdertype(0);
		beginTime = DateUtil.toDateFormat(dtStart, ChargeYearDateFmt);
		endTime = DateUtil.toDateFormat(dtEnd,ChargeYearDateFmt);
		
		order.setChargeBegintime(beginTime);
		order.setChargeEndtime(endTime);
		order.setThirdUsrIdentity("");
		order.setThirdExtraData("");
		
		order.setCouPonAmt(BigDecimal.ZERO);
		
		// 新增充电消费订单
		DB.chargeOrderDao.insertFullChargeOrder(order);
		 
		TblChargingrecord record = new TblChargingrecord();
		record.setUserId(0);
	    record.setUserPhone("");
		// 开始充电表低示数
		BigDecimal bdMeterNum = NumUtil.intToBigDecimal2(consumeRecord.getStartMeterNum());
		String beginShowsNumber = String.valueOf(bdMeterNum);
		//结束表低示数
		bdMeterNum = NumUtil.intToBigDecimal2(consumeRecord.getEndMeterNum());
		String endShowsnumber = String.valueOf(bdMeterNum);
		record.setChreBeginshowsnumber(beginShowsNumber);
		record.setChreEndshowsnumber(endShowsnumber);
		record.setChreReservationnumber("");
		record.setJPrice(new BigDecimal(0.0) );
		record.setFPrice(new BigDecimal(0.0));
		record.setPPrice(new BigDecimal(0.0));
		record.setGPrice(new BigDecimal(0.0));
		record.setQuantumDate("");
		record.setPkUserCard(0);
	    record.setUserPhone("");
	    record.setServicePrice(new BigDecimal(0.0));
		record.setChreElectricpileid(pkEpId);
		record.setChreUsingmachinecode(consumeRecord.getEpCode());
		record.setChreChargingnumber(consumeRecord.getEpGunNo());
		record.setChreChargingmethod(0);
		record.setChreTransactionnumber(consumeRecord.getSerialNo());
		record.setChreCode(chOrCode);
		record.setChreStartdate(dtStart);
		record.setChreEnddate(dtEnd);
		
	
		record.setChreReservationnumber("");
		record.setChreResttime(0);
		record.setStatus(1);
		record.setThirdUsrIdentity("");
		record.setThirdExtraData("");
		// 新增开始充电记录
		DB.chargingrecordDao.insertFullChargeRecord(record);
		
	}
	
	public static void insertFaultRecord(String serial,int stopCause,String epCode,int pkEpId,int epGunNo,Date faultDate)
	{
		if(stopCause==0)
			return ;
		try
		{
			String faultCause = ""+stopCause;
			
            TblChargingfaultrecord faultrecord = new TblChargingfaultrecord();
			faultrecord.setCfreUsingmachinecode(epCode);
			faultrecord.setCfreElectricpileid(pkEpId);
			faultrecord.setCfreElectricpilename("");
			faultrecord.setCfreTransactionnumber(serial);
			faultrecord.setcFRe_EphNo(epGunNo);
			
			faultrecord.setCfreChargingsarttime(faultDate);
			faultrecord.setCfreFaultcause(faultCause);
			// 新增故障记录
			DB.chargingfaultrecordDao.insertFaultRecord(faultrecord);
			
		}catch(Exception e)
		{
			logger.error("sh insertFaultRecord,exception stack trace:{}",e.getStackTrace());
		}
		
	}
	
	public static void handleLogin(int verSion,String epCode,EpCommClient epCommClient)
	{
		String commClientIdentity="";
		
		String epCodeZero=StringUtil.repeat("0", 15);
		if(epCode.compareTo(epCodeZero)==0)
			return ;
		
		commClientIdentity=epCode;
		if(EpService.initDiscreteEpConnect(verSion, epCode, epCommClient,0))
		{
			 StatService.addCommDiscreteEp();
			 byte[] confirmdata=null;
				
			 confirmdata = ShEpEncoder.do_login_ret((byte)verSion,epCommClient.getRevINum().byteValue());
			 EpMessageSender.sendMessage(epCommClient,confirmdata);
		}
	}
	
	public static void handleHeart(String epCode,EpCommClient epCommClient)
	{
		if(epCode.equals(epCommClient.getIdentity())==false)
		{
			logger.error("sh handleHeart fail ,epCode is error,epCode:{}",epCode);
			return;
		}
		byte[] confirmdata=null;
		int heartNum = epCommClient.getSendINum2();
		if(heartNum==0) //从1开始
			heartNum = epCommClient.getSendINum2();
		confirmdata = ShEpEncoder.do_heart_ret((byte)epCommClient.getVersion(), (byte)epCommClient.getRevINum().byteValue(), heartNum);		 
		EpMessageSender.sendMessage(epCommClient,confirmdata);
	}
	
}
	
	
