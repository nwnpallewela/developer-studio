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

public class ESBDebugger implements IESBDebugger {

	private EventDispatchJob mDispatcher;
	private boolean mIsStepping = false;
	private IESBDebuggerInterface mDebuggerInterface;
	private final Set<Integer> mBreakpoints = new HashSet<Integer>();

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
		attributeValues.put("mediator-position", "0 0");
		mDebuggerInterface.sendBreakpointCommand(ESBDebuggerConstants.SET,
				ESBDebuggerConstants.SEQUENCE, attributeValues);
		attributeValues.put("mediator-position", "1 0");
		mDebuggerInterface.sendBreakpointCommand(ESBDebuggerConstants.SET,
				ESBDebuggerConstants.SEQUENCE, attributeValues);

		mDebuggerInterface.sendCommand(ESBDebuggerConstants.RESUME);

	}

	@Override
	public void loaded() {
		fireEvent(new DebuggerStartedEvent());
	}

	@Override
	public void suspended(final int lineNumber) {
		fireEvent(new SuspendedEvent(lineNumber));
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
		System.out.println("Debugger.handleEvent() " + event);

		if (event instanceof ResumeRequest) {
			mIsStepping = (((ResumeRequest) event).getType() == ResumeRequest.STEP_OVER);
			mDebuggerInterface.sendCommand(ESBDebuggerConstants.RESUME);
		} else if (event instanceof TerminateRequest) {

		} else if (event instanceof DisconnectRequest) {

		} else if (event instanceof BreakpointRequest) {
			int line = ((BreakpointRequest) event).getLine();
			if (line != -1) {
				if (((BreakpointRequest) event).getType() == BreakpointRequest.ADDED)
					mBreakpoints.add(line);

				else if (((BreakpointRequest) event).getType() == BreakpointRequest.REMOVED)
					mBreakpoints.remove(line);
			}

		} else if (event instanceof FetchVariablesRequest) {
			// fireEvent(new VariablesEvent(mInterpreter.getVariables()));

		}
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
		System.out.println("Debugger.fireEvent() " + event);

		mDispatcher.addEvent(event);
	}

	@Override
	public IESBDebuggerInterface getESBDebuggerInterface() {
		return mDebuggerInterface;
	}

	@Override
	public void notifyResponce(Map<String, String> responce) {
		System.out.println("Debugger : " + responce.toString());
	}

	@Override
	public void notifyEvent(Map<String, String> event) {
		if (ESBDebuggerConstants.BREAKPOINT.equals(event
				.get(ESBDebuggerConstants.EVENT))) {
			Map<String, String> attributeValues = new HashMap<>();
			attributeValues.put("command", "get");
			attributeValues.put("command-argument", "properties");
			attributeValues.put("context", "axis2");

			mDebuggerInterface.sendGetPropertiesCommand(attributeValues);
		} else if (event.containsKey(ESBDebuggerConstants.AXIS2_PROPERTIES)) {

		}
		System.out.println("Event in Debugger : " + event.toString());
	}

}
