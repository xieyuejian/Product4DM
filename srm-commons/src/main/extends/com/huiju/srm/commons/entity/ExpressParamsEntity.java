
package com.huiju.srm.commons.entity;

import java.io.Serializable;

/**
 * 快递100接口参数实体
 * 
 * @author hongwl
 */
public class ExpressParamsEntity implements Serializable {
	private static final long serialVersionUID = -1200944662316774034L;
	/**
	 * 快递100分配给贵司的的公司编号
	 */
	private String customer;
	/**
	 * 签名， 用于验证身份， 按param + key + customer 的顺序进行MD5加密（注意加密后字符串要转大写）， 不需要“+”号
	 */
	private String sign;
	/**
	 * 其他参数组合成的json对象
	 */
	private ExpressParamsDtlEntity param;

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public ExpressParamsDtlEntity getParam() {
		return param;
	}

	public void setParam(ExpressParamsDtlEntity param) {
		this.param = param;
	}
}
