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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebuggerEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IEventProcessor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.IModelRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * {@link InternalEventDispatcher} manages the event communication between
 * {@link ESBDebugger} and {@link ESBDebugTarget}
 *
 */
public class InternalEventDispatcher extends Job {

	private final List<IDebugEvent> events = new ArrayList<IDebugEvent>();
	private boolean terminated = false;
	private final IEventProcessor debugTarget;
	private final IEventProcessor debugger;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public InternalEventDispatcher(final IEventProcessor debugTarget,
			final IEventProcessor debugger) {

		super(ESBDebuggerConstants.ESB_DEBUGGER_EVENT_DISPATCH_JOB);
		this.debugTarget = debugTarget;
		this.debugger = debugger;
		this.debugTarget.setEventDispatcher(this);
		this.debugger.setEventDispatcher(this);
		setSystem(true);
	}

	public void addEvent(final IDebugEvent event) {
		synchronized (events) {
			events.add(event);
		}

		synchronized (this) {
			notifyAll();
		}
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		while (!terminated) {
			if (events.isEmpty()) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException ignore) {
					// ignore this exception. This is for break wake the Job
				}
			}

			if (!monitor.isCanceled()) {
				IDebugEvent event = null;
				synchronized (events) {
					if (!events.isEmpty()) {
						event = events.remove(0);
					}
				}

				handleEvent(event);

			} else {
				terminate();
			}
		}

		return Status.OK_STATUS;
	}

	private void handleEvent(final IDebugEvent event) {
		if (event instanceof IModelRequest) {
			debugger.handleEvent(event);
		} else if (event instanceof IDebuggerEvent) {
			debugTarget.handleEvent(event);
		} else {
			log.error("Unknown event type detected: " + event.toString());
			throw new IllegalArgumentException("Unknown event type detected: "
					+ event);
		}
	}

	public void terminate() {
		terminated = true;

		synchronized (this) {
			notifyAll();
		}
	}
}
