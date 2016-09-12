package com.nbl.utils.cache;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户登录信息存储（ 测试暂用，不适用集群环境)
 * 
 * @author AlanMa
 *
 */
public class LoginCache {
	public static Set<String> custIds = new HashSet<String>();
	
	public static Set<String> sessionIds = new HashSet<String>();
}
