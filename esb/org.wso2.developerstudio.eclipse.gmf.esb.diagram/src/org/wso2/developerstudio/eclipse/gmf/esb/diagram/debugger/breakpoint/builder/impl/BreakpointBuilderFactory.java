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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * Factory design pattern implementation to get BreakpointBuilder's according to
 * the type.
 *
 */
public class BreakpointBuilderFactory {

	public enum ProjectType {
		PROXY, SEQUENCE, TEMPLATE_SEQUENCE, API, MAIN_SEQUENCE, CONNECTOR;

		public static ProjectType getEnumProjectType(String lowerCaseType) {

			switch (lowerCaseType) {
			case ESBDebuggerConstants.PROXY:
				return ProjectType.PROXY;
			case ESBDebuggerConstants.SEQUENCE:
				return ProjectType.SEQUENCE;
			case ESBDebuggerConstants.TEMPLATE_SEQUENCE:
				return ProjectType.TEMPLATE_SEQUENCE;
			case ESBDebuggerConstants.API:
				return ProjectType.API;
			case ESBDebuggerConstants.CONNECTOR:
				return ProjectType.CONNECTOR;
			case ESBDebuggerConstants.MAIN_SEQUENCE:
				return ProjectType.MAIN_SEQUENCE;
			default:
				return null;
			}
		}
	}

	/**
	 * This method takes type as a input and returns specific BreakpointBuilder
	 * 
	 * @param type
	 * @return BreakpointBuilder
	 */
	public static IESBBreakpointBuilder getBreakpointBuilder(String type) {

		ProjectType projectType = ProjectType.getEnumProjectType(type
				.toLowerCase());

		switch (projectType) {
		case PROXY:
			return new ProxyBreakpointBuilder();
		case SEQUENCE:
			return new SequenceBreakpointBuilder();
		case TEMPLATE_SEQUENCE:
			return new TemplateBreakpointBuilder();
		case API:
			return new APIBreakpointBuilder();
		case CONNECTOR:
			return new ConnectorBreakpointBuilder();
		case MAIN_SEQUENCE:
			return new MainSequenceBreakpointBuilder();
		default:
			return null;
		}
	}
}
