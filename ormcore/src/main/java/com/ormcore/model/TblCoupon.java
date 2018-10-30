package com.ormcore.model;

public class TblCoupon {
				
	/** 主键**/
	private Integer pkCoupon;
			
	/** 活动表主键**/
	private Integer pkActivity;
			
	/** 优惠券品种主键**/
	private Integer pkCouponvariety;
			
	/** 优惠券状态（1-已使用）**/
	private Integer cpStatus;
			
	/** 电桩限制（1-仅限交流电桩，2-仅限直流电装，3-不限充电桩）**/
	private Integer cpLimitation;
			
	/** 优惠券面值**/
	private Integer cpCouponvalue;
			
	/** 优惠券使用条件**/
	private Integer cpCouponcondition;
			
	/** 优惠码**/
	private String cpCouponcode;
			
	/** 持有人（用户ID）**/
	private Integer cpUserid;
			
	/** 生效时间**/
	private java.util.Date cpBegindate;
			
	/** 失效时间**/
	private java.util.Date cpEnddate;
			
	/** 创建时间**/
	private java.util.Date cpCreatedate;
			
	/** 修改时间**/
	private java.util.Date cpUpdatedate;
	private String cpRate;
	/** 可用时段**/
	private String usingTimeRange;

	public void setPkCoupon(Integer pkCoupon){
		this.pkCoupon = pkCoupon;
	} 
	
	public Integer getPkCoupon(){
		return pkCoupon;
	} 
			
	public void setPkActivity(Integer pkActivity){
		this.pkActivity = pkActivity;
	} 
	
	public Integer getPkActivity(){
		return pkActivity;
	} 
			
	public void setPkCouponvariety(Integer pkCouponvariety){
		this.pkCouponvariety = pkCouponvariety;
	} 
	
	public Integer getPkCouponvariety(){
		return pkCouponvariety;
	} 
			
	public void setCpStatus(Integer cpStatus){
		this.cpStatus = cpStatus;
	} 
	
	public Integer getCpStatus(){
		return cpStatus;
	} 
			
	public void setCpLimitation(Integer cpLimitation){
		this.cpLimitation = cpLimitation;
	} 
	
	public Integer getCpLimitation(){
		return cpLimitation;
	} 
			
	public void setCpCouponvalue(Integer cpCouponvalue){
		this.cpCouponvalue = cpCouponvalue;
	} 
	
	public Integer getCpCouponvalue(){
		return cpCouponvalue;
	} 
			
	public void setCpCouponcondition(Integer cpCouponcondition){
		this.cpCouponcondition = cpCouponcondition;
	} 
	
	public Integer getCpCouponcondition(){
		return cpCouponcondition;
	} 
			
	public void setCpCouponcode(String cpCouponcode){
		this.cpCouponcode = cpCouponcode;
	} 
	
	public String getCpCouponcode(){
		return cpCouponcode;
	} 
			
	public void setCpUserid(Integer cpUserid){
		this.cpUserid = cpUserid;
	} 
	
	public Integer getCpUserid(){
		return cpUserid;
	} 
			
	public void setCpBegindate(java.util.Date cpBegindate){
		this.cpBegindate = cpBegindate;
	} 
	
	public java.util.Date getCpBegindate(){
		return cpBegindate;
	} 
			
	public void setCpEnddate(java.util.Date cpEnddate){
		this.cpEnddate = cpEnddate;
	} 
	
	public java.util.Date getCpEnddate(){
		return cpEnddate;
	} 
			
	public void setCpCreatedate(java.util.Date cpCreatedate){
		this.cpCreatedate = cpCreatedate;
	} 
	
	public java.util.Date getCpCreatedate(){
		return cpCreatedate;
	} 
			
	public void setCpUpdatedate(java.util.Date cpUpdatedate){
		this.cpUpdatedate = cpUpdatedate;
	} 
	
	public java.util.Date getCpUpdatedate(){
		return cpUpdatedate;
	}

	public String getCpRate() {
		return cpRate;
	}

	public void setCpRate(String cpRate) {
		this.cpRate = cpRate;
	}

	public String getUsingTimeRange() {
		return usingTimeRange;
	}

	public void setUsingTimeRange(String usingTimeRange) {
		this.usingTimeRange = usingTimeRange;
	}
}
