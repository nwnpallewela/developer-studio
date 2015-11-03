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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;

/**
 * {@link BreakpointRequest} represent the request event from
 * {@link ESBDebugTarget} to {@link ESBDebugger} when a
 * <code>ESBBreakpoint<code> adding or removing operation occurred by user
 *
 */
public class BreakpointRequest extends AbstractEvent implements IModelRequest {

	public enum BreakpointEventAction {
		ADDED, REMOVED, MODIFIED
	}

	private final BreakpointEventAction mType;
	private final int mLine;
	private final Map<String, Object> breakpointAttributes;

	public BreakpointRequest(ESBBreakpoint breakpoint,
			BreakpointEventAction action)
			throws BreakpointMarkerNotFoundException, CoreException {
		mType = action;
		mLine = breakpoint.getLineNumber();
		breakpointAttributes = breakpoint.getLocation();
	}

	public BreakpointEventAction getType() {
		return mType;
	}

	public int getLine() {
		return mLine;
	}

	public Map<String, Object> getBreakpointAttributes() {
		return breakpointAttributes;
	}

	@Override
	public String toString() {
		return "BreakpointEvent: "
				+ ((getType() == BreakpointEventAction.ADDED) ? "ADDED"
						: "REMOVED") + ", line : " + getLine()
				+ " , attributes : " + getBreakpointAttributes();
	}
}
