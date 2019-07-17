package com.huiju.srm.purchasing.dto;

import java.util.List;

/**
 * @author zhuang.jq
 */
public class StdDeliveryDto {
	// 送货号
	protected String deliveryCode;
	// 单据状态
	protected Integer status;
	// 库存地点编码
	protected String storageLocationCode;
	// 客户端编码
	protected String clientCode;
	// 送货明细
	protected List<DeliveryDtlDto> deliveryDtls;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStorageLocationCode() {
		return storageLocationCode;
	}

	public void setStorageLocationCode(String storageLocationCode) {
		this.storageLocationCode = storageLocationCode;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public List<DeliveryDtlDto> getDeliveryDtls() {
		return deliveryDtls;
	}

	public void setDeliveryDtls(List<DeliveryDtlDto> deliveryDtls) {
		this.deliveryDtls = deliveryDtls;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

}
