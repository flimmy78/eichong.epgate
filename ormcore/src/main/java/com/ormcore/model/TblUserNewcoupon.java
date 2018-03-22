package com.ormcore.model;
             
public class TblUserNewcoupon {
	
	private java.lang.Integer pkUserNewcoupon;//
	private java.lang.Integer userId; //
	private java.lang.Integer acStatus;//交流新手状态
	private java.lang.Integer dcStatus;//直流新手状态
	
	public java.lang.Integer getUserId() {
		return userId;
	}
	public void setUserId(java.lang.Integer userId) {
		this.userId = userId;
	}
	public java.lang.Integer getAcStatus() {
		return acStatus;
	}
	public void setAcStatus(java.lang.Integer acStatus) {
		this.acStatus = acStatus;
	}
	public java.lang.Integer getDcStatus() {
		return dcStatus;
	}
	public void setDcStatus(java.lang.Integer dcStatus) {
		this.dcStatus = dcStatus;
	}
	
	
}
