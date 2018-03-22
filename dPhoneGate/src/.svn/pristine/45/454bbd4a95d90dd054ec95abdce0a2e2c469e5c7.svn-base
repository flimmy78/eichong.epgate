import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.phonegate.server.GateServer;
import com.ec.utils.LogUtil;


public class ServerStart {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(ServerStart.class.getName()));
	
	/**
	 * 启动网关服务器
	 * @author 
	 * 2014-12-1
	 * @param args
	 * @throws AntiException 
	 */
	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		
		//创建网关服务器
		GateServer gateServer = GateServer.getInstance();
		gateServer.start();//启动服务器
		
		long end = System.currentTimeMillis();

		logger.info("【网关】服务器启动成功！启动耗时：【{}】秒", new Object[]{(end-begin)/1000d} );
	}
}
