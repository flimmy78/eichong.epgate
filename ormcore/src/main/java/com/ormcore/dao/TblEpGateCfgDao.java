package com.ormcore.dao;


import java.util.List;

import com.ormcore.model.TblEpGateConfig;

public interface TblEpGateCfgDao {    
    
	public TblEpGateConfig get(java.lang.Integer pkGateid);
	
	public  TblEpGateConfig findOne(TblEpGateConfig tblEpGateCfg);
	
	public List<TblEpGateConfig> find(TblEpGateConfig tblEpGateCfg);
	
	public int insert(TblEpGateConfig tblEpGateCfg);
	
	public int update(TblEpGateConfig tblEpGateCfg);
	
	public int delete(java.lang.Integer pkGateid );
	public List<TblEpGateConfig> find1(TblEpGateConfig tblEpGateCfg);
	public void updateFailState(java.lang.Integer pkGateid);
}


