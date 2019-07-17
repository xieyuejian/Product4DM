package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.LogisticsDtlDtl;

/**
 * 送货管理物流详情 DAO
 * 
 * @author hongwl
 */
@Repository
public interface LogisticsDtlDtlDao extends JpaDao<LogisticsDtlDtl, Long> {

}
