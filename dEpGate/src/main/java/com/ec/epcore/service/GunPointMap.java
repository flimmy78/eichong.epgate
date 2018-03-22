package com.ec.epcore.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ec.net.proto.SingleInfo;

public class GunPointMap {
	private String epCode;
	private int epGunNo;
	private Map<Integer, SingleInfo> pointMap; 
	
	public GunPointMap(String epCode,int epGunNo)
	{
		this.epCode = epCode;
		this.epGunNo = epGunNo;
		pointMap = new ConcurrentHashMap<Integer,SingleInfo>();	
	}
	
	public String getEpCode() {
		return epCode;
	}
	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}
	public int getEpGunNo() {
		return epGunNo;
	}
	public void setEpGunNo(int epGunNo) {
		this.epGunNo = epGunNo;
	}
	public Map<Integer, SingleInfo> getPointMap() {
		return pointMap;
	}
}
