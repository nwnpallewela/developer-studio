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

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.PropertyRecievedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.CommandMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.GetPropertyCommand;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.DebugPointEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.EventMessageType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.GeneralEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.IEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.SpecialCordinationEventMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.CommandResponseMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.IResponseMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.PropertyRespondMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.DebugPointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest.ResumeRequestType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.TerminateRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
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
		debuggerEventBroker.subscribe(ESB_DEBUG_TARGET_EVENT_TOPIC, this);
		this.debuggerInterface = debuggerInterface;
		this.debuggerInterface.attachDebugger(this);
	}

	@Override
	public void handleEvent(Event eventFromBroker) {
		IDebugEvent event = (IDebugEvent) eventFromBroker
				.getProperty(ESB_DEBUGGER_EVENT_BROKER_DATA_NAME);
		try {
			if (event instanceof ResumeRequest) {
				stepping = (((ResumeRequest) event).getType()
						.equals(ResumeRequestType.STEP_OVER));
				debuggerInterface.sendCommand(new CommandMessage(
						ESBDebuggerCommands.RESUME_COMMAND));
				OpenEditorUtil.removeBreakpointHitStatus();
			} else if (event instanceof DebugPointRequest) {
				sendBreakpointForServer((DebugPointRequest) event);
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
	public void fireSuspendedEvent(final DebugPointEventMessage event) {
		fireEvent(new SuspendedEvent(event));
	}

	@Override
	public void fireResumedEvent() {
		fireEvent(new ResumedEvent(stepping ? ResumeEventType.STEPPING
				: ResumeEventType.CONTINUE));
	}

	@Override
	public void fireTerminatedEvent() throws Exception {
		ESBDebugerUtil.removeAllESBBreakpointsFromBreakpointManager();
		debuggerInterface.sendCommand(new CommandMessage(
				ESBDebuggerCommands.RESUME_COMMAND));
		fireEvent(new TerminatedEvent());
		debuggerInterface.terminate();
		debuggerEventBroker.unsubscribe(this);
	}

	private void sendBreakpointForServer(DebugPointRequest event)
			throws Exception {

		ESBDebugPointMessage debugPoint = event.getBreakpointAttributes();
		debuggerInterface.sendBreakpointCommand(debugPoint);

	}

	/**
	 * Pass an event to the {@link InternalEventDispatcher} where it is handled
	 * asynchronously.
	 * 
	 * @param event
	 *            event to handle
	 */
	public void fireEvent(final IDebugEvent event) {
		debuggerEventBroker.send(ESBDEBUGGER_EVENT_TOPIC, event);
	}

	@Override
	public IESBDebuggerInterface getESBDebuggerInterface() {
		return debuggerInterface;
	}

	@Override
	public void notifyResponce(IResponseMessage responseMessage) {

		if (responseMessage instanceof CommandResponseMessage) {
			CommandResponseMessage response = (CommandResponseMessage) responseMessage;
			if (StringUtils.isNotEmpty(response.getFailedReason())) {
				log.warn(response.getFailedReason());
			}
		} else if (responseMessage instanceof PropertyRespondMessage) {
			fireEvent(new PropertyRecievedEvent(
					(PropertyRespondMessage) responseMessage));
		}

	}

	@Override
	public void notifyEvent(IEventMessage event) {

		if (event instanceof DebugPointEventMessage) {
			if (((DebugPointEventMessage) event).getEvent() == EventMessageType.BREAKPOINT) {
				fireSuspendedEvent((DebugPointEventMessage) event);
			}

		} else if (event instanceof SpecialCordinationEventMessage) {
			SpecialCordinationEventMessage cordinationMessage = (SpecialCordinationEventMessage) event;
			log.info("Event : " + cordinationMessage.getEvent().toString()
					+ " , Message-Reciever : "
					+ cordinationMessage.getMessageReciever()
					+ " , Callback-Reciever : "
					+ cordinationMessage.getCallbackReciever());
			switch (((SpecialCordinationEventMessage) event).getEvent()) {
			case TERMINATED:
				mediationFlowCompleted();
				break;
			default:
				break;
			}
		} else if (event instanceof GeneralEventMessage) {
			GeneralEventMessage generalMessage = (GeneralEventMessage) event;
			log.info("Event : " + generalMessage.getEvent().toString());
			switch (((GeneralEventMessage) event).getEvent()) {
			case DEBUG_INFO_LOST:
				ESBDebugerUtil.repopulateESBServerBreakpoints();
				break;
			default:
				break;
			}
		}
	}

	private void mediationFlowCompleted() {
		fireEvent(new MediationFlowCompleteEvent());

	}

	private void getPropertiesFromESB() throws Exception {
		debuggerInterface.sendGetPropertiesCommand(new GetPropertyCommand(
				COMMAND_GET, PROPERTIES, AXIS2_PROPERTY_TAG));
		debuggerInterface.sendGetPropertiesCommand(new GetPropertyCommand(
				COMMAND_GET, PROPERTIES, AXIS2_CLIENT_PROPERTY_TAG));
		debuggerInterface.sendGetPropertiesCommand(new GetPropertyCommand(
				COMMAND_GET, PROPERTIES, TRANSPORT_PROPERTY_TAG));
		debuggerInterface.sendGetPropertiesCommand(new GetPropertyCommand(
				COMMAND_GET, PROPERTIES, OPERATION_PROPERTY_TAG));
		debuggerInterface.sendGetPropertiesCommand(new GetPropertyCommand(
				COMMAND_GET, PROPERTIES, SYANPSE_PROPERTY_TAG));
	}

}
