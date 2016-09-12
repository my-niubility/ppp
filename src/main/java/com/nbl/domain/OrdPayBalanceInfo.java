package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class OrdPayBalanceInfo implements Serializable {

	private static final long serialVersionUID = -5543097761191294096L;
	/**
	 * 标记
	 */
	@NotBlank(message = "Token不能为空")
	private String token;
	/**
	 * 交易订单号
	 */
	@NotBlank(message = "交易订单号不能为空")
	private String tradeOrderId;
	/**
	 * 支付类型（01：划款、02：资产管理人还款 03：资产管理人退款 04：融资人还款 05：担保人还款06：投资购买 07：转让购买
	 * 08：补贴发放）
	 */
	@NotBlank(message = "支付类型不能为空")
	private String fundsType;
	/**
	 * 实际金额
	 */
	@NotBlank(message = "实际金额不能为空")
	private String tradeAmt;
	/**
	 * 付款人客户编号
	 */
	@NotBlank(message = "付款人客户编号不能为空")
	private String custId;
	/**
	 * 交易总额
	 */
	@NotBlank(message = "交易总额不能为空")
	private String tradeTalAmt;
	/**
	 * 红包抵扣金额
	 */
	@NotBlank(message = "红包抵扣金额不能为空")
	private String redEnvAmt;

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
	 * 支付渠道编码
	 */
	@NotBlank(message = "支付渠道编码不能为空")
	private String channelCode;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(String tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	public String getFundsType() {
		return fundsType;
	}

	public void setFundsType(String fundsType) {
		this.fundsType = fundsType;
	}

	public String getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(String tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getTradeTalAmt() {
		return tradeTalAmt;
	}

	public void setTradeTalAmt(String tradeTalAmt) {
		this.tradeTalAmt = tradeTalAmt;
	}

	public String getRedEnvAmt() {
		return redEnvAmt;
	}

	public void setRedEnvAmt(String redEnvAmt) {
		this.redEnvAmt = redEnvAmt;
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

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "OrdPayBalanceInfo [token=" + token + ", tradeOrderId=" + tradeOrderId + ", fundsType=" + fundsType + ", tradeAmt=" + tradeAmt + ", custId=" + custId + ", tradeTalAmt=" + tradeTalAmt
				+ ", redEnvAmt=" + redEnvAmt + ", msgIdenCode=" + msgIdenCode + ", payPassword=" + payPassword + ", channelCode=" + channelCode + "]";
	}

}
