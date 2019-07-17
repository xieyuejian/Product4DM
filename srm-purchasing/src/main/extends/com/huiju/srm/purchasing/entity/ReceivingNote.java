package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 收货单
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_receivingnote")
public class ReceivingNote extends StdReceivingNote {
	private static final long serialVersionUID = 1416433452425883325L;
}
