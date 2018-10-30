package com.ormcore.dao;

import com.ormcore.model.FavRecord;

import java.util.List;
import java.util.Map;

public interface FavRecordDao {
	 
    public List<FavRecord> FavRecord_custlist(FavRecord favRecord);

    public int FavRecord_insert(FavRecord favRecord);

    public int queryElectricCard(Map<String, Object> map);
}
