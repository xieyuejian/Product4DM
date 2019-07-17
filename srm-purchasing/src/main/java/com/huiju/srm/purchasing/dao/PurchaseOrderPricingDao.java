package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;

/**
 * 采购订单细单定价条件关系体 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface PurchaseOrderPricingDao extends JpaDao<PurchaseOrderPricing, Long> {

}
