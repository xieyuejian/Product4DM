/**
 * 
 */
package com.huiju.srm.commons.entity;

import java.io.Serializable;

/**
 * @author ideapad
 *
 */
public class ResultInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1200944662316774034L;
	/**
	 * 信息编码
	 */
	private String code;
	/**
	 * 错误信息
	 */
	private String message;
	/**
	 * 信息内容
	 */
	private Object data;
	/**
	 * 是否成功
	 */
	private Boolean success;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public ResultInfo setCode(String code) {
		this.code = code;
		return this;
	}
	 
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public ResultInfo setMessage(String message) {
		this.message = message;
		return this;
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data; 
	}
	/**
	 * @param data the data to set
	 */
	public ResultInfo setData(Object data) {
		this.data = data;
		return this;
	}
	/**
	 * @return the success
	 */
	public Boolean getSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public ResultInfo setSuccess(Boolean success) {
		this.success = success;
		return this;
	}
	
     
}
