package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.huiju.bpm.support.service.BpmSupportService;
import com.huiju.module.data.common.JobResultData;
import com.huiju.module.data.common.Page;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;

/**
 * 采购订单 远程接口
 * 
 * @author CWQ
 */
public interface StdPurchaseOrderService extends BpmSupportService<PurchaseOrder, Long> {

	/**
	 * APP分页
	 * 
	 * @param searchParams
	 * @param specialParams
	 * @return
	 */
	public String page4App(Map<String, Object> searchParams, Map<String, Object> specialParams);

	/**
	 * 获取供应商汇率 --APP
	 * 
	 * @param vendorCode 供应商编码
	 * @param purchasingOrgCode 采购组织
	 * @param origCurrencyCode 供应商货币
	 * @return 汇率
	 */
	public String findExchangeRate(String vendorCode, String purchasingOrgCode, String origCurrencyCode);

	/**
	 * 获取APP 审核权限
	 * 
	 * @param s_userId 用户ID
	 * @param s_roleType 角色类型
	 * @param id 主键
	 * @return 权限
	 */
	public List<String> getPurchaseOrderEvents(Long userId, String roleType, Long id);

	/**
	 * 批量导入数据
	 * 
	 * @param wb excel工作簿对象
	 * @param webPrams 前台传入的web条件
	 * @param userId 当前用户id
	 * @param userName 当前用户名称
	 * @param clientCode 客户端编码
	 * @return 返回校验消息
	 */
	public String batchImportExcel(List<PurchaseOrder> orders, List<PurchaseOrderDetail> orderDetials,
			Map<String, PurchaseOrder> orderNoPOrgMap, Map<String, Object> webPrams, Long userId, String userName, String clientCode);

	/**
	 * 导入明细数据
	 * 
	 * @param wb excel工作簿对象
	 * @param userAuthMap 用户资源组条件
	 * @param webPrams 前台传入的web条件
	 * @return 返回校验消息
	 */
	public Map<Boolean, Object> importExcel(List<MaterialMasterPriceOrderDtlView> dtlVos, Map<String, Object> userAuthMap,
			Map<String, Object> webPrams, Integer srmRowIds);

	/**
	 * 撤销审批
	 * 
	 * @param id 单据ID
	 * @param userId 当前用户ID
	 * @param userName 当前用户名称
	 * @return 返回校验消息
	 */
	public String revocationCheck(Long id, Long userId, String userName);

	/**
	 * 校验货源清单
	 * 
	 * @param detail 订单明细
	 * @param model 订单主单
	 * @return true or false
	 * @throws Exception
	 */
	public Map<String, Object> validateSourceList(PurchaseOrder model);

	/**
	 * 校验采购申请可转单量
	 * 
	 * @param model 订单主单
	 * @throws Exception
	 */
	public Map<String, Object> validateApply(PurchaseOrder model);

	/**
	 * 设置价格主数据库存地点
	 * 
	 * @param viewList 价格主数据集合
	 */
	public void setMMStockLocation(List<MaterialMasterPriceOrderDtlView> viewList);

	/**
	 * 设置价格主数据库存地点
	 * 
	 * @param viewList 价格主数据集合
	 */
	public List<MaterialMasterPriceOrderDtlView> setMMStockLocation2(List<MaterialMasterPriceOrderDtlView> viewList);

	/**
	 * APP创建或者修改订单
	 * 
	 * @param orderJson 订单json
	 * @param orderJson 是否提交订单，true是，false否
	 * @param userId 当前操作人id
	 * @param userName 当前操做人名称
	 * @return 返回校验信息 key = true or false value = 消息
	 */
	public Map<Boolean, String> saveOrUpdateOrder(String orderJson, Boolean submitFlag, Long userId, String userName, String platForm);

	/**
	 * 供应商变更订单明细
	 * 
	 * @param map 存储id和变更的时间
	 * @param userId 当前用户ID
	 * @param userName 当前用户名称
	 * @param message 处理意见
	 * @return 返回成功失败
	 */
	Boolean updataToVariation(Map<Long, String> map, Long userId, String userName, String message, String platForm);

	/**
	 * 获取excel导出参数
	 * 
	 * @return 返回excel导出参数
	 */
	public Map<String, Object> getExportParams(Map<String, Object> params);

	/**
	 * 获取采购订单管控点
	 * 
	 * @param entity 实体对象
	 * @param code 管控点Code
	 * @return 1是，0否
	 * @throws Exception
	 */
	public String getPurchaseOrderControl(Object entity, String code);

	/**
	 * 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格 --APP
	 * 
	 * @param params 查询参数
	 * @return 阶梯价格对象
	 */
	public MaterialLadderPriceDtl findMaterialLadderPrice(Map<String, Object> params);

	/**
	 * 根据采购组织、资源组、工厂物料视图、物料查询工厂
	 * 
	 * @param params 查询参数
	 * @return 工厂JSON对象
	 */
	public String findPlantAll(Map<String, Object> params);

