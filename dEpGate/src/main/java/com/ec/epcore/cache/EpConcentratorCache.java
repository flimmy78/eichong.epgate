package com.ec.epcore.cache;

import com.ec.constants.YXCConstants;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.service.EpService;
import com.ec.epcore.service.EqVersionService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class EpConcentratorCache {
	private Integer pkId;
	private Integer epCount;
	
	private Integer oneBitYxVarCount;//type:1,16
	private Integer oneBitYxStartAddr;
	private Integer twoBitYxVarCount;//type:3,16
	private Integer twoBitYxStartAddr;
	private Integer YcVarCount;//type:11,10
	private Integer YcStartAddr;//
	private Integer Yc2VarCount;//type:132,2
	private Integer Yc2StartAddr;
	
	private static int[] gunNumList;
	
	private static int totalGunNum;
	
	private Integer typeSpanId; // 产品类型
	
	private EqVersionCache versionCache; //电桩实际版本和升级版本信息
	
	private EpCommClient epNetObject;//电桩网络连接
	

   public Integer getTypeSpanId() {
	   return typeSpanId;
   }


   public void setTypeSpanId(Integer typeSpanId) {
	  this.typeSpanId = typeSpanId;
   }

	
	
	public EqVersionCache getVersionCache() {
	return versionCache;
   }


   public void setVersionCache(EqVersionCache versionCache) {
	this.versionCache = versionCache;
   }

	
	
	//站编号+枪编号，桩编号+枪编号
	private  Map<Integer, String> mapEpCode = new ConcurrentHashMap<Integer,String>();
		
	
	@SuppressWarnings("rawtypes")
	public void onEpCommClientDelete()
	{
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		ArrayList<String> epCodes = new ArrayList<String>();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			
			ElectricPileCache loopEpCache = EpService.getEpByCode(epCode);
			if(loopEpCache!=null)
			{
				loopEpCache.onEpCommClientDelete();
				
				loopEpCache.setEpNetObject(null);
				
				epCodes.add(loopEpCache.getCode());
				
				//EpService.updateEpCommStatusToDb(loopEpCache.getPkEpId(), 0, 0);
			}
			
		}
		//EpService.sendEpStatusToUsrGate(epCodes,0);
	}
	
	public void onEpSendVersion()
	{
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			
			ElectricPileCache loopEpCache = EpService.getEpByCode(epCode);
			if(loopEpCache!=null)
			{
				EqVersionService.sendVersion(epNetObject, epCode, 0);
			}
		}
	}
	
	public void onEpSendTempChargeMaxNum()
	{
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			
			ElectricPileCache loopEpCache = EpService.getEpByCode(epCode);
			if(loopEpCache!=null)
			{
				EpService.queryTempChargeNum(epCode);
			}
		}
	}
	
	public void onEpDelete()
	{
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		ArrayList<String> epCodes = new ArrayList<String>();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			
			ElectricPileCache loopEpCache = EpService.getEpByCode(epCode);
			if(loopEpCache!=null)
			{
				loopEpCache.setConcentratorId(0);
				loopEpCache.onEpCommClientDelete();
				
				loopEpCache.setEpNetObject(null);
				epCodes.add(loopEpCache.getCode());
			}
			
		}
		EpService.updateEpsCommStatusToDb(this.pkId, 0, 0);
		
	//	EpService.sendEpStatusToUsrGate(epCodes,0);
		mapEpCode.clear();
	}
	
	public void updateNetObject(EpCommClient epNetObject)
	{
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			
			ElectricPileCache loopEpCache = EpService.getEpByCode(epCode);
			
			if(loopEpCache!=null)
			{
				loopEpCache.setEpNetObject(epNetObject);
				loopEpCache.updateNetObject();
			}
			
		}
		
	}
	
	public EpConcentratorCache()
	{
		oneBitYxVarCount = YXCConstants.YX_2_START_POS;//type:1,16
		oneBitYxStartAddr=1;
		twoBitYxVarCount = YXCConstants.YX_2_START_POS;//type:3,16
		twoBitYxStartAddr =1;
		
		YcVarCount= 2500;//type:11,10
		YcStartAddr=1;//
		Yc2VarCount=128;//type:132,2
		Yc2StartAddr=1;
		
		
		totalGunNum=0;
		typeSpanId=0;
		versionCache = new EqVersionCache();
	}

	public int  getGunNo(int address, int type)
	{
		if(type!=0 && type!=1 && type!=3 && type!=11 && type !=132)
		{
			return 0;
		}
		
		if(type==1 &&  address> (totalGunNum*128))
			return 0;
		if(type==3 &&  address> (totalGunNum*128))
			return 0;
		if(type==11 &&  address> (totalGunNum*2500))
			return 0;
		if(type==132 &&  address> (totalGunNum*128))
			return 0;
		int nStationIndex=0;
		int nGunNo=0;
		int nStartAddr=0;
		int nTypeCount=0;
		
		switch(type)
		{
		case 1:
			nStartAddr= oneBitYxStartAddr;
			nTypeCount = oneBitYxVarCount;
			break;
		case 3:
			nStartAddr= twoBitYxStartAddr;
			nTypeCount = twoBitYxVarCount;
			break;
		case 11:
			nStartAddr= YcStartAddr;
			nTypeCount = YcVarCount;
			break;
		default:
			nStartAddr= Yc2StartAddr;
			nTypeCount = Yc2VarCount;
			break;
		
		}
		
		
		int nTime= ((address-nStartAddr) / nTypeCount)+1;
		int loopTime=0;
		for(int i=0;i<gunNumList.length;i++)
		{
			int gunNum = gunNumList[i];
			for(int j=0;j<gunNum ;j++)
			{
				loopTime += 1;
				if(nTime == loopTime)
				{
					nStationIndex= i+1;//站编号
					nGunNo = j+1;//枪编号
				}
			}
		}
		return nStationIndex *100 + nGunNo;
	}
	
	
	
	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getEpCount() {
		return epCount;
	}

	public void setEpCount(Integer epCount) {
		this.epCount = epCount;
		gunNumList = new int[this.epCount];
	}
	public String getEpCode(int stationIndex)
	{
		return mapEpCode.get(stationIndex);
	}
	public void addEpCode(int stationIndex,String epCode)
	{
		mapEpCode.put(stationIndex, epCode);
	}
	public void setEpGunNum(int epIndex,int epGunNum)
	{
		if(epIndex<1 || epIndex>this.epCount)
			return;
		
		gunNumList[epIndex-1] = epGunNum;
		
		calcTotalNum();
	}
	
	private void calcTotalNum()
	{
		int num=0;
		for(int i=0;i<epCount;i++)
		{
			num += gunNumList[i];
		}
		this.totalGunNum = num;
	}

	
	public Integer getOneBitYxVarCount() {
		return oneBitYxVarCount;
	}


	public void setOneBitYxVarCount(Integer oneBitYxVarCount) {
		this.oneBitYxVarCount = oneBitYxVarCount;
	}


	public Integer getOneBitYxStartAddr() {
		return oneBitYxStartAddr;
	}


	public void setOneBitYxStartAddr(Integer oneBitYxStartAddr) {
		this.oneBitYxStartAddr = oneBitYxStartAddr;
	}


	public Integer getTwoBitYxVarCount() {
		return twoBitYxVarCount;
	}


	public void setTwoBitYxVarCount(Integer twoBitYxVarCount) {
		this.twoBitYxVarCount = twoBitYxVarCount;
	}


	public Integer getTwoBitYxStartAddr() {
		return twoBitYxStartAddr;
	}


	public void setTwoBitYxStartAddr(Integer twoBitYxStartAddr) {
		this.twoBitYxStartAddr = twoBitYxStartAddr;
	}


	public Integer getYcVarCount() {
		return YcVarCount;
	}

	public void setYcVarCount(Integer ycVarCount) {
		YcVarCount = ycVarCount;
	}

	public Integer getYcStartAddr() {
		return YcStartAddr;
	}

	public void setYcStartAddr(Integer ycStartAddr) {
		YcStartAddr = ycStartAddr;
	}

	public Integer getYc2VarCount() {
		return Yc2VarCount;
	}

	public void setYc2VarCount(Integer yc2VarCount) {
		Yc2VarCount = yc2VarCount;
	}

	public Integer getYc2StartAddr() {
		return Yc2StartAddr;
	}

	public void setYc2StartAddr(Integer yc2StartAddr) {
		Yc2StartAddr = yc2StartAddr;
	}
	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();
        sb.append("EpConcentratorCache");
    
        sb.append("{集中器pkId = ").append(pkId).append("}\n");
        sb.append("{电桩总数 = ").append(epCount).append("}\n");
        sb.append("{枪口总数 = ").append(totalGunNum).append("}\n");
        
        
        Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			String epCode=(String) entry.getValue();
			sb.append(",{电桩pkId = ").append(entry.getKey()).append("}\n");
			 sb.append(",{电桩编号 = ").append(epCode).append("}\n");
		}
      	return sb.toString();
       
    }


	public EpCommClient getEpNetObject() {
		return epNetObject;
	}


	public void setEpNetObject(EpCommClient epNetObject) {
		this.epNetObject = epNetObject;
	}
	
	public String getEpCodes()
	{
		String epCodes="";
		Iterator<Entry<Integer, String>> iter = mapEpCode.entrySet().iterator();
		 while (iter.hasNext()) {
			 Map.Entry entry = (Map.Entry) iter.next();
			String epCode=(String) entry.getValue();
			epCodes=epCodes+epCode+",";
		 } 
		return epCodes;
	}
}
