package com.huiju.srm.purchasing.ws;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.commons.ws.utils.MessageContent;
import com.huiju.srm.commons.ws.utils.ServiceSupport;
import com.huiju.srm.commons.ws.utils.ValidVisitor;
import com.huiju.srm.masterdata.api.CompanyClient;
import com.huiju.srm.masterdata.api.GroupClient;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialGroupClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.PurchasingGroupClient;
import com.huiju.srm.masterdata.api.PurchasingOrganizationClient;
import com.huiju.srm.masterdata.api.UnitClient;
import com.huiju.srm.masterdata.entity.Company;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.MaterialGroup;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.PurchasingGroup;
import com.huiju.srm.masterdata.entity.PurchasingOrganization;
import com.huiju.srm.masterdata.entity.Unit;
import com.huiju.srm.purchasing.dto.PurchaseOrderBomDto;
import com.huiju.srm.purchasing.dto.PurchaseOrderDetailDto;
import com.huiju.srm.purchasing.dto.PurchaseOrderDto;
import com.huiju.srm.purchasing.dto.PurchaseOrderPricingDto;
import com.huiju.srm.purchasing.dto.PurchaseOrderUnitConversionDto;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderEvent;
import com.huiju.srm.purchasing.entity.PurchaseOrderFlowState;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.PurchaseOrderType;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.sourcing.dao.MaterialMasterPriceDtlDao;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialMasterPrice;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;
import com.huiju.srm.sourcing.service.MaterialMasterPriceService;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.service.VendorService;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.handler.SoapMessageMgr;
import com.huiju.srm.ws.service.WsLogService;

/**
 * 订单相关WebService
 * 
 * @author WANGLQ
 */
@Component // 由Spring管理
@WebService(serviceName = "purchaseOrderWebService", targetNamespace = "http://www.huiju.com/purchaseOrder", endpointInterface = "com.huiju.srm.purchasing.ws.PurchaseOrderWebService")
public class PurchaseOrderWebServiceImpl extends ServiceSupport implements PurchaseOrderWebService {

	protected static final Logger log = LoggerFactory.getLogger(PurchaseOrderWebService.class);

	@Autowired
	protected WsLogService wsLogLogic;

	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;

	@Autowired
	protected GroupClient groupLogic;

	@Autowired
	protected UserClient userLogic;

	@Autowired
	protected CompanyClient companyLogic;

	@Autowired
	protected BillSetServiceClient billSetLogic;

	@Autowired
	protected PurchasingOrganizationClient purchasingOrganizationLogic;

	@Autowired
	protected PurchasingGroupClient purchasingGroupLogic;

	@Autowired
	protected VendorService vendorLogic;

	@Autowired
	protected MaterialGroupClient materialGroupLogic;

	@Autowired
	protected UnitClient unitLogic;

	@Autowired
	protected MaterialClient materialLogic;

	@Autowired
	protected PlantClient plantLogic;

	@Autowired
	protected MaterialMasterPriceDtlDao materialMasterPriceDtlEao;

	@Autowired
	protected MaterialMasterPriceService materialMasterPriceLogic;

	// 请求报文本地线程缓存
	private SoapMessageMgr soapMessageMgr;

	/**
	 * 不需要同步 -1
	 */
	protected final Integer SYNC_NO_NEED = -1;
	/**
	 * 未同步 0
	 */
	protected final Integer SYNC_UNSYNCHRONIZED = 0;
	/**
	 * 同步完成 1
	 */
	protected final Integer SYNC_SYNCHRONIZED = 1;
	/**
	 * 同步中 2
	 */
	protected final Integer SYNC_SYNCHRONIZING = 2;
	/**
	 * 同步异常 3
	 */
	protected final Integer SYNC_EXCEPTION = 3;

