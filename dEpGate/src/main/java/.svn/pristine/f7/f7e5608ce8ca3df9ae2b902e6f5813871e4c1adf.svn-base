/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ec.epcore.net.server;



import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.test.ImitateConsumeService;

public class WatchHttpServerHandler
{
	private static final Logger logger = LoggerFactory.getLogger(WatchHttpServerHandler.class);
	


    /** Buffer that stores the response content */
   
    public static String handleGetMessage( String method,Map<String, List<String>> params) throws  IOException
	{
    	StringBuilder buf = new StringBuilder();

    	switch(method)
        {
        case "/getversion":
        {
        	String retDesc=ImitateConsumeService.get_version(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/connetmonitor":
        {
        	String retDesc=ImitateConsumeService.connetMonitor(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/getmonitorstat":
        {
        	String retDesc=ImitateConsumeService.getMonitorStat(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/getThirdstat":
        {
        	String retDesc=ImitateConsumeService.getThirdstat(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/getUsrGatestat":
        {
        	String retDesc=ImitateConsumeService.getUsrGatestat(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/getBomList":
        {
        	String retDesc=ImitateConsumeService.getBomList(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/queryversion":
        {
        	String retDesc=ImitateConsumeService.queryVersion(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
        case "/force_update_ep_hex":
        {
        	String retDesc=ImitateConsumeService.force_update_ep_hex(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	
        }
        break;
       
        case "/testDropCarPlace":
        {
        	String retDesc=ImitateConsumeService.testDropCarPlace(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        	break;
        case "/testCallEp":
        {
        	String retDesc=ImitateConsumeService.testCallEp(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        	break;
        case "/testRateCmd":
        {  
        	String retDesc=ImitateConsumeService.testRateCmd(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break;
        case "/getRate":
        {  
        	String retDesc=ImitateConsumeService.getRatebyId(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break;
        case "/testStartBespoke":
        {
        	String retDesc=ImitateConsumeService.testStartBespoke(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        	else
        	{
        		buf.append("ssssssssssssssssssssssssssss");
        	}

        }
        break;  
        case "/testStartBespoke2":
        {
        	String retDesc=ImitateConsumeService.testStartBespoke2(params);
        	if(retDesc!=null)
        		buf.append(retDesc);

        }
        break;
        
        case "/testStopBespoke":
        {
        	
        	String retDesc=ImitateConsumeService.testStopBespoke(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break;
        case "/testStopBespoke2":
        {
        	
        	String retDesc=ImitateConsumeService.testStopBespoke2(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break;
        case "/testStartCharge":
        {
        	String retDesc=ImitateConsumeService.testStartCharge(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break; 	
        case "/testStartCharge2":
        {
        	String retDesc=ImitateConsumeService.testStartCharge2(params);
        	if(retDesc!=null)
        		buf.append(retDesc);
        }
        break;
        case "/testStopCharge":
        {
        	
        	String stopDesc=ImitateConsumeService.testStopCharge(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        	
        }
        break;
        case "/testStopCharge2":
        {
        	
        	String stopDesc=ImitateConsumeService.testStopCharge2(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        	
        }
        break;
        case "/user":
        {
        	
        	String stopDesc=ImitateConsumeService.findUser(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        	else
        	{
        		buf.append("ssssssssssssssssssssssssssss");
        	}
        	
        }
        break;
        case "/stat":
        {
        	String stopDesc=ImitateConsumeService.stat(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
       
        }
        break;
        case "/queryCommSignal":
        {
        	String stopDesc=ImitateConsumeService.queryCommSignal(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
       
        }
        break;
        case "/queryConsumeRecord":
        {
        	String stopDesc=ImitateConsumeService.queryConsumeRecord(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
       
        }
        break;
        case "/queryFlashRam":
        {
        	String stopDesc=ImitateConsumeService.queryFlashRam(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
       
        }
        break;
       /* case "/testCardAuth":
        {
        	String authDesc=ImitateConsumeService.testCardAuth(params);
        	if(authDesc!=null)
        		buf.append(authDesc);
        }
        
        break;*/
        case "/createIdentyCode":
        {
        	String stopDesc=ImitateConsumeService.createIdentyCode(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/gun_restore":
        {
        	String stopDesc=ImitateConsumeService.gun_restore(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/getChNum":
        {
        	String stopDesc=ImitateConsumeService.getCacheSize(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/getepdetail":
        {
        	String stopDesc=ImitateConsumeService.getEpDetail(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/getstationdetail":
        {
        	String stopDesc=ImitateConsumeService.getStationDetail(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/getReal":
        {
        	String stopDesc=ImitateConsumeService.getRealData(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/getGameConfig":
        {
        	String stopDesc=ImitateConsumeService.getGameConfig(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/getLastConsumeRecord":
        {
        	String stopDesc=ImitateConsumeService.getLastConsumeRecord(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/setConcentratorConfig":
        {
        	String stopDesc=ImitateConsumeService.setConcentratorConfig(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
		case "/setSMS":
        {
        	String stopDesc=ImitateConsumeService.setSMS(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/queryConcentratorConfig":
        {
        	String stopDesc=ImitateConsumeService.getConcentratorConfig(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/queryRate":
        {
        	String stopDesc=ImitateConsumeService.getRateFromEp(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/epReSend":
        {
        	String stopDesc=ImitateConsumeService.epReSendConfig(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        
        case "/getgundetail":
        {
        	String stopDesc=ImitateConsumeService.getgundetail(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	
        break;
        case "/removeCharge":
        {
        	String stopDesc=ImitateConsumeService.removeCharge(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/removebesp":
        {
        	String stopDesc=ImitateConsumeService.removeBespoke(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	break;
        case "/cleanuser":
        {
        	String stopDesc=ImitateConsumeService.cleanUser(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        	break;
        case "/addwritelist":
        {
        	String stopDesc=ImitateConsumeService.addwritelist(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/removewritelist":
        {
        	String stopDesc=ImitateConsumeService.removewritelist(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/openwritelist":
        {
        	String stopDesc=ImitateConsumeService.openwritelist(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/setVinCode":
        {
        	String stopDesc=ImitateConsumeService.setVinCode(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/getClientCommByChannel":
        {
        	String stopDesc=ImitateConsumeService.getClientCommByChannel(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
		case "/setPrintEpMsg":
        {
        	String stopDesc=ImitateConsumeService.setPrintEpMsg(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/setPrintPhoneMsg":
        {
        	String stopDesc=ImitateConsumeService.setPrintPhoneMsg(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/getbomListById":
        {
        	String stopDesc=ImitateConsumeService.getBomListById(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/removeUpdate":
        {
        	String stopDesc=ImitateConsumeService.removeUpdate(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/setMaxTempChargeNum":
        {
        	String stopDesc=ImitateConsumeService.setMaxTempChargeNum(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/getMaxTempChargeNum":
        {
        	String stopDesc=ImitateConsumeService.getMaxTempChargeNum(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/setEPWorkArg":
        {
        	String stopDesc=ImitateConsumeService.setEPWorkArg(params);
        	if(stopDesc!=null)
        		buf.append(stopDesc);
        }
        break;
        case "/setEPInfoArg":
        {
            String stopDesc=ImitateConsumeService.setEPInfoArg(params);
            if(stopDesc!=null)
                buf.append(stopDesc);
        }
        break;
        default:
        	break;
        
        };
        
        return buf.toString();
	}
    
    public static String handlePostMessage(String method,HashMap<String,Object> params) throws  IOException
   	{
    	return "";
   	}
}
