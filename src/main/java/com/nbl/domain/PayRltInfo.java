package com.nbl.domain;

import java.io.Serializable;

public class PayRltInfo implements Serializable {

	private static final long serialVersionUID = -7115953740221400922L;
	/**
	 * 第三方支付流水号
	 */
	private String payThdSeqNum;
	/**
	 * 付款人账号（第三方管理）
	 */
	private String paymentAcc;
	/**
	 * 交易日期
	 */
	private String tradeDate;
	/**
	 * 交易金额
	 */
	private Long amount;

	/**
	 * 支付结果信息
	 */
	private String resultInfo;

	public String getPayThdSeqNum() {
		return payThdSeqNum;
	}

	public void setPayThdSeqNum(String payThdSeqNum) {
		this.payThdSeqNum = payThdSeqNum;
	}

	public String getPaymentAcc() {
		return paymentAcc;
	}

	public void setPaymentAcc(String paymentAcc) {
		this.paymentAcc = paymentAcc;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	@Override
	public String toString() {
		return "PayRltInfo [payThdSeqNum=" + payThdSeqNum + ", paymentAcc=" + paymentAcc + ", tradeDate=" + tradeDate
				+ ", amount=" + amount + ", resultInfo=" + resultInfo + "]";
	}

}
