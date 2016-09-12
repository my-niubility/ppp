package com.nbl.domain;

import java.io.Serializable;

public class MutiCondQryPrdsInfo implements Serializable {

	private static final long serialVersionUID = 7321093656013584748L;

	/**
	 * 产品类型：000-光伏、001-电能、002-基金、003-信托、004-众筹
	 */
	private String productType;
	/**
	 * 产品类型子类:201：货币型基金 202：债券型基 203：股票型基金 204：理财型基金
	 */
	private String productLittleType;
	/**
	 * 最小产品单价
	 */
	private String unitCostMinY;
	/**
	 * 最大产品单价
	 */
	private String unitCostMaxY;
	/**
	 * 最小年化收益
	 */
	private String expectEarnRateMin;
	/**
	 * 最大年化收益
	 */
	private String expectEarnRateMax;
	/**
	 * 最小锁定期限
	 */
	private String bLockPeriodMin;
	/**
	 * 最大锁定期限
	 */
	private String bLockPeriodMax;
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
	 * 排序内容：1-创建时间，2-年化收益率，3-产品单价，4-锁定期（如不传则默认按订产品编号倒序排列）
	 */
	private String orderColumn;

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getExpectEarnRateMin() {
		return expectEarnRateMin;
	}

	public void setExpectEarnRateMin(String expectEarnRateMin) {
		this.expectEarnRateMin = expectEarnRateMin;
	}

	public String getExpectEarnRateMax() {
		return expectEarnRateMax;
	}

	public void setExpectEarnRateMax(String expectEarnRateMax) {
		this.expectEarnRateMax = expectEarnRateMax;
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

	public String getProductLittleType() {
		return productLittleType;
	}

	public void setProductLittleType(String productLittleType) {
		this.productLittleType = productLittleType;
	}

	public String getbLockPeriodMin() {
		return bLockPeriodMin;
	}

	public void setbLockPeriodMin(String bLockPeriodMin) {
		this.bLockPeriodMin = bLockPeriodMin;
	}

	public String getbLockPeriodMax() {
		return bLockPeriodMax;
	}

	public void setbLockPeriodMax(String bLockPeriodMax) {
		this.bLockPeriodMax = bLockPeriodMax;
	}

	public String getUnitCostMinY() {
		return unitCostMinY;
	}

	public void setUnitCostMinY(String unitCostMinY) {
		this.unitCostMinY = unitCostMinY;
	}

	public String getUnitCostMaxY() {
		return unitCostMaxY;
	}

	public void setUnitCostMaxY(String unitCostMaxY) {
		this.unitCostMaxY = unitCostMaxY;
	}

	@Override
	public String toString() {
		return "MutiCondQryPrdsInfo [productType=" + productType + ", productLittleType=" + productLittleType + ", unitCostMinY=" + unitCostMinY + ", unitCostMaxY=" + unitCostMaxY
				+ ", expectEarnRateMin=" + expectEarnRateMin + ", expectEarnRateMax=" + expectEarnRateMax + ", bLockPeriodMin=" + bLockPeriodMin + ", bLockPeriodMax=" + bLockPeriodMax
				+ ", startIndex=" + startIndex + ", recordNum=" + recordNum + ", orderFlag=" + orderFlag + ", orderColumn=" + orderColumn + "]";
	}

}
