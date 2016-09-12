package com.nbl.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbl.common.exception.MyBusinessRuntimeException;

/**
 * @author xuchu-tang
 * @version 1.0, 2015年12月12日
 * @description httpclient 工具类，如有不满足可自行扩展
 */
public class HttpClientUtils {

	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	/**
	 * @param url
	 *            形如：http://ip:port/xxx/xxx
	 * @param paramMap
	 *            post提交的参数
	 * @param charset
	 *            设置特点字符集，默认为utf-8
	 * @param contentType
	 *            内容类型，默认为application/json;charset=utf-8
	 * @return 返回http 的状态（status）、回执内容(entity-->HttpEntity
	 *         可根据自己需求解析HttpEntity对象)
	 */

	public static Map<String, Object> sendPost(String url, Map<String, String> paramMap, String charset, String contentType) {

		if (url == null) {
			logger.error("url is null ");
			throw new MyBusinessRuntimeException("url is null");
		}
		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Content-Type", contentType == null ? "application/json;charset=utf-8" : contentType);
		// 创建参数队列
		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
		// 处理参数
		for (Entry<String, String> entry : paramMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			formparams.add(new BasicNameValuePair(key, value));

		}
		// return map
		Map<String, Object> retMap = new HashMap<String, Object>();
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, charset == null ? "UTF-8" : charset);
			httppost.setEntity(uefEntity);
			logger.info("executing request " + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));
				}
				retMap.put("status", response.getStatusLine());
				retMap.put("entity", entity);
			} finally {
				closeConnect(response);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnect(httpclient);
		}

		return retMap;
	}

	/**
	 * @param url
	 *            形如：http://ip:port/xxx/xxx?key=value
	 * @param charset
	 *            设置特点字符集，默认为utf-8
	 * @return 返回http 的状态（status）、回执内容(entity-->HttpEntity
	 *         可根据自己需求解析HttpEntity对象)
	 * 
	 */
	public static Map<String, Object> sendGet(String url, String charset) {
		if (url == null) {
			logger.error("url is null ");
			throw new MyBusinessRuntimeException("url is null");
		}

		CloseableHttpClient httpclient = HttpClients.createDefault();
		// return map
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			logger.info("executing request " + httpget.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				// 打印响应状态
				logger.info("Response status: " + response.getStatusLine());
				if (entity != null) {
					// 打印响应内容
					logger.info("Response content: " + EntityUtils.toString(entity));
				}
				retMap.put("status", response.getStatusLine());
				retMap.put("entity", entity);

			} finally {
				closeConnect(response);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnect(httpclient);
		}

		return retMap;

	}

	/**
	 * @param response
	 *            关闭链接
	 */
	private static void closeConnect(CloseableHttpResponse response) {

		try {
			response.close();
		} catch (IOException e) {
			logger.error("close connection falure");
			e.printStackTrace();
		}
	}

	/**
	 * @param httpclient
	 *            关闭链接
	 */
	private static void closeConnect(CloseableHttpClient httpclient) {

		try {
			httpclient.close();
		} catch (IOException e) {
			logger.error("close connection falure");
			e.printStackTrace();
		}
	}

	/**
	 * 获取客户端IP
	 * 
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}

	// public static void main(String[] args) {
	// //
	// Name=hujin&Passwd=hujin&Phone=18210219964&Content=%E6%B5%8B%E8%AF%95%E4%BA%92%E9%87%91001
	// Map<String, String> paramMap = new HashMap<String, String>();
	// paramMap.put("Name", "hujin");
	// paramMap.put("Passwd", "hujin");
	// paramMap.put("Phone", "18210219964");
	// paramMap.put("Content", "%E6%B5%8B%E8%AF%95%E4%BA%92%E9%87%91003");
	//
	// Map result = sendPost("http://192.168.101.134/message/sms.http.php",
	// paramMap, null);
	// System.out.println("=========:" + result.toString());
	// }

}
