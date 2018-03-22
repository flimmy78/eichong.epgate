package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.TblUserNormal;

public interface TblUserNormalDao {
	
    public List<TblUserNormal> findUserInfoById(int accountId);
    
    public int subCost(TblUserNormal info);
    
    public int addCost(TblUserNormal info);
	
}
