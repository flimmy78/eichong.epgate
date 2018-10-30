package com.cooperate.TCEC.EAST;

/**
 * Created by Administrator on 2018/3/20.
 */
public class CheckTCECEASTPushTask implements Runnable {

    @Override
    public void run() {
        TCECEASTService.checkPushTimeout();
    }
}
