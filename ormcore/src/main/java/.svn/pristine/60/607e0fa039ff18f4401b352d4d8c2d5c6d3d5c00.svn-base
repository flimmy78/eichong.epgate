package com.ormcore.model;

import java.math.BigDecimal;


/**
 * 
 * tbl_ChargingRecord表
 * @author songjf
 *
 */
public class TblChargingrecord {
	private java.lang.Integer pkChargingrecord; // 主键
	private java.lang.Integer chreElectricpileid; // 电桩ID(tbl_ElectricPile中获取)
	private java.lang.String chreUsingmachinecode; // 使用终端机器编码
	private java.lang.String chreTransactionnumber; // 交易流水号
	private java.lang.String chreReservationnumber; // 预约号
	private java.util.Date chreStartdate; // 充电开始时间
	private java.lang.Integer chreChargingnumber; // 充电抢编号
	private java.lang.Integer chreChargingmethod; // 1:自动充满 2:按金额充 3:按里程4:按度数
	private Integer chreResttime; // 充满电剩余时间
	private java.util.Date  chreEnddate; // 
	private java.lang.String chreCode; // 充电订单编号与充电消费订单是主外键关系
	private java.lang.String chreBeginshowsnumber; // 开始充电表低示数
	private java.lang.String chreEndshowsnumber; // 结束充电表低示数
	private String userPhone;//用户手机号
	private java.lang.Integer userId;//用户手机号
	private java.lang.Integer status;//充电状态
	private BigDecimal JPrice;//尖峰价格
	private BigDecimal FPrice;//峰价格
	private BigDecimal PPrice;//尖峰价格
	private BigDecimal GPrice;//尖峰价格
	private BigDecimal ServicePrice;//尖峰价格
	private String QuantumDate;//尖峰价格
    private BigDecimal JMoney;//尖服务费
    private BigDecimal FMoney;//峰服务费
    private BigDecimal PMoney;//平服务费
    private BigDecimal GMoney;//谷服务费

	private BigDecimal FrozenAmt;//尖峰价格
	private BigDecimal RealAmt;//尖峰价格
	
	private Integer thirdCode;//第3方主键
	private Integer thirdType;//第3方主键

	private String usrGateIp;//用户网关IP
	
	private java.lang.Integer pkUserCard;
	private java.lang.Integer userOrigin;
	private int payMode;
	private int userOrgNo;
	private java.lang.String thirdUsrIdentity;
	
    private java.lang.String thirdExtraData;
	private int accountId;
	
	public java.lang.String getThirdExtraData() {
		return thirdExtraData;
	}
	public void setThirdExtraData(java.lang.String thirdExtraData) {
		this.thirdExtraData = thirdExtraData;
	}
	
	
	
	
	public TblChargingrecord()
	{
		userOrigin=0;
		thirdCode=0;
		thirdType=0;
		usrGateIp="";
	}
	/**
     * 获取主键属性
     *
     * @return pkChargingrecord
     */
	public java.lang.Integer getPkChargingrecord() {
		return pkChargingrecord;
	}
	
	/**
	 * 设置主键属性
	 *
	 * @param pkChargingrecord
	 */
	public void setPkChargingrecord(java.lang.Integer pkChargingrecord) {
		this.pkChargingrecord = pkChargingrecord;
	}
	
	
	
	
	public String getUsrGateIp() {
		return usrGateIp;
	}
	public void setUsrGateIp(String usrGateIp) {
		this.usrGateIp = usrGateIp;
	}
	
	public Integer getThirdCode() {
		return thirdCode;
	}
	public void setThirdCode(Integer thirdCode) {
		this.thirdCode = thirdCode;
	}
	public Integer getThirdType() {
		return thirdType;
	}
	public void setThirdType(Integer thirdType) {
		this.thirdType = thirdType;
	}
	public java.lang.Integer getUserId() {
		return userId;
	}

