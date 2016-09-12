package com.nbl.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.nbl.service.business.constant.SessionKeys;
import com.nbl.service.user.dto.res.UserInfo;
import com.nbl.utils.cache.LoginCache;

public class LoginFilter implements Filter {
	private final static Logger logger = LoggerFactory.getLogger(LoginFilter.class);

	private Pattern excepUrlPattern;
	private JSONObject jo;
	private String retMsgHead = "{\"meta\":{\"success\":false,\"message\":\"";
	private String retMsgFoot = "\"},\"data\":null}";

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		String excepUrlRegex = cfg.getInitParameter("excepUrlRegex");
		if (!StringUtils.isBlank(excepUrlRegex)) {
			excepUrlPattern = Pattern.compile(excepUrlRegex);
		}
	}

	public static void main(String[] args) {
		String url = "/restful/token/get";
		Pattern excepUrlPattern = Pattern.compile(".*/restful/(token/.*|idencode/.*|user/(registe/.*|login)|pwdm/setloginpwd|product/query/.*|tradeorder/(qrytrdord|prdtrdhis))");
		if (excepUrlPattern.matcher(url).matches()) {
			System.out.println("[pass check]");
		} else {
			System.out.println("[NO pass]");
		}

		String jsonHead = "{\"custId\":";
		String jsonFoot = ",\"mobile\":\"13998126666\"}";
		String json = jsonHead + "88888888" + jsonFoot;
		JSONObject obj = JSONObject.parseObject(json);
		System.out.println("custId:" + obj.getString("custId"));
		System.out.println(obj.toString());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;

		HttpSession session = servletRequest.getSession(false);
		ServletRequest requestWrapper = null;

		String uri = servletRequest.getRequestURI();
		logger.info("【URI:】" + uri);

		if (excepUrlPattern.matcher(uri).matches()) {
			chain.doFilter(request, response);
			return;
		}

		if (!(servletRequest.getHeader("x-requested-with") != null && servletRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))) {
			logger.error("not ajax request");
			logger.error("【header error】");
			ajaxError(servletResponse);
		}

		if (session == null) {
			notLogin(session, jo.getString("custId"), servletResponse);
		} else {
			UserInfo userInfo = (UserInfo) session.getAttribute(SessionKeys.USER_INFO.getValue());
			if (userInfo == null) {
				notLogin(session, jo.getString("custId"), servletResponse);
			} else {
				requestWrapper = new MyHttpServletRequest((HttpServletRequest) request);
				if (userInfo.getId().equals(jo.getString("custId"))) {
					logger.info("【pass seesion check】");
					chain.doFilter(requestWrapper, response);
				} else {
					logger.error("【id doesn't match】:" + servletRequest.getAttribute("custId"));
					notLogin(session, jo.getString("custId"), servletResponse);
				}

			}
		}
	}

	private class MyHttpServletRequest extends HttpServletRequestWrapper {

		private String _body;

		public MyHttpServletRequest(HttpServletRequest request) throws IOException {
			super(request);

			StringBuffer jsonStr = new StringBuffer();
			try (BufferedReader bufferedReader = request.getReader()) {
				String line;
				while ((line = bufferedReader.readLine()) != null)
					jsonStr.append(line);
			}
			logger.info("【request json】:" + jsonStr.toString());

			jo = JSONObject.parseObject(jsonStr.toString());
			_body = jo.toJSONString();
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes("UTF-8"));
			return new ServletInputStream() {
				public int read() throws IOException {
					return byteArrayInputStream.read();
				}

				@Override
				public boolean isFinished() {
					return false;
				}

				@Override
				public boolean isReady() {
					return false;
				}

				@Override
				public void setReadListener(ReadListener listener) {
				}
			};
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(this.getInputStream()));
		}

	}

	@Override
	public void destroy() {

	}

	private void notLogin(HttpSession session, String custId, HttpServletResponse servletResponse) throws IOException {
		LoginCache.custIds.remove(custId);
		if (session != null && session.getId() != null) {
			LoginCache.sessionIds.remove(session.getId());
		}
		servletResponse.setContentType("text/xml;charset=UTF-8");
		PrintWriter printWriter = servletResponse.getWriter();
		printWriter.print(retMsgHead + URLDecoder.decode("未登录无权操作，请您先登录", "UTF-8") + retMsgFoot);
		printWriter.flush();
		printWriter.close();
	}

	private void ajaxError(HttpServletResponse servletResponse) throws IOException {
		servletResponse.setContentType("text/xml;charset=UTF-8");
		PrintWriter printWriter = servletResponse.getWriter();
		printWriter.print(retMsgHead + URLDecoder.decode("Ajax请求类型错误", "UTF-8") + retMsgFoot);
		printWriter.flush();
		printWriter.close();
	}

}
