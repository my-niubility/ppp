package com.nbl.controller.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestEhcache {
	private final static Logger logger = LoggerFactory.getLogger(TestEhcache.class); 
	@Resource
	private EhCacheCacheManager cacheManager;
	
	@RequestMapping(value= "/setEhcache")
	public void setRequsetValue(HttpServletRequest request, HttpServletResponse response){
		
		Cache userCache = cacheManager.getCache("userCache");
		List<Person> list = new ArrayList<Person>();
		Person p1 = new Person();
		p1.setName("name1");
		p1.setAge(30);
		p1.setAddress("太阳宫1");
		
		Person p2 = new Person();
		p2.setName("name2");
		p2.setAge(40);
		p2.setAddress("太阳宫2");
		list.add(p1);
		list.add(p2);
		userCache.put("test", list);
	}
	
	@RequestMapping(value= "/getEhcache")
	public void getRequsetValue(HttpServletRequest request, HttpServletResponse response){
		
		Cache userCache = cacheManager.getCache("userCache");
		ValueWrapper vlue = userCache.get("test");
		
		List list = (List)vlue.get();
		
		Iterator<Person> it = list.iterator();
		while(it.hasNext()){
			Person p= it.next();
			logger.info("person:{},{},{}",p.getName(),p.getAge(),p.getAddress());
		}
		
		//clear
		userCache.evict("test");
	}

}
