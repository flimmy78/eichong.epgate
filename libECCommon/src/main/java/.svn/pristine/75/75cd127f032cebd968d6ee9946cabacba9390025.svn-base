package com.ec.net.proto;

public class Iec104Constant {
	
	public static final short  WM_104_CD_MIN_LENGTH=6;
	
	
	//--------------------传送原因--------------------------------------------------------
	
	//在控制方向
	public static final byte CAUSE_INIT            =4;		//初始化，启动链路确认后由子站发
	public static final byte CAUSE_REQ	           =5;		//请求
	public static final byte CAUSE_ACT	           =6;		//激活  总招、对时 主站发送
	public static final byte CAUSE_STOPACT	       =8;		//停止激活  主站在总招过程中可以停止
	//在监视方向
	public static final byte CAUSE_BACKGROUP_SCAN  =2;      //背景扫描
	public static final byte CAUSE_SPONT           =3;      //自发  变数据上送
	public static final byte CAUSE_ACT_CON	       =7;		//激活确认  子站回复主站激活
	public static final byte CAUSE_STOPACT_CON	   =9;		//停止激活确认 子站回复主站停止
	public static final byte CAUSE_ACT_TERM		   =10;		//激活终止  一次总招完成后，子站返回
	public static final byte CAUSE_RESP_CALLALL	   =20;		//响应总召唤  全遥信、全遥测上送
	public static final byte CAUSE_UNKOWN_TYPE	   =44;		//未知的类型标识
	public static final byte CAUSE_UNKOWN_CAUSE	   =45;		//未知的传送原因
	public static final byte CAUSE_UNKOWN_APDUADDR =46;		//未知的应用服务数据单元公共地址
	public static final byte CAUSE_UNKOWN_DATAADDR =47;		//未知的信息体地址
	
	
	public static final byte   WM_104_CD_HEAD_FLAG=    0x68;
	public static final byte   WM_104_CD_STARTDT= 0x07;
	public static final byte   WM_104_CD_STARTDT_CONFIRM= 0x0b;
	public static final byte   WM_104_CD_STOPDT = 0x10;

	public static final byte   WM_104_CD_TESTFR = 0x43;
	public static final short  WM_104_CD_TESTFR_CONFIRM = 0x83;
	
	public static final short  WM_104_CD_QCC_POSITION = 15;
	public static final short  WM_104_CD_TIME_POSITION= 15;



	public static final short  WM_104_CD_REASONBYTE= 2;
	public static final short  WM_104_CD_ASDUADDRESSBYTE=     2;
	public static final short  WM_104_CD_INFORMATIONBYTE=     3;


	/* Location Which Bytes In Receive Buffer  */
	public static final short  NS_STARTREASON =               8;
	public static final short  NS_STARTASDUADDRESS=           10; 
	public static final short  NS_STARTINFORMATION =          12;
	public static final short  NS_STARTQUALIFIER  =           15;
	public static final short  WM_104_CD_GROUP_BYTE =         15 ;


	public static final short  WM_104_CD_GROUP_BEGIN =        21;
	public static final short  WM_104_CD_GROUP_END =          36 ;



	public static final short  WM_104_CD_YX_START_OBJECT=     0x01;
	public static final short  WM_104_CD_YC_START_OBJECT=     0x701;
	public static final short  WM_104_CD_YCYX_OBJECT_INDEX  = 0x80;

	public static final short  WM_104_CD_SENDBUFFER =         4000;
	public static final short  WM_104_MAX_MSG_LEN =         256;
	public static final short  WM_104_CD_RECEIVEBUFFER =      256;
	public static final short  NS_CONNECTED    =              0x02;
	public static final short  NS_DISCONNECTED  =             0x20;
	public static final short  WM_104_CD_PERMIT_TIME =        30;


	public static final short  WM_104_CD_MAX_K  =             12;
	public static final short  WM_104_CD_MAX_W =              8;
	public static final short  WM_104_CD_MAX_GROUP=           30;

	public static final short  WM_104_CD_MAX_YC  =            512;
	public static final short  WM_104_CD_CHANGE_YC_CYCLE=     16;
	public static final short  WM_104_CD_ONCE_YC_COUNT=       32;
	
	public static final byte INITIAL_BYTES_TO_STRIP =3;
	
