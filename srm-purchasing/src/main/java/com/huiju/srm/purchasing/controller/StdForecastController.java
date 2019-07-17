
package com.huiju.srm.purchasing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*import org.apache.struts2.ServletActionContext;*/
import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.fs.util.FileUploadUtils;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.module.util.IOUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.PoiUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.purchasing.entity.Forecast;
import com.huiju.srm.purchasing.entity.ForecastDtl;
import com.huiju.srm.purchasing.entity.ForecastState;
import com.huiju.srm.purchasing.service.ForecastService;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.service.VendorService;

/**
 * 采购预测 controller
 * 
 * @author bairx
 *
 */
public class StdForecastController extends CloudController {

	@Autowired
	protected ForecastService forecastServiceImpl;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupClient; // 资源过滤远程接口注入
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected BpmServiceClient bpmService;
	@Autowired
	protected PlantClient plantClient;
	@Autowired
	protected VendorService vendorServiceImpl;
	@Autowired
	protected MaterialClient materialClient;

	protected String purchasingOrgCode;
	protected String purchasingGroupCode;
	protected String plantCode;
	protected String materialCode;

	protected Calendar forecastMainStartDate;
	protected Calendar forecastStartDate;
	protected Calendar forecastMainEndDate;
	protected Calendar forecastEndDate;
	protected String className;

	protected String constantsBillType = SrmConstants.BILLTYPE_CGY;

	/**
	 * <pre>
	 * 获取列表 / 查询数据
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/list")
	public Page<Forecast> list(@RequestParam String initStates, String billFlag) {

		Page<Forecast> page = buildPage(Forecast.class);
		Map<String, Object> searchParams = buildParams();
		System.out.println("list");

		if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			searchParams.putAll(userAuthGroupClient
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Forecast.class)));
		}
		searchParams.put("DISTINCT", true);
		if (SrmConstants.ROLETYPE_V.equals(getRoleTypes())) {
			searchParams.put("EQ_forecastDtls_vendorErpCode", getErpCode());
			searchParams.put("EQ_status", ForecastState.TOPASS);
		}

		// 待处理，待审核初始化状态过滤
		if (initStates != null && !"".equals(initStates)) {
			String value = initStates;
			String[] values = value.trim().split(",");
			ForecastState[] statusArray = new ForecastState[values.length];
			for (int i = 0; i < values.length; i++) {
				ForecastState status = ForecastState.valueOf(values[i].replace(" ", ""));
				statusArray[i] = status;
			}
			searchParams.put("IN_status", statusArray);
		}
		// 待处理的单据,创建人才可以查看和编辑
		if ("undeal".equals(billFlag)) {

			// 1、当前用户创建且审核不过的单据
			// 2、当前用户拥有审核权限的单据

			List<String> idsList = new ArrayList<String>();

			idsList = bpmService.getAllUncheckedKeys(getUserId().toString(), SrmConstants.BILLTYPE_CGY);
			if (idsList.size() == 0) {
				idsList.add("000");
			}
			searchParams.put("(EQ_status && EQ_createUserId ) || (EQ_status && IN_forecastId)",
					new Object[] { ForecastState.TONOPASS, getUserId(), ForecastState.TOCONFIRM, idsList });

		}

		page = forecastServiceImpl.findAllWithoutAssociation(page, searchParams);
		return page;
	}

	/**
	 * <pre>
	 * 返回编辑表单数据对象
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/get")
	public String get(Long id) {
		Forecast model = forecastServiceImpl.findById(id);
		if (model == null) {
			return dealJson(false, "信息不存在！");
		}
		return dealJson(true, DataUtils.toJson(model, "forecastDtls"));
	}

	/**
	 * <pre>
	 * 保存表单
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/save")
	public Result save(@RequestBody JsonParam<Forecast> jsonParam) {
		Forecast model = jsonParam.getModel();
		String message = "";
		String submitFlag = jsonParam.getSubmitFlag();
		Map<String, Object> params = buildParams();
		params.put("EQ_forecastMainDate", Calendar.getInstance());
		Long count = forecastServiceImpl.count(params);
		if (0 < count.longValue()) {
			return Result.error(getText("forecastMain.message.dateExistsData"));
		}

		boolean flag = checkDetail(model.getForecastDtls());
		if (!flag) {
			return Result.error("预测需求日期数据重复，请重新输入");
		}

		Long userId = getUserId();
		String userName = getUserName();
		model.setForecastNo(billSetLogic.createNextRunningNum(constantsBillType));
		model.setClientCode(getClientCode());
		model.setForecastMainDate(Calendar.getInstance());
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		model.setCreateTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		this.setForecastOneToMaryValue(model);

		if ("save".equalsIgnoreCase(submitFlag)) {
			model.setStatus(ForecastState.NEW);
			model = forecastServiceImpl.save(model);
			// 增加操作日志
			forecastServiceImpl.addLog(getUserId(), getUserName(), model.getForecastId(), "采购预测创建", SrmConstants.PERFORM_SAVE,
					model.getForecastNo(), SrmConstants.PLATFORM_WEB);
		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			model.setStatus(ForecastState.NEW);
			model = forecastServiceImpl.save(model);
			forecastServiceImpl.dealStatus(userId, userName, model.getForecastId(), ForecastState.valueOf("TOCONFIRM"), message, false);
		}
		return Result.success(true);
	}

	/**
	 * 修改
	 * 
	 * @return String
	 */
	@PostMapping("/update")
	public Result update(@RequestBody JsonParam<Forecast> jsonParam) {
		String message = "";
		Forecast model = jsonParam.getModel();
		String submitFlag = jsonParam.getSubmitFlag();
		boolean flag = checkDetail(model.getForecastDtls());
		if (!flag) {
			return Result.error("预测需求日期数据重复，请重新输入");
		}

		Long userId = getUserId();
		String userName = getUserName();
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		Forecast pd = forecastServiceImpl.findById(model.getForecastId());
		if (pd == null) {
			return Result.error(getText("message.notexisted"));
		}
		model.setForecastMainDate(pd.getForecastMainDate());
		this.setForecastOneToMaryValue(model);
		if ("save".equalsIgnoreCase(submitFlag)) {
			model.setStatus(pd.getStatus());
			model.setCreateUserId(pd.getCreateUserId());
			model.setCreateUserName(pd.getCreateUserName());
			forecastServiceImpl.save(model);
			forecastServiceImpl.addLog(getUserId(), getUserName(), model.getForecastId(), "采购预测修改", SrmConstants.PERFORM_EDIT,
					model.getForecastNo(), SrmConstants.PLATFORM_WEB);
		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			forecastServiceImpl.dealStatus(userId, userName, model.getForecastId(), ForecastState.valueOf("TOCONFIRM"), message, false);

		}
		return Result.success(true);
	}

