package com.nbl.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import com.nbl.common.constants.ComConst;
import com.nbl.util.AmtParseUtil;
import com.nbl.util.DateTimeUtils;

/**
 * Bean转换处理类
 * 
 * @author AlanMa
 *
 */
public abstract class BeanParseUtils extends org.springframework.beans.BeanUtils {

	private final static Logger logger = LoggerFactory.getLogger(BeanParseUtils.class);

	/**
	 * 解决了BeanUtils值为空未判断会出现转换异常的问题
	 * 
	 * @param source
	 * @param target
	 * @throws BeansException
	 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null&& !"".equals(value)) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
					} catch (Throwable ex) {
						logger.error("copyProperties exception", ex);
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	/**
	 * 转换成均为String的目标类
	 * 
	 * @param source
	 * @param target
	 * @param isToString
	 * @throws BeansException
	 */
	public static void copyPropertiesToString(Object source, Object target, String isToString) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}

							if (ComConst.TRUE.equals(isToString) && !(value instanceof String)) {
								String valueStr = null;
								if (value instanceof Long || value instanceof Short) {
									valueStr = String.valueOf(value);
								} else if (value instanceof BigDecimal) {
									valueStr = value.toString();
								} else if (value instanceof Date) {
									valueStr = new DateTimeUtils((Date) value).toDateTimeString();
								}
								writeMethod.invoke(target, valueStr);
							} else {
								writeMethod.invoke(target, value);
							}
						}
					} catch (Throwable ex) {
						logger.error("copyProperties exception", ex);
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	/**
	 * 将指定金额字段（元）转换为（分）
	 * 
	 * @param source
	 * @param target
	 * @param isToString
	 * @throws BeansException
	 */
	public static void copyPropertiesAmtToFen(Object source, Object target, String... amtProperties) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> amtList = (amtProperties != null ? Arrays.asList(amtProperties) : null);

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							if (value instanceof String) {
								Long valueLong = null;
								if (amtList != null && amtList.contains(targetPd.getName()) && value != null) {
									valueLong = AmtParseUtil.strToLongAmt((String) value);
									writeMethod.invoke(target, valueLong);
								} else {
									writeMethod.invoke(target, value);
								}
							} else {
								writeMethod.invoke(target, value);
							}
						}
					} catch (Throwable ex) {
						logger.error("copyProperties exception", ex);
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}
	
	/**
	 * 将指定金额字段（分）转换为（元）
	 * 
	 * @param source
	 * @param target
	 * @param isToString
	 * @throws BeansException
	 */
	public static void copyPropertiesAmtToYuan(Object source, Object target, String... amtProperties) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> amtList = (amtProperties != null ? Arrays.asList(amtProperties) : null);

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							if (!(value instanceof String)) {
								String valueStr = null;
								if (value instanceof Long || value instanceof Short) {
									if (amtList != null && amtList.contains(targetPd.getName())) {
										valueStr = AmtParseUtil.longToStrAmt((Long) value);
									} else {
										valueStr = String.valueOf(value);
									}
								} else if (value instanceof BigDecimal) {
									valueStr = value.toString();
								} else if (value instanceof Date) {
									valueStr = new DateTimeUtils((Date) value).toDateTimeString();
								}
								writeMethod.invoke(target, valueStr);
							} else {
								writeMethod.invoke(target, value);
							}
						}
					} catch (Throwable ex) {
						logger.error("copyProperties exception", ex);
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}


}
