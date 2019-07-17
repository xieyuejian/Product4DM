package com.huiju.srm.purchasing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.protobuf.TextFormat.ParseException;
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
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.UnitClient;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.MaterialPlant;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.Unit;
import com.huiju.srm.purchasing.entity.PurchasingRequisition;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionDtl;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionState;
import com.huiju.srm.purchasing.service.PurchasingRequisitionCollectionService;
import com.huiju.srm.purchasing.service.PurchasingRequisitionService;

/**
 * 采购申请controller
 * 
 * @author bairx
 *
 */
public class StdPurchasingRequisitionController extends CloudController {

	@Autowired
	protected PurchasingRequisitionService purchasingRequisitionServiceImpl;
	@Autowired
	protected MaterialPlantClient materialPlantClient;
	@Autowired
	protected UnitClient unitClient;
	@Autowired
	protected PlantClient plantClient;
	@Autowired
	protected MaterialClient materialClient;
	@Autowired
	protected PurchasingRequisitionCollectionService purchasingRequisitionCollectionServiceImpl;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupClient;
	@Autowired(required = false)
	protected BillSetServiceClient billSetServiceImpl;

	protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 获取列表 / 查询数据
	 * 
	 * @return
	 */
	@PostMapping("/list")
	public Page<PurchasingRequisition> list() {
		Page<PurchasingRequisition> page = buildPage(PurchasingRequisition.class);
		Map<String, Object> searchParams = buildParams();
		String initStates = request.getParameter("initStates");
		String billFlag = request.getParameter("billFlag");
		// 待处理，待审核初始化状态过滤
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.replaceAll("\\s*", "").split(",");
			PurchasingRequisitionState[] statusArray = new PurchasingRequisitionState[values.length];
			for (int i = 0; i < values.length; i++) {
				PurchasingRequisitionState status = PurchasingRequisitionState.valueOf(values[i].trim());
				statusArray[i] = status;
			}
			searchParams.put("IN_status", statusArray);
		}
		if (null != billFlag && billFlag.equals("undeal")) {
			// 登录用户创建且审核不过，登录用户为当前审核节点的审核角色
			List<Long> idList = purchasingRequisitionServiceImpl.findIdByStatus(getUserId());
			searchParams.put("IN_purchasingRequisitionId", idList);
		}
		// 过滤资源用户组

