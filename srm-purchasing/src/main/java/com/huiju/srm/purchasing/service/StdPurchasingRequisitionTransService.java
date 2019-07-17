package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTrans;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTransV;

/**
 * 采购申请转单Service接口
 * 
 * @author bairx
 */
public interface StdPurchasingRequisitionTransService extends JpaService<PurchasingRequisitionTrans, Long> {

	public List<PurchasingRequisitionTrans> getConfigList(String purchasingRequisitionColId, String materialCode, String userCode,
			String clientCode);

	/**
	 * 查找分配列表
	 * 
	 * @param searchParams
	 * @param materialCode
	 * @param userCode
	 * @param clientCode
	 * @return
	 */
	public Map<String, Object> getConfigList(Map<String, Object> searchParams, String userCode, String clientCode);

	/**
	 * 保存转单
	 * 
	 * @param transList 转单列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Integer saveTrans(List<Map> transList);

	/**
	 * 获取组织（公司、采购组织、供应商）
	 * 
	 * @return
	 */
	public Page<PurchasingRequisitionTransV> getGroupList(Page<PurchasingRequisitionTransV> page);

	/**
	 * 获取转单后明细
	 * 
	 * @param companyCode
	 * @param purchasingCode
	 * @param vendorCode
	 * @return
	 */
	public List<PurchasingRequisitionTrans> getChaList(String purchasingOrgCode, String vendorCode, String companyCode);
}
