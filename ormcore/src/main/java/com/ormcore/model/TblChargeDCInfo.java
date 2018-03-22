package com.ormcore.model;

import java.math.BigDecimal;
import java.util.Date;

public class TblChargeDCInfo {
	//自己建设的信息
	private Integer pk_chargeinfo;
	private Integer station_id;
	private String ep_code;//充电机编号
	private Integer ep_gun_no;
	private Date createdate;
	private Date updatedate;
	
	//交直流公共变量
	protected Short linkCarStatus;//是否连接电池(车辆)
	protected Short gunCloseStatus;//枪座状态
	protected Short gunLidStatus;//充电枪盖状态
	protected Short commStatusWithCar;//车与桩建立通信信号
	protected Short carPlaceStatus;//车位占用状态(雷达探测)
	protected Short cardReaderFault;//读卡器通讯故障
	protected Short urgentStopFault;//急停按钮动作故障
	protected Short arresterFault;//避雷器故障
	protected Short insulationCheckFault;//绝缘检测故障
	
	protected Short gunUnconnectWarn;//充电枪未连接告警
	protected Short transRecordFullWarn;//交易记录已满告警
	protected Short meterError;//电度表异常
	
	protected Short acInVolWarn;//交流输入电压过压/欠压
	protected Short chargeOverTemp;//充电机过温故障
	protected Short acCurrentLoadWarn;//交流电流过负荷告警
	protected Short outRelayStatus;//输出继电器状态
	
	//公共遥测
	protected Integer workingStatus;//充电机状态
	protected BigDecimal outVoltage;//充电机输出电压
	protected BigDecimal outCurrent;//充电机输出电流
	protected Integer carPlaceLockStatus;//地锁状态
	protected Integer soc;//SOC
	protected Integer chargedTime;//累计充电时间
	protected Integer chargeRemainTime;//估计剩余时间
	
	//公共的变长遥测
	protected BigDecimal totalActivMeterNum;//有功总电度
	
	//充电相关
	protected Integer chargeUserId =0 ;
	protected String chargeSerialNo ="";
	protected Integer chargeStartTime= 0;
	protected BigDecimal chargeStartMeterNum;
	//
	private BigDecimal chargePrice;//当前价格
    private BigDecimal chargedCost;//已充金额
    private BigDecimal chargedMeterNum;//已充度数
    private BigDecimal fronzeAmt;//冻结金额
   
	    
	
	//直流特殊属性
  	//单位遥信	17	电池反接故障	0:正常;1:故障		爱充直流特别遥信
  	private Short battryErrorLink;
  	//单位遥信	18	烟雾报警故障	0:正常;1:告警	
  	private Short fogsWarn;
  	//单位遥信	19	BMS 通信异常	0:正常;1:告警	
  	private Short bmsCommException;
  	//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
  	private Short dcMeterException;
  //单位遥信	21	直流电度表异常故障	0:正常;1:告警	
  	private Short chargeOutOverCurrent;

  	//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
  	private Short carChargeMode;
  	//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
  	private Short carSocWarn;
  	//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
  	private Short chargeModSampleOverTemp; 
  	//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
  	private Short chargeOutLinkerOverTemp;
  	//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
  	private Short chargeOutLinkerStatus;
  	//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
  	private Short chargeWholeOverCurrentWarn;
  	//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
  	private Short chargeVolWarn;
   //双位遥信	16	BMS过压/欠压告警	0:正常;1:过压;2:欠压
  	private Short bmsVolWarn;
  	
  	
  	//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
  	private BigDecimal signleBattryHighVol;
  	//34	蓄电池最高温度
  	private BigDecimal bpHighestTemperature;
  	//35	蓄电池最低温度
  	private BigDecimal bpLowestTemperature;
  	
  	//36	整车动力电池总电压	精度0.1，单位v	
  	private BigDecimal carBattryTotalVol;

  	//直流专用遥测
  	//41	A相电压	精度0.1，单位v	输入
  	private BigDecimal inAVol;
  	//42	B相电压	精度0.1，单位v
  	private BigDecimal inBVol;
  	//43	C相电压	精度0.1，单位v
  	private BigDecimal inCVol;
  	//44	A相电流	精度0.1，单位v
  	private BigDecimal inACurrent;
  	//45	B相电流	精度0.1，单位v	
  	private BigDecimal inBCurrent;
  	//46	C相电流	精度0.1，单位v	
  	private BigDecimal inCCurrent;
  	//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
  	private BigDecimal outHighestVol;
  	//48	最低输出电压	精度0.1，单位v	
  	private BigDecimal outLowestVol;
  	//49	最大输出电流	精度0.1，单位A	
  	private BigDecimal outHighestCurrent;
  	//50          桩内部环境温度
  	private BigDecimal epInterTemperature;

