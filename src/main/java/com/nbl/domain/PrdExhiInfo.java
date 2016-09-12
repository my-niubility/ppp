package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class PrdExhiInfo implements Serializable {

	private static final long serialVersionUID = 2538700175775740037L;

	/**
	 * 展示类型：01-首页-产品列表、02-新品中心-产品推荐、03-我的资产-为您推荐
	 */
	@NotBlank(message = "展示类型不能为空")
	private String exhType;

	public String getExhType() {
		return exhType;
	}

	public void setExhType(String exhType) {
		this.exhType = exhType;
	}

	@Override
	public String toString() {
		return "PrdExhiInfo [exhType=" + exhType + "]";
	}

}
