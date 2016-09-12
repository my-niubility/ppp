package com.nbl.controller.account;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ComConst;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.dto.CommRespDto;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.RechgQueryInfo;
import com.nbl.service.business.app.RechargeQryApp;
import com.nbl.service.business.constant.OrdComOrderByCol;
import com.nbl.service.business.constant.OrderByFlag;
import com.nbl.service.business.constant.RechargePayStatus;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.dto.req.RchgQryInfoDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 充值信息查询
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/rchgorder")
public class AccRechargeQry {
	private final static Logger logger = LoggerFactory.getLogger(AccRechargeQry.class);
	@Resource
	private RechargeQryApp rechargeQryApp;
	@Autowired
	private HttpSession session;

	/**
	 * 充值记录查询
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accRechargeQuery(@Valid @RequestBody RechgQueryInfo rechgQueryInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		try {
			accRechargeQueryCheck(rechgQueryInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		RchgQryInfoDto rchgQryInfoDto = new RchgQryInfoDto();
		BeanParseUtils.copyProperties(rechgQueryInfo, rchgQryInfoDto);
		rchgQryInfoDto.setUserId(userInfo.getLoginName());
		CommRespDto commRespDto = rechargeQryApp.queryRchgOrder(rchgQryInfoDto);
		if (ComConst.SUCCESS.equals(commRespDto.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(commRespDto.getData());
		} else {
			resp = new ResponseJson().failure(commRespDto.getResIdentifier().getReturnCode() + "|" + commRespDto.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private void accRechargeQueryCheck(RechgQueryInfo rechgQueryInfo) throws MyBusinessCheckException {
		String status = rechgQueryInfo.getStatus();
		if (!(RechargePayStatus.SUCCESS.getValue().equals(status) || RechargePayStatus.FAIL.getValue().equals(status) || StringUtils.isEmpty(status)))
			throw new MyBusinessCheckException(ErrorCode.POC008, "status");
		if (!StringUtils.isEmpty(rechgQueryInfo.getOrderColumn()) && OrdComOrderByCol.parseOf(rechgQueryInfo.getOrderColumn()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderColumn");
		if (!StringUtils.isEmpty(rechgQueryInfo.getOrderFlag()) && OrderByFlag.parseOf(rechgQueryInfo.getOrderFlag()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderFlag");
	}

	/**
	 * 充值记录总数查询
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accRechargeCountQuery(@Valid @RequestBody RechgQueryInfo rechgQueryInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		try {
			accRechargeQueryCheck(rechgQueryInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		RchgQryInfoDto rchgQryInfoDto = new RchgQryInfoDto();
		BeanParseUtils.copyProperties(rechgQueryInfo, rchgQryInfoDto);
		rchgQryInfoDto.setUserId(userInfo.getLoginName());
		String count = rechargeQryApp.queryRchgOrderCount(rchgQryInfoDto);
		logger.debug("[return msg]:" + count);
		resp = new ResponseJson().success(count);
		return resp;
	}
}
