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

import com.google.gson.JsonElement;

/**
 * {@link ESBTemplateDebugPointMessage} is the bean class to represent ESB
 * Template artifacts debug point message from ESB Server Debugger
 *
 */
public class ESBTemplateDebugPointMessage extends ESBDebugPointMessage {

	private ESBTemplateBean template;

	public ESBTemplateDebugPointMessage(String command, String commandArgument,
			String mediationComponent, ESBTemplateBean template) {
		super(command, commandArgument, mediationComponent);
		this.setTemplate(template);
	}

	public ESBTemplateDebugPointMessage(DebugPointEventAction action,
			Map<String, Object> attributeSet) {
		super(action.toString(), null, TEMPLATE);
		setCommandArgument((String) attributeSet.get(COMMAND_ARGUMENT));
		String templateKey = (String) attributeSet.get(TEMPLATE_KEY);
		@SuppressWarnings("unchecked")
		ESBMediatorPosition mediatorPosition = new ESBMediatorPosition(
				(List<Integer>) attributeSet.get(MEDIATOR_POSITION));

		template = new ESBTemplateBean(templateKey, mediatorPosition);
	}

	public ESBTemplateDebugPointMessage(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		super(null, null, TEMPLATE);
		setCommandArgument(event.toString().replace(QUOTATION_MARK_STRING,
				EMPTY_STRING));
		Set<Entry<String, JsonElement>> entrySet = recievedArtifactInfo
				.getAsJsonObject().entrySet();
		String templateKey = null;
		ESBMediatorPosition mediatorPosition = null;
		for (Entry<String, JsonElement> entry : entrySet) {
			if (TEMPLATE_KEY.equalsIgnoreCase(entry.getKey())) {
				templateKey = formatJsonElementValueToString(entry.getValue());
			} else if (MEDIATOR_POSITION.equalsIgnoreCase(entry.getKey())) {
				mediatorPosition = convertMediatorPositionStringToList(formatJsonElementValueToString(entry
						.getValue()));
			}
		}
		template = new ESBTemplateBean(templateKey, mediatorPosition);
	}

	public ESBTemplateBean getTemplate() {
		return template;
	}

	public void setTemplate(ESBTemplateBean template) {
		this.template = template;
	}

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.putAll(super.deserializeToMap());
		attributeMap.putAll(template.deserializeToMap());
		return attributeMap;
	}

}