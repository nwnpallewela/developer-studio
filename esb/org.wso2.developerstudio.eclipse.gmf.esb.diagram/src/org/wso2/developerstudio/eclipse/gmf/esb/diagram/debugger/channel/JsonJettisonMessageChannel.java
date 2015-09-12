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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.DebuggerCommunicationMessageModel;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.MessageAttribute;

public class JsonJettisonMessageChannel implements IChannelCommunication {

	public static final String COMMAND_KEY = "command";
	private static final String KEY_VALUE_SEPERATOR = ":";
	private static final String ATTRIBUTE_SEPERATOR = ",";
	private static final String MESSAGE_SEPERATOR = "}";

	@Override
	public String createCommand(String command) {
		try {
			JSONObject jsonCommand = new JSONObject();
			jsonCommand.put(COMMAND_KEY, command);
			return jsonCommand.toString();
		} catch (Exception ex) {
			// TODO Handle Exception
		}
		return null;

	}

	/**
	 * 
	 * 
	 */
	@Override
	public String createBreakpointCommand(String operation, String type,
			Map<String, String> attributeValues) {

		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getMessageModel(ESBDebuggerConstants.BREAKPOINT, type);
		try {
			return buildMessage(messageModel, attributeValues).toString();
		} catch (JSONException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
				message.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return message;
	}

	@Override
	public Map<String, String> getEvent(String event) {
		Map<String, String> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(event);
			message = convertJsonToMap(responceMessage);
			if (ESBDebuggerConstants.BREAKPOINT.equals(message
					.get(ESBDebuggerConstants.EVENT))) {

				String mediationComponent = message
						.get(ESBDebuggerConstants.MEDIATION_COMPONENT);
				 Map<String, String> attributes = (getAttributeValuesFromEvent(
						message.get(mediationComponent), mediationComponent));
				 Set<String> keys = attributes.keySet();
				 for (String key : keys) {
					message.put(key, attributes.get(key));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message;
	}

	private Map<String, String> getAttributeValuesFromEvent(String string,
			String mediationComponent) {
		Map<String, String> attributes = new LinkedHashMap<>();
		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getMessageModel(ESBDebuggerConstants.BREAKPOINT,
						getBreakpointType(string, mediationComponent));
		 ArrayList<String> keys = messageModel.getAttributeKeys();
		for (String key : keys) {
			if(messageModel.getAttribute(key)==null && hasValueForKey(key,string)){
				
				attributes.put(key, getValueForKey(key,string));
			}
		}
		return attributes;

	}

	private boolean hasValueForKey(String key, String string) {
		return string.contains(key);
	}

	private String getValueForKey(String key, String string) {
		int indexOfKey = string.indexOf(key);
		int indexOfValueBegining = string.indexOf(KEY_VALUE_SEPERATOR,
				indexOfKey);
		int indexOfValueEnd = string.indexOf(ATTRIBUTE_SEPERATOR,
				indexOfValueBegining);
		int indexOfMessageObjectEnd = string.indexOf(MESSAGE_SEPERATOR,
				indexOfValueBegining);
		String value="";
		if (indexOfValueEnd < 0) {
			value=string.substring(indexOfValueBegining + 2, indexOfMessageObjectEnd - 2);
		} else {
			if(indexOfValueEnd<indexOfMessageObjectEnd){
				value=string.substring(indexOfValueBegining + 2, indexOfValueEnd - 1);
			}else{
				value=string.substring(indexOfValueBegining + 2, indexOfMessageObjectEnd - 2);
			}
		}
		return value;
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
			e.printStackTrace();
		}
		return null;
	}
}
