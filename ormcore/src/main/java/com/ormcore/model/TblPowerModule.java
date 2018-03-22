package com.ormcore.model;

import java.math.BigDecimal;
import java.util.Date;

public class TblPowerModule {

	private Integer pkPowerModuleid;
	private String chargeSerialNo;//充电流水号
	private String PowerModuleName;//电源模块号
	private BigDecimal output_voltage;//输出电压(精度0.1，单位v)
	private BigDecimal output_current;//输出电流(精度0.1，单位A)
	private BigDecimal a_voltage;//A相电压(精度0.1，单位v)
	private BigDecimal b_voltage;//B相电压(精度0.1，单位v)
	private BigDecimal c_voltage;//C相电压(精度0.1，单位v)
	private BigDecimal a_current;//A相电流(精度0.01，单位A)
	private BigDecimal b_current;//A相电流(精度0.01，单位A)
	private BigDecimal c_current;//A相电流(精度0.01，单位A)
	private BigDecimal temperature;//电源模块温度(精度0.1)
	
	private Date createdate;
	private Date updatedate;
	
	
    private void Init()
    {
    	pkPowerModuleid=0;
    	chargeSerialNo="";//充电流水号
    	PowerModuleName="";
    	
    	output_voltage= new BigDecimal(0.0);//充电机输出电压
    	output_current= new BigDecimal(0.0);//充电机输出电流
 
    	a_voltage= new BigDecimal(0.0);
    	b_voltage= new BigDecimal(0.0);
    	c_voltage= new BigDecimal(0.0);
    	
    	a_current= new BigDecimal(0.0);
    	b_current= new BigDecimal(0.0);
    	c_current= new BigDecimal(0.0);
    	temperature= new BigDecimal(0.0);
    	
    }
	public TblPowerModule()
	{
		Init();
	}
	public Integer getPkPowerModuleid() {
		return pkPowerModuleid;
	}
	public void setPkPowerModuleid(Integer pkPowerModuleid) {
		this.pkPowerModuleid = pkPowerModuleid;
	}
	public String getChargeSerialNo() {
		return chargeSerialNo;
	}
	public void setChargeSerialNo(String chargeSerialNo) {
		this.chargeSerialNo = chargeSerialNo;
	}
	public String getPowerModuleName() {
		return PowerModuleName;
	}
	public void setPowerModuleName(String powerModuleName) {
		PowerModuleName = powerModuleName;
	}
	public BigDecimal getOutput_voltage() {
		return output_voltage;
	}
	public void setOutput_voltage(BigDecimal output_voltage) {
		this.output_voltage = output_voltage;
	}
	public BigDecimal getOutput_current() {
		return output_current;
	}
	public void setOutput_current(BigDecimal output_current) {
		this.output_current = output_current;
	}
	public BigDecimal getA_voltage() {
		return a_voltage;
	}
	public void setA_voltage(BigDecimal a_voltage) {
		this.a_voltage = a_voltage;
	}
	public BigDecimal getB_voltage() {
		return b_voltage;
	}
	public void setB_voltage(BigDecimal b_voltage) {
		this.b_voltage = b_voltage;
	}
	public BigDecimal getC_voltage() {
		return c_voltage;
	}
	public void setC_voltage(BigDecimal c_voltage) {
		this.c_voltage = c_voltage;
	}
	public BigDecimal getA_current() {
		return a_current;
	}
	public void setA_current(BigDecimal a_current) {
		this.a_current = a_current;
	}
	public BigDecimal getB_current() {
		return b_current;
	}
	public void setB_current(BigDecimal b_current) {
		this.b_current = b_current;
	}
	public BigDecimal getC_current() {
		return c_current;
	}
	public void setC_current(BigDecimal c_current) {
		this.c_current = c_current;
	}
	public BigDecimal getTemperature() {
		return temperature;
	}
	public void setTemperature(BigDecimal temperature) {
		this.temperature = temperature;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public Date getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}
	
	
	
	
}
