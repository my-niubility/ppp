package com.nbl.domain;

import java.io.Serializable;

public class QryBnkCrdInfo implements Serializable {

	private static final long serialVersionUID = -8982364937024552065L;

	/**
	 * 用户编号
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
		return "QryBnkCrdInfo [custId=" + custId + "]";
	}
}
