package com.nbl.controller.idencode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
import com.nbl.common.exception.MyBusinessCheckException;
import com.nbl.domain.MsgCertCode;
import com.nbl.domain.MsgCertCodeBean;
import com.nbl.service.business.constant.CertBusCase;
import com.nbl.service.business.constant.CertBusCaseCI;
import com.nbl.service.business.constant.CertBusCaseTO;
import com.nbl.service.business.constant.OrderStatus;
import com.nbl.service.business.constant.ParamKeys;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.manager.app.GeneralParameterApp;
import com.nbl.service.manager.app.TradeOrderApp;
import com.nbl.service.manager.dto.TradeOrderReqDto;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.util.CertCodeUtil;
import com.nbl.util.DateTimeUtils;
import com.nbl.utils.HttpClientUtils;
import com.nbl.utils.MsgPicCertCodeUtil;
import com.nbl.utils.json.ResponseJson;

/**
 * @author AlanMa
 *
 */
@Controller
@RequestMapping("/restful/idencode")
public class IdentifyingCode {
	private final static Logger logger = LoggerFactory.getLogger(IdentifyingCode.class);
	public static Map<String, Long> map = new ConcurrentHashMap<String, Long>();
	@Autowired
	private HttpSession session;
	// TODO 测试模拟短信网关
	@Autowired
	private ServletContext sc;
	@Resource
	private TradeOrderApp tradeOrderApp;
	@Resource
	private GeneralParameterApp generalParameterApp;
	// 发送短信时间间隔
	private static int GEN_INTERVAL = 55 * 1000;
	// 验证码失效时间
	private static int DEAD_INTERVAL = 5 * 60 * 1000;

