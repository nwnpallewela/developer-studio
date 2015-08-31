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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.impl;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * Factory design pattern implementation to get BreakpointBuilder's according to
 * the type.
 *
 */
public class BreakpointBuilderFactory {

	/**
	 * This method takes type as a input and returns specific BreakpointBuilder
	 * 
	 * @param type
	 * @return BreakpointBuilder
	 */
	public static IESBBreakpointBuilder getBreakpointBuilder(String type) {
		
		String lowerCaseType = type.toLowerCase();
		
		switch (lowerCaseType) {
		case ESBDebuggerConstants.PROXY:
			return new ProxyBreakpointBuilder();
		case ESBDebuggerConstants.SEQUENCE:
			return new SequenceBreakpointBuilder();
		case ESBDebuggerConstants.TEMPLATE_SEQUENCE:
			return new TemplateBreakpointBuilder();
		case ESBDebuggerConstants.API:
			return new APIBreakpointBuilder();
		case ESBDebuggerConstants.CONNECTOR:
			return new ConnectorBreakpointBuilder();
		case ESBDebuggerConstants.MAIN_SEQUENCE:
			return new MainSequenceBreakpointBuilder();
		default:
			return null;
		}
	}
}
