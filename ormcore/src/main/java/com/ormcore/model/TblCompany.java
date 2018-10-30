package com.ormcore.model;
             
public class TblCompany {
	
	private java.lang.Integer pkCompanyId;// 公司ID
	private java.lang.Integer cpyCompanyNumber;// 公司组织编码
	private java.lang.Integer cpynum;// 临时充电次数
	private java.lang.Integer isValid;

	public java.lang.Integer getPkCompanyId() {
		return pkCompanyId;
	}
	public void setPk_CompanyId(java.lang.Integer pkCompanyId) {
		this.pkCompanyId = pkCompanyId;
	}
	public java.lang.Integer getCpyCompanyNumber() {
		return cpyCompanyNumber;
	}
	public void setCpy_CompanyNumber(java.lang.Integer cpyCompanyNumber) {
		this.cpyCompanyNumber = cpyCompanyNumber;
	}
	public java.lang.Integer getCpynum() {
		return cpynum;
	}
	public void setCpy_num(java.lang.Integer cpynum) {
		this.cpynum = cpynum;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}
}
