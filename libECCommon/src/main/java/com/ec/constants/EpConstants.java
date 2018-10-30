package com.ec.constants;

public class EpConstants {
	
	public static int EP_AC_TYPE = 14;
	public static int EP_DC_TYPE = 5;
	
	public static int EP_TYPE_LD =0; //落地式
	public static int EP_TYPE_BG =1; //壁挂式
	public static int EP_TYPE_LG =2; //拉杆式
	public static int EP_TYPE_BX =3; //便携式
	
	public static int EP_DISCONNECT=0;
	public static int EP_CONNECT=1;
	//
	
	//1：取消预约 2：预约结束(完成) 3：续预约中 4：预约中 5:预约确认中 6：预约失败7:结算完成
	public static int BESPOKE_STATUS_CANCEL=1;
	public static int BESPOKE_STATUS_END=2;
	public static int BESPOKE_STATUS_REDO=3;
	public static int BESPOKE_STATUS_LOCK=4;
	public static int BESPOKE_STATUS_CONFIRM=5;
	public static int BESPOKE_STATUS_FAIL=6;
	//public static int BESPOKE_STATUS_COOLING=7;
	public static int BESPOKE_STATUS_FIN_UNSTAT=7;//完成未结算,用在后付费模式
	
	
	public static final int USER_ACTION_BESPOKE =1;
	public static final int USER_ACTION_CANCEL_BESPOKE =2;
	public static final int USER_ACTION_START_CHARGE =3;
	public static final int USER_ACTION_STTOP_CHARGE =4;
	
	public static final int EP_STATUS_NORMAL =0;
	public static final int EP_STATUS_IN_UPDATE= 1;
	
	/** 发起充电方式 */
	public static short CHARGE_TYPE_QRCODE = 1;//二维码充电
	public static short CHARGE_TYPE_ACCOUNT = 2;//账号充电
	public static short CHARGE_TYPE_CARD = 3;//卡充电
	
	public static final short END_BESPOKE_CANCEL =1;
	public static final short END_BESPOKE_EXPIRE_TIME  =2;
	public static final short END_BESPOKE_CHARGE =3;
	

	/** 枪预约冷却时间*/
		
	
	public static final short MIN_BESP_TIME_UINT  = 1800;//单位秒,预约计价基数,不足30分钟按30分钟算
	
	//public static final short MIN_BESP_TIME_UINT  = 300;//单位秒,预约计价基数,不足30分钟按30分钟算
	
	public static final short FREE_BESP_TIME  = 300;//单位秒,预约基数内的免费时间,用该时间忽略客户端的时间差和网络通讯时间差
	
	public static final short BESP_COOLING_TIME =300;//单位分钟，在用户取消预约后，给予用户一定的冷却时间，保证用在到达充电现场，取消预约后不会被其它用户预约和充电

	public static final short SENDING_CHARGE_TIMEOUT = 60;//单位秒，在APP发送了充电命令后，等待桩回复时间 

	public static final short SENDING_BESPOKE_TIMEOUT = 60;//单位秒，在APP发送了预约命令后，等待桩回复时间 
	
	public static final short EP_CONNECT_TIMEOUT = 30; //单位秒,已经初始化成功的电桩超时
	public static final short EP_NOINIT_CONNECT_TIMEOUT = 10; //单位秒，连接后没有初始化
	
	public static final short EP_UPDATE_CONNECT_TIMEOUT = 140; //单位秒
	
	/**通讯连接类型***/
	public static final short COMM_MODE_OF_DISCRETE_POLE = 1;
	
	public static final short COMM_MODE_CENTRATOR = 2;
	
	public static final short U_CHARGEINFO_INTERVAL = 180;//三分钟
	
	public static final short U_PHONE_INTERVAL = 30;//30秒钟
	

	
	public static final String ELECTRICPILE="1";//1：充电桩/充电树     
	public static final String POWERSTATION="2";// 2：电站
	public static final String BYCILEPILE="3";//电动自行车
	
	
	public static final int SENDTOMONITOR_IDLE_TIME = 600; //发送给监控系统的时间 ，秒
	public static final int SENDTOMONITOR_CHARGE_TIME = 180; //发送给监控系统的时间 ，秒
	
	public static final int CARD_NORMAL =10;//爱充普通公共储蓄卡
	public static final int CARD_NORMAL_PRIVATE =11;//爱充普通专属储蓄卡
	public static final int CARD_CO_CREDIT =12;//爱充企业信用卡
	public static final int CARD_THIRD_NORMAL =13;//爱充合作公共储蓄卡
	public static final int CARD_THIRD_PRIVATE =14;//爱充合作专属储蓄卡
	
	public static final int E_CARD_AUTH_ERROR_UNCONNECT =1;
	public static final int E_CARD_AUTH_TIMEOUT =2;
	public static final int E_CARD_NOT_FIND_EP =3;
	public static final int E_CARD_AUTH_FAIL =4;
	public static final int E_CARD_NO_RIGHT =5;
	public static final int E_CARD_INVALID =6;
	public static final int E_CARD_LOSS =7;
	public static final int E_CARD_NOT_BIND_USER =8;
	public static final int E_CARD_AUTH_ERROR_PW =9;
	
	public static final int P_M_POSTPAID =2;//后付费
	public static final int P_M_FIRST =1; //先付费
	
	public static final int IDENTYCODE_INVALID_TIME1 = 8*60; //身份识别码判断失效时间
	
	public static final int IDENTYCODE_INVALID_TIME2 = 16*60; //身份识别码判断失效时间
	
	public static final int MAX_CHARGE_COST = 100000;//500元
	public static final int MAX_CHARGE_AMT = 50000;//500元
	public static final int MAX_CHARGE_SERVICE_AMT = 50000;//500元
	public static final int MAX_CHARGE_METER_NUM = 900000;//900度
	
	public static final int EP_STATUS_ONLINE=15;//上线
	public static final int EP_STATUS_OFFLINE=10;//专属
	
	public static final String CITYCODE_HANGZHOU="330100"; //杭州城市代码
	
	//账号长度
	public static final int LEN_ACCOUNT = 11;
	//账号长度
	public static final int LEN_BIG_ACCOUNT = 12;

	/** 交易途径 */
	public static short CHARGE_TYPE_NORMAL_APP = 1;//爱充普通APP
	public static short CHARGE_TYPE_GOVERN_APP = 2;//爱充政企APP
	public static short CHARGE_TYPE_WX_PROG = 3;//微信小程序
	public static short CHARGE_TYPE_ALI_PROG = 4;//支付宝小程序
	public static short CHARGE_TYPE_NORMAL_CARD = 5;//储值充电卡
	public static short CHARGE_TYPE_CREDIT_CARD = 6;//信用充电卡
	public static short CHARGE_TYPE_THIRD_APP = 7;//第三方APP
	public static short CHARGE_TYPE_VIN = 8;//车辆识别码（即插即充）
	public static short CHARGE_TYPE_THIRD_CARD = 9;//第三方卡
}
