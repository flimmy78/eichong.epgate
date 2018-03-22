package com.ec.epcore.cache;

import com.ec.utils.DateUtil;

public class AuthUserCache {
	
	private int usrId;
	private String account;
	
	private long lastTime;//用户鉴权时间
	
	private short style;//1:手机;3:卡
	
	public AuthUserCache(int pUsrId,String pAccount,long time,short style)
	{
		usrId= pUsrId;
		account= pAccount;
		
		lastTime=time;//用户鉴权时间
		this.style = style;
	}

	public int getUsrId() {
		return usrId;
	}

	public void setUsrId(int usrId) {
		this.usrId = usrId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public short getStyle() {
		return style;
	}

	public void setStyle(short style) {
		this.style = style;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		  sb.append("鉴权用户(").append(usrId).append(":"+account+")\n");
		  sb.append("鉴权时间(");
		  
		  String sTime= "";
	      sTime = DateUtil.longDateToString(lastTime*1000);
	      sb.append(sTime+")\n");
	      sb.append("鉴权方向:");
		  switch(style)
		  {
		  case 1:
			  sb.append("手机\n");
		  case 3:
			  sb.append("电桩\n");
		  }
		 
		  
		  return sb.toString();
	}
	
	
}
