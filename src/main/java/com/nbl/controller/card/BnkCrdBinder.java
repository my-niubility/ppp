package com.nbl.controller.card;

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
import com.nbl.domain.BndBnkCrdInfo;
import com.nbl.service.business.constant.CredentialsType;
import com.nbl.service.business.constant.CustType;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.app.BindBankCardApp;
import com.nbl.service.user.dto.req.BindNoticeDto;
import com.nbl.service.user.dto.req.BndBnkCardDto;
import com.nbl.service.user.dto.req.ChkBndCrdInfoDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.utils.BeanParseUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * 绑卡
 * 
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/bindcard")
public class BnkCrdBinder {
	private final static Logger logger = LoggerFactory.getLogger(BnkCrdBinder.class);
	@Resource
	private BindBankCardApp bindBankCardApp;
	@Autowired
	private HttpSession session;

	/**
	 * 提交绑定银行卡信息
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/bcapply", method = RequestMethod.POST)
	public @ResponseBody ResponseJson bindCardApply(@Valid @RequestBody BndBnkCrdInfo bndBnkCrdInfo) {

		ResponseJson resp = null;
		UserInfo userInfo = null;
		try {
			//用户类型、证件类型、认证手机号与注册手机号是否相同校验
			bindCardApplyCheck(bndBnkCrdInfo);
			userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			//短信验证码校验
			MsgPicCertCodeUtil.checkMsgCertCode(session, bndBnkCrdInfo.getMsgIdenCode(), userInfo.getMobile());
			// 校验参数（用户是否已经认证成功、认证使用的银行卡是否与绑定的银行卡是否为同一卡片）
			ChkBndCrdInfoDto chkBndCrdInfoDto = new ChkBndCrdInfoDto();
			BeanParseUtils.copyProperties(bndBnkCrdInfo, chkBndCrdInfoDto);
			
			chkBndCrdInfoDto.setId(bndBnkCrdInfo.getIdentityCardNumber());
			chkBndCrdInfoDto.setCardNum(bndBnkCrdInfo.getCardNo());
			//用户是否认证
			bindBankCardApp.checkBnkCrdInfo(chkBndCrdInfoDto);
		} catch (MyBusinessCheckException e) {
			logger.error("[certification check exception...]", e);
			return new ResponseJson().failure(e.getErrorCode() + "|" + e.getErrMsgKey());
		}
		// 提交认证申请
		if (CustType.PERONAL.getValue().equals(bndBnkCrdInfo.getCustAccType())) {
			BndBnkCardDto bndBnkCardDto = new BndBnkCardDto();
			BeanParseUtils.copyProperties(bndBnkCrdInfo, bndBnkCardDto);

			CommRespDto result = bindBankCardApp.bindCardApply(bndBnkCardDto);

			if (ComConst.SUCCESS.equals(result.getResIdentifier().getReturnType())) {
				resp = new ResponseJson().success(result.getData());
			} else {
				resp = new ResponseJson().failure(result.getResIdentifier().getReturnCode() + "|" + result.getResIdentifier().getReturnMsg());
			}
		}
		if (CustType.ENTERPRISE.getValue().equals(bndBnkCrdInfo.getCustAccType())) {
			// TODO 商户认证
			resp = new ResponseJson().failure("暂未开通商户绑卡");
		}

		return resp;
	}

	private void bindCardApplyCheck(BndBnkCrdInfo bndBnkCrdInfo) throws MyBusinessCheckException {
		if (CustType.parseOf(bndBnkCrdInfo.getCustAccType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "custType");
		if (CredentialsType.parseOf(bndBnkCrdInfo.getCredentialsType()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "credentialsType");
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
		if (!bndBnkCrdInfo.getPhoneNum().equals(userInfo.getLoginName()))
			throw new MyBusinessCheckException(ErrorCode.POC020);
	}

	/**
	 * 绑卡异步结果通知
	 * 
	 * @param personCust
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/bindnotice", method = RequestMethod.POST)
	public @ResponseBody ResponseJson bindNotice(@RequestBody BindNoticeDto bindNoticeDto) {
		ResponseJson resp = null;

		bindBankCardApp.bindNotice(bindNoticeDto);

		resp = new ResponseJson().success();
		return resp;
	}

}
