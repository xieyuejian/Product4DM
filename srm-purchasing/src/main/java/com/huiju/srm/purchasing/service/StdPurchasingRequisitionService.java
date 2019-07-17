package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.bpm.support.service.BpmSupportService;
//import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.purchasing.entity.PurchasingRequisition;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionDtl;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionState;

/**
 * 采购申请Service接口
 * 
 * @author bairx
 */
public interface StdPurchasingRequisitionService extends BpmSupportService<PurchasingRequisition, Long> {
	/**
	 * 查询明细
	 * 
	 * @param searchParams 查询条件
	 * @param orderStr 排序字段和排序方向（"time,desc"）
	 * @return 符合添加的明细集合
	 */
	public List<PurchasingRequisitionDtl> findPurchasingRequisitionDtlAll(Map<String, Object> searchParams, String orderStr);

	/*
	*//**
		 * 右键操作 --APP
		 * 
		 * @param userId 审核人id
		 * @param id 审核单据id
		 * @return 事件集合
		 */
	public List<String> getPurchasingRequisitionEvents(Long userId, Long id);

	/**
	 * 处理流程 --APP
	 * 
	 * @param userId 登录用户Id
	 * @param userName 登录用户名称
	 * @param id 单据Id
	 * @param status 单据状态
	 * @param message 审核意见
	 * @return 采购申请实体
	 */
	public PurchasingRequisition dealStatus(Long userId, String userName, Long materialFrozenPriceId, PurchasingRequisitionState status,
			String message);

	/**
	 * 批量删除
	 * 
	 * @param ids 单据id集合
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param message 原因
	 */
	public void removeByIds(List<Long> ids, Long userId, String userName, String message);

	/**
	 * 关闭
	 * 
	 * @param id 单据id
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	public void close(Long id, Long userId, String userName);

	/**
	 * 取消
	 * 
	 * @param id 单据id
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	public void cancel(Long id, Long userId, String userName);

	/**
	 * 获取采购待处理的数据id --APP
	 * 
	 * @return 创建者为登录用户且审核不过的单据ID
	 */
	public List<Long> findIdByStatus(Long userId);

	/**
	 * 增加操作日志的方法
	 * 
	 * @return
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);

	// ******************************** APP部分
	// ***********************************//*
	/**
	 * 获取公司JSON --APP
	 * 
	 * @param 数据权限过滤条件
	 * @param 排除的表
	 * @return 公司JSON
	 */
	public String findCompanyJson(Map<String, Object> searchMap, String[] excludes);

	/**
	 * 撤销审核
	 */
	public String revokeAudit(Long id, Long userId, String userName);

	public PurchasingRequisition dealStatus(Long userId, String userName, Long materialFrozenPriceId, PurchasingRequisitionState status,
			String message, Boolean isAdd);
}
