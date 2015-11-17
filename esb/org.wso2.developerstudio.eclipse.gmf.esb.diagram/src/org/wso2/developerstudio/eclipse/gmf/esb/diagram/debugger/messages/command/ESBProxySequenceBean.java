/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command;

import java.util.HashMap;
import java.util.Map;

public class ESBProxySequenceBean {

	private ESBProxyBean proxy;

	public ESBProxySequenceBean(ESBProxyBean proxy) {
		super();
		this.proxy = proxy;
	}

	public ESBProxyBean getProxy() {
		return proxy;
	}

	public void setProxy(ESBProxyBean proxy) {
		this.proxy = proxy;
	}

	public Map<String,Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.putAll(proxy.deserializeToMap());
		return attributeMap;
	}

}
