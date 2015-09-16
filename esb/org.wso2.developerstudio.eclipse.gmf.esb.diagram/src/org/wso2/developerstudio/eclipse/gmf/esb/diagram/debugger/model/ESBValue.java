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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedSizedAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.SingleCompartmentComplexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.complexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.OpenEditorUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.CloneMediatorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.EntitlementMediatorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.FilterMediatorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.SwitchMediatorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ThrottleMediatorEditPart;

/**
 * This class object holds variable values to be shown in the variables table
 *
 */
public class ESBValue extends ESBDebugElement implements IValue {

	private static final CharSequence JASON_OBJECT_IDENTIFING_KEY = "{";
	private static final String EMPTY_STRING = "";
	private final String variableValue;
	private List<IVariable> valueChildren;

	public ESBValue(IDebugTarget target, String value) throws DebugException,
			JSONException {
		super(target);

		variableValue = value;
		if (value.contains(JASON_OBJECT_IDENTIFING_KEY)) {
			JSONObject responceMessage = new JSONObject(value);
			Map<String, String> message = convertJsonToMap(responceMessage);
			for (String name : message.keySet()) {
				boolean processed = false;

				if (valueChildren != null) {
					for (IVariable variable : valueChildren) {
						if (variable.getName().equals(name)) {
							variable.setValue(message.get(name));
							((ESBVariable) variable)
									.fireChangeEvent(DebugEvent.CONTENT);
							if (variable.getName().equalsIgnoreCase("envelope")) {
								AbstractMediator mediator = OpenEditorUtil
										.getPreviousHitEditPart();
								setToolTipMessage(message, name, mediator);
							}
							processed = true;
							break;
						}
					}
				} else {
					valueChildren = new ArrayList<>();
				}

				if (!processed) {
					ESBVariable esbVariable = new ESBVariable(getDebugTarget(),
							name, message.get(name));
					valueChildren.add(esbVariable);
					if (name.equalsIgnoreCase("envelope")) {
						AbstractMediator mediator = OpenEditorUtil
								.getPreviousHitEditPart();
						if (mediator instanceof FixedSizedAbstractMediator) {
							((FixedSizedAbstractMediator) mediator)
									.getPrimaryShape().setToolTipMessage(
											formatMessage(message.get(name)));
						}
					}
					esbVariable.fireCreationEvent();
				}
			}
		}
	}

	private void setToolTipMessage(Map<String, String> message, String name,
			AbstractMediator mediator) {
		if (mediator instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		} else if (mediator instanceof SingleCompartmentComplexFiguredAbstractMediator) {

			((SingleCompartmentComplexFiguredAbstractMediator) mediator)
					.getPrimaryShape().setToolTipMessage(
							formatMessage(message.get(name)));
		} else if (mediator instanceof CloneMediatorEditPart) {
			((CloneMediatorEditPart) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		} else if (mediator instanceof EntitlementMediatorEditPart) {
			((EntitlementMediatorEditPart) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		} else if (mediator instanceof FilterMediatorEditPart) {
			((FilterMediatorEditPart) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		} else if (mediator instanceof SwitchMediatorEditPart) {
			((SwitchMediatorEditPart) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		} else if (mediator instanceof ThrottleMediatorEditPart) {
			((ThrottleMediatorEditPart) mediator).getPrimaryShape()
					.setToolTipMessage(formatMessage(message.get(name)));
		}
	}

	private String formatMessage(String string) {
		String[] envelopeAttributes = string.split("><");
		String message = "";
		for (String attribute : envelopeAttributes) {
			if (attribute.startsWith("<")) {
				message = message + attribute + ">" + "\n";
			} else {
				message = message + "<" + attribute + ">" + "\n";
			}
		}
		return message;
	}

	/**
	 * This method convert JSON Message to Map Object.
	 */
	private Map<String, String> convertJsonToMap(JSONObject responceMessage)
			throws JSONException {

		Map<String, String> message = new LinkedHashMap<>();
		String value = EMPTY_STRING;
		Iterator<?> keys = responceMessage.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			value = responceMessage.getString(key);
			message.put(key, value);
		}
		return message;
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
}
