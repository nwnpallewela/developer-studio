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

import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;

/**
 * Factory design pattern implementation to get BreakpointBuilder's according to
 * the type.
 *
 */
public class ESBBreakpointBuilderFactory {

	/**
	 * This method takes project type as a input and returns specific
	 * BreakpointBuilder
	 * 
	 * @param type
	 * @return BreakpointBuilder
	 * @throws ESBDebuggerException
	 */
	public static IESBBreakpointBuilder getBreakpointBuilder(ArtifactType type)
			throws ESBDebuggerException {

		switch (type) {
		case PROXY:
			return new ProxyBreakpointBuilder();
		case SEQUENCE:
			return new SequenceBreakpointBuilder();
		case TEMPLATE_SEQUENCE:
			return new TemplateBreakpointBuilder();
		case API:
			return new APIBreakpointBuilder();
		case MAIN_SEQUENCE:
			return new MainSequenceBreakpointBuilder();
		default:
			throw new ESBDebuggerException(
					"Unsupported Project Type for Debugging");
		}
	}
}