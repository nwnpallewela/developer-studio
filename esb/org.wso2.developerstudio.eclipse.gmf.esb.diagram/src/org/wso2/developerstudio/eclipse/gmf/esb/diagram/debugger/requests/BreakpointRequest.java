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

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.model.IBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;

public class BreakpointRequest extends AbstractEvent implements IModelRequest {

	public static final int ADDED = 1;
	public static final int REMOVED = 2;
	private final int mType;
	private final int mLine;
	private final String mMessage;
	

	public BreakpointRequest(IBreakpoint breakpoint, int type) {
		mType = type;
		mLine = breakpoint.getMarker().getAttribute(IMarker.LINE_NUMBER, -1);
		mMessage = breakpoint.getMarker().getAttribute(IMarker.MESSAGE, "");
	}

	public int getType() {
		return mType;
	}

	public int getLine() {
		return mLine;
	}
	
	public String getMessage() {
		return mMessage;
	}

	@Override
	public String toString() {
		return "BreakpointEvent: " + ((getType() == ADDED) ? "ADDED" : "REMOVED") + ", line : " + getLine()+" , message : "+getMessage();
	}
}
