package com.ec.epcore.cache;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.EpConstants;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.service.EpBespokeService;
import com.ec.epcore.service.RateService;
import com.ec.epcore.service.UserService;
import com.ec.net.message.AliSMS;
import com.ec.net.message.MobiCommon;
import com.ec.utils.DateUtil;

public class BespCache {
	
	private static final Logger logger = LoggerFactory.getLogger(BespCache.class);
	
	private long bespId;
	private String bespNo;
	private Integer usrId;
	private String usrAccount;
	private String epCode;
	private int epGunNo;
	private int payMode;//预支付方式.
	
	private UserOrigin userOrigin;

	private short redo;
	private int buyTimes;//当次预约和续约有效
	
	private long clientSt;//精确到毫秒
	//服务端时间为准
	private long acceptTime;//从API接受到的时间
	private long startTime;//开始时间精确到秒
	private long endTime;//结束时间精确到秒
	private long realEndTime;//真实结束时间，精确毫秒.记录最小的结束时间
	
	private BigDecimal fronzeAmt;
	
	private BigDecimal rate;
	
	private int  status;//定义到
	
	private boolean isExpirWarn;
	
	public UserOrigin getUserOrigin() {
		return userOrigin;
	}

	public void setUserOrigin(UserOrigin userOrigin) {
		this.userOrigin = userOrigin;
	}

