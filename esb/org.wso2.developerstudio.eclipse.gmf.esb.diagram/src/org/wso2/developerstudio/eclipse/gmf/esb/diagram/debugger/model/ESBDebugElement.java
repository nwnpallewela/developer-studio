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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.IStep;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.TerminatedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.DisconnectRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.ResumeRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.TerminateRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

public abstract class ESBDebugElement extends DebugElement implements ISuspendResume, IDisconnect, ITerminate, IStep {

	public ESBDebugElement(IDebugTarget target) {
		super(target);
	}

	public enum State {
		NOT_STARTED, SUSPENDED, RESUMED, TERMINATED, STEPPING
	};

	private State mState = State.NOT_STARTED;

	@Override
	public String getModelIdentifier() {
		return ESBDebugModelPresentation.ID;
	}

	protected void setState(State state) {
		// only the DebugTarget saves the correct state.
		((ESBDebugElement) getDebugTarget()).mState = state;
	}

	protected State getState() {
		return ((ESBDebugElement) getDebugTarget()).mState;
	}

	@Override
	public ESBDebugTarget getDebugTarget() {
		return (ESBDebugTarget) super.getDebugTarget();
	}

	// ************************************************************
	// ISuspendResume
	// ************************************************************

	@Override
	public boolean canResume() {
		return isSuspended();
	}

	@Override
	public boolean canSuspend() {
		// we cannot interrupt our debugger once it is running
		return false;
	}

	@Override
	public boolean isSuspended() {
		return (getState() == State.SUSPENDED);
	}

	@Override
	public void resume() {
		getDebugTarget().fireModelEvent(new ResumeRequest(ResumeRequest.CONTINUE));
	}

	@Override
	public void suspend() throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, "com.codeandme.textinterpreter.debugger", "suspend() not supported"));
	}

	// ************************************************************
	// IDisconnect
	// ************************************************************

	@Override
	public boolean canDisconnect() {
		return canTerminate();
	}

	@Override
	public void disconnect() {
		// disconnect request from eclipse

		// send disconnect request to debugger
		getDebugTarget().fireModelEvent(new DisconnectRequest());

		// debugger is detached, simulate terminate event
		getDebugTarget().handleEvent(new TerminatedEvent());
	}

	@Override
	public boolean isDisconnected() {
		return isTerminated();
	}

	// ************************************************************
	// ITerminate
	// ************************************************************

	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}

	@Override
	public boolean isTerminated() {
		return (getState() == State.TERMINATED);
	}

	@Override
	public void terminate() {
		// terminate request from eclipse

		// send terminate request to debugger
		getDebugTarget().fireModelEvent(new TerminateRequest());
	}

	// ************************************************************
	// IStep
	// ************************************************************

	@Override
	public boolean canStepInto() {
		return false;
	}

	@Override
	public boolean canStepOver() {
		return isSuspended();
	}

	@Override
	public boolean canStepReturn() {
		return false;
	}

	@Override
	public boolean isStepping() {
		return (getState() == State.STEPPING);
	}

	@Override
	public void stepInto() throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, "com.codeandme.textinterpreter.debugger", "stepInto() not supported"));
	}

	@Override
	public void stepOver() {
		// stepOver request from eclipse

		// send stepOver request to debugger
		getDebugTarget().fireModelEvent(new ResumeRequest(ResumeRequest.STEP_OVER));
	}

	@Override
	public void stepReturn() throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, "com.codeandme.textinterpreter.debugger", "stepReturn() not supported"));
	}
	
	protected String setName(String name) {
		switch (name) {
		case ESBDebuggerConstants.AXIS2_PROPERTIES:
			return ESBDebuggerConstants.AXIS2_PROPERTY_TAG;
		case ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES:
			return ESBDebuggerConstants.AXIS2_CLIENT_PROPERTY_TAG;
		case ESBDebuggerConstants.SYNAPSE_PROPERTIES:
			return ESBDebuggerConstants.SYANPSE_PROPERTY_TAG;
		case ESBDebuggerConstants.TRANSPORT_PROPERTIES:
			return ESBDebuggerConstants.TRANSPORT_PROPERTY_TAG;
		case ESBDebuggerConstants.OPERATION_PROPERTIES:
			return ESBDebuggerConstants.OPERATION_PROPERTY_TAG;
		}
		return name;
	}
}

