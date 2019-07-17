package com.huiju.srm.purchasing.entity;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <pre>采购申请 基准类</pre>
 * @author wz 
 * @version 1.0 时间 2016/8/3 
 */

@Entity
@Table(name="d_cp_purchasingrequisition")
public class PurchasingRequisition extends StdPurchasingRequisition{

	private static final long serialVersionUID = 3075133691557657227L;
	
}
