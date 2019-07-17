package com.huiju.srm.commons.entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 协同采购
 */
@MappedSuperclass
public class CollaboratedProcurement extends BaseEntity<Long> {
private static final long serialVersionUID = 3075133691557657227L;
	/**id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CollaboratedProcurement_PK")
    @TableGenerator(name = "CollaboratedProcurement_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "CollaboratedProcurement_PK",
    allocationSize = 1)
	@Column(name = "collaboratedProcurementId")
	protected Long collaboratedProcurementId;
	/**采购订单接口同步*/
	@Column(name = "orderSyn")
	protected String orderSyn;
	/**送货排程接口同步*/
	@Column(name = "deliverySyn")
	protected String deliverySyn;
	/**送货明细来源*/
	@Column(name = "deliverySource")
	protected String deliverySource;
	/**送货单接口同步*/
	@Column(name = "deliveryOrderSyn")
	protected String deliveryOrderSyn;
	/**收货接口同步*/
	@Column(name = "receivingSyn")
	protected String receivingSyn;
	/**订单确认流程*/
	@Column(name = "orderProcess")
	protected String orderProcess;
	/**是否允许供应商修改价格*/
	@Column(name = "isEditPrice")
	protected String isEditPrice;
	/**是否允许供应商查看采购订单金额 */
	@Column(name = "isViewOrderPrice")
	protected String isViewOrderPrice;
	/**供应商默认接受采购订单标识*/
	@Column(name = "acceptOrderFlag")
	protected String acceptOrderFlag;
	/**是否需要供应商确认排程*/
	@Column(name = "scheduleComfirm")
	protected String scheduleComfirm;
	/**排程是否允许选择多个订单*/
	@Column(name = "multipleOrder")
	protected String multipleOrder;
	/**订单明细是否需要全部排程*/
	@Column(name = "isFullScheduling")
	protected String isFullScheduling;
	/**排程变更后是否需要供方确认*/
	@Column(name = "changeComfirm")
	protected String changeComfirm;
	/**采购方是否需要确认送货*/
	@Column(name = "deliveryComfirm")
	protected String deliveryComfirm;
	/**是否允许删除引用的合同条款模板 */
	@Column(name = "isDeleteTemplate")
	protected String isDeleteTemplate;
	/**是否根据价格主数据自动生成货源清单*/
	@Column(name = "isGenerate")
	protected String isGenerate;
	/**价格主数据接口同步*/
	@Column(name = "masterSyn")
	protected String masterSyn;
	/**配额数据接口同步*/
	@Column(name = "quotaSyn")
	protected String quotaSyn;
	/**质检接口同步 */
	@Column(name = "qualitySyn")
	protected String qualitySyn;
	/**是否受源清单控制*/
	@Column(name = "isControl")
	protected String isControl;
	/**送货单导出条码选择*/
	@Column(name = "barCodeType")
	protected String barCodeType;
	//订单是否供应商确认 1 是 2否
	@Column(name = "orderComfirm")
	protected String orderComfirm;
	/**所属管控点*/
	@Column(name = "belong")
	protected String belong;
	/**所属管控点id*/
	/*@Column(name = "belongId")
	protected Long belongId;*/
	
