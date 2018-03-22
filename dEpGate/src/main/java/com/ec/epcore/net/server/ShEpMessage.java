package com.ec.epcore.net.server;


/**
 * 客户端发给服务端的 消息结构：4字节长度 + 2字节协议号 + ByteBuffer数据
 * +------------+-------------+--------+------------+------------+------------+     
 * |   起始 	    |    长度      |	    版本|  序列号域   |  命 令代CMD | 数据体     |
 * +------------+-------------+--------+------------+------------+------------+       
 * |  2 byte 	|   2 byte    | 1 byte |  1 byte    |  2 byte    | n byte     |
 * +------------+-------------+--------+------------+------------+------------+ 
 * length 记录的是 2字节协议号 + ByteBuffer数据 一共占用的字节数
 * @author haojian
 * Apr 1, 2013 10:06:13 AM
 */
public class ShEpMessage {
	
	
	/**消息体长度(字节数)*/
	private int length;
	
	/**帧类型*/
	private short cmd;//
	/** 版本号*/
	private  byte version;//
	/** 流水号*/
	private  byte serial;//
	
	/**消息体*/
	private byte[] bytes;
	
	public ShEpMessage(){	
		
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public byte getSerial() {
		return serial;
	}

	public void setSerial(byte serial) {
		this.serial = serial;
	}

	

}
