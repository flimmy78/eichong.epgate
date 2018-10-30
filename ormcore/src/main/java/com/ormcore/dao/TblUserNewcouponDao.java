package com.ormcore.dao;

import com.ormcore.model.TblUserNewcoupon;

import java.util.List;

public interface TblUserNewcouponDao {
	
    public List<TblUserNewcoupon> select(int id);

    public int update(TblUserNewcoupon userNewcoupon);
    
    public int insert(TblUserNewcoupon userNewcoupon);
	
}
