package com.ec.utils;

import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(NetUtils.class.getName()));

	public static String getClientIp(Channel channel) {
		SocketAddress address = channel.remoteAddress();
		String ip = "";
		if (address != null) {
			ip = address.toString().trim();
			int index = ip.lastIndexOf(':');
			if (index < 1) {
				index = ip.length();
			}
			ip = ip.substring(1, index);
		}
		if (ip.length() > 15) {
			ip = ip.substring(Math.max(ip.indexOf("/") + 1, ip.length() - 15));
		}
		return ip;
	};

	/**
	 * 获取当前时间时分秒,返回数组
	 * 
	 * @return 数组
	 */
	public static byte[] timeToByte() {

		byte time[] = new byte[3];

		Calendar cal = Calendar.getInstance();

		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		int Minute = cal.get(Calendar.MINUTE);

		int Second = cal.get(Calendar.SECOND);

		time[0] = (byte) hourOfDay;
		time[1] = (byte) Minute;
		time[2] = (byte) Second;

		return time;
	}

	public static String timeToString(int h, int m, int s) {

		String time = String.format("%02d", h) + String.format("%02d", m)
				+ String.format("%02d", s);

		return time;
	}

	public static String timeToString(byte[] hmsTime) {

		String time = String.format("%02d", hmsTime[0])
				+ String.format("%02d", hmsTime[1])
				+ String.format("%02d", hmsTime[2]);

		return time;
	}

	public static long getLong(byte[] b) {
		long l = 0;
		for (int i = 0; i < b.length; i++) {
			if (i == 0)
				l += (long) (b[i] & 0xff);
			else
				l += (long) (b[i] & 0xff) << (i * 8);
		}
		return l;
	}

	/**
	 * 
	 * @param time 时间
	 */
	public static void sleep(long time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (Exception e) {
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
		}
	}
}
