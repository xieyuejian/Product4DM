package com.huiju.srm.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activiti.spring.boot.JpaProcessEngineAutoConfiguration;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.huiju.module.data.jpa.dao.SimpleJpaRepositoryFactoryBean;

/**
 * 定义了SRM业务服务统一的注解，避免在每一个服务中重复定义或修改后要同时修改多个地方
 * 
 * @author wanglq 2019-03-25
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, JpaProcessEngineAutoConfiguration.class })
@EnableFeignClients(basePackages = { "com.huiju.module.mvc", "com.huiju.auth.api", "com.huiju.srm", "com.huiju.core.sys.api",
		"com.huiju.core.app.api", "com.huiju.bpm.api", "com.huiju.portal.api", "com.huiju.interaction.api", "com.huiju.notify.api",
		"com.huiju.module.event.client" })
@EntityScan(basePackages = { "com.huiju.module.i18n", "com.huiju.srm", "com.huiju.module.log.jpa", "com.huiju.module.config",
		"com.huiju.module.groovy", "com.huiju.module.fs", "com.huiju.module.mail", "com.huiju.module.wechat", "com.huiju.module.event" })
@ComponentScan(basePackages = { "com.huiju.module.i18n", "com.huiju.module.cache.redis", "com.huiju.module.mvc", "com.huiju.srm",
		"com.huiju.module.log", "com.huiju.module.context", "com.huiju.module.config", "com.huiju.module.groovy", "com.huiju.module.fs",
		"com.huiju.module.mail", "com.huiju.module.wechat", "com.huiju.module.message", "com.huiju.module.event" })
@EnableJpaRepositories(repositoryFactoryBeanClass = SimpleJpaRepositoryFactoryBean.class, basePackages = { "com.huiju.module.i18n",
		"com.huiju.srm", "com.huiju.module.log.jpa", "com.huiju.module.config", "com.huiju.module.groovy", "com.huiju.module.fs",
		"com.huiju.module.mail", "com.huiju.module.wechat", "com.huiju.module.event" })
public @interface SrmServiceApplication {

}
