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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception;

/**
 * {@link BreakpointMarkerNotFoundException} throws when a Breakpoint does not
 * contain a designated @ IMarker} with it.
 */
public class BreakpointMarkerNotFoundException extends ESBDebuggerException {

	private static final long serialVersionUID = 5253296690787550656L;

	public BreakpointMarkerNotFoundException() {
	}

	public BreakpointMarkerNotFoundException(String message) {
		super(message);
	}

	public BreakpointMarkerNotFoundException(Throwable cause) {
		super(cause);
	}

	public BreakpointMarkerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
