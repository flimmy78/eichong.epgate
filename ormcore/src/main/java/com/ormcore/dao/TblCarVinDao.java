package com.ormcore.dao;

import java.util.List;
import java.util.Map;

import com.ormcore.model.TblCarVin;


public interface TblCarVinDao {

	/**
	 * 查找vin对应的记录
	 * @param code
	 * @return
	 */
	public List<TblCarVin> selectByCode(String vinCode);
	public List<TblCarVin> selectById(int vinId);
	public List<TblCarVin> selectByUserId(int userId);
	public int isValidVin(Map<String, Object> map);
	public List<TblCarVin> selectByVinCode(String vinCode);

	
}
