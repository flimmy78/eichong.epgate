package com.ec.epcore.net.server;


/**
 * 游戏服发给网关服的消息结构
 * +------------+-------------+---------------+-------------+------------------------+     
 * |   length  	| protocolNum |	  userIds     | isCompress	|	  body (byte array)  |
 * +------------+-------------+---------------+-------------+------------------------+       
 * |   4 byte 	|   2 byte    | 收消息的用户id |	 是否压缩		|		 n byte		     |
 * +------------+-------------+---------------+-------------+------------------------+ 
 * length 记录的是后续所有数据的总长度
 * @author haojian
 * Apr 1, 2013 10:06:13 AM
 */
public class InnerApiMessage {
	
	/**消息长度(字节数)*/
	private int length;
	/**协议号*/
	private short protocolNum;
	/**接收消息的用户id*/
	private int[] userIds;
	/**是否压缩*/
	private byte isCompress;
	/**消息体*/
	private byte[] bytes;
	
	public InnerApiMessage(){	
		
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public short getProtocolNum() {
		return protocolNum;
	}

	public void setProtocolNum(short protocolNum) {
		this.protocolNum = protocolNum;
	}

	public byte getIsCompress() {
		return isCompress;
	}

	public void setIsCompress(byte isCompress) {
		this.isCompress = isCompress;
	}
	
	public int[] getUserIds() {
		return userIds;
	}

	public void setUserIds(int[] userIds) {
		this.userIds = userIds;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public String toString(){
		return  "[" + protocolNum + "]"+" len="+this.getLength()+" userIds.length="+userIds.length; 
	}

}
