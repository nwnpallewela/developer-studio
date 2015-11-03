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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events;

import java.util.Map;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebuggerEvent;

/**
 * {@link VariablesEvent} represent the request event from {@link ESBDebugger}
 * to {@link ESBDebugTarget} when {@link ESBDebugger} notify new property
 * variables are received from ESB Server
 */
public class VariablesEvent extends AbstractEvent implements IDebuggerEvent {

	private final Map<String, String> mVariables;

	public VariablesEvent(Map<String, String> variables) {
		mVariables = variables;
	}

	public Map<String, String> getVariables() {
		return mVariables;
	}
}
