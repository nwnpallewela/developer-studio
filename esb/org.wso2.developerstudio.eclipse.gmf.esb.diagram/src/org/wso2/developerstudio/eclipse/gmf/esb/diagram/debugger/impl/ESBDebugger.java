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
import java.util.Map;
import java.util.Set;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.DisconnectRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.TerminateRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class ESBDebugger implements IESBDebugger {

	public static final int BREAKPOINT_ADDED = 1;
	public static final int BREAKPOINT_REMOVED = 2;
	private EventDispatchJob mDispatcher;
	private boolean mIsStepping = false;
	private IESBDebuggerInterface mDebuggerInterface;
	private final Set<Integer> mBreakpoints = new HashSet<Integer>();
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebugger(IESBDebuggerInterface debuggerInterface) {
		mDebuggerInterface = debuggerInterface;
		mDebuggerInterface.attachDebugger(this);
	}

	@Override
	public void setDebuggerInterface(int commandPort, int eventPort)
			throws UnknownHostException, IllegalArgumentException, IOException {
		mDebuggerInterface.setfEventSocket(eventPort);
		mDebuggerInterface.setfRequestSocket(commandPort);
		try {
			mDebuggerInterface.setfEventReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mDebuggerInterface.setfRequestReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mDebuggerInterface.setfRequestWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mDebuggerInterface.intializeDispatchers();

		Map<String, String> attributeValues = new HashMap<>();
		attributeValues.put("command", "set");
		attributeValues.put("command-argument", "breakpoint");
		attributeValues.put("mediation-component", "sequence");
		attributeValues.put("sequence-type", "named");
		attributeValues.put("sequence-key", "main");
		attributeValues.put("mediator-position", "0 1");
		mDebuggerInterface.sendBreakpointCommand(ESBDebuggerConstants.SET,
				ESBDebuggerConstants.SEQUENCE, attributeValues);
		attributeValues.put("mediator-position", "1 0");
		mDebuggerInterface.sendBreakpointCommand(ESBDebuggerConstants.SET,
				ESBDebuggerConstants.SEQUENCE, attributeValues);


	}

	@Override
	public void loaded() {
		fireEvent(new DebuggerStartedEvent());
	}

	@Override
	public void suspended(final Map<String, String> position) {
		fireEvent(new SuspendedEvent(position));
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
			System.out.println("Debugger Resume execution");
			mIsStepping = (((ResumeRequest) event).getType() == ResumeRequest.STEP_OVER);
			mDebuggerInterface.sendCommand(ESBDebuggerConstants.RESUME);
			
		} else if (event instanceof BreakpointRequest) {
			
				sendBreakpointForServer((BreakpointRequest) event);

		} 
	}

	private void sendBreakpointForServer(BreakpointRequest event) {
		
		Map<String, String> attributeValues = new HashMap<>();
		String breakpointMessage = event.getMessage();
		String[] attributeList = breakpointMessage.split(",");
		for (String attribute : attributeList) {
			String[] keyValuePair = attribute.split(":");
			attributeValues.put(keyValuePair[0], keyValuePair[1]);
		}
		attributeValues.put("command-argument", "breakpoint");
		if(event.getType()==BREAKPOINT_ADDED){
			attributeValues.put("command", "set");
		}else{
			attributeValues.put("command", "clear");
		}
		mDebuggerInterface.sendBreakpointCommand(
				attributeValues.get("command"),
				attributeValues.get("mediation-component"), attributeValues);

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
	public void notifyResponce(Map<String, String> responce) {
		
		if (responce.containsKey(ESBDebuggerConstants.COMMAND_RESPONSE)) {
			if (ESBDebuggerConstants.FAILED.equals(responce
					.get(ESBDebuggerConstants.COMMAND_RESPONSE))) {
				log.error(responce.get(ESBDebuggerConstants.FAILED_REASON));
				System.out.println("Logging error");
			}
		}else{
			if(responce.containsKey(ESBDebuggerConstants.AXIS2_PROPERTIES)){
				System.out.println(responce.get(ESBDebuggerConstants.AXIS2_PROPERTIES));
			} else if(responce.containsKey(ESBDebuggerConstants.SYNAPSE_PROPERTIES)){
				System.out.println(responce.get(ESBDebuggerConstants.SYNAPSE_PROPERTIES));
			}
		}

	}

	/**
	 * 
	 */
	@Override
	public void notifyEvent(Map<String, String> event) {

		if (ESBDebuggerConstants.BREAKPOINT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			suspended(event);
			Map<String, String> attributeValues = new HashMap<>();
			attributeValues.put("command", "get");
			attributeValues.put("command-argument", "properties");
			attributeValues.put("context", "axis2");
			mDebuggerInterface.sendGetPropertiesCommand(attributeValues);

		} else if (ESBDebuggerConstants.TERMINATED_EVENT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			//terminated();
			System.out.println("Terminated event from esb");
		}
	}

}
