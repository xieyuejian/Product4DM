package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货管理数据表 基准类
 * 
 * @author zhuang.jq
 */

@Entity
@Table(name = "d_cp_delivery")
public class Delivery extends StdDelivery {
	private static final long serialVersionUID = 1L;
}
