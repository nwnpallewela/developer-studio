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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.MediationFlowCompleteEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent.ResumeEventType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.VariablesEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest.ResumeRequestType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.TerminateRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * {@link ESBDebugger} implements the representation of ESB Mediation debugger
 * to communicate UI and ESB Server.
 */
public class ESBDebugger implements IESBDebugger, EventHandler {

	public enum ESBDebuggerCommands {
		SET_BREAKPOINT_SKIPPOINT("set"), CLEAR_BREAKPOINT_SKIPPOINT("clear"), GET_COMMAND(
				"get"), RESUME_COMMAND("resume");

		private final String command;

		private ESBDebuggerCommands(String commandValue) {
			command = commandValue;
		}

		public boolean equalsName(String comapreCommand) {
			return (comapreCommand == null) ? false : command
					.equals(comapreCommand);
		}

		public String toString() {
			return this.command;
		}
	}

	private IEventBroker debuggerEventBroker;
	private boolean stepping = false;
	private IESBDebuggerInterface debuggerInterface;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebugger(int commandPort, int eventPort) throws IOException {
		this(new ESBDebuggerInterface(commandPort, eventPort));
	}

	public ESBDebugger(IESBDebuggerInterface debuggerInterface) {
		debuggerEventBroker = (IEventBroker) PlatformUI.getWorkbench()
				.getService(IEventBroker.class);
		debuggerEventBroker.subscribe(
				ESBDebuggerConstants.ESB_DEBUG_TARGET_EVENT_TOPIC, this);
		this.debuggerInterface = debuggerInterface;
		this.debuggerInterface.attachDebugger(this);
	}

