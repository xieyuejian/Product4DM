package com.huiju.srm.purchasing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.json.Json;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTrans;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTransV;
//import com.huiju.srm.purchasing.entity.PurchasingRequisitionTransV;
import com.huiju.srm.purchasing.service.PurchasingRequisitionCollectionService;
import com.huiju.srm.purchasing.service.PurchasingRequisitionTransService;

/**
 * 采购申请转单controller
 * 
 * @author bairx
 *
 */
public class StdPurchasingRequisitionTransController extends CloudController {

	@Autowired
	protected PurchasingRequisitionCollectionService PurchasingRequisitionCollectionServiceImpl;

	@Autowired
	protected PurchasingRequisitionTransService purchasingRequisitionTransServiceImpl;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupClient;

	/**
	 * 查询采购申请分配列表
	 */
	@PostMapping("/configList")
	public String configList() {
		String materialCode = request.getParameter("materialCode1");
		String purchasingRequisitionColId = request.getParameter("purchasingRequisitionColId1");
		System.out.println("purchasingRequistionColId:" + purchasingRequisitionColId);
		List<PurchasingRequisitionTrans> rsList = getConfigList(materialCode, purchasingRequisitionColId);
		return renderJson(DataUtils.toJson(rsList, "purchasingRequisitionCollection"));
	}

	/*
	 * /** 获取待分配供应商数据
	 * @param materialCode
	 * @param purchasingRequisitionColId
	 * @return
	 */
	public List<PurchasingRequisitionTrans> getConfigList(String materialCode, String purchasingRequisitionColId) {
		List<PurchasingRequisitionTrans> rsList = new ArrayList<PurchasingRequisitionTrans>();
		rsList = purchasingRequisitionTransServiceImpl.getConfigList(purchasingRequisitionColId, materialCode, getClientCode(),
				getUserCode());
		return rsList;
	}

	/**
	 * <pre>
	 * 获取列表 / 查询数据
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/list")
	public Page<PurchasingRequisitionCollection> list() {
		Page<PurchasingRequisitionCollection> page = buildPage(PurchasingRequisitionCollection.class);
		Map<String, Object> searchParams = buildParams();
		page = PurchasingRequisitionCollectionServiceImpl.findAllWithoutAssociation(page, searchParams);
		return page;
	}

	/**
	 * <pre>
	 * 保存转单
	 * </pre>
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/save")
	public String save() {
		try {
			String paramsJson = request.getParameter("paramsJson");
			List<Map> transList = Json.parseArray(paramsJson, Map.class);
			Integer result = purchasingRequisitionTransServiceImpl.saveTrans(transList);
			// 操作成功的日志信息
			if (result == null || result.intValue() == 0 || result.intValue() == 2) {
				return dealJson(false, result);
			} else {
				return dealJson(true, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false);
		}
	}

	/**
	 * 获取转单后列表
	 * 
	 * @return
	 */
	@PostMapping("/configGroupList")
	public String configGroupList(int start, int limit) {
		Page<PurchasingRequisitionTransV> page = buildPage(PurchasingRequisitionTransV.class);
		page = purchasingRequisitionTransServiceImpl.getGroupList(page);
		// return renderJson(DataUtils.toJson(page));
		return DataUtils.toJson(page, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取转单后明细列表
	 * 
	 * @return
	 */
	@PostMapping("/getChaList")
	public String getChaList() {
		String purchasingOrgCode = request.getParameter("purchasingOrgCode");
		String vendorCode = request.getParameter("vendorCode");
		String companyCode = request.getParameter("companyCode");
		List<PurchasingRequisitionTrans> rsList = purchasingRequisitionTransServiceImpl.getChaList(purchasingOrgCode, vendorCode,
				companyCode);
		List<PurchasingRequisitionTrans> lists = new ArrayList<>();
		for (int i = 0; i < rsList.size(); i++) {
			if (rsList.get(i).getPurchasingRequisitionCollection() != null) {
				lists.add(rsList.get(i));
				// PurchasingRequisitionCollection purchasingRequisition=new
				// PurchasingRequisitionCollection();
				// rsList.get(i).setPurchasingRequisitionCollection(purchasingRequisition);
			}
		}
		return DataUtils.toJson(lists);
	}

}
