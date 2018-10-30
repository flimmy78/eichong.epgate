package com.ormcore.dao;



import com.ormcore.model.TblElectricPileGun;

import java.util.List;

public interface EpGunDao {
	
	public void updateGunState(TblElectricPileGun info);
	public int getPkEpGunId(TblElectricPileGun info);
	public List<TblElectricPileGun> findEpGunInfo(TblElectricPileGun info);

    public List<TblElectricPileGun> findAllEpGunInfo();

    public List<TblElectricPileGun> findEpGunInfo2(TblElectricPileGun info);
    
    public void addChargeStat(TblElectricPileGun info);
    public void updateDeviceList(TblElectricPileGun info);
    
    public void updateQR(TblElectricPileGun info);
}
