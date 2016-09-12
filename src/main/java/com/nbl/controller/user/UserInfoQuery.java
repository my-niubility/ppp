package com.nbl.controller.user;


import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.dto.CommRespDto;
import com.nbl.domain.BalanceQueryInfo;
import com.nbl.domain.UserQueryInfo;
import com.nbl.service.business.app.CustFundsQryApp;
import com.nbl.service.business.app.TrdOrdQryApp;
import com.nbl.service.business.constant.PayChanlCode;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.app.UserInfoQueryApp;
import com.nbl.service.user.dto.req.BalanceInfoQueryDto;
import com.nbl.service.user.dto.req.UserInfoQueryDto;
import com.nbl.service.user.dto.res.BalanceInfoResultDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.service.user.dto.res.UserInfoResultDto;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 客户信息查询
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/custinfo")
public class UserInfoQuery {
	private final static Logger logger = LoggerFactory.getLogger(UserInfoQuery.class);
	@Resource
	private UserInfoQueryApp userInfoQueryApp;
	@Resource
	private CustFundsQryApp custFundsQryApp;
	@Autowired
	private HttpSession session;
	@Resource
	private TrdOrdQryApp trdOrdQryApp;

	/**
	 * 用户基本信息查询
	 * 
	 * @param personCust
	 * @return
	 */
	@RequestMapping(value = "/baseinfo/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryUserInfo(@Valid @RequestBody UserQueryInfo userQueryInfo) {
		ResponseJson resp;
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		UserInfoQueryDto userInfoQueryDto = new UserInfoQueryDto();
		BeanParseUtils.copyProperties(userQueryInfo, userInfoQueryDto);
		userInfoQueryDto.setUserId(userInfo.getLoginName());
		CommRespDto custInfo = userInfoQueryApp.queryCustCenterInfo(userInfoQueryDto);
		CommRespDto fundsInfo = custFundsQryApp.queryCustFunds(userQueryInfo.getCustId());

		UserInfoResultDto result = new UserInfoResultDto();
		BeanParseUtils.copyProperties(custInfo.getData(), result);
		BeanParseUtils.copyProperties(fundsInfo.getData(), result);

		resp = new ResponseJson().success(result);
		return resp;
	}

	/**
	 * 可用余额查询
	 * 
	 * @param personCust
	 * @return
	 */
	@RequestMapping(value = "/balance/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson queryUsableBalance(@Valid @RequestBody BalanceQueryInfo balanceQueryInfo) {
		ResponseJson resp;
		BalanceInfoQueryDto balanceInfoQueryDto = new BalanceInfoQueryDto();
		BeanParseUtils.copyProperties(balanceQueryInfo, balanceInfoQueryDto);
		balanceInfoQueryDto.setPayChanlCode(PayChanlCode.ZLZB.getValue());
		BalanceInfoResultDto result = userInfoQueryApp.queryUsableBalance(balanceInfoQueryDto);

		resp = new ResponseJson().success(result);
		return resp;
	}
}
