package com.ec.epcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.service.EpService;
import com.ec.utils.LogUtil;

public class CheckMeterTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CheckMeterTask.class);

	@Override
	public void run() {

		try {
			EpService.checkTimeout();
		} catch (Exception e) {
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
		}

	}

}
