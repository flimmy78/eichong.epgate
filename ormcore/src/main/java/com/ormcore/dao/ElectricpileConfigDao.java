package com.ormcore.dao;

import java.util.List;

import com.ormcore.model.ElectricpileConfig;

public interface ElectricpileConfigDao {
	 
    public ElectricpileConfig ElectricpileConfig_get(int id);
	 
    public List<ElectricpileConfig> ElectricpileConfig_custlist(ElectricpileConfig ElectricpileConfig);

}
