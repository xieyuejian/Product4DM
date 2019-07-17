package com.huiju.srm.purchasing.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huiju.module.data.common.JobResult;
import com.huiju.module.data.common.JobResultData;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.purchasing.service.PurchaseOrderService;

/**
 * 采购订单调度任务
 * 
 * @author zhuang.jq
 */
@RestController
@RequestMapping("/cp/purchaseorderjob")
public class PurchaseOrderJobController extends CloudController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 采购订单定时器方法
     * 
     * @return
     */
    @RequestMapping("/dojob")
    public JobResult doJob() {
    	try {
    		List<JobResultData> resultList = new ArrayList<JobResultData>();
        	resultList = purchaseOrderService.purchaseOrderJobMethod();

    		return JobResult.success("CGD", resultList);
		} catch (Exception e) {
			return JobResult.error("CGD", ExceptionUtils.getStackTrace(e));
		}
    	
    }
}
