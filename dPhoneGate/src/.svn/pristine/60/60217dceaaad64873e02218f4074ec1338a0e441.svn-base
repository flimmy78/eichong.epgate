package com.ec.phonegate.client;

import com.ec.constants.UserConstants;
import com.ec.netcore.client.ECTcpClient;
import com.ec.phonegate.service.CachePhoneService;
import com.ec.usrcore.server.CommonServer;



public class PhoneClient  extends ECTcpClient {
	
	private String  epCode; 
	
	private int epGunNo;

	private int accountId;
	
	private String account;
	
	private String ip;
	private int version;
	private int cmd;
	private boolean connectFlg;
	private int orgNo;
	public PhoneClient()
	{
		epCode=""; 	
		epGunNo =0;
		accountId =0;
		
		account="";
		orgNo=UserConstants.ORG_I_CHARGE;
	}
	
	public String getEpCode() {
		return epCode;
	}
	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}
	

	
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setEpGunNo(int epGunNo) {
		this.epGunNo = epGunNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getEpGunNo() {
		return epGunNo;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public boolean isConnectFlg() {
		return connectFlg;
	}

	public void setConnectFlg(boolean connectFlg) {
		this.connectFlg = connectFlg;
	}

	public int getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(int orgNo) {
		this.orgNo = orgNo;
	}

	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("PhoneClient");
        
        sb.append("{epCode=").append(epCode).append("}\n");
        sb.append(",{epGunNo=").append(epGunNo).append("}\n");
        sb.append(",{accountId=").append(accountId).append("}\n");
        
       
        sb.append(",{account=").append(account).append("}\n");
        sb.append(",{ip=").append(ip).append("}\n");
        sb.append(",{version=").append(version).append("}\n");
   		return sb.toString();
	}
	
	public void handleNetTimeOut() {
		
		CommonServer.getInstance().phoneDisconnect(getOrgNo(), String.valueOf(getAccountId()),
				getEpCode(), getEpGunNo());
		
		CachePhoneService.removePhoneClientByAccountId(getAccountId());

		super.handleNetTimeOut();	
	}


	

	
}
