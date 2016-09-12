package com.nbl.domain;

import java.io.Serializable;

public class CanlTrdOrdInfo implements Serializable {

	private static final long serialVersionUID = 5319990392801049634L;

	/**
	 * 客户编号
	 */
	private String custId;
	/**
	 * 交易订单号
	 */
	private String tradeOrderId;
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
		return "CanlTrdOrdInfo [custId=" + custId + ", tradeOrderId=" + tradeOrderId + "]";
	}

}
