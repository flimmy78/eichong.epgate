package com.ormcore.model;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 
 * tbl_Bespoke表
 * 
 * @author songjf
 * 
 */
/**
 * 听管理
  * @Description:
  * @author songjf 
  * @createTime：2015-4-13 下午02:14:09 
  * @updator： 
  * @updateTime：   
  * @version：V1.0
 */
public class TblChargingOrder{
	/**
	 * 
	 */
	
	private String pkChargingorder; // 主键
	private String chorCode; // 充电订单编号
	private String chorAppointmencode; // 预约流水号
	private String chorPilenumber; // 桩体编号
	private String chorUserid; // 用户ID(企业ID)
	private int chorType; // 用户类型
	private String chorMoeny; // (收益)金额
	private BigDecimal chorQuantityelectricity; // 电量
	private String chorTimequantum; // 时间段
	private int chorMuzzle; //枪口编号
	private String chorChargingstatus; //订单状态
	private String chorTranstype; //交易类型
	private Date chorCreatedate; //创建时间
	private Date chorUpdatedate; // 修改时间
	private String chorUsername; // 用户姓名(企业姓名)
	private String chorTransactionnumber; // 交易流水号
	private int chorOrdertype; // 1支付宝 2银联
	private BigDecimal chorChargemoney;// 充电金额
	private BigDecimal chorServicemoney;//充电服务费金额
	private BigDecimal chOr_tipPower;//尖时段用电度数
	private BigDecimal chOr_peakPower;//峰时段用电度数
	private BigDecimal chOr_usualPower;//平时段用电度数
	private BigDecimal chOr_valleyPower;//谷时段用电度数
	private String chargeBegintime; // 时间段 hly
	private String chargeEndtime; // 时间段
	
	private BigDecimal couPonAmt; //优惠券抵扣金额
	private int pkCoupon;  //优惠券主键
	private int thirdType;  //优惠第三方类型 默认 0 无优惠  优惠券 1 VIN码 2
	
	//不与数据库进行交互
	private String eleCode;		
	private String usName;
	private String goName;
	private String eleheadName;
	private String usPhone;
	private String comName;
	private int pkUserCard;
	private int userOrigin;
	//private int payMode;
	private int userOrgNo;
	
	private java.lang.String thirdUsrIdentity;
	private java.lang.String thirdExtraData;
	private int startSoc;
	private int endSoc;
    private String vinCode;
	private int batteryRatedCapacity;
	private int batteryTotalEnergy;

	public java.lang.String getThirdExtraData() {
		return thirdExtraData;
	}
	public void setThirdExtraData(java.lang.String thirdExtraData) {
		this.thirdExtraData = thirdExtraData;
	}
	public int getThirdType() {
		return thirdType;
	}
	public void setThirdType(int thirdType) {
		this.thirdType = thirdType;
	}
	public BigDecimal getCouPonAmt() {
		return couPonAmt;
	}
	public void setCouPonAmt(BigDecimal couPonAmt) {
		this.couPonAmt = couPonAmt;
	}
	public int getPkCoupon() {
		return pkCoupon;
	}
	public void setPkCoupon(int pkCoupon) {
		this.pkCoupon = pkCoupon;
	}
	public String getChargeBegintime() {
		return chargeBegintime;
	}
	public void setChargeBegintime(String chargeBegintime) {
		this.chargeBegintime = chargeBegintime;
	}
	
	public String getChargeEndtime() {
		return chargeEndtime;
	}
	public void setChargeEndtime(String chargeEndtime) {
		this.chargeEndtime = chargeEndtime;
	}
	
