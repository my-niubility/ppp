package com.nbl.controller.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbl.utils.HttpClientUtils;

public class TestPostSend {

	public static void main(String[] args) throws JsonProcessingException {
		// TODO Auto-generated method stub

		ObjectMapper objectMapper = new ObjectMapper();
		RequestBodyTest rt = new RequestBodyTest();
		rt.setName("userName");
		rt.setMessage("13999999999");
		rt.setName("password");
		rt.setMessage("123456");
		rt.setName("msgIdenCode");
		rt.setMessage("yzm001");
		String jsonreq = objectMapper.writeValueAsString(rt);
		System.out.println("jsonreq=" + jsonreq);

		Map paramMap = new HashMap();

		String url = "http://192.168.101.247:8083/zlebank-energy-portal/user/register";
		paramMap.put("req", jsonreq);
		HttpClientUtils.sendPost(url, paramMap, null, null);
	}

	// private static void test

}
