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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests.FetchVariablesRequest;

public class ESBStackFrame extends ESBDebugElement implements IStackFrame {

	private final IThread mThread;
	private int mLineNumber = 1;
	private final List<ESBVariable> mVariables = new ArrayList<>();
	private boolean mDirtyVariables = true;

	public ESBStackFrame(IDebugTarget target, IThread thread) {
		super(target);
		mThread = thread;
	}

	@Override
	public IThread getThread() {
		return mThread;
	}

	@Override
	public synchronized IVariable[] getVariables() {
		if (mDirtyVariables) {
			mDirtyVariables = false;
			getDebugTarget().fireModelEvent(new FetchVariablesRequest());
		}

		return mVariables.toArray(new IVariable[mVariables.size()]);
	}

	@Override
	public boolean hasVariables() {
		return getVariables().length > 0;
	}

	@Override
	public int getLineNumber() {
		return mLineNumber;
	}

	@Override
	public int getCharStart() {
		return -1;
	}

	@Override
	public int getCharEnd() {
		return -1;
	}

	/*
	 * @Override public String getName() { return getSourceFile().getName() +
	 * ", line " + getLineNumber(); }
	 */

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[0];
	}

	@Override
	public boolean hasRegisterGroups() {
		return getRegisterGroups().length > 0;
	}

	public void setLineNumber(int lineNumber) {
		mLineNumber = lineNumber;
	}

	/*
	 * public IFile getSourceFile() { return (getDebugTarget()).getFile(); }
	 */

	public synchronized void setVariables(Map<String, String> variables) {
		for (String name : variables.keySet()) {
			boolean processed = false;
			// try to find existing variable
			for (ESBVariable variable : mVariables) {
				if (variable.getName().equals(name)) {
					// variable exists
					variable.setValue(variables.get(name));
					variable.fireChangeEvent(DebugEvent.CONTENT);
					processed = true;
					break;
				}
			}

			if (!processed) {
				// not found, create new variable
				ESBVariable textVariable = new ESBVariable(getDebugTarget(),
						name, variables.get(name));
				mVariables.add(textVariable);
				textVariable.fireCreationEvent();
			}
		}
	}

	@Override
	public synchronized void fireChangeEvent(int detail) {
		mDirtyVariables = true;

		super.fireChangeEvent(detail);
	}

	@Override
	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}
}
