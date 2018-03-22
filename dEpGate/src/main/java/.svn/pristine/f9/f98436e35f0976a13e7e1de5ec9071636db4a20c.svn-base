package com.ec.epcore.service;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.constants.ErrorCodeConstants;
import com.ec.epcore.cache.BomListInfo;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.EpConcentratorCache;
import com.ec.epcore.cache.EqVersionCache;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.net.proto.EqVersionInfo;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.logs.LogConstants;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.WmIce104Util;
import com.ec.utils.DateUtil;
import com.ec.utils.FileUtils;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.TblBomList;
import com.ormcore.model.TblConcentrator;
import com.ormcore.model.TblElectricPile;
import com.ormcore.model.TblEquipmentVersion;
import com.ormcore.model.TblTypeSpan;

public class EqVersionService {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(EqVersionService.class.getName()));
	
	//升级版本缓存
	public static Map<Integer, Map<String,BomListInfo>> updateHexFileMap = new ConcurrentHashMap<Integer,Map<String,BomListInfo>>();
		
	private static long lastFetchEpHexDown=0;

	public static  void AddBomList(Integer key,Map<String,BomListInfo> bomList)
	{
		if(key !=null && bomList!=null)
		{
			updateHexFileMap.put(key, bomList);
			logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "updateHexFileMapsize|bomListsize"), new Object[]{updateHexFileMap.size(),bomList.size()});
		}
		else
		{
			logger.error("[upgrade],AddBomList fail,because of,key:{},bom:{}",key,bomList);
		}
	}
	public static  Map<String,BomListInfo>  getBomList(Integer key)
	{
		if(key !=null )
		{
			return updateHexFileMap.get(key);
		}
		else
		{
			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,because of,key"), "null");
			return null;
		}
	}
	public static void removeBomList(Integer key)
	{
		updateHexFileMap.remove(key);
	}
	public static void removeAllBomList()
	{
		updateHexFileMap.clear();
	}

		
	public static void init() 
	{
		if (lastFetchEpHexDown == 0) {
			
			getTypeSpanAndBomFromDB();

			lastFetchEpHexDown = DateUtil.getCurrentSeconds();
		}
	}

	public static void getBomListByTypeSpanId(int typeSpanId) {
		// 取得全部升级版本
		List<TblBomList> tblBomList = DB.bomListDao.getAllByTypeSpanId(typeSpanId);

		if (tblBomList == null || tblBomList.size() < 1) {
			logger.info("[upgrade],getBomListByTypeSpanId TypeSpanId:{} had not TblBomList", typeSpanId);

			return;
		}

		Map<String, BomListInfo> bomMap = new ConcurrentHashMap<String, BomListInfo>();
		for (int j = 0; j < tblBomList.size(); j++) {
			TblBomList tblBom = tblBomList.get(j);
			BomListInfo bomInfo = convertBomInfo(tblBom);
			if (bomInfo.splitHardwareVersion() > 0 || bomInfo.splitSoftVersion() > 0) {
				logger.error("[upgrade],getBomListByTypeSpanId bom error,hardwareVersion:{},softVersion:{}",
						bomInfo.getHardwareVersion(), bomInfo.getSoftVersion());
				continue;
			}

			bomMap.put(bomInfo.getHardwareNumber() + bomInfo.getHardwareVersion(), bomInfo);
		}

		EqVersionService.AddBomList(typeSpanId, bomMap);
	}

	// 程序初始化时、定时升级时读取升级版本信息（tbl_bomlist和tbl_typespan）
	public static void getTypeSpanAndBomFromDB() {
		// EqVersionService.removeAllTypeSpan();
		EqVersionService.removeAllBomList();
		// 取得全部升级版本
		List<TblTypeSpan> tblTypeSpanList = DB.typeSpanDao.getAll();
		if (tblTypeSpanList == null || tblTypeSpanList.size() < 1) {
			logger.error("[upgrade],getTypeSpanAndBomFromDB had not TblTypeSpan");
			return;
		}

		int size = tblTypeSpanList.size();
		logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "TblTypeSpan.size"), new Object[]{size});

		for (int i = 0; i < size; i++) {
			TblTypeSpan typeSpan = tblTypeSpanList.get(i);

			getBomListByTypeSpanId(typeSpan.getTypeSpanId());
		}
	}

	public static BomListInfo convertBomInfo(TblBomList tblBom)
	{
		BomListInfo bomInfo =  new BomListInfo();
		bomInfo.setHardwareNumber(tblBom.getHardwareNumber());
		bomInfo.setHardwareVersion(tblBom.getHardwareVersion());
		bomInfo.setForceUpdate(tblBom.getForceUpdate());
		bomInfo.setSoftNumber(tblBom.getSoftNumber());
		bomInfo.setSoftVersion(tblBom.getSoftVersion());
		bomInfo.setTypeSpan(tblBom.getTypeSpan());
		bomInfo.setTypeSpanId(tblBom.getTypeSpanId());
		bomInfo.setFileMd5(tblBom.getFileMd5());
		bomInfo.setBomListId(tblBom.getBomListId());
		
		return bomInfo;
	}

	private static List<TblEquipmentVersion> getEpVersion(ElectricPileCache epCache, int type) {
		if (epCache == null) {
			logger.error("[upgrade],getEpVersionFromDB epCache==null");
			return null;
		}

		if (type != 1) {
			logger.error("[upgrade],getEpVersionFromDB epCode:{},invalid type:{}",
					epCache.getCode(), type);
			return null;
		}

		TblEquipmentVersion equitment = new TblEquipmentVersion();
		equitment.setProductID(epCache.getPkEpId());
		equitment.setProductType(type);
		List<TblEquipmentVersion> equitMentVerList = DB.equipmentVersionDao.findEqVersion(equitment);
		if (equitMentVerList == null || equitMentVerList.size() < 1) {
			logger.info("[upgrade],getEpVersionFromDB did not TblEquipmentVersion,epCode:{},pkEpId:{}",
					epCache.getCode(), epCache.getPkEpId());
			return null;
		}

		return equitMentVerList;
	}

	// 电桩或集中器连接初始化时读取数据库tbl_equipmentversion表
	public static void getEpVersionFromDB(ElectricPileCache epCache, int type) {
		List<TblEquipmentVersion> equitMentVerList = getEpVersion(epCache, type);
		if (equitMentVerList == null) return;
		logger.info(LogUtil.addExtLog("epCode|pkEpId|TblEquipmentVersion.size"),
				new Object[] { epCache.getCode(), epCache.getPkEpId(), equitMentVerList.size() });

		TblEquipmentVersion equitment;
		for (int i = 0; i < equitMentVerList.size(); i++) {
			EqVersionInfo verinfo = new EqVersionInfo();
			equitment = equitMentVerList.get(i);

			verinfo.setPk_EquipmentVersion(equitment.getPkEquipmentVersion());
			verinfo.setEpCode(epCache.getCode());
			verinfo.setType(type);
			verinfo.setHardwareNumber(equitment.getHardwareNumber());
			verinfo.setHardwareVersion(equitment.getHardwareVersion());
			verinfo.setSoftNumber(equitment.getFirmwareNumber());
			verinfo.setSoftVersion(equitment.getFirmwareVersion());
			epCache.getVersionCache().addEpVersion(verinfo.getHardwareNumber() + verinfo.getHardwareVersion(), verinfo);
		}
		// 设置bomList到设备
		// setBomListToEp(epCache,epCache.getTypeSpanId());
	}
	
	/**
	 * 
	 * @param epCache
	 * @param stationAddr
	 * @param type
	 */
	public static void getConcentratorVersionFromDB(EpConcentratorCache stationCache,int type)
	{
		if(stationCache ==null)
		{
			logger.error("[upgrade],getConcentratorVersionFromDB stationCache==null");
			
			return;
		}
		int stationAddr = stationCache.getPkId();
		if(type!=2)
		{
			logger.error("[upgrade],getConcentratorVersionFromDB stationAddr:{},invalid type:{}",stationAddr,type);
			return;
		}
		
			
		TblEquipmentVersion equitment = new TblEquipmentVersion();
		equitment.setProductID(stationCache.getPkId());
		equitment.setProductType(type);
		List<TblEquipmentVersion> equitMentVerList = DB.equipmentVersionDao.findEqVersion(equitment);
		if(equitMentVerList == null || equitMentVerList.size()<1)
		{
			logger.info("[upgrade],getConcentratorVersionFromDB did not TblEquipmentVersion,stationAddr:{}",stationAddr);
			return ;
		}
		logger.info("[upgrade],getConcentratorVersionFromDB stationAddr:{},had {} TblEquipmentVersion",stationAddr,equitMentVerList.size());
				
		EqVersionInfo  verinfo= new EqVersionInfo();
		for(int i=0;i<equitMentVerList.size();i++)
		{
		    equitment = equitMentVerList.get(i);
		    verinfo.setPk_EquipmentVersion(equitment.getPkEquipmentVersion());
		    verinfo.setStationAddr(stationAddr);
		    verinfo.setType(type);
		    verinfo.setHardwareNumber(equitment.getHardwareNumber());
		    verinfo.setHardwareVersion(equitment.getHardwareVersion());
		    verinfo.setSoftNumber(equitment.getFirmwareNumber());
		    verinfo.setSoftVersion(equitment.getFirmwareVersion());
		}
		stationCache.getVersionCache().addEpVersion(verinfo.getHardwareNumber()+verinfo.getHardwareVersion(), verinfo);
		//设置bomList到站
		//setBomListToStation(stationCache,stationCache.getTypeSpanId());
	}
	public static void checkEpModifyTypeSpan()
	{
		List<TblElectricPile> epList = DB.epClientDao.getLastUpdate();
		int size =epList.size();
		for(int i=0;i<size;i++)
		{
			TblElectricPile epInfo=epList.get(i);
			ElectricPileCache epCache = EpService.getMapEpCache().get(epInfo.getEpCode());
			epCache.setTypeSpanId(epInfo.getEpTypeSpanId());
		}
		
		
	}

	public static void onTimerUpdateHexFile() {
		getTypeSpanAndBomFromDB();

		Iterator iter = updateHexFileMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			int typeSpanId = (int) entry.getKey();

			// 查询所有电桩
			queryAllEpVerion(typeSpanId);
			// 查询所有集中器
			queryAllStationVerion(typeSpanId);
		}
	}

	public static void saveEqVersiontoDB(EqVersionInfo verinfo, int pkId, int type, EqVersionCache eqVerCache) {
		if (verinfo == null || eqVerCache == null) {
			return;
		}

		String key = verinfo.getHardwareNumber() + verinfo.getHardwareVersion();
		TblEquipmentVersion equipment = new TblEquipmentVersion();
		equipment.setProductID(pkId);
		equipment.setProductType(type);
		equipment.setFirmwareNumber(verinfo.getSoftNumber());
		equipment.setFirmwareVersion(verinfo.getSoftVersion());
		equipment.setHardwareNumber(verinfo.getHardwareNumber());
		equipment.setHardwareVersion(verinfo.getHardwareVersion());
		equipment.setUpdatedate(DateUtil.currentDate());

		EqVersionInfo ver = eqVerCache.getEpVersion(key);
		if (ver != null) {
			if (ver.getSoftVersion().compareTo(verinfo.getSoftVersion()) != 0
					|| ver.getSoftNumber().compareTo(verinfo.getSoftNumber()) != 0) {
				DB.equipmentVersionDao.updateEqVersion(equipment);
				logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "updateEqVersionToDB pkId|type"),
						new Object[]{pkId, type});
			}

		} else if (ver == null) {
			DB.equipmentVersionDao.insertEqVersion(equipment);
			verinfo.setPk_EquipmentVersion(equipment.getPkEquipmentVersion());
			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "insertEqVersionToDB pkId|type"),
					new Object[]{pkId, type});
		}

		eqVerCache.addEpVersion(key, verinfo);
	}

	// 处理桩回复版本信息
	public static void handleVersionAck(EpCommClient epCommClient, String epCode,int stationAddr,Vector<EqVersionInfo> verInfos) 
	{
		logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "response of query ep version enter,epCode|stationAddr"),
				new Object[]{epCode,stationAddr});
		if(verInfos == null)
	    {
		    logger.error("[upgrade]response of query ep version fail verInfos==null, epCode:{},stationAddr:{}",epCode,stationAddr);
		    return;
	    }

		EqVersionCache eqVerCache;
		int pkId;
		int type = 3;
		if(stationAddr>0) //处理站
		{
			EpConcentratorCache stationCache = EpConcentratorService.getConCentrator(epCommClient.getIdentity());
			if(stationCache ==null)
			{
				 logger.error("[upgrade]response of query ep version fail stationCache ==null, stationAddr:{}",stationAddr);
				return;
			}
			eqVerCache = stationCache.getVersionCache();
		    pkId = stationCache.getPkId();
		    type=2;
		}
		else
		{
			//更新电桩表中产品型号,整个产品型号
		    ElectricPileCache epCache = EpService.getEpByCode(epCode);
		    if(epCache ==null)
		    {
			    logger.error("[upgrade]response of query ep version fail epCache ==null, epCode:{}",epCode);
			    return;
		    }
		    
		    eqVerCache = epCache.getVersionCache();
		    pkId = epCache.getPkEpId();

			Map<String,Object> map =  new HashMap<String,Object> ();
			map.put("typeSpanId", epCache.getTypeSpanId());
			map.put("productId", pkId);
		    List<TblBomList> tblBomList = DB.bomListDao.getBomByProductId(map);
			for (TblBomList bomList : tblBomList) {
				BomListInfo bomInfo = convertBomInfo(bomList);
				if (bomInfo.splitHardwareVersion() > 0 || bomInfo.splitSoftVersion() > 0) {
					continue;
				}
				eqVerCache.AddBomList(bomList.getHardwareNumber()+bomList.getHardwareVersion(), bomInfo);
				type=1;
			}
		}

		//比较数据库中硬件版本，如硬件已换，旧硬件删除
		eqVerCache.removeEpVersion(verInfos);//
		int size = verInfos.size();
		for(int i=0;i<size;i++)
		{
			EqVersionInfo info = verInfos.get(i);
			if (type < 3) {
				saveEqVersiontoDB(info,pkId,type,eqVerCache);
			} else if (type == 3) {
				saveEqVersiontoDB(info,pkId,1,eqVerCache);
			}
		}

		if (type < 3) forceUpdateHexFile(epCommClient,epCode,stationAddr, eqVerCache);
	}	

    public static void handleUpdateAck(int stationAddr,String epCode,EqVersionInfo info) 
    {
    	if(info ==null)
		{
    		logger.error("[upgrade],response of update Over  fail, info ==null epCode:{},stationAddr:{}",epCode,stationAddr);
           return;
		}
    	EqVersionCache eqVerCache=null;
		int pkId;
		int type;
		if(stationAddr>0)//站
    	{
    		EpConcentratorCache stationCache = EpConcentratorService.getConCentrator(""+stationAddr);
    		if(stationCache ==null)
    		{
    			logger.error("[upgrade],response of update Over  fail, epCode:{},stationAddr:{}stationCache ==null",epCode,stationAddr);
    			return;
    		}
    		pkId = stationCache.getPkId();
    		eqVerCache=stationCache.getVersionCache();
    		type=2;
    	}
		else//电桩
    	{
    		ElectricPileCache epCache = EpService.getEpByCode(epCode);
    		if(epCache ==null)
    		{
    			logger.error("[upgrade],response of update Over  fail epCache ==null epCode:{},stationAddr:{}",epCode,stationAddr);
    			return;
    		}
    		pkId = epCache.getPkEpId();
    		eqVerCache=epCache.getVersionCache();
    		type=1;
    	}
		saveEqVersiontoDB(info,pkId,type,eqVerCache);	
    }
    
    public static int sendVersion(EpCommClient epCommClient,String epCode, int stationId)
    {
	    if(epCommClient==null || !epCommClient.isComm())
	    {
		   logger.error("[upgrade],send getVersion to ep fail,epCommClient is not comm,epCode:{},stationAddr:{}",epCode,stationId);
		   return  1;
	    }
	    
	    int errorCode= queryVersion(epCommClient,epCode,stationId); 
	    
		logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "epCode|stationAddr|errorCode"),
				new Object[]{epCode,stationId,errorCode});

		return errorCode;
    }
    
    public static int queryEpVersion(ElectricPileCache epClient, int typeSpanId)
    {
		logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "typeSpanId|epCode"), new Object[]{typeSpanId,epClient.getCode()});
    	epClient.setTypeSpanId(typeSpanId);
    	 
    	EpCommClient commClient = epClient.getEpNetObject();

    	return sendVersion((EpCommClient)commClient,epClient.getCode(),0);
    }

    public static int queryConcentratorVersion(EpConcentratorCache concentratorClient, int typeSpanId)
    {
    	logger.info("[upgrade], queryConcentratorVersion,typeSpanId:{},stationAddr:{}",typeSpanId,concentratorClient.getPkId());
    	concentratorClient.setTypeSpanId(typeSpanId);
    	
   		return sendVersion((EpCommClient)concentratorClient.getEpNetObject(),"0000000000000000",0);
    }

    public  static void queryAllEpByTypeSpanID(int typeSpanId,Map<String,BomListInfo> bomMap)
    {
    	if(bomMap==null)
    	{
    		logger.error("[upgrade],queryAllEpByTypeSpanID fail,bomMap is null typeSpanId:{}",typeSpanId);
	   		 return ;
    	}

		List<TblElectricPile> epPileList = DB.epClientDao.findResultObjectBySpanId(typeSpanId);
		if (epPileList == null || epPileList.size() < 1) {
			logger.error("[upgrade],queryAllEpByTypeSpanID fail,not find ep from DB typeSpanId:{}", typeSpanId);
			return;
		}

		for (int i = 0; i < epPileList.size(); i++) {
			TblElectricPile ep = epPileList.get(i);
			ElectricPileCache epClient = EpService.getEpByCode(ep.getEpCode());
			if (epClient == null) {
				logger.error("[upgrade],queryAllEpByTypeSpanID fail,epClient = null,epCode:{}", ep.getEpCode());
				continue;
			}
			// 清除电桩内存中的bom
			Iterator iter = bomMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				epClient.getVersionCache().removeBomList(key);
			}

			queryEpVersion(epClient, typeSpanId);
		}
	}

	public synchronized static int queryAllEpVerion(int typeSpanId) {
		int error = 1;

		Iterator iter = EpService.getMapEpCache().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			ElectricPileCache epClient = (ElectricPileCache) entry.getValue();
			if (epClient == null) {
				logger.error("[upgrade],queryAllEpVerion fail not find epClient typeSpanId:{}", typeSpanId);
				continue;
			}

			if (typeSpanId != epClient.getTypeSpanId()) {
				logger.error("[upgrade],queryAllEpVerion fail typeSpanId:{} != epClient.getTypeSpanId():{},epCode:{}",
						new Object[] { typeSpanId, epClient.getTypeSpanId(), epClient.getCode() });
				continue;
			}

			error = queryEpVersion(epClient, typeSpanId);
		}

		return error;
	}

    //查询标志，queryFlag=1查询所有集中器，queryFlag=0查询指定产品类型集中器
    public synchronized static int queryAllStationVerion(int typeSpanId){
    	int error =1;

    	Iterator iter = EpConcentratorService.getMapStation().entrySet().iterator();
 		
 		while (iter.hasNext()) {

 			Map.Entry entry = (Map.Entry) iter.next();
 			
 			EpConcentratorCache stationClient=(EpConcentratorCache) entry.getValue();
		
	    	if(stationClient == null )
		    {
	    		logger.error("[upgrade],queryAllStationVerion fail,stationClient= null,typeSpanId:{}",typeSpanId);
			    continue;
		    }
	    	
	    	if(typeSpanId !=stationClient.getTypeSpanId())
	    	{
	    		logger.error("[upgrade],queryAllStationVerion fail,typeSpanId !=stationClient.getTypeSpanId(),stationAddr:{},typeSpanId:{}",stationClient.getPkId(),typeSpanId);
	    		continue;
	    	}
	    	
	    	error = queryConcentratorVersion(stationClient,typeSpanId);
 		}

 		return error;
	}
     
    public static void forceUpdateHexFile(EpCommClient epCommClient,String epCode,int stationAddr,EqVersionCache eqVerCache)
 	{
    	if(epCode.compareTo("0000000000000000")==0 &&(stationAddr==0))
    	{
    		 logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,epCode|stationAddr"),epCode,stationAddr);
    		 return;
    	}
    	if(eqVerCache==null || eqVerCache.getMapEpVersion()==null)
    	{
    		logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find Version, epCode|stationAddr"),epCode,stationAddr);
    		return;
    	}
 
        //该电桩下所有的升级版本和电桩现有版本对照判断
 		Iterator iter = eqVerCache.getMapEpVersion().entrySet().iterator();
 		
 		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
 			
 			EqVersionInfo verinfo=(EqVersionInfo) entry.getValue();
 			String key=(String)entry.getKey();
 			if(verinfo==null)
 			{
 				logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find hardware in ep,epCode|stationAddr|key"),
                       new Object[]{epCode,stationAddr,key});
 				continue;
 			}
    	
 			BomListInfo bom=getBomInfo(stationAddr,epCode,verinfo.getHardwareNumber(),verinfo.getHardwareM(),verinfo.getHardwareA());
    		if(bom==null)
    		{//电桩上无该硬件
    			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find hardware from bom," +
    					" hardwareNumber|hardwareVersion|epCode|stationAddr|key"),
                       new Object[]{verinfo.getHardwareNumber(),verinfo.getHardwareVersion(),
    					epCode,stationAddr,key});
    			 continue;
    		}
     		String bomSoft = bom.getSoftNumber()+bom.getSoftVersion();
    		String epSoft = verinfo.getSoftNumber()+verinfo.getSoftVersion();
    		if(bomSoft.compareTo(epSoft)==0)//版本相同
    		{
    			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,softversion is same," +
    						"softVersion|hardwareNumber|hardwareVersion|epCode|stationAddr"),
    						new Object[]{bomSoft, bom.getHardwareNumber(),bom.getHardwareVersion(),
        					epCode,stationAddr});
    			continue;
    		}
    		if(bom.getForceUpdate()==0)//不强制，判断版本高低，升级版本高就升
    		{
    			int bomSoftver = bom.getSoftM()*100000+bom.getSoftA()*1000+bom.getSoftC();
        		int epSoftver = verinfo.getSoftM()*100000+verinfo.getSoftA()*1000+verinfo.getSoftC();
                if(bomSoftver<=epSoftver)
                {
                	logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,bomversion is Low,bom hardwareNumber|" +
                    			"hardwareVersion|epCode|stationAddr|bom softVersion|ep softVersion"),
                    			new Object[]{bom.getHardwareNumber(),bom.getHardwareVersion(),epCode,
                    	          stationAddr,bomSoftver,epSoftver});
                    continue;	
                 }
                    	
    		}

    		forceUpdateHexFile(epCommClient,epCode,stationAddr,bom.getHardwareNumber(),bom.getHardwareM(),bom.getHardwareA(),bomSoft); 
    	}
 	}
    
    public static int forceUpdateHexFile(EpCommClient epCommClient, String epCode, int stationAddr,
			String hardwareNumber, int hardwareM, int hardwareA, String bomSoft) {
		if (epCommClient == null || !epCommClient.isComm()) {
			logger.debug(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,commClient close,epCode|stationAddr"),
					epCode, stationAddr);
			return 1;
		}

		try {
			byte[] msg = EpEncoder.do_force_update_ephex((short) stationAddr,
					epCode, hardwareNumber, hardwareM, hardwareA);

			if (msg == null) {
				logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,msg is null,epCode|stationAddr"),
						epCode, stationAddr);
				return 1;
			}
			byte[] cmdTimes = WmIce104Util.timeToByte();
			EpMessageSender.sendMessage(epCommClient, 0, 0,
					Iec104Constant.C_FORCE_UPDATE_EP_HEX, msg, cmdTimes, epCommClient.getVersion());

			logger.info(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE,
							"success, send hardwareNumber|hardwareVersion|bomSoft|epCode|stationAddr"),
					new Object[] { hardwareNumber, hardwareM + "." + hardwareA, bomSoft, epCode, stationAddr });

			return 0;
		} catch (NumberFormatException e) {
			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "exception"), e.getStackTrace());
			return 1;
		}
	}
	  
	public static void handleEpHexFileSumaryReq(EpCommClient epCommClient, String epCode,short stationAddr,
			String hardwareNumber,int hardwareM,int hardwareA,int len,byte []cmdTimes)
	{
	   
	   if(epCommClient==null || !epCommClient.isComm())
	   {
		    logger.info("[upgrade],handleEpHexFileSumaryReq fail,commClient==null || commClient is close,epCode:{},stationAddr:{}",
					epCode,stationAddr);
			return;
	   }
		if (stationAddr ==0 && epCode.compareTo("0000000000000000")==0 )
    	{
			logger.info("[upgrade],handleEpHexFileSumaryReq fail,did not find epCode:{},stationAddr:{}",
					epCode,stationAddr);
			 return ;
    	}
		
		BomListInfo bom = getBomInfo(stationAddr, epCode, hardwareNumber, hardwareM, hardwareA);
		if(bom == null)
		{
			String key = hardwareNumber+hardwareM+"."+hardwareA;
		    logger.info("[upgrade],handleEpHexFileSumaryReq fail,not find bom,epCode:{},stationAddr:{},key:{}",
				new Object[]{epCode,stationAddr,key});
		    return;
		}
		
		 String  filename =getFileName(bom.getSoftNumber(),bom.getSoftVersion());	
		 String Md5Value=bom.getFileMd5();
		 byte[] msg;
		 int file_len = FileUtils.getFileSize(filename);
		 short sections =0;
		 int existFlag=0;
		 int updateFlag=0;
         if(file_len >0)
         {
        	 FileUtils.getBinaryInfo(filename, 0, file_len);
			 sections = getEpHexFileSections(file_len, len);
			 existFlag=1;
			 updateFlag=1;
			 logger.info("[upgrade], handleEpHexFileSumaryReq success, filename:{},epCode:{},stationAddr:{}",
						new Object[]{filename,epCode,stationAddr});
		 }
         else 
		 {
        	 file_len=0;
			 logger.info("[upgrade],handleEpHexFileSumaryReq fail,not find filename:{},epCode:{},stationAddr:{}",
						new Object[]{filename,epCode,stationAddr});
		 }
		 msg = EpEncoder.do_ep_hex_file_sumary(epCode,
					stationAddr, hardwareNumber, hardwareM, hardwareA,
					bom.getSoftNumber(), bom.getSoftM(), bom.getSoftA(), bom.getSoftC(), existFlag, file_len, sections,
					updateFlag,Md5Value);
		 if (msg == null) 
		 {
			 logger.info("[upgrade],handleEpHexFileSumaryReq send fail,exception");
			 return;
		 }
		 EpMessageSender.sendMessage(epCommClient, 0, 0,Iec104Constant.C_EP_HEX_FILE_SUMARY, msg,cmdTimes,epCommClient.getVersion());

	}
	public static String getFileName(String softNumber,String softVersion)
	{
		String filename= GameConfig.epExePath;
		filename = filename + softNumber + "-V"+ softVersion + ".bin";
		return filename;
	}
	public static BomListInfo getBomInfo(int stationAddr,String epCode,String hardwareNumber,int hardwareM,int hardwareA)
	{
		int typeSpanId=0;
		BomListInfo bom = null;
		Map<String,BomListInfo> bomMap = null;
		
		if(stationAddr>0)
		{
			EpConcentratorCache stationCache=EpConcentratorService.getConCentrator(""+stationAddr);
			if(stationCache==null)
			{
				logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find EpStationCache,stationAddr"),stationAddr);
				return null;
			}
			typeSpanId=stationCache.getTypeSpanId();
			bomMap = stationCache.getVersionCache().getMapBomList();
		 }
		 else
		 {
		    ElectricPileCache epCache = EpService.getEpByCode(epCode);
			if(epCache == null)
			{
				logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find epCache,epCode"),epCode);
				return null;
			}
			typeSpanId=epCache.getTypeSpanId();
			bomMap = epCache.getVersionCache().getMapBomList();
		 }
		 String key = hardwareNumber+hardwareM+"."+hardwareA;
		 if(bomMap !=null)
		 {
			 bom = bomMap.get(key);	 
		 }
		 if(bom == null)
		 {
			 bomMap =  getBomList(typeSpanId);//从全局升级map中取出
			 if(bomMap!=null)
				 bom = bomMap.get(key);
	     }
		 return bom;
	}
	public static short getEpHexFileSections(int file_len,int len)
	{
		if(file_len<1 || len<1)
			return (short)0;
		short sections = (short) (file_len / len);
		int remain = file_len % len;
		if (remain > 0)
			sections += 1;
		
		return sections;
		
	}
	public static short calcFileDownSectionLen(int file_len,int SectionIndexReq,int len)
	{
		if(file_len<1)
			return 0;

		int sections = getEpHexFileSections(file_len,len);
		
		if (SectionIndexReq < 1 || SectionIndexReq > sections) {
			return 0;
		}

		if (SectionIndexReq == sections) {
			return (short) (file_len - (sections - 1)
					* len);
		}
		else
		{
			return (short)len;
		}
	}

	public static void handleEpHexFileDownReq(EpCommClient epCommClient, String epCode,short stationAddr,
			int SectionIndexReq,EqVersionInfo versionInfo,int sectionLeng,byte []cmdTimes) {
		
		if (stationAddr == 0 && epCode.compareTo("0000000000000000") == 0) {
			logger.error("[upgrade],handleEpHexFileDownReq fail, did not find eq,epCode:{},stationAddr:{}",
					epCode,stationAddr);
			return;
		}
		
		EpCommClient commClient = EpCommClientService.getCommClient(epCommClient.getChannel());
		if (commClient == null || !commClient.isComm())
		{
			logger.error("[upgrade],handleEpHexFileDownReq fail,commClient is close,epCode:{},stationAddr:{}",
					epCode,stationAddr);
			return;
		}
		
		String  filename =getFileName(versionInfo.getSoftNumber(),versionInfo.getSoftVersion());
		int file_len = FileUtils.getFileSize(filename);

		short len = calcFileDownSectionLen(file_len,SectionIndexReq,sectionLeng);
		
		byte[] msg;
		byte[] SectionData=null;
		int successFlag=0;
		if(len<=0)
		{
			len=0;
			logger.error("[upgrade],handleEpHexFileDownReq fail,len<=0,epCode:{},stationAddr:{},softNumber:{},softVersion:{}",
					new Object[]{epCode,stationAddr,versionInfo.getSoftNumber(),versionInfo.getSoftVersion()});
		}
		else
		{
			 int offset = (SectionIndexReq - 1)* sectionLeng;
			 SectionData = FileUtils.getBinaryInfo(filename, offset, len);
			 successFlag=1;
		}
		msg = EpEncoder.do_ep_hex_file_down(epCode,stationAddr,
				versionInfo.getSoftNumber(),
				versionInfo.getSoftM(),
				versionInfo.getSoftA(),
				(short)versionInfo.getSoftC(),
				(short)SectionIndexReq,(short)len, SectionData,successFlag);
		if(msg ==null)
		{
			logger.error("[upgrade],handleEpHexFileDownReq fail,do_ep_hex_file_down exception");
			return ;
		}
	
		EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_EP_HEX_FILE_SECTION, msg,cmdTimes,commClient.getVersion());

	}	

	public static int queryVersion(EpCommClient commClient,String epCode,int stationAddr)
    {
    	if (stationAddr ==0 && epCode.compareTo("0000000000000000")==0 )
    	{
			 return ErrorCodeConstants.INVALID_EP_CODE;
    	}
    	
		byte[] data = EpEncoder.do_eqversion_req(epCode, (short)stationAddr);
		byte[] cmdTimes = WmIce104Util.timeToByte();
		EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_DEVICE_VERSION_REQ, data,cmdTimes,commClient.getVersion());
			
	    return 0;
    }
	
	 public  static void deleteEqVersionFromDB(int id)
	 {
		 DB.equipmentVersionDao.deleteEqVersion(id);
	 }
	 
     
	public static void queryAllStaionByTypeSpanID(int typeSpanId, Map<String, BomListInfo> bomMap) {
		if (bomMap == null) {
			logger.error("[upgrade],queryAllStaionByTypeSpanID fail,bomMap is null typeSpanId:{}",
					typeSpanId);
			return;
		}
		List<TblConcentrator> centList = DB.concentratorDao.findResultObjectBySpanId(typeSpanId);
		if (centList == null || centList.size() < 1) {
			logger.error(LogUtil.addFuncExtLog(LogConstants.FUNC_UPGRADE, "fail,not find concentrator from DB typeSpanId"),
					new Object[] { typeSpanId });
		}

		for (int i = 0; i < centList.size(); i++) {
			TblConcentrator concentrator = centList.get(i);
			EpConcentratorCache stationClient = EpConcentratorService.getConCentrator("" + concentrator.getPkConcentratorID());
			if (stationClient == null) {
				logger.error("[upgrade],queryAllStaionByTypeSpanID fail,stationClient = null stationAddr:{}",
						concentrator.getPkConcentratorID());
				continue;
			}

			// 清除集中器内存中的bom
			Iterator iter = bomMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				stationClient.getVersionCache().removeBomList(key);
			}

			queryConcentratorVersion(stationClient, typeSpanId);
		}
	}
    
}
