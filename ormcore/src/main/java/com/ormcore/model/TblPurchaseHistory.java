package com.ormcore.model;

import java.math.BigDecimal;
import java.util.Date;

public class TblPurchaseHistory {
	
	private Integer pk_PurchaseHistory;
	private Integer puHi_Type;
	private Date puHi_PurchaseHistoryTime;
	private BigDecimal puHi_Monetary;
	private String puHi_ConsumerRemark;
	private String puHi_PurchaseContent;
	private Date puHi_Createdate;
	
	private Integer puHi_UserOrigin;
	private String puHi_EpCode;
	private String puHi_SerialNo;
	private String puHi_BespokeNo;
    private Integer account_id;
	
	public TblPurchaseHistory(Date dt,BigDecimal cost,String consumerRemark,int type,int userId,
			int userOrigin,String content,String epCode,String serialNo,String bespokeNo,int accountId)
	{
		puHi_PurchaseHistoryTime = dt;
		puHi_Monetary = cost;
		puHi_ConsumerRemark = consumerRemark;
		puHi_Type = type;
		puHi_UserId = userId;
		puHi_PurchaseContent = content;
		
		puHi_UserOrigin = userOrigin;
		puHi_EpCode = epCode;
		puHi_SerialNo= serialNo;
		puHi_BespokeNo = bespokeNo;
        account_id = accountId;
	}
	
	public Date getPuHi_Createdate() {
		return puHi_Createdate;
	}
	public void setPuHi_Createdate(Date puHi_Createdate) {
		this.puHi_Createdate = puHi_Createdate;
	}
	private Integer puHi_UserId;
	public Integer getPk_PurchaseHistory() {
		return pk_PurchaseHistory;
	}
	public void setPk_PurchaseHistory(Integer pk_PurchaseHistory) {
		this.pk_PurchaseHistory = pk_PurchaseHistory;
	}
	public Integer getPuHi_Type() {
		return puHi_Type;
	}
	public void setPuHi_Type(Integer puHi_Type) {
		this.puHi_Type = puHi_Type;
	}
	public Date getPuHi_PurchaseHistoryTime() {
		return puHi_PurchaseHistoryTime;
	}
	            
	public void setPuHi_PurchaseHistoryTime(Date puHi_PurchaseHistoryTime) {
		this.puHi_PurchaseHistoryTime = puHi_PurchaseHistoryTime;
	}
	public BigDecimal getPuHi_Monetary() {
		return puHi_Monetary;
	}
	public void setPuHi_Monetary(BigDecimal puHi_Monetary) {
		this.puHi_Monetary = puHi_Monetary;
	}
	public String getPuHi_ConsumerRemark() {
		return puHi_ConsumerRemark;
	}
	public void setPuHi_ConsumerRemark(String puHi_ConsumerRemark) {
		this.puHi_ConsumerRemark = puHi_ConsumerRemark;
	}
	public String getPuHi_PurchaseContent() {
		return puHi_PurchaseContent;
	}
	public void setPuHi_PurchaseContent(String puHi_PurchaseContent) {
		this.puHi_PurchaseContent = puHi_PurchaseContent;
	}
	public Integer getPuHi_UserId() {
		return puHi_UserId;
	}
	public void setPuHi_UserId(Integer puHi_UserId) {
		this.puHi_UserId = puHi_UserId;
	}

	public Integer getPuHi_UserOrigin() {
		return puHi_UserOrigin;
	}

	public void setPuHi_UserOrigin(Integer puHi_UserOrigin) {
		this.puHi_UserOrigin = puHi_UserOrigin;
	}

	public String getPuHi_EpCode() {
		return puHi_EpCode;
	}

	public void setPuHi_EpCode(String puHi_EpCode) {
		this.puHi_EpCode = puHi_EpCode;
	}

	public String getPuHi_SerialNo() {
		return puHi_SerialNo;
	}

	public void setPuHi_SerialNo(String puHi_SerialNo) {
		this.puHi_SerialNo = puHi_SerialNo;
	}

	public String getPuHi_BespokeNo() {
		return puHi_BespokeNo;
	}

	public void setPuHi_BespokeNo(String puHi_BespokeNo) {
		this.puHi_BespokeNo = puHi_BespokeNo;
	}


    public Integer getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }
}
