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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.core.resources.IMarker;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * This class represents the Custom Breakpoint type for ESB Breakpoints. Both
 * design view and source view ESB breakpoints is map to this type.
 */
public class ESBBreakpoint extends Breakpoint {

	private static final String ATTRIBUTE_SEPERATOR = ",";
	private static final String KEY_VALUE_SEPERATOR = ":";
	private static final String TEMP_ATTRIBUTE_SEPERATOR = " ";
	private static final String POSITION_VALUE_SEPERATOR = "~";

	// Default constructor is needed by the debug framework to restore
	// breakpoints
	public ESBBreakpoint() {
	}

	public ESBBreakpoint(final IResource resource, final int lineNumber,
			final Map<String, Object> attributes) throws CoreException {
		this(resource, lineNumber, attributes, true);
	}

	protected ESBBreakpoint(final IResource resource, final int lineNumber,
			final Map<String, Object> attributes, final boolean persistent)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker(ESBDebuggerConstants.ESB_BREAKPOINT_MARKER);
				setMarker(marker);
				setEnabled(true);
				ensureMarker().setAttribute(IBreakpoint.PERSISTED, persistent);
				ensureMarker().setAttribute(IBreakpoint.ENABLED, true);
				ensureMarker().setAttribute(IBreakpoint.PERSISTED, persistent);
				ensureMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber);
				ensureMarker().setAttribute(IBreakpoint.ID,
						getModelIdentifier());
				ensureMarker().setAttribute(IMarker.LOCATION,
						convertMapToString(attributes));

			}

			private String convertMapToString(Map<String, Object> attributes) {
				Set<String> keys = attributes.keySet();
				StringBuilder builder = new StringBuilder();
				for (String key : keys) {
					if (ESBDebuggerConstants.MEDIATOR_POSITION
							.equalsIgnoreCase(key)) {
						@SuppressWarnings("unchecked")
						List<Integer> position = (List<Integer>) attributes
								.get(ESBDebuggerConstants.MEDIATOR_POSITION);
						builder.append(key).append(KEY_VALUE_SEPERATOR)
								.append(buildPositionStringFromList(position))
								.append(TEMP_ATTRIBUTE_SEPERATOR);
					} else {
						builder.append(key).append(KEY_VALUE_SEPERATOR)
								.append(attributes.get(key))
								.append(TEMP_ATTRIBUTE_SEPERATOR);
					}
				}
				return builder
						.toString()
						.trim()
						.replaceAll(TEMP_ATTRIBUTE_SEPERATOR,
								ATTRIBUTE_SEPERATOR);
			}

			/**
			 * @param position
			 */
			private String buildPositionStringFromList(List<Integer> position) {
				StringBuilder positionBuilder = new StringBuilder();
				for (Integer value : position) {
					positionBuilder.append(value).append(
							TEMP_ATTRIBUTE_SEPERATOR);
				}
				return positionBuilder
						.toString()
						.trim()
						.replaceAll(TEMP_ATTRIBUTE_SEPERATOR,
								POSITION_VALUE_SEPERATOR);
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	/**
	 * Returns ESB breakpoint model identifier to identify this as a ESB
	 * Breakpoint
	 */
	@Override
	public String getModelIdentifier() {
		return ESBDebugModelPresentation.ID;
	}

	/**
	 * returns source view line number of the breakpoint
	 * 
	 * @return
	 * @throws BreakpointMarkerNotFoundException
	 */
	public int getLineNumber() throws BreakpointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getAttribute(IMarker.LINE_NUMBER, -1);
		}
		throw new BreakpointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESBBreakpoint : "
						+ this);
	}

	/**
	 * Returns the map contains in ESBbreakpoint
	 * 
	 * @return
	 * @throws CoreException
	 * @throws BreakpointMarkerNotFoundException
	 */
	public Map<String, Object> getLocation() throws CoreException,
			BreakpointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			String locationString = (String) marker.getAttributes().get(
					IMarker.LOCATION);
			if (StringUtils.isNotEmpty(locationString)) {
				return convertLocationToMap(locationString);
			}
		}
		throw new BreakpointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESBBreakpoint : "
						+ this);
	}

	private Map<String, Object> convertLocationToMap(String locationString) {
		String[] locationArray = locationString.split(ATTRIBUTE_SEPERATOR);
		Map<String, Object> attributeMap = new HashMap<>();
		for (String attribute : locationArray) {
			String[] keyValue = attribute.split(KEY_VALUE_SEPERATOR);
			if (ESBDebuggerConstants.MEDIATOR_POSITION
					.equalsIgnoreCase(keyValue[0])) {
				attributeMap.put(keyValue[0],
						convertStringToIntegerList(keyValue[1]));
			} else {
				attributeMap.put(keyValue[0], keyValue[1]);
			}
		}
		return attributeMap;
	}

	private List<Integer> convertStringToIntegerList(String position) {
		String[] positionArray = position.split(POSITION_VALUE_SEPERATOR);
		List<Integer> positionList = new ArrayList<>();
		for (String value : positionArray) {
			positionList.add(Integer.parseInt(value));
		}
		return positionList;
	}

	/**
	 * Returns resource file of the marker set to breakpoint
	 * 
	 * @return
	 * @throws BreakpointMarkerNotFoundException
	 */
	public IResource getResource() throws BreakpointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getResource();
		}
		throw new BreakpointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESBBreakpoint : "
						+ this);
	}

	/**
	 * 
	 * @param breakpoint
	 * @return boolean : true if breakpoints are matched or false
	 * @throws BreakpointMarkerNotFoundException
	 * @throws CoreException
	 */
	public boolean equals(ESBBreakpoint breakpoint)
			throws BreakpointMarkerNotFoundException, CoreException {
		if (breakpoint != null) {
			Map<String, Object> message = breakpoint.getLocation();
			return isMatchedWithPropertiesMap(message);
		}
		return false;

	}

	/**
	 * This method check whether breakpoint attribute values are matched with @param
	 * message attribute values.
	 * 
	 * @param Map
	 *            <String,Object>
	 * @return boolean
	 * @throws BreakpointMarkerNotFoundException
	 * @throws CoreException
	 */
	@SuppressWarnings("unchecked")
	public boolean isMatchedWithPropertiesMap(Map<String, Object> message)
			throws BreakpointMarkerNotFoundException, CoreException {

		if (message != null) {
			Map<String, Object> breakpointMessage = getLocation();
			if (!isBreakpointPositionMatches(
					((List<Integer>) message
							.get(ESBDebuggerConstants.MEDIATOR_POSITION)),
					((List<Integer>) breakpointMessage
							.get(ESBDebuggerConstants.MEDIATOR_POSITION)))) {
				return false;
			}

			Set<String> attributeKeys = new HashSet<String>();
			attributeKeys.addAll(message.keySet());
			attributeKeys.remove(ESBDebuggerConstants.MEDIATION_COMPONENT);
			attributeKeys.remove(ESBDebuggerConstants.EVENT);
			attributeKeys.remove(ESBDebuggerConstants.MEDIATOR_POSITION);
			for (String key : attributeKeys) {
				if (!((breakpointMessage.containsKey(key) && ((String) breakpointMessage
						.get(key)).trim().equals(
						((String) message.get(key)).trim())))) {
					if (!(ESBDebuggerConstants.MAPPING_URL_TYPE
							.equalsIgnoreCase(key) && breakpointMessage
							.containsValue(message
									.get(ESBDebuggerConstants.MAPPING_URL_TYPE)))) {
						return false;
					}

				}
			}
			return true;
		}
		return false;
	}

	private boolean isBreakpointPositionMatches(
			List<Integer> messagePositionArray,
			List<Integer> breakpointPositionArray) {
		return messagePositionArray.equals(breakpointPositionArray);
	}

}
