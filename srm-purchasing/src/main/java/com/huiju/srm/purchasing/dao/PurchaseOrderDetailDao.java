package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;

/**
 * 采购订单细单双单位转换关系 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface PurchaseOrderDetailDao extends JpaDao<PurchaseOrderDetail, Long> {

}
