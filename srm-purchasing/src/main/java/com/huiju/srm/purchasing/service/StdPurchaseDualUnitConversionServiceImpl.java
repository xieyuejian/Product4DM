package com.huiju.srm.purchasing.service;

import org.springframework.stereotype.Service;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;

/**
 * 采购订单细单双单位转换关系类 业务
 * 
 * @author zhuang.jq
 */
@Service
public class StdPurchaseDualUnitConversionServiceImpl extends JpaServiceImpl<PurchaseDualUnitConversion, Long>
		implements StdPurchaseDualUnitConversionService {

}