package com.ormcore.dao;

import com.ormcore.model.FinAccount;

import java.util.List;

public interface FinAccountDao {

    public List<FinAccount> amountWarn(int accountId);

    public void updataFlagById(FinAccount finAccount1);
}
