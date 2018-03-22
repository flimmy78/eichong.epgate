package com.ormcore.dao;

import com.ormcore.model.FavRecord;

import java.util.List;

public interface FavRecordDao {
	 
    public List<FavRecord> FavRecord_custlist(FavRecord favRecord);

    public int FavRecord_insert(FavRecord favRecord);
}
