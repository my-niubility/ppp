package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class MessageQryInfo implements Serializable{
	
	private static final long serialVersionUID = -9132761930674808204L;
	
    //消息接收者custId
    @NotBlank(message = "消息接收者custId不能为空")
    private String custId;
    
    //消息类型(0:系统消息 1：业务消息 null为所有类型消息)
    private String messageType;
    
    //分页时第一条记录下标（从0开始）
	private int startIndex;
	//分页时记录条数
	private int recordNum;


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

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	@Override
	public String toString() {
		return "MessageQryInfo [custId=" + custId + ", messageType=" + messageType + ", startIndex=" + startIndex
				+ ", recordNum=" + recordNum + "]";
	}

	


	
 	
 	
}