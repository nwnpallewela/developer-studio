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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

public class ESBValue extends ESBDebugElement implements IValue {

	private final String mValue;
	private List<IVariable> mchildren;

	public ESBValue(IDebugTarget target, String value) throws DebugException {
		super(target);

		mValue = value;
		if (value.contains("{")) {
			try {
				JSONObject responceMessage = new JSONObject(value);
				Map<String, String> message = convertJsonToMap(responceMessage);
				for (String name : message.keySet()) {
					boolean processed = false;
					// try to find existing variable
					if (mchildren != null) {
						for (IVariable variable : mchildren) {
							if (variable.getName().equals(name)) {
								// variable exists
								variable.setValue(message.get(name));
								((ESBVariable) variable)
										.fireChangeEvent(DebugEvent.CONTENT);
								processed = true;
								break;
							}
						}
					} else {
						mchildren = new ArrayList<>();
					}

					if (!processed) {
						// not found, create new variable
						ESBVariable esbVariable = new ESBVariable(
								getDebugTarget(), name, message.get(name));
						mchildren.add(esbVariable);
						esbVariable.fireCreationEvent();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private Map<String, String> convertJsonToMap(JSONObject responceMessage) {
		Iterator<?> keys = responceMessage.keys();
		Map<String, String> message = new LinkedHashMap<>();
		String value = "";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				value = responceMessage.getString(key);
				message.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return message;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return ESBDebuggerConstants.VARIABLE_TYPE;
	}

	@Override
	public String getValueString() throws DebugException {
		return mValue;
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {

		IVariable[] variables = new ESBVariable[mchildren.size()];
		int count = 0;
		for (IVariable variable : mchildren) {
			variables[count] = variable;
			count++;
		}
		return variables;
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return (mchildren != null && mchildren.size() > 0);
	}
}
