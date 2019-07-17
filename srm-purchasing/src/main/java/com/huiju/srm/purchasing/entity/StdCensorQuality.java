package com.huiju.srm.purchasing.entity;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.jpa.entity.BaseEntity;
import com.huiju.srm.commons.utils.SrmSynStatus;

/**
 * <pre>质检管理表</pre>
 * @author wz 
 * @version 1.0 时间 2016/8/3 
 */
@MappedSuperclass
public class StdCensorQuality extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**送检质检单ID*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CensorQuality_PK")
    @TableGenerator(name = "CensorQuality_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "CensorQuality_PK",
    allocationSize = 1)
	@Column(name = "censorqualityId")
	protected Long censorqualityId;
	/**检验批号*/
	@Column(name = "censorqualityNo")
	protected String censorqualityNo;
	/**收货单号*/
	@Column(name = "receivingNoteNo")
	protected String receivingNoteNo;
	/**采购订单号*/
	@Column(name = "purchaseOrderNo")
	protected String purchaseOrderNo;
	/**行号*/
	@Column(name = "rowIds")
	protected Long rowIds;
	/**物料编码*/
	@Column(name = "materialCode")
	protected String materialCode;
	/**物料名称*/
	@Column(name = "materialName")
	protected String materialName;
	/**工厂编码*/
	@Column(name = "plantCode")
	protected String plantCode;
	/**工厂名称*/
	@Column(name = "plantName")
	protected String plantName;
	/**库存地点编码*/
	@Column(name = "stockCode")
	protected String stockCode;
	/**库存地点名称*/
	@Column(name = "stockName")
	protected String stockName;
	/**单位*/
	@Column(name = "unit")
	protected String unit;
	/**供应商编码*/
	@Column(name = "vendorCode")
	protected String vendorCode;
	/**供应商Erp编码*/
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;
	/**供应商名称*/
	@Column(name = "vendorName")
	protected String vendorName;
	/**凭证年度*/
	@Column(name = "voucherYear")
	protected String voucherYear;
	/**凭证编号*/
	@Column(name = "voucherNo")
	protected String voucherNo;
	/**凭证行项目号*/
	@Column(name = "voucherProNo")
	protected String voucherProNo;
	/**采购组织编码*/
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;
	/**采购组织名称*/
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;
	/**送检量*/
	@Column(name = "censorQty")
	protected BigDecimal censorQty;
	/**可检量*/
	@Column(name = "canCheckQty")
	protected BigDecimal canCheckQty;
	/**已质检合格量*/
	@Column(name = "checkQualifiedQty")
	protected BigDecimal checkQualifiedQty;
	/**已质检不合格量*/
	@Column(name = "checkUnqualifiedQty")
	protected BigDecimal checkUnqualifiedQty;
	/**已质检让步接收量*/
	@Column(name = "checkReceiveQty")
	protected BigDecimal checkReceiveQty;
	/**合格量*/
	@Column(name = "qualifiedQty")
	protected BigDecimal qualifiedQty;
	/**不合格量*/
	@Column(name = "unqualifiedQty")
	protected BigDecimal unqualifiedQty;
	/**让步接收量*/
	@Column(name = "receiveQty")
	protected BigDecimal receiveQty;
	/**质检状态*/
	@Column(name = "status")
	protected CensorQualityState status;
	/**质检结果名称*/
	@Column(name = "resultName")
	protected String resultName;
	/**质检结果代码*/
	@Column(name = "resultCode")
	protected String resultCode;
	/**送检时间*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inspectionTime")
	protected Calendar inspectionTime;
	/**质检时间*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "qualityTime")
	protected Calendar qualityTime;
	/**备注*/
	@Column(name = "remark")
	protected String remark;
	/**附件*/
	@Column(name = "uploadFileGroupId")
	protected Long uploadFileGroupId;
	/**同步状态*/
	@Column(name = "erpSyn")
	protected SrmSynStatus erpSyn;
	/**同步信息*/
	@Column(name = "erpReturnMsg")
	protected String erpReturnMsg;
	/**送检人员id*/
	@Column(name = "inspectorId")
	protected Long inspectorId;
	/**送检人员名称*/
	@Column(name = "inspectorName")
	protected String inspectorName;
	/**质检人员id*/
	@Column(name = "qualitorId")
	protected Long qualitorId;
	/**质检人员名称*/
	@Column(name = "qualitorName")
	protected String qualitorName;
	
	/**客户端编码*/
	@Column(name = "clientCode")
	protected String clientCode;
	
	
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	
	public Long getCensorqualityId() {
		return censorqualityId;
	}
	public void setCensorqualityId(Long censorqualityId) {
		this.censorqualityId = censorqualityId;
	}
	public String getCensorqualityNo() {
		return censorqualityNo;
	}
	public void setCensorqualityNo(String censorqualityNo) {
		this.censorqualityNo = censorqualityNo;
	}
	
	public String getReceivingNoteNo() {
		return receivingNoteNo;
	}
	public void setReceivingNoteNo(String receivingNoteNo) {
		this.receivingNoteNo = receivingNoteNo;
	}
	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}
	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}
	public Long getRowIds() {
		return rowIds;
	}
	public void setRowIds(Long rowIds) {
		this.rowIds = rowIds;
	}
	public String getMaterialCode() {
		return materialCode;
	}
	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public String getPlantCode() {
		return plantCode;
	}
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getVendorErpCode() {
		return vendorErpCode;
	}
	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getVoucherYear() {
		return voucherYear;
	}
	public void setVoucherYear(String voucherYear) {
		this.voucherYear = voucherYear;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public String getVoucherProNo() {
		return voucherProNo;
	}
	public void setVoucherProNo(String voucherProNo) {
		this.voucherProNo = voucherProNo;
	}
	public String getPurchasingOrgCode() {
		return purchasingOrgCode;
	}
	public void setPurchasingOrgCode(String purchasingOrgCode) {
		this.purchasingOrgCode = purchasingOrgCode;
	}
	public String getPurchasingOrgName() {
		return purchasingOrgName;
	}
	public void setPurchasingOrgName(String purchasingOrgName) {
		this.purchasingOrgName = purchasingOrgName;
	}
	public BigDecimal getCensorQty() {
		return censorQty;
	}
	public void setCensorQty(BigDecimal censorQty) {
		this.censorQty = censorQty;
	}
	public BigDecimal getCanCheckQty() {
		return canCheckQty;
	}
	public void setCanCheckQty(BigDecimal canCheckQty) {
		this.canCheckQty = canCheckQty;
	}
	public BigDecimal getCheckQualifiedQty() {
		return checkQualifiedQty;
	}
	public void setCheckQualifiedQty(BigDecimal checkQualifiedQty) {
		this.checkQualifiedQty = checkQualifiedQty;
	}
	public BigDecimal getCheckUnqualifiedQty() {
		return checkUnqualifiedQty;
	}
	public void setCheckUnqualifiedQty(BigDecimal checkUnqualifiedQty) {
		this.checkUnqualifiedQty = checkUnqualifiedQty;
	}
	public BigDecimal getCheckReceiveQty() {
		return checkReceiveQty;
	}
	public void setCheckReceiveQty(BigDecimal checkReceiveQty) {
		this.checkReceiveQty = checkReceiveQty;
	}
	public BigDecimal getQualifiedQty() {
		return qualifiedQty;
	}
	public void setQualifiedQty(BigDecimal qualifiedQty) {
		this.qualifiedQty = qualifiedQty;
	}
	public BigDecimal getUnqualifiedQty() {
		return unqualifiedQty;
	}
	public void setUnqualifiedQty(BigDecimal unqualifiedQty) {
		this.unqualifiedQty = unqualifiedQty;
	}
	public BigDecimal getReceiveQty() {
		return receiveQty;
	}
	public void setReceiveQty(BigDecimal receiveQty) {
		this.receiveQty = receiveQty;
	}
	
	public CensorQualityState getStatus() {
		return status;
	}
	public void setStatus(CensorQualityState status) {
		this.status = status;
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public Calendar getInspectionTime() {
		return inspectionTime;
	}
	public void setInspectionTime(Calendar inspectionTime) {
		this.inspectionTime = inspectionTime;
	}
	public Calendar getQualityTime() {
		return qualityTime;
	}
	public void setQualityTime(Calendar qualityTime) {
		this.qualityTime = qualityTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getUploadFileGroupId() {
		return uploadFileGroupId;
	}
	public void setUploadFileGroupId(Long uploadFileGroupId) {
		this.uploadFileGroupId = uploadFileGroupId;
	}
	public SrmSynStatus getErpSyn() {
		return erpSyn;
	}
	public void setErpSyn(SrmSynStatus erpSyn) {
		this.erpSyn = erpSyn;
	}
	public String getErpReturnMsg() {
		return erpReturnMsg;
	}
	public void setErpReturnMsg(String erpReturnMsg) {
		this.erpReturnMsg = erpReturnMsg;
	}
	public Long getInspectorId() {
		return inspectorId;
	}
	public void setInspectorId(Long inspectorId) {
		this.inspectorId = inspectorId;
	}
	public String getInspectorName() {
		return inspectorName;
	}
	public void setInspectorName(String inspectorName) {
		this.inspectorName = inspectorName;
	}
	public Long getQualitorId() {
		return qualitorId;
	}
	public void setQualitorId(Long qualitorId) {
		this.qualitorId = qualitorId;
	}
	public String getQualitorName() {
		return qualitorName;
	}
	public void setQualitorName(String qualitorName) {
		this.qualitorName = qualitorName;
	}
	


	
	
	public StdCensorQuality() {
	
	}
	public StdCensorQuality(Long censorqualityId ) {
		this.censorqualityId = censorqualityId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((censorqualityId == null) ? 0 : censorqualityId.hashCode());
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
		StdCensorQuality other = (StdCensorQuality) obj;
		if (censorqualityId == null) {
			if (other.censorqualityId != null)
				return false;
		} else if (!censorqualityId.equals(other.censorqualityId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdCensorQuality[censorqualityId=" + censorqualityId + ",censorqualityNo=" + censorqualityNo + ",receivingNoteNo=" + receivingNoteNo + ",purchaseOrderNo=" + purchaseOrderNo + ",rowIds=" + rowIds + ",materialCode=" + materialCode + ",materialName=" + materialName + ",plantCode=" + plantCode + ",plantName=" + plantName + ",stockCode=" + stockCode + ",unit=" + unit + ",vendorCode=" + vendorCode + ",vendorErpCode=" + vendorErpCode + ",vendorName=" + vendorName + ",voucherYear=" + voucherYear + ",voucherNo=" + voucherNo + ",voucherProNo=" + voucherProNo + ",purchasingOrgCode=" + purchasingOrgCode + ",purchasingOrgName=" + purchasingOrgName + ",censorQty=" + censorQty + ",canCheckQty=" + canCheckQty + ",checkQualifiedQty=" + checkQualifiedQty + ",checkUnqualifiedQty=" + checkUnqualifiedQty + ",checkReceiveQty=" + checkReceiveQty + ",qualifiedQty=" + qualifiedQty + ",unqualifiedQty=" + unqualifiedQty + ",receiveQty=" + receiveQty + ",status=" + status + ",resultName=" + resultName + ",resultCode=" + resultCode + ",inspectionTime=" + inspectionTime + ",qualityTime=" + qualityTime + ",remark=" + remark + ",uploadFileGroupId=" + uploadFileGroupId + ",erpSyn=" + erpSyn + ",erpReturnMsg=" + erpReturnMsg + ",inspectorId=" + inspectorId + ",inspectorName=" + inspectorName + ",qualitorId=" + qualitorId + ",qualitorName=" + qualitorName + "]";
	
	}

}
