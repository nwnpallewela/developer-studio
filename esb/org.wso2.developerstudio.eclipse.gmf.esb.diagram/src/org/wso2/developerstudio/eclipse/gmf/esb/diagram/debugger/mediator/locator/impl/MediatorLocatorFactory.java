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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.impl;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.IMediatorLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;


public class MediatorLocatorFactory {

public static IMediatorLocator getMediatorLocator(String type) {
		
		String lowerCaseType = type.toLowerCase();
		
		switch (lowerCaseType) {
		case ESBDebuggerConstants.PROXY:
			return new ProxyMediatorLocator();
		case ESBDebuggerConstants.SEQUENCE:
			return new SequenceMediatorLocator();
		case ESBDebuggerConstants.TEMPLATE_SEQUENCE:
			return new TemplateMediatorLocator();
		case ESBDebuggerConstants.API:
			return new APIMediatorLocator();
		case ESBDebuggerConstants.CONNECTOR:
			return new ConnectorMediatorLocator();
		case ESBDebuggerConstants.MAIN_SEQUENCE:
			return new MainSequenceMediatorLocator();
		default:
			return null;
		}
	}
}
