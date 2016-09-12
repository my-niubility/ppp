package com.nbl.common.exception;

/**
 * @author xuchu-tang
 * @version 1.0, 2015年12月12日
 * @description 平台运行时异常
 */
public class MyBusinessRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -8861779957993193065L;
	
	//错误代码
	private String errorCode;
	public MyBusinessRuntimeException(){
		super();
	}
	
	public MyBusinessRuntimeException(String message){
		super(message);
	}
	
	public MyBusinessRuntimeException(String code , String message){
		super(message);
		this.errorCode = code;
	}
	
	public MyBusinessRuntimeException(String message , Throwable cause){
		super(message,cause);
	}
	
	public MyBusinessRuntimeException(Throwable cause){
		super(cause);
	}
	
	public String getCode(){
		return errorCode;
	}
}
