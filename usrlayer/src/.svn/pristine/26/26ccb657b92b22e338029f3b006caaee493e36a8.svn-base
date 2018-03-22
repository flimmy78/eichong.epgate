package com.ec.usrcore.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCache {
	
	private static final Logger logger = LoggerFactory.getLogger(UserCache.class);
	
	
	private int id; //
	private String account; //

	/**
	 * 因为可以多充，所以对用户来说，没有当前充电.
	 */
	private Map<String, ChargeCache> chargeList = new ConcurrentHashMap<String,ChargeCache>();
	
	
	private int level;//TODO,移动到充电开始的时候
	
	public UserCache(int userId,String userAccount,int userLevel)
	{
		id = userId; //
		account =userAccount; //
		level = userLevel;
		
		
		init();
	}
		/**
	 * 装载该用户未完成的业务
	 * 1.装载用户未完成的预约
	 * 2.装载用户未完成的充电
	 */
	private void init()
	{
		//EpBespokeService.getUnStopBespokeByUserIdFromDb(this);
		//EpChargeService.getUnFinishChargeByUserIdFromDb(this);
		
	}
	public void removeCharge(String chargeSerialNo)
	{
		if(chargeSerialNo!=null && chargeSerialNo.length()>0)
		{
			chargeList.remove(chargeSerialNo);
		}
	}
	public void addCharge(ChargeCache cache)
	{
		chargeList.put(cache.getChargeSerialNo(), cache);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void clean()
	{
        level =0;
	}
	
	
		@Override
	public String toString() {
		
	
		final StringBuilder sb = new StringBuilder();
        sb.append("UserCache");
        sb.append("{id=").append(id).append("}\n");
        sb.append(",{account=").append(account).append("}\n");
		
		int chargeCount = chargeList.size();
		if(chargeCount>0)
		{
			sb.append("charge list!count").append(chargeList.size()).append(":\n");
			Iterator iter = chargeList.entrySet().iterator();
			
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				ChargeCache o=(ChargeCache) entry.getValue();
				if(o==null)
					continue;
				sb.append(o.toString());
			}
		}
    	
   		return sb.toString();
	}

	/**
	 * 只有电桩失联并且预冻结的充电
	 * @return
	 */
	public ChargeCache getHistoryCharge(String epGunNo)
	{
		Iterator iter = chargeList.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ChargeCache o=(ChargeCache) entry.getValue();
			
			String s= o.getEpCode() + o.getEpGunNo();
			if(s.compareTo(epGunNo)==0)
				continue;
			//TODO
			//INetObject netObject = epGun.getEpNetObject();
			//if(netObject!=null && !netObject.isComm())
			//	return o;	
		}
		return null;
	}
	
	/**
	 * 
	 * @param usingGun
	 * @return
	 */
	public int canCharge(String usingGun,int chargingUsrId,int chargingUsrOrgNo,String chargingUsrIdentity,int pkCard,boolean allowMutliCharge)
	{
		Iterator iter = chargeList.entrySet().iterator();
		
		String epCode = "";
		int epGunNo = 0;
		int chargedUsrId = 0;
		String chargedUsrIdentity = "";
		
		int errorCode=0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ChargeCache o=(ChargeCache) entry.getValue();
			epCode = o.getEpCode();
			epGunNo = o.getEpGunNo();
			//chargedUsrId = o.getUserId();
			chargedUsrIdentity = o.getUsrIdentity();
			
			/*if(chargingUsrOrgNo == UserConstant.ORG_I_CHARGE ||
					(chargingUsrOrgNo != UserConstant.ORG_I_CHARGE&& chargingUsrIdentity.equals(chargedUsrIdentity)))
			{
				errorCode= canPauseStatOldCharge(epCode,epGunNo,usingGun);
				if(errorCode>0)
				{
					break;
				}
			}*/
			
		}
		
		logger.debug("canCharge,errorCode:{},usingGun:{},chargingUsrId:{},orgNo:{},chargingUsrIdentity:{},pkCard:{},allowMutliCharge:{}",
				new Object[]{errorCode,usingGun,chargingUsrId,chargingUsrOrgNo,chargingUsrIdentity, pkCard,allowMutliCharge,
				epCode,epGunNo,chargedUsrId,chargedUsrIdentity});
		
		return errorCode;
		
	}	
}
