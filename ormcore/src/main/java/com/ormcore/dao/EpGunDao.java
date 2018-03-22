package com.ormcore.dao;



import java.util.List;

import com.ormcore.model.TblElectricPileGun;

public interface EpGunDao {
	
	public void updateGunState(TblElectricPileGun info);
	public int getPkEpGunId(TblElectricPileGun info);
	public List<TblElectricPileGun> findEpGunInfo(TblElectricPileGun info);
    public List<TblElectricPileGun> findEpGunInfo2(TblElectricPileGun info);
    
    public void addChargeStat(TblElectricPileGun info);
    public void updateDeviceList(TblElectricPileGun info);
    
    public void updateQR(TblElectricPileGun info);
}
