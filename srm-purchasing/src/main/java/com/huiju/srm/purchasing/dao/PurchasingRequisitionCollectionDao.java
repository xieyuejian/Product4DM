package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;

/**
 * 采购申请明细归集Dao
 * 
 * @author bairx date 2019-03-30 
 */
@Repository
public interface PurchasingRequisitionCollectionDao extends JpaDao<PurchasingRequisitionCollection, Long> {


}
