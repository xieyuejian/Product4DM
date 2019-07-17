package com.huiju.srm.purchasing.dao;

import org.springframework.stereotype.Repository;

import com.huiju.module.data.jpa.dao.JpaDao;
import com.huiju.srm.purchasing.entity.PurchaseOrder;

/**
 * 采购订单 EaoBean
 * 
 * @author zhuang.jq
 */
@Repository
public interface PurchaseOrderDao extends JpaDao<PurchaseOrder, Long> {

	/**
	 * 根据采购组织、资源组、工厂物料视图、物料查询工厂
	 * 
	 * @param params
	 * @return
	 */
	// public String findPlantAll(Map<String, Object> params) {
	// StringBuffer sql = new StringBuffer(" SELECT
	// PLANT.PLANTCODE,PLANT.PLANTNAME,MMd.qualityCheck, ");
	// sql.append(
	// " MATERIAL.MATERIALID, plant.plantid FROM D_OMD_PLANTPURCHASEORG omd,
	// B_MMD_MATERIALPLANT mmd, B_MMD_MATERIAL material, D_OMD_PLANT plant ")
	// .append(" WHERE MATERIAL.MATERIALID = MMD.MATERIALID and PLANT.PLANTID =
	// MMD.PLANTID ")
	// .append(" and OMD.PURCHASINGORGCODE =
	// '").append(params.get("EQ_purchasingOrgCode")).append("' ")
	// .append(" and material.MATERIALCODE =
	// '").append(params.get("EQ_materialCode")).append("' ")
	// .append(" and omd.plantcode = mmd.plantcode"); // modified by
	// linshp

	// if (params.containsKey("IN_plantCode")) {
	// sql.append(" and PLANT.PLANTCODE in
	// (").append(params.get("IN_plantCode")).append(") ");
	// }

	// if (params.containsKey("EQ_plantCode")) {
	// sql.append(" and PLANT.PLANTCODE =
	// '").append(params.get("EQ_plantCode")).append("' ");
	// }

	// sql.append(
	// " GROUP BY MATERIAL.MATERIALID, plant.plantid, MATERIAL.MATERIALCODE,
	// PLANT.PLANTCODE, MMd.qualityCheck,PLANT.PLANTNAME ");

	// List<Object[]> list = executeSQLQuery(sql.toString());
	// List<Map<String, Object>> jsonList = new ArrayList<Map<String,
	// Object>>();
	// for (Object[] obj : list) {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("plantCode", obj[0].toString());
	// map.put("plantName", obj[1].toString());
	// map.put("qualityCheck", obj[2] == null ? "A" : obj[2].toString());
	// jsonList.add(map);
	// }

	// return DataUtils.toJson(jsonList);
	// }

}
