package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购订单细单双单位转换关系类 - 扩展类
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_purchaseorderpricing")
public class PurchaseOrderPricing extends StdPurchaseOrderPricing {
	private static final long serialVersionUID = -2253117083909484887L;
}
