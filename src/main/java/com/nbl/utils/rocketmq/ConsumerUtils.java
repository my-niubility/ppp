package com.nbl.utils.rocketmq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * @author xuchu-tang
 * @createdate 2016年3月16日
 * @version 1.0 
 * @description :消费者工具类
 */
public class ConsumerUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsumerUtils.class); 
	
	public static ConsumerUserDispatch consumerUserDispath = new ConsumerUserDispatch();

	private static DefaultMQPushConsumer portal2UserInstance = ProductFactory.getPortal2UserConsumerInstance();
	
	public static void consumerUserMessage(){
		//添加监听器
		portal2UserInstance.setMessageListener(new MessageListenerConcurrently() {  
            public ConsumeConcurrentlyStatus consumeMessage(  
                    List<MessageExt> list,  
                    ConsumeConcurrentlyContext Context) {
            	//默认设置每次只消费一条
                Message msg = list.get(0);  
                logger.info(msg.toString()); 
                
                //调用分发器进行业务逻辑的处理
                boolean flag = consumerUserDispath.dispatchAndHandle(msg);
                
                //返回消费结果
                if(flag){
                	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 
                }else{
                	return ConsumeConcurrentlyStatus.RECONSUME_LATER; 
                }
                 
            }  
        }
		);
		
		try {
			portal2UserInstance.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	

}
