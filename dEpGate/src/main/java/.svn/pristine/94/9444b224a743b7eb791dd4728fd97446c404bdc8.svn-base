package com.ec.epcore.net.proto;

public class EpCancelBespResp {
	
	private String epCode;
	private int epGunNo;
	private String bespNo;
	private short lockFlag;
	private short SuccessFlag;
	private long et;
	
	private int errorCode;
		
	public EpCancelBespResp( String epCode,int epGunNo, String bespNo,
			short lockFlag, short SuccessFlag,long et,int errorCode)
	{
		this.epCode = epCode;
		this.epGunNo = epGunNo;
		this.bespNo = bespNo;
		this.lockFlag = lockFlag;
		this.SuccessFlag = SuccessFlag;
		this.et= et;
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

	public String getBespNo() {
		return bespNo;
	}

	public void setBespNo(String bespNo) {
		this.bespNo = bespNo;
	}

	public short getLockFlag() {
		return lockFlag;
	}

	public void setLockFlag(short lockFlag) {
		this.lockFlag = lockFlag;
	}

	public short getSuccessFlag() {
		return SuccessFlag;
	}

	public void setSuccessFlag(short successFlag) {
		SuccessFlag = successFlag;
	}

	public long getEt() {
		return et;
	}

	public void setEt(long et) {
		this.et = et;
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

        sb.append("EpBespResp\n");
        sb.append("this.epCode:"+this.epCode+"\n");
        sb.append("this.epGunNo:"+this.epGunNo+"\n");
        sb.append("this.lockFlag:"+this.lockFlag+"\n");
        sb.append("this.bespNo:"+this.bespNo+"\n");
        
        sb.append("this.SuccessFlag:"+this.SuccessFlag+"\n");
        sb.append("this.et:"+this.et+"\n");
        return sb.toString();
	}
	

}
