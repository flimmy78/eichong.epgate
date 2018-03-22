package com.ormcore.dao;

import java.util.List;
import java.util.Map;

import com.ormcore.model.RateInfo;


public interface RateInfoDao {

	/**
	 * @Title: findRateInfo
	 * @Description: 根据电桩编号获取电桩费率信息
	 * @param params
	 * @return
	 */
	
	public List<RateInfo> findRateInfo(String code);
	public List<RateInfo> getAll();
	public List<RateInfo> getLastUpdate();
	//hly 2015-9-18 add 
	public RateInfo findRateInfofromId(int rateid);
	public int findPerRateId(Map<String, Object> params);

}
