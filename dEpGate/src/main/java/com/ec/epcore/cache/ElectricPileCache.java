package com.ec.epcore.cache;

import java.util.List;

import com.cooperate.CooperateFactory;
import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.UserConstants;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.service.EpChargeService;
import com.ec.epcore.service.EpGunService;
import com.ec.epcore.service.EpService;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.WmIce104Util;
import com.ormcore.model.CompanyRela;

public class ElectricPileCache {
		
	private Integer pkEpId; // 
	private String code; //
	private String name;
    private int concentratorId;//站索引
	

	private int stationIndex;//站索引
	private int gunNum;
	private int currentType; // 
	private int epType;
	private int rateid; // 
	private int gateid; //
	private Integer typeSpanId; // 产品类型Id
	
	private long lastUseTime;
	
    private int isSupportCarPlace;
	private int isSupportBmsComm;
	private int isSupportGunLock;
	private int isSupportGunSit;
	
	private String address;
	private int company_number; //
	
	private int tempChargeMaxNum;

	private int state;//电桩状态
	private int deleteFlag;//删除标识
	
	private String  ownCityCode;//鎵�灞炲湴鍖哄尯鍘縞ode
	private String  ownProvinceCode;//省code
	
	
	private EpCommClient epNetObject;//电桩网络连接
	
	private EqVersionCache versionCache; //电桩实际版本和升级版本信息
	
	private List<CompanyRela> companyRelaList;
	private boolean meterFlag;

	public ElectricPileCache()
	{
		gateid=0;
		pkEpId=0; // 
		code=""; //
		name="";
	    concentratorId=0;//站索引

		stationIndex=0;//站索引
		gunNum=0;
		currentType=0; // 
		epType=0;
		rateid=0; // 
		gateid=0; //
		lastUseTime=0;
	    isSupportCarPlace=0;
		isSupportBmsComm=0;
		isSupportGunLock=0;
		isSupportGunSit=0;
		
		address="";
		typeSpanId=0;
		versionCache = new EqVersionCache();
		
		state = 0;
		deleteFlag = 0;
		
		ownCityCode="";
		ownProvinceCode="";
		
		tempChargeMaxNum=0;
		companyRelaList = null;
		meterFlag = false;
	}
	
	
	
   	public int getTempChargeMaxNum() {
		return tempChargeMaxNum;
	}



	public void setTempChargeMaxNum(int tempChargeMaxNum) {
		this.tempChargeMaxNum = tempChargeMaxNum;
	}



	public String getOwnCityCode() {
		return ownCityCode;
	}

	public void setOwnCityCode(String ownCityCode) {
		this.ownCityCode = ownCityCode;
	}

	public String getOwnProvinceCode() {
		return ownProvinceCode;
	}

	public void setOwnProvinceCode(String ownProvinceCode) {
		this.ownProvinceCode = ownProvinceCode;
	}

	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public EqVersionCache getVersionCache() {
		return versionCache;
	}

	public void setVersionCache(EqVersionCache versionCache) {
		this.versionCache = versionCache;
	}
	
	public Integer getTypeSpanId() {
		return typeSpanId;
	}


	public void setTypeSpanId(Integer typeSpanId) {
		this.typeSpanId = typeSpanId;
	}


	public EpCommClient getEpNetObject() {
		return epNetObject;
	}
	public void setEpNetObject(EpCommClient epNetObject) {
		this.epNetObject = epNetObject;
	}
	public Integer getEpType() {
		return epType;
	}
	public void setEpType(Integer epType) {
		this.epType = epType;
	}
	
	public void onEpCommClientDelete()
	{
		for(int i=1;i<= gunNum;i++)
		{
			EpGunCache loopEpGunCache = EpGunService.getEpGunCache(code, i);
			loopEpGunCache.setConcentratorId(this.getConcentratorId());
			loopEpGunCache.onNetStatus(0);
			loopEpGunCache.setEpNetObject(null);
		}
	}
	
