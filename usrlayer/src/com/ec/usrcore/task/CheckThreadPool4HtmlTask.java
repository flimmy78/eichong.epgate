/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ec.usrcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ec.usrcore.net.client.EpGateMessageHandler.bizExecutor4Html;
import static com.ec.usrcore.net.client.EpGateMessageHandler.workQueue4HtmlBizExecutor;


/**
 * 打印bizExecutorService线程池情况
 * @author ${zhuhuiming}
 * @version $Id: CheckThreadPoolTask.java, v 0.1 2018年07月30日 14:32 Exp $
 */
public class CheckThreadPool4HtmlTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CheckThreadPool4HtmlTask.class);

    @Override
    public void run() {
        logger.info("打印workQueue4HtmlBizExecutor线程池情况-------------start----");
        logger.info("cpu 核心数 :{}",Runtime.getRuntime().availableProcessors());
        logger.info("workQueue4HtmlBizExecutor size:{}  ", workQueue4HtmlBizExecutor.size());
        logger.info("CorePoolSize:{} ,ActiveCount:{} ,TaskCount:{},CompletedTaskCount:{} ,LargestPoolSize:{},MaximumPoolSize:{}" +
                "",new Object[]{bizExecutor4Html.getCorePoolSize(), bizExecutor4Html.getActiveCount(),
                bizExecutor4Html.getTaskCount(), bizExecutor4Html.getCompletedTaskCount(), bizExecutor4Html.getLargestPoolSize()
        , bizExecutor4Html.getMaximumPoolSize()});
        logger.info("打印bizExecutorService线程池情况-------------end----");
    }
}