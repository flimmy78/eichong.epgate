package com.ec.epcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.service.EpCommClientService;




public class CheckEpCommClientTask  implements Runnable {
    
	private static final Logger logger = LoggerFactory.getLogger(CheckEpCommClientTask.class);
	@Override
	public void run() {
		
		try
		{
			EpCommClientService.checkTimeOut();
		}
		catch(Exception e)
		{
			logger.error("CheckEpCommClientTask exception,getStackTrace:{}",e.getStackTrace());
		}
		
	}
}