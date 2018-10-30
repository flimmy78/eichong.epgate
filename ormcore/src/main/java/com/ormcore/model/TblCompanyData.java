package com.ormcore.model;

import java.io.Serializable;

public class TblCompanyData implements Serializable {

	private static final long serialVersionUID = -1434483224689759008L;
	private Integer cpyId;// 公司ID
	private Integer cpyNumber;// 公司标识
	private Integer cpyNum;// 临时充电次数
	private Integer isValid;//'是否开启盗刷校验 0.否 1.是'
	private String cpyName;//企业名称

	public Integer getCpyId() {
		return cpyId;
	}

	public void setCpyId(Integer cpyId) {
		this.cpyId = cpyId;
	}

	public Integer getCpyNumber() {
		return cpyNumber;
	}

	public void setCpyNumber(Integer cpyNumber) {
		this.cpyNumber = cpyNumber;
	}

	public Integer getCpyNum() {
		return cpyNum;
	}

	public void setCpyNum(Integer cpyNum) {
		this.cpyNum = cpyNum;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public String getCpyName() {
		return cpyName;
	}

	public void setCpyName(String cpyName) {
		this.cpyName = cpyName;
	}
}
