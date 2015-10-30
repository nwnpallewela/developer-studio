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

import java.util.Map;
import java.util.Set;

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
				System.out.println(getMarker().getType() + "    "
						+ getMarker().getId());
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
					builder.append(key).append(KEY_VALUE_SEPERATOR)
							.append(attributes.get(key))
							.append(TEMP_ATTRIBUTE_SEPERATOR);
				}
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
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLocation() throws CoreException,
			BreakpointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			if (marker.getAttributes().get(IMarker.LOCATION) instanceof Map<?, ?>) {
				return (Map<String, Object>) marker.getAttributes().get(
						IMarker.LOCATION);
			}
		}
		throw new BreakpointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESBBreakpoint : "
						+ this);
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

}
