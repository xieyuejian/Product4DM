package com.huiju.srm.purchasing.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 送货排程采购订单中间表 拓展
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_sendschedulecommon")
public class SendScheduleCommon extends StdSendScheduleCommon {
	private static final long serialVersionUID = 334059748711537547L;;

}
