package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.CensorQuality;

/**
 * 质检管理表
 * 
 * @author bairx date 2019-03-30
 */
@Repository
public interface CensorQualityDao extends JpaDao<CensorQuality, Long> {


}
