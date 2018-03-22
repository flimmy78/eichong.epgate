package com.ormcore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TblBespoke implements Serializable {
	
	
	private Long id;
	private Integer ordertype;//订单类型
	private BigDecimal amt;//实际金额
	private BigDecimal occurFrozenAmt;
	private Integer userid; 
	private Integer status;
	
	private Date beginTime;
	private Date endTime;
	private Date realityTime;
	private Date updateTime;
	
	private BigDecimal price; //预约单价
	
	private String bespokeTime;
	
	private Integer pkEpId;
	private Integer pkEpGunNo;
	
	
	private Integer userId;
	private String bespNo;
	private String partnerIdentiy;
	private String bespokeTimes;
	private String bespokeMark;
	private int userOrigin;
	private int payMode;
	private int userOrgNo;
	
   
	public TblBespoke()
	{
		price = new BigDecimal(0.0);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(Integer ordertype) {
		this.ordertype = ordertype;
	}
	public BigDecimal getAmt() {
		return amt;
	}
	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getRealityTime() {
		return realityTime;
	}
	public void setRealityTime(Date realityTime) {
		this.realityTime = realityTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getBespokeTime() {
		return bespokeTime;
	}
	public void setBespokeTime(String bespokeTime) {
		this.bespokeTime = bespokeTime;
	}
	
	public Integer getPkEpId() {
		return pkEpId;
	}
	public void setPkEpId(Integer pkEpId) {
		this.pkEpId = pkEpId;
	}
	public Integer getPkEpGunNo() {
		return pkEpGunNo;
	}
	public void setPkEpGunNo(Integer pkEpGunNo) {
		this.pkEpGunNo = pkEpGunNo;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getBespNo() {
		return bespNo;
	}
	public void setBespNo(String bespNo) {
		this.bespNo = bespNo;
	}
	
	public String getBespokeTimes() {
		return bespokeTimes;
	}
	public void setBespokeTimes(String bespokeTimes) {
		this.bespokeTimes = bespokeTimes;
	}
	public String getBespokeMark() {
		return bespokeMark;
	}
	public void setBespokeMark(String bespokeMark) {
		this.bespokeMark = bespokeMark;
	}

	public BigDecimal getOccurFrozenAmt() {
		return occurFrozenAmt;
	}

	public void setOccurFrozenAmt(BigDecimal occurFrozenAmt) {
		this.occurFrozenAmt = occurFrozenAmt;
	}

	public int getUserOrigin() {
		return userOrigin;
	}

	public void setUserOrigin(int userOrigin) {
		this.userOrigin = userOrigin;
	}

	public String getPartnerIdentiy() {
		return partnerIdentiy;
	}

	public void setPartnerIdentiy(String partnerIdentiy) {
		this.partnerIdentiy = partnerIdentiy;
	}

	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}

	public int getUserOrgNo() {
		return userOrgNo;
	}

	public void setUserOrgNo(int userOrgNo) {
		this.userOrgNo = userOrgNo;
	}
	
}
