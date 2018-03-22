package com.ormcore.dao;

import java.util.List;
import java.util.Map;

import com.ormcore.model.TblChargingOrder;



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
	
	/**
	 * @Title: selectChargeData
	 * @Description: 获取充电电度，金额，服务费，总费用，开始时间，结束时间
	 * @param params
	 * @return
	 */
	public TblChargingOrder selectChargeData(String chorTransactionnumber);

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
	
	public List<TblChargingOrder> findOrderId(String chorTransactionnumber);
	
	public String getOrderStatusBytime(TblChargingOrder tblChargingorder);

}
