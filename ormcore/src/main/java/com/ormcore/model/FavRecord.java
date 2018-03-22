package com.ormcore.model;


/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class FavRecord {

				
	/** 主键**/
	private Long pkId;
			
	/** 订单号**/
	private String orderCode;
			
	/** 渠道ID**/
	private Long cpyId;
			
	/** 用户ID**/
	private Long userId;
			
	/** 账单科目ID**/
	private Long billAccountId;
			
	/** 优惠类型ID**/
	private Long favourableId;
			
	/** 被扣资金账户ID**/
	private Long accountId;
			
	/** 优惠费用（100 = 1元）**/
	private String favourableMoney;
			
	/** 修改人**/
	private String modifier;
			
	/** 创建时间**/
	private java.util.Date gmtCreator;
			
	/** 修改时间**/
	private java.util.Date gmtModified;
					
	public void setPkId(Long pkId){
		this.pkId = pkId;
	} 
	
	public Long getPkId(){
		return pkId;
	} 
			
	public void setOrderCode(String orderCode){
		this.orderCode = orderCode;
	} 
	
	public String getOrderCode(){
		return orderCode;
	} 
			
	public void setCpyId(Long cpyId){
		this.cpyId = cpyId;
	} 
	
	public Long getCpyId(){
		return cpyId;
	} 
			
	public void setUserId(Long userId){
		this.userId = userId;
	} 
	
	public Long getUserId(){
		return userId;
	} 
			
	public void setBillAccountId(Long billAccountId){
		this.billAccountId = billAccountId;
	} 
	
	public Long getBillAccountId(){
		return billAccountId;
	} 
			
	public void setFavourableId(Long favourableId){
		this.favourableId = favourableId;
	} 
	
	public Long getFavourableId(){
		return favourableId;
	} 
			
	public void setAccountId(Long accountId){
		this.accountId = accountId;
	} 
	
	public Long getAccountId(){
		return accountId;
	} 
			
	public void setFavourableMoney(String favourableMoney){
		this.favourableMoney = favourableMoney;
	} 
	
	public String getFavourableMoney(){
		return favourableMoney;
	} 
			
	public void setModifier(String modifier){
		this.modifier = modifier;
	} 
	
	public String getModifier(){
		return modifier;
	} 
			
	public void setGmtCreator(java.util.Date gmtCreator){
		this.gmtCreator = gmtCreator;
	} 
	
	public java.util.Date getGmtCreator(){
		return gmtCreator;
	} 
			
	public void setGmtModified(java.util.Date gmtModified){
		this.gmtModified = gmtModified;
	} 
	
	public java.util.Date getGmtModified(){
		return gmtModified;
	} 
	}
