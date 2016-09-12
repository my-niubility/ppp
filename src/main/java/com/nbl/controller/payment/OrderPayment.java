package com.nbl.controller.payment;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nbl.common.constants.ComConst;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.dto.CommRespDto;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.OrdPayBalanceInfo;
import com.nbl.domain.OrdPayGatewayInfo;
import com.nbl.domain.OrdPayQuickInfo;
import com.nbl.domain.PayRltInfo;
import com.nbl.service.business.app.OrderPaymentApp;
import com.nbl.service.business.constant.BusCase;
import com.nbl.service.business.constant.FundsType;
import com.nbl.service.business.constant.PayChanlCode;
import com.nbl.service.business.constant.PaymentType;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.constant.TokenCheck;
import com.nbl.service.business.dto.req.PayAlyInfoDto;
import com.nbl.service.business.dto.req.PayRltInfoDto;
import com.nbl.service.user.app.BnkCrdInfoQryApp;
import com.nbl.service.user.app.PwdManageApp;
import com.nbl.service.user.app.UserInfoQueryApp;
import com.nbl.service.user.dto.req.BalanceInfoQueryDto;
import com.nbl.service.user.dto.req.QryBnkCrdDto;
import com.nbl.service.user.dto.res.BalanceInfoResultDto;
import com.nbl.service.user.dto.res.QryBnkCrdResultDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.util.AmtParseUtil;
import com.nbl.util.EncryptUtil;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.jjwt.JWTutils;
import com.nbl.utils.json.ResponseJson;