	@Override
	public Message saveOrUpdatePurchaseOrder(PurchaseOrderDto purchaseOrder) {
		soapMessageMgr = SoapMessageMgr.getCurrentInstance();
		// 请求参数
		String requestContent = StringUtils.isBlank(soapMessageMgr.getSoapMessageStr()) ? DataUtils.toJson(purchaseOrder)
				: soapMessageMgr.getSoapMessageStr();
		// 接口编码
		String interfaceCode = SrmConstants.SRM_PURCHASEORDERSERVICE_CODE;
		// 放置执行内容状态
		MessageContent content = createContent();
		try {
			// 验证订单信息
			if (!isValid(purchaseOrder, content)) { // , purchaseOrderVisitor
				sendMail(purchaseOrder.getPurchasingGroupCode(), content.toString());
				WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
				wsLogLogic.addErrorLog(wslog, error(content).toString());
				return error(content);
			}
			// 验证订单明细
			for (PurchaseOrderDetailDto detail : purchaseOrder.getPurchaseOrderDetails()) {
				if (!isValid(detail, content)) { // , purchaseOrderDetailVisitor
					sendMail(purchaseOrder.getPurchasingGroupCode(), content.toString());
					WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
					wsLogLogic.addErrorLog(wslog, error(content).toString());
					return error(content);
				}
				// 定价条件验证
				if (detail.getPurchaseOrderPricings() != null) {
					for (PurchaseOrderPricingDto pricing : detail.getPurchaseOrderPricings()) {
						if (!isValid(pricing, content)) { // ,
															// purchaseOrderPricingVisitor
							sendMail(purchaseOrder.getPurchasingGroupCode(), content.toString());
							WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
							wsLogLogic.addErrorLog(wslog, error(content).toString());
							return error(content);
						}
					}
				}
				// 订单BOM验证
				if (detail.getPurchaseOrderBoms() != null) {
					for (PurchaseOrderBomDto bom : detail.getPurchaseOrderBoms()) {
						if (!isValid(bom, content)) { // ,
														// purchaseOrderBomVisitor
							sendMail(purchaseOrder.getPurchasingGroupCode(), content.toString());
							WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
							wsLogLogic.addErrorLog(wslog, error(content).toString());
							return error(content);
						}
					}
				}
				// 单位转换验证
				if (detail.getPurchaseOrderUnitConversions() != null) {
					for (PurchaseOrderUnitConversionDto uc : detail.getPurchaseOrderUnitConversions()) {
						if (!isValid(uc, content)) {// , purchaseOrderUcVisitor
							sendMail(purchaseOrder.getPurchasingGroupCode(), content.toString());
							WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
							wsLogLogic.addErrorLog(wslog, error(content).toString());
							return error(content);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("订单接收出现异常：订单号：%s，信息：%s", purchaseOrder.getErpPurchaseOrderNo(), e.getMessage());
			content.put("ValidationErrorMessage", msg);
			sendMail(purchaseOrder.getPurchasingGroupCode(), msg);
			WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
			wsLogLogic.addFailLog(wslog, failed(content).toString());
			return failed(e, content);
		}
		try {
			// 查找订单是否已经存在
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EQ_erpPurchaseOrderNo", purchaseOrder.getErpPurchaseOrderNo());
			String orderJson = purchaseOrderLogic.getOrderJson(params);
			PurchaseOrder org = JSONObject.parseObject(orderJson, PurchaseOrder.class);
			// 订单不存在则保存，已存在则更新
			if (org == null) {
				saveOrder(purchaseOrder);
			} else {
				updateOrder(org, purchaseOrder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("订单接收出现异常：订单号：%s，信息：%s", purchaseOrder.getErpPurchaseOrderNo(), e.getMessage());
			content.put("OperationErrorMessage", msg);
			sendMail(purchaseOrder.getPurchasingGroupCode(), msg);
			WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
			wsLogLogic.addFailLog(wslog, failed(content).toString());
			return failed(e, content);
		}

		WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
		wsLogLogic.addSuccessLog(wslog, success(content).toString());
		return success(content);

	}

	/**
	 * 修改采购订单
	 * 
	 * @param entity
	 * @param vo
	 * @throws Exception
	 */
	private void updateOrder(PurchaseOrder entity, PurchaseOrderDto vo) throws Exception {
		setOrderData(entity, vo);// 设置主单
		setOrderDetailData(entity, vo);// 设置明细

		// 撤销审批操作
		if (StringUtils.isNotBlank(vo.getPurchaseOrderStatusCode()) && "0".equals(vo.getPurchaseOrderStatusCode())) {
			String result = purchaseOrderLogic.revocationCheck(entity.getPurchaseOrderId(), entity.getModifyUserId(),
					entity.getModifyUserName());
			if (StringUtils.isNotBlank(result)) {
				throw new RuntimeException(result);
			}
		} else {

			// 发布操作
			if (PurchaseOrderState.RELEASE.equals(entity.getPurchaseOrderState())) {
				// 重新发布操作
				entity.setPurchaseOrderCheckState(null);
				entity.setPurchaseOrderFlowState(PurchaseOrderFlowState.PASS);
				entity.setPurchaseOrderState(PurchaseOrderState.RELEASE);
				purchaseOrderLogic.save(entity);
				purchaseOrderLogic.dealPurchaseOrder(entity.getCreateUserId(), entity.getCreateUserName(), entity.getPurchaseOrderId(),
						PurchaseOrderEvent.TORELEASE.name(), "", SrmConstants.PLATFORM_WEB);

				// 完成操作
			} else if (PurchaseOrderState.CLOSE.equals(entity.getPurchaseOrderState())) {
				purchaseOrderLogic.save(entity);

				// 取消操作
			} else if (PurchaseOrderState.CANCEL.equals(entity.getPurchaseOrderState())) {
				entity.setPurchaseOrderState(PurchaseOrderState.CANCEL);
				for (PurchaseOrderDetail pod : entity.getPurchaseOrderDetails()) {
					pod.setDeleteFlag(1);
				}
				entity = purchaseOrderLogic.save(entity);
				purchaseOrderLogic.sendCancelMessage(entity, entity.getModifyUserId());
			}
		}
	}

	/**
	 * 新建订单
	 * 
	 * @param purchaseOrder 新建订单
	 * @return
	 * @throws ParseException
	 */
	private void saveOrder(PurchaseOrderDto vo) throws Exception {
		PurchaseOrder entity = new PurchaseOrder();
		setOrderData(entity, vo);// 设置主单
		setOrderDetailData(entity, vo);// 设置明细
		// 生成SRM采购订单号
		String purchaseOrderNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_CGD);
		if (StringUtils.isBlank(purchaseOrderNo)) {
			throw new RuntimeException("无法生成采购订单号");
		}

		// 发布操作
		entity.setPurchaseOrderNo(purchaseOrderNo);
		entity.setPurchaseOrderCheckState(null);
		entity.setPurchaseOrderFlowState(PurchaseOrderFlowState.PASS);
		entity.setPurchaseOrderState(PurchaseOrderState.RELEASE);
		try {
			entity = purchaseOrderLogic.save(entity);
		} catch (Exception e) {
			// 查找订单是否已经存在
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EQ_erpPurchaseOrderNo", vo.getErpPurchaseOrderNo());
			PurchaseOrder org = purchaseOrderLogic.findOne(params);
			// 订单不存在则保存，已存在则更新
			if (org != null) {
				updateOrder(org, vo);
			}
		}

		purchaseOrderLogic.dealPurchaseOrder(entity.getCreateUserId(), entity.getCreateUserName(), entity.getPurchaseOrderId(),
				PurchaseOrderEvent.TORELEASE.name(), "", SrmConstants.PLATFORM_WEB);
	}

	/**
	 * 验证和设置数据
	 * 
	 * @param entity 订单实体
	 * @param dvo vo对象
	 * @throws ParseException
	 */
	private void setOrderDetailData(PurchaseOrder entity, PurchaseOrderDto vo) throws ParseException {
		// 获取价格主数据
		Map<String, Object> materialMasterPriceParams = new HashMap<String, Object>();
		materialMasterPriceParams.put("EQ_purchasingOrgCode", vo.getPurchasingOrgCode());
		materialMasterPriceParams.put("EQ_vendorErpCode", vo.getVendorErpCode());
		// Specification<MaterialMasterPrice> spec =
		// QueryUtils.newSpecification(materialMasterPriceParams);
		Map<String, Object> materialMasterPriceMap = new HashMap<String, Object>();
		String listJson = purchaseOrderLogic.findMaterialMasterPriceJson(materialMasterPriceParams);
		List<MaterialMasterPrice> materialMasterPrices = JSONArray.parseArray(listJson, MaterialMasterPrice.class);

		if (materialMasterPrices != null && materialMasterPrices.size() > 0) {
			for (MaterialMasterPrice materialMasterPrice : materialMasterPrices) {
				String key = materialMasterPrice.getPurchasingOrgCode() + "_" + materialMasterPrice.getPlantCode() + "_"
						+ materialMasterPrice.getMaterialCode() + materialMasterPrice.getVendorErpCode();
				if (!materialMasterPriceMap.containsKey(key)) {
					materialMasterPriceMap.put(key, DataUtils.toJson(materialMasterPrice));
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> params = new HashMap<String, Object>();

		List<String> materialCodes = new ArrayList<String>();
		List<String> plantCodes = new ArrayList<String>();
		for (PurchaseOrderDetailDto pd : vo.getPurchaseOrderDetails()) {
			pd.removeZero();// 去零操作
			if (StringUtils.isNotBlank(pd.getMaterialCode())) {
				materialCodes.add(pd.getMaterialCode().trim());
			}
			if (StringUtils.isNotBlank(pd.getPlantCode())) {
				plantCodes.add(pd.getPlantCode().trim());
			}
		}
		Map<String, Object> materialMap = new HashMap<String, Object>();
		Map<String, Object> plantMap = new HashMap<String, Object>();
		Map<String, Object> unitMap = new HashMap<String, Object>();
		Map<String, MaterialGroup> materialGroupMap = new HashMap<String, MaterialGroup>();
		// 获取所有物料
		if (0 < materialCodes.size()) {
			params.put("IN_materialCode", StringUtils.join(materialCodes, ","));
			List<Material> materials = materialLogic.findAll(new FeignParam<Material>(params));
			for (Material material : materials) {
				materialMap.put(material.getMaterialCode(), material);
				materialGroupMap.put(material.getMaterialGroup().getMaterialGroupCode(), (MaterialGroup) material.getMaterialGroup());
			}
		}

		// 获取所有单位编码
		params.clear();
		params.put("EQ_status", "1");
		List<Unit> units = unitLogic.findAll(new FeignParam<Unit>(params));
		for (Unit unit : units) {
			unitMap.put(unit.getUnitCode(), unit);
		}

		// 工厂编码
		if (0 < plantCodes.size()) {
			params.clear();
			params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
			List<Plant> plants = plantLogic.findAll(new FeignParam<Plant>(params));
			for (Plant plant : plants) {
				plantMap.put(plant.getPlantCode(), plant);
			}
		}

		// 构建订单明细
		List<PurchaseOrderDetail> details = new ArrayList<PurchaseOrderDetail>();

		for (PurchaseOrderDetailDto dvo : vo.getPurchaseOrderDetails()) {
			PurchaseOrderDetail detail = getOrgDetail(entity.getPurchaseOrderDetails(), dvo);

			if (StringUtils.isNotBlank(dvo.getMaterialCode())) {
				Material materialInfo = (Material) materialMap.get(dvo.getMaterialCode().trim());
				if (materialInfo == null) {
					throw new RuntimeException(
							"物料编码不存在，请检查订单明细行[" + dvo.getRowIds() + "]的materialCode[" + dvo.getMaterialCode().trim() + "]是否正确");
				}

				// 查询物料单位
				Unit unit = (Unit) unitMap.get(materialInfo.getBaseUnitCode());
				if (unit == null) {
					throw new RuntimeException(
							"单位编码不存在，请检查订单明细行[" + dvo.getRowIds() + "]的unitCode[" + materialInfo.getBaseUnitCode() + "]是否正确");
				}

				detail.setUnitCode(materialInfo.getBaseUnitCode());
				detail.setUnitName(unit.getUnitName());

			}

			// 科目分配类别
			detail.setAccountAllocationTypeCode(dvo.getAccountAllocationTypeCode());
			detail.setAssetNumber(dvo.getAssetNumber());
			detail.setBuyerQty(new BigDecimal(dvo.getBuyerQty()).setScale(2, RoundingMode.HALF_UP));
			// 时间转换
			Calendar buyerTime = Calendar.getInstance();
			Date buyerDate = sdf.parse(dvo.getBuyerTime());
			buyerTime.setTime(buyerDate);
			detail.setBuyerTime(buyerTime);
			// 关闭标识
			detail.setCloseFlag(Integer.valueOf(dvo.getCloseLogo().trim()));
			detail.setCostCenter(dvo.getCostCenter());
			detail.setDeleteFlag(Integer.valueOf(dvo.getDeleteFlag().trim()));
			detail.setEmergencyFlag("Y".equals(dvo.getEmergencyFlag()) ? 1 : 0);
			detail.setErpSynState(SYNC_SYNCHRONIZED);
			detail.setGeneralLedgerSubject(dvo.getGeneralLedgerSubject());
			detail.setIsFree(Integer.valueOf(dvo.getIsFree().trim()));
			detail.setIsReturn(Integer.valueOf(dvo.getIsReturn().trim()));
			detail.setLineItemTypeCode(dvo.getLineItemTypeCode());

			if (dvo.getMaterialCode() == null || StringUtils.isBlank(dvo.getMaterialCode().trim())) {
				detail.setMaterialCode(null);
			} else {
				detail.setMaterialCode(dvo.getMaterialCode().trim());
			}

			// 查询物料组名称
			MaterialGroup materialGroup = materialGroupMap.get(dvo.getMaterialGroupCode());
			if (materialGroup == null) {
				throw new RuntimeException(
						"物料组不存在，请检查订单明细行[" + dvo.getRowIds() + "]的materialGroupCode[" + dvo.getMaterialGroupCode() + "]是否正确");
			}

			detail.setMaterialGroupName(materialGroup.getMaterialGroupName());
			detail.setMaterialGroupCode(dvo.getMaterialGroupCode());
			detail.setMaterialName(dvo.getMaterialName());
			detail.setOverDeliveryLimit(
					new BigDecimal(dvo.getOverDeliveryLimit() == null ? 0 : dvo.getOverDeliveryLimit()).setScale(2, RoundingMode.HALF_UP));
			detail.setPdProjectNumber(dvo.getPdProjectNumber());
			detail.setPlantCode(dvo.getPlantCode());
			detail.setPurchaseOrder(entity);

			Plant plant = (Plant) plantMap.get(dvo.getPlantCode());
			if (null != plant) {
				detail.setPlantName(plant.getPlantName());
			}

			detail.setRowIds(dvo.getRowIds());
			detail.setScheduleFlag("Y".equals(dvo.getScheduleFlag()) ? 1 : 0);
			detail.setShortDeliveryLimit(new BigDecimal(dvo.getShortDeliveryLimit() == null ? 0 : dvo.getShortDeliveryLimit()).setScale(2,
					RoundingMode.HALF_UP));
			detail.setStoreLocal(dvo.getStoreLocal());

			if (StringUtils.isBlank(dvo.getStockType())) {
				detail.setStockType("A");
			} else {
				detail.setStockType(dvo.getStockType());
			}

			detail.setTaxRateCode(dvo.getTaxCode());
			detail.setVendorQty(new BigDecimal(dvo.getBuyerQty()).setScale(2, RoundingMode.HALF_UP));
			detail.setVendorTime(buyerTime);

			// 创建双单位转换
			PurchaseDualUnitConversion unitC = null;
			List<PurchaseDualUnitConversion> conversions = new ArrayList<PurchaseDualUnitConversion>();
			for (PurchaseOrderUnitConversionDto cvo : dvo.getPurchaseOrderUnitConversions()) {
				unitC = getOrgUC(detail.getPurchaseDualUnitConversions(), cvo);
				unitC = createUnitConversion(unitC, detail, cvo);
				conversions.add(unitC);
			}
			detail.setPurchaseDualUnitConversions(conversions);

			// 创建定价条件
			if (dvo.getPurchaseOrderPricings() != null) {
				List<PurchaseOrderPricing> pricings = new ArrayList<PurchaseOrderPricing>();
				PurchaseOrderPricing orderP = null;
				for (PurchaseOrderPricingDto pvo : dvo.getPurchaseOrderPricings()) {
					orderP = getOrgPricing(detail.getPurchaseOrderPricings(), pvo);
					orderP = createPricing(orderP, detail, pvo, unitC);
					pricings.add(orderP);
				}
				detail.setPurchaseOrderPricings(pricings);
				// 价格
				detail.setLineItemValAmt(
						new BigDecimal(dvo.getVendorPrice() * unitC.getPricingQty().doubleValue() / orderP.getPriceUnit().doubleValue())
								.setScale(2, RoundingMode.HALF_UP));
				detail.setBuyerPrice(dvo.getVendorPrice() == null ? BigDecimal.ZERO
						: new BigDecimal(dvo.getVendorPrice()).setScale(2, RoundingMode.HALF_UP));
			} else {
				detail.setLineItemValAmt(BigDecimal.ZERO);
				detail.setBuyerPrice(BigDecimal.ZERO);
			}
			detail.setVendorPrice(detail.getBuyerPrice());
			detail.setSourceCode(3L);
			detail.setSrmRowids(detail.getRowIds());

			// 修改明细时，不修改以下信息
			if (detail.getPurchaseOrderDetailId() == null) {
				detail.setQtyAccord(BigDecimal.ZERO);
				detail.setQtyArrive(BigDecimal.ZERO);
				detail.setQtyCheck(BigDecimal.ZERO);
				detail.setQtyNaccord(BigDecimal.ZERO);
				detail.setQtyOnline(BigDecimal.ZERO);
				detail.setQtyQuit(BigDecimal.ZERO);
				detail.setQtySend(BigDecimal.ZERO);
				detail.setQtyStore(BigDecimal.ZERO);
				detail.setCanSendQty(detail.getBuyerQty());
				detail.setIsAchieveLimit("N");
			}

			// 如果退货标识为是，则将收货量置为订单量
			if (detail.getIsReturn() != null && detail.getIsReturn() == 1) {
				detail.setCanSendQty(BigDecimal.ZERO);
				detail.setIsAchieveLimit("Y");
			}

			detail.setUnScheduledQty(detail.getVendorQty());
			detail.setScheduledQty(BigDecimal.ZERO);

			MaterialLadderPriceDtl dtl = null;
			// 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格
			String key = entity.getPurchasingOrgCode() + "_" + detail.getPlantCode() + "_" + detail.getMaterialCode()
					+ entity.getVendorErpCode();
			if (!materialMasterPriceMap.containsKey(key)) {
				throw new RuntimeException("价格主数据不存在，请检查订单明细行[" + dvo.getRowIds() + "]，并前往SRM进行维护价格主数据");
			}
			String json = (String) materialMasterPriceMap.get(key);
			MaterialMasterPrice materialMasterPrice = JSONObject.parseObject(json, MaterialMasterPrice.class);
			if (!materialMasterPrice.getRecordType().equals(detail.getLineItemTypeCode())) {
				throw new RuntimeException("价格主数据不存在，请检查订单明细行[" + dvo.getRowIds() + "]，并前往SRM进行维护价格主数据");
			}
			List<MaterialMasterPriceDtl> _materialMasterPriceDtls = materialMasterPrice.getMaterialMasterPriceDtls();
			if (_materialMasterPriceDtls != null && _materialMasterPriceDtls.size() > 0) {
				for (MaterialMasterPriceDtl materialMasterPriceDtl : _materialMasterPriceDtls) {
					// 判断有效时间
					if (materialMasterPriceDtl.getEffectiveDate().before(detail.getBuyerTime())
							&& detail.getBuyerTime().before(materialMasterPriceDtl.getExpirationDate())) {
						List<MaterialLadderPriceDtl> materialLadderPriceDtls = materialMasterPriceDtl.getMaterialLadderPriceDtls();
						// 获取阶梯报价
						if (materialLadderPriceDtls != null && materialLadderPriceDtls.size() > 0) {
							for (MaterialLadderPriceDtl materialLadderPriceDtl : materialLadderPriceDtls) {
								if (detail.getBuyerQty().compareTo(materialLadderPriceDtl.getStartNum()) >= 0
										&& detail.getBuyerQty().compareTo(materialLadderPriceDtl.getEndNum()) <= 0) {
									dtl = materialLadderPriceDtl;
								}
							}
						}
					}
				}
			}

			if (null == dtl) {
				throw new RuntimeException(
						"价格主数据不存在3，请检查订单明细行[" + dvo.getRowIds() + "]，并前往SRM进行维护价格主数据" + materialMasterPriceMap.toString());
			}

			details.add(detail);
		}

		entity.setPurchaseOrderDetails(details);
		// 计算订单总价
		entity.setTotalAmount(countTotalAmount(entity).setScale(2, RoundingMode.HALF_UP));

		// 如果订单明细删除标识全部为是，则订单置为取消
		boolean isAllDeleted = true;
		boolean isAllClose = true;

		for (PurchaseOrderDetail detail : details) {
			if (detail.getDeleteFlag() != 1) {
				isAllDeleted = false;
			}

			if (detail.getCloseFlag() != 1) {
				isAllClose = false;
			}
		}

		// 全部删除
		if (isAllDeleted) {
			entity.setPurchaseOrderState(PurchaseOrderState.CANCEL);
			// 全部关闭
		} else if (isAllClose) {
			entity.setPurchaseOrderState(PurchaseOrderState.CLOSE);
		} else {
			entity.setPurchaseOrderState(PurchaseOrderState.RELEASE);
		}
	}

	/**
	 * 验证和设置数据
	 * 
	 * @param entity 订单实体
	 * @param vo vo对象
	 * @throws ParseException
	 */
	private void setOrderData(PurchaseOrder entity, PurchaseOrderDto vo) throws ParseException {
		// 去零操作
		vo.removeZero();

		// 获取采购员
		if (StringUtils.isNotBlank(vo.getBuyerId())) {
			User user = userLogic.findByCode(vo.getBuyerId().toLowerCase());
			if (null == user) {
				user = userLogic.findByCode(vo.getBuyerId());
			}

			if (user != null) {
				if (null != entity.getPurchaseOrderId()) {
					entity.setModifyUserId(user.getUserId());
					entity.setModifyUserName(user.getUserName());
					entity.setModifyTime(Calendar.getInstance());
				} else {
					entity.setCreateUserId(user.getUserId());
					entity.setCreateUserName(user.getUserName());
					entity.setCreateTime(Calendar.getInstance());
				}
			} else {
				throw new RuntimeException("用户编码不存在，请在SRM创建相应编码的用户buyerId[" + vo.getBuyerId() + "]");
			}
		} else {
			throw new RuntimeException("用户编码不存在，请在SRM创建相应编码的用户buyerId[" + vo.getBuyerId() + "]");
		}

		// 查找公司信息
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_companyCode", vo.getCompanyCode());
		Company company = companyLogic.findOne(params);
		if (company == null) {
			throw new RuntimeException("公司不存在，请检查companyCode[" + vo.getCompanyCode() + "]是否正确");
		}
		entity.setCompanyCode(vo.getCompanyCode());
		entity.setCompanyName(company.getCompanyName());

		// 查找采购组织信息
		params.clear();
		params.put("EQ_purchasingOrgCode", vo.getPurchasingOrgCode());
		PurchasingOrganization porg = purchasingOrganizationLogic.findOne(params);
		if (porg == null) {
			throw new RuntimeException("采购组织不存在，请检查purchasingOrgCode[" + vo.getPurchasingOrgCode() + "]是否正确");
		}
		entity.setPurchasingOrgCode(vo.getPurchasingOrgCode());
		entity.setPurchasingOrgName(porg.getPurchasingOrgName());

		// 查找采购组信息
		params.clear();
		params.put("EQ_purchasingGroupCode", vo.getPurchasingGroupCode());
		PurchasingGroup poGroup = purchasingGroupLogic.findOne(params);
		if (poGroup == null) {
			throw new RuntimeException("采购组不存在，请检查purchasingGroupCode[" + vo.getPurchasingGroupCode() + "]是否正确");
		}
		entity.setPurchasingGroupCode(vo.getPurchasingGroupCode());
		entity.setPurchasingGroupName(poGroup.getPurchasingGroupName());

		// 查找供应商信息
		params.clear();
		params.put("EQ_vendorErpCode", vo.getVendorErpCode().trim());
		Vendor vendor = vendorLogic.findOne(params);
		if (vendor == null) {
			throw new RuntimeException("供应商不存在，请检查vendorErpCode[" + vo.getVendorErpCode() + "]是否正确");
		}

		entity.setVendorErpCode(vo.getVendorErpCode().trim());
		entity.setVendorCode(vendor.getVendorCode());
		entity.setVendorName(vendor.getVendorName());
		entity.setTaxRateCode(vendor.getTaxCode());
		entity.setCurrencyCode(vo.getCurrencyCode());
		entity.setCurrencyRate(new BigDecimal(vo.getExchangeRate()).setScale(5, RoundingMode.HALF_UP));
		entity.setErpSynState(SYNC_SYNCHRONIZED);
		entity.setInternationlTradeTerm(vo.getInternationlTradeTerm());
		entity.setInternationlTradeTerm(vo.getInternationlTradeRemark());
		entity.setRemark(vo.getRemark());
		entity.setPurchaseOrderType(vo.getOrderType());
		// 订单时间转换
		Calendar orderTime = Calendar.getInstance();
		Date orderDate = new SimpleDateFormat("yyyy-MM-dd").parse(vo.getPurchaseOrderTime());
		orderTime.setTime(orderDate);
		if (entity.getPurchaseOrderTime() == null) {
			entity.setPurchaseOrderTime(orderTime);
		}
		entity.setCreateType(PurchaseOrderType.FromErp);
		entity.setRemark(vo.getRemark());
		entity.setErpPurchaseOrderNo(vo.getErpPurchaseOrderNo());
		entity.setClientCode("800");
		entity.setCheckFirst(0);// 先审核再确认
		entity.setViewFlag(1);
		entity.setSrmSyncSap(false);
	}

	/**
	 * 接收订单失败时发送邮件提醒采购员
	 * 
	 * @param purchasingGroupCode
	 * @param msg
	 */
	protected void sendMail(String purchasingGroupCode, String msg) {
	}

	/**
	 * 获取要修改的订单明细，如果没有，则新建一条
	 * 
	 * @param details 原订单明细列表
	 * @param dvo 订单明细VO
	 * @return 订单明细
	 */
	protected PurchaseOrderDetail getOrgDetail(Collection<PurchaseOrderDetail> details, PurchaseOrderDetailDto dvo) {
		PurchaseOrderDetail detail = new PurchaseOrderDetail();
		if (details == null || details.size() == 0) {
			return detail;
		}
		for (PurchaseOrderDetail d : details) {
			if (dvo.getRowIds().equals(d.getRowIds())) {
				return d;
			}
		}
		return detail;
	}

	/**
	 * 计算订单总价
	 * 
	 * @param orderVo 采购订单明细
	 * @return 订单总价
	 */
	protected BigDecimal countTotalAmount(PurchaseOrder order) {
		double amount = 0d;
		for (PurchaseOrderDetail d : order.getPurchaseOrderDetails()) {
			if (d.getDeleteFlag() == 0) {
				amount += d.getLineItemValAmt().doubleValue();
			}
		}

		return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * 获取要修改的单位转换对象(只有一条，固定取第一条)
	 * 
	 * @param ucs 原单位转换列表
	 * @param ucvo 单位转换VO
	 * @return 单位转换
	 */
	protected PurchaseDualUnitConversion getOrgUC(Collection<PurchaseDualUnitConversion> ucs, PurchaseOrderUnitConversionDto ucvo) {
		if (ucs == null || ucs.size() == 0) {
			return new PurchaseDualUnitConversion();
		}
		return ucs.iterator().next();
	}

	/**
	 * 获取要修改的定价条件，没有则新建一条
	 * 
	 * @param pricings 原定价条件列表
	 * @param pvo 定价条件VO
	 * @return 定价条件
	 */
	protected PurchaseOrderPricing getOrgPricing(Collection<PurchaseOrderPricing> pricings, PurchaseOrderPricingDto pvo) {
		PurchaseOrderPricing pricing = new PurchaseOrderPricing();
		if (pricings == null || pricings.size() == 0) {
			return pricing;
		}
		for (PurchaseOrderPricing p : pricings) {
			if (p.getPurchaseOrderPricingRowId().equals(pvo.getPurchaseOrderPricingRowId())) {
				return p;
			}
		}
		return pricing;
	}

	/**
	 * 创建定价条件
	 * 
	 * @param d 订单明细
	 * @param vo 定价条件VO
	 * @return 定价条件实体
	 */
	protected PurchaseOrderPricing createPricing(PurchaseOrderPricing p, PurchaseOrderDetail d, PurchaseOrderPricingDto vo,
			PurchaseDualUnitConversion c) {
		// 重新计算价格
		try {
			double amount = vo.getPricingQty() * c.getPricingQty().doubleValue() / vo.getPriceUnit();
			p.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP));
		} catch (Exception e) {
			p.setAmount(p.getAmount());
			e.printStackTrace();
		}

		p.setCurType(vo.getCurType());
		p.setPriceUnit(vo.getPriceUnit().longValue());
		p.setPricingQty(new BigDecimal(vo.getPricingQty()).setScale(2, RoundingMode.HALF_UP));
		p.setPurchaseOrderPricingTypeCode(vo.getPurchaseOrderPricingTypeCode());
		p.setPurchaseOrderPricingTypeName(vo.getPurchaseOrderPricingTypeName());
		p.setRowIds(vo.getRowIds().longValue());
		p.setPurchaseOrderPricingRowId(vo.getPurchaseOrderPricingRowId());
		p.setPurchaseOrderDetail(d);

		p.setPurchaseOrderPricingRowId(vo.getPurchaseOrderPricingRowId());
		p.setPurchaseOrderDetail(d);
		return p;
	}

	/**
	 * 创建订单双单位转换关系
	 * 
	 * @param d 采购订单明细
	 * @param cvo 单位转换VO
	 * @return 单位转换实体
	 */
	protected PurchaseDualUnitConversion createUnitConversion(PurchaseDualUnitConversion c, PurchaseOrderDetail d,
			PurchaseOrderUnitConversionDto cvo) {
		Integer convertDenominator = cvo.getConvertDenominator() != null ? cvo.getConvertDenominator() : 1;
		c.setConvertDenominator(new BigDecimal(convertDenominator));
		Integer convertDenominator2 = cvo.getConvertDenominator2() != null ? cvo.getConvertDenominator2() : 1;
		c.setConvertDenominator2(new BigDecimal(convertDenominator2));
		Integer convertMolecular = cvo.getConvertMolecular() != null ? cvo.getConvertMolecular() : 1;
		c.setConvertMolecular(new BigDecimal(convertMolecular));
		Integer convertMolecular2 = cvo.getConvertMolecular2() != null ? cvo.getConvertMolecular2() : 1;
		c.setConvertMolecular2(new BigDecimal(convertMolecular2));
		// 订单定价数量 = 订单数量*定价单位/订单单位（qty*convertDenominator2/convertMolecular2）
		BigDecimal pricingQty = c.getConvertDenominator2().divide(c.getConvertMolecular2(), 9, RoundingMode.HALF_UP)
				.multiply(d.getBuyerQty());
		// SKU数量 = 订单数量 * 基本单位/订单单位（qty*convertDenominator/convertMolecular）
		BigDecimal skuQty = c.getConvertDenominator().divide(c.getConvertMolecular(), 9, RoundingMode.HALF_UP).multiply(d.getBuyerQty());

		c.setPricingQty(pricingQty);
		c.setPricingUnit(cvo.getPricingUnit());
		c.setOrderDetailUnit(cvo.getOrderDetailUnit());
		c.setOrderDetailUnit2(cvo.getOrderDetailUnit());
		c.setSkuQty(skuQty);
		c.setUnitCode(cvo.getUnitCode());
		c.setPurchaseOrderDetail(d);
		return c;
	}

	/**
	 * 采购订单验证
	 */
	protected ValidVisitor purchaseOrderVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};

	/**
	 * 订单明细验证
	 */
	protected ValidVisitor purchaseOrderDetailVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};

	/**
	 * 定价条件验证
	 */
	protected ValidVisitor purchaseOrderPricingVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};

	/**
	 * 订单BOM验证
	 */
	protected ValidVisitor purchaseOrderBomVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};

	/**
	 * 单位转换验证
	 */
	protected ValidVisitor purchaseOrderUcVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};
}
