package com.nbl.domain;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

public class SerFundQryInfo implements Serializable {

	private static final long serialVersionUID = 3645652030099022361L;
	/**
	 * 类型
	 */
	@NotBlank(message = "查询type不能为空")
	private String type;
	/**
	 * 用户编号（电话）
	 */
	private String userId;
	/**
	 * 客户编号
	 */
	@NotBlank(message = "用户custId不能为空")
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
	 * 第一条记录下标（从0开始）
	 */
	private int startIndex;
	/**
	 * 记录条数
	 */
	private int recordNum;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "SerFundQryInfo [type=" + type + ", userId=" + userId + ", custId=" + custId + ", beginDate=" + beginDate
				+ ", endDate=" + endDate + ", startIndex=" + startIndex + ", recordNum=" + recordNum + "]";
	}
}
