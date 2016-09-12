package com.nbl.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class PrdDetailInfo implements Serializable {

	private static final long serialVersionUID = 6370479983973847377L;
	/**
	 * 产品编号
	 */
	@NotBlank(message = "产品编号不能为空")
	private String productId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "PrdDetailInfo [productId=" + productId + "]";
	}

}
