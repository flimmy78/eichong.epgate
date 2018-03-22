package com.ormcore.model;


/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class CompanyRela {
				
	/** 主键**/
	private Integer pkId;
			
	/** 合作公司ID**/
	private Integer pkCpyOperateId;
			
	/** 电站ID**/
	private Integer pkPowerstation;
			
	/** 桩ID**/
	private Integer pkElectricpile;

	private Integer cpyCompanyNumber;
	
	public void setPkId(Integer pkId){
		this.pkId = pkId;
	} 
	
	public Integer getPkId(){
		return pkId;
	} 
			
	public void setPkCpyOperateId(Integer pkCpyOperateId){
		this.pkCpyOperateId = pkCpyOperateId;
	} 
	
	public Integer getPkCpyOperateId(){
		return pkCpyOperateId;
	} 
			
	public void setPkPowerstation(Integer pkPowerstation){
		this.pkPowerstation = pkPowerstation;
	} 
	
	public Integer getPkPowerstation(){
		return pkPowerstation;
	} 
			
	public void setPkElectricpile(Integer pkElectricpile){
		this.pkElectricpile = pkElectricpile;
	} 
	
	public Integer getPkElectricpile(){
		return pkElectricpile;
	}

	public Integer getCpyCompanyNumber() {
		return cpyCompanyNumber;
	}

	public void setCpyCompanyNumber(Integer cpyCompanyNumber) {
		this.cpyCompanyNumber = cpyCompanyNumber;
	} 
}
