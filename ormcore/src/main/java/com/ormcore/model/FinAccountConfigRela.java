package com.ormcore.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class FinAccountConfigRela implements Serializable {

    private static final long serialVersionUID = -2863600631542180879L;
    /** 主键**/
	private Integer pkId;

	/** 渠道ID**/
	private Integer cpyId;

	/** 账单科目ID**/
	private Integer billAccountId;

	/** '付费策略 1.扣大账户 2.扣自己 3.为小账户配资'**/
	private Short paymentRule;

	/** '是否删除 0.否 1.是'**/
	private Short isDel;

	/** 创建人**/
	private String creator;

	/** 修改人**/
	private String modifier;

	/** 创建时间**/
	private Date gmtCreate;

	/** 修改时间**/
	private Date gmtModified;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getCpyId() {
		return cpyId;
	}

	public void setCpyId(Integer cpyId) {
		this.cpyId = cpyId;
	}

	public Integer getBillAccountId() {
		return billAccountId;
	}

	public void setBillAccountId(Integer billAccountId) {
		this.billAccountId = billAccountId;
	}

	public Short getPaymentRule() {
		return paymentRule;
	}

	public void setPaymentRule(Short paymentRule) {
		this.paymentRule = paymentRule;
	}

	public Short getIsDel() {
		return isDel;
	}

	public void setIsDel(Short isDel) {
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
