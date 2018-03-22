package com.ec.epcore.cache;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.Global;
import com.ec.constants.YXCConstants;
import com.ec.net.proto.SingleInfo;
import com.ec.utils.NumUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargeACInfo;


public class RealACChargeInfo extends RealChargeInfo{
	
	private static final Logger logger = LoggerFactory.getLogger(RealACChargeInfo.class);
	
	/**
	 * 充电结束后，充电测量信息清零
	 */
	@Override
	public void endCharge()
	{
		//outVoltage=0;//充电机输出电压
		//TODO:outCurrent=0;//充电机输出电流
		
		soc=0;//SOC
		chargedTime=0;//累计充电时间
		chargeRemainTime=0;//估计剩余时间
		
		//公共的变长遥测
		totalActivMeterNum=0;//有功总电度
		chargedMeterNum=0;//已充度数
		chargePrice=0;//单价
		
		//充电相关
		chargeUserId =0 ;
		chargeSerialNo ="";
		chargeStartTime= 0;
		chargeStartMeterNum=0;
		chargedCost=0;
		fronzeAmt=0;//冻结金额
	}
	public void convertFromDB(TblChargeACInfo tblRealData)
	{
		if(tblRealData==null)
			return;
		//交直流公共部分
		pkChargeInfo = tblRealData.getPk_chargeinfo();
		stationId = tblRealData.getStation_id();
		//epCode;
		//epGunNo;
		
		//公共遥信
		linkCarStatus = tblRealData.getLinkCarStatus();//是否连接电池(车辆)
		gunCloseStatus = tblRealData.getGunCloseStatus();//枪座状态
		gunLidStatus = tblRealData.getGunLidStatus();//充电枪盖状态
		commStatusWithCar = tblRealData.getCommStatusWithCar();//车与桩建立通信信号
		carPlaceStatus = tblRealData.getCarPlaceStatus();//车位占用状态(雷达探测)
		cardReaderFault = tblRealData.getCardReaderFault();//读卡器通讯故障
		urgentStopFault = tblRealData.getUrgentStopFault();//急停按钮动作故障
		arresterFault = tblRealData.getArresterFault();//避雷器故障
		insulationCheckFault = tblRealData.getInsulationCheckFault();//绝缘检测故障
		
		gunUnconnectWarn  = tblRealData.getGunUnconnectWarn();//充电枪未连接告警
		transRecordFullWarn = tblRealData.getTransRecordFullWarn();//交易记录已满告警
		meterError = tblRealData.getMeterError();//电度表异常
		
		acInVolWarn = tblRealData.getAcInVolWarn();//交流输入电压过压/欠压
		chargeOverTemp = tblRealData.getChargeOverTemp();//充电机过温故障
		acCurrentLoadWarn = tblRealData.getAcCurrentLoadWarn();//交流电流过负荷告警
		outRelayStatus = tblRealData.getOutRelayStatus();//输出继电器状态
		
		//公共遥测
		workingStatus = tblRealData.getWorkingStatus();//充电机状态
		outVoltage = tblRealData.getOutVoltage().multiply(Global.DecTime1).intValue();//充电机输出电压
		outCurrent = tblRealData.getOutCurrent().multiply(Global.DecTime1).intValue();//充电机输出电流
		carPlaceLockStatus = tblRealData.getCarPlaceLockStatus();//地锁状态
		soc = tblRealData.getSoc();//SOC
		chargedTime = tblRealData.getChargedTime();//累计充电时间
		chargeRemainTime = tblRealData.getChargeRemainTime();//估计剩余时间
		
		//公共的变长遥测
		totalActivMeterNum = tblRealData.getTotalActivMeterNum().multiply(Global.DecTime3).intValue();//有功总电度
		chargedMeterNum = tblRealData.getChargedMeterNum().multiply(Global.DecTime3).intValue();//已充度数
		chargePrice = tblRealData.getChargePrice().multiply(Global.DecTime3).intValue();//单价
		
		//充电相关
		chargeUserId  = tblRealData.getChargeUserId() ;
		chargeSerialNo  = tblRealData.getChargeSerialNo();
		chargeStartTime = tblRealData.getChargeStartTime();
		chargeStartMeterNum  = tblRealData.getChargeStartMeterNum().multiply(Global.DecTime3).intValue();
		chargedCost  = tblRealData.getChargedCost().multiply(Global.DecTime2).intValue();
		fronzeAmt = tblRealData.getFronzeAmt().multiply(Global.DecTime2).intValue();//冻结金额
		
	}
	@Override
	public boolean saveDb()
	{
		TblChargeACInfo tblRealData =new TblChargeACInfo();
		
		tblRealData.setEp_code( epCode);
		tblRealData.setEp_gun_no(epGunNo);
		
		//公共遥信
		tblRealData.setLinkCarStatus(linkCarStatus);
		tblRealData.setGunCloseStatus(gunCloseStatus);
		tblRealData.setGunLidStatus(gunLidStatus);
		tblRealData.setCommStatusWithCar(commStatusWithCar);
		tblRealData.setCarPlaceStatus(carPlaceStatus);
		tblRealData.setCardReaderFault(cardReaderFault);
		tblRealData.setUrgentStopFault(urgentStopFault);
		tblRealData.setArresterFault(arresterFault);
		tblRealData.setInsulationCheckFault(insulationCheckFault);
				
		tblRealData.setGunUnconnectWarn(gunUnconnectWarn);
		tblRealData.setTransRecordFullWarn(transRecordFullWarn);
		tblRealData.setMeterError(meterError);
				
		tblRealData.setAcInVolWarn(acInVolWarn);
		tblRealData.setChargeOverTemp(chargeOverTemp);
		tblRealData.setAcCurrentLoadWarn(acCurrentLoadWarn);
		tblRealData.setOutRelayStatus(outRelayStatus);	
				
				//公共遥测
		tblRealData.setWorkingStatus(workingStatus);
		tblRealData.setOutVoltage((new BigDecimal(outVoltage)).multiply(Global.Dec1));
		tblRealData.setOutCurrent((new BigDecimal(outCurrent)).multiply(Global.Dec2));
		tblRealData.setCarPlaceLockStatus(carPlaceLockStatus);
		tblRealData.setSoc(soc);
		tblRealData.setChargedTime(chargedTime);
		tblRealData.setChargeRemainTime(chargeRemainTime);
				
				//公共的变长遥测
		tblRealData.setTotalActivMeterNum((new BigDecimal(this.totalActivMeterNum)).multiply(Global.Dec3));
		tblRealData.setChargedMeterNum((new BigDecimal(this.chargedMeterNum)).multiply(Global.Dec3));
		tblRealData.setChargePrice((new BigDecimal(this.chargePrice)).multiply(Global.Dec3));
						
		//充电相关
		//chargeUserId  = tblRealData.getChargeUserId() ;
		//chargeSerialNo  = tblRealData.getChargeSerialNo();
		//chargeStartTime = tblRealData.getChargeStartTime();
		//chargeStartMeterNum  = tblRealData.getChargeStartMeterNum().multiply(Global.DecTime3).intValue();
		//chargedCost  = tblRealData.getChargedCost().multiply(Global.DecTime2).intValue();
		//fronzeAmt = tblRealData.getFronzeAmt().multiply(Global.DecTime2).intValue();//冻结金额
		
		DB.chargeACInfoDao.update(tblRealData);
														
		return true;
	}
	
	
	
	
	@Override
	public boolean loadFromDb(String epCode,int epGunNo)
	{
		TblChargeACInfo tblRealData =new TblChargeACInfo();
		
		tblRealData.setEp_code( epCode);
		tblRealData.setEp_gun_no(epGunNo);
		
		List<TblChargeACInfo> acChargeInfoList= DB.chargeACInfoDao.findChargeInfo(tblRealData);
		if(acChargeInfoList !=null && acChargeInfoList.size()>0)
		{
			TblChargeACInfo chargeInfo= acChargeInfoList.get(0);
			convertFromDB(chargeInfo);
		}
		else
		{
			if(DB.chargeACInfoDao.insert(tblRealData) ==1)
			{
				this.pkChargeInfo = tblRealData.getPk_chargeinfo();
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("RealAcChargeInfo");
        sb.append(",{id = ").append(pkChargeInfo).append("\n\n");
        sb.append("工作状态 = ").append(workingStatus).append(getWorkingStatusDesc()).append("\n\n");

        sb.append("有功总电度 = ").append(NumUtil.intToBigDecimal3(totalActivMeterNum)).append("\n");
        sb.append("输出电压 = ").append(NumUtil.intToBigDecimal1(outVoltage)).append("\n\r\n");
        
      //  if(workingStatus == EpConstant.EP_GUN_W_STATUS_WORK)
        {
        	sb.append("输出电流 = ").append(NumUtil.intToBigDecimal2(outCurrent)).append("\n");
        	sb.append("SOC = ").append(soc).append("\n");
        	sb.append("估计剩余时间 = ").append(chargeRemainTime).append("\n");  	
            sb.append("累计充电时间 = ").append(chargedTime).append("\n");     
            sb.append("已充度数 = ").append(NumUtil.intToBigDecimal3(chargedMeterNum)).append("\n");
            sb.append("单价 = ").append(NumUtil.intToBigDecimal3(chargePrice)).append("\n");
            sb.append("已充金额 = ").append(NumUtil.intToBigDecimal2(chargedCost)).append("\n\r\n");
            
        }
        sb.append("连接状态 = ").append(linkCarStatus).append(getLinkCarStatusDesc()).append("\n");
        sb.append("收枪状态 = ").append(gunCloseStatus).append(getGunCloseStatusDesc()).append("\n");
        sb.append("枪盖状态 = ").append(gunLidStatus).append(getGunLidStatusDesc()).append("\n");
        sb.append("车位状态 = ").append(carPlaceStatus).append(getCarPlaceStatusDesc()).append("\n");
        sb.append("车位锁状态 = ").append(carPlaceLockStatus).append(getCarPlaceLockStatusDesc()).append("\n");
        sb.append("车与桩通讯状态 = ").append(commStatusWithCar).append(getCommStatusWithCarDesc()).append("\n\r\n");     
        sb.append("输入过压/欠压 = ").append(acInVolWarn).append(getAcInVolWarnDesc()).append("\n");
        sb.append("负何告警 = ").append(acCurrentLoadWarn).append(getAcCurrentLoadWarnDesc()).append("\n");
        sb.append("输出继电器状态 = ").append(outRelayStatus).append(getOutRelayStatusDesc()).append("\n");   
        sb.append("读卡器通讯故障状态 = ").append(cardReaderFault).append(getCardReaderFaultDesc()).append("\n");  
        sb.append("急停按钮动作故障状态 = ").append(urgentStopFault).append(getUrgentStopFaultDesc()).append("\n");     
        sb.append("避雷器故障状态 = ").append(arresterFault).append(getArresterFaultDesc()).append("\n");  
        sb.append("绝缘监测故障状态 = ").append(insulationCheckFault).append(getInsulationCheckFaultDesc()).append("\n");  
        sb.append("充电枪未连接告警状态 = ").append(gunUnconnectWarn).append(getGunUnconnectWarnDesc()).append("\n");  
        sb.append("充电机过温故障状态 = ").append(chargeOverTemp).append(getChargeOverTempDesc()).append("\n"); 
        sb.append("交易记录已满告警状态 = ").append(transRecordFullWarn).append(getTransRecordFullWarnDesc()).append("\n");  
        sb.append("电度表异常状态 = ").append(meterError).append(getMeterErrorDesc()).append("\n");   
        
        sb.append("chargeUserId = ").append(this.chargeUserId).append("\n");
        sb.append("serialno = ").append(this.chargeSerialNo).append("\n");
          
		return sb.toString();
	}
	/**
	 * 
	 * @return-3:在最大遥测，遥信最大值范围外，-2:保留字段,-1：参数非法，0：没有变化;1:变化
	 */
	@Override
	public int setFieldValue(int pointAddr,SingleInfo info)
	{
		int ret=0;
		try
		{
			switch(pointAddr)
			{
			case YXCConstants.YX_1_LINKED_CAR:
			{
				
				return setLinkCarStatus((short)info.getIntValue());
				
			}
				
			case YXCConstants.YX_1_GUN_SIT:
			{
				return setGunCloseStatus((short)info.getIntValue());
			}
				
			case YXCConstants.YX_1_GUN_LID://充电枪盖状态
			{
				return setGunLidStatus((short)info.getIntValue());
			}
			
			case YXCConstants.YX_1_COMM_WITH_CAR://车与桩建立通信信号
			{
				return setCommStatusWithCar((short)info.getIntValue());
			}
			
			case YXCConstants.YX_1_CAR_PLACE://车位占用状态
			{
				return setCarPlaceStatus((short)info.getIntValue());
			}
			case YXCConstants.YX_1_CARD_READER_FAULT://读卡器故障
			{
				return setCardReaderFault((short)info.getIntValue());
			}
			case YXCConstants.YX_1_URGENT_STOP_FAULT://急停按钮动作故障
			{
				return setUrgentStopFault((short)info.getIntValue());
			}
			case YXCConstants.YX_1_ARRESTER_EXCEPTION ://避雷器故障
			{
				return setArresterFault((short)info.getIntValue());
			}
		    
			case YXCConstants.YX_1_INSULATION_EXCEPTION://绝缘检测故障
			{
				return setInsulationCheckFault((short)info.getIntValue());
			}
			case YXCConstants.YX_1_GUN_UNCONNECT_WARN://充电枪未连接告警
			{
				return setGunUnconnectWarn((short)info.getIntValue());
			}
			case YXCConstants.YX_1_TRANSRECORD_FULL_WARN://交易记录已满告警
			{
				return setTransRecordFullWarn((short)info.getIntValue());
			}
			case YXCConstants.YX_1_METER_ERROR://电度表异常
			{
				return setMeterError((short)info.getIntValue());
			}
		    
			case YXCConstants.YX_2_AC_IN_VOL_WARN ://交流输入电压过压/欠压
			{
				return setAcInVolWarn((short)info.getIntValue());
				
			}
			case YXCConstants.YX_2_CHARGE_OVER_TEMP://充电机过温故障
			{
				return setChargeOverTemp((short)info.getIntValue());
				
			}
			case YXCConstants.YX_2_AC_CURRENT_LOAD_WARN://交流电流过负荷告警
			{
				return setAcCurrentLoadWarn((short)info.getIntValue());
			}
			case YXCConstants.YX_2_OUT_RELAY_STATUS://输出继电器状态
			{
				return setOutRelayStatus((short)info.getIntValue());
			}
		    //
			case YXCConstants.YC_WORKSTATUS://充电机状态
			{
				return setWorkingStatus((short)info.getIntValue());
			}
			
			case YXCConstants.YC_OUT_VOL://充电机输出电压
			{
				return setOutVoltage((short)info.getIntValue());
			}
			
			case YXCConstants.YC_OUT_CURRENT://充电机输出电流
			{
				return setOutCurrent((short)info.getIntValue());
			}
			
		   
			case YXCConstants.YC_CAR_PLACE_LOCK://车位地锁状态
			{
				return setCarPlaceLockStatus((short)info.getIntValue());
			}
		    case YXCConstants.YC_SOC://
			{
				return setSoc((short)info.getIntValue());
			}
			
		    case YXCConstants.YC_TOTAL_TIME://累计充电时间
			{
				return setChargedTime(info.getIntValue());
			}
			
		    case YXCConstants.YC_REMAIN_TIME://估计剩余时间
			{
				return setChargeRemainTime(info.getIntValue());
			}
		    case YXCConstants.YC_ERROR_CODE://错误代码
			{
				return setErrorCode(info.getIntValue());
			}

		    case YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM://有功总电度
			{
				return setTotalActivMeterNum(info.getIntValue());
			}
			 
			    
		    case YXCConstants.YC_VAR_CHARGED_COST://已充金额
			{
				return setChargedCost(info.getIntValue());
			}
			  
		    case YXCConstants.YC_VAR_CHARGED_PRICE://单价
			{
				return setChargePrice(info.getIntValue());
			}
			 
		    case YXCConstants.YC_VAR_CHARGED_METER_NUM://已充度数
			{
				return setChargedMeterNum(info.getIntValue());
			}
		    
		    default:
		    {
		    	if( (pointAddr>YXCConstants.YX_1_DC_OUT_OVER_CURRENT_WARN && pointAddr <YXCConstants.YX_2_START_POS)||//超出单位遥信
		    		(pointAddr>YXCConstants.YX_2_BMS_VOL_WARN && pointAddr <YXCConstants.YC_START_POS)||//超出双位遥信
		    		(pointAddr>YXCConstants.YC_EP_TEMP && pointAddr <YXCConstants.YC_CHARGER_MOD_START_POS)||//超出遥测
		    		(pointAddr>YXCConstants.YC_VAR_BATTARY_FACTORY))//超出变长遥测
		    	{
		    		ret =-3;
		    	}
		    	
		    	else
		    	{
		    		ret =-2;
		    	}
		    }
		    	break;
			
			}
		}
		catch(ClassCastException e)
		{
			logger.error("setFieldValue exception pointAddr:{},getStackTrace:{}",pointAddr,e.getStackTrace());
			ret =-1;
		}
		
		return ret;
		
	}

	@Override
	public SingleInfo getFieldValue(int pointAddr)
	{
		int point=0;
		int value=-1;
		SingleInfo info= new SingleInfo();
		
		switch(pointAddr)
		{
		case YXCConstants.YX_1_LINKED_CAR:
		{
			point = pointAddr;
			value = getLinkCarStatus();
			break;
			
		}
			
		case YXCConstants.YX_1_GUN_SIT:
		{
			point = pointAddr;
			value = getGunCloseStatus();
			break;
		}
			
		case YXCConstants.YX_1_GUN_LID://充电枪盖状态
		{
			point = pointAddr;
			value = getGunLidStatus();
			break;
		}
		
		case YXCConstants.YX_1_COMM_WITH_CAR://车与桩建立通信信号
		{
			point = pointAddr;
			value = getCommStatusWithCar();
			break;
		}
		
		case YXCConstants.YX_1_CAR_PLACE://车位占用状态
		{
			point = pointAddr;
			value = getCarPlaceStatus();
			break;
		}
		case YXCConstants.YX_1_CARD_READER_FAULT://读卡器故障
		{
			point = pointAddr;
			value = getCardReaderFault();
			break;
		}
		case YXCConstants.YX_1_URGENT_STOP_FAULT://急停按钮动作故障
		{
			point = pointAddr;
			value = getUrgentStopFault();
			break;
		}
		case YXCConstants.YX_1_ARRESTER_EXCEPTION ://避雷器故障
		{
			point = pointAddr;
			value = getArresterFault();
			break;
		}
	    
		case YXCConstants.YX_1_INSULATION_EXCEPTION://绝缘检测故障
		{
			point = pointAddr;
			value = getInsulationCheckFault();
			break;
		}
		case YXCConstants.YX_1_GUN_UNCONNECT_WARN://充电枪未连接告警
		{
			point = pointAddr;
			value = getGunUnconnectWarn();
			break;
		}
		case YXCConstants.YX_1_TRANSRECORD_FULL_WARN://交易记录已满告警
		{
			point = pointAddr;
			value = getTransRecordFullWarn();
			break;
		}
		case YXCConstants.YX_1_METER_ERROR://电度表异常
		{
			point = pointAddr;
			value = getMeterError();
			break;
		}
	    
	    
		
	    
		case YXCConstants.YX_2_AC_IN_VOL_WARN ://交流输入电压过压/欠压
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getAcInVolWarn();
			break;
			
		}
		case YXCConstants.YX_2_CHARGE_OVER_TEMP://充电机过温故障
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getChargeOverTemp();
			break;
			
		}
		case YXCConstants.YX_2_AC_CURRENT_LOAD_WARN://交流电流过负荷告警
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getAcCurrentLoadWarn();
			break;
		}
		case YXCConstants.YX_2_OUT_RELAY_STATUS://输出继电器状态
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getOutRelayStatus();
			break;
		}
		
	    
		
