package com.nbl.utils.jjwt;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * @author Donald
 * @createdate 2016年7月8日
 * @version 1.0
 * @description :token工具类，请根据实际需求使用 提供 1、生成普通的token值，并提供普通的token校验
 *              2、生成防重复提交的token值，并提供防重复token校验
 */
public class JWTutils {

	private final static Logger logger = LoggerFactory.getLogger(JWTutils.class);

	private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

	private final static SecretKey key = MacProvider.generateKey(SignatureAlgorithm.HS512);

	/**
	 * @param userName
	 *            登录系统的用户名
	 * @param expMills
	 *            token过期时间，已毫秒为单位.默认是30分钟
	 * @return
	 * @description:生成普通的token值
	 */
	public static String generateSingalToken(String userName, long expMills) {

		long exp = 30 * 60 * 1000;
		if (expMills > 0) {
			exp = expMills;
		}

		String singalToken = Jwts.builder().setIssuer("zlebank").setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + exp)).signWith(SignatureAlgorithm.HS512, key)
				.compact();
		logger.info("[token]:" + singalToken);
		return singalToken;
	}

	/**
	 * @param token
	 *            需要校验的token字符串值
	 * @param userName
	 *            系统登录名称
	 * @return
	 * @description:提供普通的token校验
	 */
	public static boolean verifyToken(String token, String userName) {

		boolean verifyFlag = true;
		try {
			verifyFlag = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject().equals(userName);
			logger.info("verify token flag = {}", verifyFlag);
		} catch (ExpiredJwtException ex) {
			logger.error("token ExpiredJwtException，对应的用户是：{}", userName);
			verifyFlag = false;
		} catch (UnsupportedJwtException ex) {
			logger.error("token UnsupportedJwtException，对应的用户是：{}", userName);
			verifyFlag = false;
		} catch (MalformedJwtException ex) {
			logger.error("token  MalformedJwtException，对应的用户是：{}", userName);
			verifyFlag = false;
		} catch (PrematureJwtException ex) {
			logger.error("token PrematureJwtException，对应的用户是：{}", userName);
			verifyFlag = false;
		} catch (SignatureException ex) {
			logger.error("token SignatureException，对应的用户是：{}", userName);
			verifyFlag = false;
		} catch (ClaimJwtException ex) {
			logger.error("token ClaimJwtException，对应的用户是：{}", userName);
			verifyFlag = false;
		}
		return verifyFlag;
	}

	/**
	 * @param userName
	 *            登录系统的用户名
	 * @param expMills
	 *            token过期时间，已毫秒为单位.默认是30分钟
	 * @return
	 * @description:生成防重复提交的token值
	 */
	public static String generateRepeateRequestToken(String userName, long expMills) {

		long exp = 30 * 60 * 1000;
		if (expMills > 0) {
			exp = expMills;
		}

		String singalToken = Jwts.builder().setIssuer("zlebank").setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + exp)).signWith(SignatureAlgorithm.HS512, key)
				.compact();
		// 保存
		map.put(singalToken, "0");
		return singalToken;
	}

	/**
	 * @param token
	 *            需要校验的token字符串值
	 * @param userName
	 *            系统登录名称
	 * @return "1"-成功， "2"-重复请求，"3"-校验失败，"4"-异常
	 * @description:提供防重复token校验
	 */
	public static String verifyTokenAndRemove(String token, String userName) {
		logger.info("[token in]:" + token);
		logger.info("[token map]:" + map.toString());
		// 成功
		String verifyFlag = "1";
		try {
			String value = map.get(token);
			if (value == null) {
				// 重复提交
				return "2";
			} else {
				synchronized (map) {
					String sed = map.get(token);
					if (sed != null) {
						logger.info("romove token begin...........");
						map.remove(token);
						logger.info("romove token end...........");
					} else {
						// 重复提交
						return "2";
					}
				}
			}

			if (!Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject().equals(userName)) {
				// 校验失败
				return "3";
			}

		} catch (ExpiredJwtException ex) {
			logger.error("token ExpiredJwtException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		} catch (UnsupportedJwtException ex) {
			logger.error("token UnsupportedJwtException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		} catch (MalformedJwtException ex) {
			logger.error("token  MalformedJwtException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		} catch (PrematureJwtException ex) {
			logger.error("token PrematureJwtException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		} catch (SignatureException ex) {
			logger.error("token SignatureException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		} catch (ClaimJwtException ex) {
			logger.error("token ClaimJwtException，对应的用户是：{}", userName);
			// 校验异常
			verifyFlag = "4";
		}
		logger.info("[token verifyFlag]:" + verifyFlag);
		return verifyFlag;

	}

	public static void main(String[] args) throws InterruptedException {
		// String token = generateSingalToken("donald",30000);
		String token = generateRepeateRequestToken("donald", new Long(0));
		System.out.println("generate token=" + token);
		System.out.println("verify result:=" + verifyTokenAndRemove(token, "donald"));
		System.out.println("verify result:=" + verifyTokenAndRemove(token, "donald"));
	}
}
