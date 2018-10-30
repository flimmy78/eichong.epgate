package com.ormcore.dao;

import com.ormcore.model.ElectricPileCachePo;
import com.ormcore.model.TblElectricPile;

import java.util.List;

public interface TblElectricPileDao {
	
	public List<?> getElectricpileForMap(TblElectricPile tblElectricpile);
	
	public List<TblElectricPile> findResultObjectBySpanId(int typeSpanId);
	
	public List<TblElectricPile> findResultObject(String code);

	public List<ElectricPileCachePo> initAllEpInfo();

	public List<TblElectricPile> initAllEpBaseInfo();

	public List<TblElectricPile> getLastUpdate();
	
	public int updateCommStatus(TblElectricPile epClient);
	public int updateAllCommStatus(int gateid);
	
	public int updateCommStatusByStationId(TblElectricPile epClient);
	
	
	public List<TblElectricPile>  getEpsByStationId(int nStationId);
	public List<TblElectricPile>  getEpsByStatus(int nStationId);
	public int updateRateId(TblElectricPile epClient);
	
	public List<TblElectricPile> getEpTypeByUserChargeOrder(int userId);
	
	public List<TblElectricPile> findResultObjectByCompany(int compny_number);
}
