package com.cooperate;

public abstract class Push implements IPush {
    protected String appId = "";
    protected String appKey = "";
    protected String appSecret = "";

    protected String chargeRespUrl = "";
    protected String stopChargeRespUrl = "";
    protected String chargeEventUrl = "";
    protected String statusChangeUrl = "";
    protected String netStatusChangeUrl = "";
    protected String realDataUrl = "";
    protected String orderUrl = "";
    protected String tokenUrl = "";

    protected int orgNo;
    protected int mode = 0;//1:只发充电时实时数据，2:充电和空闲时都发实时数据
    protected long period;
    protected String orgCode;

    public Push() {
        this.mode = 1;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getChargeRespUrl() {
        return chargeRespUrl;
    }

    public String getStopchargeRespUrl() {
        return stopChargeRespUrl;
    }

    public String getChargeEventUrl() {
        return chargeEventUrl;
    }

    public String getStatusChangeUrl() {
        return statusChangeUrl;
    }

    public void setStatusChangeUrl(String statusChangeUrl) {
        this.statusChangeUrl = statusChangeUrl;
    }

    public String getNetStatusChangeUrl() {
        return netStatusChangeUrl;
    }

    public void setNetStatusChangeUrl(String netStatusChangeUrl) {
        this.netStatusChangeUrl = netStatusChangeUrl;
    }

    public String getRealDataUrl() {
        return realDataUrl;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public int getOrgNo() {
        return orgNo;
    }

    public int getMode() {
        return mode;
    }

    public long getPeriod() {
        return period;
    }

    public String getOrgCode() {
        return orgCode;
    }
}
