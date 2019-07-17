package com.huiju.srm.purchasing.service;

import org.springframework.stereotype.Service;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.srm.purchasing.entity.DeliveryView;

/**
 * 送货点收数据表EaoBean 扩展类
 * 
 * @author zhuang.jq
 */
@Service
public class DeliveryViewServiceImpl extends JpaServiceImpl<DeliveryView, Long> implements DeliveryViewService {

}
