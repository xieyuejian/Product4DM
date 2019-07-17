package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.srm.masterdata.api.DataDictClient;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.entity.DataDict;
import com.huiju.srm.masterdata.entity.MaterialPlant;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.sourcing.dao.MaterialMasterPriceDtlDao;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialMasterPrice;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialUnitConversionDtl;

/**
 * 查询价格主数据视图
 * 
 * @author zhuang.jq
 *
 */
@Service
public class StdMaterialMasterPriceOrderDtlViewServiceImpl extends JpaServiceImpl<MaterialMasterPriceOrderDtlView, Long>
		implements StdMaterialMasterPriceOrderDtlViewService {
	@Autowired(required = false)
	protected MaterialMasterPriceDtlDao materialMasterPriceDtlDao;
	@Autowired(required = false)
	protected MaterialPlantClient materialPlantLogic;
	@Autowired(required = false)
	protected MaterialClient materialLogic;
	@Autowired(required = false)
	protected DataDictClient dataDictClient;

	@Override
	public Page<MaterialMasterPriceOrderDtlView> findPage(Page<MaterialMasterPriceDtl> mmdPage, Page<MaterialMasterPriceOrderDtlView> page,
			Map<String, Object> searchParams) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		Calendar z = Calendar.getInstance();
		searchParams.put("LE_effectiveDate", c);
		z.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
		searchParams.put("GT_expirationDate", z);
		searchParams.put("NE_materialMasterPrice_isFrozen", "A001");
		mmdPage = materialMasterPriceDtlDao.findAll(mmdPage, searchParams);
		buildViewPage(mmdPage, page);
		return page;
	}

	protected void buildViewPage(Page<MaterialMasterPriceDtl> mmdPage, Page<MaterialMasterPriceOrderDtlView> page) {
		long totalCount = mmdPage.getTotalCount();
		page.setTotalCount(totalCount);
		List<MaterialMasterPriceDtl> mmds = mmdPage.getRecords();
		List<MaterialMasterPriceOrderDtlView> list = new ArrayList<MaterialMasterPriceOrderDtlView>();

		Map<String, Object> dataDictMap = new HashMap<String, Object>();
		dataDictMap.put("EQ_groupCode", "recordtype");
		List<DataDict> dataDicts = dataDictClient.findAll(new FeignParam<DataDict>(dataDictMap));
		dataDictMap.clear();
		if (dataDicts != null && dataDicts.size() > 0) {
			for (DataDict dataDict : dataDicts) {
				dataDictMap.put(dataDict.getItemCode(), dataDict.getItemName());
			}
		}
		if (mmds != null && mmds.size() > 0) {

			// 物料工厂
			Map<String, MaterialPlant> mpMaps = new HashMap<String, MaterialPlant>();

			for (MaterialMasterPriceDtl dtl : mmds) {
				MaterialMasterPrice mmp = dtl.getMaterialMasterPrice();
				List<MaterialUnitConversionDtl> materialUnitConversionDtls = dtl.getMaterialUnitConversionDtls();
				List<MaterialLadderPriceDtl> materialLadderPriceDtls = dtl.getMaterialLadderPriceDtls();
				MaterialMasterPriceOrderDtlView view = new MaterialMasterPriceOrderDtlView();
				view.setMaterialCode(mmp.getMaterialCode());
				view.setMaterialName(mmp.getMaterialName());
				view.setVendorCode(mmp.getVendorCode());
				view.setVendorErpCode(mmp.getVendorErpCode());
				view.setVendorName(mmp.getVendorName());
				view.setRecordType(mmp.getRecordType());
				view.setRecordTypeName((String) dataDictMap.get(mmp.getRecordType()));
				view.setPlantCode(mmp.getPlantCode());
				view.setPlantName(mmp.getPlantName());
				view.setPriceUnit(dtl.getPriceUnit());
				view.setMaterialMasterPriceDtlId(dtl.getMaterialMasterPriceDtlId());
				view.setMaterialMasterPriceId(mmp.getMaterialMasterPriceId());
				view.setExcessDeliveryLimit(dtl.getExcessDeliveryLimit());
				view.setDeliveryLimit(dtl.getDeliveryLimit());
				view.setTaxRateCode(dtl.getTaxRateCode());
				view.setPlannedDays(dtl.getPlannedDays() == null ? BigDecimal.ZERO : BigDecimal.valueOf(dtl.getPlannedDays()));
				view.setCurrencyCode(dtl.getCurrencyCode());
				view.setPurchasingOrgCode(mmp.getPurchasingOrgCode());
				view.setEffectiveDate(dtl.getEffectiveDate());
				view.setExpirationDate(dtl.getExpirationDate());
				if (materialUnitConversionDtls != null && materialUnitConversionDtls.size() > 0) {
					MaterialUnitConversionDtl unitConversion = materialUnitConversionDtls.get(0);
					view.setMaterialUnitConversionDtlId(unitConversion.getMaterialUnitConversionDtlId());
					view.setElementaryUnit(unitConversion.getElementaryUnit());
					view.setElementaryUnitCode(unitConversion.getElementaryUnitCode());
					view.setOrderElementaryUnit(unitConversion.getOrderElementaryUnit());
					view.setOrderElementaryUnitCode(unitConversion.getOrderElementaryUnitCode());
					view.setOrderPricingUnit(unitConversion.getOrderPricingUnit());
					view.setOrderPricingUnitCode(unitConversion.getOrderPricingUnitCode());
					view.setPricingUnit(unitConversion.getPricingUnit());
					view.setPricingUnitCode(unitConversion.getPricingUnitCode());
				}
				if (materialLadderPriceDtls != null && materialLadderPriceDtls.size() > 0) {
					BigDecimal startNum = materialLadderPriceDtls.get(0).getStartNum();
					BigDecimal nonTaxPrice = materialLadderPriceDtls.get(0).getNonTaxPrice();
					Long materialLadderPriceDtlId = materialLadderPriceDtls.get(0).getMaterialLadderPriceDtlId();
					for (MaterialLadderPriceDtl ladderPriceDtl : materialLadderPriceDtls) {
						if (ladderPriceDtl.getStartNum().compareTo(startNum) < 0) {
							nonTaxPrice = ladderPriceDtl.getNonTaxPrice();
							startNum = ladderPriceDtl.getStartNum();
							materialLadderPriceDtlId = ladderPriceDtl.getMaterialLadderPriceDtlId();
						}
					}
					view.setNonTaxPrice(nonTaxPrice);
					view.setMaterialLadderPriceDtlId(materialLadderPriceDtlId);
				}

				if (mmp.getPlantCode() != null) {
					String key = mmp.getMaterialCode() + "_" + mmp.getPlantCode();

					MaterialPlant mp = null;
					if (mpMaps.containsKey(key)) {
						mp = mpMaps.get(key);
					} else {
						Map<String, Object> searchParams = new HashMap<>();
						searchParams.put("EQ_materialCode", mmp.getMaterialCode());
						searchParams.put("EQ_plantCode", mmp.getPlantCode());
						FeignParam<MaterialPlant> param = new FeignParam<MaterialPlant>(searchParams);
						mp = materialPlantLogic.findOne(param);
					}

					if (mp != null) {
						view.setQualityCheck(mp.getQualityCheck());
						view.setStockLocationCode(mp.getStockLocation() == null ? null : mp.getStockLocation().getStockLocationCode());
						view.setStorLocCode(mp.getStockLocation() == null ? null : mp.getStockLocation().getStockLocationCode());
						view.setJitFlag(mp.getMaterial() == null ? null : mp.getMaterial().getJitFlag());
						view.setStockLocationCode(mp.getStorlocCode());
						view.setStorLocCode(mp.getStorlocCode());
						view.setQualityCheck(mp.getQualityCheck());
						mpMaps.put(key, mp);
					}
				}

				list.add(view);
			}
			page.setRecords(list);
		}
	}

}
