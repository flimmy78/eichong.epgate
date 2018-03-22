package com.ec.usrcore.task;

import com.ec.usrcore.service.EpGateService;

public class ScanEpGateTask implements Runnable {

	@Override
	public void run() {
		EpGateService.scanEpGate();
	}
}