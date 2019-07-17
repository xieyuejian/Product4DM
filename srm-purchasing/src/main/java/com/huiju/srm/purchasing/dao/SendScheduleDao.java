package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.SendSchedule;

/**
 * 送货排程 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface SendScheduleDao extends JpaDao<SendSchedule, Long> {

}