	/**
	 * 删除
	 * 
	 * @return String
	 */
	@PostMapping("/delete")
	public Result delete(@RequestParam List<Long> ids, @RequestParam String message) {
		forecastServiceImpl.removeByIds(ids, getUserId(), getUserName(), message);
		return Result.success(true);

	}

	/**
	 * 查询采购预测细表明细
	 */
	@PostMapping("/findforecastdtlall")
	public String findForecastDtlAll() {
		Map<String, Object> searchParams = buildParams();
		if (SrmConstants.ROLETYPE_V.equals(getRoleTypes())) {
			searchParams.put("EQ_vendorErpCode", getErpCode());
		}
		List<ForecastDtl> list = forecastServiceImpl.findForecastDtlAll(searchParams);
		return renderJson(DataUtils.toJson(list, new String[] { "forecast" }));
	}

	/**
	 * 设置招标单一对多的值
	 */
	protected void setForecastOneToMaryValue(Forecast model) {
		if (model.getForecastDtls() != null && model.getForecastDtls().size() > 0) {
			for (ForecastDtl item : model.getForecastDtls()) {
				item.setForecast(model);
			}
		}

	}

	/**
	 * 校验采购预测明细 唯一性
	 * 
	 * @param forecastDtls 采购预测明细
	 */
	protected boolean checkDetail(List<ForecastDtl> forecastDtls) {
		// TODO Auto-generated method stub
		ForecastDtl detail = new ForecastDtl();
		ForecastDtl detail1 = new ForecastDtl();
		for (int i = 0; i < forecastDtls.size(); i++) {
			detail = forecastDtls.get(i);
			for (int j = i + 1; j < forecastDtls.size(); j++) {

				detail1 = forecastDtls.get(j);

				if (detail.getMaterialCode().equals(detail1.getMaterialCode())
						&& detail.getForecastMainDate().compareTo(detail1.getForecastMainDate()) == 0
						&& detail.getPlantCode().equals(detail1.getPlantCode())) {
					return false;
				}

			}
		}
		return true;
	}

