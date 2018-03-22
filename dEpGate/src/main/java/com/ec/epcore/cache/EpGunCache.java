package com.ec.epcore.cache;

import com.cooperate.CooperateFactory;
import com.cooperate.IPush;
import com.cooperate.Push;
import com.cooperate.RealDataRT;
import com.ec.config.Global;
import com.ec.constants.*;
import com.ec.cooperate.measurePoint;
import com.ec.cooperate.real3rdFactory;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.net.proto.*;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.service.*;
import com.ec.logs.LogConstants;
import com.ec.net.message.AliSMS;
import com.ec.net.message.MobiCommon;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.SingleInfo;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.client.ITcpClient;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ec.utils.NumUtil;
import com.ec.utils.StringUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.RateInfo;
import com.ormcore.model.TblBespoke;
import com.ormcore.model.TblChargingOrder;
import com.ormcore.model.TblElectricPileGun;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EpGunCache {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpGunCache.class.getName()));
	
	private int concentratorId;

	private int pkEpId; 
	
	private String  epCode;
	
	private int pkEpGunId;
	
	private int epGunNo;
	
	int currentType;
	
	private int status;
	
	private ITcpClient epNetObject;//电桩网络连接
	
//	private ReadWriteLock myLock;//执行操作所需的锁对象 
	
	private BespCache bespCache;
	
	private ChargeCache chargeCache;
	
	private AuthUserCache authCache;
	
	RealChargeInfo realChargeInfo;
	
	
	private String identyCode;// 识别码
	
	private long createIdentyCodeTime;//生成识别码的时间
	
	
	private long lastUDTime;//更新到数据库的信息
	
	private long lastUPTime;//手机更新时间
	
		
	private long lastSendToMonitorTime; //记录上一次发送给监控系统的时间
	
	RealDataRT sendInfo3rd=null;	
	
	//充电时 保存发送到监控中心的遥测和变遥测 
	Map<Integer, SingleInfo> changeYcMap= new ConcurrentHashMap<Integer, SingleInfo>();
	Map<Integer, SingleInfo> changeVarYcMap= new ConcurrentHashMap<Integer, SingleInfo>();
	
	
	
	


    private boolean statusChangeOfMonitor=false;
    public boolean isStatusChangeOfMonitor() {
		return statusChangeOfMonitor;
	}
	public void setStatusChangeOfMonitor(boolean statusChangeOfMonitor) {
		this.statusChangeOfMonitor = statusChangeOfMonitor;
	}

	
	
	public long getLastSendToMonitorTime() {
		return lastSendToMonitorTime;
	}
	public AuthUserCache getAuthCache() {
		return authCache;
	}
	public void setAuthCache(AuthUserCache authCache) {
		this.authCache = authCache;
	}
	
	

	public void setLastSendToMonitorTime(long lastSendToMonitorTime) {
		this.lastSendToMonitorTime = lastSendToMonitorTime;
	}

	public EpGunCache(){
		
		concentratorId=0;

		pkEpId=0; 
		
		epCode="";
		
		pkEpGunId=0;
		
		epGunNo=0;
		
		currentType=0;
		
		status=0;
		
		epNetObject=null;//电桩网络连接
		
		bespCache=null;
		
		chargeCache=null;
		
		authCache=null;
		
		realChargeInfo=null;

		identyCode="";// 识别码
		
		createIdentyCodeTime=0;//生成识别码的时间
		
		lastUDTime=0;//更新到数据库的信息
		
		lastUPTime=0;//手机更新时间
		
		lastSendToMonitorTime=0; //记录上一次发送给监控系统的时间
		
	
	}
	
	
	public long getCreateIdentyCodeTime() {
		return createIdentyCodeTime;
	}
	public void setCreateIdentyCodeTime(long createIdentyCodeTime) {
		this.createIdentyCodeTime = createIdentyCodeTime;
	}
	
	public String getIdentyCode() {
		return identyCode;
	}
	
	public void setIdentyCode(String identyCode) {
		this.identyCode = identyCode;
	}
	
	
	public int getPkEpGunId() {
		return pkEpGunId;
	}

	public void setPkEpGunId(int pkEpGunId) {
		this.pkEpGunId = pkEpGunId;
	}

	public int getConcentratorId() {
		return concentratorId;
	}

	public void setConcentratorId(int concentratorId) {
		this.concentratorId = concentratorId;
	}

	public int getPkEpId() {
		return pkEpId;
	}
	

	

	public ITcpClient getEpNetObject() {
		return epNetObject;
	}

	public void setEpNetObject(ITcpClient epNetObject) {
		onNetStatus(1);
		this.epNetObject = epNetObject;
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

	public BespCache getBespCache() {
		
		 
		 BespCache retBespCache= null;
		 retBespCache = this.bespCache;
         //释放读锁   
		return retBespCache;
	}
	

	public void setBespCache(BespCache bespCache) {
        this.bespCache = bespCache; 
	}

	public ChargeCache getChargeCache() {
		
		return chargeCache;
	}

	public void setChargeCache(ChargeCache chargeCache) {
		 
		
		this.chargeCache = chargeCache;
	}

	public RealChargeInfo getRealChargeInfo() {
		return realChargeInfo;
	}

	public void setRealChargeInfo(RealChargeInfo realChargeInfo) {
		this.realChargeInfo = realChargeInfo;
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
	
	public int get_gun2carLinkStatus()
	{
		return this.realChargeInfo.getLinkCarStatus();
	}
	

	public void onRealDataChange(Map<Integer, SingleInfo> pointMap,int type)
	{
		if(type!=1 && type!=3 && type!=11 &&type!=132)
		{
			logger.error("onRealDataChange type error type:{},epCode:{},epGunNo:{}",
					new Object[]{type,epCode,epGunNo});
			return;
		}
		int currentType = realChargeInfo.getCurrentType();
		if(currentType!= EpConstants.EP_AC_TYPE && currentType !=  EpConstants.EP_DC_TYPE )
		{
			logger.error("onrealDataChange currentType error,epCode:{},epGunNo:{},currentType:{}",
					new Object[]{epCode,epGunNo,currentType});
			return ;
		}
		
		
		
		int oldEpWorkStatus= this.realChargeInfo.getWorkingStatus();
		int oldGun2CarStatus = this.realChargeInfo.getLinkCarStatus();
		int carPlaceStatus = this.realChargeInfo.getCarPlaceStatus();


		Map<Integer, SingleInfo> changePointMap= new ConcurrentHashMap<Integer, SingleInfo>();
		//1.处理实时信息到内存
		handleRealData(pointMap,type,changePointMap);
		//2.处理枪状态
		int newGun2CarStatus = this.realChargeInfo.getLinkCarStatus();
		if(newGun2CarStatus!=oldGun2CarStatus)
		{
			logger.debug("newGun2CarStatus:{},oldGun2CarStatus:{}",newGun2CarStatus,oldGun2CarStatus);
			this.handleGun2CarLinkStatus(newGun2CarStatus);
		}
	
	
		int newStatus=-1;
		
		//3.处理监控，第三方,用户端充电实时信息
		//如果电桩重来没有更新过工作状态，那么不处理工作状态
		if(realChargeInfo!=null && this.realChargeInfo.getWorkStatusUpdateTime()>0)//
		{
			newStatus = EpGunService.convertEpWorkStatus(this.realChargeInfo.getWorkingStatus());
			SingleInfo workPoint =pointMap.get(YXCConstants.YC_WORKSTATUS);
			if(newStatus!=-1 && workPoint != null)
			{
				this.onStatusChange(newStatus);
			}
			if(oldEpWorkStatus!=this.realChargeInfo.getWorkingStatus()&&
			      (oldEpWorkStatus==3))
			{
			   //停止充电给前端应答,临时
			    gotoStopChargeStatus(1,0);
			}
			if(oldEpWorkStatus!=this.realChargeInfo.getWorkingStatus())
			{
				this.handleGunWorkStatus(oldEpWorkStatus, this.realChargeInfo.getWorkingStatus());
			}
			//3.2
			onRealDataChangeToMonitor(oldEpWorkStatus,changePointMap,type);
			//e租网
            if (checkOrgNo(UserConstants.ORG_SHSTOP) == 1) {
                if (oldEpWorkStatus != this.realChargeInfo.getWorkingStatus()) {
                    this.handleChargeRealData(UserConstants.ORG_SHSTOP);
                }
            }
			if (checkOrgNo(UserConstants.ORG_TCEC_NANRUI) == 1) {
				if (deviceStatusChange(carPlaceStatus, oldEpWorkStatus, oldGun2CarStatus) > 0) {
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_NANRUI, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_NANRUI);
			}
			if (checkOrgNo(UserConstants.ORG_EC) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_EC, true);
				}
				this.handleChargeRealData(UserConstants.ORG_EC);
			}
			if (checkOrgNo(UserConstants.ORG_EVC) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_EVC, true);
				}
				this.handleChargeRealData(UserConstants.ORG_EVC);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_ECHONG) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_ECHONG, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_ECHONG);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_SHENZHEN) == 1) {
				if (deviceStatusChange(carPlaceStatus, oldEpWorkStatus, oldGun2CarStatus) > 0) {
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_SHENZHEN, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_SHENZHEN);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_BEIQI) == 1) {
				if (deviceStatusChange(carPlaceStatus, oldEpWorkStatus, oldGun2CarStatus) > 0) {
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_BEIQI, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_BEIQI);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_HESHUN) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_HESHUN, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_HESHUN);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_BAIDU) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_BAIDU, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_BAIDU);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_GUOWANG) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_GUOWANG, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_GUOWANG);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_BEIQI) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_BEIQI, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_BEIQI);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_NANRUI) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_NANRUI, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_NANRUI);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_NANCHONG) == 1) {
				if (deviceStatusChange(carPlaceStatus, oldEpWorkStatus, oldGun2CarStatus) > 0) {
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_NANCHONG, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_NANCHONG);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_ALIPAY) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_ALIPAY, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_ALIPAY);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_HAINAN) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_HAINAN, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_HAINAN);
			}
			if (checkOrgNo(UserConstants.ORG_TCEC_XIAOJU) == 1) {
				if(deviceStatusChange(carPlaceStatus,oldEpWorkStatus,oldGun2CarStatus)>0)
				{
					this.handleSignleOrgNo(UserConstants.ORG_TCEC_XIAOJU, true);
				}
				this.handleChargeRealData(UserConstants.ORG_TCEC_XIAOJU);
			}

			//3.4
			handleUserRealInfo();
			
		}
	}
	public int deviceStatusChange(int carPlaceStatus,int oldEpWorkStatus,int oldGun2CarStatus)
	{
		int ret=0;
		if(oldEpWorkStatus !=this.realChargeInfo.getWorkingStatus() ||
				oldGun2CarStatus !=this.realChargeInfo.getLinkCarStatus()||
						carPlaceStatus != this.realChargeInfo.getCarPlaceStatus())
		{
			ret =1;
		}
		return ret;
	}
		
	public int checkOrgNo(int orgNo)
	{
		if (!CooperateFactory.isCooperate(orgNo)) return 0;

		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if(epCache==null)
		{
			return 0;
		}
		//合作方电桩过滤
		if (!epCache.canOrgOperate(orgNo)) return 0;
		if(epCache.getDeleteFlag()==1)
		{
			return 0;
		}
		
		return 1;
	}

	private int checkTCOrgNo(int orgNo)
	{
		if (orgNo == UserConstants.ORG_EC || orgNo == UserConstants.ORG_CCZC || orgNo == UserConstants.ORG_TJD ||
				orgNo == UserConstants.ORG_TCEC_ECHONG || orgNo == UserConstants.ORG_EVC || orgNo == UserConstants.ORG_TCEC_SHENZHEN ||
				orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_HESHUN || orgNo == UserConstants.ORG_TCEC_BAIDU ||
				orgNo == UserConstants.ORG_TCEC_GUOWANG || orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_NANRUI ||
				orgNo == UserConstants.ORG_TCEC_NANCHONG || orgNo == UserConstants.ORG_TCEC_ALIPAY || orgNo == UserConstants.ORG_TCEC_HAINAN || orgNo == UserConstants.ORG_TCEC_XIAOJU)
			return checkOrgNo(orgNo);
		return 0;
	}

	private int checkPushOrgNo(int orgNo)
	{
		if (orgNo == UserConstants.ORG_EC || orgNo == UserConstants.ORG_SHSTOP || orgNo == UserConstants.ORG_TJD ||
				orgNo == UserConstants.ORG_TCEC_ECHONG || orgNo == UserConstants.ORG_EVC || orgNo == UserConstants.ORG_TCEC_SHENZHEN ||
				orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_HESHUN || orgNo == UserConstants.ORG_TCEC_BAIDU ||
				orgNo == UserConstants.ORG_TCEC_GUOWANG || orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_NANRUI ||
				orgNo == UserConstants.ORG_TCEC_NANCHONG || orgNo == UserConstants.ORG_TCEC_ALIPAY || orgNo == UserConstants.ORG_TCEC_HAINAN || orgNo == UserConstants.ORG_TCEC_XIAOJU)
			return checkOrgNo(orgNo);
		return 0;
	}

	/**
	 * 处理实时数据到内存对象
	 * @param pointMap
	 * @param type
	 * @param changePointMap
	 */
	private void handleRealData(Map<Integer, SingleInfo> pointMap,int type,Map<Integer, SingleInfo> changePointMap)
	{
		Iterator iter = pointMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			int pointAddr = ((Integer)entry.getKey()).intValue();
			SingleInfo info=(SingleInfo) entry.getValue();
			int ret=0;
			
			if(currentType== EpConstants.EP_AC_TYPE)
				ret = ((RealACChargeInfo)realChargeInfo).setFieldValue(pointAddr,info);
			else
				ret = ((RealDCChargeInfo)realChargeInfo).setFieldValue(pointAddr,info);
			
			if((type==3) && info.getAddress()>YXCConstants.YX_2_START_POS&& info.getAddress()<YXCConstants.YC_START_POS)
			{
				info.setAddress(info.getAddress()-YXCConstants.YX_2_START_POS);
			}
			else if((type==11 ) && info.getAddress()>YXCConstants.YC_START_POS&& info.getAddress()<YXCConstants.YC_VAR_START_POS)
			{
				info.setAddress(info.getAddress()-YXCConstants.YC_START_POS);
			}
			else if((type==132) && info.getAddress()>YXCConstants.YC_VAR_START_POS)
			{
				info.setAddress(info.getAddress()-  YXCConstants.YC_VAR_START_POS);
			}
			
			if(ret==-1)
			{
				logger.info("onrealDataChange,epCode:{},epGunNo:{},address:{},value:{} setFieldValue value invalid",
						new Object[]{epCode,epGunNo,pointAddr,info.getIntValue()});			
			}
			else if(ret ==-3)
			{
					logger.info("onrealDataChange,epCode:{},epGunNo:{},address:{},setFieldValue address invalid",
						new Object[]{epCode,epGunNo,pointAddr});	
			}
			else if(ret == -2)
			{
					logger.debug("onrealDataChange,epCode:{},epGunNo:{},address:{},setFieldValue reserved address",
						new Object[]{epCode,epGunNo,pointAddr});
			}
			else if(ret==1)
			{
				if(type==1 || type==3 || type==11|| type==132 )
				{
					changePointMap.put(info.getAddress(),info);
					if( type==11)
					{
						changeYcMap.put(info.getAddress(),info);
					}
					else if(type==132)
					{
						changeVarYcMap.put(info.getAddress(),info);
					}
				}
			}
		}
	}
	
	private void handleUserRealInfo()
	{
		if(this.status == GunConstants.EP_GUN_STATUS_CHARGE)
		{	
			saveRealInfoToDbWithCharge();
			
			if(chargeCache ==null)
			{
				logger.error("handleUserRealInfo chargeCache ==null epCode:{},epGunNo:{}",epCode,epGunNo);
				return;
			}
			
			Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
			respMap.put("epcode", epCode);
			respMap.put("epgunno", epGunNo);
			respMap.put("currentType", currentType);
			
			ChargingInfo  chargingInfo = getCharingInfo();
			
			if(chargingInfo == null)
			{
				return ;
			}
			
			
			UserOrigin usrOrg = chargeCache.getUserOrigin();
			if(usrOrg == null)
			{
				return;
			}
			
			int orgn=usrOrg.getOrgNo();
			respMap.put("usrId", this.getCurUserId());
			logger.debug("usrId:{}",this.getCurUserId());
			respMap.put("orgn", orgn);
			respMap.put("token", chargeCache.getToken());
			respMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			//只推送手机
			logger.debug("chargeinfo orgNo:{},source:{}",usrOrg.getOrgNo(),usrOrg.getCmdFromSource());
			if(usrOrg.getOrgNo()!=UserConstants.ORG_CCZC && usrOrg.getOrgNo()!=UserConstants.ORG_SHSTOP)
			{
				onEvent(EventConstant.EVENT_REAL_CHARGING,usrOrg.getCmdFromSource(),usrOrg,0,0,respMap,(Object)chargingInfo);
			}
		}
		else
		{
			saveRealInfoToDb();
		}
		
	}


	
	
	
	private void sendToMonitor(int eventType,UserOrigin userOrigin,int ret,int cause, Object changePointMap)
	{
		Map<String ,Object> paramsMap = new ConcurrentHashMap<String, Object>();
		paramsMap.put("epcode", epCode);
		paramsMap.put("epgunno", epGunNo);
		paramsMap.put("currenttype", this.realChargeInfo.getCurrentType());
		
		MonitorService.onEvent(eventType,userOrigin,0,0,(Object)paramsMap,(Object)changePointMap);
		
	}
	
	
	public void sendRealChangeYxtoMonitor(Map<Integer, SingleInfo> changePointMap,int type,int oldEpWorkStatus, int newEpWorkStatus)
	{
		if(type!=1 && type!=3)
		{
			logger.error("[realData]sendRealChangeYxtoMonitor,invalid type:{},epCode:{},epGunNo:{}",
					new Object[]{type,epCode,epGunNo});
			return ;
		}
		
		int changeSize = changePointMap.size();
		if(changeSize<1)
		{
			//logger.debug("sendRealChangeYxtoMonitor,realData,type:{},epCode:{},epGunNo:{},changeSize<1",
			//		new Object[]{type,epCode,epGunNo});
			return;
		}
		if(type==1)
		{
			sendToMonitor(EventConstant.EVENT_ONE_BIT_YX,null,0,0,(Object)changePointMap);
			
		}
		else if(type==3)
		{
			sendToMonitor(EventConstant.EVENT_TWO_BIT_YX,null,0,0,(Object)changePointMap);
			  
		}
	}
	
	public void sendRealChangeYctoMonitor( int newEpWorkStatus)
	{
		if(newEpWorkStatus != 3 )
		{
			Map<Integer, SingleInfo> carLockMap= new ConcurrentHashMap<Integer, SingleInfo>();
			SingleInfo info = changeYcMap.get(2);
			if(info!=null)
			{
				carLockMap.put(2, info);
				sendToMonitor(EventConstant.EVENT_YC,null,0,0,(Object)carLockMap);
				changeYcMap.remove(2);
			}
			return ;
		}
		long now  = DateUtil.getCurrentSeconds();
		long diff = now - this.lastSendToMonitorTime;
		if(diff> GameConfig.montiorTimeInterval)
		{
			if(changeYcMap.size()>0)
				sendToMonitor(EventConstant.EVENT_YC,null,0,0,(Object)changeYcMap);
			if(changeVarYcMap.size()>0)
				sendToMonitor(EventConstant.EVENT_VAR_YC,null,0,0,(Object)changeVarYcMap);
			changeYcMap.clear();
			changeVarYcMap.clear();
			
			lastSendToMonitorTime = now;
		}
	}
	
	public ChargingInfo getCharingInfo()
	{
		if(chargeCache==null)
		{
			return null;
		}
		long now = DateUtil.getCurrentSeconds();
		long diff = now - this.lastUPTime;
		if(diff>GameConfig.chargeInfoUPTime)//三分钟
		{
			this.lastUPTime= now;
			
			ChargingInfo charingInfo= calcCharingInfo();
			
			return charingInfo;
		}
		return null;
	}
	
	public void saveRealInfoToDb()
	{
		long now=DateUtil.getCurrentSeconds();
		long diff = DateUtil.getCurrentSeconds() - this.lastUDTime;
		if(diff>GameConfig.saveRealInfoToDbInterval)//三分钟
		{
			lastUDTime = now;
			int currentType = realChargeInfo.getCurrentType();
			if(currentType == EpConstants.EP_AC_TYPE){
				((RealACChargeInfo)realChargeInfo).saveDb();
			}
			else
			{		
				((RealDCChargeInfo)realChargeInfo).saveDb();
			}
		}
	}
	public void saveRealInfoToDbWithCharge()
	{
		long now=DateUtil.getCurrentSeconds();
		long diff = now - this.lastUDTime;
		if(diff<GameConfig.saveRealInfoToDbInterval)//三分钟
			return ;
		
		lastUDTime = now;
		int currentType = realChargeInfo.getCurrentType();
		if(currentType == EpConstants.EP_AC_TYPE){
			((RealACChargeInfo)realChargeInfo).saveDb();
		}
		else
		{		
			((RealDCChargeInfo)realChargeInfo).saveDb();
		}
			
		if(realChargeInfo.getWorkingStatus() == GunConstants.EP_GUN_W_STATUS_WORK&&
				currentType == EpConstants.EP_DC_TYPE)
		{
			if(chargeCache ==null)
				return;
			String chargeSerialNo= chargeCache.getChargeSerialNo();
			
			((RealDCChargeInfo)realChargeInfo).saveChargeCarInfoToDB(chargeSerialNo);
			((RealDCChargeInfo)realChargeInfo).savePowerModuleToDB(chargeSerialNo);
		}
		
	}
	public void onRedoBespokeSuccess()
	{
		logger.info("[bespoke] onRedobespokeSuccess,epCode:{},epGunNo:{},status:{},Redo:{}",
				new Object[]{epCode,epGunNo,this.status,bespCache.getRedo()});
		if(bespCache.getRedo()!=1)
		{
			return ;
		}
		
		bespCache.onRedoBespokeSuccess();
		
		EpBespokeService.updateRedoBespokeToDb(bespCache.getBespId(), bespCache);
		
	
		
		String messagekey = String.format("%03d%s", Iec104Constant.C_BESPOKE, bespCache.getBespNo());
		EpCommClientService.removeRepeatMsg(messagekey);  
	
		 do_bespoke_consume_resp(1, 0, bespCache.getAccountId(), 1, bespCache.getBespNo());
  	
		
	}
	/**
	 * 1:根据预约应答成功
	 * 2：根据状态预约成功
	 * @param method
	 */
	public void onBespokeSuccess(int method)
	{
		logger.info("[bespoke] onbespokeSuccess,epCode:{},epGunNo:{},method:{}", new Object[]{this.epCode, this.epGunNo, method});
		
		if(bespCache!=null && bespCache.getStatus()!= EpConstants.BESPOKE_STATUS_LOCK)
		{
			StatService.addBespoke();
			
			String messagekey = String.format("%03d%s", Iec104Constant.C_BESPOKE, bespCache.getBespNo());
			
			EpCommClientService.removeRepeatMsg(messagekey);
			
			
			bespCache.onBespokeSuccess();
			
			long pkBespId = EpBespokeService.insertBespokeToDb(this.pkEpId, this.pkEpGunId, bespCache);	
			bespCache.setBespId(pkBespId);
			
			UserCache u= UserService.getUserCache(bespCache.getAccount());
			
			u.addBesp(bespCache);
			
			UserService.putUserCache(u);
            
            do_bespoke_consume_resp(1,0,bespCache.getAccountId(),0,bespCache.getBespNo());
    		
		   
		}
		
	}
	
	public void onBespokeFail(int errorCode)
	{
		logger.info("[bespoke] onBespokeFail,epCode:{},epGunNo:{},errorCode:{}", new Object[]{this.epCode, this.epGunNo, errorCode});
		
		if(bespCache!=null && bespCache.getStatus()== EpConstants.BESPOKE_STATUS_CONFIRM)
		{
			StatService.subBespoke();
			
			String messagekey = String.format("%03d%s", Iec104Constant.C_BESPOKE,bespCache.getBespNo());
			
			EpCommClientService.removeRepeatMsg(messagekey);
			
			
			bespCache.setStatus(EpConstants.BESPOKE_STATUS_FAIL);

			//通知前端
			Map<String, Object> chargeMap = new ConcurrentHashMap<String, Object>();
			
			chargeMap.put("bespNo", bespCache.getBespNo());
			chargeMap.put("redo", bespCache.getRedo());
			chargeMap.put("usrId", bespCache.getAccountId());
			
			handleEvent(EventConstant.EVENT_BESPOKE,0,errorCode,null,chargeMap);
		}
		
		cleanBespokeInfo();
	}
	
	public void onEndBespoke()
	{
		logger.debug("onEndBespoke");
		if(this.bespCache!=null)
		{
			long now = DateUtil.getCurrentSeconds();
			long diff = now - this.bespCache.getStartTime();
			if(diff >GameConfig.bespokeCmdTime)
			{
				endBespoke( EpConstants.END_BESPOKE_CANCEL );
			}
		}
	}
	
	public void onEndCharge()
	{
		logger.debug("onEndCharge");
	}
	
	public String getOrgNoExtra(int orgNo, int chargeFlag,int success,int errorCode)
	{
		return getOrgNoExtra(orgNo, 0, chargeFlag, success, errorCode);
	}

	public String getOrgNoExtra(int orgNo, int retFlag, int chargeFlag,int success,int errorCode)
	{
		String extraData = "";
		String userIdentity = "";
		String token="";
		if(chargeCache!=null)
		{
			userIdentity = chargeCache.getThirdUsrIdentity();
			token = chargeCache.getToken();
		}


		switch(orgNo)
		{
			case UserConstants.ORG_CCZC:
				extraData = EpChargeService.getExtraData_CCZC(epCode,epGunNo,userIdentity,
						token,chargeFlag,success,errorCode);
				break;
			case UserConstants.ORG_CHAT:
				extraData = EpChargeService.getExtraData_CHAT(epCode,epGunNo,userIdentity,
						token,chargeFlag,success,errorCode);
				break;
			case UserConstants.ORG_EC:
				extraData = EpChargeService.getExtraData_EC(epCode,
						epGunNo,token, userIdentity,chargeFlag,success,errorCode);
				break;
			case UserConstants.ORG_EVC:
			case UserConstants.ORG_TCEC_SHENZHEN:
			case UserConstants.ORG_TCEC_HESHUN:
			case UserConstants.ORG_TCEC_BAIDU:
			case UserConstants.ORG_TCEC_ECHONG:
			case UserConstants.ORG_TCEC_GUOWANG:
			case UserConstants.ORG_TCEC_BEIQI:
			case UserConstants.ORG_TCEC_NANRUI:
			case UserConstants.ORG_TCEC_NANCHONG:
			case UserConstants.ORG_TCEC_ALIPAY:
			case UserConstants.ORG_TCEC_HAINAN:
			case UserConstants.ORG_TCEC_XIAOJU:
				if (retFlag == 0) {
					extraData = EpChargeService.getExtraData_TCEC(epCode,
							epGunNo, token, chargeFlag, success, errorCode);
				} else {
					extraData = EpChargeService.getExtraData_resp_TCEC(epCode,
							epGunNo, token, chargeFlag);
				}
			default:
				break;
		}
		return extraData;
	}

	
	public void onStartChargeSuccess()
	{
		if(chargeCache!=null&& chargeCache.getStatus() == ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD )
		{
			chargeCache.setStatus(ChargeRecordConstants.CS_WAIT_INSERT_GUN);
			
			String messagekey = String.format("%03d%s", Iec104Constant.C_START_ELECTRICIZE,chargeCache.getChargeSerialNo());
			EpCommClientService.removeRepeatMsg(messagekey);
			
			EpChargeService.updateChargeRecordStatus(chargeCache.getChargeSerialNo(), ChargeRecordConstants.CS_WAIT_INSERT_GUN);
			
			Map<String, Object> chargeMap = new ConcurrentHashMap<String, Object>();
			
			chargeMap.put("epcode", epCode);
			chargeMap.put("epgunno", epGunNo);
			
			chargeMap.put("orgn", chargeCache.getUserOrigin().getOrgNo());
			chargeMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			chargeMap.put("token", chargeCache.getToken());
			
			String extraData = "";
			int orgNo = chargeCache.getUserOrigin().getOrgNo();

			if (checkOrgNo(orgNo) == 1)
			{
				extraData = this.getOrgNoExtra(orgNo, 1, 0,1,0);
				Push rd =  (Push) CooperateFactory.getPush(orgNo);
				if(rd!=null)
				{
					logger.info("onStartChargeSuccess EVENT_CHARGE_EP_RESP rd.onChargeEpResp:{}",orgNo);
					if (orgNo != UserConstants.ORG_CCZC)
						rd.onChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,1, 0);
				}
				else
				{
					logger.info("onStartChargeSuccess EVENT_CHARGE_EP_RESP did not find RealData:{}",orgNo);
				}
				
			}
			else
			{
				handleEvent(EventConstant.EVENT_CHARGE_EP_RESP,1,0,null,chargeMap);
			}
			
			logger.info("charge reponse success accountId:{},serialNo:{},epCode:{},epGunNo:{},orgn:{},usrLog:{},token:{},extraData:{}",
                    new Object[]{chargeCache.getUserId(), chargeCache.getChargeSerialNo(), epCode, epGunNo,
                            chargeCache.getUserOrigin().getOrgNo(), chargeCache.getThirdUsrIdentity(),
                            chargeCache.getToken(), extraData});
			
		}
		
	}
	
	public void onStartChargeFail(int method,int errorCode)
	{
		logger.debug("onStartChargeFail,epCharge,epCode:{},epGunNo:{},method:{},errorCode:{}",
				new Object[]{epCode,epGunNo,method,errorCode});
	
		//电桩接受充电失败.
		if(chargeCache!=null && chargeCache.getStatus()== ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD) // 没有在充电状态
		{
			int usrId= chargeCache.getUserId();
			
			String messagekey = String.format("%03d%s", Iec104Constant.C_START_ELECTRICIZE,chargeCache.getChargeSerialNo());
			EpCommClientService.removeRepeatMsg(messagekey);
			
			//1.退钱和修改状态
			EpChargeService.onChargeFail(usrId,chargeCache);
			
			//2.通知前端
			Map<String, Object> chargeMap = new ConcurrentHashMap<String, Object>();
			
			chargeMap.put("epcode", epCode);
			chargeMap.put("epgunno", epGunNo);
			chargeMap.put("orgn", chargeCache.getUserOrigin().getOrgNo());
			chargeMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			chargeMap.put("token", chargeCache.getToken());
		
			chargeMap.put("extraData", "");
			
			String extraData = "";
			int orgNo = chargeCache.getUserOrigin().getOrgNo();
			if (checkOrgNo(orgNo) == 1)
			{
				extraData = this.getOrgNoExtra(orgNo, 1,0,0,errorCode);
				IPush rd =  CooperateFactory.getPush(orgNo);
				if(rd!=null)
				{
					logger.info("onStartChargeFail EVENT_CHARGE_EP_RESP rd.onChargeEpResp:{}",orgNo);
					if (orgNo != UserConstants.ORG_CCZC)
						rd.onChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,1, 0);
				}
				else
				{
					logger.info("onStartChargeFail EVENT_CHARGE_EP_RESP did not find RealData:{}",orgNo);
				}
			}
			else
			{
				handleEvent(EventConstant.EVENT_CHARGE,0,errorCode,null,chargeMap);
			}
			
			logger.info("charge reponse fail accountId:{},serialNo:{},epCode:{},epGunNo:{},method:{},errorCode:{}",
					new Object[]{chargeCache.getUserId(),chargeCache.getChargeSerialNo(),epCode,epGunNo,method,errorCode});
			
			cleanChargeInfo();
		}
	}

	/**
	 * 1:表示通过电桩充电事件
	 * 2：表示通过状态
	 * @param method
	 */
	public int  onStartChargeEventFail(int method,int errorCode)
	{
		logger.debug(LogUtil.addBaseExtLog("method|chargeCache")
                , new Object[]{LogConstants.FUNC_ONCHARGEEVENT, epCode, epGunNo, chargeCache.getUserOrigin().getOrgNo(), chargeCache.getUserId(), method, chargeCache});
		
		if(chargeCache != null && (chargeCache.getStatus() == ChargeRecordConstants.CS_WAIT_INSERT_GUN||
				chargeCache.getStatus() == ChargeRecordConstants.CS_WAIT_CHARGE))
		{
			int usrId= chargeCache.getUserId();
		
			//1.退钱和修改状态
			EpChargeService.onChargeFail(usrId,chargeCache);
			
			//2.清空实时表信息
			BigDecimal bdZero = new BigDecimal(0.0);
			EpGunService.updateChargeInfoToDbByEpCode(this.currentType,this.epCode,this.epGunNo,
					bdZero,"",bdZero,0,0);
			
			//3.跟前段应答
			Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
			respMap.put("epcode", epCode);
			respMap.put("epgunno", epGunNo);
			respMap.put("account",chargeCache.getAccount());
			respMap.put("orgn", chargeCache.getUserOrigin().getOrgNo());
			respMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			respMap.put("token", chargeCache.getToken());
			
			String extraData = "";
			
			int orgNo = chargeCache.getUserOrigin().getOrgNo();
			if(checkOrgNo(orgNo) == 1 && orgNo == UserConstants.ORG_CHAT)
			{
				extraData = this.getOrgNoExtra(orgNo, 1,2,0,errorCode);
				IPush rd =  CooperateFactory.getPush(orgNo);
				if(rd!=null)
				{
					logger.info(LogUtil.getBaseExtLog()
							,new Object[]{LogConstants.FUNC_ONCHARGEEVENT,epCode,epGunNo,chargeCache.getUserOrigin().getOrgNo(),chargeCache.getUserId()});
					if (orgNo == UserConstants.ORG_EC) {
						rd.onChargeEvent(orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,0, errorCode);
					} else if (orgNo == UserConstants.ORG_CCZC) {
						rd.onChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,0, errorCode);
					} else {
						rd.onChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,0, errorCode);
					}
				}
				else
				{
					logger.error(LogUtil.getBaseExtLog()
							,new Object[]{LogConstants.FUNC_ONCHARGEEVENT,epCode,epGunNo,chargeCache.getUserOrigin().getOrgNo(),chargeCache.getUserId()});
				}
			}
			else
			{
				if (orgNo == UserConstants.ORG_CHAT) extraData = this.getOrgNoExtra(orgNo,2,0,errorCode);
			    respMap.put("extraData", extraData);
			    handleEvent(EventConstant.EVENT_START_CHARGE_EVENT,0,errorCode,null,respMap);
			}
			
			logger.info(LogUtil.addBaseExtLog("serialNo|method|errorCode|errorDesc")
					,new Object[]{LogConstants.FUNC_ONCHARGEEVENT,epCode,epGunNo,orgNo,chargeCache.getUserId(),chargeCache.getChargeSerialNo(),method,errorCode,chargeCache.getStopCauseDesc(errorCode)});
			
			cleanChargeInfo();
			
			return 1;
		}
		else
		{
			return 2;
		}

		
	}
	private void cleanIdentyCode()
	{
		EpGunService.clearIdentyCode(this.pkEpGunId);
		 
		 this.setIdentyCode("");
		 this.setCreateIdentyCodeTime(0);
	}
	/**
	 * method,1:通过事件
	 *        2：通过状态
	 * @param method
	 */
	public int onStartChargeEventSuccess(int method,long st)
	{
		logger.debug("onStartChargeEventSuccess,method:{},epCode:{},epGunNo:{}",new Object[]{method,epCode,epGunNo});
		
		if(chargeCache!=null && chargeCache.getStatus()!= ChargeRecordConstants.CS_CHARGING)
		{
			chargeCache.setStatus(ChargeRecordConstants.CS_CHARGING);
			
			String bespokeNo= StringUtil.repeat("0", 12);
			if(this.bespCache!=null)
			{
				bespokeNo =this.bespCache.getBespNo();
				//结束预约
				this.endBespoke(EpConstants.END_BESPOKE_CHARGE);
			}
			
			int orgNo = chargeCache.getUserOrigin().getOrgNo();
			
			//给前段应答
			Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
			respMap.put("epcode", epCode);
			respMap.put("epgunno", epGunNo);
			respMap.put("account",chargeCache.getAccount());
			respMap.put("usrId",chargeCache.getUserId());
			respMap.put("orgn", orgNo);
			respMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			respMap.put("token", chargeCache.getToken());
			
            String extraData = "";
            
          
            if( CooperateFactory.isCooperate(orgNo) && (orgNo == UserConstants.ORG_EC || orgNo == UserConstants.ORG_CCZC))
			{
				extraData = this.getOrgNoExtra(orgNo, 2,1,0);
				Push rd =  (Push) CooperateFactory.getPush(orgNo);
				if(rd!=null)
				{
					logger.info("onStartChargeEventSuccess EVENT_START_CHARGE_EVENT rd.onChargeEpResp:{}",orgNo);
					if (orgNo == UserConstants.ORG_EC) {
						rd.onChargeEvent(orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,1,0);
					} else {
						rd.onChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,1, 0);
					}
				}
				else
				{
					logger.info("onStartChargeEventSuccess EVENT_START_CHARGE_EVENT did not find RealData:{}",orgNo);
				}
			}
			else
			{
				respMap.put("extraData", extraData);
				handleEvent(EventConstant.EVENT_START_CHARGE_EVENT,1,0,null,respMap);
			}
            
            
			
			UserCache u= UserService.getUserCache(chargeCache.getAccount());
			ChargeCache  historyCharge= u.getHistoryCharge(chargeCache.getEpCode()+ chargeCache.getEpGunNo());
	
			if(historyCharge!=null)
			{
				//EpChargeService.pauseStatCharge(historyCharge);
				u.removeCharge(historyCharge.getChargeSerialNo());
			}	
			Date date = new Date();
	
			
			String chOrCode = chargeCache.getChOrCode();
			chargeCache.setSt(st);
			
					
			int chorType= EpChargeService.getOrType(u.getLevel());
			
			int userOrgNo=1000;
			if(chargeCache.getUserOrigin()!=null)
				userOrgNo= chargeCache.getUserOrigin().getOrgNo();
			int payMode = chargeCache.getPayMode();
			int pkOrderId= EpChargeService.insertChargeOrderToDb(chargeCache.getUserId(), chorType,chargeCache.getPkUserCard(),userOrgNo, 
					pkEpId, epCode, epGunNo, chargeCache.getChargingMethod(),bespokeNo, chOrCode, 
					chargeCache.getFronzeAmt(),payMode,userOrgNo,st,
					chargeCache.getChargeSerialNo(), this.chargeCache.getRateInfo(),
					chargeCache.getThirdUsrIdentity(),chargeCache.getToken());
			chargeCache.setPkOrderId(pkOrderId);
				
			EpChargeService.updateBeginRecordToDb(chargeCache.getUserId(), chorType,chargeCache.getAccount(),chargeCache.getPkUserCard(),userOrgNo, 
					pkEpId, epCode, epGunNo, chargeCache.getChargingMethod(),bespokeNo, chOrCode, 
					chargeCache.getFronzeAmt(),st,chargeCache.getChargeSerialNo(),0, this.chargeCache.getRateInfo(),0,0,0);
			
				
			pushFirstRealData();
				
			
			StatService.addCharge();
			
			cleanIdentyCode();
			 
			 
			 logger.info("charge event success accountId:{},serialNo:{},epCode:{},epGunNo:{},method:{},st:{},extra:{}",
						new Object[]{chargeCache.getUserId(),chargeCache.getChargeSerialNo(),epCode,epGunNo,method,st,extraData});
				
			 
			return 1;
		}
		else
		{
			return 2;
		}
		
	}
	
	public void onNetStatus(int epStatus)
	{
		if(this.chargeCache!=null && this.status == GunConstants.EP_GUN_STATUS_CHARGE)
		{
			handleEvent(EventConstant.EVENT_EP_NET_STATUS, epStatus, 0, null, null);
			
		}
	}
	private void checkWaitInsertGunCharge()
	{
		if(chargeCache!=null &&( chargeCache.getStatus()== ChargeRecordConstants.CS_WAIT_INSERT_GUN||
				chargeCache.getStatus() ==  ChargeRecordConstants.CS_WAIT_CHARGE))
		{
			long now = DateUtil.getCurrentSeconds();
			long diff = now - this.chargeCache.getSt();//超时判断为充电后10分钟
			logger.debug(LogUtil.addExtLog("st|diff")
					,new Object[]{this.chargeCache.getSt(),diff});
			if(diff > GameConfig.epWaitGun)
			{
				this.onStartChargeEventFail(2, 6001);
				
			}
		}
	}
	public void checkChargeCmdTimeOut(int method)
	{
		if(chargeCache!=null && chargeCache.getStatus()== ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD)
		{
			long now = DateUtil.getCurrentSeconds();
			long diff = now - this.chargeCache.getLastCmdTime();//超时判断为充电后10分钟
			if(diff > GameConfig.chargeCmdTime)
			{
				this.onStartChargeFail(method, 6001);
				
			}
		}
	}
	public void checkBespokeCmdTimeOut()
	{
		if(bespCache!=null && bespCache.getStatus()== EpConstants.BESPOKE_STATUS_CONFIRM)
		{
			long now = DateUtil.getCurrentSeconds();
			long diff = now - this.bespCache.getStartTime();
			if(diff>GameConfig.bespokeCmdTime)
			{
				onBespokeFail(6001);
			}
		}
	}
	private boolean gunFault(int status)
	{
		if(status==1 ||
				(status>=GunConstants.EP_GUN_W_STATUS_LESS_VOL_FAULT && status<=GunConstants.EP_GUN_W_STATUS_URGENT_STOP))
		{
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private void handleEpTimeout(int oldWorkStatus,int newWorkStatus )
	{
		ElectricPileCache epCache= EpService.getEpByCode(epCode);
		if(epCache.getConcentratorId()<=0)
			return;
		
		//集中器
		if(newWorkStatus == GunConstants.EP_GUN_W_STATUS_OFF_LINE &&
				this.status != GunConstants.EP_GUN_STATUS_IDLE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_IDLE, true);
			EpService.onEpCommStatusChange(epCode,epCache.getPkEpId(), 0, epCache.getGateid());
		
		}
		else if(oldWorkStatus == GunConstants.EP_GUN_W_STATUS_OFF_LINE &&
				oldWorkStatus !=newWorkStatus) 
		{
			EpService.onEpCommStatusChange(epCode,epCache.getPkEpId(), 1, epCache.getGateid());
			
		}
	}
	/**
	 * 进入电桩初始化状态,不处理电桩任何事物
	 */
	
	private void gotoEpInitStatus()
	{
		this.modifyStatus(GunConstants.EP_GUN_STATUS_EP_INIT, false);
		
	}
	/**
	 * 进入电桩空闲状态。
	 * 1.如果电桩有未完成的预约命令，那么终止预约
	 * 2.如果电桩有未结算的预约，终止并结算预约
	 * 3.如果有等待插枪的充电，那么终止充电
	 */
	private void gotoIdleStatus()//进入空闲状态
	{	
		if(chargeCache!=null)
		{
			if(chargeCache.getStatus() ==  ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD)
			{
				checkChargeCmdTimeOut(2);
			}
			else if(chargeCache.getStatus() ==  ChargeRecordConstants.CS_WAIT_INSERT_GUN ||
					chargeCache.getStatus() ==  ChargeRecordConstants.CS_WAIT_CHARGE)
			{
				//如果电桩已经变为空闲，还有等待擦枪，不需要等待10
				checkWaitInsertGunCharge();
			}
			else
			{
				
			}
			
		}
		if(bespCache!=null)
		{
			if( bespCache.getStatus() == EpConstants.BESPOKE_STATUS_LOCK)
			{
				EpCommClient epCommClient =  (EpCommClient)this.getEpNetObject();
				
				if(epCommClient!=null && epCommClient.isComm())
				{
					long now = DateUtil.getCurrentSeconds();
					
					long diff = now - this.bespCache.getStartTime();
					if(diff>180)//直流桩上报状态数据太慢，3分钟之内不根据判断超时
					{
						logger.info("[bespoke] end by gotoIdleStatus,accountId:{},bespNo:{},epCode:{},epGunNo:{}",
								new Object[]{bespCache.getAccountId(),bespCache.getBespNo(),epCode,epGunNo});
						
						endBespoke( EpConstants.END_BESPOKE_CANCEL );
					}
				}
			}
			else if(bespCache.getStatus() == EpConstants.BESPOKE_STATUS_CONFIRM)
			{
				this.checkBespokeCmdTimeOut();
				
			}
			else
			{
				
			}
		}
		
		
		if(this.status !=  GunConstants.EP_GUN_STATUS_IDLE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_IDLE, true);
			cleanEpRealChargeInfo();
		}
		
		this.setAuthCache(null);
	
	}
	private void gotoUserOperStatus()//插枪或者收枪状态
	{
		if(status != GunConstants.EP_GUN_STATUS_EP_OPER)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_EP_OPER, false);
		}
		
	}
	private void gotoWaitCharge()//等待充电
	{
		this.onInsertGunSuccess();
		if(status != GunConstants.EP_GUN_STATUS_WAIT_CHARGE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_WAIT_CHARGE, true);
		}
		
	}
	private void gotoUserAuthStatus()//进入用户鉴权状态
	{
		if(status != GunConstants.EP_GUN_STATUS_USER_AUTH)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_USER_AUTH, false);
		}
	}
	
	
	private void gotoBespokeStatus()//进入预约状态
	{
		this.onBespokeSuccess(2);
		if(this.status !=  GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED, true);
		}
	}
	private void gotoChargeStatus()//进入充电状态
	{
		this.onStartChargeEventSuccess(2, DateUtil.getCurrentSeconds());
		if(this.status !=  GunConstants.EP_GUN_STATUS_CHARGE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_CHARGE, true);
		}
	}
	private void gotoFaultStatus()//进入故障状态
	{
		checkWaitInsertGunCharge();
		
		if(this.status !=  GunConstants.EP_GUN_STATUS_STOP_USE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_STOP_USE, true);
		}
		
	}
	private void gotoSettingStatus()//进入设置状态
	{
		this.checkChargeCmdTimeOut(1);
		
		if(this.status!= GunConstants.EP_GUN_STATUS_SETTING)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_SETTING, false);
			EpGunService.updateGunState(getPkEpGunId(), 9);
		}
		
	}
	private void gotoChargeModeStatus()//进入充电模式选择状态
	{
		if(status != GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE)
		{
			this.modifyStatus(GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE, false);
		}
	}
	//该函数主要用来处理集中器内的电桩
	private void gotoOffLineStatus()
	{
		logger.debug("gotoOffLineStatus,realData,epcode:{},gunno:{},this.status:{}",new Object[]{epCode,epGunNo,this.status});
		
		EpCommClient epCommClient = (EpCommClient)epNetObject;
		
		
		if(epCommClient==null || !epCommClient.isComm() || epCommClient.getMode()!=2)
			return;
	
		
		if(this.status!= GunConstants.EP_GUN_STATUS_OFF_LINE)//
		{
			EpService.onEpCommStatusChange(epCode, getPkEpId(), 0, 0);
		}
	}
	private void gotoUpgradeStatus()
	{
		if(this.status!= GunConstants.EP_GUN_STATUS_EP_UPGRADE)
		{
			EpGunService.updateGunState(this.getPkEpGunId(), GunConstants.EP_GUN_STATUS_STOP_USE);
			
			this.modifyStatus(GunConstants.EP_GUN_STATUS_EP_UPGRADE, false);
		}
	}
	
	public void gotoStopChargeStatus(int successflag,int stopCause)
	{
		logger.debug("gotoStopCharge epCode:{},epGunNo:{},stopCause:{},successflag:{}",
				new Object[]{epCode,epGunNo,stopCause,successflag});
		
		if(chargeCache==null|| chargeCache.getStatus() == ChargeRecordConstants.CS_CHARGE_END )
		{
			return ;
		}
		
		if(chargeCache.getUserOrigin()==null)
		{
			logger.debug("gotoStopCharge  chargeCache.getUserOrigin() ==null epCode:{},epGunNo:{}",
					new Object[]{epCode,epGunNo});
			return;
		}
		chargeCache.setStatus(ChargeRecordConstants.CS_CHARGE_END);
		
		try
		{
		int orgNo=chargeCache.getUserOrigin().getOrgNo();
		String extraData = "";
		
		if((CooperateFactory.isCooperate(orgNo) && orgNo != UserConstants.ORG_SHSTOP)
				|| orgNo == UserConstants.ORG_CHAT)
		{
			extraData = this.getOrgNoExtra(orgNo, 1, successflag, stopCause);
			
			logger.debug("gotoStopCharge orgNo:{},extraData:{}", orgNo,extraData);
		}
			
		
		// 给前段应答
		Map<String, Object> respMap = new ConcurrentHashMap<String, Object>();
		respMap.put("epcode", epCode);
		respMap.put("epgunno", epGunNo);
		respMap.put("account", chargeCache.getAccount());
		respMap.put("orgn", chargeCache.getUserOrigin().getOrgNo());
		respMap.put("usrLog", chargeCache.getThirdUsrIdentity());
		respMap.put("token", chargeCache.getToken());
		respMap.put("extraData", extraData);

		logger.debug("gotoStopCharge handleEvent:{},respMap:{}",extraData,respMap);
		
		
		if (CooperateFactory.isCooperate(orgNo) && orgNo == UserConstants.ORG_EC)
		{
			IPush rd =  CooperateFactory.getPush(orgNo);
			if(rd==null)
			{
				logger.info("EVENT_STOP_CHARGE did not find RealData:{}",orgNo);
				return ;
			}
			if (orgNo != UserConstants.ORG_CCZC)
				rd.onStopChargeEpResp(chargeCache.getToken(), orgNo, chargeCache.getThirdUsrIdentity(), epCode, epGunNo, extraData,1, 0);
		}
		else
		{
			handleEvent(EventConstant.EVENT_STOP_CHARGE_EP_RESP, 1, 0, null, respMap);
		}
		}
		catch(Exception e)
		{
			logger.info("gotoStopChargeStatus,epCode:{},epGunNo:{},e.getStackTrace():{}", new Object[]{epCode,epGunNo,e.getStackTrace()});
		}
	}
	
	private void onRealDataChangeToMonitor(int oldEpWorkStatus,Map<Integer, SingleInfo> changePointMap,int changeType)
	{
		
		if(oldEpWorkStatus!=this.realChargeInfo.getWorkingStatus())
		{
			if( oldEpWorkStatus==3)
			{
				this.cleanEpRealChargeInfo();
				
			}
			dispatchWholeRealToMonitor(oldEpWorkStatus);
		}
		else
		{
			if(changeType==1 || changeType==3)
			{
				sendRealChangeYxtoMonitor(changePointMap,changeType,oldEpWorkStatus, this.realChargeInfo.getWorkingStatus());
			}
			else
			{
				sendRealChangeYctoMonitor(this.realChargeInfo.getWorkingStatus());
			}
		}
		
	}
	
	private void onStatusChange(int newStatus)
	{
	
		switch(newStatus)
		{
		case GunConstants.EP_GUN_STATUS_IDLE:
			this.gotoIdleStatus();
			break;
		case GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED:
			this.gotoBespokeStatus();
			break;
		case GunConstants.EP_GUN_STATUS_CHARGE:
			this.gotoChargeStatus();
			break;
		case GunConstants.EP_GUN_STATUS_STOP_USE:
			this.gotoFaultStatus();
			break;
		case GunConstants.EP_GUN_STATUS_EP_OPER:
			this.gotoUserOperStatus();
			break;
		case GunConstants.EP_GUN_STATUS_USER_AUTH:
			this.gotoUserAuthStatus();
			break;
		case GunConstants.EP_GUN_STATUS_SETTING:
			gotoSettingStatus();
			break;
		case GunConstants.EP_GUN_STATUS_FROZEN_AMT:
			break;
		case GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE:
			gotoChargeModeStatus();
			break;
		case GunConstants.EP_GUN_STATUS_EP_UPGRADE:
			break;
		case GunConstants.EP_GUN_STATUS_OFF_LINE:
			gotoOffLineStatus();
			break;
		case GunConstants.EP_GUN_STATUS_EP_INIT:
			this.gotoEpInitStatus();
			break;
		case GunConstants.EP_GUN_STATUS_WAIT_CHARGE:
			this.gotoWaitCharge();
			break;
		case GunConstants.EP_GUN_STATUS_TIMER_CHARGE:
			this.gotoWaitCharge();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param chargeCmdResp
	 * @return
	 */
	public int onEpStartCharge(ChargeCmdResp chargeCmdResp)
	{
		logger.debug(LogUtil.addExtLog("chargeCmdResp"),chargeCmdResp);
		
		if(chargeCmdResp.getRet() ==  1) //电桩接受充电成功,变为等待插枪
		{
			onStartChargeSuccess();
		}
		else
		{
			//电桩接受充电失败
			onStartChargeFail(1, chargeCmdResp.getErrorCause());
		}
		
		return 0;
	}
	private void modifyStatus(int status,boolean modifyDb)
	{
		logger.debug("modifyStatus,epcode:{},gunno:{},this.status:{},newStatus:{}",
				new Object[]{epCode,epGunNo,this.status,status});
		this.status = status;
		
		if(modifyDb)
		{
			EpGunService.updateGunState(this.getPkEpGunId(), status);
		}
	}
	private void cleanEpRealChargeInfo()
	{
		if(realChargeInfo != null)
		{
			if(realChargeInfo.getCurrentType() == EpConstants.EP_AC_TYPE)
				((RealACChargeInfo)realChargeInfo).endCharge();
			else if(realChargeInfo.getCurrentType() ==EpConstants.EP_DC_TYPE)
				((RealDCChargeInfo)realChargeInfo).endCharge(); 
		}
		
	}
	public int onEpStopChargeEvent(int epRet,String userAccount, String aerialNo)
	{
		if(epRet ==  0)//充电桩,充电成功
		{
			if(this.status == GunConstants.EP_GUN_STATUS_CHARGE)
			{
				return 3;
			}
			this.status = GunConstants.EP_GUN_STATUS_IDLE;
			cleanEpRealChargeInfo();
		}
		else//没插枪超时，那么转为空闲
		{
			//失败.变为空闲
			this.status = GunConstants.EP_GUN_STATUS_IDLE;
		}
		
		return 0;
	}
	/**
	 * 
	 * @param epChargeEvent
	 * @return
	 */
	public int handleStartChargeEvent(ChargeEvent epChargeEvent)
	{
		if(chargeCache==null )//特殊卡
		{
			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_ONCHARGEEVENT, "fail,chargeCache is null,epCode|epGunNo")
					,new Object[]{epCode,epGunNo});

			return 1; //
		} else {
			logger.debug(LogUtil.addFuncExtLog("epCode|epGunNo|epChargeEvent")
                    , new Object[]{LogConstants.FUNC_ONCHARGEEVENT, epCode, epGunNo, epChargeEvent});
		}
		if(epChargeEvent.getSerialNo().compareTo(chargeCache.getChargeSerialNo())!=0)
		{
			logger.error(LogUtil.addBaseExtLog("epChargeEvent|chargeCacheepChargeEvent.getSerialNo()|chargeCache.getChargeSerialNo()")
					,new Object[]{LogConstants.FUNC_ONCHARGEEVENT + " fail,invalid serialNo",epCode,epGunNo,chargeCache.getUserOrigin().getOrgNo(),chargeCache.getUserId(),epChargeEvent.getSerialNo(),chargeCache.getChargeSerialNo()});
			return 3;//数据不存在
		}
		int retCode=0;
		if(epChargeEvent.getSuccessFlag() ==  1)//充电桩,充电成功
		{	
			retCode = this.onStartChargeEventSuccess(1,epChargeEvent.getStartChargeTime());
		}
		else//没插枪超时，那么转为空闲
		{
			retCode= onStartChargeEventFail(1,epChargeEvent.getErrorCode());
		}
		
		return retCode;
	}
	
	public void cleanBespokeInfo()
	{
		if(bespCache!=null)
		{
			logger.info("[bespoke] cleanUsrBespoke,userId:{},usrAccount:{},bespNo()",
							new Object[]{bespCache.getAccountId(),
					bespCache.getAccount(),bespCache.getBespNo()});
			
			UserService.cleanUsrBespoke(bespCache.getAccountId(),
                    bespCache.getAccount(), bespCache.getBespNo());
			
			
		}
 		 setBespCache(null);
	}
	public void cleanChargeInfo()
	{
		if(chargeCache!=null)
		{
			UserService.cleanUsrCharge(chargeCache.getUserId(),
					chargeCache.getAccount(),chargeCache.getChargeSerialNo());
			
			setChargeCache(null);
		}
		//清除车端信息和电源模块数据
		if(this.currentType == EpConstants.EP_DC_TYPE)
		{
			((RealDCChargeInfo)realChargeInfo).cleanChargeInfo();
		}
		
	}
	public ChargeCache makeChargeInfo(UserCache chargeUser,String thirdUsrIdentity,RateInfo rateInfo,short chargeStyle,int nFrozenAmt,
			BigDecimal prensentAmt,int payMode,int orgNo, int cmdFromSource,String cmdChIdentity,String token)
	{
		ChargeCache chargingCacheObj = new ChargeCache();
		
		String serialNo = epCode + EpChargeService.makeSerialNo();
		chargingCacheObj.setChargeSerialNo(serialNo);
		
		chargingCacheObj.setSt(DateUtil.getCurrentSeconds());
		
		chargingCacheObj.setUserId(chargeUser.getId());
		chargingCacheObj.setAccount(chargeUser.getAccount());
		chargingCacheObj.setThirdUsrIdentity(thirdUsrIdentity);
		chargingCacheObj.setToken(token);
		
		chargingCacheObj.setFronzeAmt(nFrozenAmt);
		chargingCacheObj.setPresent(prensentAmt);
		
		RateInfo curRateInfo = new RateInfo();
        curRateInfo.setModelId(rateInfo.getModelId());
		curRateInfo.setJ_Rate(rateInfo.getJ_Rate());
		curRateInfo.setF_Rate(rateInfo.getF_Rate());
		curRateInfo.setP_Rate(rateInfo.getP_Rate());
		curRateInfo.setG_Rate(rateInfo.getG_Rate());
		curRateInfo.setServiceRate(rateInfo.getServiceRate());
		curRateInfo.setQuantumDate(rateInfo.getQuantumDate());
        if (rateInfo.getModelId() == 3) {
            curRateInfo.setJ_RateMoney(rateInfo.getJ_RateMoney());
            curRateInfo.setF_RateMoney(rateInfo.getF_RateMoney());
            curRateInfo.setP_RateMoney(rateInfo.getP_RateMoney());
            curRateInfo.setG_RateMoney(rateInfo.getG_RateMoney());
        }
		
		chargingCacheObj.setRateInfo(curRateInfo);
		chargingCacheObj.setStartChargeStyle(chargeStyle);
		chargingCacheObj.setPayMode(payMode);
		UserOrigin userOrigin = new UserOrigin(orgNo,cmdFromSource,cmdChIdentity);
		
		chargingCacheObj.setUserOrigin(userOrigin);
		
		chargingCacheObj.setStatus(ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD);
		
		chargingCacheObj.setLastCmdTime(DateUtil.getCurrentSeconds());
		
		return chargingCacheObj;
	}

	public int startChargeAction(UserCache chargeUser, String thirdUsrIdentity,
			ChargeCardCache card, RateInfoCache rateInfo, String bespNo,
			short chargeStyle, int frozenAmt, int payMode, int orgNo,
			int fromSource, String actionIdentity, String token, byte[] cmdTimes) {

		int chargingUserId = chargeUser.getId();
		String chargingAccout = chargeUser.getAccount();
		// 1.有别人预约,不能充电
		BespCache bespCache = getBespCache();
		if (bespCache != null && bespCache.getAccountId() != chargingUserId) {
			logger.error(LogUtil.addBaseExtLog("fail,bespCache.getAccountId() != chargingUserId,bespUserID"),
					new Object[] { LogConstants.FUNC_START_CHARGE, this.epCode,
					this.epGunNo, orgNo, chargingUserId, bespCache.getAccountId() });
			return ErrorCodeConstants.EPE_OTHER_BESP;
		}

		if (epNetObject == null) {
			logger.error(LogUtil.addBaseNoExtLog("epNetObject == null"),
					new Object[] { LogConstants.FUNC_START_CHARGE, this.epCode,
					this.epGunNo, orgNo, chargingUserId });
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if (!epNetObject.isComm()) {
			logger.error(LogUtil.addBaseExtLog("status"), new Object[] {
					LogConstants.FUNC_START_CHARGE, this.epCode, this.epGunNo,
					orgNo, chargingUserId, epNetObject.getStatus() });
			return ErrorCodeConstants.EP_UNCONNECTED;
		}

		EpCommClient epCommClient = (EpCommClient) epNetObject;

		if (this.status == GunConstants.EP_GUN_STATUS_STOP_USE) {
			return ErrorCodeConstants.EPE_GUN_FAULT;
		}
		if (this.status == GunConstants.EP_GUN_STATUS_CHARGE) {
			return ErrorCodeConstants.EPE_REPEAT_CHARGE;
		}
		if (this.status == GunConstants.EP_GUN_STATUS_SETTING) {
			return ErrorCodeConstants.EPE_IN_EP_OPER;
		}

		if (this.chargeCache != null) {
			if (chargeCache.getStatus()== ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD) {
				onStartChargeFail(3, ErrorCodeConstants.EPE_STOP_CHARGE);
			} else if (chargeCache.getStatus() == ChargeRecordConstants.CS_WAIT_INSERT_GUN ||
					chargeCache.getStatus() == ChargeRecordConstants.CS_WAIT_CHARGE) {
				onStartChargeEventFail(3, ErrorCodeConstants.EPE_STOP_CHARGE);
			}
			/*if (chargeCache.getUserId() != chargeUser.getId()) {
				logger.error(LogUtil.addBaseExtLog("fail,other is chargeing chargeCache"),
						new Object[] { LogConstants.FUNC_START_CHARGE,
						this.epCode, this.epGunNo, orgNo, chargingUserId, this.chargeCache });
				return ErrorCodeConstants.EPE_OTHER_CHARGING;
			} else {
				logger.error(LogUtil.addBaseExtLog("fail,repeat charge chargeCache"),
						new Object[] { LogConstants.FUNC_START_CHARGE,
						this.epCode, this.epGunNo, orgNo, chargingUserId, this.chargeCache });
				return ErrorCodeConstants.EPE_REPEAT_CHARGE;
			}*/
		}

		BigDecimal bdPresentAmt = new BigDecimal(0.0);
		BigDecimal bdFrozenAmt = NumUtil.intToBigDecimal2(frozenAmt);
		if (!UserService.checkThirdOrgNo(orgNo)) {
			int usrId = chargeUser.getId();
			BigDecimal bdRemainAmt = new BigDecimal(0.0);
			UserRealInfo u = UserService.findUserRealInfo(usrId);
			if (null != u) {
				bdRemainAmt = u.getMoney();
			}

			logger.info(LogUtil.addBaseExtLog("amt|payMode"), new Object[] {
					LogConstants.FUNC_START_CHARGE, this.epCode, this.epGunNo,
					orgNo, chargingUserId, bdRemainAmt, payMode });

			// 100倍后转为整数
			bdRemainAmt = bdRemainAmt.multiply(Global.DecTime2);
			int nRemainAmt = NumUtil.BigDecimal2ToInt(bdRemainAmt);

			// 冻结金额
			if (nRemainAmt < 0 || frozenAmt <= 0 || nRemainAmt < frozenAmt) {
				logger.error(LogUtil.addBaseExtLog("bdRemainAmt|bdFrozenAmt"),
						new Object[] { LogConstants.FUNC_START_CHARGE,
								this.epCode, this.epGunNo, orgNo,
								chargingUserId, bdRemainAmt, bdFrozenAmt });
				return ErrorCodeConstants.EPE_NO_ENOUGH_MONEY;
			}
			int iPresent = NumUtil.BigDecimal2ToInt(u.getPresent().multiply(Global.DecTime2));
			if (nRemainAmt - iPresent < frozenAmt) bdPresentAmt = NumUtil.intToBigDecimal2(frozenAmt - (nRemainAmt - iPresent));
			if (!chargeUser.isRemainAmtWarn()
					&& (nRemainAmt - frozenAmt) < (chargeUser
							.getRemainAmtWarnValue() * 100)
					&& chargeUser.getAccount().length() == 12) {
				chargeUser.setRemainAmtWarn(true);
				int n = nRemainAmt - frozenAmt;
				String warnAmt = NumUtil.intToBigDecimal2(n).toString();

				logger.info(
						LogUtil.addBaseExtLog("managePhone|customerPhone"),
						new Object[] {
								LogConstants.FUNC_START_CHARGE
										+ " big account remainAmtWarning",
								this.epCode, this.epGunNo, orgNo,
								chargingUserId,
								chargeUser.getRemainAmtWarnPhone(),
								chargeUser.getRemainAmtWarnCPhone() });

				EpChargeService.msgRMAmtWarningToManager(usrId,
						chargeUser.getRemainAmtWarnPhone(),
						chargeUser.getName(), warnAmt);

				EpChargeService.msgRemainAmtWarning(usrId,
						chargeUser.getRemainAmtWarnCPhone(), warnAmt);
			}
		}

		ChargeCache chargingCacheObj = makeChargeInfo(chargeUser,
				thirdUsrIdentity, rateInfo.getRateInfo(), chargeStyle,
				frozenAmt, bdPresentAmt, payMode, orgNo, fromSource, actionIdentity, token);
		if (!UserService.checkThirdOrgNo(orgNo)) {
			UserService.subAmt(chargeUser.getId(), bdFrozenAmt, bdPresentAmt,
					chargingCacheObj.getChargeSerialNo());
		}

		chargingCacheObj.setEpCode(this.getEpCode());
		chargingCacheObj.setEpGunNo(this.getEpGunNo());

		String transactionNumber = chargingCacheObj.getChargeSerialNo();
		this.chargeCache = chargingCacheObj;

		if (status != GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED) {
			modifyStatus(GunConstants.EP_GUN_STATUS_USER_AUTH, false);
		}

		if (bespNo.length() == 0) {
			bespNo = StringUtil.repeat("0", 12);// 如果预约编号空
		}
		chargingCacheObj.setBespNo(bespNo);
		byte[] bcdBespNo = WmIce104Util.str2Bcd(bespNo);
		assert (bcdBespNo.length == 6);

		// logger.info("EventConstant.EVENT_CHARGE,source:{},actionIdentity{}",source,actionIdentity);

		chargingCacheObj.setCard(card);
		String curUserAccount = chargingCacheObj.getAccount();
		Date date = new Date();
		String chOrCode = EpChargeService.makeChargeOrderNo(this.pkEpGunId,
				chargeCache.getUserId());
		ChargeEvent chargeEvent = new ChargeEvent(epCode, epGunNo,
				transactionNumber, 0, (int) (date.getTime() / 1000), 0, 1, 0);

		BigDecimal bdFronzeAmt = NumUtil.intToBigDecimal2(chargeCache
				.getFronzeAmt());

		EpGunService.updateChargeInfoToDbByEpCode(this.currentType,
				this.epCode, this.epGunNo,
				new BigDecimal(this.realChargeInfo.getChargedMeterNum())
						.multiply(Global.Dec3), chargeEvent.getSerialNo(),
				bdFronzeAmt, 0, chargeCache.getUserId());

		chargeCache.setChOrCode(chOrCode);

		/*UserCache memUserInfo = UserService.getUserCache(curUserAccount);*/
		if (chargeUser != null && card != null) {
			logger.debug(LogUtil.getBaseExtLog(), new Object[] {
					LogConstants.FUNC_START_CHARGE, this.epCode, this.epGunNo,
					orgNo, card.getId() });
			// chargeCache.setPkUserCard(memUserInfo.getCard().getId());
		}
		int chorType = EpChargeService.getOrType(chargeUser.getLevel());

        chargeUser.addCharge(chargeCache);
		String bespokeNo = StringUtil.repeat("0", 12);
		if (this.bespCache != null) {
			bespokeNo = this.bespCache.getBespNo();
		}

		int curUserId = chargeCache.getUserId();
		EpChargeService.insertChargeRecordToDb(curUserId, chorType,
				curUserAccount, chargeCache.getPkUserCard(), orgNo, pkEpId,
				epCode, epGunNo, chargeCache.getChargingMethod(), bespokeNo,
				chOrCode, chargeCache.getFronzeAmt(), chargeCache.getPresent(), payMode, orgNo,
				chargeEvent, this.chargeCache.getRateInfo(), 4,
				chargeCache.getThirdUsrIdentity(), chargeCache.getToken(),
				chargeUser.getAccountId());
		RateService.addPurchaseHistoryToDB(NumUtil.intToBigDecimal2(chargeCache.getFronzeAmt()),1
				,curUserId,0,"充电消费",epCode,chargeEvent.getSerialNo(),"",chargeUser.getAccountId());

		logger.info(LogUtil.addBaseExtLog("chargeSerialNo"), new Object[] {
				LogConstants.FUNC_START_CHARGE, this.epCode, this.epGunNo,
				orgNo, chargingUserId, chargeCache.getChargeSerialNo() });

        String passwd = "e10adc3949ba59abbe56e057f20f883e";
        if (orgNo == UserConstants.ORG_I_CHARGE || payMode==EpConstants.P_M_FIRST) {
            UserRealInfo realUserInfo = UserService
                    .findUserRealInfo(curUserId);
            passwd = realUserInfo.getPassword();
        } else if (payMode!=EpConstants.P_M_FIRST) {
            chargingAccout = "12345678912";
        }

        byte[] data = EpEncoder.do_start_electricize(epCode, (byte) epGunNo,
				chargingAccout, 0, (byte) chargeStyle, frozenAmt, 1,
                passwd, chargeCache.getChargeSerialNo(), rateInfo);

		if (data == null) {
			logger.error(LogUtil.getBaseExtLog(), new Object[] {
					LogConstants.FUNC_START_CHARGE, this.epCode, this.epGunNo,
					orgNo, chargingUserId });
		}

		// 命令加时标
		String messagekey = String.format("%03d%s",
				Iec104Constant.C_START_ELECTRICIZE,
				chargeCache.getChargeSerialNo());
		EpMessageSender.sendRepeatMessage(epCommClient, messagekey, 0, 0,
				Iec104Constant.C_START_ELECTRICIZE, data, cmdTimes,
				epCommClient.getVersion());
		// }

		// 清空鉴权用户
		setAuthCache(null);

		return 0;
	}

	public int stopChargeAction(int orgNo,int usrId,String thirdUsrIdentity,int source,String actionIdentity)
	{
		EpCommClient commClient= (EpCommClient)getEpNetObject();
		if(commClient ==null || !commClient.isComm())
		{
			return ErrorCodeConstants.EP_UNCONNECTED;//
		}
		
		ChargeCache chargeCacheObj = getChargeCache();
		//没有在充电，不能结束充电
		if(chargeCacheObj==null )
		{
			logger.error("stopcharge fail,chargeCacheObj==null,epCode:{},epGunNo:{}",epCode,epGunNo);
			return  ErrorCodeConstants.EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING;//
		}
		//不是充电的用户不能结束充电  
		if(orgNo == UserConstants.ORG_I_CHARGE) 
		{
			if( chargeCacheObj.getUserId() != usrId)
			{
				logger.error("stopcharge fail,user without charge,epCode:{},epGunNo:{},orgNo:{},userId:{},chargeCacheObj.getUserId():{}",
						new Object[]{epCode,epGunNo,orgNo,usrId,chargeCacheObj.getUserId()});return  ErrorCodeConstants.EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING;
			}
		}
		else
		{
			if( thirdUsrIdentity==null||
				!thirdUsrIdentity.equals(chargeCacheObj.getThirdUsrIdentity())||
				chargeCacheObj.getUserId() != usrId  )
			{
				logger.error("stopcharge fail,user without charge,epCode:{},epGunNo:{},orgNo:{},userId:{},chargeCacheObj.getUserId():{},thirdUsrIdentity:{},chargeCacheObj.getThirdUsrIdentity():{}",
						new Object[]{epCode,epGunNo,orgNo,usrId,chargeCacheObj.getUserId(),thirdUsrIdentity,chargeCacheObj.getThirdUsrIdentity()});
				return  ErrorCodeConstants.EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING;
			}
			
		}
		//TODO,记得测试，在没插枪之前取消
		if(this.status != GunConstants.EP_GUN_STATUS_CHARGE && this.status != GunConstants.EP_GUN_STATUS_EP_OPER
				&& this.status != GunConstants.EP_GUN_STATUS_WAIT_CHARGE && this.status != GunConstants.EP_GUN_STATUS_TIMER_CHARGE)
		{
			logger.error("stopcharge fail,gun not charging,epCode:{},epGunNo:{},status:{}",new Object[]{epCode,epGunNo,status});
			return  ErrorCodeConstants.EPE_NOT_ENABLE_STOP_WITHOUT_CHARGING;//
		}

        
		byte bcdqno = (byte)epGunNo;
		
	
		
		byte[] data= EpEncoder.do_stop_electricize(epCode, bcdqno);
		if(data == null)
		{
				logger.error("stopcharge fail,epCode:{},epGunNo:{}, do_stop_electricize exception",epCode,epGunNo);
			return ErrorCodeConstants.EP_PACK_ERROR;
			
		}
		byte[] cmdTimes = WmIce104Util.timeToByte();
		
		EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_STOP_ELECTRICIZE, data,cmdTimes,commClient.getVersion());
			logger.info("stopcharge accountId:{},account:{},chargeSerialNo:{},epCode:{},epGunNo:{},cmdTimes:{}{}{} to ep",
					new Object[]{chargeCache.getUserId(),chargeCache.getAccount(),chargeCache.getChargeSerialNo(),epCode,epGunNo,(int)cmdTimes[0],(int)cmdTimes[1],(int)cmdTimes[2]});

		
		return 0;
	}
	
	public int getCurUserId()
	{
		if( authCache!=null && authCache.getUsrId()>0)
			return authCache.getUsrId();
		
		if(bespCache!=null && bespCache.getAccountId()>0)
			return bespCache.getAccountId();
		
		if( chargeCache!=null && chargeCache.getUserId()>0)
			return chargeCache.getUserId();
		
		return 0;
	}
	
	public int startBespokeAction(UserCache userInfo,RateInfoCache rateInfo,int redo,int secBuyOutTime,String bespNo,
			int payMode,int orgNo,int cmdFromSource,String cmdIdentily)
	{	
		//1.充电桩未连接不能充电
		EpCommClient commClient = (EpCommClient)getEpNetObject();
		if(commClient==null || commClient.isComm()==false) {
			
			return ErrorCodeConstants.EP_UNCONNECTED;//
		}
		
		if( redo == 1 )
		{
			// 11.这个枪没有预约不能续约
			if(status != GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
			{
				
				return ErrorCodeConstants.BESP_NO_NOT_EXIST;
			}
			if (this.bespCache.getAccountId() != userInfo.getId()) {
				return ErrorCodeConstants.NOT_SELF_REDO_BESP;//
			}
		}
		if(redo == 0)
		{
			if(status ==GunConstants.EP_GUN_STATUS_EP_OPER)//用户使用状态，允许使用用户预约
			{
				int curUserId = getCurUserId(); 
				if(curUserId!=0 && curUserId != userInfo.getId())
				{
					return ErrorCodeConstants.CAN_NOT_BESP_IN_BESP_COOLING;
				}
				return ErrorCodeConstants.USED_GUN;//
			}
			if(status == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
				return ErrorCodeConstants.EPE_OTHER_BESP;//
			
			if(this.status == GunConstants.EP_GUN_STATUS_SETTING)
			 {
				return ErrorCodeConstants.EPE_IN_EP_OPER;
			 }
			if(this.status == GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE)
			 {
				return ErrorCodeConstants.EPE_IN_EP_OPER;
			 }
			
		}
		if(status == GunConstants.EP_GUN_STATUS_IDLE )
		{
			status = GunConstants.EP_GUN_STATUS_EP_OPER;
		}
				
		if (redo == 1) {
			long bespTotalTime = this.bespCache.getEndTime() + secBuyOutTime
					- bespCache.getStartTime();
			if (bespTotalTime > (6 * 3600)) {
				return ErrorCodeConstants.BESP_TO_MAX_TIME;//
			}
		}
		

		BigDecimal fronzingAmt = RateService.calcBespAmt(rateInfo.getRateInfo().getBespokeRate(),secBuyOutTime / 60);

		int usrId= userInfo.getId();
		BigDecimal userRemainAmt= UserService.getRemainBalance(usrId);
		logger.info("[bespoke] remainAmt,accountId:{},amt:{},payMode:{} before fronze amt",
				new Object[]{usrId,userRemainAmt.doubleValue(),payMode});
		
	
		// 12.钱不够不能充电
		if(payMode == EpConstants.P_M_FIRST && userRemainAmt.compareTo(fronzingAmt)<0)
		{
			logger.error("[bespoke] fail,not enough money,accountId{},amt:{},userRemainAmt:{},curUserAccount{},bespNo{},epCode{},epGunNo{},redo:{}"
					,new Object[]{usrId,fronzingAmt.doubleValue(),userRemainAmt,userInfo.getAccount(),bespNo,this.epCode,this.epGunNo,redo});

			
			return ErrorCodeConstants.EPE_NO_ENOUGH_MONEY;//
		}
	
		byte bcdqno = (byte) epGunNo;
		byte bredo = (byte) redo;
		byte[] start_time = WmIce104Util.getP56Time2a();

		// todo:20150803
		String CardNo = new String("1234567891234567");// 充电卡号
		String CarCardNo = new String("1234567891234567");// 车牌号
		
		java.util.Date dt = new Date();
		long bespSt = dt.getTime() / 1000;

		if (redo == 0) {
			BespCache bespCacheObj = new BespCache();
			
			
			bespCacheObj.setAccount(userInfo.getAccount());
			bespCacheObj.setBespNo(bespNo);
			
			long et = bespSt + (long) (secBuyOutTime);
			
			bespCacheObj.setStartTime(bespSt);
			bespCacheObj.setEndTime(et);
			bespCacheObj.setRealEndTime(et);

			bespCacheObj.setAccountId(userInfo.getId());
			bespCacheObj.setRate(rateInfo.getRateInfo().getBespokeRate());
			bespCacheObj.setStatus(EpConstants.BESPOKE_STATUS_CONFIRM);
			
			bespCacheObj.setEpCode(this.epCode);
			bespCacheObj.setEpGunNo(this.epGunNo);
			
			bespCacheObj.setPayMode(payMode);
			UserOrigin userOrigin = new UserOrigin(orgNo,cmdFromSource,cmdIdentily);
			
			bespCacheObj.setUserOrigin(userOrigin);
		
			this.bespCache = bespCacheObj;
			
			userInfo.addBesp(bespCache);
		}
		bespCache.setBuyTimes(secBuyOutTime / 60);
		
		bespCache.setRedo((short)redo);
	
		
		byte[] bcdAccountNo = WmIce104Util.str2Bcd(userInfo.getAccount());
	
		byte[] orderdata = EpEncoder.do_bespoke(
				WmIce104Util.str2Bcd(epCode), bespNo, bcdqno, bredo,
				bcdAccountNo, WmIce104Util.str2Bcd(CardNo), start_time,
				(short)(secBuyOutTime/60), StringUtil.repeat("0", 16).getBytes());

		byte[] cmdTimes = WmIce104Util.timeToByte();
		
		String messagekey = String.format("%03d%s", Iec104Constant.C_BESPOKE,bespCache.getBespNo());
		
		EpMessageSender.sendRepeatMessage(commClient, messagekey, 0, 0,Iec104Constant.C_BESPOKE, orderdata,cmdTimes,commClient.getVersion());
		logger.info("[bespoke] start accountId:{},amt:{},bredo:{},curUserAccount{},bespNo{},epCode{},epGunNo{},cmdTimes:{}{}{}"
		           ,new Object[]{usrId,fronzingAmt.doubleValue(),bredo,bespCache.getAccount(),bespNo,epCode,epGunNo,(int)cmdTimes[0],(int)cmdTimes[1],(int)cmdTimes[2]});
  					
		return 0;
		
	}
	public int stopBespokeAction(int source,String srcIdentity ,String bespno,int usrId)
	{
		//this.curAction = EventConstant.EVENT_CANNEL_BESPOKE;
		//this.curActionOccurTime = DateUtil.getCurrentSeconds();
	
		byte[] sendMsg = EpEncoder.do_cancel_bespoke(epCode, this.epGunNo,bespno);
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		EpCommClient commClient = (EpCommClient)this.epNetObject;
		EpMessageSender.sendMessage(commClient, 0, 0, Iec104Constant.C_CANCEL_BESPOKE, sendMsg,cmdTimes,commClient.getVersion());
			logger.info("cancelbespoke send to ep,accountId:{},bespNo:{},epCode:{},epGunNo:{},cmdTimes:{}{}{}"
					,new Object[]{usrId,bespno,epCode,epGunNo,(int)cmdTimes[0],(int)cmdTimes[1],(int)cmdTimes[2]});
		
		return 0;
	}
	
	
	public void cleanChargeInfoInRealData()
	{
		try
		{
		BigDecimal bdZero = new BigDecimal(0.0);
		EpGunService.updateChargeInfoToDbByEpCode(this.currentType,this.epCode,this.epGunNo,
				bdZero,"",bdZero,0,0);
		}
		catch(Exception e)
		{
			logger.error("cleanChargeInfoInRealData exception,getStackTrace:{}",e.getStackTrace());
			
		}
	
	}

	public UserCache getChargeUser(ChargeCache ccObj, String account) {
		UserCache u = null;
		int chargeStyle = ccObj.getStartChargeStyle();

		if (chargeStyle == EpConstants.CHARGE_TYPE_QRCODE)
			u = UserService.getUserCache(account);
		if (chargeStyle == EpConstants.CHARGE_TYPE_CARD) {
			ChargeCardCache card = UserService.getCard(account);
			if (card != null)
				u = UserService.getUserCache(card.getUserId());
		}
		if (u == null) {
			u = UserService.getUserCache(ccObj.getUserId());
		}

		if (u == null) {
			logger.error("getChargeUser invalid params,chargeStyle:{},account:{}",
					chargeStyle, account);
		}

		return u;
	}
	
	public int checkChargeTimeAndMeter(ConsumeRecord consumeRecord)
	{
		int chargeTime = (int)((consumeRecord.getEndTime() - consumeRecord.getStartTime())/60);
		if(chargeTime> GameConfig.maxChargeTime)
		{
			return -1;
		}
		long timeStart = 0;
		long timeEnd = DateUtil.getCurrentSeconds();
		List<TblChargingOrder> chargeOrderList = DB.chargeOrderDao.selectChargeData(consumeRecord.getSerialNo());
		if (chargeOrderList != null && chargeOrderList.size() > 0) {
			TblChargingOrder chargeOrder = chargeOrderList.get(0);
			timeStart = DateUtil.toLong(DateUtil.parse(chargeOrder.getChargeBegintime(),"yyyy-MM-dd HH:mm:ss"))/1000;
		}
		if(chargeTime<0) {
			if (timeStart != 0) consumeRecord.setStartTime(timeStart);
			consumeRecord.setEndTime(timeEnd);
			logger.warn(LogUtil.addExtLog("1 serialNo|startTime|endTime"),
					new Object[]{consumeRecord.getSerialNo(),consumeRecord.getStartTime(),consumeRecord.getEndTime()});
		} else {
			chargeTime = (int) ((consumeRecord.getEndTime() - timeEnd) / 60);
			if (chargeTime > 0) {
				consumeRecord.setEndTime(timeEnd);
				logger.warn(LogUtil.addExtLog("2 serialNo|startTime|endTime"),
						new Object[]{consumeRecord.getSerialNo(),consumeRecord.getStartTime(),consumeRecord.getEndTime()});
			} else if (timeStart > 0) {
				chargeTime = (int) ((consumeRecord.getStartTime() - timeStart) / 60);
				if (chargeTime > 240 || chargeTime < -240) {
					consumeRecord.setStartTime(timeStart);
					logger.warn(LogUtil.addExtLog("3 serialNo|startTime|endTime"),
							new Object[]{consumeRecord.getSerialNo(),consumeRecord.getStartTime(),consumeRecord.getEndTime()});
				}
			}
		}
		if(consumeRecord.getTotalDl()>GameConfig.maxChargeMeterNum  || consumeRecord.getTotalDl()<0 )
		{
			return -2;
		}
		return 0;
		
	}
	public int checkChargeAmt(int usrId,int fronzeAmt,int payMode,ConsumeRecord consumeRecord)
	{
		int ret = checkChargeAmt(String.valueOf(usrId), UserConstants.ORG_I_CHARGE, consumeRecord);
		if (ret < 0 || payMode != EpConstants.P_M_FIRST) return ret;

		int chargeAmt = consumeRecord.getTotalChargeAmt();
		int serviceAmt = consumeRecord.getServiceAmt();
		int consumeAmt = chargeAmt+serviceAmt;
		String epCode = consumeRecord.getEpCode();
		int gunNo = consumeRecord.getEpGunNo();
		String serialNo = consumeRecord.getSerialNo();
		int chargeCost=0;
		if (consumeRecord.getType() == 1) fronzeAmt = fronzeAmt * 100;
		
		if(fronzeAmt< consumeAmt)
		{
			int diff = consumeAmt-fronzeAmt;
			logger.error(LogUtil.addBaseExtLog("stat error,fronzeAmt| < totalConsumeAmt|diff|serialNo")
					,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,UserConstants.ORG_I_CHARGE,usrId,fronzeAmt,consumeAmt,(consumeAmt-fronzeAmt),serialNo});
			chargeCost= fronzeAmt;
			
			serviceAmt = serviceAmt-diff;//多出的钱从服务金额中扣除
			if(serviceAmt<0)
			{
				serviceAmt=0;
				chargeAmt=fronzeAmt;
			}
			logger.error(LogUtil.addBaseExtLog("chargeAmt|serviceAmt|serialNo")
					,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,UserConstants.ORG_I_CHARGE,usrId,chargeAmt,serviceAmt,serialNo});
			consumeRecord.setTotalChargeAmt(chargeAmt);	
			consumeRecord.setServiceAmt(serviceAmt);
		}
		else
		{
			chargeCost = consumeAmt;
		}
		consumeRecord.setTotalAmt(chargeCost);	//计算总的消费金额
		
		return 0;
	}
	
	public int checkChargeAmt(String usrId,int orgNo,ConsumeRecord consumeRecord)
	{
		int chargeAmt = consumeRecord.getTotalChargeAmt();
		int serviceAmt = consumeRecord.getServiceAmt();
		String epCode = consumeRecord.getEpCode();
		int gunNo = consumeRecord.getEpGunNo();
		String serialNo = consumeRecord.getSerialNo();
		long maxChargeAmt = GameConfig.maxChargeAmt;
		long maxChargeServiceAmt = GameConfig.maxChargeServiceAmt;
		long maxChargeCost = GameConfig.maxChargeCost;
		if (consumeRecord.getType() == 1) {
			maxChargeAmt = GameConfig.maxChargeAmt * 100;
			maxChargeServiceAmt = GameConfig.maxChargeServiceAmt * 100;
			maxChargeCost = GameConfig.maxChargeCost * 100;
		}
		
		if(chargeAmt<0 || chargeAmt>maxChargeAmt)
		{
			logger.error(LogUtil.addBaseExtLog("chargeAmt|serialNo")
					,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,orgNo,usrId,chargeAmt,serialNo});
			return -1;
		}
		if(serviceAmt<0 || serviceAmt>maxChargeServiceAmt)
		{
			logger.error(LogUtil.addBaseExtLog("serviceAmt|serialNo")
					,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,orgNo,usrId,serviceAmt,serialNo});
			return -2;
		}
		logger.info(LogUtil.addBaseExtLog("chargeAmt|serviceAmt|serialNo")
				,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,orgNo,usrId,chargeAmt,serviceAmt,serialNo});
	    
		int consumeAmt = chargeAmt+serviceAmt;
		if(consumeAmt<0 || consumeAmt>maxChargeCost)
		{
			logger.error(LogUtil.addBaseExtLog("consumeAmt|serialNo")
					,new Object[]{LogConstants.FUNC_END_CHARGE,epCode,gunNo,orgNo,usrId,consumeAmt,serialNo});
			return -3;
		}

		consumeRecord.setTotalAmt(consumeAmt);	//计算总的消费金额
		
		return 0;
	}

	public  int checkConsumeRecord(ChargeCache ccObj,String consumeAccount)
	{
		//充电记录没有
		if(ccObj ==null)
		{
			logger.info("[endcharge]checkConsumeRecord!epCode:{},epGunNo:{},did not find charge record",
					new Object[]{this.epCode,this.epGunNo});
			
			return 2;
		}
		
		String serialNo = ccObj.getChargeSerialNo();
		
		//4.如果已经结算,那么立即还回
		int chargeStatus = ccObj.getStatus();
		if( chargeStatus == ChargeRecordConstants.CS_CHARGE_FAIL
				||chargeStatus == ChargeRecordConstants.CS_STAT
				)
		{
			//已经失败或者已经结算的充电不结算
			logger.info("[endcharge]checkConsumeRecord,had no order,have charge record status=3,chargeSerialNo:{}",serialNo);
			
			return 3;
		}
		

	
	    UserCache chargeUser =  getChargeUser(ccObj,consumeAccount);
	    if(chargeUser==null)
	    {
		     logger.info("endcharge did not find user,accountId:{},epCode:{},epGunNo:{}:chargeSerialNo:{}",
				new Object[]{ccObj.getUserId(),epCode,epGunNo,ccObj.getChargeSerialNo()});
		     
		     return 2;
	    }
		
		return 1;
		
	}
	
	/**
	 *
	 * @param consumeRecord
	 * @return  
	 * 3:异常
	   2:充电记录不存在
	   1:处理成功
	 */
	public int statFirstPayCharge(ChargeCache ccObj,ConsumeRecord consumeRecord)
	{	
		logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE, "ccObj|consumeRecord"), ccObj,consumeRecord);
		try
		{ 
			int chargeUserId= ccObj.getUserId();
			String chargeUserAccount = ccObj.getAccount();
			ccObj.setEt(consumeRecord.getEndTime());
			
			boolean isPauseStat = (ccObj.getStatus() == ChargeRecordConstants.CS_PAUSE_STAT)?true:false;

			String stopCause = consumeRecord.getStopCause();
			int cause = 0;
			if (stopCause.indexOf("|") > 0) {
				cause = Integer.valueOf(stopCause.split("|")[0]);
			} else {
				cause = Integer.valueOf(stopCause);
			}
			gotoStopChargeStatus(1,cause);
			//1.检查充电金额
			int retCheckChargeAmt = checkChargeAmt(chargeUserId,ccObj.getFronzeAmt(),
					ccObj.getPayMode(),consumeRecord);
			
			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE, "chargeUserId|chargeCacheObj"), chargeUserId,ccObj);
			ccObj.setStatus(ChargeRecordConstants.CS_STAT);
			//2.检查度数和时间
			int retCheckChargeTimeAndMeter = checkChargeTimeAndMeter(consumeRecord);
			EpChargeService.calcChargeAmt(ccObj.getRateInfo(), consumeRecord);

			int totalAmt = 0;
			int totalChargeMeterNum=0;
			int totalChargeTime=0;
			
			//钱和电表读数正确，时间正确.记录正常订单.统计钱,电表读数,时间
			//钱和电表读数正确，时间错误.记录正常订单.统计钱,电表读数,统计时间为0
			//钱和电表读数错误，不管时间如何,记录异常订单.不做统计
			if(retCheckChargeTimeAndMeter>=0)//时间正确，取得时间统计值
			{
				totalChargeTime = consumeRecord.getChargeUseTimes();
				totalChargeMeterNum = consumeRecord.getTotalDl();
			}
			
	
			UserCache chargeUser = UserService.getUserCache(chargeUserId);
			
			int userFirst = EpChargeService.isNewCouponStatus(currentType,chargeUser.getNewcouponAcStatus(),chargeUser.getNewcouponDcStatus());
		
			//1.处理正常的消费记录数据
			int couPonAmt = 0; //优惠券金额
			if(retCheckChargeAmt>=0 && retCheckChargeTimeAndMeter>=0)
			{
				couPonAmt = EpChargeService.handleRightConsumeRecord(this,consumeRecord,
						ccObj,chargeUser,currentType,totalChargeTime,
						 totalChargeMeterNum,isPauseStat);
				
				//e租网
				if(checkTCOrgNo(ccObj.getUserOrigin().getOrgNo()) == 1) {
					this.handleChargeOrder(ccObj.getUserOrigin().getOrgNo(), consumeRecord, ccObj.getRateInfo());
					return 1;
				}
				if (checkOrgNo(UserConstants.ORG_SHSTOP) == 1) {
					this.handleChargeOrder(UserConstants.ORG_SHSTOP, consumeRecord, ccObj.getRateInfo());
				}

				totalAmt = consumeRecord.getTotalAmt();
			}
			else
			{
				logger.error("endcharge  StatAmtRet error,accountId:{},epCode:{},epGunNo:{},meterNum:{},totalAmt:{},chargeTime:{}", 
						new Object[]{chargeUserId,consumeRecord.getEpCode(),consumeRecord.getEpGunNo(),
						consumeRecord.getTotalDl(),totalAmt,consumeRecord.getChargeUseTimes()});
				
				//记录异常订单数据
				EpChargeService.updateChargeToDb(this,ccObj, 
						consumeRecord,true,new BigDecimal(0.0),0,0,ccObj.getRateInfo().getServiceRate());
				
			}
		
			//4.故障记录到故障表
			EpChargeService.insertFaultRecord(consumeRecord.getStopCause(),epCode,this.pkEpId,epGunNo,consumeRecord.getSerialNo(),new Date(consumeRecord.getEndTime()*1000));
			//5.非主动停止充电,给用户发短信
			int orgn=ccObj.getUserOrigin().getOrgNo();
			if(cause >2 && chargeUserAccount!=null && (orgn == 0 || orgn == UserConstants.ORG_I_CHARGE))
			{
				this.onChargeNotice(cause,chargeUserAccount);
			}
			
			//7.给前段发消息
			String chOrCode = ccObj.getChOrCode();
			
			
			Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();

			respMap.put("epcode", epCode);
			respMap.put("epgunno", epGunNo);
			
			respMap.put("orgn", orgn);
			respMap.put("token", ccObj.getToken());
			respMap.put("usrLog", ccObj.getThirdUsrIdentity());
			respMap.put("usrId", this.getCurUserId());
			respMap.put("pkEpId", pkEpId);
			
			respMap.put("orderid",chOrCode);		   
			 //用户新手状态
			respMap.put("userFirst",userFirst);
			//优惠券面值金额
			respMap.put("couPonAmt",couPonAmt);
			//实际优惠金额
			int realCouPonAmt = consumeRecord.getRealCouponAmt();//实际优惠金额
			respMap.put("realCouPonAmt",realCouPonAmt);
			
			if(UsrGateService.isComm(ccObj.getUserOrigin()) )
			{
				logger.debug("endchargeWithConsumeRecord send phone,epCode:{},epGunNo:{}, chargeUserId:{}",new Object[]{epCode,epGunNo, chargeUserId});
				
				handleEvent(EventConstant.EVENT_CONSUME_RECORD,0,0,respMap,(Object)consumeRecord);
				logger.info("endcharge send to UsrGate,accountId:{},account:{},epCode:{},epGunNo:{},chargeSerialNo:{},userFirst:{},couPonAmt:{},realCouPonAmt:{}",
						new Object[]{ccObj.getUserId(),consumeRecord.getEpUserAccount(),epCode,epGunNo,
						ccObj.getChargeSerialNo(),userFirst,couPonAmt,realCouPonAmt});
			}
			else
			{
				logger.info("endcharge send to api,accountId:{},account:{},epCode:{},epGunNo:{},chargeSerialNo:{}",
						new Object[]{ccObj.getUserId(),consumeRecord.getEpUserAccount(),epCode,epGunNo,ccObj.getChargeSerialNo()});
				
				if(orgn == UserConstants.ORG_I_CHARGE)	
				{
				   AppApiService.sendStopChargeByPhoneDisconnect(epCode,epGunNo, chargeUserId,1,0,
						consumeRecord.getChargeUseTimes());
				}
			}
		
			return 1;
		}
		catch (Exception e) {
			logger.error(LogUtil.addFuncExtLog("consumerecord|exception"), consumeRecord, e.getStackTrace());
			return 3;
		}
		
	}
	
	/***
	 * 1:取消预约,2：预约到期，3：充电开始
	 */
	public void endBespoke(int style)
	{
		logger.debug("endbespoke,epCode:{},epGunNo:{},style:{}",new Object[]{epCode,epGunNo,style});
		BespCache bespCacheObj = getBespCache();
		if(bespCacheObj == null)
		{
			logger.info("endbespoke fail!bespCacheObj ==null");
			return ;
		}
		logger.info("endbespoke,accountId:{},bespNo:{},epCode:{},epGunNo:{},style:{}",
				new Object[]{bespCacheObj.getAccountId(),bespCacheObj.getBespNo(),epCode,epGunNo,style});
		
        
		if( bespCacheObj.getStatus() != EpConstants.BESPOKE_STATUS_LOCK)
		{
			logger.info("endbespoke fail!bespCacheObj.getStatus() error,bespCacheObj:{}",bespCacheObj);
			return ;	
		}
		Date now = new Date();
		bespCacheObj.setEndTime(now.getTime()/1000);
		bespCacheObj.setRealEndTime(now.getTime()/1000);
		
		BigDecimal realBespAmt = EpBespokeService.statBespoke(bespCacheObj);
		
		EpBespokeService.endBespoke(epCode, realBespAmt, bespCacheObj, now);
		
		StatService.subBespoke();
	
		//5.给前端应答
		Map<String, Object> bespokeMap = new ConcurrentHashMap<String, Object>();

		bespokeMap.put("userId",bespCacheObj.getAccountId());
		bespokeMap.put("bespNo",bespCache.getBespNo());
		
		bespokeMap.put("amt",realBespAmt.doubleValue());
		
		BigDecimal curUserBalance = UserService.getRemainBalance(bespCacheObj.getAccountId());
		bespokeMap.put("remainAmt",curUserBalance.doubleValue());
		
		bespokeMap.put("account",bespCacheObj.getAccount());
		
		bespokeMap.put("epCode",epCode);
		bespokeMap.put("epGunNo",epGunNo);
		
		bespokeMap.put("st",bespCacheObj.getStartTime());
		bespokeMap.put("et",bespCacheObj.getEndTime());
		bespokeMap.put("style",style);
		
		handleEvent(EventConstant.EVENT_CANNEL_BESPOKE, 1, 0, null, bespokeMap);

		
		logger.info("endbespoke success,BespNo:{},accountId:{},account:{},realBespAmt:{},remainAmt:{},epcode:{},gunno:{}",new Object[]{
				bespCache.getBespNo(),bespCache.getAccountId(),bespCache.getAccount(),realBespAmt.doubleValue(),curUserBalance.doubleValue(),this.epCode,this.epGunNo});
		
		cleanBespokeInfo();
	}
	
	public boolean init(ElectricPileCache epCache, int epGunNo, int bootLoader) {
		String epCode = epCache.getCode();
		int currentType = epCache.getCurrentType();

		if (currentType != EpConstants.EP_DC_TYPE && currentType != EpConstants.EP_AC_TYPE) {
			logger.error("initConnect,initGun fail,epCode:{},currentType:{} error",
					epCode, currentType);
			return false;
		}

		this.currentType = currentType;
		TblElectricPileGun tblEpGun = EpGunService.getDbEpGun(pkEpId, epGunNo);
		if (tblEpGun == null) {
			logger.error("initConnect,initGun fail,did not find gun,epCode:{},pkEpId:{},epGunNo:{}",
					new Object[] { epCode, pkEpId, epGunNo });
			return false;
		}

		this.chargeCache = null;
		this.bespCache = null;

		this.setPkEpGunId(tblEpGun.getPkEpGunId());

		this.concentratorId = epCache.getConcentratorId();
		this.identyCode = tblEpGun.getQr_codes();
		this.createIdentyCodeTime = tblEpGun.getQrdate() - GameConfig.identycodeTimeout2;

		if (this.realChargeInfo == null || bootLoader == 0) {
			// 1.初始化实时数据
			RealChargeInfo tmpRealChargeInfo = null;
			if (currentType == EpConstants.EP_DC_TYPE) {
				RealDCChargeInfo chargeInfo = new RealDCChargeInfo();
				tmpRealChargeInfo = chargeInfo;
			} else {
				RealACChargeInfo chargeInfo = new RealACChargeInfo();
				tmpRealChargeInfo = chargeInfo;
			}
			tmpRealChargeInfo.init();
			tmpRealChargeInfo.setCurrentType(currentType);
			tmpRealChargeInfo.setEpCode(epCode);
			tmpRealChargeInfo.setEpGunNo(epGunNo);
			// 1.装载实时数据
			boolean loadSuccess = tmpRealChargeInfo.loadFromDb(epCode, epGunNo);

			if (!loadSuccess) {
				if (currentType == EpConstants.EP_DC_TYPE) {
					logger.error("initConnect,initGun fail,did not load in tbl_chargeinfo_dc,epCode:{},epGunNo:{}",
							new Object[] { epCode, epGunNo });
				} else {
					logger.error("initConnect,initGun fail,did not load in tbl_chargeinfo_ac,epCode:{},epGunNo:{}",
							new Object[] { epCode, epGunNo });
				}
				return false;
			}

			this.realChargeInfo = tmpRealChargeInfo;
		}
		if (bootLoader == 1) {
			this.modifyStatus(GunConstants.EP_GUN_STATUS_EP_UPGRADE, true);
			logger.info("initConnect,initGun success,epCode:{},epGunNo:{},status:{},boot=1",
					new Object[] { epCode, epGunNo, status });
			return true;
		}
		int epGunStatusInDb = tblEpGun.getEpState();
		// 以数据库最后枪头状态为准
		this.modifyStatus(epGunStatusInDb, false);

		// 2.取最新的预约中的预约记录
		initBespokeFromDB(epCode, epGunNo);
		// 3.取最新的未完成的充电记录
		initChargeFromDB(epCode, epGunNo);

		logger.info("initConnect,initGun success,epCode:{},epGunNo:{},status:{},boot=0",
				new Object[] { epCode, epGunNo, status });

		return true;
	}
	
	public void initBespokeFromDB(String epCode,int epGunNo)
	{
		BespCache tmpBespCache=null;
		TblBespoke besp = EpBespokeService.getUnStopBespokeFromDb(this.pkEpId, this.pkEpGunId);
		if (besp != null) {
			tmpBespCache = EpBespokeService.makeBespokeCache(besp);
			tmpBespCache.setEpCode(this.epCode);
			tmpBespCache.setEpGunNo(this.epGunNo);
			
			// 检查是否过期,如果过期.那么结算
			long diff  = EpBespokeService.expireTime(tmpBespCache);
		
			if (diff > 0) {
				//结算
				Date now = new Date();
				tmpBespCache.setRealEndTime(now.getTime()/1000);
				if(tmpBespCache.getRealEndTime() > tmpBespCache.getEndTime())
				{
					tmpBespCache.setRealEndTime(tmpBespCache.getEndTime());
				}
				logger.debug("initConnect,epCode:{},besp != null, diff > 0,epGunNo:{}",epCode,epGunNo);
				BigDecimal realBespAmt = EpBespokeService.statBespoke(tmpBespCache);
				EpBespokeService.endBespoke(epCode, realBespAmt, tmpBespCache, now);
			
				tmpBespCache=null;
			} else {
				tmpBespCache.setStatus(EpConstants.BESPOKE_STATUS_LOCK);
			}
		}
		if(tmpBespCache!=null)
		{
			logger.debug("initConnect,has besp in DB,epCode:{},epGunNo:{}",epCode,epGunNo);
			String chargeAccount = tmpBespCache.getAccount();
			//装载未完成充电用户
			UserCache userCache = UserService.getUserCache(chargeAccount);
			
			if(userCache!=null)
			{
				logger.debug("initConnect,set bespinfo to userCache,epCode:{},epGunNo:{}",epCode,epGunNo);
				userCache.addBesp(tmpBespCache);
			}
			this.bespCache = tmpBespCache;
			
		}
	}

	public void initChargeFromDB(String epCode, int epGunNo) {
		// 3.取最新的未完成的充电记录
		ChargeCache tmpChargeCache = EpChargeService.getUnFinishChargeFromDb(epCode, epGunNo);
		if (tmpChargeCache != null) {
			logger.debug("initConnect,has charge in DB,epCode:{},epGunNo:{},status:{}",
					new Object[] { epCode, epGunNo, tmpChargeCache.getStatus() });

			String chargeAccount = tmpChargeCache.getAccount();
			// 装载未完成充电用户
			UserCache userCache = UserService.getUserCache(chargeAccount);

			tmpChargeCache.setEpCode(epCode);
			tmpChargeCache.setEpGunNo(epGunNo);

			if (userCache != null) {
				logger.debug("initConnect,set chargeinfo to userCache,epCode:{},epGunNo:{},userCache:{}",
						new Object[] { epCode, epGunNo, userCache });

				userCache.addCharge(tmpChargeCache);
			}
			// tmpChargeCache.getUserOrigin().setCmdChIdentity(userCache.getAccount());
			this.chargeCache = tmpChargeCache;
		}

		logger.info("initConnect,epCode:{},initGun success,epGunNo:{},status:{}",
				new Object[] { epCode, epGunNo, status });

		return;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
        sb.append("EpGunCache\n");
        sb.append("集中器pkId = ").append(this.getConcentratorId()).append("\n");
        sb.append("电桩pkId = ").append(this.getPkEpId()).append("\n");
        sb.append("电桩编号 = ").append(this.getEpCode()).append("\n");
        
        sb.append("枪口pkId = ").append(this.pkEpGunId).append("\n");
        sb.append("枪口编号 = ").append(this.epGunNo).append("\n");
        //sb.append("当前用户ID = ").append(curUserId).append("\n");
        //sb.append("当前用户账号 = ").append(curUserAccount).append("\n");
        
        sb.append("识别码 = ").append(identyCode).append("\n");
        
        String sTime= "";
        sTime = DateUtil.longDateToString(createIdentyCodeTime*1000);
        sb.append("识别码产生时间 = ").append(sTime).append("\n");
        
        sTime= DateUtil.longDateToString(lastSendToMonitorTime*1000);
        sb.append("监控实时数据更新时间= ").append(sTime).append("\n");
        
        sTime= DateUtil.longDateToString(this.lastUDTime*1000);
        sb.append("数据库实时数据更新时间 = ").append(sTime).append("\n");
        
        sTime= DateUtil.longDateToString(lastUPTime*1000);
        sb.append("手机充电信息更新时间  = ").append(sTime).append("\n");
        
        EpCommClient commClient =(EpCommClient)this.epNetObject;
        if(commClient == null)
        {
        	sb.append("not find comm client\n");
        }
        else
        {
        	sb.append("\nEpCommClient:").append(commClient.toString()).append("\n");
        }
       
        sb.append("枪口状态 = "+status+"(" ).append( EpGunService.getGunStatusDesc(status)).append( ")\n");
        if(this.realChargeInfo ==null)
        {
        	sb.append("无实时数据\n");
        }
        else
        {
        	sb.append("实时数据工作状态 = ").append(this.realChargeInfo.getWorkingStatus() ).append(this.realChargeInfo.getWorkingStatusDesc()).append("\n\n");
        }
        
        if(this.authCache ==null)
        {
        	sb.append("无鉴权用户\n");
        }
        else
        {
        	sb.append(this.authCache.toString() ).append("\n");
        }
        
        if(this.bespCache ==null)
        {
        	sb.append("无预约\n\r\n");
        }
        else
        {
        	sb.append(this.bespCache.toString() ).append("\n");
        }
        
        if(this.chargeCache ==null)
        {
        	sb.append("无充电\n\r\n");
        }
        else
        {
        	sb.append(this.chargeCache.toString() ).append("\n");
        }
        
      
        return sb.toString();
	}
	
	public UserOrigin getBespokeUserOrigin()
	{
		if(bespCache==null)
			return null;
		return bespCache.getUserOrigin();
	}
	public UserOrigin getChargeUserOrigin()
	{
		if(chargeCache==null)
			return null;
		return chargeCache.getUserOrigin();
	}
	public UserOrigin getActionUserOrigin(int action)
	{
		UserOrigin userOrigin=null;
	
		switch(action)
		{
		case EventConstant.EVENT_BESPOKE:
			userOrigin = getBespokeUserOrigin();
			break;
		case EventConstant.EVENT_CANNEL_BESPOKE:
			userOrigin = getBespokeUserOrigin();
			break;
		case EventConstant.EVENT_CHARGE:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_STOP_CHARGE:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_CHARGE_EP_RESP:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_STOP_CHARGE_EP_RESP:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_START_CHARGE_EVENT:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_REAL_CHARGING:
			userOrigin = getChargeUserOrigin();
			break;
		
		case EventConstant.EVENT_CARD_AUTH:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_EP_STATUS:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_CONSUME_RECORD:
			userOrigin = getChargeUserOrigin();
			break;
		
		case EventConstant.EVENT_EP_STAT:
			userOrigin = getChargeUserOrigin();
			break;
		case EventConstant.EVENT_EP_NET_STATUS:
			userOrigin = getChargeUserOrigin();
			break;
		
		default:
			break;
		}
		return userOrigin;
	}
	public int getDefaultUserOrigin(int action)
	{
		int userOrigin=-1;
	
		switch(action)
		{
		case EventConstant.EVENT_BESPOKE:
			
			break;
		case EventConstant.EVENT_CANNEL_BESPOKE:
			userOrigin = 0;//取消预约默认
			break;
		case EventConstant.EVENT_CHARGE:
			
			break;
		case EventConstant.EVENT_STOP_CHARGE:
			
			break;
		case EventConstant.EVENT_START_CHARGE_EVENT:
			
			break;
		case EventConstant.EVENT_REAL_CHARGING:
			
			break;
		
		case EventConstant.EVENT_CARD_AUTH:
			
			break;
		case EventConstant.EVENT_EP_STATUS:
			break;
		case EventConstant.EVENT_CONSUME_RECORD:
			userOrigin=0;
			break;
		
		case EventConstant.EVENT_EP_STAT:
			
			break;
		case EventConstant.EVENT_EP_NET_STATUS:
			
			break;
		case EventConstant.EVENT_ONE_BIT_YX:
		case EventConstant.EVENT_TWO_BIT_YX:
		case EventConstant.EVENT_YC:
		case EventConstant.EVENT_VAR_YC:
		
			userOrigin= UserConstants.CMD_FROM_MONTIOR;
			break;
		default:
			break;
		}
		return userOrigin;
	}
	public void onEvent(int action,int source,UserOrigin userOrigin,int ret,int cause,Object w,Object extraData)
	{
		switch(source)
		{
		case UserConstants.CMD_FROM_API://app api
			AppApiService.onEvent(action,userOrigin,ret,cause,w,extraData);
			break;
		case UserConstants.CMD_FROM_PHONE://phone client
		case UserConstants.CMD_FROM_third:
			
			UsrGateService.onEvent(action,userOrigin,ret,cause,w,extraData);
			break;
		case UserConstants.CMD_FROM_MONTIOR://phone client
			MonitorService.onEvent(action,userOrigin,ret,cause,w,extraData);
		case UserConstants.ORG_PARTNER_MOBILE:
		//	ChinaMobileService.onEvent(action, userOrigin,ret,cause,w,extraData);
			break;
		default:
			logger.error("onEvent,epCode:{},epGunNo:{},error source:{}",
					new Object[]{epCode,epGunNo,source});
			break;
				
		}
		
	}
	public void handleEvent(int action,int ret,int cause,Object w,Object extraData)
	{
		UserOrigin userOrigin = getActionUserOrigin(action);
		
		logger.debug("handleEvent,epCode:{},epGunNo:{},action:{}.userOrigin:{},extraData:{}",
				new Object[]{epCode,epGunNo,action,userOrigin,extraData});
		if(userOrigin!=null)
		{
			onEvent(action,userOrigin.getCmdFromSource(),userOrigin,ret,cause,w,extraData);
		}
		else
		{
			logger.debug("handleEvent,epCode:{},epGunNo:{},action:{},userOrigin:{},curUserId:{}",
					new Object[]{epCode,epGunNo,action,userOrigin,this.getCurUserId()});
		}
		
	}
	
	public void handleEventExtra(int action,int source,int ret,int cause,Object w,Object extraData)
	{
		
		onEvent(action,source,null,ret,cause,w,extraData);
		
	}
	

	public int onEpCancelBespRet(EpCommClient epCommClient, EpCancelBespResp cancelBespResp)
	{
		logger.debug("onEpCancelBespRet,epCommClient:{},cancelBespResp:{}",epCommClient,cancelBespResp);
		
		if(bespCache!=null)
		{
			if(bespCache.getBespNo().compareTo(cancelBespResp.getBespNo()) !=0)
			{
				logger.debug("onEpCancelBespRet bespCache.getBespNo:{},cancelBespResp.getBespNo:{}", bespCache.getBespNo(),cancelBespResp.getBespNo());
				return 0;
			}
			Map<String, Object> bespokeMap = new ConcurrentHashMap<String, Object>();
			int curUserId =  bespCache.getAccountId();
			bespokeMap.put("userId",curUserId);
			//bespokeMap.put("redo",nRedo);
			bespokeMap.put("bespNo",bespCache.getBespNo());
			
			
			if(cancelBespResp.getSuccessFlag() ==0)//取消预约失败
			{	//5.给前端应答
				handleEvent(EventConstant.EVENT_CANNEL_BESPOKE, 0, 0, null, bespokeMap);
			}
			else////取消预约成功
			{
				this.endBespoke( EpConstants.END_BESPOKE_CANCEL);
			}
		}
		else
		{
			logger.debug("onEpCancelBespRet bespCache=null,epCode:{},epGunNo:{}", this.epCode,this.epGunNo);
		}
	
		return 0;
	}
	public int onEpBespokeResp(EpBespResp bespResp)
	{
		if(bespCache==null)
			return 2; //数据不存在

		String epBespokeNo= bespResp.getBespNo();
		String bespokeNo= this.bespCache.getBespNo();
		
		if(bespokeNo.compareTo(epBespokeNo)!=0)
		{
			return 2; //数据不存在
		}
		if(getStatus() == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED && bespCache.getRedo()!=1)
		{
			return 3;//已经处理
		}
		int nEpRedo = bespResp.getnRedo();
		if(bespCache.getRedo()!=nEpRedo) //下发的预约标识和回复的预约标识不一致
		{
			return 2;//数据不存在
		}
		
		int errorCode=2;
		if(bespResp.getSuccessFlag() == 1)
		{
			int curUserId =  getCurUserId();
			errorCode = onEpBespSuccess( bespResp.getBespNo(), bespResp.getnRedo(),curUserId);
		}
		else
		{
			errorCode = onEpBespFail(bespResp.getBespNo(),bespResp.getnRedo());
		}
		return errorCode;
	}
	public int onEpBespSuccess(String bespNo,int nRedo,int usrId)
	{
		logger.info("bespoke response success,accountId:{},bespNo:{},redoFlag:{},epCode:{},epGunNo:{}",
				new Object[]{usrId,bespNo,nRedo,epCode,epGunNo});
		//算钱和时间,并且保存到数据库
		if (nRedo == 0) {
			
			this.onBespokeSuccess(1);
		} else {
			onRedoBespokeSuccess();
		}
		    
		return 1;
	}
	public void do_bespoke_consume_resp(int ret,int cause,int usrId,int redo,String bespokeNo)
	{
		//5.给前端应答
		Map<String, Object> bespokeMap = new ConcurrentHashMap<String, Object>();

		bespokeMap.put("usrId",usrId);
		bespokeMap.put("redo",redo);
		bespokeMap.put("bespNo",bespokeNo);
		
		logger.debug("do_bespoke_consume_resp,EventConstant.EVENT_BESPOKE.ret:{}, cause:{}",ret, cause);
		
		handleEvent(EventConstant.EVENT_BESPOKE, ret, cause, null, bespokeMap);
	}
	public int onEpBespFail(String bespNo,int nRedo)
	{
		int usrId = this.getCurUserId();
		logger.info("bespoke response fail,accountId:{},bespNo:{},redoFlag:{},epCode:{},epGunNo:{}",
				new Object[]{usrId,bespNo,nRedo,epCode,epGunNo});
		//电桩可能会发多条预约上来
		if(this.bespCache!=null && this.bespCache.getStatus() == EpConstants.BESPOKE_STATUS_CONFIRM)
		{
			do_bespoke_consume_resp(0,0,usrId,nRedo,bespNo);
		
			String messagekey = String.format("%03d%s", Iec104Constant.C_BESPOKE,bespCache.getBespNo());
		
		    EpCommClientService.removeRepeatMsg(messagekey);
		
			if (nRedo == 0)// 预约失败
			{
				bespCache.setStatus(EpConstants.BESPOKE_STATUS_FAIL);
				
				this.cleanBespokeInfo();
			}
		}
		
		return 1;
	}
	
	public int dropCarPlaceLockAction()
	{
		EpCommClient commClient= (EpCommClient)getEpNetObject();
		if(commClient ==null || !commClient.isComm())
		{
			logger.debug("dropCarPlaceLockAction commClient is null,epCode:{},epGunNo:{}",this.epCode,this.epGunNo);
			return ErrorCodeConstants.EP_UNCONNECTED;//
		}
		
		if(!commClient.isComm())
		{
			
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		byte[] data = EpEncoder.do_drop_carplace_lock(epCode, epGunNo);
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		EpMessageSender.sendMessage(commClient,0,0, Iec104Constant.C_DROP_CARPLACE_LOCK,data,cmdTimes,commClient.getVersion());
		
		return 0;
	}

	
	// 过期,强制结束
	public void forceEndBespoke() 
	{
		BespCache bespokeCache = this.getBespCache();
		if(bespokeCache==null || bespokeCache.getStatus() != EpConstants.BESPOKE_STATUS_LOCK)
			return ;
		
		EpCommClient commClient = (EpCommClient)getEpNetObject();
		if(commClient!=null && commClient.isComm())
		{
			//向桩发取消预约
			byte[] cancelMsg = EpEncoder.do_cancel_bespoke( epCode, (byte)epGunNo, bespokeCache.getBespNo());
		    byte[] cmdTimes = WmIce104Util.timeToByte();
			EpMessageSender.sendMessage(commClient, (short)0, 0, (byte)Iec104Constant.C_CANCEL_BESPOKE, cancelMsg,cmdTimes,commClient.getVersion());
		}
		else
		{
			this.endBespoke( EpConstants.END_BESPOKE_EXPIRE_TIME);
			
		}
	}
	private ChargingInfo calcCharingInfo()
	{
		
		long now = DateUtil.getCurrentSeconds();
		
		this.lastUPTime= now;
		ChargingInfo charingInfo = new ChargingInfo();
		
		
		
		if(this.chargeCache!=null)
		{
			charingInfo.setFronzeAmt(this.chargeCache.getFronzeAmt());
		}
		else
		{
			charingInfo.setFronzeAmt(0);
		}
		if(this.status != GunConstants.EP_GUN_STATUS_CHARGE)
		{
			charingInfo.setChargeAmt(0);
			charingInfo.setTotalTime(0);
			charingInfo.setChargeMeterNum(0);
		}
		else
		{
			
			charingInfo.setChargeAmt(this.realChargeInfo.getChargedCost());
			charingInfo.setTotalTime(this.realChargeInfo.getChargedTime());
			charingInfo.setChargeMeterNum(this.realChargeInfo.getChargedMeterNum());
		}
		charingInfo.setOutVol(this.realChargeInfo.getOutVoltage());
		charingInfo.setOutCurrent(this.realChargeInfo.getOutCurrent());
		
		charingInfo.setRateInfo(realChargeInfo.getChargePrice()/10);
		if(currentType == EpConstants.EP_DC_TYPE)
		{
			charingInfo.setSoc(((RealDCChargeInfo)realChargeInfo).getSoc());
		}
		else
		{
			charingInfo.setSoc(0);
		}
		charingInfo.setDeviceStatus(0);
		charingInfo.setWarns(0);
		charingInfo.setWorkStatus(this.status);
		
		return charingInfo;
		
	}
	public void pushFirstRealData()
	{
		
		 ChargingInfo  chargingInfo = calcCharingInfo();
		
		if(chargingInfo!=null)
		{
			Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
			respMap.put("epcode", epCode);
			respMap.put("epgunno", epGunNo);
			respMap.put("usrId", getCurUserId());
			if(this.chargeCache!=null)
			{
				int orgn=chargeCache.getUserOrigin().getOrgNo();
				respMap.put("orgn", orgn);
				respMap.put("token", chargeCache.getToken());
				respMap.put("usrLog", chargeCache.getThirdUsrIdentity());
			}
			else
			{
				
				respMap.put("orgn", 0);
				respMap.put("token", "");
				respMap.put("usrLog", "");
			}
			handleEvent(EventConstant.EVENT_REAL_CHARGING,0,0,respMap,(Object)chargingInfo);
		}
	}
	

	
	
	public  void  onChargeNotice(int stopCause,String curUserAccount)
	{
		logger.debug("onChargeNotice send msg,stopCause:{},curUserAccount:{}",stopCause,curUserAccount);
		String stopChargeDesc= EpChargeService.getStopChargeDesc(stopCause);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		if(GameConfig.sms ==1)
		{
			try
			{
				String content = MessageFormat.format("结束充电提醒：尊敬的用户，由于{0}，您的本次充电于{1}结束，请收枪后查看结算信息。", stopChargeDesc,dateFormat.format(new Date()));
				MobiCommon.sendWanMatMessage(content,curUserAccount);
			}
			catch(Exception e)
			{
				logger.error("onChargeNotice fail,e.getMessage:{}",e.getMessage());
				
			}
		}
		else if(GameConfig.sms ==2)
		{
		
			HashMap<String,Object>  params=new HashMap<String,Object>();
			params.put("error", stopChargeDesc);
			params.put("time", dateFormat.format(new Date()));
			
			JSONObject jsonObject = JSONObject.fromObject(params);
			
			boolean flag=AliSMS.sendAliSMS(curUserAccount, "SMS_16775083", jsonObject.toString());
			if(!flag)
			{
				logger.debug("SMS:onChargeNotice fail,userAccount:{}",curUserAccount);
			}
		}
	}
	
	public void dispatchWholeRealToMonitor(int preWorkStatus)
	{
		logger.debug("realData dispatchWholeRealToMonitor,epCode:{},epGunNo:{}",epCode,epGunNo);
		Map<Integer, SingleInfo> oneYxRealInfo=null;
		if(currentType== EpConstants.EP_AC_TYPE)
			oneYxRealInfo = ((RealACChargeInfo)realChargeInfo).getWholeOneBitYx();
		else
			oneYxRealInfo = ((RealDCChargeInfo)realChargeInfo).getWholeOneBitYx();
		
		
		 sendToMonitor(EventConstant.EVENT_ONE_BIT_YX,null,0,0,(Object)oneYxRealInfo);
			
		 Map<Integer, SingleInfo> twoYxRealInfo=null;
		 if(currentType== EpConstants.EP_AC_TYPE)
			 twoYxRealInfo = ((RealACChargeInfo)realChargeInfo).getWholeTwoBitYx();
		else
			twoYxRealInfo = ((RealDCChargeInfo)realChargeInfo).getWholeTwoBitYx();
			
		 
		 sendToMonitor(EventConstant.EVENT_TWO_BIT_YX,null,0,0,(Object)twoYxRealInfo);
			
		 Map<Integer, SingleInfo> ycRealInfo=null;
		 if(currentType== EpConstants.EP_AC_TYPE)
			 ycRealInfo = ((RealACChargeInfo)realChargeInfo).getWholeYc();
		else
			ycRealInfo = ((RealDCChargeInfo)realChargeInfo).getWholeYc(preWorkStatus);
			
		
		//遥测
		 sendToMonitor(EventConstant.EVENT_YC,null,0,0,(Object)ycRealInfo);
		 
		 
		 Map<Integer, SingleInfo> varYcRealInfo=null;
		 if(currentType== EpConstants.EP_AC_TYPE)
			 varYcRealInfo = ((RealACChargeInfo)realChargeInfo).getWholeVarYc();
		else
			varYcRealInfo = ((RealDCChargeInfo)realChargeInfo).getWholeVarYc();
		
		 
		sendToMonitor(EventConstant.EVENT_VAR_YC,null,0,0,(Object)varYcRealInfo);
	
		changeYcMap.remove(1);
		long now = DateUtil.getCurrentSeconds();
		setLastSendToMonitorTime(now);
		
	}
	
	/**
	 * 处理大账户消费记录（北汽出行、西安一卡通）
	 * @param consumeRecord
	 * @param epGunCache
	 * @return   4：无效的交易流水号
	 * 			3:已经处理
				2:数据不存在
				1:处理成功
	 */
	public int endBigConsumeRecord(ConsumeRecord consumeRecord )
	{
		String cardInNo= consumeRecord.getEpUserAccount();
		
		int orgNo = consumeRecord.getUserOrgin();
        UserCache userInfo = UserService.getUserIdByOrgNo(orgNo);
		if(userInfo==null)
		{
			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE,"not find user info,cardInNo|orgNo"),cardInNo,orgNo);
			return 4;
		}
		//检查有没有卡，如果没有，插入一条
		ChargeCardCache cardCache= UserService.getCard(cardInNo);
		int pkCardId = 0;
		if(cardCache==null)
		{
			pkCardId = UserService.insertBigCard(cardInNo, orgNo, userInfo.getId());
		}
		else
		{
			pkCardId=cardCache.getId();
		}
		
		UserCache cardUser= UserService.getUserCache(userInfo.getId());
		
		int orderStatus = EpChargeService.getChargeOrderStatus(consumeRecord.getSerialNo());
		logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE, "serialNo|orderStatus"),consumeRecord.getSerialNo(),orderStatus);
		if(orderStatus==2|| orderStatus==3)//
			return 3;
		if(cardUser==null)
		{
			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE, "not find user info,cardInNo"),cardInNo);
			return 4;
		}
		logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_END_CHARGE, "cardUser"),cardUser);
		
		RateInfo rateInfo = RateService.getRateInfo(getEpCode()).getRateInfo();
		String chOrCode = EpChargeService.makeChargeOrderNo(this.pkEpGunId,cardUser.getId());
		
		int chorType= EpChargeService.getOrType(cardUser.getLevel());
		
		int chargeTime = (int)((consumeRecord.getEndTime()-consumeRecord.getStartTime())/60);			
		EpChargeService.addChargeStat(getPkEpGunId(),consumeRecord.getTotalDl(),chargeTime,consumeRecord.getTotalAmt());

		boolean exceptionData = false;
		if (checkChargeAmt(consumeRecord.getEpUserAccount(),consumeRecord.getUserOrgin(),consumeRecord) < 0) exceptionData = true;
		EpChargeService.insertChargeWithConsumeRecord(cardUser.getId(),chorType,cardUser.getAccount(),pkCardId,consumeRecord.getUserOrgin(),getPkEpGunId(),
				getEpCode(),getEpGunNo(),EpConstants.CHARGE_TYPE_CARD,"",chOrCode,2,new BigDecimal(0.0),0,0,consumeRecord,
				rateInfo,rateInfo.getServiceRate(),exceptionData);
		
		cardUser.removeCharge(consumeRecord.getSerialNo());
		return 1;
	}
	/**
	 *处理大账户信用卡
	 * @param consumeRecord
	 * @return  4：无效的交易流水号
	 * 			3:已经处理
				2:数据不存在
				1:处理成功
	 */
	public int endCreditConsumeRecord(ConsumeRecord consumeRecord )
	{
		String cardInNo= consumeRecord.getEpUserAccount();
		ChargeCardCache cardCache = UserService.getCard(cardInNo);
		if(cardCache==null)
		{
			logger.info("endcharge endCreditConsumeRecord not find user info,cardInNo:{}",cardInNo);
			return 4;
		}
		UserCache cardUser= UserService.getUserCache(cardCache.getUserId());
		int payMode = DB.phDao.getPayMode(cardUser.getAccountId());

		int orderStatus = EpChargeService.getChargeOrderStatus(consumeRecord.getSerialNo());
		logger.debug("endcharge endCreditConsumeRecord serialNo:{},orderStatus:{}",consumeRecord.getSerialNo(),orderStatus);
		if(orderStatus==2|| orderStatus==3)//
			return 3;
		if(cardUser==null)
		{
			logger.info("endcharge endCreditConsumeRecord not find user info,cardInNo:{}",cardInNo);
			return 4;
		}
		logger.debug("endcharge endCreditConsumeRecord cardUser:{}",cardUser);
		
		BigDecimal servicePrice=null;
		int discountType=0;
		boolean exceptionData = false;
		if (checkChargeAmt(consumeRecord.getEpUserAccount(),consumeRecord.getUserOrgin(),consumeRecord) < 0) exceptionData = true;
		Map<String,Object> map =  new HashMap<String,Object> ();
		map.put("cpyId", cardUser.getCpyId());
		map.put("levelId", cardUser.getLevel());
		map.put("pkEpId", pkEpId);
		int rateInfoId = DB.rateInfoDao.findPerRateId(map);
		RateInfo rateInfo;
		if (rateInfoId > 0) {
			rateInfo = RateService.getRateById(rateInfoId).getRateInfo();
		} else {
			rateInfo = RateService.getRateInfo(getEpCode()).getRateInfo();
		}
		EpChargeService.calcChargeAmt(rateInfo, consumeRecord);
		if(EpChargeService.isVinCodeDiscount(epCode, epGunNo,0,EpConstants.P_M_POSTPAID, consumeRecord))
		{
			servicePrice = consumeRecord.getDiscountServicePrice();
			discountType = ChargeRecordConstants.CHARGEORDER_THIRDTYPE_VIN;
		}
		else
		{
			servicePrice = rateInfo.getServiceRate();
		}
		int pkVinCode= consumeRecord.getDiscountIdentity();
		
		logger.info("endcharge endCreditConsumeRecord epCode:{},epGunNo:{},discountType:{},vinCode:{},pkVinCode:{},chargeSerialNo:{},discountServicePrice:{},rateInfo.getServiceRate():{}",
				new Object[]{epCode,epGunNo,discountType,consumeRecord.getCarVinCode(),pkVinCode,consumeRecord.getSerialNo(),servicePrice,rateInfo.getServiceRate()});
		
		String chOrCode = EpChargeService.makeChargeOrderNo(this.pkEpGunId,cardUser.getId());
		
		int chorType= EpChargeService.getOrType(cardUser.getLevel());
		
		int chargeTime = (int)((consumeRecord.getEndTime()-consumeRecord.getStartTime())/60);
		
		EpChargeService.addChargeStat(getPkEpGunId(),consumeRecord.getTotalDl(),chargeTime,consumeRecord.getTotalAmt());
		
		BigDecimal discountAmt = NumUtil.intToBigDecimal2(consumeRecord.getRealCouponAmt());
		if (consumeRecord.getType() == 1) discountAmt = NumUtil.intToBigDecimal4(consumeRecord.getRealCouponAmt());
		
		BigDecimal chargeAmt,serviceAmt;
		if (cardCache.getCardType() != 12 && consumeRecord.getTransType() == 2 && !exceptionData) {
			if (consumeRecord.getType() == 0) {
				chargeAmt = NumUtil.intToBigDecimal2(consumeRecord.getTotalChargeAmt());
				serviceAmt = NumUtil.intToBigDecimal2(consumeRecord.getServiceAmt());
			} else {
				chargeAmt = NumUtil.intToBigDecimal4(consumeRecord.getTotalChargeAmt());
				serviceAmt = NumUtil.intToBigDecimal4(consumeRecord.getServiceAmt());
			}
			RateService.addPurchaseHistoryToDB(chargeAmt.add(serviceAmt),1,cardUser.getId(),0,"充电消费",epCode,consumeRecord.getSerialNo(),"",cardUser.getAccountId());
			BigDecimal frozenAmt = cardUser.getMoney();
			BigDecimal presentAmt = cardUser.getPresent();
			BigDecimal consumeAmt = chargeAmt.add(serviceAmt);
			BigDecimal kyAmt = frozenAmt.subtract(presentAmt);
			if (consumeAmt.compareTo(kyAmt) <= 0) {
				UserService.subAmt(cardUser.getId(), consumeAmt, new BigDecimal(0), consumeRecord.getSerialNo());
			} else {
				if (consumeAmt.compareTo(frozenAmt) <= 0) {
					UserService.subAmt(cardUser.getId(), consumeAmt, consumeAmt.subtract(kyAmt), consumeRecord.getSerialNo());
				} else {
					UserService.subAmt(cardUser.getId(), frozenAmt, presentAmt, consumeRecord.getSerialNo());
				}
			}
		}
		EpChargeService.insertChargeWithConsumeRecord(cardUser.getId(),chorType,cardUser.getAccount(),cardCache.getId(),cardCache.getCompanyNumber(),getPkEpGunId(),
				getEpCode(),getEpGunNo(),EpConstants.CHARGE_TYPE_CARD,"",chOrCode,payMode,discountAmt ,pkVinCode,discountType,consumeRecord,
				rateInfo,servicePrice,exceptionData);

		if (checkOrgNo(UserConstants.ORG_SHSTOP) == 1) {
			this.handleChargeOrder(UserConstants.ORG_SHSTOP,consumeRecord,rateInfo);
		}
		return 1;
		
	}
	public void handleGun2CarLinkStatus(int status)
	{
		int usrId=this.getCurUserId();
       
		UsrGateService.handleGun2CarLinkStatus(status,(long)usrId,this.epCode,this.epGunNo);	
	}
	
	public void handleGunWorkStatus(int oldstatus, int status)
	{
		int usrId=this.getCurUserId();
    
		
	
		UsrGateService.handleGunWorkStatus(oldstatus, status,(long)usrId,this.epCode,this.epGunNo);	
	}
	/**
	 *处理萍乡特殊的消费记录
	 * @param consumeRecord
	 * @return  4：无效的交易流水号
	 * 			3:已经处理
				2:数据不存在
				1:处理成功
	 */
	public int endPXConsumeRecord(ConsumeRecord consumeRecord )
	{
		
		String account= GameConfig.bigAccount1002;
		logger.debug("endcharge endPXConsumeRecord,account:{}",account);
		
		
		UserCache cardUser= UserService.getUserCache(account);
		if(cardUser==null)
		{
			logger.info("endcharge endPXConsumeRecord not find user info,account:{}",account);
			return 4;
		}
		logger.debug("endcharge endPXConsumeRecord,cardUser:{}",cardUser);
		
			
		
		RateInfo rateInfo = RateService.getRateInfo(getEpCode()).getRateInfo();
		
		String chOrCode = EpChargeService.makeChargeOrderNo(this.pkEpGunId,cardUser.getId());
		
		
		int chorType= EpChargeService.getOrType(3);
		
		int chargeTime = (int)((consumeRecord.getEndTime()-consumeRecord.getStartTime())/60);
		logger.debug("endcharge endPXConsumeRecord 4-1002-5:chargeTime{}",chargeTime);
		EpChargeService.addChargeStat(getPkEpGunId(),consumeRecord.getTotalDl(),chargeTime,consumeRecord.getTotalAmt());

		boolean exceptionData = false;
		if (checkChargeAmt(consumeRecord.getEpUserAccount(),consumeRecord.getUserOrgin(),consumeRecord) < 0) exceptionData = true;
		EpChargeService.insertChargeWithConsumeRecord(cardUser.getId(),chorType,cardUser.getAccount(),0,consumeRecord.getUserOrgin(),getPkEpGunId(),
				getEpCode(),getEpGunNo(),EpConstants.CHARGE_TYPE_ACCOUNT,"",chOrCode,2,
				new BigDecimal(0.0),0,0,consumeRecord,rateInfo,rateInfo.getServiceRate(),exceptionData);
		
		return 1;
	}

	public int canRedoBespoke(int bespUsrId,String redoBespNo)
	{
		if(bespCache ==null)
			return ErrorCodeConstants.BESP_NO_NOT_EXIST;// 
		
		String bespNo = bespCache.getBespNo();
		if(bespNo.compareTo(redoBespNo)!=0)//没有同编号的预约
		{
			logger.info("[bespoke]canRedoBespoke,bespNo:{},redoBespNo",bespNo,redoBespNo);
			return ErrorCodeConstants.BESP_NO_NOT_EXIST;//
		}
		return 0;
		
	}
	public int canBespoke(int bespUsrId)
	{
		//先判断业务
		if( status == GunConstants.EP_GUN_STATUS_CHARGE||
		    status == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED||
		    status== GunConstants.EP_GUN_STATUS_USER_AUTH)
		{
			int curUsrId = getCurUserId();
					
			 if(status == GunConstants.EP_GUN_STATUS_CHARGE)
			 {
				 if(curUsrId!=bespUsrId)
				 {
					 return ErrorCodeConstants.EPE_OTHER_CHARGING;
				 }
				 else
				 {
					 return ErrorCodeConstants.CAN_NOT_BESP_IN_ELE;
				 }
			 }
			 else if(status == GunConstants.EP_GUN_STATUS_BESPOKE_LOCKED)
			 {
				 if(curUsrId!=bespUsrId)
				 {
					 return ErrorCodeConstants.EPE_OTHER_BESP;
				 }
				 else
				 {
					 return ErrorCodeConstants.SELF_HAVED_BESP;
				 }
			 }
			 else
			 {
				return ErrorCodeConstants.USED_GUN;
			 }
		}
		else if( status ==  GunConstants.EP_GUN_STATUS_SETTING||
				 status == GunConstants.EP_GUN_STATUS_EP_OPER||
			     status == GunConstants.EP_GUN_STATUS_SELECT_CHARGE_MODE||
				    status ==  GunConstants.EP_GUN_STATUS_WAIT_CHARGE)
		{
			return ErrorCodeConstants.EPE_IN_EP_OPER;//
		}
		else if( status>30 || 
				 status == GunConstants.EP_GUN_STATUS_EP_INIT||
				 status== GunConstants.EP_GUN_STATUS_OFF_LINE||
			     status == GunConstants.EP_GUN_STATUS_STOP_USE)
		{
			return ErrorCodeConstants.EPE_GUN_FAULT;
		}	
		else if( status == GunConstants.EP_GUN_STATUS_EP_UPGRADE)
		{
			return ErrorCodeConstants.EP_UPDATE;//
		}
		
		return 0;
	}
	/**
	 * >0:可以直接返回的错误
	 * 0：当前用户在充电其他用户正在充电
	 * -1：没有充电 
	 * @param chargingUsrId
	 * @return
	 */
	public int canWatchCharge(int chargingUsrId)
	{
		if(chargingUsrId<=0)
			return -1;
		if( status == GunConstants.EP_GUN_STATUS_CHARGE)
		{
			int curUsrId = getCurUserId();
			 if(curUsrId<=0 || curUsrId != chargingUsrId)
			 {
				 logger.error("canCharge innormal,status:{},curUsrId:{}",
						 new Object[]{status,curUsrId,chargingUsrId});
				 return ErrorCodeConstants.EPE_OTHER_CHARGING;
		     }
			 else
			 {
		         return 0;
			 }
		}
		else
		{
			return -1;
		}
				
		
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
				 logger.error("canCharge innormal,status:{},curUsrId:{}",status,curUsrId);
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
		else if(status ==  GunConstants.EP_GUN_STATUS_SETTING)
		 {
			return ErrorCodeConstants.EPE_IN_EP_OPER;
		 }
		
		else if(status ==  GunConstants.EP_GUN_STATUS_EP_OPER && startChargeStyle == EpConstants.CHARGE_TYPE_QRCODE)
		{
			return ErrorCodeConstants.EPE_IN_EP_OPER;//
		}
		else if (status ==  GunConstants.EP_GUN_STATUS_WAIT_CHARGE || status ==  GunConstants.EP_GUN_STATUS_TIMER_CHARGE)
		{
			return ErrorCodeConstants.EPE_IN_EP_OPER;
		   
		}
		else if(status ==  GunConstants.EP_GUN_STATUS_EP_UPGRADE)
		{
			return ErrorCodeConstants.EP_UPDATE;//
		}
		else if(status >30||
				 status == GunConstants.EP_GUN_STATUS_EP_INIT||
				 status== GunConstants.EP_GUN_STATUS_OFF_LINE||
			     status == GunConstants.EP_GUN_STATUS_STOP_USE)
		{
			return ErrorCodeConstants.EPE_GUN_FAULT;//
		}
		return 0;
		
	}
	
	public void onInsertGunSuccess()
	{
		if(chargeCache!=null&& (chargeCache.getStatus() == ChargeRecordConstants.CS_ACCEPT_CONSUMEER_CMD ||
				chargeCache.getStatus() == ChargeRecordConstants.CS_WAIT_INSERT_GUN))
		{
			chargeCache.setStatus(ChargeRecordConstants.CS_WAIT_CHARGE);
			
			String messagekey = String.format("%03d%s", Iec104Constant.C_START_ELECTRICIZE,chargeCache.getChargeSerialNo());
			EpCommClientService.removeRepeatMsg(messagekey);
			
			EpChargeService.updateChargeRecordStatus(chargeCache.getChargeSerialNo(),ChargeRecordConstants.CS_WAIT_CHARGE);
			
			Map<String, Object> chargeMap = new ConcurrentHashMap<String, Object>();
			
			chargeMap.put("epcode", epCode);
			chargeMap.put("epgunno", epGunNo);
			
			//logger.debug("onStartChargeSuccess EventConstant.EVENT_CHARGE,epCode:{},epGunNo:{}",epCode,epGunNo);
			handleEvent(EventConstant.EVENT_CHARGE,1,0,null,chargeMap);
			
			logger.info("charge reponse success accountId:{},serialNo:{},epCode:{},epGunNo:{}",
					new Object[]{chargeCache.getUserId(),chargeCache.getChargeSerialNo(),epCode,epGunNo});
			
		}
		
		
	}
	
	public SingleInfo getSingleInfo(int addr)
    {
		SingleInfo singInfo = null;
		if (this.currentType == EpConstants.EP_AC_TYPE) {
			RealACChargeInfo acReals = (RealACChargeInfo)realChargeInfo;
			singInfo = acReals.getFieldValue(addr);

		} else {
			RealDCChargeInfo dcReals = (RealDCChargeInfo) realChargeInfo;
			singInfo = dcReals.getFieldValue(addr);
		}
		return singInfo;
	}
	
	private  Map<String ,Object> getRealData()
	{
		Map<String ,Object> realInfo=null;
		
		Map<String, measurePoint> mapTrd = real3rdFactory.getmeasurePoints();
		if(mapTrd==null || mapTrd.size()<=0)
			return null;

		realInfo = new HashMap<>();
		
		Iterator iterTrd = mapTrd.entrySet().iterator();
		while (iterTrd.hasNext()) {
			Map.Entry entry = (Map.Entry) iterTrd.next();
			String key = (String)entry.getKey();
			measurePoint thirdRealData = (measurePoint) entry.getValue();
			if (thirdRealData == null) {
				continue;
			}
			
			int addr=0;
			
			if(thirdRealData.getType()==1){
				addr=thirdRealData.getAddr();
			}
			else if(thirdRealData.getType()==2){
				addr=thirdRealData.getAddr()+ YXCConstants.YX_2_START_POS;
			}
			else if(thirdRealData.getType()==3){
				addr=thirdRealData.getAddr()+ YXCConstants.YC_START_POS;
			}
			else if(thirdRealData.getType()==4){
				addr=thirdRealData.getAddr()+ YXCConstants.YC_VAR_START_POS;
			}
			
			SingleInfo singInfo = getSingleInfo(addr);
			if (singInfo == null) {
				continue;
			}
			realInfo.put(key, singInfo.getIntValue());
		}
	
		return realInfo;
		
	}

	public void handleECSignleOrgNo(int orgNo, int status) {
		Map<String ,Object> realData = getRealData();
		realData.put("3_1", status);
		handleSignleOrgNo(orgNo, realData, true);
	}

	public void handleSignleOrgNo(int orgNo,boolean workStatus) {
		Map<String ,Object> realData = getRealData();
		handleSignleOrgNo(orgNo, realData, workStatus);
	}

	private void handleSignleOrgNo(int orgNo,Map<String ,Object> realData,boolean workStatus)
	{
		logger.info("handleSignleOrgNo enter,orgNo:{},workStatus:{}",orgNo,workStatus);
		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if(epCache==null)
		{
			logger.info("handleSignleOrgNo did not find ElectricPileCache:{}",epCode);
			return ;
		}
		
		if(orgNo<=0)
		{
			logger.info("handleSignleOrgNo orgNo:{} is not valid",orgNo);
			return ;
		}
		
		
		Push rd =  (Push) CooperateFactory.getPush(orgNo);
		if(rd==null)
		{
			logger.info("handleSignleOrgNo did not find RealData:{}",orgNo);
			return ;
		}
		
		if(rd.getMode()!=1 && rd.getMode()!=2)
		{
			logger.info("handleSignleOrgNo did not find OrgSendConfig:{}",rd.getMode());
			
			return ;
		}
		
		if(workStatus==false) //状态变化送
		{
			logger.info("handleSignleOrgNo workStatus ==false");
			return;
		}
		
		String token="";
		String userIdentity="";
		boolean needSend=false;
		if(rd.getMode()==1)
		{
			if(this.chargeCache!=null)
			{
				userIdentity = chargeCache.getThirdUsrIdentity();
				needSend=true;
			}
		}
		else
		{
			needSend=true;
		}
		if(needSend)
		{
			if(realData==null)
			{
				logger.info("handleSignleOrgNo realData==null");
				
				return ;
			}
			
			rd.onEpStatusChange(token, orgNo,userIdentity,epCode, epGunNo,this.currentType,  realData,"");
			logger.debug("rd.onEpStatusChange");
		}
	}
	
	public void handleChargeRealData(int orgNo)
	{
		Push rd = (Push) CooperateFactory.getPush(orgNo);
		if (rd == null) return;

		if (logger.isDebugEnabled()) {
			logger.debug(LogUtil.addExtLog("enter,orgNo"), orgNo);
		}

		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if(epCache==null)
		{
			logger.info(LogUtil.addExtLog("fail did not find epCode"),epCode);
			return ;
		}

		if(this.chargeCache==null && orgNo != UserConstants.ORG_SHSTOP)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(LogUtil.getExtLog("fail chargeCache==null"));
			}
			return ;
		} else if(this.chargeCache!=null && chargeCache.getUserOrigin().getOrgNo() != orgNo && orgNo != UserConstants.ORG_SHSTOP && orgNo != UserConstants.ORG_TCEC_NANRUI) {
			return ;
		} else if(this.chargeCache!=null && chargeCache.getUserOrigin().getOrgNo() == orgNo && checkPushOrgNo(orgNo) == 0 && orgNo != UserConstants.ORG_SHSTOP && orgNo != UserConstants.ORG_TCEC_NANRUI) {
			return ;
		}
		
		if(rd.getMode()!=1 && rd.getMode()!=2)
		{
			logger.info(LogUtil.addExtLog("fail did not find OrgSendConfig"),rd.getMode());
			
			return ;
		}
		if(this.sendInfo3rd==null)
		{
			sendInfo3rd = new RealDataRT(epCache.getCompany_number(),0);
		}
		
		long now=  DateUtil.getCurrentSeconds();
		long diff = now - sendInfo3rd.getLastTime();
		if(diff<rd.getPeriod() && checkPushOrgNo(orgNo) == 0)
		{
			logger.info(LogUtil.addExtLog("fail now|sendInfo3rd.getLastTime()|diff:|osc.getPeriod()"),
					new Object[]{now,sendInfo3rd.getLastTime(),diff,rd.getPeriod()});
			return;
		}
		
		String token="";
		String userIdentity="";
		float servicePrice=0;
		Map<String ,Object> realData= getRealData();
		if(realData==null)
		{
			logger.info(LogUtil.getExtLog("fail realData==null"));
			return ;
		}

		if (orgNo != UserConstants.ORG_SHSTOP && orgNo != UserConstants.ORG_TCEC_NANRUI && orgNo != UserConstants.ORG_EC) {
			userIdentity = chargeCache.getThirdUsrIdentity();
			token = chargeCache.getToken();
			servicePrice=chargeCache.getRateInfo().getServiceRate().floatValue();
		}
		rd.onRealData(token, orgNo,userIdentity,epCode, epGunNo,this.currentType, servicePrice, realData,"");
		sendInfo3rd.setLastTime(now);

		logger.debug(LogUtil.getExtLog("rd.onRealData success"));
	}
	
	public void handleChargeOrder(int orgNo,ConsumeRecord consumeRecord,RateInfo rateInfo)
	{
		Push rd =  (Push) CooperateFactory.getPush(orgNo);
		if (rd==null) return;

		logger.info(LogUtil.addExtLog("enter,orgNo"),orgNo);

		ElectricPileCache epCache = EpService.getEpByCode(epCode);
		if(epCache==null)
		{
			logger.info(LogUtil.addExtLog("did not find ElectricPileCache"),epCode);
			return;
		}

		String token="";
		String userIdentity="";
		if(this.chargeCache !=null)
		{
			userIdentity = chargeCache.getThirdUsrIdentity();
			token = chargeCache.getToken();	
		}
		int inter_type=epCache.getCurrentType();
		
		double dec2=0.01;
		if (consumeRecord.getType() == 1) dec2 = 0.0001;
		double dec3=0.001;
		float money = (float)(consumeRecord.getTotalAmt()*dec2);
		float elect_money = (float)(consumeRecord.getTotalChargeAmt()*dec2);
		float service_money = (float)(consumeRecord.getServiceAmt()*dec2);
		float elect = (float)(consumeRecord.getTotalDl()*dec3);
		float start_elect = (float)(consumeRecord.getStartMeterNum()*dec3);
		float end_elect = (float)(consumeRecord.getEndMeterNum()*dec3);
		
		
		float cusp_elect = (float)(consumeRecord.getjDl()*dec3);
		float cusp_elect_price = rateInfo.getJ_Rate().floatValue();
		float cusp_service_price = rateInfo.getServiceRate().floatValue();		
		float cusp_elect_money = (float)(consumeRecord.getjAmt()*dec2);
		
		BigDecimal value = new BigDecimal(consumeRecord.getjDl()).multiply(rateInfo.getServiceRate());
		value.setScale(2,BigDecimal.ROUND_HALF_UP);
		float cusp_service_money = (float)(value.floatValue()*dec3);
		float cusp_money = cusp_elect_money+cusp_service_money;
		
		float peak_elect = (float)(consumeRecord.getfDl()*dec3);
		float peak_elect_price = rateInfo.getF_Rate().floatValue();
		float peak_service_price = rateInfo.getServiceRate().floatValue();		
		float peak_elect_money = (float)(consumeRecord.getfAmt()*dec2);
		
		 value = new BigDecimal(consumeRecord.getfDl()).multiply(rateInfo.getServiceRate());
		value.setScale(2,BigDecimal.ROUND_HALF_UP);
		float peak_service_money = (float)(value.floatValue()*dec3);
		float peak_money = peak_elect_money+peak_service_money;
		
		float flat_elect = (float)(consumeRecord.getpDl()*dec3);
		float flat_elect_price =  rateInfo.getP_Rate().floatValue();
		float flat_service_price = rateInfo.getServiceRate().floatValue();		
		float flat_elect_money = (float)(consumeRecord.getpAmt()*dec2);
		
		 value = new BigDecimal(consumeRecord.getpDl()).multiply(rateInfo.getServiceRate());
		value.setScale(2,BigDecimal.ROUND_HALF_UP);
		float flat_service_money = (float)(value.floatValue()*dec3);
		float flat_money = flat_elect_money+flat_service_money;
		
		float valley_elect = (float)(consumeRecord.getgDl()*dec3);
		float valley_elect_price = rateInfo.getG_Rate().floatValue();
		float valley_service_price = rateInfo.getServiceRate().floatValue();		
		float valley_elect_money = (float)(consumeRecord.getgAmt()*dec2);
		
		 value = new BigDecimal(consumeRecord.getgDl()).multiply(rateInfo.getServiceRate());
		value.setScale(2,BigDecimal.ROUND_HALF_UP);
		float valley_service_money = (float)(value.floatValue()*dec3);
		float valley_money = valley_elect_money+valley_service_money;
		long start_time = consumeRecord.getStartTime();
		long end_time = consumeRecord.getEndTime();
		int stop_msodel=1;
		
		int stop_reason=4;//app请求结束
		String stopCause = consumeRecord.getStopCause();
		int cause = 0;
		if (stopCause.indexOf("|") > 0) {
			cause = Integer.valueOf(stopCause.split("|")[0]);
		} else {
			cause = Integer.valueOf(stopCause);
		}
		if(cause==12)
		{
			stop_reason=2;//自动充满
		}
		else if((cause>=3 && cause<=11)
				||(cause>=13 && cause<=19))
		{
			stop_reason=1;//故障
		}
		else if(cause==2)
		{
			stop_reason=3;//刷卡正常结束
		}
		long time = DateUtil.getCurrentSeconds();
		
		int soc=0;
		if(inter_type==EpConstants.EP_DC_TYPE)
		{
			soc=((RealDCChargeInfo)realChargeInfo).getSoc();
			inter_type=2;
		}
		else
			inter_type=1;
		String extra = "";
		if (orgNo == UserConstants.ORG_SHSTOP) {
			token = "3";
			if (chargeCache != null) token = chargeCache.getChargeStyleSHStop();
			extra = consumeRecord.getSerialNo();
		} else if (orgNo == UserConstants.ORG_CCZC) {
			extra = EpChargeService.getExtraData_CCZC(epCode,epGunNo,userIdentity,
					token,4,1,0);
		}

		rd.onChargeOrder( token, orgNo, userIdentity, epCode, epGunNo,
						 inter_type, money, elect_money, service_money, elect, start_elect, end_elect
						, cusp_elect, cusp_elect_price, cusp_service_price, cusp_money, cusp_elect_money, cusp_service_money
						, peak_elect, peak_elect_price, peak_service_price, peak_money, peak_elect_money, peak_service_money
						, flat_elect, flat_elect_price, flat_service_price, flat_money, flat_elect_money, flat_service_money
						, valley_elect, valley_elect_price, valley_service_price, valley_money, valley_elect_money,
						valley_service_money,(int)start_time, (int)end_time, stop_msodel, stop_reason, soc, (int)time,extra);

		logger.debug(LogUtil.getExtLog("rd.onChargeOrder"));
	}
	
}
