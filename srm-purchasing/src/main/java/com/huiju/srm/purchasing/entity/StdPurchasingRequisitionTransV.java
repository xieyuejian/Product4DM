package com.huiju.srm.purchasing.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
/**
 * 采购申请转单视图
 * 
 * @author bairx
 * @date 2019/3/30 
 */
@MappedSuperclass
public class StdPurchasingRequisitionTransV implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7155114124309260111L;
	@Id
	@Column(name="zj")
	protected String zj;

	/**公司编码*/
	@Column(name = "companyCode")
	protected String companyCode;

	/**公司名称*/
	@Column(name = "companyName")
	protected String companyName;

	/**采购组织编码*/
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;

	/**采购组织名称*/
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;

	/**供应商编码*/
    @Column(name = "vendorCode")
	protected String vendorCode;

	/**供应商ERP编码*/
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;

	/**供应商名称*/
	@Column(name = "vendorName")
	protected String vendorName;

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

}
