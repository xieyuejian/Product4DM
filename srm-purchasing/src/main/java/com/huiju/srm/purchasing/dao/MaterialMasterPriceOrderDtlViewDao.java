package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;

/**
 * 视图eao
 * 
 * @author zhuang.jq
 */
@Repository
public interface MaterialMasterPriceOrderDtlViewDao extends JpaDao<MaterialMasterPriceOrderDtlView, Long> {

}
