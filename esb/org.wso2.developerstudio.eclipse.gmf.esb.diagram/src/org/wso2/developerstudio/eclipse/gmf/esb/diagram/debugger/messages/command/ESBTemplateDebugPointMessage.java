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

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.EventMessageType;
import com.google.gson.JsonElement;

/**
 * {@link ESBTemplateDebugPointMessage} is the bean class to represent ESB
 * Template artifacts debug point message from ESB Server Debugger
 *
 */
public class ESBTemplateDebugPointMessage extends AbstractESBDebugPointMessage {

	private ESBTemplateBean template;

	public ESBTemplateDebugPointMessage(String command,
			String commandArguement, String mediationComponent,
			ESBTemplateBean template) {
		super(command, commandArguement, mediationComponent);
		this.setTemplate(template);
	}


	public ESBTemplateDebugPointMessage(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		super(null, null, TEMPLATE);
		setCommandArgument(event.toString());
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


	public boolean equalsIgnoreType(
			ESBTemplateDebugPointMessage debugPointMessage) {
		if (mediationComponent
				.equals(debugPointMessage.getMediationComponent())
				&& template.equals(debugPointMessage.getTemplate())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object debugPointMessage) {
		if (debugPointMessage instanceof ESBTemplateDebugPointMessage) {
			ESBTemplateDebugPointMessage debugPointMessageTemp = (ESBTemplateDebugPointMessage) debugPointMessage;
			if (!(mediationComponent.equals((debugPointMessageTemp)
					.getMediationComponent())
					&& commandArgument.equals((debugPointMessageTemp)
							.getCommandArgument()) && template
						.equals(debugPointMessageTemp.getTemplate()))) {
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
		result = HASHCODE_MULTIPLIER_VALUE * result
				+ mediationComponent.hashCode()
				+ MEDIATION_COMPONENT.hashCode();
		result = HASHCODE_MULTIPLIER_VALUE * result
				+ commandArgument.hashCode() + COMMAND_ARGUMENT.hashCode();
		result = HASHCODE_MULTIPLIER_VALUE * result + template.hashCode()
				+ TEMPLATE.hashCode();
		return result;
	}

	@Override
	public ESBMediatorPosition getMediatorPosition() {
		return template.getMediatorPosition();
	}

	@Override
	public void setMediatorPosition(List<Integer> positionList) {
		template.setMediatorPosition(new ESBMediatorPosition(positionList));
	}

	@Override
	public String getSequenceType() {
		return "";
	}


}
