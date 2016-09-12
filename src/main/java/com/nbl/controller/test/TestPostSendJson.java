package com.nbl.controller.test;

import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbl.utils.HttpClientUtils;

public class TestPostSendJson {
	
	private final static Logger logger = LoggerFactory.getLogger(TestPostSendJson.class); 

	private static final String APPLICATION_JSON = "application/json";
    
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	
	public static void main(String[] args) throws Exception{
		
		String url = "http://192.168.98.128:8080/zlebank-energy-portal/restful/requestJson";
		ObjectMapper objectMapper = new ObjectMapper();
		RequestBodyTest rt = new RequestBodyTest();
		rt.setName(null);
		rt.setMessage("sunny");
		String jsonreq=objectMapper.writeValueAsString(rt);
		System.out.println("jsonreq="+jsonreq);

        jsonreq = StringEscapeUtils.unescapeHtml(jsonreq);
		httpPostWithJSON(url, jsonreq);

	}
	
	public static void httpPostWithJSON(String url, String json) throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();  
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
        StringEntity se = new StringEntity(json);
        se.setContentType(CONTENT_TYPE_TEXT_JSON);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
        httpPost.setEntity(se);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        
        try {  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
            	logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
            }
        } finally {  
        }  
    }
}