/**
 * 订单支付
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/order/payment")
public class OrderPayment {
	private final static Logger logger = LoggerFactory.getLogger(OrderPayment.class);
	@Resource
	private OrderPaymentApp orderPaymentApp;
	@Autowired
	private HttpSession session;
	@Resource
	private PwdManageApp pwdManageApp;
	@Resource
	private BnkCrdInfoQryApp bnkCrdInfoQryApp;
	@Resource
	private UserInfoQueryApp userInfoQueryApp;

	/**
	 * 快捷支付
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/quick", method = RequestMethod.POST)
	public @ResponseBody ResponseJson quickPayment(@Valid @RequestBody OrdPayQuickInfo orderPayInfo) {
		logger.info("[enter quickPayment inparam is :]" + orderPayInfo.toString());
		ResponseJson resp = null;
		try {
			quickPaymentCheck(orderPayInfo);
			checkMsgCodeAndPayPwd(orderPayInfo.getCustId(), orderPayInfo.getMsgIdenCode(), null);
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		PayAlyInfoDto payAlyInfoDto = new PayAlyInfoDto();
		BeanUtils.copyProperties(orderPayInfo, payAlyInfoDto);
		payAlyInfoDto.setPayCustId(orderPayInfo.getCustId());
		payAlyInfoDto.setPaymentType(PaymentType.SHORTCUT_PAYMENT.getValue());
		payAlyInfoDto.setRedEnvAmt(AmtParseUtil.strToLongAmt(orderPayInfo.getRedEnvAmt()));
		payAlyInfoDto.setTradeAmt(AmtParseUtil.strToLongAmt(orderPayInfo.getTradeAmt()));
		payAlyInfoDto.setTradeTalAmt(AmtParseUtil.strToLongAmt(orderPayInfo.getTradeTalAmt()));
		QryBnkCrdResultDto bnkCrd = bnkCrdInfoQryApp.queryDefaultCard(new QryBnkCrdDto(orderPayInfo.getCustId()));
		payAlyInfoDto.setPayBankType(bnkCrd.getBankType());
		payAlyInfoDto.setPayBankCardNo(bnkCrd.getCardNo());

		// PayAlyResultDto result =
		// orderPaymentApp.paymentApplyQuick(payAlyInfoDto);
		CommRespDto result = orderPaymentApp.paymentApplyQuick(payAlyInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private void quickPaymentCheck(OrdPayQuickInfo orderPayInfo) throws MyBusinessCheckException {
		if (FundsType.parseOf(orderPayInfo.getFundsType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "fundsType");
		checkToken(orderPayInfo.getToken(), orderPayInfo.getCustId());
		amtCheck(orderPayInfo.getTradeTalAmt(), orderPayInfo.getTradeAmt(), orderPayInfo.getRedEnvAmt());
	}

	/**
	 * 余额支付
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/balance", method = RequestMethod.POST)
	public @ResponseBody ResponseJson balancePayment(@Valid @RequestBody OrdPayBalanceInfo ordPayBalanceInfo) {
		ResponseJson resp = null;

		try {
			balancePaymentCheck(ordPayBalanceInfo);
			checkMsgCodeAndPayPwd(ordPayBalanceInfo.getCustId(), ordPayBalanceInfo.getMsgIdenCode(), ordPayBalanceInfo.getPayPassword());
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		PayAlyInfoDto payAlyInfoDto = new PayAlyInfoDto();
		BeanUtils.copyProperties(ordPayBalanceInfo, payAlyInfoDto);
		payAlyInfoDto.setPaymentType(PaymentType.ACCOUNT_PAYMENT.getValue());
		payAlyInfoDto.setPayCustId(ordPayBalanceInfo.getCustId());
		payAlyInfoDto.setRedEnvAmt(AmtParseUtil.strToLongAmt(ordPayBalanceInfo.getRedEnvAmt()));
		payAlyInfoDto.setTradeAmt(AmtParseUtil.strToLongAmt(ordPayBalanceInfo.getTradeAmt()));
		payAlyInfoDto.setTradeTalAmt(AmtParseUtil.strToLongAmt(ordPayBalanceInfo.getTradeTalAmt()));
		QryBnkCrdResultDto bnkCrd = bnkCrdInfoQryApp.queryDefaultCard(new QryBnkCrdDto(ordPayBalanceInfo.getCustId()));
		payAlyInfoDto.setPayBankType(bnkCrd.getBankType());
		payAlyInfoDto.setPayBankCardNo(bnkCrd.getCardNo());

		CommRespDto result = orderPaymentApp.paymentApplyBalance(payAlyInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private void balancePaymentCheck(OrdPayBalanceInfo ordPayBalanceInfo) throws MyBusinessCheckException {
		if (FundsType.parseOf(ordPayBalanceInfo.getFundsType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "fundsType");
		checkToken(ordPayBalanceInfo.getToken(), ordPayBalanceInfo.getCustId());
		amtCheck(ordPayBalanceInfo.getTradeTalAmt(), ordPayBalanceInfo.getTradeAmt(), ordPayBalanceInfo.getRedEnvAmt());
		// 余额校验
		BalanceInfoQueryDto reqParam = new BalanceInfoQueryDto(ordPayBalanceInfo.getCustId(), PayChanlCode.ZLZB.getValue());
		BalanceInfoResultDto result = userInfoQueryApp.queryUsableBalance(reqParam);
		if (AmtParseUtil.strToLongAmt(result.getUsableBalance()) < AmtParseUtil.strToLongAmt(ordPayBalanceInfo.getTradeAmt())) {
			throw new MyBusinessCheckException(ErrorCode.POC023);
		}
	}

	private void amtCheck(String tradeTalAmt, String tradeAmt, String redEnvAmt) throws MyBusinessCheckException {
		Long tradeTalAmtL = AmtParseUtil.strToLongAmt(tradeTalAmt);
		Long tradeAmtL = AmtParseUtil.strToLongAmt(tradeAmt);
		Long redEnvAmtL = AmtParseUtil.strToLongAmt(redEnvAmt);
		if (tradeTalAmtL != tradeAmtL + redEnvAmtL) {
			throw new MyBusinessCheckException(ErrorCode.POC012);
		}
	}

	/**
	 * 网关支付
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/gateway", method = RequestMethod.POST)
	public @ResponseBody ResponseJson gatewayPayment(@Valid@RequestBody OrdPayGatewayInfo ordPayGatewayInfo) {
		ResponseJson resp = null;

		try {
			gatewayPaymentCheck(ordPayGatewayInfo);
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		PayAlyInfoDto payAlyInfoDto = new PayAlyInfoDto();
		BeanUtils.copyProperties(ordPayGatewayInfo, payAlyInfoDto);
		payAlyInfoDto.setPaymentType(PaymentType.GATEWAY_PAYMENT_RECHARGE.getValue());
		payAlyInfoDto.setPayCustId(ordPayGatewayInfo.getCustId());
		payAlyInfoDto.setRedEnvAmt(AmtParseUtil.strToLongAmt(ordPayGatewayInfo.getRedEnvAmt()));
		payAlyInfoDto.setTradeAmt(AmtParseUtil.strToLongAmt(ordPayGatewayInfo.getTradeAmt()));
		payAlyInfoDto.setTradeTalAmt(AmtParseUtil.strToLongAmt(ordPayGatewayInfo.getTradeTalAmt()));
		QryBnkCrdResultDto bnkCrd = bnkCrdInfoQryApp.queryDefaultCard(new QryBnkCrdDto(ordPayGatewayInfo.getCustId()));
		payAlyInfoDto.setPayBankType(bnkCrd.getBankType());
		payAlyInfoDto.setPayBankCardNo(bnkCrd.getCardNo());

		CommRespDto result = orderPaymentApp.paymentApplyGateway(payAlyInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	private void gatewayPaymentCheck(OrdPayGatewayInfo ordPayGatewayInfo) throws MyBusinessCheckException {
		if (FundsType.parseOf(ordPayGatewayInfo.getFundsType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "fundsType");
		checkToken(ordPayGatewayInfo.getToken(), ordPayGatewayInfo.getCustId());
	}

	private void checkToken(String token, String custId) throws MyBusinessCheckException {
		String busCase = (String) session.getAttribute(SessionKeys.TOKEN_CASE.getValue());
		if (BusCase.PAYMENT.getValue().equals(busCase)) {
			if (TokenCheck.SUCCESS.getValue().equals(JWTutils.verifyTokenAndRemove(token, custId))) {
				session.removeAttribute(SessionKeys.TOKEN_CASE.getValue());
			} else {
				throw new MyBusinessCheckException(ErrorCode.POC011);
			}
		} else {
			throw new MyBusinessCheckException(ErrorCode.POC015, BusCase.PAYMENT.getValue());
		}
	}

	private void checkMsgCodeAndPayPwd(String custId, String msgIdenCode, String paymentPwd) throws MyBusinessCheckException {
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		MsgPicCertCodeUtil.checkMsgCertCode(session, msgIdenCode, userInfo.getMobile());
		if (paymentPwd != null) {
			String payPwd = EncryptUtil.encodeDouble(paymentPwd + userInfo.getMobile());
			pwdManageApp.checkPayPwd(custId, payPwd);
		}
	}

	/**
	 * 支付结果通知(快捷支付)
	 * 
	 * @param rchgNoticeInfo
	 * @return
	 */
	@RequestMapping(value = "/quickpaynotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson paymentAlyQckNotice(@RequestBody PayRltInfo payRltInfo) {
		ResponseJson resp = null;
		PayRltInfoDto payRltInfoDto = new PayRltInfoDto();
		BeanUtils.copyProperties(payRltInfo, payRltInfoDto);
		try {
			orderPaymentApp.paymentAlyQckNotice(payRltInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[paymentAlyQckNotice exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();
		logger.info("[return msg]:" + resp.toString());
		return resp;
	}

	/**
	 * 支付结果通知(余额支付)
	 * 
	 * @param rchgNoticeInfo
	 * @return
	 */
	@RequestMapping(value = "/balancepaynotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson paymentAlyBalNotice(@RequestBody PayRltInfo payRltInfo) {
		logger.info("[enter paymentAlyBalNotice inparam is :]" + payRltInfo.toString());
		ResponseJson resp = null;
		PayRltInfoDto payRltInfoDto = new PayRltInfoDto();
		BeanUtils.copyProperties(payRltInfo, payRltInfoDto);
		try {
			orderPaymentApp.paymentAlyBalNotice(payRltInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[paymentAlyBalNotice exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();
		logger.info("[return msg]:" + resp.toString());
		return resp;
	}

	/**
	 * 支付结果通知(网关支付)
	 * 
	 * @param rchgNoticeInfo
	 * @return
	 */
	@RequestMapping(value = "/gatewaypaynotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson paymentAlyGtyNotice(@RequestBody PayRltInfo payRltInfo) {
		logger.info("[enter paymentAlyGtyNotice inparam is :]" + payRltInfo.toString());
		ResponseJson resp = null;
		PayRltInfoDto payRltInfoDto = new PayRltInfoDto();
		BeanUtils.copyProperties(payRltInfo, payRltInfoDto);
		try {
			orderPaymentApp.paymentAlyGtyNotice(payRltInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[paymentAlyGtyNotice exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();
		return resp;
	}

}
