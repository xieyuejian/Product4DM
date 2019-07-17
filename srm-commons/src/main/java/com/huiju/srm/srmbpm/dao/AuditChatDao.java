package com.huiju.srm.srmbpm.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.srmbpm.entity.AuditChat;

@Repository
public interface AuditChatDao extends JpaDao<AuditChat, Long> {
}
