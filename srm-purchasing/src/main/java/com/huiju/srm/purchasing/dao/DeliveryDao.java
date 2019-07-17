package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.Delivery;

/**
 * 送货管理数据表EaoBean
 * 
 * @author zhuang.jq
 */
@Repository
public interface DeliveryDao extends JpaDao<Delivery, Long> {

}
