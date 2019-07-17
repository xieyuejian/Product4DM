package com.huiju.srm.purchasing.service;

import java.util.Map;

import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;

/**
 * 物料主数据视图 远程接口
 * 
 * @author zhuang.jq
 */
public interface StdMaterialMasterPriceOrderDtlViewService extends JpaService<MaterialMasterPriceOrderDtlView, Long> {
    public Page<MaterialMasterPriceOrderDtlView> findPage(Page<MaterialMasterPriceDtl> mmdPage,
            Page<MaterialMasterPriceOrderDtlView> page, Map<String, Object> searchParams);
}
