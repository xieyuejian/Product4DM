package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购申请转单视图
 * 
 * @author bairx
 * @date 2019/3/30 
 */
@Entity
@Table(name = "v_purchaserequisitiontrans")
public class PurchasingRequisitionTransV extends StdPurchasingRequisitionTransV {

	private static final long serialVersionUID = 3075133691557657227L;

}
