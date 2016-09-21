package com.nbl.controller.account;

import java.util.regex.Pattern;

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

import com.nbl.common.constants.ComConst;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.dto.CommRespDto;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.WithdrawInfo;
import com.nbl.domain.WthdrwNoticeInfo;
import com.nbl.service.business.app.WithdrawApp;
import com.nbl.service.business.constant.PayChanlCode;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.constant.WithdrawType;
import com.nbl.service.user.app.BnkCrdInfoQryApp;
import com.nbl.service.user.app.ChkWthdwApp;
import com.nbl.service.user.app.UserInfoQueryApp;
import com.nbl.service.user.dto.req.BalanceInfoQueryDto;
import com.nbl.service.user.dto.req.ChkWtDwInfoDto;
import com.nbl.service.user.dto.req.QryBnkCrdDto;
import com.nbl.service.user.dto.req.UserInfoQueryDto;
import com.nbl.service.user.dto.req.WthdwAlyInfoDto;
import com.nbl.service.user.dto.req.WthdwNoticeDto;
import com.nbl.service.user.dto.res.BalanceInfoResultDto;
import com.nbl.service.user.dto.res.QryBnkCrdResultDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.service.user.dto.res.UserInfoResultDto;
import com.nbl.util.AmtParseUtil;
import com.nbl.util.EncryptUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * 提现
 * 
 * @author AlanMa
 * 
 */
@Controller
@RequestMapping("/restful/accwithdraw")
public class AccWithdraw {
	private final static Logger logger = LoggerFactory.getLogger(AccWithdraw.class);
	@Resource
	private WithdrawApp withdrawApp;
	@Resource
	private ChkWthdwApp chkWthdwApp;
	@Autowired
	private HttpSession session;
	@Resource
	private BnkCrdInfoQryApp bnkCrdInfoQryApp;
	@Resource
	private UserInfoQueryApp userInfoQueryApp;

	/**
	 * 账户提现
	 * 
	 * @param withdrawInfo
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accWithdraw(@Valid @RequestBody WithdrawInfo withdrawInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = null;
		try {
			// 提现方式校验
			accWithdrawCheck(withdrawInfo);
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			// 短息验证码校验
			MsgPicCertCodeUtil.checkMsgCertCode(session, withdrawInfo.getMsgIdenCode(), userInfo.getMobile());
			// 校验参数&支付密码校验
			String pwd = EncryptUtil.encodeDouble(withdrawInfo.getPayPassword() + userInfo.getMobile());
			logger.debug("【user payment password is】:" + pwd);
			ChkWtDwInfoDto chkWtDwInfoDto = new ChkWtDwInfoDto();
			BeanParseUtils.copyProperties(withdrawInfo, chkWtDwInfoDto);
			chkWtDwInfoDto.setPayPassword(pwd);
			// 用户是否绑卡，是否设置支付密码，支付密码是否正确校验
			chkWthdwApp.chkWthdwInfo(chkWtDwInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("【chkWthdwInfo exception...】", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		WthdwAlyInfoDto wthdwAlyInfoDto = new WthdwAlyInfoDto();
		wthdwAlyInfoDto.setPhoneNum(userInfo.getMobile());
		BeanParseUtils.copyProperties(withdrawInfo, wthdwAlyInfoDto);

		CommRespDto commResp = userInfoQueryApp.queryCustCenterInfo(new UserInfoQueryDto(userInfo.getMobile(), userInfo.getCustId()));
		UserInfoResultDto userIdenInfo = (UserInfoResultDto) commResp.getData();
		wthdwAlyInfoDto.setCustName(userIdenInfo.getName());
		// 查询默认的银行卡信息
		QryBnkCrdResultDto bnkCrdInfo = bnkCrdInfoQryApp.queryDefaultCard(new QryBnkCrdDto(userInfo.getCustId()));
		wthdwAlyInfoDto.setBankType(bnkCrdInfo.getBankType());
		wthdwAlyInfoDto.setBankCardNo(bnkCrdInfo.getCardNo());
		wthdwAlyInfoDto.setAmt(AmtParseUtil.strToLongAmt(withdrawInfo.getWithdrawAmt()));
		// WthdwAlyResultDto result =
		// withdrawApp.withDrawApply(wthdwAlyInfoDto);
		logger.info("[wthdwAlyInfoDto inparam is :]" + wthdwAlyInfoDto.toString());
		CommRespDto result = withdrawApp.withDrawApply(wthdwAlyInfoDto);

		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(result.getData());
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;

	}

	private void accWithdrawCheck(WithdrawInfo withdrawInfo) throws MyBusinessCheckException {
		if (WithdrawType.parseOf(withdrawInfo.getWithdrawType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "withdrawType");
		if (!WithdrawType.GENERAL.getValue().equals(withdrawInfo.getWithdrawType()))
			throw new MyBusinessCheckException(ErrorCode.POB001, "目前只支持普通提现");
		//提现金额最小为为保留两位小数
		if (!(Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$").matcher(withdrawInfo.getWithdrawAmt()).matches()))
			throw new MyBusinessCheckException(ErrorCode.POC008, "withdrawAmt");
		// 余额校验
		BalanceInfoQueryDto reqParam = new BalanceInfoQueryDto(withdrawInfo.getCustId(), PayChanlCode.ZLZB.getValue());
		BalanceInfoResultDto result = userInfoQueryApp.queryUsableBalance(reqParam);
		if (AmtParseUtil.strToLongAmt(result.getUsableBalance()) < AmtParseUtil.strToLongAmt(withdrawInfo.getWithdrawAmt())) {
			throw new MyBusinessCheckException(ErrorCode.POC023);
		}
	}

	/**
	 * 提现异步结果通知
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/withdrawnotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson withdrawNotice(@RequestBody WthdrwNoticeInfo wthdrwNoticeInfo) {
		ResponseJson resp = null;
		WthdwNoticeDto wthdwNoticeDto = new WthdwNoticeDto();
		BeanParseUtils.copyProperties(wthdrwNoticeInfo, wthdwNoticeDto);
		try {
			withdrawApp.rechargeNotice(wthdwNoticeDto);
		} catch (MyBusinessCheckException e) {
			logger.error("【rechargeNotice exception】", e);
			return new ResponseJson().failure(e.getErrorCode() + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();
		return resp;
	}
}