		searchParams.putAll(userAuthGroupClient
				.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), PurchasingRequisition.class)));
		page = purchasingRequisitionServiceImpl.findAllWithoutAssociation(page, searchParams);
		return page;
	}

	/**
	 * 返回编辑表单数据对象
	 * 
	 * @return
	 */
	@PostMapping("/get")
	public String get(Long id) {
		PurchasingRequisition model = purchasingRequisitionServiceImpl.findById(id);
		if (model == null) {
			return "信息不存在";
		}
		return renderJson(model, new String[] { " purchasingRequisitionDtls" });
	}

	/**
	 * 保存表单
	 * 
	 * @return
	 */
	@PostMapping("/save")
	public String save(@RequestBody JsonParam<PurchasingRequisition> jsonParam) {
		try {
			String submitFlag = jsonParam.getSubmitFlag();
			PurchasingRequisition model = jsonParam.getModel();
			Long userId = getUserId();
			String userName = getUserName();
			model.setPurchasingRequisitionNo(billSetServiceImpl.createNextRunningNum(SrmConstants.BILLTYPE_PR));
			model.setClientCode(getClientCode());
			model.setCreateUserId(getUserId());
			model.setCreateUserName(getUserName());
			model.setApplicantCode(getUserCode());
			model.setApplicantName(getUserName());
			model.setApplicantTime(Calendar.getInstance());
			model.setCreateTime(Calendar.getInstance());
			model.setModifyUserId(getUserId());
			model.setModifyUserName(getUserName());
			model.setModifyTime(Calendar.getInstance());
			this.setPurchasingRequisitionOneToMaryValue(model);
			if ("save".equalsIgnoreCase(submitFlag)) {

				model.setStatus(PurchasingRequisitionState.NEW);
				model = purchasingRequisitionServiceImpl.save(model);
				purchasingRequisitionServiceImpl.addLog(getUserId(), getUserName(), model.getPurchasingRequisitionId(), "采购申请提交",
						SrmConstants.PERFORM_SAVE, model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);
			} else if ("audit".equalsIgnoreCase(submitFlag)) {
				model.setStatus(PurchasingRequisitionState.NEW);
				model = purchasingRequisitionServiceImpl.save(model);
				purchasingRequisitionServiceImpl.dealStatus(userId, userName, model.getPurchasingRequisitionId(),
						PurchasingRequisitionState.valueOf("TOCONFIRM"), "", false);
				purchasingRequisitionServiceImpl.addLog(getUserId(), getUserName(), model.getPurchasingRequisitionId(), "采购申请提交",
						SrmConstants.PERFORM_AUDIT, model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);
			}
			return dealJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false);
		}
	}

	/**
	 * 修改
	 * 
	 * @return String
	 */
	@PostMapping("/update")
	public Result update(@RequestBody JsonParam<PurchasingRequisition> jsonParam) {
		PurchasingRequisition model = jsonParam.getModel();
		String submitFlag = jsonParam.getSubmitFlag();
		Long userId = getUserId();
		String userName = getUserName();
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		PurchasingRequisition pd = purchasingRequisitionServiceImpl.findById(model.getPurchasingRequisitionId());
		if (pd == null) {
			return Result.error(getText("message.notexisted"));
		}
		this.setPurchasingRequisitionOneToMaryValue(model);
		model.setStatus(pd.getStatus());
		if ("save".equalsIgnoreCase(submitFlag)) {
			model = purchasingRequisitionServiceImpl.save(model);
			purchasingRequisitionServiceImpl.addLog(getUserId(), getUserName(), model.getPurchasingRequisitionId(), "采购申请修改",
					SrmConstants.PERFORM_EDIT, model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);
		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			model = purchasingRequisitionServiceImpl.save(model);
			purchasingRequisitionServiceImpl.dealStatus(userId, userName, model.getPurchasingRequisitionId(),
					PurchasingRequisitionState.valueOf("TOCONFIRM"), "", false);
			purchasingRequisitionServiceImpl.addLog(getUserId(), getUserName(), model.getPurchasingRequisitionId(), "采购申请提交",
					SrmConstants.PERFORM_AUDIT, model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);
		}

		return Result.success(true);

	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	@PostMapping("/delete")
	public Result delete(@RequestParam List<Long> ids, @RequestParam String message) {
		purchasingRequisitionServiceImpl.removeByIds(ids, getUserId(), getUserName(), message);
		return Result.success(true);
	}

	/**
	 * 关闭
	 * 
	 * @return String
	 */
	@PostMapping("/close")
	public Result close(Long id) {
		PurchasingRequisition model = purchasingRequisitionServiceImpl.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}

		purchasingRequisitionServiceImpl.close(id, getUserId(), getUserName());

		return Result.success(true);
	}

	/**
	 * 取消
	 * 
	 * @return
	 */
	@PostMapping("/cancel")
	public Result cancel(Long id) {
		PurchasingRequisition model = purchasingRequisitionServiceImpl.findById(id);
		if (model == null) {
			return Result.error("message.notexisted");
		}
		// 只有状态为发布且未被询报价、招投标、采购订单模块引用的采购申请才允许被取消
		if (PurchasingRequisitionState.TOPASS.equals(model.getStatus()) && !"1".equals(model.getIsUsed())) {
			purchasingRequisitionServiceImpl.cancel(id, getUserId(), getUserName());
			return Result.success(true);
		} else {
			return Result.error(getText("message.cancel"));
		}
	}

	/**
	 * 下载模板
	 * 
	 * @return
	 */
	@PostMapping("/download")
	public String downLoad() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new ClassPathResource("template/Requistion.xls").getInputStream();
			out = response.getOutputStream();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment;filename=采购申请明细导入模板.xls");
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
	 * 导入数据
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	@PostMapping("/importexcel")
	public Result importExcel(HttpServletRequest request) {
		Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
		String key = files.keySet().iterator().next();
		File excelFile = files.get(key);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(excelFile);
			HSSFSheet sheet = new HSSFWorkbook(inputStream).getSheetAt(0);
			if (0 == sheet.getLastRowNum()) {
				return Result.error(getText("message.theDateIsNull"));
			}

			// 查询条件
			Map<String, Object> searchParams = new HashMap<String, Object>();

			List<HSSFCell> hfList = new ArrayList<HSSFCell>();
			Map<Integer, List<String>> mapList = new HashMap<Integer, List<String>>();
			// 验证结果
			Map<Integer, String> mapSb = new HashMap<Integer, String>();
			// 需要验证必填项
			Map<Integer, String> validMap = new HashMap<Integer, String>();
			if (sheet.getRow(0) != null) {
				HSSFRow row = sheet.getRow(0);
				for (int i = 0; i < 6; i++) {
					HSSFCell hf = row.getCell(i);
					if (hf.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						String Str = hf.getStringCellValue().trim();
						if (Str != null && Str.contains("*")) {
							validMap.put(i, Str.substring(0, Str.indexOf("*")));
						}
					}
				}
			}

			Map<String, MaterialPlant> matPlantMaps = new HashMap<String, MaterialPlant>();

			for (int numRows = 1; numRows <= sheet.getLastRowNum(); numRows++) {
				List<String> sList = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				hfList.clear();
				if (null != sheet.getRow(numRows)) {
					HSSFRow row = sheet.getRow(numRows);
					for (int i = 0; i < 6; i++) {
						hfList.add(row.getCell(i));
						// sList.add(String.valueOf(row.getCell(i) == null ? ""
						// : row.getCell(i)));
					}
					// 导入验证
					String companyCode = request.getParameter("companyCode");

					importValid(searchParams, hfList, numRows, sb, sList, validMap, companyCode, matPlantMaps);
					mapList.put(numRows, sList);
					if (sb.length() > 1) {
						mapSb.put(numRows, sb.toString());
					}

				}
			}
			StringBuffer sbFinal = new StringBuffer();
			if (mapSb != null && mapSb.size() > 0) {
				for (Integer s : mapSb.keySet()) {
					sbFinal.append(mapSb.get(s));
				}
				return Result.error(sbFinal.toString());
			} else {
				if (mapList != null && mapList.size() > 0) {
					List<PurchasingRequisitionDtl> modelList = new ArrayList<PurchasingRequisitionDtl>();
					importData(searchParams, modelList, mapList, matPlantMaps);
					return Result.success(DataUtils.toJson(modelList));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return Result.error("导入数据不正确");
		} finally {
			if (null != inputStream) {
				try {
					excelFile = null;
					inputStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return Result.success();
	}

	/**
	 * 查询采购申请明细
	 * 
	 * @return
	 */
	@PostMapping("/findpurchasingrequisitiondtlall")
	public String findPurchasingRequisitionDtlAll(String sort, String dir) {
		Map<String, Object> searchParams = buildParams();
		List<PurchasingRequisitionDtl> list = purchasingRequisitionServiceImpl.findPurchasingRequisitionDtlAll(searchParams,
				sort + "," + dir);
		return renderJson(DataUtils.toJson(list, "purchasingRequisition"));
	}

	/**
	 * 设置招标单一对多的值
	 */
	// @Mapping("/setPurchasingRequisitionOneToMaryValue")
	protected void setPurchasingRequisitionOneToMaryValue(PurchasingRequisition model) {
		for (PurchasingRequisitionDtl item : model.getPurchasingRequisitionDtls()) {
			item.setPurchasingRequisition(model);
		}
	}

	/**
	 * 获取审核流程事件
	 *
	 * @return
	 */
	@PostMapping("/getevents")
	public String getEvents(Long id) {
		Long s_userId = getUserId();
		getUserPermissions();
		String[] events4Authorities = { // 事件对应的权限
				"purchasingrequisition_toconfirm", "purchasingrequisition_pass", "purchasingrequisition_nopass",
				"purchasingrequisition_undeal_topass", "purchasingrequisition_undeal_tonopass", "purchasingrequisition_undeal_toconfirm" };
		StringBuffer buf = new StringBuffer();
		for (String auth : events4Authorities) {
			// if (s_authorities.indexOf("|" + auth + "|") > -1) {
			if (buf.length() > 0)
				buf.append(",");
			buf.append("'" + auth + "'");
			// }
		}
		String eventAuth = buf.toString();

		List<String> events = purchasingRequisitionServiceImpl.getPurchasingRequisitionEvents(s_userId, id);
		buf = new StringBuffer("[");
		for (String event : events) {
			if (event != null) {
				if (eventAuth.indexOf("purchasingrequisition_" + event.toLowerCase() + "'") > -1
						|| eventAuth.indexOf("'purchasingrequisition_undeal_" + event.toLowerCase() + "'") > -1
						|| event.indexOf("#") > -1) {
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
		try {
			Long userId = getUserId();
			String userName = getUserName();
			purchasingRequisitionServiceImpl.dealStatus(userId, userName, Long.valueOf(id), PurchasingRequisitionState.valueOf(billState),
					message);
			return dealJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false);
		}
	}

	/**
	 * 导入校验方法
	 * 
	 * @param searchParams 创建的查询载体
	 * @param hfList 读取的单元格
	 * @param numRows 第几行
	 * @param sdf 日期转换
	 * @param sb 校验结果
	 * @param sList 一行的数据集
	 * @param validMap 必填字段
	 * @param matPlantMaps
	 */
	@SuppressWarnings("static-access")
	@PostMapping("/importvalid")
	protected void importValid(Map<String, Object> searchParams, List<HSSFCell> hfList, int numRows, StringBuilder sb, List<String> sList,
			Map<Integer, String> validMap, String companyCode, Map<String, MaterialPlant> matPlantMaps) {
		int index = 0;
		for (HSSFCell hc : hfList) {
			String Str = null;
			if (hc != null) {
				if (hc.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					Str = hc.getStringCellValue().trim();
					if (Str != null && Str.indexOf(".") != -1) {
						Str = Str.substring(0, Str.indexOf("."));
					}
				} else if (hc.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					if (HSSFDateUtil.isCellDateFormatted(hc)) {// 判断是否是日期类型
						Date d = hc.getDateCellValue();
						if (d != null) {
							Date now = new Date();
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(now);
							calendar.add(calendar.DATE, -1);
							now = calendar.getTime();
							if (d.before(now)) {
								sb.append("第" + numRows + "行：需求日期不能小于当前日期!\n");
							}
							Str = sdf.format(d);
						}
					} else {
						NumberFormat nf = NumberFormat.getInstance();
						nf.setGroupingUsed(false);// true时的格式：1,234,567,890
						Double acno = hc.getNumericCellValue();
						if (acno != null) {
							Str = String.valueOf(acno);
						}
					}
				}
			}
			if (validMap.get(index) != null) {
				if (hc == null || Str == null || StringUtils.isBlank(Str)) {
					sb.append("第" + numRows + "行：" + validMap.get(index) + "不能为空!\n");
				}
			}
			sList.add(Str);
			index++;
		}

		// 验证
		String plantCode = sList.get(3);
		searchParams.clear();
		searchParams.put("EQ_plantCode", plantCode);

		Plant plant = plantClient.findOne(searchParams);

		if (StringUtils.isBlank(sList.get(0))) {
			// 若导入物料编码为空，校验工厂是否属于该公司，不存在给出提示。
			if (plant != null) {
				if (plant.getCompanyCode() == null || StringUtils.isBlank(plant.getCompanyCode())
						|| !companyCode.equals(plant.getCompanyCode())) {
					sb.append("第" + numRows + "行：" + plant.getPlantCode() + "工厂不属于" + companyCode + "公司!\n");
				}
			} else {
				sb.append("第" + numRows + "行：" + plantCode + "工厂不属于" + companyCode + "公司!\n");
			}

		} else {
			searchParams.clear();
			searchParams.put("EQ_materialCode", sList.get(0));
			FeignParam<Material> materialParam = new FeignParam<Material>();
			materialParam.setParams(searchParams);
			List<Material> materialList = materialClient.findAll(materialParam);
			if (materialList.size() < 1) {
				sb.append("第" + numRows + "行物料编码【" + sList.get(0) + "】不存在于系统中!\n");
			}
			// 物料编码在系统中是否存在，不存在给出对应提示；
			// 若导入物料编码不为空，校验工厂是否属于该公司（主单）且存在与物料、工厂关
			// 系视图中，不存在给出提示；
			if (plant != null) {
				if (plant.getCompanyCode() == null || StringUtils.isBlank(plant.getCompanyCode())
						|| !companyCode.equals(plant.getCompanyCode())) {
					sb.append("第" + numRows + "行：" + plant.getPlantCode() + "工厂不属于" + companyCode + "公司!\n");
				}
			} else {
				sb.append("第" + numRows + "行：" + plantCode + "工厂不属于" + companyCode + "公司!\n");
			}
			searchParams.put("EQ_plantCode", plantCode);
			FeignParam<MaterialPlant> meterialParam = new FeignParam<MaterialPlant>();
			searchParams.put("EQ_plantCode", "2100");
			searchParams.put("EQ_materialCode", "100023");
			meterialParam.setParams(searchParams);
			List<MaterialPlant> materialPlant = materialPlantClient.findAll(meterialParam);
			if (materialPlant.size() < 1) {
				sb.append("第" + numRows + "行物料编码【" + sList.get(0) + "】不存在于物料、工厂关系视图中!\n");
			} else {
				matPlantMaps.put(sList.get(0) + "_" + plantCode, materialPlant.get(0));
			}
		}
	}

	/**
	 * 导入数据处理
	 * 
	 * @param searchParams 查询载体
	 * @param mmpaList 导入的价格申请单据集
	 * @param mapList 导入的数据集
	 * @param matPlantMaps
	 * @param sdf 日期格式化
	 * @throws ParseException
	 */

	protected void importData(Map<String, Object> searchParams, List<PurchasingRequisitionDtl> modelList,
			Map<Integer, List<String>> mapList, Map<String, MaterialPlant> matPlantMaps) throws ParseException {
		for (Integer index : mapList.keySet()) {
			PurchasingRequisitionDtl po = new PurchasingRequisitionDtl();
			List<String> dataList = mapList.get(index);
			// for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(0) == null || StringUtils.isBlank(dataList.get(0))) {
				po.setSource("2");// 临时
			} else {
				po.setSource("1");// 正式
			}
			// 物料编码
			po.setMaterialCode(dataList.get(0));
			// 物料名称
			if (StringUtils.isBlank(dataList.get(1))) {
				searchParams.clear();
				searchParams.put("EQ_materialCode", dataList.get(0));
				FeignParam<Material> materialParam = new FeignParam<Material>();
				materialParam.setParams(searchParams);
				List<Material> materialList = materialClient.findAll(materialParam);
				if (materialList != null && materialList.size() > 0) {
					po.setMaterialName(materialList.get(0).getMaterialName());
				}
			} else {
				po.setMaterialName(dataList.get(1));
			}

			// 单位
			po.setUnitCode(dataList.get(2));
			searchParams.clear();
			searchParams.put("EQ_unitCode", dataList.get(2));
			Unit unit = unitClient.findOne(searchParams);
			if (unit != null) {
				po.setUnitName(unit.getUnitName());// 单位名称
			}
			// 工厂
			searchParams.clear();
			searchParams.put("EQ_plantCode", dataList.get(3));
			Plant plant = plantClient.findOne(searchParams);
			po.setPlantCode(dataList.get(3));
			if (plant != null) {
				po.setPlantName(plant.getPlantName());
			}
			String key = po.getMaterialCode() + "_" + po.getPlantCode();
			if (matPlantMaps.containsKey(key)) {
				MaterialPlant materialPlant = matPlantMaps.get(key);
				po.setPurchasingGroupCode(materialPlant.getPurchasingGroupCode());
				po.setPurchasingGroupName(materialPlant.getPurchasingGroupName());
			}

//			po.setDemandDate(CommonUtil.strToDate(dataList.get(4)));
			// 需求量
			po.setQuantityDemanded(new BigDecimal(dataList.get(5)));
			modelList.add(po);
		}
	}

	/**
	 * 根据物料找工厂
	 * 
	 * @return
	 */
	@RequestMapping("/findplantbymaterial")
	public List<Plant> findPlantByMaterial() {
		Map<String, Object> searchParams = buildParams();
		FeignParam<MaterialPlant> materialPlantParam = new FeignParam<>();
		materialPlantParam.setParams(searchParams);
		List<MaterialPlant> list = materialPlantClient.findAll(materialPlantParam);
		List<Plant> plantList = new ArrayList<Plant>();
		for (MaterialPlant material : list) {
			plantList.add(material.getPlant());
		}
		return plantList;
	}

	/**
	 * 根据物料、工厂找采购组
	 * 
	 * @return
	 */
	@PostMapping("/findpurchasinggroup")
	public List<MaterialPlant> findPurchasingGroup(String materialCode, String plantCode) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_materialCode", materialCode);
		searchParams.put("EQ_plantCode", plantCode);
		FeignParam<MaterialPlant> materialPlantParam = new FeignParam<>();
		materialPlantParam.setParams(searchParams);
		List<MaterialPlant> list = materialPlantClient.findAll(materialPlantParam);
		return list;
	}

	/**
	 *
	 */
	/**
	 * 撤销审核
	 * 
	 * @param id 单据id
	 * @return
	 */
	@RequestMapping("/revokeaudit")
	public Result revokeAudit(Long id) {
		String result = purchasingRequisitionServiceImpl.revokeAudit(id, getUserId(), getUserName());
		return Result.success(result);
	}

}
