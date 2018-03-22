package com.ec.epcore.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.ec.epcore.net.proto.EqVersionInfo;
import com.ec.epcore.service.EqVersionService;


public class EqVersionCache {
	
	//保存电桩实际的硬件清单
	private  Map<String, EqVersionInfo> mapEpVersion = new ConcurrentHashMap<String,EqVersionInfo>();
	//保存桩需要升级的硬件清单
	private  Map<String,BomListInfo> mapBomList = null;

	
	public EqVersionCache()
	{
		 mapBomList = null;
	}
	
	public  Map<String,BomListInfo> getMapBomList()
	{
		return mapBomList;
	}
	public  Map<String,EqVersionInfo> getMapEpVersion()
	{
		return mapEpVersion;
	}
	    
	public void addEpVersion(String key,EqVersionInfo verinfo )
	{
		if(verinfo==null || key==null)
				return;
			
		mapEpVersion.put(key, verinfo);
	}
		
	public EqVersionInfo getEpVersion(String key)
	{
		if(key==null)
			return null;
		return mapEpVersion.get(key);
	}
	public   void AddBomList(String key,BomListInfo bomList)
	{
		if(key !=null)
		{  
			if(mapBomList==null)
			{
				mapBomList = new ConcurrentHashMap<String,BomListInfo>();
			}
			mapBomList.put(key, bomList);
		}
	}
	public   BomListInfo getBomList(String key)
	{
		if(key==null)
		    	return null;
		return mapBomList.get(key);
	}
	public  void removeMapBomList()
	{
		if(mapBomList !=null)
		      mapBomList.clear();
	}
	
	public  void removeBomList(String key)
	{
		if(mapBomList !=null)
		         mapBomList.remove(key);
	}
	
	public void removeEpVersion(Vector<EqVersionInfo> verInfos)
	{
		Iterator iter = mapEpVersion.entrySet().iterator();
				
		while (iter.hasNext()) {
			int result =0;
			Map.Entry entry = (Map.Entry) iter.next();
			String keyStr=(String) entry.getKey();
			for(int i = 0;i < verInfos.size();i++)
			{
				String key = verInfos.get(i).getHardwareNumber()+verInfos.get(i).getHardwareVersion();
				if(keyStr.compareTo(key) ==0)//相同的硬件型号和版本号
				{
					result=1;
					break;
				}
			}
			if(result==0)//找不到相同的硬件，删除数据库中保存的记录
			{
				EqVersionInfo ver= (EqVersionInfo) entry.getValue();
				EqVersionService.deleteEqVersionFromDB( ver.getPk_EquipmentVersion());
				mapEpVersion.remove(keyStr);
			}
		}
	}
	
	public String bomToString()
	{
		if(mapBomList==null || mapBomList.size() == 0)
			return "bomList is null";
		final StringBuilder sb = new StringBuilder();
    	 Iterator iter = mapBomList.entrySet().iterator();	
 		 while (iter.hasNext()) {

 			Map.Entry entry = (Map.Entry) iter.next();
 			
 			BomListInfo ver=(BomListInfo) entry.getValue();
 	        sb.append("{");
 	       sb.append("bomId=").append(ver.getBomListId()).append("\n\n");
 	        sb.append("硬件编号 = ").append(ver.getHardwareNumber()).append("\n\n");
 	        sb.append("硬件版本号 = ").append(ver.getHardwareVersion()).append("\n\n");
 	        sb.append("固件编号 = ").append(ver.getSoftNumber()).append("\n\n");
 	        sb.append("固件版本号 = ").append(ver.getSoftVersion()).append("\n\n");
 	        sb.append("强制更新标识=").append(ver.getForceUpdate()).append("}\n\n\n");
 		 }
 		 return sb.toString();
	}
	
	public String verInfoToString()
	{
		 if(mapEpVersion.size() == 0)
			return "epVersion is null";
		 
		 final StringBuilder sb = new StringBuilder();
		 Iterator iter = mapEpVersion.entrySet().iterator();	
	 		 while (iter.hasNext()) {
	 			 
	 		Map.Entry entry = (Map.Entry) iter.next();	
	 		EqVersionInfo ver=(EqVersionInfo) entry.getValue();
	 	    sb.append("{");
	 	    sb.append("硬件编号 = ").append(ver.getHardwareNumber()).append("\n\n");
	 	    sb.append("硬件版本号 = ").append(ver.getHardwareVersion()).append("\n\n");
	 	    sb.append("固件编号 = ").append(ver.getSoftNumber()).append("\n\n");
	 	    sb.append("固件版本号 = ").append(ver.getSoftVersion()).append("}\n\n\n");
	 	}
	 	return sb.toString();
	}
	
}
