package com.nbl.controller.login;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
import com.nbl.domain.CheckContent;
import com.nbl.domain.CustInfo;
import com.nbl.domain.LoginInfo;
import com.nbl.domain.LogoutInfo;
import com.nbl.domain.PersonCust;
import com.nbl.service.business.constant.RegChannelCode;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.business.constant.UserType;
import com.nbl.service.user.app.RegisteUserApp;
import com.nbl.service.user.dto.req.UserInfoDto;
import com.nbl.service.user.dto.req.UserLoginDto;
import com.nbl.service.user.dto.res.LoginResJsonDto;
import com.nbl.service.user.dto.res.LoginResultDto;
import com.nbl.service.user.dto.res.RegArgtResult;
import com.nbl.service.user.dto.res.RegChkResultDto;
import com.nbl.service.user.dto.res.RegisteResultDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.util.EncryptUtil;
import com.nbl.util.ErrCodeUtil;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.HttpClientUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.cache.LoginCache;
import com.nbl.utils.json.ResponseJson;

/**
 * 注册登录
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/user")
public class UserRegister {
	private final static Logger logger = LoggerFactory.getLogger(UserRegister.class);
	@Resource
	private RegisteUserApp registeUserApp;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;

	@RequestMapping(value = "/registe/apply", method = RequestMethod.POST)
	public @ResponseBody ResponseJson userRegister(@Valid @RequestBody PersonCust personCust) {

		ResponseJson resp = null;

		try {
			// 用户类型、注册渠道校验
			userRegisterCheck(personCust);
			// 短信验证码是否过期，注册手机号、短息验证码输入是否正确校验
			MsgPicCertCodeUtil.checkMsgCertCode(session, personCust.getMsgIdenCode(), personCust.getMobile());
		} catch (MyBusinessCheckException e) {
			logger.error("[chkRechgInfo exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		// 密码转码(密码+手机号)
		String pwd = EncryptUtil.encodeDouble(personCust.getPassword() + personCust.getMobile());
		logger.debug("[user password is]:" + pwd);

		// 组装DTO数据
		UserInfoDto userInfoDto = new UserInfoDto();
		BeanParseUtils.copyProperties(personCust, userInfoDto);
		userInfoDto.setPassword(pwd);
		logger.info("[userInfoDto is]:" + userInfoDto.toString());
		userInfoDto.setIp(HttpClientUtils.getClientIP(request));

		// 登记注册信息
		CommRespDto result = registeUserApp.registeUserApp(userInfoDto);
		logger.info("[result is]:" + result.toString());
		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			UserInfo userInfo = new UserInfo();
			RegisteResultDto regRes = (RegisteResultDto) result.getData();
			BeanParseUtils.copyProperties(regRes.getUserInfo(), userInfo);
			session.setAttribute(SessionKeys.USER_INFO.getValue(), userInfo);
			CustInfo custInfo = new CustInfo(regRes.getCustId());
			// 注册成功返回custId
			resp = new ResponseJson().success(custInfo);
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}
		return resp;
	}

	private void userRegisterCheck(PersonCust personCust) throws MyBusinessCheckException {
		if (UserType.parseOf(personCust.getCustType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "custType");
		if (RegChannelCode.parseOf(personCust.getRegChanCode()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "regChanCode");
	}

	/**
	 * 注册信息校验
	 * 
	 * @param checkContent
	 * @return
	 */
	@RequestMapping(value = "/registe/check", method = RequestMethod.POST)
	public @ResponseBody ResponseJson regCheck(@RequestBody CheckContent checkContent) {
		ResponseJson response = null;
		switch (Integer.parseInt(checkContent.getCheckType())) {
		case ComConst.IS_REGISTED:
			// 手机号是否已注册
			RegChkResultDto result = registeUserApp.isRegistedApp(checkContent.getLoginName());
			logger.info("[isRegisted]:" + result.getIsRegisted());
			response = new ResponseJson().success(result);
			break;
		default:
			logger.info("[check type :]" + checkContent.getCheckType() + " is beside");
			response = new ResponseJson().failure("非法的校验类型" + checkContent.getCheckType());
			break;
		}
		return response;
	}

	/**
	 * 查询注册协议
	 * 
	 * @param checkContent
	 * @return
	 */
	@RequestMapping(value = "/registe/getRegAgrt", method = RequestMethod.GET)
	public @ResponseBody ResponseJson getRegAgrt() {
		ResponseJson resp = null;
		RegArgtResult result = registeUserApp.getRegAgrt();
		resp = new ResponseJson().success(result);
		return resp;
	}

	/**
	 * 用户登录
	 * 
	 * @param checkContent
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody ResponseJson userLogin(@Valid	@RequestBody LoginInfo loginInfo) {
		ResponseJson resp;
		// 验证码校验
		try {
			MsgPicCertCodeUtil.checkPicCertCode(session, loginInfo.getCertCode());
		} catch (MyBusinessCheckException e) {
			logger.error(e.getErrorCode() + e.getErrMsgKey());
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		// 查询登录帐户信息
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setMobile(loginInfo.getMobile());
		String pwd = EncryptUtil.encodeDouble(loginInfo.getPassword() + loginInfo.getMobile());
		logger.debug("[user password is]:" + pwd);
		userLoginDto.setPassword(pwd);
		userLoginDto.setIp(HttpClientUtils.getClientIP(request));
		userLoginDto.setSessionId(session.getId());
		CommRespDto result = registeUserApp.loginApp(userLoginDto);
		logger.info("【loginApp result】:" + result.toString());
		if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
			LoginResultDto loginResult = (LoginResultDto) result.getData();
			//TODO 是否多点登录(已存在custId，且session不同)
//			if (!LoginCache.custIds.add(loginResult.getUserInfo().getCustId()) && LoginCache.sessionIds.add(session.getId())) {
//				// TODO 更新最后登录时间
//				LoginCache.sessionIds.remove(session.getId());
//				return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC022));
//			}
			// 登录成功后，将用户信息放到session中
			UserInfo userInfo = new UserInfo();
			BeanParseUtils.copyProperties(loginResult.getUserInfo(), userInfo);
			session.setAttribute(SessionKeys.USER_INFO.getValue(), userInfo);
			LoginResJsonDto loginRes = new LoginResJsonDto();
			BeanParseUtils.copyProperties(loginResult, loginRes);
			loginRes.setCustId(userInfo.getCustId());
			resp = new ResponseJson().success(loginRes);
		} else {
			resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
		}

		return resp;
	}

	/**
	 * 用户登录退出（注销）
	 * 
	 * @param checkContent
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody ResponseJson userLogout(@Valid@RequestBody LogoutInfo logoutInfo) {
		ResponseJson resp = null;
		UserInfo userInfo = null;
		if (session == null) {
			return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC021, logoutInfo.getMobile()));
		} else {
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			if (userInfo == null) {
				return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC021, logoutInfo.getMobile()));
			} else {
				if (!logoutInfo.getMobile().equals(userInfo.getMobile())) {
					logger.error("【phone number doesn't match】:" + userInfo.getMobile());
					return new ResponseJson().failure(ErrCodeUtil.getErrMsgStr(ErrorCode.POC021, logoutInfo.getMobile()));
				}
			}
		}
		// 从session中移除用户登录对象
		session.setAttribute(SessionKeys.USER_INFO.getValue(), null);
		LoginCache.custIds.remove(userInfo.getCustId());
		LoginCache.sessionIds.remove(session.getId());
		resp = new ResponseJson().success();
		return resp;
	}

}
