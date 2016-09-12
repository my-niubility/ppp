package com.nbl.utils.json;

/**
 * @author xuchu-tang
 * @createdate 2016年3月23日
 * @version 1.0
 * @description :1、这是json格式返回的统一出口。 2、它包括基本元数据（成功标识、描述）、结果数据（具体格式由交互双方商定）
 */
public class ResponseJson {

	private static final String OK = "ok";

	private static final String ERROR = "error";

	private Meta meta;

	private Object data;

	public Meta getMeta() {
		return meta;
	}

	public Object getData() {
		return data;
	}

	/**
	 * @return
	 * @description:只返回状态，没有结果集
	 */
	public ResponseJson success() {
		this.meta = new Meta(true, OK);
		return this;
	}

	/**
	 * @param data
	 * @return
	 * @description:返回结果集
	 */
	public ResponseJson success(Object data) {

		this.meta = new Meta(true, OK);
		this.data = data;
		return this;
	}

	/**
	 * @return
	 * @description:返回错误结果
	 */
	public ResponseJson failure() {
		this.meta = new Meta(false, ERROR);
		return this;
	}

	/**
	 * @param message
	 * @return
	 * @description:返回带有错误描述的结果
	 */
	public ResponseJson failure(String message) {
		this.meta = new Meta(false, message);
		return this;
	}

	// 内部类封装返回数据
	public class Meta {

		private boolean success;

		private String message;

		public Meta(boolean success) {
			this.success = success;
		}

		public Meta(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public String toString() {
			return "Meta [success=" + success + ", message=" + message + "]";
		}

	}

	@Override
	public String toString() {
		return "ResponseJson [meta=" + meta + ", data=" + (data != null ? data.toString() : data) + "]";
	}

}
