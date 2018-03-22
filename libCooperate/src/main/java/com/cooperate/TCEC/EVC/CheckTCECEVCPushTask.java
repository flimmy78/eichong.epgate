package com.cooperate.TCEC.EVC;


public class CheckTCECEVCPushTask implements Runnable {

	@Override
	public void run() {
		TCECEVCService.checkPushTimeout();
	}
}