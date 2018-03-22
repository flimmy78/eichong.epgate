package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.ElectricpileWorkarg;

public interface ElectricpileWorkargDao {
	 
    public List<ElectricpileWorkarg> ElectricpileWorkarg_custlist(ElectricpileWorkarg ElectricpileWorkarg);
	 
    public int ElectricpileWorkarg_update(ElectricpileWorkarg ElectricpileWorkarg);
	 
    public int ElectricpileWorkarg_updateFail(ElectricpileWorkarg ElectricpileWorkarg);
	 
    public int ElectricpileWorkarg_insert(ElectricpileWorkarg ElectricpileWorkarg);

}
