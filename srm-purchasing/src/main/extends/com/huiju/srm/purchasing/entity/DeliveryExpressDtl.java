package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货管理快递明细，对接快递100
 * 
 * @author hongwl
 */
@Entity
@Table(name = "d_cp_deliveryexpressdtl")
public class DeliveryExpressDtl extends StdDeliveryExpressDtl {
	private static final long serialVersionUID = 1L;
}
