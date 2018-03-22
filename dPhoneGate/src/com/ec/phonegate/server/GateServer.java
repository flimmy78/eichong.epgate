package com.ec.phonegate.server;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.conf.CoreConfig;
import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.model.conf.ServerConfigs;
import com.ec.netcore.netty.httpserver.AbstractHttpServer;
import com.ec.netcore.netty.server.AbstractNettyServer;
import com.ec.netcore.server.impl.AbstractGameServer;
import com.ec.phonegate.codec.PhoneDecoder;
import com.ec.phonegate.codec.PhoneEncoder;
import com.ec.phonegate.config.GameConfig;
import com.ec.phonegate.service.CallBackService;
import com.ec.phonegate.service.PhoneService;
import com.ec.usrcore.server.CommonServer;
import com.ec.utils.LogUtil;

public class GateServer extends AbstractGameServer{
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(GateServer.class.getName()));
	
	private static GateServer gameServer;
	
	private static Object lock = new Object();
	
	
	/**临时的手机长链接服务*/
	public static AbstractNettyServer nettyPhoneServer;
	
	/**临时的手机长链接服务*/
	public static AbstractHttpServer watchHttpServer=null;
	

	public GateServer() {
	
		//创建netty服务器
		ServerConfigs serverConfigs = CoreConfig.serverConfigs;
		if (serverConfigs != null) {
			
			ServerConfig phoneSvrCfg = serverConfigs.get("phone-server");
			//
			if (phoneSvrCfg != null) {
				
				ByteToMessageDecoder decoder = new PhoneDecoder();
				MessageToByteEncoder encoder = new PhoneEncoder();
				
				CommonServer commonServer = CommonServer.getInstance();
				commonServer.init(Integer.valueOf(GameConfig.serverType), null, new CallBackService());
				PhoneServer nettyServer = new PhoneServer(phoneSvrCfg, decoder, encoder,0,0);
				nettyServerList.add(nettyServer);
				nettyPhoneServer = nettyServer;
			}
			else
			{
				String errMsg = "【Gate服务器】缺少【手机接口】访问配置...服务器强行退出！";
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		}
	}
	
	/**
	 * 创建服务端服务器
	 * @author 
	 * 2014-11-28
	 * @return
	 */
	public static GateServer getInstance() {
		synchronized(lock){
			if(gameServer==null){
				gameServer = new GateServer();
			}
		}
		return gameServer;
	}
	public void init(){
		super.init();
		new GameConfig();//初始化服务器基础配置

		logger.info("初始化服务成功...");
	}
	
	@Override
	public void start() {
	
		super.start();
		
	}

	@Override
	public void stop() {
		
		//1、停止 netty服务器、停止 netty客户端、关闭线程池、关闭任务池
		super.stop();
		
	}
	
	@Override
	public void startTimerServer() {
		   
        //检查手机僵尸状态通讯
        PhoneService.startCommClientTimeout(5);
        
		super.startTimerServer();
		
		logger.info("所有定时任务启动成功!");
		
	}
}
