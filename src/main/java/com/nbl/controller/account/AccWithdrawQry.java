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
import com.nbl.domain.WthdrQueryInfo;
import com.nbl.service.business.app.WithdrawQryApp;
import com.nbl.service.business.constant.OrdComOrderByCol;
import com.nbl.service.business.constant.OrderByFlag;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.constant.WithdrawPayStatus;
import com.nbl.service.user.dto.req.WthdrQryInfoDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.json.ResponseJson;

/**
 * 提现信息查询
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/wthdrorder")
public class AccWithdrawQry {
	private final static Logger logger = LoggerFactory.getLogger(AccWithdrawQry.class);
	@Resource
	private WithdrawQryApp withdrawQryApp;
	@Autowired
	private HttpSession session;

	/**
	 * 提现记录查询
	 * 
	 * @param wthdrQueryInfo
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accWithdrQuery(@Valid @RequestBody WthdrQueryInfo wthdrQueryInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		try {
			//参数校验
			accWithdrQueryCheck(wthdrQueryInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		WthdrQryInfoDto wthdrQryInfoDto = new WthdrQryInfoDto();
		BeanParseUtils.copyProperties(wthdrQueryInfo, wthdrQryInfoDto);

		wthdrQryInfoDto.setUserId(userInfo.getLoginName());
		// WthdrQryResultDto result =
		// withdrawQryApp.queryWthdrOrder(wthdrQryInfoDto);

		CommRespDto result = withdrawQryApp.queryWthdrOrder(wthdrQryInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		logger.debug("[return msg]:" + resp.toString());

		return resp;
	}

	private void accWithdrQueryCheck(WthdrQueryInfo wthdrQueryInfo) throws MyBusinessCheckException {
		String status = wthdrQueryInfo.getStatus();
		if (!(WithdrawPayStatus.SUCCESS.getValue().equals(status) || WithdrawPayStatus.FAIL.getValue().equals(status) || StringUtils.isEmpty(status)))
			throw new MyBusinessCheckException(ErrorCode.POC008, "status");
		if (!StringUtils.isEmpty(wthdrQueryInfo.getOrderColumn()) && OrdComOrderByCol.parseOf(wthdrQueryInfo.getOrderColumn()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderColumn");
		if (!StringUtils.isEmpty(wthdrQueryInfo.getOrderFlag()) && OrderByFlag.parseOf(wthdrQueryInfo.getOrderFlag()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "orderFlag");
	}

	/**
	 * 提现记录总数查询
	 * 
	 * @param wthdrQueryInfo
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accWithdrCountQuery(@Valid@RequestBody WthdrQueryInfo wthdrQueryInfo) {
		logger.info("[enter accWithdrCountQuery inparam is :]" + wthdrQueryInfo.toString());
		ResponseJson resp = null;
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		try {
			accWithdrQueryCheck(wthdrQueryInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chk exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		WthdrQryInfoDto wthdrQryInfoDto = new WthdrQryInfoDto();
		BeanParseUtils.copyProperties(wthdrQueryInfo, wthdrQryInfoDto);
		wthdrQryInfoDto.setUserId(userInfo.getLoginName());
		String count = withdrawQryApp.queryWthdrOrderCount(wthdrQryInfoDto);
		resp = new ResponseJson().success(count);
		return resp;
	}

}
