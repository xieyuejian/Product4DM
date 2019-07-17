package com.huiju.srm.purchasing.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.data.jpa.utils.QueryUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;

/**
 * 采购订单明细action 产品
 * 
 * @author CWQ
 * 
 */
@Certificate(value = { "CP_order" }, requiredType = RequiredType.ONE)
public class StdPurchaseOrderDetailController extends CloudController {
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected UserAuthGroupServiceClient userAuthGroupLogic;

	protected String operate;
	protected Integer closeFlag;
	protected String className;

	/**
	 * 获取列表数据
	 */
	@PostMapping(value = "/list")
	public String list() {
		Page<PurchaseOrderDetail> page = buildPage(PurchaseOrderDetail.class);
		Map<String, Object> searchParams = buildParams();
		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			// 供应商角色只能查看自己的
			searchParams.put("EQ_purchaseOrder_vendorErpCode", getErpCode());
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			Map<String, Object> userAuthMap = userAuthGroupLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Delivery.class));
			// 当前用户所属采购组织
			if (userAuthMap.containsKey("IN_purchasingOrgCode")) {
				searchParams.put("IN_purchaseOrder_purchasingOrgCode", userAuthMap.get("IN_purchasingOrgCode"));
			}
			// 当前用户所属采购组
			if (userAuthMap.containsKey("IN_purchasingGroupCode")) {
				searchParams.put("IN_purchaseOrder_purchasingGroupCode", userAuthMap.get("IN_purchasingGroupCode"));
			}
			// 当前用户所属公司
			if (userAuthMap.containsKey("IN_companyCode")) {
				searchParams.put("IN_purchaseOrder_companyCode", userAuthMap.get("IN_companyCode"));
			}
		}
		page = purchaseOrderDetailLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, "purchaseDualUnitConversions", "purchaseOrderPricings", "purchaseOrder.purchaseOrderDetails");
	}

	/**
	 * 设置查询参数
	 * 
	 * @param searchParams
	 */
	protected void setSearchParams(Map<String, Object> searchParams) {
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		searchParams.put("NE_deleteFlag", 1);

		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			searchParams.put("EQ_purchaseOrder_vendorErpCode", getErpCode());
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			// 资源组查询
			Map<String, Object> userAuthParams = userAuthGroupLogic.buildAuthFieldParamsDetail(new UserAuthGroupParam(getClientCode(),
					getUserCode(), PurchaseOrder.class, new String[] { "purchaseOrder", "materialGroupCode", "plantCode" }));
			searchParams.putAll(userAuthParams);
		}
	}

	/**
	 * 加载细单数据
	 */
	@PostMapping(value = "/findall")
	public List<PurchaseOrderDetail> findAll() {
		Map<String, Object> searchParams = buildParams();
		int roleType = getRoleTypes().indexOf(SrmConstants.ROLETYPE_V);
		return purchaseOrderDetailLogic.findAllDtl(searchParams, roleType);
	}

	/**
	 * 关闭明细
	 */
	@PostMapping(value = "/close")
	public Result close(Long id) {
		try {
			String operate = request.getParameter("operate");
			Integer closeFlag = Integer.parseInt(request.getParameter("closeFlag"));
			PurchaseOrderState state = purchaseOrderLogic.closePurchaseOrderDetail(id, operate, closeFlag, getUserId(), getUserName());
			if (state != null) {
				return Result.success();
			} else {
				return Result.error("所选订单明细在途量不为0，不能关闭！");
			}
		} catch (Exception e) {
			return Result.error("");
		}
	}

	@PostMapping(value = "/export")
	public Result export() {
		InputStream in = null;
		OutputStream out = null;
		try {
			Map<String, Object> searchParams = buildParams();
			setSearchParams(searchParams);// 设置参数
			Sort sort = QueryUtils.buildSort("purchaseOrder.purchaseOrderNo,DESC");
			List<PurchaseOrderDetail> list = purchaseOrderDetailLogic.findAllAndSort(searchParams, sort);

			OutputStream stream = response.getOutputStream();

			in = request.getServletContext().getResourceAsStream("/template/PurchaseOrderDetail.xls");
			if (null == in) {
				return Result.error(getText("surplusMaterial.failure"));
			}

			HSSFWorkbook wb = new HSSFWorkbook(in);
			HSSFSheet sheet = wb.getSheetAt(0);
			setContent(wb, sheet, list);

			response.setContentType("application/msexcel");
			response.setHeader("Content-disposition", "attachment; filename=PurchaseOrderDetail.xls");

			wb.write(stream);
			stream.close();
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
	 * 设置excel内容
	 */
	protected void setContent(HSSFWorkbook wb, HSSFSheet sheet, List<PurchaseOrderDetail> list) {
		int rowSeq = 1;
		for (PurchaseOrderDetail pod : list) {
			int cellSeq = 0;
			HSSFRow row = sheet.createRow(rowSeq++);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			HSSFCell cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() != null && pod.getPurchaseOrder().getPurchaseOrderTime() != null
					? df.format(pod.getPurchaseOrder().getPurchaseOrderTime().getTime())
					: "");

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() == null ? "" : pod.getPurchaseOrder().getPurchaseOrderNo());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getRowIds() == null ? "" : pod.getRowIds().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() == null ? "" : pod.getPurchaseOrder().getVendorErpCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() == null ? "" : pod.getPurchaseOrder().getVendorName());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getMaterialCode() == null ? "" : pod.getMaterialCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getMaterialName() == null ? "" : pod.getMaterialName());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getVendorQty() == null ? "" : pod.getVendorQty().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getUnitCode() == null ? "" : pod.getUnitCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getQtySend() == null ? "" : pod.getQtySend().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getQtyOnline() == null ? "" : pod.getQtyOnline().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getQtyArrive() == null ? "" : pod.getQtyArrive().toString());

			BigDecimal qtyWaitSend = BigDecimal.ZERO;
			BigDecimal vendorQty = pod.getVendorQty() == null ? BigDecimal.ZERO : pod.getVendorQty();
			BigDecimal qtyArrive = pod.getQtyArrive() == null ? BigDecimal.ZERO : pod.getQtyArrive();
			BigDecimal qtyOnline = pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline();
			BigDecimal qtyQuit = pod.getQtyQuit() == null ? BigDecimal.ZERO : pod.getQtyQuit();
			pod.getQtyQuit();
			pod.getQtySend();

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getQtyQuit() == null ? "" : pod.getQtyQuit().toString());

			// 可发货量
			qtyWaitSend = vendorQty.subtract(qtyArrive).subtract(qtyOnline).add(qtyQuit);
			cell = row.createCell(cellSeq++);
			cell.setCellValue(qtyWaitSend.doubleValue());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getBuyerPrice() == null ? "" : pod.getBuyerPrice().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getLineItemValAmt() == null ? "" : pod.getLineItemValAmt().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getBuyerTime() == null ? "" : df.format(pod.getBuyerTime().getTime()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getVendorTime() == null ? "" : df.format(pod.getVendorTime().getTime()));

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getOverDeliveryLimit() == null ? "" : pod.getOverDeliveryLimit().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getShortDeliveryLimit() == null ? "" : pod.getShortDeliveryLimit().toString());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getRemark());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() == null ? "" : pod.getPurchaseOrder().getPurchasingOrgCode());

			cell = row.createCell(cellSeq++);
			cell.setCellValue(pod.getPurchaseOrder() == null ? "" : pod.getPurchaseOrder().getPurchasingGroupCode());
		}
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(Integer closeFlag) {
		this.closeFlag = closeFlag;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
