package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.inquiry.entity.PriceAffirm;
import com.huiju.srm.inquiry.entity.PriceAffirmDetail;
import com.huiju.srm.inquiry.entity.PriceAffirmState;
import com.huiju.srm.inquiry.service.PriceAffirmDetailService;
import com.huiju.srm.purchasing.dao.PurchasingRequisitionTransDao;
import com.huiju.srm.purchasing.dao.PurchasingRequisitionTransVDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTrans;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTransV;
import com.huiju.srm.sourcing.dao.MaterialMasterPriceDtlDao;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialMasterPrice;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;

/**
 * 采购申请转单实现类
 * 
 * @author bairx
 *
 */
@Service
public class StdPurchasingRequisitionTransServiceImpl extends JpaServiceImpl<PurchasingRequisitionTrans, Long>
		implements StdPurchasingRequisitionTransService {
	@Autowired
	protected PurchasingRequisitionTransDao purchasingRequisitionTransDao;
	@Autowired
	protected PurchasingRequisitionCollectionService purchasingRequisitionCollectionsServiceImpl;
	@Autowired
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired
	protected MaterialMasterPriceDtlDao materialMasterPriceDtlDao;

	@Autowired
	protected PriceAffirmDetailService priceAffirmDetailLogic;
	@Autowired
	protected PurchasingRequisitionTransService purchasingRequisitionTransLocal;
	@Autowired
	protected PurchasingRequisitionTransVDao purchasingRequisitionTransVEao;

	@Override
	public List<PurchasingRequisitionTrans> getConfigList(String purchasingRequisitionColId, String materialCode, String clientCode,
			String userCode) {
		PurchasingRequisitionCollection prc = purchasingRequisitionCollectionsServiceImpl
				.findById(Long.valueOf(purchasingRequisitionColId));
		List<PurchasingRequisitionTrans> rsList = new ArrayList<PurchasingRequisitionTrans>();
		Map<String, PurchasingRequisitionTrans> cacheRecs = new HashMap<String, PurchasingRequisitionTrans>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		List<MaterialMasterPriceDtl> mmpDtls = new ArrayList<MaterialMasterPriceDtl>();
		List<PriceAffirmDetail> padList = new ArrayList<PriceAffirmDetail>();
		List<PurchasingRequisitionTrans> configedList = new ArrayList<PurchasingRequisitionTrans>();
		List<PurchasingRequisitionCollection> collectionList = new ArrayList<PurchasingRequisitionCollection>();
		try {
			if (StringUtils.isNotBlank(materialCode)) {
				// 获取价格主数据
				if (mmpDtls == null || mmpDtls.size() < 1) {
					searchMap.clear();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currDate = formatter.format(Calendar.getInstance().getTime());
					searchMap.put("LE_effectiveDate", currDate);
					searchMap.put("GE_expirationDate", currDate);
					searchMap.put("EQ_materialMasterPrice_materialCode", materialCode);
					searchMap.put("EQ_materialMasterPrice_purchasingGroupCode", prc.getPurchasingGroupCode());
					mmpDtls = materialMasterPriceDtlDao.findAll(searchMap);

				}
				for (MaterialMasterPriceDtl mmpd : mmpDtls) {
					if (!materialCode.equals(mmpd.getMaterialMasterPrice().getMaterialCode())) {
						continue;
					}
					MaterialMasterPrice mmp = mmpd.getMaterialMasterPrice();
					PurchasingRequisitionTrans prt = new PurchasingRequisitionTrans();
					prt.setPurchasingOrgCode(mmp.getPurchasingOrgCode());
					prt.setPurchasingOrgName(mmp.getPurchasingOrgName());
					prt.setVendorCode(mmp.getVendorCode());
					prt.setVendorErpCode(mmp.getVendorErpCode());
					prt.setVendorName(mmp.getVendorName());
					prt.setPurchaseType(mmp.getRecordType());
					prt.setTransferQuantity(new BigDecimal(0));
					prt.setTaxrateCode(mmpd.getTaxRateCode());
					prt.setTaxrateValue(mmpd.getTaxRateValue());
					// Map<String, Object> lpMap = new HashMap<String,
					// Object>();
					// lpMap.put("EQ_materialMasterPriceDtl_materialMasterPriceDtlId",
					// mmpd.getMaterialMasterPriceDtlId());
					// List<MaterialLadderPriceDtl> mlpdList =
					// materialMasterPriceLogic.findMaterialLadderPriceDtlAll(lpMap);
					List<MaterialLadderPriceDtl> mlpdList = mmpd.getMaterialLadderPriceDtls();
					double nonTaxPrice = 0.00;
					double num = 0.00;
					// 取有效时间段内阶梯报价的最小起始数量的未税价
					for (int i = 0; i < mlpdList.size(); i++) {
						// 起始数量
						double tempStartNum = mlpdList.get(i).getStartNum() == null ? 0.00 : mlpdList.get(i).getStartNum().doubleValue();
						// 未税价
						double tmpPrice = mlpdList.get(i).getNonTaxPrice() == null ? 0.00 : mlpdList.get(i).getNonTaxPrice().doubleValue();
						if (i == 0) {
							num = tempStartNum;
							nonTaxPrice = tmpPrice;
						} else if (tempStartNum < num) {
							num = tempStartNum;
							nonTaxPrice = tmpPrice;
						}
					}
					prt.setPrice(new BigDecimal(nonTaxPrice));
					rsList.add(prt);
					String key = mmp.getPurchasingOrgCode() + "_" + mmp.getPurchasingOrgName() + "_" + mmp.getVendorCode() + "_"
							+ mmp.getVendorName() + "_" + mmp.getRecordType();
					cacheRecs.put(key, prt);
				}
			} else {
				// 获取定价单数据
				if (padList == null || padList.size() < 1) {

					searchMap.clear();
					searchMap.put("EQ_priceAffirm_priceAffirmState", PriceAffirmState.Close);
					searchMap.put("EQ_materialName", prc.getMaterialName());
					searchMap.put("EQ_plantCode", prc.getPlantCode());
					searchMap.put("EQ_priceAffirm_purchasingGroupCode", prc.getPurchasingGroupCode());
					padList = priceAffirmDetailLogic.findAll(searchMap);

				}
				for (PriceAffirmDetail pad : padList) {
					PriceAffirm pa = pad.getPriceAffirm();
					PurchasingRequisitionTrans prt = new PurchasingRequisitionTrans();
					prt.setPurchasingOrgCode(pa.getPurchasingOrgCode());
					prt.setPurchasingOrgName(pa.getPurchasingOrgName());
					prt.setVendorCode(pad.getVendorCode());
					prt.setVendorErpCode(pad.getVendorErpCode());
					prt.setVendorName(pad.getVendorName());
					prt.setPurchaseType(pa.getPriceInquiry().getInfoRecordTypeCode());
					prt.setTransferQuantity(new BigDecimal(0));
					prt.setTaxrateCode(pad.getTaxCode());
					prt.setTaxrateValue(pad.getTaxRate());
					prt.setPrice(new BigDecimal((new java.text.DecimalFormat("#.00"))
							.format(pad.getQuotationPriceincTax().doubleValue() / (1 + pad.getTaxRate().doubleValue()))));
					rsList.add(prt);
					String key = pa.getPurchasingOrgCode() + "_" + pa.getPurchasingOrgName() + "_" + pad.getVendorCode() + "_"
							+ pad.getVendorName() + "_" + pa.getPriceInquiry().getInfoRecordTypeCode();
					cacheRecs.put(key, prt);
				}
			}
			// 查询已分配的
			if (configedList == null || configedList.size() < 1) {
				Map<String, Object> searchParams = new HashMap<String, Object>();
				searchParams.put("EQ_isTransfered", 0);
				searchParams.put("EQ_purchasingRequisitionCollection_purchasingRequisitionColId", purchasingRequisitionColId);
				configedList = purchasingRequisitionTransDao.findAllWithoutAssociation(searchParams);
				searchParams.clear();
				searchParams.put("EQ_purchasingRequisitionColId", purchasingRequisitionColId);
				collectionList = purchasingRequisitionCollectionsServiceImpl.findAll(searchParams);
				for (int i = 0; i < collectionList.size() && i < configedList.size(); i++) {
					configedList.get(i).setPurchasingRequisitionCollection(collectionList.get(i));
				}

			}
			for (PurchasingRequisitionTrans prts : configedList) {
				if (prts.getPurchasingRequisitionCollection() != null) {
					if (!Long.valueOf(purchasingRequisitionColId)
							.equals(prts.getPurchasingRequisitionCollection().getPurchasingRequisitionColId())) {
						continue;
					}
				}
				String key = prts.getPurchasingOrgCode() + "_" + prts.getPurchasingOrgName() + "_" + prts.getVendorCode() + "_"
						+ prts.getVendorName() + "_" + prts.getPurchaseType();
				PurchasingRequisitionTrans prt = cacheRecs.get(key);
				if (prt != null) {
					prt.setTransferQuantity(prts.getTransferQuantity());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rsList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer saveTrans(List<Map> transList) {
		if (transList == null || transList.isEmpty()) {
			return 1;
		}
		// 判断是否存在可分配数量为负数
		for (Map<String, Object> tranMap : transList) {
			Integer canTransferQuantity = (Integer) tranMap.get("canTransferQuantity");
			List<Map<String, Object>> transOrders = (List<Map<String, Object>>) tranMap.get("transOrders");
			int configedQuantity = 0;
			for (Map<String, Object> configMap : transOrders) {
				Integer transferQuantity = (Integer) configMap.get("transferQuantity");
				configedQuantity += transferQuantity.intValue();
			}
			int tmpCanTransferQuantity = canTransferQuantity - configedQuantity;
			if (tmpCanTransferQuantity < 0) {
				return 2;
			}
		}
		// 更新数据到数据库中
		for (Map<String, Object> tranMap : transList) {
			Integer purchasingRequisitionColId = (Integer) tranMap.get("purchasingRequisitionColId");
			String companyCode = (String) tranMap.get("companyCode");
			String companyName = (String) tranMap.get("companyName");
			String purchasingRequisitionNo = (String) tranMap.get("purchasingRequisitionNo");
			List<Map<String, Object>> transOrders = (List<Map<String, Object>>) tranMap.get("transOrders");
			// 查询关联的已分配列表
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_purchasingRequisitionCollection_purchasingRequisitionColId", purchasingRequisitionColId);
			searchParams.put("EQ_isTransfered", 0);
			List<PurchasingRequisitionTrans> configedList = purchasingRequisitionTransDao.findAll(searchParams);
			for (PurchasingRequisitionTrans configed : configedList) {
				purchasingRequisitionTransDao.delete(configed);
			}
			int configedQuantity = 0;
			// 插入新分配的数据
			for (Map<String, Object> configMap : transOrders) {
				String purchasingOrgCode = (String) configMap.get("purchasingOrgCode");
				String purchasingOrgName = (String) configMap.get("purchasingOrgName");
				String vendorCode = (String) configMap.get("vendorCode");
				String vendorErpCode = (String) configMap.get("vendorErpCode");
				String vendorName = (String) configMap.get("vendorName");
				String purchaseType = (String) configMap.get("purchaseType");
				String taxrateCode = (String) configMap.get("taxrateCode");
				String taxrateValue = (String) configMap.get("taxrateValue");
				Integer transferQuantity = (Integer) configMap.get("transferQuantity");
				String price = configMap.get("price") == null ? "0.00" : configMap.get("price").toString();
				PurchasingRequisitionTrans drt = new PurchasingRequisitionTrans();
				drt.setPurchasingRequisitionNo(purchasingRequisitionNo);
				drt.setPurchasingOrgCode(purchasingOrgCode);
				drt.setPurchasingOrgName(purchasingOrgName);
				drt.setVendorCode(vendorCode);
				drt.setVendorErpCode(vendorErpCode);
				drt.setVendorName(vendorName);
				drt.setCompanyCode(companyCode);
				drt.setCompanyName(companyName);
				PurchasingRequisitionCollection prc = new PurchasingRequisitionCollection();
				prc.setPurchasingRequisitionColId(Long.valueOf(purchasingRequisitionColId));
				drt.setPurchasingRequisitionCollection(prc);
				drt.setIsTransfered("0");
				drt.setTransferQuantity(new BigDecimal(transferQuantity));
				drt.setAssignedQuantity(new BigDecimal(transferQuantity));
				drt.setPurchaseType(purchaseType);
				drt.setTaxrateCode(taxrateCode);
				drt.setTaxrateValue(new BigDecimal(taxrateValue));
				drt.setPrice(new BigDecimal(price));
				purchasingRequisitionTransDao.save(drt);
				configedQuantity += transferQuantity.intValue();
			}

			String isConfiged = "1";
			if (configedQuantity == 0) {
				isConfiged = "0";
			}

			PurchasingRequisitionCollection prc = purchasingRequisitionCollectionsServiceImpl
					.findById(Long.valueOf(purchasingRequisitionColId));
			prc.setConfigState(isConfiged);
			prc.setTransferQuantity(new BigDecimal(configedQuantity));
			purchasingRequisitionCollectionsServiceImpl.save(prc);
		}
		return 1;
	}

	@Override
	public Page<PurchasingRequisitionTransV> getGroupList(Page<PurchasingRequisitionTransV> page) {
		return purchasingRequisitionTransVEao.findAll(page, new HashMap<String, Object>());
	}

	@Override
	public List<PurchasingRequisitionTrans> getChaList(String purchasingOrgCode, String vendorCode, String companyCode) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_purchasingOrgCode", purchasingOrgCode);
		searchParams.put("EQ_vendorCode", vendorCode);
		if (StringUtils.isBlank(companyCode)) {
			searchParams.put("IS_companyCode", "NULL");
		} else {
			searchParams.put("EQ_companyCode", companyCode);
		}
		searchParams.put("GT_transferQuantity", 0);

		List<PurchasingRequisitionTrans> prtList = purchasingRequisitionTransDao.findAll(searchParams);
		return prtList == null ? new ArrayList<PurchasingRequisitionTrans>() : prtList;
	}

	@Override
	public Map<String, Object> getConfigList(Map<String, Object> searchParams, String userCode, String clientCode) {
		List<PurchasingRequisitionCollection> collectionList = new ArrayList<PurchasingRequisitionCollection>();
		searchParams.put("GT_canTransferQuantity", 0);
		searchParams.put("IS_configState", "NOTNULL");
		Map<String, Object> _searchMap = userAuthGroupLogic
				.buildAuthFieldParams(new UserAuthGroupParam(clientCode, userCode, PurchasingRequisitionCollection.class));
		searchParams.putAll(_searchMap);
		collectionList = purchasingRequisitionCollectionsServiceImpl.findAll(searchParams);

		List<MaterialMasterPriceDtl> mmpDtls = new ArrayList<MaterialMasterPriceDtl>();
		new ArrayList<PriceAffirmDetail>();
		new ArrayList<PurchasingRequisitionTrans>();
		List<Long> idList = new ArrayList<Long>();
		List<String> materialCodes = new ArrayList<String>();
		// 判断是否存在供应商信息
		for (PurchasingRequisitionCollection prc : collectionList) {
			if (StringUtils.isNotBlank(prc.getMaterialCode())) {
				materialCodes.add(prc.getMaterialCode());
			}
		}
		// 获取价格主数据
		Map<String, Object> searchMap = new HashMap<String, Object>();
		if (mmpDtls == null || mmpDtls.size() < 1) {
			searchMap.clear();
			searchMap = userAuthGroupLogic
					.buildAuthFieldParamsDetail(new UserAuthGroupParam(clientCode, userCode, MaterialMasterPrice.class));
			// String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new
			// Date());
			Calendar currDate = Calendar.getInstance();
			currDate.set(Calendar.HOUR, 0);
			currDate.set(Calendar.MINUTE, 0);
			currDate.set(Calendar.MILLISECOND, 0);

			searchMap.put("LE_effectiveDate", currDate);
			searchMap.put("GE_expirationDate", currDate);
			searchMap.put("IN_materialMasterPrice_materialCode", materialCodes);
			mmpDtls = materialMasterPriceDtlDao.findAll(searchMap);
		}
		// 判断是否存在供应商信息
		for (PurchasingRequisitionCollection prc : collectionList) {
			// 获取供应商信息
			// List<PurchasingRequisitionTrans> list
			List<PurchasingRequisitionTrans> rsList = new ArrayList<PurchasingRequisitionTrans>();
			new HashMap<String, PurchasingRequisitionTrans>();

			try {
				if (StringUtils.isNotBlank(prc.getMaterialCode())) {
					// searchMap.put("EQ_materialMasterPrice_materialCode",
					// materialCode);
					for (MaterialMasterPriceDtl mmpd : mmpDtls) {
						if (prc.getMaterialCode().equals(mmpd.getMaterialMasterPrice().getMaterialCode())) {
							mmpd.getMaterialMasterPrice();
							PurchasingRequisitionTrans prt = new PurchasingRequisitionTrans();
							rsList.add(prt);
							break;
						}

					}
				} else {

					PurchasingRequisitionTrans prt = new PurchasingRequisitionTrans();
					rsList.add(prt);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (rsList.size() == 0) {
				idList.add(prc.getPurchasingRequisitionColId());
			}
		}
		if (idList.size() > 0) {
			// 过滤不存在供应商信息的数据
			searchParams.put("NOTIN_purchasingRequisitionColId", idList);
		}
		return searchParams;
	}
}
