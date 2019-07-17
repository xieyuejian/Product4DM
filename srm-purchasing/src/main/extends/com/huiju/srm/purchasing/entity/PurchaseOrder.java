package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购订单实体类 - 扩展类
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_purchaseorder")
public class PurchaseOrder extends StdPurchaseOrder {
	private static final long serialVersionUID = 334059748711537547L;;
}
