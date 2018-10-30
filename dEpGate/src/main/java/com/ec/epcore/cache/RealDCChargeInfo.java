package com.ec.epcore.cache;


import com.alibaba.fastjson.JSON;
import com.ec.config.Global;
import com.ec.constants.YXCConstants;
import com.ec.epcore.service.EpChargeService;
import com.ec.epcore.service.EpGunService;
import com.ec.net.proto.SingleInfo;
import com.ec.utils.NumUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblChargeDCInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RealDCChargeInfo extends RealChargeInfo{
	
	private static final Logger logger = LoggerFactory.getLogger(RealDCChargeInfo.class);
	
	//直流专用遥信
	//单位遥信	17	电池反接故障	0:正常;1:故障		爱充直流特别遥信
	private short battryErrorLink;
	//单位遥信	18	烟雾报警故障	0:正常;1:告警	
	private short fogsWarn;
	//单位遥信	19	BMS 通信异常	0:正常;1:告警	
	private short bmsCommException;
	//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
	private short dcMeterException;
	
	//单位遥信	21	直流输出过流告警	0:正常;1:告警	
	private short chargeOutOverCurrent;

	//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
	private short carChargeMode;
	//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
	private short carSocWarn;
	//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
	private short chargeModSampleOverTemp; 
	//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
	private short chargeOutLinkerOverTemp;
	//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
	private short chargeOutLinkerStatus;
	//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
	private short chargeWholeOverCurrentWarn;
	//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
	private short chargeVolWarn;
	//双位遥信	16	BMS过压/欠压告警	0:正常;1:过压;2:欠压
	private short bmsVolWarn;

	//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
	private int signleBattryHighVol;
	//34	蓄电池当前温度
	private int bpTemperature;
	//35	蓄电池最低温度
	private int bpLowestTemperature;
	
	//36	整车动力电池总电压	精度0.1，单位v	
	private int carBattryTotalVol;

	//直流专用遥测
	//41	A相电压	精度0.1，单位v	输入
	private int inAVol;
	//42	B相电压	精度0.1，单位v
	private int inBVol;
	//43	C相电压	精度0.1，单位v
	private int inCVol;
	//44	A相电流	精度0.1，单位v
	private int inACurrent;
	//45	B相电流	精度0.1，单位v	
	private int inBCurrent;
	//46	C相电流	精度0.1，单位v	
	private int inCCurrent;
	//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
	private int outHighestVol;
	//48	最低输出电压	精度0.1，单位v	
	private int outLowestVol;
	//49	最大输出电流	精度0.1，单位A	
	private int outHighestCurrent;
	//50          桩内部环境温度 
	private int epInterTemperature;
	
	private Map<Integer,ChargePowerModInfo> mapPowerModule = new  ConcurrentHashMap<Integer,ChargePowerModInfo>();

	private ChargeCarInfo chargeCarInfo;
	
	public void cleanChargeInfo()
	{
		mapPowerModule.clear();
		chargeCarInfo =null;
	}
	
	
	
	public void powerModuleEndCharge()
	{
		//电源模块数据
        Iterator iter = mapPowerModule.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ChargePowerModInfo powerModule=(ChargePowerModInfo) entry.getValue();
			if(powerModule==null)
				continue;
			powerModule.endCharge();
		}
	}
	
	public Map<Integer, SingleInfo> getWholePowerModYc()
	{
		Map<Integer, SingleInfo> ycRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		
		//电源模块数据
        Iterator iter = mapPowerModule.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ChargePowerModInfo powerModule=(ChargePowerModInfo) entry.getValue();
			
			for(int i=1;i<=9;i++)
			{
				SingleInfo info = new SingleInfo();
				
				int startPos = (powerModule.getModId()-1) *9;
				info.setAddress( startPos + i);
				info.setIntValue(0);
			}
		}
		
		return ycRealInfo;
	}

	public ChargeCarInfo getChargeCarInfo() {
		return chargeCarInfo;
	}




	public void setChargeCarInfo(ChargeCarInfo chargeCarInfo) {
		this.chargeCarInfo = chargeCarInfo;
	}




	public int getEpInterTemperature() {
		return epInterTemperature;
	}

	public int setEpInterTemperature(int epInterTemperature) {
		
        int ret=0;
		
		if(this.epInterTemperature != epInterTemperature)
		{
			this.epInterTemperature = epInterTemperature;
			ret =1;
		}
		
		return ret;
		
	}



	public short getBattryErrorLink() {
		return battryErrorLink;
	}

	public String getBattryErrorLinkDesc() {
		
		String desc="";
		switch(this.battryErrorLink)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="反接故障";
       	 	break;
        default:
        	desc="未知状态";
       	 	break;
        }
		return desc;
     }
	public int setBattryErrorLink(short battryErrorLink) {
		int ret=0;
		
		if(this.battryErrorLink != battryErrorLink)
		{
			this.battryErrorLink = battryErrorLink;
			ret =1;
		}
		
		return ret;
	}

	public short getFogsWarn() {
		return fogsWarn;
	}
    public String getFogsWarnDesc() {
		
		String desc="";
		switch(this.fogsWarn)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="告警";
       	 	break;
        default:
        	desc="未知状态";
       	 	break;
        }
		return desc;
     }
	public int setFogsWarn(short fogsWarn) {
		int ret=0;
		
		if(this.fogsWarn != fogsWarn)
		{
			this.fogsWarn = fogsWarn;
			ret =1;
		}
		
		return ret;
	}

	public short getBmsCommException() {
		return bmsCommException;
	}
	
    public String getBmsCommExceptionDesc() {
		
		String desc="";
		switch(this.bmsCommException)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="通信异常";
       	 	break;
        default:
        	desc="未知状态";
       	 	break;
        }
		return desc;
     }

	public int setBmsCommException(short bmsCommException) {
		int ret=0;
		
		if(this.bmsCommException != bmsCommException)
		{
			this.bmsCommException = bmsCommException;
			ret =1;
		}
		
		return ret;
	}

	public short getDcMeterException() {
		return dcMeterException;
	}

    public String getDcMeterExceptionDesc() {
		
		String desc="";
		switch(this.dcMeterException)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="直流电度表异常";
       	 	break;
        default:
        	desc="未知状态";
       	 	break;
        }
		return desc;
     }

	public int setDcMeterException(short dcMeterException) {
		int ret=0;
		
		if(this.dcMeterException != dcMeterException)
		{
			this.dcMeterException = dcMeterException;
			ret =1;
		}
		
		return ret;
	}

	public short getCarChargeMode() {
		return carChargeMode;
	}

	 public String getCarChargeModeDesc() {
			
			String desc="";
			switch(this.carChargeMode)
	        {
	        case 0:
	        	desc="不可信";
	        	 break;
	        case 1:
	        	desc="恒压";
	       	 	break;
	        case 2:
	        	desc="恒流";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
	     }
	public int setCarChargeMode(short carChargeMode) {
		int ret=0;
		
		if(this.carChargeMode != carChargeMode)
		{
			this.carChargeMode = carChargeMode;
			ret =1;
		}
		
		return ret;
	}

	public short getCarSocWarn() {
		return carSocWarn;
	}
	 public String getCarSocWarnDesc() {
			
			String desc="";
			switch(this.carSocWarn)
	        {
	        case 0:
	        	desc="不可信";
	        	 break;
	        case 1:
	        	desc="过高";
	       	 	break;
	        case 2:
	        	desc="过低";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
	     }
	

	public int setCarSocWarn(short carSocWarn) {
		int ret=0;
		
		if(this.carSocWarn != carSocWarn)
		{
			this.carSocWarn = carSocWarn;
			ret =1;
		}
		
		return ret;
	}

	public short getChargeModSampleOverTemp() {
		return chargeModSampleOverTemp;
	}
	
	 public String getChargeModSampleOverTempDesc() {
			
			String desc="";
			switch(this.chargeModSampleOverTemp)
	        {
	        case 0:
	        	desc="正常";
	        	 break;
	        case 1:
	        	desc="过温";
	       	 	break;
	        case 2:
	        	desc="不可信";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
	     }

	public int setChargeModSampleOverTemp(short chargeModSampleOverTemp) {
		int ret=0;
		
		if(this.chargeModSampleOverTemp != chargeModSampleOverTemp)
		{
			this.chargeModSampleOverTemp = chargeModSampleOverTemp;
			ret =1;
		}
		
		return ret;
	}

	public short getChargeOutLinkerOverTemp() {
		return chargeOutLinkerOverTemp;
	}
	
	public String getChargeOutLinkerOverTempDesc() {
		
		String desc="";
		switch(this.chargeOutLinkerOverTemp)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="过温";
       	 	break;
        case 2:
        	desc="不可信";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
     }
	

	public int setChargeOutLinkerOverTemp(short chargeOutLinkerOverTemp) {
		int ret=0;
		
		if(this.chargeOutLinkerOverTemp != chargeOutLinkerOverTemp)
		{
			this.chargeOutLinkerOverTemp = chargeOutLinkerOverTemp;
			ret =1;
		}
		
		return ret;
	}

	public short getChargeOutLinkerStatus() {
		return chargeOutLinkerStatus;
	}
	
    public String getChargeOutLinkerStatusDesc() {
		
		String desc="";
		switch(this.chargeOutLinkerStatus)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="不正常";
       	 	break;
        case 2:
        	desc="不可信";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
     }

	public int setChargeOutLinkerStatus(short chargeOutLinkerStatus) {
		int ret=0;
		
		if(this.chargeOutLinkerStatus != chargeOutLinkerStatus)
		{
			this.chargeOutLinkerStatus = chargeOutLinkerStatus;
			ret =1;
		}
		
		return ret;
	}

	public short getChargeWholeOverCurrentWarn() {
		return chargeWholeOverCurrentWarn;
	}
	
	 public String getChargeWholeOverCurrentWarnDesc() {
			
			String desc="";
			switch(this.chargeWholeOverCurrentWarn)
	        {
	        case 0:
	        	desc="正常";
	        	 break;
	        case 1:
	        	desc="过流";
	       	 	break;
	        case 2:
	        	desc="不可信";
	       	 	break;
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
	     }

	public int setChargeWholeOverCurrentWarn(short chargeWholeOverCurrentWarn) {
		int ret=0;
		
		if(this.chargeWholeOverCurrentWarn != chargeWholeOverCurrentWarn)
		{
			this.chargeWholeOverCurrentWarn = chargeWholeOverCurrentWarn;
			ret =1;
		}
		
		return ret;
	}

	public short getChargeVolWarn() {
		return chargeVolWarn;
	}
	 public String getChargeVolWarnDesc() {
			
			String desc="";
			switch(this.chargeVolWarn)
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
	public int setChargeVolWarn(short chargeVolWarn) {
		int ret=0;
		
		if(this.chargeVolWarn != chargeVolWarn)
		{
			this.chargeVolWarn = chargeVolWarn;
			ret =1;
		}
		
		return ret;
	}
	

	/*public int getOneBattryHighVol() {
		return oneBattryHighVol;
	}

	public int setOneBattryHighVol(int oneBattryHighVol) {
		int ret=0;
		
		if(this.oneBattryHighVol != oneBattryHighVol)
		{
			this.oneBattryHighVol = oneBattryHighVol;
			ret =1;
		}
		
		return ret;
	}
	*/
	public int getBpHighestTemperature() {
		return bpTemperature;
	}

	public int setBpHighestTemperature(int bpTemperature) {
		int ret=0;
		
		if(this.bpTemperature != bpTemperature)
		{
			this.bpTemperature = bpTemperature;
			ret =1;
		}
		
		return ret;
	}

	public int getBpLowestTemperature() {
		return bpLowestTemperature;
	}

	public int setBpLowestTemperature(int bpLowestTemperature) {
		int ret=0;
		
		if(this.bpLowestTemperature != bpLowestTemperature)
		{
			this.bpLowestTemperature = bpLowestTemperature;
			ret =1;
		}
		
		return ret;
	}

	public int getCarBattryTotalVol() {
		return carBattryTotalVol;
	}

	public int setCarBattryTotalVol(int carBattryTotalVol) {
		int ret=0;
		
		if(this.carBattryTotalVol != carBattryTotalVol)
		{
			this.carBattryTotalVol = carBattryTotalVol;
			ret =1;
		}
		
		return ret;
	}

	public int getInAVol() {
		return inAVol;
	}

	public int setInAVol(int inAVol) {
		int ret=0;
		
		if(this.inAVol != inAVol)
		{
			this.inAVol = inAVol;
			ret =1;
		}
		
		return ret;
	}

	public int getInBVol() {
		return inBVol;
	}

	public int setInBVol(int inBVol) {
		int ret=0;
		
		if(this.inBVol != inBVol)
		{
			this.inBVol = inBVol;
			ret =1;
		}
		
		return ret;
	}

	public int getInCVol() {
		return inCVol;
	}

	public int setInCVol(int inCVol) {
		int ret=0;
		
		if(this.inCVol != inCVol)
		{
			this.inCVol = inCVol;
			ret =1;
		}
		
		return ret;
	}

	public int getInACurrent() {
		return inACurrent;
	}

	public int setInACurrent(int inACurrent) {
		int ret=0;
		
		if(this.inACurrent != inACurrent)
		{
			this.inACurrent = inACurrent;
			ret =1;
		}
		
		return ret;
	}

	public int getInBCurrent() {
		return inBCurrent;
	}

	public int setInBCurrent(int inBCurrent) {
		int ret=0;
		
		if(this.inBCurrent != inBCurrent)
		{
			this.inBCurrent = inBCurrent;
			ret =1;
		}
		
		return ret;
	}

	public int getInCCurrent() {
		return inCCurrent;
	}

	public int setInCCurrent(int inCCurrent) {
		int ret=0;
		
		if(this.inCCurrent != inCCurrent)
		{
			this.inCCurrent = inCCurrent;
			ret =1;
		}
		
		return ret;
	}

	public int getOutHighestVol() {
		return outHighestVol;
	}

	public int setOutHighestVol(int outHighestVol) {
		int ret=0;
		
		if(this.outHighestVol != outHighestVol)
		{
			this.outHighestVol = outHighestVol;
			ret =1;
		}
		
		return ret;
	}

	public int getOutLowestVol() {
		return outLowestVol;
	}

	public int setOutLowestVol(int outLowestVol) {
		int ret=0;
		
		if(this.outLowestVol != outLowestVol)
		{
			this.outLowestVol = outLowestVol;
			ret =1;
		}
		
		return ret;
	}

	public int getOutHighestCurrent() {
		return outHighestCurrent;
	}

	public int setOutHighestCurrent(int outHighestCurrent) {
		int ret=0;
		
		if(this.outHighestCurrent != outHighestCurrent)
		{
			this.outHighestCurrent = outHighestCurrent;
			ret =1;
		}
		
		return ret;
	}

	public int getSignleBattryHighVol() {
		return signleBattryHighVol;
	}

	public int setSignleBattryHighVol(int signleBattryHighVol) {
		int ret=0;
		
		if(this.signleBattryHighVol != signleBattryHighVol)
		{
			this.signleBattryHighVol = signleBattryHighVol;
			ret =1;
		}
		return ret;
	}
	
	

	public short getChargeOutOverCurrent() {
		return chargeOutOverCurrent;
	}
	
	public String getChargeOutOverCurrentDesc() {
		
		String desc="";
		switch(this.chargeOutOverCurrent)
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
	

	public int setChargeOutOverCurrent(short chargeOutOverCurrent) {
		int ret=0;
		if(chargeOutOverCurrent!=0 && chargeOutOverCurrent!=1)
			ret=-1;
		{
			if(this.chargeOutOverCurrent != chargeOutOverCurrent)
			{
				this.chargeOutOverCurrent = chargeOutOverCurrent;
				ret =1;
			}
		}
		
		return ret;
	}

	public short getBmsVolWarn() {
		return bmsVolWarn;
	}
	public String getBmsVolWarnDesc() {
		
		String desc="";
		switch(this.bmsVolWarn)
        {
        case 0:
        	desc="正常";
        	 break;
        case 1:
        	desc="过压";
       	 	break;
        case 2:
        	desc="过压";
       	 	break;
        default:
        	desc="未知";
       	 	break;
        }
		return desc;
     }

	public int setBmsVolWarn(short bmsVolWarn) {
		int ret=0;
		if(bmsVolWarn!=1 && bmsVolWarn!=2&& bmsVolWarn!=0)
			ret=-1;
		{
			if(this.bmsVolWarn != bmsVolWarn)
			{
				this.bmsVolWarn = bmsVolWarn;
				ret =1;
			}
		}
		
		return ret;
	}

	public RealDCChargeInfo()
	{
		//直流专用遥信
		battryErrorLink =0;
		fogsWarn =0;
		bmsCommException=0;
		dcMeterException =0;

		//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
		carChargeMode=0;
		carSocWarn=0;
		chargeModSampleOverTemp=0; 
		chargeOutLinkerOverTemp =0;
		chargeOutLinkerStatus=0;
		chargeWholeOverCurrentWarn=0;
		chargeVolWarn=0;
		
		//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
		signleBattryHighVol=0;
		bpTemperature=0;
		bpLowestTemperature=0;
		
		//36	整车动力电池总电压	精度0.1，单位v	
		carBattryTotalVol=0;

		//直流专用遥测
		inAVol=0;
		inBVol=0;
		inCVol=0;
		inACurrent=0;
		inBCurrent=0;
		inCCurrent=0;
		outHighestVol=0;
		outLowestVol=0;
		outHighestCurrent=0;

		//12  蓄电池充电过流告警	1：M_SP_NA_1  BIN 码  1Byte	布尔型,  变化上传；0：不过	流，1 过流
		//overCurrentWarn=0;
		//13  蓄电池模块采样点过温告警 1：M_SP_NA_1  BIN 码  1Byte 布尔型,  变化上传；0：不过温，1 过温
		//overTempWarn=0;
		//16  单体电池最高电压	11：M_ME_NB_1  BIN 码  2Byte	精确到小数点后三位
		//oneHighestVol=0;
		//17  单体电池最低电压11：M_ME_NB_1  BIN 码  2Byte 精确到小数点后三位
		//oneLowestVol=0;
	}
	
	
	public void convertFromDB(TblChargeDCInfo tblRealData)
	{
		if(tblRealData==null)
			return;
		
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
		
		//===============直流特别部分=====================
		battryErrorLink = tblRealData.getBattryErrorLink();
		//单位遥信	18	烟雾报警故障	0:正常;1:告警	
		fogsWarn = tblRealData.getFogsWarn();
		//单位遥信	19	BMS 通信异常	0:正常;1:告警	
		bmsCommException = tblRealData.getBmsCommException();
		//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
		dcMeterException = tblRealData.getDcMeterException();
		chargeOutOverCurrent= tblRealData.getChargeOutOverCurrent();

		//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
		carChargeMode= tblRealData.getCarChargeMode();
		//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
		carSocWarn = tblRealData.getCarSocWarn();
		//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
		chargeModSampleOverTemp = tblRealData.getChargeModSampleOverTemp(); 
		//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
		chargeOutLinkerOverTemp = tblRealData.getChargeOutLinkerOverTemp();
		//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
		chargeOutLinkerStatus = tblRealData.getChargeOutLinkerStatus();
		//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
		chargeWholeOverCurrentWarn = tblRealData.getChargeWholeOverCurrentWarn();
		//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
		chargeVolWarn = tblRealData.getChargeVolWarn();
		
		bmsVolWarn = tblRealData.getBmsVolWarn();
		
		

		//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
		signleBattryHighVol = tblRealData.getSignleBattryHighVol().multiply(Global.DecTime1).intValue();
		//34	蓄电池最高温度
		bpTemperature = tblRealData.getBpHighestTemperature().multiply(Global.DecTime1).intValue();
		//35	蓄电池最低温度
		bpLowestTemperature = tblRealData.getBpLowestTemperature().multiply(Global.DecTime1).intValue();
		
		//36	整车动力电池总电压	精度0.1，单位v	
		carBattryTotalVol = tblRealData.getCarBattryTotalVol().multiply(Global.DecTime1).intValue();

		//直流专用遥测
		//41	A相电压	精度0.1，单位v	输入
		inAVol = tblRealData.getInAVol().multiply(Global.DecTime1).intValue();
		//42	B相电压	精度0.1，单位v
		inBVol = tblRealData.getInBVol().multiply(Global.DecTime1).intValue();
		//43	C相电压	精度0.1，单位v
		inCVol = tblRealData.getInCVol().multiply(Global.DecTime1).intValue();
		//44	A相电流	精度0.1，单位v
		inACurrent = tblRealData.getInACurrent().multiply(Global.DecTime2).intValue();
		//45	B相电流	精度0.1，单位v	
		inBCurrent = tblRealData.getInBCurrent().multiply(Global.DecTime2).intValue();
		//46	C相电流	精度0.1，单位v	
		inCCurrent = tblRealData.getInCCurrent().multiply(Global.DecTime2).intValue();
		//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
		outHighestVol = tblRealData.getOutHighestVol().multiply(Global.DecTime1).intValue();
		//48	最低输出电压	精度0.1，单位v	
		outLowestVol = tblRealData.getOutLowestVol().multiply(Global.DecTime1).intValue();
		//49	最大输出电流	精度0.1，单位A	
		outHighestCurrent = tblRealData.getOutHighestCurrent().multiply(Global.DecTime2).intValue();
	}
	
	@Override
	public boolean saveDb()
	{
		TblChargeDCInfo tblRealData =new TblChargeDCInfo();
		
		tblRealData.setEp_code(this.epCode);
		tblRealData.setEp_gun_no(this.epGunNo);
		
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
		tblRealData.setChargedCost((new BigDecimal(this.getChargedCost())).multiply(Global.Dec2));
				
		//充电相关
		
		//===============直流特别部分=====================
		tblRealData.setBattryErrorLink(battryErrorLink);
		//单位遥信	18	烟雾报警故障	0:正常;1:告警	
		tblRealData.setFogsWarn(fogsWarn);
		//单位遥信	19	BMS 通信异常	0:正常;1:告警	
		tblRealData.setBmsCommException(bmsCommException);
		//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
		tblRealData.setDcMeterException(dcMeterException);
		//单位遥信	21	直流输出过流告警	0:正常;1:告警	
		tblRealData.setBmsVolWarn(this.chargeOutOverCurrent);

		//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
		tblRealData.setCarChargeMode(carChargeMode);
		//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
		tblRealData.setCarSocWarn(carSocWarn);
		//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
		tblRealData.setChargeModSampleOverTemp(chargeModSampleOverTemp); 
		//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
		tblRealData.setChargeOutLinkerOverTemp(chargeOutLinkerOverTemp);
		//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
		tblRealData.setChargeOutLinkerStatus(chargeOutLinkerStatus);
		//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
		tblRealData.setChargeWholeOverCurrentWarn(chargeWholeOverCurrentWarn);
		//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
		tblRealData.setChargeVolWarn(chargeVolWarn);
		
		//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
		tblRealData.setBmsVolWarn(bmsVolWarn);
		
		

		//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
		tblRealData.setSignleBattryHighVol((new BigDecimal(this.signleBattryHighVol)).multiply(Global.Dec1));
		//34	蓄电池最高温度
		tblRealData.setBpHighestTemperature((new BigDecimal(this.bpTemperature)).multiply(Global.Dec1));
		//35	蓄电池最低温度
		tblRealData.setBpLowestTemperature((new BigDecimal(this.bpLowestTemperature)).multiply(Global.Dec1));
		
		//36	整车动力电池总电压	精度0.1，单位v	
		tblRealData.setCarBattryTotalVol((new BigDecimal(this.carBattryTotalVol)).multiply(Global.Dec1));
		
		
		//直流专用遥测
		tblRealData.setInAVol((new BigDecimal(this.inAVol)).multiply(Global.Dec1));
		
		//42	B相电压	精度0.1，单位v
		tblRealData.setInBVol((new BigDecimal(this.inBVol)).multiply(Global.Dec1));
		//43	C相电压	精度0.1，单位v
		tblRealData.setInCVol((new BigDecimal(this.inCVol)).multiply(Global.Dec1));
		//44	A相电流	精度0.1，单位v
		tblRealData.setInACurrent((new BigDecimal(this.inACurrent)).multiply(Global.Dec2));
		//45	B相电流	精度0.1，单位v	
		tblRealData.setInBCurrent((new BigDecimal(this.inBCurrent)).multiply(Global.Dec2));
		//46	C相电流	精度0.1，单位v	
		tblRealData.setInCCurrent((new BigDecimal(this.inCCurrent)).multiply(Global.Dec2));
		
		//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
		tblRealData.setOutHighestVol((new BigDecimal(this.outHighestVol)).multiply(Global.Dec1));
		
		//48	最低输出电压	精度0.1，单位v
		tblRealData.setOutLowestVol((new BigDecimal(this.outLowestVol)).multiply(Global.Dec1));
		//49	最大输出电流	精度0.1，单位A	
		tblRealData.setOutHighestCurrent((new BigDecimal(this.outHighestCurrent)).multiply(Global.Dec2));
		//50	桩内部环境温度
		tblRealData.setEpInterTemperature((new BigDecimal(this.epInterTemperature)).multiply(Global.Dec1));
				

		DB.chargeDCInfoDao.update(tblRealData);
		
		return true;
	}
	
	@Override
	public boolean loadFromDb(String epCode,int epGunNo)
	{
		TblChargeDCInfo tblRealData =new TblChargeDCInfo();
		
		tblRealData.setEp_code( epCode);
		tblRealData.setEp_gun_no(epGunNo);
		
		List<TblChargeDCInfo> dcChargeInfoList= DB.chargeDCInfoDao.findChargeInfo(tblRealData);
		if(dcChargeInfoList !=null && dcChargeInfoList.size()>0)
		{
			TblChargeDCInfo chargeInfo= dcChargeInfoList.get(0);
			convertFromDB(chargeInfo);
		}
		else
		{
			if(DB.chargeDCInfoDao.insert(tblRealData) ==1)
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
        sb.append("RealDcChargeInfo");
        sb.append(",{id = ").append(pkChargeInfo).append("}\n\r\n");
        sb.append("工作状态  = ").append(workingStatus).append(getWorkingStatusDesc()).append("\n\n");   
        sb.append("有功总电度 = ").append(NumUtil.intToBigDecimal3(totalActivMeterNum)).append("\n");
        sb.append("输出电压 = ").append(NumUtil.intToBigDecimal1(outVoltage)).append("\n\r\n");
        
     //   if(workingStatus ==EpConstant.EP_GUN_W_STATUS_WORK)
        { 
        	sb.append("输出电流 = ").append(NumUtil.intToBigDecimal2(outCurrent)).append("\n");
        	sb.append("SOC = ").append(soc).append("\n");
        	sb.append("估计剩余时间 = ").append(chargeRemainTime).append("\n");  
        	sb.append("电池组最低温度 = ").append(NumUtil.intToBigDecimal1(bpLowestTemperature)).append("\n");
            sb.append("电池组最高温度 = ").append(NumUtil.intToBigDecimal1(bpTemperature)).append("\n");
             sb.append("整车动力电池总电压 = ").append(NumUtil.intToBigDecimal1(carBattryTotalVol)).append("\n");
            sb.append("累计充电时间 = ").append(chargedTime).append("}\n");
            sb.append("最大输出电流 = ").append(NumUtil.intToBigDecimal2(outHighestCurrent)).append("\n"); 
            sb.append("最低输出电压 = ").append(NumUtil.intToBigDecimal1(outLowestVol)).append("\n");
            sb.append("最高输出电压 = ").append(NumUtil.intToBigDecimal1(outHighestVol)).append("\n\n");
            sb.append("已充度数 = ").append(NumUtil.intToBigDecimal3(chargedMeterNum)).append("\n");
            sb.append("单价 = ").append(NumUtil.intToBigDecimal3(chargePrice)).append("\n");
            sb.append("已充金额 = ").append(NumUtil.intToBigDecimal2(chargedCost)).append("\n");
            sb.append("整车动力蓄电池SOC告警状态 = ").append(carSocWarn).append(getCarSocWarnDesc()).append("\n");
            sb.append("蓄电池模块采样点过温告警状态 = ").append(chargeModSampleOverTemp).append(getChargeModSampleOverTempDesc()).append("\n\r\n");     
            
            //车端数据
            if(this.chargeCarInfo!=null)
            {
            	sb.append(chargeCarInfo.toString());
            }
            //电源模块数据
            Iterator iter = mapPowerModule.entrySet().iterator();
    		while (iter.hasNext()) {
    			Map.Entry entry = (Map.Entry) iter.next();
    			ChargePowerModInfo powerModule=(ChargePowerModInfo) entry.getValue();
    			if(powerModule != null)
    			{
    				sb.append(powerModule.toString());
    			}
    		}
        }
        sb.append("A相电压 = ").append(NumUtil.intToBigDecimal1(inAVol)).append("\n");
        sb.append("B相电压 = ").append(NumUtil.intToBigDecimal1(inBVol)).append("\n");
        sb.append("C相电压 = ").append(NumUtil.intToBigDecimal1(inCVol)).append("\n");
        sb.append("A相电流 = ").append(NumUtil.intToBigDecimal2(inACurrent)).append("\n");
        sb.append("B相电流 = ").append(NumUtil.intToBigDecimal2(inBCurrent)).append("\n");
        sb.append("C相电流 = ").append(NumUtil.intToBigDecimal2(inCCurrent)).append("\n\r\n"); 
        sb.append("连接状态 = ").append(linkCarStatus).append(getLinkCarStatusDesc()).append("\n");
        sb.append("收枪状态 = ").append(gunCloseStatus).append(getGunCloseStatusDesc()).append("\n");
        sb.append("枪盖状态 = ").append(gunLidStatus).append(getGunLidStatusDesc()).append("\n");
        sb.append("车位状态 = ").append(carPlaceStatus).append(getCarPlaceStatusDesc()).append("\n");
        sb.append("车位锁状态 = ").append(carPlaceLockStatus).append(getCarPlaceLockStatusDesc()).append("\n"); 
        sb.append("车与桩通讯状态 = ").append(commStatusWithCar).append(getCommStatusWithCarDesc()).append("\n\r\n");       
        sb.append("负何告警 = ").append(acCurrentLoadWarn).append(getAcCurrentLoadWarnDesc()).append("\n");
        sb.append("直流输出过流告警状态 = ").append(chargeOutOverCurrent).append(getChargeOutOverCurrentDesc()).append("\n"); 
        sb.append("充电模式状态 = ").append(carChargeMode).append(getCarChargeModeDesc()).append("\n"); 
        sb.append("输出连接器过温状态 = ").append(chargeOutLinkerOverTemp).append(getChargeOutLinkerOverTempDesc()).append("\n"); 
        sb.append("整车动力蓄电池组输出连接器连接状态 = ").append(chargeOutLinkerStatus).append(getChargeOutLinkerStatusDesc()).append("\n"); 
        sb.append("直流母线输出过压/欠压状态 = ").append(chargeVolWarn).append(getChargeVolWarnDesc()).append("\n");
        sb.append("BMS过压/欠压告警状态 = ").append(bmsVolWarn).append(getBmsVolWarnDesc()).append("\n"); 
        sb.append("充电枪未连接告警状态 = ").append(gunUnconnectWarn).append(getGunUnconnectWarnDesc()).append("\n"); 
        sb.append("输入过压/欠压 = ").append(acInVolWarn).append(getAcInVolWarnDesc()).append("\n");
        sb.append("输出继电器状态 = ").append(outRelayStatus).append(getOutRelayStatusDesc()).append("\n");  
        sb.append("蓄电池模块采样点过温告警 = ").append(chargeModSampleOverTemp).append(getChargeModSampleOverTempDesc()).append("\n"); 
        sb.append("读卡器通讯故障状态 = ").append(cardReaderFault).append(getCardReaderFaultDesc()).append("\n");  
        sb.append("急停按钮动作故障状态 = ").append(urgentStopFault).append(getUrgentStopFaultDesc()).append("\n");    
        sb.append("避雷器故障状态 = ").append(arresterFault).append(getArresterFaultDesc()).append("\n"); 
        sb.append("绝缘监测故障状态 = ").append(insulationCheckFault).append(getInsulationCheckFaultDesc()).append("\n");  
        sb.append("电池反接故障状态 = ").append(battryErrorLink).append(getBattryErrorLinkDesc()).append("\n");
        sb.append("充电机过温故障状态 = ").append(chargeOverTemp).append(getChargeOverTempDesc()).append("\n");
        sb.append("烟雾报警告警状态 = ").append(fogsWarn).append(getFogsWarnDesc()).append("\n"); 
        sb.append("交易记录已满告警状态 = ").append(transRecordFullWarn).append(getTransRecordFullWarnDesc()).append("\n");    
        sb.append("电度表异常状态 = ").append(meterError).append(getMeterErrorDesc()).append("\n"); 
        sb.append("BMS通信异常状态 = ").append(bmsCommException).append(getBmsCommExceptionDesc()).append("\n");
        sb.append("直流电度表异常故障状态 = ").append(dcMeterException).append(getDcMeterExceptionDesc()).append("\n");
        sb.append("电池反接故障状态 = ").append(battryErrorLink).append(getBattryErrorLinkDesc()).append("\n"); 
        
        sb.append("chargeUserId = ").append(this.chargeUserId).append("\n");
        sb.append("serialno = ").append(this.chargeSerialNo).append("\n");
		return sb.toString();
	}
	@Override
	public void endCharge()
	{
		//outVoltage=0;
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
		
		//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
		signleBattryHighVol=0;
		//34	蓄电池最高温度
		bpTemperature=0;
		//35	蓄电池最低温度
		bpLowestTemperature=0;
		
		//36	整车动力电池总电压	精度0.1，单位v	
		carBattryTotalVol=0;

	
		//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
		outHighestVol=0;
		//48	最低输出电压	精度0.1，单位v	
		outLowestVol=0;
	
		outHighestCurrent=0;
		
	     if(chargeCarInfo != null)
		       chargeCarInfo.endCharge();
		
		powerModuleEndCharge();
	}
	
	
	
	public int setPowerModuleValue(int addr,SingleInfo singleInfo )
	{
		logger.debug("setPowerModuleValue,addr:{}, singleInfo:{}",addr, JSON.toJSON(singleInfo));
		int ret=0;
		if(addr < YXCConstants.YC_CHARGER_MOD_START_POS  || addr>= 2500)
		{
			return -1;
		}
		
		int moduleNo=(addr-YXCConstants.YC_CHARGER_MOD_START_POS)/9+1;
		
		ChargePowerModInfo powserModInfo = this.mapPowerModule.get(moduleNo);
		if(powserModInfo==null)
		{
			powserModInfo =  new  ChargePowerModInfo();
			powserModInfo.setModId(moduleNo);
			
		}
		
		int calcAddr = ((addr-YXCConstants.YC_CHARGER_MOD_START_POS)%9)+1;
		ret= powserModInfo.setYcValue(calcAddr,singleInfo.getIntValue());
		if(ret==1)
		{
			powserModInfo.setChange(1);	
		}
		
		mapPowerModule.put(moduleNo, powserModInfo);
		
		return ret;
	}
	
	public int getCarInfoIntValue(int addr)
	{
		
		if(!EpGunService.checkCardInfoAddr(addr))
		{
			return 0;
		}
		
		if(this.chargeCarInfo==null)
			return 0;
		
		return chargeCarInfo.getYcIntValue(addr);
		
		
	}

	public String getCarInfoStringValue(int addr) {
		if (!EpGunService.checkCardInfoAddr(addr)) {
			return "";
		}
		if (this.chargeCarInfo == null)
			return "";

		return chargeCarInfo.getYcStrValue(addr);
	}

	public int setCarInfoValue(int addr,SingleInfo singleInfo )
	{
		logger.debug("setPowerModuleValue,addr:{}, singleInfo:{}",addr, JSON.toJSON(singleInfo));
		
		int ret=0;
		if(!EpGunService.checkCardInfoAddr(addr))
		{
			return -2;
		}
		
		if(chargeCarInfo==null)
		{
			chargeCarInfo =  new  ChargeCarInfo();
		}
		
		
		ret= chargeCarInfo.setYcValue(addr,singleInfo);
		if(ret==1)
		{
			chargeCarInfo.setChange(1);	
		}
		
		
		return ret;
	}
	
	public void saveChargeCarInfoToDB(String chargeSerialNo)
	{
		EpChargeService.saveVehicleBatteryToDb(chargeCarInfo,chargeSerialNo);
	}
	
	public void savePowerModuleToDB(String chargeSerialNo)
	{
		EpChargeService.savePowerModule(mapPowerModule,chargeSerialNo);
	}
	
	/**
	 * 
	 * @return-3:在最大遥测，遥信最大值范围外，-2:保留字段,-1：参数非法，0：没有变化;1:变化
	 */
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
		    
		    
			case YXCConstants.YX_1_BATTRY_ERROR_LINK://电池反接故障
			{
				return setBattryErrorLink((short)info.getIntValue());
			}
		
			case YXCConstants.YX_1_FOGS_WARN://烟雾报警故障
			{
				return setFogsWarn((short)info.getIntValue());
				
			}
			case YXCConstants.YX_1_BMS_ERROR://BMS 通信异常
			{
				return setBmsCommException((short)info.getIntValue());
				
			}
			case YXCConstants.YX_1_DCMETER_ERROR://直流电度表异常
			{
				return setDcMeterException((short)info.getIntValue());
				
			}
			case YXCConstants.YX_1_DC_OUT_OVER_CURRENT_WARN://直流电度表异常
			{
				return setChargeOutOverCurrent((short)info.getIntValue());
				
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
			
		    
			case YXCConstants.YX_2_DC_SUPPLY_CHARGE_STYLE://直流电源模块充电模式
			{
				return setCarChargeMode((short)info.getIntValue());
			}
			
			case YXCConstants.YX_2_BATTRY_SOC_WARN://整车动力蓄电池SOC告警
			{
				return setCarSocWarn((short)info.getIntValue());
			}
			
			case YXCConstants.YX_2_BATTRY_SAMPLE_OVER_TEMP://蓄电池模块采样点过温告警
			{
				return setCarSocWarn((short)info.getIntValue());
			}
			
			case YXCConstants.YX_2_OUT_LINKER_OVER_TEMP://输出连接器过温
			{
				return setCarSocWarn((short)info.getIntValue());
			}
			
		    
			case YXCConstants.YX_2_OUT_LINKER_STATUS://输出连接器过温
			{
				return setChargeOutLinkerOverTemp((short)info.getIntValue());
			}
			
			case YXCConstants.YX_2_BATTRY_CHARGE_OVER_CURRENT://整车蓄电池充电过流告警
			{
				return setChargeWholeOverCurrentWarn((short)info.getIntValue());
			}
			
		    
			case YXCConstants.YX_2_DC_OUT_VOL_WARN://直流母线输出过压/欠压
			{
				return setChargeVolWarn((short)info.getIntValue());
				
			}
			case YXCConstants.YX_2_BMS_VOL_WARN://直流母线输出过压/欠压
			{
				return setBmsVolWarn((short)info.getIntValue());
				
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
		    case YXCConstants.YC_ERROR_CODE://错误代码
			{
				return setErrorCode(info.getIntValue());
			}
			
			
		    case YXCConstants.YC_REMAIN_TIME://估计剩余时间
			{
				return setChargeRemainTime(info.getIntValue());
			}
			
		    
		    case YXCConstants.YC_BATTARY_TYPE://电池类型
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			
		    
		    case YXCConstants.YC_BATTARY_RATED_CAPACITY://整车动力蓄电池系统额定容量
			{
				return this.setCarInfoValue(pointAddr, info);
			}
			
		    case YXCConstants.YC_BATTARY_MAKE_YEAR://电池组生产日期年
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			
		    case YXCConstants.YC_BATTARY_MAKE_DATE://电池组生产日期年
			{
				return this.setCarInfoValue(pointAddr, info);
			}
			
		    case YXCConstants.YC_BATTARY_CHARGE_TIME://电池组生产日期年
			{
				return this.setCarInfoValue(pointAddr, info);
			}
			
		    case YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL://单体蓄电池最高允许充电电压
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			
		    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT://最高允许充电电流
			{
				
				return this.setCarInfoValue(pointAddr, info);
			}
			
		    case YXCConstants.YC_BATTRY_TOTAL_POWER://动力蓄电池标称总能量
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			
		    case YXCConstants.YC_BATTRY_HIGH_VOL://最高允许充电总电压(额定总电压)
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			
		    case YXCConstants.YC_BATTRY_CAN_HIGH_TEMP://最高允许温度
			{
				return this.setCarInfoValue(pointAddr, info);
				
			}
			  
		    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP://单体蓄电池最高电压和组号
			{
				return setSignleBattryHighVol((short)info.getIntValue());
				
			}
			
		    case YXCConstants.YC_BATTRY_HIGHEST_TEMP://电池组最高温度
			{
				return setBpHighestTemperature((short)info.getIntValue());
			}
			
		    case YXCConstants.YC_BATTRY_LOWEST_TEMP://电池组最高温度
			{
				return setBpLowestTemperature((short)info.getIntValue());
				
			}
			
		    
		    case YXCConstants.YC_CAR_BATTRY_TOTAL_VOL://整车动力电池总电压
			{
				return setCarBattryTotalVol((short)info.getIntValue());
				
			}
		
		    
		    case YXCConstants.YC_A_VOL://A相电压
			{
				return setInAVol(info.getIntValue());
			}
			
		    case YXCConstants.YC_B_VOL://B相电压
			{
				return setInBVol(info.getIntValue());
			}
			
		    case YXCConstants.YC_C_VOL://C相电压
			{
				return setInCVol(info.getIntValue());
			}
			
		    
		    case YXCConstants.YC_A_CURRENT://A相电流
			{
				return setInACurrent(info.getIntValue());
			}
			
		    case YXCConstants.YC_B_CURRENT://B相电流
			{
				return setInBCurrent(info.getIntValue());
			}
			
		    case YXCConstants.YC_C_CURRENT://C相电流
			{
				return setInCCurrent(info.getIntValue());
			}
			
		    
		    case YXCConstants.YC_OUT_HIGH_VOL://最高输出电压
			{
				return setOutHighestVol(info.getIntValue());
			}
			
		    case YXCConstants.YC_OUT_LOW_VOL://最低输出电压
			{
				return setOutLowestVol(info.getIntValue());
			}
			
		    case YXCConstants.YC_OUT_HIGH_CURRENT://最低输出电压
			{
				return setOutHighestCurrent(info.getIntValue());
			}
		    case YXCConstants.YC_EP_TEMP://电桩内部温度
			{
				return setEpInterTemperature(info.getIntValue());
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
		    case YXCConstants.YC_VAR_CAR_VIN://车辆识别码
			{
				return this.setCarInfoValue(pointAddr, info);		
			}
		    case YXCConstants.YC_VAR_BATTARY_FACTORY://厂商
			{
				return this.setCarInfoValue(pointAddr, info);
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
		    	else if((pointAddr>=YXCConstants.YC_CHARGER_MOD_START_POS) && (pointAddr<YXCConstants.YC_VAR_START_POS))
		    	{
		    		return setPowerModuleValue(pointAddr,info);
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
	    
	    
		case YXCConstants.YX_1_BATTRY_ERROR_LINK://电池反接故障
		{
			point = pointAddr;
			value = getBattryErrorLink();
			break;
		}
	
		case YXCConstants.YX_1_FOGS_WARN://烟雾报警故障
		{
			point = pointAddr;
			value = getFogsWarn();
			break;
			
		}
		case YXCConstants.YX_1_BMS_ERROR://BMS 通信异常
		{
			point = pointAddr;
			value = getBmsCommException();
			break;
			
		}
		case YXCConstants.YX_1_DCMETER_ERROR://直流电度表异常
		{
			point = pointAddr;
			value = getDcMeterException();
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
		
	    
		case YXCConstants.YX_2_DC_SUPPLY_CHARGE_STYLE://直流电源模块充电模式
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getCarChargeMode();
			break;
		}
		
		case YXCConstants.YX_2_BATTRY_SOC_WARN://整车动力蓄电池SOC告警
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getCarSocWarn();
			break;
		}
		
		case YXCConstants.YX_2_BATTRY_SAMPLE_OVER_TEMP://蓄电池模块采样点过温告警
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getCarSocWarn();
			break;
		}
		
		case YXCConstants.YX_2_OUT_LINKER_OVER_TEMP://输出连接器过温
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getCarSocWarn();
			break;
		}
		
	    
		case YXCConstants.YX_2_OUT_LINKER_STATUS://输出连接器过温
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getChargeOutLinkerOverTemp();
			break;
		}
		
		case YXCConstants.YX_2_BATTRY_CHARGE_OVER_CURRENT://整车蓄电池充电过流告警
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getChargeWholeOverCurrentWarn();
			break;
		}
		
	    
		case YXCConstants.YX_2_DC_OUT_VOL_WARN://直流母线输出过压/欠压
		{
			point = pointAddr - YXCConstants.YX_2_START_POS ;
			value = getChargeVolWarn();
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
		
	    
	    case YXCConstants.YC_BATTARY_TYPE://电池类型
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
		}
		
	    
	    case YXCConstants.YC_BATTARY_RATED_CAPACITY://整车动力蓄电池系统额定容量
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_YEAR://电池组生产日期年
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		
	    case YXCConstants.YC_BATTARY_MAKE_DATE://电池组生产日期年
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
		}
		
	    case YXCConstants.YC_BATTARY_CHARGE_TIME://电池组生产日期年
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL://单体蓄电池最高允许充电电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		
	    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT://最高允许充电电流
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		
	    case YXCConstants.YC_BATTRY_TOTAL_POWER://动力蓄电池标称总能量
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		
	    case YXCConstants.YC_BATTRY_HIGH_VOL://最高允许充电总电压(额定总电压)
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		
	    case YXCConstants.YC_BATTRY_CAN_HIGH_TEMP://最高允许温度
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarInfoIntValue(pointAddr);
			break;
			
		}
		  
	    case YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP://单体蓄电池最高电压和组号
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getSignleBattryHighVol();
			break;
			
		}
		
	    case YXCConstants.YC_BATTRY_HIGHEST_TEMP://电池组最高温度
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getBpHighestTemperature();
			break;
		}
		
	    case YXCConstants.YC_BATTRY_LOWEST_TEMP://电池组最高温度
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getBpLowestTemperature();
			break;
		}
		
	    
	    case YXCConstants.YC_CAR_BATTRY_TOTAL_VOL://整车动力电池总电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getCarBattryTotalVol();
			break;
		}
	
	    
	    case YXCConstants.YC_A_VOL://A相电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInAVol();
			break;
		}
		
	    case YXCConstants.YC_B_VOL://B相电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInBVol();
			break;
		}
		
	    case YXCConstants.YC_C_VOL://C相电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInCVol();
			break;
		}
		
	    
	    case YXCConstants.YC_A_CURRENT://A相电流
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInACurrent();
			break;
		}
		
	    case YXCConstants.YC_B_CURRENT://B相电流
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInBCurrent();
			break;
		}
		
	    case YXCConstants.YC_C_CURRENT://C相电流
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getInCCurrent();
			break;
		}
		
	    
	    case YXCConstants.YC_OUT_HIGH_VOL://最高输出电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getOutHighestVol();
			break;
		}
		
	    case YXCConstants.YC_OUT_LOW_VOL://最低输出电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getOutLowestVol();
			break;
		}
		
	    case YXCConstants.YC_OUT_HIGH_CURRENT://最低输出电压
		{
			point = pointAddr - YXCConstants.YC_START_POS ;
			value = getOutHighestCurrent();
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
	    case YXCConstants.YC_VAR_BATTARY_FACTORY:
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			info.setStrValue(getCarInfoStringValue(pointAddr));
			
			break;
		}
	    case YXCConstants.YC_VAR_CAR_VIN:
		{
			point = pointAddr - YXCConstants.YC_VAR_START_POS ;
			info.setStrValue(getCarInfoStringValue(pointAddr));
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
				 YXCConstants.YX_1_METER_ERROR,
				 
				 YXCConstants.YX_1_BATTRY_ERROR_LINK,
				 YXCConstants.YX_1_FOGS_WARN,
				 YXCConstants.YX_1_BMS_ERROR,
				 YXCConstants.YX_1_DCMETER_ERROR,
				 YXCConstants.YX_1_DC_OUT_OVER_CURRENT_WARN
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
				 YXCConstants.YX_2_OUT_RELAY_STATUS,
				 YXCConstants.YX_2_DC_SUPPLY_CHARGE_STYLE,
				 YXCConstants.YX_2_BATTRY_SOC_WARN,
				 YXCConstants.YX_2_BATTRY_SAMPLE_OVER_TEMP,
				 YXCConstants.YX_2_OUT_LINKER_OVER_TEMP,
				    
				 YXCConstants.YX_2_OUT_LINKER_STATUS,
				 YXCConstants.YX_2_BATTRY_CHARGE_OVER_CURRENT,
				    
				 YXCConstants.YX_2_DC_OUT_VOL_WARN,
				 YXCConstants.YX_2_BMS_VOL_WARN
			};
		for(int i=0;i<twoYxArray.length;i++)
		{
			 SingleInfo loop= getFieldValue(twoYxArray[i]);
			 if(loop!=null)
			 {
				 twoYxRealInfo.put(loop.getAddress(), loop);
			 }
		}
		
		return twoYxRealInfo;
		
	}
	public Map<Integer, SingleInfo> getWholeYc(int preWorkStatus)
	{
		//遥测
		Map<Integer, SingleInfo> ycRealInfo = new ConcurrentHashMap<Integer, SingleInfo>();
		int ycArray[] = new int[]{YXCConstants.YC_WORKSTATUS,
			YXCConstants.YC_CAR_PLACE_LOCK,
			YXCConstants.YC_OUT_VOL,
			YXCConstants.YC_OUT_CURRENT,//充电机输出电流
			YXCConstants.YC_SOC,//
			YXCConstants.YC_TOTAL_TIME,//累计充电时间
			YXCConstants.YC_REMAIN_TIME,//估计剩余时间
					 
			YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP,
			YXCConstants.YC_BATTRY_HIGHEST_TEMP,
			 YXCConstants.YC_BATTRY_LOWEST_TEMP,
			    
			 YXCConstants.YC_CAR_BATTRY_TOTAL_VOL,
			    
			 YXCConstants.YC_A_VOL,
			 YXCConstants.YC_B_VOL,
			 YXCConstants.YC_C_VOL,
			    
			 YXCConstants.YC_A_CURRENT,
			 YXCConstants.YC_B_CURRENT,
			 YXCConstants.YC_C_CURRENT,
			 YXCConstants.YC_OUT_HIGH_VOL,
			 YXCConstants.YC_OUT_LOW_VOL,
			 YXCConstants.YC_OUT_HIGH_CURRENT,
			 
			 //车端数据
			 YXCConstants.YC_BATTARY_TYPE,
 			 YXCConstants.YC_BATTARY_RATED_CAPACITY,
			 YXCConstants.YC_BATTARY_MAKE_YEAR,
			 YXCConstants.YC_BATTARY_MAKE_DATE,
			 YXCConstants.YC_BATTARY_CHARGE_TIME,
			 YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL,
			 YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT,
			 YXCConstants.YC_BATTRY_TOTAL_POWER,
			 YXCConstants.YC_BATTRY_HIGH_VOL,
			 YXCConstants.YC_BATTRY_CAN_HIGH_TEMP,
			    
		    YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP,
		    YXCConstants.YC_BATTRY_HIGHEST_TEMP,
		    YXCConstants.YC_BATTRY_LOWEST_TEMP,
		    
		    YXCConstants.YC_CAR_BATTRY_TOTAL_VOL
					 };
		
		for(int i=0;i<ycArray.length;i++)
		{
			 SingleInfo loop= getFieldValue(ycArray[i]);
			 if(loop!=null)
			 {
				 ycRealInfo.put(loop.getAddress(), loop);
			 }
		}
		
		//前一次是充电，把车端数据和电源模块数据清空后上报
		if(preWorkStatus ==3)
		{
			int ycCarArray[] = new int[]{
					 
			 //车端数据
			 YXCConstants.YC_BATTARY_TYPE,
 			 YXCConstants.YC_BATTARY_RATED_CAPACITY,
			 YXCConstants.YC_BATTARY_MAKE_YEAR,
			 YXCConstants.YC_BATTARY_MAKE_DATE,
			 YXCConstants.YC_BATTARY_CHARGE_TIME,
			 YXCConstants.YC_SIGNLE_BATTRY_CAN_HIGH_VOL,
			 YXCConstants.YC_SIGNLE_BATTRY_HIGH_CURRENT,
			 YXCConstants.YC_BATTRY_TOTAL_POWER,
			 YXCConstants.YC_BATTRY_HIGH_VOL,
			 YXCConstants.YC_BATTRY_CAN_HIGH_TEMP,
			    
		    YXCConstants.YC_SIGNLE_BATTRY_HIGH_VOL_GROUP,
		    YXCConstants.YC_BATTRY_HIGHEST_TEMP,
		    YXCConstants.YC_BATTRY_LOWEST_TEMP,
		    
		    YXCConstants.YC_CAR_BATTRY_TOTAL_VOL
							 };
			for(int i=0;i<ycCarArray.length;i++)
			{
				 SingleInfo loop= getFieldValue(ycCarArray[i]);
				 if(loop!=null)
				 {
					 ycRealInfo.put(loop.getAddress(), loop);
				 }
			}
			//电源模块
			
			Map<Integer, SingleInfo> ycRealInfo2 =getWholePowerModYc();
			
			Iterator iter = ycRealInfo2.entrySet().iterator();
			
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				
				
				SingleInfo info=(SingleInfo) entry.getValue();
				ycRealInfo.put(info.getAddress(), info);
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
				 YXCConstants.YC_VAR_CHARGED_METER_NUM,
				 
				 YXCConstants.YC_VAR_CAR_VIN,
				 YXCConstants.YC_VAR_BATTARY_FACTORY
				 
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
