package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.DeliveryView;

/**
 * <pre>
 * 送货管理数据表EaoBean
 * </pre>
 * 
 * <br>
 * JDK 版本：JDK 1.6
 * 
 * @author wz
 */
@Repository
public interface DeliveryViewDao extends JpaDao<DeliveryView, Long> {

}
