package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class WithdrawInfo implements Serializable {

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
	private String withdrawAmt;
	/**
	 * 提现方式：01：普通提现 02：快速提现
	 */
	@NotBlank(message = "提现方式不能为空")
	private String withdrawType;
	/**
	 * 渠道编号
	 */
	@NotBlank(message = "渠道编号不能为空")
	private String channelCode;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
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

	public String getWithdrawAmt() {
		return withdrawAmt;
	}

	public void setWithdrawAmt(String withdrawAmt) {
		this.withdrawAmt = withdrawAmt;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "WithdrawInfo [custId=" + custId + ", msgIdenCode=" + msgIdenCode + ", payPassword=" + payPassword + ", withdrawAmt=" + withdrawAmt + ", withdrawType=" + withdrawType + ", channelCode="
				+ channelCode + "]";
	}

}
