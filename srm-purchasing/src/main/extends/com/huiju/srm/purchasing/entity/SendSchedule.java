package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货排程主单 拓展
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_sendschedule")
public class SendSchedule extends StdSendSchedule {
	private static final long serialVersionUID = -4413226695777701988L;

}
