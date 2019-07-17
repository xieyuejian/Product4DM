package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FetchType;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.interaction.api.InteractionClient;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.groovy.service.GroovyScriptInvokerService;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.StockLocation;
import com.huiju.srm.purchasing.dao.CensorQualityDao;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.util.PurchaseOrderConstant;
import com.huiju.srm.stock.entity.Instock;
import com.huiju.srm.stock.entity.InstockDtl;
import com.huiju.srm.stock.entity.InstockState;
import com.huiju.srm.stock.entity.Outstock;
import com.huiju.srm.stock.entity.OutstockDtl;
import com.huiju.srm.stock.entity.OutstockState;
import com.huiju.srm.stock.service.InstockService;
import com.huiju.srm.stock.service.OutstockService;
import com.huiju.srm.ws.entity.WsRequestLog;
import com.huiju.srm.ws.service.WsRequestLogService;

public class StdCensorQualityServiceImpl extends JpaServiceImpl<CensorQuality, Long> implements StdCensorQualityService {
	@Autowired
	protected CensorQualityDao censorQualityDao;
	@Autowired
	protected UserClient userClient;
	@Autowired(required = false)
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;
	@Autowired
	protected BillSetServiceClient billSetLogic;
	@Autowired
	protected ReceivingNoteService receivingNoteLogic;
	@Autowired
	protected InstockService InstockLogic;
	@Autowired
	protected WsRequestLogService wsRequestLogLogic;
	@Autowired(required = false)
	protected InteractionClient interactLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected OutstockService outstockLogic;
	@Autowired
	protected PlantClient plantLogic;
	@Autowired
	protected StockLocationClient stockLocationLogic;

	@Override
	public CensorQuality mergeCensorQuality(CensorQuality censorQuality, Object[] creator) {
		CensorQuality old_object = (CensorQuality) censorQualityDao.getById(censorQuality.getCensorqualityId());
		if (old_object == null) {
			return null;
		}
		censorQuality.setClientCode(old_object.getClientCode());
		if (CensorQualityState.CHECKED.equals(old_object.getStatus())) {
			throw new RuntimeException("已检验完成");
		}
		// object.setInspectionTime(Calendar.getInstance());
		censorQuality.setQualifiedQty(censorQuality.getQualifiedQty() == null ? BigDecimal.ZERO : censorQuality.getQualifiedQty());
		censorQuality.setUnqualifiedQty(censorQuality.getUnqualifiedQty() == null ? BigDecimal.ZERO : censorQuality.getUnqualifiedQty());
		censorQuality.setReceiveQty(censorQuality.getReceiveQty() == null ? BigDecimal.ZERO : censorQuality.getReceiveQty());

		// 1) 已质检合格量=原已质检合格量+本次质检的合格量
		// 2) 已质检不合格量=原已质检不合格量+本次质检的不合格量
		// 3) 已质检让步接收量=原已质检让步接收量+本次质检的让步接收量
		// 4) 可检量=送检量-已质检合格量-已质检不合格量-已质检让步接收量
		// 5) 质检时间为当前提交的时间；
		censorQuality.setCheckQualifiedQty((old_object.getCheckQualifiedQty() == null ? BigDecimal.ZERO : old_object.getCheckQualifiedQty())
				.add(censorQuality.getQualifiedQty()));
		censorQuality.setCheckUnqualifiedQty(
				(old_object.getCheckUnqualifiedQty() == null ? BigDecimal.ZERO : old_object.getCheckUnqualifiedQty())
						.add(censorQuality.getUnqualifiedQty()));
		censorQuality.setCheckReceiveQty((old_object.getCheckReceiveQty() == null ? BigDecimal.ZERO : old_object.getCheckReceiveQty())
				.add(censorQuality.getReceiveQty()));

		censorQuality.setQualityTime(Calendar.getInstance());

		// 回置收货单的质检结果，质检状态
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("EQ_receivingNoteNo", censorQuality.getReceivingNoteNo());
		ReceivingNote rn = receivingNoteLogic.findOne(maps);

		// 若本次检验的合格量+不合格量+让步接收量<可检量时，状态置为“检验中”；= "检验完成"

		BigDecimal total = censorQuality.getQualifiedQty().add(censorQuality.getUnqualifiedQty()).add(censorQuality.getReceiveQty());

		if (total.compareTo(censorQuality.getCanCheckQty()) == 0) {
			censorQuality.setStatus(CensorQualityState.CHECKED);

			// 检验完成相关逻辑待补充
			// 系统配置，1、是否启用库存管理，2、是否同步
			censorQuality = synData(censorQuality, creator);

		} else if (total.compareTo(censorQuality.getCanCheckQty()) == -1) {
			censorQuality.setStatus(CensorQualityState.CHECKING);
		}
		rn.setStatus(censorQuality.getStatus());
		// 根据管控点判断是否生成退货
		rn = createReceivingNote(rn, censorQuality, creator);
		receivingNoteLogic.save(rn);
		// 清空本次检验的合格量，不合格量，让步接收量
		censorQuality.setCanCheckQty(old_object.getCensorQty().subtract(censorQuality.getCheckQualifiedQty())
				.subtract(censorQuality.getCheckUnqualifiedQty()).subtract(censorQuality.getCheckReceiveQty()));
		censorQuality.setQualifiedQty(censorQuality.getCanCheckQty());
		censorQuality.setUnqualifiedQty(BigDecimal.ZERO);
		censorQuality.setReceiveQty(BigDecimal.ZERO);

		CensorQuality o = censorQualityDao.save(censorQuality);// 重新保存对象
		return o;
	}

