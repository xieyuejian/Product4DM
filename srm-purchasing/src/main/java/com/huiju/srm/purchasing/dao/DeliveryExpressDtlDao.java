package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.DeliveryExpressDtl;

/**
 * 送货管理快递信息 DAO
 * 
 * @author hongwl
 */
@Repository
public interface DeliveryExpressDtlDao extends JpaDao<DeliveryExpressDtl, Long> {

}
