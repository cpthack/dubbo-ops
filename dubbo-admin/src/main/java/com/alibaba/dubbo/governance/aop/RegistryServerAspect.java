/**
 * Copyright (c) 2013-2020, cpthack 成佩涛 (1044559878@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.governance.aop;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.governance.config.RegistryServerConfiguration;
import com.alibaba.dubbo.governance.service.impl.AbstractService;
import com.alibaba.dubbo.governance.sync.RegistryServerSync;
import com.alibaba.dubbo.governance.web.home.module.control.Menu;
import com.alibaba.dubbo.registry.RegistryService;

/**
 * <b>RegistryServerAspect.java</b></br>
 * 
 * <pre>
 * RegistryServer对象注入切面拦截
 * </pre>
 *
 * @author cpthack 1044559878@qq.com
 * @date Feb 2, 2018 12:02:13 AM
 * @since JDK 1.8
 */
public class RegistryServerAspect {
	
	private final static Logger			logger						 = LoggerFactory.getLogger(RegistryServerAspect.class);
	private final String				DEFAULT_ZOOKEEPER_GOURP_NAME = "home";
	@Autowired
	private RegistryServerConfiguration	registryServerConfiguration;
	
	@Autowired
	private HttpServletRequest			request;
	
	public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Object result = null;
		AbstractService abstractService = null;
		try {
			
			if (proceedingJoinPoint.getTarget() instanceof AbstractService) {
				abstractService = (AbstractService) proceedingJoinPoint.getTarget();
				// 设置关键对象
				abstractService.setRegistryService(getCurrentRegistryService());
				abstractService.setSync(getCurrentRegistryServerSync());
				logger.info("设置所有继承了AbstractService类的RegistryService对象和RegistryServerSync对象值.class=[{}]", proceedingJoinPoint.getTarget().getClass());
			}
			
			if (proceedingJoinPoint.getTarget() instanceof Menu) {
				Menu menu = (Menu) proceedingJoinPoint.getTarget();
				menu.setRegistryServerSync(getCurrentRegistryServerSync());
				logger.info("设置所有继承了Menu类的RegistryServerSync对象值.class=[{}]", proceedingJoinPoint.getTarget().getClass());
			}
			
			result = proceedingJoinPoint.proceed();
		}
		catch (Throwable e) {
			logger.error("RegistryServer对象注入切面拦截器操作失败.", e);
		}
		return result;
	}
	
	public void beforeAdvice() {
	}
	
	public void afterAdvice() {
	}
	
	public void afterReturnAdvice(Object result) {
	}
	
	public void throwingAdvice(JoinPoint joinPoint, Exception e) {
	}
	
	private String getZookeeperGroupName() {
		String zookeeperGroupName = DEFAULT_ZOOKEEPER_GOURP_NAME;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return zookeeperGroupName;
		}
		for (Cookie cookie : cookies) {
			if ("zookeeperGroupName".equals(cookie.getName())) {
				zookeeperGroupName = cookie.getValue();
			}
		}
		return zookeeperGroupName;
	}
	
	@SuppressWarnings("static-access")
	private RegistryService getCurrentRegistryService() {
		String zookeeperGroupName = getZookeeperGroupName();
		return registryServerConfiguration.getRegistryservicemap().get(zookeeperGroupName);
	}
	
	@SuppressWarnings("static-access")
	private RegistryServerSync getCurrentRegistryServerSync() {
		String zookeeperGroupName = getZookeeperGroupName();
		return registryServerConfiguration.getRegistryserversyncmap().get(zookeeperGroupName);
	}
}
