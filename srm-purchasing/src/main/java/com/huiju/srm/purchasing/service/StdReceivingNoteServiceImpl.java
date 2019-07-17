package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.groovy.service.GroovyScriptInvokerService;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.bidding.util.PoiUtils;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.commons.utils.I18nUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.api.TaxRateClient;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.StockLocation;
import com.huiju.srm.masterdata.entity.TaxRate;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.sourcing.dao.MaterialLadderPriceDtlDao;
import com.huiju.srm.stock.entity.Instock;
import com.huiju.srm.stock.entity.InstockDtl;
import com.huiju.srm.stock.entity.InstockState;
import com.huiju.srm.stock.entity.Outstock;
import com.huiju.srm.stock.entity.OutstockDtl;
import com.huiju.srm.stock.entity.OutstockState;
import com.huiju.srm.stock.service.InstockService;
import com.huiju.srm.stock.service.OutstockService;

/**
 * 收货单接口实现类
 * 
 * @author zhuang.jq
 */
public class StdReceivingNoteServiceImpl extends JpaServiceImpl<ReceivingNote, Long> implements StdReceivingNoteService {

	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired
	protected BillSetServiceClient billSetLogic;
	@Autowired
	protected PurchaseDualUnitConversionService purchaseDualUnitConversionLogic;
	@Autowired
	protected DeliveryService deliveryLogic;// 送货远程接口注入
	@Autowired
	protected DeliveryDtlService deliveryDtlLogic;// 送货明细远程接口注入

	@Autowired
	protected SendScheduleService sendScheduleLogic;// 送货远程接口注入
	@Autowired
	protected SendScheduleDetailService sendScheduleDetailLogic;// 送货明细远程接口注入
	@Autowired
	protected TaxRateClient taxRateLogic;
	@Autowired
	protected StockLocationClient stockLocationLogic;
	@Autowired
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;
	@Autowired
	protected InstockService instockLogic;
	@Autowired
	protected OutstockService outstockLogic;
	@Autowired
	protected CensorQualityService censorQualityLogic;// 检验批
	@Autowired
	protected MaterialPlantClient materialPlantLogic;
	@Autowired
	protected PortalServiceClient portalDealDataLogic;
	@Autowired
	protected UserClient userLogic;
	@Autowired
	protected MaterialLadderPriceDtlDao materialLadderPriceDtlEao;
	@Autowired
	protected PlantClient plantLogic;

	/**
	 * 冲销
	 * 
	 * @param entity 收货实体
	 * @return 收货实体
	 */
	public ReceivingNote chargeOff(ReceivingNote entity) {
		try {
			entity.setCreateTime(Calendar.getInstance());

			ReceivingNote oldNote = dao.getById(entity.getReceivingNoteId());// 本次冲销的单据
			ReceivingNote newNote = new ReceivingNote(); // 本次冲销产生的新单据
			ReceivingNote rn = oldNote;
			if (entity.getCanChargeOffNum().compareTo(oldNote.getCanChargeOffNum()) > 0) {
				throw new Exception("【凭证信息" + oldNote.getMaterialCertificateYear() + "-" + oldNote.getMaterialCertificateCode() + "-"
						+ oldNote.getMaterialCertificateItem() + "】的可冲销数量为 " + oldNote.getCanChargeOffNum().doubleValue() + " 小于本次冲销数量 "
						+ entity.getCanChargeOffNum().doubleValue() + "，不允许进行冲销");
			}
			String certificateInfo = oldNote.getMaterialCertificateYear() + "_" + oldNote.getMaterialCertificateCode() + "_"
					+ oldNote.getMaterialCertificateItem();
			String oldCertificateInfo = oldNote.getOmaterialCertificateYear() + "_" + oldNote.getOmaterialCertificateCode() + "_"
					+ oldNote.getOmaterialCertificateItem();
			if (!certificateInfo.equals(oldCertificateInfo)) {
				Map<String, Object> maps = new HashMap<String, Object>();
				maps.put("EQ_materialCertificateYear", entity.getOmaterialCertificateYear());
				maps.put("EQ_materialCertificateItem", entity.getOmaterialCertificateItem());
				maps.put("EQ_materialCertificateCode", entity.getOmaterialCertificateCode());

				List<ReceivingNote> rns = dao.findAll(maps);// 第一次收货产生的原始单据
				if (rns == null || rns.size() == 0) {
					throw new Exception("SRM 不存在【凭证信息 " + oldNote.getOmaterialCertificateYear() + "-"
							+ oldNote.getOmaterialCertificateCode() + "-" + oldNote.getOmaterialCertificateItem() + "】的记录");
				}
				rn = rns.get(0);
				if (rn.getAcceptReturnFlag().equals(oldNote.getAcceptReturnFlag())
						&& entity.getCanChargeOffNum().compareTo(rn.getCanChargeOffNum()) > 0) {
					throw new Exception("【凭证信息 " + rn.getMaterialCertificateYear() + "-" + rn.getMaterialCertificateCode() + "-"
							+ rn.getMaterialCertificateItem() + "】的可冲销量为" + rn.getCanChargeOffNum().doubleValue() + " 小于本次冲销数量 "
							+ entity.getCanChargeOffNum() + "，不允许进行冲销");
				}
			}

			// ClassReflection.reflectionAttr(oldNote,newNote);
			newNote = CommonUtil.copyObject(oldNote);
			// BeanUtils.copyProperties(newNote, oldNote);
			// 编码生成
			String shdNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_SHD);
			if (shdNo == null) {
				throw new Exception("####收货单保存失败，无法生成单据号.####");
			}
			newNote.setReceivingNoteNo(shdNo);
			// 主键置空
			newNote.setReceivingNoteId(null);
			// 收退货标识1收货2退货
			if (oldNote.getAcceptReturnFlag().equals(101l)) {
				newNote.setAcceptReturnFlag(102l);
			} else {
				newNote.setAcceptReturnFlag(101l);
			}

			newNote.setInvoiceQty(BigDecimal.ZERO);// 可对账数量（定价单位）
			newNote.setReconciliableQty(BigDecimal.ZERO); // 可对账数量（订单单位）
			newNote.setCanChargeOffNum(entity.getCanChargeOffNum());// 可冲销数量（订单单位）
			newNote.setQtyReceive(entity.getCanChargeOffNum());
			newNote.setFixPriceQty(
					entity.getCanChargeOffNum().multiply(oldNote.getExchangeRate() == null ? BigDecimal.ONE : oldNote.getExchangeRate())); // 定价数量（订单单位）
			newNote.setStockQty(entity.getCanChargeOffNum());
			newNote.setCanChargeOffNum(entity.getCanChargeOffNum());

			// 对账日期，凭证日期，过账日期
			newNote.setCertificateDate(Calendar.getInstance());// 凭证日期
			newNote.setPostingDate(Calendar.getInstance());// 过账日期
			// 生成新的物料凭证号
			// 收货单的物料凭证号 materialCertificateCode
			String mcc = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_MCC);
			if (mcc == null) {
				throw new Exception("####收货单保存失败，无法生成物料凭证号.####");
			}
			newNote.setMaterialCertificateCode(mcc);
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			newNote.setMaterialCertificateYear(year + "");
			// Todo凭证行号
			newNote.setMaterialCertificateItem("1");
			// 凭证年度-凭证编码-凭证行号
			newNote.setMaterialCertificate(newNote.getMaterialCertificateYear() + "-" + newNote.getMaterialCertificateCode() + "-"
					+ newNote.getMaterialCertificateItem());

			// 创建修改信息
			newNote.setCreateTime(Calendar.getInstance());

			// 计算含税未税总金额、税额
			newNote.setAmountnoTax(newNote.getPrice().multiply(newNote.getCanChargeOffNum()));
			newNote.setTotalTax(newNote.getAmountnoTax().multiply(newNote.getTaxRate()));
			newNote.setTotalAmountAndTax(newNote.getAmountnoTax().add(newNote.getTotalTax()));

			newNote = dao.save(newNote);

			// SRM 进行冲销时，需要增加如下逻辑判断：判断被冲销 B 记录“原凭证年度+原凭证编号+原凭
			// 证行号”与“现凭证年度+现凭证编号+现凭证行号”是否一致：
			// 1) 如果凭证信息一致：判断“SRM 本次冲销数量”是否小于被冲销 B 记录“可冲销数量”，
			// ◆ 如果大于：不允许在 SRM 系统对该记录进行冲销，并返回“【凭证信息 XX-XX-XX】的
			// 可冲销数量为 XX 小于本次冲销数量 XX，不允许进行冲销”提示消息
			// ◆ 如果小于等于：则允许在 SRM 进行冲销
			// 2) 如果凭证信息不一致：先判断“SRM 本次冲销数量”是否小于被冲销 B 记录“可冲销数量
			// ◆ 如果大于，不允许在 SRM 系统对该记录进行冲销，并返回“【凭证信息 XX-XX-XX】的
			// 可冲销数量为 XX 小于本次冲销数量 XX，不允许进行冲销”提示消息
			// ◆ 如果小于等于，则根据被冲销 B 记录的“原凭证年度+原凭证编号+原凭证行号”查找
			// 现凭证年度+现凭证编号+现凭证行号”一致的另一条 A 记录：
			//  如果找到另一条 A 记录：判断被冲销 B 记录的“收退货标识”与另一条 A 记录的
			// 收退货标识”是否一致：
			// ✓ 如果一致：校验“SRM 本次冲销数量”是否小于 A 记录的“可对账数量”，如
			// 果小于则允许在 SRM 进行冲销，否则不允许在 SRM 系统对被冲销记录进行冲
			// 销，并返回“【凭证信息 XX-XX-XX】的实际可冲销数量（收货单可冲销量）为
			// SRM_Ent_需求规格书_发票对账
			// XX 小于本次冲销数量 XX，不允许进行冲销”。
			// ✓ 如果不一致：则允许在 SRM 进行冲销
			//  如果找不到另一条 A 记录： 则不允许在 SRM 系统对该收货记录进行冲销，并返回
			// SRM 不存在【凭证信息 XX-XX-XX】的记录”提示消息
			// 5. SRM 生成冲销数据时，判断被冲销 B 记录“原凭证年度+原凭证编号+原凭证行号”与“现凭证
			// 年度+现凭证编号+现凭证行号”是否一致，如果凭证信息一致。需要回置被冲销 B 记录的如下
			// 字段：
			// 1) 可冲销数量（订单单位）=原可冲销数量（订单单位）- 新生成记录可冲销数量（订单单位）
			// 2) 可对账数量（订单单位）=原可对账数量（订单单位）- 新生成记录可对账数量（订单单位）
			// 3) 可对账数量（定价单位）=原可对账数量（定价单位）- 新生成记录可对账数量（定价单
			// 位）
			// 6. SRM 生成冲销数据时，判断被冲销 B 记录“原凭证年度+原凭证编号+原凭证行号”与“现凭证
			// 年度+现凭证编号+现凭证行号”是否一致，如果凭证信息不一致，需要回置被冲销 B 记录的如下字段：
			// 1) 先回执被冲销 B 记录“可冲销数量”
			// ◆ 可冲销数量（订单单位）=原可冲销数量（订单单位）- 新生成记录可冲销数量（订单单位）
			// 2) 再根据被冲销记录“原凭证年度+原凭证编号+原凭证行号”查找“现凭证年度+现凭证编号现凭证行号”一致的另一条 A 记录后，
			// 判断 2 条记录的收退货标识是否一致：
			// ◆ 如果收退货标识一致：则需要回置找到的另一条记录的相关字段，回执逻辑如下：
			// ◼ 可冲销数量（订单单位）=原可冲销数量（订单单位）- 新生成记录可冲销数量（订单单位）
			// ◼ 可对账数量（订单单位）=原可对账数量（订单单位）- 新生成记录可对账数量（订 单单位）
			// ◼ 可对账数量（定价单位）=原可对账数量（定价单位）- 新生成记录可对账数量（定 价单位）
			// ◆ 如果收退货标识不一致：
			// ◼ 可冲销数量（订单单位）=原可冲销数量（订单单位）+ 新生成记录可冲销数量订单单位）
			// ◼ 可对账数量（订单单位）=原可对账数量（订单单位）+ 新生成记录可对账数量（订单单位）
			// ◼ 可对账数量（定价单位）=原可对账数量（定价单位）+ 新生成记录可对账数量（定价单位）"

