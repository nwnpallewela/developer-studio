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
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.DebuggerCommunicationMessageModel;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.MessageAttribute;

public class JsonJettisonMessageChannel implements IChannelCommunication {
	
	public static final String COMMAND_KEY = "command";

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
	public String createBreakpointCommand(String operation, String type, Map<String, String> attributeValues) {
		
		MessageAttribute messageModel = DebuggerCommunicationMessageModel.getMessageModel(ESBDebuggerConstants.BREAKPOINT, type);
		try {
			return buildMessage(messageModel,attributeValues).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject buildMessage(MessageAttribute messageModel, Map<String, String> attributeValues) throws JSONException {
		JSONObject jsonCommand = new JSONObject();
		Set<String> attributePropertyNames = messageModel.getAttributes();
		for (String propertyName : attributePropertyNames) {
			if (messageModel.getAttribute(propertyName) == null) {
				if (attributeValues.containsKey(propertyName)) {
					jsonCommand.put(propertyName, attributeValues.get(propertyName));
				}
			} else{
				jsonCommand.put(propertyName, buildMessage(messageModel.getAttribute(propertyName),attributeValues));
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
	        while( keys.hasNext() ){
	            String key = (String)keys.next();
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public String createGetPropertiesCommand(Map<String, String> attributeValues) {
		MessageAttribute messageModel = DebuggerCommunicationMessageModel.getPropertyMessageModel(ESBDebuggerConstants.GET_PROPERTY);
		try {
			return buildMessage(messageModel,attributeValues).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