	static {
		IdentifyingCode.MsgCerCodeCache mccc = new IdentifyingCode().new MsgCerCodeCache();
		Thread t = new Thread(mccc);
		t.start();
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param msgCertCode
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sendMsgCode", method = RequestMethod.POST)
	public @ResponseBody ResponseJson sendMsgCode(@Valid @RequestBody MsgCertCode msgCertCode) {
		String certCode = null;
		try {
			// 短信验证码时间校验
			MsgPicCertCodeUtil.checkMsgCertCodeTime(msgCertCode.getPhoneNum());
			// 短信验证码使用场景、custId非空、用户是否登录校验
			businessCheck(msgCertCode);
			//用户输入手机号与注册手机号是否相同校验
			phoneNumCheck(msgCertCode);
			// 生成短信验证码
			certCode = CertCodeUtil.getRegMsgCertCode();
			logger.info("[phoneNum]:" + msgCertCode.getPhoneNum() + "|" + "[certCodeUtil]:" + certCode);
			if (ComConst.TRUE.equals(generalParameterApp.getValueByCode(ParamKeys.MCC_MODE.getValue()))) {
				// 生产短信网关
				String des = generalParameterApp.getValueByCode(ParamKeys.MCC_CONT.getValue()).replaceFirst("%s", certCode);
				String url = generalParameterApp.getValueByCode(ParamKeys.MCC_URL.getValue());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("Name", generalParameterApp.getValueByCode(ParamKeys.MCC_UN.getValue()));
				paramMap.put("Passwd", generalParameterApp.getValueByCode(ParamKeys.MCC_PWD.getValue()));
				paramMap.put("Phone", msgCertCode.getPhoneNum());
				paramMap.put("Content", des);
				logger.info("[paramMap]:" + paramMap.toString());
				HttpClientUtils.sendPost(url, paramMap, null, "application/x-www-form-urlencoded;charset=utf-8");
			} else {
				// TODO 测试模式将短信验证发送至模拟短信网关
				List<MsgCertCodeBean> msgCertBeans = null;
				MsgCertCodeBean msgCertBean = new MsgCertCodeBean();
				msgCertBean.setMsgCertCode(certCode);
				msgCertBean.setPhoneNum(msgCertCode.getPhoneNum());
				msgCertBean.setCustId(msgCertCode.getCustId());
				msgCertBean.setCertBusCase(msgCertCode.getCertBusCase());
				msgCertBean.setSendTime(DateTimeUtils.now().toDateTimeString());
				msgCertBean.setDeadTime(new DateTimeUtils(DateTimeUtils.getMinAfterDate("5")).toDateTimeString());
				msgCertBean.setTradeOrderId(msgCertCode.getTradeOrderId());
				msgCertBeans = (List<MsgCertCodeBean>) sc.getAttribute("msgCertBeans");
				msgCertBeans = msgCertBeans == null ? new ArrayList<MsgCertCodeBean>() : msgCertBeans;
				msgCertBeans.add(msgCertBean);
				sc.setAttribute("msgCertBeans", msgCertBeans);
			}
			session.setAttribute(SessionKeys.CERT_CODE_PHO.getValue(), msgCertCode.getPhoneNum().trim());
			session.setAttribute(SessionKeys.MSG_CERT_CODE.getValue(), certCode);
		} catch (MyBusinessCheckException e) {
			logger.error("get msg cert code exception", e);
			return new ResponseJson().failure(e.getErrorCode() + e.getErrMsgKey());
		} catch (ParseException e) {
			logger.error("ParseException", e);
			return new ResponseJson().failure(e.getMessage());
		}
		return new ResponseJson().success();
	}

	private void businessCheck(MsgCertCode msgCertCode) throws MyBusinessCheckException {
		if (CertBusCase.parseOf(msgCertCode.getCertBusCase()) == null)
			throw new MyBusinessCheckException(ErrorCode.POC008, "msgCertCode");
		if (CertBusCaseCI.parseOf(msgCertCode.getCertBusCase()) != null) {
			if (StringUtils.isEmpty(msgCertCode.getCustId())) {
				throw new MyBusinessCheckException(ErrorCode.POC006, "custId");
			} else {
				UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
				if (userInfo == null || !msgCertCode.getCustId().equals(userInfo.getCustId())) {
					throw new MyBusinessCheckException(ErrorCode.POB001, "未登录无权操作，请您先登录");
				}
			}
		}
		if (CertBusCaseTO.parseOf(msgCertCode.getCertBusCase()) != null) {
			if (StringUtils.isEmpty(msgCertCode.getTradeOrderId())) {
				throw new MyBusinessCheckException(ErrorCode.POC006, "tradeOrderId");
			} else if (StringUtils.isEmpty(msgCertCode.getCustId())) {
				throw new MyBusinessCheckException(ErrorCode.POC006, "custId");
			} else {
				UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
				if (!msgCertCode.getCustId().equals(userInfo.getCustId())) {
					throw new MyBusinessCheckException(ErrorCode.POB001, "未登录无权操作，请您先登录");
				}
				TradeOrderReqDto to = tradeOrderApp.tradeOrderDetail(msgCertCode.getTradeOrderId());
				if (to == null) {
					throw new MyBusinessCheckException(ErrorCode.POB001, "订单号" + msgCertCode.getTradeOrderId() + "错误");
				}
				String orderStatus = to.getOrderStatus();
				if (OrderStatus.PAY_SUCCESS.getValue().equals(orderStatus) || OrderStatus.ORDER_CANCEL.getValue().equals(orderStatus)) {
					throw new MyBusinessCheckException(ErrorCode.POC017, to.getId(), OrderStatus.parseOf(orderStatus).getDisplayName());
				}
			}
		}
	}

	/**
	 * 获取图形验证码
	 * 
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping(value = "/getCertCode", method = RequestMethod.GET)
	public void getCode(HttpServletResponse resp) throws IOException {
		final int width = 95;// 定义图片的width
		final int height = 40;// 定义图片的height
		final int codeCount = 4;// 定义图片上显示验证码的个数
		final int xx = 25;// 数值越大间距越大
		final int fontHeight = 28;
		final int codeY = 31;// 数值越大越靠下
		char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
				'7', '8', '9' };

		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Graphics2D gd = buffImg.createGraphics();
		// Graphics2D gd = (Graphics2D) buffImg.getGraphics();
		Graphics gd = buffImg.getGraphics();
		// 创建一个随机数生成器类
		Random random = new Random();
		// 将图像填充为白色
		gd.setColor(Color.WHITE);
		gd.fillRect(0, 0, width, height);

		// 创建字体，字体的大小应该根据图片的高度来定。
		Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
		// 设置字体。
		gd.setFont(font);

		// 画边框。
		gd.setColor(Color.BLACK);
		gd.drawRect(0, 0, width - 1, height - 1);

		// 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
		gd.setColor(Color.BLACK);
		for (int i = 0; i < 40; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			gd.drawLine(x, y, x + xl, y + yl);
		}

		// randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
		StringBuffer randomCode = new StringBuffer();
		int red = 0, green = 0, blue = 0;

		// 随机产生codeCount数字的验证码。
		for (int i = 0; i < codeCount; i++) {
			// 得到随机产生的验证码数字。
			String code = String.valueOf(codeSequence[random.nextInt(36)]);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			red = random.nextInt(255);
			green = random.nextInt(255);
			blue = random.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中。
			gd.setColor(new Color(red, green, blue));
			if (i == 0) {
				gd.drawString(code, 1, codeY);
			} else {
				gd.drawString(code, i * xx, codeY);
			}

			// 将产生的四个随机数组合在一起。
			randomCode.append(code);
		}
		// 将四位数字的验证码保存到Session中。
		session.setAttribute(SessionKeys.PIC_CERT_CODE.getValue(), randomCode.toString());
		logger.info("[pic random code is]:" + randomCode);

		// 禁止图像缓存。
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);

		resp.setContentType("image/jpeg");

		// 将图像输出到Servlet输出流中。
		ServletOutputStream sos = resp.getOutputStream();
		ImageIO.write(buffImg, "jpeg", sos);
		sos.close();
	}

	class MsgCerCodeCache implements Runnable {

		Set<String> keys = map.keySet();

		private void refreshCache() {
			for (String key : keys) {
				// 验证码输入超时
				if (System.currentTimeMillis() - map.get(key) > GEN_INTERVAL) {
					map.remove(key);
				}
				// 验证码失效，清除session手机号跟验证码
				if (System.currentTimeMillis() - map.get(key) > DEAD_INTERVAL && key.equals(session.getAttribute(SessionKeys.CERT_CODE_PHO.getValue()))) {
					session.removeAttribute(SessionKeys.CERT_CODE_PHO.getValue());
					session.removeAttribute(SessionKeys.MSG_CERT_CODE.getValue());
				}
			}
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e) {
				}
				refreshCache();
			}
		}

	}
	
	public void phoneNumCheck(MsgCertCode msgCertCode) throws MyBusinessCheckException{
		//手机号合法性校验
		String phoneNum = msgCertCode.getPhoneNum();
		String regexPhoneNum = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
		Pattern patternPhoneNum = Pattern.compile(regexPhoneNum);
		if(!patternPhoneNum.matcher(phoneNum).matches()){
			throw new MyBusinessCheckException(ErrorCode.POC008, "phoneNum");
		}
		//场景为非注册场景下手机号与注册手机号是否相同校验
		String regexCertBusCase = "^[0][2-9]$";
		Pattern patternCertBusCase = Pattern.compile(regexCertBusCase);
		if(patternCertBusCase.matcher(msgCertCode.getCertBusCase()).matches()){
			UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			if(!userInfo.getMobile().equals(phoneNum)){
				throw new MyBusinessCheckException(ErrorCode.POC024, "phoneNum");
			}
		}
	}
}