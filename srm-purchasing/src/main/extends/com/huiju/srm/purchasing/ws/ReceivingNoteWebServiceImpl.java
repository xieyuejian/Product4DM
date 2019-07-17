
package com.huiju.srm.purchasing.ws;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.srm.commons.utils.IdUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.commons.ws.utils.MessageContent;
import com.huiju.srm.commons.ws.utils.ServiceSupport;
import com.huiju.srm.masterdata.api.CurrencyClient;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.PurchasingGroupClient;
import com.huiju.srm.masterdata.api.PurchasingOrganizationClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.api.TaxRateClient;
import com.huiju.srm.masterdata.entity.Currency;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.PurchasingGroup;
import com.huiju.srm.masterdata.entity.PurchasingOrganization;
import com.huiju.srm.masterdata.entity.StockLocation;
import com.huiju.srm.masterdata.entity.TaxRate;
import com.huiju.srm.purchasing.dao.SendScheduleCommonDao;
import com.huiju.srm.purchasing.dto.ReceivingNoteDto;
import com.huiju.srm.purchasing.dto.WsConstant;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleCommon;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.purchasing.service.CensorQualityService;
import com.huiju.srm.purchasing.service.DeliveryDtlService;
import com.huiju.srm.purchasing.service.DeliveryService;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.purchasing.service.ReceivingNoteService;
import com.huiju.srm.purchasing.service.SendScheduleDetailService;
import com.huiju.srm.purchasing.service.SendScheduleService;
import com.huiju.srm.vendor.dao.VendorPorgDtlDao;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.entity.VendorPorgDtl;
import com.huiju.srm.vendor.service.VendorService;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.handler.SoapMessageMgr;
import com.huiju.srm.ws.service.WsLogService;

/**
 * @author xufq
 */
@Component // 由Spring管理
@WebService(serviceName = "receivingNoteWebService", targetNamespace = "http://www.huiju.com/receivingNote", endpointInterface = "com.huiju.srm.purchasing.ws.ReceivingNoteWebService")
public class ReceivingNoteWebServiceImpl extends ServiceSupport implements ReceivingNoteWebService {

	private static final Logger log = LoggerFactory.getLogger(ReceivingNoteWebServiceImpl.class);
	@Autowired
	protected ReceivingNoteService receivingNoteLogic;
	@Autowired
	protected DeliveryDtlService deliveryDtlLogic;
	@Autowired
	protected DeliveryService deliveryLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected SendScheduleDetailService sendscheduleDetailLogic;
	@Autowired
	protected SendScheduleService sendscheduleLogic;
	@Autowired
	protected PurchasingOrganizationClient purchasingOrganizationLogic;
	@Autowired
	protected PurchasingGroupClient purchasingGroupLogic;
	@Autowired
	protected SendScheduleCommonDao sendscheduleCommonDao;
	@Autowired
	protected ReceivingNoteCheckHelper helper;
	@Autowired
	protected TaxRateClient taxRateLogic;
	@Autowired
	protected VendorService vendorLogic;
	@Autowired
	protected BillSetServiceClient billSetLogic;
	@Autowired
	protected PlantClient plantLogic;
	@Autowired
	protected StockLocationClient stockLocationLogic;
	@Autowired
	protected CurrencyClient currencyLogic;
	@Autowired
	protected MaterialClient materialLogic;
	@Autowired
	protected VendorPorgDtlDao vendorProgDtlEao;
	@Autowired
	protected CensorQualityService censorQualityLogic;// 检验批
	@Autowired
	protected WsLogService wsLogLogic;

	// 请求报文本地线程缓存
	private SoapMessageMgr soapMessageMgr;

