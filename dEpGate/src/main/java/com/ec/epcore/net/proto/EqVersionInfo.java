package com.ec.epcore.net.proto;

public class EqVersionInfo {
	
	private int pk_EquipmentVersion;
	private String epCode;
	private int  stationAddr;
	private int  type;
	//固件编码
	private String softNumber;
	//固件版本	
	private String softVersion;
	//主版本
	private int softM;
	//副版本
	private int softA;
	//编译版本
	private int softC;
	//硬件编码
	private String hardwareNumber;
	//硬件版本	
	private String hardwareVersion;
	//主版本
	private int hardwareM;
		//副版本
	private int hardwareA;
	
	
	public EqVersionInfo()
	{
		softNumber = "0";
		softVersion="0";
		softM=0;
		softA=0;
		softC=0;
		hardwareNumber="0";
		hardwareVersion="0";
		hardwareM=0;
		hardwareA=0;
		pk_EquipmentVersion=0;
	}
	
	
	
	public int getPk_EquipmentVersion() {
		return pk_EquipmentVersion;
	}

	public void setPk_EquipmentVersion(int pk_EquipmentVersion) {
		this.pk_EquipmentVersion = pk_EquipmentVersion;
	}
	

	public String getEpCode() {
		return epCode;
	}


	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}


	public int getStationAddr() {
		return stationAddr;
	}


	public void setStationAddr(int stationAddr) {
		this.stationAddr = stationAddr;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getSoftNumber() {
		return softNumber;
	}


	public void setSoftNumber(String softNumber) {
		this.softNumber = softNumber;
	}


	public String getSoftVersion() {
		return softVersion;
	}


	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}


	public int getSoftM() {
		return softM;
	}


	public void setSoftM(int softM) {
		this.softM = softM;
	}


	public int getSoftA() {
		return softA;
	}


	public void setSoftA(int softA) {
		this.softA = softA;
	}


	public int getSoftC() {
		return softC;
	}


	public void setSoftC(int softC) {
		this.softC = softC;
	}


	public String getHardwareNumber() {
		return hardwareNumber;
	}


	public void setHardwareNumber(String hardwareNumber) {
		this.hardwareNumber = hardwareNumber;
	}


	public String getHardwareVersion() {
		return hardwareVersion;
	}


	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}


	public int getHardwareM() {
		return hardwareM;
	}


	public void setHardwareM(int hardwareM) {
		this.hardwareM = hardwareM;
	}


	public int getHardwareA() {
		return hardwareA;
	}


	public void setHardwareA(int hardwareA) {
		this.hardwareA = hardwareA;
	}
    

	
		
}
