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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.MediationFlowCompleteEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent.ResumeEventType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.VariablesEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest.BreakpointEventAction;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * {@link ESBDebugTarget} is the root of the ESB Mediation Debugger debug
 * element hierarchy. It supports terminate, suspend , resume, breakpoint, skip
 * points
 *
 */
public class ESBDebugTarget extends ESBDebugElement implements IDebugTarget,
		EventHandler {

	private IEventBroker debugTargetEventBroker;
	private final ESBDebugProcess esbDebugProcess;
	private final List<ESBDebugThread> esbDebugThreads = new ArrayList<ESBDebugThread>();
	private final ILaunch esbDebugerLaunch;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebugTarget(final ILaunch launch) {
		super(null);
		debugTargetEventBroker = (IEventBroker) PlatformUI.getWorkbench()
				.getService(IEventBroker.class);
		debugTargetEventBroker.subscribe(
				ESBDebuggerConstants.ESBDEBUGGER_EVENT_TOPIC, this);
		esbDebugerLaunch = launch;
		fireCreationEvent();
		esbDebugProcess = new ESBDebugProcess(this);
		esbDebugProcess.fireCreationEvent();
	}

	@Override
	public void handleEvent(Event eventFromBroker) {
		if (!isDisconnected()) {
			IDebugEvent event = (IDebugEvent) eventFromBroker
					.getProperty(ESBDebuggerConstants.ESB_DEBUGGER_EVENT_BROKER_DATA_NAME);
			if (event instanceof DebuggerStartedEvent) {
				ESBDebugThread thread = new ESBDebugThread(this);
				esbDebugThreads.add(thread);
				thread.fireCreationEvent();

				ESBStackFrame stackFrame = new ESBStackFrame(this, thread);
				thread.addStackFrame(stackFrame);
				stackFrame.fireCreationEvent();

				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpointListener(this);

				resume();

			} else if (event instanceof SuspendedEvent) {
				setState(ESBDebuggerState.SUSPENDED);
				fireSuspendEvent(0);
				getThreads()[0].fireSuspendEvent(DebugEvent.BREAKPOINT);
				try {
					showSource((SuspendedEvent) event);
				} catch (ESBDebuggerException e) {
					log.error(e.getMessage(), e);
				}
				fireModelEvent(new FetchVariablesRequest());

			} else if (event instanceof ResumedEvent) {
				if (((ResumedEvent) event).getType() == ResumeEventType.CONTINUE) {
					setState(ESBDebuggerState.RESUMED);
					getThreads()[0].fireResumeEvent(DebugEvent.UNSPECIFIED);
				}

			} else if (event instanceof VariablesEvent) {

				try {
					getThreads()[0].getTopStackFrame().setVariables(
							((VariablesEvent) event).getVariables());
				} catch (DebugException e) {
					log.error("Error while seting variable values", e);
				}

			} else if (event instanceof TerminatedEvent) {
				setState(ESBDebuggerState.TERMINATED);

				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpointListener(this);
				debugTargetEventBroker.unsubscribe(this);
				this.fireTerminateEvent();
			} else if (event instanceof MediationFlowCompleteEvent) {
				setState(ESBDebuggerState.RESUMED);
				OpenEditorUtil.removeBreakpointHitStatus();
			}
		}

	}

	/**
	 * Pass an event to the {@link InternalEventDispatcher} where it is handled
	 * asynchronously.
	 * 
	 * @param event
	 *            event to handle
	 */
	void fireModelEvent(final IDebugEvent event) {
		debugTargetEventBroker.send(
				ESBDebuggerConstants.ESB_DEBUG_TARGET_EVENT_TOPIC, event);
	}

	/**
	 * This method finds the breakpoint registered in Breakpoint Manager which
	 * suspended the ESB Server and call a method to open the source file and
	 * show the associated mediator
	 * 
	 * @param event
	 * @throws ESBDebuggerException
	 */
	private void showSource(SuspendedEvent event) throws ESBDebuggerException {

		Map<String, Object> info = event.getDetail();
		ESBBreakpoint breakpoint = getMatchingBreakpoint(info);
		IFile file = (IFile) breakpoint.getResource();
		if (file.exists()) {
			OpenEditorUtil.openSeparateEditor(file, event);
		}
	}

	private ESBBreakpoint getMatchingBreakpoint(Map<String, Object> info)
			throws ESBDebuggerException {

		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (IBreakpoint breakpoint : breakpoints) {
			try {
				if (((ESBBreakpoint) breakpoint)
						.isMatchedWithPropertiesMap(info)) {
					return (ESBBreakpoint) breakpoint;
				}
			} catch (CoreException | BreakpointMarkerNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		}

		throw new ESBDebuggerException(
				"Matching Breakpoint not found in Breakpoint Manager with attributes");
	}

	@Override
	public ILaunch getLaunch() {
		return esbDebugerLaunch;
	}

	@Override
	public IProcess getProcess() {
		return esbDebugProcess;
	}

	@Override
	public ESBDebugThread[] getThreads() {
		return esbDebugThreads.toArray(new ESBDebugThread[esbDebugThreads
				.size()]);
	}

	@Override
	public boolean hasThreads() {
		return !esbDebugThreads.isEmpty();
	}

	@Override
	public String getName() {
		return "ESB DebugTarget";
	}

	@Override
	public boolean supportsBreakpoint(final IBreakpoint breakpoint) {
		if (breakpoint instanceof ESBBreakpoint) {
			return true;
		}
		return false;
	}

	@Override
	public ESBDebugTarget getDebugTarget() {
		return this;
	}

	private boolean isEnabledBreakpoint(IBreakpoint breakpoint)
			throws CoreException {
		return breakpoint.isEnabled()
				&& (DebugPlugin.getDefault().getBreakpointManager().isEnabled());
	}

	/**
	 * This method get called when Breakpoint Manager got any new breakpoint
	 * Registered.
	 */
	@Override
	public void breakpointAdded(final IBreakpoint breakpoint) {
		try {
			if (breakpoint instanceof ESBBreakpoint
					&& supportsBreakpoint(breakpoint)
					&& isEnabledBreakpoint(breakpoint)) {

				fireModelEvent(new BreakpointRequest(
						(ESBBreakpoint) breakpoint, BreakpointEventAction.ADDED));
			}
		} catch (BreakpointMarkerNotFoundException e) {
			log.error(e.getMessage(), e);
			ESBDebugerUtil.removeESBBreakpointFromBreakpointManager(breakpoint);
		} catch (CoreException e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * This method get called when any breakpoint is removed from the Breakpoint
	 * Manager.
	 */
	@Override
	public void breakpointRemoved(final IBreakpoint breakpoint,
			final IMarkerDelta delta) {
		try {
			if (breakpoint instanceof ESBBreakpoint
					&& supportsBreakpoint(breakpoint)
					&& isEnabledBreakpoint(breakpoint)) {

				fireModelEvent(new BreakpointRequest(
						(ESBBreakpoint) breakpoint,
						BreakpointEventAction.REMOVED));
			}
		} catch (BreakpointMarkerNotFoundException e) {
			log.error(e.getMessage(), e);
			ESBDebugerUtil.removeESBBreakpointFromBreakpointManager(breakpoint);
		} catch (CoreException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void breakpointChanged(final IBreakpoint breakpoint,
			final IMarkerDelta delta) {
		breakpointRemoved(breakpoint, delta);
		breakpointAdded(breakpoint);
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long arg0, long arg1) {
		return null;
	}

}