	public static final short IEC_MSG_LEN_FIELD =2;
	
	public static final short IEC_104_T0= 20;
	public static final short IEC_104_T1= 15;
	public static final short IEC_104_T2= 10;
	public static final short IEC_104_T3= 20;
	
	//���׮ҵ��Ա����
	public static final short   WM_CD_CMD_UP = 0x82;
	public static final short   WM_CD_CMD_DOWN=    0x85;
	public static final short   WMCD_CMD_MONTOR=    0x84;
	
	public static final byte M_SP_NA = 1;
	public static final byte M_DP_NA = 3;
	public static final byte M_ME_NB = 11;
	public static final byte M_IT_NA = 15;
	public static final byte M_RE_NA = (byte)0x82;
	public static final byte M_MD_NA = (byte)0x84;
	public static final byte M_JC_NA = (byte)0x86;
	
	public static final byte C_IC_NA = (byte)0x64;
	public static final byte C_CI_NA = (byte)0x65;
	public static final byte C_CS_NA = (byte)0x67;
	public static final byte C_SD_NA = (byte)0x85;
	

	//费率模型	
	public static final int M_CONSUME_MODEL_REQ= 1;
	//计费模型
	public static final int C_CONSUME_MODEL = 1;
	//费率模型应答	
	public static final int M_CONSUME_MODE_RET = 2;
	//
	public static final int C_BUSINESS_TIME = 3;
	//私有充电桩下发充电桩运营时间
	public static final int M_BUSINESS_TIME_RET  = 3;
	//费率模型(4位电价)	
	public static final int M_CONSUME_MODEL4_REQ= 4;
	//计费模型(4位电价)	
	public static final int C_CONSUME_MODEL4 = 4;
	//费率模型(新账户)
	public static final int M_CONSUME_MODEL6_REQ= 6;
	//计费模型(新账户)
	public static final int C_CONSUME_MODEL6 = 6;

	//无卡鉴权
	public static final int C_NOCARD_AUTH = 11;

	//无卡支付用户密码鉴权
	public static final int M_NOCARD_PW_AUTH = 11;
	//无卡支付验证码鉴权	
	public static final int M_NOCARD_YZM_AUTH = 12;

	//
	public static final int C_CARD_AUTH_RET = 11;

	//不圈存卡应答
	public static final int C_CARD_AUTH_RESP = 13;
	//不圈存卡鉴权
	public static final int M_CARD_AUTH = 13;
	
	//预约
	public static final int C_BESPOKE = 31;
	//预约锁定上行数据
	public static final int M_BESPOKE_RET = 31;

	public static final int C_BESPOKE_CONFIRM = 32;

	//取消预约
	public static final int C_CANCEL_BESPOKE = 33;

	//取消预约上行数据
	public static final int M_CANCEL_BESPOKE_RET = 33;


	//开始充电
	public static final int C_START_ELECTRICIZE = 41;

	//用户开始充电上行数据
	public static final int M_START_ELECTRICIZE_RET = 41;

	public static final int M_START_CHARGE_EVENT = 42;
	

	//开始充电事件上行数据	
	//public static final byte M_START_ELECTRICIZE_EVENT = 42;

	public static final int C_CHARGE_EVENT_CONFIRM = 42;

	public static final int C_STOP_ELECTRICIZE = 43;

	public static final int M_STOP_ELECTRICIZE_RET = 43;

	public static final int C_CARD_FRONZE_AMT = 44;

	public static final int M_CARD_FRONZE_AMT_RET = 44;
	

	//结束充电事件上行数据
	public static final int M_STOP_ELECTRICIZE_EVENT = 45;

	//充电消费记录上行数据
	public static final int M_CONSUME_RECORD = 46;

	public static final int C_CONSUME_RECORD_CONFIRM = 46;
	
	public static final int M_CONSUME_RECORD_WITH_VINCODE = 50;
	
	public static final int C_CONSUME_RECORD_VINCODE_CONFIRM = 50;

	public static final int M_CONSUME_RECORD_WITH_SOC = 52;

	public static final int C_CONSUME_RECORD_SOC_CONFIRM = 52;

	public static final int M_CONSUME_RECORD_WITH_RATE = 53;

	public static final int C_CONSUME_RECORD_RATE_CONFIRM = 53;

