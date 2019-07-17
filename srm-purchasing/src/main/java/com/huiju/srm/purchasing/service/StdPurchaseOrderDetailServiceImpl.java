package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.data.jpa.utils.QueryUtils;
import com.huiju.srm.purchasing.dao.PurchaseDualUnitConversionDao;
import com.huiju.srm.purchasing.dao.PurchaseOrderPricingDao;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;
import com.huiju.srm.purchasing.util.PurchaseOrderConstant;

/**
 * 采购订单明细 业务
 * 
 * @author CWQ date 2016-08-04 18:25:46
 */
public class StdPurchaseOrderDetailServiceImpl extends JpaServiceImpl<PurchaseOrderDetail, Long> implements StdPurchaseOrderDetailService {

	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected PurchaseDualUnitConversionDao purchaseDualUnitConversionDao;
	@Autowired
	protected PurchaseOrderPricingDao purchaseOrderPricingDao;

	/**
	 * 获取订单明细最大剩余可收货量 可送数量 = (订单量 + 退货量 - 收货量) + (过货限额 * 订单量)
	 * 
	 * @return 最大剩余收货量
	 */
	public BigDecimal getMaxReceiveQty(PurchaseOrderDetail pod) {
		BigDecimal normalQty = getNormalReceiveQty(pod);

		// 订单量
		BigDecimal vendorQty = pod.getVendorQty() == null ? BigDecimal.ZERO : pod.getVendorQty();
		// 过货限额
		BigDecimal overDeliveryLimit = pod.getOverDeliveryLimit() == null ? BigDecimal.ZERO : pod.getOverDeliveryLimit();

		return normalQty.add(overDeliveryLimit.multiply(vendorQty));
	}

	/**
	 * 获取订单明细的正常可收货数量 可送数量 = 订单量 + 退货量 - 收货量
	 * 
	 * @return 正常的剩余收货量
	 */
	public BigDecimal getNormalReceiveQty(PurchaseOrderDetail pod) {

		if (pod == null) {
			throw new IllegalArgumentException("采购订单明细不能为空");
		}
		// 订单量
		BigDecimal vendorQty = pod.getVendorQty() == null ? BigDecimal.ZERO : pod.getVendorQty();
		// 退货量
		BigDecimal qtyQuit = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();
		// 收货量
		BigDecimal qtyArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
		// 可送数量 = 订单量 + 退货量 - 收货量
		return ((vendorQty.add(qtyQuit)).subtract(qtyArrive));

	}

	/**
	 * 获取采购订单明细的最大可退数量
	 * 
	 * @return 最大可退数量
	 */
	public BigDecimal getMaxRefundQty(PurchaseOrderDetail pod) {

		if (pod == null) {
			throw new IllegalArgumentException("采购订单明细不能为空");
		}
		// 收货量
		return pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
	}

	public List<PurchaseOrderDetail> findAllDtl(Map<String, Object> searchParams, int roleType) {
		try {
			List<PurchaseOrderDetail> purchaseOrderDetails = dao.findAllWithoutAssociation(searchParams, "srmRowids,ASC");

			PurchaseOrder order = purchaseOrderLogic
					.findById(Long.parseLong(searchParams.get("EQ_purchaseOrder_purchaseOrderId").toString()));
			// List<PurchaseOrderDetail> purchaseOrderDetails =
			// order.getPurchaseOrderDetails();
			Integer isVendorView = 1;

			// 供应商是否可以查看金额
			if (order != null && -1 < roleType) {
				String flag = purchaseOrderLogic.getPurchaseOrderControl(order, PurchaseOrderConstant.GROOVY_VENDORVIEW);
				if (!PurchaseOrderConstant.GROOVY_YES.equals(flag)) {
					isVendorView = 0;
				}
			}

			for (PurchaseOrderDetail detail : purchaseOrderDetails) {
				detail.setIsVendorView(isVendorView);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("EQ_purchaseOrderDetail_purchaseOrderDetailId", detail.getPurchaseOrderDetailId());
				List<PurchaseDualUnitConversion> unitConversions = purchaseDualUnitConversionDao.findAll(map);
				if (unitConversions != null && unitConversions.size() > 0) {
					detail.setUnitConversionInfo(DataUtils.toJson(unitConversions, "purchaseOrderDetail"));
					detail.setPricingUnit(unitConversions.get(0).getPricingUnit());
				}
				List<PurchaseOrderPricing> pricings = purchaseOrderPricingDao.findAll(map);
				if (null != pricings && pricings.size() > 0) {
					detail.setPricingInfo(DataUtils.toJson(pricings, "purchaseOrderDetail"));
				}
			}
			return purchaseOrderDetails;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 排序得到采购订单明细List
	 */
	public List<PurchaseOrderDetail> findAllAndSort(Map<String, Object> searchParams, Sort sort) {
		Specification<PurchaseOrderDetail> spec = QueryUtils.newSpecification(searchParams);
		return dao.findAll(spec, sort);
	}

	/**
	 * 重新计算订单明细的可送货数量
	 * 
	 * @param pod 订单明细
	 */
	@Override
	public PurchaseOrderDetail recountCanSendQty(PurchaseOrderDetail pod) {
		// 可送货量 = 订单量-收货量+退货量-在途量
		BigDecimal buyerQty = pod.getBuyerQty() == null ? BigDecimal.ZERO : pod.getBuyerQty();
		BigDecimal qtyArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
		BigDecimal qtyOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();
		BigDecimal qtyQuit = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();
		BigDecimal can = buyerQty.subtract(qtyArrive).add(qtyQuit).subtract(qtyOnline);
		if (BigDecimal.ZERO.compareTo(can) > 0) {
			can = BigDecimal.ZERO;
		}
		pod.setCanSendQty(can);
		// 添加标识 -- 订单可送货量达到下容差标识-->当可送货量<订单量*交货不足限度时，将该标识置为是

		BigDecimal num = buyerQty.multiply(pod.getShortDeliveryLimit().divide(new BigDecimal("100")));
		if (can.compareTo(num) <= 0) {
			pod.setIsAchieveLimit("Y");
		} else {
			pod.setIsAchieveLimit("N");
		}

		return save(pod);
	}

	/**
	 * 采购订单明细是否关闭
	 * 
	 * @param pod 采购订单明细
	 * @return true 关闭，false打开
	 */
	@Override
	public boolean canClosePod(PurchaseOrderDetail pod) {
		BigDecimal vendorQty = pod.getVendorQty() == null ? BigDecimal.ZERO : pod.getVendorQty();

		BigDecimal qtyArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
		BigDecimal qtyQuit = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();

		// 订单明细关闭条件：订单量*（1-交货不足限度）<=该订单明细的所有收货量-退货量
		BigDecimal accept_qty = qtyArrive.subtract(qtyQuit);

		boolean canClose = vendorQty.multiply(BigDecimal.ONE.subtract(pod.getShortDeliveryLimit().divide(new BigDecimal("100"))))
				.compareTo(accept_qty) <= 0;

		if (canClose && pod.getQtyOnline().compareTo(BigDecimal.ZERO) == 0) {
			return true;
		}

		return false;
	}

}