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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.DebuggerCommunicationMessageModel;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.MessageAttribute;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class JsonJettisonMessageChannel implements IChannelCommunication {

	public static final String COMMAND_KEY = "command";
	private static final String KEY_VALUE_SEPERATOR = ":";
	private static final String ATTRIBUTE_SEPERATOR = ",";
	private static final String MESSAGE_SEPERATOR = "}";

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	@Override
	public String createCommand(String command) {
		try {
			JSONObject jsonCommand = new JSONObject();
			jsonCommand.put(COMMAND_KEY, command);
			return jsonCommand.toString();
		} catch (JSONException ex) {
			log.error("Error while creating Command JSON message", ex);
		}
		return null;

	}

	private JSONObject buildMessage(MessageAttribute messageModel,
			Map<String, String> attributeValues) throws JSONException {
		JSONObject jsonCommand = new JSONObject();
		Set<String> attributePropertyNames = messageModel.getAttributes();
		for (String propertyName : attributePropertyNames) {
			if (messageModel.getAttribute(propertyName) == null) {
				if (attributeValues.containsKey(propertyName)) {
					String value = attributeValues.get(propertyName);
					if (propertyName
							.equals(ESBDebuggerConstants.MEDIATION_COMPONENT)
							&& (value.equals(ESBDebuggerConstants.PROXY) || value
									.equals(ESBDebuggerConstants.API))) {
						jsonCommand.put(
								ESBDebuggerConstants.MEDIATION_COMPONENT,
								ESBDebuggerConstants.SEQUENCE);
					} else {
						jsonCommand.put(propertyName, value);
					}

				}
			} else {
				jsonCommand.put(
						propertyName,
						buildMessage(messageModel.getAttribute(propertyName),
								attributeValues));
			}
		}
		return jsonCommand;

	}

	@Override
	public Map<String, String> getResponce(String responce) {

		Map<String, String> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(responce);
			message = convertJsonToMap(responceMessage);
		} catch (JSONException e) {
			log.error("Error while creating Responce JSON message", e);
		}
		return message;
	}

	private Map<String, String> convertJsonToMap(JSONObject responceMessage) {
		Iterator<?> keys = responceMessage.keys();
		Map<String, String> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				value = responceMessage.getString(key);
			} catch (JSONException e) {
				log.error("Error while converting JSONToMap", e);
			}
			message.put(key, value);
		}

		return message;
	}

	private Map<String, String> convertJsonToMapFlat(JSONObject responceMessage) {
		Iterator<?> keys = responceMessage.keys();
		Map<String, String> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				value = responceMessage.getString(key);
				if (value.contains(MESSAGE_SEPERATOR) && value.startsWith("{")) {
					JSONObject insideMessage = new JSONObject(value);
					message.putAll(convertJsonToMapFlat(insideMessage));
				} else {
					message.put(key, value);
				}

			} catch (JSONException e) {
				log.error("Error while converting JSONToMap", e);
			}
		}
		return message;
	}

	@Override
	public Map<String, String> getEvent(String event) {
		Map<String, String> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(event);
			message = convertJsonToMapFlat(responceMessage);
		} catch (JSONException e) {
			log.error("Error while creating Event Map", e);
		}
		return message;
	}

	private String getBreakpointType(String string, String mediationComponent) {
		if (ESBDebuggerConstants.TEMPLATE.equals(mediationComponent)) {
			return ESBDebuggerConstants.TEMPLATE;
		} else if (ESBDebuggerConstants.CONNECTOR.equals(mediationComponent)) {
			return ESBDebuggerConstants.CONNECTOR;
		} else if (ESBDebuggerConstants.SEQUENCE.equals(mediationComponent)) {
			// if(string.contains(ESBDebuggerConstants.SEQUENCE+CHILD_ATTRIBUTE_SEPERATOR+ESBDebuggerConstants.PROXY)){
			if (string.contains(ESBDebuggerConstants.PROXY_KEY)) {
				return ESBDebuggerConstants.PROXY;
			} else if (string.contains(ESBDebuggerConstants.API_KEY)) {
				return ESBDebuggerConstants.API;
			} else {
				return ESBDebuggerConstants.SEQUENCE;
			}
		}
		return null;
	}

	@Override
	public String createGetPropertiesCommand(Map<String, String> attributeValues) {
		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getPropertyMessageModel(ESBDebuggerConstants.GET_PROPERTY);
		try {
			return buildMessage(messageModel, attributeValues).toString();
		} catch (JSONException e) {
			log.error("Error while building get properties command message", e);
		}
		return null;
	}

	@Override
	public String createBreakpointCommand(String operation, String type,
			Map<String, String> attributeValues) {
		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getMessageModel(ESBDebuggerConstants.BREAKPOINT, type);
		try {
			return buildMessage(messageModel, attributeValues).toString();
		} catch (JSONException e) {
			log.error("Error while creating Breakpoint Command JSON message", e);
		}
		return null;
	}

}
