package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class CertiInfo implements Serializable {

	private static final long serialVersionUID = 5327114223616771729L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 账户类型(01：客户 02：商户)
	 */
	@NotBlank(message = "账户类型不能为空")
	private String custAccType;
	/**
	 * 姓名
	 */
	@NotBlank(message = "姓名不能为空")
	private String cardName;
	/**
	 * 证件类型
	 */
	@NotBlank(message = "证件类型不能为空")
	private String credentialsType;
	/**
	 * 证件编号
	 */
	@NotBlank(message = "证件编号不能为空")
	private String identityCardNumber;
	/**
	 * 银行编码
	 */
	@NotBlank(message = "银行编码不能为空")
	private String bankType;
	/**
	 * 卡号
	 */
	@NotBlank(message = "卡号不能为空")
	private String cardNo;
	/**
	 * 预留手机号
	 */
	@NotBlank(message = "预留手机号不能为空")
	private String phoneNum;
	/**
	 * 短信验证码
	 */
	@NotBlank(message = "短信验证码不能为空")
	private String msgIdenCode;
	/**
	 * 用户昵称
	 */
	@NotBlank(message = "用户昵称")
	private String nickname;
	/**
	 * 认证渠道编码001：门户网站、002：IOS手机客户端、003：Android手机客户端、004：微信、005：微博、006：其他
	 */
	@NotBlank(message = "注册渠道编码不能为空")
	private String regChanCode;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustAccType() {
		return custAccType;
	}

	public void setCustAccType(String custAccType) {
		this.custAccType = custAccType;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	public String getIdentityCardNumber() {
		return identityCardNumber;
	}

	public void setIdentityCardNumber(String identityCardNumber) {
		this.identityCardNumber = identityCardNumber;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getMsgIdenCode() {
		return msgIdenCode;
	}

	public void setMsgIdenCode(String msgIdenCode) {
		this.msgIdenCode = msgIdenCode;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRegChanCode() {
		return regChanCode;
	}

	public void setRegChanCode(String regChanCode) {
		this.regChanCode = regChanCode;
	}

	@Override
	public String toString() {
		return "CertiInfo [custId=" + custId + ", custAccType=" + custAccType + ", cardName=" + cardName + ", credentialsType=" + credentialsType + ", identityCardNumber=" + identityCardNumber
				+ ", bankType=" + bankType + ", cardNo=" + cardNo + ", phoneNum=" + phoneNum + ", msgIdenCode=" + msgIdenCode + ", nickname=" + nickname + ", regChanCode=" + regChanCode + "]";
	}

}
