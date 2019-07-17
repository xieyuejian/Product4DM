package com.huiju.srm.commons.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huiju.module.context.Constants;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.log.Log;
import com.huiju.module.log.Logs;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.notify.dto.NotifyParam;

/**
 * SRM 消息通知message
 * 
 * @author chensw
 * 
 */

public class SrmNotifyMessage {

	/** 单据id */
	private String billId;
	/** 单据编码 */
	private String billNo;
	/** 单据类型编码 */
	private String billTypeCode;
	/** 单据类型名称 */
	private String billTypeName;
	/** 用户编码 */
	private String userCode;
	/** 用户名称 */
	private String userName;
	/** 供应商编码 */
	private String vendorCode;
	/** 供应商名称 */
	private String vendorName;
	/** 审核意见 */
	private String message;
	/** 单据状态 */
	private String billState;
	/** 当前时间 */
	private String currentTime;
	/** 微信模版参数 */
	private String first;
	private String keyword1;
	private String keyword2;
	private String keyword3;
	private String remark;

	/** map参数 */
	private Map<String, Object> paramsMap;

	/** 通知模版编码 */
	private String notifyCode;
	/** 消息发送人 */
	private Long senderId;
	/** 消息发送人 */
	private String senderCode;
	/** 消息接收人 */
	private List<Long> receiverIds = new ArrayList<Long>();
	/** 消息接收人 */
	private List<String> receiverCodes = new ArrayList<String>();

	private static final Log log = Logs.getLog(SrmNotifyMessage.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	/** 消息通知远程接口 */
	private NotifySenderClient notifySenderClient;

	public SrmNotifyMessage(String currentTime) {
		this.currentTime = currentTime;
		this.keyword2 = currentTime;
		this.put("currentTime", currentTime);
		this.put("keyword2", currentTime);
	}

	/**
	 * 构造SRM消息通知对象
	 * 
	 * @return
	 */
	public static SrmNotifyMessage build() {
		return new SrmNotifyMessage(sdf.format(new Date()));
	}

	/**
	 * 单据id
	 * 
	 * @param billId
	 * @return
	 */
	public SrmNotifyMessage billId(String billId) {
		this.billId = billId;
		return this.put("billId", billId);
	}

	/**
	 * 单据编码
	 * 
	 * @param billNo
	 * @return
	 */
	public SrmNotifyMessage billNo(String billNo) {
		this.billNo = billNo;
		return this.put("billNo", billNo);
	}

	/**
	 * 单据类型编码
	 * 
	 * @param billTypeCode
	 * @return
	 */
	public SrmNotifyMessage billTypeCode(String billTypeCode) {
		this.billTypeCode = billTypeCode;
		return this.put("billTypeCode", billTypeCode);
	}

	/**
	 * 单据类型名称
	 * 
	 * @param billTypeName
	 * @return
	 */
	public SrmNotifyMessage billTypeName(String billTypeName) {
		this.billTypeName = billTypeName;
		return this.put("billTypeName", billTypeName);
	}

	/**
	 * 用户编码
	 * 
	 * @param userCode
	 * @return
	 */
	public SrmNotifyMessage userCode(String userCode) {
		this.userCode = userCode;
		return this.put("userCode", userCode);
	}

	/**
	 * 用户名称
	 * 
	 * @param userName
	 * @return
	 */
	public SrmNotifyMessage userName(String userName) {
		this.userName = userName;
		return this.put("userName", userName);
	}

	/**
	 * 供应商编码
	 * 
	 * @param vendorCode
	 * @return
	 */
	public SrmNotifyMessage vendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
		return this.put("vendorCode", vendorCode);
	}

	/**
	 * 供应商名称
	 * 
	 * @param vendorName
	 * @return
	 */
	public SrmNotifyMessage vendorName(String vendorName) {
		this.vendorName = vendorName;
		return this.put("vendorName", vendorName);
	}

	/**
	 * 审核意见
	 * 
	 * @param message
	 * @return
	 */
	public SrmNotifyMessage message(String message) {
		this.message = message;
		return this.put("message", message);
	}

	/**
	 * 单据状态
	 * 
	 * @param billState
	 * @return
	 */
	public SrmNotifyMessage billState(String billState) {
		this.billState = billState;
		return this.put("billState", billState);
	}

	/**
	 * 微信模版消息参数first
	 * 
	 * @param first
	 * @return
	 */
	public SrmNotifyMessage first(String first) {
		this.first = first;
		return this.put("first", first);
	}

	/**
	 * 微信模版消息参数keyword1
	 * 
	 * @param keyword1
	 * @return
	 */
	public SrmNotifyMessage keyword1(String keyword1) {
		this.keyword1 = keyword1;
		return this.put("keyword1", keyword1);
	}

	/**
	 * 微信模版消息参数keyword2
	 * 
	 * @param keyword2
	 * @return
	 */
	public SrmNotifyMessage keyword2(String keyword2) {
		this.keyword2 = keyword2;
		return this.put("keyword2", keyword2);
	}

