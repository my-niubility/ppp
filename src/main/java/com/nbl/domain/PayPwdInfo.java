package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class PayPwdInfo implements Serializable {

	private static final long serialVersionUID = -8654023636983276952L;

	/**
	 * 设置类型：1-设置交易密码、2-修改交易密码、3-设置新交易密码（忘记密码）
	 */
	@NotBlank(message = "设置类型不能为空")
	private String setType;
	/**
	 * 原交易密码
	 */
	private String orgPayPwd;
	/**
	 * 新交易密码
	 */
	@NotBlank(message = "新交易密码不能为空")
	private String newPayPwd;
	/**
	 * 短信验证码
	 */
	private String msgIdenCode;
	/**
	 * 个人客户编号
	 */
	@NotBlank(message = "个人客户编号不能为空")
	private String custId;
	/**
	 * 用户登录手机号
	 */
	@NotBlank(message = "手机号不能为空")
	private String phoneNum;

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public String getOrgPayPwd() {
		return orgPayPwd;
	}

	public void setOrgPayPwd(String orgPayPwd) {
		this.orgPayPwd = orgPayPwd;
	}

	public String getNewPayPwd() {
		return newPayPwd;
	}

	public void setNewPayPwd(String newPayPwd) {
		this.newPayPwd = newPayPwd;
	}

	public String getMsgIdenCode() {
		return msgIdenCode;
	}

	public void setMsgIdenCode(String msgIdenCode) {
		this.msgIdenCode = msgIdenCode;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Override
	public String toString() {
		return "PayPwdInfo [setType=" + setType + ", orgPayPwd=" + orgPayPwd + ", newPayPwd=" + newPayPwd
				+ ", msgIdenCode=" + msgIdenCode + ", custId=" + custId + ", phoneNum=" + phoneNum + "]";
	}

}
