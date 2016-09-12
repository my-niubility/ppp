package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class CalculatorInfo implements Serializable {

	private static final long serialVersionUID = 631314224144644900L;
	/**
	 * 投资金额
	 */
	@NotBlank(message = "投资金额不能为空")
	private String investAmt;
	/**
	 * 投资期限（天数）
	 */
	private String investPeriodDay;
	/**
	 * 投资期限（月）
	 */
	private String investPeriodMonth;
	/**
	 * 年化利率
	 */
	@NotBlank(message = "年化利率不能为空")
	private String yearRate;
	/**
	 * 偿还方式（返租方式）
	 */
	@NotBlank(message = "偿还方式不能为空")
	private String repayMode;
	/**
	 * 年换天标准（360或365）
	 */
	private String yearDay;

	public String getInvestAmt() {
		return investAmt;
	}

	public void setInvestAmt(String investAmt) {
		this.investAmt = investAmt;
	}

	public String getInvestPeriodDay() {
		return investPeriodDay;
	}

	public void setInvestPeriodDay(String investPeriodDay) {
		this.investPeriodDay = investPeriodDay;
	}

	public String getInvestPeriodMonth() {
		return investPeriodMonth;
	}

	public void setInvestPeriodMonth(String investPeriodMonth) {
		this.investPeriodMonth = investPeriodMonth;
	}

	public String getYearRate() {
		return yearRate;
	}

	public void setYearRate(String yearRate) {
		this.yearRate = yearRate;
	}

	public String getRepayMode() {
		return repayMode;
	}

	public void setRepayMode(String repayMode) {
		this.repayMode = repayMode;
	}

	public String getYearDay() {
		return yearDay;
	}

	public void setYearDay(String yearDay) {
		this.yearDay = yearDay;
	}

	@Override
	public String toString() {
		return "Calculator [investAmt=" + investAmt + ", investPeriodDay=" + investPeriodDay + ", investPeriodMonth=" + investPeriodMonth + ", yearRate=" + yearRate + ", repayMode=" + repayMode
				+ ", yearDay=" + yearDay + "]";
	}
}
