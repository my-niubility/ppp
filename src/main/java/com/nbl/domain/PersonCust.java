package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class PersonCust implements Serializable {

	private static final long serialVersionUID = 190628985108635544L;
	/**
	 * 登录名称
	 */
	@NotBlank(message = "登录名称不能为空")
	private String loginName;
	/**
	 * 登录密码
	 */
	@NotBlank(message = "登录密码不能为空")
	private String password;
	/**
	 * 手机号码
	 */
	@NotBlank(message = "手机号不能为空")
	private String mobile;
	/**
	 * 用户类型 CP：个人 CB：企业
	 */
	@NotBlank(message = "用户类型不能为空")
	private String custType;
	/**
	 * 短信验证码
	 */
	@NotBlank(message = "短信验证码不能为空")
	private String msgIdenCode;
	/**
	 * 注册渠道编码001：门户网站、002：IOS手机客户端、003：Android手机客户端、004：微信、005：微博、006：其他
	 */
	@NotBlank(message = "注册渠道编码不能为空")
	private String regChanCode;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getMsgIdenCode() {
		return msgIdenCode;
	}

	public void setMsgIdenCode(String msgIdenCode) {
		this.msgIdenCode = msgIdenCode;
	}

	public String getRegChanCode() {
		return regChanCode;
	}

	public void setRegChanCode(String regChanCode) {
		this.regChanCode = regChanCode;
	}

	@Override
	public String toString() {
		return "PersonCust [loginName=" + loginName + ", password=" + password + ", mobile=" + mobile + ", custType="
				+ custType + ", msgIdenCode=" + msgIdenCode + ", regChanCode=" + regChanCode + "]";
	}

}
