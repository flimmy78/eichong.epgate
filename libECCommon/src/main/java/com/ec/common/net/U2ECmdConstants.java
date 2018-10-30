package com.ec.common.net;

public class U2ECmdConstants {

	public static final byte  HEAD_FLAG1 = 0x45;
	public static final byte  HEAD_FLAG2 = 0x43;
	
	public static final int EP_LOGIN= 101;//网关登录程序
	public static final int EP_ACK= 102;//ACK响应
	public static final int EP_HEART= 103;//心跳
	public static final int EP_ONLINE= 202;//电桩在线
	public static final int PHONE_ONLINE= 203;//手机在线
	
	public static final int PHONE_CONNECT_INIT= 1001;//手机连接初始化(带部分充电逻辑)
	
	public static final int EP_CHARGE= 1002;//1002	充电
	public static final int EP_CHARGE_EP_RESP= 1003;//充电命令应答
	public static final int EP_CHARGE_EVENT= 1004;//1003	充电事件
	
	public static final int EP_STOP_CHARGE= 1010;//1004	停止充电
	public static final int EP_STOP_CHARGE_EP_RESP= 1011;//电桩停止应答
	
	public static final int EP_REALINFO= 1200;//1005实时信息

	public static final int EP_REALINFO_4HTML = 1205;//非Html渠道发起的充电实时数据

	public static final int EP_CONSUME_RECODE= 1101;//1006	消费记录
	public static final int CCZC_QUERY_ORDER=1102;//查询订单信息
	
	public static final int EP_GUN_CAR_STATUS=1201;//枪连接状态
	public static final int EP_GUN_WORK_STATUS=1203;//枪工作状态

	public static final int EP_4COMMON_REALDATA=1204;//通用实时数据

	public static final int EP_GUN_STATUS_CHANGE_DATA = 1206;//发送状态变化数据给html 全国的

}
