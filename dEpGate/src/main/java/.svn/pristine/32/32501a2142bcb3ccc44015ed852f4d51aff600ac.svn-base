package com.ec.epcore.net.server;

import com.ec.epcore.cache.UserOrigin;
import com.ec.netcore.client.ECTcpClient;
import com.ec.utils.DateUtil;

public class UsrGateClient  extends ECTcpClient {
	
	

	private int usrGateId;
	
	UserOrigin userOrigin;
	
	String ip;
	
	private int version;
	
	public UsrGateClient()
	{
		userOrigin=new UserOrigin(0,0,"");
		usrGateId=0;
		ip="";
		version=1;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	

	
	public int getUsrGateId() {
		return usrGateId;
	}



	public void setUsrGateId(int usrGateId) {
		this.usrGateId = usrGateId;
	}



	public int getVersion() {
		return version;
	}



	public void setVersion(int version) {
		this.version = version;
	}



	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("UsrGateClient");
        
        sb.append(",{usrGateId=").append(usrGateId).append("}\n");
        sb.append(",{ip=").append(ip).append("}\n");
        sb.append(",{version=").append(version).append("}\n");
        sb.append(",{status=").append(status);
        switch(status)
        {
        case 2:
        	sb.append("已连接，网关登录成功").append("}\n");
        	break;
        case 1:
        	sb.append("已连接，网关位登录").append("}\n)");
        	break;
        default:
        	sb.append("未连接").append("}\n");
        	break;
		
        }
        sb.append(",{channel=").append(channel).append("}\n");
        sb.append(",{identity=").append(identity).append("}\n");
        sb.append(",{lastUseTime=").append(DateUtil.StringYourDate(DateUtil.toDate(lastUseTime*1000))).append("}\n\n");
        sb.append(userOrigin.toString());
   		return sb.toString();
	}

	public UserOrigin getUserOrigin() {
		return userOrigin;
	}

	public void setUserOrigin(UserOrigin userOrigin) {
		this.userOrigin = userOrigin;
	}

}

