package com.ormcore.model;

import java.math.BigDecimal;


public class TblCarVin {
				
	/** 主键**/
	private Integer pkCarVin;
			
	/** VIN码**/
	private String vinCode;
			
	/** 汽车合作公司名字**/
	private String vinName;
			
	/** 服务费率**/
	private BigDecimal vinServicemoney;
			
	/** 是否删除 默认 0 未删除 1 删除**/
	private Integer vinIsdelete;

			
	/** 创建时间**/
	private java.util.Date cpCreatedate;
			
	/** 修改时间**/
	private java.util.Date cpUpdatedate;

	public Integer getPkCarVin() {
		return pkCarVin;
	}

	public void setPkCarVin(Integer pkCarVin) {
		this.pkCarVin = pkCarVin;
	}

	public String getVinCode() {
		return vinCode;
	}

	public void setVinCode(String vinCode) {
		this.vinCode = vinCode;
	}

	public String getVinName() {
		return vinName;
	}

	public void setVinName(String vinName) {
		this.vinName = vinName;
	}

	public BigDecimal getVinServicemoney() {
		return vinServicemoney;
	}

	public void setVinServicemoney(BigDecimal vinServicemoney) {
		this.vinServicemoney = vinServicemoney;
	}

	public Integer getVinIsdelete() {
		return vinIsdelete;
	}

	public void setVinIsdelete(Integer vinIsdelete) {
		this.vinIsdelete = vinIsdelete;
	}

	public java.util.Date getCpCreatedate() {
		return cpCreatedate;
	}

	public void setCpCreatedate(java.util.Date cpCreatedate) {
		this.cpCreatedate = cpCreatedate;
	}

	public java.util.Date getCpUpdatedate() {
		return cpUpdatedate;
	}

	public void setCpUpdatedate(java.util.Date cpUpdatedate) {
		this.cpUpdatedate = cpUpdatedate;
	}
	
	
	
}