	/**
	 * 1. SRM返回消息代码给SAP，从而知道是否同步成功。SRM接收成功则返回S，反之则返回E；
	 * 2.SRM接收时需要进行判断：供应商编码、物料编码、工厂编码、库存地点编码、采购组织编码、币别编码字段需要效验主数据中是否存在，若不存在，则返回E和原因（多种原因需一次性返回，如：物料编码XXX在物料主数据中不存在，库存地点编码xxx在库存地点主数据不存在，请先维护！）
	 * 3. SRM接收数据后，根据物料凭证年度+物料凭证编号+物料凭证中的项目号进行唯一判断，如果系统中不存在该记录，则新增，如果已存在，则忽略。
	 * 4.SRM收到SAP传输的移动类型为“101、105、123、162、411”时，则置为收货；收到SAP传输的移动类型为“102、106、122、161、412”，则置为退货。
	 * 5.根据SAP传输过来的不同凭证，SRM需要做不同的处理
	 * 1）如果SAP传输的数据类型为101、105；则SAP根据该凭证中的物料及工厂到物料工厂关系视图查询物料的质检标志，并将该标志传输给SRM；如果该标志为非限制，则SRM更新该收货记录的库存类型为非限制且质检状态为质检完成；如果该标志为质检，则更新该凭证的库存类型为质检且质检状态为待质检同时根据该收货记录生成对应的检验批（生成检验批的逻辑与SRM收货时生成的逻辑一致，注意生成的质检数量需要为基本数量）
	 * 2）如果SAP传输的数据类型为123、162、411、412；则SRM更新该凭证的库存类型为非限制且质检状态为质检完成；
	 * 3）如果SAP传输的数据为类型为102、106、122、161，则更新该凭证记录的类型为非限制，质检完成状态为质检完成；SRM根据传输过来的原始凭证到SRM质检模块中查找是否有对应的检验批；如果有，则需要对应减少该检验批的可检验量（扣减的量为冲销量且为基本数量，依订单单位与基本单位换算关系进行换算），当可检验量为0时，将检验批置为取消同时将该检验批对应的收货批次的质检状态改为质检完成。
	 * 6. 如果是对送货单进行收货，收到该送货单的收货数据后，更新送货单的状态为收货完成（SAP送货单需要控制只允许一次点收）
	 * 7.收货后，如果是根据送货单收货，则需要回置对应订单的在途量，根据传输过来凭证的送货单明细ID扣减该送货单明细所对应订单明细的在途量（扣减的在途量=该送货明细的送货量，即当前送货单在途量为0。）
	 * 8. 收货后，如果满足订单关闭条件，会将订单关闭；冲销后如果不满足订单关闭条件，会再次将订单打开，将订单状态置为执行 1)
	 * 订单明细关闭条件：订单量*（1-过量交货限度）<=该订单明细的所有收货量-退货量 2)
	 * 订单主单关闭条件：该订单明细所有删除标识为否的明细的关闭状态都为是
	 * 
	 * 9. 收货后，该收货主数据的对账标识默认为未对账；该收货的来源默认来源于sap。
	 * 10.SRM接收数据后，需要计算该收货记录的价格（凭证货币金额未税/PO定价单位数量），价格保留九位小数；需要计算含税金额（凭证货币金额未税*（1+税率）），含税金额保留两位小数；计算含税金额后需要计算税额（含税金额-凭证货币金额未税）；税额保留两位小数；SRM接收数据后，需要默认将订单单位数量默认置值给可冲销数量（不考虑SAP冲销时回置该数量的场景，在SRM控制如果来源为SAP的不允许冲销）
	 * 11、可对账量、可冲销数量（订单单位）、可对账量（定价单位）、订单/定价单位转换系数、订单单位已冲销量、定价单位已冲销数量、取值逻辑如下：
	 * 1）SRM接受数据后（原凭证等于现凭证），即 “物料凭证年度+物料凭证编号+物料凭证行号”
	 * 与“原凭证年度+原凭证编号+原凭证行号”一致，则以下字段赋值如下： 可对账数量（订单单位）reconciliableQty：默认等于“收货数量”、
	 * 可冲销数量（订单单位）canChargeOffNum：默认等于“收货数量”、
	 * 可对账数量（定价单位）invoiceQty：默认等于“PO定价单位数量”fixPriceQty、订单/定价单位转换系数
	 * exchangeRate：收货数量/PO定价单位数量。 2）SRM接受数据后（原凭证不等于现凭证），即
	 * “物料凭证年度+物料凭证编号+物料凭证行号” 与“原凭证年度+原凭证编号+原凭证行号”不一致，则以下字段赋值如下：
	 * 可对账数量（订单单位）：默认等于0、 可对账数量（定价单位）：默认等于0、
	 * 可冲销数量（订单单位）：默认等于“收货数量”、订单/定价单位转换系数：收货数量/PO定价单位数量。
	 * 3）如果生成收货数据时（无论收货/冲销数据），当“收货金额=0”时，“可对账数量（订单单位）”默认赋值为0；“可对账数量（定价单位）”默认赋值为0。
	 * 4）订单单位已冲销量、定价单位已冲销数量默认为0.
	 */
	@Override
	public Message saveOrUpdateReceivingNote(List<ReceivingNoteDto> orders) {
		soapMessageMgr = SoapMessageMgr.getCurrentInstance();
		// 请求参数
		String requestContent = StringUtils.isBlank(soapMessageMgr.getSoapMessageStr()) ? DataUtils.toJson(orders)
				: soapMessageMgr.getSoapMessageStr();
		Calendar.getInstance();
		// 接口编码
		String interfaceCode = SrmConstants.SRM_RECEIVINGNOTESERVICE_CODE;
		IdUtils.genNextId();

		MessageContent content = createContent();
		WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
		try {

			if (!isValid(orders, content)) {

				wslog.setResponseTime(Calendar.getInstance());
				wsLogLogic.addErrorLog(wslog, error(content).toString());
				return error(content);
			}

			// 遍历接口数据，去除当前系统中已经存在的收退货信息
			orders = removeExistData(orders);

			if (orders != null && orders.size() > 0) {
				// ################## END valid ###################
				// 收货单的订单号List
				List<String> purchaseOrderNoList = new ArrayList<String>();
				// 送货单明细ID集合
				List<Long> deliveryDtlIds = new ArrayList<Long>();

				for (ReceivingNoteDto noteVo : orders) {
					noteVo.removeZero();// 去零
					noteVo.setShoppingNoticeDetailId(noteVo.getShoppingNoticeDetailId() == null ? 0L : noteVo.getShoppingNoticeDetailId());
					if (noteVo.getShoppingNoticeDetailId() != null && noteVo.getShoppingNoticeDetailId() != 0L) {
						deliveryDtlIds.add(noteVo.getShoppingNoticeDetailId());
					}
					if (!purchaseOrderNoList.contains(noteVo.getPurchaseOrderNo())) {
						purchaseOrderNoList.add(noteVo.getPurchaseOrderNo());
					}
				}
				// 获取送货单明细
				List<DeliveryDtl> deliveryDtls = new ArrayList<DeliveryDtl>();
				Map<String, Object> searchParams = new HashMap<String, Object>();
				if (deliveryDtlIds != null && deliveryDtlIds.size() > 0) {
					searchParams.put("IN_deliveryDtlId", deliveryDtlIds);
					deliveryDtls = deliveryDtlLogic.findAll(searchParams);
				}
				// 获取相关送货单Id
				List<Long> deliveryIds = new ArrayList<Long>();
				// 送货明细数据集
				Map<String, DeliveryDtl> deliveryDtlMap = new HashMap<String, DeliveryDtl>();
				if (deliveryDtls != null && deliveryDtls.size() > 0) {
					for (DeliveryDtl deliveryDtl : deliveryDtls) {
						if (!deliveryIds.contains(deliveryDtl.getDelivery().getDeliveryId())) {
							deliveryIds.add(deliveryDtl.getDelivery().getDeliveryId());
						}
						deliveryDtlMap.put(deliveryDtl.getDeliveryDtlId().toString(), deliveryDtl);
					}
				}

				// 与本次送货单明细同个送货单的其他明细（不包含本次收货的送货单明细）
				List<DeliveryDtl> deliveryAllDtls = new ArrayList<DeliveryDtl>();
				if (deliveryIds != null && deliveryIds.size() > 0) {
					searchParams.clear();
					searchParams.put("IN_delivery_deliveryId", deliveryIds);
					searchParams.put("EQ_cancelFlag", "0");
					searchParams.put("EQ_closeFlag", "0");
					searchParams.put("NOTIN_deliveryDtlId", deliveryDtlIds);
					deliveryAllDtls = deliveryDtlLogic.findAll(searchParams);
				}

				// 获取订单
				searchParams.clear();
				searchParams.put("IN_purchaseOrder_erpPurchaseOrderNo", purchaseOrderNoList);
				List<PurchaseOrderDetail> orderDtls = purchaseOrderDetailLogic.findAll(searchParams);
				Map<String, PurchaseOrderDetail> orderDtlMap = new HashMap<String, PurchaseOrderDetail>();
				if (orderDtls != null && orderDtls.size() > 0) {
					for (PurchaseOrderDetail dtl : orderDtls) {
						orderDtlMap.put(dtl.getPurchaseOrder().getErpPurchaseOrderNo() + "_" + dtl.getRowIds(), dtl);
					}
				}

				if (!valid(orders, content, deliveryDtlMap, orderDtlMap)) {
					wslog.setResponseTime(Calendar.getInstance());
					wsLogLogic.addErrorLog(wslog, error(content).toString());
					return error(content);
				}

				for (ReceivingNoteDto noteVo : orders) {
					// 构建收退货信息
					createOrder(deliveryDtlMap, orderDtlMap, noteVo, content);
					// 扣减在途等数量
					countDownOrder(deliveryDtlMap, orderDtlMap, noteVo, content);

					String purchaseOrderNo = noteVo.getPurchaseOrderNo();
					if (!purchaseOrderNoList.contains(purchaseOrderNo)) {
						purchaseOrderNoList.add(purchaseOrderNo);
					}

				}

				// 将剩余未收货的送货单明细数量回置
				rebackOtherShoppingNotice(deliveryAllDtls);
				// 将所有收完明细的送货单置为完成
				doFinishShoppingNotice(deliveryDtls);

				// 关闭采购订单
				for (String purchaseOrderNo : purchaseOrderNoList) {
					closePurchaseOrder(purchaseOrderNo);
				}
			}

		} catch (Exception e) {
			log.error("收退货接口出错, 数据量:[{}], 收退货信息{}", orders == null ? 0 : orders.size(), orders, e);
			e.printStackTrace();
			wslog.setResponseTime(Calendar.getInstance());
			wsLogLogic.addFailLog(wslog, failed(e, content).toString());
			return failed(e, content);
		}

		wslog.setResponseTime(Calendar.getInstance());
		wsLogLogic.addSuccessLog(wslog, success(content).toString());
		return success(content);
	}

