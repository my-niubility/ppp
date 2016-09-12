package com.nbl.utils.cache;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nbl.utils.PropertiesReadUtils;

/**
 * @author Donald
 * @createdate 2016年7月11日
 * @version 1.0 
 * @description :
 * 1、通过配置文件设置系统缓存使用规则，本地缓存(local),redis缓存(redis)
 * 2、配置文件normalConfig.properties
 */
@Service
public class BusinessCacheUtils {
	
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(BusinessCacheUtils.class);
	
	private final static String fileName = "classpath:properties/normalConfig.properties";
	//缓存标志：本地缓存(local),redis缓存(redis)
	private static String cacheFlagKey = "cacheFlag";
	private static String cacheFlag = null;
	
	static {
		
		//读取配置文件设置
		Properties p = PropertiesReadUtils.readProperties(fileName);
		
		cacheFlag = p.getProperty(cacheFlagKey);
		
		logger.info("---------cacheFlag----------"+cacheFlag);
	}
	
	/**
	 * @return
	 * @description:返回缓存标识----本地缓存(local),redis缓存(redis)
	 */
	public String getCacheFlag(){
		return cacheFlag;
	}
	
	
}
