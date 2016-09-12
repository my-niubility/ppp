package com.nbl.controller.certif;

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
import com.nbl.domain.CertiInfo;
import com.nbl.service.business.constant.CreateType;
import com.nbl.service.business.constant.CredentialsType;
import com.nbl.service.business.constant.CustType;
import com.nbl.service.business.constant.RegisteredType;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.app.CertificationApp;
import com.nbl.service.user.dto.req.CertNoticeDto;
import com.nbl.service.user.dto.req.ChkCertInfoDto;
import com.nbl.service.user.dto.req.UserCertDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.HttpClientUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * 用户认证
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/certifi")
public class Certification {
	private final static Logger logger = LoggerFactory.getLogger(Certification.class);
	@Resource
	private CertificationApp certificationApp;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;

	/**
	 * 提交实名认证信息
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody ResponseJson userCertificate(@Valid @RequestBody CertiInfo certiInfo) {

		ResponseJson resp = null;
		UserInfo userInfo = null;
		try {
			//用户实名认证时客户类型、证件类型、认证手机号与注册手机号是否相同校验
			userCertificateCheck(certiInfo);
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			//手机短信验证码校验
			MsgPicCertCodeUtil.checkMsgCertCode(session, certiInfo.getMsgIdenCode(), userInfo.getMobile());

			// 校验参数（用户是否存在、手机号与用户名是否一致、银行是否开通此业务）
			ChkCertInfoDto chkCertInfoDto = new ChkCertInfoDto();
			BeanParseUtils.copyProperties(certiInfo, chkCertInfoDto);
			//检查当前用户是否已经认证
			certificationApp.checkCertInfo(chkCertInfoDto);

			// 提交认证申请
			if (CustType.PERONAL.getValue().equals(certiInfo.getCustAccType())) {
				UserCertDto userCertDto = new UserCertDto();
				BeanParseUtils.copyProperties(certiInfo, userCertDto);
				
				userCertDto.setCreateType(CreateType.PORTAL.getValue());
				userCertDto.setRegisteredType(RegisteredType.PROTAL.getValue());
				userCertDto.setIp(HttpClientUtils.getClientIP(request));

				CommRespDto result = certificationApp.certificate(userCertDto);

				if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
					resp = new ResponseJson().success(result.getData());
				} else {
					resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
				}
			}
			if (CustType.ENTERPRISE.getValue().equals(certiInfo.getCustAccType())) {
				// TODO 商户认证
				resp = new ResponseJson().failure("暂未开通商户认证");
			}
		} catch (MyBusinessCheckException e) {
			logger.error("[certification check exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}

		return resp;
	}
	
	/**
	 * 用户实名认证时客户类型、证件类型、认证手机号与注册手机号是否相同校验
	 * @param certiInfo
	 * @throws MyBusinessCheckException
	 */
	private void userCertificateCheck(CertiInfo certiInfo) throws MyBusinessCheckException {
		if (CustType.parseOf(certiInfo.getCustAccType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "custType");
		if (CredentialsType.parseOf(certiInfo.getCredentialsType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "credentialsType");
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		if (!certiInfo.getPhoneNum().equals(userInfo.getLoginName()))
			throw new MyBusinessCheckException(ErrorCode.POC009);
	}

	/**
	 * 认证异步结果通知
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/certnotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson certNotice(@RequestBody CertNoticeDto certNoticeDto) {
		ResponseJson resp = null;

		certificationApp.certNotice(certNoticeDto);

		resp = new ResponseJson().success();
		return resp;
	}
}
