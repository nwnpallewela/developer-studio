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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

public class ESBDebugPoint {

	protected String command;
	protected String commandArgument;
	protected String mediationComponent;

	public ESBDebugPoint(String command, String commandArgument,
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

	public Map<String, Object> deserializeToMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(COMMAND_ARGUMENT, commandArgument);
		attributeMap.put(MEDIATION_COMPONENT, mediationComponent);
		return attributeMap;
	}

	protected ESBMediatorPosition convertMediatorPositionStringToList(
			String position) {
		List<Integer> positionList = new ArrayList<>();
		String[] positionArray = position.split(" ");
		for (String positionValue : positionArray) {
			positionList.add(Integer.parseInt(positionValue));
		}
		return new ESBMediatorPosition(positionList);
	}

}
