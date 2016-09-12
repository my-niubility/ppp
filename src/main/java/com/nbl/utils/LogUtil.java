package com.nbl.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName: 日志打印工具
 * @author AlanMa
 * @date 2016-07-15
 * 
 */
@Aspect
public class LogUtil {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String functionName = null;
	private StringBuffer inputParamStr = null; // 传入参数
	private Map<String, Object> outputParamMap = null; // 存放输出结果
	private long startTimeMillis = 0; // 开始时间
	private long endTimeMillis = 0; // 结束时间

	@Before("execution(* com.zlebank.controller..*.*(..)) and args(..)")
	public void doBeforeInServiceLayer(JoinPoint joinPoint) {
		startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
	}

	@After("execution(* com.zlebank.controller..*.*(..)) and args(..)")
	public void doAfterInServiceLayer(JoinPoint joinPoint) {
		endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
		this.printOptLog();
	}

	@Around("execution(* com.zlebank.controller..*.*(..)) and args(..)")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

		functionName = pjp.getTarget().getClass().getName() + "." + pjp.getSignature().getName();
		// 获取输入参数
		Object[] args = pjp.getArgs();
		inputParamStr = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			inputParamStr.append("[args" + i + "]:" + (args[i] != null ? args[i].toString() : ""));
		}

		// 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
		outputParamMap = new HashMap<String, Object>();
		Object result = pjp.proceed();// result的值就是被拦截方法的返回值
		outputParamMap.put("result", result);

		return result;
	}

	private void printOptLog() {

		String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
		logger.info("\n【function name】：" + functionName + "\n【start_time】：" + optTime + "\n【process_time】："
				+ (endTimeMillis - startTimeMillis) + "ms ;" + "\n【param】：" + inputParamStr.toString() + ";"
				+ "\n【result】：" + outputParamMap.toString());
	}
}