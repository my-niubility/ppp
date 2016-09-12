package com.nbl.utils;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.rocketmq.client.producer.SendStatus;

public class TestUtils {
	private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SendStatus ss = SendStatus.FLUSH_DISK_TIMEOUT;
		
		if("FLUSH_DISK_TIMEOUT".equals(ss.toString())){
			System.out.println("ss.same:"+ss);

		}
		map.put("ttt", "dd");
		
		map.remove("tt");
		map.remove("tt");
	}

}
