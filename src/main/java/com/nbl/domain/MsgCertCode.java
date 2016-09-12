package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class MsgCertCode implements Serializable {

	private static final long serialVersionUID = 3833719684422234970L;
	@NotBlank(message = "手机号不能为空")
	private String phoneNum;
	@NotBlank(message = "短信验证码场景不能为空")
	private String certBusCase;
	private String custId;
	private String tradeOrderId;

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getCertBusCase() {
		return certBusCase;
	}

	public void setCertBusCase(String certBusCase) {
		this.certBusCase = certBusCase;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(String tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	@Override
	public String toString() {
		return "MsgCertCode [phoneNum=" + phoneNum + ", certBusCase=" + certBusCase + ", custId=" + custId + ", tradeOrderId=" + tradeOrderId + "]";
	}

}
