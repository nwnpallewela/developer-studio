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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EsbGroupingShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FilterMediatorGraphicalShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedSizedAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.complexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.BreakpointBuilderFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;

public class ESBDebugerUtil {

	private static AbstractMediator recentlyAddedMediator;
	private static boolean pageChangeOperationActivated;
	private static boolean pageCreateOperationActivated;

	private static AbstractMediator deletedMediator;
	private static boolean deleteOperationPerformed;
	private static final String ATTRIBUTE_SEPERATOR = ",";
	private static final CharSequence SEQUENCE_MEDIATION_COMPONENT = "mediation-component:sequence";
	private static final CharSequence PROXY_MEDIATION_COMPONENT = "mediation-component:proxy";
	private static final CharSequence API_MEDIATION_COMPONENT = "mediation-component:api";

	public static void setDeletedMediator(AbstractMediator editPart) {
		deletedMediator = editPart;
	}

	public static AbstractMediator getDeletedMediator() {
		return deletedMediator;
	}

	public static void setDeleteOperationPerformed(boolean status) {
		deleteOperationPerformed = status;
	}

	public static boolean isDeleteOperationPerformed() {
		return deleteOperationPerformed;
	}

	public static void setPageCreateOperationActivated(boolean status) {
		pageCreateOperationActivated = status;
	}

	public static boolean isPageCreateOperationActivated() {
		return pageCreateOperationActivated;
	}

	public static void setPageChangeOperationActivated(boolean status) {
		pageChangeOperationActivated = status;
	}

	public static boolean isPageChangeOperationActivated() {
		return pageChangeOperationActivated;
	}

	public static void setRecentlyAddedMediator(AbstractMediator addedMediator) {
		recentlyAddedMediator = addedMediator;
	}

	public static AbstractMediator getRecentlyAddedMediator() {
		return recentlyAddedMediator;
	}

	public static void addBreakpointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) part).getPrimaryShape()
					.addBreakpointMark();
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).addBreakpointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
		part.setBreakpointStatus(true);
	}

	public static void removeBreakpointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) part).getPrimaryShape()
					.removeBreakpointMark();
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).removeBreakpointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
		part.setBreakpointStatus(false);
	}

	public static Map<String, String> getDetailsToMap(IBreakpoint breakpoint) {
		String message = ((ESBBreakpoint) breakpoint).getMessage();
		Map<String, String> detailsMap = new LinkedHashMap<>();
		String[] attributes = message.split(",");
		for (String string : attributes) {
			String[] keyValuePair = string.split(":");
			if (keyValuePair.length == 2) {
				detailsMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
		return detailsMap;
	}

	public static void modifyBreakpointsAfterMediatorAddition(
			AbstractMediator abstractMediator) throws CoreException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor) {

			IResource resource = getIResourceFromIEditorPart(activeEditor);

			EsbServer esbServer = getESBServerFromIEditorPart(activeEditor);

			IESBBreakpointBuilder breakpointBuilder = BreakpointBuilderFactory
					.getBreakpointBuilder(esbServer.getType().getName());

			if (breakpointBuilder != null) {
				breakpointBuilder.updateExistingBreakpoints(resource,
						abstractMediator, esbServer,
						ESBDebuggerConstants.MEDIATOR_INSERT_ACTION);
			}
			setRecentlyAddedMediator(null);
		}
	}

	public static void modifyBreakpointsAfterMediatorDeletion()
			throws CoreException {

		if (getDeletedMediator() != null) {

			IEditorPart activeEditor = EditorUtils.getActiveEditor();

			if (activeEditor instanceof EsbMultiPageEditor) {

				IResource resource = getIResourceFromIEditorPart(activeEditor);

				EsbServer esbServer = getESBServerFromIEditorPart(activeEditor);

				IESBBreakpointBuilder breakpointBuilder = BreakpointBuilderFactory
						.getBreakpointBuilder(esbServer.getType().getName());

				if (breakpointBuilder != null) {
					breakpointBuilder.updateExistingBreakpoints(resource,
							getDeletedMediator(), esbServer,
							ESBDebuggerConstants.MEDIATOR_DELETE_ACTION);
				}
				setDeletedMediator(null);
				ESBDebugerUtil.setDeleteOperationPerformed(false);
			}
		}
	}

	private static EsbServer getESBServerFromIEditorPart(
			IEditorPart activeEditor) {
		Diagram diagram = ((EsbMultiPageEditor) (activeEditor)).getDiagram();
		EsbDiagram esbDiagram = (EsbDiagram) diagram.getElement();
		EsbServer esbServer = esbDiagram.getServer();
		return esbServer;
	}

	private static IResource getIResourceFromIEditorPart(
			IEditorPart activeEditor) {
		IFile file = ((FileEditorInput) (((EsbMultiPageEditor) activeEditor)
				.getEditorInput())).getFile();
		IResource resource = (IResource) file.getAdapter(IResource.class);
		return resource;
	}

	public static void repopulateESBServerBreakpoints() throws CoreException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		for (IBreakpoint breakpoint : breakpoints) {
			DebugPlugin.getDefault().getBreakpointManager()
					.removeBreakpoint(breakpoint, false);
			DebugPlugin.getDefault().getBreakpointManager()
					.addBreakpoint(breakpoint);
		}
	}

	public static boolean isBreakpointMatches(String message,
			String breakpointMessage) {
		String[] attributes = breakpointMessage.split(ATTRIBUTE_SEPERATOR);
		for (String string : attributes) {
			if (!(mediationComponentMatches(message, string) || message
					.contains(string))) {
				return false;
			}
		}
		return true;
	}

	private static boolean mediationComponentMatches(String message,
			String string) {
		if (message.contains(SEQUENCE_MEDIATION_COMPONENT)
				&& (string.contains(PROXY_MEDIATION_COMPONENT) || string
						.contains(API_MEDIATION_COMPONENT))) {
			return true;
		}
		return false;
	}

}
