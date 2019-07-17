package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.ForecastDtl;

/**
 * 采购预测细单
 * 
 * @author bairx date 2019-03-30
 */
@Repository
public interface ForecastDtlDao extends JpaDao<ForecastDtl, Long> {


}
