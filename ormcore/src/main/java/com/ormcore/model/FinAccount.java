package com.ormcore.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class FinAccount implements Serializable {

	private static final long serialVersionUID = -8661478449881571595L;
	/** 主键**/
	private Integer accountId;
			
	/** 资金账户号**/
	private String accountNo;
			
	/** 支付密码**/
	private String accountPwd;
			
	/** 普通用户充值余额**/
	private BigDecimal accountBalance;

	/** 赠送余额**/
	private BigDecimal accountPresent;

	/** '结算方式 1.后付费 2.预付费'**/
	private Integer tradeType;

	/** 预警金额**/
	private BigDecimal accountWarn;

	/** 预警手机号**/
	private String warnPhone;

	/** '预警标识0：未发送 1：已发送'**/
	private Short warnFlag;

	/** '1.正常 2.冻结 3.删除'**/
	private Integer accountStatus;

	/** '是否删除 0.否 1.是'**/
	private Integer isDel;

	/** 创建人**/
	private String creator;

	/** 修改人**/
	private String modifier;

	/** 创建时间**/
	private java.util.Date gmtCreate;

	private java.util.Date gmtModified;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountPwd() {
		return accountPwd;
	}

	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public BigDecimal getAccountPresent() {
		return accountPresent;
	}

	public void setAccountPresent(BigDecimal accountPresent) {
		this.accountPresent = accountPresent;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public BigDecimal getAccountWarn() {
		return accountWarn;
	}

	public void setAccountWarn(BigDecimal accountWarn) {
		this.accountWarn = accountWarn;
	}

	public String getWarnPhone() {
		return warnPhone;
	}

	public void setWarnPhone(String warnPhone) {
		this.warnPhone = warnPhone;
	}

	public Short getWarnFlag() {
		return warnFlag;
	}

	public void setWarnFlag(Short warnFlag) {
		this.warnFlag = warnFlag;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
}
