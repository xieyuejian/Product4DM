package com.huiju.srm.ws.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.ws.entity.WsRequestLog;



/**
 * SRM webService log
 * 
 * @author ZJQ
 */
@Repository
public interface WsRequestLogDao extends JpaDao<WsRequestLog,Long> {

}
