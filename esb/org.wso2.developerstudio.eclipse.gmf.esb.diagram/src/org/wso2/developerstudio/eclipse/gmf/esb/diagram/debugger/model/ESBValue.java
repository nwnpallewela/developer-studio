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
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;

import com.google.gson.JsonElement;

/**
 * This class object holds variable values to be shown in the variables table
 *
 */
public class ESBValue extends ESBDebugElement implements IValue {

	private static final String KEY_ENVELOPE = "envelope";
	private static final String EMPTY_STRING = "";

	private final String variableValue;
	private List<IVariable> valueChildren;

	public ESBValue(ESBDebugTarget debugTarget, String expression) {
		super(debugTarget);
		variableValue = expression;
	}

	public ESBValue(IDebugTarget target, JsonElement value)
			throws DebugException {
		super(target);

		variableValue = value.toString();
		if (!value.isJsonNull()) {
			Set<Entry<String, JsonElement>> entrySet = value.getAsJsonObject()
					.entrySet();
			for (Entry<String, JsonElement> entry : entrySet) {
				boolean processed = false;
				if (valueChildren != null) {
					processed = addValueToMatchingChildVariable(entry,
							processed);
				} else {
					valueChildren = new ArrayList<>();
				}

				if (!processed) {
					addNewChildVariable(entry);
				}
			}
		}
	}

	/**
	 * @param entry
	 * @throws DebugException
	 */
	private void addNewChildVariable(Entry<String, JsonElement> entry)
			throws DebugException {
		ESBVariable esbVariable = new ESBVariable(getDebugTarget(),
				entry.getKey(), entry.getValue().toString()
						.replace("\"", EMPTY_STRING));
		valueChildren.add(esbVariable);
		if (KEY_ENVELOPE.equalsIgnoreCase(entry.getKey())) {
			OpenEditorUtil.setToolTipMessageOnMediator(entry.getValue()
					.toString().replace("\"", EMPTY_STRING));
		}
		esbVariable.fireCreationEvent();
	}

	/**
	 * @param entry
	 * @param processed
	 * @return
	 * @throws DebugException
	 */
	private boolean addValueToMatchingChildVariable(
			Entry<String, JsonElement> entry, boolean processed)
			throws DebugException {
		for (IVariable variable : valueChildren) {
			if (variable.getName().equals(entry.getKey())) {
				variable.setValue(entry.getValue().toString()
						.replace("\"", EMPTY_STRING));
				((ESBVariable) variable).fireChangeEvent(DebugEvent.CONTENT);
				if (variable.getName().equalsIgnoreCase(KEY_ENVELOPE)) {
					OpenEditorUtil.setToolTipMessageOnMediator(entry.getValue()
							.toString().replace("\"", EMPTY_STRING));
				}
				processed = true;
				break;
			}
		}
		return processed;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return ESBDebuggerConstants.VARIABLE_TYPE;
	}

	/**
	 * This method returns the value contains in this ESBValue object.
	 */
	@Override
	public String getValueString() throws DebugException {
		return variableValue;
	}

	/**
	 * This should return false if the ESBValue is Garbage Collected.
	 */
	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	/**
	 * This method returns Array of child variables contain in this ESBValue.
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {

		IVariable[] variables = new ESBVariable[valueChildren.size()];
		int count = 0;
		for (IVariable variable : valueChildren) {
			variables[count] = variable;
			count++;
		}
		return variables;
	}

	/**
	 * This method returns true if this ESBValue has child variables
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return !(valueChildren == null || valueChildren.isEmpty());
	}

	public List<IVariable> getVariableList() {
		return valueChildren;
	}

	public void addChildVariable(IVariable child) {
		if (valueChildren == null) {
			valueChildren = new ArrayList<IVariable>();
			valueChildren.add(child);
		} else {
			valueChildren.add(child);
		}

	}
}
