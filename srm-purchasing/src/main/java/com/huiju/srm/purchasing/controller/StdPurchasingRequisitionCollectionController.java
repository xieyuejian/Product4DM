package com.huiju.srm.purchasing.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.PurchasingGroupClient;
import com.huiju.srm.masterdata.api.UnitClient;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;
import com.huiju.srm.purchasing.service.PurchasingRequisitionCollectionService;

/**
 * 采购申请明细controller
 * 
 * @author bairx
 *
 */
public class StdPurchasingRequisitionCollectionController extends CloudController {

	@Autowired
	protected PurchasingRequisitionCollectionService purchasingRequisitionCollectionServiceImpl;

	@Autowired
	protected PlantClient plantClient;
	@Autowired
	protected PurchasingGroupClient purchasingGroupClient;
	@Autowired
	protected UnitClient unitClient;

	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic;

	/**
	 * 获取列表/查询数据
	 * 
	 * @return
	 */
	@PostMapping("/list")
	public Page<PurchasingRequisitionCollection> list() {
		Page<PurchasingRequisitionCollection> page = buildPage(PurchasingRequisitionCollection.class);
		Map<String, Object> searchParams = buildParams();
		UserAuthGroupParam authGroupParam = new UserAuthGroupParam(getClientCode(), getUserCode(), PurchasingRequisitionCollection.class);
		Map<String, Object> searchMap = userAuthGroupLogic.buildAuthFieldParamsWithBlank(authGroupParam);
		searchParams.putAll(searchMap);
		page = purchasingRequisitionCollectionServiceImpl.findAllWithoutAssociation(page, searchParams);
		return page;

	}

	/**
	 * 返回编辑表单数据对象
	 * 
	 * @return
	 */
	@RequestMapping("/get")
	public Result get(Long id) {
		PurchasingRequisitionCollection model = purchasingRequisitionCollectionServiceImpl.findById(id);
		if (model == null) {
			return Result.error("");
		}

		return Result.success(DataUtils.toJson(model, new String[] { "purchasingRequisitionTrans" }));
	}

	/**
	 * 保存表单
	 * 
	 * @return
	 */
	@PostMapping("/save")
	public Result save(@RequestBody JsonParam<PurchasingRequisitionCollection> jsonParam) {
		PurchasingRequisitionCollection model = jsonParam.getModel();
		model = purchasingRequisitionCollectionServiceImpl.save(model);
		return Result.success(true);
	}

	/**
	 * 修改
	 * 
	 * @return
	 */
	@PostMapping("/update")
	public Result update(@RequestBody JsonParam<PurchasingRequisitionCollection> jsonParam) {
		PurchasingRequisitionCollection model = jsonParam.getModel();
		PurchasingRequisitionCollection pd = purchasingRequisitionCollectionServiceImpl.findById(model.getPurchasingRequisitionColId());
		if (pd == null) {
			return Result.error(getText("message.notexisted"));
		}
		purchasingRequisitionCollectionServiceImpl.save(model);
		return Result.success(true);
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	@PostMapping("/delete")
	public Result delete(List<Long> ids) {
		purchasingRequisitionCollectionServiceImpl.deleteById(ids);
		return Result.success(true);
	}

	/**
	 * 调用sap接口导入数据
	 * 
	 * @return
	 */
	@PostMapping("/importfromsap")
	public String importFromSap(String params) {
		String param[] = params.split(",");
		int result = purchasingRequisitionCollectionServiceImpl.importFromSap(param);
		System.out.println("result=" + result);
		if (result == 0) {
			return dealJson(false, getText("message.notExist"));
		} else if (result == 1) {
			return dealJson(true);
		}
		return dealJson(false);
	}

}
