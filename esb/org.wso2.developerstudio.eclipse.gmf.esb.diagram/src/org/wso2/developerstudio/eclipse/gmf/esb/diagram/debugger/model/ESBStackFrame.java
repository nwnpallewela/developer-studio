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
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond.PropertyRespondMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * {@link ESBStackFrame} represents an execution context in a suspended thread.
 * And it contains variables representing visible locals and arguments at the
 * current execution location.
 * 
 */
public class ESBStackFrame extends ESBDebugElement implements IStackFrame {

	private final IThread thread;
	private int lineNumber = 1;
	private List<IVariable> variables = new ArrayList<>();
	private boolean variablesDirty = true;

	public ESBStackFrame(IDebugTarget target, IThread thread) {
		super(target);
		this.thread = thread;
	}

	@Override
	public IThread getThread() {
		return thread;
	}

	@Override
	public synchronized IVariable[] getVariables() {
		if (variablesDirty) {
			variablesDirty = false;
			getDebugTarget().fireModelEvent(new FetchVariablesRequest());
		}

		return variables.toArray(new IVariable[variables.size()]);
	}

	@Override
	public boolean hasVariables() {
		return getVariables().length > 0;
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int getCharStart() {
		return -1;
	}

	@Override
	public int getCharEnd() {
		return -1;
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[0];
	}

	@Override
	public boolean hasRegisterGroups() {
		return getRegisterGroups().length > 0;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public synchronized void setVariables(
			PropertyRespondMessage propertyRespondMessage)
			throws DebugException {

		String name = propertyRespondMessage.getScope();
		boolean processed = false;
		for (IVariable variable : variables) {
			if (variable.getName().equals(getUITableVariableName(name))) {
				((ESBVariable)variable).setValue(propertyRespondMessage.getPropertyValues());
				((ESBVariable) variable).fireChangeEvent(DebugEvent.CONTENT);
				processed = true;
				break;
			}
		}

		if (!processed) {
			ESBVariable textVariable = new ESBVariable(getDebugTarget(),
					getUITableVariableName(name), propertyRespondMessage
							.getPropertyValues().toString());
			variables.add(textVariable);
			textVariable.fireCreationEvent();
		}
	}

	@Override
	public synchronized void fireChangeEvent(int detail) {
		variablesDirty = true;
		super.fireChangeEvent(detail);
	}

	@Override
	public String getName() throws DebugException {
		return "ESB Debugger Stack Frame";
	}

	private String getUITableVariableName(String name) {
		switch (name) {
		case ESBDebuggerConstants.AXIS2_PROPERTIES:
			return ESBDebuggerConstants.AXIS2_PROPERTY_UI_NAME;
		case ESBDebuggerConstants.AXIS2_CLIENT_PROPERTIES:
			return ESBDebuggerConstants.AXIS2_CLIENT_PROPERTY_UI_NAME;
		case ESBDebuggerConstants.SYNAPSE_PROPERTIES:
			return ESBDebuggerConstants.SYANPSE_PROPERTY_UI_NAME;
		case ESBDebuggerConstants.TRANSPORT_PROPERTIES:
			return ESBDebuggerConstants.TRANSPORT_PROPERTY_UI_NAME;
		case ESBDebuggerConstants.OPERATION_PROPERTIES:
			return ESBDebuggerConstants.OPERATION_PROPERTY_UI_NAME;
		}
		return name;
	}

}