	    //
		case YXCConstants.YC_WORKSTATUS://充电机状态
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getWorkingStatus();
			break;
		}
		
		case YXCConstants.YC_OUT_VOL://充电机输出电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getOutVoltage();
			break;
		}
		
		case YXCConstants.YC_OUT_CURRENT://充电机输出电流
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getOutCurrent();
			break;
		}
		
	   
		case YXCConstants.YC_CAR_PLACE_LOCK://车位地锁状态
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarPlaceLockStatus();
			break;
		}
		
	    
	    case YXCConstants.YC_SOC://
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getSoc();
			break;
		}
		
	    case YXCConstants.YC_TOTAL_TIME://累计充电时间
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getChargedTime();
			break;
		}
		
	    case YXCConstants.YC_REMAIN_TIME://估计剩余时间
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getChargeRemainTime();
			break;
		}
		
	   
	    
	    
	    case YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM://有功总电度
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			value = getTotalActivMeterNum();
			break;
		}
		 
		    
	    case YXCConstants.YC_VAR_CHARGED_COST://已充金额
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			value = getChargedCost();
			break;
		}
		  
	    case YXCConstants.YC_VAR_CHARGED_PRICE://单价
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			value = getChargePrice();
			break;
		}
		 
	    case YXCConstants.YC_VAR_CHARGED_METER_NUM://已充度数
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			value = getChargedMeterNum();
			break;
		}
		    
	    default:
	    
	    	break;
		
		}
		
		if(value>=0)
		{
			info.setAddress(point);
			info.setIntValue(value);
			return info;
		}
		return null;
		
		
	}
	public Map<Integer, SingleInfo> getWholeOneBitYx()
	{
		Map<Integer, SingleInfo> oneYxRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		 int oneYxArray[] = new int[]{
				 YXCConstants.YX_1_LINKED_CAR,
				 YXCConstants.YX_1_GUN_SIT,
				 YXCConstants.YX_1_GUN_LID,
				 YXCConstants.YX_1_COMM_WITH_CAR,
				 YXCConstants.YX_1_CAR_PLACE,
				 YXCConstants.YX_1_CARD_READER_FAULT,
				 YXCConstants.YX_1_URGENT_STOP_FAULT,
				 YXCConstants.YX_1_ARRESTER_EXCEPTION,
				    
				 YXCConstants.YX_1_INSULATION_EXCEPTION,
				 YXCConstants.YX_1_GUN_UNCONNECT_WARN,
				 YXCConstants.YX_1_TRANSRECORD_FULL_WARN,
				 YXCConstants.YX_1_METER_ERROR
			};
		for(int i=0;i<oneYxArray.length;i++)
		{
			 SingleInfo loop= getFieldValue(oneYxArray[i]);
			 if(loop!=null)
			 {
				 oneYxRealInfo.put(loop.getAddress(), loop);
			 }
		}
		
		return oneYxRealInfo;
		
	}
	
	public Map<Integer, SingleInfo> getWholeTwoBitYx()
	{
		Map<Integer, SingleInfo> twoYxRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		 int twoYxArray[] = new int[]{
				 YXCConstants.YX_2_AC_IN_VOL_WARN,
				 YXCConstants.YX_2_CHARGE_OVER_TEMP,
				 YXCConstants.YX_2_AC_CURRENT_LOAD_WARN,
				 YXCConstants.YX_2_OUT_RELAY_STATUS
			};
		for(int i=0;i<twoYxArray.length;i++)
		{
			 SingleInfo loop=  getFieldValue(twoYxArray[i]);
			 if(loop!=null)
			 {
				 twoYxRealInfo.put(loop.getAddress(), loop);
			 }
		}
		return twoYxRealInfo;
		
	}
	public Map<Integer, SingleInfo> getWholeYc()
	{
		//遥测
		Map<Integer, SingleInfo> ycRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		 int ycArray[] = new int[]{YXCConstants.YC_WORKSTATUS,
					YXCConstants.YC_CAR_PLACE_LOCK,
					YXCConstants.YC_OUT_VOL,
					 YXCConstants.YC_OUT_CURRENT,//充电机输出电流
					 YXCConstants.YC_SOC,//
					 YXCConstants.YC_TOTAL_TIME,//累计充电时间
					 YXCConstants.YC_REMAIN_TIME//估计剩余时间
					 };
		for(int i=0;i<ycArray.length;i++)
		{
			 SingleInfo loop= getFieldValue(ycArray[i]);
			 if(loop!=null)
			 {
				 ycRealInfo.put(loop.getAddress(), loop);
			 }
		}
		
		return ycRealInfo;
				
		
	}
	public Map<Integer, SingleInfo> getWholeVarYc()
	{
		 Map<Integer, SingleInfo> varYcRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		 int varYcArray[] = new int[]{
				 YXCConstants.YC_VAR_ACTIVE_TOTAL_METERNUM,
				 YXCConstants.YC_VAR_CHARGED_COST,
				 YXCConstants.YC_VAR_CHARGED_PRICE,
				 YXCConstants.YC_VAR_CHARGED_METER_NUM
		 };
		 for(int i=0;i<varYcArray.length;i++)
		{
			 SingleInfo loop= getFieldValue(varYcArray[i]);
			 
			 if(loop!=null)
			 {
				 varYcRealInfo.put(loop.getAddress(), loop);
			 }
		}
		 return varYcRealInfo;
		
	}
	
	
	
	
	
}
