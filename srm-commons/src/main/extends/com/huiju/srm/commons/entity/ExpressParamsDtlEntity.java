
package com.huiju.srm.commons.entity;

import java.io.Serializable;

/**
 * 快递100接口参数明细实体
 * 
 * @author hongwl
 */
public class ExpressParamsDtlEntity implements Serializable {
	private static final long serialVersionUID = -1200944662316774034L;
	/**
	 * 查询的快递公司的编码，一律用小写字母（必填）
	 */
	private String com;
	/**
	 * 查询的快递单号， 单号的最大长度是32个字符（必填）
	 */
	private String num;
	/**
	 * 收件人或寄件人的手机号或固话（顺丰单号必填，也可以填写后四位，如果是固话，请不要上传分机号）
	 */
	private String phone;
	/**
	 * 出发地城市，省-市-区，如 广东省深圳市南山区
	 */
	private String from;
	/**
	 * 目的地城市，省-市-区，如 北京市朝阳区
	 */
	private String to;
	/**
	 * 添加此字段表示开通行政区域解析功能。0：关闭（默认），1：开通行政区域解析功能，2：开通行政解析功能并且返回出发、目的及当前城市信息
	 */
	private Integer result;
	/**
	 * 单位时间
	 */
	private Integer spanTime;
	/**
	 * 允许调用接口次数最大值
	 */
	private Integer maxInterNum;

	public Integer getSpanTime() {
		return spanTime;
	}

	public void setSpanTime(Integer spanTime) {
		this.spanTime = spanTime;
	}

	public Integer getMaxInterNum() {
		return maxInterNum;
	}

	public void setMaxInterNum(Integer maxInterNum) {
		this.maxInterNum = maxInterNum;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Integer getResult() {
		return result;
	}

	public void setResultv2(Integer result) {
		this.result = result;
	}

}
