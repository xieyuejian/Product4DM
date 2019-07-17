package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.DeliveryDtl;

/**
 * 送货管理明细表 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface DeliveryDtlDao extends JpaDao<DeliveryDtl, Long> {

}
