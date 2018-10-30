package com.ormcore.dao;


import com.ormcore.model.TblPurchaseHistory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PurchaseHistoryDao {
	
	public int insertPurchaseRecord(TblPurchaseHistory info);

	public int getAccountId(TblPurchaseHistory info);

	public int getPayMode(int accountID);

	public int getPayRule(int cpyId);

	public int getCount(TblPurchaseHistory info);

	public BigDecimal getFrozen(TblPurchaseHistory info);
}

