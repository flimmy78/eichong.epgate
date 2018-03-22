package com.ec.constants;

public class TimingChargeConstants {
	
	/****** 定时充电下发给电桩状态   ******/
	/** 0：未下发定时数据 */
	public static final int ISSUED_TIMING_CHARGE_STATUS_UNSEND = 0;
	/** 1：已下发数据但未收到响应 */
	public static final int ISSUED_TIMING_CHARGE_STATUS_UNREC = 1;
	/** 2：下发定时成功 */
	public static final int ISSUED_TIMING_CHARGE_STATUS_SUCCESS = 2;
	/** 3：下发定时失败 */
	public static final int ISSUED_TIMING_CHARGE_STATUS_FAIL = 3;	
}
