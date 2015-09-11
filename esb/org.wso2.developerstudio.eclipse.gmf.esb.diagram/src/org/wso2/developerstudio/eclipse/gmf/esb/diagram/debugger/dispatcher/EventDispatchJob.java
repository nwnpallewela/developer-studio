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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebugEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.IModelRequest;

public class EventDispatchJob extends Job {

	private final List<IDebugEvent> mEvents = new ArrayList<IDebugEvent>();
	private boolean mTerminated = false;
	private final IEventProcessor debugTarget;
	private final IEventProcessor debugger;

	public EventDispatchJob(final IEventProcessor debugTarget,
			final IEventProcessor debugger) {
		super("ESB Mediation Debugger event dispatcher");

		this.debugTarget = debugTarget;
		this.debugger = debugger;

		setSystem(true);
	}

	public void addEvent(final IDebugEvent event) {
		synchronized (mEvents) {
			mEvents.add(event);
		}

		synchronized (this) {
			notifyAll();
		}
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		while (!mTerminated) {
			// wait for new events
			if (mEvents.isEmpty()) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
				}
			}

			if (!monitor.isCanceled()) {

				IDebugEvent event = null;
				synchronized (mEvents) {
					if (!mEvents.isEmpty())
						event = mEvents.remove(0);
				}

				if (event != null)
					handleEvent(event);

			} else
				terminate();
		}

		return Status.OK_STATUS;
	}

	private void handleEvent(final IDebugEvent event) {
		// forward event handling to target
		if (event instanceof IModelRequest) {
			debugger.handleEvent(event);
		} else if (event instanceof IDebugEvent) {
			debugTarget.handleEvent(event);
		} else {
			throw new RuntimeException("Unknown event detected: " + event);
		}
	}

	public void terminate() {
		mTerminated = true;

		// wake up job
		synchronized (this) {
			notifyAll();
		}
	}
}
