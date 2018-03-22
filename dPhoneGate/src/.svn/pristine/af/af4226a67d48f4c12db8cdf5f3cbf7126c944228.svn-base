import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ec.net.proto.WmIce104Util;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * 关闭服务器,不带包名，便于服务器上使用
 * @author 
 * 2014-12-1 下午3:37:47
 */
public class ServerStop {
	public static String confPath = System.getProperty("user.dir")
			+ System.getProperty("file.separator") + "conf"
			+ System.getProperty("file.separator")+"game-config.xml";
	
	public static void main(String[] args) {
		String src = "";
		src +="e10adc3949ba59abbe56e057f20f883e"+13718;
		System.out.printf(WmIce104Util.MD5Encode(src.getBytes()));
		//shutDown( getShutDownPort() );
	}
	
	/**
	 * 停止服务器并发送消息
	 * @author 
	 * 2014-12-1
	 * @param shutdownPoint
	 */
	public static void shutDown(int shutdownPoint){
		String returnMsg = null;
		
		try {
			System.out.printf("shutdownPoint:"+shutdownPoint+"\n");
			Socket socket = new Socket("localhost",shutdownPoint);
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			byte[] bb = "shutdown".getBytes();
			os.write(bb);
			os.flush();
			
			byte[] bb2 = new byte[1024];
			int len = is.read(bb2);
			while(len!=-1){
				returnMsg = new String(bb2,0,len);
				len = is.read(bb2);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(3000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取服务器关闭端口
	 * @author 
	 * 2014-12-1
	 * @return
	 */
	public static int getShutDownPort(){
		int shutDownPort = 0;
		
		try {
			Document doc = getW3CDocument(confPath);
			//得到文档名称为Student的元素的节点列表
            NodeList nList = doc.getElementsByTagName("shutdown-port");
            shutDownPort = Integer.valueOf(nList.item(0).getFirstChild().getNodeValue().trim());
			
           // System.out.println("关闭端口："+shutDownPort);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    return shutDownPort;
	}
	
	
	
	public static Document getW3CDocument(String fileName){
		Document doc = null;
		
		try{
            //得到DOM解析器的工厂实例
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //从DOM工厂中获得DOM解析器
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //把要解析的xml文档读入DOM解析器
            
            doc = dbBuilder.parse(new File(confPath));
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return doc;
	}
}
