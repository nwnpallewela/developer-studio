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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.IEventProcessor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.ResumedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.VariablesEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.BreakpointRequest;

public class ESBDebugTarget extends ESBDebugElement implements IDebugTarget,
		IEventProcessor {

	private EventDispatchJob mDispatcher;
	private final ESBProcess mProcess;
	private final List<ESBThread> mThreads = new ArrayList<ESBThread>();
	private final ILaunch mLaunch;

	public ESBDebugTarget(final ILaunch launch, int requestPortInternal,
			int eventPortInternal) {
		super(null);
		mLaunch = launch;
		fireCreationEvent();
		// create a process
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

	@Override
	public void handleEvent(final IDebugEvent event) {

		if (!isDisconnected()) {

			if (event instanceof DebuggerStartedEvent) {
				// create debug thread
				ESBThread thread = new ESBThread(this);
				mThreads.add(thread);
				thread.fireCreationEvent();

				// create stack frame
				ESBStackFrame stackFrame = new ESBStackFrame(this, thread);
				thread.addStackFrame(stackFrame);
				stackFrame.fireCreationEvent();

				// add breakpoint listener
				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpointListener(this);

				// attach deferred breakpoints to debugger
				IBreakpoint[] breakpoints = DebugPlugin.getDefault()
						.getBreakpointManager()
						.getBreakpoints(getModelIdentifier());
				for (IBreakpoint breakpoint : breakpoints) {
					breakpointAdded(breakpoint);
				}

				// resume execution after setting breakpoints
				resume();
				// suspend();

			} else if (event instanceof SuspendedEvent) {
				// breakpoint hit
				setState(State.SUSPENDED);

				getThreads()[0].getTopStackFrame().setLineNumber(
						((SuspendedEvent) event).getLineNumber());
				getThreads()[0].getTopStackFrame().fireChangeEvent(
						DebugEvent.CONTENT);

				// inform eclipse of suspended state
				getThreads()[0].fireSuspendEvent(DebugEvent.BREAKPOINT);

			} else if (event instanceof ResumedEvent) {
				if (((ResumedEvent) event).getType() == ResumedEvent.STEPPING) {
					setState(State.STEPPING);
					getThreads()[0].fireResumeEvent(DebugEvent.STEP_OVER);

				} else {
					setState(State.RESUMED);
					getThreads()[0].fireResumeEvent(DebugEvent.UNSPECIFIED);
				}

			} else if (event instanceof VariablesEvent) {
				getThreads()[0].getTopStackFrame().setVariables(
						((VariablesEvent) event).getVariables());

			} else if (event instanceof TerminatedEvent) {
				// debugger is terminated
				setState(State.TERMINATED);

				// unregister breakpoint listener
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpointListener(this);

				// we do not need our dispatcher anymore
				mDispatcher.terminate();

				// inform eclipse of terminated state
				fireTerminateEvent();
			}
		}
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
		/*
		 * if (mFile.equals(breakpoint.getMarker().getResource())) { //
		 * breakpoint on our source file return true; }
		 */

		return true;
	}

	@Override
	public ESBDebugTarget getDebugTarget() {
		return this;
	}

	/*
	 * public IFile getFile() { return mFile; }
	 */

	private boolean isEnabledBreakpoint(IBreakpoint breakpoint) {
		try {
			return breakpoint.isEnabled()
					&& (DebugPlugin.getDefault().getBreakpointManager()
							.isEnabled());// || (breakpoint instanceof
											// ESBRunToLineBreakpoint)
		} catch (CoreException e) {
			// ignore invalid breakpoint
		}

		return false;
	}

	// ************************************************************
	// IBreakpointListener
	// ************************************************************

	@Override
	public void breakpointAdded(final IBreakpoint breakpoint) {

		if (supportsBreakpoint(breakpoint) && isEnabledBreakpoint(breakpoint)) {
			fireModelEvent(new BreakpointRequest(breakpoint,
					BreakpointRequest.ADDED));
			System.out.println("Breakpoint Added");
		}
	}

	@Override
	public void breakpointRemoved(final IBreakpoint breakpoint,
			final IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint) && isEnabledBreakpoint(breakpoint)) {

			fireModelEvent(new BreakpointRequest(breakpoint,
					BreakpointRequest.REMOVED));
			System.out.println("Breakpoint Removed");

		}
	}

	@Override
	public void breakpointChanged(final IBreakpoint breakpoint,
			final IMarkerDelta delta) {
		breakpointRemoved(breakpoint, delta);
		breakpointAdded(breakpoint);
	}

	// ************************************************************
	// IMemoryBlockRetrieval
	// ************************************************************

	@Override
	public boolean supportsStorageRetrieval() {
		return true;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long arg0, long arg1)
			throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

}
