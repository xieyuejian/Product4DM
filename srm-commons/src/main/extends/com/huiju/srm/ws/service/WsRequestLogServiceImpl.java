package com.huiju.srm.ws.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.IdUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.ws.entity.WsRequestLog;
import com.huiju.srm.ws.entity.WsRequestLogDtl;

/**
 * @author ZJQ
 */
@Service
public class WsRequestLogServiceImpl extends JpaServiceImpl<WsRequestLog, Long> implements WsRequestLogService {

	@Autowired
	private PlatformTransactionManager transactionManager;

	protected final Map<String, String> interfaceMaps = new HashMap<String, String>() {
		private static final long serialVersionUID = -4468284336043585303L;

		{
			put("SRM_MATERIAL", "物料主数据");
			put("SRM_DELIVERY", "送货信息");
			put("SRM_PURCHASINGORDER", "采购订单");
			put("SRM_RECEIVING", "收退货信息");
			put("SRM_QUALITY", "质检信息");
			put("SRM_PREINVOICE", "预制发票");
			put("SRM_STOCK", "库存信息");
			put("SRM_SUBCONSIGNMENT", "寄售分包库存");

			/** 供应商同步接口 */
			put("SRM_101", "供应商同步");
			put("SRM_102", "价格主数据同步");
			put("SRM_103", "价格主数据冻结");
			put("SRM_104", "货源清单同步");
			put("SRM_105", "采购申请同步");
			put("SRM_106", "采购订单同步");
			put("SRM_107", "送货信息同步");
			put("SRM_108", "收货信息同步");
			put("SRM_109", "质检信息同步");
			put("SRM_110", "预制发票同步");
			put("SRM_111", "预制发票冲销");
			put("SRM_112", "快递接口");
		}
	};

	protected final int SUCCESS_CODE = 1;
	protected final int FAIL_CODE = 2;
	protected final int ERROR_CODE = 0;
	protected final String SOURCE_SYSTEM = "SRM";

	@Override
	protected Class<WsRequestLog> getEntityClass() {
		return WsRequestLog.class;
	}

	@Override
	public WsRequestLog findWsRequestLog(Long id) {
		WsRequestLog entity = this.findById(id);
		if (entity == null) {
			return null;
		}
		List<WsRequestLogDtl> dtls = entity.getWsRequestLogDtls();
		if (dtls != null && dtls.size() > 0) {
			for (WsRequestLogDtl dtl : dtls) {
				entity.setRequestContent(StringUtils.isBlank(dtl.getRequestContent()) ? null
						: dtl.getRequestContent().replaceAll("\\s*((2[0-3])|([0-1]?\\d)):[0-5]?\\d(:[0-5]?\\d)", "[HH:mm:ss]"));
				entity.setResponseContent(StringUtils.isBlank(dtl.getResponseContent()) ? null
						: dtl.getResponseContent().replaceAll("\\s*((2[0-3])|([0-1]?\\d)):[0-5]?\\d(:[0-5]?\\d)", "[HH:mm:ss]"));
			}
		}
		return entity;
	}

	// ----------------------------------------add by cwq on 2019-04-25
	// 16:01:49---------------------------------------------------------
	/**
	 * 创建log对象
	 * 
	 * @param interactCode 接口编码
	 * @param businessCodes 业务单据编码
	 * @param requestContent 请求报文
	 * @return 返回日志对象
	 */
	public WsRequestLog createTargetErpLog(String interfaceCode, String businessCodes, String requestContent) {
		WsRequestLog log = createLog(interfaceCode, businessCodes, requestContent);
		log.setTargetSystem(SrmConstants.TAEGET_SYSTEM);
		dao.save(log);
		return log;
	}

	/**
	 * 创建log对象
	 * 
	 * @param interfaceCode
	 * @param requestContent
	 * @return
	 */
	private WsRequestLog createLog(String interfaceCode, String businessCodes, String requestContent) {
		WsRequestLog log = new WsRequestLog();
		log.setRequestId(IdUtils.genNextId());
		log.setLogId(IdUtils.genNextId());
		log.setInterfaceCode(interfaceCode);
		log.setInterfaceName(interfaceMaps.get(interfaceCode));
		log.setRequestTime(Calendar.getInstance());
		log.setBusinessCodes(businessCodes);
		log.setSourceSystem(SOURCE_SYSTEM);
		WsRequestLogDtl logDtl = new WsRequestLogDtl();
		logDtl.setLogDtlId(IdUtils.genNextId());
		logDtl.setRequestContent(requestContent);
		logDtl.setWsRequestLog(log);
		List<WsRequestLogDtl> logDtls = new ArrayList<WsRequestLogDtl>();
		logDtls.add(logDtl);
		log.setWsRequestLogDtls(logDtls);
		return log;
	}

	/**
	 * 异常日志
	 * 
	 * @param log 日志对象
	 */
	public void addErrorLog(WsRequestLog log, String responseContent) {
		log.setSuccessflag(ERROR_CODE);
		addLog(log, responseContent);
	}

	/**
	 * 异常日志
	 * 
	 * @param log 志对象
	 */
	public void addSuccessLog(WsRequestLog log, String responseContent) {
		log.setSuccessflag(SUCCESS_CODE);
		addLog(log, responseContent);
	}

	/**
	 * 失败日志
	 * 
	 * @param log 日志对象
	 */
	public void addFailLog(WsRequestLog log, String responseContent) {
		log.setSuccessflag(FAIL_CODE);
		addLog(log, responseContent);
	}

	/**
	 * 统一插入数据入口
	 * 
	 * @param log 日志对象
	 * @param responseContent 响应保温
	 */
	private void addLog(WsRequestLog log, String responseContent) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			log.getWsRequestLogDtls().get(0).setResponseContent(responseContent);
			log.setResponseTime(Calendar.getInstance());
			dao.save(log);
			transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				transactionManager.rollback(status);
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}

	// ----------------------------------------add by cwq on 2019-04-25
	// 16:01:49---------------------------------------------------------

}
