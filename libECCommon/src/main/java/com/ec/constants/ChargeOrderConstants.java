package com.ec.constants;

public class ChargeOrderConstants {
	
	/** 订单状态 */
	public static int ORDER_STATUS_NO = -1;// 无订单
	public static int ORDER_STATUS_WAIT = 1;// 待支付
	public static int ORDER_STATUS_SUCCESS = 2;// 支付成功
	public static int ORDER_STATUS_DONE = 3;// 操作完成
	public static int ORDER_STATUS_DELETE = 0;// 订单删除
	public static int ORDER_STATUS_EXCEPTION = 4;// 订单数据异常
	public static int ORDER_PAUSE_STAT = 5;//
}