			if (certificateInfo.equals(oldCertificateInfo)) {
				oldNote.setCanChargeOffNum(oldNote.getCanChargeOffNum().subtract(entity.getCanChargeOffNum())); // 可冲销数量
				if (oldNote.getAmountnoTax().compareTo(BigDecimal.ZERO) > 0 && oldNote.getReconciliableQty() != null) {
					oldNote.setReconciliableQty(oldNote.getReconciliableQty().subtract(newNote.getCanChargeOffNum())); // 可对账数量（订单单位）
					oldNote.setInvoiceQty(oldNote.getInvoiceQty().subtract(newNote.getCanChargeOffNum()
							.multiply(oldNote.getExchangeRate() == null ? BigDecimal.ONE : oldNote.getExchangeRate()))); // 可对账数量（定价单位）
				}
				oldNote.setModifyTime(Calendar.getInstance());
				dao.save(oldNote);
			} else {
				oldNote.setCanChargeOffNum(oldNote.getCanChargeOffNum().subtract(entity.getCanChargeOffNum())); // 可冲销数量
				if (rn.getAcceptReturnFlag() == oldNote.getAcceptReturnFlag()) {
					rn.setCanChargeOffNum(rn.getCanChargeOffNum().subtract(entity.getCanChargeOffNum())); // 可冲销数量
					if (oldNote.getAmountnoTax().compareTo(BigDecimal.ZERO) > 0) {
						rn.setInvoiceQty(rn.getInvoiceQty().subtract(entity.getCanChargeOffNum().multiply(oldNote.getExchangeRate()))); // 可对账数量（定价单位）
						rn.setReconciliableQty(rn.getReconciliableQty().subtract(entity.getCanChargeOffNum())); // 可对账数量（订单单位）
					}
				} else {
					rn.setCanChargeOffNum(rn.getCanChargeOffNum().add(entity.getCanChargeOffNum())); // 可冲销数量
					if (oldNote.getAmountnoTax().compareTo(BigDecimal.ZERO) > 0) {
						rn.setInvoiceQty(rn.getInvoiceQty().add(newNote.getCanChargeOffNum().multiply(oldNote.getExchangeRate()))); // 可对账数量（定价单位）
						rn.setReconciliableQty(rn.getReconciliableQty().add(entity.getCanChargeOffNum())); // 可对账数量（订单单位）
					}
				}
				dao.save(oldNote);
				dao.save(rn);
			}

			PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailLogic.findById(oldNote.getPurchaseOrderDetailId());
			// 更新执行中订单明细
			BigDecimal qty_quit = null;// 退货量
			BigDecimal qty_arrive = null;// 收货量
			if (oldNote.getAcceptReturnFlag().equals(101l)) {// 收退货标识101收货201退货
				// 冲销收货即退货，增加退货量
				qty_quit = purchaseOrderDetail.getQtyQuit().add(entity.getCanChargeOffNum());
				purchaseOrderDetail.setQtyQuit(qty_quit);
			} else {
				// 冲销退货即收货，增加收货量
				qty_arrive = purchaseOrderDetail.getQtyArrive().add(entity.getCanChargeOffNum());
				purchaseOrderDetail.setQtyArrive(qty_arrive);
				// qty_quit =
				// purchaseOrderDetail.getQtyQuit().subtract(entity.getCanChargeOffNum());
				// purchaseOrderDetail.setQtyQuit(qty_quit);
			}

			purchaseOrderDetail.getBuyerQty();
			qty_arrive = purchaseOrderDetail.getQtyArrive();
			qty_quit = purchaseOrderDetail.getQtyQuit();
			qty_arrive.subtract(qty_quit);

			purchaseOrderDetail = canCloseOrder(purchaseOrderDetail);

			purchaseOrderDetailLogic.save(purchaseOrderDetail);

