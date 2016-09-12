package com.nbl.utils.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * @author xuchu-tang
 * @createdate 2016年3月16日
 * @version 1.0 
 * @description :
 */
public class ProductFactory {
	
	private static DefaultMQProducer portal2UserProducerInstance;
	
	private static DefaultMQPushConsumer portal2UserConsumerInstance;
	
	private static DefaultMQProducer bussinessProducerInstance;
	
	private static DefaultMQPushConsumer bussinessConsumerInstance;
	
	private static DefaultMQProducer userProducerInstance;
	
	private static DefaultMQPushConsumer userConsumerInstance;
	
	private static DefaultMQProducer mpProducerInstance;
	
	private static DefaultMQPushConsumer mpConsumerInstance;
	
	/**
	 * @return
	 * @description:门户对用户系统的生产者创建
	 */
	private static DefaultMQProducer createPortal2UserProducerInstance(){
		
		portal2UserProducerInstance = new DefaultMQProducer();
		portal2UserProducerInstance.setProducerGroup(RocketmqConfig.Portal2User_Producer_GroupName);
		portal2UserProducerInstance.setNamesrvAddr(RocketmqConfig.NameServiceAddr);
		try {
			portal2UserProducerInstance.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		return portal2UserProducerInstance;
	}
	
	/**
	 * @return
	 * @description:获取门户对用户系统的生产者
	 */
	public static DefaultMQProducer getPortal2UserProducerInstance(){
		if(portal2UserProducerInstance == null){
			createPortal2UserProducerInstance();
			return portal2UserProducerInstance;
		}else{
			return portal2UserProducerInstance;
		}
	}
	
	/**
	 * @return
	 * @description:门户对用户系统的消费者创建
	 */
	private static DefaultMQPushConsumer createPortal2UserConsumerInstance(){
		
		portal2UserConsumerInstance = new DefaultMQPushConsumer();
		portal2UserConsumerInstance.setConsumerGroup(RocketmqConfig.Portal2User_Consumer_GroupName);
		portal2UserConsumerInstance.setNamesrvAddr(RocketmqConfig.NameServiceAddr);
		try {
			portal2UserConsumerInstance.subscribe(RocketmqConfig.Portal_Receive_User_Topic, "*");
		} catch (MQClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		portal2UserConsumerInstance.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		return portal2UserConsumerInstance;
	}
	
	/**
	 * @return
	 * @description:获取门户对用户系统的消费者
	 */
	public static DefaultMQPushConsumer getPortal2UserConsumerInstance(){
		if(portal2UserConsumerInstance == null){
			createPortal2UserConsumerInstance();
			return portal2UserConsumerInstance;
		}else{
			return portal2UserConsumerInstance;
		}
	}

	
	/**
	 * @return
	 * @description:业务系统生产者创建
	 */
	private static DefaultMQProducer createBussinessInstance(){
		
		bussinessProducerInstance = new DefaultMQProducer();
		bussinessProducerInstance.setProducerGroup(RocketmqConfig.Bussiness_Producer_GroupName);
		bussinessProducerInstance.setNamesrvAddr(RocketmqConfig.NameServiceAddr);
		try {
			bussinessProducerInstance.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		return bussinessProducerInstance;
	}
	
	/**
	 * @return
	 * @description:获取业务系统生产者
	 */
	public static DefaultMQProducer getBussinessProducerInstance(){
		if(bussinessProducerInstance == null){
			createBussinessInstance();
			return bussinessProducerInstance;
		}else{
			return bussinessProducerInstance;
		}
	}

	/**
	 * @return
	 * @description:用户系统生产者创建
	 */
	private static DefaultMQProducer createUserInstance(){
		
		userProducerInstance = new DefaultMQProducer();
		userProducerInstance.setProducerGroup(RocketmqConfig.User_Producer_GroupName);
		userProducerInstance.setNamesrvAddr(RocketmqConfig.NameServiceAddr);
		try {
			userProducerInstance.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		return userProducerInstance;
	}
	
	/**
	 * @return
	 * @description:获取用户系统生产者
	 */
	public static DefaultMQProducer getUserProducerInstance(){
		if(userProducerInstance == null){
			createUserInstance();
			return userProducerInstance;
		}else{
			return userProducerInstance;
		}
	}

	/**
	 * @return
	 * @description:管理系统生产者创建
	 */
	private static DefaultMQProducer createMpInstance(){
		
		mpProducerInstance = new DefaultMQProducer();
		mpProducerInstance.setProducerGroup(RocketmqConfig.Mp_Producer_GroupName);
		mpProducerInstance.setNamesrvAddr(RocketmqConfig.NameServiceAddr);
		try {
			mpProducerInstance.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		return mpProducerInstance;
	}
	
	/**
	 * @return
	 * @description:获取管理系统生产者
	 */
	public static DefaultMQProducer getMpProducerInstance(){
		if(mpProducerInstance == null){
			createMpInstance();
			return mpProducerInstance;
		}else{
			return mpProducerInstance;
		}
	}


}
