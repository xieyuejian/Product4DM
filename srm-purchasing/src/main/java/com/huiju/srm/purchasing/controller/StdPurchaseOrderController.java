package com.huiju.srm.purchasing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.fs.util.FileUploadUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.CloudReportController;
import com.huiju.srm.commons.utils.PoiUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.api.PlantPurchaseOrgClient;
import com.huiju.srm.masterdata.entity.MaterialPlant;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.PlantPurchaseOrg;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderCheckState;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderFlowState;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.service.MaterialMasterPriceOrderDtlViewService;
import com.huiju.srm.purchasing.service.PurchaseDualUnitConversionService;
import com.huiju.srm.purchasing.service.PurchaseOrderPricingService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.purchasing.util.PurchaseOrderConstant;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.service.VendorService;

/**
 * 采购订单action 产品
 * 
 * @author CWQ
 */
@Certificate(value = { "CP_order" }, requiredType = RequiredType.ONE)
public class StdPurchaseOrderController extends CloudReportController {
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired(required = false)
	protected VendorService vendorLogic;
	@Autowired(required = false)
	protected PurchaseDualUnitConversionService purchaseDualUnitConversionLogic;
	@Autowired(required = false)
	protected PurchaseOrderPricingService purchaseOrderPricingLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired(required = false)
	protected MaterialPlantClient materialPlantLogic;
	@Autowired(required = false)
	protected MaterialClient materialLogic;
	@Autowired(required = false)
	protected MaterialMasterPriceOrderDtlViewService materialMasterPriceDtlViewLogic;
	@Autowired(required = false)
	protected PlantPurchaseOrgClient plantPurchaseOrgLogic;
	@Autowired(required = false)
	protected UserClient userLogic;

	protected final String execList = "ExecList";
	protected final String unDealList = "UnDealList";

	/**
	 * 获取供应商
	 * 
	 */
	@PostMapping(value = "/findvendor")
	public Vendor findVendor() {
		Map<String, Object> searchParams = buildParams();
		List<Vendor> vendors = vendorLogic.findAllWithoutAssociation(searchParams);
		if (vendors != null && vendors.size() > 0) {
			return vendors.get(0);
		}
		return null;
	}

	/**
	 * 获取汇率
	 */
	@PostMapping(value = "/findexchangerate")
	public Result findExchangeRate() {

		String vendorCode = request.getParameter("vendorCode");
		String orgCode = request.getParameter("purchasingOrgCode");
		String origCurrencyCode = request.getParameter("origCurrencyCode");

		String message = purchaseOrderLogic.findExchangeRate(vendorCode, orgCode, origCurrencyCode);

		if (message.contains("-")) {
			return Result.success(message);
		} else {
			return Result.error(message);
		}
	}

	/**
	 * 共同的代码，后期业务不同可做具体修改
	 */
	public void excute() {
		// initStates = stateToJson();
		// permissions = checkPermissions(authoritys);
	}

