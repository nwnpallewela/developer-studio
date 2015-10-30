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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.ESBBreakpointBuilderFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * This is a utility class which contains methods related to breakpoints
 *
 */
public class ESBBreakpointTarget {

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	/**
	 * This method checks whether selected line can be assign as a line
	 * breakpoint.
	 */
	public static boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		// TODO This function should implement when source view breakpoints are
		// adding to debugger
		return false;
	}

	/**
	 * This method checks whether selected part can be assign as a diagram
	 * breakpoint.
	 * 
	 * @param part
	 * @return
	 */
	public static boolean canToggleDiagramBreakpoints(EditPart part) {
		return true;
	}

	/**
	 * This method performs the source view breakpoint insertion action
	 */
	public static void toggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		// This method should be implement to support source view breakpoints
		throw new UnsupportedOperationException(
				"Line breakpoint are not supported");
	}

	/**
	 * This method performs the graphical view breakpoint insertion action
	 * 
	 * @param part
	 * @throws CoreException
	 * @throws ESBDebuggerException
	 */
	public static void toggleDiagramBreakpoints(AbstractMediator part)
			throws CoreException, ESBDebuggerException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor) {

			IFile file = ((FileEditorInput) (((EsbMultiPageEditor) activeEditor)
					.getEditorInput())).getFile();
			Diagram diagram = ((EsbMultiPageEditor) (activeEditor))
					.getDiagram();
			EsbDiagram esbDiagram = (EsbDiagram) diagram.getElement();
			EsbServer esbServer = esbDiagram.getServer();

			IESBBreakpointBuilder breakpointBuilder = ESBBreakpointBuilderFactory
					.getBreakpointBuilder(esbServer.getType());

			IResource resource = (IResource) file.getAdapter(IResource.class);
			ESBBreakpoint breakpoint = breakpointBuilder.getESBBreakpoint(
					esbServer, resource, part);
			ESBBreakpoint existingBreakpoint = getMatchingBreakpoint(breakpoint);
			if (existingBreakpoint == null) {
				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpoint(breakpoint);
				ESBDebugerUtil.addBreakpointMark(part);
			} else {
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpoint(existingBreakpoint, true);
				ESBDebugerUtil.removeBreakpointMark(part);
			}
		}
	}

	/**
	 * This method finds similar mediator registered as a breakpoint in the
	 * BreakpointManager and returns.
	 * 
	 * @param targetBreakpoint
	 * @return ESBBreakpoint if found or null
	 */
	private static ESBBreakpoint getMatchingBreakpoint(
			ESBBreakpoint targetBreakpoint) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		for (IBreakpoint breakpoint : breakpoints) {
			try {
				ESBBreakpoint esbBreakpoint = (ESBBreakpoint) breakpoint;
				if ((esbBreakpoint.getResource()).equals(targetBreakpoint
						.getResource())) {

					if (ESBDebugerUtil.isBreakpointMatches(
							targetBreakpoint.getLocation(),
							esbBreakpoint.getLocation())) {
						return esbBreakpoint;
					} else if ((esbBreakpoint).getLineNumber() == (targetBreakpoint
							.getLineNumber() + 1)
							&& targetBreakpoint.getLineNumber() != -1) {
						return esbBreakpoint;
					}
				}
			} catch (BreakpointMarkerNotFoundException e) {
				log.error(e.getMessage(), e);
				ESBDebugerUtil
						.removeESBBreakpointFromBreakpointManager(breakpoint);
			} catch (CoreException e) {
				log.error(e.getMessage(), e);
			}

		}
		return null;
	}
}
