package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货管理明细表 基准类
 * 
 * @author zhuang.jq
 */

@Entity
@Table(name = "d_cp_deliverydtl")
public class DeliveryDtl extends StdDeliveryDtl {
	private static final long serialVersionUID = 1L;
}
