package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货管理物流详情细细单，对接快递100
 * 
 * @author hongwl
 */
@Entity
@Table(name = "d_cp_logisticsdtldtl")
public class LogisticsDtlDtl extends StdLogisticsDtlDtl {
	private static final long serialVersionUID = 1L;
}
