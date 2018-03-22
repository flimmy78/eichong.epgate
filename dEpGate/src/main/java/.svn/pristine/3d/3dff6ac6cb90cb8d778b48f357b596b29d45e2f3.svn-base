package com.ec.epcore.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MsgWhiteList {
	
	private static boolean open=false ;
	
	public static Map<String, String> list = new ConcurrentHashMap<String,String>();
		
	
	public static boolean isOpen() {
		return open;
	}
	
	public static void setOpen(boolean open) {
		MsgWhiteList.open = open;
	}
	
	public synchronized static void addWhite(String epCode){
		list.put(epCode,epCode);
	}
	
	public synchronized static void removeWhite(String epCode){
		list.remove(epCode);
	}
	
	public synchronized static boolean find(String epCode){
		String v = list.get(epCode);
		if(v==null)
			return false;
		return true;
	}
	
	
}
