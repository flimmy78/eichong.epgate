/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ec.epcore.net.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.httpserver.AbstractHttpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
public class WatchHttpServer extends AbstractHttpServer {

	private static final Logger logger = LoggerFactory.getLogger(WatchHttpServer.class);
	
	public WatchHttpServer(ServerConfig serverConfig) {
		super(serverConfig);
	}
	@Override
	public String handleGetMessage(String path,Map<String, List<String>> params)
	{
		try {
			return WatchHttpServerHandler.handleGetMessage( path, params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("handleGetMessage exception,e.getMessage():{}",e.getMessage());
	
			return "";
		}
		
	}
	@Override
	public String handlePostMessage(String postMethod,
	HashMap<String, Object> params)
	{
		try {
			return WatchHttpServerHandler.handlePostMessage( postMethod, params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("handlePostMessage exception,e.getMessage():{}",e.getMessage());
		
			return "";
		}
		
	}
    

	//@Override
	public void writeResponse() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stop() {
		super.stop();
		logger.info("PhoneNettyServer server stop...");
		
	}
}
