package com.ormcore.model;

/**
 * @description:
 * @author: autoCode
 * @history:
 */
public class TblPartner {

	/** 主键**/
	private Integer pkPartner;
			
	/** 合格充电桩伙伴名字**/
	private String partnername;
			
	/** 合格充电桩伙伴Key**/
	private String partnerkey;
			
	/** 合格充电桩伙伴Token**/
	private String partnertoken;
			
	/** 合格充电桩伙伴注册日期**/
	private java.util.Date registerdate;
			
	/** 合格充电桩伙伴为我们分配的编号**/
	private String partnerclientid;
			
	/** 1:天;2:小时**/
	private Integer partnerupdatecycletype;
			
	/** 数值**/
	private Integer partnerupdatecyclevalue;
			
	/** 合格充电桩伙伴API服务IP**/
	private String partnerclientip;
			
	/** 合格充电桩伙伴API服务Port**/
	private Integer partnerclientport;
			
	/** 合格充电桩伙伴API服务Key**/
	private String partnerclientkey;
			
	/** 先付费后付费(1:先付费,2:后付费)**/
	private Integer paymod;
			
	/** **/
	private java.util.Date createdate;
			
	/** 修改时间**/
	private java.util.Date updatedate;
			
	/** 第三方获取token地址**/
	private String tokenurl;
			
	/** 第三方获取token密匙**/
	private String tokensecret;
			
	/** 接口传递密匙：消息密匙|消息密匙初始向量|签名密匙**/
	private String secret;
			
	/** 推送启动充电结果url**/
	private String pushstarturl;
			
	/** 推送停止充电结果url**/
	private String pushstopurl;
			
	/** 推送充电订单url**/
	private String pushorderurl;
			
	/** 推送充电设备状态url**/
	private String pushequipstatusurl;
			
	/** 推送订单对账结果url**/
	private String pushordercheckurl;
			
	/** 推送充电状态url**/
	private String pushchargestatusurl;
			
	/** 关联tbl_company 主键**/
	private Long cpyId;
					
	public void setPkPartner(Integer pkPartner){
		this.pkPartner = pkPartner;
	} 
	
	public Integer getPkPartner(){
		return pkPartner;
	} 
			
	public void setPartnername(String partnername){
		this.partnername = partnername;
	} 
	
	public String getPartnername(){
		return partnername;
	} 
			
	public void setPartnerkey(String partnerkey){
		this.partnerkey = partnerkey;
	} 
	
	public String getPartnerkey(){
		return partnerkey;
	} 
			
	public void setPartnertoken(String partnertoken){
		this.partnertoken = partnertoken;
	} 
	
	public String getPartnertoken(){
		return partnertoken;
	} 
			
	public void setRegisterdate(java.util.Date registerdate){
		this.registerdate = registerdate;
	} 
	
	public java.util.Date getRegisterdate(){
		return registerdate;
	} 
			
	public void setPartnerclientid(String partnerclientid){
		this.partnerclientid = partnerclientid;
	} 
	
	public String getPartnerclientid(){
		return partnerclientid;
	} 
			
	public void setPartnerupdatecycletype(Integer partnerupdatecycletype){
		this.partnerupdatecycletype = partnerupdatecycletype;
	} 
	
	public Integer getPartnerupdatecycletype(){
		return partnerupdatecycletype;
	} 
			
	public void setPartnerupdatecyclevalue(Integer partnerupdatecyclevalue){
		this.partnerupdatecyclevalue = partnerupdatecyclevalue;
	} 
	
	public Integer getPartnerupdatecyclevalue(){
		return partnerupdatecyclevalue;
	} 
			
	public void setPartnerclientip(String partnerclientip){
		this.partnerclientip = partnerclientip;
	} 
	
	public String getPartnerclientip(){
		return partnerclientip;
	} 
			
	public void setPartnerclientport(Integer partnerclientport){
		this.partnerclientport = partnerclientport;
	} 
	
	public Integer getPartnerclientport(){
		return partnerclientport;
	} 
			
	public void setPartnerclientkey(String partnerclientkey){
		this.partnerclientkey = partnerclientkey;
	} 
	
	public String getPartnerclientkey(){
		return partnerclientkey;
	} 
			
	public void setPaymod(Integer paymod){
		this.paymod = paymod;
	} 
	
	public Integer getPaymod(){
		return paymod;
	} 
			
	public void setCreatedate(java.util.Date createdate){
		this.createdate = createdate;
	} 
	
	public java.util.Date getCreatedate(){
		return createdate;
	} 
			
	public void setUpdatedate(java.util.Date updatedate){
		this.updatedate = updatedate;
	} 
	
	public java.util.Date getUpdatedate(){
		return updatedate;
	} 
			
	public void setTokenurl(String tokenurl){
		this.tokenurl = tokenurl;
	} 
	
	public String getTokenurl(){
		return tokenurl;
	} 
			
	public void setTokensecret(String tokensecret){
		this.tokensecret = tokensecret;
	} 
	
	public String getTokensecret(){
		return tokensecret;
	} 
			
	public void setSecret(String secret){
		this.secret = secret;
	} 
	
	public String getSecret(){
		return secret;
	} 
			
	public void setPushstarturl(String pushstarturl){
		this.pushstarturl = pushstarturl;
	} 
	
	public String getPushstarturl(){
		return pushstarturl;
	} 
			
	public void setPushstopurl(String pushstopurl){
		this.pushstopurl = pushstopurl;
	} 
	
	public String getPushstopurl(){
		return pushstopurl;
	} 
			
	public void setPushorderurl(String pushorderurl){
		this.pushorderurl = pushorderurl;
	} 
	
	public String getPushorderurl(){
		return pushorderurl;
	} 
			
	public void setPushequipstatusurl(String pushequipstatusurl){
		this.pushequipstatusurl = pushequipstatusurl;
	} 
	
	public String getPushequipstatusurl(){
		return pushequipstatusurl;
	} 
			
	public void setPushordercheckurl(String pushordercheckurl){
		this.pushordercheckurl = pushordercheckurl;
	} 
	
	public String getPushordercheckurl(){
		return pushordercheckurl;
	} 
			
	public void setPushchargestatusurl(String pushchargestatusurl){
		this.pushchargestatusurl = pushchargestatusurl;
	} 
	
	public String getPushchargestatusurl(){
		return pushchargestatusurl;
	} 
			
	public void setCpyId(Long cpyId){
		this.cpyId = cpyId;
	} 
	
	public Long getCpyId(){
		return cpyId;
	} 
	}
