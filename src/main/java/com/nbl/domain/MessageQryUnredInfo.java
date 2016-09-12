package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class MessageQryUnredInfo implements Serializable{
	
	private static final long serialVersionUID = 763575002442009558L;
	//消息接收者custId
    @NotBlank(message = "消息接收者custId不能为空")
    private String custId;
    private String messageType;
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	@Override
	public String toString() {
		return "MessageQryUnredInfo [custId=" + custId + ", messageType=" + messageType + "]";
	}
	
    

  




	
 	
 	
}