package com.ec.epcore.cache;

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
	BigDecimal present;
	private String deviceid;//手机设备ID号
	private String invitePhone;//邀请者号码
	private int accountId;
	private int cpyId;
	private int cpyNumber;
	private int levelId;

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

	public BigDecimal getPresent() {
		return present;
	}

	public void setPresent(BigDecimal present) {
		this.present = present;
	}

	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCpyId() {
        return cpyId;
    }

    public void setCpyId(int cpyId) {
        this.cpyId = cpyId;
    }

	public int getCpyNumber() {
		return cpyNumber;
	}

	public void setCpyNumber(int cpyNumber) {
		this.cpyNumber = cpyNumber;
	}

	public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int canCharge()
	{
		if(getStatus() == 3)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT;
		}
		if(getStatus() == 2)
		{
			return ErrorCodeConstants.INVALID_ACCOUNT_STATUS;
		}
		return 0;
	}
	
}
