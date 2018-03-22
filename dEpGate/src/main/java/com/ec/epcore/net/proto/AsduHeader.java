package com.ec.epcore.net.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.utils.LogUtil;

public class AsduHeader {

	private static final Logger logger =  LoggerFactory.getLogger(LogUtil.getLogName(AsduHeader.class.getName()));
	
	public static final short  H_LEN =         9;
	   
	/**
	 * ASDU类型
	 */
	private byte type;
	private byte limit;//VSQ
	private byte con[]=new byte[2];
	private byte conn_address[]=new byte[2];
	private byte body_address[]=new byte[3];
	
	
	//private byte connext[]= new byte[6];
	
	  public byte getLimit() {
		return limit;
	}

	public void setLimit(byte limit) {
		this.limit = limit;
	}

	public byte[] getCon() {
		return con;
	}

	public void setCon(byte[] con) {
		this.con = con;
	}

	public byte[] getConn_address() {
		return conn_address;
	}

	public void setConn_address(byte[] conn_address) {
		this.conn_address = conn_address;
	}

	public byte[] getBody_address() {
		return body_address;
	}

	public void setBody_address(byte[] body_address) {
		this.body_address = body_address;
	}

	@Override
	    public AsduHeader clone() {
	        try {
	            return (AsduHeader) super.clone();
	        } catch (CloneNotSupportedException e) {
	        	logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
	        }
	        return null;
	    }
	 
	    public AsduHeader() {
	    	
	    	type=0;
	    	limit=0;
	 
	    }
	    
	    
	 
	    public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public AsduHeader(byte[] headerdata) {
	        this.type = headerdata[0];
	       
	    	this.limit= headerdata[1];
	    	
	    	System.arraycopy(headerdata,2,this.con,0,2);
	    	System.arraycopy(headerdata,4,this.conn_address,0,2);
	    	//System.arraycopy(headerdata,6,this.body_address,0,3);
	    }
	 
	
	
	
	public synchronized byte toByteArray()[] {
		ByteArrayOutputStream bout= new ByteArrayOutputStream(6);
		try {
			bout.write(type);
			bout.write(limit);
			bout.write(con);
			bout.write(conn_address);
			bout.write(body_address);
			return bout.toByteArray();
		} catch (IOException e) {
        	logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
			return null;
		}
    }

}
