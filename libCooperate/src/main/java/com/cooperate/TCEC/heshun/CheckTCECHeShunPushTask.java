package com.cooperate.TCEC.heshun;


public class CheckTCECHeShunPushTask implements Runnable {

	@Override
	public void run() {
		TCECHeShunService.checkPushTimeout();
	}
}