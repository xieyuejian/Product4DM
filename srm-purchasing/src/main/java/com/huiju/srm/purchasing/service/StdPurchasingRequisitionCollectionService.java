package com.huiju.srm.purchasing.service;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;

/**
 * 采购申请明细归集 Servic接口
 * 
 * @author bairx
 */
public interface StdPurchasingRequisitionCollectionService extends JpaService<PurchasingRequisitionCollection, Long> {
	/**
	 * 从SAP导入数据
	 * 
	 * @param params 参数
	 * @return 采购申请明细集合
	 */
	public int importFromSap(String[] params);

	/**
	 * 根据采购申请单号行号移除对应的采购申请明细归集
	 * 
	 * @param purchasingRequisitionNo 采购申请单号
	 * @param rowNo 行号 传入null则删除全部
	 */
	public void removeByNo(String purchasingRequisitionNo, Long rowNo);
}
