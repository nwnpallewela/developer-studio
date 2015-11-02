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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.IEventProcessor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.MediationFlowCompleteEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.VariablesEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class ESBDebugTarget extends ESBDebugElement implements IDebugTarget,
		IEventProcessor {

	private EventDispatchJob mDispatcher;
	private final ESBProcess mProcess;
	private final List<ESBThread> mThreads = new ArrayList<ESBThread>();
	private final ILaunch mLaunch;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebugTarget(final ILaunch launch) {
		super(null);
		mLaunch = launch;
		fireCreationEvent();
		mProcess = new ESBProcess(this);
		mProcess.fireCreationEvent();
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
	void fireModelEvent(final IDebugEvent event) {
		mDispatcher.addEvent(event);
	}

	/**
	 * Handles events sent from {@link ESBDebugger} through
	 * {@link EventDispatchJob}
	 */
	@Override
	public void handleEvent(final IDebugEvent event) {

		if (!isDisconnected()) {

			if (event instanceof DebuggerStartedEvent) {
				ESBThread thread = new ESBThread(this);
				mThreads.add(thread);
				thread.fireCreationEvent();

				ESBStackFrame stackFrame = new ESBStackFrame(this, thread);
				thread.addStackFrame(stackFrame);
				stackFrame.fireCreationEvent();

				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpointListener(this);

				resume();

			} else if (event instanceof SuspendedEvent) {
				setState(State.SUSPENDED);
				fireSuspendEvent(0);
				getThreads()[0].fireSuspendEvent(DebugEvent.BREAKPOINT);
				try {
					showSource((SuspendedEvent) event);
				} catch (ESBDebuggerException e) {
					log.error(e.getMessage(), e);
				}
				fireModelEvent(new FetchVariablesRequest());

			} else if (event instanceof ResumedEvent) {
				if (((ResumedEvent) event).getType() == ResumedEvent.CONTINUE) {
					setState(State.RESUMED);
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
				setState(State.TERMINATED);

				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpointListener(this);

				mDispatcher.terminate();
				fireTerminateEvent();
			} else if (event instanceof MediationFlowCompleteEvent) {
				setState(State.RESUMED);
				OpenEditorUtil.removeBreakpointHitStatus();
			}
		}
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
		return mLaunch;
	}

	@Override
	public IProcess getProcess() {
		return mProcess;
	}

	@Override
	public ESBThread[] getThreads() {
		return mThreads.toArray(new ESBThread[mThreads.size()]);
	}

	@Override
	public boolean hasThreads() {
		return !mThreads.isEmpty();
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
						(ESBBreakpoint) breakpoint, BreakpointRequest.ADDED));
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
						(ESBBreakpoint) breakpoint, BreakpointRequest.REMOVED));
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
	public IMemoryBlock getMemoryBlock(long arg0, long arg1)
			throws DebugException {
		return null;
	}

}
