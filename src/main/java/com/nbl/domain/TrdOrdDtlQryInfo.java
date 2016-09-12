package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class TrdOrdDtlQryInfo implements Serializable {

	private static final long serialVersionUID = -8526528480328491893L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 产品编号
	 */
	@NotBlank(message = "产品编号不能为空")
	private String productId;
	/**
	 * 支付订单号
	 */
	@NotBlank(message = "支付订单号不能为空")
	private String payOrderNo;
	/**
	 * 交易订单号
	 */
	@NotBlank(message = "交易订单号不能为空")
	private String tradeOrderId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPayOrderNo() {
		return payOrderNo;
	}

	public void setPayOrderNo(String payOrderNo) {
		this.payOrderNo = payOrderNo;
	}

	public String getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(String tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	@Override
	public String toString() {
		return "TrdOrdDtlQryInfo [productId=" + productId + ", payOrderNo=" + payOrderNo + ", tradeOrderId="
				+ tradeOrderId + "]";
	}

}
