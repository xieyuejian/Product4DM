package com.huiju.srm.purchasing.ws;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.util.StringUtils;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.commons.ws.utils.MessageContent;
import com.huiju.srm.commons.ws.utils.ServiceSupport;
import com.huiju.srm.purchasing.dto.CensorQualityDto;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.service.CensorQualityService;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.ReceivingNoteService;
import com.huiju.srm.stock.service.StockService;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.handler.SoapMessageMgr;
import com.huiju.srm.ws.service.WsLogService;

/**
 * 质检接口
 * 
 * @author zhuang.jq
 */
@Component // 由Spring管理
@WebService(serviceName = "censorQualityWebService", targetNamespace = "http://www.huiju.com/censorQuality", endpointInterface = "com.huiju.srm.purchasing.ws.CensorQualityWebService")
public class CensorQualityWebServiceImpl extends ServiceSupport implements CensorQualityWebService {

	@Autowired
	protected ReceivingNoteService receivingNoteLogic;
	@Autowired
	protected BillSetServiceClient billSetLogic;
	@Autowired
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired
	protected CensorQualityService censorQualityLogic;
	@Autowired
	protected StockService stockLogic;
	@Autowired
	protected PortalServiceClient portalDealDataLogic;
	@Autowired
	protected UserClient userLogic;
	// 请求报文本地线程缓存
	private SoapMessageMgr soapMessageMgr;
	@Autowired
	protected WsLogService wsLogLogic;

	/**
	 * 质检新增修改
	 * 
	 * @param censorQuality 质检vo
	 * @return 处理结果
	 */
	@Override
	public Message saveOrUpdateCensorQuality(CensorQualityDto censorQuality) {
		soapMessageMgr = SoapMessageMgr.getCurrentInstance();
		// 请求参数
		String requestContent = StringUtils.isBlank(soapMessageMgr.getSoapMessageStr()) ? DataUtils.toJson(censorQuality)
				: soapMessageMgr.getSoapMessageStr();
		// 接口编码
		String interfaceCode = SrmConstants.SRM_QUALITY_CODE;
		// 放置执行内容状态
		MessageContent content = createContent();
		WsLog wlog = wsLogLogic.createSourceErpLog(interfaceCode, requestContent);
		try {
			// 保存或者更新质检
			saveUpdateCensorQuality(censorQuality);
		} catch (Exception e) {
			e.printStackTrace();
			wsLogLogic.addFailLog(wlog, failed(e, content).toString());
			return failed(e, content);
		}
		wsLogLogic.addSuccessLog(wlog, success(content).toString());
		return success(content);
	}

	/**
	 * 创建质检对象
	 * 
	 * @param vo 质检vo
	 * @return 返回质检对象
	 */
	protected void saveUpdateCensorQuality(CensorQualityDto vo) {
		// 查询收货数据
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_voucherYear", vo.getMaterialCertificateYear());
		params.put("EQ_voucherNo", vo.getMaterialCertificateCode());
		params.put("EQ_voucherProNo", vo.getMaterialCertificateItem());
		CensorQuality entity = censorQualityLogic.findOne(params);
		if (entity == null) {
			throw new RuntimeException("检验数据不存在，请检查物料凭证年度：" + vo.getMaterialCertificateYear() + "，物料凭证编号:" + vo.getMaterialCertificateCode()
					+ "，物料凭证中的项目：" + vo.getMaterialCertificateItem() + "是否正确");
		}

		if (StringUtils.isBlank(vo.getResultCode())) {
			throw new RuntimeException("没有设置质检结果");
		}

		entity.setResultCode(vo.getResultCode());
		entity.setQualityTime(Calendar.getInstance());

		// 合格
		if ("1".equals(vo.getResultCode())) {
			entity.setResultName("合格");
		} else if ("2".equals(vo.getResultCode())) {
			entity.setResultName("让步接收");
		} else if ("3".equals(vo.getResultCode())) {
			entity.setResultName("拒绝");
		}

		// 合格量 = 送检量 - 本次不合格量 - 让步接收量
		BigDecimal censorQty = entity.getCensorQty(); // 送检量
		BigDecimal unqualifiedQty = vo.getUnqualifiedQty() == null ? BigDecimal.ZERO : vo.getUnqualifiedQty(); // 本次不合格量
		BigDecimal receiveQty = vo.getReceiveQty() == null ? BigDecimal.ZERO : vo.getReceiveQty(); // 本次让步接收量
		BigDecimal qualifiedQty = censorQty.subtract(unqualifiedQty).subtract(receiveQty);

		entity.setCheckQualifiedQty(qualifiedQty);
		entity.setCheckUnqualifiedQty(unqualifiedQty);
		entity.setCheckReceiveQty(receiveQty);
		entity.setCanCheckQty(BigDecimal.ZERO);
		entity.setStatus(CensorQualityState.CHECKED); // 已检验
		entity.setErpSyn(SrmSynStatus.SYNSUCCESS);

		User user = null;
		// 获取采购员
		if (StringUtils.isNotBlank(vo.getUserCode())) {
			params.clear();
			user = userLogic.findByCode(vo.getUserCode().toLowerCase());

			if (null == user) {
				user = userLogic.findByCode(vo.getUserCode());
			}

			if (user == null) {
				throw new RuntimeException("用户编码不存在，请在SRM创建相应编码的用户buyerId[" + vo.getUserCode() + "]");
			} else {
				entity.setQualitorId(user.getUserId());
				entity.setQualitorName(user.getUserName());
			}
		}

		censorQualityLogic.save(entity);

		// 回置收货单的质检结果，质检状态
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("EQ_materialCertificateYear", vo.getMaterialCertificateYear());
		maps.put("EQ_materialCertificateItem", vo.getMaterialCertificateItem());
		maps.put("EQ_materialCertificateCode", vo.getMaterialCertificateCode());

		ReceivingNote rn = receivingNoteLogic.findOne(maps);
		if (rn != null) {
			rn.setStatus(entity.getStatus());
			receivingNoteLogic.save(rn);
		}

	}
}
