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
package com.alibaba.dubbo.governance.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.governance.sync.RegistryServerSync;
import com.alibaba.dubbo.registry.RegistryService;

/**
 * <b>RegistryServerConfiguration.java</b></br>
 * 
 * <pre>
 * RegistryServer关键对象全局配置器
 * </pre>
 *
 * @author cpthack 1044559878@qq.com
 * @date 2018年2月1日 下午7:42:56
 * @since JDK 1.8
 */
public class RegistryServerConfiguration implements InitializingBean {
	
	private final static Logger							 logger				   = LoggerFactory.getLogger(RegistryServerConfiguration.class);
	
	private final static Map<String, RegistryService>	 registryServiceMap	   = new HashMap<String, RegistryService>();
	private final static Map<String, RegistryServerSync> registryServerSyncMap = new HashMap<String, RegistryServerSync>();
	
	@Autowired
	private ApplicationContext							 context;
	
	@Autowired
	private ApplicationConfig							 applicationConfig;
	
	public void afterPropertiesSet() throws Exception {
		createRegistryServiceMap();// 根据多分组创建RegistryService集合
		createRegistryServerSyncMap();// 根据多分组的registryServiceMap集合创建registryServerSyncMap集合
	}
	
	private void createRegistryServiceMap() {
		RegistryConfig defaultRegistryConfig = context.getBean(RegistryConfig.class);
		String registryGroupStr = defaultRegistryConfig.getGroup();
		if (StringUtils.isBlank(registryGroupStr)) {
			return;
		}
		String[] registryGroupArgs = registryGroupStr.split(",");
		for (String registryGroup : registryGroupArgs) {
			RegistryConfig registryConfig = defaultRegistryConfig;
			registryConfig.setGroup(registryGroup);
			RegistryService registryService = createRpcService(RegistryService.class, registryConfig);
			registryServiceMap.put(registryGroup, registryService);
			logger.info("创建RegistryServiceMap对象.group=[{}],RegistryService=[{}]", registryGroup, registryService);
		}
	}
	
	private void createRegistryServerSyncMap() {
		for (Map.Entry<String, RegistryService> entry : registryServiceMap.entrySet()) {
			RegistryServerSync registryServerSync = new RegistryServerSync();
			registryServerSync.setRegistryService(entry.getValue());
			registryServerSyncMap.put(entry.getKey(), registryServerSync);
			logger.info("创建registryServerSyncMap对象.group=[{}],RegistryService=[{}]", entry.getKey(), registryServerSync);
		}
	}
	
	private <T> T createRpcService(Class<T> type, RegistryConfig registryConfig) {
		ReferenceConfig<T> reference = new ReferenceConfig<T>();
		reference.setApplication(applicationConfig);
		reference.setRegistry(registryConfig);
		reference.setInterface(type);
		return reference.get();
	}
	
	public static Map<String, RegistryService> getRegistryservicemap() {
		return registryServiceMap;
	}
	
	public static Map<String, RegistryServerSync> getRegistryserversyncmap() {
		return registryServerSyncMap;
	}
	
}
