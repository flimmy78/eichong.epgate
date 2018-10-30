package com.ormcore.dao;

import com.ormcore.model.TblCompany;

import java.util.List;


public interface TblCompanyDao {
	
    public List<TblCompany> findone(int CompanyNumber);

    public List<TblCompany> findAllCompany();


}
