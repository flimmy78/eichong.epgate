package com.ec.epcore.service;

public class ChargingInfo {
	private int workStatus;  //充电机状态
	private int totalTime;  //累计充电时间
	private int outVol;   //充电机输出电压
	private int outCurrent;//充电机输出电流
	private int chargeMeterNum; //已充度数
	private int rateInfo;  //费率
	private int fronzeAmt;  //冻结金额
	private int chargeAmt;   //已充金额
	private int soc;        // soc
	private int deviceStatus;  // 设备状态  0
	private int warns;       //警告   0


	private long chargeStartTime;//精确到秒
	private float ServiceRate;  //服务费

	private float elecAmt;//单价
	private float totalPower;//有功总电度

	public float getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(final float totalPower) {
		this.totalPower = totalPower;
	}


	public float getElecAmt() {
		return elecAmt;
	}

	public void setElecAmt(final float elecAmt) {
		this.elecAmt = elecAmt;
	}

	public float getServiceRate() {
		return ServiceRate;
	}

	public void setServiceRate(final float serviceRate) {
		ServiceRate = serviceRate;
	}

	public long getChargeStartTime() {
		return chargeStartTime;
	}

	public void setChargeStartTime(final long chargeStartTime) {
		this.chargeStartTime = chargeStartTime;
	}



	public int getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(int workStatus) {
		this.workStatus = workStatus;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public int getOutVol() {
		return outVol;
	}
	public void setOutVol(int outVol) {
		this.outVol = outVol;
	}
	public int getOutCurrent() {
		return outCurrent;
	}
	public void setOutCurrent(int outCurrent) {
		this.outCurrent = outCurrent;
	}
	public int getChargeMeterNum() {
		return chargeMeterNum;
	}
	public void setChargeMeterNum(int chargeMeterNum) {
		this.chargeMeterNum = chargeMeterNum;
	}
	public int getRateInfo() {
		return rateInfo;
	}
	public void setRateInfo(int rateInfo) {
		this.rateInfo = rateInfo;
	}
	public int getChargeAmt() {
		return chargeAmt;
	}
	public void setChargeAmt(int chargeAmt) {
		this.chargeAmt = chargeAmt;
	}
	public int getSoc() {
		return soc;
	}
	public void setSoc(int soc) {
		this.soc = soc;
	}
	public int getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public int getWarns() {
		return warns;
	}
	public void setWarns(int warns) {
		this.warns = warns;
	}
	public int getFronzeAmt() {
		return fronzeAmt;
	}
	public void setFronzeAmt(int fronzeAmt) {
		this.fronzeAmt = fronzeAmt;
	}
	
}
