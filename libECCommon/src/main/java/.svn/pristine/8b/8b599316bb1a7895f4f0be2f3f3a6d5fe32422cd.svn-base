package com.ec.net.proto;

public class NR {
	private short sendSq;
	
	private short recvSq;
	public NR(byte[] bytes)
	{
		sendSq=0;
		recvSq=0;
		if(bytes!=null && bytes.length==4)
		{
			
			sendSq = (short)(bytes[0]&0xff);
			sendSq += (short)((bytes[1]&0xff) << 8);
			sendSq = (short)(sendSq >> 1);
			
			recvSq = (short)(bytes[2]&0xff);
			recvSq += (short)((bytes[3]&0xff) << 8);
			recvSq = (short)(recvSq >> 1);
		}
	}
	
	public short getSendSq() {
		return sendSq;
	}
	public short getRecvSq() {
		return recvSq;
	}
	

}
