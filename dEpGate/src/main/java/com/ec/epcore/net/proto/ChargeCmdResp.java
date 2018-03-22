package com.ec.epcore.net.proto;

public class ChargeCmdResp {
	
	private String epCode;
	private int epGunNo;
	
	private int ret;
	private short errorCause;
	
	public ChargeCmdResp(String epCode,int epGunNo,int ret,short errorCause)
	{
		this.epCode = epCode;
		this.epGunNo = epGunNo;
		this.ret =  ret;
		this.errorCause = errorCause;
		
	}
	
	public String getEpCode() {
		return epCode;
	}
	
	public int getEpGunNo() {
		return epGunNo;
	}
	
	public int getRet() {
		return ret;
	}
	
	public short getErrorCause() {
		return errorCause;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
        sb.append("ChargeCmdResp");
        
        sb.append("{ret=").append(ret).append("}\n");
        sb.append(",{errorCause=").append(errorCause).append("}\n");
        
        return sb.toString();
	}
	
	
	
	
	
		

}