	public Long getCollaboratedProcurementId() {
		return collaboratedProcurementId;
	}
	public void setCollaboratedProcurementId(Long collaboratedProcurementId) {
		this.collaboratedProcurementId = collaboratedProcurementId;
	}
	public String getOrderSyn() {
		return orderSyn;
	}
	public void setOrderSyn(String orderSyn) {
		this.orderSyn = orderSyn;
	}
	public String getDeliverySyn() {
		return deliverySyn;
	}
	public void setDeliverySyn(String deliverySyn) {
		this.deliverySyn = deliverySyn;
	}
	public String getDeliverySource() {
		return deliverySource;
	}
	public void setDeliverySource(String deliverySource) {
		this.deliverySource = deliverySource;
	}
	public String getDeliveryOrderSyn() {
		return deliveryOrderSyn;
	}
	public void setDeliveryOrderSyn(String deliveryOrderSyn) {
		this.deliveryOrderSyn = deliveryOrderSyn;
	}
	public String getReceivingSyn() {
		return receivingSyn;
	}
	public void setReceivingSyn(String receivingSyn) {
		this.receivingSyn = receivingSyn;
	}
	public String getOrderProcess() {
		return orderProcess;
	}
	public void setOrderProcess(String orderProcess) {
		this.orderProcess = orderProcess;
	}
	public String getIsEditPrice() {
		return isEditPrice;
	}
	public void setIsEditPrice(String isEditPrice) {
		this.isEditPrice = isEditPrice;
	}
	public String getIsViewOrderPrice() {
		return isViewOrderPrice;
	}
	public void setIsViewOrderPrice(String isViewOrderPrice) {
		this.isViewOrderPrice = isViewOrderPrice;
	}
	public String getAcceptOrderFlag() {
		return acceptOrderFlag;
	}
	public void setAcceptOrderFlag(String acceptOrderFlag) {
		this.acceptOrderFlag = acceptOrderFlag;
	}
	public String getScheduleComfirm() {
		return scheduleComfirm;
	}
	public void setScheduleComfirm(String scheduleComfirm) {
		this.scheduleComfirm = scheduleComfirm;
	}
	public String getMultipleOrder() {
		return multipleOrder;
	}
	public void setMultipleOrder(String multipleOrder) {
		this.multipleOrder = multipleOrder;
	}
	public String getIsFullScheduling() {
		return isFullScheduling;
	}
	public void setIsFullScheduling(String isFullScheduling) {
		this.isFullScheduling = isFullScheduling;
	}
	public String getChangeComfirm() {
		return changeComfirm;
	}
	public void setChangeComfirm(String changeComfirm) {
		this.changeComfirm = changeComfirm;
	}
	public String getDeliveryComfirm() {
		return deliveryComfirm;
	}
	public void setDeliveryComfirm(String deliveryComfirm) {
		this.deliveryComfirm = deliveryComfirm;
	}
	public String getIsDeleteTemplate() {
		return isDeleteTemplate;
	}
	public void setIsDeleteTemplate(String isDeleteTemplate) {
		this.isDeleteTemplate = isDeleteTemplate;
	}
	public String getIsGenerate() {
		return isGenerate;
	}
	public void setIsGenerate(String isGenerate) {
		this.isGenerate = isGenerate;
	}
	public String getMasterSyn() {
		return masterSyn;
	}
	public void setMasterSyn(String masterSyn) {
		this.masterSyn = masterSyn;
	}
	public String getQuotaSyn() {
		return quotaSyn;
	}
	public void setQuotaSyn(String quotaSyn) {
		this.quotaSyn = quotaSyn;
	}
	public String getQualitySyn() {
		return qualitySyn;
	}
	public void setQualitySyn(String qualitySyn) {
		this.qualitySyn = qualitySyn;
	}
	public String getIsControl() {
		return isControl;
	}
	public void setIsControl(String isControl) {
		this.isControl = isControl;
	}
	public String getBarCodeType() {
		return barCodeType;
	}
	public void setBarCodeType(String barCodeType) {
		this.barCodeType = barCodeType;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	} 
	
	
	public String getOrderComfirm() {
		return orderComfirm;
	}
	public void setOrderComfirm(String orderComfirm) {
		this.orderComfirm = orderComfirm;
	}
	public CollaboratedProcurement() {
	
	}
	public CollaboratedProcurement(Long collaboratedProcurementId ) {
		this.collaboratedProcurementId = collaboratedProcurementId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collaboratedProcurementId == null) ? 0 : collaboratedProcurementId.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollaboratedProcurement other = (CollaboratedProcurement) obj;
		if (collaboratedProcurementId == null) {
			if (other.collaboratedProcurementId != null)
				return false;
		} else if (!collaboratedProcurementId.equals(other.collaboratedProcurementId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdCollaboratedProcurement[collaboratedProcurementId=" + collaboratedProcurementId + ",orderSyn=" + orderSyn + ",deliverySyn=" + deliverySyn + ",deliverySource=" + deliverySource + ",deliveryOrderSyn=" + deliveryOrderSyn + ",receivingSyn=" + receivingSyn + ",orderProcess=" + orderProcess + ",isEditPrice=" + isEditPrice + ",isViewOrderPrice=" + isViewOrderPrice + ",acceptOrderFlag=" + acceptOrderFlag + ",scheduleComfirm=" + scheduleComfirm + ",multipleOrder=" + multipleOrder + ",isFullScheduling=" + isFullScheduling + ",changeComfirm=" + changeComfirm + ",deliveryComfirm=" + deliveryComfirm + ",isDeleteTemplate=" + isDeleteTemplate + ",isGenerate=" + isGenerate + ",masterSyn=" + masterSyn + ",quotaSyn=" + quotaSyn + ",qualitySyn=" + qualitySyn + ",isControl=" + isControl + ",barCodeType=" + barCodeType + ",belong=" + belong  + "]";
	}

}
