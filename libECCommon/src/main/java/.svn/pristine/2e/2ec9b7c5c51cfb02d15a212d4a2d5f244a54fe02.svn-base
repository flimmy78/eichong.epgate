package com.ec.net.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TimeStage {
	
	private int startTime;

	private int endTime;
	
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	private Integer  flag;
	
	private static final Logger logger = LoggerFactory
			.getLogger(TimeStage.class);
	public synchronized byte toByteArray()[] {
		
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
			
			byte[] st= WmIce104Util.int2Bytes(startTime);
			bout.write(st);
				
			byte[] et= WmIce104Util.int2Bytes(endTime);
			bout.write(et);
			
			byte bFlag = (byte)flag.intValue();
			
			bout.write(bFlag);
	
			return bout.toByteArray();
		} catch (IOException e) {
			logger.error("toByteArray exception,e.getStackTrace:{}",e.getMessage());
			return null;
		}
	}

}