	/**
	 * 根据管控点，生成收退货数据
	 * 
	 * @param rn 质检对应的收货单
	 * @param censorQuality 质检单据
	 * @param creator
	 * @return
	 */
	protected ReceivingNote createReceivingNote(ReceivingNote rn, CensorQuality censorQuality, Object[] creator) {

		// 不合格数量生成退货
		BigDecimal unqualifiedQty = censorQuality.getUnqualifiedQty();
		unqualifiedQty = unqualifiedQty == null ? BigDecimal.ZERO : unqualifiedQty;
		if (BigDecimal.ZERO.compareTo(unqualifiedQty) < 0) {
			// 不合格数量大于0
			// 根据集团配置项：“质检接口同步”判断，如果配置“是”，则不自动生成退货记录，如果配置“否”，则要自动生成退货记录
			Map<String, Object> hm = new HashMap<String, Object>();
			hm.put("po", censorQuality);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("map", hm);
			try {
				Object obj = groovyScriptInvokerLogic.invoke("CP0701", map);// 质检接口同步标识
				System.out.println("====obj====" + obj);
				if ("2".equals(obj)) {
					// 生成退货单据

					// 回写原收货单据的“可开票数量”、“可冲销数量”
					Map<String, Object> searchParams = new HashMap<String, Object>();
					searchParams.put("EQ_purchaseOrderDetailId", rn.getPurchaseOrderDetailId());
					PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailLogic.findOne(searchParams);

					// 获取订单单位/定价单位转换关系
					List<PurchaseDualUnitConversion> purchaseDualUnitConversions = purchaseOrderDetail.getPurchaseDualUnitConversions();
					PurchaseDualUnitConversion purchaseDualUnitConversion = purchaseDualUnitConversions.size() > 0
							? purchaseDualUnitConversions.get(0)
							: new PurchaseDualUnitConversion();

					BigDecimal convertDenominator2 = purchaseDualUnitConversion.getConvertDenominator2(); // 定价单位
					BigDecimal convertMolecular2 = purchaseDualUnitConversion.getConvertMolecular2();// 订单单位
					BigDecimal exchangeRate2 = convertDenominator2.divide(convertMolecular2, 3, RoundingMode.HALF_UP); // 定价单位/订单单位转换系数

					BigDecimal convertDenominator = purchaseDualUnitConversion.getConvertDenominator(); // 基本单位
					BigDecimal convertMolecular = purchaseDualUnitConversion.getConvertMolecular();// 订单单位
					BigDecimal exchangeRate = convertMolecular.divide(convertDenominator, 3, RoundingMode.HALF_UP); // 订单单位/基本单位转换系数

					// 不合格数量转换的--冲销数量 = 不合格数量 * 订单单位 / 基本单位
					BigDecimal chargeOffNum = unqualifiedQty.multiply(exchangeRate);
					// 不合格数量转换的--开票数量 = 冲销数量 * 定价单位 / 订单单位
					BigDecimal invoiceQty = chargeOffNum.multiply(exchangeRate2);

					rn.setCanChargeOffNum(rn.getCanChargeOffNum().subtract(chargeOffNum));
					rn.setInvoiceQty(rn.getInvoiceQty().subtract(invoiceQty));
					// 生成新的收退货数据

					createNewEntity(rn, chargeOffNum, invoiceQty, unqualifiedQty, creator);
					// 增加对应订单明细的退货量
					BigDecimal qtyQuit = purchaseOrderDetail.getQtyQuit() == null ? BigDecimal.ZERO : purchaseOrderDetail.getQtyQuit();
					purchaseOrderDetail.setQtyQuit(qtyQuit.add(rn.getQtyReceive()));
					purchaseOrderDetailLogic.recountCanSendQty(purchaseOrderDetail);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} // 质检接口同步标识
			createOutStock(rn);
		}

		return rn;
	}

	/**
	 * 生成新的收退货数据
	 * 
	 * @param entity 原收退货数据
	 * @param quality 订单数量
	 * @param invoiceQty 定价数量
	 * @param checkUnqualifiedQty 不合格数量（sku）
	 * @param creator
	 */
	protected void createNewEntity(ReceivingNote entity, BigDecimal quality, BigDecimal invoiceQty, BigDecimal checkUnqualifiedQty,
			Object[] creator) {
		ReceivingNote oldNote = receivingNoteLogic.findById(entity.getReceivingNoteId());
		ReceivingNote newNote = new ReceivingNote();
		newNote = CommonUtil.copyObject(oldNote);
		// 编码生成
		String shdNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_SHD);
		newNote.setReceivingNoteNo(shdNo);
		// 主键置空
		newNote.setReceivingNoteId(null);
		// 收退货标识1收货2退货
		if (oldNote.getAcceptReturnFlag().equals(101l)) {
			newNote.setAcceptReturnFlag(102l);
		} else {
			newNote.setAcceptReturnFlag(101l);
		}
		// 生成新的物料凭证号
		String mcc = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_MCC);
		newNote.setMaterialCertificateCode(mcc);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		newNote.setMaterialCertificateYear(year + "");
		// Todo凭证行号
		newNote.setMaterialCertificateItem("1");
		// 数量
		newNote.setQtyReceive(quality);
		newNote.setInvoiceQty(BigDecimal.ZERO);
		newNote.setAmountnoTax(invoiceQty.multiply(newNote.getPrice()));
		newNote.setTotalAmountAndTax(newNote.getAmountnoTax().multiply((BigDecimal.ONE.add(newNote.getTaxRate()))));
		newNote.setTotalTax(newNote.getTotalAmountAndTax().subtract(newNote.getAmountnoTax()));
		// 生成的退货记录为质检完成状态--2
		newNote.setCanChargeOffNum(quality);
		newNote.setFixPriceQty(invoiceQty);
		newNote.setStockQty(checkUnqualifiedQty);
		newNote.setStatus(CensorQualityState.CHECKED);
		newNote.setCreateTime(Calendar.getInstance());
		newNote.setCreateUserId(Long.parseLong(creator[0].toString()));
		newNote.setCreateUserName(creator[2].toString());
		receivingNoteLogic.save(newNote);

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
			e.printStackTrace();
		}

	}

	// 质检完成后，根据管控点，生成入库单，同步接口
	protected CensorQuality synData(CensorQuality censorQuality, Object[] creator) {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("po", censorQuality);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("map", hm);
		try {

			Object obj = groovyScriptInvokerLogic.invoke("CP0702", map);// 是否启用库存
			Object obj1 = groovyScriptInvokerLogic.invoke("CP0701", map);// 质检接口同步标识

			if ("1".equals(obj) && censorQuality.getMaterialCode() != null) {// 启用库存,物料编码不为空,生成入库单
				Instock stock = new Instock();

				stock.setInStockNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_RKD));

				stock.setPlantCode(censorQuality.getPlantCode());
				stock.setPlantName(censorQuality.getPlantName());
				stock.setStoreLocalCode(censorQuality.getStockCode());
				stock.setStoreLocalName(censorQuality.getStockName());

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String str = sdf.format(censorQuality.getQualityTime().getTime());
				Date date = (Date) sdf.parse(str);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				stock.setInDate(calendar);

				// stock.setInDate(censorQuality.getQualityTime());
				stock.setInType("001");// 收货入库
				stock.setRemark(censorQuality.getCensorqualityNo());
				stock.setState(InstockState.TOPASS);

				stock.setCreateUserId(Long.parseLong(creator[0].toString()));
				stock.setCreateUserName(creator[2].toString());
				stock.setCreateTime(Calendar.getInstance());

				stock.setModifyUserId(Long.parseLong(creator[0].toString()));
				stock.setModifyUserName(creator[2].toString());
				stock.setModifyTime(Calendar.getInstance());

				List<InstockDtl> instockDtls = new ArrayList<InstockDtl>();
				InstockDtl instockDtl = new InstockDtl();
				instockDtl.setRowNo(1L);
				instockDtl.setMaterialCode(censorQuality.getMaterialCode());
				instockDtl.setMaterialName(censorQuality.getMaterialName());
				instockDtl.setUnitCode(censorQuality.getUnit());
				instockDtl.setNum(censorQuality.getCheckQualifiedQty().add(censorQuality.getCheckReceiveQty()));// 合格量+让步接收量
				instockDtl.setInstock(stock);

				instockDtls.add(instockDtl);

				stock.setInstockDtls(instockDtls);

				InstockLogic.save(stock);
			}
			System.out.println("同步标志：" + obj1);

			if ("1".equals(obj1)) { // || "1".equals(obj)
				// 检验完成的检验批同步到SAP
				syncToSap(censorQuality);
			} else {
				censorQuality.setErpSyn(SrmSynStatus.SYNSUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return censorQuality;
	}

	@Override
	public String synErp(Long id) throws Exception {
		CensorQuality entity = censorQualityDao.getById(id);
		if (entity == null) {
			return "送检质检单不存在";
		}
		// 同步接口
		String checkFirst = this.getCensorQualityControl(entity, "CP0701");
		// 接口设置为不需要同步
		if (PurchaseOrderConstant.GROOVY_NO.equals(checkFirst)) {
			entity.setErpSyn(SrmSynStatus.SYNSUCCESS);
			entity = censorQualityDao.save(entity);
		} else {
			boolean flag = syncToSap(entity);
			if (!flag) {
				return "同步失败";
			}
		}
		return "";
	}

	/**
	 * 获取送检质检单管控点 默认同步
	 * 
	 * @param entity 实体对象
	 * @param code 管控点Code
	 * @return
	 * @throws Exception
	 */
	protected String getCensorQualityControl(Object entity, String code) throws Exception {
		Map<String, Object> poMap = new HashMap<String, Object>();
		poMap.put("po", entity);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("map", poMap);
		String value = (String) groovyScriptInvokerLogic.invoke(code, params);
		if (StringUtils.isBlank(value) || !value.equals(PurchaseOrderConstant.YES)) {
			return PurchaseOrderConstant.GROOVY_NO;
		}
		return PurchaseOrderConstant.GROOVY_YES;
	}

	/**
	 * 同步到SAP
	 * 
	 * @param entity
	 */
	protected Boolean syncToSap(CensorQuality entity) throws Exception {
		// 接口编码
		String interfaceCode = SrmConstants.SRM_QUALITY_CODE;
		JSONObject jsonMap = null;
		JSONObject dataMap = null;
		String json = null;
		String params = "";
		WsRequestLog wrlog = new WsRequestLog();
		try {
			// 设置同步中状态
			entity.setErpSyn(SrmSynStatus.SYNCHRONIZING);
			censorQualityDao.save(entity);
			// 业务场景编码
			params = DataUtils.toJson(entity, FetchType.EAGER);
			wrlog = wsRequestLogLogic.createTargetErpLog(interfaceCode, entity.getCensorqualityNo(), params);
			// 接口URL
			json = interactLogic.invoke("AddUpdateCensorQuality", params);
			jsonMap = JSONObject.parseObject(json);
			dataMap = jsonMap.getJSONObject("data");
			JSONObject etReturn = dataMap == null ? null : dataMap.getJSONObject("EtReturn");
			JSONObject returnMap = etReturn == null ? null : etReturn.getJSONObject("item");
			if (dataMap.getJSONObject("EtReturn") == null) {
				returnMap = dataMap;

			} else {
				returnMap = dataMap.getJSONObject("EtReturn").getJSONObject("item");

			}

			if (!"0".equals(jsonMap.getString("errcode"))) {
				Integer errcode = jsonMap.getInteger("errcode");
				String message = returnMap == null ? "" : returnMap.getString("Message");
				String errorMessgae[] = { "", "与服务器通讯异常", "服务器返回异常", "认证失败", "超时", "重新尝试失败，并不再自动重试", "其他" };

				if (message == null) {
					message = errorMessgae[errcode];
				}

				entity.setErpSyn(SrmSynStatus.SYNFAILED);
				entity.setErpReturnMsg(message);
				censorQualityDao.save(entity);
				wsRequestLogLogic.addFailLog(wrlog, json);
				return false;
			} else {
				// 同步失败
				String type = returnMap.getString("Type");
				String message = returnMap.getString("Message");
				if ("E".equals(type)) {
					entity.setErpSyn(SrmSynStatus.SYNFAILED);
					entity.setErpReturnMsg(message);
					wsRequestLogLogic.addErrorLog(wrlog, json);
				} else {
					entity.setErpSyn(SrmSynStatus.SYNSUCCESS);
					entity.setErpReturnMsg(message);
					wsRequestLogLogic.addSuccessLog(wrlog, json);
				}
				censorQualityDao.save(entity);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			entity.setErpSyn(SrmSynStatus.SYNFAILED);
			censorQualityDao.save(entity);
			wsRequestLogLogic.addFailLog(wrlog, json);
			return false;
		}
	}

	@Override
	public List<User> getCensorQualityCheckers(CensorQuality censorQuality) {
		List<User> users = new ArrayList<User>();
		Map<String, String> map = new HashMap<String, String>();
		String sql = "select u.userid, agd.authcode, agd.authvalue " + "   from s_sm_authgroupuser sma, s_sm_authgroupdetail agd,s_user u "
				+ "   where sma.usercode = u.usercode " + "   and sma.agrcode = agd.agrcode " + "   and u.userid in ( "
				+ "      select distinct(ur.userid) from s_user_role ur, s_role_authority ra, s_authority a "
				+ "      where ur.roleid = ra.roleid " + "      and ra.authorityid = a.authorityid "
				+ "      and a.authorityname ='D_CP_CENSORQUALITY_CHECK' " + "   ) " + "   and agd.authcode = 'purchasingOrgCode' "
				+ "   and agd.clientcode = '800'" + "   order by userid";
		List<Object[]> objectList = censorQualityDao.executeSqlQueryArray(sql);
		if (objectList == null || objectList.isEmpty()) {
			return users;
		}
		for (Object[] obj : objectList) {
			String userID = String.valueOf(obj[0]);
			String authValue = (String) obj[2];

			if (authValue.equals("*")) {
				User user = userClient.findById(Long.valueOf(userID));
				users.add(user);
				continue;
			}

			String o = map.get(userID);
			if (o == null || o == "") {
				map.put(userID, authValue);
			} else {
				map.put(userID, o + "," + authValue);
			}
		}

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().indexOf(censorQuality.getPurchasingOrgCode()) > -1) {
				User user = userClient.findById(Long.valueOf(entry.getKey()));
				users.add(user);
			}
		}

		return users;
	}

	public ReceivingNote chargeOff(ReceivingNote entity) {
		try {
			entity.setCreateTime(Calendar.getInstance());
			ReceivingNote oldNote = receivingNoteLogic.findById(entity.getReceivingNoteId());
			if (oldNote.getCanChargeOffNum() == null) {
				oldNote.setCanChargeOffNum(entity.getCanChargeOffNum());
			}
			ReceivingNote newNote = new ReceivingNote();
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

			// 冲销数量
			newNote.setQtyReceive(entity.getCanChargeOffNum());
			newNote.setFixPriceQty(entity.getCanChargeOffNum());
			newNote.setInvoiceQty(entity.getCanChargeOffNum());
			newNote.setStockQty(entity.getCanChargeOffNum());
			newNote.setCanChargeOffNum(entity.getCanChargeOffNum());
			// 可冲销数量
			oldNote.setCanChargeOffNum(oldNote.getCanChargeOffNum().subtract(entity.getCanChargeOffNum()));
			// 对账日期，凭证日期，过账日期
			newNote.setCertificateDate(Calendar.getInstance());// 凭证日期
			newNote.setPostingDate(Calendar.getInstance());// 过账日期
			// 创建修改信息
			newNote.setCreateTime(Calendar.getInstance());
			oldNote.setModifyTime(Calendar.getInstance());
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

			// 计算含税未税总金额、税额
			if (newNote.getPrice() == null) {
				newNote.setPrice(BigDecimal.ZERO);
			}
			newNote.setAmountnoTax(newNote.getPrice().multiply(newNote.getCanChargeOffNum()));
			newNote.setTotalTax(newNote.getAmountnoTax().multiply(newNote.getTaxRate()));
			newNote.setTotalAmountAndTax(newNote.getAmountnoTax().add(newNote.getTotalTax()));
			// 退货数量大于0 才新增生成退货记录
			if (entity.getCanChargeOffNum().compareTo(BigDecimal.ZERO) == 1) {
				newNote = receivingNoteLogic.save(entity);
			}
			receivingNoteLogic.save(oldNote);
			PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailLogic.findById(oldNote.getPurchaseOrderDetailId());
			// 更新执行中订单明细
			BigDecimal qty_quit = null;// 退货量
			BigDecimal accept_qty = null;// 实际的量
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

			BigDecimal b_qty = purchaseOrderDetail.getBuyerQty();// 购买量
			qty_arrive = purchaseOrderDetail.getQtyArrive();
			qty_quit = purchaseOrderDetail.getQtyQuit();
			accept_qty = qty_arrive.subtract(qty_quit);
			// 如果订单量*（1-限额不足）<= accept_qty,对应采购订单细单关闭
			if (b_qty.multiply(new BigDecimal("1").subtract(purchaseOrderDetail.getShortDeliveryLimit().divide(new BigDecimal("100"))))
					.compareTo(accept_qty) != 1) {
				purchaseOrderDetail.setCloseFlag(1);// 关闭订单
			} else {
				purchaseOrderDetail.setCloseFlag(0);// 打开订单
			}
			purchaseOrderDetailLogic.save(purchaseOrderDetail);

			// 判断采购订单细单数量是否收货完毕，可否关闭细单 ---20150922 xufq
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_erpPurchaseOrderNo", entity.getPurchaseOrderNo());
			// 查找细单，取出细单的 数量（先找主单Id）
			PurchaseOrder purchaseOrder = purchaseOrderLogic.findOne(searchParams);
			if (purchaseOrder == null) {
				searchParams.clear();
				searchParams.put("EQ_purchaseOrderNo", entity.getPurchaseOrderNo());
				purchaseOrder = purchaseOrderLogic.findOne(searchParams);
			}
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
			// dealCensorQuality(newNote, oldNote);
			// dealSendSchedule(newNote, oldNote);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
				.module(SrmConstants.BILLTYPE_ZJD)// 设置日志模块
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
