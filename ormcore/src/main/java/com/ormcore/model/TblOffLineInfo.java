package com.ormcore.model;

import java.util.Date;

/**
 * Created by zangyaoyi on 2017/8/2.
 */
public class TblOffLineInfo {
    private Integer id;
    private String elPiElectricPileCode;
    private String beginOfflineTime;
    private String endOfflineTime;
    private Integer type;
    private Date createTime; //创建时间
    private Date updateTime; // 修改时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getElPiElectricPileCode() {
        return elPiElectricPileCode;
    }

    public void setElPiElectricPileCode(String elPiElectricPileCode) {
        this.elPiElectricPileCode = elPiElectricPileCode;
    }

    public String getBeginOfflineTime() {
        return beginOfflineTime;
    }

    public void setBeginOfflineTime(String beginOfflineTime) {
        this.beginOfflineTime = beginOfflineTime;
    }

    public String getEndOfflineTime() {
        return endOfflineTime;
    }

    public void setEndOfflineTime(String endOfflineTime) {
        this.endOfflineTime = endOfflineTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
