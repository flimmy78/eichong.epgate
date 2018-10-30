package com.ormcore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TblUserCompany implements Serializable {

    private static final long serialVersionUID = 9199524549133771385L;
    /** 用户ID**/
    private Integer userId;

    /** 所属渠道ID**/
    private Integer cpyId;

    /** 用户昵称**/
    private String userName;

    /** 用户名称**/
    private String userCpyName;

    /** 手机号**/
    private String userCpyPhone;

    /** '性别 1.男 2.女'**/
    private Short userCpySex;

    /** 设备ID**/
    private String userDeviceId;

    /** 车牌号**/
    private String userPlateNum;

    /** 资金账户ID**/
    private Integer accountId;

    /** 创建时间**/
    private java.util.Date gmtCreate;

    /** 修改时间**/
    private java.util.Date gmtModified;

    /** 创建人**/
    private String creator;

    /** 修改人**/
    private String modifier;

    /** 用户汽车品牌ID**/
    private Short userCarCompanyId;

    /** 用户汽车车型ID**/
    private Short userCarTypeId;

    /** 用户学历**/
    private String userDiploma;

    /** 用户职业**/
    private String userProfession;

    /** 用户收入**/
    private BigDecimal userEarning;

    public String getUserProfession() {
        return userProfession;
    }

    public void setUserProfession(String userProfession) {
        this.userProfession = userProfession;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCpyId() {
        return cpyId;
    }

    public void setCpyId(Integer cpyId) {
        this.cpyId = cpyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCpyName() {
        return userCpyName;
    }

    public void setUserCpyName(String userCpyName) {
        this.userCpyName = userCpyName;
    }

    public String getUserCpyPhone() {
        return userCpyPhone;
    }

    public void setUserCpyPhone(String userCpyPhone) {
        this.userCpyPhone = userCpyPhone;
    }

    public Short getUserCpySex() {
        return userCpySex;
    }

    public void setUserCpySex(Short userCpySex) {
        this.userCpySex = userCpySex;
    }

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public String getUserPlateNum() {
        return userPlateNum;
    }

    public void setUserPlateNum(String userPlateNum) {
        this.userPlateNum = userPlateNum;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Short getUserCarCompanyId() {
        return userCarCompanyId;
    }

    public void setUserCarCompanyId(Short userCarCompanyId) {
        this.userCarCompanyId = userCarCompanyId;
    }

    public Short getUserCarTypeId() {
        return userCarTypeId;
    }

    public void setUserCarTypeId(Short userCarTypeId) {
        this.userCarTypeId = userCarTypeId;
    }

    public String getUserDiploma() {
        return userDiploma;
    }

    public void setUserDiploma(String userDiploma) {
        this.userDiploma = userDiploma;
    }

    public BigDecimal getUserEarning() {
        return userEarning;
    }

    public void setUserEarning(BigDecimal userEarning) {
        this.userEarning = userEarning;
    }



}
