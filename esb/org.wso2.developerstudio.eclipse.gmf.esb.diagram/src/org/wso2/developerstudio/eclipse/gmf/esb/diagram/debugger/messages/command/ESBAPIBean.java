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
 * {@link ESBAPIBean} holds attributes which identifies API Artifact uniquely
 * and defined in ESB Mediation Debugger communication API's
 *
 */
public class ESBAPIBean {

	private String apiKey;
	private ESBAPIResourceBean resourse;
	private String sequenceType;
	private ESBMediatorPosition mediatorPosition;

	public ESBAPIBean(String apiKey, ESBAPIResourceBean resourse,
			String sequenceType, ESBMediatorPosition mediatorPosition) {
		super();
		this.apiKey = apiKey;
		this.resourse = resourse;
		this.sequenceType = sequenceType;
		this.mediatorPosition = mediatorPosition;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public ESBAPIResourceBean getResourse() {
		return resourse;
	}

	public void setResourse(ESBAPIResourceBean resourse) {
		this.resourse = resourse;
	}

	public String getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(String sequenceType) {
		this.sequenceType = sequenceType;
	}

	public ESBMediatorPosition getMediatorPosition() {
		return mediatorPosition;
	}

	public void setMediatorPosition(ESBMediatorPosition mediatorPosition) {
		this.mediatorPosition = mediatorPosition;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(API_KEY, apiKey);
		attributeMap.put(SEQUENCE_TYPE, sequenceType);
		attributeMap.putAll(mediatorPosition.deserializeToMap());
		attributeMap.putAll(resourse.deserializeToMap());
		return attributeMap;
	}

}
