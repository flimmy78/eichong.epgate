package com.ec.utils;

public class BitUtils {
	
	short [] get2Bits(byte b)
	{
		short[] values = new short[4];
		int value= (int)b&0xff;
		values[0] = (short)(value %4);
		values[1] = (short)((value>>2)%4); 
		values[2] = (short)((value>>4)%4);
		values[3] = (short)((value>>6)%4);
		
		return values;
	}
	short [] getBits(byte b)
	{
		short[] values = new short[8];
		int value= (int)b&0xff;
		values[0] = (short)(value%2);
		values[1] = (short)((value>>>1)%2);
		values[2] = (short)((value>>>2)%2);
		values[3] = (short)((value>>>3)%2);
		values[4] = (short)((value>>>4)%2);
		values[5] = (short)((value>>>5)%2);
		values[6] = (short)((value>>>6)%2);
		values[7] = (short)((value>>7)%2);
		return values;
	}


}