	/**
	 * 查找订单明细
	 * 
	 * @param purchasingOrderId 采购订单ID
	 * @return 订单明细
	 */
	public List<PurchaseOrderDetail> findPurchaseOrderDetails(Long purchasingOrderId);

	/**
	 * 关闭订单明细
	 * 
	 * @param purchaseOrderDetailId 细单ID
	 * @param operate 关闭、取消关闭
	 * @param closeFlag 1、0
	 * @return 订单的状态
	 * @throws Exception
	 */
	public PurchaseOrderState closePurchaseOrderDetail(Long purchaseOrderDetailId, String operate, int closeFlag, Long userId,
			String userName);

	/**
	 * 获取操作权限
	 * 
	 * @param userId 当前用户ID
	 * @param roleType 角色字符串
	 * @param id 单据ID
	 * @param authorities 当前用户所有权限
	 * @param btnStateFlag 标识是否右键操作，1是，0否
	 * @return 返回权限字符串
	 */
	public String getPurchaseOrderEvents(Long userId, String roleType, Long id, List<String> authorities, String btnStateFlag);

	/**
	 * 订单状态变更及审核
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param purchaseOrderId 主单ID
	 * @param status 要变更的状态或审核结果
	 * @param message 其他需要保存的消息，如审核意见等
	 * @param ids 接收通知的用户ID
	 * @return 变更后的订单
	 * @throws Exception
	 */
	public PurchaseOrder dealPurchaseOrder(Long userId, String userName, Long purchaseOrderId, String status, String message,
			String platForm, String... ids);

	/**
	 * 供应商变更订单
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @param order 要变更的订单信息
	 * @return 变更后的订单实体
	 */
	public PurchaseOrder toHold(Long userId, String userName, PurchaseOrder order);

	/**
	 * 获取采购待处理的数据id
	 * 
	 * @param userId 用户ID
	 * @param isBuyer 是否是采购角色，true or false
	 * @return
	 */
	public String findIdByStatus(Long userId, Boolean isBuyer);

	/**
	 * 同步到ERP
	 * 
	 * @param purchaseOrderId 采购订单ID
	 * @param 交货明细行，仅供排程使用，其他情况传null
	 * @return 同步结果
	 */
	public Boolean doSync(Long purchaseOrderId, List<SendScheduleDetail> list, String status);

	/**
	 * 更新采购订单
	 * 
	 * @param model 要更新的采购订单
	 * @param 是否提交订单，"audit" 是，其他字符串否
	 * @return 更新后的订单
	 * @throws Exception
	 */
	public PurchaseOrder mergeLogic(PurchaseOrder model, String submitFlag, Long userId, String userName, String platForm);

	/**
	 * 新建订单
	 * 
	 * @param model 要更新的采购订单
	 * @param 是否提交订单，"audit" 是，其他字符串否
	 * @return 新建后的订单
	 * @throws Exception
	 */
	public PurchaseOrder persistPo(PurchaseOrder model, String submitFlag, String platForm);

	/**
	 * 设置excel 下拉
	 * 
	 * @param wb excel对象
	 */
	void setExcelCombox(HSSFWorkbook wb);

	/**
	 * 删除订单
	 * 
	 * @param ids 要删除的ID
	 * @param 当前用户ID
	 * @param 当前用户名称
	 */
	public void removePo(List<Long> ids, Long userId, String userName, String message);

	/**
	 * 采购订单定时器方法
	 */
	public List<JobResultData> purchaseOrderJobMethod();

	/**
	 * 发送取消的消息
	 * 
	 * @param entity 实体
	 * @param userId 操作人
	 */
	public void sendCancelMessage(PurchaseOrder entity, Long userId);

	/**
	 * 增加日志
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);

	/**
	 * 订单新建 添加已有物料(生效的价格主数据)
	 * 
	 * @param page MaterialMasterPriceDtlView 对象
	 * @param searchParams (传EQ_vendorCode 供应商编码;EQ_purchasingOrgCode
	 *            采购组织编码;EQ_currencyCode 主单选完供应商带出的货币编码)
	 * @return 物料编码、名称、新建记录类别、工厂编码、未税价格
	 */
	public String findMaterialMasterPricePage(Page<MaterialMasterPriceOrderDtlView> page, Map<String, Object> searchParams);

	/**
	 * 根据采购组织+公司查工厂
	 * 
	 * @param searchParams (根据采购组织过滤-->传EQ_purchasingOrgCode
	 *            采购组织编码;EQ_companyCode 公司编码)
	 * @return
	 */
	public String findPlantByPurchasingOrgCompany(String clientCode, String userCode, Map<String, Object> searchParams);

	/**
	 * 获取价格主数据json
	 * 
	 * @param searchParams
	 * @return
	 */
	public String findMaterialMasterPriceJson(Map<String, Object> searchParams);

	/**
	 * 获取采购订单json
	 * 
	 * @param searchParams
	 * @return
	 */
	public String getOrderJson(Map<String, Object> searchParams);

}
