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

public class ESBAPISequenceBean {

	private ESBAPIBean api;

	public ESBAPISequenceBean(ESBAPIBean api) {
		super();
		this.setApi(api);
	}

	public ESBAPIBean getApi() {
		return api;
	}

	public void setApi(ESBAPIBean api) {
		this.api = api;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.putAll(api.deserializeToMap());
		return attributeMap;
	}

}
