package com.ormcore.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TblBomList {

	private static final Logger logger = LoggerFactory.getLogger(TblBomList.class);
	
	    private Integer bomListId;
		private Integer typeSpanId; 
		
		private String typeSpan;
	
		private String  softNumber; //固件编号
		private String softVersion; //固件版本，对应数据库bl_FirmwareVersion
		private String hardwareNumber;//硬件编号
		private String hardwareVersion; //硬件版本
		
		private Integer forceUpdate;
		private String fileMd5;
		private Date createdate;
		private Date updatedate;
		
		private Integer hardwareM; //硬件主版本  由硬件版本拆分
		private Integer hardwareA; //硬件子版本
		private Integer softM; //软件主版本  由固件版本拆分
		private Integer softA; //软件子版本
		private Integer softC; //软件编译版本
		
		public  TblBomList()
		{
			bomListId=0;
			typeSpanId=0;
			forceUpdate=0;
			softNumber=new String("0");
			softVersion= new String("0");
			hardwareNumber=new String("0");
			hardwareVersion=new String("0");
			typeSpan = new String("0");
			fileMd5=new String("00000000000000000000000000000000");
			createdate= new Date(); 
			updatedate = new Date();
			hardwareM=0;
			hardwareA=0;
			softM=0;
			softA=0;
			softC=0;
			
		}
		
		
		
		public String getTypeSpan() {
			return typeSpan;
		}



		public void setTypeSpan(String typeSpan) {
			this.typeSpan = typeSpan;
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
        		logger.error("splitSoftVersion exception,e.getMessage:{}",e.getMessage());
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
        		logger.error("splitHardwareVersion exception,e.getMessage:{}",e.getMessage());
    			return 1;
    		}
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


		public Integer getHardwareM() {
			return hardwareM;
		}


		public void setHardwareM(Integer hardwareM) {
			this.hardwareM = hardwareM;
		}


		public Integer getHardwareA() {
			return hardwareA;
		}


		public void setHardwareA(Integer hardwareA) {
			this.hardwareA = hardwareA;
		}


		public Integer getSoftM() {
			return softM;
		}


		public void setSoftM(Integer softM) {
			this.softM = softM;
		}


		public Integer getSoftA() {
			return softA;
		}


		public void setSoftA(Integer softA) {
			this.softA = softA;
		}


		public Integer getSoftC() {
			return softC;
		}


		public void setSoftC(Integer softC) {
			this.softC = softC;
		}


		public String getFileMd5() {
			return fileMd5;
		}

		public void setFileMd5(String fileMd5) {
			this.fileMd5 = fileMd5;
		}
		
		
		
		public Integer getBomListId() {
			return bomListId;
		}

		public void setBomListId(Integer bomListId) {
			this.bomListId = bomListId;
		}

		public Integer getTypeSpanId() {
			return typeSpanId;
		}

		public void setTypeSpanId(Integer typeSpanId) {
			this.typeSpanId = typeSpanId;
		}

		public Integer getForceUpdate() {
			return forceUpdate;
		}

		public void setForceUpdate(Integer forceUpdate) {
			this.forceUpdate = forceUpdate;
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
		
		
		
}
