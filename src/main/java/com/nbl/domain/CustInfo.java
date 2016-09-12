package com.nbl.domain;

import java.io.Serializable;

public class CustInfo implements Serializable {

	private static final long serialVersionUID = -2401875244975480498L;
	/**
	 * 用户编号
	 */
	private String custId;
	
	public CustInfo() {
		super();
	}

	public CustInfo(String custId) {
		super();
		this.custId = custId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Override
	public String toString() {
		return "CustInfo [custId=" + custId + "]";
	}
}
