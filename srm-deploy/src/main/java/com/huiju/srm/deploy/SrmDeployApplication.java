package com.huiju.srm.deploy;

import org.springframework.boot.SpringApplication;

import com.huiju.srm.commons.annotation.SrmServiceApplication;

@SrmServiceApplication
public class SrmDeployApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrmDeployApplication.class, args);
	}

}