	/**
	 * 保存或更新收货单
	 * 
	 * @throws ParseException
	 */
	protected ReceivingNote createOrder(Map<String, DeliveryDtl> deliveryDtlMap, Map<String, PurchaseOrderDetail> orderDtlMap,
			ReceivingNoteDto noteVo, MessageContent content) throws ParseException {

		String sflag = "#101#123#162#411";// 收货
		String tflag = "#102#122#161#412";// 退货

		// 新增
		ReceivingNote entity = new ReceivingNote();

		entity.setReceivingNoteNo(billSetLogic.createNextRunningNum("SHD"));
		entity.setClientCode("800");// 未提供客户端编码接口，暂时定为800
		entity.setCreateTime(Calendar.getInstance());
		if (sflag.contains("#" + noteVo.getAcceptReturnFlag())) {
			entity.setAcceptReturnFlag(101l);
		} else if (tflag.contains("#" + noteVo.getAcceptReturnFlag())) {
			entity.setAcceptReturnFlag(102l);
		}

		// 设置收退货数据基本信息
		entity = setBaseInfo(entity, noteVo);

		PurchaseOrderDetail pod = orderDtlMap.get(noteVo.getPurchaseOrderNo() + "_" + noteVo.getRowId());
		if (pod != null) {
			entity.setPurchaseOrderNo(pod.getPurchaseOrder().getPurchaseOrderNo());
			entity.setErpPurchaseOrderNo(pod.getPurchaseOrder().getErpPurchaseOrderNo());
			entity.setPurchaseOrderDetailId(pod.getPurchaseOrderDetailId());

			if (noteVo.getMaterialCode() == null || StringUtils.isBlank(noteVo.getMaterialCode())) {
				entity.setMaterialName(pod.getMaterialName());
			}
		}
		DeliveryDtl snd = deliveryDtlMap.get(noteVo.getShoppingNoticeDetailId().toString());

		if (snd != null) {
			// 带入送货单信息
			entity.setShoppingNoticeNo(snd.getDelivery().getDeliveryCode());
		}

		entity = setSpecialInfo(entity, noteVo);

		// 构建或更新检验批
		entity = setCensorQuality(noteVo, entity);
		entity = receivingNoteLogic.save(entity);

		content.append(getKey(noteVo), "数据处理成功");
		return entity;
	}

