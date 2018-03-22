package com.ec.epcore.net.proto;

import com.ec.utils.StringUtil;

public class ChargeEvent {
	//1	终端机器编码	BCD码	8Byte	16位编码	
	private String epCode;
	//2	充电枪编号	BIN码	1Byte
	private int epGunNo;
	//3	交易流水号	BCD码	16Byte	终端机器编码16+序列号16	
	private String serialNo;
	//4	表低示数	BIN码	4Byte	精确到小数点后三位，单位度，倍数1000
	private int startMeterNum;
	//5	充电开始时间	CP56Time2a	7Byte	
	private long startChargeTime;
	//8	充满电剩余时间	BIN码	4Byte	精确到秒数
	private int remainTime;
	//直流有效,交流数据为0	
	//9	标识	BIN码	1Byte	1:开始充电
	private int successFlag;
	
	private int onlineFlag;
	
	public int getOnlineFlag() {
		return onlineFlag;
	}
	public void setOnlineFlag(int onlineFlag) {
		this.onlineFlag = onlineFlag;
	}
	private String bespokeNo;
	
	private int errorCode;

	//0:放弃（未插枪超时等）
	public ChargeEvent(String epCode,int epGunNo,String serialNo,int startMeterNum,long startChargeTime,int remainTime,int  successFlag,int errorCode)
	//0:放弃（未插枪超时等）)
	{
		this.epCode = epCode;
		//2	充电枪编号	BIN码	1Byte
		this.epGunNo = epGunNo;
		//3	交易流水号	BCD码	16Byte	终端机器编码16+序列号16	
		this.serialNo =serialNo;
		//4	表低示数	BIN码	4Byte	精确到小数点后三位，单位度，倍数1000
		this.startMeterNum = startMeterNum;
		//5	充电开始时间	CP56Time2a	7Byte	
		this.startChargeTime = startChargeTime;
		//8	充满电剩余时间	BIN码	4Byte	精确到秒数
		this.remainTime =remainTime;
		//直流有效,交流数据为0	
		//9	标识	BIN码	1Byte	1:开始充电
		this.successFlag=successFlag;
		//0:放弃（未插枪超时等）
		
		this.bespokeNo = StringUtil.repeat("0", 12);
		
		this.errorCode = errorCode;
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
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public int getStartMeterNum() {
		return startMeterNum;
	}
	public void setStartMeterNum(int startMeterNum) {
		this.startMeterNum = startMeterNum;
	}
	public long getStartChargeTime() {
		return startChargeTime;
	}
	public void setStartChargeTime(long startChargeTime) {
		this.startChargeTime = startChargeTime;
	}
	public int getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(int remainTime) {
		this.remainTime = remainTime;
	}
	public int getSuccessFlag() {
		return successFlag;
	}
	public void setSuccessFlag(int successFlag) {
		this.successFlag = successFlag;
	}
	
	
	public String getBespokeNo() {
		return bespokeNo;
	}
	public void setBespokeNo(String bespokeNo) {
		this.bespokeNo = bespokeNo;
	}
	
	
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("ChargeEvent");
        
       
        sb.append("{epCode=").append(epCode).append("}\n");
        sb.append(",{epGunNo=").append(epGunNo).append("}\n");
        sb.append(",{serialNo=").append(serialNo).append("}\n");
        
               
        sb.append(",{startMeterNum=").append(startMeterNum).append("}\n");
        sb.append(",{startChargeTime=").append(startChargeTime).append("}\n");
        
        sb.append(",{remainTime=").append(remainTime).append("}\n");
        sb.append(",{successFlag=").append(successFlag).append("}\n");
        sb.append(",{errorCode=").append(errorCode).append("}\n");
        
   		return sb.toString();
	}
	


}