	@Override
	public void handleEvent(Event eventFromBroker) {
		IDebugEvent event = (IDebugEvent) eventFromBroker
				.getProperty(ESBDebuggerConstants.ESB_DEBUGGER_EVENT_BROKER_DATA_NAME);
		try {
			if (event instanceof ResumeRequest) {
				stepping = (((ResumeRequest) event).getType()
						.equals(ResumeRequestType.STEP_OVER));
				debuggerInterface
						.sendCommand(ESBDebuggerCommands.RESUME_COMMAND);
				OpenEditorUtil.removeBreakpointHitStatus();
			} else if (event instanceof BreakpointRequest) {
				sendBreakpointForServer((BreakpointRequest) event);
			} else if (event instanceof FetchVariablesRequest) {
				getPropertiesFromESB();
			} else if (event instanceof TerminateRequest) {
				fireTerminatedEvent();
			}
		} catch (IOException e) {
			log.error("Termination Operation Failed", e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void fireLoadedEvent() {
		fireEvent(new DebuggerStartedEvent());
	}

	@Override
	public void fireSuspendedEvent(final Map<String, Object> event) {
		fireEvent(new SuspendedEvent(event));
	}

	@Override
	public void fireResumedEvent() {
		fireEvent(new ResumedEvent(stepping ? ResumeEventType.STEPPING
				: ResumeEventType.CONTINUE));
	}

	@Override
	public void fireTerminatedEvent() throws IOException {
		fireEvent(new TerminatedEvent());
		debuggerInterface.terminate();
		debuggerEventBroker.unsubscribe(this);
	}

	private void sendBreakpointForServer(BreakpointRequest event)
			throws Exception {

		Map<String, Object> breakpointAttributes = event
				.getBreakpointAttributes();
		breakpointAttributes.put(ESBDebuggerConstants.COMMAND_ARGUMENT,
				ESBDebuggerConstants.BREAKPOINT);

		switch (event.getType()) {
		case ADDED:
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerCommands.SET_BREAKPOINT_SKIPPOINT.toString());
			debuggerInterface.sendBreakpointCommand(breakpointAttributes);
			break;
		case REMOVED:
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerCommands.CLEAR_BREAKPOINT_SKIPPOINT.toString());
			debuggerInterface.sendBreakpointCommand(breakpointAttributes);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid Breakpoint action reqested");
		}

	}

	/**
	 * Pass an event to the {@link InternalEventDispatcher} where it is handled
	 * asynchronously.
	 * 
	 * @param event
	 *            event to handle
	 */
	public void fireEvent(final IDebugEvent event) {
		debuggerEventBroker.send(ESBDebuggerConstants.ESBDEBUGGER_EVENT_TOPIC,
				event);
	}

	@Override
	public IESBDebuggerInterface getESBDebuggerInterface() {
		return debuggerInterface;
	}

	@Override
	public void notifyResponce(Map<String, Object> responce) {

		if (responce.containsKey(ESBDebuggerConstants.COMMAND_RESPONSE)) {
			if (ESBDebuggerConstants.FAILED.equals(responce
					.get(ESBDebuggerConstants.COMMAND_RESPONSE))) {
				log.warn((String) responce
						.get(ESBDebuggerConstants.FAILED_REASON));
			}
		} else {
			if (responce.containsKey(ESBDebuggerConstants.AXIS2_PROPERTIES)) {
				sendPropertyValuesForDebuggerEventBroker(responce,
						ESBDebuggerConstants.AXIS2_PROPERTIES);
			} else if (responce
					.containsKey(ESBDebuggerConstants.SYNAPSE_PROPERTIES)) {
				sendPropertyValuesForDebuggerEventBroker(responce,
						ESBDebuggerConstants.SYNAPSE_PROPERTIES);
			} else if (responce
					.containsKey(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES)) {
				sendPropertyValuesForDebuggerEventBroker(responce,
						ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES);
			} else if (responce
					.containsKey(ESBDebuggerConstants.OPERATION_PROPERTIES)) {
				sendPropertyValuesForDebuggerEventBroker(responce,
						ESBDebuggerConstants.OPERATION_PROPERTIES);
			} else if (responce
					.containsKey(ESBDebuggerConstants.TRANSPORT_PROPERTIES)) {
				sendPropertyValuesForDebuggerEventBroker(responce,
						ESBDebuggerConstants.TRANSPORT_PROPERTIES);
			}
		}

	}

	/**
	 * @param responce
	 * @param propertyType
	 */
	private void sendPropertyValuesForDebuggerEventBroker(
			Map<String, Object> responce, String propertyType) {
		Map<String, String> mVariables = new HashMap<>();
		mVariables.put(propertyType, (String) responce.get(propertyType));
		fireEvent(new VariablesEvent(mVariables));
	}

	@Override
	public void notifyEvent(Map<String, Object> event) {

		if (ESBDebuggerConstants.BREAKPOINT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			fireSuspendedEvent(event);
		} else if (ESBDebuggerConstants.TERMINATED_EVENT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			mediationFlowCompleted();
		} else if (ESBDebuggerConstants.DEBUG_INFO_LOST_EVENT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			ESBDebugerUtil.repopulateESBServerBreakpoints();
		}
	}

	private void mediationFlowCompleted() {
		fireEvent(new MediationFlowCompleteEvent());

	}

	private void getPropertiesFromESB() throws Exception {
		Map<String, Object> attributeValues = new LinkedHashMap<>();
		attributeValues.put(ESBDebuggerConstants.COMMAND,
				ESBDebuggerConstants.COMMAND_GET);
		attributeValues.put(ESBDebuggerConstants.COMMAND_ARGUMENT,
				ESBDebuggerConstants.PROPERTIES);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.AXIS2_PROPERTY_TAG);
		debuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.AXIS2_CLIENT_PROPERTY_TAG);
		debuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.TRANSPORT_PROPERTY_TAG);
		debuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.OPERATION_PROPERTY_TAG);
		debuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.SYANPSE_PROPERTY_TAG);
		debuggerInterface.sendGetPropertiesCommand(attributeValues);
	}

}