	/**
	 * 获取审核流程事件
	 * 
	 * @return
	 */
	@PostMapping("/getevents")
	public String geteventsgetEvents(String id) {
		Long s_userId = getUserId();
		String s_roleType = getRoleTypes();
		String[] events4Authorities = { // 事件对应的权限
				"forecast_topass", "forecast_toconfirm", "forecast_tonopass", "forecast_undeal_tonopass", "forecast_undeal_toconfirm",
				"forecast_undeal_topass" };
		StringBuffer buf = new StringBuffer();
		for (String auth : events4Authorities) {
			if (buf.length() > 0)
				buf.append(",");
			buf.append("'" + auth + "'");
		}
		String eventAuth = buf.toString();
		List<String> events = forecastServiceImpl.getForecastEvents(s_userId, s_roleType, Long.valueOf(id));
		buf = new StringBuffer("[");
		for (String event : events) {
			// System.out.println("event" + events);
			if (event != null) {
				if (eventAuth.indexOf("'forecast_" + event.toLowerCase() + "'") > -1
						|| eventAuth.indexOf("'forecast_undeal" + event.toLowerCase() + "'") > -1 || event.indexOf("#") > -1) {
					buf.append("'" + event + "',");
				}
			}
		}
		if (buf.length() > 1) {
			buf.append("'@'");
		}
		buf.append("]");
		return renderJson(buf.toString());
	}

