/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.governance.sync;

import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.governance.config.RegistryServerConfiguration;
import com.alibaba.dubbo.registry.RegistryService;

public class RegistryServerSyncExt implements InitializingBean, DisposableBean {
	
	private static final Logger			logger = LoggerFactory.getLogger(RegistryServerSyncExt.class);
	
	private static final URL SUBSCRIBE = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
            Constants.INTERFACE_KEY, Constants.ANY_VALUE,
            Constants.GROUP_KEY, Constants.ANY_VALUE,
            Constants.VERSION_KEY, Constants.ANY_VALUE,
            Constants.CLASSIFIER_KEY, Constants.ANY_VALUE,
            Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY + ","
            + Constants.CONSUMERS_CATEGORY + ","
            + Constants.ROUTERS_CATEGORY + ","
            + Constants.CONFIGURATORS_CATEGORY,
            Constants.ENABLED_KEY, Constants.ANY_VALUE,
            Constants.CHECK_KEY, String.valueOf(false));
	
	@Autowired
	private RegistryServerConfiguration	registryServerConfiguration;
	
	public void afterPropertiesSet() throws Exception {
		Map<String, RegistryService>	 registryServiceMap	 = registryServerConfiguration.getRegistryservicemap();
		Map<String, RegistryServerSync> registryServerSyncMap = registryServerConfiguration.getRegistryserversyncmap();
		for(String groupName:registryServiceMap.keySet()) {
			registryServiceMap.get(groupName).subscribe(SUBSCRIBE, registryServerSyncMap.get(groupName));
		}
	}
	
	public void destroy() throws Exception {
		Map<String, RegistryService>	 registryServiceMap	 = registryServerConfiguration.getRegistryservicemap();
		Map<String, RegistryServerSync> registryServerSyncMap = registryServerConfiguration.getRegistryserversyncmap();
		for(String groupName:registryServiceMap.keySet()) {
			registryServiceMap.get(groupName).unsubscribe(SUBSCRIBE, registryServerSyncMap.get(groupName));
		}
	}
}
