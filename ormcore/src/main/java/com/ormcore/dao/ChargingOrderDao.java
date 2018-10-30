package com.ormcore.dao;

import com.ormcore.model.TblChargingOrder;

import java.util.List;
import java.util.Map;



public interface ChargingOrderDao {
	
	public TblChargingOrder get(java.lang.Integer pkChargingorder);

	public <K, V> Map<K, V> findOne(java.lang.Integer pkChargingorder);

	public <T, K, V> List<T> find(Map<K, V> params);

	/**
	 * @Title: insertChargeOrder
	 * @Description: 新增充电消费订单
	 * @param params
	 * @return
	 */
	public int insertChargeOrder(TblChargingOrder tblChargingorder);

	public int insertFullChargeOrder(TblChargingOrder tblChargingorder);

	public int insertChargeOrderExt(TblChargingOrder tblChargingorder);

	public int insertFullChargeOrderExt(TblChargingOrder tblChargingorder);

	public int updateOrderExt(TblChargingOrder tblChargingorder);

	/**
	 * @Title: selectChargeData
	 * @Description: 获取充电电度，金额，服务费，总费用，开始时间，结束时间
	 * @param params
	 * @return
	 */
	public List<TblChargingOrder> selectChargeData(String chorTransactionnumber);

	/**
	 * @Title: updateStatus
	 * @Description: 更新订单状态
	 * @param params
	 * @return
	 */
	public int updateStatus(TblChargingOrder tblChargingorder);

	public int updateOrder(TblChargingOrder tblChargingorder);

	public int delete(java.lang.Integer pkChargingorder);

	
	
	public int getUnpayOrder(int userId);
	
	public String getOrderStatus(String chorTransactionnumber);

	public String getMaxOrCode(String chorCode);

	public List<TblChargingOrder> findOrderId(String chorTransactionnumber);

	public String getOrderStatusBytime(TblChargingOrder tblChargingorder);

}
