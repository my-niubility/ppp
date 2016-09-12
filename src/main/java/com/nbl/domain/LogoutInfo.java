package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author maxinwei
 * @createdate 2016年4月11日
 * @version 1.0
 * @description :用户注销信息
 */
public class LogoutInfo implements Serializable {

	private static final long serialVersionUID = 9082413651316250594L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 手机号码
	 */
	@NotBlank(message = "手机号不能为空")
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "LogoutInfo [custId=" + custId + ", mobile=" + mobile + "]";
	}

}
