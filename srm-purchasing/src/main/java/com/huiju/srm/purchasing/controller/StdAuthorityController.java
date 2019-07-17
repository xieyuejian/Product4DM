package com.huiju.srm.purchasing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.huiju.module.data.common.Result;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.service.CensorQualityService;

public class StdAuthorityController extends CloudController {
	@Autowired
	protected CensorQualityService censorQualityServiceImpl;

	@PostMapping("/get")
	public Result edit(Long id) {
		CensorQuality model = censorQualityServiceImpl.findById(id);
		if (model == null) {
			return Result.error("信息不存在！");
		}
		return Result.success(model);

	}
}
