package com.ormcore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * spring容器
 * @author 
 * 2014-11-27 上午11:23:06
 */
public class GameContext {
	private static final Logger logger = LoggerFactory.getLogger(GameContext.class);
	
	/**
	 * spring 配置文件路径
	 */
	private static String confPath = 
		System.getProperty("file.separator")
		+ System.getProperty("user.dir")
		+ System.getProperty("file.separator") + "conf"
		+ System.getProperty("file.separator");
	
	/*private static String confPath = GameContext.getConfPath();
	
	private static String getConfPath(){
		String s = GameContext.class.getResource("").getPath();
		s = s.substring(1, s.indexOf("gate") + 4);
		//s+= System.getProperty("user.dir");
		s+= System.getProperty("file.separator") + "conf";
		s+= System.getProperty("file.separator");
		return s;
	}*/
	/**
	 * spring容器
	 */
	private static ApplicationContext context = null;
	
    public GameContext(String path)
    {
    	if (path != null) {
    		if ("".equals(path)) path = this.getClass().getResource("/").getPath();
    		confPath = System.getProperty("file.separator")
    				+ path + System.getProperty("file.separator") + "conf"
    				+ System.getProperty("file.separator");
    	}
		logger.info("confPath：{}",confPath);
		
		String[] files = {
				 confPath+"applicationContext.xml"
				 ,confPath+"mybatis.xml"
				 ,confPath+"applicationContext-task.xml"
				};
		
		for(int i=0; i<files.length;i++)
		{
		logger.info("file：{}",files[i]);
		}
		context = new FileSystemXmlApplicationContext(files); 
		logger.info("初始化spring配置结束...");
    }
	
	/**
	 * 获取Spring容器管理的对象
	 * @author haojian
	 * Apr 8, 2013 4:46:11 PM
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName){
		Object obj = context.getBean(beanName);
		return obj;
	}
	
	public static void main(String[] args){
		String s = GameContext.class.getResource("").getPath();
		s = s.substring(1, s.indexOf("EpGateTrunk"));
		System.out.println(s);
	}
}
