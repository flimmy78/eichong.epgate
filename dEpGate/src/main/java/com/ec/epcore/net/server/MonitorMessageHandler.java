package com.ec.epcore.net.server;

import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.epcore.net.codec.MonitorDecoder;
import com.ec.epcore.net.proto.AnalyzeConstant;




/**
 * 接受数据中心数据并处理
 */
public class MonitorMessageHandler{

	private static final Logger logger = LoggerFactory.getLogger(MonitorMessageHandler.class);

    /**
	 * 接受数据中心数据并处理
	 * @param channel
	 * @param message
	 * @throws IOException 
	 */
     public static void handleMessage(Channel channel, MonitorMessage message) {
		
    	 byte[] msg = message.getBytes();
    	
    	 
    	 try {
    	     processFrame(channel,message.getCos(),message.getCmd(),msg);
    	 }catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void processFrame(Channel channel,int cos,int type, byte[] msg)
			throws IOException {

        int msgLen = msg.length;
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg);
        
       // int cos = byteBuffer.get();
       // int type = ByteBufferUtil.readUB2(byteBuffer);
	    
		boolean logMsg = true;

		switch (type & 0xffff) 
		{
		case AnalyzeConstant.D_HEART:// 1
        {
           
        	MonitorDecoder.decodeHeart(channel, byteBuffer);
        	
		}
		break;

		default:
		break;
		}

	}


	
}