    private void Init()
    {
    	pk_chargeinfo=0;
    	station_id =0 ;
    	ep_code="";//充电机编号
    	ep_gun_no=0;
    	
    	
    	//交直流公共变量
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
    	workingStatus=0;//充电机状态
    	outVoltage= new BigDecimal(0.0);//充电机输出电压
    	outCurrent= new BigDecimal(0.0);//充电机输出电流
    	carPlaceLockStatus=0;//地锁状态
    	soc=0;//SOC
    	chargedTime=0;//累计充电时间
    	chargeRemainTime=0;//估计剩余时间
    	
    	//公共的变长遥测
    	totalActivMeterNum= new BigDecimal(0.0);//有功总电度
    	
    	//充电相关
    	chargeUserId =0 ;
    	chargeSerialNo ="";
    	chargeStartTime= 0;
    	chargeStartMeterNum= new BigDecimal(0.0);
    	//
    	chargePrice= new BigDecimal(0.0);//当前价格
        chargedCost= new BigDecimal(0.0);//已充金额
        chargedMeterNum= new BigDecimal(0.0);//已充度数
        fronzeAmt= new BigDecimal(0.0);//冻结金额
        
        //===========================================
        battryErrorLink=0;
      	//单位遥信	18	烟雾报警故障	0:正常;1:告警	
      	fogsWarn=0;
      	//单位遥信	19	BMS 通信异常	0:正常;1:告警	
      	bmsCommException=0;
      	//单位遥信	20	直流电度表异常故障	0:正常;1:告警	
      	dcMeterException=0;
      	chargeOutOverCurrent=0;

      	//双位遥信	9	充电模式	0:不可信;1:恒压;2:恒流		爱充直流特别遥信
      	carChargeMode=0;
      	//双位遥信	10	整车动力蓄电池SOC告警	0:正常;1:过高;2:过低
      	carSocWarn=0;
      	//双位遥信	11	蓄电池模块采样点过温告警	0:正常;1:过温;2:不可信
      	chargeModSampleOverTemp=0; 
      	//双位遥信	12	输出连接器过温	0:正常;1:过温;2:不可信
      	chargeOutLinkerOverTemp=0;
      	//双位遥信	13	整车动力蓄电池组输出连接器连接状态	0:正常;1:不正常;2:不可信	
      	chargeOutLinkerStatus=0;
      	//双位遥信	14	整车蓄电池充电过流告警	0:正常;1:过流;2:不可信	
      	chargeWholeOverCurrentWarn=0;
      	//双位遥信	15	直流母线输出过压/欠压	0:正常;1:过压;2:欠压
      	chargeVolWarn=0;
      	bmsVolWarn=0;
      	
      	
      	//33	单体蓄电池最高电压和组号		采集蓄电池，充电握手阶段和充电中都有
      	signleBattryHighVol= new BigDecimal(0.0);
      	//34	蓄电池最高温度
      	bpHighestTemperature= new BigDecimal(0.0);
      	//35	蓄电池最低温度
      	bpLowestTemperature= new BigDecimal(0.0);
      	
      	//36	整车动力电池总电压	精度0.1，单位v	
      	carBattryTotalVol= new BigDecimal(0.0);

      	//直流专用遥测
      	//41	A相电压	精度0.1，单位v	输入
      	inAVol= new BigDecimal(0.0);
      	//42	B相电压	精度0.1，单位v
      	inBVol= new BigDecimal(0.0);
      	//43	C相电压	精度0.1，单位v
      	inCVol= new BigDecimal(0.0);
      	//44	A相电流	精度0.1，单位v
      	inACurrent= new BigDecimal(0.0);
      	//45	B相电流	精度0.1，单位v	
      	inBCurrent= new BigDecimal(0.0);
      	//46	C相电流	精度0.1，单位v	
      	inCCurrent= new BigDecimal(0.0);
      	//47	最高输出电压	精度0.1，单位v	充电中，充电机输出
      	outHighestVol= new BigDecimal(0.0);
      	//48	最低输出电压	精度0.1，单位v	
      	outLowestVol= new BigDecimal(0.0);
      	//49	最大输出电流	精度0.1，单位A	
      	outHighestCurrent= new BigDecimal(0.0);
      	//50        桩内部环境温度   精度0.1，单位度
      	epInterTemperature= new BigDecimal(0.0);
    }
	public TblChargeDCInfo()
	{
		Init();
	}
	public TblChargeDCInfo(String epCode,int epGunNo,
			BigDecimal chargePrice,BigDecimal chargedCost,int chargedTime,BigDecimal chargedMeterNum,int userId)
	{
		Init();
		this.ep_code = epCode;
		this.ep_gun_no = epGunNo;
		
		this.chargePrice= chargePrice;
		
		this.chargedCost= chargedCost;
		this.chargedMeterNum= chargedMeterNum;//已充度数
		this.chargedTime= chargedTime;//已充时间(分钟)
		
	}
	
	
	public BigDecimal getEpInterTemperature() {
		return epInterTemperature;
	}
	public void setEpInterTemperature(BigDecimal epInterTemperature) {
		this.epInterTemperature = epInterTemperature;
	}
	public Integer getPk_chargeinfo() {
		return pk_chargeinfo;
	}
	public void setPk_chargeinfo(Integer pk_chargeinfo) {
		this.pk_chargeinfo = pk_chargeinfo;
	}
	public Integer getStation_id() {
		return station_id;
	}
	public void setStation_id(Integer station_id) {
		this.station_id = station_id;
	}
	public String getEp_code() {
		return ep_code;
	}
	public void setEp_code(String ep_code) {
		this.ep_code = ep_code;
	}
	public Integer getEp_gun_no() {
		return ep_gun_no;
	}
	public void setEp_gun_no(Integer ep_gun_no) {
		this.ep_gun_no = ep_gun_no;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public Date getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}
	public Short getLinkCarStatus() {
		return linkCarStatus;
	}
	public void setLinkCarStatus(Short linkCarStatus) {
		this.linkCarStatus = linkCarStatus;
	}
	public Short getGunCloseStatus() {
		return gunCloseStatus;
	}
	public void setGunCloseStatus(Short gunCloseStatus) {
		this.gunCloseStatus = gunCloseStatus;
	}
	public Short getGunLidStatus() {
		return gunLidStatus;
	}
	public void setGunLidStatus(Short gunLidStatus) {
		this.gunLidStatus = gunLidStatus;
	}
	public Short getCommStatusWithCar() {
		return commStatusWithCar;
	}
	public void setCommStatusWithCar(Short commStatusWithCar) {
		this.commStatusWithCar = commStatusWithCar;
	}
	public Short getCarPlaceStatus() {
		return carPlaceStatus;
	}
	public void setCarPlaceStatus(Short carPlaceStatus) {
		this.carPlaceStatus = carPlaceStatus;
	}
	public Short getCardReaderFault() {
		return cardReaderFault;
	}
	public void setCardReaderFault(Short cardReaderFault) {
		this.cardReaderFault = cardReaderFault;
	}
	public Short getUrgentStopFault() {
		return urgentStopFault;
	}
	public void setUrgentStopFault(Short urgentStopFault) {
		this.urgentStopFault = urgentStopFault;
	}
	public Short getArresterFault() {
		return arresterFault;
	}
	public void setArresterFault(Short arresterFault) {
		this.arresterFault = arresterFault;
	}
	public Short getInsulationCheckFault() {
		return insulationCheckFault;
	}
	public void setInsulationCheckFault(Short insulationCheckFault) {
		this.insulationCheckFault = insulationCheckFault;
	}
	public Short getGunUnconnectWarn() {
		return gunUnconnectWarn;
	}
	public void setGunUnconnectWarn(Short gunUnconnectWarn) {
		this.gunUnconnectWarn = gunUnconnectWarn;
	}
	public Short getTransRecordFullWarn() {
		return transRecordFullWarn;
	}
	public void setTransRecordFullWarn(Short transRecordFullWarn) {
		this.transRecordFullWarn = transRecordFullWarn;
	}
	public Short getMeterError() {
		return meterError;
	}
	public void setMeterError(Short meterError) {
		this.meterError = meterError;
	}
	public Short getAcInVolWarn() {
		return acInVolWarn;
	}
	public void setAcInVolWarn(Short acInVolWarn) {
		this.acInVolWarn = acInVolWarn;
	}
	public Short getChargeOverTemp() {
		return chargeOverTemp;
	}
	public void setChargeOverTemp(Short chargeOverTemp) {
		this.chargeOverTemp = chargeOverTemp;
	}
	public Short getAcCurrentLoadWarn() {
		return acCurrentLoadWarn;
	}
	public void setAcCurrentLoadWarn(Short acCurrentLoadWarn) {
		this.acCurrentLoadWarn = acCurrentLoadWarn;
	}
	public Short getOutRelayStatus() {
		return outRelayStatus;
	}
	public void setOutRelayStatus(Short outRelayStatus) {
		this.outRelayStatus = outRelayStatus;
	}
	public Integer getWorkingStatus() {
		return workingStatus;
	}
	public void setWorkingStatus(Integer workingStatus) {
		this.workingStatus = workingStatus;
	}
	public BigDecimal getOutVoltage() {
		return outVoltage;
	}
	public void setOutVoltage(BigDecimal outVoltage) {
		this.outVoltage = outVoltage;
	}
	public BigDecimal getOutCurrent() {
		return outCurrent;
	}
	public void setOutCurrent(BigDecimal outCurrent) {
		this.outCurrent = outCurrent;
	}
	public Integer getCarPlaceLockStatus() {
		return carPlaceLockStatus;
	}
	public void setCarPlaceLockStatus(Integer carPlaceLockStatus) {
		this.carPlaceLockStatus = carPlaceLockStatus;
	}
	public Integer getSoc() {
		return soc;
	}
	public void setSoc(Integer soc) {
		this.soc = soc;
	}
	public Integer getChargedTime() {
		return chargedTime;
	}
	public void setChargedTime(Integer chargedTime) {
		this.chargedTime = chargedTime;
	}
	public Integer getChargeRemainTime() {
		return chargeRemainTime;
	}
	public void setChargeRemainTime(Integer chargeRemainTime) {
		this.chargeRemainTime = chargeRemainTime;
	}
	public BigDecimal getTotalActivMeterNum() {
		return totalActivMeterNum;
	}
	public void setTotalActivMeterNum(BigDecimal totalActivMeterNum) {
		this.totalActivMeterNum = totalActivMeterNum;
	}
	public int getChargeUserId() {
		return chargeUserId;
	}
	public void setChargeUserId(int chargeUserId) {
		this.chargeUserId = chargeUserId;
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
	public BigDecimal getChargeStartMeterNum() {
		return chargeStartMeterNum;
	}
	public void setChargeStartMeterNum(BigDecimal chargeStartMeterNum) {
		this.chargeStartMeterNum = chargeStartMeterNum;
	}
	public BigDecimal getChargePrice() {
		return chargePrice;
	}
	public void setChargePrice(BigDecimal chargePrice) {
		this.chargePrice = chargePrice;
	}
	public BigDecimal getChargedCost() {
		return chargedCost;
	}
	public void setChargedCost(BigDecimal chargedCost) {
		this.chargedCost = chargedCost;
	}
	public BigDecimal getChargedMeterNum() {
		return chargedMeterNum;
	}
	public void setChargedMeterNum(BigDecimal chargedMeterNum) {
		this.chargedMeterNum = chargedMeterNum;
	}
	public BigDecimal getFronzeAmt() {
		return fronzeAmt;
	}
	public void setFronzeAmt(BigDecimal fronzeAmt) {
		this.fronzeAmt = fronzeAmt;
	}
	public Short getBattryErrorLink() {
		return battryErrorLink;
	}
	public void setBattryErrorLink(Short battryErrorLink) {
		this.battryErrorLink = battryErrorLink;
	}
	public Short getFogsWarn() {
		return fogsWarn;
	}
	public void setFogsWarn(Short fogsWarn) {
		this.fogsWarn = fogsWarn;
	}
	public Short getBmsCommException() {
		return bmsCommException;
	}
	public void setBmsCommException(Short bmsCommException) {
		this.bmsCommException = bmsCommException;
	}
	public Short getDcMeterException() {
		return dcMeterException;
	}
	public void setDcMeterException(Short dcMeterException) {
		this.dcMeterException = dcMeterException;
	}
	public Short getCarChargeMode() {
		return carChargeMode;
	}
	public void setCarChargeMode(Short carChargeMode) {
		this.carChargeMode = carChargeMode;
	}
	public Short getCarSocWarn() {
		return carSocWarn;
	}
	public void setCarSocWarn(Short carSocWarn) {
		this.carSocWarn = carSocWarn;
	}
	public Short getChargeModSampleOverTemp() {
		return chargeModSampleOverTemp;
	}
	public void setChargeModSampleOverTemp(Short chargeModSampleOverTemp) {
		this.chargeModSampleOverTemp = chargeModSampleOverTemp;
	}
	public Short getChargeOutLinkerOverTemp() {
		return chargeOutLinkerOverTemp;
	}
	public void setChargeOutLinkerOverTemp(Short chargeOutLinkerOverTemp) {
		this.chargeOutLinkerOverTemp = chargeOutLinkerOverTemp;
	}
	public Short getChargeOutLinkerStatus() {
		return chargeOutLinkerStatus;
	}
	public void setChargeOutLinkerStatus(Short chargeOutLinkerStatus) {
		this.chargeOutLinkerStatus = chargeOutLinkerStatus;
	}
	public Short getChargeWholeOverCurrentWarn() {
		return chargeWholeOverCurrentWarn;
	}
	public void setChargeWholeOverCurrentWarn(Short chargeWholeOverCurrentWarn) {
		this.chargeWholeOverCurrentWarn = chargeWholeOverCurrentWarn;
	}
	public Short getChargeVolWarn() {
		return chargeVolWarn;
	}
	public void setChargeVolWarn(Short chargeVolWarn) {
		this.chargeVolWarn = chargeVolWarn;
	}
	public BigDecimal getSignleBattryHighVol() {
		return signleBattryHighVol;
	}
	public void setSignleBattryHighVol(BigDecimal signleBattryHighVol) {
		this.signleBattryHighVol = signleBattryHighVol;
	}
	public BigDecimal getBpHighestTemperature() {
		return bpHighestTemperature;
	}
	public void setBpHighestTemperature(BigDecimal bpHighestTemperature) {
		this.bpHighestTemperature = bpHighestTemperature;
	}
	public BigDecimal getBpLowestTemperature() {
		return bpLowestTemperature;
	}
	public void setBpLowestTemperature(BigDecimal bpLowestTemperature) {
		this.bpLowestTemperature = bpLowestTemperature;
	}
	public BigDecimal getCarBattryTotalVol() {
		return carBattryTotalVol;
	}
	public void setCarBattryTotalVol(BigDecimal carBattryTotalVol) {
		this.carBattryTotalVol = carBattryTotalVol;
	}
	public BigDecimal getInAVol() {
		return inAVol;
	}
	public void setInAVol(BigDecimal inAVol) {
		this.inAVol = inAVol;
	}
	public BigDecimal getInBVol() {
		return inBVol;
	}
	public void setInBVol(BigDecimal inBVol) {
		this.inBVol = inBVol;
	}
	public BigDecimal getInCVol() {
		return inCVol;
	}
	public void setInCVol(BigDecimal inCVol) {
		this.inCVol = inCVol;
	}
	public BigDecimal getInACurrent() {
		return inACurrent;
	}
	public void setInACurrent(BigDecimal inACurrent) {
		this.inACurrent = inACurrent;
	}
	public BigDecimal getInBCurrent() {
		return inBCurrent;
	}
	public void setInBCurrent(BigDecimal inBCurrent) {
		this.inBCurrent = inBCurrent;
	}
	public BigDecimal getInCCurrent() {
		return inCCurrent;
	}
	public void setInCCurrent(BigDecimal inCCurrent) {
		this.inCCurrent = inCCurrent;
	}
	public BigDecimal getOutHighestVol() {
		return outHighestVol;
	}
	public void setOutHighestVol(BigDecimal outHighestVol) {
		this.outHighestVol = outHighestVol;
	}
	public BigDecimal getOutLowestVol() {
		return outLowestVol;
	}
	public void setOutLowestVol(BigDecimal outLowestVol) {
		this.outLowestVol = outLowestVol;
	}
	public BigDecimal getOutHighestCurrent() {
		return outHighestCurrent;
	}
	public void setOutHighestCurrent(BigDecimal outHighestCurrent) {
		this.outHighestCurrent = outHighestCurrent;
	}
	public Short getChargeOutOverCurrent() {
		return chargeOutOverCurrent;
	}
	public void setChargeOutOverCurrent(Short chargeOutOverCurrent) {
		this.chargeOutOverCurrent = chargeOutOverCurrent;
	}
	public Short getBmsVolWarn() {
		return bmsVolWarn;
	}
	public void setBmsVolWarn(Short bmsVolWarn) {
		this.bmsVolWarn = bmsVolWarn;
	}
	public void setChargeUserId(Integer chargeUserId) {
		this.chargeUserId = chargeUserId;
	}
	public void setChargeStartTime(Integer chargeStartTime) {
		this.chargeStartTime = chargeStartTime;
	}
	
	
	
	

}
