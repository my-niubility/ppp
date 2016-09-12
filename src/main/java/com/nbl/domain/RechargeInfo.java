package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class RechargeInfo implements Serializable {

	private static final long serialVersionUID = -7660338824126963857L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 短信验证码
	 */
	@NotBlank(message = "短信验证码不能为空")
	private String msgIdenCode;
	/**
	 * 支付密码
	 */
	@NotBlank(message = "支付密码不能为空")
	private String payPassword;

	/**
	 * 金额
	 */
	@NotBlank(message = "金额不能为空")
	private String rechargeAmt;
	/**
	 * 充值发起方式（01：绑卡充值，02：普通充值）【只针对线下转账】
	 */
	private String bingingCardType;
	/**
	 * 充值方式：00：网银充值、01：快捷充值、02：线下转账、03：其他
	 */
	@NotBlank(message = "充值方式不能为空")
	private String rechargeType;
	
	/**
	 * 渠道编码
	 */
	@NotBlank(message = "渠道编码不能为空")
	private String channelCode;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getRechargeAmt() {
		return rechargeAmt;
	}

	public void setRechargeAmt(String rechargeAmt) {
		this.rechargeAmt = rechargeAmt;
	}

	public String getBingingCardType() {
		return bingingCardType;
	}

	public void setBingingCardType(String bingingCardType) {
		this.bingingCardType = bingingCardType;
	}

	public String getMsgIdenCode() {
		return msgIdenCode;
	}

	public void setMsgIdenCode(String msgIdenCode) {
		this.msgIdenCode = msgIdenCode;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "RechargeInfo [custId=" + custId + ", msgIdenCode=" + msgIdenCode + ", payPassword=" + payPassword + ", rechargeAmt=" + rechargeAmt + ", bingingCardType=" + bingingCardType
				+ ", rechargeType=" + rechargeType + ", channelCode=" + channelCode + "]";
	}

}
