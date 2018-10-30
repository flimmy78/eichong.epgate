package com.cooperate.TCEC.putian;

/**
 * Created by Administrator on 2018/3/20.
 */
public class CheckTCECPuTianPushTask implements Runnable {

    @Override
    public void run() {
        TCECPuTianService.checkPushTimeout();
    }
}
