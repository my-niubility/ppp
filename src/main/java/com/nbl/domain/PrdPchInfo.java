package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author AlanMa
 *
 */
public class PrdPchInfo implements Serializable {

	private static final long serialVersionUID = 3108731735088380083L;
	/**
	 * 用户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 产品编号
	 */
	@NotBlank(message = "产品编号不能为空")
	private String productId;

	// /**
	// * 资产管理人客户号
	// */
	// @NotBlank(message = "资产管理人客户号不能为空")
	// private String assetCustId;

	/**
	 * 转让方客户号
	 */
	private String transferCustId;

	/**
	 * 订单类别:0:投资 ,1:协议转让 ,2:划款 ,3:融资人还款 ,4:资管人还款 ,5:资管人退款 ,6:补贴发放
	 */
	@NotBlank(message = "订单类别不能为空")
	private String invenstType;

	/**
	 * 红包抵扣金额
	 */
	@NotBlank(message = "交易总额不能为空")
	private String redEnvAmt;

	/**
	 * 利息（用于还款）
	 */
	private String interest;

	/**
	 * 本金
	 */
	private String principal;

	/**
	 * 购买单位数
	 */
	@NotBlank(message = "购买单位数不能为空")
	private String purchasePortion;

	/**
	 * 期次（用于还款付息）
	 */
	private String repayTerm;

	/**
	 * 转让ID（关联转让表）
	 */
	private String transferId;

	/**
	 * 渠道编码
	 */
	@NotBlank(message = "购买渠道编码不能为空")
	private String channelCode;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId == null ? null : productId.trim();
	}

	public String getTransferCustId() {
		return transferCustId;
	}

	public void setTransferCustId(String transferCustId) {
		this.transferCustId = transferCustId == null ? null : transferCustId.trim();
	}

	public String getInvenstType() {
		return invenstType;
	}

	public void setInvenstType(String invenstType) {
		this.invenstType = invenstType;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getPurchasePortion() {
		return purchasePortion;
	}

	public void setPurchasePortion(String purchasePortion) {
		this.purchasePortion = purchasePortion;
	}

	public String getRepayTerm() {
		return repayTerm;
	}

	public void setRepayTerm(String repayTerm) {
		this.repayTerm = repayTerm;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId == null ? null : transferId.trim();
	}

	public String getRedEnvAmt() {
		return redEnvAmt;
	}

	public void setRedEnvAmt(String redEnvAmt) {
		this.redEnvAmt = redEnvAmt;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String toString() {
		return "PrdPchInfo [custId=" + custId + ", productId=" + productId + ", transferCustId=" + transferCustId + ", invenstType=" + invenstType + ", redEnvAmt=" + redEnvAmt + ", interest="
				+ interest + ", principal=" + principal + ", purchasePortion=" + purchasePortion + ", repayTerm=" + repayTerm + ", transferId=" + transferId + ", channelCode=" + channelCode + "]";
	}

}
