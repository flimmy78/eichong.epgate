/*
 * eichong.com Inc.
 * Copyright (c) 2014-2018 All Rights Reserved.
 */
package com.ec.netcore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author ${zhuhuiming}
 * @version $Id: RedisConfigServie.java, v 0.1 2018年07月11日 19:13 Exp $
 */
public class RedisConfigService {

    private static final Logger logger = LoggerFactory.getLogger("RedisConfigService");
    private static Properties pro = new Properties();

    public static void redisInit() {
        logger.debug("redis params init");
        File f = new File("conf/redis.properties");
        try {
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                pro.load(fis);
                fis.close();
            } else {
                logger.error("conf/system.properties 配置文件不存在!");
            }
        } catch (Exception ex) {
            logger.error("redis params init fail ", ex);
        }

    }
    public static String getValue(String key) {
        if (null != pro) {
            return pro.getProperty(key);
        }
        return "";
    }
}