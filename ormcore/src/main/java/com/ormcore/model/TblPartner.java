package com.ormcore.model;

public class TblPartner {
	private Integer pkPartner;
	private String name;
	private String key;
	private String token;
	private String clientId;
	private String clientIp;
	private Integer clientPort;
	private String clientKey;
	
	private java.util.Date registerdate;
	
	private java.util.Date updatedate;
	
	public java.util.Date getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(java.util.Date updatedate) {
		this.updatedate = updatedate;
	}

	private Integer updateCycleType;
	
	public java.util.Date getRegisterdate() {
		return registerdate;
	}

	public void setRegisterdate(java.util.Date registerdate) {
		this.registerdate = registerdate;
	}

	public Integer getUpdateCycleType() {
		return updateCycleType;
	}

	public void setUpdateCycleType(Integer updateCycleType) {
		this.updateCycleType = updateCycleType;
	}

	public Integer getUpdateCycleValue() {
		return updateCycleValue;
	}

	public void setUpdateCycleValue(Integer updateCycleValue) {
		this.updateCycleValue = updateCycleValue;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	private Integer updateCycleValue;
	
	private Integer valid;

	
	public Integer getPkPartner() {
		return pkPartner;
	}

	public void setPkPartner(Integer pkPartner) {
		this.pkPartner = pkPartner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Integer getClientPort() {
		return clientPort;
	}

	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	
	
	
}
