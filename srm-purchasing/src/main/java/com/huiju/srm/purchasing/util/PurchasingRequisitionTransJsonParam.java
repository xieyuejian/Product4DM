package com.huiju.srm.purchasing.util;

import com.huiju.module.data.common.JsonParam;

public class PurchasingRequisitionTransJsonParam<T> extends JsonParam<T> {

	private static final long serialVersionUID = 4546490060144171089L;

	protected String paramsJson;

	public String getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}
}
