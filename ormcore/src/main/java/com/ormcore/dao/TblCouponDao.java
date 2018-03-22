package com.ormcore.dao;

import java.util.List;
import java.util.Map;

import com.ormcore.model.TblCoupon;

public interface TblCouponDao {
	public List<TblCoupon> queryCoupon(Map<String, Object> map);
	
	public int updateCoupon(int pkCoupon);

    public int insertCoupon(TblCoupon coupon);

	public List<Integer> queryActivity(int actActivityrule);

    public void insertInviteCoupon(Map<String, Object> map);
}
