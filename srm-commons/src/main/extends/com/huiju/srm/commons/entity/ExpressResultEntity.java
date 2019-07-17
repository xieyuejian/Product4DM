
package com.huiju.srm.commons.entity;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 快递100接口数据返回实体
 * 
 * @author hongwl
 */
public class ExpressResultEntity implements Serializable {

	private static final long serialVersionUID = -1200944662316774034L;
	/**
	 * 接口调用状态编码（0：成功 1：失败）
	 */
	private String status;
	/**
	 * 物流状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态
	 */
	private ExpressState state;
	/**
	 * 接口调用返回消息
	 */
	private String message;
	/**
	 * 快递公司编码
	 */
	private String expressCompanyCode;
	/**
	 * 快递公司名称
	 */
	private String expressCompanyName;
	/**
	 * 物流更新时间
	 */
	private Calendar updateTime;
	/**
	 * 上次接口调用时间
	 */
	private Calendar invokeTime;
	/**
	 * 物流更新内容
	 */
	private String context;

	public Calendar getInvokeTime() {
		return invokeTime;
	}

	public void setInvokeTime(Calendar invokeTime) {
		this.invokeTime = invokeTime;
	}

	public Calendar getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Calendar updateTime) {
		this.updateTime = updateTime;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ExpressState getState() {
		return state;
	}

	public void setState(ExpressState state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExpressCompanyCode() {
		return expressCompanyCode;
	}

	public void setExpressCompanyCode(String expressCompanyCode) {
		this.expressCompanyCode = expressCompanyCode;
	}

	public String getExpressCompanyName() {
		return expressCompanyName;
	}

	public void setExpressCompanyName(String expressCompanyName) {
		this.expressCompanyName = expressCompanyName;
	}

}
