package com.nbl.domain;

import java.io.Serializable;

public class RchgNoticeInfo implements Serializable {

	private static final long serialVersionUID = 1128772836055278968L;
	/**
	 * 账户余额
	 */
	private String balance;
	/**
	 * 交易结果信息
	 */
	private String resultInfo;
	/**
	 * 充值订单号（证联）
	 */
	private String rechargeId;
	/**
	 * 备注
	 */
	private String remark;

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	public String getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(String rechargeId) {
		this.rechargeId = rechargeId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "RchgNoticeDto [balance=" + balance + ", resultInfo=" + resultInfo + ", rechargeId=" + rechargeId
				+ ", remark=" + remark + "]";
	}
}
