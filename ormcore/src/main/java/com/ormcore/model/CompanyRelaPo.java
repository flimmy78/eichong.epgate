/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ormcore.model;

import java.util.List;

/** 桩的公司关系
 * @author ${zhuhuiming}
 * @version $Id: CompanyRelaPo.java, v 0.1 2018年08月22日 21:53 Exp $
 */
public class CompanyRelaPo {
    /**
     * 桩ID
     **/
    private Integer pkElectricpile;

    private List<CompanyRela> companyRelaList;

    public Integer getPkElectricpile() {
        return pkElectricpile;
    }

    public void setPkElectricpile(final Integer pkElectricpile) {
        this.pkElectricpile = pkElectricpile;
    }

    public List<CompanyRela> getCompanyRelaList() {
        return companyRelaList;
    }

    public void setCompanyRelaList(final List<CompanyRela> companyRelaList) {
        this.companyRelaList = companyRelaList;
    }
}