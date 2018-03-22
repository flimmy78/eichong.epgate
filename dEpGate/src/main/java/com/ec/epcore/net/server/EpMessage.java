package com.ec.epcore.net.server;


/**
 * 客户端发给服务端的 消息结构：4字节长度 + 2字节协议号 + ByteBuffer数据
 * +------------+-------------+------------------------+     
 * |   length  	| protocolNum |	 	body (byte array)  |
 * +------------+-------------+------------------------+       
 * |   4 byte 	|   2 byte    | 		n byte		   |
 * +------------+-------------+------------------------+ 
 * length 记录的是 2字节协议号 + ByteBuffer数据 一共占用的字节数
 * @author haojian
 * Apr 1, 2013 10:06:13 AM
 */
public class EpMessage {
	
	
	/**消息体长度(字节数)*/
	private int length;
	
	/**帧类型*/
	private short frame_type;//1:协议帧，2:U帧，3：I帧，4:S帧
	
	
	/**ASDU 类型*/
	private short iec104_type;
	
	/**ASDU 类型的子类型*/
	private int record_type;
	
	/**8个暂时不使用的消息体*/
	private byte[] bytesTo8;
	/**消息体*/
	private byte[] bytes;
	
	public EpMessage(){	
		
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public short getIec104Type() {
		return iec104_type;
	}

	public void setIec104Type(short type) {
		this.iec104_type = type;
	}
	
	public short getFrameType() {
		return frame_type;
	}

	public void setFrameType(short type) {
		this.frame_type = type;
	}
	
	
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getRecordType() {
		return record_type;
	}

	public void setRecordType(int Type) {
		record_type = Type;
	}

	public byte[] getBytesTo8() {
		return bytesTo8;
	}

	public void setBytesTo8(byte[] bytesTo8) {
		this.bytesTo8 = bytesTo8;
	}

	public String toString(){
		return  "[" + iec104_type + "]"+" len="+this.getLength()+" bytes.length=" + bytes.length; 
	}

}
