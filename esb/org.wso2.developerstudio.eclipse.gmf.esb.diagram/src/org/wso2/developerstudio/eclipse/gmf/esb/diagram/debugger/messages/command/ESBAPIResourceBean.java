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

/**
 * {@link ESBAPIResourceBean} holds attributes which identifies API Resource with in API
 * artifact uniquely and defined in ESB Mediation Debugger communication API's
 *
 */
public class ESBAPIResourceBean {

	private String method;
	private String urlMapping;
	private String uriTemplate;

	public ESBAPIResourceBean(String method, String uriMapping,
			String uriTemplate) {
		super();
		this.method = method;
		this.urlMapping = uriMapping;
		this.uriTemplate = uriTemplate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUriMapping() {
		return urlMapping;
	}

	public void setUriMapping(String uriMapping) {
		this.urlMapping = uriMapping;
	}

	public String getUriTemplate() {
		return uriTemplate;
	}

	public void setUriTemplate(String uriTemplate) {
		this.uriTemplate = uriTemplate;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(METHOD, method);
		attributeMap.put(URI_MAPPING, urlMapping);
		attributeMap.put(URL_TEMPLATE, uriTemplate);
		return attributeMap;
	}

}
