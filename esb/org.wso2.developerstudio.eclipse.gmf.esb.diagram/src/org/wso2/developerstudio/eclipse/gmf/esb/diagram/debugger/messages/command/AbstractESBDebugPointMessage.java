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

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

/**
 * {@link AbstractESBDebugPointMessage} is the root bean class to represent ESB
 * artifacts debug point message from ESB Server Debugger. Every artifact debug
 * point message bean class should extend this class
 *
 */
public abstract class AbstractESBDebugPointMessage {

	protected static final CharSequence EMPTY_STRING = "";
	private static final String SPACE_STRING = " ";

	protected String command;
	protected String commandArgument;
	protected String mediationComponent;

	public AbstractESBDebugPointMessage(String command, String commandArgument,
			String mediationComponent) {
		super();
		this.command = command;
		this.commandArgument = commandArgument;
		this.mediationComponent = mediationComponent;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommandArgument() {
		return commandArgument;
	}

	public void setCommandArgument(String commandArgument) {
		this.commandArgument = commandArgument;
	}

	public String getMediationComponent() {
		return mediationComponent;
	}

	public void setMediationComponent(String mediationComponent) {
		this.mediationComponent = mediationComponent;
	}



	protected ESBMediatorPosition convertMediatorPositionStringToList(
			String position) {
		List<Integer> positionList = new ArrayList<>();
		String[] positionArray = position.trim().split(SPACE_STRING);
		for (String positionValue : positionArray) {
			positionList.add(Integer.parseInt(positionValue));
		}
		return new ESBMediatorPosition(positionList);
	}

	protected String formatJsonElementValueToString(JsonElement value) {
		return value.getAsString();

	}

	public boolean equalsIgnoreType(AbstractESBDebugPointMessage debugPointMessage) {
		if (mediationComponent
				.equals(debugPointMessage.getMediationComponent())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object debugPointMessage) {
		if (debugPointMessage instanceof AbstractESBDebugPointMessage) {
			AbstractESBDebugPointMessage debugPointMessageTemp = (AbstractESBDebugPointMessage) debugPointMessage;
			if (!(mediationComponent.equals((debugPointMessageTemp)
					.getMediationComponent()) && commandArgument
					.equals((debugPointMessageTemp).getCommandArgument()))) {
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
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public abstract ESBMediatorPosition getMediatorPosition();

	/**
	 * 
	 * @return
	 */
	public abstract void setMediatorPosition(List<Integer> position);

	/**
	 * 
	 */
	public abstract String getSequenceType();

}
