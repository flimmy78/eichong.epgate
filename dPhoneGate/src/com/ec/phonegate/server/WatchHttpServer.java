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
package com.ec.phonegate.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ec.netcore.model.conf.ServerConfig;
import com.ec.netcore.netty.httpserver.AbstractHttpServer;
import com.ec.utils.LogUtil;



/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
public class WatchHttpServer extends AbstractHttpServer {

	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(PhoneServer.class.getName()));
	
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
			e.printStackTrace();
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
			e.printStackTrace();
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
		logger.info(LogUtil.getExtLog("PhoneNettyServer server stop..."));
		
	}
}
