package com.ec.epcore.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.EpConstants;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.EpConcentratorCache;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.RealChargeInfo;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.SingleInfo;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.conf.CoreConfig;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblElectricPile;

public class EpConcentratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EpConcentratorService.class.getName()));
	
	private static Map<String, EpConcentratorCache> mapStation = new ConcurrentHashMap<String,EpConcentratorCache>();
	
	public static String getCacheSize()
	{
		final StringBuilder sb = new StringBuilder();
        sb.append("EpConcentratorService:\n");
        
        sb.append("mapStation count:").append(mapStation.size()).append("\n\n");
         
        return sb.toString();
		
	}
	
	public static Map<String, EpConcentratorCache> getMapStation()
	{
		return mapStation;
	}

	public static int InitEp(EpCommClient commClient,EpConcentratorCache epStation,List<TblElectricPile> epList,int commVersion,int bootStatus)
	{
		if(epList==null || epList.size()<=0)
			    return 0;
		epStation.setEpCount(epList.size());
		try{
			
			for(int i=0;i< epList.size();i++)
			{
				TblElectricPile tblEp = epList.get(i);
				ElectricPileCache epCache = EpService.convertElectricPileCache(tblEp);
				
				if(epCache.initGuns(bootStatus))
				{
					epStation.setEpGunNum(tblEp.getStationIndex(), tblEp.getEpGunNum());
					epStation.addEpCode(tblEp.getStationIndex(), tblEp.getEpCode());
					epCache.setEpNetObject(commClient);
					epCache.updateNetObject();
					epCache.setConcentratorId(epStation.getPkId());	
					
					EpService.addEpCache(epCache);
					
					//每次连接上来更新下最大临时充电次数
					int MaxNum=EpService.getTempChargeMaxNumFromDB(epCache.getCompany_number());
					epCache.setTempChargeMaxNum(MaxNum);
					//初始化设备硬件版本信息
					EqVersionService.getEpVersionFromDB(epCache,1);
				}
				
			}
		}
		catch (Exception e) {
			logger.error(LogUtil.addExtLog("stationId|exception"), epStation.getPkId(), e.getStackTrace());
			return 1;
		}
		return 0;
	}

	public static EpConcentratorCache InitStation(EpCommClient commClient,int stationId,int commVersion,int bootStatus)
	{
		EpConcentratorCache epStation = getConCentrator(""+stationId);
		if(epStation !=null)
		{
		   return epStation;
		}
		epStation = new EpConcentratorCache();
		List<TblElectricPile> epList = DB.epClientDao.getEpsByStationId(stationId);
		if(epList==null || epList.size()<=0)
		{
			logger.error(LogUtil.addExtLog("have not TblElectricPile, stationId|commVersion|bootStatus"),
					new Object[]{stationId,commVersion,bootStatus});
			return null;
		}
		epStation.setPkId(stationId);
		int error = InitEp( commClient, epStation,epList, commVersion, bootStatus);
		if(error>0)
			return null;
		return epStation;
	}

	public static void AddConCentrator(EpConcentratorCache conCentratorCache)
	{
		if(conCentratorCache!=null)
		{
			mapStation.put(""+conCentratorCache.getPkId(), conCentratorCache);//这个不移除,以免在重连的时候查数据库
		}
	}

	public static EpConcentratorCache getConCentrator(String stationKey)
	{
		EpConcentratorCache conCentratorCache = null;
		if(stationKey!=null && stationKey.length()>0)
		{
			conCentratorCache = mapStation.get(stationKey);
		}
		return conCentratorCache;
	}

	public static EpConcentratorCache getConCentrator(Integer Id)
	{
		EpConcentratorCache conCentratorCache = null;
		if(Id!=0)
		{
			conCentratorCache = mapStation.get(Id);
		}
		return conCentratorCache;
	}

	public static boolean initStationConnect(int commVersion,short nStationId,EpCommClient commClient,int boot)
	{
		if(commClient ==null )
		{
			logger.error(LogUtil.addExtLog("commClient is null, stationId"), nStationId);
			return false;
		}
		String strStationId = ""+nStationId;
		EpConcentratorCache epStation = getConCentrator(strStationId);
		if(epStation!=null && epStation.getEpNetObject()!=null)
		{
			EpCommClientService.handleOldClient(commClient, strStationId);
			
		}
		else
		{
			epStation = InitStation(commClient,nStationId,commVersion,boot);
		}
		if(epStation==null)
		{
			logger.error(LogUtil.addExtLog("fail, stationId|commVersion|channel|boot"),
					new Object[]{nStationId,commVersion,commClient.getChannel(),boot});
			return false;
		}
		int gateId = CoreConfig.gameConfig.getId();
		
		commClient.initNetSuccess(strStationId, commVersion, EpConstants.COMM_MODE_CENTRATOR);
	
		epStation.setEpNetObject(commClient);
		epStation.updateNetObject(commClient);
		commClient.setRevINum(0);
		commClient.setGateId(gateId);
			
		EpService.updateEpsCommStatusToDb(nStationId, 1, gateId);
		
		//增加到缓存
		AddConCentrator(epStation);
		//初始化设备硬件版本信息
		EqVersionService.getConcentratorVersionFromDB(epStation,2);
		logger.info(LogUtil.addExtLog("stationId|gateId|commVersion|boot|epNum|channel"),
  				new Object[]{nStationId,gateId,commVersion,boot,epStation.getEpCount(),commClient.getChannel()});
		
		return true;	
	}
	
	public static void handleOneBitYxInfo(String key,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(key);
		
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("not find StationCache, realData dataType:1=oneBitYx,stationId"),key);
			return ;
		}
		Map<String, GunPointMap> pointMaps = new ConcurrentHashMap<String,GunPointMap>();
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			
			int pos = stationCache.getGunNo(address,1);
			logger.debug(LogUtil.addExtLog("realData dataType:1=oneBitYx,stationId|pos|address"),
					new Object[]{key,pos,address});
			if(pos==0)
				continue;
			
			int nStationIndex = pos/100;
			int epGunNo = pos % 100;
			String epCode = stationCache.getEpCode(nStationIndex);
			
			
			int yxPos = address % 128;
			if(!EpChargeService.isValidAddress(yxPos,1))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:1=oneBitYx,stationId|epCode|epGunNo|yxPos|invalid address"),
						new Object[]{stationCache.getPkId(),epCode,epGunNo,yxPos,address});

				continue;
			}

			
			GunPointMap gunPointMap= pointMaps.get(epCode+epGunNo);
			if(gunPointMap==null)
			{
				gunPointMap = new GunPointMap(epCode,epGunNo);
				pointMaps.put(epCode+epGunNo, gunPointMap);
			}
			
			info.setAddress(yxPos);
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		disptachPointToGun(1,pointMaps);
	}
	public static void handleOneBitYxInfo_v4(String epCode,int epGunNo,String key,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(key);
		
		if(stationCache==null)
		{
			logger.debug(LogUtil.addExtLog("not find EpStationCache, realData dataType:1=oneBitYx,epCode|epGunNo|stationId"),
					new Object[]{epCode,epGunNo,key});
			return ;
		}
		GunPointMap gunPointMap= new GunPointMap(epCode,epGunNo);
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			int yxPos = address % 128;
			if(!EpChargeService.isValidAddress(yxPos,1))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:1=oneBitYx,stationId|epCode|epGunNo|invalid address|yxPos"),
						new Object[]{stationCache.getPkId(),epCode,epGunNo,address,yxPos});

				continue;
			}
			
		}
		EpGunCache epGunCache =  EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache !=null)
		{
			epGunCache.onRealDataChange(gunPointMap.getPointMap(), 1);
		}
	}
	public static void handleTwoBitYxInfo(String key,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(key);
		
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("fail-- not find StationCache, realData dataType:2=twoBitYx,stationId"),key);
			return ;
			
		}
		
		Map<String, GunPointMap> pointMaps = new ConcurrentHashMap<String,GunPointMap>();
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			
			int pos = stationCache.getGunNo(address,3);
			
			if(pos==0)
			{
				logger.debug(LogUtil.addExtLog("realData dataType:2=twoBitYx,stationId|pos|address"),
						new Object[]{key,pos,address});
				continue;
			}
			
			int nStationIndex = pos/100;
			int epGunNo = pos % 100;
			String epCode = stationCache.getEpCode(nStationIndex);
			
			
			int yxPos = address % 128;
			if(!EpChargeService.isValidAddress(yxPos,3))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:2=twoBitYx,stationId|yxPos|invalid address"),
						new Object[]{stationCache.getPkId(),yxPos,address});

				continue;
			}
			
			GunPointMap gunPointMap= pointMaps.get(epCode+epGunNo);
			if(gunPointMap==null)
			{
				gunPointMap = new GunPointMap(epCode,epGunNo);
				pointMaps.put(epCode+epGunNo, gunPointMap);
			}
			
			info.setAddress(yxPos+YXCConstants.YX_2_START_POS);
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		disptachPointToGun(1,pointMaps);
	}
	public static void handleTwoBitYxInfo_v4(String epCode,int epGunNo,String stationKey,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(stationKey);
		
		if(stationCache==null)
		{
			logger.debug(LogUtil.addExtLog("not find EpStationCache, realData dataType:2=twoBitYx,epCode|epGunNo|stationId"),
					new Object[]{epCode,epGunNo,stationKey});
			return ;
			
		}
		
		GunPointMap gunPointMap = new GunPointMap(epCode,epGunNo);
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			int yxPos = address % 128;
			if(!EpChargeService.isValidAddress(yxPos,3))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:2=twoBitYx,stationId|epCode|epGunNo|yxPos|invalid address"),
						new Object[]{stationCache.getPkId(),epCode,epGunNo,yxPos,address});

				continue;
			}
			
			
			info.setAddress(yxPos+YXCConstants.YX_2_START_POS);
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		EpGunCache epGunCache =  EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache !=null)
		{
			epGunCache.onRealDataChange(gunPointMap.getPointMap(), 3);
		}
	}
	public static void handleYcInfo(String stationKey,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(stationKey);
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("fail-- not find StationCache, realData dataType:3=yc,stationId"),stationKey);
			return ;
			
		}
		Map<String, GunPointMap> pointMaps = new ConcurrentHashMap<String,GunPointMap>();
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			
			int pos = stationCache.getGunNo(address,11);
			
			if(pos==0)
			{
				logger.debug(LogUtil.addExtLog("realData dataType:3=yc,stationId|pos|address"),
						new Object[]{stationKey,pos,address});
				continue;
			}
			
			int nStationIndex = pos/100;
			int epGunNo = pos%100;
			
			String epCode = stationCache.getEpCode(nStationIndex);
			
			int ycPos  = address % 2500;
	
			if(!EpChargeService.isValidAddress(ycPos,11))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:3=yc,stationId|ycPos|epCode|epGunNo|invalid address"),
						new Object[]{stationCache.getPkId(),ycPos,epCode,epGunNo,address});
				continue;
			}
			
			
			GunPointMap gunPointMap= pointMaps.get(epCode+epGunNo);
			if(gunPointMap==null)
			{
				gunPointMap = new GunPointMap(epCode,epGunNo);
				pointMaps.put(epCode+epGunNo, gunPointMap);
			}	
			info.setAddress(ycPos+YXCConstants.YC_START_POS);
			
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		disptachPointToGun(11,pointMaps);
	}
	
	public static void handleYcInfo_v4(String epCode,int epGunNo, String stationKey,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(stationKey);
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("not find EpStationCache, realData dataType:3=yc,epCode|epGunNo|stationKey"),
					new Object[]{epCode,epGunNo,stationKey});
			return ;
			
		}
		GunPointMap gunPointMap= new GunPointMap(epCode,epGunNo);
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			
			int ycPos  = address % 2500;
	
			if(!EpChargeService.isValidAddress(ycPos,11))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:3=yc,epCode|epGunNo|invalid address|ycPos|stationId"),
						new Object[]{epCode,epGunNo,address,ycPos,stationCache.getPkId()});

				continue;
			}	
			info.setAddress(ycPos+YXCConstants.YC_START_POS);
			
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		EpGunCache epGunCache =  EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache !=null)
		{
			epGunCache.onRealDataChange(gunPointMap.getPointMap(), 11);
		}
	}
	public static void handleVarYcInfo(String stationKey,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(stationKey);
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("fail-- not find StationCache, realData dataType:4=varYc,stationId"),stationKey);
			return ;
		}
			
		Map<String, GunPointMap> pointMaps = new ConcurrentHashMap<String,GunPointMap>();
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			
			int pos = stationCache.getGunNo(address,132);
			
			if(pos==0)
			{
				logger.debug(LogUtil.addExtLog("realData dataType:4=varYc,stationId|pos|address"),
						new Object[]{stationKey,pos,address});
				continue;
			}
			int nStationIndex = pos/100;
			int epGunNo = pos%100;
			
			String epCode = stationCache.getEpCode(nStationIndex);
			
			
			GunPointMap gunPointMap= pointMaps.get(epCode+epGunNo);
			if(gunPointMap==null)
			{
				gunPointMap = new GunPointMap(epCode,epGunNo);
				pointMaps.put(epCode+epGunNo, gunPointMap);
			}
			int yc2Pos  = address % 128;
			
			if(!EpChargeService.isValidAddress(yc2Pos,132))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:4=varYc,stationId|yc2Pos|epCode|epGunNo|invalid address"),
						new Object[]{stationCache.getPkId(),yc2Pos,epCode,epGunNo,address});
				
				continue;
			}
			
			info.setAddress(yc2Pos+YXCConstants.YC_VAR_START_POS);
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		
		disptachPointToGun(132,pointMaps);
	}
	
	public static void handleVarYcInfo_v4(String epCode,int epGunNo, String stationKey,Vector<SingleInfo> singleInfos)
	{
		EpConcentratorCache stationCache = mapStation.get(stationKey);
		if(stationCache==null)
		{
			logger.error(LogUtil.addExtLog("not find EpStationCache, realData dataType:4=varYc,epCode|epGunNo|stationKey"),
					new Object[]{epCode,epGunNo,stationKey});
			return ;
		}
			
		GunPointMap gunPointMap = new GunPointMap(epCode,epGunNo);
		
		for(int i=0;i< singleInfos.size(); i++ )
		{
			SingleInfo info = singleInfos.get(i);
			int address = info.getAddress();//紧凑排列
			int yc2Pos  = address % 128;
			
			if(!EpChargeService.isValidAddress(yc2Pos,132))
			{
				logger.debug(LogUtil.addExtLog("realData dataType:4=varYc,epCode|epGunNo|invalid address|yc2Pos|stationId"),
						new Object[]{epCode,epGunNo,address,yc2Pos,stationCache.getPkId()});
				
				continue;
			}
						
			info.setAddress(yc2Pos+YXCConstants.YC_VAR_START_POS);
			RealChargeInfo.AddPoint(gunPointMap.getPointMap(), info);
		}
		EpGunCache epGunCache =  EpGunService.getEpGunCache(epCode, epGunNo);
		if(epGunCache !=null)
		{
			epGunCache.onRealDataChange(gunPointMap.getPointMap(), 11);
		}
	}
	
	public static void disptachPointToGun(int type,Map<String, GunPointMap> pointMaps)
	{
		Iterator iter = pointMaps.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			
			GunPointMap gunPointMap=(GunPointMap) entry.getValue();
			
			if(gunPointMap==null)
			{
				continue;	
			}
			EpGunCache epGunCache =  EpGunService.getEpGunCache(gunPointMap.getEpCode(), gunPointMap.getEpGunNo());
			if(epGunCache !=null)
			{
				epGunCache.onRealDataChange(gunPointMap.getPointMap(), type);
			}
		}
	}
	
	
	public static void handleCommClientTimeOut(String clientIdentity,int gateId)
	{
		if(clientIdentity==null || clientIdentity.length()<1)
		{
			logger.info("[epChannel],handleCommClientTimeOut fail, clientIdentity is empty");
			return;
		}

		int pkConcentratorId = Integer.parseInt(clientIdentity);
		
		EpConcentratorCache conCentrator= mapStation.get(pkConcentratorId);
		if(conCentrator!=null)
		{
			conCentrator.onEpCommClientDelete();
		}
		//RemoveConCentrator(channel);
		
		EpService.updateEpsCommStatusToDb(pkConcentratorId, 0, gateId);
		logger.info(LogUtil.addExtLog("modify CommStatus=0 to db,clientIdentity"),clientIdentity);
	}
	
	public static void concentratorConfig(int concentratorId)
	{
		EpConcentratorCache epStation = getConCentrator(""+concentratorId);
		if(epStation == null)
		{
			logger.error(LogUtil.addExtLog("epStation is null,stationId"),concentratorId);
			return;
		}
		
		//解绑老的ep
		epStation.onEpDelete();
		List<TblElectricPile> epList = DB.epClientDao.getEpsByStatus(concentratorId);

		//做协议逻辑
		EpCommClient commClient = EpCommClientService.getCommClient(""+concentratorId);
		String epCodes="";
		if(epList==null || epList.size()<=0)
		{
			epStation.setEpCount(0);
		}
		else
		{
			epStation.setPkId(concentratorId);
			int error = InitEp(commClient,epStation,epList,commClient.getVersion(), 0);
			if(error==0)
			{
				epCodes= epStation.getEpCodes();
			}
		}
		if(commClient ==null || !commClient.isComm())
		{
			logger.error(LogUtil.addExtLog("commClient is close,stationId"),concentratorId);	
			return;
		}
		int epCount = epStation.getEpCount();
		byte[]data = EpEncoder.do_concentroter_setep((short)concentratorId,(short)epCount, epCodes); 
		byte[] cmdTimes = WmIce104Util.timeToByte();
		EpMessageSender.sendMessage(commClient,0,0, Iec104Constant.C_CONCENTROTER_SET_EP,data,cmdTimes,commClient.getVersion());
	
	}
	
	public static void updateAllCommStatus(int gateId)
	{
		DB.epClientDao.updateAllCommStatus(gateId);
	}

}
