package com.ec.epcore.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BomListInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(BomListInfo.class);
	
	private int typeSpanId; 
	
	private String typeSpan;

	private String  softNumber; //固件编号
	private String softVersion; //固件版本，对应数据库bl_FirmwareVersion
	private String hardwareNumber;//硬件编号
	private String hardwareVersion; //硬件版本
	
	private int forceUpdate;
	private String fileMd5;
	private Date createdate;
	private Date updatedate;
	
	private int hardwareM; //硬件主版本  由硬件版本拆分
	private int hardwareA; //硬件子版本
	private int softM; //软件主版本  由固件版本拆分
	private int softA; //软件子版本
	private int softC; //软件编译版本
	private int bomListId; //bom表索引
	
	public  BomListInfo()
	{
		typeSpanId=0;
		typeSpan="";
		softNumber="";
		softVersion= "0";
		hardwareNumber="";
		hardwareVersion="0";
		
		fileMd5="";
		createdate= new Date(); 
		updatedate = new Date();
		hardwareM=0;
		hardwareA=0;
		softM=0;
		softA=0;
		softC=0;
		bomListId=0;
		
	}
	
	

	public int getBomListId() {
		return bomListId;
	}



	public void setBomListId(int bomListId) {
		this.bomListId = bomListId;
	}



	public int getTypeSpanId() {
		return typeSpanId;
	}

	public void setTypeSpanId(int typeSpanId) {
		this.typeSpanId = typeSpanId;
	}

	public String getTypeSpan() {
		return typeSpan;
	}

	public void setTypeSpan(String typeSpan) {
		this.typeSpan = typeSpan;
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

	public int getForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(int forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
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
	
	public int splitSoftVersion() {
		try{
		String [] softVer= softVersion.split("\\.");
	    int len = softVer.length;
	    if(len !=3)
	    	return 1;
	    for(int i=0;i<len;i++)
	    {
	    	int ver = Integer.parseInt(softVer[i]);
	    	switch(i)
	    	{
	    	case 0:
	    		setSoftM(ver);
	    		break;
	    	case 1:
	    		setSoftA(ver);
	    		break;
	    	case 2:
	    		setSoftC(ver);
	    		break;
	    	default:
	    		break;
	    	}
	    	 
	      }
	   
	     return 0;
    	}catch (Exception e) {
    		logger.error("splitSoftVersion exception,e.getMessage():{}",e.getMessage());
			return 1;
		}
	}
    public int splitHardwareVersion() {
    	try{
    	String [] hardwareVer= hardwareVersion.split("\\.");
	    int len = hardwareVer.length;
	    if(len !=2)
	    	return 1;
	    for(int i=0;i<len;i++)
	    {
	    	int ver = Integer.parseInt(hardwareVer[i]);
	    	switch(i)
	    	{
	    	case 0:
	    		setHardwareM(ver);
	    		break;
	    	case 1:
	    		setHardwareA(ver);
	    		break;
	        default:
	        	break;
	    	}
	    }
	   
	    return 0;
    	}catch (Exception e) {
    		logger.error("splitHardwareVersion exception,e.getMessage():{}",e.getMessage());
			return 1;
		}
	}

	
}
