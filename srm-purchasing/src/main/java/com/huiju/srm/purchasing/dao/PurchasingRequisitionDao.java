package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisition;

/**
 * 采购明细归集EaoBean
 * 
 * @author bairx date 2019-03-30
 */
@Repository
public interface PurchasingRequisitionDao extends JpaDao<PurchasingRequisition, Long> {


}
