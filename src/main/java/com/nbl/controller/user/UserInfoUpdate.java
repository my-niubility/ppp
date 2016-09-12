package com.nbl.controller.user;

import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ComConst;
import com.nbl.common.exception.MyBusinessRuntimeException;
import com.nbl.domain.CustPersonUpInfo;
import com.nbl.service.manager.app.CustPersonApp;
import com.nbl.service.user.dto.req.CustPersonReqUpDto;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.BeanUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 更新客户基本信息
 * @author chenhongji
 *
 */
@Controller
@RequestMapping("/restful/custinfo")
public class UserInfoUpdate {
	
	private final static Logger logger = LoggerFactory.getLogger(UserInfoQuery.class);
	@Resource
	CustPersonApp custPersonApp;
	/**
	 * 更新客户基本信息
	 * @param custPersonUpInfo
	 * @return
	 */
	@RequestMapping(value = "/baseinfo/update", method = RequestMethod.POST)
	public @ResponseBody ResponseJson updateUserCommInfo(@Valid @RequestBody CustPersonUpInfo custPersonUpInfo) {
		ResponseJson resp;
		try{
			paramCheck(custPersonUpInfo);
		} catch(MyBusinessRuntimeException e){
			logger.error("[updateUserCommInfo exception...]", e);
			return new ResponseJson().failure(e.getMessage());
		}
		
		try{
			investmentExpCheck(custPersonUpInfo.getInvestmentExp());
		} catch(MyBusinessRuntimeException e){
			logger.error("[updateUserCommInfo exception...]", e);
			return new ResponseJson().failure(e.getMessage());
		}
		
		logger.info("enter updateUserCommInfo inParams is "+custPersonUpInfo.toString());
		CustPersonReqUpDto custPersonReqUpDto = new CustPersonReqUpDto();
		BeanParseUtils.copyProperties(custPersonUpInfo, custPersonReqUpDto);
		String count=custPersonApp.updateCustCommonInfo(custPersonReqUpDto);
		resp = new ResponseJson().success(count);
		return resp;

	}
	/**
	 * 参数校验
	 * @param custPersonUpInfo
	 */
	public static void paramCheck(CustPersonUpInfo custPersonUpInfo) {
		String countryId = custPersonUpInfo.getCountryId().trim();
		String gender = custPersonUpInfo.getGender();
		String investmentExp = custPersonUpInfo.getInvestmentExp().trim();
		String profession = custPersonUpInfo.getProfession().trim();
		if(gender==null&&"".equals(countryId)&&"".equals(investmentExp)&&"".equals(profession)){
			throw new MyBusinessRuntimeException("基本信息修改项不能全部为空");
		}
		custPersonUpInfo.setCountryId(countryId);
		custPersonUpInfo.setGender(gender);
		custPersonUpInfo.setInvestmentExp(investmentExp);
		custPersonUpInfo.setProfession(profession);
	}
	/**
	 * 投资经验年限校验
	 * @param investmentExp
	 */
	public void investmentExpCheck(String investmentExp){
		//如果用户输入了investmentExp才进行校验
		if(!"".equals(investmentExp.trim())){
			Pattern pattern = Pattern.compile("[0-4]{0,1}[0-9]{1}");
			if(!pattern.matcher(investmentExp).matches()){
				throw new MyBusinessRuntimeException("投资年限请输入1-50以内的正整数");
			}
		}
		
	}
	
}