	public BigDecimal getChOr_tipPower() {
		return chOr_tipPower;
	}
	public void setChOr_tipPower(BigDecimal chOr_tipPower) {
		this.chOr_tipPower = chOr_tipPower;
	}
	public BigDecimal getChOr_peakPower() {
		return chOr_peakPower;
	}
	public void setChOr_peakPower(BigDecimal chOr_peakPower) {
		this.chOr_peakPower = chOr_peakPower;
	}
	public BigDecimal getChOr_usualPower() {
		return chOr_usualPower;
	}
	public void setChOr_usualPower(BigDecimal chOr_usualPower) {
		this.chOr_usualPower = chOr_usualPower;
	}
	public BigDecimal getChOr_valleyPower() {
		return chOr_valleyPower;
	}
	public void setChOr_valleyPower(BigDecimal chOr_valleyPower) {
		this.chOr_valleyPower = chOr_valleyPower;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getUsPhone() {
		return usPhone;
	}
	public void setUsPhone(String usPhone) {
		this.usPhone = usPhone;
	}
	public String getEleCode() {
		return eleCode;
	}
	public void setEleCode(String eleCode) {
		this.eleCode = eleCode;
	}
	public String getUsName() {
		return usName;
	}
	public void setUsName(String usName) {
		this.usName = usName;
	}
	public String getGoName() {
		return goName;
	}
	public void setGoName(String goName) {
		this.goName = goName;
	}
	public String getEleheadName() {
		return eleheadName;
	}
	public void setEleheadName(String eleheadName) {
		this.eleheadName = eleheadName;
	}
	public String getPkChargingorder() {
		return pkChargingorder;
	}
	public void setPkChargingorder(String pkChargingorder) {
		this.pkChargingorder = pkChargingorder;
	}
	public String getChorCode() {
		return chorCode;
	}
	public void setChorCode(String chorCode) {
		this.chorCode = chorCode;
	}
	public String getChorAppointmencode() {
		return chorAppointmencode;
	}
	public void setChorAppointmencode(String chorAppointmencode) {
		this.chorAppointmencode = chorAppointmencode;
	}
	public String getChorPilenumber() {
		return chorPilenumber;
	}
	public void setChorPilenumber(String chorPilenumber) {
		this.chorPilenumber = chorPilenumber;
	}
	public String getChorUserid() {
		return chorUserid;
	}
	public void setChorUserid(String chorUserid) {
		this.chorUserid = chorUserid;
	}
	public int getChorType() {
		return chorType;
	}
	public void setChorType(int chorType) {
		this.chorType = chorType;
	}
	public String getChorMoeny() {
		return chorMoeny;
	}
	public void setChorMoeny(String chorMoeny) {
		this.chorMoeny = chorMoeny;
	}
	public BigDecimal getChorQuantityelectricity() {
		return chorQuantityelectricity;
	}
	public void setChorQuantityelectricity(BigDecimal chorQuantityelectricity) {
		this.chorQuantityelectricity = chorQuantityelectricity;
	}
	public String getChorTimequantum() {
		return chorTimequantum;
	}
	public void setChorTimequantum(String chorTimequantum) {
		this.chorTimequantum = chorTimequantum;
	}
	
	public int getChorMuzzle() {
		return chorMuzzle;
	}
	public void setChorMuzzle(int chorMuzzle) {
		this.chorMuzzle = chorMuzzle;
	}
	public String getChorChargingstatus() {
		return chorChargingstatus;
	}
	public void setChorChargingstatus(String chorChargingstatus) {
		this.chorChargingstatus = chorChargingstatus;
	}
	public String getChorTranstype() {
		return chorTranstype;
	}
	public void setChorTranstype(String chorTranstype) {
		this.chorTranstype = chorTranstype;
	}
	public Date getChorCreatedate() {
		return chorCreatedate;
	}
	public void setChorCreatedate(Date chorCreatedate) {
		this.chorCreatedate = chorCreatedate;
	}
	public Date getChorUpdatedate() {
		return chorUpdatedate;
	}
	public void setChorUpdatedate(Date chorUpdatedate) {
		this.chorUpdatedate = chorUpdatedate;
	}
	public String getChorUsername() {
		return chorUsername;
	}
	public void setChorUsername(String chorUsername) {
		this.chorUsername = chorUsername;
	}
	public String getChorTransactionnumber() {
		return chorTransactionnumber;
	}
	public void setChorTransactionnumber(String chorTransactionnumber) {
		this.chorTransactionnumber = chorTransactionnumber;
	}
	public int getChorOrdertype() {
		return chorOrdertype;
	}
	public void setChorOrdertype(int chorOrdertype) {
		this.chorOrdertype = chorOrdertype;
	}
	public BigDecimal getChorChargemoney() {
		return chorChargemoney;
	}
	public void setChorChargemoney(BigDecimal chorChargemoney) {
		this.chorChargemoney = chorChargemoney;
	}
	public BigDecimal getChorServicemoney() {
		return chorServicemoney;
	}
	public void setChorServicemoney(BigDecimal chorServicemoney) {
		this.chorServicemoney = chorServicemoney;
	}
	public int getPkUserCard() {
		return pkUserCard;
	}
	public void setPkUserCard(int pkUserCard) {
		this.pkUserCard = pkUserCard;
	}
	public int getUserOrigin() {
		return userOrigin;
	}
	public void setUserOrigin(int userOrigin) {
		this.userOrigin = userOrigin;
	}
	/*public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}*/
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
	public int getStartSoc() {
		return startSoc;
	}
	public void setStartSoc(int startSoc) {
		this.startSoc = startSoc;
	}
	public int getEndSoc() {
		return endSoc;
	}
	public void setEndSoc(int endSoc) {
		this.endSoc = endSoc;
	}

    public String getVinCode() {
        return vinCode;
    }

    public void setVinCode(String vinCode) {
        this.vinCode = vinCode;
    }

	public int getBatteryRatedCapacity() {
		return batteryRatedCapacity;
	}

	public void setBatteryRatedCapacity(int batteryRatedCapacity) {
		this.batteryRatedCapacity = batteryRatedCapacity;
	}

	public int getBatteryTotalEnergy() {
		return batteryTotalEnergy;
	}

	public void setBatteryTotalEnergy(int batteryTotalEnergy) {
		this.batteryTotalEnergy = batteryTotalEnergy;
	}
}