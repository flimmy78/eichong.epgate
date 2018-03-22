package com.ec.usrcore.cache;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.Global;
import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.GunConstants;
import com.ec.constants.UserConstants;
import com.ec.logs.LogConstants;
import com.ec.usrcore.net.client.EpGateNetConnect;
import com.ec.usrcore.service.CacheService;
import com.ec.usrcore.service.EpChargeService;
import com.ec.usrcore.service.EpGateService;
import com.ec.usrcore.service.EpGunService;
import com.ec.usrcore.service.UserService;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NumUtil;

public class EpGunCache {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGunCache.class.getName()));
	
	private int pkEpId; 
	
	private String  epCode;

	private int epGunNo;
	
	private int pkEpGunId;
	
	
	int currentType;
	
	private int status;
	
	//private BespCache bespCache;
	
	private ChargeCache chargeCache;
	
	private boolean isNeedFronzeAmt;
	
	private long lastUPTime;//手机更新时间
	
	
	private AuthUserCache auth;

	public EpGunCache(int pkEpId,String epCode,int epGunNo){
		
		this.pkEpId =pkEpId;
		this.epCode =epCode;
		this.epGunNo =epGunNo;
			
		lastUPTime =0;//手机更新时间
		currentType =0;	
	}
	
	public int getPkEpGunId() {
		return pkEpGunId;
	}

	public void setPkEpGunId(int pkEpGunId) {
		this.pkEpGunId = pkEpGunId;
	}

	

	public int getPkEpId() {
		return pkEpId;
	}
	
	public int getCurrentType() {
		return currentType;
	}

	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}

	public int getCurUserId() {
		
		if( chargeCache!=null && chargeCache.getUserId()>0)
			return chargeCache.getUserId();
		
		if( auth!=null && auth.getUsrId()>0)
			return auth.getUsrId();
		
		return 0;
	}
	
	public void setPkEpId(int pkEpId) {
		this.pkEpId = pkEpId;
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

	public int getStatus() {
		return status;
	}

	
	

	public ChargeCache getChargeCache() {
		 
		ChargeCache retChargeCache= null;
		retChargeCache = this.chargeCache;
         
		return retChargeCache;
	}

	public void setChargeCache(ChargeCache chargeCache) {
		 
		
		this.chargeCache = chargeCache;
	}

	

	public boolean isNeedFronzeAmt() {
		return isNeedFronzeAmt;
	}

	public void setNeedFronzeAmt(boolean isNeedFronzeAmt) {
		this.isNeedFronzeAmt = isNeedFronzeAmt;
	}
	public int checkSingleYx(int value)
	{
		int ret=0;
		if(value!=0 && value!=1)
		{
			ret = -1;
		}
		return ret;
	}
	
	public ChargingInfo getCharingInfo()
	{
		
		return null;
	}

	public void modifyStatus(int status,boolean modifyDb)
	{
		logger.debug(LogUtil.addExtLog("this.status|status"),this.status,status);
		this.status = status;
		
		if(modifyDb)
		{
			EpGunService.updateGunState(this.getPkEpGunId(), status);
		}
	}
	
	public void cleanChargeInfo()
	{
		if(chargeCache!=null)
		{
			
			int userId = chargeCache.getUserId();
			
			if(userId>0)
			{
				UserCache u2= UserService.getUserCache(userId, 2);
				if(u2!=null )
				{
					u2.clean();
					CacheService.putUserCache(u2);
				}
			}
		}
	}
		
	public int startChargeAction(String token,int orgNo,String userIdentity,String epCode,int epGunNo,
			short chargeStyle,int frozenAmt,int payMode, int watchPrice,String carCode, String vinCode, int serverType)
	{
		if (payMode == EpConstants.P_M_FIRST && orgNo == UserConstants.ORG_I_CHARGE)
		{
			BigDecimal bdRemainAmt = UserService.getRemainBalance(Integer.valueOf(userIdentity), serverType);
			
			//100倍后转为整数
			bdRemainAmt = bdRemainAmt.multiply(Global.DecTime2);
			int nRemainAmt= NumUtil.BigDecimal2ToInt(bdRemainAmt);
			BigDecimal bdFrozenAmt = NumUtil.intToBigDecimal2(frozenAmt);
			//充电冻结金额
			logger.info(LogUtil.addBaseExtLog("bdRemainAmt|bdFrozenAmt|payMode"),
					new Object[]{LogConstants.FUNC_START_CHARGE,epCode,epGunNo,orgNo,userIdentity,bdRemainAmt,bdFrozenAmt,payMode});
			
			//冻结金额
			if(nRemainAmt<0 || frozenAmt<=0 || nRemainAmt<frozenAmt)
			{
				return ErrorCodeConstants.EPE_NO_ENOUGH_MONEY;
			}
		}

		EpGateNetConnect commClient = CacheService.getEpGate(epCode);
		commClient.setLastSendTime(DateUtil.getCurrentSeconds());
		EpGateService.sendCharge(commClient.getChannel(), epCode, epGunNo, userIdentity, chargeStyle, frozenAmt, payMode, watchPrice, orgNo, carCode, vinCode, token);
	
		return 0;
	}
	
	public int stopChargeAction(String token,int orgNo,String userIdentity,EpGateNetConnect commClient)
	{
		commClient.setLastSendTime(DateUtil.getCurrentSeconds());
		EpGateService.sendStopCharge(commClient.getChannel(), epCode, epGunNo, orgNo, userIdentity, token);
		
		return 0;
	}
	
	public boolean loadUnFinishedWork(int serverType)
	{
		this.chargeCache = null;
		
		//3.取最新的未完成的充电记录
		ChargeCache tmpChargeCache=EpChargeService.GetUnFinishChargeCache(epCode, epGunNo);
		if(tmpChargeCache!=null)
		{
			logger.debug(LogUtil.addExtLog("tmpChargeCache.getStatus()"),tmpChargeCache.getStatus());
			//String chargeAccount = tmpChargeCache.getAccount();
			//装载未完成充电用户
			UserCache userCache = UserService.getUserCache(tmpChargeCache.getUserId(), serverType);
			tmpChargeCache.setEpCode(epCode);
			tmpChargeCache.setEpGunNo(epGunNo);
			
			if(userCache!=null)
			{
				logger.error(LogUtil.addExtLog("epCode|epGunNo|set chargeInfo to userCache"),new Object[]{epCode,epGunNo,userCache});
				if (userCache.getId() == tmpChargeCache.getUserId()) userCache.addCharge(tmpChargeCache);
				tmpChargeCache.getUserOrigin().setCmdChIdentity(userCache.getAccount());
			}
			this.chargeCache = tmpChargeCache;
		}
	
		logger.info(LogUtil.addExtLog("epCode|epGunNo|init status"),new Object[]{epCode,epGunNo,status});
		
		return true;
	}

	public AuthUserCache getAuth() {
		return auth;
	}

	public void setAuth(AuthUserCache auth) {
		this.auth = auth;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
        sb.append("EpGunCache\n");
        sb.append("电桩pkId = ").append(this.getPkEpId()).append("\n");
        sb.append("电桩编号 = ").append(this.getEpCode()).append("\n");
        
        sb.append("枪口pkId = ").append(this.pkEpGunId).append("\n");
        sb.append("枪口编号 = ").append(this.epGunNo).append("\n");
        
        String sTime= DateUtil.StringYourDate(DateUtil.toDate(lastUPTime*1000));
        sb.append("手机充电信息更新时间  = ").append(sTime).append("\n");
        
        
        
        if(this.chargeCache ==null)
        {
        	sb.append("无充电\n\r\n");
        }
        else
        {
        	sb.append(this.chargeCache.toString() ).append("\n");
        }
        
        if(this.auth ==null)
        {
        	sb.append("无鉴权用户\n");
        }
        else
        {
        	sb.append(this.auth.toString() ).append("\n");
        }
        
      
        return sb.toString();
	}
	
	public int canCharge(int startChargeStyle,int chargingUsrId,boolean init)
	{
		//先判断业务
		if( status == GunConstants.EP_GUN_STATUS_CHARGE||
			status == GunConstants.EP_GUN_STATUS_WAIT_CHARGE||
		    status == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED||
		    status== GunConstants.EP_GUN_STATUS_USER_AUTH)
		{
			int curUsrId = getCurUserId();
			
			 if(curUsrId<=0)
			 {
				 logger.error(LogUtil.addExtLog("innormal,status|curUsrId"),status,curUsrId);
				 return 0;
			 }
			 
			 if(status == GunConstants.EP_GUN_STATUS_CHARGE || status == GunConstants.EP_GUN_STATUS_WAIT_CHARGE)
			 {
				 if(curUsrId != chargingUsrId)
				 {
					 
					 //return ErrorCodeConstants.EPE_OTHER_CHARGING;
				 }
				 else
				 {
					 if(!init)
						 return ErrorCodeConstants.EPE_REPEAT_CHARGE;
				 }
			 }
			 else if(status == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
			 {
				 if(curUsrId != chargingUsrId)
				 {
					 return ErrorCodeConstants.EPE_OTHER_BESP;
				 }
				 return 0;
			 }
			 else
				return ErrorCodeConstants.USED_GUN;
		}

		return canCharge(startChargeStyle);
	}

	public int canCharge(int startChargeStyle)
	{
		if(status ==  GunConstants.EP_GUN_STATUS_SETTING)
		{
			return ErrorCodeConstants.EPE_IN_EP_OPER;
		}
		if(status == GunConstants.EP_GUN_STATUS_EP_OPER && startChargeStyle == EpConstants.CHARGE_TYPE_QRCODE)
		{
			return ErrorCodeConstants.EPE_IN_EP_OPER;
		}
		if(status ==  GunConstants.EP_GUN_STATUS_EP_UPGRADE)
		{
			return ErrorCodeConstants.EP_UPDATE;
		}
		if(status>30||
				 status == GunConstants.EP_GUN_STATUS_EP_INIT||
				 status== GunConstants.EP_GUN_STATUS_OFF_LINE||
			     status == GunConstants.EP_GUN_STATUS_STOP_USE)
		{
			return ErrorCodeConstants.EPE_GUN_FAULT;
		}
		return 0;
	}
	
}
