package com.huiju.srm.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置器
 * 
 * @author wanglq 2019-04-10
 *
 */
@Configuration
public class SrmConfigurer implements WebMvcConfigurer {

	/**
	 * 通过CORS协议配置允许所有资源的跨域访问
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

}
