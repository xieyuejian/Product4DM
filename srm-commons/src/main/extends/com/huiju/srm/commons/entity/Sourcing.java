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
 * 采购寻源
 */
@MappedSuperclass
public class Sourcing extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/** id */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Sourcing_PK")
	@TableGenerator(name = "Sourcing_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "Sourcing_PK", allocationSize = 1)
	@Column(name = "sourcingId")
	protected Long sourcingId;
	/** 价格主数据接口同步 */
	@Column(name = "priceMasterSyn")
	protected String priceMasterSyn;
	/** 价格主数据批量导入条数限制 */
	@Column(name = "batchLimit")
	protected BigDecimal batchLimit;
	/** 货源清单接口同步 */
	@Column(name = "inventorySyn")
	protected String inventorySyn;
	/** 配额数据接口同步 */
	@Column(name = "quotaSyn")
	protected String quotaSyn;
	/** 是否需要供应商确认合同 */
	@Column(name = "confirmContract")
	protected String confirmContract;
	/** 合同确认流程 */
	@Column(name = "contractProcess")
	protected String contractProcess;
	/** 是否需要供应商确认补充协议 */
	@Column(name = "confirmAgreement")
	protected String confirmAgreement;
	/** 补充协议确认流程 */
	@Column(name = "agreementProcess")
	protected String agreementProcess;
	/** 询价单是否根据物料组过滤供应商 */
	@Column(name = "materialGroupFilter1")
	protected String materialGroupFilter1;
	/** 招标单是否根据物料组过滤供应商 */
	@Column(name = "materialGroupFilter2")
	protected String materialGroupFilter2;
	/** 允许参与报价的供应商范围 */
	@Column(name = "offerRange")
	protected String offerRange;
	/** 是否需要供应商确认询价单 */
	@Column(name = "comfirmInquirySheet")
	protected String comfirmInquirySheet;
	/** 定价是否默认勾选第一名 */
	@Column(name = "checkFirst")
	protected String checkFirst;
	/** 是否需要供应商确认招标单 */
	@Column(name = "comfirmBidding1")
	protected String comfirmBidding1;
	/** 是否需要采购方确认招标单 */
	@Column(name = "comfirmBidding2")
	protected String comfirmBidding2;
	/** 投标单是否显示最低价 */
	@Column(name = "isShowMinPrice")
	protected String isShowMinPrice;
	/** 投标单是否显示总分和排名 */
	@Column(name = "isShowRank")
	protected String isShowRank;
	/** 允许参与投标的供应商范围 */
	@Column(name = "bidRange")
	protected String bidRange;
	/** 是否允许删除引用的合同条款模板 */
	@Column(name = "isDeleteTemplate")
	protected String isDeleteTemplate;
	/** 是否根据价格主数据自动生成货源清 */
	@Column(name = "isGenerate")
	protected String isGenerate;
	/** 所属管控点 */
	@Column(name = "belong")
	protected String belong;
	/** 所属管控点Id */
	/*
	 * @Column(name = "belongId") protected Long belongId;
	 */
	/** 是否允许多家供应商同时中标 */
	@Column(name = "isAllowBidSameTime")
	protected String isAllowBidSameTime;
	/** 投标单显示最低价相关信息 */
	@Column(name = "minimumPriceView")
	protected String minimumPriceView;
	/**询报价是否供应商需要确认*/
	@Column(name = "inquiringComfirm")
	protected String inquiringComfirm;
	/**竞价是否供应商需要确认*/
	@Column(name = "biddingComfirm")
	protected String biddingComfirm;

	public Long getSourcingId() {
		return sourcingId;
	}

	public void setSourcingId(Long sourcingId) {
		this.sourcingId = sourcingId;
	}

	public String getPriceMasterSyn() {
		return priceMasterSyn;
	}

	public void setPriceMasterSyn(String priceMasterSyn) {
		this.priceMasterSyn = priceMasterSyn;
	}

	public BigDecimal getBatchLimit() {
		return batchLimit;
	}

	public void setBatchLimit(BigDecimal batchLimit) {
		this.batchLimit = batchLimit;
	}

	public String getInventorySyn() {
		return inventorySyn;
	}

	public void setInventorySyn(String inventorySyn) {
		this.inventorySyn = inventorySyn;
	}

	public String getQuotaSyn() {
		return quotaSyn;
	}

	public void setQuotaSyn(String quotaSyn) {
		this.quotaSyn = quotaSyn;
	}

	public String getConfirmContract() {
		return confirmContract;
	}

	public void setConfirmContract(String confirmContract) {
		this.confirmContract = confirmContract;
	}

	public String getContractProcess() {
		return contractProcess;
	}

	public void setContractProcess(String contractProcess) {
		this.contractProcess = contractProcess;
	}

	public String getConfirmAgreement() {
		return confirmAgreement;
	}

	public void setConfirmAgreement(String confirmAgreement) {
		this.confirmAgreement = confirmAgreement;
	}

	public String getAgreementProcess() {
		return agreementProcess;
	}

	public void setAgreementProcess(String agreementProcess) {
		this.agreementProcess = agreementProcess;
	}

	public String getMaterialGroupFilter1() {
		return materialGroupFilter1;
	}

	public void setMaterialGroupFilter1(String materialGroupFilter1) {
		this.materialGroupFilter1 = materialGroupFilter1;
	}

	public String getMaterialGroupFilter2() {
		return materialGroupFilter2;
	}

	public void setMaterialGroupFilter2(String materialGroupFilter2) {
		this.materialGroupFilter2 = materialGroupFilter2;
	}

	public String getOfferRange() {
		return offerRange;
	}

	public void setOfferRange(String offerRange) {
		this.offerRange = offerRange;
	}

	public String getComfirmInquirySheet() {
		return comfirmInquirySheet;
	}

	public void setComfirmInquirySheet(String comfirmInquirySheet) {
		this.comfirmInquirySheet = comfirmInquirySheet;
	}

	public String getCheckFirst() {
		return checkFirst;
	}

	public void setCheckFirst(String checkFirst) {
		this.checkFirst = checkFirst;
	}

	public String getComfirmBidding1() {
		return comfirmBidding1;
	}

	public void setComfirmBidding1(String comfirmBidding1) {
		this.comfirmBidding1 = comfirmBidding1;
	}

	public String getComfirmBidding2() {
		return comfirmBidding2;
	}

	public void setComfirmBidding2(String comfirmBidding2) {
		this.comfirmBidding2 = comfirmBidding2;
	}

	public String getIsShowMinPrice() {
		return isShowMinPrice;
	}

	public void setIsShowMinPrice(String isShowMinPrice) {
		this.isShowMinPrice = isShowMinPrice;
	}

	public String getIsShowRank() {
		return isShowRank;
	}

	public void setIsShowRank(String isShowRank) {
		this.isShowRank = isShowRank;
	}

	public String getBidRange() {
		return bidRange;
	}

	public void setBidRange(String bidRange) {
		this.bidRange = bidRange;
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

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public String getIsAllowBidSameTime() {
		return isAllowBidSameTime;
	}

	public void setIsAllowBidSameTime(String isAllowBidSameTime) {
		this.isAllowBidSameTime = isAllowBidSameTime;
	}

	public String getMinimumPriceView() {
		return minimumPriceView;
	}

	public void setMinimumPriceView(String minimumPriceView) {
		this.minimumPriceView = minimumPriceView;
	}
	
	public String getInquiringComfirm() {
		return inquiringComfirm;
	}

	public void setInquiringComfirm(String inquiringComfirm) {
		this.inquiringComfirm = inquiringComfirm;
	}

	public String getBiddingComfirm() {
		return biddingComfirm;
	}

	public void setBiddingComfirm(String biddingComfirm) {
		this.biddingComfirm = biddingComfirm;
	}

	public Sourcing() {

	}

	public Sourcing(Long sourcingId) {
		this.sourcingId = sourcingId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourcingId == null) ? 0 : sourcingId.hashCode());
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
		Sourcing other = (Sourcing) obj;
		if (sourcingId == null) {
			if (other.sourcingId != null)
				return false;
		} else if (!sourcingId.equals(other.sourcingId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StdSourcing[sourcingId=" + sourcingId + ",priceMasterSyn=" + priceMasterSyn + ",batchLimit="
				+ batchLimit + ",inventorySyn=" + inventorySyn + ",quotaSyn=" + quotaSyn + ",confirmContract="
				+ confirmContract + ",contractProcess=" + contractProcess + ",confirmAgreement=" + confirmAgreement
				+ ",agreementProcess=" + agreementProcess + ",materialGroupFilter1=" + materialGroupFilter1
				+ ",materialGroupFilter2=" + materialGroupFilter2 + ",offerRange=" + offerRange
				+ ",comfirmInquirySheet=" + comfirmInquirySheet + ",checkFirst=" + checkFirst + ",comfirmBidding1="
				+ comfirmBidding1 + ",comfirmBidding2=" + comfirmBidding2 + ",isShowMinPrice=" + isShowMinPrice
				+ ",isShowRank=" + isShowRank + ",bidRange=" + bidRange + ",isDeleteTemplate=" + isDeleteTemplate
				+ ",isGenerate=" + isGenerate + ",belong=" + belong + ",inquiringComfirm=" + inquiringComfirm + ",biddingComfirm=" + biddingComfirm + "]";
	}

}
