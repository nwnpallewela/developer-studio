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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.EventMessageType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.DebugPointRequest.DebugPointEventAction;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

import com.google.gson.JsonElement;

public class ESBProxyDebugPoint extends ESBDebugPoint {

	private ESBProxySequenceBean sequence;

	public ESBProxyDebugPoint(String command, String commandArgument,
			String mediationComponent, ESBProxySequenceBean sequence) {
		super(command, commandArgument, mediationComponent);
		this.setSeqeunce(sequence);
	}

	public ESBProxyDebugPoint(DebugPointEventAction action,
			Map<String, Object> attributeSet) {
		super(action.toString(), null, ESBDebuggerConstants.SEQUENCE);
		setCommandArgument((String) attributeSet
				.get(ESBDebuggerConstants.COMMAND_ARGUMENT));
		String proxyKey = (String) attributeSet
				.get(ESBDebuggerConstants.PROXY_KEY);
		String sequenceType = (String) attributeSet
				.get(ESBDebuggerConstants.SEQUENCE_TYPE);
		@SuppressWarnings("unchecked")
		ESBMediatorPosition mediatorPosition = new ESBMediatorPosition(
				(List<Integer>) attributeSet
						.get(ESBDebuggerConstants.MEDIATOR_POSITION));

		ESBProxyBean proxy = new ESBProxyBean(proxyKey, sequenceType,
				mediatorPosition);
		sequence = new ESBProxySequenceBean(proxy);

	}

	public ESBProxyDebugPoint(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		super(null, null, PROXY);
		setCommandArgument(event.toString());
		Set<Entry<String, JsonElement>> entrySet = recievedArtifactInfo
				.getAsJsonObject().entrySet();
		String proxyKey = null;
		String sequenceType = null;
		ESBMediatorPosition mediatorPosition = null;
		for (Entry<String, JsonElement> proxyEntry : entrySet) {
			JsonElement proxyArtifactInfo = proxyEntry.getValue();
			Set<Entry<String, JsonElement>> proxyEntrySet = proxyArtifactInfo
					.getAsJsonObject().entrySet();
			for (Entry<String, JsonElement> entry : proxyEntrySet) {
				if (PROXY_KEY.equalsIgnoreCase(entry.getKey())) {
					proxyKey = entry.getValue().toString().replace("\"", "");
				} else if (MEDIATOR_POSITION.equalsIgnoreCase(entry.getKey())) {
					mediatorPosition = convertMediatorPositionStringToList(entry
							.getValue().toString().replace("\"", ""));
				} else if (SEQUENCE_TYPE.equalsIgnoreCase(entry.getKey())) {
					sequenceType = entry.getValue().toString()
							.replace("\"", "");
				}
			}

		}
		ESBProxyBean proxy = new ESBProxyBean(proxyKey, sequenceType,
				mediatorPosition);
		sequence = new ESBProxySequenceBean(proxy);
	}

	public ESBProxySequenceBean getSeqeunce() {
		return sequence;
	}

	public void setSeqeunce(ESBProxySequenceBean seqeunce) {
		this.sequence = seqeunce;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(COMMAND_ARGUMENT, commandArgument);
		attributeMap.put(MEDIATION_COMPONENT, mediationComponent);
		attributeMap.putAll(sequence.deserializeToMap());
		return attributeMap;
	}

}