			// 判断采购订单细单数量是否收货完毕，可否关闭细单 ---20150922 xufq
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_purchaseOrderNo", entity.getPurchaseOrderNo());
			// 查找细单，取出细单的 数量（先找主单Id）
			List<PurchaseOrder> purchaseOrders = purchaseOrderLogic.findAll(searchParams);
			if (purchaseOrders == null || purchaseOrders.size() == 0) {
				searchParams.clear();
				searchParams.put("EQ_erpPurchaseOrderNo", entity.getErpPurchaseOrderNo());
				purchaseOrders = purchaseOrderLogic.findAll(searchParams);
			}
			PurchaseOrder purchaseOrder = purchaseOrders.get(0);
			// 取未删除的细单，判断细单是否关闭，如果细单全为关闭，主单状态置为“关闭”
			searchParams.clear();
			searchParams.put("EQ_deleteFlag", 0);// 未关闭
			searchParams.put("EQ_purchaseOrder_purchaseOrderId", purchaseOrder.getPurchaseOrderId());
			List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailLogic.findAll(searchParams);
			if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
				int count = 0;// 与 purchaseOrderDetail1.size()比较，相等关闭主单
				for (PurchaseOrderDetail pod : purchaseOrderDetails) {
					// 订单已关闭
					if (1 == pod.getCloseFlag()) {
						count++;
					}
				}
				// 全部明细都关闭了就把主单置为关闭
				if (count == purchaseOrderDetails.size()) {
					purchaseOrder.setPurchaseOrderState(PurchaseOrderState.CLOSE);
				} else {
					purchaseOrder.setPurchaseOrderState(PurchaseOrderState.OPEN);
				}
				purchaseOrderLogic.save(purchaseOrder);
			}
			// 产生退货出库单
			createOutStock(newNote);
			// 处理质检单
			dealCensorQuality(newNote, oldNote);

			if (purchaseOrderDetail == null || purchaseOrderDetail.getScheduleFlag() != 1) {
				dealSendSchedule(newNote, oldNote, purchaseOrderDetail.getPurchaseOrderDetailId());
			}
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 是否可关闭采购订单
	 * 
	 * @param pod 采购订单明细
	 * @return
	 */
	protected PurchaseOrderDetail canCloseOrder(PurchaseOrderDetail pod) {
		BigDecimal vendorQty = pod.getVendorQty() == null ? BigDecimal.ZERO : pod.getVendorQty();
		BigDecimal qtyArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();

		boolean canClose = vendorQty.multiply(BigDecimal.ONE.subtract(pod.getShortDeliveryLimit().divide(new BigDecimal("100"))))
				.compareTo(qtyArrive) <= 0;

		// 订单行明细删除标志为否，关闭标识为否、收货数量达到最低收货量（订单数量*（1-交货不足限度））时，并且订单的在途量为0时，系统行明细自动关闭（20190613）
		if (canClose && pod.getQtyOnline().compareTo(BigDecimal.ZERO) == 0) {

			pod.setCloseFlag(1);// 关闭订单
		} else {
			pod.setCloseFlag(0);// 关闭订单
		}
		return pod;
	}

	/**
	 * 冲销时，对检验批进行处理
	 * 
	 * @param newNote 收货单
	 */
	protected void dealCensorQuality(ReceivingNote newNote, ReceivingNote oldNote) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_voucherNo", oldNote.getOmaterialCertificateCode());
		searchParams.put("EQ_voucherProNo", oldNote.getOmaterialCertificateItem());
		searchParams.put("EQ_voucherYear", oldNote.getOmaterialCertificateYear());
		// ReceivingNote rn = dao.findOne(searchParams);
		// if (rn != null) {
		// searchParams.clear();
		// searchParams.put("EQ_receivingNoteNo", rn.getReceivingNoteNo());
		List<CensorQuality> cqs = censorQualityLogic.findAll(searchParams);
		if (cqs == null || cqs.size() == 0) {
			return;
		}
		CensorQuality cq = cqs.get(0);
		// }

		// 对收货进行冲销，产生退货
		if (newNote.getAcceptReturnFlag().equals(102l)) {// 退货
			// 1、根据原物料凭证号+原物料凭证行号 找对应的 收货单
			// 2、获取该收货单 质检状态，“待检” --> 送检数量=送检数量-本次冲销数量对应的SKU数量
			// 3、送检量 = 0 ，质检状态 “取消”
			/* && cq.getStatus().equals(CensorQualityState.TOCHECK) */
			if (cq != null) {
				BigDecimal censorQty = cq.getCensorQty().subtract(newNote.getCanChargeOffNum());
				cq.setCensorQty(censorQty);
				cq.setCanCheckQty(censorQty);
				if (censorQty.compareTo(BigDecimal.ZERO) == 0) {
					cq.setStatus(CensorQualityState.CANCEL);
				}
				censorQualityLogic.save(cq);
			}
		} else {
			// 对退货冲销，产生收货记录
			// 1、根据原物料凭证号+原物料凭证行号 找对应的 收货单
			// 2、获取该收货单 质检状态，“待检” --> 送检数量=送检数量+本次冲销数量对应的SKU数量;“取消” -->
			// 送检数量=送检数量+本次冲销数量对应的SKU数量 ，质检状态变 “待检”
			if (cq != null) {
				BigDecimal censorQty = cq.getCensorQty().add(newNote.getCanChargeOffNum());
				if (cq.getStatus().equals(CensorQualityState.TOCHECK)) {
					cq.setCensorQty(censorQty);
					cq.setCanCheckQty(censorQty);
				}
				if (cq.getStatus().equals(CensorQualityState.CANCEL)) {
					cq.setCensorQty(censorQty);
					cq.setCanCheckQty(censorQty);
					cq.setStatus(CensorQualityState.TOCHECK);
				}
				censorQualityLogic.save(cq);
			}
		}
	}

	/**
	 * 冲销时，对送货排程单进行处理
	 * 
	 * @param newNote 收货单
	 */
	protected void dealSendSchedule(ReceivingNote newNote, ReceivingNote oldNote, Long purchaseOrderDetailId) {
		if (oldNote.getShoppingNoticeDetailId() == null) {
			return;
		}
		DeliveryDtl deliveryDtl = deliveryDtlLogic.findById(oldNote.getShoppingNoticeDetailId());
		if (deliveryDtl.getSendDetailId() == null) {
			return;
		}
		SendScheduleDetail detail = sendScheduleDetailLogic.findById(deliveryDtl.getSendDetailId());
		if (detail == null) {
			return;
		}
		// 对收货进行冲销，产生退货
		if (newNote.getAcceptReturnFlag().equals(102l)) {// 退货
			// 1、退货数量 = 退货数量+本次冲销订单单位数量
			// 2、可送货数量 = 需求数量-在途数-已收货量+退货数量
			BigDecimal returnGoodsQty = newNote.getCanChargeOffNum()
					.add(detail.getReturnGoodsQty() == null ? BigDecimal.ZERO : detail.getReturnGoodsQty());
			BigDecimal canSendQty = detail.getScheduleQty().subtract(detail.getOnWayQty() == null ? BigDecimal.ZERO : detail.getOnWayQty())
					.subtract(detail.getReceiptQty() == null ? BigDecimal.ZERO : detail.getReceiptQty()).add(returnGoodsQty);
			detail.setReturnGoodsQty(returnGoodsQty);
			detail.setCanSendQty(canSendQty);
		} else {// 对退货冲销，产生收货记录 暂时没有需求说明
		}
		// 排程送货标识更新
		if (detail.getScheduleQty().compareTo(detail.getCanSendQty()) == 0) {// 如果可送货量等于需求量则为未送货
			detail.setSendFlag(0);
		} else if (detail.getScheduleQty().compareTo(detail.getCanSendQty()) > 0 && detail.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
																																			// 为部分送货
			detail.setSendFlag(1);
		} else if (detail.getCanSendQty().compareTo(BigDecimal.ZERO) == 0) {// 如果可送货量等于0为完全送货
			detail.setSendFlag(2);
		}
		sendScheduleDetailLogic.save(detail);
	}

	/**
	 * 进行收货处理
	 * 
	 * @param newNote 收货单
	 */
	@Override
	public Map<Boolean, String> receiving(Delivery model, Long userId, String userName) {
		Map<Boolean, String> result = new HashMap<Boolean, String>();
		try {
			String mcc = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_MCC);
			if (mcc == null) {

				throw new Exception("####收货单保存失败，无法生成物料凭证号.####");

			}
			Delivery delivery = deliveryLogic.findById(model.getDeliveryId());
			if (delivery == null) {
				throw new Exception("####找不到对应的送货单信息####");
			}
			if (DeliveryState.CLOSE.equals(delivery.getStatus())) {
				result.put(false, "shoppingNotice.message.closed");
				return result;
			}

			Map<String, Instock> instockMap = new HashMap<String, Instock>();
			Map<String, List<InstockDtl>> instockDtlMap = new HashMap<String, List<InstockDtl>>();

			List<Long> idList = new ArrayList<Long>();
			List<Long> podDtList = new ArrayList<Long>();
			List<Long> ssdIdList = new ArrayList<Long>();
			Map<Long, DeliveryDtl> dtlMap = new HashMap<Long, DeliveryDtl>();
			Map<Long, PurchaseOrderDetail> poDtlMap = new HashMap<Long, PurchaseOrderDetail>();
			Map<Long, SendScheduleDetail> ssDtlMap = new HashMap<Long, SendScheduleDetail>();
			for (int i = 0; i < model.getDeliveryDtls().size(); i++) {
				DeliveryDtl snd = model.getDeliveryDtls().get(i);
				idList.add(snd.getDeliveryDtlId());
				podDtList.add(snd.getOrderDetailId());
				if (snd.getSendDetailId() != null) {
					ssdIdList.add(snd.getSendDetailId());
				}
			}
			Map<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("IN_deliveryDtlId", idList);
			List<DeliveryDtl> dtlList = deliveryDtlLogic.findAll(searchMap);
			for (DeliveryDtl snd : dtlList) {
				dtlMap.put(snd.getDeliveryDtlId(), snd);
			}
			searchMap.clear();
			searchMap.put("IN_purchaseOrderDetailId", podDtList);
			List<PurchaseOrderDetail> poDtlList = purchaseOrderDetailLogic.findAll(searchMap);
			for (PurchaseOrderDetail pod : poDtlList) {
				// 已经关闭的采购订单，收货数量只能为0
				if (1 == pod.getCloseFlag()) {
					result.put(false, "shoppingNotice.message.orderClosedCannotReceiving");
					return result;
				}
				poDtlMap.put(pod.getPurchaseOrderDetailId(), pod);
			}
			if (ssdIdList != null && ssdIdList.size() > 0) {
				searchMap.clear();
				searchMap.put("IN_sendScheduleDetailId", ssdIdList);
				List<SendScheduleDetail> ssDtlList = sendScheduleDetailLogic.findAll(searchMap);
				for (SendScheduleDetail ssd : ssDtlList) {
					ssDtlMap.put(ssd.getSendScheduleDetailId(), ssd);
				}
			}

			for (int i = 0; i < model.getDeliveryDtls().size(); i++) {
				DeliveryDtl snd = model.getDeliveryDtls().get(i);
				if (snd.getAcceptQty().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				PurchaseOrderDetail pod = new PurchaseOrderDetail();
				if (snd != null) {
					pod = poDtlMap.get(snd.getOrderDetailId());
					// 已经关闭的采购订单，收货数量只能为0
					BigDecimal acceptQty = snd.getAcceptQty() == null ? BigDecimal.ZERO : snd.getAcceptQty();
					BigDecimal receivedQty = pod.getReceiveQty() == null ? BigDecimal.ZERO : pod.getReceiveQty();// 送货单中订单已收货量
					// 收货更新到货数量
					BigDecimal qtyOfArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
					pod.setQtyArrive(qtyOfArrive.add(acceptQty));// 到货数量累加
					pod.setReceiveQty(receivedQty.add(acceptQty));// 已收货数量累加
					// 有送货单才扣减在途数量
					BigDecimal qtyOfOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();// 采购明细的在途数量
					pod.setQtyOnline(qtyOfOnline.subtract(acceptQty));// 在途数量为原在途数量-点收量
					// 获取原来的送货明细
					DeliveryDtl oldDetail = dtlMap.get(snd.getDeliveryDtlId());
					// 累加送货单收货数量
					BigDecimal oldReceivedQty = oldDetail.getReceivedQty() == null ? BigDecimal.ZERO : oldDetail.getReceivedQty();
					snd.setReceivedQty(oldReceivedQty.add(snd.getAcceptQty()));// 已收数量为原已收数量+点收量
					// 累加点收数量
					snd.setAcceptQty(oldDetail.getAcceptQty().add(acceptQty));// 点收数量=原点收数量+点收数量
					if (snd.getReceivedQty().compareTo(snd.getDeliveryNumber()) > 0) {// 判断已收货数量是否大于送货数量
						result.put(false, "shoppingNotice.message.receivedQtyCanNotBiggerThenDeliverQty");
						return result;
					}

					pod = canCloseOrder(pod);

					purchaseOrderDetailLogic.recountCanSendQty(pod);

					deliveryDtlLogic.save(snd);
					snd.setAcceptQty(acceptQty);// 回置点收量
					snd.setErpPurchaseOrderNo(pod.getPurchaseOrder().getErpPurchaseOrderNo());
				}
				ReceivingNote rn = createReceivingNote(delivery, userId, userName, snd, mcc, i + 1);
				rn = dao.save(rn);
				if ("X".equals(pod.getStockType()) || "1".equals(pod.getStockType())) {// 库存类型质检，产生检验批
					CensorQuality censorQuality = createCensorQuality(rn, delivery, snd);
					censorQuality = censorQualityLogic.save(censorQuality);

				} else if ("A".equals(pod.getStockType()) || "2".equals(pod.getStockType())) {
					createStock(rn, delivery, snd, instockMap, instockDtlMap);
				}
				// 来源送货排程，回置其已收货量、在途量
				if (snd.getDataFrom().equals(2l)) {
					this.subtractSendscheduleDetail(snd, ssDtlMap);
				}
			}
			Map<String, String> stockLocationNames = new HashMap<String, String>();
			Set<String> stockLocationCodes = new HashSet<String>();
			for (Instock instock : instockMap.values()) {
				stockLocationCodes.add(instock.getStoreLocalCode());
			}
			if (stockLocationCodes != null && stockLocationCodes.size() > 0) {
				Map<String, Object> searchParams = new HashMap<String, Object>();
				searchParams.put("IN_stockLocationCode", stockLocationCodes);
				FeignParam<StockLocation> feignParamStock = new FeignParam<StockLocation>(searchParams);
				List<StockLocation> stockLocations = stockLocationLogic.findAll(feignParamStock);
				if (stockLocations != null && stockLocations.size() > 0) {
					for (StockLocation stockLocation : stockLocations) {
						stockLocationNames.put(stockLocation.getStockLocationCode(), stockLocation.getStockLocationName());
					}
				}
			}
			// 保存入库单
			for (Instock instock : instockMap.values()) {
				instock.setInStockNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_RKD));
				instock.setStoreLocalName(stockLocationNames.get(instock.getStoreLocalCode()));
				instockLogic.save(instock);
			}

			delivery = deliveryLogic.findById(model.getDeliveryId());
			delivery.setStatus(DeliveryState.RECEIVING);// 送货单状态置为收货中
			dealPurchaseOrder(delivery);
			dealShoppingNotice(delivery);
			result.put(true, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void createStock(ReceivingNote rn, Delivery delivery, DeliveryDtl dd, Map<String, Instock> instockMap,
			Map<String, List<InstockDtl>> instockDtlMap) {

		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("po", rn);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("map", hm);
		try {

			Object obj = groovyScriptInvokerLogic.invoke("CP0702", map);// 是否启用库存

			if ("1".equals(obj) && rn.getMaterialCode() != null) {// 启用库存,物料编码不为空
																	// ， 生成入库单
				Instock stock = new Instock();
				if (instockMap.containsKey(rn.getPlantCode() + "_" + rn.getStoreLocalCode())) {
					stock = instockMap.get(rn.getPlantCode() + "_" + rn.getStoreLocalCode());
				} else {
					instockMap.put(rn.getPlantCode() + "_" + rn.getStoreLocalCode(), stock);
				}

				// stock.setInStockNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_RKD));

				stock.setPlantCode(rn.getPlantCode());
				stock.setPlantName(delivery.getPlantName());
				stock.setStoreLocalCode(dd.getStorageLocationCode());
				stock.setInDate(Calendar.getInstance());

				stock.setInType("001");// 收货入库
				stock.setRemark(dd.getRemark());
				stock.setState(InstockState.TOPASS);

				stock.setCreateUserId(rn.getCreateUserId());
				stock.setCreateUserName(rn.getCreateUserName());
				stock.setCreateTime(Calendar.getInstance());

				stock.setModifyUserId(rn.getCreateUserId());
				stock.setModifyUserName(rn.getCreateUserName());
				stock.setModifyTime(Calendar.getInstance());

				List<InstockDtl> instockDtls = new ArrayList<InstockDtl>();
				InstockDtl instockDtl = new InstockDtl();
				if (instockDtlMap.containsKey(rn.getPlantCode() + "_" + rn.getStoreLocalCode())) {
					instockDtls = instockDtlMap.get(rn.getPlantCode() + "_" + rn.getStoreLocalCode());
				} else {
					instockDtlMap.put(rn.getPlantCode() + "_" + rn.getStoreLocalCode(), instockDtls);
				}
				instockDtl.setRowNo(1L);
				if (instockDtls != null && instockDtls.size() > 0) {
					int count = instockDtls.size();
					instockDtl.setRowNo((long) ++count);
				}

				instockDtl.setMaterialCode(rn.getMaterialCode());
				instockDtl.setMaterialName(rn.getMaterialName());
				instockDtl.setUnitCode(dd.getUnitCode());
				instockDtl.setNum(dd.getAcceptQty());// 数量
				instockDtl.setInstock(stock);

				instockDtls.add(instockDtl);

				stock.setInstockDtls(instockDtls);

				// InstockLogic.save(stock);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 冲销产生出库单
	 * 
	 * @param rn
	 */
	protected void createOutStock(ReceivingNote rn) {

		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("po", rn);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("map", hm);
		try {

			Object obj = groovyScriptInvokerLogic.invoke("CP0702", map);// 是否启用库存

			if ("1".equals(obj) && rn.getMaterialCode() != null) {// 启用库存,物料编码不为空
																	// ， 生成入库单
				Outstock stock = new Outstock();

				stock.setOutStockNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_CKD));

				Map<String, Object> searchParams = new HashMap<String, Object>();
				stock.setPlantCode(rn.getPlantCode());
				searchParams.put("EQ_plantCode", rn.getPlantCode());
				FeignParam<Plant> feignParam = new FeignParam<Plant>(searchParams);
				List<Plant> plants = plantLogic.findAll(feignParam);
				if (plants != null && plants.size() > 0) {
					stock.setPlantName(plants.get(0).getPlantName());
				}
				if (StringUtils.isNotEmpty(rn.getStoreLocalCode())) {
					stock.setStoreLocalCode(rn.getStoreLocalCode());
					searchParams.clear();
					searchParams.put("EQ_stockLocationCode", rn.getStoreLocalCode());
					FeignParam<StockLocation> feignParamStock = new FeignParam<StockLocation>(searchParams);
					List<StockLocation> stockLocations = stockLocationLogic.findAll(feignParamStock);
					if (stockLocations != null && stockLocations.size() > 0) {
						stock.setStoreLocalName(stockLocations.get(0).getStockLocationName());
					}
				}
				stock.setOutDate(Calendar.getInstance());

				stock.setOutType("002");// 退货出库
				// stock.setRemark(rn.getrem);
				stock.setStatus(OutstockState.TOPASS);

				stock.setCreateUserId(rn.getCreateUserId());
				stock.setCreateUserName(rn.getCreateUserName());
				stock.setCreateTime(Calendar.getInstance());

				stock.setModifyUserId(rn.getCreateUserId());
				stock.setModifyUserName(rn.getCreateUserName());
				stock.setModifyTime(Calendar.getInstance());

				List<OutstockDtl> outstockDtls = new ArrayList<OutstockDtl>();
				OutstockDtl outstockDtl = new OutstockDtl();

				outstockDtl.setRowNo(1L);
				outstockDtl.setMaterialCode(rn.getMaterialCode());
				outstockDtl.setMaterialName(rn.getMaterialName());
				outstockDtl.setUnitCode(rn.getUnitCode());
				outstockDtl.setNum(rn.getQtyReceive());// 冲销数量
				outstockDtl.setOutstock(stock);

				outstockDtls.add(outstockDtl);

				stock.setOutstockDtls(outstockDtls);

				outstockLogic.save(stock);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 每次点收，都生成一个检验批次
	 * 
	 * @param rn 收货单
	 * @param delivery 送货单
	 * @param dd 送货单明细
	 * @return 质检对象
	 */
	protected CensorQuality createCensorQuality(ReceivingNote rn, Delivery delivery, DeliveryDtl dd) {
		CensorQuality c = new CensorQuality();

		c.setCensorqualityNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_ZJD));
		c.setClientCode(delivery.getClientCode());

		c.setReceivingNoteNo(rn.getReceivingNoteNo());
		c.setPurchaseOrderNo(rn.getErpPurchaseOrderNo());
		c.setRowIds(Long.parseLong(rn.getPurchaseOrderItem()));
		c.setMaterialCode(rn.getMaterialCode());
		c.setMaterialName(rn.getMaterialName());
		c.setVendorCode(rn.getVendorCode());
		c.setVendorName(rn.getVendorName());
		c.setVendorErpCode(rn.getVendorErpCode());
		c.setCensorQty(rn.getStockQty());
		c.setCanCheckQty(rn.getStockQty());
		c.setStatus(CensorQualityState.TOCHECK);
		c.setErpSyn(SrmSynStatus.SYNCHRONIZEDNOT);
		c.setCheckQualifiedQty(BigDecimal.ZERO);
		c.setCheckReceiveQty(BigDecimal.ZERO);
		c.setCheckUnqualifiedQty(BigDecimal.ZERO);
		c.setQualifiedQty(BigDecimal.ZERO);
		c.setReceiveQty(BigDecimal.ZERO);
		c.setUnqualifiedQty(BigDecimal.ZERO);

		c.setPurchasingOrgCode(delivery.getPurchasingOrgCode());
		c.setPurchasingOrgName(delivery.getPurchasingOrgName());
		c.setPlantCode(delivery.getPlantCode());
		c.setPlantName(delivery.getPlantName());
		c.setUnit(rn.getStockUnit());
		c.setStockCode(dd.getStorageLocationCode());
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.clear();
		searchParams.put("EQ_stockLocationCode", dd.getStorageLocationCode());
		FeignParam<StockLocation> feignParamStock = new FeignParam<StockLocation>(searchParams);
		List<StockLocation> stockLocations = stockLocationLogic.findAll(feignParamStock);
		if (stockLocations != null && stockLocations.size() > 0) {
			c.setStockName(stockLocations.get(0).getStockLocationName());
		}

		c.setVoucherYear(rn.getMaterialCertificateYear());// 凭证年度
		c.setVoucherNo(rn.getMaterialCertificateCode());// 编号
		c.setVoucherProNo(rn.getMaterialCertificateItem());// 行项目号

		c.setInspectionTime(Calendar.getInstance());// 送检时间
		c.setCreateTime(Calendar.getInstance());// 创建时间
		c.setCreateUserId(rn.getCreateUserId());
		c.setCreateUserName(rn.getCreateUserName());

		return c;
	}

	/**
	 * 根据点收的送货单明细，生成收货单
	 * 
	 * @param delivery
	 * @param snd 送货单明细
	 * @param mcc 物料凭证编号
	 * @param rowids 行号
	 * @return 收货单
	 * @throws Exception
	 */
	protected ReceivingNote createReceivingNote(Delivery delivery, Long userId, String userName, DeliveryDtl snd, String mcc, int rowids) {
		ReceivingNote rn = new ReceivingNote();
		rn.setReceivingNoteNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_SHD));
		String clientCode = delivery.getClientCode();
		rn.setClientCode(clientCode);
		rn.setCreateUserId(userId);
		rn.setCreateUserName(userName);
		rn.setCreateTime(Calendar.getInstance());
		rn.setShoppingNoticeNo(delivery.getDeliveryCode());
		rn.setShoppingNoticeDetailId(snd.getDeliveryDtlId());
		rn.setShoppingNoticeRowId(snd.getLineNumber());
		rn.setPurchaseOrderNo(snd.getPurchaseOrderCode());
		rn.setErpPurchaseOrderNo(snd.getErpPurchaseOrderNo());
		rn.setStoreLocalCode(snd.getStorageLocationCode());// 库存地点编码
		rn.setVendorCode(delivery.getVendorCode());
		rn.setVendorName(delivery.getVendorName());
		rn.setMaterialCode(snd.getMaterialCode());
		rn.setMaterialName(snd.getMaterialName());
		rn.setQtyReceive(snd.getAcceptQty());// 收货量
		rn.setAcceptReturnFlag(101L);// 收货
		rn.setReceiptBillFlag(0);// 未对账
		rn.setOrigin(0);// 0来源于srm
		rn.setVendorErpCode(delivery.getVendorErpCode());
		rn.setModifyTime(Calendar.getInstance());
		// 生成新的物料凭证号
		// 收货单的物料凭证号 materialCertificateCode

		// 同一批点收的收货单，物料凭证号应相同，但行号递增1
		rn.setMaterialCertificateItem(rowids + "");
		rn.setMaterialCertificateCode(mcc);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		rn.setMaterialCertificateYear(year + "");

		// 凭证年度-凭证编码-凭证行号
		rn.setMaterialCertificate(
				rn.getMaterialCertificateYear() + "-" + rn.getMaterialCertificateCode() + "-" + rn.getMaterialCertificateItem());

		// 原物料凭证相关
		rn.setOmaterialCertificateCode(mcc);
		rn.setOmaterialCertificateItem(rowids + "");
		rn.setOmaterialCertificateYear(year + "");

		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_purchaseOrderDetailId", snd.getOrderDetailId());
		PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailLogic.findOne(searchParams);
		PurchaseOrder purchaseOrder = purchaseOrderDetail.getPurchaseOrder();

		// 稅率
		rn.setTaxCode(purchaseOrderDetail.getTaxRateCode());
		searchParams.clear();
		searchParams.put("EQ_taxRateCode", purchaseOrderDetail.getTaxRateCode());
		FeignParam<TaxRate> feignParam = new FeignParam<TaxRate>(searchParams);
		List<TaxRate> trs = taxRateLogic.findAll(feignParam);
		if (trs != null) {
			rn.setTaxRate(trs.get(0).getTaxRateValue());
		} else {
			rn.setTaxRate(BigDecimal.ZERO);
		}

		rn.setCurrencyCode(purchaseOrderDetail.getPurchaseOrder().getCurrencyCode());
		// 记录类别（SPECIALWHSEFLAG）：根据订单明细的行项目类别进行转换，当为标准时，赋值为0，当为寄售2时，赋值为K，当为分包3时，赋值为O
		String lineItemTypeCode = purchaseOrderDetail.getLineItemTypeCode();
		// modify by zjq 对账单来源收货明细判断寄售类型根据specialwhseFlag = k 所以这边需要进行转换
		if (lineItemTypeCode.equals("0")) {
			rn.setSpecialwhseFlag("0");
		} else if (lineItemTypeCode.equals("2")) {
			rn.setSpecialwhseFlag("K");
		} else {
			rn.setSpecialwhseFlag("O");
		}
		rn.setSpecialwhseFlag(lineItemTypeCode);
		rn.setStockType(purchaseOrderDetail.getStockType());
		if ("X".equals(purchaseOrderDetail.getStockType())) {// 库存类型质检，产生检验批,收货单质检状态为“待检”
			rn.setStatus(CensorQualityState.TOCHECK);
		} else {
			rn.setStatus(CensorQualityState.CHECKED);
		}
		// 对账日期，凭证日期，过账日期
		rn.setCertificateDate(Calendar.getInstance());// 凭证日期
		rn.setPostingDate(Calendar.getInstance());// 过账日期
		// 采购组织编码和名称
		rn.setPurchasingOrgCode(purchaseOrder.getPurchasingOrgCode());
		rn.setPurchasingOrgName(purchaseOrder.getPurchasingOrgName());
		// 专业组
		rn.setPurchasingGroupCode(purchaseOrder.getPurchasingGroupCode());
		rn.setPurchasingGroupName(purchaseOrder.getPurchasingGroupName());
		// 采购订单行项目
		if (snd.getLineNumber() != null) {
			rn.setPurchaseOrderItem(snd.getLineNumber().toString());
		}
		rn.setPurchaseOrderDetailId(snd.getOrderDetailId());
		// 项目
		rn.setPlantCode(purchaseOrderDetail.getPlantCode());

		BigDecimal priceUnit = BigDecimal.ONE;
		if (purchaseOrderDetail.getPurchaseOrderPricings().get(0).getPriceUnit() != null) {
			priceUnit = new BigDecimal(purchaseOrderDetail.getPurchaseOrderPricings().get(0).getPriceUnit());
		}
		rn.setPrice(purchaseOrderDetail.getBuyerPrice().divide(priceUnit, 4, RoundingMode.HALF_UP));
		// 获取订单单位/定价单位转换关系
		List<PurchaseDualUnitConversion> purchaseDualUnitConversions = purchaseOrderDetail.getPurchaseDualUnitConversions();
		PurchaseDualUnitConversion purchaseDualUnitConversion = purchaseDualUnitConversions.size() > 0 ? purchaseDualUnitConversions.get(0)
				: new PurchaseDualUnitConversion();
		BigDecimal convertDenominator2 = purchaseDualUnitConversion.getConvertDenominator2(); // 定价单位
		BigDecimal convertMolecular2 = purchaseDualUnitConversion.getConvertMolecular2();// 订单单位
		BigDecimal exchangeRate2 = convertDenominator2.divide(convertMolecular2, 3, RoundingMode.HALF_UP); // 定价单位/订单单位转换系数
		rn.setExchangeRate(exchangeRate2); // PO定价单位数量/收货数量
		rn.setFixPriceQty(snd.getAcceptQty().multiply(exchangeRate2)); // 定价数量
		rn.setInvoiceQty(snd.getAcceptQty().multiply(exchangeRate2)); // 可对账数量（定价单位）
		rn.setReconciliableQty(snd.getAcceptQty());// 可对账数量（订单单位）
		rn.setCanChargeOffNum(snd.getAcceptQty());// 可冲销数量为收货量（订单单位）

		BigDecimal convertDenominator = purchaseDualUnitConversion.getConvertDenominator(); // 基本单位
		BigDecimal convertMolecular = purchaseDualUnitConversion.getConvertMolecular();// 订单单位
		BigDecimal exchangeRate = convertDenominator.divide(convertMolecular, 3, RoundingMode.HALF_UP); // 基本单位/订单单位转换系数

		// 双单位转换
		rn.setStockQty(snd.getAcceptQty().multiply(exchangeRate));

		rn.setUnitCode(snd.getUnitCode()); // 订单单位
		rn.setFixPriceUnitCode(purchaseDualUnitConversion.getPricingUnit()); // 定价单位
		rn.setStockUnit(purchaseDualUnitConversion.getUnitCode());// 基本单位

		if (purchaseOrderDetail.getBuyerPrice() != null && purchaseOrderDetail.getBuyerPrice().compareTo(BigDecimal.ZERO) > 0) {
			rn.setAmountnoTax(rn.getFixPriceQty().multiply(purchaseOrderDetail.getBuyerPrice()).divide(priceUnit, 2, RoundingMode.HALF_UP)); // 供应商价格(未税单价)*收货量/价格单位
		} else {
			rn.setAmountnoTax(BigDecimal.ZERO);// 供应商价格(单价)*收货量
			rn.setInvoiceQty(BigDecimal.ZERO); // 可对账数量（定价单位）
			rn.setReconciliableQty(BigDecimal.ZERO);// 可对账数量（订单单位）
		}
		rn.setTotalAmountAndTax(rn.getAmountnoTax().multiply(BigDecimal.ONE.add(rn.getTaxRate())));// 总金额：数量
																									// *
																									// 单价*（1+税率）
		rn.setTotalTax(rn.getAmountnoTax().multiply(rn.getTaxRate()));// 税额：数量 *
																		// 单价*税率
		rn.setInvoiceFlag(0);
		return rn;
	}

	/**
	 * 处理是否关闭送货单
	 * 
	 * @param sn 送货单
	 */
	protected void dealShoppingNotice(Delivery sn) {
		boolean closeFlag = true;
		int a = 0;

		for (DeliveryDtl snd : sn.getDeliveryDtls()) {
			// 如果送货明细的点收量==送货数量
			if (snd.getReceivedQty().compareTo(snd.getDeliveryNumber()) >= 0) {
				snd.setIsFinish("1");// 已收货
				a++;
			}
			snd.setAcceptQty(snd.getDeliveryNumber().subtract(snd.getReceivedQty()));// 下一次的点收数量默认为送货量-已收数量
		}
		if (a != sn.getDeliveryDtls().size()) {
			closeFlag = false;
		}
		// 送货单全部收完货，就将主单的状态置为关闭
		if (closeFlag == true) {
			sn.setStatus(DeliveryState.CLOSE);// 送货单状态置为已完成
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
					.setBillId(sn.getDeliveryId().toString()).setCreatorId(sn.getCreateUserId().toString());
			portalDealDataLogic.data4Portal(pp);
		}
		deliveryLogic.save(sn);
	}

	/**
	 * 处理是否关闭订单明细
	 * 
	 * @param sn 送货单
	 * @throws Exception
	 */
	protected void dealPurchaseOrder(Delivery sn) {
		// 判断采购订单细单数量是否收货完毕，可否关闭细单---20150922 xufq
		Long orderDetailId = null;
		Set<Long> poIds = new HashSet<Long>();
		for (DeliveryDtl sendDetail : sn.getDeliveryDtls()) {
			orderDetailId = sendDetail.getOrderDetailId();

			PurchaseOrderDetail pod = purchaseOrderDetailLogic.findById(orderDetailId);

			if (pod != null) {
				poIds.add(pod.getPurchaseOrder().getPurchaseOrderId());
			}
		}

		List<Long> syncOrderIds = new ArrayList<Long>();

		// 判断订单是否关闭
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IN_purchaseOrderId", StringUtils.join(poIds.toArray(), ","));
		List<PurchaseOrder> pos = purchaseOrderLogic.findAll(params);
		if (pos != null && pos.size() > 0) {
			for (PurchaseOrder purchaseOrder : pos) {
				List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrder.getPurchaseOrderDetails();
				if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
					int count = 0;// 与 purchaseOrderDetail1.size()比较，相等关闭主单

					for (PurchaseOrderDetail pod1 : purchaseOrderDetails) {
						if (1 == pod1.getCloseFlag() || 1 == pod1.getDeleteFlag()) {
							count++;
						}
					}

					if (count == purchaseOrderDetails.size()) {
						purchaseOrder.setPurchaseOrderState(PurchaseOrderState.CLOSE);
						purchaseOrderLogic.save(purchaseOrder);
					}

					if (!syncOrderIds.contains(purchaseOrder.getPurchaseOrderId())) {
						syncOrderIds.add(purchaseOrder.getPurchaseOrderId());
					}
				}
			}
			// 同步订单
			for (Long id : syncOrderIds) {
				// 同步接口
				purchaseOrderLogic.doSync(id, null, "");
			}
		}
	}

	/**
	 * 关闭收货单
	 * 
	 * @param id 收货单id
	 */
	public void close(Long id) {
		// 延迟加载的对象，在需要的时候可以使用FETCH，抓取多个使用逗号隔开,bean不需要
		Delivery model = deliveryLogic.findById(id);
		if (model == null) {
			return;
		}
		// 发布状态的送货单才可以关闭
		/*
		 * if(!(DeliveryState.RELEASE.equals(model.getStatus()))){ return; }
		 */
		model.setStatus(DeliveryState.CLOSE);

		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("EQ_delivery_deliveryId", model.getDeliveryId());
		List<DeliveryDtl> deliveryDtls = deliveryDtlLogic.findAll(maps);
		if (deliveryDtls != null && deliveryDtls.size() > 0) {
			Map<Long, PurchaseOrderDetail> poDtlMap = new HashMap<Long, PurchaseOrderDetail>();
			Map<Long, SendScheduleDetail> ssDtlMap = new HashMap<Long, SendScheduleDetail>();
			Map<String, Object> searchMap = new HashMap<String, Object>();
			List<Long> podDtList = new ArrayList<Long>();
			List<Long> ssdIdList = new ArrayList<Long>();
			for (DeliveryDtl detail : deliveryDtls) {
				podDtList.add(detail.getOrderDetailId());
				if (detail.getSendDetailId() != null) {
					ssdIdList.add(detail.getSendDetailId());
				}
			}
			searchMap.put("IN_purchaseOrderDetailId", podDtList);
			List<PurchaseOrderDetail> poDtlList = purchaseOrderDetailLogic.findAll(searchMap);
			for (PurchaseOrderDetail pod : poDtlList) {
				poDtlMap.put(pod.getPurchaseOrderDetailId(), pod);
			}
			if (ssdIdList != null && ssdIdList.size() > 0) {
				searchMap.clear();
				searchMap.put("IN_sendScheduleDetailId", ssdIdList);
				List<SendScheduleDetail> ssDtlList = sendScheduleDetailLogic.findAll(searchMap);
				for (SendScheduleDetail ssd : ssDtlList) {
					ssDtlMap.put(ssd.getSendScheduleDetailId(), ssd);
				}
			}

			for (DeliveryDtl detail : deliveryDtls) {

				if (detail.getDataFrom() == 1) {// 来源订单明细
					this.updatePurchaseOrderDetailOnline(detail, poDtlMap);
				} else if (detail.getDataFrom() == 2) {// 来源排程子明细
					this.subtractSendscheduleDetail(detail, ssDtlMap);
				}
				// 设置对应的送货明细取消标志为1
				// detail.setCancelFlag(1);
			}
		}

		model = deliveryLogic.save(model);

		// 消息提醒
		// this.alert(model, "ASN_CLOSESELF");

		// 同步sap
	}

	/**
	 * 更新采购订单明细在途量
	 * 
	 * @param item 送货单明细
	 * @param id 订单明细id
	 */
	protected void updatePurchaseOrderDetailOnline(DeliveryDtl item, Map<Long, PurchaseOrderDetail> poDtlMap) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = poDtlMap.get(item.getOrderDetailId());
		if (pod == null) {
			pod = purchaseOrderDetailLogic.findById(item.getOrderDetailId());
		}
		// 已收数量 receiveQty
		BigDecimal receiveQty = item.getReceivedQty() == null ? BigDecimal.ZERO : item.getReceivedQty();
		// 送货数量deliveryQty
		BigDecimal deliveryQty = item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber();

		BigDecimal qty = deliveryQty.subtract(receiveQty);

		// 在途量
		BigDecimal oldQtyOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();
		BigDecimal newQtyOnline = oldQtyOnline.subtract(qty);
		pod.setQtyOnline(newQtyOnline);

		purchaseOrderDetailLogic.recountCanSendQty(pod);
	}

	/**
	 * 私有 减 排程送货量在途量
	 * 
	 * @param item 送货明细
	 */
	protected void subtractSendscheduleDetail(DeliveryDtl item, Map<Long, SendScheduleDetail> ssDtlMap) {

		if (item.getSendDetailId() != null) {
			// 找到对应的排程子明细
			SendScheduleDetail ssd = ssDtlMap.get(item.getSendDetailId());
			if (ssd == null) {
				ssd = sendScheduleDetailLogic.findById(item.getSendDetailId());
			}
			// 已收货量
			BigDecimal oldReceiptQty = ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty();
			BigDecimal newReceiptQty = oldReceiptQty.add(item.getAcceptQty() == null ? BigDecimal.ZERO : item.getAcceptQty());
			ssd.setReceiptQty(newReceiptQty);

			// 在途量
			BigDecimal oldOnWayQty = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();
			BigDecimal newQtyOnline = oldOnWayQty.subtract(item.getAcceptQty() == null ? BigDecimal.ZERO : item.getAcceptQty());
			if (newQtyOnline.compareTo(BigDecimal.ZERO) <= 0) {
				newQtyOnline = BigDecimal.ZERO;
			}
			ssd.setOnWayQty(newQtyOnline);

			// 可送货量 = 排程需求量 - 在途量 - 收货量 + 退货量
			if (ssd.getScheduleQty() != null) {
				BigDecimal newCanSendQty = ssd.getScheduleQty().subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
						.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
						.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
				ssd.setCanSendQty(newCanSendQty);
			}
			// }

			// 排程送货标识更新
			if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {// 如果可送货量等于需求量则
																			// 为未送货
				ssd.setSendFlag(0);
			} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
																																		// 为部分送货
				ssd.setSendFlag(1);
			} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0) {// 如果可送货量等于0为完全送货
				ssd.setSendFlag(2);
			}

			sendScheduleDetailLogic.save(ssd);
		}
	}

	/**
	 * 私有 更新排程单的状态
	 * 
	 * @param sendscheduleNo 排程单号
	 */
	protected void updateSendscheduleStatus(String sendscheduleNo) {
		// 获取全部的该排程单对应的排程子明细记录
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_sendScheduleNo", sendscheduleNo);
		List<SendScheduleDetail> ssdList = sendScheduleDetailLogic.findAll(searchParams);

		if (ssdList != null && ssdList.size() > 0) {
			// 如果明细中全部为完全送货这该排程单状态设置为完成
			boolean flag = true;
			for (SendScheduleDetail ssd : ssdList) {
				if (ssd.getSendFlag() != 2) {
					flag = false;
					break;
				}
			}
			if (flag) {
				SendSchedule ssd = sendScheduleLogic.findFirst(searchParams);
				ssd.setSendScheduleState(SendScheduleState.OPEN);
				sendScheduleLogic.save(ssd);
			}
		}
	}

	/**
	 * 根据前台参数获取送货单信息
	 * 
	 * @param params 参数
	 * @param userId 用户ID
	 * @param userName 用户名
	 */
	@Override
	public Map<Boolean, String> getDelivery(String params, Long userId, String userName) {
		Map<Boolean, String> result = new HashMap<Boolean, String>();
		Delivery delivery = new Delivery();
		List<DeliveryDtl> list = new ArrayList<DeliveryDtl>();
		JSONObject obj = JSONObject.parseObject(params);
		JSONArray array = (JSONArray) obj.get("deliveryDtls");
		Long deliveryId = Long.valueOf(obj.get("deliveryId").toString());
		delivery = deliveryLogic.findById(deliveryId);
		for (int i = 0; i < array.size(); i++) {
			DeliveryDtl detail = new DeliveryDtl();
			JSONObject detailObj = (JSONObject) array.get(i);
			Long detailId = Long.valueOf(detailObj.get("detailId").toString());
			detail = deliveryDtlLogic.findById(detailId);
			detail.setAcceptQty(detailObj.getBigDecimal("acceptQty"));
			if (StringUtils.isNotBlank(detailObj.get("storageLocationCode").toString())) {
				detail.setStorageLocationCode(detailObj.get("storageLocationCode").toString());
			} else {
				detail.setStorageLocationCode(delivery.getStorageLocationCode());
			}
			list.add(detail);
		}
		delivery.setDeliveryDtls(list);
		result = receiving(delivery, userId, userName);
		return result;
	}

	/**
	 * 导入收货数据
	 * 
	 * @param sheet表格对象
	 * @param userAuthMap用户组资源
	 * @param webPrams查询条件
	 * @param userId用户ID
	 * @param userName用户名
	 * @param clientCode客户端编码
	 * @throws Exception
	 */
	public String batchImportExcel(HSSFSheet sheet, Map<String, Object> webPrams, Long userId, String userName, String clientCode)
			throws Exception {
		String message = null;
		Map<String, PurchaseOrder> orderMap = new HashMap<String, PurchaseOrder>();
		Map<String, PurchaseOrderDetail> orderDetailMap = new HashMap<String, PurchaseOrderDetail>();
		Map<String, PurchaseDualUnitConversion> unitConversionMap = new HashMap<String, PurchaseDualUnitConversion>();
		List<ReceivingNote> toCheckList = new ArrayList<ReceivingNote>();
		List<ReceivingNote> receivingNotes = new ArrayList<ReceivingNote>();

		getBatchExcelData(sheet, orderMap, orderDetailMap, unitConversionMap, receivingNotes);

		// 校验数据
		message = validateMainExcelData(orderMap, orderDetailMap, unitConversionMap, toCheckList, receivingNotes);
		if (StringUtils.isNotBlank(message)) {
			return message;
		}
		// 设置生成收货单
		setReceivingNote(orderMap, orderDetailMap, receivingNotes, unitConversionMap, clientCode, userId, userName);
		// 设置生成检验批
		setCensorQuality(toCheckList);
		return message;
	}

	/**
	 * 校验主单数据
	 * 
	 * @param orderMap采购订单集合
	 * @param orderDetailMap订单明细集合
	 * @param unitConversionMap双单位转换集合
	 * @param receivingNotes收货单集合
	 * @return提示信息
	 */
	protected String validateMainExcelData(Map<String, PurchaseOrder> orderMap, Map<String, PurchaseOrderDetail> orderDetailMap,
			Map<String, PurchaseDualUnitConversion> unitConversionMap, List<ReceivingNote> toCheckList,
			List<ReceivingNote> receivingNotes) {
		StringBuilder messages = new StringBuilder();
		String eachMessage = "";
		Map<String, Object> searchParams = new HashMap<String, Object>();
		// 需要验证excel中物料凭证年度+物料凭证编码+物料凭证中的项号是否有重复
		Map<String, String> existMap = new HashMap<String, String>();
		if (0 == receivingNotes.size()) {
			return getText("porder.exsitDetailInfo");// "不存在明细信息";
		}
		int index = 1;
		String materialCertificateYears = "";
		String materialCertificateCodes = "";
		String materialCertificateItems = "";
		String stockLocationCodes = "";
		Map<String, String> i18nText = new HashMap<String, String>();
		// 缓存国际化,防止循环查询
		i18nText.put("label.theRow", getText("label.theRow"));
		i18nText.put("vendor.canNotBeEmpty", getText("vendor.canNotBeEmpty"));
		i18nText.put("porder.relationNo", getText("porder.relationNo"));
		i18nText.put("receivingnote.sappurchaseOrderItem", getText("receivingnote.sappurchaseOrderItem"));
		i18nText.put("receiptBillDetail.qtyReceive", getText("receiptBillDetail.qtyReceive"));
		i18nText.put("censorQuality.materialVoucherYear", getText("censorQuality.materialVoucherYear"));
		i18nText.put("preInvoiceDetail.materialCertificateCode", getText("preInvoiceDetail.materialCertificateCode"));
		i18nText.put("receivingnote.materialcertificateItem", getText("receivingnote.materialcertificateItem"));
		i18nText.put("receivingnote.certificateDate", getText("receivingnote.certificateDate"));
		i18nText.put("preInvoice.postingDate", getText("preInvoice.postingDate"));
		i18nText.put("shoppingNotice.storeLocalCode", getText("shoppingNotice.storeLocalCode"));
		i18nText.put("receivingNote.batchImport.excelHasRepeatData", getText("receivingNote.batchImport.excelHasRepeatData"));
		for (ReceivingNote receivingNote : receivingNotes) {
			materialCertificateYears += "," + receivingNote.getMaterialCertificateYear();
			materialCertificateCodes += "," + receivingNote.getMaterialCertificateCode();
			materialCertificateItems += "," + receivingNote.getMaterialCertificateItem();
			stockLocationCodes += "," + receivingNote.getStoreLocalCode();

			if (StringUtils.isBlank(receivingNote.getPurchaseOrderNo())) {
				eachMessage = i18nText.get("porder.relationNo");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (StringUtils.isBlank(receivingNote.getPurchaseOrderItem())) {
				eachMessage = i18nText.get("receivingnote.sappurchaseOrderItem");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (null == receivingNote.getQtyReceive()) {
				eachMessage = i18nText.get("receiptBillDetail.qtyReceive");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (StringUtils.isBlank(receivingNote.getMaterialCertificateYear())) {
				eachMessage = i18nText.get("censorQuality.materialVoucherYear");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (StringUtils.isBlank(receivingNote.getMaterialCertificateCode())) {
				eachMessage = i18nText.get("preInvoiceDetail.materialCertificateCode");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (StringUtils.isBlank(receivingNote.getMaterialCertificateItem())) {
				eachMessage = i18nText.get("receivingnote.materialcertificateItem");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (null == receivingNote.getCertificateDate()) {
				eachMessage = i18nText.get("receivingnote.certificateDate");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (null == receivingNote.getPostingDate()) {
				eachMessage = i18nText.get("preInvoice.postingDate");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			if (StringUtils.isBlank(receivingNote.getStoreLocalCode())) {
				eachMessage = i18nText.get("shoppingNotice.storeLocalCode");
				messages.append(getResource("label.theRow", i18nText, new String[] { (index) + "" }) + "：");
				messages.append(eachMessage + i18nText.get("vendor.canNotBeEmpty") + "\n");
			}
			// 验证excel文件中是否有重复的“物料凭证年度+物料凭证编码+物料凭证中的项号”
			String checkedStr = receivingNote.getMaterialCertificateYear() + "_" + receivingNote.getMaterialCertificateCode() + "_"
					+ receivingNote.getMaterialCertificateItem();
			if (existMap.containsKey(checkedStr)) {
				messages.append(getResource("receivingNote.batchImport.excelHasRepeatData", i18nText, index + "") + "\n");
			} else {
				existMap.put(checkedStr, "");
			}
			if (messages.length() == 0) {
				// 获取采购订单
				PurchaseOrder purchaseOrder = new PurchaseOrder();
				searchParams.clear();
				if (!orderMap.containsKey(receivingNote.getErpPurchaseOrderNo())) {
					searchParams.put("EQ_erpPurchaseOrderNo", receivingNote.getErpPurchaseOrderNo());
					purchaseOrder = purchaseOrderLogic.findOne(searchParams);
					orderMap.put(receivingNote.getErpPurchaseOrderNo(), purchaseOrder);
				} else {
					purchaseOrder = orderMap.get(receivingNote.getErpPurchaseOrderNo());
				}

				receivingNote.setPurchaseOrderNo(purchaseOrder.getPurchaseOrderNo());
				receivingNote.setErpPurchaseOrderNo(purchaseOrder.getErpPurchaseOrderNo());
				// 获取采购订单明细
				PurchaseOrderDetail detail = new PurchaseOrderDetail();
				if (purchaseOrder != null) {
					searchParams.clear();
					searchParams.put("EQ_purchaseOrder_purchaseOrderId", purchaseOrder.getPurchaseOrderId());
					searchParams.put("EQ_rowIds", receivingNote.getPurchaseOrderItem());
					detail = purchaseOrderDetailLogic.findOne(searchParams);
				}
				orderDetailMap.put(receivingNote.getErpPurchaseOrderNo(), detail);
				// 获取双单位转换关系
				searchParams.clear();
				PurchaseDualUnitConversion unitConversion = new PurchaseDualUnitConversion();
				if (detail != null) {
					searchParams.put("EQ_purchaseOrderDetail_purchaseOrderDetailId", detail.getPurchaseOrderDetailId());
					unitConversion = purchaseDualUnitConversionLogic.findOne(searchParams);
				}
				unitConversionMap.put(receivingNote.getErpPurchaseOrderNo(), unitConversion);
				// 质检状态为质检
				if (receivingNote.getStatus().equals(CensorQualityState.TOCHECK)) {
					toCheckList.add(receivingNote);
				}
			}
			index++;
		}
		if (0 < messages.length()) {
			return messages.toString();
		}
		i18nText.put("receivingNote.batchImport.OrderNoNotExist", getText("receivingNote.batchImport.OrderNoNotExist"));
		i18nText.put("receivingNote.batchImport.orderDetailNoNotExist", getText("receivingNote.batchImport.orderDetailNoNotExist"));
		i18nText.put("receivingNote.batchImport.stockLocalCodeNotExist", getText("receivingNote.batchImport.stockLocalCodeNotExist"));
		i18nText.put("receivingNote.batchImport.stockLocalCodeNotExist", getText("receivingNote.batchImport.stockLocalCodeNotExist"));
		i18nText.put("receivingNote.batchImport.stockLockAndPlantNotExist", getText("receivingNote.batchImport.stockLockAndPlantNotExist"));
		i18nText.put("receivingNote.batchImport.yearAndCodeAdnItemExist", getText("receivingNote.batchImport.yearAndCodeAdnItemExist"));
		// 校验是否存在采购订单
		index = 1;
		for (ReceivingNote receivingNote : receivingNotes) {
			if (orderMap.get(receivingNote.getErpPurchaseOrderNo()) == null) {
				// 第{0}行订单号所属的采购订单不存在于系统中
				eachMessage += getResource("receivingNote.batchImport.OrderNoNotExist", i18nText, index + "") + "\n";
			}
			if (StringUtils.isNotBlank(eachMessage)) {
				messages.append(eachMessage);
				eachMessage = "";
			}

			index++;
		}
		// 校验是否存在订单明细
		index = 1;
		for (ReceivingNote receivingNote : receivingNotes) {
			if (orderDetailMap.get(receivingNote.getErpPurchaseOrderNo()) == null) {
				// 第{0}行项目号所属的采购订单明细不存在于系统中
				eachMessage += getResource("receivingNote.batchImport.orderDetailNoNotExist", i18nText, index + "") + "\n";
			}
			if (StringUtils.isNotBlank(eachMessage)) {
				messages.append(eachMessage);
				eachMessage = "";
			}

			index++;
		}
		// 获取库存地点
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IN_stockLocationCode", stockLocationCodes.substring(1).split(","));
		List<StockLocation> stockList = stockLocationLogic.findAll(new FeignParam<StockLocation>(params));
		Map<String, StockLocation> stockMap = new HashMap<String, StockLocation>();
		for (StockLocation stockLocation : stockList) {
			stockMap.put(stockLocation.getStockLocationCode(), stockLocation);
		}
		index = 1;
		// 验证库存地点是否存在于系统中
		for (ReceivingNote receivingNote : receivingNotes) {
			if (!stockMap.containsKey(receivingNote.getStoreLocalCode())) {
				// 库存地点编码是否存在于系统中，不存在提示：“导入收货信息中第 X行中的库存地点编码不存在”
				eachMessage += getResource("receivingNote.batchImport.stockLocalCodeNotExist", i18nText, index + "") + "\n";
			}
			if (StringUtils.isNotBlank(eachMessage)) {
				messages.append(eachMessage);
				eachMessage = "";
			}

			index++;
		}
		Map<String, Boolean> existStockLocation = new HashMap<String, Boolean>();
		// 验证库存地点与工厂的从属关系
		index = 1;
		for (ReceivingNote receivingNote : receivingNotes) {
			// 获取订单明细中的工厂编码
			String plantCode = "";
			if (orderDetailMap.get(receivingNote.getErpPurchaseOrderNo()) != null) {
				plantCode = orderDetailMap.get(receivingNote.getErpPurchaseOrderNo()).getPlantCode();
			}
			params.clear();
			if (!existStockLocation.containsKey(plantCode + "_" + receivingNote.getStoreLocalCode())) {
				params.put("EQ_plantCode", plantCode);
				params.put("EQ_stockLocationCode", receivingNote.getStoreLocalCode());
				List<StockLocation> plantList = stockLocationLogic.findAll(new FeignParam<StockLocation>(params));
				if (plantList == null || plantList.size() == 0) {
					// 导入收货信息中第 X 行中的库存地点与工厂不存在从属关系
					eachMessage += getResource("receivingNote.batchImport.stockLockAndPlantNotExist", i18nText, index + "") + "\n";
					existStockLocation.put(plantCode + "_" + receivingNote.getStoreLocalCode(), false);
				} else {
					existStockLocation.put(plantCode + "_" + receivingNote.getStoreLocalCode(), true);
				}
			} else {
				if (!existStockLocation.get(plantCode + "_" + receivingNote.getStoreLocalCode())) {
					// 导入收货信息中第 X 行中的库存地点与工厂不存在从属关系
					eachMessage += getResource("receivingNote.batchImport.stockLockAndPlantNotExist", i18nText, index + "") + "\n";
				}
			}
			if (StringUtils.isNotBlank(eachMessage)) {
				messages.append(eachMessage);
				eachMessage = "";
			}

			index++;
		}
		// 验证物料凭证年度+物料凭证编码+物料凭证中的项号是否已存在
		params.clear();
		params.put("IN_materialCertificateYear", materialCertificateYears.substring(1).split(","));
		params.put("IN_materialCertificateCode", materialCertificateCodes.substring(1).split(","));
		params.put("IN_materialCertificateItem", materialCertificateItems.substring(1).split(","));
		List<ReceivingNote> receivingNoteList = dao.findAll(params);
		Map<String, ReceivingNote> receivingNoteMap = new HashMap<String, ReceivingNote>();
		for (ReceivingNote receivingNote : receivingNoteList) {
			String key = receivingNote.getMaterialCertificateYear() + "_" + receivingNote.getMaterialCertificateCode() + "_"
					+ receivingNote.getMaterialCertificateItem();
			receivingNoteMap.put(key, receivingNote);
		}
		index = 1;

		for (ReceivingNote receivingNote : receivingNotes) {
			String key = receivingNote.getMaterialCertificateYear() + "_" + receivingNote.getMaterialCertificateCode() + "_"
					+ receivingNote.getMaterialCertificateItem();
			if (receivingNoteMap.containsKey(key)) {
				// 物料凭证年度+物料凭证编码+物料凭证中的项号已存在
				eachMessage += getResource("receivingNote.batchImport.yearAndCodeAdnItemExist", i18nText, index + "") + "\n";
			}
			if (StringUtils.isNotBlank(eachMessage)) {
				messages.append(eachMessage);
				eachMessage = "";
			}

			index++;
		}

		return messages.toString();
	}

	/**
	 * 生成检验批
	 * 
	 * @param toCheckMap 需要生成检验批的数据
	 */
	protected void setCensorQuality(List<ReceivingNote> toCheckList) {
		for (ReceivingNote rn : toCheckList) {
			CensorQuality c = new CensorQuality();

			c.setCensorqualityNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_ZJD));
			c.setClientCode(rn.getClientCode());
			c.setReceivingNoteNo(rn.getReceivingNoteNo());
			c.setPurchaseOrderNo(rn.getErpPurchaseOrderNo());
			c.setRowIds(Long.parseLong(rn.getPurchaseOrderItem()));
			c.setMaterialCode(rn.getMaterialCode());
			c.setMaterialName(rn.getMaterialName());
			c.setVendorCode(rn.getVendorCode());
			c.setVendorName(rn.getVendorName());
			c.setVendorErpCode(rn.getVendorErpCode());
			c.setCensorQty(rn.getQtyReceive());
			c.setCanCheckQty(rn.getQtyReceive());
			c.setStatus(CensorQualityState.TOCHECK);
			c.setErpSyn(SrmSynStatus.SYNCHRONIZEDNOT);
			c.setCheckQualifiedQty(BigDecimal.ZERO);
			c.setCheckReceiveQty(BigDecimal.ZERO);
			c.setCheckUnqualifiedQty(BigDecimal.ZERO);
			c.setQualifiedQty(BigDecimal.ZERO);
			c.setReceiveQty(BigDecimal.ZERO);
			c.setUnqualifiedQty(BigDecimal.ZERO);
			c.setPurchasingOrgCode(rn.getPurchasingOrgCode());
			c.setPurchasingOrgName(rn.getPurchasingOrgName());
			c.setPlantCode(rn.getPlantCode());
			c.setUnit(rn.getUnitCode());
			c.setStockCode(rn.getStoreLocalCode());
			c.setVoucherYear(rn.getMaterialCertificateYear());// 凭证年度
			c.setVoucherNo(rn.getMaterialCertificateCode());// 编号
			c.setVoucherProNo(rn.getMaterialCertificateItem());// 行项目号
			c.setInspectionTime(Calendar.getInstance());// 送检时间
			c.setCreateTime(Calendar.getInstance());// 创建时间
			c.setCreateUserId(rn.getCreateUserId());
			c.setCreateUserName(rn.getCreateUserName());

			censorQualityLogic.save(c);
		}
	}

	/**
	 * 设置收货单
	 * 
	 * @param orderMap采购订单集合
	 * @param orderDetailMap订单明细集合
	 * @param receivingNotes收货单集合
	 * @param userId用户ID
	 * @param userName用户名
	 */
	protected void setReceivingNote(Map<String, PurchaseOrder> orderMap, Map<String, PurchaseOrderDetail> orderDetailMap,
			List<ReceivingNote> receivingNotes, Map<String, PurchaseDualUnitConversion> unitConversionMap, String clientCode, Long userId,
			String userName) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		// 订单
		PurchaseOrder order = new PurchaseOrder();
		// 订单明细
		PurchaseOrderDetail detail = new PurchaseOrderDetail();
		// 双单位转换关系
		PurchaseDualUnitConversion unitConversion = new PurchaseDualUnitConversion();
		for (ReceivingNote receivingNote : receivingNotes) {
			// 订单
			order = orderMap.get(receivingNote.getErpPurchaseOrderNo());
			// 订单明细
			detail = orderDetailMap.get(receivingNote.getErpPurchaseOrderNo());
			// 双单位转换关系
			unitConversion = unitConversionMap.get(receivingNote.getErpPurchaseOrderNo());
			// 收货单编号
			receivingNote.setReceivingNoteNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_SHD));
			// 订单明细ID
			receivingNote.setPurchaseOrderDetailId(detail.getPurchaseOrderDetailId());
			// 供应商编码
			receivingNote.setVendorCode(order.getVendorCode());
			// 供应商ERP编码
			receivingNote.setVendorErpCode(order.getVendorErpCode());
			// 供应商名称
			receivingNote.setVendorName(order.getVendorName());
			// 物料编码
			receivingNote.setMaterialCode(detail.getMaterialCode());
			// 物料名称
			receivingNote.setMaterialName(detail.getMaterialName());
			// 库存类型
			receivingNote.setStockType(detail.getStockType());
			// 来源(excel)
			receivingNote.setOrigin(2);
			// 税率编码
			receivingNote.setTaxCode(detail.getTaxRateCode());
			// 根据税率编码查找税率
			searchParams.clear();
			searchParams.put("EQ_taxRateCode", detail.getTaxRateCode());
			TaxRate taxRate = taxRateLogic.findOne(searchParams);
			// 税率
			if (taxRate != null) {
				receivingNote.setTaxRate(taxRate.getTaxRateValue());
			} else {
				receivingNote.setTaxRate(BigDecimal.ZERO);
			}
			// 特殊库存标识
			receivingNote.setSpecialwhseFlag(detail.getLineItemTypeCode());
			// 采购组织编码
			receivingNote.setPurchasingOrgCode(order.getPurchasingOrgCode());
			// 采购组织名称
			receivingNote.setPurchasingOrgName(order.getPurchasingOrgName());
			// 采购组编码
			receivingNote.setPurchasingGroupCode(order.getPurchasingGroupCode());
			// 采购组名称
			receivingNote.setPurchasingGroupName(order.getPurchasingGroupName());
			// 工厂
			receivingNote.setPlantCode(detail.getPlantCode());
			// 原物料凭证年度
			receivingNote.setOmaterialCertificateYear(receivingNote.getMaterialCertificateYear());
			// 原物料凭证编号
			receivingNote.setOmaterialCertificateCode(receivingNote.getMaterialCertificateCode());
			// 原物料凭证中的项目
			receivingNote.setOmaterialCertificateItem(receivingNote.getMaterialCertificateItem());
			// 收货金额
			receivingNote.setAmountnoTax(detail.getLineItemValAmt());
			// sku数量
			receivingNote.setStockQty(unitConversion.getSkuQty());
			// sku单位
			receivingNote.setStockUnit(unitConversion.getUnitCode());
			// 定价单位数量
			receivingNote.setFixPriceQty(unitConversion.getPricingQty());
			// 定价单位
			receivingNote.setFixPriceUnitCode(unitConversion.getPricingUnit());
			// 订单单位
			receivingNote.setUnitCode(unitConversion.getOrderDetailUnit());
			// 采购订单行号
			receivingNote.setPurchaseOrderItem(String.valueOf(detail.getRowIds()));
			// 客户端编码
			receivingNote.setClientCode(clientCode);
			// 用户ID
			receivingNote.setCreateUserId(userId);
			// 创建人
			receivingNote.setCreateUserName(userName);
			// 创建时间
			receivingNote.setCreateTime(Calendar.getInstance());
			// 修改时间
			receivingNote.setModifyTime(Calendar.getInstance());

			receivingNote.setPurchaseOrderNo(order.getPurchaseOrderNo());
			receivingNote.setErpPurchaseOrderNo(order.getErpPurchaseOrderNo());
			// 保存收货单
			dao.save(receivingNote);
			updatePurchaseOrder(receivingNote, detail);
		}
	}

	/**
	 * 更新采购订单及订单明细
	 * 
	 * @param receivingNote 收货单
	 * @param detail 订单明细
	 */
	public void updatePurchaseOrder(ReceivingNote receivingNote, PurchaseOrderDetail detail) {
		// 到货数量
		BigDecimal qtyOfArrive = BigDecimal.ZERO;
		// 已收货量
		BigDecimal receivedQty = BigDecimal.ZERO;
		// 在途数量
		BigDecimal qtyOfOnline = BigDecimal.ZERO;
		qtyOfArrive = detail.getQtyArrive() == null ? BigDecimal.ZERO : detail.getQtyArrive();
		receivedQty = detail.getReceiveQty() == null ? BigDecimal.ZERO : detail.getReceiveQty();// 已收货量
		detail.setQtyArrive(qtyOfArrive.add(receivingNote.getQtyReceive()));// 到货数量累加
		detail.setReceiveQty(receivedQty.add(receivingNote.getQtyReceive()));// 已收货数量累加
		// 有送货单才扣减在途数量
		qtyOfOnline = detail.getQtyOnline() == null ? BigDecimal.ZERO : detail.getQtyOnline();// 采购明细的在途数量
		if (qtyOfOnline.compareTo(receivingNote.getQtyReceive()) >= 0) {
			detail.setQtyOnline(qtyOfOnline.subtract(receivingNote.getQtyReceive()));// 在途数量为原在途数量-收货量
		}
		BigDecimal qty_arrive = detail.getReceiveQty();// 收货量
		BigDecimal qty_quit = detail.getQtyQuit();// 退货量
		detail.getVendorQty();
		qty_arrive.subtract(qty_quit);
		detail = canCloseOrder(detail);
		// 更新订单明细
		purchaseOrderDetailLogic.recountCanSendQty(detail);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchaseOrderNo", detail.getPurchaseOrder().getPurchaseOrderNo());
		PurchaseOrder purchaseOrder = purchaseOrderLogic.findOne(params);
		// 取未删除的细单，判断细单是否关闭，如果细单全为关闭，主单状态置为“关闭”
		params.clear();
		params.put("EQ_purchaseOrder_purchaseOrderId", purchaseOrder.getPurchaseOrderId());
		params.put("EQ_deleteFlag", 0);
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailLogic.findAll(params);
		if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
			int count = 0;// 与 purchaseOrderDetail1.size()比较，相等关闭主单

			for (PurchaseOrderDetail pod : purchaseOrderDetails) {
				if (1 == pod.getCloseFlag()) {
					count++;
				}
			}
			if (count == purchaseOrderDetails.size()) {
				purchaseOrder.setPurchaseOrderState(PurchaseOrderState.CLOSE);
			}
		}
		// 更新订单
		purchaseOrderLogic.save(purchaseOrder);
	}

	/**
	 * 获取收货单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	protected void getBatchExcelData(HSSFSheet sheet, Map<String, PurchaseOrder> orderMap, Map<String, PurchaseOrderDetail> orderDetailMap,
			Map<String, PurchaseDualUnitConversion> unitConversionMap, List<ReceivingNote> receivingNotes) throws Exception {
		Map<String, ReceivingNote> receivingNoteMap = new HashMap<String, ReceivingNote>();
		for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
			HSSFRow row = sheet.getRow(rowSeq);
			ReceivingNote receivingNote = new ReceivingNote();
			int cellSeq = 0;
			if (PoiUtils.isBlankRow(row)) {
				continue;
			}

			if (null != row) {
				// 关联号
				HSSFCell cell = row.getCell(cellSeq++);
				receivingNote.setPurchaseOrderNo(getCellStringValue(cell));
				receivingNote.setErpPurchaseOrderNo(getCellStringValue(cell));

				// 行项目
				cell = row.getCell(cellSeq++);
				receivingNote.setPurchaseOrderItem(getCellStringValue(cell));
				// 送货单号
				cell = row.getCell(cellSeq++);
				receivingNote.setShoppingNoticeNo(getCellStringValue(cell));

				// 供应商编码
				cell = row.getCell(cellSeq++);
				receivingNote.setVendorCode(getCellStringValue(cell));

				// 供应商名称
				cell = row.getCell(cellSeq++);
				receivingNote.setVendorName(getCellStringValue(cell));

				// 物料编码
				cell = row.getCell(cellSeq++);
				receivingNote.setMaterialCode(getCellStringValue(cell));

				// 物料名称
				cell = row.getCell(cellSeq++);
				receivingNote.setMaterialName(getCellStringValue(cell));

				// 收货数量
				cell = row.getCell(cellSeq++);
				receivingNote.setQtyReceive(getCellBigDecimalValue(cell));

				// 收退货标识
				cell = row.getCell(cellSeq++);
				if (getCellStringValue(cell).equals("收货")) {
					receivingNote.setAcceptReturnFlag(101l);
				} else {
					receivingNote.setAcceptReturnFlag(102l);
				}

				// 质检标识
				cell = row.getCell(cellSeq++);
				if (getCellStringValue(cell).equals("质检")) {
					receivingNote.setStatus(CensorQualityState.TOCHECK);
				} else {
					receivingNote.setStatus(CensorQualityState.CHECKED);
				}

				// 对账标识
				cell = row.getCell(cellSeq++);
				if (getCellStringValue(cell).equals("已对账")) {
					receivingNote.setReceiptBillFlag(1);
				} else {
					receivingNote.setReceiptBillFlag(0);
				}

				// 物料凭证年度
				cell = row.getCell(cellSeq++);
				receivingNote.setMaterialCertificateYear(getCellStringValue(cell));

				// 物料凭证编码
				cell = row.getCell(cellSeq++);
				receivingNote.setMaterialCertificateCode(getCellStringValue(cell));

				// 物料凭证中的项号
				cell = row.getCell(cellSeq++);
				receivingNote.setMaterialCertificateItem(getCellStringValue(cell));

				// 凭证日期
				cell = row.getCell(cellSeq++);
				receivingNote.setCertificateDate(getCellCalendarValue(cell));

				// 过账日期
				cell = row.getCell(cellSeq++);
				receivingNote.setPostingDate(getCellCalendarValue(cell));

				// 库存地点编码
				cell = row.getCell(cellSeq++);
				receivingNote.setStoreLocalCode(getCellStringValue(cell));

				receivingNoteMap.put(receivingNote.getErpPurchaseOrderNo(), receivingNote);
				receivingNotes.add(receivingNote);
			}
		}

	}

	/**
	 * 获取excel列的值，并将其转换成数值类型
	 * 
	 * @param cell 列对象
	 * @return
	 */
	protected BigDecimal getCellBigDecimalValue(HSSFCell cell) {
		BigDecimal bigDecimal = null;

		if (null == cell) {
			return bigDecimal;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			bigDecimal = BigDecimal.valueOf(cell.getNumericCellValue());

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			String str = cell.getStringCellValue().trim();

			if (str.contains(".")) {
				str = str.substring(0, str.indexOf("."));
			}

			if (!"".equals(str)) {
				bigDecimal = BigDecimal.valueOf(Double.parseDouble(str));
			}
		}

		// 当数量小于0时，置为0
		if (null != bigDecimal && bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
			bigDecimal = BigDecimal.ZERO;
		}

		return bigDecimal;
	}

	/**
	 * 获取excel列值，并转换成String类型的数据
	 * 
	 * @param cell 列对象
	 * @return
	 */
	protected String getCellStringValue(HSSFCell cell) {
		String value = "";

		if (null == cell) {
			return value;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);// true时的格式：1,234,567,890
			double acno = cell.getNumericCellValue();
			value = nf.format(acno);

		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue().trim();
		}

		return value;
	}

	/**
	 * 获取excel列数值，并将其转换成时间类型的数据
	 * 
	 * @param cell列对象
	 * @return
	 * @throws Exception
	 */
	protected Calendar getCellCalendarValue(HSSFCell cell) throws Exception {
		Calendar calendar = null;

		if (null == cell) {
			return calendar;
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = sdf.parse(cell.getStringCellValue().trim());
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			calendar = Calendar.getInstance();
			calendar.setTime(cell.getDateCellValue());
		}

		return calendar;
	}

	/**
	 * 获取国际化资源
	 * 
	 * @param key
	 * @return
	 */
	protected String getText(String key) {
		return I18nUtils.getText(key);
	}

	/**
	 * 替换国家化资源中的下标
	 * 
	 * @param key 资源主键
	 * @param params 要替换的元素
	 * @return
	 */
	protected String getResource(String key, Map<String, String> i18nText, String... params) {
		String value = i18nText.get(key);
		if (StringUtils.isEmpty(value)) {
			value = getText(key);
		}
		if (StringUtils.isNotBlank(value) && null != params && 0 < params.length) {
			int i = 0;
			for (String param : params) {
				String str = "{" + i++ + "}";
				value = value.replace(str, param);
			}
		}

		return value;
	}

	@Override
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal) {
		if (StringUtils.isBlank(action)) {
			action = "数据操作日志";
		}
		if (StringUtils.isNotBlank(terminal) && !terminal.equals(SrmConstants.PLATFORM_WEB)) {
			message = StringUtils.isBlank(message) ? "" : ",原因：" + message;
			message = terminal + message;
		}

		// create log
		Log log = Logs.getLog();
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)//
				// 设置日志级别
				.module(SrmConstants.BILLTYPE_SHD)// 设置日志模块
				.key(billPk)// 日志信息的主要key
				.action(action)// 操作的动作
				.message(message)// 日志的记录内容
				.result("success")// 日志的操作结果
				.operatorName(userName)// 日志的操作人
				.operatorId(userId)// 操作人id
				.businessNo(businessNo) // 单据编号
				.terminal(terminal) // 终端标识
				.log();// 调用记录日志
	}

}
