package com.ec.usrcore.cache;

import java.math.BigDecimal;

import com.ec.constants.ErrorCodeConstants;

public class UserRealInfo {
	private int id; //
	private String account; //
	private String name; //
	private String password;//
	private int level;//
	private int status;//
	
	BigDecimal money;//钱
	private String deviceid;//手机设备ID号 
	private String invitePhone;//邀请者号码
	private int cpy_number;
	
	public String getInvitePhone() {
		return invitePhone;
	}
	public void setInvitePhone(String invitePhone) {
		this.invitePhone = invitePhone;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public int getCpy_number() {
		return cpy_number;
	}

	public void setCpy_number(int cpy_number) {
		this.cpy_number = cpy_number;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int canCharge()
	{
		if (status == 3)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		if (status == 2)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT_STATUS;
		}
		return 0;
	}
	
}