	/**
	 * 设置基本信息，数量，金额
	 * 
	 * @param entity 收退货信息
	 * @param noteVo 接口数据
	 * @return
	 */
	private ReceivingNote setBaseInfo(ReceivingNote entity, ReceivingNoteDto noteVo) throws ParseException {

		Vendor vendor = findVendor(noteVo);
		entity.setVendorCode(vendor != null ? vendor.getVendorCode() : entity.getVendorErpCode());
		entity.setVendorErpCode(vendor != null ? vendor.getVendorErpCode() : entity.getVendorErpCode());
		entity.setVendorName(vendor != null ? vendor.getVendorName() : null);

		// 自动带入物料组
		Material materil = helper.getMaterialInfo(noteVo.getMaterialCode());
		if (materil != null) {
			entity.setMaterialName(materil.getMaterialName());
		}

		PurchasingOrganization poz = findPurchasingOrganization(noteVo);
		if (poz != null) {
			// 带入采购组织信息
			entity.setPurchasingOrgName(poz.getPurchasingOrgName());
		}
		entity.setPurchasingGroupCode(noteVo.getPurchasingGroupCode());
		PurchasingGroup pg = findPurchasingGroup(noteVo);
		if (pg != null) {
			// 带入采购组信息
			entity.setPurchasingGroupName(pg.getPurchasingGroupName());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		entity.setErpPurchaseOrderNo(noteVo.getPurchaseOrderNo());
		entity.setPurchaseOrderItem(noteVo.getRowId() == null ? null : String.valueOf(noteVo.getRowId()));
		entity.setMaterialCertificateYear(noteVo.getMaterialCertificateYear());
		entity.setMaterialCertificateCode(noteVo.getMaterialCertificateCode());
		entity.setMaterialCertificateItem(noteVo.getMaterialCertificateItem());
		entity.setShoppingNoticeDetailId(noteVo.getShoppingNoticeDetailId());
		entity.setSpecialwhseFlag(noteVo.getSpecialWhseFlag());
		entity.setStoreLocalCode(noteVo.getStoreLocalCode());
		entity.setVendorErpCode(noteVo.getVendorErpCode());
		entity.setPurchasingOrgCode(noteVo.getBusinessCode());
		entity.setOmaterialCertificateCode(
				noteVo.getOmaterialCertificateCode() == null ? noteVo.getMaterialCertificateCode() : noteVo.getOmaterialCertificateCode());

		entity.setOmaterialCertificateItem(
				noteVo.getOmaterialCertificateItem() == null ? noteVo.getMaterialCertificateItem() : noteVo.getOmaterialCertificateItem());

		entity.setOmaterialCertificateYear(
				noteVo.getOmaterialCertificateYear() == null ? noteVo.getMaterialCertificateYear() : noteVo.getOmaterialCertificateYear());
		// 凭证年度-凭证编码-凭证行号
		entity.setMaterialCertificate(entity.getMaterialCertificateYear() + "-" + entity.getMaterialCertificateCode() + "-"
				+ entity.getMaterialCertificateItem());
		entity.setMaterialCode(noteVo.getMaterialCode());
		entity.setPlantCode(noteVo.getPlantCode());
		entity.setStockUnit(entity.getStockUnit());
		entity.setStockQty(noteVo.getSkuQty());

		Calendar certificateDate = Calendar.getInstance();
		certificateDate.setTime(sdf.parse(noteVo.getCertificateDate()));
		entity.setCertificateDate(certificateDate);
		Calendar postingDate = Calendar.getInstance();
		postingDate.setTime(sdf.parse(noteVo.getPostingDate()));
		entity.setPostingDate(postingDate);
		entity.setQtyReceive(noteVo.getQtyReceive());
		entity.setUnitCode(noteVo.getUnitCode());
		entity.setCurrencyCode(noteVo.getCurrencyCode());
		entity.setTaxCode(noteVo.getTaxCode());

		// SAP GR-Bsd IV标识用于判断预制发票同步sap时是否合并明细"X"是空否
		entity.setRgBsd(noteVo.getRgBsd());
		// 接口，接收数据后，invoiceFlag默认为0
		entity.setInvoiceFlag(0);
		// 来源
		entity.setOrigin(1);
		// 未对账：0(默认)，已对账：1
		entity.setReceiptBillFlag(0);
		return entity;
	}

	/**
	 * 设置特殊信息，数量，金额
	 * 
	 * @param entity 收退货信息
	 * @param noteVo 接口数据
	 * @return
	 */
	private ReceivingNote setSpecialInfo(ReceivingNote entity, ReceivingNoteDto noteVo) {
		// SRM接收数据后，需要计算该收货记录的价格（凭证货币金额未税/PO定价单位数量），价格保留九位小数；
		if (noteVo.getAmountMoney() != null && noteVo.getFixPriceQty() != null) {
			entity.setPrice(noteVo.getAmountMoney().divide(noteVo.getFixPriceQty(), 4, BigDecimal.ROUND_HALF_UP));
		}
		TaxRate tr = getTaxRate(entity.getTaxCode());
		if (tr != null && tr.getTaxRateValue() != null) {
			// 带入税率值
			entity.setTaxRate(tr.getTaxRateValue());
		}
		// 需要计算含税金额（凭证货币金额未税*（1+税率）），含税金额保留两位小数；
		if (noteVo.getAmountMoney() != null && entity.getTaxRate() != null) {
			entity.setTotalAmountAndTax(
					(noteVo.getAmountMoney().multiply((BigDecimal.ONE.add(entity.getTaxRate())))).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		// 计算含税金额后需要计算税额（含税金额-凭证货币金额未税）；税额保留两位小数；
		if (noteVo.getAmountMoney() != null && entity.getTotalAmountAndTax() != null) {
			entity.setTotalTax((entity.getTotalAmountAndTax().subtract(noteVo.getAmountMoney())).setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		// 库存类型 质检标识
		if ("X".equals(noteVo.getStockType()) && (Long.valueOf(101).equals(noteVo.getAcceptReturnFlag()))) {
			entity.setStockType("X");// 质检
			entity.setStatus(CensorQualityState.TOCHECK);
		} else if (Long.valueOf(123).equals(noteVo.getAcceptReturnFlag()) || Long.valueOf(162).equals(noteVo.getAcceptReturnFlag())
				|| Long.valueOf(411).equals(noteVo.getAcceptReturnFlag()) || Long.valueOf(412).equals(noteVo.getAcceptReturnFlag())
				|| Long.valueOf(102).equals(noteVo.getAcceptReturnFlag()) || Long.valueOf(122).equals(noteVo.getAcceptReturnFlag())
				|| Long.valueOf(161).equals(noteVo.getAcceptReturnFlag()) || StringUtils.isBlank(noteVo.getStockType())) {
			entity.setStockType("A");
			entity.setStatus(CensorQualityState.CHECKED);
		} else {
			entity.setStockType("X");// 质检
			entity.setStatus(CensorQualityState.TOCHECK);
		}

		// 判断特殊库存标示
		if ("K".equalsIgnoreCase(noteVo.getSpecialWhseFlag())) {// 寄售
			entity.setSpecialwhseFlag("K");

		} else if ("O".equalsIgnoreCase(noteVo.getSpecialWhseFlag())) {// /分包
			entity.setSpecialwhseFlag("O");
		} else {
			// 接口，接收接口数据时，特殊库存标识0
			entity.setSpecialwhseFlag("0");
		}
		entity.setFixPriceQty(noteVo.getFixPriceQty());
		entity.setFixPriceUnitCode(noteVo.getFixPriceUnitCode());
		entity.setAmountnoTax(noteVo.getAmountMoney() == null ? BigDecimal.ZERO : noteVo.getAmountMoney());

		if (entity.getMaterialCertificateCode().equals(entity.getOmaterialCertificateCode())
				&& entity.getMaterialCertificateItem().equals(entity.getOmaterialCertificateItem())
				&& entity.getMaterialCertificateYear().equals(entity.getOmaterialCertificateYear()) && noteVo.getAmountMoney() != null) {
			/**
			 * SRM接受数据后（原凭证等于现凭证）
			 * 
			 * 可对账数量（订单单位）reconciliableQty：默认等于“收货数量”、
			 * 可冲销数量（订单单位）canChargeOffNum：默认等于“收货数量”、
			 * 可对账数量（定价单位）invoiceQty：默认等于“PO定价单位数量”fixPriceQty、 订单/定价单位转换系数
			 * exchangeRate：收货数量/PO定价单位数量。
			 */
			entity.setCanChargeOffNum(noteVo.getQtyReceive());
			entity.setReconciliableQty(noteVo.getQtyReceive());

		} else {
			/**
			 * 可对账数量（订单单位）：默认等于0, 可对账数量（定价单位）：默认等于0、 可冲销数量（订单单位）：默认等于“收货数量”、
			 * 订单/定价单位转换系数：收货数量/PO定价单位数量。
			 */
			entity.setCanChargeOffNum(BigDecimal.ZERO);
			entity.setReconciliableQty(BigDecimal.ZERO);
		}
		entity.setInvoiceQty(noteVo.getFixPriceQty());
		BigDecimal exchangeRate = entity.getFixPriceQty().divide(entity.getQtyReceive(), 3, RoundingMode.HALF_UP);
		entity.setExchangeRate(exchangeRate);

		if (entity.getAmountnoTax().compareTo(BigDecimal.ZERO) == 0) {
			entity.setInvoiceQty(BigDecimal.ZERO); // 可对账数量（定价单位）
			entity.setReconciliableQty(BigDecimal.ZERO); // 可对账数量（订单单位）
		}

		return entity;
	}

	/**
	 * 构建检验批数据
	 * 
	 * @param noteVo 收退货接口dto
	 * @param entity 收退货信息
	 * @return
	 */
	private ReceivingNote setCensorQuality(ReceivingNoteDto noteVo, ReceivingNote entity) {
		// 1）如果SAP传输的数据类型为101、105，并将该标志传输给SRM；
		// 如果该标志为非限制，则SRM更新该收货记录的库存类型为非限制且质检状态为质检完成；
		// 如果该标志为质检，则更新该凭证的库存类型为质检且质检状态为待质检同时根据该收货记录生成对应的检验批（生成检验批的逻辑与SRM收货时生成的逻辑一致，注意生成的质检数量需要为SKU数量）
		// 2）如果SAP传输的数据为类型为102、106、122、161，则更新该凭证记录的类型为非限制，质检完成状态为质检完成；
		// SRM根据传输过来的原始凭证到SRM质检模块中查找是否有对应的检验批；如果有，则需要对应减少该检验批的可检验量（扣减的量为冲销量且为SKU单位数量），
		// 当可检验量为0时，将检验批置为取消同时将该检验批对应的收货批次的质检状态改为质检完成
		if ("X".equals(noteVo.getStockType()) && Long.valueOf(101).equals(noteVo.getAcceptReturnFlag())) {
			createCensorQuality(entity, noteVo);
		} else if (Long.valueOf(102).equals(noteVo.getAcceptReturnFlag()) || Long.valueOf(122).equals(noteVo.getAcceptReturnFlag())
				|| Long.valueOf(161).equals(noteVo.getAcceptReturnFlag())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("EQ_voucherYear", noteVo.getOmaterialCertificateYear());
			map.put("EQ_voucherNo", noteVo.getOmaterialCertificateCode());
			map.put("EQ_voucherProNo", noteVo.getOmaterialCertificateItem());
			CensorQuality c = censorQualityLogic.findOne(map);
			if (c != null) {
				if (!CensorQualityState.CHECKED.equals(c.getStatus())) {
					// 检验完成不能继续操作
					c.setCensorQty(c.getCensorQty().subtract(noteVo.getSkuQty()));
					c.setCanCheckQty(c.getCanCheckQty().subtract(noteVo.getSkuQty()));
					if (c.getCanCheckQty().compareTo(BigDecimal.ZERO) != 1) {
						c.setStatus(CensorQualityState.CANCEL);
					}
					censorQualityLogic.save(c);
				}
			}
		} else {
			// 自动将检验标识 -> 2(已经完成)
			entity.setStatus(CensorQualityState.CHECKED);
		}
		return entity;
	}

	/**
	 * 每次点收，都生成一个检验批次
	 * 
	 * @param rn 收退货信息
	 * @param noteVo 收退货信息
	 */
	protected void createCensorQuality(ReceivingNote rn, ReceivingNoteDto noteVo) {
		CensorQuality c = new CensorQuality();

		c.setCensorqualityNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_ZJD));
		c.setClientCode("800");

		c.setReceivingNoteNo(rn.getReceivingNoteNo());
		c.setPurchaseOrderNo(rn.getErpPurchaseOrderNo());
		c.setRowIds(Long.parseLong(rn.getPurchaseOrderItem()));
		c.setMaterialCode(rn.getMaterialCode());
		c.setMaterialName(rn.getMaterialName());
		c.setVendorCode(rn.getVendorCode());
		c.setVendorName(rn.getVendorName());
		c.setVendorErpCode(rn.getVendorErpCode());
		c.setUnit(rn.getStockUnit());
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

		c.setPurchasingOrgCode(rn.getPurchasingOrgCode());
		c.setPurchasingOrgName(rn.getPurchasingOrgName());
		c.setPlantCode(rn.getPlantCode());
		c.setStockCode(rn.getStoreLocalCode());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_plantCode", rn.getPlantCode());
		Plant plant = plantLogic.findOne(params);
		if (null != plant) {
			c.setPlantName(plant.getPlantName());
		}

		params.clear();
		params.put("EQ_stockLocationCode", rn.getStoreLocalCode());
		StockLocation location = stockLocationLogic.findOne(params);

		if (null != location) {
			c.setStockName(location.getStockLocationName());
		}

		c.setStockCode(rn.getStoreLocalCode());
		c.setVoucherYear(rn.getMaterialCertificateYear());// 凭证年度
		c.setVoucherNo(rn.getMaterialCertificateCode());// 编号
		c.setVoucherProNo(rn.getMaterialCertificateItem());// 行项目号

		c.setInspectionTime(Calendar.getInstance());// 送检时间

		censorQualityLogic.save(c);
	}

	/**
	 * 遍历接口数据，去除当前系统中已经存在的收退货信息
	 * 
	 * @param orders
	 * @return
	 */
	protected List<ReceivingNoteDto> removeExistData(List<ReceivingNoteDto> orders) {
		// TODO Auto-generated method stub
		List<ReceivingNoteDto> newDatas = new ArrayList<ReceivingNoteDto>();
		for (ReceivingNoteDto noteVo : orders) {
			ReceivingNote entity = findReceiveingNote(noteVo);
			if (entity == null) {
				newDatas.add(noteVo);
			}
		}

		return newDatas;
	}

	protected void doFinishShoppingNotice(List<DeliveryDtl> deliveryOtherDtls) {
		if (deliveryOtherDtls != null && deliveryOtherDtls.size() > 0) {
			for (DeliveryDtl snd : deliveryOtherDtls) {
				Delivery sn = snd.getDelivery();
				sn.setStatus(DeliveryState.CLOSE);
				deliveryLogic.save(sn);
			}
		}
	}

	/**
	 * 该送货单其他明细关闭
	 * 
	 * @param deliveryOtherDtls
	 */
	protected void rebackOtherShoppingNotice(List<DeliveryDtl> deliveryOtherDtls) {
		if (deliveryOtherDtls != null && deliveryOtherDtls.size() > 0) {
			for (DeliveryDtl snd : deliveryOtherDtls) {
				// 更新相关的送货单明细
				snd.setReceivedNumber(BigDecimal.ZERO);// 点收量
				snd.setCreateTime(Calendar.getInstance());// 点收时间
				snd.setCloseFlag("1");
				deliveryDtlLogic.save(snd);

				if (snd.getDataFrom() == 1) {
					this.subPOD(snd, snd.getOrderDetailId());
				} else if (snd.getDataFrom() == 2) {
					this.subSSD(snd);
				}
			}
		}

	}

	/**
	 * 7.收货后，如果是根据送货单收货，则需要回置对应订单的在途量，根据传输过来凭证的送货单明细ID扣减该送货单明细所对应订单明细的在途量（扣减的在途量=该送货明细的送货量，即当前送货单在途量为0。）
	 * 
	 * @param deliveryDtlMap 送货单明细集合
	 * @param orderDtlMap 采购订单明细几个
	 * @param noteVo 接口传递数据
	 * @param content
	 */
	protected void countDownOrder(Map<String, DeliveryDtl> deliveryDtlMap, Map<String, PurchaseOrderDetail> orderDtlMap,
			ReceivingNoteDto noteVo, MessageContent content) {
		String key = getKey(noteVo);
		String sflag = "#101#123#162#411";
		String tflag = "#102#122#161#412";
		// 接口接收数量
		final BigDecimal qtyOfInput = noteVo.getQtyReceive();

		// 更新送货单
		String deliveryDtlId = noteVo.getShoppingNoticeDetailId() == null ? "" : noteVo.getShoppingNoticeDetailId().toString();
		DeliveryDtl snd = deliveryDtlMap.get(deliveryDtlId);

		if (snd != null && sflag.contains("#" + noteVo.getAcceptReturnFlag())) {
			// 收货
			// 更新相关的送货单明显
			snd.setReceivedQty(qtyOfInput);
			snd.setIsFinish("1");// 已收货状态

			deliveryDtlLogic.save(snd);
			content.append(key, "相关送货单[" + snd.getDeliveryDtlId() + "]收货完成");

		} else if (snd != null && tflag.contains("#" + noteVo.getAcceptReturnFlag())) {
			// 退货
			// 更新相关的送货单明显
			snd.setReturnNumber(qtyOfInput);// 退货量
			snd.setIsFinish("1");// 已收货状态

			deliveryDtlLogic.save(snd);
			content.append(key, "相关送货单[" + snd.getDeliveryDtlId() + "]收货完成");
		}

		PurchaseOrderDetail pod = orderDtlMap.get(noteVo.getPurchaseOrderNo() + "_" + noteVo.getRowId());
		if (pod != null) {
			// 更新相关的采购订单明细
			if (sflag.contains("#" + noteVo.getAcceptReturnFlag())) {
				pod.getVendorQty();
				pod.getVendorQty();
				if (pod.getIsReturn() == 1) {
					// 退货的采购订单
					// 已有退货量
					BigDecimal qtyQuit = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();
					pod.setQtyQuit(qtyQuit.add(qtyOfInput));// 退货数量累加
					boolean canClose = purchaseOrderDetailLogic.canClosePod(pod);
					if (canClose) {
						pod.setCloseFlag(1);
					}

				} else {
					// 收货时需要更新对应的订单明细 和 排程子明细 如果改订单明细的可排程标识为1时
					// 收货更新到货数量
					BigDecimal qtyOfArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
					pod.setQtyArrive(qtyOfArrive.add(noteVo.getQtyReceive()));// 到货数量累加
					BigDecimal qtyOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();
					if (snd != null) {
						qtyOnline = qtyOnline.subtract(snd.getDeliveryNumber());
						pod.setQtyOnline(qtyOnline);
					}
					// 可送货量 等于 需求量 - 含本次已收货量 - （在途量-本次送货数量） + 退货量
					pod = purchaseOrderDetailLogic.recountCanSendQty(pod);

					boolean canClose = purchaseOrderDetailLogic.canClosePod(pod);

					// 订单行明细删除标志为否，关闭标识为否、收货数量达到最低收货量（订单数量*（1-交货不足限度））时，并且订单的在途量为0时，系统行明细自动关闭（20190613）
					if (canClose) {
						pod.setCloseFlag(1);
					}

					if (snd != null) {
						// 有送货单才扣减在途数量
						BigDecimal deliveryQty = snd.getDeliveryNumber() == null ? BigDecimal.ZERO : snd.getDeliveryNumber();// 采购订单对应的送货单的送货数量

						// 如果该采购明细排程为1则 同步更新排程明细子明细 相关数量
						if (pod.getScheduleFlag() == 1) {
							// 找到送货单明细
							DeliveryDtl sn = deliveryDtlLogic.findById(noteVo.getShoppingNoticeDetailId());
							SendScheduleDetail ssd = sendscheduleDetailLogic.findById(sn.getOrderDetailId());
							if (ssd != null) {
								// 收货量累计
								BigDecimal receiptQty = ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty();
								ssd.setReceiptQty(receiptQty.add(noteVo.getQtyReceive()));

								// 该排程明细的在途量为根据该明细所建送货单明细的送货数量
								BigDecimal onWayQty = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();// 排程明细的在途量

								ssd.setOnWayQty(onWayQty.subtract(deliveryQty));// 在途量减少

								// 可送货量 等于 需求量 - 在途量 - 收获量 + 退货量
								// 获取需求量
								BigDecimal scheduleQty = ssd.getScheduleQty() == null ? BigDecimal.ZERO : ssd.getScheduleQty();
								BigDecimal canSendQty = scheduleQty
										.subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
										.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
										.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
								ssd.setCanSendQty(canSendQty);

								// 根据明细收货数量来回置对应的排程明细
								// 如果收货量等于零则排程子明细的送货标识为未送货
								// 如果收货量不等于零并且小于需求量则送货标识为部分收货
								// 如果收货量等于或大于需求量则送货标识为已送货

								if (ssd.getReceiptQty().compareTo(BigDecimal.ZERO) == 0) {
									ssd.setSendFlag(0);
								} else if (ssd.getReceiptQty().compareTo(scheduleQty) == 1
										|| ssd.getReceiptQty().compareTo(scheduleQty) == 0) {
									ssd.setSendFlag(2);
								} else {
									ssd.setSendFlag(1);
								}

								SendScheduleDetail ssdNew = sendscheduleDetailLogic.save(ssd);

								content.append(key, "相关排程单[" + ssdNew.getSendScheduleDetailId() + "]更新完成");
							}
						}
					}
				}

			} else if (tflag.contains("#" + noteVo.getAcceptReturnFlag())) {
				// 退货时不用更新排程单 只要更新对应的订单明细

				if (qtyOfInput.doubleValue() > 0) {
					if (pod.getIsReturn() == 1) {
						BigDecimal qtyOfArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
						pod.setQtyArrive(qtyOfArrive.add(qtyOfInput));// 到货数量累加
					} else {
						BigDecimal quitCount = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();
						pod.setQtyQuit(quitCount.add(qtyOfInput));// 退货数量累加
					}

					// 重新打开采购订单以及采购订单明细
					boolean canClose = purchaseOrderDetailLogic.canClosePod(pod);
					if (!canClose) {
						pod.setCloseFlag(0);
						PurchaseOrder po = pod.getPurchaseOrder();
						po.setPurchaseOrderState(PurchaseOrderState.OPEN);
						purchaseOrderLogic.save(po);
					}

				}
			}

			purchaseOrderDetailLogic.save(pod);
			content.append(key, "相关的采购订单明细更新完成");

		}
	}

	/**
	 * 接口校验
	 * 
	 * @param orders 接口传递收退货信息
	 * @param content 校验信息内容
	 * @param deliveryDtlMap 送货单明细
	 * @param orderDtlMap 订单明细
	 * @return boolean 校验结果
	 */
	public boolean valid(List<ReceivingNoteDto> orders, MessageContent content, Map<String, DeliveryDtl> deliveryDtlMap,
			Map<String, PurchaseOrderDetail> orderDtlMap) {
		StringBuilder sb = new StringBuilder();
		String sflag = "#101#123#411#161";
		String tflag = "#102#122#412#162";

		boolean isValid = true;
		// 物料
		// 采购组织
		List<String> materialList = new ArrayList<String>();
		Map<String, Object> materialMap = new HashMap<String, Object>();
		// 采购组织
		List<String> purchasingOrgList = new ArrayList<String>();
		Map<String, Object> purchasingOrgMap = new HashMap<String, Object>();
		// 供应商
		List<String> vendorList = new ArrayList<String>();
		Map<String, Object> vendorMap = new HashMap<String, Object>();
		// 库存地点
		List<String> storeLocalList = new ArrayList<String>();
		Map<String, Object> storeLocalMap = new HashMap<String, Object>();
		// 工厂
		List<String> plantList = new ArrayList<String>();
		Map<String, Object> plantMap = new HashMap<String, Object>();
		// 单位
		List<String> currencyList = new ArrayList<String>();
		Map<String, Object> currencyMap = new HashMap<String, Object>();
		for (ReceivingNoteDto rn : orders) {
			if (StringUtils.isNotBlank(rn.getMaterialCode())) {
				materialList.add(rn.getMaterialCode());
			}
			if (StringUtils.isNotBlank(rn.getBusinessCode())) {
				purchasingOrgList.add(rn.getBusinessCode());
			}
			if (StringUtils.isNotBlank(rn.getVendorErpCode())) {
				vendorList.add(removeZero(rn.getVendorErpCode()));
			}
			if (StringUtils.isNotBlank(rn.getStoreLocalCode())) {
				storeLocalList.add(rn.getStoreLocalCode());
			}
			if (StringUtils.isNotBlank(rn.getPlantCode())) {
				plantList.add(rn.getPlantCode());
			}
			if (StringUtils.isNotBlank(rn.getCurrencyCode())) {
				currencyList.add(rn.getCurrencyCode());
			}
		}
		Map<String, Object> searchParams = new HashMap<String, Object>();
		if (materialList != null && materialList.size() > 0) {

			searchParams.put("IN_materialCode", StringUtils.join(materialList, ","));
			List<Material> materials = materialLogic.findAll(new FeignParam<Material>(searchParams));
			if (materials != null && materials.size() > 0) {
				for (Material material : materials) {
					materialMap.put(material.getMaterialCode(), material);
				}
			}
			searchParams.clear();
		}
		if (purchasingOrgList != null && purchasingOrgList.size() > 0) {
			searchParams.put("IN_purchasingOrgCode", StringUtils.join(purchasingOrgList, ","));
			List<PurchasingOrganization> purchasingOrgs = purchasingOrganizationLogic
					.findAll(new FeignParam<PurchasingOrganization>(searchParams));
			if (purchasingOrgs != null && purchasingOrgs.size() > 0) {
				for (PurchasingOrganization org : purchasingOrgs) {
					purchasingOrgMap.put(org.getPurchasingOrgCode(), org);
				}
			}
			searchParams.clear();
		}
		if (vendorList != null && vendorList.size() > 0) {
			searchParams.put("IN_vendor_vendorErpCode", StringUtils.join(vendorList, ","));
			List<VendorPorgDtl> VendorPorgDtls = vendorProgDtlEao.findAll(searchParams);
			if (VendorPorgDtls != null && VendorPorgDtls.size() > 0) {
				for (VendorPorgDtl vendorProgDtl : VendorPorgDtls) {
					vendorMap.put(vendorProgDtl.getVendor().getVendorErpCode() + "_" + vendorProgDtl.getPurchasingOrgCode(), vendorProgDtl);
				}
			}
			searchParams.clear();
		}

		if (storeLocalList != null && storeLocalList.size() > 0) {
			searchParams.put("IN_stockLocationCode", StringUtils.join(storeLocalList, ","));
			List<StockLocation> storeLocals = stockLocationLogic.findAll(new FeignParam<StockLocation>(searchParams));
			if (storeLocals != null && storeLocals.size() > 0) {
				for (StockLocation storeLocal : storeLocals) {
					storeLocalMap.put(storeLocal.getStockLocationCode(), storeLocal);
				}
			}
			searchParams.clear();
		}

		if (plantList != null && plantList.size() > 0) {
			searchParams.put("IN_plantCode", StringUtils.join(plantList, ","));
			List<Plant> plants = plantLogic.findAll(new FeignParam<Plant>(searchParams));
			if (plants != null && plants.size() > 0) {
				for (Plant plant : plants) {
					plantMap.put(plant.getPlantCode(), plant);
				}
			}
			searchParams.clear();
		}

		if (currencyList != null && currencyList.size() > 0) {
			searchParams.put("IN_currencyCode", StringUtils.join(currencyList, ","));
			List<Currency> currencys = currencyLogic.findAll(new FeignParam<Currency>(searchParams));
			if (currencys != null && currencys.size() > 0) {
				for (Currency currency : currencys) {
					currencyMap.put(currency.getCurrencyCode(), currency);
				}
			}
			searchParams.clear();
		}

		for (ReceivingNoteDto rn : orders) {

			// 退货标识检查 101、105、123、162、411收货, 102、106、122、161、412退货
			if (!(sflag.contains("#" + rn.getAcceptReturnFlag()) || tflag.contains("#" + rn.getAcceptReturnFlag()))) {
				sb.append(",错误的收退货标识,");
			}

			// ========================== 送货单检验 =========================
			// SAP如果有传入送货单ID 检查送货单必须存在并收货数量必须小于送货单的数量
			DeliveryDtl snd = deliveryDtlMap.get(rn.getShoppingNoticeDetailId().toString());

			if (rn.getShoppingNoticeDetailId() != null && rn.getShoppingNoticeDetailId() != 0L && snd == null) {
				if (!"K".equals(rn.getSpecialWhseFlag())) {// /当为寄售时不要判断
					sb.append(",未找到指定的送货单明细[").append(rn.getShoppingNoticeDetailId()).append("]");
				}
			} else if (snd != null) {

				// 送货单已经取消
				if ("1".equals(snd.getCancelFlag())) {
					sb.append(",收货单关联的送货单明细[" + snd.getDeliveryDtlId() + "]已经取消");
				}

				// 退货不要判断收货标识
				if (!tflag.contains("#" + rn.getAcceptReturnFlag())) {
					// 送货单 已经收货则不允许重复收货
					if ("1".equals(snd.getIsFinish())) {
						sb.append(",收货单对应的送货单明细[" + snd.getDeliveryDtlId() + "]已经完成收货请不要重复收货");
					}
				}
			}

			// ===========================采购订单检验======================
			// 如果有传入采购订单明细的信息,存在检查
			if (rn.getPurchaseOrderNo() != null && rn.getRowId() != null) {

				PurchaseOrderDetail pod = orderDtlMap.get(rn.getPurchaseOrderNo() + "_" + rn.getRowId());
				if (pod == null) {
					sb.append(",未找到指定的采购订单明细");
				} else {

					// 采购订单明细已标删除
					if (Integer.valueOf(1).equals(pod.getDeleteFlag())) {
						sb.append(",指定的采购订单明细已标记为删除");
					}
					// SAP采购组编码
					if (rn.getPurchasingGroupCode() == null) {
						sb.append(",sap采购组编码不能为空");
					}

					// 采购订单状态, 执行 + 审核通过 + 已同步 + (供应商接受/供应商确认变更) 的订单才允许收货
					PurchaseOrder po = pod.getPurchaseOrder();
					if (!access(po)) {// 不满足收货条件
						sb.append(",采购订单不满足收货必要条件, 收货必要条件:已同步");
					}

					/*
					 * if (tflag.contains("#" + rn.getAcceptReturnFlag())) { //
					 * 取原物料凭证一样的收退货单据的可冲销量 BigDecimal maxRefundQty =
					 * purchaseOrderDetailLogic.getMaxRefundQty(pod); if
					 * (rn.getQtyReceive() != null && maxRefundQty.doubleValue()
					 * < rn.getQtyReceive().doubleValue()) {
					 * sb.append(",退货量[").append(rn.getQtyReceive()).append(
					 * "]大于采购订单明细最大可退量[").append(maxRefundQty).append("]"); } }
					 */

				}
			}

			// ========================物料检验=====================
			// 检查物料凭证号
			if (!materialMap.containsKey(rn.getMaterialCode())) {
				sb.append(",未找到指定的物料[").append(rn.getMaterialCode()).append("]");
			}

			// ===============================验证采购组织============
			if (!purchasingOrgMap.containsKey(rn.getBusinessCode())) {
				sb.append(",未找到采购组织[").append(rn.getBusinessCode()).append("]");
			}

			// ===============================验证供应商是否在对的采购组织有============
			if (!vendorMap.containsKey(rn.getVendorErpCode() + "_" + rn.getBusinessCode())) {
				sb.append(",在采购组织[").append(rn.getBusinessCode()).append("]下未找到指定供应商[").append(rn.getVendorErpCode()).append("]");
			}

			// ===============================验证库存地点编码============
			if (!storeLocalMap.containsKey(rn.getStoreLocalCode())) {
				sb.append(",未找到库存地点编码[").append(rn.getStoreLocalCode()).append("]");
			}

			// ===============================验证工厂============
			if (!plantMap.containsKey(rn.getPlantCode())) {
				sb.append(",未找到工厂编码[").append(rn.getPlantCode()).append("]");
			}

			// ===============================验证币别============
			if (!currencyMap.containsKey(rn.getCurrencyCode())) {
				sb.append(",未找到币别编码[").append(rn.getCurrencyCode()).append("]");
			}

			if (sb.length() > 0) {
				content.append(getKey(rn), sb.substring(1).toString());
				isValid = false;
			}
		}

		return isValid;
	}

	/**
	 * 判断采购订单是否允许收货.
	 * <p>
	 * 必要条件: 执行 + 审核通过 + 已同步 + (供应商接受/供应商确认变更)
	 * 
	 * @param po
	 * @return
	 */
	protected boolean access(PurchaseOrder po) {
		// 已同步
		if (WsConstant.SYNC_SYNCHRONIZED.equals(po.getErpSynState())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断送货排程单是否允许收货.
	 * <p>
	 * 必要条件: 执行 + 已同步
	 * 
	 * @param po
	 * @return
	 */
	protected boolean checkStatus(Long shoppingNoticeDetailId) {
		// 找到对应的送货单明细
		DeliveryDtl snd = deliveryDtlLogic.findById(shoppingNoticeDetailId);
		// 找到对应的排程单
		SendScheduleCommon ssd = sendscheduleCommonDao.getById(snd.getOrderId());
		if (ssd.getSendSchedule() != null) {
			if (SendScheduleState.OPEN.equals(ssd.getSendSchedule().getSendScheduleState())
					&& WsConstant.SYNC_SYNCHRONIZED.equals(ssd.getSendSchedule().getErpSynState())) {
				// 执行 + 已同步
				return true;
			}
		}
		return false;
	}

	/**
	 * 关闭采购订单 订单主单关闭条件：该订单明细所有删除标识为否的明细的关闭状态都为是
	 * 
	 * @param purchaseOrderNo 采购订单号
	 */
	protected void closePurchaseOrder(String purchaseOrderNo) {
		PurchaseOrder purchaseOrder = findPurchaseOrder(purchaseOrderNo);
		// 取未删除的细单，判断细单是否关闭，如果细单全为关闭，主单状态置为“关闭”
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchaseOrder_purchaseOrderId", purchaseOrder.getPurchaseOrderId());
		params.put("EQ_deleteFlag", 0);
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailLogic.findAll(params);
		if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
			int count = 0;
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

	protected Vendor findVendor(ReceivingNoteDto noteVo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_vendorErpCode", noteVo.getVendorErpCode());
		return vendorLogic.findOne(params);
	}

	/**
	 * 根据物料凭证年度+物料凭证编号+物料凭证中的项目号作为唯一条件
	 * 
	 * @return 未找到返回{@code null}
	 */
	protected ReceivingNote findReceiveingNote(ReceivingNoteDto noteVo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_materialCertificateYear", noteVo.getMaterialCertificateYear());
		params.put("EQ_materialCertificateCode", noteVo.getMaterialCertificateCode());
		params.put("EQ_materialCertificateItem", noteVo.getMaterialCertificateItem());
		return receivingNoteLogic.findOne(params);
	}

	/**
	 * purchaseOrderNo 查找采购订单
	 * 
	 * @return 未找到返回{@code null}
	 */
	protected PurchaseOrder findPurchaseOrder(String purchaseOrderNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_erpPurchaseOrderNo", purchaseOrderNo);
		return purchaseOrderLogic.findOne(params);
	}

	/**
	 * purchaseOrderNo + rowId 关联采购订单明细
	 * 
	 * @return 未找到返回{@code null}
	 */
	protected PurchaseOrderDetail findPurchaseOrderDetail(ReceivingNoteDto noteVo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchaseOrder_erpPurchaseOrderNo", noteVo.getPurchaseOrderNo());
		params.put("EQ_rowIds", noteVo.getRowId());
		return purchaseOrderDetailLogic.findOne(params);
	}

	/**
	 * 根据送货单明细行ID关联查询 送货单明细
	 * 
	 * @return 未找到返回{@code null}
	 */
	protected DeliveryDtl findShoppingNoticeDetail(ReceivingNoteDto noteVo) {
		if (noteVo.getShoppingNoticeDetailId() == null) {
			return null;
		}

		return deliveryDtlLogic.findById(noteVo.getShoppingNoticeDetailId());
	}

	/**
	 * 根据采购组织编码关联查询采购组织
	 * 
	 * @param noteVo
	 * 
	 * @return未找到返回{@code null}
	 */
	protected PurchasingOrganization findPurchasingOrganization(ReceivingNoteDto noteVo) {
		if (noteVo.getBusinessCode() == null) {
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchasingOrgCode", noteVo.getBusinessCode());
		return purchasingOrganizationLogic.findOne(params);
	}

	/**
	 * 根据采购组编码关联查询 采购组
	 * 
	 * @param noteVo
	 * @return
	 */
	protected PurchasingGroup findPurchasingGroup(ReceivingNoteDto noteVo) {
		if (noteVo.getPurchasingGroupCode() == null) {
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchasingGroupCode", noteVo.getPurchasingGroupCode());
		return purchasingGroupLogic.findOne(params);
	}

	/**
	 * 根据采购组编码关联查询 采购组
	 * 
	 * @param order
	 * @return
	 */
	protected PurchasingGroup findPurchasingGroupByCode(String code) {
		if (code == null) {
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchasingGroupCode", code);
		return purchasingGroupLogic.findOne(params);
	}

	/**
	 * 获取税率值
	 * 
	 * @param order 收货的信息记录
	 * @return 税率值
	 */
	protected TaxRate getTaxRate(String taxCode) {
		if (taxCode == null) {
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_taxRateCode", taxCode);
		return taxRateLogic.findOne(params);

	}

	/**
	 * 验证采购组织是否存在
	 * 
	 * @param rn sap 收货信息
	 * @return true 存在 false 不存在
	 */
	protected boolean checkPurchasingOrganization(ReceivingNoteDto rn) {
		// 系统中是否存在该供应商
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_purchasingOrgCode", rn.getBusinessCode());
		PurchasingOrganization p = purchasingOrganizationLogic.findOne(params);
		if (p == null) {
			return false;
		}
		return true;
	}

	/**
	 * 验证对应点供应商是否存在
	 * 
	 * @param rn sap 收货信息
	 * @return true 存在 false 不存在
	 */
	protected boolean checkVendorErpCodeIsHas(ReceivingNoteDto rn) {
		// 系统中是否存在该供应商
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_vendorErpCode", removeZero(rn.getVendorErpCode()));
		Vendor vd = vendorLogic.findOne(params);
		if (vd == null) {
			return false;
		} else {
			// 如果存在则判断是否是该采购组织下的
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("EQ_vendor_vendorId", vd.getVendorId());
			params1.put("EQ_purchasingOrgCode", rn.getBusinessCode());
			VendorPorgDtl vdpo = vendorLogic.findVendorPorgDtl(params1);

			if (vdpo == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证库存地点是否存在
	 * 
	 * @param rn sap 收货信息
	 * @return true 存在 false 不存在
	 */
	protected boolean checkStoreLocalCode(ReceivingNoteDto rn) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_stockLocationCode", rn.getStoreLocalCode());
		StockLocation s = stockLocationLogic.findOne(params);
		if (s == null) {
			return false;
		}
		return true;
	}

	/**
	 * 验证工厂是否存在
	 * 
	 * @param rn sap 收货信息
	 * @return true 存在 false 不存在
	 */
	protected boolean checkPlantCode(ReceivingNoteDto rn) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_plantCode", rn.getPlantCode());
		Plant p = plantLogic.findOne(params);
		if (p == null) {
			return false;
		}
		return true;
	}

	/**
	 * 验证币别是否存在
	 * 
	 * @param rn sap 收货信息
	 * @return true 存在 false 不存在
	 */
	protected boolean checkCurrencyCode(ReceivingNoteDto rn) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_currencyCode", rn.getCurrencyCode());
		Currency c = currencyLogic.findOne(params);
		if (c == null) {
			return false;
		}
		return true;
	}

	/**
	 * 去零
	 * 
	 * @return 返回去零后的值
	 */
	protected String removeZero(String sourceData) {
		if (StringUtils.isBlank(sourceData)) {
			return sourceData;
		}

		boolean flag = true;
		while (flag) {
			flag = sourceData.startsWith("0");
			if (flag) {
				sourceData = sourceData.substring(1, sourceData.length());
			}
		}

		return sourceData;
	}

	/**
	 * 取消送货单明细 重置货单对应的订单明细 送货量 在途量
	 *
	 * @param item 送货明细
	 * @param id 采购订单Id
	 */
	protected void subPOD(DeliveryDtl item, Long id) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = purchaseOrderDetailLogic.findById(id);

		// 送货量
		BigDecimal oldQtySend = pod.getQtySend() == null ? BigDecimal.ZERO : pod.getQtySend();
		BigDecimal newQtySend = oldQtySend.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		pod.setQtySend(newQtySend);

		// 在途量
		BigDecimal oldQtyOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();
		BigDecimal newQtyOnline = oldQtyOnline.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		pod.setQtyOnline(newQtyOnline);

		purchaseOrderDetailLogic.recountCanSendQty(pod);
	}

	/**
	 * 取消送货单明细 重置送货排程 送货量 在途量
	 *
	 * @param item 送货明细
	 */
	protected void subSSD(DeliveryDtl item) {

		// 找到对应的排程子明细
		SendScheduleDetail ssd = sendscheduleDetailLogic.findById(item.getSendDetailId());

		// 送货量
		BigDecimal oldDeliveryQty = ssd.getDeliveryQty() == null ? BigDecimal.ZERO : ssd.getDeliveryQty();
		BigDecimal newDeliveryQty = oldDeliveryQty.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		ssd.setDeliveryQty(newDeliveryQty);

		// 在途量
		BigDecimal oldOnWayQty = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();
		BigDecimal newQtyOnline = oldOnWayQty.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		ssd.setOnWayQty(newQtyOnline);

		// 可送货量 =排程需求量 - 在途量 - 收货量 + 退货量
		if (ssd.getScheduleQty() != null) {
			BigDecimal newCanSendQty = ssd.getScheduleQty().subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
					.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
					.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
			ssd.setCanSendQty(newCanSendQty);
		}

		// 排程送货标识更新
		if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {
			ssd.setSendFlag(0); // 如果可送货量等于需求量则 为未送货
		} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
			ssd.setSendFlag(1); // 为部分送货
		} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0 || ssd.getCanSendQty().compareTo(BigDecimal.ZERO) < 0) {
			ssd.setSendFlag(2); // 完全送货
		}

		SendScheduleDetail newSsd = sendscheduleDetailLogic.save(ssd);

		// 更新排程状态更新
		this.updateSSStatus(newSsd.getSendScheduleNo());

		// 同步更新订单明细
		this.subPOD(item, newSsd.getPurchaseOrderDetailId());

	}

	/**
	 * 取消送货单细单 更新排程单的状态
	 *
	 * @param sendscheduleNo 排程单号
	 */
	protected void updateSSStatus(String sendscheduleNo) {
		// 获取全部的该排程单对应的排程子明细记录
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_sendScheduleNo", sendscheduleNo);
		List<SendScheduleDetail> ssdList = sendscheduleDetailLogic.findAll(searchParams);

		if (ssdList != null && ssdList.size() > 0) {
			// 如果明细中全部为完全送货这该排程单状态设置为完成
			boolean flag = true;
			for (SendScheduleDetail ssd : ssdList) {
				if (ssd.getSendFlag() != 2) {
					break;
				}
			}
			if (flag) {
				SendSchedule ssd = sendscheduleLogic.findOne(searchParams);
				ssd.setSendScheduleState(SendScheduleState.OPEN);
				sendscheduleLogic.save(ssd);
			}
		}
	}
}
