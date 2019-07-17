package com.huiju.srm.srmbpm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * 授权单实体类
 * 
 * @author hongwl
 *
 * @date 2019年4月16日
 */
@Entity
@Table(name = "s_authorize")
public class Authorize extends StdAuthorize {
	private static final long serialVersionUID = 334059748711537547L;
}