	public void setUserId(java.lang.Integer userId) {
		this.userId = userId;
	}
	
	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	/**
     * 获取电桩ID(tbl_ElectricPile中获取)属性
     *
     * @return chreElectricpileid
     */
	public java.lang.Integer getChreElectricpileid() {
		return chreElectricpileid;
	}
	
	/**
	 * 设置电桩ID(tbl_ElectricPile中获取)属性
	 *
	 * @param chreElectricpileid
	 */
	public void setChreElectricpileid(java.lang.Integer chreElectricpileid) {
		this.chreElectricpileid = chreElectricpileid;
	}
	
	/**
     * 获取使用终端机器编码属性
     *
     * @return chreUsingmachinecode
     */
	public java.lang.String getChreUsingmachinecode() {
		return chreUsingmachinecode;
	}
	
	/**
	 * 设置使用终端机器编码属性
	 *
	 * @param chreUsingmachinecode
	 */
	public void setChreUsingmachinecode(java.lang.String chreUsingmachinecode) {
		this.chreUsingmachinecode = chreUsingmachinecode;
	}
	
	/**
     * 获取交易流水号属性
     *
     * @return chreTransactionnumber
     */
	public java.lang.String getChreTransactionnumber() {
		return chreTransactionnumber;
	}
	
	/**
	 * 设置交易流水号属性
	 *
	 * @param chreTransactionnumber
	 */
	public void setChreTransactionnumber(java.lang.String chreTransactionnumber) {
		this.chreTransactionnumber = chreTransactionnumber;
	}
	
	/**
     * 获取预约号属性
     *
     * @return chreReservationnumber
     */
	public java.lang.String getChreReservationnumber() {
		return chreReservationnumber;
	}
	
	/**
	 * 设置预约号属性
	 *
	 * @param chreReservationnumber
	 */
	public void setChreReservationnumber(java.lang.String chreReservationnumber) {
		this.chreReservationnumber = chreReservationnumber;
	}
	
	/**
     * 获取充电开始时间属性
     *
     * @return chreStartdate
     */
	public java.util.Date getChreStartdate() {
		return chreStartdate;
	}
	
	/**
	 * 设置充电开始时间属性
	 *
	 * @param chreStartdate
	 */
	public void setChreStartdate(java.util.Date chreStartdate) {
		this.chreStartdate = chreStartdate;
	}
	
	/**
     * 获取充电抢编号属性
     *
     * @return chreChargingnumber
     */
	public java.lang.Integer getChreChargingnumber() {
		return chreChargingnumber;
	}
	
	/**
	 * 设置充电抢编号属性
	 *
	 * @param chreChargingnumber
	 */
	public void setChreChargingnumber(java.lang.Integer chreChargingnumber) {
		this.chreChargingnumber = chreChargingnumber;
	}
	
	/**
     * 获取1:自动充满 2:按金额充 3:按里程4:按度数属性
     *
     * @return chreChargingmethod
     */
	public java.lang.Integer getChreChargingmethod() {
		return chreChargingmethod;
	}
	
	/**
	 * 设置1:自动充满 2:按金额充 3:按里程4:按度数属性
	 *
	 * @param chreChargingmethod
	 */
	public void setChreChargingmethod(java.lang.Integer chreChargingmethod) {
		this.chreChargingmethod = chreChargingmethod;
	}
	
	
	public Integer getChreResttime() {
		return chreResttime;
	}

	public void setChreResttime(Integer chreResttime) {
		this.chreResttime = chreResttime;
	}

	/**
     * 获取属性
     *
     * @return chreEnddate
     */
	public java.util.Date getChreEnddate() {
		return chreEnddate;
	}
	
	/**
	 * 设置属性
	 *
	 * @param chreEnddate
	 */
	public void setChreEnddate(java.util.Date chreEnddate) {
		this.chreEnddate = chreEnddate;
	}
	
	/**
     * 获取充电订单编号与充电消费订单是主外键关系属性
     *
     * @return chreCode
     */
	public java.lang.String getChreCode() {
		return chreCode;
	}
	
