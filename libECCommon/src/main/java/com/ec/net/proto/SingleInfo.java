package com.ec.net.proto;

public class SingleInfo {
	int address;
	//byte[] value;
	int intValue;//存遥信遥测值
	String strValue;
	int qdsDesc;
	public SingleInfo(int address,int intValue,String strValue,int qdsDesc)
	{
		this.address = address;
		//byte[] value;
		this.intValue = intValue;//存遥信遥测值
		this.strValue =strValue;
		this.qdsDesc = qdsDesc;
	}
	public SingleInfo()
	{
		
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public int getQdsDesc() {
		return qdsDesc;
	}
	public void setQdsDesc(int qdsDesc) {
		this.qdsDesc = qdsDesc;
	}
	
	
}
