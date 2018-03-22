package com.ec.usrcore.service;

import com.ec.usrcore.cache.ProtoVersionStat;

public class StatService {
	
	private static ProtoVersionStat phoneStat = new ProtoVersionStat();
	
	private static ProtoVersionStat epStat = new ProtoVersionStat();
	
	private static int chargeCount=0;

	public static void addCharge()
	{
		chargeCount+=1;
	}
	public static void subCharge()
	{
		chargeCount-=1;
	}
	public static int getChargeCount() {
		return chargeCount;
	}
	
	public static void regProtocolVersion(int type,int version,String user)
	{
		if(type==1)
			phoneStat.addVersion(version, user);
		else
			epStat.addVersion(version, user);
	}
	public static void unRegProtocolVersion(int type,int version)
	{
		if(type==1)
			phoneStat.offProtocol(version);
		else
			epStat.offProtocol(version);
	}
	
	public static String stat()
	{
		
		final StringBuilder sb = new StringBuilder();
    	
    	int epChargeCount = StatService.getChargeCount();
    	sb.append("正在充电的充电桩（枪口）总数：").append(epChargeCount).append(" \n");
    	
    	sb.append("\n手机协议版本:").append(phoneStat);
    	
    	sb.append("\n电桩协议版本:").append(epStat);
   
		return sb.toString();
	}
}
