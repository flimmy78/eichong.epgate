package com.ormcore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
public class TblChargeACInfo implements Serializable {

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
	protected int chargeUserId =0 ;
	protected String chargeSerialNo ="";
	protected int chargeStartTime= 0;
	protected BigDecimal chargeStartMeterNum;
	//
	private BigDecimal chargePrice;//当前价格
    private BigDecimal chargedCost;//已充金额
    private BigDecimal chargedMeterNum;//已充度数
    private BigDecimal fronzeAmt;//冻结金额
	
    private void init()
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
    	
    }
	public TblChargeACInfo()
	{
		
		init();
	}
	public TblChargeACInfo(String epCode,int epGunNo,BigDecimal chargePrice,
			BigDecimal chargedCost,int chargedTime,BigDecimal chargedMeterNum,int userId)
	{
		init();
		this.ep_code = epCode;
		this.ep_gun_no = epGunNo;
		
		this.chargePrice= chargePrice;
		
		this.chargedCost= chargedCost;
		this.chargedMeterNum= chargedMeterNum;//已充度数
		this.chargedTime= chargedTime;//已充时间(分钟)
		this.chargeUserId = userId;
		
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

}
