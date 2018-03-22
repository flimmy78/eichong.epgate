package com.ormcore.dao;

import java.util.List;
import java.util.Map;

import com.ormcore.model.TblBomList;


public interface BomListDao {

	public List<TblBomList> getAll();
	
	public List<TblBomList> getAllByTypeSpanId(int Id);
	
	public List<TblBomList> getAllByBomListId(int Id);
	
	public List<TblBomList> getBomByProductId(Map<String, Object> map);
}
