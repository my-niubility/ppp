package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class MessageQryDetailInfo implements Serializable{
	
	private static final long serialVersionUID = -7958521423352643946L;

	//消息接收者custId
    @NotBlank(message = "消息接收者custId不能为空")
    private String custId;
    
    //消息id
    @NotBlank(message = "消息id不能为空")
    private String messageId;
    
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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
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
		return "MessageQryDetailInfo [custId=" + custId + ", messageId=" + messageId + ", startIndex=" + startIndex
				+ ", recordNum=" + recordNum + "]";
	}



	


	
 	
 	
}