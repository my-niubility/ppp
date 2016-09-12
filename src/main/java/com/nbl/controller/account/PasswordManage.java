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

import com.alibaba.dubbo.common.utils.StringUtils;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.ModPwdInfo;
import com.nbl.domain.PayPwdInfo;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.constant.SetLoginPwdType;
import com.nbl.service.business.constant.SetPwdType;
import com.nbl.service.user.app.PwdManageApp;
import com.nbl.service.user.dto.req.LogPwdInfoDto;
import com.nbl.service.user.dto.req.PayPwdInfoDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.util.EncryptUtil;
import com.nbl.util.ErrCodeUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * 密码管理
 * 
 * @author AlanMa
 */
@Controller
@RequestMapping("/restful/pwdm")
public class PasswordManage {
	private final static Logger logger = LoggerFactory.getLogger(PasswordManage.class);
	@Resource
	private PwdManageApp pwdManageApp;
	@Autowired
	private HttpSession session;

	/**
	 * 设置支付密码
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/setpaypwd", method = RequestMethod.POST)
	public @ResponseBody ResponseJson setPayPwd(@Valid @RequestBody PayPwdInfo payPwdInfo) {
		ResponseJson resp = null;
		String orgPwd = null;
		UserInfo userInfo = null;
		try {
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			//交易密码设置类型校验
			setPayPwdCheck(payPwdInfo);
			//忘记密码，重新设置
			if (SetPwdType.SET_NEW_PAY_PWD.getValue().equals(payPwdInfo.getSetType())) {
				//短信验证码校验，重置密码时短信验证码不能为空，修改密码时短信验证码为空
				MsgPicCertCodeUtil.checkMsgCertCode(session, payPwdInfo.getMsgIdenCode(), userInfo.getMobile());
			}
			//修改密码
			if (SetPwdType.MOD_PAY_PWD.getValue().equals(payPwdInfo.getSetType())) {
				//新旧密码是否为空
				if (StringUtils.isEmpty(payPwdInfo.getOrgPayPwd()) || StringUtils.isEmpty(payPwdInfo.getNewPayPwd())) {
					return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC006, "原密码和新密码"));
				}
				orgPwd = EncryptUtil.encodeDouble(payPwdInfo.getOrgPayPwd() + payPwdInfo.getPhoneNum());
				logger.debug("[user orginal payment password is]:" + orgPwd);
			}

			String pwd = EncryptUtil.encodeDouble(payPwdInfo.getNewPayPwd() + payPwdInfo.getPhoneNum());
			logger.debug("[user new payment password is]:" + pwd);
			PayPwdInfoDto payPwdInfoDto = new PayPwdInfoDto();
			BeanParseUtils.copyProperties(payPwdInfo, payPwdInfoDto);
			payPwdInfoDto.setNewPayPwd(pwd);
			payPwdInfoDto.setOrgPayPwd(orgPwd);

			pwdManageApp.setPayPwd(payPwdInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();

		return resp;
	}

	private void setPayPwdCheck(PayPwdInfo payPwdInfo) throws MyBusinessCheckException {
		if (SetPwdType.parseOf(payPwdInfo.getSetType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "setType");
	}

	/**
	 * 设置登录密码
	 * 
	 * @param certiInfo
	 * @return
	 */
	@RequestMapping(value = "/setloginpwd", method = RequestMethod.POST)
	public @ResponseBody ResponseJson setLoginPwd(@Valid @RequestBody ModPwdInfo modPwdInfo) {
		ResponseJson resp = null;
		String orgPwd = null;
		UserInfo userInfo = null;
		try {
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			if (userInfo != null)
				logger.info("[setLoginPwd userInfo:]" + userInfo.toString());
			String phonNum = userInfo == null ? modPwdInfo.getPhoneNum() : userInfo.getMobile();
			//设置新密码
			if (SetLoginPwdType.SET_NEW_LOG_PWD.getValue().equals(modPwdInfo.getSetType())) {
				MsgPicCertCodeUtil.checkMsgCertCode(session, modPwdInfo.getMsgIdenCode(), phonNum);
			}
			//修改密码
			if (SetPwdType.MOD_PAY_PWD.getValue().equals(modPwdInfo.getSetType())) {
				//判断新旧密码是否为空
				if (StringUtils.isEmpty(modPwdInfo.getOrgLoginPwd()) || StringUtils.isEmpty(modPwdInfo.getNewLoginPwd())) {
					return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC006, "原密码和新密码"));
				}
				orgPwd = EncryptUtil.encodeDouble(modPwdInfo.getOrgLoginPwd() + modPwdInfo.getPhoneNum());
				logger.debug("[user orginal payment password is]:" + orgPwd);
			}

			String pwd = EncryptUtil.encodeDouble(modPwdInfo.getNewLoginPwd() + phonNum);
			logger.debug("[user new login password is]:" + pwd);
			LogPwdInfoDto logPwdInfoDto = new LogPwdInfoDto();
			BeanParseUtils.copyProperties(modPwdInfo, logPwdInfoDto);
			logPwdInfoDto.setNewLoginPwd(pwd);
			logPwdInfoDto.setOrgLoginPwd(orgPwd);
			logPwdInfoDto.setPhoneNum(phonNum);

			pwdManageApp.setPayPwd(logPwdInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		resp = new ResponseJson().success();

		return resp;
	}

}
