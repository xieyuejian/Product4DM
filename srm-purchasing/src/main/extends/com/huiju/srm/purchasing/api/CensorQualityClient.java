package com.huiju.srm.purchasing.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huiju.module.data.common.FeignParam;
import com.huiju.srm.purchasing.entity.CensorQuality;

/**
 * 质检客户端，供其他服务调用
 * 
 * @author bairx  2019-04-04
 */
public interface CensorQualityClient {

	// --------------------------------------------Feign----------------------------------------------

	/**
	 * @param params 查询
	 */
	@RequestMapping("/find")
	public List<CensorQuality> find(@RequestBody FeignParam<CensorQuality> feignParam);

}
