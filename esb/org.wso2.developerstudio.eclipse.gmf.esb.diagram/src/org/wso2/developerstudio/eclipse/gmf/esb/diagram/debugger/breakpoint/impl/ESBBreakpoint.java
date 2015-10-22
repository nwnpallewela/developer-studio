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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.core.resources.IMarker;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * This class represents the Custom Breakpoint type for ESB Breakpoints. Both
 * design view and source view ESB breakpoints is map to this type.
 */
public class ESBBreakpoint extends Breakpoint {

	/**
	 * Default constructor is needed by the debug framework to restore
	 * breakpoints
	 */
	public ESBBreakpoint() {
	}

	public ESBBreakpoint(final IResource resource, final int lineNumber,
			final Map<String,String> attributes) throws CoreException {
		this(resource, lineNumber, attributes, true);
	}

	protected ESBBreakpoint(final IResource resource, final int lineNumber,
			final Map<String,String> attributes, final boolean persistent)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker(ESBDebuggerConstants.ESB_BREAKPOINT_MARKER);
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, true);
				marker.setAttribute(IBreakpoint.PERSISTED, persistent);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.LOCATION, attributes);
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
	 */
	public int getLineNumber() {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getAttribute(IMarker.LINE_NUMBER, -1);
		}
		return -1;
	}

	/**
	 * Returns the map contains in ESBbreakpoint
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getLocation() {
		IMarker marker = getMarker();
		if (marker != null) {
			try {
				if(marker.getAttributes().get(IMarker.LOCATION) instanceof Map<?, ?>){
				return (Map<String, String>) marker.getAttributes().get(IMarker.LOCATION);
				}
			} catch (CoreException e) {
				//log this exception
			}
		}
		return null;
	}

	/**
	 * Returns resource file of the marker set to breakpoint
	 * 
	 * @return
	 */
	public IResource getResource() {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getResource();
		}
		return null;
	}

}
