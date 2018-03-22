package com.ec.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCacheMap {
	
	private Map<String, Object> objectMap = new ConcurrentHashMap<String,Object>();
	public ObjectCacheMap()
	{
		
	}
	
	public void add(String key,Object obj)
	{
		objectMap.put(key, obj);
	}
	public Object find(String key)
	{
		return objectMap;
	}
	public int size()
	{
		return objectMap.size();
	}
	public boolean remove(String key)
	{
		boolean ret=false;
		if(key==null || key.length()<=0)
		{
			return ret;
		}
		Iterator iter = objectMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String loppKey= (String)entry.getKey();
			
			if(loppKey!=null && loppKey.compareTo(key)==0)
			{
				iter.remove();
				ret= true;
				break;
			}
		}
		return ret;
	}

}
