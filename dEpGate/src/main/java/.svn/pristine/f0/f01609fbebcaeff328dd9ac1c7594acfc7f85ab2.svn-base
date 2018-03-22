package com.ec.epcore.net.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ec.net.proto.WmIce104Util;

public class ApciHeader {
	
	private static Logger logger = Logger.getLogger(ApciHeader.class);
	
	public static final short NUM_CTRL = 4;//控制域字节数
	public static final short NUM_LEN_FIELD =2;//APCI长度域字节数
	public static final short NUM_HEAD = 1;//104规约标识字节数
	   
    private byte flag;
    private int length;
    private byte[] ctrl=new byte[4];
   
    @Override
    public ApciHeader clone() {
        try {
            return (ApciHeader) super.clone();
        } catch (CloneNotSupportedException e) {
        	logger.error("clone exception,e.getMessage:"+e.getMessage());
          
        }
        return null;
    }
 
    public ApciHeader() {
    	flag = 0x68;
    }
 
    public ApciHeader(byte[] headerdata) {
      //  this.flag = headerdata[0];
      //  this.length = headerdata[1];
        
     //   System.arraycopy(headerdata,NUM_CTRL,this.ctrl,0,NUM_CTRL);
        
    }
 
    public ApciHeader(byte flag, byte length, byte[] ctrl) {
        
    }
    
    public static int getHLen()
    {
    	return NUM_CTRL+ NUM_LEN_FIELD+NUM_HEAD;
    }
    
  
    public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		
	}
	public void setUFrameType(byte uType) {
		this.ctrl[0] = uType;
		
	}
	public void setSFrame(int NR) {//add by hly 
		this.ctrl[0] = 1;
		this.ctrl[1] = 0;
		this.ctrl[2] = (byte)((NR % 128) << 1);
		this.ctrl[3] = (byte)(NR / 128);
		
	}
	public void setINr(int sendINum,int recvINum) {//add by hly 
		this.ctrl[0] = (byte)((sendINum % 128) << 1);
		this.ctrl[1] = (byte)(sendINum / 128);
		this.ctrl[2] = (byte)((recvINum % 128) << 1);
		this.ctrl[3] = (byte)(recvINum / 128);
		
	}

	public synchronized byte toByteArray()[] {
		
		ByteArrayOutputStream bout= new ByteArrayOutputStream(NUM_CTRL+ NUM_LEN_FIELD+NUM_HEAD );
		try {
			
			bout.write(this.flag);
			bout.write(WmIce104Util.short2Bytes((short)this.length));
		
			bout.write(this.ctrl);
			return bout.toByteArray();
			
		} catch (IOException e) {
			logger.error("toByteArray exception,e.getMessage:"+e.getMessage());
			
			return null;
		}
    }
       
 
    @Override
    public String toString() {
    	
    	byte FormatType= (byte) (ctrl[0]&0x03);
        if(FormatType==0||FormatType ==2)            /* I */
        {
        	return "I Frame[length=" + length  + "]";
        	
               //Ice104ProcessFormatI();
        }
        else 
        {
               if(FormatType==1)     /*  S  */ 
            	   return "S Frame[length=" + length  + "]";
                    //Ice104ProcessFormatS();
               else
               {
                    if(FormatType==3)/*  U  */
                    	return "U Frame[length=" + length  + "]";
                       //Ice104ProcessFormatU();
               }
         }
        return "";
    }
}
