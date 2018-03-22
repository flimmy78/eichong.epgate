package com.ec.epcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.service.EpGunService;
import com.ec.epcore.service.NetMessageService;

public class EpMessageTask  implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(EpMessageTask.class);
	@Override
	public void run() {
		try
		{
			NetMessageService.checkEpReSendMessage();
			logger.debug("EpMessageTask checkEpReSendMessage");
		}
		catch(Exception e)
		{
			logger.error("EpMessageTask exception,getStackTrace:{}",e.getStackTrace());
		}
	}

}

