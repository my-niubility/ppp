package com.nbl.utils.rocketmq;

/**
 * @author xuchu-tang
 * @createdate 2016年3月16日
 * @version 1.0 
 * @description :mq常规消息配置类
 */
public class RocketmqConfig {

	/**
	 * 门户往用户系统的消息发送组名称
	 */
	public static final String Portal2User_Producer_GroupName = "portal2UserSendGroupName";
	
	/**
	 * 门户对用户系统的消息接收组名称
	 */
	public static final String Portal2User_Consumer_GroupName = "portal2UserReceiveGroupName";
	
	/**
	 * 业务系统消息发送组名称
	 */
	public static final String Bussiness_Producer_GroupName = "bussinessSendGroupName";
	
	/**
	 * 业务系统消息接收组名称
	 */
	public static final String Bussiness_Consumer_GroupName = "bussinessReceiveGroupName";

	/**
	 * 用户系统消息发送组名称
	 */
	public static final String User_Producer_GroupName = "userSendGroupName";
	
	/**
	 * 用户系统消息接收组名称
	 */
	public static final String User_Consumer_GroupName = "userReceiveGroupName";

	/**
	 * 管理系统消息发送组名称
	 */
	public static final String Mp_Producer_GroupName = "mpSendGroupName";
	
	/**
	 * 管理系统消息接收组名称
	 */
	public static final String Mq_Consumer_GroupName = "mpReceiveGroupName";
	
	/**
	 * 门户发送用户的topic
	 */
	public static final String Portal_Send_User_Topic = "PortalSendUserTopic";

	/**
	 * 门户接收用户的topic
	 */
	public static final String Portal_Receive_User_Topic = "PortalReceiveUserTopic";
	
	/**
	 * 业务发送topic
	 */
	public static final String Bussiness_Send_Topic = "BussinessSendTopic";

	/**
	 * 业务接收topic
	 */
	public static final String Bussiness_Receive_Topic = "BussinessReceiveTopic";
	
	/**
	 * 用户发送topic
	 */
	public static final String User_Send_Topic = "UserSendTopic";

	/**
	 * 用户接收topic
	 */
	public static final String User_Receive_Topic = "UserReceiveTopic";

	/**
	 * 管理发送topic
	 */
	public static final String Mp_Send_Topic = "MpSendTopic";

	/**
	 * 管理接收topic
	 */
	public static final String Mp_Receive_Topic = "MpReceiveTopic";


	/**
	 * 注册服务器地址
	 */
	public static final String NameServiceAddr = "192.168.98.128:9876";

	
}