	public static final int M_DC_SELF_CHECK_FINISHED = 48;
		
	//用户余额告警消息
	public static final int M_BALANCE_WARNING = 47;

	//电桩附件控制管理	101~140	
	//电桩设备支持报告上行数据	31	101	
	public static final int M_EP_REPORT_DEVICE = 101;			
	//进场呼叫	27	102	
	public static final int C_NEAR_CALL_EP = 102;
				
	//降地锁	31	103	
	public static final int C_DROP_CARPLACE_LOCK = 103;
				
	//打开锁枪装置下行数据	28	104	打开枪锁装置应答上行数据.	33	104	
	public static final int C_OPEN_EPGUN_LOCK_EQUIP = 104;

	public static final int M_OPEN_EPGUN_LOCK_EQUIP = 104;
	
	//锁枪失败告警上行数据	30	105
	public static final int M_LOCK_GUN_FAIL_WARNING = 105;
	
	//电桩维护管理
	public static final int C_DEVICE_VERSION_REQ = 141;

	public static final int M_DEVICE_VERSION_RET = 141;

	public static final int C_FORCE_UPDATE_EP_HEX = 142;
	
	public static final int C_EP_HEX_FILE_SUMARY = 143;

	public static final int M_EP_HEX_FILE_SUMARY_REQ = 143;
	
	//
	public static final int C_EP_HEX_FILE_SECTION = 144;

	//<23> 充电桩文件下载请求
	public static final int M_EP_HEX_FILE_DOWN_REQ = 144;	


	public static final int M_HEX_FILE_UPDATE_RET = 145;

	public static final int C_UPDATE_IP = 146;
	
	//查询远端设备信号强度
	public static final int C_QUERY_COMM_SIGNAL = 147;
	
	public static final int M_COMM_SIGNAL_RET = 147;
	//集中器桩体配置
	public static final int C_CONCENTROTER_SET_EP= 148;
	//集中器桩体配置应答
	public static final int M_CONCENTROTER_SET_EP_RET = 148;
	//读取集中器桩体配置
	public static final int C_CONCENTROTER_GET_EP= 149;
	//读取集中器桩体配置应答
	public static final int M_CONCENTROTER_GET_EP_RET = 149;	
	//查询远端设备费率
	public static final int C_GET_CONSUME_MODEL= 150;
	//远端设备费率应答
	public static final int M_GET_CONSUME_MODEL_RET = 150;
	//查询远端flash ram
	public static final int C_GET_FLASH_RAM= 151;
	//查询远端flash ram应答报文
	public static final int M_GET_FLASH_RAM_RET = 151;
	//查询远端临时充电次数
	public static final int C_GET_TEMPCHARGE_NUM= 152;
	//查询远端临时充电次数应答报文
	public static final int M_GET_TEMPCHARGE_NUM_RET = 152;
	//设置远端临时充电次数
	public static final int C_SET_TEMPCHARGE_NUM = 153;
	//设置远端临时充电次数应答报文
	public static final int M_SET_TEMPCHARGE_NUM_RET = 153;
	/** 下发定时充电事件给电桩 */
	public static final int C_SET_EP_TIMINGCHARGE = 154;
	/** 电桩接收定时充电后返回值 */
	public static final int M_GET_EP_TIMINGCHARGE_RET = 154;
	//下发电桩工作参数配置
	public static final int C_SET_EP_WORK_ARG = 155;
	//下发电桩工作参数配置应答报文
	public static final int M_SET_EP_WORK_ARG_RET = 155;
    //下发查询电桩信息报文
    public static final int C_SET_EP_INFO = 156;
    //下发查询电桩信息报文应答
    public static final int M_SET_EP_INFO_RET = 156;
    //电桩离线报文上报
    public static final int M_GET_EP_OFFLINE_INFO_RET = 157;
	
	//电桩业务查询
	public static final int C_QUERY_CONSUME_RECORD = 162;
	//电桩业务应答
	public static final int M_QUERY_CONSUME_RECORD_RET = 162;

	//
	public static final int C_EP_STAT = 161;		

    //
	public static final int M_EP_STAT_RET = 161;
	
	public static final int C_EP_IDENTYCODE = 49;
	
	public static final int M_EP_IDENTYCODE = 49;
}
