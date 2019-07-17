package com.huiju.srm.ws.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.ws.entity.WsLog;


/**
 * SRM webService log
 * 
 * @author ZJQ
 */
@Repository
public interface WsLogDao extends JpaDao<WsLog,Long> {

  
}
