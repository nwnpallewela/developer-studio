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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EsbGroupingShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FilterMediatorGraphicalShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedSizedAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.complexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBDebugPointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.ESBDebugPointBuilderFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.CloudConnectorOperationEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class ESBDebugerUtil {

	private static AbstractMediator recentlyAddedMediator;
	private static boolean pageChangeOperationActivated;
	private static boolean pageCreateOperationActivated;

	private static AbstractMediator deletedMediator;
	private static boolean deleteOperationPerformed;
	private static final String ATTRIBUTE_SEPERATOR = ",";
	private static final String EMPTY_STRING = "";
	private static final String SPACE_CHARACTER = " ";

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public static void popUpErrorDialog(Exception e, final String message) {
		log.error(e.getMessage(), e);
		String simpleMessage = e.getMessage();
		final IStatus editorStatus = new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, simpleMessage);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(Display.getDefault().getActiveShell(),
						ESBDebuggerConstants.ERROR_MESSAGE_TAG, message,
						editorStatus);

			}
		});
	}

	/**
	 * Remove breakpoint from Breakpoint Manager
	 * 
	 * @param breakpoint
	 */
	public static void removeESBDebugpointFromBreakpointManager(
			IBreakpoint breakpoint) {
		try {
			DebugPlugin.getDefault().getBreakpointManager()
					.removeBreakpoint(breakpoint, true);
		} catch (CoreException e1) {
			log.error("Error while removing breakpoint : " + breakpoint, e1);
		}
	}

	public static void removeAllESBBreakpointsFromBreakpointManager() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		for (IBreakpoint breakpoint : breakpoints) {
			removeESBDebugpointFromBreakpointManager(breakpoint);
		}
	}

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
			if (part instanceof CloudConnectorOperationEditPart) {
				((CloudConnectorOperationEditPart) part).getPrimaryShape()
				.addBreakpointMark();
			} else {
				((FixedSizedAbstractMediator) part).getPrimaryShape()
						.addBreakpointMark();
			}
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
	
	public static void addSkippointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			if (part instanceof CloudConnectorOperationEditPart) {
				((CloudConnectorOperationEditPart) part).getPrimaryShape()
				.addSkippointMark();
			} else {
				((FixedSizedAbstractMediator) part).getPrimaryShape()
						.addSkippointMark();
			}
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).addSkippointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
		part.setSkippointStatus(true);
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
	
	public static void removeSkippointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) part).getPrimaryShape()
					.removeSkippointMark();
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).removeSkippointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
		part.setSkippointStatus(false);
	}

	public static void modifyBreakpointsAfterMediatorAddition(
			AbstractMediator abstractMediator) throws CoreException,
			ESBDebuggerException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor) {

			IResource resource = getIResourceFromIEditorPart(activeEditor);

			EsbServer esbServer = getESBServerFromIEditorPart(activeEditor);

			IESBDebugPointBuilder breakpointBuilder = ESBDebugPointBuilderFactory
					.getBreakpointBuilder(esbServer.getType());

			try {
				breakpointBuilder.updateExistingDebugPoints(resource,
						abstractMediator, esbServer,
						ESBDebuggerConstants.MEDIATOR_INSERT_ACTION);
			} catch (MediatorNotFoundException e) {
				log.info(
						"Inserted Mediator not found in a valid location for breakpoint validation",
						e);
			} finally {
				setRecentlyAddedMediator(null);
			}
		}
	}

	public static void modifyBreakpointsAfterMediatorDeletion()
			throws CoreException, ESBDebuggerException {

		if (getDeletedMediator() != null) {

			IEditorPart activeEditor = EditorUtils.getActiveEditor();

			if (activeEditor instanceof EsbMultiPageEditor) {

				IResource resource = getIResourceFromIEditorPart(activeEditor);

				EsbServer esbServer = getESBServerFromIEditorPart(activeEditor);

				IESBDebugPointBuilder breakpointBuilder = ESBDebugPointBuilderFactory
						.getBreakpointBuilder(esbServer.getType());

				try {
					breakpointBuilder.updateExistingDebugPoints(resource,
							getDeletedMediator(), esbServer,
							ESBDebuggerConstants.MEDIATOR_DELETE_ACTION);
				} catch (MediatorNotFoundException e) {
					log.info(
							"Deleted Mediator not found in a valid location for breakpoint validation",
							e);
				} finally {
					setDeletedMediator(null);
					ESBDebugerUtil.setDeleteOperationPerformed(false);
				}
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

	/**
	 * This method publish registered ESB Breakpoints to connected Server
	 * 
	 * @throws CoreException
	 */
	public static void repopulateESBServerBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		for (IBreakpoint breakpoint : breakpoints) {
			try {
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpoint(breakpoint, false);
				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpoint(breakpoint);
			} catch (CoreException e) {
				log.error(
						"Error while repopulating breakpoint with Breakpoint Manager",
						e);
			}
		}
	}

	/**
	 * This method returns true if all attributes contain in @param message is
	 * in @param breakpointMessage
	 * 
	 * @param message
	 * @param breakpointMessage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isBreakpointMatches(Map<String, Object> message,
			Map<String, Object> breakpointMessage) {
		Set<String> attributeKeys = new HashSet<String>();
		attributeKeys.addAll(message.keySet());
		attributeKeys.remove(ESBDebuggerConstants.MEDIATION_COMPONENT);
		attributeKeys.remove(ESBDebuggerConstants.EVENT);

		if (!isBreakpointPositionMatches(
				((List<Integer>) message
						.get(ESBDebuggerConstants.MEDIATOR_POSITION)),
				((List<Integer>) breakpointMessage
						.get(ESBDebuggerConstants.MEDIATOR_POSITION)))) {
			return false;
		}
		attributeKeys.remove(ESBDebuggerConstants.MEDIATOR_POSITION);
		for (String key : attributeKeys) {
			if (!((breakpointMessage.containsKey(key) && ((String) breakpointMessage
					.get(key)).trim()
					.equals(((String) message.get(key)).trim())))) {
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

	private static boolean isBreakpointPositionMatches(
			List<Integer> messagePositionArray,
			List<Integer> breakpointPositionArray) {

		return breakpointPositionArray.equals(messagePositionArray);
	}

	public static String getMethodValuesFromResource(APIResource apiResource) {
		String method = EMPTY_STRING;
		if (apiResource.isAllowGet()) {
			method += ESBDebuggerConstants.API_METHOD_GET + SPACE_CHARACTER;
		}
		if (apiResource.isAllowPost()) {
			method += ESBDebuggerConstants.API_METHOD_POST + SPACE_CHARACTER;
		}
		if (apiResource.isAllowPut()) {
			method += ESBDebuggerConstants.API_METHOD_PUT + SPACE_CHARACTER;
		}
		if (apiResource.isAllowDelete()) {
			method += ESBDebuggerConstants.API_METHOD_DELETE + SPACE_CHARACTER;
		}
		if (apiResource.isAllowOptions()) {
			method += ESBDebuggerConstants.API_METHOD_OPTIONS + SPACE_CHARACTER;
		}
		if (apiResource.isAllowHead()) {
			method += ESBDebuggerConstants.API_METHOD_HEAD + SPACE_CHARACTER;
		}
		if (apiResource.isAllowPatch()) {
			method += ESBDebuggerConstants.API_METHOD_PATCH + SPACE_CHARACTER;
		}
		method = method.trim().replace(SPACE_CHARACTER, ATTRIBUTE_SEPERATOR);
		return method;
	}

}
