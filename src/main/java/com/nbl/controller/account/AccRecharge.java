package com.nbl.controller.account;

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
import com.nbl.domain.RchgNoticeInfo;
import com.nbl.domain.RechargeInfo;
import com.nbl.service.business.app.RechargeApp;
import com.nbl.service.business.constant.RechargeType;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.app.BnkCrdInfoQryApp;
import com.nbl.service.user.app.RechargeChkApp;
import com.nbl.service.user.app.UserInfoQueryApp;
import com.nbl.service.user.dto.req.ChkRechgInfoDto;
import com.nbl.service.user.dto.req.QryBnkCrdDto;
import com.nbl.service.user.dto.req.RchgNoticeDto;
import com.nbl.service.user.dto.req.RechgAlyInfoDto;
import com.nbl.service.user.dto.req.UserInfoQueryDto;
import com.nbl.service.user.dto.res.QryBnkCrdResultDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.service.user.dto.res.UserInfoResultDto;
import com.nbl.util.AmtParseUtil;
import com.nbl.util.EncryptUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * 账户充值
 * 
 * @author AlanMa
 * 
 */
@Controller
@RequestMapping("/restful/accrecharge")
public class AccRecharge {
	private final static Logger logger = LoggerFactory.getLogger(AccRecharge.class);
	@Resource
	private RechargeApp rechargeApp;
	@Resource
	private RechargeChkApp rechargeChkApp;
	@Autowired
	private HttpSession session;
	@Resource
	private BnkCrdInfoQryApp bnkCrdInfoQryApp;
	@Resource
	private UserInfoQueryApp userInfoQueryApp;

	/**
	 * 账户充值
	 * 
	 * @param certiInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody ResponseJson accRecharge(@Valid @RequestBody RechargeInfo rechargeInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = null;
		try {
			//充值类型校验
			accRechargeCheck(rechargeInfo);
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			//短信验证码校验
			MsgPicCertCodeUtil.checkMsgCertCode(session, rechargeInfo.getMsgIdenCode(), userInfo.getMobile());
			// 校验参数&支付密码校验
			String pwd = EncryptUtil.encodeDouble(rechargeInfo.getPayPassword() + userInfo.getMobile());
			logger.debug("[user payment password is]:" + pwd);
			ChkRechgInfoDto chkRechgInfoDto = new ChkRechgInfoDto();
			BeanParseUtils.copyProperties(rechargeInfo, chkRechgInfoDto);
			chkRechgInfoDto.setPayPassword(pwd);
			//设置支付金额
			chkRechgInfoDto.setAmt(AmtParseUtil.strToLongAmt(rechargeInfo.getRechargeAmt()));
			//用户是否绑卡、是否设置密码、支付密码校验
			rechargeChkApp.chkRechgInfo(chkRechgInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		RechgAlyInfoDto rechgAlyInfoDto = new RechgAlyInfoDto();
		rechgAlyInfoDto.setPhoneNum(userInfo.getMobile());
		BeanParseUtils.copyProperties(rechargeInfo, rechgAlyInfoDto);

		CommRespDto commResp = userInfoQueryApp.queryCustCenterInfo(new UserInfoQueryDto(userInfo.getMobile(), userInfo.getCustId()));
		UserInfoResultDto userIdenInfo = (UserInfoResultDto) commResp.getData();
		rechgAlyInfoDto.setCustName(userIdenInfo.getName());

		QryBnkCrdResultDto bnkCrdInfo = bnkCrdInfoQryApp.queryDefaultCard(new QryBnkCrdDto(rechargeInfo.getCustId()));
		rechgAlyInfoDto.setBankType(bnkCrdInfo.getBankType());
		rechgAlyInfoDto.setBankCardNo(bnkCrdInfo.getCardNo());
		rechgAlyInfoDto.setAmt(AmtParseUtil.strToLongAmt(rechargeInfo.getRechargeAmt()));

		logger.info("[rechargeApply inparam is :]" + rechgAlyInfoDto.toString());
		CommRespDto commRespDto = rechargeApp.rechargeApply(rechgAlyInfoDto);
		if (ComConst.SUCCESS.equals(commRespDto.getResIdentifier().getReturnType())) {
			resp = new ResponseJson().success(commRespDto.getData());
		} else {
			resp = new ResponseJson().failure(commRespDto.getResIdentifier().getReturnCode() + "|" + commRespDto.getResIdentifier().getReturnMsg());
		}

		return resp;

	}

	private void accRechargeCheck(RechargeInfo rechargeInfo) throws MyBusinessCheckException {
		if (RechargeType.parseOf(rechargeInfo.getRechargeType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "custType");
		if (!RechargeType.SHORTCUT.getValue().equals(rechargeInfo.getRechargeType()))
			throw new MyBusinessCheckException(ErrorCode.POB001, "目前只支持快捷支付充值");
		// if (!TokenCheck.SUCCESS.getValue()
		// .equals(JWTutils.verifyTokenAndRemove(rechargeInfo.getToken(),
		// rechargeInfo.getCustId())))
		// throw new MyBusinessCheckException(ErrorCode.POC011);
	}

	/**
	 * 充值异步结果通知
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/rechargenotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson rechargeNotice(@RequestBody RchgNoticeInfo rchgNoticeInfo) {
		ResponseJson resp = null;
		RchgNoticeDto rchgNoticeDto = new RchgNoticeDto();
		BeanParseUtils.copyProperties(rchgNoticeInfo, rchgNoticeDto);
		try {
			rechargeApp.rechargeNotice(rchgNoticeDto);
		} catch (MyBusinessCheckException e) {
			logger.error("【rechargeNotice exception】", e);
			return new ResponseJson().failure(e.getErrorCode() + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();
		return resp;
	}

}

