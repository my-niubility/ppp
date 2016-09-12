package com.nbl.domain;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

public class CustPersonUpInfo implements Serializable{

	private static final long serialVersionUID = -4806013143015885524L;
	
	@NotBlank(message = "用户编号不能为空")
	private String custId;
	
    private String countryId;

    private String gender;

    private String investmentExp;

    private String profession;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }


    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId == null ? null : countryId.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    
    public String getInvestmentExp() {
		return investmentExp;
	}

	public void setInvestmentExp(String investmentExp) {
		this.investmentExp = investmentExp;
	}

	public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
    }

	@Override
	public String toString() {
		return "CustPersonUpInfo [custId=" + custId + ", countryId=" + countryId + ", gender=" + gender
				+ ", investmentExp=" + investmentExp + ", profession=" + profession + "]";
	}

	

   

}
