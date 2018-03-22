package com.ormcore.model;

import java.io.Serializable;
             
public class TblEquipmentRepair  implements Serializable {
	private java.lang.Integer id;//id
	private java.lang.Integer warrantyTypeId;//保修类型
	private java.lang.String content;//保修内容
	private java.lang.Integer userId;//用户ID
	private java.lang.Integer warrantyStatus;//1：未处理 2 处理中 3：已处理 
	private java.lang.Integer status;//0：显示 -1已删除
	private java.lang.Integer epid; //电桩、电站id
	private java.lang.Integer device_type;//电桩、电站

	
	public TblEquipmentRepair()
	{
		warrantyTypeId=0;
		content="";
		userId=0;
		warrantyStatus=1;
		status=0;
		epid=1;
		device_type=1;
		id=0;
	}
	
	
	public java.lang.Integer getId() {
		return id;
	}


	public void setId(java.lang.Integer id) {
		this.id = id;
	}


	public java.lang.Integer getWarrantyTypeId() {
		return warrantyTypeId;
	}
	public void setWarrantyTypeId(java.lang.Integer warrantyTypeId) {
		this.warrantyTypeId = warrantyTypeId;
	}
	public java.lang.String getContent() {
		return content;
	}
	public void setContent(java.lang.String content) {
		this.content = content;
	}
	public java.lang.Integer getUserId() {
		return userId;
	}
	public void setUserId(java.lang.Integer userId) {
		this.userId = userId;
	}
	public java.lang.Integer getWarrantyStatus() {
		return warrantyStatus;
	}
	public void setWarrantyStatus(java.lang.Integer warrantyStatus) {
		this.warrantyStatus = warrantyStatus;
	}
	public java.lang.Integer getStatus() {
		return status;
	}
	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	public java.lang.Integer getEpid() {
		return epid;
	}
	public void setEpid(java.lang.Integer epid) {
		this.epid = epid;
	}
	public java.lang.Integer getDevice_type() {
		return device_type;
	}
	public void setDevice_type(java.lang.Integer device_type) {
		this.device_type = device_type;
	}
	
	
}
