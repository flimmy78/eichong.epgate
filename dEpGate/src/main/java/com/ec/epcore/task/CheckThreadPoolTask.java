/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ec.epcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ec.epcore.net.server.EpMessageHandler.bizExecutorService;
import static com.ec.epcore.net.server.EpMessageHandler.workQueue4BizExecutor;

/**
 * 打印bizExecutorService线程池情况
 * @author ${zhuhuiming}
 * @version $Id: CheckThreadPoolTask.java, v 0.1 2018年07月30日 14:32 Exp $
 */
public class CheckThreadPoolTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CheckThreadPoolTask.class);

    @Override
    public void run() {
        logger.info("打印bizExecutorService线程池情况-------------start----");
        logger.info("cpu 核心数 :{}",Runtime.getRuntime().availableProcessors());
        logger.info("workQueue4BizExecutor size:{}  ", workQueue4BizExecutor.size());
        logger.info("CorePoolSize:{} ,ActiveCount:{} ,TaskCount:{},CompletedTaskCount:{} ,LargestPoolSize:{},MaximumPoolSize:{}" +
                "",new Object[]{ bizExecutorService.getCorePoolSize(), bizExecutorService.getActiveCount(),
                bizExecutorService.getTaskCount(),bizExecutorService.getCompletedTaskCount(),bizExecutorService.getLargestPoolSize()
        ,bizExecutorService.getMaximumPoolSize()});
        logger.info("打印bizExecutorService线程池情况-------------end----");

    }
}