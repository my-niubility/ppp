package com.nbl.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.nbl.common.exception.MyBusinessRuntimeException;
import com.nbl.utils.cache.BusinessCacheUtils;

/**
 * @author Donald
 * @createdate 2016年6月21日
 * @version 1.0 
 * @description :操作properties配置文件
 */
public class PropertiesReadUtils {
	
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(BusinessCacheUtils.class);
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * @param fileName
	 * @return
	 * @description:读取配置文件
	 */
	public static Properties readProperties(String fileName){
		
		Properties prop = new Properties();
		logger.info("------读取配置文件-------：{}",fileName);
		
        //读取属性文件a.properties
		InputStream in;
		try {
			Resource resource = resourceLoader.getResource( fileName );
			in = resource.getInputStream();
			prop.load(in);
	        //加载属性列表
	        Iterator<String> it=prop.stringPropertyNames().iterator();
	        while(it.hasNext()){
	            String key=it.next();
	            System.out.println(key+":"+prop.getProperty(key));
	        }
	        in.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new MyBusinessRuntimeException(e1.getMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
			throw new MyBusinessRuntimeException(e.getMessage()); 
		}   
        return prop;
	}
	
	/**
	 * @param fileName
	 * @param proParameter
	 * @return
	 * @description:设置配置文件
	 */
	public static boolean setProperties(String fileName, Map<String, String> proParameter){
		
		if(!proParameter.isEmpty()){
			
			Properties prop = new Properties();
			 ///保存属性到b.properties文件
			FileOutputStream oFile;
			try {
				//true表示追加打开
				oFile = new FileOutputStream(fileName, true);
				
				Set<String> keySet = proParameter.keySet();
				Iterator<String> it = keySet.iterator();
				while(it.hasNext()){
					String key = it.next();
					prop.setProperty(key, proParameter.get(key));
				}
				prop.store(oFile, "The New properties file");
				oFile.close();

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				throw new MyBusinessRuntimeException(e1.getMessage()); 
			} catch (IOException e) {
				e.printStackTrace();
				throw new MyBusinessRuntimeException(e.getMessage()); 
			}   

		}
		
		return true;
	}

}
