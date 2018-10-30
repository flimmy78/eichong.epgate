package com.ec.netcore.util;

import com.ec.netcore.service.MultipleJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisUtil {


	/**
	 * redis key 过期设置
	 * @param key
	 * @param seconds
	 * @return
	 */
	public static boolean expire(String key, int seconds) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * redis key 过期设置
	 *
	 * @param key
	 * @param seconds
	 * @return
	 */
	public static boolean expire4Byte(byte[] key, int seconds) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}
	/**
	 * String(k,v)
	 * @param key
	 * @return value
	 */
	public static String get(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return "";
	}

	/**
	 * 插入String（k,v）
	 * @param key
	 * @param value
	 */
	public static boolean set(String key, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.set(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	public static String set4Lock(String key, String value, String nxxx, String expx, long time) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		String result="FALSE";
		try {
			if (null != jedis) {
				result = jedis.set(key, value, nxxx, expx, time);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return result;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return result;
	}
	public static boolean setExpire(String key,int time, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.setex(key, time, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	public static Long decr(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.decr(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	public static Long decrBy(String key, Long num) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.decrBy(key, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * 判断某个key是否存在
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.exists(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
        }
		return false;
	}

	/**
	 * 判断某个key是否存在
	 *
	 * @param key
	 * @return
	 */
	public static boolean existsByte(byte[] key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.exists(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return false;
	}

	/**
	 * 删除
	 * @param key
	 */
	public static boolean del(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.del(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 判断是否是成员
	 * @param key
	 */
	public static boolean sismember(String key, String member) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		boolean ismember = false;
		try {
			if (null != jedis) {
				ismember = jedis.sismember(key, member);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return ismember;
	}

	/**
	 * key对应值自增+1
	 * @param key
	 */
	public static Long incr(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.incr(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * key对应值自增+num
	 * @param key
	 */
	public static Long incrBy(String key, Long num) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.incrBy(key, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * list
	 * @param key
	 * @param index
	 * @return value
	 */
	public static String lindex(String key, long index) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.lindex(key, index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return "";
	}

	/**
	 * 从list头部添加数据
	 * @param key
	 * @param string
	 */
	public static boolean lpush(String key, String... string) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.lpush(key, string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	public static boolean lpush4Byte(byte[] key, byte[]string) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.lpush(key, string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 从list尾部添加数据
	 * @param key
	 * @param string
	 */
	public static boolean rpush(String key, String... string) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.rpush(key, string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 获取全部list数据
	 * @param key
	 */
	public static List<String> lrange(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.lrange(key, 0, -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}


	/**
	 * 从list尾部移除并返回移除数据
	 * @param key
	 */
	public static String rpop(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.rpop(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return "";
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return "";
	}

	/**
	 * 获取map中对应域值
	 * @param key
	 * @param field
	 * @return value
	 */
	public static String hget(String key, String field) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hget(key, field);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return "";
	}

	/**
	 * 获取全部域值
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetall(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hgetAll(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptyMap();
	}

	/**
	 * 向map中添加k,v
	 * @param key
	 * @param field
	 * @param value
	 */
	public static boolean hset(String key, String field, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.hset(key, field, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	public static boolean hmset(String key, Map<String,String>map) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.hmset(key, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 返回哈希表中所有的key
	 */
	public static Set<String> hkeys(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hkeys(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptySet();
	}

	/**
	 * 判断hash中是否存在某个域
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean hexists(String key, String field) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hexists(key, field);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return false;
	}

	/**
	 * map中对应域 +value
	 * @param key
	 * @param field
	 * @param value
	 */
	public static Long hincrBy(String key, String field, long value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hincrBy(key, field, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * map中对应域 +value
	 * @param key
	 * @param field
	 * @param value
	 */
	public static Double hincrByFloat(String key, String field, double value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hincrByFloat(key, field, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * 删除map中对应域
	 * @param key
	 * @param fields
	 */
	public static boolean hdel(String key, String... fields) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.hdel(key, fields);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 添加成员到set集合
	 * @param key
	 * @param members
	 */
	public static boolean sadd(String key, String... members) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.sadd(key, members);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 返回所有set集合
	 * @param key
	 * @param
	 */
	public static Set<String> smembers(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.smembers(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
        }
		return Collections.emptySet();
	}

	/**
	 * 添加带得分成员
	 * @param key
	 * @param score
	 * @param member
	 */
	public static boolean zadd(String key, double score, String member) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.zadd(key, score, member);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 移除带得分成员
	 * @param key
	 * @param
	 * @param member
	 */
	public static boolean zrem(String key, String member) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				jedis.zrem(key, member);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return false;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return true;
	}

	/**
	 * 移除带得分成员
	 * @param key
	 * @param
	 * @param member
	 */
	public static long zrem1(String key, String member) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.zrem(key, member);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return -1l;
	}

	/**
	 * 获取降序排列值
	 * @param key
	 * @param start
	 * @param end
	 */
	public static Set<String> zrevrange(String key, int start, int end) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.zrevrange(key, start, end);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptySet();
	}

	/**
	 * 获取升序排列值
	 * @param key
	 * @param
	 * @param
	 */
	public static Set<String> zrangeByScore(String key, String min, String max) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptySet();
	}

	public static Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.zrevrangeWithScores(key, start, end);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptySet();
	}

	public static Set<Tuple> zrevrangeByScoreWithScores(String key, long start, long end) {
		Jedis jedis = MultipleJedisPool.getJedisFromPool();
		try {
			if (null != jedis) {
				return jedis.zrevrangeByScoreWithScores(key, start, end);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.jedisBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.jedisRelease(jedis);
		}
		return Collections.emptySet();
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public static Long zcard(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.zcard(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return 0l;
	}

	/**
	 * 查看set集合数
	 * @param key
	 * @param
	 * @return
	 */
	public static long scard(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.scard(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return 0;

	}

	/**
	 * 获取set中随机元素
	 * @param key
	 * @return
	 */
	public static String srandmember(String key) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.srandmember(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return "";
	}

	/**
	 * 返回随机num个值
	 * @param key
	 * @param num
	 * @return
	 */
	public static List<String> srandmember(String key, int num) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.srandmember(key, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return Collections.emptyList();
	}

	public static String getset(String key, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.getSet(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	public static Long setnx(String key, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.setnx(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	public static Long hsetnx(String key, String field, String value) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.hsetnx(key, field, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素
	 * @param key
	 * @param members
	 */
	public static long srem(String key, String... members) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.srem(key, members);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return 0;
	}

	public static Set<String> keys(String pattern) {
		Jedis jedis = MultipleJedisPool.getJedisFromPool();
		try {
			if (null != jedis)
				return jedis.keys(pattern);
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.jedisBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.jedisRelease(jedis);
		}
		return null;
	}

	public static Set<String> sinter(String... keys) {
		Jedis jedis = MultipleJedisPool.getJedisFromPool();
		try {
			if (null != jedis) {
				Set<String> set = jedis.sinter(keys);
				return set;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.jedisBrokenRelease(jedis);
		} finally {
			if (null != jedis)
				MultipleJedisPool.jedisRelease(jedis);
		}
		return null;
	}

	/**
	 *
	 * @param key
	 * @param d
	 * @return
	 */
	public static Double incrByFloat(String key, double d) {
		ShardedJedis jedis = MultipleJedisPool.getMasterInstance();
		try {
			if (null != jedis) {
				return jedis.incrByFloat(key, d);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MultipleJedisPool.masterBrokenRelease(jedis);
			return null;
		} finally {
			if (null != jedis)
				MultipleJedisPool.masterRelease(jedis);
		}
		return null;
	}

}
