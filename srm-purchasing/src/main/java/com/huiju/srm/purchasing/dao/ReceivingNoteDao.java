package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.ReceivingNote;

/**
 * 收获单 DAO
 * 
 * @author zhuang.jq
 */
@Repository
public interface ReceivingNoteDao extends JpaDao<ReceivingNote, Long> {
}
