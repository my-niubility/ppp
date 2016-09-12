package com.nbl.controller.payment;

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

import com.nbl.common.constants.ErrorCode;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.TokenReqInfo;
import com.nbl.service.business.constant.BusCase;
import com.nbl.service.business.constant.OrderStatus;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.manager.app.TradeOrderApp;
import com.nbl.util.ErrCodeUtil;
import com.nbl.utils.jjwt.JWTutils;
import com.nbl.utils.json.ResponseJson;

/**
 * 获取指定场景Token
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/token")
public class TokenCtrl {
	private final static Logger logger = LoggerFactory.getLogger(TokenCtrl.class);

	@Autowired
	private HttpSession session;
	@Resource
	private TradeOrderApp tradeOrderApp;

	/**
	 * Token获取
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public @ResponseBody ResponseJson getToken(@Valid @RequestBody TokenReqInfo tokenReqInfo) {
		ResponseJson resp = null;
		String token = null;
		try {

			if (BusCase.PAYMENT.getValue().equals(tokenReqInfo.getBusCase())) {
				if (StringUtils.isEmpty(tokenReqInfo.getTradeOrderId())) {
					throw new MyBusinessCheckException(ErrCodeUtil.getErrMsgStr(ErrorCode.POC014, tokenReqInfo.getBusCase(), "tradeOrderId"));
				}
				checkPaymentCase(tokenReqInfo.getTradeOrderId());
				token = JWTutils.generateRepeateRequestToken(tokenReqInfo.getCustId(), 0);
				session.setAttribute(SessionKeys.TOKEN_CASE.getValue(), BusCase.PAYMENT.getValue());
			} else {
				logger.info("[business case is wrong...]:" + tokenReqInfo.getBusCase());
				throw new MyBusinessCheckException(ErrCodeUtil.getErrMsgStr(ErrorCode.POC013, tokenReqInfo.getBusCase()));
			}

		} catch (MyBusinessCheckException e) {
			logger.error("get token exception", e);
			return new ResponseJson().failure(e.getErrorCode() + e.getErrMsgKey());
		}

		resp = new ResponseJson().success(token);

		logger.info("[return msg]:" + resp.toString());
		return resp;
	}
	/**
	 * 检查订单状态是否为待支付状态
	 * @param tradeOrderId
	 * @throws MyBusinessCheckException
	 */
	private void checkPaymentCase(String tradeOrderId) throws MyBusinessCheckException {
		// TODO Auto-generated method stub
		String status = tradeOrderApp.getOrderStatus(tradeOrderId);
		if(!OrderStatus.TO_PAY.getValue().equals(status)){
			throw new MyBusinessCheckException(ErrCodeUtil.getErrMsgStr(ErrorCode.POC017,tradeOrderId,status));
		}
		
	}

}