	public boolean updateNetObject()
	{
		
		for(int i=1;i<= gunNum;i++)
		{
			EpGunCache gunCache = EpGunService.getEpGunCache(code, i);
			
			if(gunCache!=null)
			{
				gunCache.setConcentratorId(this.getConcentratorId());
				EpGunService.putEpGunCache(code, i, gunCache);
				
				gunCache.setEpNetObject(epNetObject);	
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean initGuns(int bootLoader) {

		for (int i = 1; i <= gunNum; i++) {
			EpGunCache gunCache = EpGunService.getEpGunCache(code, i);
			if (gunCache == null)
				gunCache = new EpGunCache();
			gunCache.setEpGunNo(i);
			gunCache.setEpCode(code);
			gunCache.setPkEpId(this.pkEpId);

			if (gunCache.init(this, i, bootLoader)) {
				gunCache.setConcentratorId(this.getConcentratorId());
				EpGunService.putEpGunCache(code, i, gunCache);

				gunCache.setEpNetObject(epNetObject);
			} else {
				return false;
			}
		}

		return true;
	}
	
    public Integer getIsSupportCarPlace() {
		return isSupportCarPlace;
	}
	public void setIsSupportCarPlace(Integer isSupportCarPlace) {
		this.isSupportCarPlace = isSupportCarPlace;
	}
	public Integer getIsSupportBmsComm() {
		return isSupportBmsComm;
	}
	public void setIsSupportBmsComm(Integer isSupportBmsComm) {
		this.isSupportBmsComm = isSupportBmsComm;
	}
	public Integer getIsSupportGunLock() {
		return isSupportGunLock;
	}
	public void setIsSupportGunLock(Integer isSupportGunLock) {
		this.isSupportGunLock = isSupportGunLock;
	}
	public Integer getIsSupportGunSit() {
		return isSupportGunSit;
	}
	public void setIsSupportGunSit(Integer isSupportGunSit) {
		this.isSupportGunSit = isSupportGunSit;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getGunNum() {
		return gunNum;
	}
	public void setGunNum(int gunNum) {
		this.gunNum = gunNum;
	}
	public void setPkEpId(int pkEpId) {
		this.pkEpId = pkEpId;
	}
	public long getLastUseTime() {
		return lastUseTime;
	}
	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}
	
	
	public int getCurrentType() {
		return currentType;
	}
	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}
	public int getRateid() {
		return rateid;
	}
	public void setRateid(int rateid) {
		this.rateid = rateid;
	}
	public int getPkEpId() {
		return pkEpId;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public int getConcentratorId() {
		return concentratorId;
	}
	public void setConcentratorId(int concentratorId) {
		this.concentratorId = concentratorId;
	}

	public int getStationIndex() {
		return stationIndex;
	}

	public void setStationIndex(int stationIndex) {
		this.stationIndex = stationIndex;
	}
	
	public int getGateid() {
		return gateid;
	}
	public void setGateid(int gateid) {
		this.gateid = gateid;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<CompanyRela> getCompanyRelaList() {
		return companyRelaList;
	}

	public void setCompanyRelaList(List<CompanyRela> companyRelaList) {
		this.companyRelaList = companyRelaList;
	}

	public boolean isMeterFlag() {
		return meterFlag;
	}

	public void setMeterFlag(boolean meterFlag) {
		this.meterFlag = meterFlag;
	}

	public void onNetStatus(int epStatus)
	{
    	boolean canOperate = checkOrgOperate(UserConstants.ORG_EC);
        boolean canOperate1 = checkOrgOperate(UserConstants.ORG_TCEC_ECHONG);
		for(int i=1;i<= gunNum;i++)
		{
			EpGunCache loopEpGunCache = EpGunService.getEpGunCache(code, i);
			loopEpGunCache.onNetStatus(epStatus);
			if (canOperate && loopEpGunCache != null) loopEpGunCache.handleECSignleOrgNo(UserConstants.ORG_EC, 0);
            if (canOperate1 && loopEpGunCache != null) loopEpGunCache.handleECSignleOrgNo(UserConstants.ORG_TCEC_ECHONG, 0);
			EpChargeService.insertConsumeRecord(loopEpGunCache);
		}
	}

	public void sendStatus(int status)
	{
        boolean canOperate = checkOrgOperate(UserConstants.ORG_EC);
        boolean canOperate1 = checkOrgOperate(UserConstants.ORG_TCEC_ECHONG);
        if (!canOperate && !canOperate1) return;
        for(int i=1;i<= gunNum;i++)
        {
            EpGunCache loopEpGunCache = EpGunService.getEpGunCache(code, i);
            if (canOperate && loopEpGunCache != null) loopEpGunCache.handleECSignleOrgNo(UserConstants.ORG_EC, status);
            //if (canOperate1) loopEpGunCache.handleECSignleOrgNo(UserConstants.ORG_TCEC_ECHONG, status);
        }
	}
	
	public int getCompany_number() {
		return company_number;
	}

	public void setCompany_number(int company_number) {
		this.company_number = company_number;
	}

	public int callEpAction(int type,int time,float lng,float lag)
	{
		EpCommClient commClient =(EpCommClient) getEpNetObject();
		if(commClient==null)
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
		}
		if(!commClient.isComm())
		{
			return ErrorCodeConstants.EP_UNCONNECTED;
			
		}
		byte[] data = EpEncoder.do_near_call_ep(this.code, type,time);
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		
		EpMessageSender.sendMessage(commClient,0,0, Iec104Constant.C_NEAR_CALL_EP,data,cmdTimes,commClient.getVersion());
		
		return 0;
	}

	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();
        sb.append("ElectricPileCache");
    
        sb.append("{电桩pkEpId = ").append(pkEpId).append("}\n");
        sb.append("{电桩名称 = ").append(name).append("}\n");
        sb.append("{电桩编号 = ").append(code).append("}\n");
        sb.append("{枪口数量 = ").append(gunNum).append("}\n");
        sb.append("{集中器pkId = ").append(concentratorId).append("}\n");
        sb.append("{在站中序号 = ").append(stationIndex).append("}\n");
        sb.append("{epType(电桩类型) = ").append(epType).append(getEpTypeDesc()).append("}\n");
        
        switch(currentType)
        {
        case 5:
        	sb.append("{电桩类型 = ").append("5直流").append("}\n");
        	break;
        case 14:
        	sb.append("{电桩类型 = ").append("14交流").append("}\n");
        	break;
        default:
        	sb.append("{电桩类型 = ").append(currentType).append("未知").append("}\n");
        	break;
		
        }
        sb.append("{gateid = ").append(gateid).append("}\n");
        sb.append("{产品ID = ").append(typeSpanId).append("}\n");
        sb.append("{地址 = ").append(address).append("}\n");
        sb.append("{省代码 = ").append(ownProvinceCode).append("}\n");
        sb.append("{市代码 = ").append(ownCityCode).append("}\n");
        
        sb.append("{公司标识 = ").append(company_number).append("}\n\r\n");
        
        sb.append("{费率id = ").append(rateid).append("}\n");
        sb.append("{最大临时充电次数 = ").append(tempChargeMaxNum).append("}\n");
        
      	return sb.toString();
       
    }
	public int canNearCall(boolean reload)
	{
		if(reload)
			EpService.getEpRealStatus(this);
		
		
 		if(getState()==EpConstants.EP_STATUS_OFFLINE)
 		{
 			return ErrorCodeConstants.EP_PRIVATE;
 		}
 		else if(getState()<EpConstants.EP_STATUS_OFFLINE)
 		{
 			return ErrorCodeConstants.EP_NOT_ONLINE;
 		}
 		if(getDeleteFlag() !=0)
 		{
 			return ErrorCodeConstants.EP_NOT_ONLINE;
 		}
		
		return 0;
	}
	public int canCardCharge(boolean reload)
	{
		if(reload)
			EpService.getEpRealStatus(this);
		
		return 0;
	}
	public int canBespoke(boolean reload)
	{
		if(reload)
			EpService.getEpRealStatus(this);
		
		if(getState()==EpConstants.EP_STATUS_OFFLINE){
		   return ErrorCodeConstants.EP_PRIVATE;
	   }
	   else if(getState()<EpConstants.EP_STATUS_OFFLINE){
		   return ErrorCodeConstants.EP_NOT_ONLINE;
	   }
	   if(getDeleteFlag() !=0){
		   return ErrorCodeConstants.EP_NOT_ONLINE;
	   }
		
	   return 0;
		
		
	}
	/**
	 * 
	 * @param reload,true.重新加载电桩实时数据
	 * @param pkUserCard,如果没有绑卡，那么不在线的桩不能充电
	 * @return
	 */
	public int canCharge(boolean reload,int pkUserCard,int cardType,int cardOrgNo,int epGunNo)
	{
		if(epGunNo<1|| epGunNo> getGunNum())
		{
			return ErrorCodeConstants.INVALID_EP_GUN_NO;//
		}
		if(reload)
			EpService.getEpRealStatus(this);
		
		
		if(getState()==EpConstants.EP_STATUS_OFFLINE )
		{
			if(pkUserCard==0)
			{
				//TODO:
				return ErrorCodeConstants.EP_PRIVATE;
			}
			else
			{
				if(cardType == EpConstants.CARD_NORMAL || cardType == EpConstants.CARD_THIRD_NORMAL)
				{
					return ErrorCodeConstants.EP_PRIVATE;
				}
				else
				{
					if(company_number != cardOrgNo)
					{
						return ErrorCodeConstants.EP_PRIVATE;
					}
				}
			}
		}
		else if(getState()!=1)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}
		if(getDeleteFlag() !=0)
		{
			return ErrorCodeConstants.EP_NOT_ONLINE;
		}
		
		return 0;
	}
	public int canAuth(boolean reload)
	{
		if(reload)
			EpService.getEpRealStatus(this);

		/*if(getState()<EpConstants.EP_STATUS_OFFLINE)
 		{
 			return EpConstants.E_CARD_NOT_FIND_EP;
 		}*/
 		if(getDeleteFlag() !=0)
 		{
 			return EpConstants.E_CARD_NOT_FIND_EP;
 		}
 		return 0;
	}

	public boolean canOrgOperate(int orgNo)
	{
		if (companyRelaList == null) return false;
		for (CompanyRela companyRela : companyRelaList)
			if (companyRela.getCpyCompanyNumber() == orgNo) return true;
		
		return false;
	}

	public boolean checkOrgOperate(int orgNo)
	{
		if (!CooperateFactory.isCooperate(orgNo)) return true;

		//合作方电桩过滤
		return canOrgOperate(orgNo);
	}
	
	  public String getEpTypeDesc() {
			
			String desc="";
			switch(this.epType)
	        {
	        case 0:
	        	desc="落地式";
	        	 break;
	        case 1:
	        	desc="壁挂式";
	       	 	break;
	        case 2:
	        	desc="拉杆式";
	       	 	break;
	        case 3:
	        	desc="便携式";
	       	 	break;
	       	 
	        default:
	        	desc="未知";
	       	 	break;
	        }
			return desc;
		}
 }


