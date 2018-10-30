package com.ormcore.dao;

import com.ormcore.model.FinAccount;
import com.ormcore.model.TblUser;

import java.util.List;

public interface TblUserDao {

    public List<TblUser> findIdByAccount(String userAccount);

}
