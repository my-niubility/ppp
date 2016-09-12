package com.nbl.utils.rocketmq;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * @author xuchu-tang
 * @createdate 2016年3月16日
 * @version 1.0 
 * @description :获取发送消息的工具类
 */
public class ProducerUtils {
	
	
	//门户生产者
	private static DefaultMQProducer portal2UserInstance = ProductFactory.getPortal2UserProducerInstance();
	
	/**
	 * @param serviceTags 发送消息标识，一般是消费者提供的服务名称（与消费者协商）
	 * @param sequceId 发送消息的流水号，要求唯一（便于后续日志的分析与查看）
	 * @param messBody 序列化之后的消息（消息格式与消费者协商）
	 * @return 返回发送至队列 的结果集
	 * @description:门户生产者发送队列
	 */
	public static Map<String,String> portalSendMessage2UserQueue(String serviceTags, String sequceId, byte[] messBody){
		
		Message msg = new Message();
		msg.setBody(messBody);
		msg.setKeys(sequceId);
		msg.setTags(serviceTags);
		msg.setTopic(RocketmqConfig.Portal_Send_User_Topic);
		
		SendResult res;
		try {
			res = portal2UserInstance.send(msg);
			return result2Map(res);
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	/**
	 * @param sr 发送队列返回的结果集
	 * @return 
	 * @description:转换包装结果集
	 */
	private static Map<String,String> result2Map(SendResult sr){
		Map<String,String> returnMap = new HashMap<String,String>();
		if("SEND_OK".equals(sr.toString())){
			returnMap.put("status", "SEND_OK");
		}else if("FLUSH_DISK_TIMEOUT".equals(sr.toString())){
			returnMap.put("status", "FLUSH_DISK_TIMEOUT");
		}else if("FLUSH_SLAVE_TIMEOUT".equals(sr.toString())){
			returnMap.put("status", "FLUSH_SLAVE_TIMEOUT");
		}else if("SLAVE_NOT_AVAILABLE".equals(sr.toString())){
			returnMap.put("status", "SLAVE_NOT_AVAILABLE");
		}
		returnMap.put("msgId", sr.getMsgId());
		returnMap.put("transactionId", sr.getTransactionId());
		return returnMap;
	}
}
