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
 * {@link ESBSequenceBean} holds attributes which identifies Proxy Artifact
 * uniquely and defined in ESB Mediation Debugger communication API's
 *
 */
public class ESBSequenceBean {

	private String sequenceType;
	private String sequenceKey;
	private ESBMediatorPosition mediatorPosition;

	public ESBSequenceBean(String sequenceType, String sequenceKey,
			ESBMediatorPosition mediatorPosition) {
		super();
		this.sequenceType = sequenceType;
		this.sequenceKey = sequenceKey;
		this.mediatorPosition = mediatorPosition;
	}

	public String getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(String sequenceType) {
		this.sequenceType = sequenceType;
	}

	public String getSequenceKey() {
		return sequenceKey;
	}

	public void setSequenceKey(String sequenceKey) {
		this.sequenceKey = sequenceKey;
	}

	public ESBMediatorPosition getMediatorPosition() {
		return mediatorPosition;
	}

	public void setMediatorPosition(ESBMediatorPosition mediatorPosition) {
		this.mediatorPosition = mediatorPosition;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(SEQUENCE_KEY, sequenceKey);
		attributeMap.put(SEQUENCE_TYPE, sequenceType);
		attributeMap.putAll(mediatorPosition.deserializeToMap());
		return attributeMap;
	}

}