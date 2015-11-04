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
				ESBDebuggerConstants.ESBDEBUGTARGET_EVENT_TOPIC, this);
		this.debuggerInterface = debuggerInterface;
		this.debuggerInterface.attachDebugger(this);
	}

	@Override
	public void handleEvent(Event eventFromBroker) {
		IDebugEvent event = (IDebugEvent) eventFromBroker
				.getProperty(ESBDebuggerConstants.ESB_DEBUGGER_EVENT_BROKER_DATA_NAME);
		if (event instanceof ResumeRequest) {
			stepping = (((ResumeRequest) event).getType()
					.equals(ResumeRequestType.STEP_OVER));
			debuggerInterface.sendCommand(ESBDebuggerConstants.RESUME);
			OpenEditorUtil.removeBreakpointHitStatus();
		} else if (event instanceof BreakpointRequest) {
			sendBreakpointForServer((BreakpointRequest) event);
		} else if (event instanceof FetchVariablesRequest) {
			getPropertiesFromESB();
		} else if (event instanceof TerminateRequest) {
			terminated();
		}

	}

	@Override
	public void loaded() {
		fireEvent(new DebuggerStartedEvent());
	}

	@Override
	public void suspended(final Map<String, Object> event) {
		fireEvent(new SuspendedEvent(event));
	}

	@Override
	public void resumed() {
		fireEvent(new ResumedEvent(stepping ? ResumeEventType.STEPPING
				: ResumeEventType.CONTINUE));
	}

	@Override
	public void terminated() {
		fireEvent(new TerminatedEvent());
	}

	private void sendBreakpointForServer(BreakpointRequest event) {

		Map<String, Object> breakpointAttributes = event
				.getBreakpointAttributes();
		breakpointAttributes.put(ESBDebuggerConstants.COMMAND_ARGUMENT,
				ESBDebuggerConstants.BREAKPOINT);

		switch (event.getType()) {
		case ADDED:
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerConstants.SET);
			break;
		case REMOVED:
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerConstants.CLEAR);
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid Breakpoint action reqested");
		}
		debuggerInterface
				.sendBreakpointCommand((String) breakpointAttributes
						.get(ESBDebuggerConstants.COMMAND),
						(String) breakpointAttributes
								.get(ESBDebuggerConstants.MEDIATION_COMPONENT),
						breakpointAttributes);

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
				Map<String, String> mVariables = new HashMap<>();
				mVariables.put(ESBDebuggerConstants.AXIS2_PROPERTIES,
						(String) responce
								.get(ESBDebuggerConstants.AXIS2_PROPERTIES));
				fireEvent(new VariablesEvent(mVariables));
			} else if (responce
					.containsKey(ESBDebuggerConstants.SYNAPSE_PROPERTIES)) {
				Map<String, String> mVariables = new HashMap<>();
				mVariables.put(ESBDebuggerConstants.SYNAPSE_PROPERTIES,
						(String) responce
								.get(ESBDebuggerConstants.SYNAPSE_PROPERTIES));
				fireEvent(new VariablesEvent(mVariables));
			} else if (responce
					.containsKey(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES)) {
				Map<String, String> mVariables = new HashMap<>();
				mVariables
						.put(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES));
				fireEvent(new VariablesEvent(mVariables));
			} else if (responce
					.containsKey(ESBDebuggerConstants.OPERATION_PROPERTIES)) {
				Map<String, String> mVariables = new HashMap<>();
				mVariables
						.put(ESBDebuggerConstants.OPERATION_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.OPERATION_PROPERTIES));
				fireEvent(new VariablesEvent(mVariables));
			} else if (responce
					.containsKey(ESBDebuggerConstants.TRANSPORT_PROPERTIES)) {
				Map<String, String> mVariables = new HashMap<>();
				mVariables
						.put(ESBDebuggerConstants.TRANSPORT_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.TRANSPORT_PROPERTIES));
				fireEvent(new VariablesEvent(mVariables));
			}
		}

	}

	/**
	 * 
	 */
	@Override
	public void notifyEvent(Map<String, Object> event) {

		if (ESBDebuggerConstants.BREAKPOINT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			suspended(event);
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

	private void getPropertiesFromESB() {
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
