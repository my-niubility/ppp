package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class TokenReqInfo implements Serializable {

	private static final long serialVersionUID = -6555017970447091691L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 业务场景01-支付
	 */
	@NotBlank(message = "业务场景不能为空")
	private String busCase;
	/**
	 * 交易订单号
	 */
	private String tradeOrderId;

	public TokenReqInfo() {
		super();
	}

	public TokenReqInfo(String custId) {
		super();
		this.custId = custId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getBusCase() {
		return busCase;
	}

	public void setBusCase(String busCase) {
		this.busCase = busCase;
	}

	public String getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(String tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	@Override
	public String toString() {
		return "TokenReqInfo [custId=" + custId + ", busCase=" + busCase + ", tradeOrderId=" + tradeOrderId + "]";
	}

}
