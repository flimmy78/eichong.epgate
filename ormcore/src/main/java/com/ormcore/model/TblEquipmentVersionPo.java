/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ormcore.model;

import java.util.List;

/**
 * @author ${zhuhuiming}
 * @version $Id: TblEquipmentVersionPo.java, v 0.1 2018年08月05日 17:06 Exp $
 */
public class TblEquipmentVersionPo  {
    private Integer productID;
    private List<TblEquipmentVersion>equipmentVersionList;

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(final Integer productID) {
        this.productID = productID;
    }

    public List<TblEquipmentVersion> getEquipmentVersionList() {
        return equipmentVersionList;
    }

    public void setEquipmentVersionList(final List<TblEquipmentVersion> equipmentVersionList) {
        this.equipmentVersionList = equipmentVersionList;
    }
}