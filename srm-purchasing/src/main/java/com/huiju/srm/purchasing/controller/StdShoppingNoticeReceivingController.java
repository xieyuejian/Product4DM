package com.huiju.srm.purchasing.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.service.DeliveryDtlService;
import com.huiju.srm.purchasing.service.DeliveryService;
import com.huiju.srm.purchasing.service.DeliveryViewService;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.purchasing.service.ReceivingNoteService;

/**
 * 送货单点收Action
 * 
 * @author xufq
 *
 */
@Certificate(value = { "CP_receiving" }, requiredType = RequiredType.ONE)
public class StdShoppingNoticeReceivingController extends CloudController {

	@Autowired(required = false)
	protected ReceivingNoteService receivingNoteLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic; // 资源过滤远程接口注入
	@Autowired(required = false)
	protected DeliveryService deliveryLogic;// 送货远程接口注入
	@Autowired(required = false)
	protected DeliveryDtlService deliveryDtlLogic;// 送货明细远程接口注入
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;// 采购订单远程接口注入
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;// 采购订单远程接口注入
	@Autowired(required = false)
	protected DeliveryViewService deliveryViewLogic;// 送货点收接口注入

	protected String synStatus;
	protected String className;

	/**
	 * <pre>
	 * 获取列表 / 查询数据
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public Page<Delivery> list() {
		String systemRole = getRoleTypes();
		Page<Delivery> page = buildPage(Delivery.class);
		Map<String, Object> searchParams = buildParams();
		// 是按照送货单状态为:（待收货，收货中，同步成功）的才可以点收
		searchParams.put("IN_status", Arrays.asList(DeliveryState.WAIT, DeliveryState.RECEIVING));
		searchParams.put("EQ_synchronizeStatus", SrmSynStatus.SYNSUCCESS);

		if (SrmConstants.ROLETYPE_V.equals(systemRole)) { // 供应商
			// 供应商不能查看列表数据
			searchParams.put("EQ_deliveryId", 0l);

			// 只有采购才要根据资源组过滤
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			Map<String, Object> searchParams1 = userAuthGroupLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Delivery.class));
			searchParams.putAll(searchParams1);
		}

		return deliveryLogic.findAllWithoutAssociation(page, searchParams);
	}

	/**
	 * 返回编辑表单数据对象
	 * 
	 * @return
	 */
	@PostMapping(value = "/get")
	public Result get(Long id) {
		Delivery model = deliveryLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		return Result.success(DataUtils.toJson(model, "deliveryDtls"));
	}

	/**
	 * 查询送货明细
	 * 
	 * @return
	 */
	@PostMapping(value = "/finddeliverydtlall")
	public String findDeliveryDtlAll() {

		Map<String, Object> searchParams = buildParams();
		List<DeliveryDtl> list = deliveryDtlLogic.findAll(searchParams);
		// 默认点收数量 = 可送数量
		if (list != null && list.size() > 0) {
			for (DeliveryDtl dtl : list) {
				dtl.setAcceptQty(dtl.getDeliveryNumber().subtract(dtl.getReceivedQty()));
				dtl.setStorageLocationCode(dtl.getDelivery().getStorageLocationCode());
			}
		}

		return DataUtils.toJson(list, "delivery");
	}

	/**
	 * 送货单点收,可以分批收货，全部收完，送货单状态为已完成（只有发布状态的送货单才可以点收） 需要更新送货单的已收货数量，对应采购订单明细的已收货数量
	 * 
	 * @return
	 */
	@PostMapping(value = "/receiving")
	public Result receiving(@RequestBody JsonParam<Delivery> param) {
		Delivery model = param.getModel();
		Map<Boolean, String> result = new HashMap<Boolean, String>();
		result = receivingNoteLogic.receiving(model, getUserId(), getUserName());
		if (result.containsKey(true)) {
			// 增加日志
			receivingNoteLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单点收", SrmConstants.PERFORM_COLLECTPOINTS,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);
			return Result.success();
		} else if (result.size() > 0) {
			return Result.error(getText(result.get(false)));
		} else {
			return Result.error("");
		}

	}

	/**
	 * 关闭送货单
	 * 
	 * @return
	 */
	@PostMapping(value = "/close")
	public Result close(Long id) {
		receivingNoteLogic.close(id);
		receivingNoteLogic.addLog(getUserId(), getUserName(), id, "送货单关闭", SrmConstants.PERFORM_TOCLOSE, "", SrmConstants.PLATFORM_WEB);
		return Result.success();

	}

	// =====================================get/set======================================//
	public String getSynStatus() {
		return synStatus;
	}

	public void setSynStatus(String synStatus) {
		this.synStatus = synStatus;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
