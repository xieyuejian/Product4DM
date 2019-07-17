package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <pre>采购申请转单基准类</pre>
 * @author bairx 
 * @version 1.0 时间 2019/3/30 
 */

@Entity
@Table(name="d_cp_purchaserequisitiontrans")
public class PurchasingRequisitionTrans extends StdPurchasingRequisitionTrans{

	private static final long serialVersionUID = 3075133691557657227L;
	
}
