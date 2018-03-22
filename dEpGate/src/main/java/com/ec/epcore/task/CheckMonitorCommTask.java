package com.ec.epcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.service.MonitorService;




public class CheckMonitorCommTask implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(CheckGunTask.class);
	@Override
	public void run() {
        try
		{
	
		MonitorService.checkTimeOut();
		}
		catch(Exception e)
		{
			logger.error("CheckMonitorCommTask exception,getStackTrace:{}",e.getStackTrace());
		}
	}

}
