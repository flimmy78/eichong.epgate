package com.ormcore.dao;

import com.ormcore.model.TblUserCompany;

import java.util.List;

public interface TblUserCompanyDao {

    public List<TblUserCompany> findDataById(int userId);
}
