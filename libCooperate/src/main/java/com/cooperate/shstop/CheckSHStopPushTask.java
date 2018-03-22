package com.cooperate.shstop;


public class CheckSHStopPushTask implements Runnable {

	@Override
	public void run() {
		SHStopService.checkSHSTOPPushTimeout();
	}
}