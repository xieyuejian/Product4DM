package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.dao.PurchaseOrderDetailDao;
import com.huiju.srm.purchasing.dao.PurchaseOrderPricingDao;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;
import com.huiju.srm.purchasing.util.PurchaseOrderConstant;

/**
 * 采购订单细单双单位转换关系类 业务
 * 
 * @author zhuang.jq
 */
public class StdPurchaseOrderPricingServiceImpl extends JpaServiceImpl<PurchaseOrderPricing, Long>
        implements StdPurchaseOrderPricingService {

    @Autowired
    protected PurchaseOrderPricingDao purchaseOrderPricingEao;
    @Autowired
    protected PurchaseOrderDetailDao purchaseOrderDetailEao;
    @Autowired
    protected PurchaseOrderService purchaseOrderLogic;

    /**
     * 排序查找数据库中所有的数据
     * 
     * @param searchParams
     * @param sortEntity
     * @return
     * @throws Exception
     */
    public List<PurchaseOrderPricing> getPurchaseOrderPricing(Map<String, Object> searchParams, String roleType) {
        List<PurchaseOrderPricing> list = purchaseOrderPricingEao.findAll(searchParams);
        // 编辑时是可以编辑价格
        if (-1 == roleType.indexOf(SrmConstants.ROLETYPE_V)) {
            if (searchParams.containsKey("EQ_purchaseOrderDetail_purchaseOrderDetailId")) {
                Long id = Long.parseLong(searchParams.get("EQ_purchaseOrderDetail_purchaseOrderDetailId").toString());
                PurchaseOrderDetail detail = purchaseOrderDetailEao.getById(id);
                String isEditPrice = purchaseOrderLogic.getPurchaseOrderControl(detail.getPurchaseOrder(),
                        PurchaseOrderConstant.GROOVY_EDITPRICE);
                for (PurchaseOrderPricing purchaseOrderPricing : list) {
                    purchaseOrderPricing.setIsEditPrice(isEditPrice);
                    purchaseOrderPricing.setIsEditPrice(isEditPrice);
                }
            }
        }

        return list;
    }

    /**
     * 排序查找数据库中所有的数据
     * 
     * @param searchParams
     * @param sortEntity
     * @return
     */
    @Override
    public List<PurchaseOrderPricing> findPurchaseOrderPricingAll(Map<String, Object> searchParams, String sort) {
        return purchaseOrderPricingEao.findAll(searchParams, sort);
    }

    @Override
    public String getPurchaseOrderPricingString(Map<String, Object> searchParams, String roleType) {
        try {
            List<PurchaseOrderPricing> list = this.getPurchaseOrderPricing(searchParams, roleType);
            if (list != null && list.size() > 0) {
                return DataUtils.toJson(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }
}