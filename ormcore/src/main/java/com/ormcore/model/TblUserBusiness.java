package com.ormcore.model;

import java.io.Serializable;

public class TblUserBusiness   implements Serializable {
	private java.lang.Integer id; 
	
	private java.lang.Integer user_id; 
	
	private java.lang.Integer parterId;
	private java.lang.Integer companyId; 
	private java.lang.String phone;
	private java.math.BigDecimal balance;
	
	public TblUserBusiness()
	{
		
	}


	public java.lang.Integer getId() {
		return id;
	}


	public void setId(java.lang.Integer id) {
		this.id = id;
	}


	public java.lang.Integer getParterId() {
		return parterId;
	}


	public void setParterId(java.lang.Integer parterId) {
		this.parterId = parterId;
	}


	public java.lang.Integer getCompanyId() {
		return companyId;
	}


	public void setCompanyId(java.lang.Integer companyId) {
		this.companyId = companyId;
	}


	public java.lang.String getPhone() {
		return phone;
	}


	public void setPhone(java.lang.String phone) {
		this.phone = phone;
	}


	public java.math.BigDecimal getBalance() {
		return balance;
	}


	public void setBalance(java.math.BigDecimal balance) {
		this.balance = balance;
	}
	
	
	
}

