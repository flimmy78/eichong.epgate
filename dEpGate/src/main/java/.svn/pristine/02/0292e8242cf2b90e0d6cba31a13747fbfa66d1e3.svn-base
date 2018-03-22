package com.ec.epcore.cache;

public class UserOrigin {
	
	private int orgNo;//1000:万马;
	private int cmdFromSource;//1:API,2:手机;3电桩
	private String consumeGateIdentity;//"",空，来自于其他消费服务器的IP.

	
	public UserOrigin(int userOrgNo,int cmdFromSource,String cmdChIdentity)
	{
		this.orgNo = userOrgNo;
		this.cmdFromSource = cmdFromSource;
		this.consumeGateIdentity = cmdChIdentity;
	}
	
	public int getOrgNo() {
		return orgNo;
	}



	public void setOrgNo(int orgNo) {
		this.orgNo = orgNo;
	}
	
	
	

	public int getCmdFromSource() {
		return cmdFromSource;
	}



	public void setCmdFromSource(int cmdFromSource) {
		this.cmdFromSource = cmdFromSource;
	}



	public String getCmdChIdentity() {
		return consumeGateIdentity;
	}



	public void setCmdChIdentity(String cmdChIdentity) {
		this.consumeGateIdentity = cmdChIdentity;
	}



	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("UserOrigin");
      
        sb.append(",{userOrgin=").append(orgNo).append("}\n");
		
		sb.append(",{cmdFromSource=").append(cmdFromSource).append("}\n");
		sb.append(",{consumeGateIdentity=").append(consumeGateIdentity).append("}\n");
         
   		return sb.toString();
	}
}
