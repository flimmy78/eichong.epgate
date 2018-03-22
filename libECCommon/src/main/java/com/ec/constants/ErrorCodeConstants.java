package com.ec.constants;

public class ErrorCodeConstants {
	
	/** 充电桩后台公共错误 */
	public static final int EP_UNCONNECTED = 6000;//电桩未连接
	
	public static final int EP_TIMEOUT = 6001;//充电桩未响应,超时
	
	public static final int INVALID_EP_CODE = 6100;//电桩编码无效
	
	public static final int INVALID_EP_GUN_NO = 6101;//电桩枪口编码无效
	
	public static final int INVALID_RATE = 6102;//电桩没有绑定费率
	
	 public static final int EP_UPDATE = 6104;//电桩正在升级，不能使用
	
	
	public static final int GUN_STOP = 6200;//充电枪被停用,不能使用，TODO
	
	public static final int EP_NOT_ONLINE = 6201;//电桩未上线
	
	public static final int EP_PRIVATE = 6202;//电桩私有
	
	public static final int EP_NOT_OPERATE = 6203;//电桩对合作方私有（该充电桩暂不开放）
   
    
    public static final int EP_PACK_ERROR = 6105;//报文无效
    
    //public static int EPE_GUN_OPERATING = 6106;//
    
    public static final int USED_GUN = 6300;//桩已经被别人使用
    public static final int EPE_IN_EP_OPER = 6301;//桩在操作中，比如设置，不能预约或充电
	
    
    
    public static final int INVALID_ACCOUNT_LEN = 6401;//用户长度无效
    
    public static final int ERROR_ACCOUNT_PW = 6402;//用户密码错误
    
    public static final int ERROR_PHONE_CRC_CODE = 6403;//校验码错误
    
    public static final int INVALID_ACCOUNT = 6404;//用户无效,不存在或者存在多个
    
    public static final int INVALID_ACCOUNT_STATUS = 6405;//用户状态无效
    
    public static final int USER_OPER_IN_OTHER_EP = 6406;//用户在其它桩上操作
    
  
  
	/** 预约错误*/
	public static final int INVALID_BUY_TIMES = 6500;// 参数买断时间小于最小值30分钟
	
	public static final int INVALID_REDO_FLAG = 6501;// 续约标识无效
	
	public static final int INVALID_BESP_NO_LEN = 6502;//预约编号长度无效
	
	
	
	
	public static final int SELF_HAVED_BESP = 6600;//自己已有预约
	
	public static final int EPE_OTHER_BESP = 6601;//已经有人预约
	
	public static final int NOT_SELF_REDO_BESP = 6602;//不是自己的预约不能续约
	
	//public static int NOT_HAVE_BESP = 6603;//没有预约
	
	public static final int CAN_NOT_BESP_IN_ELE= 6604;//充电中,不能预约
	
	public static final int CAN_NOT_BESP_GUN_CONNECTED= 6605;//充电枪已经连接，不能预约
	
	public static final int BESP_NO_NOT_EXIST= 6606;//预约编号不存在
	
	public static final int BESP_TO_MAX_TIME= 6607;//预约已经到6个小时，不能再续约
	
    public static final int CAN_NOT_BESP_IN_BESP_COOLING= 6608;//预约5分钟冷却中,不能在其他桩预约
	
	public static final int CAN_NOT_CHARGE_IN_BESP_COOLING= 6609;//预约5分钟冷却中,不能在其他桩充电
	public static final int CAN_NOT_STOP_BESPOKE= 6610;//没有预约，不能停止TODO
	
	public static final int CANNOT_FLASH_LED_WITHOUT_BESP= 6620;  //没有预约，不能闪LED灯
	
	public static final int CANNOT_REPEAT_FLASH_LED_WITHOUT_BESP= 6621;//正在闪LED,不能重复闪
	
	public static final int CANNOT_STOP_FLASH_LED_WITHOUT_BESP= 6622;  //没有闪LED，不能停止LED灯
	
	public static final int CANNOT_SOUNDING_WITHOUT_BESP= 6630;  //没有预约，不能呼叫电桩
	
	public static final int CANNOT_REPEAT_SOUNDING_WITHOUT_BESP= 6631;  //没有预约，不能重复呼叫电桩
	
	public static final int CANNOT_STOP_SOUNDING_WITHOUT_BESP= 6632;  //没有预约，不能停止呼叫
	
	public static final int CANNOT_OTHER_OPER= 6633;//预约中，不能进行除(续约，取消预约，充电)等操作
	
	

	/** 充电相关错误*/
	public static int EPE_CHARGE_STYLE= 6700;//充电方式错误
	
	public static int EPE_HAVE_NOT_PAY_ORDER= 6701;//有未支付的订单
	
	public static int EPE_NO_ENOUGH_MONEY = 1002;//用户金额不足,不能充电
	
	public static int EPE_GUN_NOT_LINKED = 6702;//充电枪没插好,不能充电
	
	public static int EPE_GUN_LID_NOT_COVER = 6703;//充电枪盖没盖好,不能充电
	
	public static int EPE_NOT_COMM_WITH_CAR = 6704;//未与车通讯，不能充电
	
	public static int EPE_GUN_FAULT1 = 6705;//故障，不能充电
	
	public static int EPE_GUN_LID_OPEND = 6706;//枪盖已经打开,不能重复打开

	//
	public static int EPE_REPEAT_CHARGE = 6800;	//已经在充电,不能重复充电
	
	public static int EPE_OTHER_CHARGING = 6801;//其他人已经在充电,不能充电
	
	public static int EPE_NOT_ENABLE_ELECTRIC_BY_BESPOKEED = 6802;//	没有充电,不能停止充电	
	
	public static int EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING = 6803;//	没有充电,不能停止充电
	
	public static int EPE_GUN_FAULT = 6804;//故障，不能充电
	
	public static int EPE_STOP_CHARGE = 6900;	//用户主动结束等待插枪状态

	public static String getErrorDesc(int errorCode)
	{
		String desc="";
		switch(errorCode)
		{
		case 0:
			desc="ok";
			break;
		case 1:
			desc="请求参数错误";
			break;
		case 2:
			desc="未注册";
			break;
		case 3:
			desc="签名失败";
			break;
		case 4:
			desc="已经注册";
			break;
		case 6000:
			desc="电桩未连接";
			break;
			
		case 6001:
			desc="充电桩未响应,超时";
			break;
			
		case 6100:
			desc="电桩编码无效";
			
		case 6101:
			desc="电桩枪口编码无效";
			break;
			
		case 6702://EPE_GUN_NOT_LINKED:
			desc ="充电枪没插好,不能充电";
			break;

		case 6705://EPE_GUN_FAULT1:
			desc="/故障，不能充电";
			break;
			
		case 6706://EPE_GUN_LID_OPENED:
			desc="枪盖已经打开,不能重复打开";
			break;
		case 6707://EPE_EP_CHARGE_FAIL:
			desc="充电桩开始充电失败";
			break;	
			
			
		case 6800://EPE_REPEAT_CHARGE:
			desc="已经在充电,不能重复充电";
			break;
			
		case 6899://EPE_USEING:
			desc="业务占用,不能充电";
			break;
			
		case 6803://EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING:
			desc="没有充电,不能停止充电";
			break;
			
		case 6804://EPE_GUN_FAULT:
			desc="故障，不能充电";
			break;
		case 6805:	
			desc="充电桩停止充电失败";	
			break;
			
		default:
			desc="其他错误";
			break;
		}
		return desc;
	}
	
   
}
