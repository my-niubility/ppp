package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class UserQueryInfo implements Serializable {

	private static final long serialVersionUID = 190628985108635544L;
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
		return "UserQueryInfo [custId=" + custId + "]";
	}

}
