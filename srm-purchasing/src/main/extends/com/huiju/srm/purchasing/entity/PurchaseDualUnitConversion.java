package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购订单细单双单位转换关系类
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_dualunitconversion")
public class PurchaseDualUnitConversion extends StdPurchaseDualUnitConversion {
	private static final long serialVersionUID = 8001367088837275769L;

}
