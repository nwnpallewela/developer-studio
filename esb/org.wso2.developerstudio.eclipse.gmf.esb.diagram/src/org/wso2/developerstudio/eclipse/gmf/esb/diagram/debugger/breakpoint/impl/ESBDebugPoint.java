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

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.MediatorPositionGsonSerializer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.PojoToGsonCustomNamingStrategy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.DebugPointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.AbstractESBDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBMediatorPosition;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event.EventMessageType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * This class represents the Custom Breakpoint type for ESB Breakpoints. Both
 * design view and source view ESB breakpoints is map to this type.
 */
public class ESBDebugPoint extends Breakpoint {

	// Default constructor is needed by the debug framework to restore
	// breakpoints
	public ESBDebugPoint() {
	}

	public ESBDebugPoint(final IResource resource, final int lineNumber,
			final AbstractESBDebugPointMessage debugPoint) throws CoreException {
		this(resource, lineNumber, debugPoint, true);
	}

	protected ESBDebugPoint(final IResource resource, final int lineNumber,
			final AbstractESBDebugPointMessage debugPoint, final boolean persistent)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker(ESBDebuggerConstants.ESB_BREAKPOINT_MARKER);
				setMarker(marker);
				setEnabled(true);
				ensureMarker().setAttribute(IBreakpoint.ENABLED, true);
				ensureMarker().setAttribute(IBreakpoint.PERSISTED, persistent);
				ensureMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber);
				ensureMarker().setAttribute(IBreakpoint.ID,
						getModelIdentifier());
				System.out.println(convertDebugPointToString(debugPoint));
				ensureMarker().setAttribute(IMarker.LOCATION,
						convertDebugPointToString(debugPoint));

			}

			/**
			 * This method converts the {@link AbstractESBDebugPointMessage} for a
			 * <code>String</code> value using <a
			 * href="https://sites.google.com/site/gson/gson-user-guide"
			 * >Gson</a> library {@link GsonBuilder}
			 * 
			 * @param debugPoint
			 * @return
			 */
			private String convertDebugPointToString(
					AbstractESBDebugPointMessage debugPoint) {
				GsonBuilder builder = new GsonBuilder();
				builder.registerTypeAdapter(ESBMediatorPosition.class,
						new MediatorPositionGsonSerializer());
				builder.setFieldNamingStrategy(new PojoToGsonCustomNamingStrategy());
				Gson debugPointMessage = builder.create();
				System.out.println(debugPointMessage.toJson(debugPoint).toString());
				return debugPointMessage.toJson(debugPoint).toString();
			}

		};
		run(getMarkerRule(resource), runnable);
	}

	/**
	 * Returns ESB breakpoint model identifier to identify this as a ESB Debug
	 * Point
	 */
	@Override
	public String getModelIdentifier() {
		return ESBDebugModelPresentation.ID;
	}

	/**
	 * returns source view line number of the Debug Point
	 * 
	 * @return
	 * @throws DebugPointMarkerNotFoundException
	 */
	public int getLineNumber() throws DebugPointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getAttribute(IMarker.LINE_NUMBER, -1);
		}
		throw new DebugPointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESB Debug Point : "
						+ this);
	}

	/**
	 * Returns the ESBDebugPointMessage contains in Debug Point
	 * 
	 * @return
	 * @throws CoreException
	 * @throws DebugPointMarkerNotFoundException
	 */
	public AbstractESBDebugPointMessage getLocation() throws CoreException,
			DebugPointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			String debugPointMessage = (String) marker.getAttributes().get(
					IMarker.LOCATION);
			if (StringUtils.isNotEmpty(debugPointMessage)) {
				return convertLocationToDebugPointMessage(debugPointMessage);
			}
		}
		throw new DebugPointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESB Debug Point : "
						+ this);
	}

	private AbstractESBDebugPointMessage convertLocationToDebugPointMessage(
			String locationString) {
		JsonElement jsonElement = new JsonParser().parse(locationString);
		Set<Entry<String, JsonElement>> entrySet = jsonElement
				.getAsJsonObject().entrySet();

		EventMessageType commandArgument = null;
		JsonElement recievedArtifactInfo = null;
		ArtifactType debugPointType = null;

		for (Entry<String, JsonElement> entry : entrySet) {

			switch (entry.getKey()) {
			case COMMAND_ARGUMENT:
				commandArgument = getDebugPointType(ESBDebugerUtil
						.formatEntryValueToString(entry));
				break;
			case SEQUENCE:
				recievedArtifactInfo = entry.getValue();
				debugPointType = ArtifactType.SEQUENCE;
				break;
			case TEMPLATE:
				recievedArtifactInfo = entry.getValue();
				debugPointType = ArtifactType.TEMPLATE;
				break;
			}
		}
		return ESBDebugerUtil.getESBDebugPoint(debugPointType, commandArgument,
				recievedArtifactInfo);
	}

	private EventMessageType getDebugPointType(String debuPointType) {
		switch (debuPointType) {
		case BREAKPOINT:
			return EventMessageType.BREAKPOINT;
		case SKIP:
			return EventMessageType.SKIPPOINT;
		default:
			throw new IllegalArgumentException("Invalid Debug Point Type : "
					+ debuPointType);
		}
	}

	/**
	 * Returns resource file of the marker set to breakpoint
	 * 
	 * @return
	 * @throws DebugPointMarkerNotFoundException
	 */
	public IResource getResource() throws DebugPointMarkerNotFoundException {
		IMarker marker = getMarker();
		if (marker != null) {
			return marker.getResource();
		}
		throw new DebugPointMarkerNotFoundException(
				"Assoiciated IMarker value not found for ESB Debug Point : "
						+ this);
	}

	public boolean equals(ESBDebugPoint breakpoint)
			throws DebugPointMarkerNotFoundException, CoreException {
		if (breakpoint != null) {
			AbstractESBDebugPointMessage debugPointMessage = breakpoint.getLocation();
			return isMatchedWithDebugPoint(debugPointMessage, true);
		}
		return false;

	}

	/**
	 * This method checks whether given debugPoint matches with this debug point
	 * with out considering the attribute debug point type
	 * 
	 * Eg: If you match a breakpoint and a skip point of same mediator this
	 * method returns true
	 * 
	 * @param debugPoint
	 * @return
	 * @throws DebugPointMarkerNotFoundException
	 * @throws CoreException
	 */
	public boolean equalsIgnoreType(ESBDebugPoint debugPoint)
			throws DebugPointMarkerNotFoundException, CoreException {
		if (debugPoint != null) {
			AbstractESBDebugPointMessage debugPointMessage = debugPoint.getLocation();
			return isMatchedWithDebugPoint(debugPointMessage, false);
		}
		return false;

	}

	/**
	 * @param breakpoint
	 * @return boolean : true if breakpoints are matched or false
	 */
	@Override
	public boolean equals(Object breakpoint) {
		try {
			return equals((ESBDebugPoint) breakpoint);
		} catch (DebugPointMarkerNotFoundException | CoreException
				| ClassCastException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		try {
			AbstractESBDebugPointMessage debugPointMessage = getLocation();
			return debugPointMessage.hashCode();
		} catch (DebugPointMarkerNotFoundException | CoreException e) {
			return 0;
		}
	}

	/**
	 * This method check whether breakpoint attribute values are matched with @param
	 * message attribute values.
	 * 
	 * @param debugPointMessage
	 *            should contains the attributes related to debug point as a key
	 *            value map
	 * @param shouldDebugPointTypeMatch
	 *            true/false : (if the matching should consider debug point type
	 *            / if matching should only consider the mediator position not
	 *            the type)
	 * @return boolean
	 * @throws DebugPointMarkerNotFoundException
	 * @throws CoreException
	 */
	public boolean isMatchedWithDebugPoint(
			AbstractESBDebugPointMessage debugPointMessage,
			boolean shouldDebugPointTypeMatch)
			throws DebugPointMarkerNotFoundException, CoreException {
		AbstractESBDebugPointMessage debugPointMessageOfThisDebugPoint = getLocation();
		if (shouldDebugPointTypeMatch) {
			return debugPointMessage.equals(debugPointMessageOfThisDebugPoint);
		} else {
			return debugPointMessage
					.equalsIgnoreType(debugPointMessageOfThisDebugPoint);
		}
	}

	/**
	 * This method checks whether this {@link ESBDebugPoint} is a breakpoint.
	 * 
	 * @return boolean - true : if this is a breakpoint, false : if this is a
	 *         skip point or any other type
	 * @throws DebugPointMarkerNotFoundException
	 * @throws CoreException
	 */
	public boolean isBreakpoint() throws DebugPointMarkerNotFoundException,
			CoreException {
		AbstractESBDebugPointMessage debugPoint = getLocation();
		if (BREAKPOINT.equals(debugPoint.getCommandArgument())) {
			return true;
		}
		return false;
	}

	/**
	 * This method checks whether this {@link ESBDebugPoint} is a skip point.
	 * 
	 * @return boolean - true : if this is a skip point, false : if this is a
	 *         breakpoint or any other type
	 * @throws DebugPointMarkerNotFoundException
	 * @throws CoreException
	 */
	public boolean isSkippoint() throws DebugPointMarkerNotFoundException,
			CoreException {
		AbstractESBDebugPointMessage debugPoint = getLocation();
		if (SKIP.equals(debugPoint.getCommandArgument())) {
			return true;
		}
		return false;
	}
}
