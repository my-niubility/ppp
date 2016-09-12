/**
 * 
 */
package com.nbl.domain;

/**
 * @author sulong
 * 
 */
public class RepayTerm {
	//期数
	private int term;
	//周期(天数)
	private long period;
	//还款结束日期
	private String repayEndDate;

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getRepayEndDate() {
		return repayEndDate;
	}

	public void setRepayEndDate(String repayEndDate) {
		this.repayEndDate = repayEndDate;
	}

	
}
