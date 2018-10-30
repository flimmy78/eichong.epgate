package com.ec.netcore.util;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TaskJedisUtil {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setObjectFromRedis(String mapKey, Object o, Class cls) {
		Field[] fields = cls.getDeclaredFields();
		try {
			for (Field field : fields) {
				Method getMethod = cls.getMethod("get"
						+ captureName(field.getName()));
				Object oo = getMethod.invoke(o);
				if (null != oo && !"".equals(oo)) {
					JedisUtil.hset(mapKey, field.getName(), oo.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String captureName(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getObjectFromRedis(String mapKey, Class cls) {
		Field[] fields = cls.getDeclaredFields();
		Object o = null;
		try {
			o = cls.newInstance();
			int i = 0;
			for (Field field : fields) {
				Method setMethod = cls.getMethod(
						"set" + captureName(field.getName()), field.getType());
				String v = JedisUtil.hget(mapKey, field.getName());
				if (StringUtils.isNotBlank(v) && !"null".equals(v)) {
					i++;
					if (field.getType() == Integer.TYPE
							|| field.getType() == Integer.class) {
						setMethod.invoke(o, Integer.valueOf(v));
					} else if (field.getType() == Long.TYPE
							|| field.getType() == Long.class) {
						setMethod.invoke(o, Long.valueOf(v));
					} else if (field.getType() == Double.TYPE
							|| field.getType() == Double.class) {
						setMethod.invoke(o, Double.valueOf(v));
					} else {
						setMethod.invoke(o, v);
					}
				}
			}
			if (i == 0) {
				o = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return o;
	}
}
