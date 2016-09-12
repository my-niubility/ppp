
package com.nbl.common.constants;

/**
 * 用于定义错误码和错误信息 <br>
 * 错误码规则：应用类型分类{2位}+错误类型分类{1位}+错误序号{3位}<br>
 * 应用类型分类：<br>
 * PO——zlebank-energy-portal<br>
 * 错误类型分类：<br>
 * C-信息校验类<br>
 * B-业务信息规则类<br>
 * D-数据库操作类<br>
 * T-系统间通讯类<br>
 * P-参数转换类<br>
 * E-其他系统异常类
 * 
 * @version 1.0.0
 * @author AlanMa
 *
 */
public class ErrorCode {
	/**
	 * [%s]
	 */
	public static final String POB001 = "POB001|[%s]";
	/**
	 * 短信验证码[%s]输入错误
	 */
	public static final String POC001 = "POC001|短信验证码[%s]输入错误";
	/**
	 * 获取验证码失败
	 */
	public static final String POC002 = "POC002|获取验证码失败";
	/**
	 * 验证码[%s]输入错误
	 */
	public static final String POC003 = "POC003|验证码[%s]输入错误";
	/**
	 * 验证码[%s]输入错误
	 */
	public static final String POC004 = "POC003|验证码[%s]输入错误";
	/**
	 * 验证码已过期时效，请重新获取
	 */
	public static final String POC005 = "POC005|验证码已过期失效，请重新获取";
	/**
	 * [%s]不能为空
	 */
	public static final String POC006 = "POC006|[%s]不能为空";
	/**
	 * 产品ID不符合规范
	 */
	public static final String POC007 = "POC007|产品ID不符合规范";
	/**
	 * [%s]参数不符合规范
	 */
	public static final String POC008 = "POC008|[%s]参数不符合规范";
	/**
	 * 认证手机号与注册手机号不符
	 */
	public static final String POC009 = "POC009|认证手机号与注册手机号不符";
	/**
	 * 手机号不符
	 */
	public static final String POC010 = "POC010|非接收验证码使用的手机号";
	/**
	 * TOKEN校验失败，请重新获取TOKEN
	 */
	public static final String POC011 = "POC011|TOKEN校验失败，请重新获取TOKEN";
	/**
	 * 【总金额】不等于【实际交易金额】+【红包抵扣金额】
	 */
	public static final String POC012 = "POC012|【总金额】不等于【实际交易金额】+【红包抵扣金额】";
	/**
	 * 无此业务场景[%s]
	 */
	public static final String POC013 = "POC013|无此业务场景[%s]";
	/**
	 * 业务场景[%s],[%s]不能为空
	 */
	public static final String POC014 = "POC014| 业务场景[%s],[%s]不能为空";
	/**
	 * TOKEN值与业务场景[%s]不符
	 */
	public static final String POC015 = "POC015|TOKEN值与业务场景[%s]不符";
	/**
	 * 操作过于频繁，请稍后尝试获取验证码
	 */
	public static final String POC016 = "POC016|操作过于频繁，请稍后尝试获取验证码";
	/**
     * 此订单【[%s]】状态为【[%s]】不能支付
     */
    public static final String POC017 = "POC017|此订单【[%s]】状态为【[%s]】不能支付";
    /**
     * 用户[%s]，已在[%s]登录，请先退出再登录
     */
    public static final String POC018 = "POC018|用户[%s]，已在[%s]登录，请先退出再登录";
    /**
     * 用户未设置支付密码
     */
    public static final String POC019 = "POC019|用户未设置支付密码";
	/**
	 * 绑卡手机号与注册手机号不符
	 */
	public static final String POC020 = "POC020|绑卡手机号与注册手机号不符";
	/**
	 * 用户[%s]尚未登录，无法注销
	 */
	public static final String POC021 = "POC021|此用户[%s]尚未登录，无法注销";
	/**
	 * 同一帐号已在其它地方登录
	 */
	public static final String POC022 = "POC022|同一帐号已在其它地方登录";
	/**
	 * 余额不足
	 */
	public static final String POC023 = "POC023|余额不足";
	/**
	 * 验证码[%s]输入错误
	 */
	public static final String POP001 = "POP001|时间转换异常";
}
