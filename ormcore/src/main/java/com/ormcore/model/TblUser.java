package com.ormcore.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户汇总表
 */
public class TblUser implements Serializable {

	private static final long serialVersionUID = -4085821372336519331L;
	/** 用户ID**/
	private Integer userId;

	/** 资金账户号**/
	private String userAccount;

	/** 支付密码**/
	private String userPassword;

	/** '用户类别1：超级管理员 2：系统管理员  3:渠道用户  6:普通用户 7.卡默认用户 8.业务管理员'**/
	private Short userLeval;

	/** 用户状态 1:正常 2:冻结  3:删除**/
	private Short userStatus;

	/** 身份证号**/
	private String userIdcard;

	/** 活动区域：省**/
	private String provinceCode;

	/** '活动区域：市'**/
	private String cityCode;

	/** 活动区域：区域**/
	private String areaCode;

	/** 创建时间**/
	private java.util.Date gmtCreate;

	/** 修改时间**/
	private java.util.Date gmtModified;

	/** 创建人**/
	private String creator;

	/** 修改人**/
	private String modifier;

	/** '0.充充侠 1.用户自定义'**/
	private Short userHeadImg;

	/** 等级ID**/
	private Integer levelId;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Short getUserLeval() {
		return userLeval;
	}

	public void setUserLeval(Short userLeval) {
		this.userLeval = userLeval;
	}

	public Short getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Short userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserIdcard() {
		return userIdcard;
	}

	public void setUserIdcard(String userIdcard) {
		this.userIdcard = userIdcard;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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

	public Short getUserHeadImg() {
		return userHeadImg;
	}

	public void setUserHeadImg(Short userHeadImg) {
		this.userHeadImg = userHeadImg;
	}

	public Integer getLevelId() {
		return levelId;
	}

	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}
}
