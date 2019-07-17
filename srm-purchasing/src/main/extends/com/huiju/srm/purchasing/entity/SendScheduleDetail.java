package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货排程细单 拓展
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_sendscheduledetail")
public class SendScheduleDetail extends StdSendScheduleDetail {
	private static final long serialVersionUID = 5788784452821856105L;

}
