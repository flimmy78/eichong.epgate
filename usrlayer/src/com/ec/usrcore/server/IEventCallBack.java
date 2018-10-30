package com.ec.usrcore.server;

import java.util.Map;

public interface IEventCallBack {
	/**
	 * 爱充手机用户能否使用电桩
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param account
	 * @param ret
	 * @param errorCode
	 * @param status
	 * @param currentType
	 */
	public void onCanUseEp(int orgNo,String userIdentity,String epCode,int epGunNo,String account,int ret,int errorCode,int status,int currentType);

	
	/**
	 * 电桩放电事件
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra
	 * @param status
	 */
	public void onChargeEvent(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status);
	/**
	 * 启动充电EpGate应答
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra,第三方调用时根据版本号组合不同的参数值
	 * @param ret
	 * @param errorCode
	 */
	public void onStartCharge(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode);
	
	
	/**
	 * 电桩停止充电应答
	 * @param token
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param ret
	 * @param errorCode
	 */
	public void onStopCharge(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode);

	
	/**
	 * 根据订单号查询订单详情应答
	 * @param orgNo
	 * @param extra
	 * @param ret
	 * @param errorCode
	 */
	public void onQueryOrderInfo(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int ret,int errorCode);
	
	/**
	 * 实时数据
	 * @param token
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param realData
	 */
	public void onRealData(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,Map<String ,Object> realData);

	/**
	 * 为Html提供其他渠道的充电实时数据
	 *
	 * @param token
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param realData
	 */
	public void onChargeReal4Html(int orgNo, String userIdentity, String epCode, int epGunNo, String extra, Map<String, Object> realData);

	/**
	 * 4Common实时数据查询
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra
	 * @param ranRuiQueryData
	 */
	public void onQueryCommonRealData(String epCode, int epGunNo, String extra, String ranRuiQueryData);
	/**
	 * 消费记录
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra
	 * @param data
	 */
	public void onChargeOrder(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,Map<String ,Object> data);

	/**
	 * 枪与车连接状态变化通知，手机端实现，e租网等第三方平台不实现
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra
	 * @param status
	 */
	public void onGunLinkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status);

	/**
	 * 枪工作状态变化通知，手机端实现，e租网等第三方平台不实现
	 * @param orgNo
	 * @param userIdentity
	 * @param epCode
	 * @param epGunNo
	 * @param extra
	 * @param status
	 */
	public void onGunWorkStatusChange(int orgNo,String userIdentity,String epCode,int epGunNo,String extra,int status);

	//给html推送全国idle 变化状态的数据
	public void onGunWorkStatusChange4Html(String epCode, int epGunNo,int currentType,String realData);
}
