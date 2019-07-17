package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.OrderMessage;

/**
 * 采购订单审核日志DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface OrderMessageDao extends JpaDao<OrderMessage, Long> {

}
