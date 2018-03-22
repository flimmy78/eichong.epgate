package com.cooperate.TCEC.util;

import java.util.Date;

/**
 * Created by zangyaoyi on 2017/12/4.
 */
public class TokenModel {
    private int org;
    private Date tokenAvailableTime;
    private String staticToken;
    private String operatorId ;
    private String operatorSecret ;
    private String dataSecret ;
    private String sigSecret ;
    private String dataSecretIv ;

    public int getOrg() {
        return org;
    }

    public void setOrg(int org) {
        this.org = org;
    }

    public Date getTokenAvailableTime() {
        return tokenAvailableTime;
    }

    public void setTokenAvailableTime(Date tokenAvailableTime) {
        this.tokenAvailableTime = tokenAvailableTime;
    }

    public String getStaticToken() {
        return staticToken;
    }

    public void setStaticToken(String staticToken) {
        this.staticToken = staticToken;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorSecret() {
        return operatorSecret;
    }

    public void setOperatorSecret(String operatorSecret) {
        this.operatorSecret = operatorSecret;
    }

    public String getDataSecret() {
        return dataSecret;
    }

    public void setDataSecret(String dataSecret) {
        this.dataSecret = dataSecret;
    }

    public String getSigSecret() {
        return sigSecret;
    }

    public void setSigSecret(String sigSecret) {
        this.sigSecret = sigSecret;
    }

    public String getDataSecretIv() {
        return dataSecretIv;
    }

    public void setDataSecretIv(String dataSecretIv) {
        this.dataSecretIv = dataSecretIv;
    }
}
