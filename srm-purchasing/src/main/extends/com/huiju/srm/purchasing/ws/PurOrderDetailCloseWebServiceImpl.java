package com.huiju.srm.purchasing.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.srm.commons.utils.IdUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.commons.ws.utils.MessageContent;
import com.huiju.srm.commons.ws.utils.ServiceSupport;
import com.huiju.srm.commons.ws.utils.ValidVisitor;
import com.huiju.srm.purchasing.dto.PurOrderDetailCloseDto;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.handler.SoapMessageMgr;
import com.huiju.srm.ws.service.WsLogService;

/**
 * 订单关闭WebService
 * 
 * @author WANGLQ
 */
@Component // 由Spring管理
@WebService(serviceName = "purOrderDetailCloseWebService", targetNamespace = "http://www.huiju.com/purOrderDetailClose", endpointInterface = "com.huiju.srm.purchasing.ws.PurOrderDetailCloseWebService")
public class PurOrderDetailCloseWebServiceImpl extends ServiceSupport implements PurOrderDetailCloseWebService {

	protected static final Logger log = LoggerFactory.getLogger(PurOrderDetailCloseWebServiceImpl.class);

	@Autowired
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired
	protected WsLogService wsLogLogic;
	// 请求报文本地线程缓存
	private SoapMessageMgr soapMessageMgr;

	@Override
	public Message updatePurOrderCloseState(List<PurOrderDetailCloseDto> purOrderDetails) {
		soapMessageMgr = SoapMessageMgr.getCurrentInstance();
		// 请求参数
		String requestContent = StringUtils.isBlank(soapMessageMgr.getSoapMessageStr()) ? DataUtils.toJson(purOrderDetails)
				: soapMessageMgr.getSoapMessageStr();
		Calendar.getInstance();
		// 接口编码
		String interfaceCode = SrmConstants.SRM_PURORDERDETAILCLOSESERVICE_CODE;
		IdUtils.genNextId();
		// 放置执行内容状态
		MessageContent content = createContent();
		try {
			// 验证订单明细
			for (PurOrderDetailCloseDto detail : purOrderDetails) {
				if (!isValid(detail, content, purchaseOrderDetailVisitor)) {
					WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
					wsLogLogic.addErrorLog(wslog, error(content).toString());
					return error(content);
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, PurOrderDetailCloseDto> vomap = new HashMap<String, PurOrderDetailCloseDto>();
			String ordernos = "";
			for (PurOrderDetailCloseDto vo : purOrderDetails) {
				vomap.put(vo.getErpPurchaseOrderNo().trim() + "#" + vo.getRowIds().toString().trim(), vo);
				if (!ordernos.contains(vo.getErpPurchaseOrderNo())) {
					if (StringUtils.isBlank(ordernos)) {
						ordernos += vo.getErpPurchaseOrderNo();
					} else {
						ordernos += "," + vo.getErpPurchaseOrderNo();
					}
				}
			}
			map.put("IN_erpPurchaseOrderNo", ordernos);
			List<PurchaseOrder> list = purchaseOrderLogic.findAll(map);
			map.clear();
			map.put("IN_purchaseOrder_erpPurchaseOrderNo", ordernos);
			List<PurchaseOrderDetail> pods = purchaseOrderDetailLogic.findAll(map);

			Map<String, List<PurchaseOrderDetail>> podMaps = validDetails(pods, vomap);
			Integer size = 0;
			Integer deleteflag = 0;
			Integer nodelete = 0;
			Integer closeflag = 0;
			for (PurchaseOrder order : list) {
				if (pods != null) {
					size = podMaps.get(order.getErpPurchaseOrderNo().trim()).size();
					deleteflag = 0;
					nodelete = 0;
					closeflag = 0;
					for (PurchaseOrderDetail detail : podMaps.get(order.getErpPurchaseOrderNo().trim())) {
						if (vomap.containsKey(order.getErpPurchaseOrderNo().trim() + "#" + detail.getRowIds().toString().trim())) {
							PurOrderDetailCloseDto vo = vomap
									.get(order.getErpPurchaseOrderNo().trim() + "#" + detail.getRowIds().toString().trim());
							if ("Y".equals(vo.getCloseFlag())) {// 关闭
								detail.setCloseFlag(1);
							} else {
								detail.setCloseFlag(0);
							}
							purchaseOrderDetailLogic.save(detail);
						}
						if (detail.getDeleteFlag() != null && detail.getDeleteFlag().equals(1)) {
							// 删除标识为是
							deleteflag++;
						} else {
							nodelete++;
							if (detail.getCloseFlag() != null && detail.getCloseFlag().equals(1)) {
								closeflag++;
							}
						}
					}
					if (size.equals(deleteflag)) {
						order.setPurchaseOrderState(PurchaseOrderState.CANCEL);
					} else if (nodelete.equals(closeflag)) {
						order.setPurchaseOrderState(PurchaseOrderState.CLOSE);
					} else if (nodelete.compareTo(closeflag) > 0) {
						order.setPurchaseOrderState(PurchaseOrderState.OPEN);
					}
					purchaseOrderLogic.save(order);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("订单关闭接口出现异常：订单号：%s，信息：%s", purOrderDetails, e.getMessage());
			// log.error(msg, e);
			content.put("ValidationErrorMessage", msg);
			WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
			wsLogLogic.addFailLog(wslog, failed(content).toString());
			return failed(e, content);
		}
		// log.info("订单关闭同步成功, ERP单号:{}", purOrderDetails);
		WsLog wslog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
		wsLogLogic.addSuccessLog(wslog, success(content).toString());
		return success(content);

	}

	protected Map<String, List<PurchaseOrderDetail>> validDetails(List<PurchaseOrderDetail> pods,
			Map<String, PurOrderDetailCloseDto> vomap) {
		Map<String, List<PurchaseOrderDetail>> podMaps = new HashMap<String, List<PurchaseOrderDetail>>();
		Set<String> set = new HashSet<String>();
		if (pods != null) {

			for (PurchaseOrderDetail detail : pods) {
				String no = detail.getPurchaseOrder().getErpPurchaseOrderNo().trim();
				set.add(no + "#" + detail.getRowIds().toString().trim());
				if (podMaps.containsKey(no)) {
					podMaps.get(no).add(detail);
				} else {
					List<PurchaseOrderDetail> details = new ArrayList<PurchaseOrderDetail>();
					details.add(detail);
					podMaps.put(no, details);
				}
			}
		}
		for (String key : vomap.keySet()) {
			if (!set.contains(key)) {
				PurOrderDetailCloseDto vo = vomap.get(key);
				throw new RuntimeException("订单明细不存在，请检查订单号[" + vo.getErpPurchaseOrderNo() + "],行号[" + vo.getRowIds() + "]是否存在");
			}
		}
		return podMaps;
	}

	/**
	 * 订单明细验证
	 */
	protected ValidVisitor purchaseOrderDetailVisitor = new ValidVisitor() {

		@Override
		public String valid(Object object) {
			StringBuffer sb = new StringBuffer();
			return sb.toString().length() > 0 ? sb.toString() : null;
		}
	};

}
