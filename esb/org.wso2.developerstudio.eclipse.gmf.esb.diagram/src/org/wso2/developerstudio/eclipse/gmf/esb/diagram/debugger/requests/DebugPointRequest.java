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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.DebugPointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBAPIDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.AbstractESBDebugPointMessage;
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
	private AbstractESBDebugPointMessage debugPoint;

	public DebugPointRequest(ESBDebugPoint debugPoint,
			DebugPointEventAction action)
			throws DebugPointMarkerNotFoundException, CoreException {
		type = action;
		lineNumber = debugPoint.getLineNumber();
		this.debugPoint = debugPoint.getLocation();
	}

	public DebugPointEventAction getType() {
		return type;
	}

	public int getLine() {
		return lineNumber;
	}

	public AbstractESBDebugPointMessage getBreakpointAttributes() {
		return debugPoint;
	}

	@Override
	public String toString() {
		return "BreakpointEvent: "
				+ ((getType() == DebugPointEventAction.ADDED) ? "ADDED"
						: "REMOVED") + ", line : " + getLine()
				+ " , attributes : " + getBreakpointAttributes();
	}

	public AbstractESBDebugPointMessage getDebugPoint() {
		return debugPoint;
	}
}
