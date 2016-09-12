package com.nbl.domain;

import java.io.Serializable;
import java.util.Date;

public class HisTrdOrdInfo implements Serializable {

	private static final long serialVersionUID = 6649425798525443526L;

	/**
	 * 订单最后更新开始时间（不传默认为工作日期，时间00:00:00）
	 */
	private Date updateDateStart;
	/**
	 * 订单最后更新结束时间（不传默认为工作日期，时间23:59:59）
	 */
	private Date updateDateEnd;
	/**
	 * 订单状态（00:等待付款、01:支付成功、02:支付失败、03:订单取消）
	 */
	private String orderStatus;

	public Date getUpdateDateStart() {
		return updateDateStart;
	}

	public void setUpdateDateStart(Date updateDateStart) {
		this.updateDateStart = updateDateStart;
	}

	public Date getUpdateDateEnd() {
		return updateDateEnd;
	}

	public void setUpdateDateEnd(Date updateDateEnd) {
		this.updateDateEnd = updateDateEnd;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "HisTrdOrdInfo [updateDateStart=" + updateDateStart + ", updateDateEnd=" + updateDateEnd
				+ ", orderStatus=" + orderStatus + "]";
	}

}
