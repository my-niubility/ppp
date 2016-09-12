package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class BalanceQueryInfo implements Serializable {
	private static final long serialVersionUID = 1316276549080722955L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Override
	public String toString() {
		return "BalanceQueryInfo [custId=" + custId + "]";
	}
}
