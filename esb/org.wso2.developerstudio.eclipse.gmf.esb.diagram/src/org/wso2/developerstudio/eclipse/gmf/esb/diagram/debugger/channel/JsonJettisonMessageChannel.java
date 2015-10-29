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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
	private static final String MESSAGE_SEPERATOR = "}";
	private static final String SPACE_STRING = " ";
	private static final String JSON_MESSAGE_PREFIX = "{";

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
			Map<String, Object> attributeValues) throws JSONException {
		JSONObject jsonCommand = new JSONObject();
		Set<String> attributePropertyNames = messageModel.getAttributes();
		for (String propertyName : attributePropertyNames) {
			if (messageModel.getAttribute(propertyName) == null) {
				if (attributeValues.containsKey(propertyName)) {
					if (ESBDebuggerConstants.MEDIATOR_POSITION
							.equalsIgnoreCase(propertyName)) {
						@SuppressWarnings("unchecked")
						List<Integer> position = (List<Integer>) attributeValues
								.get(ESBDebuggerConstants.MEDIATOR_POSITION);
						StringBuilder builder = new StringBuilder();
						for (int value : position) {
							builder.append(value).append(SPACE_STRING);
						}
						jsonCommand.put(ESBDebuggerConstants.MEDIATOR_POSITION,
								builder.toString().trim());
					} else {
						String value = (String) attributeValues
								.get(propertyName);
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
	public Map<String, Object> getResponce(String responce) {

		Map<String, Object> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(responce);
			message = convertJsonToMap(responceMessage);
		} catch (JSONException e) {
			log.error("Error while creating Responce JSON message", e);
		}
		return message;
	}

	private Map<String, Object> convertJsonToMap(JSONObject responceMessage) {
		Iterator<?> keys = responceMessage.keys();
		Map<String, Object> message = new LinkedHashMap<>();
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

	private Map<String, Object> convertJsonToMapFlat(JSONObject responceMessage) {
		Iterator<?> keys = responceMessage.keys();
		Map<String, Object> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				value = responceMessage.getString(key);
				if (value.contains(MESSAGE_SEPERATOR)
						&& value.startsWith(JSON_MESSAGE_PREFIX)) {
					JSONObject insideMessage = new JSONObject(value);
					message.putAll(convertJsonToMapFlat(insideMessage));
				} else {
					if (ESBDebuggerConstants.MEDIATOR_POSITION
							.equalsIgnoreCase(key)) {
						String[] positionArray = value.trim().split(
								SPACE_STRING);
						int[] position = new int[positionArray.length];
						int count = 0;
						for (String positionValue : positionArray) {
							position[count] = Integer.parseInt(positionValue);
						}
						message.put(key, position);
					} else {
						message.put(key, value);
					}
				}

			} catch (JSONException e) {
				log.error("Error while converting JSONToMap", e);
			}
		}
		return message;
	}

	@Override
	public Map<String, Object> getEvent(String event) {
		Map<String, Object> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(event);
			message = convertJsonToMapFlat(responceMessage);
		} catch (JSONException e) {
			log.error("Error while creating Event Map", e);
		} catch (NumberFormatException e) {
			log.error(
					"Recieved Event Message have a invalid Mediator Position value",
					e);
		}
		return message;
	}

	@Override
	public String createGetPropertiesCommand(Map<String, Object> attributeValues) {
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
			Map<String, Object> attributeValues) {
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
