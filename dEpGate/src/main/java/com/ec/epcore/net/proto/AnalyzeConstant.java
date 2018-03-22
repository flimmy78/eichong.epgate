package com.ec.epcore.net.proto;

public class AnalyzeConstant{
	
	public static final short  ANALYZE_SENDBUFFER=256;
	
	public static final byte   HEAD_FLAG1 = 0x45;
	
	public static final byte   HEAD_FLAG2 = 0x43;
	

//	<2>	心跳
	public static final short D_HEART = 1;
//	<13>单位遥信	
	public static final short REAL_ONE_BIT_YX= 2;
//	<13>双位遥信	
	public static final short REAL_TWO_BIT_YX= 3;
//	<14>交流遥测	
	public static final short REAL_YC = 4;
//	<15>交流变长遥测	
	public static final short REAL_VAR_YC = 5;
	
	public static final int NET_KEEP_LIVE_INTERVAL = 60;//网络连接保持心跳的活动报文周期
	public static final int RE_CONNECT_INTERVAL = 10;//重连时间间隔周期
	public static final int HEART_INTERVAL = 30;//监控客户端发送心跳周期

		
}
