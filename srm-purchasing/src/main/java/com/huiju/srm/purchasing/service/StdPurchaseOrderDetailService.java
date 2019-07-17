package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;

/**
 * 采购订单明细 远程接口
 * 
 * @author CWQ
 */
public interface StdPurchaseOrderDetailService extends JpaService<PurchaseOrderDetail, Long> {
	/**
	 * 查询所有采购订单细单
	 * 
	 * @param searchParams
	 * @param roleType
	 * @return
	 */
	public List<PurchaseOrderDetail> findAllDtl(Map<String, Object> searchParams, int roleType);

	/**
	 * 获取订单明细最大剩余可收货量 可送数量 = (订单量 + 退货量 - 收货量) + (过货限额 * 订单量)
	 * 
	 * @param pod订单明细对象
	 * @return 最大剩余收货量
	 */
	BigDecimal getMaxReceiveQty(PurchaseOrderDetail pod);

	/**
	 * 获取订单明细的正常可收货数量 可送数量 = 订单量 + 退货量 - 收货量
	 * 
	 * @param pod订单明细对象
	 * @return 正常的剩余收货量
	 */
	BigDecimal getNormalReceiveQty(PurchaseOrderDetail pod);

	/**
	 * 获取采购订单明细的最大可退数量
	 * 
	 * @param pod订单明细对象
	 * @return 最大可退数量
	 */
	BigDecimal getMaxRefundQty(PurchaseOrderDetail pod);

	/**
	 * 排序得到采购订单明细List
	 */
	public List<PurchaseOrderDetail> findAllAndSort(Map<String, Object> searchParams, Sort sort);

	/**
	 * 重新计算订单明细的可送货数量
	 * 
	 * @param pod 订单明细
	 */
	public PurchaseOrderDetail recountCanSendQty(PurchaseOrderDetail pod);

	/**
	 * 采购订单明细是否关闭
	 * 
	 * @param pod 采购订单明细
	 * @return true 关闭，false打开
	 */
	public boolean canClosePod(PurchaseOrderDetail pod);
}
