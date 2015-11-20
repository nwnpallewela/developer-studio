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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger.ESBDebuggerCommands;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.CommandMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.GetPropertyCommand;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.IEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.IResponseMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * {@link JsonJettisonMessageFactory} has the implementation of functions for
 * communication channel message conversion between ESB Debugger and ESB Server
 *
 */
public class JsonJettisonMessageFactory implements ICommunicationMessageFactory {

	public String createCommand(ESBDebuggerCommands command)
			throws JSONException {
		try {
			JSONObject jsonCommand = new JSONObject();
			jsonCommand.put(ESBDebuggerConstants.COMMAND, command.toString());
			return jsonCommand.toString();
		} catch (JSONException ex) {
			throw new JSONException("Error while creating Command JSON message");
		}
	}

	/*@Override
	public Map<String, Object> convertResponseMessageToMap(String response)
			throws JSONException {

		Map<String, Object> convertedResponseMap = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(response);
			convertedResponseMap = convertJsonResponseToMap(responceMessage);
		} catch (JSONException e) {
			throw new JSONException(
					"Error while converting Response JSON message");
		}
		return convertedResponseMap;
	}

	@Override
	public Map<String, Object> convertEventMessageToMap(String event)
			throws JSONException {
		Map<String, Object> message = new LinkedHashMap<>();
		try {
			JSONObject responceMessage = new JSONObject(event);
			message = convertJsonToMap(responceMessage);
		} catch (JSONException e) {
			throw new JSONException("Error while creating Event Map");
		} catch (NumberFormatException e) {
			throw new NumberFormatException(
					"Recieved Event Message have a invalid Mediator Position value");
		}
		return message;
	}

	@Override
	public String createGetPropertiesCommand(Map<String, Object> attributeValues)
			throws JSONException {
		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getMessageModel(ESBDebuggerConstants.GET_PROPERTY);
		try {
			return buildMessage(messageModel, attributeValues).toString();
		} catch (JSONException e) {
			throw new JSONException(
					"Error while building get properties command message");
		}
	}

	@Override
	public String createBreakpointCommand(Map<String, Object> attributeValues)
			throws JSONException {
		String type = (String) attributeValues
				.get(ESBDebuggerConstants.MEDIATION_COMPONENT);
		MessageAttribute messageModel = DebuggerCommunicationMessageModel
				.getMessageModel(ESBDebuggerConstants.BREAKPOINT, type);
		try {
			return buildMessage(messageModel, attributeValues).toString();
		} catch (JSONException e) {
			throw new JSONException(
					"Error while creating Breakpoint Command JSON message");
		}
	}*/

/*	*//**
	 * This method build communication channel message to send to ESB Server
	 * Debugger
	 * 
	 * @param messageModel
	 * @param attributeValues
	 * @return
	 * @throws JSONException
	 *//*
	private JSONObject buildMessage(MessageAttribute messageModel,
			Map<String, Object> attributeValues) throws JSONException {
		JSONObject jsonCommand = new JSONObject();
		Set<String> attributePropertyNames = messageModel.getAttributeKeySet();
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
				JSONObject innerMessage = buildMessage(
						messageModel.getAttribute(propertyName),
						attributeValues);
				if (innerMessage.keys().hasNext()) {
					jsonCommand.put(propertyName, innerMessage);
				}
			}
		}
		return jsonCommand;

	}
	
	private Map<String, Object> convertJsonResponseToMap(JSONObject responceMessage) throws JSONException {
		Iterator<?> keys = responceMessage.keys();
		Map<String, Object> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
				value = responceMessage.getString(key);
			message.put(key, value);
		}

		return message;
	}

	*//**
	 * This method converts a JSON message to a Map with all keys as String
	 * values
	 * 
	 * @param responseMessage
	 * @return
	 * @throws JSONException
	 *//*
	private Map<String, Object> convertJsonToMap(JSONObject responseMessage)
			throws JSONException {
		Iterator<?> keys = responseMessage.keys();
		Map<String, Object> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			value = responseMessage.getString(key);
			if (value.contains(MESSAGE_SEPERATOR)
					&& value.startsWith(JSON_MESSAGE_PREFIX)) {
				JSONObject insideMessage = new JSONObject(value);
				message.putAll(convertJsonToMap(insideMessage));
			} else {
				if (ESBDebuggerConstants.MEDIATOR_POSITION
						.equalsIgnoreCase(key)) {
					String[] positionArray = value.trim().split(SPACE_STRING);
					List<Integer> position = new ArrayList<>();
					for (String positionValue : positionArray) {
						position.add(Integer.parseInt(positionValue));
					}
					message.put(key, position);
				} else {
					message.put(key, value);
				}
			}
		}
		return message;
	}*/

	@Override
	public String createBreakpointCommand(ESBDebugPointMessage debugPoint)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createCommand(CommandMessage command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createGetPropertiesCommand(
			GetPropertyCommand getPropertyCommand) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponseMessage convertResponseToIResponseMessage(String response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventMessage convertEventToIEventMessage(String buffer)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
