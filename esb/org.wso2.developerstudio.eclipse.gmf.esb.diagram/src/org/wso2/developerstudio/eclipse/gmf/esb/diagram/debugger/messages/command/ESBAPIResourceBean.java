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

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

import java.util.HashMap;
import java.util.Map;

public class ESBAPIResourceBean {

	private String method;
	private String uriMapping;
	private String urlTemplate;

	public ESBAPIResourceBean(String method, String uriMapping,
			String uriTemplate) {
		super();
		this.method = method;
		this.uriMapping = uriMapping;
		this.urlTemplate = uriTemplate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUriMapping() {
		return uriMapping;
	}

	public void setUriMapping(String uriMapping) {
		this.uriMapping = uriMapping;
	}

	public String getUriTemplate() {
		return urlTemplate;
	}

	public void setUriTemplate(String uriTemplate) {
		this.urlTemplate = uriTemplate;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(METHOD, method);
		attributeMap.put(URI_MAPPING, uriMapping);
		attributeMap.put(URL_TEMPLATE, urlTemplate);
		return attributeMap;
	}

}
