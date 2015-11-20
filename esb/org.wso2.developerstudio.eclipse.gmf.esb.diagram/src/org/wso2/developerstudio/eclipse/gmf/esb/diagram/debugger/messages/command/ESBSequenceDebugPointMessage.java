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
 * {@link ESBSequenceDebugPointMessage} is the bean class to represent ESB
 * Sequence artifacts debug point message from ESB Server Debugger
 *
 */
public class ESBSequenceDebugPointMessage extends ESBDebugPointMessage {

	private ESBSequenceBean sequence;

	public ESBSequenceDebugPointMessage(String command, String commandArgument,
			String mediationComponent, ESBSequenceBean sequence) {
		super(command, commandArgument, mediationComponent);
		this.sequence = sequence;
	}

	public ESBSequenceDebugPointMessage(DebugPointEventAction action,
			Map<String, Object> attributeSet) {
		super(action.toString(), null, ESBDebuggerConstants.SEQUENCE);
		setCommandArgument((String) attributeSet
				.get(ESBDebuggerConstants.COMMAND_ARGUMENT));
		String sequenceKey = (String) attributeSet
				.get(ESBDebuggerConstants.SEQUENCE_KEY);
		String sequenceType = (String) attributeSet
				.get(ESBDebuggerConstants.SEQUENCE_TYPE);
		@SuppressWarnings("unchecked")
		ESBMediatorPosition mediatorPosition = new ESBMediatorPosition(
				(List<Integer>) attributeSet
						.get(ESBDebuggerConstants.MEDIATOR_POSITION));

		sequence = new ESBSequenceBean(sequenceType, sequenceKey,
				mediatorPosition);
	}

	public ESBSequenceDebugPointMessage(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		super(null, null, SEQUENCE);
		setCommandArgument(event.toString().replace(QUOTATION_MARK_STRING,
				EMPTY_STRING));
		Set<Entry<String, JsonElement>> entrySet = recievedArtifactInfo
				.getAsJsonObject().entrySet();
		String sequenceKey = null;
		String sequenceType = null;
		ESBMediatorPosition mediatorPosition = null;
		for (Entry<String, JsonElement> entry : entrySet) {
			if (SEQUENCE_KEY.equalsIgnoreCase(entry.getKey())) {
				sequenceKey = formatJsonElementValueToString(entry.getValue());
			} else if (SEQUENCE_TYPE.equalsIgnoreCase(entry.getKey())) {
				sequenceType = formatJsonElementValueToString(entry.getValue());
			} else if (MEDIATOR_POSITION.equalsIgnoreCase(entry.getKey())) {
				mediatorPosition = convertMediatorPositionStringToList(formatJsonElementValueToString(entry
						.getValue()));
			}
		}
		sequence = new ESBSequenceBean(sequenceType, sequenceKey,
				mediatorPosition);
	}

	public ESBSequenceBean getSequence() {
		return sequence;
	}

	public void setSequence(ESBSequenceBean sequence) {
		this.sequence = sequence;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.putAll(super.deserializeToMap());
		attributeMap.putAll(sequence.deserializeToMap());
		return attributeMap;
	}

}
