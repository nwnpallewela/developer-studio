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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class DebuggerCommunicationMessageModel {

	private static Map<String, MessageAttribute> messageModels;
	private static MessageAttribute breakpointRoot;

	private DebuggerCommunicationMessageModel() {
	}
	
	public static void populateMessageModels(){
		if(messageModels == null){
		messageModels = new LinkedHashMap<String, MessageAttribute>();
		breakpointRoot = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		addBreakpointModels();
		addGetContextPropertyModel();
		addChangeContextPropertyModel();
		addResumeModel();
		}
	}
	private static void addGetContextPropertyModel() {
		MessageAttribute structure = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		structure.put(ESBDebuggerConstants.COMMAND, null);
		structure.put(ESBDebuggerConstants.COMMAND_ARGUMENT, null);
		structure.put(ESBDebuggerConstants.CONTEXT, null);
		MessageAttribute propertyAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		propertyAttribute.put(ESBDebuggerConstants.PROPERTY_NAME, null);
		structure.put(ESBDebuggerConstants.PROPERTY, propertyAttribute);
		messageModels.put(ESBDebuggerConstants.GET_PROPERTY, structure);
	}
	
	private static void addChangeContextPropertyModel() {
		MessageAttribute structure = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		structure.put(ESBDebuggerConstants.COMMAND, null);
		structure.put(ESBDebuggerConstants.COMMAND_ARGUMENT, null);
		structure.put(ESBDebuggerConstants.CONTEXT, null);
		MessageAttribute propertyAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		propertyAttribute.put(ESBDebuggerConstants.PROPERTY_NAME, null);
		propertyAttribute.put(ESBDebuggerConstants.PROPERTY_VALUE, null);
		structure.put(ESBDebuggerConstants.PROPERTY, propertyAttribute);
		messageModels.put(ESBDebuggerConstants.CHANGE_PROPERTY, structure);
	}
	
	private static void addResumeModel() {
		MessageAttribute structure = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		structure.put(ESBDebuggerConstants.COMMAND, null);
		messageModels.put(ESBDebuggerConstants.RESUME, structure);
	}
	
	
	private static void addBreakpointModels() {
		addBreakpointModel(ESBDebuggerConstants.SEQUENCE);
		addBreakpointModel(ESBDebuggerConstants.TEMPLATE);
		addBreakpointModel(ESBDebuggerConstants.CONNECTOR);
		addBreakpointModel(ESBDebuggerConstants.PROXY);
		addBreakpointModel(ESBDebuggerConstants.API);
	}

	private static void addBreakpointModel(String type) {

		MessageAttribute structure = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		structure.put(ESBDebuggerConstants.COMMAND, null);
		structure.put(ESBDebuggerConstants.COMMAND_ARGUMENT, null);
		structure.put(ESBDebuggerConstants.MEDIATION_COMPONENT, null);
		MessageAttribute specifiedAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
		switch (type) {
		case ESBDebuggerConstants.SEQUENCE:
			specifiedAttribute.put(ESBDebuggerConstants.SEQUENCE_KEY, null);
			specifiedAttribute.put(ESBDebuggerConstants.SEQUENCE_TYPE, null);
			specifiedAttribute.put(ESBDebuggerConstants.MEDIATOR_POSITION, null);
			structure.put(ESBDebuggerConstants.SEQUENCE, specifiedAttribute);
			break;
		case ESBDebuggerConstants.TEMPLATE:
			specifiedAttribute.put(ESBDebuggerConstants.TEMPLATE_KEY, null);
			specifiedAttribute.put(ESBDebuggerConstants.MEDIATOR_POSITION, null);
			structure.put(ESBDebuggerConstants.TEMPLATE, specifiedAttribute);
			break;
		case ESBDebuggerConstants.CONNECTOR:
			specifiedAttribute.put(ESBDebuggerConstants.CONNECTOR_KEY, null);
			specifiedAttribute.put(ESBDebuggerConstants.METHOD_NAME, null);
			specifiedAttribute.put(ESBDebuggerConstants.MEDIATOR_POSITION, null);
			structure.put(ESBDebuggerConstants.CONNECTOR, specifiedAttribute);
			break;
		case ESBDebuggerConstants.PROXY:
			MessageAttribute proxyAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
			proxyAttribute.put(ESBDebuggerConstants.PROXY_KEY, null);
			proxyAttribute.put(ESBDebuggerConstants.SEQUENCE_TYPE, null);
			proxyAttribute.put(ESBDebuggerConstants.MEDIATOR_POSITION, null);
			specifiedAttribute.put(ESBDebuggerConstants.PROXY, proxyAttribute);
			structure.put(ESBDebuggerConstants.SEQUENCE, specifiedAttribute);
			break;
		case ESBDebuggerConstants.API:
			MessageAttribute apiAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
			MessageAttribute resourceAttribute = new MessageAttribute(new LinkedHashMap<String, MessageAttribute>());
			apiAttribute.put(ESBDebuggerConstants.API_KEY, null);
			resourceAttribute.put(ESBDebuggerConstants.METHOD, null);
			resourceAttribute.put(ESBDebuggerConstants.URI_MAPPING, null);
			resourceAttribute.put(ESBDebuggerConstants.URL_TEMPLATE, null);
			apiAttribute.put(ESBDebuggerConstants.RESOURCE, resourceAttribute);
			apiAttribute.put(ESBDebuggerConstants.SEQUENCE_TYPE, null);
			apiAttribute.put(ESBDebuggerConstants.MEDIATOR_POSITION, null);
			specifiedAttribute.put(ESBDebuggerConstants.API, apiAttribute);
			structure.put(ESBDebuggerConstants.SEQUENCE, specifiedAttribute);
			break;
		default:
			break;
		}

		breakpointRoot.put(type, structure);
		messageModels.put(ESBDebuggerConstants.BREAKPOINT, breakpointRoot);
	}

	public static MessageAttribute getMessageModel(String operation, String type) {
		return messageModels.get(operation).getAttribute(type);
	}
	public static MessageAttribute getPropertyMessageModel(String operation) {
		return messageModels.get(operation);
	}
}
