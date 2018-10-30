package com.ec.netcore.service;

import com.ec.netcore.constants.RedisConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleJedisPool {
    private static JedisPoolConfig config;
    private static JedisPool jedisPool;
    private static ShardedJedisPool sharedJedisPoolMaster;
    private static Logger logger = LoggerFactory.getLogger(MultipleJedisPool.class);

    static {
        try {
            Map<String, String> mastermap = new HashMap<String, String>();
            String password = RedisConfigService.getValue(RedisConstants.PASSWD_REDIS)
                    .trim();
            String pathm = RedisConfigService.getValue(RedisConstants.PATH_REDISM).trim();
            logger.info("redis--pathm:" + pathm + ",password:" + password);
            if (StringUtils.isNotBlank(pathm)) {
                String[] pms = pathm.split(":");
                if (null != pms && pms.length == 2) {
                    mastermap.put(pms[0].trim(), pms[1].trim());
                }
            }
            Map<String, String> slavemap = new HashMap<String, String>();
            String paths = RedisConfigService.getValue(RedisConstants.PATH_REDISS).trim();
            if (StringUtils.isNotBlank(paths)) {
                String[] slaves = paths.split(",");
                if (null != slaves) {
                    for (String slave : slaves) {
                        if (StringUtils.isNotBlank(slave)) {
                            String[] pss = slave.split(":");
                            if (null != pss && pss.length == 2) {
                                slavemap.put(pss[0].trim(), pss[1].trim());
                            }
                        }
                    }
                }
            }
            // JedisShardInfo info = new JedisShardInfo("","");
            // 生成多机连接List
            List<JedisShardInfo> mastershards = new ArrayList<JedisShardInfo>();
            for (Map.Entry<String, String> entry : mastermap.entrySet()) {
                JedisShardInfo JedisShardInfo = new JedisShardInfo(entry.getKey(),
                        Integer.valueOf(entry.getValue()), "master");
                JedisShardInfo.setPassword(password);
                mastershards.add(JedisShardInfo);
            }
            // 初始化连接池配置对象
            config = new JedisPoolConfig();
            config.setMaxIdle(1000);
            config.setMaxTotal(-1);
            config.setMaxWaitMillis(100 * 1000);
            config.setTestOnBorrow(false);
            config.setTestOnCreate(true);

            // 实例化连接池
            sharedJedisPoolMaster = new ShardedJedisPool(config, mastershards);

            jedisPool = new JedisPool(config, pathm.split(":")[0],
                    Integer.valueOf(pathm.split(":")[1]), 3000, password, 0, null);
        } catch (Exception e) {
            logger.error("create JedisPool error : " + e);
        }
    }

    public static ShardedJedis getMasterInstance() {
        try {
            if (null != sharedJedisPoolMaster) {
                return sharedJedisPoolMaster.getResource();
            }
        } catch (Exception e) {
            logger.error("getMasterInstance error:{}", e);
        }
        return null;
    }

    public static void masterRelease(ShardedJedis jedis) {
        if (jedis != null) {
            sharedJedisPoolMaster.returnResourceObject(jedis);
        }
    }

    /**
     * Jedis对象出异常的时候，回收Jedis对象资源
     *
     * @param jedis
     */
    public static void masterBrokenRelease(ShardedJedis jedis) {
        if (jedis != null) {
            sharedJedisPoolMaster.returnBrokenResource(jedis);
        }

    }

    public static Jedis getJedisFromPool() {
        if (null != jedisPool) {
            return jedisPool.getResource();
        }
        return null;
    }

    public static void jedisRelease(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * Jedis对象出异常的时候，回收Jedis对象资源
     *
     * @param jedis
     */
    public static void jedisBrokenRelease(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }

    }

    private static long index = 9000000000l;

    public static String generateKey() {
        return String.valueOf(Thread.currentThread().getId()) + "_" + (index++);
    }

    private static void close(ShardedJedis shardedJedis,
                              ShardedJedisPool sharedJedisPool) {
        if (shardedJedis != null && sharedJedisPool != null) {
            sharedJedisPool.returnResource(shardedJedis);
        }
        if (sharedJedisPool != null) {
            sharedJedisPool.destroy();
        }
    }


}
