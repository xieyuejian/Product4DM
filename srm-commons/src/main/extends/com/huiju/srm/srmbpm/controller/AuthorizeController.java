package com.huiju.srm.srmbpm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权单action
 * 
 * @author hongwl
 *
 * @date 2019年4月16日
 */
@RestController
@RequestMapping("/sys/authorize")
public class AuthorizeController extends StdAuthorizeController {
	private static final long serialVersionUID = -7872416956155644151L;
}
