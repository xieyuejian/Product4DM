package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;

/**
 * 送货排程明细 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface SendScheduleDetailDao extends JpaDao<SendScheduleDetail, Long> {

}
