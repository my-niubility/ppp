package com.nbl.domain;

import java.io.Serializable;

public class InvestHistoryInfo implements Serializable {

	private static final long serialVersionUID = 5207049946247033360L;

	/**
	 * 客户编号
	 */
	private String custId;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Override
	public String toString() {
		return "InvestHistoryInfo [custId=" + custId + "]";
	}

}
