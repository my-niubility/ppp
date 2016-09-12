package com.nbl.domain;

import java.io.Serializable;

public class BankTypeInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1652806300535036341L;

	private String bankType;

    private String bankName;

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType == null ? null : bankType.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }
}