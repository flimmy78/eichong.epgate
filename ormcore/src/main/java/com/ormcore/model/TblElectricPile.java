package com.ormcore.model;

public class TblElectricPile {
	
	private Integer pkEpId; 
	private String  epCode; 
	private String  epName; 
	private Integer  epGunNum; 
	private Integer currentType;
	private Integer comm_status; 
	private Integer rateid;
	private Integer gateid; 
	private Integer stationId;
	private Integer epType;
	private Integer company_number;
	
	private String  elPiOwnCityCode;//鎵�灞炲湴鍖哄尯鍘縞ode
    private String  elPiOwnProvinceCode;//省code
	
	private java.util.Date elpiUpdatedate; // 淇敼鏃堕棿
	
	private java.lang.Integer elpiChargingmode; // 鐢垫々鍏呯數鏂瑰紡锛岄厤缃弬鏁板唴瀹圭殑ID
	// 锛堢洿娴佸厖鐢垫々锛屼氦娴佸厖鐢垫々锛�
	
	private java.lang.Integer elpiPowerinterface; // 鐢垫々鎺ュ彛鏂瑰紡锛岄厤缃弬鏁板唴瀹圭殑ID锛堝浗鏍囥�佹鏍囥�佺編鏍囷級7鍥芥爣锛�19缇庢爣锛�20娆ф爣
	
	private java.lang.Integer elpiPoweruser; // 鐢垫々鐢ㄩ�旓紝閰嶇疆鍙傛暟鍐呭鐨処D锛堢數鍔ㄨ溅锛岀數鍔ㄨ嚜琛岃溅锛屽鍔熻兘锛堢數鍔ㄨ溅銆佺數鍔ㄨ嚜琛岃溅銆佹墜鏈篣SB锛夛級
	
	private java.lang.Integer elpiState; // 鐢垫々鐘舵�侊紙0-鑽夌 5-鎻愪氦瀹℃牳 3-宸查┏鍥� 10-绂荤嚎15-涓婄嚎锛�
	
	private String address;
	
	private java.lang.Integer  epTypeSpanId; 
	
	private java.lang.Integer deleteFlag;//删除标识
	
	public String getElPiOwnProvinceCode() {
		return elPiOwnProvinceCode;
	}
	public void setElPiOwnProvinceCode(String elPiOwnProvinceCode) {
		this.elPiOwnProvinceCode = elPiOwnProvinceCode;
	}
	
	
	public java.lang.Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(java.lang.Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public java.lang.Integer getEpTypeSpanId() {
		return epTypeSpanId;
	}
	public void setEpTypeSpanId(java.lang.Integer epTypeSpanId) {
		this.epTypeSpanId = epTypeSpanId;
	}
	
	public Integer getEpType() {
		return epType;
	}
	public void setEpType(Integer epType) {
		this.epType = epType;
	}
	
	public Integer getStationId() {
		return stationId;
	}
	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}
	public Integer getStationIndex() {
		return stationIndex;
	}
	public void setStationIndex(Integer stationIndex) {
		this.stationIndex = stationIndex;
	}
	private Integer stationIndex;
	public Integer getPkEpId() {
		return pkEpId;
	}
	public void setPkEpId(Integer pkEpId) {
		this.pkEpId = pkEpId;
	}
	public String getEpCode() {
		return epCode;
	}
	public Integer getCurrentType() {
		return currentType;
	}
	public void setCurrentType(Integer currentType) {
		this.currentType = currentType;
	}
	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}
	public String getEpName() {
		return epName;
	}
	public void setEpName(String epName) {
		this.epName = epName;
	}
	public Integer getEpGunNum() {
		return epGunNum;
	}
	public void setEpGunNum(Integer epGunNum) {
		this.epGunNum = epGunNum;
	}
	public Integer getComm_status() {
		return comm_status;
	}
	public void setComm_status(Integer comm_status) {
		this.comm_status = comm_status;
	}
	public Integer getRateid() {
		return rateid;
	}
	public void setRateid(Integer rateid) {
		this.rateid = rateid;
	}
	public Integer getGateid() {
		return gateid;
	}
	public void setGateid(Integer gateid) {
		this.gateid = gateid;
	}
	public String getElPiOwnCityCode() {
		return elPiOwnCityCode;
	}
	public void setElPiOwnCityCode(String elPiOwnCityCode) {
		this.elPiOwnCityCode = elPiOwnCityCode;
	}
	public java.util.Date getElpiUpdatedate() {
		return elpiUpdatedate;
	}
	public void setElpiUpdatedate(java.util.Date elpiUpdatedate) {
		this.elpiUpdatedate = elpiUpdatedate;
	}
	public java.lang.Integer getElpiChargingmode() {
		return elpiChargingmode;
	}
	public void setElpiChargingmode(java.lang.Integer elpiChargingmode) {
		this.elpiChargingmode = elpiChargingmode;
	}
	public java.lang.Integer getElpiPowerinterface() {
		return elpiPowerinterface;
	}
	public void setElpiPowerinterface(java.lang.Integer elpiPowerinterface) {
		this.elpiPowerinterface = elpiPowerinterface;
	}
	public java.lang.Integer getElpiPoweruser() {
		return elpiPoweruser;
	}
	public void setElpiPoweruser(java.lang.Integer elpiPoweruser) {
		this.elpiPoweruser = elpiPoweruser;
	}
	public java.lang.Integer getElpiState() {
		return elpiState;
	}
	public void setElpiState(java.lang.Integer elpiState) {
		this.elpiState = elpiState;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getCompany_number() {
		return company_number;
	}
	public void setCompany_number(Integer company_number) {
		this.company_number = company_number;
	}

	@Override
	public String toString() {
		return "TblElectricPile{" +
				"pkEpId=" + pkEpId +
				", epCode='" + epCode + '\'' +
				", epName='" + epName + '\'' +
				", epGunNum=" + epGunNum +
				", currentType=" + currentType +
				", comm_status=" + comm_status +
				", rateid=" + rateid +
				", gateid=" + gateid +
				", stationId=" + stationId +
				", epType=" + epType +
				", company_number=" + company_number +
				", elPiOwnCityCode='" + elPiOwnCityCode + '\'' +
				", elPiOwnProvinceCode='" + elPiOwnProvinceCode + '\'' +
				", elpiUpdatedate=" + elpiUpdatedate +
				", elpiChargingmode=" + elpiChargingmode +
				", elpiPowerinterface=" + elpiPowerinterface +
				", elpiPoweruser=" + elpiPoweruser +
				", elpiState=" + elpiState +
				", address='" + address + '\'' +
				", epTypeSpanId=" + epTypeSpanId +
				", deleteFlag=" + deleteFlag +
				", stationIndex=" + stationIndex +
				'}';
	}
}
