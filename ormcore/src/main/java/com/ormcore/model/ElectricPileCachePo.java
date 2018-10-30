package com.ormcore.model;

import java.util.List;

//缓存ep 及相关公司权限
public class ElectricPileCachePo {

	private Integer pkEpId; //
	private String code; //
	private String name;
	private int currentType; //
	private int rateid; //
	private int gunNum;
	private int concentratorId;//集中器id
	private int stationIndex;//集中器内序号
	private int gateid; //
	private String address;
	private Integer typeSpanId; // 产品类型Id
	private Integer company_number;
	private int state;//电桩状态
	private int deleteFlag;//删除标识
	private String ownCityCode;//
	private String ownProvinceCode;//省code
	private List<CompanyRela> companyRelaList;

	public Integer getCompany_number() {
		return company_number;
	}

	public void setCompany_number(final Integer company_number) {
		this.company_number = company_number;
	}

	public Integer getPkEpId() {
		return pkEpId;
	}

	public void setPkEpId(final Integer pkEpId) {
		this.pkEpId = pkEpId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getCurrentType() {
		return currentType;
	}

	public void setCurrentType(final int currentType) {
		this.currentType = currentType;
	}

	public int getRateid() {
		return rateid;
	}

	public void setRateid(final int rateid) {
		this.rateid = rateid;
	}

	public int getGunNum() {
		return gunNum;
	}

	public void setGunNum(final int gunNum) {
		this.gunNum = gunNum;
	}

	public int getConcentratorId() {
		return concentratorId;
	}

	public void setConcentratorId(final int concentratorId) {
		this.concentratorId = concentratorId;
	}

	public int getStationIndex() {
		return stationIndex;
	}

	public void setStationIndex(final int stationIndex) {
		this.stationIndex = stationIndex;
	}

	public int getGateid() {
		return gateid;
	}

	public void setGateid(final int gateid) {
		this.gateid = gateid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public Integer getTypeSpanId() {
		return typeSpanId;
	}

	public void setTypeSpanId(final Integer typeSpanId) {
		this.typeSpanId = typeSpanId;
	}

	public int getState() {
		return state;
	}

	public void setState(final int state) {
		this.state = state;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(final int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getOwnCityCode() {
		return ownCityCode;
	}

	public void setOwnCityCode(final String ownCityCode) {
		this.ownCityCode = ownCityCode;
	}

	public String getOwnProvinceCode() {
		return ownProvinceCode;
	}

	public void setOwnProvinceCode(final String ownProvinceCode) {
		this.ownProvinceCode = ownProvinceCode;
	}

	public List<CompanyRela> getCompanyRelaList() {
		return companyRelaList;
	}

	public void setCompanyRelaList(final List<CompanyRela> companyRelaList) {
		this.companyRelaList = companyRelaList;
	}

	@Override
	public String toString() {
		return "ElectricPileCachePo{" +
				"pkEpId=" + pkEpId +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", currentType=" + currentType +
				", rateid=" + rateid +
				", gunNum=" + gunNum +
				", concentratorId=" + concentratorId +
				", stationIndex=" + stationIndex +
				", gateid=" + gateid +
				", address='" + address + '\'' +
				", typeSpanId=" + typeSpanId +
				", company_number=" + company_number +
				", state=" + state +
				", deleteFlag=" + deleteFlag +
				", ownCityCode='" + ownCityCode + '\'' +
				", ownProvinceCode='" + ownProvinceCode + '\'' +
				", companyRelaList=" + companyRelaList.size() +
				'}';
	}
}


