package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;

/**
 * * 采购订单明细 EaoBean
 * 
 * @author zhuang.jq
 */
@Repository
public interface PurchaseDualUnitConversionDao extends JpaDao<PurchaseDualUnitConversion, Long> {

}