	public BespCache(){
		rate =new BigDecimal(0.0);
		status=0;
		isExpirWarn=false;
		payMode= EpConstants.P_M_FIRST;
	}
	
	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
		
	}
	
	

	public String getEpCode() {
		return epCode;
	}

	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}

	public int getEpGunNo() {
		return epGunNo;
	}

	public void setEpGunNo(int epGunNo) {
		this.epGunNo = epGunNo;
	}
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
        sb.append("预约信息:\n");
        
        sb.append(",{电桩编号 = ").append(epCode).append("}\n");
   	    sb.append(",{枪编号 = ").append(epGunNo).append("}\n");
        
        sb.append("{预约pkId = ").append(bespId).append("}\n");
        sb.append(",{预约编号 = ").append(bespNo).append("}\n");
        sb.append(",{费率 = ").append(rate).append("}\n");
        
        sb.append("{用户ID = ").append(usrId).append("}\n");
        sb.append("{用户账号 = ").append(usrAccount).append("}\n");
        
 
        sb.append(",{预约标识 = ").append(redo).append(getRedoDesc(redo)).append("}\n");
        sb.append(",{买断时间(分钟) = ").append(buyTimes).append("}\n");
 
        String sTime= DateUtil.StringYourDate(DateUtil.toDate(startTime*1000));
        sb.append(",{开始时间 = ").append(sTime).append("}\n");
        
        sTime= DateUtil.StringYourDate(DateUtil.toDate(endTime*1000));
        
        sb.append(",{结束时间  = ").append(sTime).append("}\n");
        
        sTime= DateUtil.StringYourDate(DateUtil.toDate(realEndTime*1000));
        sb.append(",{实际结束时间 = ").append(sTime).append("}\n");
        
        sb.append(",{用户来源 = ").append(userOrigin).append("}\n");
	
        sb.append(",{状态 = ").append(status).append(EpBespokeService.getBespokeMemDesc(status)).append("}\n");
        
        sb.append(",{付款方式 = ").append(payMode).append(RateService.getPayModeDesc(payMode)).append("}\n");
        
        sb.append(",{是否发送15分钟倒计时= ").append(isExpirWarn).append("}\n");
        
        
   		return sb.toString();
	}

	public  static String getRedoDesc(short redo)
	{
		String desc="";
		switch(redo)
		{
			case 0:
				desc = "预约";
				break;
			case 1:
				desc = "续约";
				break;
			default:
				desc = "未知";
				break;
		}
	   return desc;
	}
	
	public long getBespId() {
		return bespId;
	}

	public void setBespId(long bespId) {
		this.bespId = bespId;
	}

	public String getBespNo() {
		return bespNo;
	}

	public void setBespNo(String bespNo) {
		this.bespNo = bespNo;
	}

	

	public Integer getAccountId() {
		return usrId;
	}

	public void setAccountId(Integer accountId) {
		this.usrId = accountId;
	}

	public String getAccount() {
		return usrAccount;
	}

	public void setAccount(String account) {
		this.usrAccount = account;
	}
	
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public short getRedo() {
		return redo;
	}


	public void setRedo(short redo) {
		this.redo = redo;
	}


	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public long getClientSt() {
		return clientSt;
	}

	public void setClientSt(long clientSt) {
		this.clientSt = clientSt;
	}
	

	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(long realEndTime) {
		this.realEndTime = realEndTime;
	}
	
	public void setRealEndTime2(long realEndTime) {
		
		this.realEndTime = realEndTime;
	}

	public BigDecimal getFronzeAmt() {
		return fronzeAmt;
	}

	public void setFronzeAmt(BigDecimal fronzeAmt) {
		this.fronzeAmt = fronzeAmt;
	}


	public boolean isExpirWarn() {
		return isExpirWarn;
	}

	public void setExpirWarn(boolean isExpirWarn) {
		this.isExpirWarn = isExpirWarn;
	}

	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

	public long getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(long acceptTime) {
		this.acceptTime = acceptTime;
	}
	public void onBespokeExpiringWarn(String address,String userAccount)
	{
		logger.debug("onBespokeExpiringWarn");
		if(isExpirWarn())
		{
			return ;
		}
		logger.debug("onBespokeExpiringWarn,userAccount:{}",userAccount);
		setExpirWarn(true);
		
		if(GameConfig.sms ==1)
		{
			
			String content = MessageFormat.format("预约即将到期：尊敬的用户，您的预约还剩{0}分钟，请及时前往{1}充电。温馨提示：如逾期，该桩可能会被其他小伙伴占用哦",15, address);
			MobiCommon.sendWanMatMessage(content,userAccount);
		}
		else
		{
			
			HashMap<String,Object>  params=new HashMap<String,Object>();
			params.put("time", "15");
			params.put("addr", address);
			
			JSONObject jsonObject = JSONObject.fromObject(params);
			
			boolean flag=AliSMS.sendAliSMS(userAccount, "SMS_17110100", jsonObject.toString());
			if(!flag)
			{
				logger.debug("SMS:onBespokeExpiringWarn fail,userAccount:{}",userAccount);
			}
			
		}
	
	}
	public void onRedoBespokeSuccess()
	{
		setRedo((short)0);
		//预约成功，告警信息需要重置
		setExpirWarn(false);
		
		long addingTime = 0;
		BigDecimal addingFronzeAmt= new BigDecimal(0.0);
		
		addingTime  =getBuyTimes() * 60;// 秒
		addingFronzeAmt = RateService.calcBespAmt(getRate(), addingTime / 60);
		
		long et = getEndTime() + getBuyTimes()* 60;

		setEndTime(et);
		setRealEndTime(et);
		setFronzeAmt(getFronzeAmt().add(addingFronzeAmt));
		setBuyTimes(0);// 续约成功，买断时间归零
		
		if(getPayMode() ==  EpConstants.P_M_FIRST)
		{
			UserService.subAmt(getAccountId(), addingFronzeAmt,this.bespNo);
		}
		
		logger.info("[bespoke] onRedobespokeSuccess,epCode:{},epGunNo:{},account:{},BespAmt:{}",new Object[]{epCode,epGunNo,this.getAccount(),addingFronzeAmt});
		
	}
	public void onBespokeSuccess()
	{
		setStatus(EpConstants.BESPOKE_STATUS_LOCK);
		
		long addingTime = 0;
		
		BigDecimal addingFronzeAmt= new BigDecimal(0.0);
		addingTime  = getEndTime()- getStartTime();// 秒
		addingFronzeAmt = RateService.calcBespAmt(getRate(), addingTime / 60);
		setFronzeAmt(addingFronzeAmt);
		if(getPayMode() ==  EpConstants.P_M_FIRST)
		{
			logger.info("[bespoke] subAmt accountId:{},BespNo:{},BespAmt:{}",
	        		   new Object[]{getAccountId(),getBespNo(),addingFronzeAmt});
	            
			UserService.subAmt(getAccountId(), addingFronzeAmt,getBespNo());
		}
		
		
	}
}
