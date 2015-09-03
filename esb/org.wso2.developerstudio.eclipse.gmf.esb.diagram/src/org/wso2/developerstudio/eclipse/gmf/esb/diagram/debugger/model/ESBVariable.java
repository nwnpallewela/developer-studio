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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

public class ESBVariable extends ESBDebugElement implements IVariable {

	private final String mName;
	private IValue mValue;

	protected ESBVariable(IDebugTarget target, String name, String value) throws DebugException {
		super(target);
		mName = name;
		setValue(value);
	}

	@Override
	public void setValue(String expression) throws DebugException {
		mValue = new ESBValue(getDebugTarget(), expression);
	}

	@Override
	public void setValue(IValue value) {
		mValue = value;
	}

	@Override
	public boolean supportsValueModification() {
		return false;
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}

	@Override
	public IValue getValue() {
		return mValue;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return ESBDebuggerConstants.VARIABLE_TYPE;
	}

	@Override
	public boolean hasValueChanged() throws DebugException {
		return false;
	}
}

