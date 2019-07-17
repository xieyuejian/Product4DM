package com.huiju.srm.srmbpm.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.srmbpm.entity.Authorize;

@Repository
public interface AuthorizeDao extends JpaDao<Authorize, Long> {
}
