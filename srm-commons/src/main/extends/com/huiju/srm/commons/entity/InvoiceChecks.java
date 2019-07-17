package com.huiju.srm.commons.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 发票对账
 */
@MappedSuperclass
public class InvoiceChecks extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "InvoiceChecks_PK")
    @TableGenerator(name = "InvoiceChecks_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "InvoiceChecks_PK",
    allocationSize = 1)
	@Column(name = "invoiceChecksId")
	protected Long invoiceChecksId;
	/**发票勾兑接口同步*/
	@Column(name = "invoiceBendingSyn")
	protected String invoiceBendingSyn;
	/**发票明细条数限制*/
	@Column(name = "numberRestriction")
	protected BigDecimal numberRestriction;
	/**税控发票号码最大长度限制*/
	@Column(name = "maxlLength")
	protected Long maxlLength;
	/**对账单发起方*/
	@Column(name = "sponsor")
	protected String sponsor;
	/**是否按物料组分开对账*/
	@Column(name = "materialGroupAccount")
	protected String materialGroupAccount;
	/**对账单明细的结算未税价是否允许修改*/
	@Column(name = "nonTaxPriceIsEdit")
	protected String nonTaxPriceIsEdit;
	/**对账单明细的结算未税金额是否允许修改 */
	@Column(name = "amountNotTaxEdit")
	protected String amountNotTaxEdit;
	/**对账单明细的结算含税价是否允许修改*/
	@Column(name = "taxPriceEdit")
	protected String taxPriceEdit;
	/**对账单明细的结算含税金额是否允许修改 */
	@Column(name = "amountTaxEdit")
	protected String amountTaxEdit;
	/**对账单明细的结算黄金价是否允许修改*/
	@Column(name = "goldPriceEdit")
	protected String goldPriceEdit;
	/**对账价格来源*/
	@Column(name = "accountSource")
	protected String accountSource;
	/**所属管控点*/
	@Column(name = "belong")
	protected String belong;
	/**管控点id*/
	/*@Column(name = "belongId")
	protected Long belongId;*/
	
	public Long getInvoiceChecksId() {
		return invoiceChecksId;
	}
	public void setInvoiceChecksId(Long invoiceChecksId) {
		this.invoiceChecksId = invoiceChecksId;
	}
	public String getInvoiceBendingSyn() {
		return invoiceBendingSyn;
	}
	public void setInvoiceBendingSyn(String invoiceBendingSyn) {
		this.invoiceBendingSyn = invoiceBendingSyn;
	}
	public BigDecimal getNumberRestriction() {
		return numberRestriction;
	}
	public void setNumberRestriction(BigDecimal numberRestriction) {
		this.numberRestriction = numberRestriction;
	}
	public Long getMaxlLength() {
		return maxlLength;
	}
	public void setMaxlLength(Long maxlLength) {
		this.maxlLength = maxlLength;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	public String getMaterialGroupAccount() {
		return materialGroupAccount;
	}
	public void setMaterialGroupAccount(String materialGroupAccount) {
		this.materialGroupAccount = materialGroupAccount;
	}
	public String getNonTaxPriceIsEdit() {
		return nonTaxPriceIsEdit;
	}
	public void setNonTaxPriceIsEdit(String nonTaxPriceIsEdit) {
		this.nonTaxPriceIsEdit = nonTaxPriceIsEdit;
	}
	public String getAmountNotTaxEdit() {
		return amountNotTaxEdit;
	}
	public void setAmountNotTaxEdit(String amountNotTaxEdit) {
		this.amountNotTaxEdit = amountNotTaxEdit;
	}
	public String getTaxPriceEdit() {
		return taxPriceEdit;
	}
	public void setTaxPriceEdit(String taxPriceEdit) {
		this.taxPriceEdit = taxPriceEdit;
	}
	public String getAmountTaxEdit() {
		return amountTaxEdit;
	}
	public void setAmountTaxEdit(String amountTaxEdit) {
		this.amountTaxEdit = amountTaxEdit;
	}
	public String getGoldPriceEdit() {
		return goldPriceEdit;
	}
	public void setGoldPriceEdit(String goldPriceEdit) {
		this.goldPriceEdit = goldPriceEdit;
	}
	public String getAccountSource() {
		return accountSource;
	}
	public void setAccountSource(String accountSource) {
		this.accountSource = accountSource;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
 
 
	public InvoiceChecks() {
	
	}
	public InvoiceChecks(Long invoiceChecksId ) {
		this.invoiceChecksId = invoiceChecksId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((invoiceChecksId == null) ? 0 : invoiceChecksId.hashCode());
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
		InvoiceChecks other = (InvoiceChecks) obj;
		if (invoiceChecksId == null) {
			if (other.invoiceChecksId != null)
				return false;
		} else if (!invoiceChecksId.equals(other.invoiceChecksId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdInvoiceChecks[invoiceChecksId=" + invoiceChecksId + ",invoiceBendingSyn=" + invoiceBendingSyn + ",numberRestriction=" + numberRestriction + ",maxlLength=" + maxlLength + ",sponsor=" + sponsor + ",materialGroupAccount=" + materialGroupAccount + ",nonTaxPriceIsEdit=" + nonTaxPriceIsEdit + ",amountNotTaxEdit=" + amountNotTaxEdit + ",taxPriceEdit=" + taxPriceEdit + ",amountTaxEdit=" + amountTaxEdit + ",goldPriceEdit=" + goldPriceEdit + ",accountSource=" + accountSource + ",belong=" + belong  + "]";
	}

}
