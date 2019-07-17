package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.MaiLSubmitAuditLog;

/**
 * 提交邮件审核日志DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface MaiLSubmitAuditLogDao extends JpaDao<MaiLSubmitAuditLog, String> {
}
