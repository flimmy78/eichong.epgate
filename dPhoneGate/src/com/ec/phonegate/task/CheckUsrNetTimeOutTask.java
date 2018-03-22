package com.ec.phonegate.task;

import com.ec.phonegate.service.PhoneService;

public class CheckUsrNetTimeOutTask implements Runnable {

	@Override
	public void run() {
		PhoneService.checkTimeOut();
	}
}
