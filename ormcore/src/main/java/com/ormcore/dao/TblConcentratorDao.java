package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.TblConcentrator;


public interface TblConcentratorDao {
	
	public int findStation(int id);
	
	
	public List<TblConcentrator> findResultObjectBySpanId(int typeId);
	

}
