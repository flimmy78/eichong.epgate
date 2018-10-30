package com.ec.epcore.cache;

import com.ec.constants.YXCConstants;
import com.ec.epcore.service.EpGunService;
import com.ec.net.proto.SingleInfo;
import com.ec.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class RealChargeInfo extends Object{
	
	
	private static final Logger logger = LoggerFactory.getLogger(RealChargeInfo.class);
	
	protected int pkChargeInfo;
	
	private int currentType=0; //EP_AC_TYPE = 14;EP_DC_TYPE = 5;
	
	protected int stationId;
	
	protected String epCode;
	
	protected int epGunNo;
	
	//公共遥信
	protected short linkCarStatus;//是否连接电池(车辆)
	protected short gunCloseStatus;//枪座状态
	protected short gunLidStatus;//充电枪盖状态
	protected short commStatusWithCar;//车与桩建立通信信号
	protected short carPlaceStatus;//车位占用状态(雷达探测)
	protected short cardReaderFault;//读卡器通讯故障
	protected short urgentStopFault;//急停按钮动作故障
	protected short arresterFault;//避雷器故障
	protected short insulationCheckFault;//绝缘检测故障
	
	protected short gunUnconnectWarn;//充电枪未连接告警
	protected short transRecordFullWarn;//交易记录已满告警
	protected short meterError;//电度表异常
	
	protected short acInVolWarn;//交流输入电压过压/欠压
	protected short chargeOverTemp;//充电机过温故障
	protected short acCurrentLoadWarn;//交流电流过负荷告警
	protected short outRelayStatus;//输出继电器状态
	
	//公共遥测
	protected int workingStatus;//充电机状态
	protected int outVoltage;//充电机输出电压
	protected int outCurrent;//充电机输出电流
	protected int carPlaceLockStatus;//地锁状态
	protected int soc;//SOC
	protected int chargedTime;//累计充电时间
	protected int chargeRemainTime;//估计剩余时间
	
	//公共的变长遥测
	protected int totalActivMeterNum;//有功总电度
	protected int chargedMeterNum;//已充度数
	protected int chargePrice;//单价
	
	//充电相关
	protected int chargeUserId =0 ;
	protected String chargeSerialNo ="";
	protected int chargeStartTime= 0;
	protected int chargeStartMeterNum;
	protected int chargedCost;
	protected int fronzeAmt;//冻结金额
	
	protected long workStatusUpdateTime;
	
	protected int errorCode;//地址8，错误代码
	
	//直流：个，交流：个
	public  void init()
	{
		pkChargeInfo =0 ;
		currentType=0;
		stationId =0;
		epCode ="";
		
		epGunNo=0;
		
		//公共遥信
		linkCarStatus=0;//是否连接电池(车辆)
		gunCloseStatus=0;//枪座状态
		gunLidStatus=0;//充电枪盖状态
		commStatusWithCar=0;//车与桩建立通信信号
		carPlaceStatus=0;//车位占用状态(雷达探测)
		cardReaderFault=0;//读卡器通讯故障
		urgentStopFault=0;//急停按钮动作故障
		arresterFault=0;//避雷器故障
		insulationCheckFault=0;//绝缘检测故障
		
		gunUnconnectWarn=0;//充电枪未连接告警
		transRecordFullWarn=0;//交易记录已满告警
		meterError=0;//电度表异常
		
		acInVolWarn=0;//交流输入电压过压/欠压
		chargeOverTemp=0;//充电机过温故障
		acCurrentLoadWarn=0;//交流电流过负荷告警
		outRelayStatus=0;//输出继电器状态
		
		//公共遥测
		workingStatus=0;//充电机状态,默认为离线
		outVoltage=0;//充电机输出电压
		outCurrent=0;//充电机输出电流
		carPlaceLockStatus=0;//地锁状态
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
		workStatusUpdateTime=0;
		
		errorCode=0;//错误代码
		
	}
	
    
	
	public int getErrorCode() {
		return errorCode;
	}



	public int setErrorCode(int errorCode) {
		int ret=0;
		if(this.errorCode != errorCode)
		{
			this.errorCode = errorCode;
			String str = String.format("%04x", errorCode) ;
			logger.info("ycdata,epCode:{},errorCode:{}",epCode,str);
			ret=1;
		}
		return ret;
	}



	public static void AddPoint(Map<Integer, SingleInfo> pointMap, int address, int intValue,String strValue,int qdsDesc)
	{
		SingleInfo info = new SingleInfo();
		info.setAddress(address);
		info.setIntValue(intValue);
		info.setStrValue(strValue);
		info.setQdsDesc( qdsDesc);
		
		pointMap.put(address,info);
	}
	public static void AddPoint(Map<Integer, SingleInfo> pointMap, SingleInfo info)
	{
		pointMap.put(info.getAddress(),info);
	}
	
	public void onDataChange(Map<Integer, SingleInfo> pointMap,boolean all)
	{
	}
	public boolean loadFromDb(String epCode,int epGunNo)
	{
		return true;
	}
	
	public boolean saveDb()
	{
		return true;
	}


	public int getPkChargeInfo() {
		return pkChargeInfo;
	}


	public void setPkChargeInfo(int pkChargeInfo) {
		this.pkChargeInfo = pkChargeInfo;
	}


	public int getCurrentType() {
		return currentType;
	}


	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}


	public int getStationId() {
		return stationId;
	}


	public void setStationId(int stationId) {
		this.stationId = stationId;
	}


	public String getEpCode() {
		return epCode;
	}


	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}


	public int getEpGunNo() {
		return epGunNo;
	}


	public void setEpGunNo(int epGunNo) {
		this.epGunNo = epGunNo;
	}


	public short getLinkCarStatus() {
		return linkCarStatus;
	}
	public String getLinkCarStatusDesc() {
		
		String desc="";
		switch(this.linkCarStatus)
        {
        case 0:
        	desc="断开";
        	 break;
        case 1:
        	desc="连接";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}
	
	public int setLinkCarStatus(short linkCarStatus) {
		int ret=0;
		if(linkCarStatus!=0 && linkCarStatus!=1)
		{
			ret =-1;
		}
		else
		{
			if(this.linkCarStatus != linkCarStatus)
			{
				this.linkCarStatus = linkCarStatus;
				ret =1;
			}
		}
		return ret;
	}


	public short getGunCloseStatus() {
		return gunCloseStatus;
	}

    public String getGunCloseStatusDesc() {
		
		String desc="";
		switch(this.gunCloseStatus)
        {
        case 0:
        	desc="未收好";
        	 break;
        case 1:
        	desc="收好";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setGunCloseStatus(short gunCloseStatus) {
		int ret=0;
		if(gunCloseStatus!=0 && gunCloseStatus!=1)
		{
			ret =-1;
		}
		else
		{
			if(this.gunCloseStatus != gunCloseStatus)
			{
				this.gunCloseStatus = gunCloseStatus;
				ret =1;
			}
		}
		return ret;
	}


	public short getGunLidStatus() {
		return gunLidStatus;
	}
    public String getGunLidStatusDesc() {
		
		String desc="";
		switch(this.gunLidStatus)
        {
        case 0:
        	desc="未关";
        	 break;
        case 1:
        	desc="关上";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setGunLidStatus(short gunLidStatus) {
		int ret=0;
		if(gunLidStatus!=0 && gunLidStatus!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.gunLidStatus != gunLidStatus)
			{
				this.gunLidStatus = gunLidStatus;
				ret =1;
			}
		}
		return ret;
	}


	public short getCommStatusWithCar() {
		return commStatusWithCar;
	}

	public String getCommStatusWithCarDesc() {
	
		String desc="";
		switch(this.commStatusWithCar)
        {
        case 0:
        	desc="未通讯";
        	 break;
        case 1:
        	desc="通讯";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setCommStatusWithCar(short commStatusWithCar) {
		int ret=0;
		if(commStatusWithCar!=0 && commStatusWithCar!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.commStatusWithCar != commStatusWithCar)
			{
				this.commStatusWithCar = commStatusWithCar;
				ret =1;
			}
		}
		return ret;
	}


	public short getCarPlaceStatus() {
		return carPlaceStatus;
	}
    public String getCarPlaceStatusDesc() {
		
		String desc="";
		switch(this.carPlaceStatus)
        {
        case 0:
        	desc="无车";
        	 break;
        case 1:
        	desc="有车";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setCarPlaceStatus(short carPlaceStatus) {
		int ret=0;
		if(carPlaceStatus!=0 && carPlaceStatus!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.carPlaceStatus != carPlaceStatus)
			{
				this.carPlaceStatus = carPlaceStatus;
				ret =1;
			}
		}
		return ret;
	}


	public short getCardReaderFault() {
		return cardReaderFault;
	}

    public String getCardReaderFaultDesc() {
		
		String desc="";
		switch(this.cardReaderFault)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="故障";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setCardReaderFault(short cardReaderFault) {
		int ret=0;
		if(cardReaderFault!=0 && cardReaderFault!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.cardReaderFault != cardReaderFault)
			{
				this.cardReaderFault = cardReaderFault;
				ret =1;
			}
		}
		return ret;
	}


	public short getUrgentStopFault() {
		return urgentStopFault;
	}
    public String getUrgentStopFaultDesc() {
		
		String desc="";
		switch(this.urgentStopFault)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="急停故障";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setUrgentStopFault(short urgentStopFault) {
		int ret=0;
		if(urgentStopFault!=0 && urgentStopFault!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.urgentStopFault != urgentStopFault)
			{
				this.urgentStopFault = urgentStopFault;
				ret =1;
			}
		}
		return ret;
	}


	public short getArresterFault() {
		return arresterFault;
	}
    public String getArresterFaultDesc() {
		
		String desc="";
		switch(this.arresterFault)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="故障";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setArresterFault(short arresterFault) {
		int ret=0;
		if(arresterFault!=0 && arresterFault!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.arresterFault != arresterFault)
			{
				this.arresterFault = arresterFault;
				ret =1;
			}
		}
		return ret;
	}


	public short getInsulationCheckFault() {
		return insulationCheckFault;
	}
    public String getInsulationCheckFaultDesc() {
		
		String desc="";
		switch(this.insulationCheckFault)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="故障";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setInsulationCheckFault(short insulationCheckFault) {
		int ret=0;
		if(insulationCheckFault!=0 && insulationCheckFault!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.insulationCheckFault != insulationCheckFault)
			{
				this.insulationCheckFault = insulationCheckFault;
				ret =1;
			}
		}
		return ret;
	}


	public short getGunUnconnectWarn() {
		return gunUnconnectWarn;
	}
	 public String getGunUnconnectWarnDesc() {
			
			String desc="";
			switch(this.gunUnconnectWarn)
	        {
	        case 0:
	        	desc="正常";
	        	 break;
	        case 1:
	        	desc="告警";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}


	public int setGunUnconnectWarn(short gunUnconnectWarn) {
		int ret=0;
		if(gunUnconnectWarn!=0 && gunUnconnectWarn!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.gunUnconnectWarn != gunUnconnectWarn)
			{
				this.gunUnconnectWarn = gunUnconnectWarn;
				ret =1;
			}
		}
		return ret;
	}


	public short getTransRecordFullWarn() {
		return transRecordFullWarn;
	}
	 public String getTransRecordFullWarnDesc() {
			
			String desc="";
			switch(this.transRecordFullWarn)
	        {
	        case 0:
	        	desc="正常";
	        	 break;
	        case 1:
	        	desc="告警";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}

	public int setTransRecordFullWarn(short transRecordFullWarn) {
		int ret=0;
		if(transRecordFullWarn!=0 && transRecordFullWarn!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.transRecordFullWarn != transRecordFullWarn)
			{
				this.transRecordFullWarn = transRecordFullWarn;
				ret =1;
			}
		}
		return ret;
	}


	public short getMeterError() {
		return meterError;
	}
	 public String getMeterErrorDesc() {
			
			String desc="";
			switch(this.transRecordFullWarn)
	        {
	        case 0:
	        	desc="电度表正常";
	        	 break;
	        case 1:
	        	desc="电度表异常";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}

	public int setMeterError(short meterError) {
		int ret=0;
		if(meterError!=0 && meterError!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.meterError != meterError)
			{
				this.meterError = meterError;
				ret =1;
			}
		}
		return ret;
	}


	public short getAcInVolWarn() {
		return acInVolWarn;
	}
	public String getAcInVolWarnDesc() {
		
		String desc="";
		switch(this.acInVolWarn)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="过压";
       	 	break;
        case 2:
        	desc="欠压";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setAcInVolWarn(short acInVolWarn) {
		int ret=0;
		if(acInVolWarn!=0 && acInVolWarn!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.acInVolWarn != acInVolWarn)
			{
				this.acInVolWarn = acInVolWarn;
				ret =1;
			}
		}
		return ret;
	}


	public short getChargeOverTemp() {
		return chargeOverTemp;
	}

    public String getChargeOverTempDesc() {
		
		String desc="";
		switch(this.chargeOverTemp)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="过温故障";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
	}

	public int setChargeOverTemp(short chargeOverTemp) {
		int ret=0;
		if(chargeOverTemp!=0 && chargeOverTemp!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.chargeOverTemp != chargeOverTemp)
			{
				this.chargeOverTemp = chargeOverTemp;
				ret =1;
			}
		}
		return ret;
	}


	public short getAcCurrentLoadWarn() {
		return acCurrentLoadWarn;
	}

	 public String getAcCurrentLoadWarnDesc() {
			
			String desc="";
			switch(this.acCurrentLoadWarn)
	        {
	        case 0:
	        	desc="正常";
	        	 break;
	        case 1:
	        	desc="过负荷告警";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}

	public int setAcCurrentLoadWarn(short acCurrentLoadWarn) {
		int ret=0;
		if(acCurrentLoadWarn!=0 && acCurrentLoadWarn!=1 &&  acCurrentLoadWarn!=2)
		{
			ret=-1;
		}
		else
		{
			if(this.acCurrentLoadWarn != acCurrentLoadWarn)
			{
				this.acCurrentLoadWarn = acCurrentLoadWarn;
				ret =1;
			}
		}
		return ret;
	}


	public short getOutRelayStatus() {
		return outRelayStatus;
	}
	 public String getOutRelayStatusDesc() {
			
			String desc="";
			switch(this.outRelayStatus)
	        {
	        case 0:
	        	desc="关";
	        	 break;
	        case 1:
	        	desc="开";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}


	public int setOutRelayStatus(short outRelayStatus) {
		int ret=0;
		if(outRelayStatus!=0 && outRelayStatus!=1)
		{
			ret=-1;
		}
		else
		{
			if(this.outRelayStatus != outRelayStatus)
			{
				this.outRelayStatus = outRelayStatus;
				ret =1;
			}
		}
		return ret;
	}


	public int getWorkingStatus() {
		return workingStatus;
	}

	 public String getWorkingStatusDesc() {
			
			String desc="";
			switch(this.workingStatus)
	        {
	        case 0:
	        	desc="离线";
	        	 break;
	        case 2:
	        	desc="待机";
	       	 	break;
	        case 3:
	        	desc="工作";
	       	 	break;
	        case 8:
	        	desc="预约";
	       	 	break;
	        case 9:
	        	desc="在线升级";
	       	 	break;
	        case 10:
	        	desc="操作中(进入刷卡，用户名鉴权流程)";
	        	break;
	        case 12:
	        	desc="充电模式选择(防止用户在操作中被预约)";
	 	       	break;
	        case 11:
	        	desc="设置状态";
	       	 	break;
	        case 31:
	        	desc="欠压故障";
	        	break;
	        case 32:
	        	desc="过压故障";
	        	break;
	        case 33:
	        	desc="过电流故障";	   
	       	 	break;
	        case 34:
	        	desc="防雷器故障";	 
	        	break;
	        case 35:
	        	desc="电表故障";	 
	        	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}


	public int setWorkingStatus(int workingStatus) {
		int ret=0;
		
		
		if(	!EpGunService.checkWorkStatus(workingStatus))
		{
			ret=-1;
		}
		else
		{
			if(this.workingStatus != workingStatus)
			{
				this.workingStatus = workingStatus;
				ret =1;
				logger.info("[realData],epCode:{},epGunNo:{},RealChargeInfo.workingStatus:{}",new Object[]{epCode,epGunNo,workingStatus});
			}
			//做了个时间更新的标记， 之后用到
			this.workStatusUpdateTime =DateUtil.getCurrentSeconds();
		}
		return ret;
	}


	public int getOutVoltage() {
		return outVoltage;
	}


	public int setOutVoltage(int outVoltage) {
		int ret=0;
		
		if(this.outVoltage != outVoltage)
		{
			this.outVoltage = outVoltage;
			ret =1;
		}
		
		return ret;
	}


	public int getOutCurrent() {
		return outCurrent;
	}


	public int setOutCurrent(int outCurrent) {
		int ret=0;
		
		if(this.outCurrent != outCurrent)
		{
			this.outCurrent = outCurrent;
			ret =1;
		}
		
		return ret;
	}


	public int getCarPlaceLockStatus() {
		return carPlaceLockStatus;
	}
    
	 public String getCarPlaceLockStatusDesc() {
			
			String desc="";
			switch(this.carPlaceLockStatus)
	        {
	        case 0:
	        	desc="地锁设备无法探测";
	        	 break;
	        case 1:
	        	desc="降下";
	       	 	break;
	        case 2:
	        	desc="升起";
	        	break;
	        case 3:
	        	desc="运动中";
	       	 	break;
	        case 4:
	        	desc="异常或故障";
	        	break;
	        default:
	        	desc="未知状态";
	       	 	break;
	        }
			return desc;
	 }


	public int setCarPlaceLockStatus(int carPlaceLockStatus) {
		int ret=0;
		if(!EpGunService.checkCarPlaceLockStatus(carPlaceLockStatus))
		{
			ret=-1;
		}
		else
		{
			if(this.carPlaceLockStatus != carPlaceLockStatus)
			{
				this.carPlaceLockStatus = carPlaceLockStatus;
				ret =1;
			}
		}
		return ret;
	}


	public int getSoc() {
		return soc;
	}


	public int setSoc(int soc) {
		int ret=0;
		
		if(this.soc != soc)
		{
			this.soc = soc;
			ret =1;
		}
		
		return ret;
	}


	public int getChargedTime() {
		return chargedTime;
	}


	public int setChargedTime(int chargedTime) {
		int ret=0;
		
		if(this.chargedTime != chargedTime)
		{
			this.chargedTime = chargedTime;
			ret =1;
		}
		
		return ret;
	}


	public int getChargeRemainTime() {
		return chargeRemainTime;
	}


	public int setChargeRemainTime(int chargeRemainTime) {
		int ret=0;
		
		if(this.chargeRemainTime != chargeRemainTime)
		{
			this.chargeRemainTime = chargeRemainTime;
			ret =1;
		}
		
		return ret;
	}


	public int getTotalActivMeterNum() {
		return totalActivMeterNum;
	}


	public int setTotalActivMeterNum(int totalActivMeterNum) {
		int ret=0;
		
		if(this.totalActivMeterNum != totalActivMeterNum)
		{
			this.totalActivMeterNum = totalActivMeterNum;
			ret =1;
		}
		
		return ret;
	}

	
	public int getChargedMeterNum() {
		return chargedMeterNum;
	}


	public int setChargedMeterNum(int chargedMeterNum) {
		int ret=0;
		
		if(this.chargedMeterNum != chargedMeterNum)
		{
			this.chargedMeterNum = chargedMeterNum;
			ret =1;
		}
		
		return ret;
	}
	


	public int getChargePrice() {
		return chargePrice;
	}


	public int setChargePrice(int chargePrice) {
		this.chargePrice = chargePrice;
		return 0;
	}


	public int getChargeUserId() {
		return chargeUserId;
	}


	public int setChargeUserId(int chargeUserId) {
		int ret=0;
		
		
		this.chargeUserId = chargeUserId;
			
		
		return ret;
	}


	public String getChargeSerialNo() {
		return chargeSerialNo;
	}


	public void setChargeSerialNo(String chargeSerialNo) {
		this.chargeSerialNo = chargeSerialNo;
	}


	public int getChargeStartTime() {
		return chargeStartTime;
	}


	public void setChargeStartTime(int chargeStartTime) {
		this.chargeStartTime = chargeStartTime;
	}


	public int getChargeStartMeterNum() {
		return chargeStartMeterNum;
	}


	public void setChargeStartMeterNum(int chargeStartMeterNum) {
		this.chargeStartMeterNum = chargeStartMeterNum;
	}


	public int getChargedCost() {
		return chargedCost;
	}


	public int setChargedCost(int chargedCost) {
		int ret=0;
		if(this.chargedCost != chargedCost)
		{
			this.chargedCost = chargedCost;
			ret =1;
		}
		return ret;
	}


	public int getFronzeAmt() {
		return fronzeAmt;
	}


	public int setFronzeAmt(int fronzeAmt) {
		this.fronzeAmt = fronzeAmt;
		return 0;
	}
	
	public void endCharge()
	{
	}


	public long getWorkStatusUpdateTime() {
		return workStatusUpdateTime;
	}
	
	/**
	 * 
	 * @return-3:在最大遥测，遥信最大值范围外，-2:保留字段,-1：参数非法，0：没有变化;1:变化
	 */
	protected int setFieldValue(int pointAddr,SingleInfo info)
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
	
	protected SingleInfo getFieldValue(int pointAddr)
	{
		return null;
	}
}
