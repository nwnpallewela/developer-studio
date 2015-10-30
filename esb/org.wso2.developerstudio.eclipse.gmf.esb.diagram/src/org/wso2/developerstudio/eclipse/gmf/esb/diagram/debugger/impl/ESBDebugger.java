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
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.MediationFlowCompleteEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.VariablesEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class ESBDebugger implements IESBDebugger {

	public static final int BREAKPOINT_ADDED = 1;
	public static final int BREAKPOINT_REMOVED = 2;
	private EventDispatchJob mDispatcher;
	private boolean mIsStepping = false;
	private IESBDebuggerInterface mDebuggerInterface;
	private final Set<Integer> mBreakpoints = new HashSet<Integer>();
	private Map<String, String> mVariables;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebugger(IESBDebuggerInterface debuggerInterface) {
		mDebuggerInterface = debuggerInterface;
		mDebuggerInterface.attachDebugger(this);
		mVariables = new HashMap<>();
	}

	@Override
	public void setDebuggerInterface(int commandPort, int eventPort)
			throws UnknownHostException, IllegalArgumentException, IOException {
		mDebuggerInterface.setfEventSocket(eventPort);
		mDebuggerInterface.setfRequestSocket(commandPort);
		try {
			mDebuggerInterface.setfEventReader();
		} catch (IOException e) {
			log.error("Error while seting Event Reader for DebuggerInterface",
					e);
		}
		try {
			mDebuggerInterface.setfRequestReader();
		} catch (IOException e) {
			log.error(
					"Error while seting Request Reader for DebuggerInterface",
					e);
		}
		try {
			mDebuggerInterface.setfRequestWriter();
		} catch (IOException e) {
			log.error(
					"Error while seting Request Writer for DebuggerInterface",
					e);
		}
		mDebuggerInterface.intializeDispatchers();

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
		fireEvent(new ResumedEvent(mIsStepping ? ResumedEvent.STEPPING
				: ResumedEvent.CONTINUE));
	}

	@Override
	public void terminated() {
		fireEvent(new TerminatedEvent());
	}

	@Override
	public boolean isBreakpoint(final int lineNumber) {
		if (mBreakpoints.contains(lineNumber))
			return true;

		return mIsStepping;
	}

	@Override
	public void handleEvent(final IDebugEvent event) {

		if (event instanceof ResumeRequest) {
			mIsStepping = (((ResumeRequest) event).getType() == ResumeRequest.STEP_OVER);
			mDebuggerInterface.sendCommand(ESBDebuggerConstants.RESUME);
			OpenEditorUtil.removeBreakpointHitStatus();
		} else if (event instanceof BreakpointRequest) {

			sendBreakpointForServer((BreakpointRequest) event);

		} else if (event instanceof FetchVariablesRequest) {
			if (mVariables != null) {
				fireEvent(new VariablesEvent(mVariables));
			}
		}
	}

	private void sendBreakpointForServer(BreakpointRequest event) {

		Map<String, Object> breakpointAttributes = event
				.getBreakpointAttributes();
		breakpointAttributes.put(ESBDebuggerConstants.COMMAND_ARGUMENT,
				ESBDebuggerConstants.BREAKPOINT);

		if (event.getType() == BREAKPOINT_ADDED) {
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerConstants.SET);
		} else {
			breakpointAttributes.put(ESBDebuggerConstants.COMMAND,
					ESBDebuggerConstants.CLEAR);
		}
		mDebuggerInterface.sendBreakpointCommand(((String) breakpointAttributes
				.get(ESBDebuggerConstants.COMMAND)),
				((String) breakpointAttributes
						.get(ESBDebuggerConstants.MEDIATION_COMPONENT)),
				breakpointAttributes);

	}

	public void setEventDispatcher(final EventDispatchJob dispatcher) {
		mDispatcher = dispatcher;
	}

	/**
	 * Pass an event to the {@link EventDispatchJob} where it is handled
	 * asynchronously.
	 * 
	 * @param event
	 *            event to handle
	 */
	private void fireEvent(final IDebugEvent event) {

		mDispatcher.addEvent(event);
	}

	@Override
	public IESBDebuggerInterface getESBDebuggerInterface() {
		return mDebuggerInterface;
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

				mVariables.put(ESBDebuggerConstants.AXIS2_PROPERTIES,
						(String) responce
								.get(ESBDebuggerConstants.AXIS2_PROPERTIES));
			} else if (responce
					.containsKey(ESBDebuggerConstants.SYNAPSE_PROPERTIES)) {
				mVariables.put(ESBDebuggerConstants.SYNAPSE_PROPERTIES,
						(String) responce
								.get(ESBDebuggerConstants.SYNAPSE_PROPERTIES));
			} else if (responce
					.containsKey(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES)) {
				mVariables
						.put(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES));
			} else if (responce
					.containsKey(ESBDebuggerConstants.OPERATION_PROPERTIES)) {
				mVariables
						.put(ESBDebuggerConstants.OPERATION_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.OPERATION_PROPERTIES));
			} else if (responce
					.containsKey(ESBDebuggerConstants.TRANSPORT_PROPERTIES)) {
				mVariables
						.put(ESBDebuggerConstants.TRANSPORT_PROPERTIES,
								(String) responce
										.get(ESBDebuggerConstants.TRANSPORT_PROPERTIES));
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
			getPropertiesFromESB();

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
		mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.AXIS2_CLIENT_PROPERTY_TAG);
		mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.TRANSPORT_PROPERTY_TAG);
		mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.OPERATION_PROPERTY_TAG);
		mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
		attributeValues.put(ESBDebuggerConstants.CONTEXT,
				ESBDebuggerConstants.SYANPSE_PROPERTY_TAG);
		mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
	}

}
