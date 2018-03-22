package com.ec.utils;

import java.math.BigDecimal;

import com.ec.config.Global;

public class NumUtil {
	
	public static BigDecimal intToBigDecimal1(int i)
	{
		BigDecimal value = new BigDecimal(i).multiply(Global.Dec1);
		value = value.setScale(1,BigDecimal.ROUND_HALF_UP);
		return value;
	}
	public static int BigDecimal1ToInt(BigDecimal value)
	{
		value = value.setScale(1,BigDecimal.ROUND_HALF_UP);
		return value.intValue();
	}

	/**
	 * 整数转换成0.01精度小数
	 * @param i
	 * @return
	 */
	public static BigDecimal intToBigDecimal2(int i)
	{
		BigDecimal value = new BigDecimal(i).multiply(Global.Dec2);
		value = value.setScale(2,BigDecimal.ROUND_HALF_UP);
		return value;
	}
	public static int BigDecimal2ToInt(BigDecimal value)
	{
		value = value.setScale(2,BigDecimal.ROUND_HALF_UP);
		return value.intValue();
	}

	/**
	 * 整数转换成0.001精度小数
	 * @param i
	 * @return
	 */
	public static BigDecimal intToBigDecimal3(int i)
	{
		BigDecimal value = new BigDecimal(i).multiply(Global.Dec3);
		value = value.setScale(3,BigDecimal.ROUND_HALF_UP);
		return value;
	}

	/**
	 * 整数转换成0.0001精度小数
	 * @param i
	 * @return
	 */
	public static BigDecimal intToBigDecimal4(int i)
	{
		BigDecimal value = new BigDecimal(i).multiply(Global.Dec4);
		value = value.setScale(4,BigDecimal.ROUND_HALF_UP);
		return value;
	}

	public static BigDecimal intToBigDecimal42(int i)
	{
		BigDecimal value = new BigDecimal(i).multiply(Global.Dec4);
		value = value.setScale(2,BigDecimal.ROUND_HALF_UP);
		return value;
	}

	public static BigDecimal intToBigDecimal3(int i,float j)
	{
		BigDecimal value = new BigDecimal(i).multiply(new BigDecimal(0.001)).multiply(new BigDecimal(j));
		value = value.setScale(3,BigDecimal.ROUND_HALF_UP);
		return value;
	}
	
	public static int BigDecimal3ToInt(BigDecimal value)
	{
		value = value.setScale(3,BigDecimal.ROUND_HALF_UP);
		return value.intValue();
	}

}
