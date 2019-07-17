package com.huiju.srm.purchasing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.fs.util.FileUploadUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.util.IOUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.CloudReportController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.api.TaxRateClient;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.service.CensorQualityService;
import com.huiju.srm.purchasing.service.PurchaseDualUnitConversionService;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.purchasing.service.ReceivingNoteService;

/**
 * 收货Action
 * 
 * @author zhengjf
 *
 */
@Certificate(value = { "CP_receiving" }, requiredType = RequiredType.ONE)
public class StdReceivingNoteController extends CloudReportController {

	@Autowired(required = false)
	protected ReceivingNoteService receivingNoteLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected PlantClient plantLogic;
	@Autowired(required = false)
	protected MaterialClient materialLogic;
	@Autowired(required = false)
	protected StockLocationClient stockLocationLogic;
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired(required = false)
	protected TaxRateClient taxRateLogic;
	@Autowired(required = false)
	protected PurchaseDualUnitConversionService purchaseDualUnitConversionLogic;
	@Autowired(required = false)
	protected MaterialPlantClient materialPlantLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired
	protected CensorQualityService censorQualityLogic;

	/**
	 * 获取列表/查询数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public Page<ReceivingNote> list() {
		String vendorCode = getErpCode();
		Page<ReceivingNote> page = buildPage(ReceivingNote.class);
		Map<String, Object> searchParams = buildParams();

		if (isRoleOf(SrmConstants.ROLETYPE_V)) { // 供应商
			// 供应商只能查看到自己的数据
			searchParams.put("EQ_vendorCode_OR_EQ_vendorErpCode", new String[] { getUserCode(), vendorCode });
			// 只有采购才要根据资源组过滤
		} else {
			Map<String, Object> searchParams1 = userAuthGroupLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), ReceivingNote.class));
			searchParams.putAll(searchParams1);
		}

		if (searchParams.containsKey("LE_certificateDate")) {
			String time = searchParams.get("LE_certificateDate").toString();
			if (StringUtils.isNotBlank(time)) {
				time += " 23:59:59";
				searchParams.put("LE_certificateDate", time);
			}
		}

		return receivingNoteLogic.findAll(page, searchParams);
	}

	/**
	 * 重写查询条件
	 */
	@Override
	public Map<String, Object> buildCondition() {
		Map<String, Object> params = buildParams();
		String systemRole = getRoleTypes();
		StringBuffer sql = new StringBuffer(" where 1 = 1 ");

		if (SrmConstants.ROLETYPE_V.equals(systemRole)) { // 供应商
			sql.append(" and t.vendorErpCode = '").append(getErpCode()).append("' ");
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			Map<String, Object> poaramsTemp = userAuthGroupLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), ReceivingNote.class));
			params.putAll(poaramsTemp);
		}

		setSQlCondition(params, sql);
		params.clear();
		String condition = sql.toString().replaceAll("receivingNoteNo", "grnNo");
		params.put("sql", condition);
		return params;
	}

	/**
	 * 设置查询条件
	 * 
	 * @param params 查询载体
	 * @param sql 查询语句
	 */
	protected void setSQlCondition(Map<String, Object> params, StringBuffer sql) {
		for (String key : params.keySet()) {
			String value = params.get(key).toString();

			if (key.startsWith("LIKE_")) {
				String fieldName = key.split("_")[1];
				sql.append(" and t.").append(fieldName).append(" like '%").append(value).append("%' ");
			}

			if (key.startsWith("EQ_")) {
				String fieldName = key.split("_")[1];
				sql.append(" and t.").append(fieldName).append(" = '").append(value).append("' ");
			}

			if (key.startsWith("GE_")) {
				String fieldName = key.split("_")[1];
				value += " 23:59:59";
				sql.append(" and t.").append(fieldName).append(" >= to_date('").append(value).append("','yyyy-mm-dd hh24:mi:ss') ");
			}

			if (key.startsWith("LE_")) {
				String fieldName = key.split("_")[1];
				value += " 00:00:00";
				sql.append(" and t.").append(fieldName).append(" <= to_date('").append(value).append("','yyyy-mm-dd hh24:mi:ss') ");
			}

			if (key.startsWith("IN_")) {
				String fieldName = key.split("_")[1];
				sql.append(" and t.").append(fieldName).append(" in (").append(value).append(") ");
			}
		}
	}

	/**
	 * 返回编辑表单数据对象
	 * 
	 * @return
	 */
	@PostMapping(value = "/get")
	public Result get(Long id) {
		ReceivingNote model = receivingNoteLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		return Result.success(model);
	}

	/**
	 * 下载模板
	 * 
	 * @return
	 */
	@PostMapping(value = "/download")
	public Result downLoad() {
		InputStream in = null;
		OutputStream out = null;
		try {
			String templateFile = getAttribute("templateFile");
			String fileName = getAttribute("fileName");
			// in = request.getServletContext().getResourceAsStream("/template/"
			// + templateFile);
			in = new ClassPathResource("template/" + templateFile).getInputStream();
			out = response.getOutputStream();
			if (null == in) {
				return Result.error("获取模版路径失败");
			}

			fileName = new String(fileName.getBytes("UTF-8"), "iso8859-1") + ".xls";

			if (templateFile.equals("ReceivingNote.xls")) {
				response.reset();
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.setContentLength(in.available());
				IOUtils.copy(in, out);
				out.flush();
			}

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
	 * 导入数据
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	@PostMapping(value = "/importexcel")
	public Result importExcel(HttpServletRequest request) {
		FileInputStream inputStream = null;
		try {
			Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
			String key = files.keySet().iterator().next();
			File excelFile = files.get(key);
			inputStream = new FileInputStream(excelFile);
			HSSFSheet sheet = new HSSFWorkbook(inputStream).getSheetAt(0);
			Map<String, Object> webParams = buildParams();
			String messgae = receivingNoteLogic.batchImportExcel(sheet, webParams, getUserId(), getUserName(), getClientCode());
			if (StringUtils.isNotBlank(messgae)) {
				return Result.error(messgae);
			}
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("导入失败！");
		}
	}

	/**
	 * 冲销
	 * 
	 * @return
	 */
	@PostMapping(value = "/chargeoff")
	public Result chargeOff(@RequestBody JsonParam<ReceivingNote> param) {
		try {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_receivingNoteNo", param.getModel().getReceivingNoteNo());
			searchParams.put("NE_status", CensorQualityState.TOCHECK);
			List<CensorQuality> cqs = censorQualityLogic.findAll(searchParams);
			if (cqs != null && cqs.size() > 0) {
				return Result.error("该收货单已质检，不允许冲销！");
			}
			receivingNoteLogic.chargeOff(param.getModel());
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getCause().getMessage());
		}
	}
}
