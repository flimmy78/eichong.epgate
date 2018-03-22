package com.ec.epcore.net.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.net.proto.WmIce104Util;


public class BlankUser {
	
	private static final Logger logger = LoggerFactory.getLogger(BlankUser.class);
	
	private byte state;
	private String card_no;
	
	public BlankUser(String vCardNo,byte vState)
	{
		card_no = vCardNo;
		
		state=vState;
	}
	
	public synchronized byte toByteArray()[] {
		
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
			
			bout.write(WmIce104Util.str2Bcd(card_no));
			
			bout.write(state);
			
			return bout.toByteArray();
		} catch (IOException e) {
			logger.error("toByteArray exception,e.StackTrace:{}",e.getStackTrace());
			return null;
		}
	}
	
	

}
