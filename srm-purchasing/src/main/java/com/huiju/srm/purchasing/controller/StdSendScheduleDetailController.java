package com.huiju.srm.purchasing.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.purchasing.service.SendScheduleDetailService;

/**
 * 送货排程action 产品
 * 
 * @author CWQ date 2016-08-09 15:20:35
 */
@Certificate(value = { "CP_sendschedule" }, requiredType = RequiredType.ONE)
public class StdSendScheduleDetailController extends CloudController {
	@Autowired(required = false)
	protected SendScheduleDetailService sendScheduleDetailLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient CloudController;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthLogic;
	protected String className;

	/**
	 * 导出报表
	 * 
	 * @return
	 */
	@PostMapping(value = "/exprot")
	public Result exprot() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = request.getServletContext().getResourceAsStream("/template/SendScheduleDetails.xls");
			if (null == in) {
				Result.error("获取模版路径失败");
			}

			HSSFWorkbook wb = new HSSFWorkbook(in);
			HSSFSheet sheet = wb.getSheetAt(0);

			// 设置表头
			setTitle(wb, sheet);
			// 查询数据
			Map<String, Object> searchParams = buildParams();
			setSearchParams(searchParams);
			List<SendScheduleDetail> list = sendScheduleDetailLogic.findAll(searchParams);
			// 设置主单的采购组织名称
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setPurchasingOrgName(list.get(i).getSendScheduleCommon().getSendSchedule().getPurchasingOrgName());
			}
			setContent(wb, sheet, list);

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=SendScheduleDetails.xls");
			ServletOutputStream stream = response.getOutputStream();
			wb.write(stream);
			stream.flush();
			stream.close();
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
	 * 加载列表数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public String list() {
		Page<SendScheduleDetail> page = buildPage(SendScheduleDetail.class);
		Map<String, Object> searchParams = buildParams();
		setSearchParams(searchParams);
		page = sendScheduleDetailLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, "sendScheduleCommons");
	}

	/**
	 * 设置列表查询参数
	 * 
	 * @param searchParams
	 */
	protected void setSearchParams(Map<String, Object> searchParams) {
		searchParams.put("EQ_sendScheduleCommon_sendSchedule_sendScheduleState", SendScheduleState.OPEN);

		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			// 供应商只能查看到自己且状态不为新建的数据
			searchParams.put("EQ_vendorCode", getErpCode());

		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			// 资源组查询
			searchParams.putAll(userAuthLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), SendScheduleDetail.class)));
		}
	}

	/**
	 * 设置excel内容
	 * 
	 * @param wb工作簿对象
	 * @param titleRow表格对象
	 * @param list排程明细集合
	 */
	protected void setContent(HSSFWorkbook wb, HSSFSheet sheet, List<SendScheduleDetail> list) {
		int rowSeq = 2;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (SendScheduleDetail sd : list) {
			int cellSeq = 0;
			HSSFRow row = sheet.createRow(rowSeq++);

			HSSFCell cell = row.createCell(cellSeq++);
			cell.setCellValue(sdf.format(sd.getScheduleTime().getTime()));

			cell = row.createCell(cellSeq++);
			if (sd.getSendFlag() != null && sd.getSendFlag() == 0) {
				cell.setCellValue("未送货");
			} else if (sd.getSendFlag() != null && sd.getSendFlag() == 1) {
				cell.setCellValue("部分送货");
			} else {
				cell.setCellValue("已送货");
			}

			cell = row.createCell(cellSeq++);
			cell.setCellValue(sd.getMaterialCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(sd.getMaterialName());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(sd.getVendorCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(sd.getVendorName());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getScheduleQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getDeliveryQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getOnWayQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getReceiptQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getReturnGoodsQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getCanSendQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getSendScheduleNo()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getPurchaseOrderNo()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getRowIds()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getSendQty()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getUnitCode()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getPlantCode()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getStockLocal()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getPurchasingOrgCode()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(getStringValue(sd.getPurchasingOrgName()));
		}
	}

	/**
	 * 获取字符串类型的值
	 * 
	 * @param obj要转换的对象
	 * @return
	 */
	protected String getStringValue(Object obj) {
		if (null == obj) {
			return "";
		}

		return obj.toString();
	}

	/**
	 * 设置excel 标题
	 * 
	 * @param wb工作簿对象
	 * @param titleRow表格标题行
	 */
	protected void setTitle(HSSFWorkbook wb, HSSFSheet sheet) {
		HSSFFont font = wb.createFont();
		font.setColor(Font.COLOR_RED);
		HSSFRow titleRow = sheet.getRow(1);

		for (int i = 0, len = titleRow.getLastCellNum(); i < len && null != titleRow; i++) {
			HSSFCell cell = titleRow.getCell(i);
			if (null != cell && cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String key = cell.getStringCellValue();
				String allowBlank = "";
				if (key.contains("*")) {
					key = key.substring(1);
					allowBlank = "*";
				}

				key = getText(key);
				HSSFRichTextString richString = new HSSFRichTextString(key + allowBlank);
				richString.applyFont(key.length(), key.length() + allowBlank.length(), font);

				if (StringUtils.isNotBlank(key)) {
					cell.setCellValue(richString);
				}
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
