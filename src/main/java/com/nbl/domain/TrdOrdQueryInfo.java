package com.nbl.domain;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

public class TrdOrdQueryInfo implements Serializable {

	private static final long serialVersionUID = -5870205993692029197L;

	/**
	 * 客户编号
	 */
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	/**
	 * 起始日期
	 */
	private Date beginDate;
	/**
	 * 终止日期
	 */
	private Date endDate;
	/**
	 * 状态：00:交易中、01:已付款、02:支付失败、03:已取消（不传默认查全部状态）
	 */
	private String status;
	/**
	 * 第一条记录下标（从0开始）
	 */
	private int startIndex;
	/**
	 * 记录条数
	 */
	private int recordNum;
	/**
	 * 排序标识1-升序，0-降序
	 */
	private String orderFlag;
	/**
	 * 排序内容：1-订单号，2-购买时间，3-购买金额（如不传则默认按订单号倒序排列）
	 */
	private String orderColumn;
	/**
	 * 交易订单号
	 */
	private String tradeOrderId;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	public String getOrderFlag() {
		return orderFlag;
	}

	public void setOrderFlag(String orderFlag) {
		this.orderFlag = orderFlag;
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	public String getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(String tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	@Override
	public String toString() {
		return "TrdOrdQueryInfo [custId=" + custId + ", beginDate=" + beginDate + ", endDate=" + endDate + ", status="
				+ status + ", startIndex=" + startIndex + ", recordNum=" + recordNum + ", orderFlag=" + orderFlag
				+ ", orderColumn=" + orderColumn + ", tradeOrderId=" + tradeOrderId + "]";
	}
}
