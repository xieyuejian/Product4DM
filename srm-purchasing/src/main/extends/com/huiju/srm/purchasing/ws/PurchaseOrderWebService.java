package com.huiju.srm.purchasing.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.purchasing.dto.PurchaseOrderDto;

/**
 * 订单接口
 * 
 * @author zhuang.jq
 */
@WebService(name = "purchaseOrderWebService", targetNamespace = "http://www.huiju.com/purchaseOrder")
public interface PurchaseOrderWebService {
    /**
     * 新增或修改订单 包括取消订单和关闭订单的方法
     * 
     * @param purchaseOrder
     *            采购订单VO
     * @return 处理结果
     */
    @WebResult(name = "result")
    @WebMethod(operationName = "saveOrUpdatePurchaseOrder")
    Message saveOrUpdatePurchaseOrder(@WebParam(name = "purchaseOrder") PurchaseOrderDto purchaseOrder);
}
