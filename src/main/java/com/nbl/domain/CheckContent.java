package com.nbl.domain;

import java.io.Serializable;

public class CheckContent implements Serializable{

	private static final long serialVersionUID = 5211184124302400676L;
	private String checkType;

	private String loginName;
	
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
}
