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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.governance.config.RegistryServerConfiguration;

public class RegistryServerSyncExt implements InitializingBean, DisposableBean {
	
	private static final Logger			logger = LoggerFactory.getLogger(RegistryServerSyncExt.class);
	
	@Autowired
	private RegistryServerConfiguration	registryServerConfiguration;
	
	@SuppressWarnings("static-access")
	public void afterPropertiesSet() throws Exception {
		Map<String, RegistryServerSync> registryServerSyncMap = registryServerConfiguration.getRegistryserversyncmap();
		for (String groupName : registryServerSyncMap.keySet()) {
			registryServerSyncMap.get(groupName).afterPropertiesSet();
			logger.info("调用RegistryServerSync.afterPropertiesSet().groupName=[{}]", groupName);
		}
	}
	
	@SuppressWarnings("static-access")
	public void destroy() throws Exception {
		Map<String, RegistryServerSync> registryServerSyncMap = registryServerConfiguration.getRegistryserversyncmap();
		for (String groupName : registryServerSyncMap.keySet()) {
			registryServerSyncMap.get(groupName).destroy();
			logger.info("调用RegistryServerSync.destroy().groupName=[{}]", groupName);
		}
	}
}
