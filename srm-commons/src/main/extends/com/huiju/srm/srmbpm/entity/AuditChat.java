package com.huiju.srm.srmbpm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * 审核沟通扩展类
 *
 * @author hongwl
 *
 * @date 2019年4月12日
 */
@Entity
@Table(name = "s_auditchat")
public class AuditChat extends StdAuditChat {

	private static final long serialVersionUID = 1L;

}
