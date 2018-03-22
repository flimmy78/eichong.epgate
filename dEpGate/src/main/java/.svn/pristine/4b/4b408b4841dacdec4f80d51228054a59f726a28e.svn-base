package com.ec.epcore.net.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class AnalyzeHeader {
	
	private static Logger logger = Logger.getLogger(AnalyzeHeader.class);
	
	public static final short NUM_LEN_FIELD =2;//长度域字节数
	public static final short NUM_HEAD = 2;//协议标识//
	   
   
    private int length;
    private byte[] flag=new byte[2];
    
    @Override
    public AnalyzeHeader clone() {
        try {
            return (AnalyzeHeader) super.clone();
        } catch (CloneNotSupportedException e) {
        	logger.error("clone exception,e.getMessage:"+e.getMessage());
           
        }
        return null;
    }
 
    public AnalyzeHeader() {
    	this.flag[0] = AnalyzeConstant.HEAD_FLAG1;
		this.flag[1] = AnalyzeConstant.HEAD_FLAG2;
    }
 
  
    
    public static int getHLen()
    {
    	return NUM_HEAD+ NUM_LEN_FIELD;
    }
    
  
    public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		
	}
	

	public synchronized byte toByteArray()[] {
		
			ByteArrayOutputStream bout= new ByteArrayOutputStream(NUM_HEAD+ NUM_LEN_FIELD );
			try {
				
				bout.write(this.flag);
				bout.write((byte)(this.length&0x00ff));
				bout.write((byte)((this.length>>8)&0x00ff));
			return bout.toByteArray();
			
		} catch (IOException e) {
			logger.error("toByteArray exception,e.getMessage:"+e.getMessage());
		
			return null;
		}
    }
       
 
    @Override
    public String toString() {
    	
        return "";
    }
}

