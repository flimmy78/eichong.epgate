package com.ec.logs;

/**
 * 日志记录常量表
 */
public class LogConstants {
	public static final String PROJ_DEPGATE = "depgate";
	public static final String PROJ_DPHONEGATE = "dphonegate";
	public static final String PROJ_USRLAYER = "usrlayer";
	public static final String PROJ_LIB_COOPERATE = "libcooperate";
	public static final String PROJ_LIB_ECCOMMON = "libeccommon";

	/**手机初始化*/
	public static final String FUNC_PHONE_INIT = "phoneinit";
	/**手机初始化*/
	public static final String FUNC_PHONE_DISCONNECT = "phonedisconnect";
	/**卡鉴权*/
	public static final String FUNC_CARD_AUTH = "cardauth";
	/**卡鉴权应答*/
	public static final String FUNC_CARD_AUTH_RESP = "cardauthresp";
	/**冻结金额*/
	public static final String FUNC_CARD_FRONZE_AMT = "cardfronzeamt";
	/**接收充电*/
	public static final String FUNC_RECV_CHARGE = "recvcharge";
	/**开始充电*/
	public static final String FUNC_START_CHARGE = "startcharge";
	/**停止充电应答*/
	public static final String FUNC_ONSTARTCHARGE = "onstartcharge";
	/**充电事件*/
	public static final String FUNC_ONCHARGEEVENT = "onchargeevent";
	/**接受停止充电*/
	public static final String FUNC_RECV_STOP_CHARGE = "recvstopcharge";
	/**停止充电*/
	public static final String FUNC_STOP_CHARGE = "stopcharge";
	/**停止充电应答*/
	public static final String FUNC_ONSTOPCHARGE = "onstopcharge";
	/**消费记录*/
	public static final String FUNC_CONSUME_RECORD = "consumerecord";
	/**充电订单推送*/
	public static final String FUNC_ONCHARGEORDER = "onchargeorder";
	/**实时数据推送*/
	public static final String FUNC_ONREALDATA = "onrealdata";
	/**
	 * 实时数据推送
	 */
	public static final String FUNC_ONREALDATA_4HTML = "onrealdata4html";
	/** 通用实时数据查询*/
	public static final String FUNC_4COMMONREALDATA = "4commonrealdata";
	/**电桩状态推送*/
	public static final String FUNC_ONEPSTATUSCHANGE = "onepstatuschange";
	/**电桩联网状态推送*/
	public static final String FUNC_ONEPNETSTATUSCHANGE = "onepnetstatuschange";
	
	/**订单查询*/
	public static final String FUNC_QUERY_ORDER = "queryorder";
	/**枪与车连接状态*/
	public static final String FUNC_GUNLINK_STATUS = "gunlinkstatus";
	/**枪工作状态*/
	public static final String FUNC_GUNWORK_STATUS = "gunworkstatus";

	public static final String FUNC_CHANNEL = "[epchannel]";
	/**版本升级*/
	public static final String FUNC_UPGRADE = "[upgrade]";
	/**结束充电*/
	public static final String FUNC_END_CHARGE = "[endcharge]";
	// 状态变化推送至html
	public static final String FUNC_GUNWORK_STATUS_CHANGE_REALDATA = "gunworkstatuschangerealdata";

	public static final String FUNC_PREVENT_REPEAT_CONSUME_RECORD ="preventrepeatconsumerecord";
	public static final String FUNC_PREVENT_REPEAT_CARDFROZEAMT = "preventrepeatcardfrozeamt";
	public static final String FUNC_PREVENT_REPEAT_CHARGEEVENT = "preventrepeatchargeevent";
}
