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

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.CommandMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBAPIDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBMediatorPosition;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBProxyDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBSequenceDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBTemplateDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.GetPropertyCommand;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.DebugPointEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.EventMessageType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.GeneralEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.IEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.SpecialCordinationEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.CommandResponseMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.IResponseMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.PropertyRespondMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonGsonMessageFactory implements ICommunicationMessageFactory {

	@Override
	public String createCommand(CommandMessage command) {
		Gson commandMessage = new Gson();
		String message = commandMessage.toJson(command);
		return message;
	}

	@Override
	public String createGetPropertiesCommand(GetPropertyCommand command) {

		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingStrategy(new PojoToGsonCustomNamingStrategy());
		Gson propertyCommandMessage = builder.create();
		return propertyCommandMessage.toJson(command);
	}

	@Override
	public IResponseMessage convertResponseMessageToMap(String response) {
		JsonElement jasonElement = new JsonParser().parse(response);
		Set<Entry<String, JsonElement>> entrySet = jasonElement
				.getAsJsonObject().entrySet();
		String commandResponse = null;
		String failedReason = null;
		String scope = null;
		JsonElement propertyValues = null;
		for (Entry<String, JsonElement> entry : entrySet) {
			if (COMMAND_RESPONSE.equals(entry.getKey())) {
				commandResponse = entry.getValue().toString().replace("\"", "");
			} else if (FAILED_REASON.equals(entry.getKey())) {
				failedReason = entry.getValue().toString().replace("\"", "");
			} else {
				scope = entry.getKey();
				propertyValues = entry.getValue();
			}
		}

		if (StringUtils.isNotEmpty(commandResponse)) {
			return new CommandResponseMessage(commandResponse, failedReason);
		} else if (!propertyValues.isJsonNull()) {
			return new PropertyRespondMessage(scope, propertyValues);
		}
		throw new IllegalArgumentException(
				"Invalid Responce message Recieved from ESB Server Debugger "
						+ response);
	}

	@Override
	public IEventMessage convertEventMessageToMap(String eventMessage) {

		JsonElement jasonElement = new JsonParser().parse(eventMessage);
		Set<Entry<String, JsonElement>> entrySet = jasonElement
				.getAsJsonObject().entrySet();
		EventMessageType event = null;
		String callbackReciever = null;
		String messageReciever = null;
		JsonElement sequence = null;
		JsonElement template = null;
		JsonElement recievedArtifactInfo = null;
		ArtifactType debugPointType = null;
		for (Entry<String, JsonElement> entry : entrySet) {

			if (EVENT.equals(entry.getKey())) {
				event = getEventMessageType(entry.getValue().toString()
						.replace("\"", ""));
			} else if (CALLBACK_RECIEVER.equals(entry.getKey())) {
				callbackReciever = entry.getValue().toString()
						.replace("\"", "");
			} else if (MESSAGE_RECIEVER.equals(entry.getKey())) {
				messageReciever = entry.getValue().toString().replace("\"", "");
			} else if (SEQUENCE.equals(entry.getKey())) {
				sequence = entry.getValue();
				debugPointType = ArtifactType.SEQUENCE;
			} else if (TEMPLATE.equals(entry.getKey())) {
				template = entry.getValue();
				debugPointType = ArtifactType.TEMPLATE;
			}
		}

		if (debugPointType != null) {
			switch (debugPointType) {
			case TEMPLATE:
				recievedArtifactInfo = template;
				break;
			case SEQUENCE:
				recievedArtifactInfo = sequence;
				break;
			default:
				break;
			}
		}

		if (event != null) {
			switch (event) {
			case RESUMED_CLIENT:
				return new GeneralEventMessage(event);
			case DEBUG_INFO_LOST:
				return new GeneralEventMessage(event);
			case STARTED:
				return new SpecialCordinationEventMessage(event,
						messageReciever, callbackReciever);
			case CALLBACK:
				return new SpecialCordinationEventMessage(event,
						messageReciever, callbackReciever);
			case TERMINATED:
				return new SpecialCordinationEventMessage(event,
						messageReciever, callbackReciever);
			case BREAKPOINT:
			case SKIPPOINT:
				return (IEventMessage) new DebugPointEventMessage(event,
						getESBDebugPoint(debugPointType, event,
								recievedArtifactInfo));
			default:
				throw new IllegalArgumentException(
						"Invalid Event Message Recieved from ESB Server Debugger : "
								+ eventMessage);
			}
		} else {
			throw new IllegalArgumentException(
					"Invalid Message Recieved from ESB Server Debugger Which doesn't have an event tag : "
							+ eventMessage);
		}

	}

	private ESBDebugPoint getESBDebugPoint(ArtifactType debugPointType,
			EventMessageType event, JsonElement recievedArtifactInfo) {
		switch (debugPointType) {
		case TEMPLATE:
			return getTemplateDebugPoint(event, recievedArtifactInfo);
		case SEQUENCE:
			return getSequenceTypeDebugPoint(event, recievedArtifactInfo);
		default:
			throw new IllegalArgumentException(
					"Illegal Artifacr type for create debug point "
							+ debugPointType);
		}

	}

	private ESBDebugPoint getSequenceTypeDebugPoint(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		Set<Entry<String, JsonElement>> entrySet = recievedArtifactInfo
				.getAsJsonObject().entrySet();
		if (entrySet.size() == 1) {
			for (Entry<String, JsonElement> entry : entrySet) {
				if (ESBDebuggerConstants.PROXY.equalsIgnoreCase(entry.getKey())) {
					return new ESBProxyDebugPoint(event, recievedArtifactInfo);
				} else if (ESBDebuggerConstants.API.equalsIgnoreCase(entry
						.getKey())) {
					return new ESBAPIDebugPoint(event, recievedArtifactInfo);
				}
			}
		} else {
			return new ESBSequenceDebugPoint(event, recievedArtifactInfo);
		}
		return null;
	}

	private ESBDebugPoint getTemplateDebugPoint(EventMessageType event,
			JsonElement recievedArtifactInfo) {
		return new ESBTemplateDebugPoint(event, recievedArtifactInfo);
	}

	private EventMessageType getEventMessageType(String event) {
		switch (event) {
		case BREAKPOINT:
			return EventMessageType.BREAKPOINT;
		case SKIP:
			return EventMessageType.SKIPPOINT;
		case STARTED_EVENT_TYPE:
			return EventMessageType.STARTED;
		case TERMINATED_EVENT_TYPE:
			return EventMessageType.TERMINATED;
		case CALLBACK_EVENT_TYPE:
			return EventMessageType.CALLBACK;
		case RESUMED_CLIENT_EVENT_TYPE:
			return EventMessageType.RESUMED_CLIENT;
		case DEBUG_INFO_LOST_EVENT:
			return EventMessageType.DEBUG_INFO_LOST;
		default:
			throw new IllegalArgumentException("Invalid Event Message Type : "
					+ event);
		}

	}

	@Override
	public String createBreakpointCommand(ESBDebugPoint debugPoint)
			throws Exception {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ESBMediatorPosition.class,
				new MediatorPositionGsonSerializer());
		builder.setFieldNamingStrategy(new PojoToGsonCustomNamingStrategy());
		Gson debugPointMessage = builder.create();
		return debugPointMessage.toJson(debugPoint);
	}

}
