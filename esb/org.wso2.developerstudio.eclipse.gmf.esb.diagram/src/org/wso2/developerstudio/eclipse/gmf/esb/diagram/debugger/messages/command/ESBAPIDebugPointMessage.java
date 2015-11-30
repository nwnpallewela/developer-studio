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

/**
 * {@link ESBAPIDebugPointMessage} is the bean class to represent API artifacts
 * debug point message from ESB Server Debugger
 *
 */
public class ESBAPIDebugPointMessage extends AbstractESBDebugPointMessage {

	private ESBAPISequenceBean sequence;

	public ESBAPIDebugPointMessage(String command, String commandArgument,
			String mediationComponent, ESBAPISequenceBean sequence) {
		super(command, commandArgument, mediationComponent);
		this.setSequence(sequence);
	}

	public ESBAPIDebugPointMessage(DebugPointEventAction action,
			Map<String, Object> attributeSet) {
		super(action.toString(), null, ESBDebuggerConstants.SEQUENCE);
		setCommandArgument((String) attributeSet
				.get(ESBDebuggerConstants.COMMAND_ARGUMENT));
		String apiKey = (String) attributeSet.get(ESBDebuggerConstants.API_KEY);
		String sequenceType = (String) attributeSet
				.get(ESBDebuggerConstants.SEQUENCE_TYPE);
		@SuppressWarnings("unchecked")
		ESBMediatorPosition mediatorPosition = new ESBMediatorPosition(
				(List<Integer>) attributeSet
						.get(ESBDebuggerConstants.MEDIATOR_POSITION));

		String method = (String) attributeSet.get(ESBDebuggerConstants.METHOD);
		String uriMapping = (String) attributeSet
				.get(ESBDebuggerConstants.URL_MAPPING);
		String uriTemplate = (String) attributeSet
				.get(ESBDebuggerConstants.URI_TEMPLATE);
		ESBAPIResourceBean resource = new ESBAPIResourceBean(method,
				uriMapping, uriTemplate);
		ESBAPIBean api = new ESBAPIBean(apiKey, resource, sequenceType,
				mediatorPosition);
		sequence = new ESBAPISequenceBean(api);
	}

	public ESBAPIDebugPointMessage(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		super(null, null, SEQUENCE);
		setCommandArgument(event.toString());
		Set<Entry<String, JsonElement>> entrySet = recievedArtifactInfo
				.getAsJsonObject().entrySet();
		String method = null;
		String uriTemplate = null;
		String uriMapping = null;
		String apiKey = null;
		JsonElement resourceElement = null;
		ESBMediatorPosition mediatorPosition = null;
		String sequenceType = null;
		for (Entry<String, JsonElement> apiEntry : entrySet) {
			JsonElement apiArtifactInfo = apiEntry.getValue();
			Set<Entry<String, JsonElement>> apiEntrySet = apiArtifactInfo
					.getAsJsonObject().entrySet();
			for (Entry<String, JsonElement> entry : apiEntrySet) {
				if (API_KEY.equalsIgnoreCase(entry.getKey())) {
					apiKey = formatJsonElementValueToString(entry.getValue());
				} else if (MEDIATOR_POSITION.equalsIgnoreCase(entry.getKey())) {
					mediatorPosition = convertMediatorPositionStringToList(formatJsonElementValueToString(entry
							.getValue()));
				} else if (SEQUENCE_TYPE.equalsIgnoreCase(entry.getKey())) {
					sequenceType = formatJsonElementValueToString(entry
							.getValue());
				} else if (RESOURCE.equalsIgnoreCase(entry.getKey())) {
					resourceElement = entry.getValue();
				}
			}

		}

		Set<Entry<String, JsonElement>> resourseEntrySet = resourceElement
				.getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> apiEntry : resourseEntrySet) {
			if (METHOD.equalsIgnoreCase(apiEntry.getKey())) {
				method = formatJsonElementValueToString(apiEntry.getValue());
			} else if (MAPPING_URL_TYPE.equalsIgnoreCase(apiEntry.getKey())) {
				uriMapping = formatJsonElementValueToString(apiEntry.getValue());
				uriTemplate = uriMapping;
			} else if (URI_TEMPLATE.equalsIgnoreCase(apiEntry.getKey())) {
				uriTemplate = formatJsonElementValueToString(apiEntry
						.getValue());
			} else if (URL_MAPPING.equalsIgnoreCase(apiEntry.getKey())) {
				uriMapping = formatJsonElementValueToString(apiEntry.getValue());
			}
		}

		ESBAPIResourceBean resource = new ESBAPIResourceBean(method,
				uriMapping, uriTemplate);

		ESBAPIBean api = new ESBAPIBean(apiKey, resource, sequenceType,
				mediatorPosition);
		sequence = new ESBAPISequenceBean(api);
	}

	public ESBAPISequenceBean getSequence() {
		return sequence;
	}

	public void setSequence(ESBAPISequenceBean sequence) {
		this.sequence = sequence;
	}

	public boolean equalsIgnoreType(ESBAPIDebugPointMessage debugPointMessage) {
		if (mediationComponent
				.equals(debugPointMessage.getMediationComponent())
				&& sequence.equals(debugPointMessage.getSequence())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object debugPointMessage) {
		if (debugPointMessage instanceof ESBAPIDebugPointMessage) {
			ESBAPIDebugPointMessage debugPointMessageTemp = (ESBAPIDebugPointMessage) debugPointMessage;
			if (!(mediationComponent.equals((debugPointMessageTemp)
					.getMediationComponent())
					&& commandArgument.equals((debugPointMessageTemp)
							.getCommandArgument()) && sequence
						.equals(debugPointMessageTemp.getSequence()))) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = INITIAL_HASHCODE_RESULT_VALUE;
		result = HASHCODE_MULTIPLIER_VALUE * result + sequence.hashCode()
				+ SEQUENCE.hashCode();
		result = HASHCODE_MULTIPLIER_VALUE * result
				+ commandArgument.hashCode() + COMMAND_ARGUMENT.hashCode();
		result = HASHCODE_MULTIPLIER_VALUE * result
				+ MEDIATION_COMPONENT.hashCode()
				+ mediationComponent.hashCode();
		return result;
	}

	@Override
	public ESBMediatorPosition getMediatorPosition() {
		return sequence.getApi().getMediatorPosition();
	}

	@Override
	public void setMediatorPosition(List<Integer> position) {
		sequence.getApi()
				.setMediatorPosition(new ESBMediatorPosition(position));
	}

	@Override
	public String getSequenceType() {
		return sequence.getApi().getSequenceType();
	}
}