	/**
	 * 设置充电订单编号与充电消费订单是主外键关系属性
	 *
	 * @param chreCode
	 */
	public void setChreCode(java.lang.String chreCode) {
		this.chreCode = chreCode;
	}
	
	/**
     * 获取开始充电表低示数属性
     *
     * @return chreBeginshowsnumber
     */
	public java.lang.String getChreBeginshowsnumber() {
		return chreBeginshowsnumber;
	}
	
	/**
	 * 设置开始充电表低示数属性
	 *
	 * @param chreBeginshowsnumber
	 */
	public void setChreBeginshowsnumber(java.lang.String chreBeginshowsnumber) {
		this.chreBeginshowsnumber = chreBeginshowsnumber;
	}
	
	/**
     * 获取结束充电表低示数属性
     *
     * @return chreEndshowsnumber
     */
	public java.lang.String getChreEndshowsnumber() {
		return chreEndshowsnumber;
	}
	
	/**
	 * 设置结束充电表低示数属性
	 *
	 * @param chreEndshowsnumber
	 */
	public void setChreEndshowsnumber(java.lang.String chreEndshowsnumber) {
		this.chreEndshowsnumber = chreEndshowsnumber;
	}
	

	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}

	public BigDecimal getJPrice() {
		return JPrice;
	}

	public void setJPrice(BigDecimal jPrice) {
		JPrice = jPrice;
	}

	public BigDecimal getFPrice() {
		return FPrice;
	}

	public void setFPrice(BigDecimal fPrice) {
		FPrice = fPrice;
	}

	public BigDecimal getPPrice() {
		return PPrice;
	}

	public void setPPrice(BigDecimal pPrice) {
		PPrice = pPrice;
	}

	public BigDecimal getGPrice() {
		return GPrice;
	}

	public void setGPrice(BigDecimal gPrice) {
		GPrice = gPrice;
	}

	public BigDecimal getServicePrice() {
		return ServicePrice;
	}
	public void setServicePrice(BigDecimal servicePrice) {
		ServicePrice = servicePrice;
	}
	public String getQuantumDate() {
		return QuantumDate;
	}

	public void setQuantumDate(String quantumDate) {
		QuantumDate = quantumDate;
	}

    public BigDecimal getJMoney() {
        return JMoney;
    }

    public void setJMoney(BigDecimal JMoney) {
        this.JMoney = JMoney;
    }

    public BigDecimal getFMoney() {
        return FMoney;
    }

    public void setFMoney(BigDecimal FMoney) {
        this.FMoney = FMoney;
    }

    public BigDecimal getPMoney() {
        return PMoney;
    }

    public void setPMoney(BigDecimal PMoney) {
        this.PMoney = PMoney;
    }

    public BigDecimal getGMoney() {
        return GMoney;
    }

    public void setGMoney(BigDecimal GMoney) {
        this.GMoney = GMoney;
    }

    public BigDecimal getFrozenAmt() {
		return FrozenAmt;
	}

	public void setFrozenAmt(BigDecimal frozenAmt) {
		FrozenAmt = frozenAmt;
	}

	public BigDecimal getRealAmt() {
		return RealAmt;
	}

	public void setRealAmt(BigDecimal realAmt) {
		RealAmt = realAmt;
	}
	
	public java.lang.Integer getPkUserCard() {
		return pkUserCard;
	}

	public void setPkUserCard(java.lang.Integer pkUserCard) {
		this.pkUserCard = pkUserCard;
	}

	public java.lang.Integer getUserOrigin() {
		return userOrigin;
	}

	public void setUserOrigin(java.lang.Integer userOrigin) {
		this.userOrigin = userOrigin;
	}
	public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}
	public int getUserOrgNo() {
		return userOrgNo;
	}
	public void setUserOrgNo(int userOrgNo) {
		this.userOrgNo = userOrgNo;
	}
	public java.lang.String getThirdUsrIdentity() {
		return thirdUsrIdentity;
	}
	public void setThirdUsrIdentity(java.lang.String thirdUsrIdentity) {
		this.thirdUsrIdentity = thirdUsrIdentity;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
}