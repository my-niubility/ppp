package com.nbl.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.nbl.common.constants.ErrorCode;
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.controller.account.AccRecharge;
import com.nbl.service.business.constant.SessionKeys;

public class MsgPicCertCodeUtil {

	private final static Logger logger = LoggerFactory.getLogger(AccRecharge.class);

	public static Map<String, Long> map = new ConcurrentHashMap<String, Long>();

	private static int GEN_INTERVAL = 55 * 1000;
	
	/**
	 * 短信验证码校验
	 * @param session
	 * @param postCertCode
	 * @param phoneNum
	 * @throws MyBusinessCheckException
	 */
	public static void checkMsgCertCode(HttpSession session, String postCertCode, String phoneNum) throws MyBusinessCheckException {
		String sessionCertCode = (String) (session.getAttribute(SessionKeys.MSG_CERT_CODE.getValue()));
		String sessionPhoneNum = (String) (session.getAttribute(SessionKeys.CERT_CODE_PHO.getValue()));
		//短信验证码是否过期
		if (StringUtils.isEmpty(sessionCertCode) || StringUtils.isEmpty(sessionPhoneNum)) {
			throw new MyBusinessCheckException(ErrorCode.POC005);
		}
		//短信验证码是否输入正确
		if (!postCertCode.equals(sessionCertCode)) {
			logger.info("短信验证码校验失败：" + session.getAttribute(SessionKeys.MSG_CERT_CODE.getValue()));
			throw new MyBusinessCheckException(ErrorCode.POC001, postCertCode);
		}
		//手机号是否正确
		if (!phoneNum.equals(sessionPhoneNum)) {
			logger.info("短信验证码校验失败：" + session.getAttribute(SessionKeys.CERT_CODE_PHO.getValue()));
			throw new MyBusinessCheckException(ErrorCode.POC010);
		}
		//校验完将手机号、短信验证码置空
		session.setAttribute(SessionKeys.MSG_CERT_CODE.getValue(), null);
		session.setAttribute(SessionKeys.CERT_CODE_PHO.getValue(), null);
	}
	
	/**
	 * 图片验证码校验
	 * @param session
	 * @param postCertCode
	 * @throws MyBusinessCheckException
	 */
	public static void checkPicCertCode(HttpSession session, String postCertCode) throws MyBusinessCheckException {
		String sessionCertCode = (String) (session.getAttribute(SessionKeys.PIC_CERT_CODE.getValue()));
		if (StringUtils.isEmpty(sessionCertCode)) {
			throw new MyBusinessCheckException(ErrorCode.POC005, postCertCode);
		}
		if (!postCertCode.equalsIgnoreCase(sessionCertCode)) {
			logger.info("图形验证码校验失败：" + sessionCertCode);
			throw new MyBusinessCheckException(ErrorCode.POC003, postCertCode);
		}
		session.setAttribute(SessionKeys.PIC_CERT_CODE.getValue(), null);
	}
	
	public static void checkMsgCertCodeTime(String phoneNum) throws MyBusinessCheckException {

		if (map.get(phoneNum) == null || map.get(phoneNum) == 0) {
			logger.info("no record [phoneNum]:" + phoneNum);
			map.put(phoneNum, System.currentTimeMillis());
		} else {
			logger.info("[phoneNum]:" + phoneNum + "\t[map]:" + map.toString());
			if (System.currentTimeMillis() - map.get(phoneNum) < GEN_INTERVAL) {
				throw new MyBusinessCheckException(ErrorCode.POC016);
			}
		}
	}

}