	/**
	 * 获取价格主数据
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/findmaterialmasterpricedetail")
	public Page<MaterialMasterPriceOrderDtlView> findMaterialMasterPriceDetail() {
		Page<MaterialMasterPriceDtl> mmdPage = buildPage(MaterialMasterPriceDtl.class);
		Map<String, Object> searchParams = buildParams();
		Page<MaterialMasterPriceOrderDtlView> page = buildPage(MaterialMasterPriceOrderDtlView.class);

		return materialMasterPriceDtlViewLogic.findPage(mmdPage, page, searchParams);
	}

	/**
	 * 获取库存地点、库存类型
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/getstorelocation")
	public MaterialPlant getStoreLocation() {
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQ_materialCode", request.getParameter("mCode"));
		searchParams.put("EQ_plantCode", request.getParameter("pCode"));
		FeignParam<MaterialPlant> param = new FeignParam<MaterialPlant>(searchParams);
		return materialPlantLogic.findOne(param);
	}

	/**
	 * 根据采购组织、资源组、工厂物料视图、物料查询工厂数据
	 * 
	 * @return "NONE"
	 */
	@GetMapping(value = "/findplantall")
	public String findPlantAll() {
		// 没有物料编码
		Map<String, Object> searchParams = buildParams();
		if (!searchParams.containsKey("EQ_materialCode")) {
			FeignParam<PlantPurchaseOrg> param = new FeignParam<PlantPurchaseOrg>(searchParams);
			List<PlantPurchaseOrg> plantPurchaseOrgList = plantPurchaseOrgLogic.findAll(param);
			return DataUtils.toJson(plantPurchaseOrgList);
		}
		searchParams.putAll(
				userAuthGroupLogic.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Plant.class)));
		return purchaseOrderLogic.findPlantAll(searchParams);
	}

	/**
	 * 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/findmaterialladderprice")
	public String findMaterialLadderPrice() {
		Map<String, Object> params = buildParams();
		params.put("LE_effectiveDate", Calendar.getInstance());
		params.put("GT_expirationDate", Calendar.getInstance());
		MaterialLadderPriceDtl ladderPriceDtl = purchaseOrderLogic.findMaterialLadderPrice(params);

		String result = "-1";
		if (null != ladderPriceDtl) {
			result = ladderPriceDtl.getNonTaxPrice().toString();
		}
		return result;
	}

	/**
	 * 下载导入模版
	 * 
	 * @return "NONE"
	 */
	@SuppressWarnings("all")
	@PostMapping(value = "/download")
	public Result downloadTemplate(String templateFile, String fileName) {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new ClassPathResource("template/" + templateFile).getInputStream();
			out = response.getOutputStream();

			fileName = "订单导入模板.xls";
			HSSFWorkbook wb = new HSSFWorkbook(in);

			if (templateFile.equals("PurchaseOrderMatertals.xls")) {
				PoiUtils.setExcelTitle(wb, 1);

			} else if (templateFile.equals("PurchaseOrders.xls")) {
				// 设置excel下拉
				purchaseOrderLogic.setExcelCombox(wb);
			}

			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			// ServletOutputStream stream = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return Result.success();
	}

	/**
	 * 批量导入订单
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/batchimport")
	public Result batchImport(HttpServletRequest request) {
		InputStream in = null;
		HSSFWorkbook wb = null;
		try {
			Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
			String key = files.keySet().iterator().next();
			File excelFile = files.get(key);
			in = new FileInputStream(excelFile);
			wb = new HSSFWorkbook(in);
			Map<String, Object> webParams = buildParams();
			String message = getBatchExcelData(wb, webParams);// 获取excel数据
			if (StringUtils.isNotBlank(message)) {
				return Result.error(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.success();
	}

	/**
	 * 获取采购订单主单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getBatchExcelData(HSSFWorkbook wb, Map<String, Object> webParams) throws Exception {
		List<PurchaseOrder> orders = new ArrayList<PurchaseOrder>();
		List<PurchaseOrderDetail> orderDetials = new ArrayList<PurchaseOrderDetail>();
		HSSFSheet sheet = wb.getSheetAt(0);
		StringBuilder messages = new StringBuilder();
		Map<String, PurchaseOrder> orderNoPOrgMap = new HashMap<String, PurchaseOrder>();
		Map<String, String> nosMap = new HashMap<String, String>();

		for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
			HSSFRow row = sheet.getRow(rowSeq);

			PurchaseOrder order = new PurchaseOrder();
			int cellSeq = 0;
			if (PoiUtils.isBlankRow(row, 0, 6)) {
				continue;
			}

			// 关联号
			HSSFCell cell = row.getCell(cellSeq++);
			order.setPurchaseOrderNo(PoiUtils.getCellStringValue(cell));
			nosMap.put(order.getPurchaseOrderNo(), order.getPurchaseOrderNo());

			// 采购组织
			cell = row.getCell(cellSeq++);
			order.setPurchasingOrgCode(PoiUtils.getCellStringValue(cell));

			// 采购组编码
			cell = row.getCell(cellSeq++);
			order.setPurchasingGroupCode(PoiUtils.getCellStringValue(cell));

			// 公司编码
			cell = row.getCell(cellSeq++);
			order.setCompanyCode(PoiUtils.getCellStringValue(cell));

			// 供应商编码
			cell = row.getCell(cellSeq++);
			order.setVendorCode(PoiUtils.getCellStringValue(cell));
			order.setVendorErpCode(PoiUtils.getCellStringValue(cell));

			// 订单日期
			cell = row.getCell(cellSeq++);
			try {
				Calendar purchaseOrderTime = PoiUtils.getCellCalendarValue(cell);
				order.setPurchaseOrderTime(purchaseOrderTime);
			} catch (Exception e) {
				// porderDetail.batchImport.purchaseOrderTime.format =
				// 基本信息工作簿中第{0}行的订单时间格式不正确
				messages.append(getResource("porderDetail.batchImport.purchaseOrderTime.format", rowSeq + "") + "\n");
			}

			// 采购订单类型
			cell = row.getCell(cellSeq++);
			String purchaseOrderType = getComboxValue(PoiUtils.getCellStringValue(cell));
			order.setPurchaseOrderType(purchaseOrderType);

			orderNoPOrgMap.put(order.getPurchaseOrderNo(), order);
			orders.add(order);
		}

		sheet = wb.getSheetAt(1);

		for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
			HSSFRow row = sheet.getRow(rowSeq);
			PurchaseOrderDetail orderDetail = new PurchaseOrderDetail();
			int cellSeq = 0;

			if (PoiUtils.isBlankRow(row, 0, 12)) {
				continue;
			}

			// 关联号
			HSSFCell cell = row.getCell(cellSeq++);
			String key = PoiUtils.getCellStringValue(cell);
			orderDetail.setPurchaseOrder(orderNoPOrgMap.get(key));
			if (StringUtils.isNotBlank(key)) {
				nosMap.remove(key);
			}

			// 行项目类别
			cell = row.getCell(cellSeq++);
			String lineItemTypeCode = getComboxValue(PoiUtils.getCellStringValue(cell));
			orderDetail.setLineItemTypeCode(lineItemTypeCode);

			// 物料编码
			cell = row.getCell(cellSeq++);
			orderDetail.setMaterialCode(PoiUtils.getCellStringValue(cell));

			// 订单单位
			cell = row.getCell(cellSeq++);
			orderDetail.setUnitCode(getComboxValue(PoiUtils.getCellStringValue(cell)));
			// 数量
			cell = row.getCell(cellSeq++);
			orderDetail.setBuyerQty(PoiUtils.getCellBigDecimalValue(cell));
			orderDetail.setVendorQty(orderDetail.getBuyerQty());

			// 工厂编码
			cell = row.getCell(cellSeq++);
			orderDetail.setPlantCode(PoiUtils.getCellStringValue(cell));

			// 库存地点编码
			cell = row.getCell(cellSeq++);
			orderDetail.setStoreLocal(PoiUtils.getCellStringValue(cell));

			// 税率编码
			cellSeq++;

			// 交货日期
			cell = row.getCell(cellSeq++);
			try {
				Calendar buyerTime = PoiUtils.getCellCalendarValue(cell);
				orderDetail.setBuyerTime(buyerTime);
			} catch (Exception e) {
				// porderDetail.batchImport.vendorTime.format =
				// 明细信息工作簿中第{0}行的确认交货日期格式不正确
				messages.append(getResource("porderDetail.batchImport.vendorTime.format", rowSeq + "") + "\n");
			}

			// 确认交货日期
			cell = row.getCell(cellSeq++);
			try {
				Calendar vendorTime = PoiUtils.getCellCalendarValue(cell);
				orderDetail.setVendorTime(vendorTime);
			} catch (Exception e) {
				// porderDetail.batchImport.vendorTime.format =
				// 明细信息工作簿中第{0}行的确认交货日期格式不正确
				messages.append(getResource("porderDetail.batchImport.vendorTime.format", rowSeq + "") + "\n");
			}

			// 是否免费
			cell = row.getCell(cellSeq++);
			String isFreeStr = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isNotBlank(isFreeStr)) {
				Integer isFree = isFreeStr.equals("yes") ? 1 : isFreeStr.equals("no") ? 0 : null;
				orderDetail.setIsFree(isFree);
			}

			// 是否排程
			cell = row.getCell(cellSeq++);
			String schStr = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isNotBlank(schStr)) {
				Integer sch = schStr.equals("yes") ? 1 : schStr.equals("no") ? 0 : null;
				orderDetail.setScheduleFlag(sch);
			} else {
				orderDetail.setScheduleFlag(0);
			}

			// 备注
			cell = row.getCell(cellSeq++);
			orderDetail.setRemark(PoiUtils.getCellStringValue(cell));

			orderDetials.add(orderDetail);

		}

		for (String key : nosMap.keySet()) {
			// porderDetail.batchImport.notFindDetail =
			// 基本信息中关联单号{0}找不到明细信息中对应的关联单号
			messages.append(getResource("porderDetail.batchImport.notFindDetail", key) + "\n");
		}

		if (messages.length() > 0) {
			return messages.toString();
		}

		String messgae = purchaseOrderLogic.batchImportExcel(orders, orderDetials, orderNoPOrgMap, webParams, getUserId(), getUserName(),
				getClientCode());
		return messgae;
	}

	/**
	 * 导入订单明细
	 * 
	 * @return
	 */
	@PostMapping(value = "/importexcel")
	public Result importExcel(HttpServletRequest request) {
		InputStream in = null;
		Map<Boolean, Object> returnMap = new HashMap<Boolean, Object>();
		try {
			Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
			String key = files.keySet().iterator().next();
			File excelFile = files.get(key);
			in = new FileInputStream(excelFile);
			HSSFWorkbook wb = new HSSFWorkbook(in);
			Map<String, Object> userAuthMap = userAuthGroupLogic.buildAuthFieldParams(
					new UserAuthGroupParam(getClientCode(), getUserCode(), Plant.class, new String[] { "companyCode" }));

			Map<String, Object> webParams = buildParams();
			String srmRowIds = getAttribute("srmRowIds");
			List<MaterialMasterPriceOrderDtlView> dtlVos = new ArrayList<MaterialMasterPriceOrderDtlView>();
			// 获取excel数据
			getExcelData(wb, dtlVos);
			returnMap = purchaseOrderLogic.importExcel(dtlVos, userAuthMap, webParams, Integer.valueOf(srmRowIds));

			if (returnMap.containsKey(false)) {
				return Result.error(returnMap.get(false) == null ? "导入出错" : returnMap.get(false).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("");
		}
		return Result.success(returnMap.get(true));
	}

	/**
	 * 读取excel数据
	 * 
	 * @param wb
	 * @param dtlVos
	 * @return
	 * @throws Exception
	 */
	protected void getExcelData(HSSFWorkbook wb, List<MaterialMasterPriceOrderDtlView> dtlVos) throws Exception {
		HSSFSheet sheet = wb.getSheetAt(0);
		for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
			MaterialMasterPriceOrderDtlView vo = new MaterialMasterPriceOrderDtlView();
			int cellSeq = 0;
			// 行项目类别、物料编码、物料名称、单位、工厂、数量、是否退货、交货日期、备注
			// 行项目类别
			HSSFRow row = sheet.getRow(rowSeq);
			if (null != row) {
				HSSFCell cell = row.getCell(cellSeq++);
				boolean flag = checkNullRow(row, 7);
				if (flag) {
					continue;
				}

				String recordType = PoiUtils.getCellStringValue(cell);
				if (StringUtils.isNotBlank(recordType)) {
					recordType = recordType.split("\\[")[0];
				}
				vo.setRecordType(recordType);

				// 物料编码
				cell = row.getCell(cellSeq++);
				vo.setMaterialCode(PoiUtils.getCellStringValue(cell));

				// 物料名称
				cell = row.getCell(cellSeq++);
				vo.setMaterialName(PoiUtils.getCellStringValue(cell));

				// 单位编码
				cell = row.getCell(cellSeq++);
				vo.setUnitCode(PoiUtils.getCellStringValue(cell));

				// 工厂
				cell = row.getCell(cellSeq++);
				vo.setPlantCode(PoiUtils.getCellStringValue(cell));
				// 库存类型
				// cell = row.getCell(cellSeq++);
				// vo.setItemCode(PoiUtils.getCellStringValue(cell));
				cell = row.getCell(cellSeq++);
				String stockType = PoiUtils.getCellStringValue(cell);
				if (StringUtils.isNotBlank(stockType)) {
					stockType = stockType.split("\\[")[0];
				}
				vo.setItemCode(stockType);
				// 库存地点
				cell = row.getCell(cellSeq++);
				vo.setStockLocationCode(PoiUtils.getCellStringValue(cell));
				// 数量
				cell = row.getCell(cellSeq++);

				vo.setBuyerQty(PoiUtils.getCellBigDecimalValue(cell).setScale(3, BigDecimal.ROUND_HALF_UP));

				// 是否退货
				cell = row.getCell(cellSeq++);
				String isReturn = PoiUtils.getCellStringValue(cell);
				Integer isr = StringUtils.isBlank(isReturn) ? null : isReturn.equals("yes") ? 1 : 0;
				vo.setIsReturn(isr);

				// 交货日期
				cell = row.getCell(cellSeq++);
				vo.setBuyerTime(PoiUtils.getCellCalendarValue(cell));

				// 备注
				cell = row.getCell(cellSeq++);
				vo.setRemark(PoiUtils.getCellStringValue(cell));
				dtlVos.add(vo);
			}
		}
	}

	/**
	 * 检查该行是不是空行
	 */
	protected boolean checkNullRow(HSSFRow row, int totalNum) {
		boolean flag = true;
		for (int i = 0; i < totalNum; i++) {
			HSSFCell hc = row.getCell(i);
			if (hc != null) {
				hc.setCellType(HSSFCell.CELL_TYPE_STRING);
				String context = hc.getStringCellValue().trim();
				if (StringUtils.isNotBlank(context)) {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 设置报表导出语句的参数
	 */
	public Map<String, Object> buildCondition() {
		Map<String, Object> params = buildParams();
		params.putAll(purchaseOrderLogic.getExportParams(params));
		return params;
	}

	/**
	 * 获取列表数据
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/list")
	public Page<PurchaseOrder> list() {
		String methodName = getAttribute("methodName");
		Page<PurchaseOrder> page = buildPage(PurchaseOrder.class);
		Map<String, Object> searchParams = buildParams();
		String initStates = getInitStates();
		setListParams(searchParams);
		String billFlag = getBillFlag();
		if (StringUtils.isNotEmpty(billFlag) && billFlag.equals("undeal")) {
			methodName = "UnDealList";
		}
		// 单据状态查询
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			List<PurchaseOrderState> statusArray = new ArrayList<PurchaseOrderState>();
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replaceAll(" ", "");
				PurchaseOrderState status = PurchaseOrderState.valueOf(values[i]);
				statusArray.add(status);
			}
			searchParams.put("IN_purchaseOrderState", statusArray);
		}
		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			setVendorSearchParams(searchParams, methodName);
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			setBuyerParams(searchParams, methodName);
		} else {
			setOtherParams(searchParams, methodName);
		}
		searchParams.put("DISTINCT", true);
		return purchaseOrderLogic.findAllWithoutAssociation(page, searchParams);

	}

	/**
	 * 设置供应商查询参数
	 * 
	 * @param roleType 角色
	 * @param searchParams web查询参数
	 */
	protected void setVendorSearchParams(Map<String, Object> searchParams, String methodName) {
		// 供应商角色只能查看自己的
		searchParams.put("EQ_vendorErpCode", getErpCode());
		searchParams.put("NE_purchaseOrderState", PurchaseOrderState.NEW);

		// 待处理列表
		if (unDealList.equals(methodName)) {
			searchParams.put("IN_purchaseOrderCheckState", Arrays.asList(PurchaseOrderCheckState.CONFIRM));
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.RELEASE);
		} else if (execList.equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		} else {
			searchParams.put("IS_purchaseOrderCheckState", "NOTNULL");
		}
	}

	/***
	 * 设置其他角色查询权限
	 * 
	 * @param searchParams web查询参数
	 */
	protected void setOtherParams(Map<String, Object> searchParams, String methodName) {
		// String methodName = getAttribute("methodName");
		if (unDealList.equals(methodName)) {
			String idsStr = purchaseOrderLogic.findIdByStatus(getUserId(), false);
			searchParams.put("IN_purchaseOrderId", idsStr);
		} else if (execList.equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		}
	}

	/**
	 * 设置采购查询参数
	 * 
	 * @param roleType 角色
	 * @param searchParams web查询参数
	 */
	protected void setBuyerParams(Map<String, Object> searchParams, String methodName) {
		// 资源组查询
		searchParams.putAll(userAuthGroupLogic
				.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), PurchaseOrder.class)));

		if (unDealList.equals(methodName)) {
			String idsStr = purchaseOrderLogic.findIdByStatus(getUserId(), true);
			searchParams.put("IN_purchaseOrderId", idsStr);

		} else if (execList.equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		}
	}

	/**
	 * 设置列表查询权限
	 * 
	 * @param searchParams web查询参数
	 */
	protected void setListParams(Map<String, Object> searchParams) {
		String purchaseOrderState = getAttribute("purchaseOrderState");
		String purchaseOrderFlowState = getAttribute("purchaseOrderFlowState");
		String purchaseOrderCheckState = getAttribute("purchaseOrderCheckState");
		String erpSynState = getAttribute("erpSynState");

		// 订单状态
		if (StringUtils.isNotBlank(purchaseOrderState)) {
			String[] states = purchaseOrderState.split(",");
			List<PurchaseOrderState> searcharr = new ArrayList<PurchaseOrderState>();
			for (String state : states) {
				searcharr.add(PurchaseOrderState.valueOf(state.trim()));
			}
			searchParams.put("IN_purchaseOrderState", searcharr);
		}

		// 流程状态
		if (StringUtils.isNotBlank(purchaseOrderFlowState)) {
			String[] states = purchaseOrderFlowState.split(",");
			List<PurchaseOrderFlowState> searchflowarr = new ArrayList<PurchaseOrderFlowState>();
			for (String state : states) {
				searchflowarr.add(PurchaseOrderFlowState.valueOf(state.trim()));
			}
			searchParams.put("IN_purchaseOrderFlowState", searchflowarr);
		}

		// 确认状态
		if (StringUtils.isNotBlank(purchaseOrderCheckState)) {
			String[] states = purchaseOrderCheckState.split(",");
			List<PurchaseOrderCheckState> searchcheckarr = new ArrayList<PurchaseOrderCheckState>();
			for (String state : states) {
				searchcheckarr.add(PurchaseOrderCheckState.valueOf(state.trim()));
			}
			searchParams.put("IN_purchaseOrderCheckState", searchcheckarr);
		}

		// 同步状态
		if (StringUtils.isNotBlank(erpSynState)) {
			String[] states = erpSynState.split(",");
			List<Integer> stateArray = new ArrayList<Integer>();
			for (String state : states) {
				stateArray.add(Integer.valueOf(state.trim()));
			}
			searchParams.put("IN_erpSynState", stateArray);
		}
	}

	/**
	 * 替换国家化资源中的下标
	 * 
	 * @param key 资源主键
	 * @param params 要替换的元素
	 * @return
	 */
	protected String getResource(String key, String... params) {
		String value = getText(key);
		if (StringUtils.isNotBlank(value) && null != params && 0 < params.length) {
			int i = 0;
			for (String param : params) {
				String str = "{" + i++ + "}";
				value = value.replace(str, param);
			}
		}

		return value;
	}

	/**
	 * 新建采购订单
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/save")
	public Result save(@RequestBody JsonParam<PurchaseOrder> param) {
		PurchaseOrder model = param.getModel();
		StringBuffer messages = new StringBuffer();
		String submitFlag = param.getSubmitFlag();
		int index = 0;
		int rowNo = 0;
		for (PurchaseOrderDetail detail : model.getPurchaseOrderDetails()) {
			rowNo++;
			messages = validateFields(detail, messages, rowNo); // 非空字段校验
		}
		if (0 < messages.length()) {
			return Result.error(messages.toString());
		}
		Map<String, Object> validateSourceListMap = purchaseOrderLogic.validateSourceList(model);
		for (PurchaseOrderDetail detail : model.getPurchaseOrderDetails()) {
			index++;
			String key = detail.getMaterialCode() + "_" + detail.getPlantCode() + "_" + model.getVendorErpCode();
			Boolean falg = validateSourceListMap.containsKey(key) && validateSourceListMap.get(key).equals("1") ? true : false;
			// Boolean falg = purchaseOrderLogic.validateSourceList(detail,
			// model);
			// XX物料未维护货源清单
			// porder.materialNotSourceList={0}物料未维护货源清单
			if (!falg) {
				messages.append(getResource("label.theRow", new String[] { (index) + "" }) + "：");
				messages.append(getResource("porder.materialNotSourceList", new String[] { detail.getMaterialCode() })).append("。");
			}
		}
		if (0 < messages.length()) {
			return Result.error(messages.toString());
		}
		// 校验采购申请可下单量
		messages.setLength(0);
		Map<String, Object> validateApplyMap = purchaseOrderLogic.validateApply(model);
		for (Map.Entry<String, Object> entry : validateApplyMap.entrySet()) {
			messages.append(getText("porder.lineNo") + entry.getKey() + "，");
			String[] values = entry.getValue().toString().split("_");
			messages.append(getResource("porder.purchaseApply.validate", new String[] { values[0], values[1] })).append("。");
		}

		if (0 < messages.length()) {
			return Result.error(messages.toString());
		}

		String poorderNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_CGD);
		if (poorderNo == null) {
			return Result.error("");
		}
		model.setPurchaseOrderNo(poorderNo);
		model.setErpPurchaseOrderNo(poorderNo);
		String clientCode = getClientCode();
		model.setClientCode(clientCode);
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		model.setCreateTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		// 保存
		model = purchaseOrderLogic.persistPo(model, submitFlag, SrmConstants.PLATFORM_WEB);
		return Result.success();

	}

	/**
	 * 变更采购订单
	 * 
	 * @return "NONE"
	 */
	@PostMapping(value = "/update")
	public Result update(@RequestBody JsonParam<PurchaseOrder> param) {
		PurchaseOrder model = param.getModel();
		StringBuffer messages = new StringBuffer();
		String submitFlag = param.getSubmitFlag();
		int index = 0;
		int rowNo = 0;
		for (PurchaseOrderDetail detail : model.getPurchaseOrderDetails()) {
			rowNo++;
			messages = validateFields(detail, messages, rowNo); // 非空字段校验
		}
		if (0 < messages.length()) {
			return Result.error(messages.toString());
		}

		Map<String, Object> validateSourceListMap = purchaseOrderLogic.validateSourceList(model);
		for (PurchaseOrderDetail detail : model.getPurchaseOrderDetails()) {
			index++;
			String key = detail.getMaterialCode() + "_" + detail.getPlantCode() + "_" + model.getVendorErpCode();
			Boolean falg = validateSourceListMap.containsKey(key) && validateSourceListMap.get(key).equals("1") ? true : false;
			// detail = processData(detail);
			// XX物料未维护货源清单
			// porder.materialNotSourceList={0}物料未维护货源清单
			if (!falg) {
				messages.append(getResource("label.theRow", new String[] { (index) + "" }) + "：");
				messages.append(getResource("porder.materialNotSourceList", new String[] { detail.getMaterialCode() })).append("。");
			}
		}
		if (0 < messages.length()) {
			return Result.error(messages.toString());
		}
		model.setErpSynState(0);
		model.setModifyUserId(getUserId());
		model.setModifyTime(Calendar.getInstance());
		model.setModifyUserName(getUserName());
		model.setPurchaseOrderState(PurchaseOrderState.NEW);
		model.setViewFlag(0);

		model = purchaseOrderLogic.mergeLogic(model, submitFlag, getUserId(), getUserName(), SrmConstants.PLATFORM_WEB);
		if ("save".equalsIgnoreCase(submitFlag)) {
			purchaseOrderLogic.addLog(getUserId(), getUserName(), model.getPurchaseOrderId(), "采购订单修改", SrmConstants.PERFORM_SAVE,
					model.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		}

		return Result.success();

	}

	/**
	 * 验证必填字段
	 * 
	 * @param detail
	 * @param messages
	 * @param rowNo 行号
	 * @return 验证信息
	 */
	public StringBuffer validateFields(PurchaseOrderDetail detail, StringBuffer messages, int rowNo) {
		if (StringUtils.isBlank(detail.getStoreLocal())) {
			messages.append("第" + rowNo + "行的库存地点不能为空。<br>");
		}
		if (StringUtils.isBlank(detail.getMaterialName())) {
			messages.append("第" + rowNo + "行的物料名称不能为空。<br>");
		}
		if (StringUtils.isBlank(detail.getUnitCode())) {
			messages.append("第" + rowNo + "行的单位不能为空。<br>");
		}
		if (null == detail.getBuyerQty()) {
			messages.append("第" + rowNo + "行的数量不能为空。<br>");
		}
		if (StringUtils.isBlank(detail.getPlantCode())) {
			messages.append("第" + rowNo + "行的工厂不能为空。<br>");
		}
		if (StringUtils.isBlank(detail.getStockType())) {
			messages.append("第" + rowNo + "行的库存类型不能为空。<br>");
		}
		if (StringUtils.isBlank(detail.getTaxRateCode())) {
			messages.append("第" + rowNo + "行的税率编码不能为空。<br>");
		}
		if (null == detail.getBuyerTime()) {
			messages.append("第" + rowNo + "行的交货日期不能为空。<br>");
		}
		if (null == detail.getVendorTime()) {
			messages.append("第" + rowNo + "行的确认交货日期不能为空。<br>");
		}
		if (null == detail.getOverDeliveryLimit()) {
			messages.append("第" + rowNo + "行的过量交货限度不能为空。<br>");
		}
		if (null == detail.getShortDeliveryLimit()) {
			messages.append("第" + rowNo + "行的交货不足限度不能为空。<br>");
		}
		return messages;
	}

	/**
	 * 获取采购订单主单数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/get")
	public Result get(Long id) {
		PurchaseOrder model = purchaseOrderLogic.findById(id);
		if (model == null) {
			return Result.error("");
		}
		// 供应商查看置为"已查看"
		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			if (model.getViewFlag() == null || model.getViewFlag().equals(0)) {
				model.setViewFlag(1);
				purchaseOrderLogic.save(model);
			}
			String flag = purchaseOrderLogic.getPurchaseOrderControl(model, PurchaseOrderConstant.GROOVY_VENDORVIEW);
			// 是否可以查看价格
			if (!PurchaseOrderConstant.GROOVY_YES.equals(flag)) {
				model.setTotalAmount(null);
			}
		}
		return Result.success(DataUtils.toJson(model, "purchaseOrderDetails"));
	}

	/**
	 * 删除采购订单
	 * 
	 * @return
	 */
	@PostMapping(value = "/delete")
	public Result delete(@RequestParam List<Long> ids, @RequestParam String message) {
		PurchaseOrder entity = purchaseOrderLogic.findById(ids.get(0));
		purchaseOrderLogic.removePo(ids, getUserId(), getUserName(), message);
		purchaseOrderLogic.addLog(getUserId(), getUserName(), entity.getPurchaseOrderId(), "采购订单删除,原因:" + message,
				SrmConstants.PERFORM_DELETE, entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 同步采购订单
	 * 
	 * @return
	 */
	@PostMapping(value = "/syncerp")
	public Result syncErp(Long id) {
		PurchaseOrder model = purchaseOrderLogic.findById(id);
		Boolean flag = purchaseOrderLogic.doSync(id, null, "");
		// 保存日志
		purchaseOrderLogic.addLog(getUserId(), getUserName(), model.getPurchaseOrderId(), "采购订单同步", SrmConstants.PERFORM_SYNC,
				model.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);

		if (flag) {
			return Result.success();
		} else {
			return Result.error("");
		}
	}

	/**
	 * 采购订单撤销审批
	 * 
	 * @return
	 */
	@PostMapping(value = "/revocationcheck")
	public Result revocationCheck(Long id) {
		String returnValue = purchaseOrderLogic.revocationCheck(id, getUserId(), getUserName());
		if (StringUtils.isNotBlank(returnValue)) {
			return Result.error(getText(returnValue));
		}
		return Result.success();
	}

	/**
	 * 获取操作权限
	 * 
	 * @return
	 */
	@PostMapping(value = "/getevents")
	public String getEvents(Long id, String btnStateFlag) {
		if (id == null) {
			return NONE;
		}
		return purchaseOrderLogic.getPurchaseOrderEvents(getUserId(), getRoleTypes(), id, getUserPermissions(), btnStateFlag);
	}

	/**
	 * 处理流程状态
	 * 
	 * @return
	 */
	@PostMapping(value = "/dealstatus")
	public Result dealStatus(Long id, String message) {
		Long userId = getUserId();
		String userName = getUserName();
		String billState = getBillState();
		// flowRule
		PurchaseOrder model = purchaseOrderLogic.dealPurchaseOrder(userId, userName, id, billState, message, SrmConstants.PLATFORM_WEB);

		if (model.getReturnValue() != null) {
			String[] returnValues = model.getReturnValue().split("_");
			String returnValue = getText(returnValues[0]);
			returnValue = returnValue.replace("{0}", returnValues[1]);
			return Result.error(returnValue);
		}
		return Result.success();
	}

	/**
	 * 供应商变更采购订单
	 * 
	 * @return
	 */
	@PostMapping(value = "/dealhold")
	public Result dealHold(@RequestBody JsonParam<PurchaseOrder> param) {
		PurchaseOrder model = param.getModel();
		model = purchaseOrderLogic.toHold(getUserId(), getUserName(), model);
		// 保存日志
		purchaseOrderLogic.addLog(getUserId(), getUserName(), model.getPurchaseOrderId(), "采购订单变更", SrmConstants.PERFORM_CHANGE,
				model.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 采购订单流程状态中文转换
	 * 
	 * @return
	 */
	@PostMapping(value = "/tranlatepurchaseordercheckstate")
	public String tranlatePurchaseOrderCheckState(int index) {
		String result = null;
		switch (index) {
		case 0:
			result = "待审核";
			break;
		case 1:
			result = "接受";
			break;
		case 2:
			result = "变更";
			break;
		case 3:
			result = "确认变更";
			break;
		case 4:
			result = "拒绝";
			break;
		case 5:
			result = "确认拒绝";
			break;
		}
		return result;
	}

	/**
	 * 获取采购订单明细定价条件
	 * 
	 * @retur
	 */
	@PostMapping(value = "/getpurchaseorderpricing")
	public String getPurchaseOrderPricing() {
		Map<String, Object> searchParams = buildParams();
		List<PurchaseOrderPricing> list = purchaseOrderPricingLogic.getPurchaseOrderPricing(searchParams, getRoleTypes());
		return DataUtils.toJson(list, "purchaseOrderDetail");
	}

	/**
	 * 获取采购订单双单位转换关系维护
	 * 
	 * @rturn
	 */
	@PostMapping(value = "/getpurchasedualunitconversion")
	public String getPurchaseDualUnitConversion() {
		Map<String, Object> searchParams = buildParams();
		List<PurchaseDualUnitConversion> list = purchaseDualUnitConversionLogic.findAll(searchParams);
		return DataUtils.toJson(list, "purchaseOrderDetail.purchaseDualUnitConversions");
	}

	/**
	 * 获取下拉值
	 * 
	 * @param value
	 * @return
	 */
	protected String getComboxValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			int endIndex = value.indexOf("]");
			if (-1 != endIndex) { // 数据如果不是[KG]千克，不作处理
				value = value.substring(1, endIndex);
			}
		}
		return value;
	}

	/**
	 * 校验时间数据
	 * 
	 * @param buyerTime
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String validateDate(Calendar buyerTime, int i, String key) {
		if (null != buyerTime && 10000 <= buyerTime.getTime().getYear()) {
			// porderDetail.batchImport.buyerTime.format =
			// 明细信息工作簿中第{0}行的交货日期格式不正确
			return getResource("porderDetail.batchImport." + key + ".format", i + "") + "\n";
		}

		return "";
	}

	/**
	 * 处理细单、细细单数据
	 * 
	 * @param detail
	 * @return
	 */
	protected PurchaseOrderDetail processData(PurchaseOrderDetail detail) {
		List<PurchaseOrderPricing> parsePricingArray = JSONArray.parseArray(detail.getPricingInfo(), PurchaseOrderPricing.class);
		List<PurchaseDualUnitConversion> parseUnitArray = JSONArray.parseArray(detail.getUnitConversionInfo(),
				PurchaseDualUnitConversion.class);
		List<PurchaseOrderPricing> parseArray1 = new ArrayList<PurchaseOrderPricing>(); // 定价条件
		List<PurchaseDualUnitConversion> parseArray2 = new ArrayList<PurchaseDualUnitConversion>(); // 双单位
		if (null != parsePricingArray) {
			for (PurchaseOrderPricing l : parsePricingArray) {
				if (l.getPurchaseOrderPricingId() != null) {
					l.setPurchaseOrderPricingId(null);
				}
				parseArray1.add(l);
			}
			detail.setPurchaseOrderDetailId(null);
			detail.setPurchaseOrderPricings(parseArray1);
			for (PurchaseOrderPricing pricing : detail.getPurchaseOrderPricings()) {
				pricing.setPurchaseOrderDetail(detail);
			}
		}
		if (null != parseUnitArray) {
			for (PurchaseDualUnitConversion l : parseUnitArray) {
				if (l.getPurchaseOrderQtyId() != null) {
					l.setPurchaseOrderQtyId(null);
				}
				parseArray2.add(l);
			}
			detail.setPurchaseDualUnitConversions(parseArray2);
			for (PurchaseDualUnitConversion unit : detail.getPurchaseDualUnitConversions()) {
				unit.setPurchaseOrderDetail(detail);
			}
		}
		return detail;
	}

}
