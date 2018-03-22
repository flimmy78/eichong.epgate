package com.ec.usrcore.test;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.net.proto.WmIce104Util;
import com.ec.usrcore.cache.ElectricPileCache;
import com.ec.usrcore.cache.EpGunCache;
import com.ec.usrcore.cache.RateInfoCache;
import com.ec.usrcore.cache.UserCache;
import com.ec.usrcore.service.CacheService;
import com.ec.usrcore.service.EpService;
import com.ec.usrcore.service.RateService;
import com.ec.usrcore.service.StatService;
import com.ec.usrcore.service.UserService;

public class ImitateConsumeService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ImitateConsumeService.class);
	
	public static Map<String,String> userKeyMap = new ConcurrentHashMap<String, String>();

	
	public static String getConnetUserKey(String userName)
	{
		String userKey=null;
		userKey = userKeyMap.get(userName);
		
		return userKey;
	}
	public static void putConnetUserKey(String userName,String userKey)
	{
		userKeyMap.put(userName, userKey);
	}
	static public int checkSign(Map<String, List<String>> params)
	{
			int errorCode=0; 
			Object oUser = params.get("user");
			Object oSign = params.get("sign");
			if(oUser == null || oSign == null)
			{
				errorCode=1;
				return errorCode;
			}
			
			String src="";
			String sign="";
			String userName="";
			
			Collection<String> keyset= params.keySet();
			List<String> list = new ArrayList<String>(keyset);  
			   
			//对key键值按字典升序排序  
			Collections.sort(list);  
			            
		    for (int i = 0; i < list.size(); i++) {
		    	 String key = list.get(i);
		    	 if(key.compareTo("sign")==0)
		    	 {
		    		 sign = (String)params.get(key).get(0);
		    	 }
		    	 else
		    	 {
		    		 if(key.compareTo("user")==0)
		    		 {
		    			 userName= (String) params.get(key).get(0);
		    		 }
		    		 if(src.length()!=0)
		    			 src =src +"&";
		    		 src +=list.get(i)+"="+params.get(key).get(0);
		    	 } 
		     }
		    
		    String userKey = getConnetUserKey(userName);
		    if(userKey ==null)
		    {
		    	return 2;
		    }
		    src += userKey ;
		   
		    String calcSign = WmIce104Util.MD5Encode(src.getBytes());
		    if(calcSign.compareTo(sign)!=0)
		    {
		    	return 3;
		    }
		    	
		    
		    return 0;
	}
	public static String findUser( Map<String, List<String>> params)
	{
		boolean paramValid = false;
    	
    	String account="";
    	
    	
        if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
    	List<String> vals = params.get("account");

    	if(vals!=null && vals.size()>=1)
    	{
    		account = vals.get(0);
    		paramValid = true;
    	}
        
        if(!paramValid)
        	return "params invalid";
        int error = checkSign(params);
        if(error>0)
			return "checkSign fail";
      
    	UserCache u = UserService.getUserCache( account );
		if(u == null)
		{
			return "not find user" + account;
		}
		//UnitTest.testPauseStat(u.getId(), u.getAccount());
		return ""+u;    
	}
	
	public static String getEpDetail( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		String epCode = null;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		List<String> vals = params.get("code");
		if (vals != null && vals.size() >= 1)
		{
			epCode = vals.get(0);
			paramValid = true;

		}

		if (!paramValid) {
			return "params invalid";
		}
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		ElectricPileCache epClient= EpService.getEpCache(epCode);
		if(	epClient !=null )
		{
			String epDetails = epClient.toString();
			RateInfoCache rateInfo= RateService.getRateById(epClient.getRateid());
			if(rateInfo ==null)
			     epDetails=epDetails+"rateInfo is null";
			else
				epDetails=epDetails+rateInfo.getRateInfo().toString();
			return (epDetails);
		} 
			
		return ("not find ep");
		

    }
	
	public static String getgundetail( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		String epCode = null;
		int epGunNo = 0;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		List<String> vals = params.get("code");
		List<String> valsGunNo = params.get("gunno");
		if (vals != null && vals.size() >= 1 && valsGunNo != null
				&& valsGunNo.size() >= 1) {
			epCode = vals.get(0);
			epGunNo = Integer.parseInt(valsGunNo.get(0));

			paramValid = true;

		}

		if (!paramValid) {
			return "params invalid";
		}
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		EpGunCache epGunCache = CacheService.getEpGunCache(epCode, epGunNo);
		if (epGunCache != null) {
			String gunDetails = epGunCache.toString();
			return (gunDetails);
		} else {
			String value = MessageFormat.format("getgundetail! not find,epCode:{0}, epGunNo:{1}\n", epCode,
					epGunNo);
			return (value);
		}

	}
	
	public static String stat( Map<String, List<String>> params)
    {
       
        if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
    	return StatService.stat();
    }
	
	public static String cleanUser( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	
    	String account="";
    	
    	
        if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
    	List<String> vals = params.get("account");

    	if(vals!=null && vals.size()>=1)
    	{
    		account = vals.get(0);
    		paramValid = true;
    	}
        
        if(!paramValid)
        	return "params invalid";
        
        int error = checkSign(params);
       if(error>0)
			return "checkSign fail";
      
    	UserCache u = UserService.getUserCache( account );
		if(u == null)
		{
			return "not find user" + account;
		}
		u.clean();
		logger.info("cleanUser,account:{}",account);
		return ""+u;    
    }
	
	public static String getClientCommByChannel( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	String channel = null;

		List<String> vals = params.get("channel");
		
		if(vals!=null && vals.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			channel = vals.get(0);
			//EpCommClient commClient = EpCommClientService.getCommClient(channel);
			
			String str = "";//commClient.toString();
 			return str;
		}
		return "params invalid";
    }
	
	public static String getChNum( Map<String, List<String>> params)
    {
		if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		final StringBuilder sb = new StringBuilder();
		
		/*String epStatMsg =  EpCommClientService.getDebugInfo();
		
		String stationStatMsg =  EpConcentratorService.getDebugInfo();*/
		
		String PhoneStatMsg =  "";//PhoneService.getDebugInfo();
		
		sb.append(PhoneStatMsg);
		
		return sb.toString();
    }
	
}
	
