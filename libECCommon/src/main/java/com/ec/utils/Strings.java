package com.ec.utils;

import java.util.Map;

public class Strings {
	public static boolean isNullOrEmpty(String value) {
		return (value == null || value.length() == 0);
	}

	public static boolean isNullOrEmpty(Object value) {
		return (value == null || value.toString().length() == 0);
	}

	public static int getIntValue(Map<String ,Object> realData,String key)
	{
		Object o = realData.get(key);
		int value= -1;
		if(o!=null) value = (int)o;
		return value;
	}
}
