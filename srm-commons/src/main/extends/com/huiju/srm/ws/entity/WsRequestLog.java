package com.huiju.srm.ws.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * SRM webService log
 * 
 * @author ZJQ 2018-3-22 10:03:51
 */
@Entity
@Table(name = "s_log_wsrequest")
public class WsRequestLog implements Serializable {
	private static final long serialVersionUID = 5774817310205199517L;

	/** ID **/
	@Id
	@Column(name = "logid")
	protected Long logId;

	/** 源系统 **/
	@Column(name = "sourcesystem")
	protected String sourceSystem;

	/** 目标系统 **/
	@Column(name = "targetsystem")
	protected String targetSystem;
	
	/** 接口编号 **/
	@Column(name = "interfacecode")
	protected String interfaceCode;

	/** 接口名称 **/
	@Column(name = "interfacename")
	protected String interfaceName;

	/** 发送时间 **/
	@Column(name = "requesttime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar requestTime;

	/** 响应时间 **/
	@Column(name = "responsetime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar responseTime;

	/** 成功失败标识 1成功，0失败 ，2异常 **/
	@Column(name = "successflag")
	protected Integer successflag;

	/** 本次请求ID **/
	@Column(name = "requestid")
	protected Long requestId;

	/** 本次请求业务编码 **/
	@Column(name = "businesscodes")
	protected String businessCodes;

	/** 本次请求内容 **/
	@Transient
	protected String requestContent;

	/** 本次响应内容 **/
	@Transient
	protected String responseContent;

	/** 明细 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "wsRequestLog", fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<WsRequestLogDtl> wsRequestLogDtls;

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getInterfaceCode() {
		return interfaceCode;
	}

	public void setInterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Calendar getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Calendar requestTime) {
		this.requestTime = requestTime;
	}

	public Calendar getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Calendar responseTime) {
		this.responseTime = responseTime;
	}

	public Integer getSuccessflag() {
		return successflag;
	}

	public void setSuccessflag(Integer successflag) {
		this.successflag = successflag;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public List<WsRequestLogDtl> getWsRequestLogDtls() {
		return wsRequestLogDtls;
	}

	public void setWsRequestLogDtls(List<WsRequestLogDtl> wsRequestLogDtls) {
		this.wsRequestLogDtls = wsRequestLogDtls;
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

	public String getBusinessCodes() {
		return businessCodes;
	}

	public void setBusinessCodes(String businessCodes) {
		this.businessCodes = businessCodes;
	} 
	 
}
