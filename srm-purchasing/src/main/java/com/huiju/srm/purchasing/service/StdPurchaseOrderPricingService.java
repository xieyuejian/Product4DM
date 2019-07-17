package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;

/**
 * 采购订单定价条件远程接口
 * 
 * @author CWQ
 */
public interface StdPurchaseOrderPricingService extends JpaService<PurchaseOrderPricing, Long> {

    /**
     * 获取订单条件
     * 
     * @param searchParams
     *            查询参数
     * @param roleType
     *            角色类型
     * @return 返回
     */
    List<PurchaseOrderPricing> getPurchaseOrderPricing(Map<String, Object> searchParams, String roleType);

    /**
     * APP排序查找数据库中所有的数据
     * 
     * @param searchParams
     *            查询参数
     * @param sortEntity
     *            排序
     * @return
     */
    List<PurchaseOrderPricing> findPurchaseOrderPricingAll(Map<String, Object> searchParams, String sort);

    /**
     * APP查询订单价格明细
     * 
     * @param searchParams
     *            查询条件 {EQ_purchaseOrderDetail_purchaseOrderDetailId:订单明细id值}
     * @param roleType
     *            角色类型
     * @return PurchaseOrderPricing 对象字符串
     * @throws Exception
     */
    String getPurchaseOrderPricingString(Map<String, Object> searchParams, String roleType);

}