	/**
	 * 处理流程状态
	 * 
	 * @return
	 */
	@PostMapping("/dealstatus")
	public String dealStatus(String id, String message, String billState) {
		System.out.print("billState" + billState);
		try {
			Long userId = getUserId();
			String userName = getUserName();
			forecastServiceImpl.dealStatus(userId, userName, Long.valueOf(id), ForecastState.valueOf(billState), message);
			// 操作成功的日志信息
			return dealJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false);
		}
	}

	/*   *//**
			 * 导出模板
			 * 
			 * @return String
			 */
	@PostMapping("/download")
	public String download() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new ClassPathResource("template/ForecastDtl.xls").getInputStream();
			out = response.getOutputStream();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment;filename=采购预测明细导入模板.xls");
			response.setContentLength(in.available());
			IOUtils.copy(in, out);
			out.flush();
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
		return NONE;
	}

	/**
	 * 数据导入
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/importdata")
	public Result importData(HttpServletRequest request) {
		Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
		String key = files.keySet().iterator().next();
		File excelFile = files.get(key);
		Map<String, Object> searchMaps = new HashMap<String, Object>();
		FileInputStream inputStream = null;
		HSSFWorkbook wb = null;
		try {
			inputStream = new FileInputStream(excelFile);
			wb = new HSSFWorkbook(inputStream);
			HSSFSheet sheet = wb.getSheetAt(0);
			if (0 == sheet.getLastRowNum() || 1001 <= sheet.getLastRowNum()) {
				return Result.error("不允许导入空数据行或者不能超过1000行");
			}
			List<ForecastDtl> results = new ArrayList<ForecastDtl>();
			String value = "";
			List<String> materialRecords = new ArrayList<String>();
			List<String> plantRecords = new ArrayList<String>();
			List<String> vendorRecords = new ArrayList<String>();
			for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
				HSSFRow row = sheet.getRow(rowSeq);
				int cellSeq = 0;
				if (null == row || PoiUtils.isBlankRow(row)) {
					continue;
				}
				// 物料编码
				HSSFCell cell = row.getCell(cellSeq++);
				value = PoiUtils.getCellStringValue(cell);
				if (StringUtils.isNotBlank(value)) {
					if (!materialRecords.contains(value)) {
						materialRecords.add(value);
					}
				}
				// 物料名称
				cellSeq++;
				// 预测时间
				cellSeq++;
				// 预测数量
				cellSeq++;
				// 单位
				cellSeq++;
				// 工厂
				cell = row.getCell(cellSeq++);
				value = PoiUtils.getCellStringValue(cell);
				if (StringUtils.isNotBlank(value)) {
					if (!plantRecords.contains(value)) {
						plantRecords.add(value);
					}
				}
				// 供应商
				cell = row.getCell(cellSeq++);
				value = PoiUtils.getCellStringValue(cell);
				if (StringUtils.isNotBlank(value)) {
					if (!vendorRecords.contains(value)) {
						vendorRecords.add(value);
					}
				}
			}
			// 获取匹配数据
			// 获取相关的供应商
			Map<String, Vendor> vendorMaps = new HashMap<String, Vendor>();
			searchMaps.clear();
			searchMaps.put("IN_vendorErpCode", StringUtils.join(vendorRecords, ","));
			List<Vendor> vendors = vendorServiceImpl.findAllWithoutAssociation(searchMaps);
			if (vendors != null && vendors.size() > 0) {
				for (Vendor v : vendors) {
					vendorMaps.put(v.getVendorErpCode(), v);
				}
			}
			Map<String, String> plantMaps = new HashMap<String, String>();
			Map<String, Object> plantParams = new HashMap<String, Object>();
			plantParams.put("EQ_clientCode", getClientCode());
			plantParams.put("IN_plantCode", StringUtils.join(plantRecords, ","));
			FeignParam<Plant> fparams = new FeignParam<Plant>();
			fparams.setParams(plantParams);
			List<Plant> plants = plantClient.findAllWithoutAssociation(fparams);
			if (plants != null && plants.size() > 0) {
				for (Plant p : plants) {
					plantMaps.put(p.getPlantCode(), p.getPlantName());
				}
			}

			Map<String, Material> materialMaps = new HashMap<String, Material>();
			Map<String, Object> materialParams = new HashMap<String, Object>();
			FeignParam<Material> materialParam = new FeignParam<Material>();
			materialParams.put("EQ_clientCode", getClientCode());
			materialParams.put("IN_materialCode", StringUtils.join(materialRecords, ","));
			materialParam.setParams(materialParams);
			List<Material> materials = materialClient.findAllWithoutAssociation(materialParam);
			if (materials != null && materials.size() > 0) {
				for (Material m : materials) {
					materialMaps.put(m.getMaterialCode(), m);
				}
			}
			// 获取完成的导入明细数据
			String resultMsg = getSheetData(sheet, results, vendorMaps, plantMaps, materialMaps);
			// 判断是否有错误
			if (StringUtils.isNotBlank(resultMsg)) {
				return Result.error(resultMsg.toString());
			} else {
				// 返回数据给前台
				return Result.success(DataUtils.toJson(results, new String[] { "forecast" }));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// renderHtml("{success:false,info:'请下载模板填入信息后导入'}");
			return Result.error("请下载模板填入信息后导入'");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
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
	}

	public String getSheetData(HSSFSheet sheet, List<ForecastDtl> results, Map<String, Vendor> vendorMaps, Map<String, String> plantMaps,
			Map<String, Material> materialMaps) {
		String sheetTitle = "采购预测明细";
		String eachMessage = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int index = 1;
		String value = "";
		StringBuilder messages = new StringBuilder("");
		for (int rowSeq = 1, rowCount = sheet.getLastRowNum(); rowSeq <= rowCount; rowSeq++) {
			HSSFRow row = sheet.getRow(rowSeq);
			ForecastDtl detail = new ForecastDtl();// 采购预测明细
			int cellSeq = 0;
			if (null == row || PoiUtils.isBlankRow(row)) {
				continue;
			}
			// 物料编码
			HSSFCell cell = row.getCell(cellSeq++);
			value = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isBlank(value)) {
				eachMessage += sheetTitle + "中第" + index + "行中的物料编码不能为空\n";
			} else {
				if (materialMaps.containsKey(value)) {
					// 设置物料编码、名称、单位编码、单位名称
					detail.setMaterialCode(value);
					Material m = materialMaps.get(value);
					detail.setMaterialName(m.getMaterialName());
					detail.setUnitName(m.getBaseUnitCode());
				} else {
					eachMessage += sheetTitle + "中第" + index + "行中的物料编码不存在\n";
				}
			}
			cellSeq++;
			// 预测时间
			cell = row.getCell(cellSeq++);
			value = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isBlank(value)) {
				eachMessage += sheetTitle + "中第" + index + "行中的预测需求日期不能为空\n";
			} else {
				try {
					Date date = sdf.parse(value);// 预测需求时间
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					detail.setForecastMainDate(calendar);
				} catch (Exception e) {
					// 日期格式错误
					eachMessage += sheetTitle + "中第" + index + "行中的预测需求日期数据格式错误\n";
				}
			}

			// 预测数量
			cell = row.getCell(cellSeq++);
			value = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isNotBlank(value)) {
				if (isNumber(value)) {
					detail.setForecastNum(PoiUtils.getCellBigDecimalValue(cell));
				} else {
					eachMessage += sheetTitle + "中第" + index + "行中的预测数量数据格式错误\n";
				}
			} else {
				eachMessage += sheetTitle + "中第" + index + "行中的预测数量不能为空\n";
			}

			// 单位
			cellSeq++;
			// cell = row.getCell(cellSeq++);
			// value = PoiUtils.getCellStringValue(cell);
			// if (StringUtils.isNotBlank(value)) {
			// detail.setUnitName(value);
			// } else {
			// eachMessage += sheetTitle + "中第" + index + "行中的单位不能为空\n";
			// }

			// 工厂
			cell = row.getCell(cellSeq++);
			value = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isNotBlank(value)) {
				if (plantMaps.containsKey(value)) {
					detail.setPlantCode(value);
					detail.setPlantName(plantMaps.get(value));
				} else {
					eachMessage += sheetTitle + "中第" + index + "行中的工厂编码不存在\n";
				}
			}

			// 供应商
			cell = row.getCell(cellSeq++);
			value = PoiUtils.getCellStringValue(cell);
			if (StringUtils.isNotBlank(value)) {
				if (vendorMaps.containsKey(value)) {
					Vendor vendor = vendorMaps.get(value);
					detail.setVendorCode(vendor.getVendorCode());
					detail.setVendorErpCode(vendor.getVendorErpCode());
					detail.setVendorName(vendor.getVendorName());
				} else {
					eachMessage += sheetTitle + "中第" + index + "行中的供应商编码不存在\n";
				}
			} else {
				eachMessage += sheetTitle + "中第" + index + "行中的供应商编码不能为空\n";
			}

			results.add(detail);
			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}
		return messages.toString();
	}

	/**
	 * 校验是不是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		String reg = "^[0-9]+(.[0-9]+)?$";
		return str.matches(reg);
	}

	/*
	 * /** 从sap中查询采购申请信息
	 * @return
	 */
	@PostMapping("/purchasingapplysearch")
	public String purchasingApplySearch(String purchasingOrgCode, String purchasingGroupCode, String materialCode, String plantCode,
			String forecastMainStartDate, String forecastMainEndDate, String forecastStartDate, String forecastEndDate) {
		String json = forecastServiceImpl.purchasingApplySearch4String(getClientCode(), purchasingOrgCode, purchasingGroupCode,
				materialCode, plantCode, forecastMainStartDate, forecastMainEndDate, forecastStartDate, forecastEndDate);
		return renderJson(json);

	}

	/**
	 * 撤销审批
	 * 
	 * @return
	 */
	@PostMapping("/revokeaudit")
	public String revokeAudit(String id) {
		try {
			String result = forecastServiceImpl.revokeAudit(Long.valueOf(id), getUserId(), getUserName());

			return dealJson(true, result);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false);
		}
	}

	// =================================================set/get
	// ===================================

	public String getPurchasingOrgCode() {
		return purchasingOrgCode;
	}

	public void setPurchasingOrgCode(String purchasingOrgCode) {
		this.purchasingOrgCode = purchasingOrgCode;
	}

	public String getPurchasingGroupCode() {
		return purchasingGroupCode;
	}

	public void setPurchasingGroupCode(String purchasingGroupCode) {
		this.purchasingGroupCode = purchasingGroupCode;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public Calendar getForecastMainStartDate() {
		return forecastMainStartDate;
	}

	public void setForecastMainStartDate(Calendar forecastMainStartDate) {
		this.forecastMainStartDate = forecastMainStartDate;
	}

	public Calendar getForecastStartDate() {
		return forecastStartDate;
	}

	public void setForecastStartDate(Calendar forecastStartDate) {
		this.forecastStartDate = forecastStartDate;
	}

	public Calendar getForecastMainEndDate() {
		return forecastMainEndDate;
	}

	public void setForecastMainEndDate(Calendar forecastMainEndDate) {
		this.forecastMainEndDate = forecastMainEndDate;
	}

	public Calendar getForecastEndDate() {
		return forecastEndDate;
	}

	public void setForecastEndDate(Calendar forecastEndDate) {
		this.forecastEndDate = forecastEndDate;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