	/**
	 * 微信模版消息参数keyword3
	 * 
	 * @param keyword3
	 * @return
	 */
	public SrmNotifyMessage keyword3(String keyword3) {
		this.keyword3 = keyword3;
		return this.put("keyword3", keyword3);
	}

	/**
	 * 微信模版消息参数remark
	 * 
	 * @param remark
	 * @return
	 */
	public SrmNotifyMessage remark(String remark) {
		this.remark = remark;
		return this.put("remark", remark);
	}

	/**
	 * 消息通知编码
	 * 
	 * @param notifyCode
	 * @return
	 */
	public SrmNotifyMessage notifyCode(String notifyCode) {
		this.notifyCode = notifyCode;
		return this;
	}

	/**
	 * 消息通知发送人
	 * 
	 * @param sender
	 * @return
	 */
	public SrmNotifyMessage senderId(Long senderId) {
		this.senderId = senderId;
		return this;
	}

	/**
	 * 消息通知发送人
	 * 
	 * @param sender
	 * @return
	 */
	public SrmNotifyMessage senderCode(String senderCode) {
		this.senderCode = senderCode;
		return this;
	}

	/**
	 * 消息通知接收人
	 * 
	 * @param receivers
	 * @return
	 */
	public SrmNotifyMessage receiverIds(List<Long> receiverIds) {
		this.receiverIds = receiverIds;
		return this;
	}

	/**
	 * 消息通知接收人
	 * 
	 * @param receivers
	 * @return
	 */
	public SrmNotifyMessage receiverId(Long receiverId) {
		this.receiverIds.add(receiverId);
		return this;
	}

	/**
	 * 消息通知接收人
	 * 
	 * @param receivers
	 * @return
	 */
	public SrmNotifyMessage receiverCodes(List<String> receiverCodes) {
		this.receiverCodes = receiverCodes;
		return this;
	}

	/**
	 * 消息通知接收人
	 * 
	 * @param receivers
	 * @return
	 */
	public SrmNotifyMessage receiverCode(String receiverCode) {
		this.receiverCodes.add(receiverCode);
		return this;
	}

	/**
	 * 消息通知远程接口
	 * 
	 * @param notifySenderLogic
	 * @return
	 */
	public SrmNotifyMessage notifySender(NotifySenderClient notifySenderClient) {
		this.notifySenderClient = notifySenderClient;
		return this;
	}

	/**
	 * 扩展参数传递方法
	 * 
	 * @param key 键值
	 * @param value 值
	 * @return
	 */
	public SrmNotifyMessage put(String key, Object value) {
		if (this.paramsMap == null) {
			this.paramsMap = new HashMap<String, Object>();
		}
		this.paramsMap.put(key, value);
		return this;
	}

	/**
	 * 消息模版参数转map对象
	 * 
	 * @return 消息模版参数map对象
	 */
	public Map<String, Object> toMap() {
		// log.warn(Json.toJson(paramsMap));
		if (this.paramsMap == null) {
			this.paramsMap = new HashMap<String, Object>();
		}
		Field[] fs = this.getClass().getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			if (f.getType().toString().endsWith("String")) {
				if (this.paramsMap.get(f.getName()) == null) {
					this.paramsMap.put(f.getName(), "");
				}
			}
		}
		return this.paramsMap;
	}

	/**
	 * 消息模版参数转json
	 * 
	 * @return 消息模版参数json
	 */
	public String toJson() {
		if (this.paramsMap == null) {
			this.paramsMap = new HashMap<String, Object>();
		}
		Field[] fs = this.getClass().getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			if (f.getType().toString().endsWith("String")) {
				if (this.paramsMap.get(f.getName()) == null) {
					this.paramsMap.put(f.getName(), "");
				}
			}
		}
		return DataUtils.toJson(this.paramsMap);
	}

	/**
	 * 消息通知发送
	 * 
	 * @return
	 */
	public boolean send() {
		if (this.notifySenderClient == null || (this.senderId == null && this.senderCode == null) || this.notifyCode == null) {
			log.warn("notify sender error!");
			return false;
		}
		return notifySenderClient
				.send(NotifyParam.build().clientCode(Constants.DEFAUTL_CLIENT_CODE).senderId(senderId).notifyCode(notifyCode)
						.senderCode(senderCode).receiverIds(receiverIds).receiverCodes(receiverCodes).extraParams(paramsMap));
	}

	@Override
	public String toString() {
		return "SrmNotifyMessage [billId=" + billId + ", billNo=" + billNo + ", billTypeCode=" + billTypeCode + ", billTypeName="
				+ billTypeName + ", userCode=" + userCode + ", userName=" + userName + ", vendorCode=" + vendorCode + ", vendorName="
				+ vendorName + ", message=" + message + ", billState=" + billState + ", currentTime=" + currentTime + ", first=" + first
				+ ", keyword1=" + keyword1 + ", keyword2=" + keyword2 + ", keyword3=" + keyword3 + ", remark=" + remark + ", paramsMap="
				+ paramsMap + ", notifyCode=" + notifyCode + ", sender=" + (senderId != null ? senderId : senderCode) + "]";
	}

}
