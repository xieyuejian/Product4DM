package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.SendScheduleSelect;

@Repository
public interface SendScheduleSelectDao extends JpaDao<SendScheduleSelect, Long> {

}
