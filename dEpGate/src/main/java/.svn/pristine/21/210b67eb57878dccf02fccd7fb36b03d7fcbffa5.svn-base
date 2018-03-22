package com.ec.epcore.test;


import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.ec.constants.ErrorCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.config.Global;
import com.ec.constants.EpConstants;
import com.ec.constants.UserConstants;
import com.ec.constants.YXCConstants;
import com.ec.epcore.cache.BespCache;
import com.ec.epcore.cache.BomListInfo;
import com.ec.epcore.cache.ElectricPileCache;
import com.ec.epcore.cache.EpConcentratorCache;
import com.ec.epcore.cache.EpGunCache;
import com.ec.epcore.cache.MsgWhiteList;
import com.ec.epcore.cache.RateInfoCache;
import com.ec.epcore.cache.RealChargeInfo;
import com.ec.epcore.cache.UserCache;
import com.ec.epcore.cache.UserRealInfo;
import com.ec.epcore.config.GameConfig;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.epcore.service.AppClientService;
import com.ec.epcore.service.ChargingInfo;
import com.ec.epcore.service.EpBespokeService;
import com.ec.epcore.service.EpChargeService;
import com.ec.epcore.service.EpCommClientService;
import com.ec.epcore.service.EpConcentratorService;
import com.ec.epcore.service.EpGunService;
import com.ec.epcore.service.EpService;
import com.ec.epcore.service.EqVersionService;
import com.ec.epcore.service.GunPointMap;
import com.ec.epcore.service.MonitorService;
import com.ec.epcore.service.RateService;
import com.ec.epcore.service.StatService;
import com.ec.epcore.service.UserService;
import com.ec.epcore.service.UsrGateService;
import com.ec.net.proto.Iec104Constant;
import com.ec.net.proto.SingleInfo;
import com.ec.net.proto.WmIce104Util;
import com.ec.netcore.client.ITcpClient;
import com.ec.utils.DateUtil;
import com.ec.utils.FileUtils;
import com.ec.utils.NumUtil;
import com.ec.utils.StringUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.ElectricpileWorkarg;
import com.ormcore.model.RateInfo;

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
	
	static public String testStartBespoke(Map<String, List<String>> params)
	{
		boolean paramValid=false;
    	if (params.isEmpty())
        {
        	return "paramValid is invalid";
        }
        String epCode =null;
        short time =0;
        int epGunNo=0;
        String phone = null;
        int redo = 0;
        String ip =null;
        String pokeCode=null;
        int payMode=1;
        String pwMd5=null;
        
        List<String> vals = params.get("code");
        List<String> vals1 = params.get("phone");
        List<String> vals2 = params.get("gunno");
        List<String> vals3 = params.get("time");
        List<String> vals4 = params.get("redo");
        List<String> vals5 = params.get("bespno");
        List<String> vals6 = params.get("payMode");
        List<String> vals7 = params.get("psWord");
        if(vals!=null && vals.size()>=1 && vals1!=null && vals1.size()>=1
        		&&vals2!=null && vals2.size()>=1&&vals3!=null && vals3.size()>=1
        		&&vals4!=null && vals4.size()>=1
        		&&vals6!=null && vals6.size()>=1
        		&&vals7!=null && vals7.size()>=1)
        {
        	int error = checkSign(params);
        	if(error>0)
				return "checkSign fail";
			
        	epCode =  vals.get(0);
        	time = (short)(Integer.parseInt(vals3.get(0)));
        	epGunNo = Integer.parseInt(vals2.get(0));
        	phone = vals1.get(0);
        	redo = Integer.parseInt(vals4.get(0));
        	
        	pokeCode= vals5.get(0);
        	payMode = Integer.parseInt(vals6.get(0));
        	
        	pwMd5 = vals7.get(0);
        	paramValid = true;
        }
        if(!paramValid)
        	 return "paramValid is invalid";
       
        if(epCode.length() != YXCConstants.LEN_CODE )
        {
        	return "paramValid is invalid";
        }
       
        
        UserRealInfo userRealInfo =  UserService.findUserRealInfo(phone);
	    if(userRealInfo==null)
			  return "无效用户";
		if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
			  return "无效用户";
         
    	
        EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
        if(epGunCache==null)
        {
        	logger.info("testStartBespoke not find EpGunCache!epCode:{},epGunNo:{}",epCode, epGunNo);
        	return "not find EpGunCache";
        	
        }
        BespCache bespCacheObj =epGunCache.getBespCache();
    	if(bespCacheObj != null)
    	{
    		pokeCode = bespCacheObj.getBespNo();
    	}
        
    	 java.util.Date dt = new Date();
    	 if(pokeCode.compareTo("0")==0)
         {
     		pokeCode = String.valueOf(dt.getTime() / 1000);
        
 	         pokeCode = pokeCode.substring(2, pokeCode.length());
 	         pokeCode += (int) (Math.random() * 9000 + 1000);
         }
	    long bespSt = dt.getTime() / 1000;
	    
	    
	    int epid = epGunCache.getPkEpId();
	    int gunid= epGunCache.getPkEpGunId();
	    int useid=0;
		
			
		UserCache u= UserService.getUserCache(phone);
		if(null != u)
		{
			useid = u.getId();
		}
		else
		{
			return "无效用户";
		}
		logger.info("bespoke accept accountId:{},bespNo:{},buyOutTime:{},epCode:{},epGunNo:{} from ImitateConsumeService",
	    		new Object[]{useid,pokeCode,time,epCode,epGunNo});
		
	    int errorCode = EpBespokeService.apiBespoke(epCode,
		           1, epid, pokeCode, time, bespSt, redo,
		           useid, phone, (long) gunid, payMode,1000,0,"");
	    
	    if(errorCode>0)
		{
	    	logger.info("bespoke apiBespoke errorCode:{},accountId:{},bespNo:{},buyOutTime:{},epCode:{},epGunNo:{}",
	  	    		new Object[]{errorCode,useid,pokeCode,time,epCode,epGunNo});
	    	
			return "errorCode:"+ errorCode;
		}
		return "send bespoke cmd success";
	}
	
	static public String testStartBespoke2(Map<String, List<String>> params)
	{
		boolean paramValid=false;
    	if (params.isEmpty())
        {
        	return "paramValid is invalid";
        }
        String epCode =null;
        short time =0;
        int epGunNo=0;
        String phone = null;
        int redo = 0;
        String ip =null;
        String bespNo =null;
        String pwMd5=null;
        
        List<String> vals = params.get("code");
        List<String> vals1 = params.get("phone");
        List<String> vals2 = params.get("gunno");
        List<String> vals3 = params.get("time");
        List<String> vals4 = params.get("redo");
        List<String> vals5 = params.get("bespno");
        List<String> vals6 = params.get("psWord");
        if(vals!=null && vals.size()>=1 && vals1!=null && vals1.size()>=1
        		&&vals2!=null && vals2.size()>=1&&vals3!=null && vals3.size()>=1
        		&&vals4!=null && vals4.size()>=1
        		&&vals5!=null && vals5.size()>=1&&vals6!=null && vals6.size()>=1
        		)
        {
        	int error = checkSign(params);
        	if(error>0)
				return "checkSign fail";
			
        	epCode =  vals.get(0);
        	time = (short)(Integer.parseInt(vals3.get(0)));
        	epGunNo = Integer.parseInt(vals2.get(0));
        	phone = vals1.get(0);
        	redo = Integer.parseInt(vals4.get(0));
        	
        	bespNo= vals5.get(0);
        	if(redo==1 && (bespNo.compareTo("0")==0))
        		return "预约编号空";
        	
        	pwMd5 = vals6.get(0);
        	
        	paramValid = true;
        }
        if(!paramValid)
        	 return "paramValid is invalid";
       
        if(epCode.length() != YXCConstants.LEN_CODE )
        {
        	return "paramValid is invalid";
        }
        ElectricPileCache epClient= EpService.getEpByCode(epCode);
		if(	epClient ==null )
		{
			return "not find epClient";
		}
		EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
	    if(commClient==null || !commClient.isComm())
	    {
	    	return " fail,epClient comm close!";
		  
		
	    }
        
	    UserRealInfo userRealInfo =  UserService.findUserRealInfo(phone);
    	if(userRealInfo==null)
		        return "无效用户";
	    if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
		        return "无效用户";
       
   		
        EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
        if(epGunCache==null)
        {
        	logger.info("not find EpGunCache!epCode:{},epGunNo:{}",epCode, epGunNo);
        	return "not find EpGunCache";
        	
        }
        
	    
	    int epid = epGunCache.getPkEpId();
	    int gunid= epGunCache.getPkEpGunId();
	     
	    int useid=0;
		
			
		UserCache u= UserService.getUserCache(phone);
		if(null != u)
		{
			useid = u.getId();
		}
		else
		{
			return "无效用户";
		}
		
		byte[] bcdAccountNo = WmIce104Util.str2Bcd(phone);
		
		String CardNo = new String("1234567891234567");// 充电卡号
		String CarCardNo = new String("1234567891234567");// 车牌号
		
		Date date = new Date();
		if(bespNo.compareTo("0")==0)
		{
			bespNo= String.valueOf(date.getTime()/1000);
		bespNo = bespNo.substring(2,bespNo.length());
		bespNo += (int)(Math.random()*9000+1000);
		}
		
		byte[] start_time = WmIce104Util.getP56Time2a();
		
		logger.info("bespoke2 accept from tool,accountId:{},buyOutTime:{},epCode:{},epGunNo:{} ",
	    		new Object[]{useid,time,epCode,epGunNo});
		
		byte[] cmdTimes = WmIce104Util.timeToByte();
		byte[] orderdata = EpEncoder.do_bespoke(
				WmIce104Util.str2Bcd(epCode), bespNo, (byte)epGunNo, (byte)redo,
				bcdAccountNo, WmIce104Util.str2Bcd(CardNo), start_time,
				time, StringUtil.repeat("0", 16).getBytes());

		EpMessageSender.sendMessage(commClient,  0, 0,Iec104Constant.C_BESPOKE, orderdata,cmdTimes,commClient.getVersion());
	
		return "send bespoke cmd success!";
	}
	
	
	
	static public String testStopBespoke(Map<String, List<String>> params)
	{
		String pwMd5=null;
		boolean paramValid=false;
		if (params.isEmpty())
	    {
	    	return "params.isEmpty()";
	    }
	    List<String> vals = params.get("code");
	    List<String> vals1 = params.get("gunno");
	    List<String> vals2 = params.get("psWord");
	   
	    String epCode = null;
	    int epGunNo =0;
	   
	    if(vals!=null && vals.size()>=1 
	    		&& vals1!=null && vals1.size()>=1
	    		&& vals2!=null && vals2.size()>=1)
	    {
	    	epCode =  vals.get(0);
	    	epGunNo =  Integer.parseInt(vals1.get(0));
	    	pwMd5 = vals2.get(0);
	    	paramValid=true;
	    }
	    if(!paramValid)
	    {
	    	 return "paramValid is invalid";
	    	
	    }
	    if(epCode.length() != YXCConstants.LEN_CODE )
	    {
	    	return "paramValid is invalid";
	    }
	    int error = checkSign(params);
	    if(error>0)
			return "checkSign fail";
		
		EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
        if(epGunCache==null)
        {
        	logger.info("cancelbespoke accept from tool fail, not find EpGunCache!epCode:{},epGunNo:{}",epCode, epGunNo);
        	return "not find EpGunCache";
        	
        }
		
    	BespCache bespCacheObj =epGunCache.getBespCache();
    	int errorCode = 0;
    	if(bespCacheObj != null)
    	{
    		 UserRealInfo userRealInfo =  UserService.findUserRealInfo(bespCacheObj.getAccount());
    		 if(userRealInfo==null)
    				 return "无效用户";
    		 if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
    				 return "无效用户";
    			
    		errorCode = EpBespokeService.apiStopBespoke(bespCacheObj.getBespId(), bespCacheObj.getBespNo(), epCode,1,99,"");
    	}
    	else
    		return "bespCacheObj is null";
    	
    	if(errorCode>0)
    	{
    		return "errorCode:"+ errorCode;
    	}
    	 logger.info("cancelbespoke accept from tool,bespno:{}",bespCacheObj.getBespNo());
    	 
    	return "send stopBespoke cmd success!";
	   
	}
	
	static public String testStopBespoke2( Map<String, List<String>> params)
	{
	
		boolean paramValid=false;
		if (params.isEmpty())
	    {
	    	return "params.isEmpty()";
	    }
	    List<String> vals = params.get("code");
	    List<String> vals1 = params.get("gunno");
	    List<String> vals2 = params.get("bespno");
	   
	    String epCode = null;
	    int epGunNo =0;
	    
	    String bespNo = null;
	   
	    if(vals!=null && vals.size()>=1 
	    		&& vals1!=null && vals1.size()>=1
	    		&& vals2!=null&& vals2.size()>=1)
	    {
	    	epCode =  vals.get(0);
	    	epGunNo =  Integer.parseInt(vals1.get(0));
	    	bespNo =  vals2.get(0);
	    	
	    	paramValid=true;
	    }
	    if(!paramValid)
	    {
	    	 return "paramValid is invalid";
	    	
	    }
	    if(epCode.length() != YXCConstants.LEN_CODE )
	    {
	    	return "paramValid is invalid";
	    }
	    int error = checkSign(params);
	    if(error>0)
			return "checkSign fail";
	    
	    
	    ElectricPileCache epClient= EpService.getEpByCode(epCode);
		if(	epClient ==null )
		{
			return "not find epClient";
		}
		EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
	    if(commClient==null || !commClient.isComm())
	    {
	    	return " fail,epClient comm close!";
		  
		
	    }
		
		EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
        if(epGunCache==null)
        {
        	logger.error("cancelbespoke2 accept from tool fail, not find EpGunCache!epCode:{},epGunNo:{}",epCode, epGunNo);
        	return "not find EpGunCache";
        	
        }
      
        byte[] sendMsg = EpEncoder.do_cancel_bespoke(epCode, epGunNo,bespNo);
        byte[] cmdTimes = WmIce104Util.timeToByte();
        EpMessageSender.sendMessage(commClient, 0, 0, Iec104Constant.C_CANCEL_BESPOKE, sendMsg,cmdTimes,commClient.getVersion());
    	
        logger.info("cancelbespoke2 accept from tool,bespno:{}",bespNo);

        return "send stopbespoke cmd success!";
	   
	}
	
	static public String testDropCarPlace(Map<String, List<String>> params)
	{
	
		boolean paramValid=false;
		if (params.isEmpty())
	    {
	    	return "params.isEmpty()";
	    }
	    List<String> vals = params.get("code");
	    List<String> vals1 = params.get("gunno");
	    List<String> vals2 = params.get("account");
	   
	    String epCode = null;
	    int epGunNo =0;
	    String account="";
	   
	    if(vals!=null && vals.size()>=1 && vals1!=null && vals1.size()>=1&& vals2!=null && vals2.size()>=1)
	    {
	    	epCode =  vals.get(0);
	    	epGunNo =  Integer.parseInt(vals1.get(0));
	    	account = vals2.get(0);
	    	paramValid=true;
	    }
	    if(!paramValid)
	    {
	    	 return "paramValid is invalid";
	    	
	    }
	    if(epCode.length() != YXCConstants.LEN_CODE )
	    {
	    	return "paramValid is invalid";
	    }
	    
	    int error = checkSign(params);
	    if(error>0)
			return "checkSign fail";
	    
	    UserCache  userCache = UserService.getUserCache(account);
		if(userCache==null)
		{
			return "account is not exsist";
			
		}
	    
	    
	    
	    int errorCode = EpGunService.dropCarPlaceLockAction(epCode, epGunNo,userCache.getId(),(float)0.0,(float)0.0);
	    
	    return "errorCode:"+ errorCode;
	}
	static public String testCallEp(Map<String, List<String>> params)
	{
	
		boolean paramValid=false;
		if (params.isEmpty())
	    {
	    	return "params.isEmpty()";
	    }
	    List<String> vals = params.get("code");
	   
	    List<String> vals1 = params.get("type");
	    List<String> vals2 = params.get("time");
	    List<String> vals3 = params.get("account");
	   
	    String epCode = null;
	    int type =0;
	    int time =0;
	    String account="";
	   
	    if(vals!=null && vals.size()>=1 
	    		&& vals1!=null && vals1.size()>=1
	    		&& vals2!=null && vals2.size()>=1
	    		&& vals3!=null && vals3.size()>=1)
	    {
	    	epCode =  vals.get(0);
	    	type =  Integer.parseInt(vals1.get(0));
	    	time =  Integer.parseInt(vals2.get(0));
	    	account =  vals3.get(0);
	    	paramValid=true;
	    }
	    if(!paramValid)
	    {
	    	 return "paramValid is invalid1";
	    	
	    }
	    if(epCode.length() != YXCConstants.LEN_CODE )
	    {
	    	return "paramValid is invalid,epCode.length() != YXCConstants.LEN_CODE";
	    }
	    int error = checkSign(params);
	    if(error>0)
			return "checkSign fail";
		
	    UserCache u = UserService.getUserCache( account );
		
		if(u == null)
			return "无效用户";
		
	 
		int errorCode =EpService.doNearCallEpAction(epCode,type,time,u.getId(),(float)0.0,(float)0.0);
		 return "errorCode:"+ errorCode;
	}
	
	static public String testStartCharge( Map<String, List<String>> params)
	{
		boolean paramValid=false;
    	int epGunNo=-1;
    	String epCode=null;
    	String account = null;
    	String pwMd5=null;
    	int frozenAmt= 12;
    	int chargetype=1;
    	int payMode=1;
        if (params.isEmpty())
        	return "params.isEmpty()";
       
    	List<String> vals = params.get("code");
    	List<String> vals2 = params.get("gunno");
    	List<String> vals3 = params.get("account");
    	List<String> vals4 = params.get("chargetype");
    	List<String> vals5 = params.get("froneamt");
    	List<String> vals6 = params.get("payMode");
    	List<String> vals7 = params.get("psWord");
    	
    	if(vals!=null && vals.size()>=1
    			&&vals2!=null && vals2.size()>=1
    			&&vals3!=null && vals3.size()>=1
    			&&vals4!=null && vals4.size()>=1
    			&&vals5!=null && vals5.size()>=1
    			&&vals6!=null && vals6.size()>=1
    			&&vals7!=null && vals7.size()>=1)
    	{
    		epCode = vals.get(0);	
    		epGunNo = Integer.parseInt(vals2.get(0));
    		account = vals3.get(0);
    		chargetype = Integer.parseInt(vals4.get(0));
    		
    		double dFronzeAmt =  Double.parseDouble(vals5.get(0));
    		
    		BigDecimal bdFronzeAmt = new BigDecimal(dFronzeAmt);
    		bdFronzeAmt = bdFronzeAmt.multiply(Global.DecTime2);
    		frozenAmt = NumUtil.BigDecimal2ToInt(bdFronzeAmt);
    	
    		payMode =Integer.parseInt(vals6.get(0));
    		pwMd5 = vals7.get(0);
    		paramValid = true;
    	}
        
       if(paramValid && epGunNo>=0 && epCode!=null   && account!=null && pwMd5!=null)
       {
    	   int error = checkSign(params);
    	   if(error>0)
				return "checkSign fail";
			
       		UserRealInfo userRealInfo =  UserService.findUserRealInfo(account);
    		if(userRealInfo==null)
    			return "无效用户";
    		if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
    			return "无效用户";
    		
             UserCache u = UserService.getUserCache( account );
       		
       		if(u == null)
       			return "无效用户";
   		
   		
       		
       		logger.debug("receivce start charge (from test tools) epCode:{},epGunNo:{},account:{} ",
       				new Object[]{epCode,epGunNo,account});
       		byte[] cmdTimes = WmIce104Util.timeToByte();
       		int errorCode = EpChargeService.apiStartElectric(epCode, epGunNo, u,"",null, StringUtil.repeat("0", 12), (short)chargetype, 
       				frozenAmt,payMode,1000,1,"","",cmdTimes);
       		if(errorCode>0)
       		{
       			return "errorCode:"+ errorCode;
       		}
       		logger.info("start charge success(from test tools) epCode:{},epGunNo:{},account:{} ",
       				new Object[]{epCode,epGunNo,account});
       		
       		return "errorCode=0";
       }
       return "params invalid";
		
	}
	static public String testStartCharge2( Map<String, List<String>> params)
	{
		boolean paramValid=false;
    	int epGunNo=-1;
    	String epCode=null;
    	String account = null;
    	String pwMd5=null;
        if (params.isEmpty())
        	return "params.isEmpty()";
       
    	List<String> vals = params.get("code");
    	List<String> vals2 = params.get("gunno");
    	List<String> vals3 = params.get("account");
    	List<String> vals4 = params.get("psWord");
    	if(vals!=null && vals.size()>=1
    			&&vals2!=null && vals2.size()>=1
    			&&vals3!=null && vals3.size()>=1
    			&&vals4!=null && vals4.size()>=1)
    	{
    		epCode = vals.get(0);	
    		epGunNo = Integer.parseInt(vals2.get(0));
    		account = vals3.get(0);
    		pwMd5 = vals4.get(0);
    		paramValid = true;
    	}
        
       if(paramValid && epGunNo>=0 && epCode!=null   && account!=null && pwMd5!=null)
       {
    	   int error = checkSign(params);
    	   if(error>0)
				return "checkSign fail";
			
    	   UserRealInfo userRealInfo =  UserService.findUserRealInfo(account);
   	    	if(userRealInfo==null)
   			        return "无效用户";
   		    if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
   			        return "无效用户";
   		
    	   UserCache u = UserService.getUserCache( account );
       		
       		if(u == null)
       			return "无效用户";
   		
   		
       	   ElectricPileCache epClient= EpService.getEpByCode(epCode);
 		  if(epClient ==null )
 		  {
 			return "not find epClient";
 		  }
 		 EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
 	       if(commClient==null || !commClient.isComm())
 	       {
 	    	   return " fail,epClient comm close!";
 		   }
 	    
       		double frozenAmt= 12.0;
       		
       		String serialNo = epCode + EpChargeService.makeSerialNo();

           Map<String,Object> map =  new HashMap<String,Object>();
           map.put("cpyId", userRealInfo.getCpyId());
           map.put("levelId", userRealInfo.getLevelId());
           map.put("pkEpId", epClient.getPkEpId());
           int rateInfoId = DB.rateInfoDao.findPerRateId(map);
           int modelId = 3;
           if (rateInfoId == 0) {
               modelId = 1;
               rateInfoId = epClient.getRateid();
           }
           RateInfoCache rateInfo = RateService.getRateById(rateInfoId);

           if(rateInfo == null || (modelId == 3 && rateInfo.getRateInfo().getModelId() != 3) || (modelId == 1 && rateInfo.getRateInfo().getModelId() == 3))
           {
               return "not find RateId";
           }

       		byte[] data = null;
			int commVer = commClient.getVersion();
			if(commVer<YXCConstants.PROTOCOL_VERSION_V4)
			{
				data = EpEncoder.do_start_electricize(epCode, (byte)epGunNo, 
	       				account, 0, (byte)1,1200,1,WmIce104Util.MD5Encode("123456".getBytes()),serialNo,rateInfo);
			}
			else
			{
				data = EpEncoder.do_start_electricize_v4(epCode, (byte)epGunNo, 
	       				account, 0, (byte)1,1200,1,WmIce104Util.MD5Encode("123456".getBytes()),serialNo

						,(byte)0,0,0,0,0,0);
			}
			if(data==null)
			{
				return "startCharge do_start_electricize error!";
			}
			
			EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
	        if(epGunCache==null)
	        {
	        	logger.info("start2 charge fail(from test tools),epCode:{},epGunNo:{} not find EpGunCache",epCode, epGunNo);
	        	return "not find EpGunCache";
	        	
	        }
	        byte[] cmdTimes = WmIce104Util.timeToByte();
			EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_START_ELECTRICIZE, data,cmdTimes,commVer);	
       		

	 	      logger.info("start2 charge send success(from test tools),epCode:{},epGunNo:{},account:{} success",
	     				new Object[]{epCode,epGunNo,account});
       		
       		return "send startCharge cmd success!";
       }
       return "params invalid";
		
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
	
	public static String testStopCharge( Map<String, List<String>> params)
	{
		boolean paramValid = false;
    	int epGunNo=-1;
    	String epCode=null;
    	String apiClientIp="10.9.2.107";
    	String account="";
    	String pwMd5=null;
    	
        if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	List<String> vals = params.get("code");
    	List<String> vals2 = params.get("gunno");
    	List<String> vals3 = params.get("account");
    	List<String> vals4= params.get("psWord");

    	if(vals!=null && vals.size()>=1
    			&&vals2!=null && vals2.size()>=1
    			&&vals3!=null && vals3.size()>=1
    			&&vals4!=null && vals4.size()>=1)
    	{
    		epCode = vals.get(0);
    		epGunNo = Integer.parseInt(vals2.get(0));
    		account = vals3.get(0);
    		paramValid = true;
    		pwMd5 = vals4.get(0);
    	}
        
        if(!paramValid)
        	return "params invalid";
        
        int error = checkSign(params);
        if(error>0)
			return "checkSign fail";
        
        UserRealInfo userRealInfo =  UserService.findUserRealInfo(account);
	    if(userRealInfo==null)
			  return "无效用户";
		if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
			  return "无效用户";
        
    	UserCache u = UserService.getUserCache( account );
		
		if(u == null)
			return "无效用户";
		
    	int errorCode = EpChargeService.apiStopElectric(epCode, epGunNo,  UserConstants.ORG_I_CHARGE,u.getId(),u.getAccount(),0,apiClientIp);
    	if(errorCode>0)
		{
			return "errorCode:"+ errorCode;
		}
    	logger.info("stop charge send success(from test tool) ,epCode:{},epGunNo:{},account:{} ",
    			new Object[]{epCode,epGunNo,account});
    	
		return "send stopCharge cmd success!";
        
	}
	
	public static String testStopCharge2( Map<String, List<String>> params)
	{
		boolean paramValid = false;
    	int epGunNo=-1;
    	String epCode=null;
    	String apiClientIp="10.9.2.107";
    	String account="";
    	String pwMd5=null;
    	
        if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	List<String> vals = params.get("code");
    	List<String> vals2 = params.get("gunno");
    	List<String> vals3 = params.get("account");
    	List<String> vals4= params.get("psWord");
    	
    	if(vals!=null && vals.size()>=1
    			&&vals2!=null && vals2.size()>=1
    			&&vals3!=null && vals3.size()>=1
    			&&vals4!=null && vals4.size()>=1)
    	{
    		epCode = vals.get(0);
    		epGunNo = Integer.parseInt(vals2.get(0));
    		account = vals3.get(0);
    		pwMd5 = vals4.get(0);
    		paramValid = true;
    	}
        
        if(!paramValid)
        	return "params invalid";
        
        int error = checkSign(params);
        if(error>0)
			return "checkSign fail";
        
        UserRealInfo userRealInfo =  UserService.findUserRealInfo(account);
	    if(userRealInfo==null)
			  return "无效用户";
		if(userRealInfo.getPassword().compareTo(pwMd5) !=0)
			  return "无效用户";
		
    	UserCache u = UserService.getUserCache( account );
		if(u == null)
			return "无效用户";
		
		
		 ElectricPileCache epClient= EpService.getEpByCode(epCode);
		 if(	epClient ==null )
		 {
				return "not find epClient";
		 }
		 EpCommClient commClient = (EpCommClient)epClient.getEpNetObject();
		 if(commClient==null || !commClient.isComm())
		  {
		    	return " fail,epClient comm close !";
		  }
		    
   		byte[] data= EpEncoder.do_stop_electricize(epCode, (byte)epGunNo);
		if(data == null)
		{
			return "stopcharge do_stop_electricize error!";
			
		}
		
		EpGunCache epGunCache= EpGunService.getEpGunCache(epCode, epGunNo);
        if(epGunCache==null)
        {
        	logger.error("teststopcharge2,epCode:{},epGunNo:{} not find EpGunCache",epCode, epGunNo);
        	return "not find EpGunCache";
        	
        }
        byte[] cmdTimes = WmIce104Util.timeToByte();
        EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_STOP_ELECTRICIZE, data,cmdTimes,commClient.getVersion());
    	
        logger.info("stop charge2 send success(from test tool),epCode:{},epGunNo:{},account:{} success",
    			new Object[]{epCode,epGunNo,account});
        
        return "send stopCharge cmd success!";
        
	}
	
	public static String force_update_ep_hex( Map<String, List<String>> params)
	{
	    boolean paramValid=false;
		
		if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
		
		String  code =  null;
		String  hardCode =  null;
		String  hardver =  null;
        int hardnum=0;
        String softcode = null;
        String softver = null;
        String md5=null;
        int update = 0;
        int stationAddr=0;
		List<String> vals1 = params.get("code");
		List<String> vals2 = params.get("hardcode");
		List<String> vals3 = params.get("hardver");
		List<String> vals4 = params.get("stationaddr");
		List<String> vals5 = params.get("softcode");
		List<String> vals6 = params.get("softver");
		List<String> vals7 = params.get("md5");
		//List<String> vals8 = params.get("update");
		
		if(vals1!=null && vals1.size()>=1 
				&& vals2!=null && vals2.size()>=1
				&& vals3!=null && vals3.size()>=1
				&& vals4!=null && vals4.size()>=1
				&& vals5!=null && vals5.size()>=1
				&& vals6!=null && vals6.size()>=1
				&& vals7!=null && vals7.size()>=1
			//	&& vals8!=null && vals8.size()>=1
				)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			code=vals1.get(0);
			stationAddr=Integer.parseInt(vals4.get(0));
			hardCode=vals2.get(0);
			hardver=vals3.get(0);
			softcode=vals5.get(0);
			softver=vals6.get(0);
			md5=vals7.get(0);
			//update=Integer.parseInt(vals8.get(0));
			
			if(stationAddr==0 && code.compareTo("0")==0)
			{
				return "stationAddr or code invalid";
			}
			   
			BomListInfo bom=new BomListInfo();
	        bom.setSoftNumber(softcode);
		    bom.setSoftVersion(softver);
			bom.setHardwareNumber(hardCode);
			bom.setHardwareVersion(hardver);
			bom.setFileMd5(md5);
			bom.setForceUpdate(1);
			if(bom.splitSoftVersion()>0)
			{
				logger.error("[upgrade],accept Monitor updateHexFile,softVersion error");
				return "softVersion error";
			}
			if(bom.splitHardwareVersion()>0)
			{
				logger.error("[upgrade],accept Monitor updateHexFile,hardwareVersion error");
				return "hardwareVersion error";
			}
			EpCommClient epCommClient = null;
			if(stationAddr>0) 
			{
				EpConcentratorCache stationClient=EpConcentratorService.getConCentrator(""+stationAddr);	
	 	    	 if(stationClient == null )
	 		     {
	 	    		return "not find stationClient";
	 		     }
	 	    	stationClient.getVersionCache().AddBomList(bom.getHardwareNumber()+bom.getHardwareVersion(), bom);
	 	    	epCommClient = (EpCommClient)stationClient.getEpNetObject();
	 	    	code="0000000000000000";
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(code);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				epCommClient =(EpCommClient)epClient.getEpNetObject();
				epClient.getVersionCache().AddBomList(bom.getHardwareNumber()+bom.getHardwareVersion(), bom);
			}
			logger.info("[upgrade],accept Monitor updateHexFile,epCode:{},stationAddr:{}",code,stationAddr);
			 if(code.compareTo("0")==0)
			 {
				 code="0000000000000000";
			 }
			 error = EqVersionService.forceUpdateHexFile(epCommClient,code,stationAddr,bom.getHardwareNumber(),
					         bom.getHardwareM(),bom.getHardwareA(),bom.getSoftNumber()+bom.getSoftVersion());
            if(error>0)
            	return "send forceUpdateHexFile fail!";
			paramValid=true;
		}
		 if(!paramValid)
	        return "params invalid";
		 return "send forceUpdateHexFile cmd success!";
	}
	

	
	
	
	public static String testRateCmd( Map<String, List<String>> params)
	{
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("rateid");
		String epCode = null;
		int rateid = 0;
		if ((vals != null && vals.size() >= 1)
				&& (vals1 != null && vals1.size() >= 1)) {
			epCode = vals.get(0);
			rateid = Integer.parseInt(vals1.get(0));
			paramValid = true;
		}
		if (!paramValid) {
			return "params invalid";
		}
		if (paramValid && epCode != null && rateid > 0) {
			
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			RateInfo rateInfo = DB.rateInfoDao.findRateInfofromId(rateid);
			if (rateInfo == null) {
				return ("费率下发 失败： 数据库中无费率信息" + "rateId:" + rateid + "\n");

			}
			RateInfoCache rateInfoCache = RateService.convertFromDb(rateInfo);
			if (rateInfoCache.parseStage()) 
			{// 如果数据库费率分析成功，那么所有桩都更新.并更新内存
				RateService.AddRate(rateInfo.getId(), rateInfoCache);
				//////////////////////////////
				String str="";
				int errorCode = EpService.updateEpRate(epCode,rateid,rateInfoCache);
		    	if(errorCode>0)
		    		 str=str+"费率下发失败,ep通讯失败" ;
		    	else
		    	{
		    		 str=str+"费率下发完成" ;
		    		 ElectricPileCache  epClient = EpService.getEpByCode(epCode);
		    		 EpService.updateEpRateToDb(epClient.getPkEpId(),rateid);
		    	}
		    	
		    	return str;
			} 
			else
			{// 如果数据库费率分析失败
				return ("费率下发 失败： 数据库费率分析失败" + "rateId:" + rateid + "\n");

			}
		}

		return "费率下发完成";
	}
	
	
	public static String gun_restore( Map<String, List<String>> params)
	{
		boolean paramValid = false;
		ElectricPileCache epClient = null;
		int epGunNo = 0;
		String epCode = null;

		if (params.isEmpty()) {
			return "params.isEmpty()";
		}

		List<String> vals = params.get("code");
		List<String> valsGunNo = params.get("gunno");
		if (vals != null && vals.size() >= 1 && valsGunNo != null
				&& valsGunNo.size() >= 1) {
			epCode = vals.get(0);
			epGunNo = Integer.parseInt(valsGunNo.get(0));
			epClient = EpService.getEpByCode(epCode);
			if(epClient == null)
				return "epClient is null";
			int nGunNum = epClient.getGunNum();
			if (epCode != null
					&& epCode.length() == YXCConstants.LEN_CODE
					&& epGunNo >= 1 && epGunNo <= nGunNum) {
				paramValid = true;
			}
		}
		if (!paramValid) {
			return "params invalid";
		}

		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		if ( epClient != null) {
			int pkEpgunId = 0;// EpGunService.getPkEpGunId(epClient.getPkEpId(),
								// epCode, epGunNo);
			// EpGunService.updateEpGunState(pkEpgunId,
			// epCode,epGunNo,EpConstant.EP_GUN_STATUS_IDLE);
			// 更新内存
			EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
			/*
			 * if(epGunCache !=null) {
			 * epGunCache.setStatus(EpConstant.EP_GUN_STATUS_IDLE); }
			 */
			logger.info("gun_restore epGunNo:{},epCode:{}",epGunNo,epCode);
			return ("gunstore fail,status:" + epGunCache.getStatus());
		} else {
			return ("gunstore gun no params is invalid\n");
		}
	}
	
	
	
	
	public static String openwritelist( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String Value = null;
		List<String> valsCode = params.get("open");
		if (valsCode != null && valsCode.size() >= 1) {
			Value = valsCode.get(0);
			int nOpen = Integer.parseInt(Value);
			if (nOpen == 1)
			{
				MsgWhiteList.setOpen(true);
				return "open !";
			}
			else if (nOpen == 0)
			{
				MsgWhiteList.setOpen(false);
				return "close !";
			}
			
		} 
		return "params invalid";

	}

	public static String removewritelist( Map<String, List<String>> params)
    {
		boolean paramValid = false;
	   
	    if (params.isEmpty()) {
	 	return "params.isEmpty()";
	   }
    	String epCode =  null;
    	List<String> valsCode = params.get("code");
    	if(valsCode!=null && valsCode.size()>=1)
    	{
    		epCode =  valsCode.get(0);
    		MsgWhiteList.removeWhite(epCode);
    		paramValid = true;
    		logger.info("removewritelist,code:{}",epCode);
		}
		if (!paramValid) {
			return "params invalid";
		}
		return "remove success";
    }
	public static String addwritelist( Map<String, List<String>> params)
    {
		boolean paramValid = false;
	    if (params.isEmpty()) {
		 	return "params.isEmpty()";
		   }
    	String epCode =  null;
    	List<String> valsCode = params.get("code");
    	if(valsCode!=null && valsCode.size()>=1)
    	{
    		epCode =  valsCode.get(0);
    		MsgWhiteList.addWhite(epCode);
    		paramValid = true;
		}
		if (!paramValid) {
			return "params invalid";
		}
		return "add success";
    }
	
	public static String getStationDetail(Map<String, List<String>> params)
    {
		boolean paramValid = false;
		int stationaddr = 0;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		List<String> vals = params.get("stationaddr");
		if (vals != null && vals.size() >= 1)
		{
			stationaddr = Integer.parseInt(vals.get(0));
			paramValid = true;

		}
		if (!paramValid) {
			return "params invalid";
		}
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		if(stationaddr>0) 
		{
			EpConcentratorCache stationClient=EpConcentratorService.getConCentrator(""+stationaddr);	
 	    	 if(stationClient != null )
 		     {
 	    		String stationDetails = stationClient.toString();
			     return (stationDetails);
 		     }
		}
		return ("not find station");
		

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
		
		ElectricPileCache epClient= EpService.getEpByCode(epCode);
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
		
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if (epGunCache != null) {
			String gunDetails = epGunCache.toString();
			return (gunDetails);
		} else {
			String value = MessageFormat.format("getgundetail! not find,epCode:{0}, epGunNo:{1}\n", epCode,
					epGunNo);
			return (value);
		}

	}
	public static String removeCharge( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		List<String> valsCode = params.get("code");
		List<String> valsGunNo = params.get("gunno");
		if (valsCode != null && valsCode.size() >= 1 && valsGunNo != null
				&& valsGunNo.size() >= 1) {
			
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode = valsCode.get(0);
			int epGunNo = 0;
			String sEpGunNo = valsGunNo.get(0);
			epGunNo = Integer.parseInt(sEpGunNo);

			String resp = EpChargeService.forceRemoveCharge(epCode, epGunNo);
			return (resp);
		}

		return "params invalid";
	}
	public static String removeBespoke( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		List<String> valsCode = params.get("code");
		List<String> valsGunNo = params.get("gunno");
		if (valsCode != null && valsCode.size() >= 1 && valsGunNo != null
				&& valsGunNo.size() >= 1) {
			
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode = valsCode.get(0);
			int epGunNo = 0;
			String sEpGunNo = valsGunNo.get(0);
			epGunNo = Integer.parseInt(sEpGunNo);

			String resp = EpChargeService.forceRemoveBespoke(epCode, epGunNo);
			return (resp);
		}

		return "params invalid";
	}
	public static String testCardAuth( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String cardInnerNo = "";
		
		List<String> vals = params.get("cardInnerNo");
		
		if(vals!=null && vals.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			cardInnerNo =  vals.get(0);
			String epCode = "3301021010000008";
			//UserService.handleUserCardAuth(null,epCode,1,cardInnerNo,"1",5);
			
			
			ChargingInfo charingInfo = new ChargingInfo();
			
			
			int totalTime = 2;
			charingInfo.setTotalTime(totalTime);
			charingInfo.setChargeMeterNum(1000);
			charingInfo.setFronzeAmt(1000);
			charingInfo.setRateInfo(1000);
			charingInfo.setOutCurrent(100);
			charingInfo.setOutVol(23000);
			charingInfo.setChargeAmt(200);
			charingInfo.setSoc(0);
			charingInfo.setDeviceStatus(0);
			charingInfo.setWarns(0);
			charingInfo.setWorkStatus(3);
			
			
			if(charingInfo!=null)
			{
				Map<String ,Object> respMap = new ConcurrentHashMap<String, Object>();
				respMap.put("epcode", epCode);
				respMap.put("epgunno", 1);
				
				//ChinaMobileService.onEvent(EventConstant.EVENT_REAL_CHARGING,null,0,0,respMap,(Object)charingInfo);
			}
			
			String chOrCode ="";// EpChargeService.makeChargeOrderNo(123);
			String serialNo = EpChargeService.makeSerialNo();
			
			Map<String, Object> consumeRecordMap = new ConcurrentHashMap<String, Object>();
			logger.info("testCardAuth epGunCache.getChargeCache().getChOrCode():{}",chOrCode);
			consumeRecordMap.put("usrId",123);
			consumeRecordMap.put("orderid",chOrCode);
			consumeRecordMap.put("st",DateUtil.getCurrentSeconds()-1000);
			consumeRecordMap.put("et",DateUtil.getCurrentSeconds());
			consumeRecordMap.put("totalMeterNum",1000);
			consumeRecordMap.put("totalAmt",1000);
			consumeRecordMap.put("serviceAmt",1111);
			consumeRecordMap.put("pkEpId",1);
			consumeRecordMap.put("epCode",epCode);
			consumeRecordMap.put("chargStartEnergy",90000);
			consumeRecordMap.put("chargEndEnergy",99000);
			consumeRecordMap.put("account","wm1234567");
			consumeRecordMap.put("serialNo",epCode+serialNo);
			
			//ChinaMobileService.onEvent(EventConstant.EVENT_CONSUME_RECORD,null,0,0,null,(Object)consumeRecordMap);
			
			paramValid = true;
			
			return "";
		}
		else
		{
		return "params invalid";
		}
    }
	
	public static String get_version( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		short stationaddr =0;
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("stationaddr");
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			stationaddr=(short)Integer.parseInt(vals1.get(0));
			String retString;
			paramValid = true;
			if(stationaddr>0) 
			{
				EpConcentratorCache stationClient=EpConcentratorService.getConCentrator(""+stationaddr);	
	 	    	 if(stationClient == null )
	 		     {
	 	    		return "not find stationClient";
	 		     }
	 	    	retString = stationClient.getVersionCache().verInfoToString();
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(epCode);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				retString = epClient.getVersionCache().verInfoToString();
				
			}
			paramValid = true;
			
			return retString;
		}
		return "params invalid";	
    }
	public static String getBomList( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		short stationaddr =0;
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("stationaddr");
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			stationaddr=(short)Integer.parseInt(vals1.get(0));
			String retString="";
			paramValid = true;
			if(stationaddr>0) 
			{
				EpConcentratorCache stationClient=EpConcentratorService.getConCentrator(""+stationaddr);	
	 	    	 if(stationClient == null )
	 		     {
	 	    		return "not find stationClient";
	 		     }
	 	    	retString = stationClient.getVersionCache().bomToString();
				    
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(epCode);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				retString = epClient.getVersionCache().bomToString();
				
			}
			paramValid = true;
			
			return retString;
		}
		return "params invalid";	
    }
	public static String queryVersion( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		short stationaddr =0;
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("stationaddr");
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			stationaddr=(short)Integer.parseInt(vals1.get(0));
			final StringBuilder sb = new StringBuilder();
			paramValid = true;
			EpCommClient  commClient=null;
			
			if(stationaddr>0) 
			{
				epCode="0000000000000000";
				commClient =EpCommClientService.getCommClient(""+stationaddr);
			}
			else
			{
				stationaddr=0;
				ElectricPileCache epClient= EpService.getEpByCode(epCode);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				commClient = epClient.getEpNetObject();
			    if(commClient==null || !commClient.isComm())
			    {
			    	return "queryEpVersion fail,epClient commClient is null!";
			    }
			}
			error = EqVersionService.sendVersion(commClient,epCode,stationaddr);
			if(error==0)
			{
			   paramValid = true;
			   return "send query cmd success!";
			}
			else
			{
				String str = "send query cmd fail,error="+error;
				return str;
			}
		}
		return "params invalid";	
    }
	
	public static String connetMonitor( Map<String, List<String>> params)
    {
		try
		{
			if (params.isEmpty()) {
				return "params.isEmpty()";
			}
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
		   MonitorService.getCommClient().reconnection();
		   
		   return "reconnection action finish!";
		}
		catch (Exception e) {
			logger.error("connetMonitor exception,e.getMessage():{}",e.getMessage());
			
		}
		return "reconnection action fail！";
    }
	
	public static String getMonitorStat( Map<String, List<String>> params)
    {
		try
		{
			if (params.isEmpty()) {
				return "params.isEmpty()";
			}
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			
			return MonitorService.stat();
			
		}
		catch (Exception e) {
			logger.error("getMonitorStat exception,e.getMessage():{}",e.getMessage());
		}
		return "getMonitorStat fail";
    }
	public static String getThirdstat( Map<String, List<String>> params)
    {
		try
		{
			if (params.isEmpty()) {
				return "params.isEmpty()";
			}
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			
			return "";
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "getThirdstat fail";
    }
	public static String getUsrGatestat( Map<String, List<String>> params)
    {
		try
		{
			if (params.isEmpty()) {
				return "params.isEmpty()";
			}
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			
			return UsrGateService.stat();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "getUsrGatestat fail";
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
	public static String queryCommSignal( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        String epCode = null;
		short stationaddr =0;
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("stationaddr");
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			stationaddr=(short)Integer.parseInt(vals1.get(0));
			final StringBuilder sb = new StringBuilder();
			paramValid = true;
			if(stationaddr>0) 
			{
	 	    	 EpCommClient  commClient = EpCommClientService.getCommClient(""+stationaddr);
	 		    if(commClient == null)
	 		    {
	 		    	return "queryCommSignal fail,station commClient is null!";
	 				
	 		    }
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(epCode);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				ITcpClient commClient = epClient.getEpNetObject();
			    if(commClient==null || !commClient.isComm())
			    {
			    	return "queryCommSignal fail,epClient commClient is null!";
				  
				
			    }
			    EpService.queryCommSignal(epCode,(short)0);
			}
			paramValid = true;
			
			return "send query Comm Signal success!";
		}
		return "params invalid";
    }
	public static String queryConsumeRecord( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        String epCode = null;
		int enteryNum=0;
		int startPos=0;
		
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("startPos");
		List<String> vals2 = params.get("startNum");
		
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1
				&& vals2!=null && vals2.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			startPos=Integer.parseInt(vals1.get(0));
			enteryNum=Integer.parseInt(vals2.get(0));
			
			error = EpService.queryConsumeRecord(epCode, startPos, enteryNum);
			
			if(error>0)
			{
				String str = "error:"+error;
				return str;
			}
			paramValid = true;
			
			return "send queryConsumeRecord success!";
		}
		return "params invalid";
    }
	public static String getRatebyId( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
		int rateid =0;
		
		List<String> vals = params.get("rateid");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			rateid=Integer.parseInt(vals.get(0));
			
			RateInfoCache rateInfo = RateService.getRateById(rateid);
			if(rateInfo ==null)
				return "not find rateInfo !";
			
			paramValid = true;
			
			return rateInfo.toString();
		}
		return "params invalid";
    }
	public static String getRealData( Map<String, List<String>> params)
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
		
		EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
		if (epGunCache != null) {
			RealChargeInfo realInfo = epGunCache.getRealChargeInfo();
			if(realInfo !=null)
			{
			   String realString = realInfo.toString();
			   return (realString);
			}
			else
				return "RealChargeInfo is null";
		} else {
			String value = MessageFormat.format("getRealData! not find,epCode:{0}, epGunNo:{1}\n", epCode,
					epGunNo);
			return (value);
		}

    }
	public static String getGameConfig( Map<String, List<String>> params)
    {
		try
		{
			if (params.isEmpty()) {
				return "params.isEmpty()";
			}
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			
			return GameConfig.getString();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "getGameConfig fail";

    }
	
	public static String createIdentyCode( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        String epCode = null;
		int gunno =0;
		
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("gunno");
		
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			gunno=Integer.parseInt(vals1.get(0));
			EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, gunno);
			if (epGunCache == null) 
			{
				return "epGunCache is null, send fail!";
			}
			EpCommClient commClient= (EpCommClient)epGunCache.getEpNetObject();
			EpService.handleEpIdentyCodeQuery(commClient,epCode, gunno,(byte)0,(byte)0,(byte)0);
	
			if(error>0)
			{
				String str = "error:"+error;
				return str;
			}
			paramValid = true;
			
			return "createIdentyCode success!";
		}
		return "params invalid";
		
    }
	
	public static String getLastConsumeRecord( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        String epCode = null;
		int gunno =0;
		
		List<String> vals = params.get("code");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			char[] buf = new char[1024];
			String fileName =epCode + ".log";
			int leng = FileUtils.readLog(fileName, buf);
			if(leng==0)
				return "no find";
			final StringBuilder sb = new StringBuilder();
			sb.append(buf);
			
			return sb.toString();
		}
		return "params invalid";
		
    }
	
	
	public static String getConcentratorConfig( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        int stationaddr = 0;
		List<String> vals = params.get("stationaddr");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			stationaddr = Integer.parseInt(vals.get(0));
			
			EpCommClient  commClient = EpCommClientService.getCommClient(""+stationaddr);
 		    if(commClient == null || !commClient.isComm())
 		    {
 		    	return "queryConcentratorConfig fail,station commClient is close!";
 				
 		    }
 		   byte []cmdTimes = WmIce104Util.timeToByte();
 			
 			byte[] statData= EpEncoder.do_concentroter_getep((short)stationaddr);
 			EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_CONCENTROTER_GET_EP, statData,cmdTimes,commClient.getVersion());
			return "send query ConcentratorConfig cmd success!";
		}
		return "params invalid";
    }
	
	public static String getRateFromEp( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        String  epCode = null;
		List<String> vals = params.get("code");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode = vals.get(0);
			ElectricPileCache epClient= EpService.getEpByCode(epCode);
			if(	epClient ==null )
			{
				return "not find epClient";
			}
			EpCommClient commClient =(EpCommClient)epClient.getEpNetObject();
		    if(commClient==null || !commClient.isComm())
		    {
		    	return "queryRateInfo fail,epClient commClient is close!";
		    }
 		   byte []cmdTimes = WmIce104Util.timeToByte();
 			
 			byte[] statData= EpEncoder.do_query_ep_rateInfo(epCode);
 			EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_GET_CONSUME_MODEL, statData,cmdTimes,commClient.getVersion());
			return "send query rateInfo cmd success!";
		}
		return "params invalid";
    }
	
	public static String epReSendConfig( Map<String, List<String>> params)
    {
		if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		List<String> vals = params.get("flag");
	    List<String> vals1 = params.get("time");
	    if(vals!=null && vals.size()>=1  && 
	    		vals1!=null && vals1.size()>=1)
	   {
    	   int flag = Integer.parseInt(vals.get(0));
		   int time = Integer.parseInt(vals1.get(0));
		   GameConfig.resendEpMsgFlag = flag;
		   GameConfig.resendEpMsgTime = time;
		   String str = "GameConfig.resendEpMsgFlag="+GameConfig.resendEpMsgFlag
				   +"\nGameConfig.resendEpMsgTime="+GameConfig.resendEpMsgTime;
		   return str;
	   }
	    return "params invalid";
			 
	    
	    
    }
	public static String getCacheSize( Map<String, List<String>> params)
    {
		if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        
		int error = checkSign(params);
		if(error>0)
			return "checkSign fail";
		
		final StringBuilder sb = new StringBuilder();
		
		String stationStatMsg =  EpConcentratorService.getCacheSize();
	
		String epCommClientStatMsg =  EpCommClientService.getCacheSize();
		      
		String usrGateStatMsg = UsrGateService.getCacheSize();
		String epStatMsg =  EpService.getCacheSize();
		
		String gunStatMsg =  EpGunService.getCacheSize();
	
		
		String apiStatMsg =  AppClientService.getCacheSize();

		sb.append(epCommClientStatMsg).append(stationStatMsg).append(epStatMsg).append(gunStatMsg).append(apiStatMsg);
		
		return sb.toString();
    }
	
	public static String queryFlashRam( Map<String, List<String>> params)
    {
		boolean paramValid = false;
	    if (params.isEmpty()) {
    	    return "params.isEmpty()";
       }
        String  epCode = null;
        int stationAddr=0;
        int type=1;
        int startPos=0;
        int len=0;
	    List<String> vals = params.get("code");
	    List<String> vals1 = params.get("stationaddr");
	    List<String> vals2 = params.get("type");
	    List<String> vals3 = params.get("pos");
	    List<String> vals4 = params.get("leng");
	
	   if(vals!=null && vals.size()>=1 )
	   {
		 
		   int error = checkSign(params);
		   if(error>0)
			   return "checkSign fail";
		   
		   epCode = vals.get(0);
		   stationAddr = Integer.parseInt(vals1.get(0));
		   type = Integer.parseInt(vals2.get(0));
		   startPos = Integer.parseInt(vals3.get(0));
		   len = Integer.parseInt(vals4.get(0));
		   EpCommClient  commClient=null;
		   if(stationAddr>0) 
			{
			   commClient  = EpCommClientService.getCommClient(""+stationAddr);
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(epCode);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				commClient = (EpCommClient)epClient.getEpNetObject();
			}
		   if(commClient==null || !commClient.isComm())
		   {
			    return "queryFlashRam fail, commClient is close!";
		   }
		   
		   byte[] cmdTimes = WmIce104Util.timeToByte();
		   byte[] data= EpEncoder.do_get_flashRam(epCode, (short)stationAddr, (short)type, startPos,(short)len);
		   EpMessageSender.sendMessage(commClient,0,0,Iec104Constant.C_GET_FLASH_RAM, data,cmdTimes,commClient.getVersion());
		   return "send queryFlashRam cmd success!";
	   }
	   return "params invalid";
    }
	
	public static String setConcentratorConfig( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        int stationaddr = 0;
        String codes=null;
		List<String> vals = params.get("stationaddr");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			stationaddr = Integer.parseInt(vals.get(0));
			
			EpCommClient  commClient = EpCommClientService.getCommClient(""+stationaddr);
 		    if(commClient == null || !commClient.isComm())
 		    {
 		    	return "queryConcentratorConfig fail,station commClient is close!";
 				
 		    } 
 		    EpConcentratorService.concentratorConfig(stationaddr);
 			return "send set ConcentratorConfig cmd success!";
		}
		return "params invalid";
    }
	
	public static String setSMS( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        int sms = 0;
		List<String> vals = params.get("SMS");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			sms = Integer.parseInt(vals.get(0));
			if(sms>=0 && sms<3)
			   GameConfig.sms = sms;
 		   
			String str="GameConfig.sms="+GameConfig.sms;
 			return str;
		}
		return "params invalid";
    }

	public static String setPrintEpMsg( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        int printEpMsg=0;
		List<String> vals = params.get("printEpMsg");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			printEpMsg = Integer.parseInt(vals.get(0));
			
			GameConfig.printEpMsg=printEpMsg;
			
			String str="GameConfig.printEpMsg="+GameConfig.printEpMsg;
			return str;
		}
		return "params invalid";
    }
	
	public static String setPrintPhoneMsg( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
        int printPhoneMsg=0;
        List<String> vals = params.get("printPhoneMsg");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			printPhoneMsg = Integer.parseInt(vals.get(0));
            
			GameConfig.printPhoneMsg=printPhoneMsg;
			
			String str="GameConfig.printPhoneMsg="+GameConfig.printPhoneMsg;
			return str;
		}
		return "params invalid";
    }
	
	public static String setVinCode( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	String vinCode = null;
        String code=null;
        int gunno=0;
		List<String> vals = params.get("carVinCode");
		List<String> vals1 = params.get("code");
		List<String> vals2 = params.get("gunno");
		
		if(vals!=null && vals.size()>=1 &&vals1!=null && vals1.size()>=1
				&&vals2!=null && vals2.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			vinCode = vals.get(0);
			code = vals1.get(0);
			gunno = Integer.parseInt(vals2.get(0));
			ElectricPileCache epCache = EpService.getEpByCode(code);
			if(epCache==null)
			{
				return " setVinCode  fail epCache ==null!";
			}
			if(epCache.getCurrentType() == EpConstants.EP_AC_TYPE)
			{
				return " setVinCode ep is AC";
			}
			EpGunCache epGunCache = EpGunService.getEpGunCache(code, gunno);
			if(epGunCache ==null)
			{
				return " setVinCode  fail epGunCache ==null!";
			}
			GunPointMap gunPointMap = new GunPointMap(code,gunno);
			SingleInfo singleInfo=new SingleInfo();
			singleInfo.setAddress(YXCConstants.YC_VAR_CAR_VIN);
			singleInfo.setStrValue(vinCode);
			gunPointMap.getPointMap().put(YXCConstants.YC_VAR_CAR_VIN, singleInfo);
			
			epGunCache.onRealDataChange(gunPointMap.getPointMap(), 132);
			
 			return " setVinCode  success!";
		}
		return "params invalid";
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
			EpCommClient commClient = EpCommClientService.getCommClient(channel);
			
			String str = commClient.toString();
 			return str;
		}
		return "params invalid";
    }
	public static String getBomListById( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        int id=0;
		List<String> vals = params.get("id");
		
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			id = Integer.parseInt(vals.get(0));
			
			Map<String,BomListInfo> bomMap =  EqVersionService.getBomList(id);
			
			if(bomMap==null || bomMap.size() == 0)
				return "bomList is null";
			final StringBuilder sb = new StringBuilder();
	    	Iterator iter = bomMap.entrySet().iterator();	
	 		while (iter.hasNext()) {

	 			Map.Entry entry = (Map.Entry) iter.next();	 			
	 			BomListInfo ver=(BomListInfo) entry.getValue();
	 	        sb.append("产品id=").append(id).append(":\n\n");
	 	        sb.append("{bomId=").append(ver.getBomListId()).append("\n\n");
	 	        sb.append("硬件编号 = ").append(ver.getHardwareNumber()).append("\n\n");
	 	        sb.append("硬件版本号 = ").append(ver.getHardwareVersion()).append("\n\n");
	 	        sb.append("固件编号 = ").append(ver.getSoftNumber()).append("\n\n");
	 	        sb.append("固件版本号 = ").append(ver.getSoftVersion()).append("\n\n");
	 	        sb.append("强制更新标识=").append(ver.getForceUpdate()).append("}\n\n\n");
	 		 }
	 		 return sb.toString();
		}
		return "params invalid";
    }
	public static String removeUpdate( Map<String, List<String>> params)
    {
		boolean paramValid = false;
    	if (params.isEmpty()) {
        	return "params.isEmpty()";
        }
    	
        String  code=null;
        int stationaddr = 0;
		List<String> vals = params.get("stationaddr");
		List<String> vals1 = params.get("code");
		
		if(vals!=null && vals.size()>=1&& vals1!=null && vals1.size()>=1)
		{
			code = vals1.get(0);
			stationaddr=Integer.parseInt(vals.get(0));
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			if(stationaddr>0) 
			{
				EpConcentratorCache stationCache = EpConcentratorService.getConCentrator(stationaddr);
				if(	stationCache ==null )
				{
					return "not find stationCache";
				}
				stationCache.getVersionCache().removeMapBomList();
			}
			else
			{
				ElectricPileCache epClient= EpService.getEpByCode(code);
				if(	epClient ==null )
				{
					return "not find epClient";
				}
				epClient.getVersionCache().removeMapBomList();	
			}
			
		}
		return "params invalid";
    }
	
	public static String setMaxTempChargeNum( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		int tempChargeNum =0;
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("num");
		if(vals!=null && vals.size()>=1 
				&& vals1!=null && vals1.size()>=1)
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			tempChargeNum=(short)Integer.parseInt(vals1.get(0));
			final StringBuilder sb = new StringBuilder();
			paramValid = true;
			EpCommClient  commClient=null;
			ElectricPileCache epClient= EpService.getEpByCode(epCode);
			if(	epClient ==null )
			{
				return "not find epClient";
			}
			commClient = epClient.getEpNetObject();
			if(commClient==null || !commClient.isComm())
			{
			    return "[tempCharge]setMaxTempChargeNum fail,epClient commClient is null!";
			}
			epClient.setTempChargeMaxNum(tempChargeNum);
			error = EpService.setTempChargeNum(epCode);
			if(error==0)
			{
				logger.info("[tempCharge]setMaxTempChargeNum success,epCode:{},tempChargeNum:{}",
						epCode,tempChargeNum);
			   paramValid = true;
			   return "send query cmd success!";
			}
			else
			{
				String str = "send query cmd fail,error="+error;
				return str;
			}
		}
		return "params invalid";	
    }
	
	
	public static String getMaxTempChargeNum( Map<String, List<String>> params)
    {
		boolean paramValid = false;
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		List<String> vals = params.get("code");
		if(vals!=null && vals.size()>=1 )
		{
			int error = checkSign(params);
			if(error>0)
				return "checkSign fail";
			
			epCode =  vals.get(0);
			final StringBuilder sb = new StringBuilder();
			paramValid = true;
			EpCommClient  commClient=null;
			ElectricPileCache epClient= EpService.getEpByCode(epCode);
			if(	epClient ==null )
			{
				return "not find epClient";
			}
			commClient = epClient.getEpNetObject();
			if(commClient==null || !commClient.isComm())
			{
			    return "getMaxTempChargeNum fail,epClient commClient is null!";
			}
		
			error = EpService.queryTempChargeNum(epCode);
			if(error==0)
			{
				logger.info("[tempCharge]getMaxTempChargeNum success,epCode:{}",
						epCode);
			   paramValid = true;
			   return "send query cmd success!";
			}
			else
			{
				String str = "send query cmd fail,error="+error;
				return str;
			}
		}
		return "params invalid";	
    }
	
	public static String setEPWorkArg( Map<String, List<String>> params)
    {
		if (params.isEmpty()) {
			return "params.isEmpty()";
		}
		String epCode = null;
		
		List<String> vals = params.get("code");
		List<String> vals1 = params.get("num");
		List<String> vals2 = params.get("time");
		List<String> vals3 = params.get("flag");
		if(vals!=null && vals.size()>=1 && (vals1!=null && vals1.size()>=1 || vals2!=null && vals2.size()>=1
				|| vals3!=null && vals3.size()>=1))
		{
			/*int error = checkSign(params);
			if(error>0)
				return "checkSign fail";*/
			
			epCode =  vals.get(0);
	 		ElectricpileWorkarg epWorkarg = new ElectricpileWorkarg();
	 		List<ElectricpileWorkarg> list;
	 		epWorkarg.setEpCode(epCode);
	 		epWorkarg.setIssuedStatus(0);
			if (!vals1.get(0).equals("")) {
				epWorkarg.setArgId(3);
				epWorkarg.setArgValue(vals1.get(0));
		 		list = DB.electricpileWorkargDao.ElectricpileWorkarg_custlist(epWorkarg);
		 		if (list == null || list.size() == 0) {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_insert(epWorkarg);
		 		} else {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
		 		}
			}
			if (!vals2.get(0).equals("")) {
				epWorkarg.setArgId(2);
				epWorkarg.setArgValue(vals2.get(0));
		 		list = DB.electricpileWorkargDao.ElectricpileWorkarg_custlist(epWorkarg);
		 		if (list == null || list.size() == 0) {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_insert(epWorkarg);
		 		} else {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
		 		}
			}
			if (!vals3.get(0).equals("")) {
				epWorkarg.setArgId(1);
				epWorkarg.setArgValue(vals3.get(0));
		 		list = DB.electricpileWorkargDao.ElectricpileWorkarg_custlist(epWorkarg);
		 		if (list == null || list.size() == 0) {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_insert(epWorkarg);
		 		} else {
			 		DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
		 		}
			}
			EpCommClient  commClient=null;
			ElectricPileCache epClient= EpService.getEpByCode(epCode);
			if(	epClient ==null )
			{
				return "not find epClient";
			}
			commClient = epClient.getEpNetObject();
			if(commClient==null || !commClient.isComm())
			{
			    return "epClient commClient is null!";
			}
			EpService.sendWorkArg(epCode);
			logger.info("sendWorkArg success,epCode:{}", epCode);
			return "send query cmd success!";
		}
		return "params invalid";	
    }

    public static String setEPInfoArg(Map<String, List<String>> params) {
        if (params.isEmpty()) {
            return "params.isEmpty()";
        }
        String epCode = null;

        List<String> vals = params.get("code");
        List<String> vals1 = params.get("iccid");
        List<String> vals2 = params.get("imsi");
        if (vals != null && vals.size() >= 1 && (vals1 != null && vals1.size() >= 1 && vals2 != null && vals2.size() >= 1)) {
            logger.info("checkSign is skip");
            epCode = vals.get(0);
            ElectricpileWorkarg epWorkarg = new ElectricpileWorkarg();
            List<ElectricpileWorkarg> list;
            epWorkarg.setEpCode(epCode);
            epWorkarg.setIssuedStatus(0);

            if (!vals1.get(0).equals("")) {
                epWorkarg.setArgId(21);
                epWorkarg.setArgValue(0+"");
                list = DB.electricpileWorkargDao.ElectricpileWorkarg_custlist(epWorkarg);
                if (list == null || list.size() == 0) {
                    DB.electricpileWorkargDao.ElectricpileWorkarg_insert(epWorkarg);
                } else {
                    DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
                }
            }
            if (!vals2.get(0).equals("")) {
                epWorkarg.setArgId(22);
                epWorkarg.setArgValue(0+"");
                list = DB.electricpileWorkargDao.ElectricpileWorkarg_custlist(epWorkarg);
                if (list == null || list.size() == 0) {
                    DB.electricpileWorkargDao.ElectricpileWorkarg_insert(epWorkarg);
                } else {
                    DB.electricpileWorkargDao.ElectricpileWorkarg_update(epWorkarg);
                }
            }

            EpCommClient commClient = null;
            ElectricPileCache epClient = EpService.getEpByCode(epCode);
            if (epClient == null) {
                return "not find epClient";
            }
            commClient = epClient.getEpNetObject();
            if (commClient == null || !commClient.isComm()) {
                return "epClient commClient is null!";
            }
            EpService.sendQueryEpInfo(epCode);
            logger.info("sendQueryEpInfo success,epCode:{}", epCode);
            return "send query cmd success!";

        }
        return "params invalid";
    }
	
}
	
