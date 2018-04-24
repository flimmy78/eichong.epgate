package com.cooperate.config;

//import com.google.common.base.Strings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.cooperate.ThirdFieldConfig;
import com.cooperate.utils.Strings;

/**
 * Created by zangyaoyi on 2016/12/30.
 */
public class ConfigManager {
   //系统配置文件
    private static Properties eChongProperties = null;

    private static ConfigManager propertiesManager = null;
    
    public static Properties getProperties(String fileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Properties properties = new Properties();
		try {
			properties.load(is);
			if(is!=null)is.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return properties;
	}
   
    public boolean init(String filename)
    {
    	try {
    		eChongProperties = getProperties(filename);
    		
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return false;
    }
    
    public static Map<String, ThirdFieldConfig> loadConfig()
	{
		return null;
	}

    /**
     * 初始化Manager
     */
    public static ConfigManager getMessageManager() {
        ConfigManager manager;
        if (propertiesManager == null) {
            manager = new ConfigManager();
        } else {
            manager = propertiesManager;
        }

        return manager;

    }

    public String getEChongProperties(String messageKey) {
        if (Strings.isNullOrEmpty(messageKey)) {
            return null;
        }
       
        return eChongProperties.getProperty(messageKey);

    }

    public String getEChongProperties(String messageKey,String defaultValue) {
        if (Strings.isNullOrEmpty(messageKey)) {
            return null;
        }
       
        return eChongProperties.getProperty(messageKey,defaultValue).trim();

    }
}
