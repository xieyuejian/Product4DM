package com.huiju.srm.srmbpm.entity;

import java.util.Calendar;

import javax.persistence.*;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 审核沟通基础类
 * 
 * @author hongwl
 *
 * @date 2019年4月12日
 */
@MappedSuperclass
public class StdAuditChat extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AuditChat_PK")
	@TableGenerator(name = "AuditChat_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "AuditChat_PK", allocationSize = 1)
	@Column(name = "chatId")
	protected Long chatId;
	/** 父沟通ID */
	@Column(name = "parentId")
	protected Long parentId;
	/** 发送人ID */
	@Column(name = "senderId")
	protected Long senderId;
	/** 发送人姓名 */
	@Column(name = "senderName")
	protected String senderName;
	/** 接收人ID */
	@Column(name = "receipentId")
	protected Long receipentId;
	/** 接收人姓名 */
	@Column(name = "receipentName")
	protected String receipentName;
	/** 内容 */
	@Column(name = "message")
	protected String message;
	/** 发送日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createTime")
	protected Calendar createTime;
	/** 附件ID */
	@Column(name = "uploadFileGroupId")
	protected Long uploadFileGroupId;
	/** 单据编号 */
	@Column(name = "billNo")
	protected String billNo;
	/** 模块编码 */
	@Column(name = "processKey")
	private String processKey;
	/** 单据ID */
	@Column(name = "businessKey")
	private Long businessKey;
	@Transient
	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Long getReceipentId() {
		return receipentId;
	}

	public void setReceipentId(Long receipentId) {
		this.receipentId = receipentId;
	}

	public Long getUploadFileGroupId() {
		return uploadFileGroupId;
	}

	public void setUploadFileGroupId(Long uploadFileGroupId) {
		this.uploadFileGroupId = uploadFileGroupId;
	}

	public String getReceipentName() {
		return receipentName;
	}

	public void setReceipentName(String receipentName) {
		this.receipentName = receipentName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public Long getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(Long businessKey) {
		this.businessKey = businessKey;
	}
}
