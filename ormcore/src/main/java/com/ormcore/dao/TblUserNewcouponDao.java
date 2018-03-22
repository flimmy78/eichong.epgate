package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.TblUserNewcoupon;

public interface TblUserNewcouponDao {
	
    public List<TblUserNewcoupon> select(int id);

    public int update(TblUserNewcoupon userNewcoupon);
    
    public int insert(TblUserNewcoupon userNewcoupon);
	
}
