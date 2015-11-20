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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.DebugpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBAPIDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBProxyDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBSequenceDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBTemplateDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * {@link DebugPointRequest} represent the request event from
 * {@link ESBDebugTarget} to {@link ESBDebugger} when a
 * <code>ESBBreakpoint<code> adding or removing operation occurred by user
 *
 */
public class DebugPointRequest extends AbstractEvent implements IModelRequest {

	public enum DebugPointEventAction {
		ADDED("set"), REMOVED("clear");
		
		private final String action;

		private DebugPointEventAction(String actionValue) {
			action = actionValue;
		}

		public boolean equalsName(String comapreAction) {
			return (comapreAction == null) ? false : action
					.equals(comapreAction);
		}

		public String toString() {
			return this.action;
		}
	}

	private final DebugPointEventAction type;
	private final int lineNumber;
	private ESBDebugPointMessage debugPoint;

	public DebugPointRequest(ESBDebugPoint breakpoint,
			DebugPointEventAction action)
			throws DebugpointMarkerNotFoundException, CoreException {
		type = action;
		lineNumber = breakpoint.getLineNumber();
		debugPoint = setDebugPoint(breakpoint);

	}

	private ESBDebugPointMessage setDebugPoint(ESBDebugPoint breakpoint)
			throws DebugpointMarkerNotFoundException, CoreException {
		Map<String, Object> attributeSet = breakpoint.getLocation();
		String mediationComponent=(String) attributeSet.get(ESBDebuggerConstants.MEDIATION_COMPONENT);
		switch(mediationComponent){
		case ESBDebuggerConstants.PROXY:
			debugPoint = new ESBProxyDebugPointMessage(type,attributeSet);
			break;
		case ESBDebuggerConstants.MAIN_SEQUENCE:
		case ESBDebuggerConstants.SEQUENCE:
			debugPoint = new ESBSequenceDebugPointMessage(type,attributeSet);
			break;
		case ESBDebuggerConstants.API:
			debugPoint = new ESBAPIDebugPointMessage(type,attributeSet);
			break;
		case ESBDebuggerConstants.TEMPLATE:
			debugPoint = new ESBTemplateDebugPointMessage(type,attributeSet);
			break;
		}
		return debugPoint;
	}

	public DebugPointEventAction getType() {
		return type;
	}

	public int getLine() {
		return lineNumber;
	}

	public ESBDebugPointMessage getBreakpointAttributes() {
		return debugPoint;
	}

	@Override
	public String toString() {
		return "BreakpointEvent: "
				+ ((getType() == DebugPointEventAction.ADDED) ? "ADDED"
						: "REMOVED") + ", line : " + getLine()
				+ " , attributes : " + getBreakpointAttributes();
	}

	public ESBDebugPointMessage getDebugPoint() {
		return debugPoint;
	}
}
