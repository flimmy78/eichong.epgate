package com.ec.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static String repeat(String src, int num) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < num; i++)
			s.append(src);
		return s.toString();
	}

	public static String divF(byte[] bytes) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0x0FF) == 255) {
				break;
			}

			s.append(bytes[i]);
		}
		return s.toString();
	}

	public static String getRandomString(int length) {
		String str = "ABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(36);// 0~61
			sf.append(str.charAt(number));
		}
		return sf.toString();
	}

	public static String getByteString(byte[] src) {
		if (src == null || src.length <= 0)
			return "";

		String s = new String(src);
		return s;
	}

	public static String getCString(byte[] src) {
		int pos = -1;
		int len = src.length;
		for (int i = 0; i < len; i++) {
			if (src[i] == 0) {
				pos = i + 1;
				break;
			}
		}
		if (pos == -1)	//return "";
			pos = len + 1;

		byte[] dest = new byte[pos - 1];
		System.arraycopy(src, 0, dest, 0, pos - 1);
		String s = new String(dest);
		return s;
	}

	public static String getAscii(byte[] src) {
		int pos = -1;
		int len = src.length;
		for (int i = 0; i < len; i++) {
			if (src[i] == 0) {
				pos = i + 1;
				break;
			}
			if (src[i] < 0x20 || src[i] > 0x7e) {
				return " ";
			}
		}
		if (pos == -1) pos = len + 1;
			//return "";

		byte[] dest = new byte[pos - 1];
		System.arraycopy(src, 0, dest, 0, pos - 1);
		String s = new String(dest);
		s.trim();
		return s;
	}

	public static String getString(Object str) {
		if (str == null)
			return "";

		return str.toString();
	}

	public static String getIpAddress() {
		InetAddress localhost;
		String ipAddress = StringUtils.EMPTY;
		try {
			localhost = InetAddress.getLocalHost();
			ipAddress = localhost.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return ipAddress;
	}

	public static String join(CharSequence charsequence,
			CharSequence acharsequence[]) {
		Objects.requireNonNull(charsequence);
		Objects.requireNonNull(acharsequence);
		StringBuilder stringjoiner = new StringBuilder();
		CharSequence acharsequence1[] = acharsequence;
		int i = acharsequence1.length;
		for (int j = 0; j < i; j++) {
			CharSequence charsequence1 = acharsequence1[j];
			stringjoiner.append(charsequence1).append(charsequence);
		}
		if (stringjoiner.length() > 0)
			stringjoiner = stringjoiner.deleteCharAt(stringjoiner.length()
					- charsequence.length());
		return stringjoiner.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String join(CharSequence charsequence, Iterable iterable) {
		Objects.requireNonNull(charsequence);
		Objects.requireNonNull(iterable);
		StringBuilder stringjoiner = new StringBuilder();
		CharSequence charsequence1 = StringUtils.EMPTY;
		for (Iterator iterator = iterable.iterator(); iterator.hasNext(); stringjoiner
				.append(charsequence1).append(charsequence))
			charsequence1 = (CharSequence) iterator.next();
		if (stringjoiner.length() > 0)
			stringjoiner = stringjoiner.deleteCharAt(stringjoiner.length()
					- charsequence.length());
		return stringjoiner.toString();
	}
}
