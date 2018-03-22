package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.ElectricpileMeternum;

public interface ElectricpileMeternumDao {
	 
    public List<ElectricpileMeternum> ElectricpileMeternum_custlist(ElectricpileMeternum ElectricpileMeternum);
	 
    public int ElectricpileMeternum_insert(ElectricpileMeternum ElectricpileMeternum);

}
