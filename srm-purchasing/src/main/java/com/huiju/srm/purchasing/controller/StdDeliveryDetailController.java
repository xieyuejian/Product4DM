package com.huiju.srm.purchasing.controller;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.DataDictClient;
import com.huiju.srm.masterdata.entity.DataDict;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.service.DeliveryDtlService;

@Certificate(value = { "CP_delivery" }, requiredType = RequiredType.ONE)
public class StdDeliveryDetailController extends CloudController {
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthLogic;
	@Autowired(required = false)
	protected DeliveryDtlService deliveryDetailLogic;
	@Autowired(required = false)
	protected DataDictClient dataDictLogic;

	protected String status; // 查询单据状态
	protected String closeFlag;
	protected String lineItemTypes;
	protected String className;

	public Map<String, Object> getSearchParams() {
		String initStates = getInitStates();
		// String roleType = getRoleType();
		Map<String, Object> searchParams = buildParams();
		// 供应商只可以查看自己的送货单
		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			searchParams.put("EQ_delivery_vendorErpCode", getErpCode());
			// 采购只可以查看所属采购组织的送货单
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			Map<String, Object> userAuthMap = userAuthLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Delivery.class));
			// 当前用户所属采购组织
			if (userAuthMap.containsKey("IN_purchasingOrgCode")) {
				searchParams.put("IN_delivery_purchasingOrgCode", userAuthMap.get("IN_purchasingOrgCode"));
			}
			// 当前用户所属工厂
			if (userAuthMap.containsKey("IN_plantCode")) {
				searchParams.put("IN_delivery_plantCode", userAuthMap.get("IN_plantCode"));
			}
		}
		// 单据状态查询
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			List<DeliveryState> statusArray = new ArrayList<DeliveryState>();
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replaceAll(" ", "");
				DeliveryState status = DeliveryState.valueOf(values[i]);
				statusArray.add(status);
			}
			searchParams.put("IN_delivery_status", statusArray);
		}

		// 关闭标识
		if (closeFlag != null) {
			String value = closeFlag;
			String[] values = value.trim().split(",");
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replaceAll(" ", "");
			}
			searchParams.put("IN_closeFlag", values);
		}

		// 行项目类别
		if (lineItemTypes != null) {
			String value = lineItemTypes;
			String[] values = value.trim().split(",");
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replaceAll(" ", "");
			}
			searchParams.put("IN_lineItemTypes", values);
		}

		searchParams.put("EQ_cancelFlag", "0");
		return searchParams;
	}

	/**
	 * 获取列表/查询数据（分页）
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public String list(String initStates) {
		Page<DeliveryDtl> page = buildPage(DeliveryDtl.class);
		Map<String, Object> searchParams = getSearchParams();
		page = deliveryDetailLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, "deliveryDtls");
	}

	/**
	 * 导出
	 * 
	 * @return
	 */
	@PostMapping("/exportexcel")
	public void exportExcel() {
		Map<String, Object> searchParams = getSearchParams();
		List<DeliveryDtl> deliveryDtls = deliveryDetailLogic.findAll(searchParams);

		FileInputStream fis = null;
		OutputStream os = null;
		try {
			Workbook wb = new HSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			Sheet sheet = wb.createSheet("delivery view");

			for (int i = 0; i < 21; i++) {
				sheet.setColumnWidth(i, 17 * 256);
			}

			CellStyle title = wb.createCellStyle();
			title.setFillPattern(CellStyle.SOLID_FOREGROUND);
			title.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			title.setAlignment(CellStyle.ALIGN_CENTER);
			title.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

			CellStyle content = wb.createCellStyle();
			title.setAlignment(CellStyle.ALIGN_CENTER);
			title.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

			CellStyle date = wb.createCellStyle();
			date.setAlignment(CellStyle.ALIGN_CENTER);
			date.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			date.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));

			CellStyle number = wb.createCellStyle();
			number.setAlignment(CellStyle.ALIGN_CENTER);
			number.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			number.setDataFormat(wb.createDataFormat().getFormat("0.000"));

			Row row0 = sheet.createRow((short) 0);// 第一行

			setTitleCell(row0, 0, "送货单状态", title);
			setTitleCell(row0, 1, "采购订单号", title);
			setTitleCell(row0, 2, "行号", title);
			setTitleCell(row0, 3, "物料编码", title);
			setTitleCell(row0, 4, "物料名称", title);
			setTitleCell(row0, 5, "单位编码", title);
			setTitleCell(row0, 6, "送货数量", title);
			setTitleCell(row0, 7, "收货数量", title);
			setTitleCell(row0, 8, "供应商编码", title);
			setTitleCell(row0, 9, "供应商名称", title);
			setTitleCell(row0, 10, "送货单号", title);
			setTitleCell(row0, 11, "采购组织编码", title);
			setTitleCell(row0, 12, "采购组织名称", title);
			setTitleCell(row0, 13, "工厂", title);
			setTitleCell(row0, 14, "库存地点", title);
			setTitleCell(row0, 15, "送货日期", title);
			setTitleCell(row0, 16, "送货方式", title);
			setTitleCell(row0, 17, "送达日期", title);
			setTitleCell(row0, 18, "排程单号", title);
			setTitleCell(row0, 19, "行项目类别", title);
			setTitleCell(row0, 20, "关闭标识", title);

			for (int i = 1; i <= deliveryDtls.size(); i++) {
				Row row = sheet.createRow((short) i);
				setContentCell(row, 0, deliveryDtls.get(i - 1).getDelivery().getStatus().getStateDesc(), content);
				setContentCell(row, 1, deliveryDtls.get(i - 1).getPurchaseOrderCode(), content);
				setContentCell(row, 2, String.valueOf(deliveryDtls.get(i - 1).getLineNumber()), content);
				setContentCell(row, 3, deliveryDtls.get(i - 1).getMaterialCode(), content);
				setContentCell(row, 4, deliveryDtls.get(i - 1).getMaterialName(), content);
				setContentCell(row, 5, deliveryDtls.get(i - 1).getUnitCode(), content);
				if (deliveryDtls.get(i - 1).getDeliveryNumber() != null) {
					setNumberCell(row, 6, deliveryDtls.get(i - 1).getDeliveryNumber().doubleValue(), number);
				}

				if (deliveryDtls.get(i - 1).getReceivedNumber() != null) {
					setNumberCell(row, 7, deliveryDtls.get(i - 1).getReceivedNumber().doubleValue(), number);
				}

				setContentCell(row, 8, deliveryDtls.get(i - 1).getDelivery().getVendorCode(), content);
				setContentCell(row, 9, deliveryDtls.get(i - 1).getDelivery().getVendorName(), content);
				setContentCell(row, 10, deliveryDtls.get(i - 1).getDelivery().getDeliveryCode(), content);
				setContentCell(row, 11, deliveryDtls.get(i - 1).getDelivery().getPurchasingOrgCode(), content);
				setContentCell(row, 12, deliveryDtls.get(i - 1).getDelivery().getPurchasingOrgName(), content);
				setContentCell(row, 13, deliveryDtls.get(i - 1).getDelivery().getPlantName(), content);
				setContentCell(row, 14, deliveryDtls.get(i - 1).getDelivery().getStorageLocationName(), content);
				setDateCell(row, 15, deliveryDtls.get(i - 1).getDelivery().getDeliveryDate(), date);
				Map<String, Object> searchMap = new HashMap<String, Object>();
				searchMap.put("EQ_groupCode", "deliveryTypes");
				searchMap.put("EQ_status", "1");
				searchMap.put("EQ_itemCode", deliveryDtls.get(i - 1).getDelivery().getDeliveryTypes());

				FeignParam<DataDict> param = new FeignParam<DataDict>(searchMap, null);
				List<DataDict> dataDicts = dataDictLogic.findAll(param);
				setContentCell(row, 16, dataDicts.get(0).getItemName(), content);

				if (deliveryDtls.get(i - 1).getDelivery().getServiceDate() != null) {
					setDateCell(row, 17, deliveryDtls.get(i - 1).getDelivery().getServiceDate(), date);
				}

				setContentCell(row, 18, deliveryDtls.get(i - 1).getScheduleCode(), content);
				setContentCell(row, 19, deliveryDtls.get(i - 1).getLineItemTypes(), content);
				setContentCell(row, 20, closeFlag(deliveryDtls.get(i - 1)), content);
			}

			response.reset();
			String fileName = "送货看板.xls";
			fileName = fileName.replaceAll(" ", "");
			fileName = new String(fileName.getBytes("UTF-8"), "iso8859-1");
			response.setContentType("application/x-msdownload");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			wb.close();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 设置标题栏单元格
	 * 
	 * @param row 行号
	 * @param number 列号
	 * @param cellValue 单元格值
	 * @param tytle 单元格样式
	 */
	protected static void setTitleCell(Row row, int number, String cellValue, CellStyle tytle) {
		Cell index = row.createCell(number);
		index.setCellValue(cellValue);
		index.setCellStyle(tytle);
	}

	/**
	 * 设置内容栏单元格
	 * 
	 * @param row 行号
	 * @param number 列号
	 * @param cellValue 单元格值
	 * @param tytle 单元格样式
	 */
	protected static void setContentCell(Row row, int number, String cellValue, CellStyle tytle) {
		Cell index = row.createCell(number);
		index.setCellValue(cellValue);
		index.setCellStyle(tytle);
	}

	/**
	 * 设置日期单元格
	 * 
	 * @param row 行号
	 * @param number 列号
	 * @param cellValue 单元格值
	 * @param tytle 单元格样式
	 */
	protected static void setDateCell(Row row, int number, Calendar cellValue, CellStyle tytle) {
		Cell index = row.createCell(number);
		index.setCellValue(cellValue.getTime());
		index.setCellStyle(tytle);
	}

	/**
	 * 设置数字单元格
	 * 
	 * @param row 行号
	 * @param number 列号
	 * @param cellValue 单元格值
	 * @param tytle 单元格样式
	 */
	protected static void setNumberCell(Row row, int number, double cellValue, CellStyle tytle) {
		Cell index = row.createCell(number);
		index.setCellValue(cellValue);
		index.setCellStyle(tytle);
	}

	protected String closeFlag(DeliveryDtl deliveryDtl) {
		String value = deliveryDtl.getCloseFlag();
		if (value.equals("0")) {
			return "否";
		} else {
			return "是";
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(String closeFlag) {
		this.closeFlag = closeFlag;
	}

	public String getLineItemTypes() {
		return lineItemTypes;
	}

	public void setLineItemTypes(String lineItemTypes) {
		this.lineItemTypes = lineItemTypes;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
