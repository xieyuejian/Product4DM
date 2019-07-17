package com.huiju.srm.ws.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * SRM webService log
 * 
 * @author ZJQ
 */
@Entity
@Table(name = "s_log_interfacedtl")
public class WsLogDtl implements Serializable {
	private static final long serialVersionUID = 5774817310205199517L;

	/** ID **/
	@Id
	@Column(name = "logdtlid")
	protected Long logDtlId;

	/** 日志主单 */
	@ManyToOne
	@JoinColumn(name = "logId", referencedColumnName = "logId", nullable = false)
	protected WsLog wsLog;

	/** 请求的值数据 **/
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "requestcontent", columnDefinition = "CLOB")
	protected String requestContent;

	/** 异常信息 **/
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "responsecontent", columnDefinition = "CLOB")
	protected String responseContent;

	public Long getLogDtlId() {
		return logDtlId;
	}

	public void setLogDtlId(Long logDtlId) {
		this.logDtlId = logDtlId;
	}

	public WsLog getWsLog() {
		return wsLog;
	}

	public void setWsLog(WsLog wsLog) {
		this.wsLog = wsLog;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

}
