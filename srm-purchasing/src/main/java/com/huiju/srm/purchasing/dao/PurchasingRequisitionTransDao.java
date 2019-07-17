package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTrans;

/**
 * 采购申请明细归集Dao
 * 
 * @author bairx date 2019-03-30 
 */
@Repository
public interface PurchasingRequisitionTransDao extends JpaDao<PurchasingRequisitionTrans, Long> {


}
