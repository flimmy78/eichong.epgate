package com.ec.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public static int getFileSize(String filepath)
	{
		File f= new File(filepath);  
		if (f.exists() && f.isFile())
			return (int) f.length();
		else
			return 0;
	};
	
	public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
		  try {
		      MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		      MessageDigest md5 = MessageDigest.getInstance("MD5");
		      md5.update(byteBuffer);
		      BigInteger bi = new BigInteger(1, md5.digest());
		      value = bi.toString(16);
		  } catch (Exception e) {
			  logger.error("getMd5ByFile exception,e.getMessage():{}",e.getMessage());
		    
		  } finally {
		            if(null != in) {
		              try {
		            in.close();
		        } catch (IOException e) {
		        	logger.error("getMd5ByFile  in.close() exception,e.getMessage():{}",e.getMessage());
		        }
		      }
		  }
		  return value;
    }

	public static byte[] getBinaryInfo(String fileName,int pos,int len)
	{	
		FileInputStream in=null;
		DataInputStream dis=null;
		try {
			 File file = new File(fileName); 
			 in = new FileInputStream(file);  
			 dis= new DataInputStream(in);
			 dis.skip(pos);
			
        	byte []buffer=new byte[len]; 
            int len1=0; 
            len1=dis.read(buffer, 0, len);
          
            return buffer;
	    } 
		catch (IOException e) {
			logger.error("getBinaryInfo exception,e.getMessage():{}",e.getMessage());
            return null;
        }
		finally
		{
			 try {
				if(dis !=null)
				{
					dis.close();
				}
				if(in !=null)
				{
					 in.close();
				}
			 } catch (IOException e) {
				 logger.error("getBinaryInfo in.close() exception,e.getMessage():{}",e.getMessage());
			}
		}
	};
	public static String getMsgLogPath()
	{
		//return "/root/tools/apache-tomcat-7.0.59/webapps/ROOT/log/";
		return System.getProperty("user.dir") + File.separator + "msg" + File.separator;	
		
	}
	
	public static void CreateCommMsgLogFile(String fileName)
	{
		//File path=new File("/root/tools/apache-tomcat-7.0.59/webapps/ROOT/log");
		File dir=new File(getMsgLogPath(),fileName);
		try {
			dir.createNewFile();
		} catch (IOException e) {
			logger.error("CreateCommMsgLogFile exception,e.getMessage():{}",e.getMessage());
		}  
	};
	
	public static void appendLog(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
       	 String path=getMsgLogPath();
       	 String FullfileName=path+fileName;
           FileWriter writer = new FileWriter(FullfileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
        	logger.error("appendLog exception,e.getMessage():{}",e.getMessage());
        }
    }
	public static void writeLog(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数false,覆盖
       	 String path=getMsgLogPath();
       	 String FullfileName=path+fileName;
           FileWriter writer = new FileWriter(FullfileName, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
        	logger.error("writeLog exception,e.getMessage():{}",e.getMessage());
        }
    }
	  public static void getAllFileName(String path,ArrayList<String> fileName)
	    {
	        File file = new File(path);
	        String [] names = file.list();
	        if(names != null)
	        fileName.addAll(Arrays.asList(names));
	        
	    }
	
       public static int readLog(String fileName, char []buf) {
		  
		  int len = 0;
		  
	      try {
	             //打开一个读文件器
	        	String path=getMsgLogPath();
	        	String FullfileName=path+fileName;
	            FileReader reader = new FileReader(FullfileName);
 
	            len = reader.read(buf);
	            
	            reader.close();
	         } catch (IOException e) {
	        	 logger.error("readLog exception,e.getMessage():{}",e.getMessage());
	         }
	     
	     return len;
	  }
	
}
