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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import com.alibaba.dubbo.governance.service.impl.AbstractService;

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
	
	public void beforeAdvice() {
		System.out.println("前置通知执行了");
	}
	
	public void afterAdvice() {
		System.out.println("后置通知执行了");
	}
	
	public void afterReturnAdvice(String result) {
		System.out.println("返回通知执行了" + "运行业务方法返回的结果为" + result);
	}
	
	public String aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String result = null;
		AbstractService abstractService = null;
		try {
			System.out.println("环绕通知开始执行了");
			
			if (proceedingJoinPoint.getTarget() instanceof AbstractService) {
				System.out.println("设置关键对象数据.");
				abstractService = (AbstractService) proceedingJoinPoint.getTarget();
				// 设置关键对象
				// abstractService.setRegistryService(registryService);
				// abstractService.setSync(sync);
			}
			result = (String) proceedingJoinPoint.proceed();
			System.out.println("环绕通知执行结束了");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void throwingAdvice(JoinPoint joinPoint, Exception e) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("异常通知执行了.");
		stringBuffer.append("方法:").append(joinPoint.getSignature().getName()).append("出现了异常.");
		stringBuffer.append("异常信息为:").append(e.getMessage());
		System.out.println(stringBuffer.toString());
	}
}
