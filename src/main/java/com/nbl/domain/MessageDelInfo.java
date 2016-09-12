package com.nbl.domain;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.alibaba.fastjson.JSONObject;

public class MessageDelInfo implements Serializable{
	
	private static final long serialVersionUID = 2092558525450776383L;
	
	//消息接收者custId
    @NotBlank(message = "消息接收者custId不能为空")
    private String custId;
    
    //待删除的信息id
    //@NotBlank(message = "待删除的信息id集合不能为空")
    private List<String> messageIds;
    
    
	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public List<String> getMessageIds() {
		return messageIds;
	}

	public void setMessageIds(List<String> messageIds) {
		this.messageIds = messageIds;
	}

	@Override
	public String toString() {
		return "MessageDelInfo [custId=" + custId + ", messageIds=" + messageIds + "]";
	}
	
	/*public static void main(String[] args) {
		MessageDelInfo result = JSONObject.parseObject("{\"custId\":\"CP2016072500002\",\"messageIds\":[\"2016082300000515\",\"2016082300000518\"]}", MessageDelInfo.class);
		System.out.println(result.toString());
	}*/

	
 
	



	
 	
 	
}