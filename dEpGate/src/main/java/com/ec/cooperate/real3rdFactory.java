package com.ec.cooperate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooperate.CooperateFactory;
import com.ec.netcore.conf.CoreConfig;
import com.ec.utils.LogUtil;
//import com.cooperate.CooperateFactory;

public class real3rdFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(real3rdFactory.class.getName()));
	
		
	private static Map<String,measurePoint> measurePoints= new ConcurrentHashMap<String, measurePoint>();
			
	public static void init()
	{
		
		//1.配置公共测点表
		initPointConfig();
		
		//2.初始化第三方合作公共库
		CooperateFactory.init();
	}
	
	
	public static void initPointConfig()
	{
		try
		{
			Element root = CoreConfig.getRootElement("thirdRealData.xml");
			if (root == null) {
				return ;
			}
				
			List<Element> list = root.getChildren("point");
			for (Element e : list) {

				measurePoint pInfo = new measurePoint();
				String addr = e.getAttribute("addr").getValue();
				String type = e.getAttribute("type").getValue();
				pInfo.setAddr(Integer.parseInt(addr));
				pInfo.setType(Integer.parseInt(type));

				measurePoints.put(type + "_" + addr, pInfo);
			}
			logger.info(LogUtil.addExtLog("MapThirdRealData.size"), measurePoints.size());
		
		} catch (Exception e) {
			logger.error(LogUtil.addExtLog("exception"), e.getStackTrace());
		}
		
	}

	public  static Map<String,measurePoint> getmeasurePoints()
	{
		return measurePoints;
	}

}
