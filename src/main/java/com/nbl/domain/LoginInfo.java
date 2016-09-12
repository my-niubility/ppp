package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;
/**
 * @author maxinwei
 * @createdate 2016年4月11日
 * @version 1.0 
 * @description :用户登录信息
 */
public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 9082413651316250594L;
	/**
	 * 验证码
	 */
	@NotBlank(message = "验证码不能为空")
	private String certCode;
	/**
	 * 登录密码
	 */
	@NotBlank(message = "登录密码不能为空")
	private String password;
	/**
	 * 手机号码
	 */
	@NotBlank(message = "手机号码不能为空")
	private String mobile;

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "LoginInfo [certCode=" + certCode + ", password=" + password + ", mobile=" + mobile + "]";
	}
}
