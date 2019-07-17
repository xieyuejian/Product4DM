package com.huiju.srm.purchasing.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.interaction.api.InteractionClient;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.purchasing.dao.PurchasingRequisitionCollectionDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;

/**
 * 采购申请明细实现类
 * 
 * @author bairx
 *
 */
@Service
public class StdPurchasingRequisitionCollectionServiceImpl extends JpaServiceImpl<PurchasingRequisitionCollection, Long>
		implements StdPurchasingRequisitionCollectionService {
	@Autowired
	protected PurchasingRequisitionCollectionDao purchasingRequisitionCollectionDao;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	protected final String BILLTYPE = "PR";
	@Autowired
	protected InteractionClient interactLogic;

	/**
	 * 从SAP导入数据
	 * 
	 * @param params 参数
	 * @return 采购申请明细集合
	 */
	@Override
	public int importFromSap(String[] params) {
		List<PurchasingRequisitionCollection> resultList = new ArrayList<PurchasingRequisitionCollection>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject paramObj = new JSONObject();
		try {

			String materialCode = "";
			if (StringUtils.isNotBlank(params[0])) {
				materialCode = params[0].trim();
			}

			String plantCode = "";
			if (StringUtils.isNotBlank(params[1])) {
				plantCode = params[1].trim();
			}

			paramObj.put("materialCode", materialCode);// 物料编码
			paramObj.put("plantCode", plantCode);// 工厂编码

			if (StringUtils.isNotBlank(params[2])) {
				String demandDateFrom = "";
				String demandDateTo = "";

				if (StringUtils.isNotBlank(params[2])) {
					demandDateFrom = params[2].substring(0, 10).trim();
				}

				if (StringUtils.isNotBlank(params[3])) {
					demandDateTo = params[3].substring(0, 10).trim();
				}

				paramObj.put("forecastMainStartDate", demandDateFrom);// 需求起始日期
				paramObj.put("forecastMainEndDate", demandDateTo);// 需求结束日期
			}

			Map<String, String> param = new HashMap<String, String>();
			// 业务场景编码
			param.put("scenarioCode", "getPurchaseApplyNew");
			// JSON数据
			param.put("json", DataUtils.toJson(paramObj));
			// 调用RESTFul接口并获取返回值
			// String json = HttpClientUtils.post(interfaceURL, param);

			String json = interactLogic.invoke("getPurchaseApplyNew", paramObj.toJSONString());
			JSONObject jsonMap = JSONObject.parseObject(json);
			JSONObject dataMap = jsonMap.getJSONObject("data");
			JSONObject returnMap = dataMap.getJSONObject("EtReturn").getJSONObject("item");

			if (!"S".equals(returnMap.getString("Type"))) {
				return 0;
			}

			try {
				JSONArray itemArr = dataMap.getJSONObject("ItTosrm").getJSONArray("item");
				System.out.println(itemArr);

				System.out.println(itemArr.size());
				if (itemArr != null && itemArr.size() > 0) {
					for (int i = 0; i < itemArr.size(); i++) {

						JSONObject dataItem = itemArr.getJSONObject(i);
						PurchasingRequisitionCollection pr = new PurchasingRequisitionCollection();
//						pr.setPurchasingRequisitionNo(CommonUtil.removeZero(dataItem.getString("Banfn")));//
						// 采购申请单号
						pr.setRowNo(dataItem.getLong("Bnfpo"));// 行号
						pr.setPlantCode(dataItem.getString("Werks"));// 工厂编码
						pr.setMaterialCode(dataItem.getString("Matnr"));// 物料编码
						pr.setMaterialName(dataItem.getString("Txz01"));// 物料名称
						pr.setUnitCode(dataItem.getString("Meins"));// 单位编码
						pr.setPurchasingGroupCode(dataItem.getString("Ekgrp"));// 采购组编码
						if (StringUtils.isNotBlank(dataItem.getString("Lfdat"))) {
							// 日期转换
							Date date = sdf.parse(dataItem.getString("Lfdat"));
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							pr.setDemandDate(calendar);// 需求日期
						}
						pr.setQuantityDemanded(dataItem.getBigDecimal("Obmng"));// 需求数量
						pr.setSource("2");// 数据来源
						pr.setCreateTime(Calendar.getInstance());
						resultList.add(pr);

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (0 < resultList.size()) {
				saveOrUpdateData(resultList);
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 1;
	}

	public void saveOrUpdateData(List<PurchasingRequisitionCollection> resultList) {
		Map<String, Object> params = new HashMap<String, Object>();
		PurchasingRequisitionCollection oldPrc = new PurchasingRequisitionCollection();
		for (PurchasingRequisitionCollection prc : resultList) {
			params.clear();
			params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
			params.put("EQ_rowNo", prc.getRowNo());
			params.put("EQ_source", "2");
			oldPrc = purchasingRequisitionCollectionDao.findOne(params);
			if (oldPrc == null) {
				params.clear();
				params.put("EQ_plantCode", prc.getPlantCode());
				params.clear();
				params.put("EQ_unitCode", prc.getUnitCode());
				params.clear();
				params.put("EQ_purchasingGroupCode", prc.getPurchasingGroupCode());
				purchasingRequisitionCollectionDao.save(prc);
			} else {
				oldPrc.setMaterialCode(prc.getMaterialCode());
				oldPrc.setMaterialName(prc.getMaterialName());
				oldPrc.setPlantCode(prc.getPlantCode());
				oldPrc.setPlantName(prc.getPlantName());
				oldPrc.setUnitCode(prc.getUnitCode());
				oldPrc.setUnitName(prc.getUnitName());
				oldPrc.setPurchasingGroupCode(prc.getPurchasingGroupCode());
				oldPrc.setPurchasingGroupName(prc.getPurchasingGroupName());
				oldPrc.setDemandDate(prc.getDemandDate());
				oldPrc.setQuantityDemanded(prc.getQuantityDemanded());
				oldPrc.setSource(prc.getSource());
				purchasingRequisitionCollectionDao.save(oldPrc);
			}
		}
	}

	/**
	 * 根据采购申请单号行号移除对应的采购申请明细归集
	 * 
	 * @param purchasingRequisitionNo 采购申请单号
	 * @param rowNo 行号
	 */
	@Override
	public void removeByNo(String purchasingRequisitionNo, Long rowNo) {
		Map<String, Object> searchMaps = new HashMap<String, Object>();
		searchMaps.put("EQ_purchasingRequisitionNo", purchasingRequisitionNo);
		if (rowNo != null) {
			searchMaps.put("EQ_rowNo", rowNo);
		}
		List<PurchasingRequisitionCollection> list = findAll(searchMaps);
		if (list != null) {
			deleteAll(list);
		}
	}

}
