package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.SendScheduleCommon;

/**
 * 送货排程中间 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface SendScheduleCommonDao extends JpaDao<SendScheduleCommon, Long> {

}
