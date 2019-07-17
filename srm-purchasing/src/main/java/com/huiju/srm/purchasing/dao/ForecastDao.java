package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.Forecast;

/**
 * 采购预测
 * 
 * @author bairx date 2019-03-30
 */
@Repository
public interface ForecastDao extends JpaDao<Forecast, Long> {


}
