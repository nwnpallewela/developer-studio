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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.BreakpointBuilderFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;

/**
 * This is a utility class which contains methods related to breakpoints
 *
 */
public class ESBBreakpointTarget {

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
	 * @param selection
	 * @return
	 */
	public static boolean canToggleDiagramBreakpoints(EditPart part,
			EObject selection) {
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
	 * @param selection
	 * @throws CoreException
	 * @throws MediatorNotFoundException 
	 */
	public static void toggleDiagramBreakpoints(AbstractMediator part,
			EObject selection) throws CoreException, MediatorNotFoundException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor) {

			IFile file = ((FileEditorInput) (((EsbMultiPageEditor) activeEditor)
					.getEditorInput())).getFile();
			Diagram diagram = ((EsbMultiPageEditor) (activeEditor))
					.getDiagram();
			EsbDiagram esbDiagram = (EsbDiagram) diagram.getElement();
			EsbServer esbServer = esbDiagram.getServer();

			IESBBreakpointBuilder breakpointBuilder = BreakpointBuilderFactory
					.getBreakpointBuilder(esbServer.getType());

			if (breakpointBuilder != null) {
				IResource resource = (IResource) file
						.getAdapter(IResource.class);
				boolean partReversed = part.reversed;
				ESBBreakpoint breakpoint = breakpointBuilder.getESBBreakpoint(
						esbServer, resource, selection, partReversed);
				if (breakpoint != null) {
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
		}
	}

	/**
	 * 
	 * @param targetBreakpoint
	 * @return
	 * @throws CoreException
	 */
	private static ESBBreakpoint getMatchingBreakpoint(
			ESBBreakpoint targetBreakpoint) throws CoreException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		if (breakpoints != null) {
			for (IBreakpoint breakpoint : breakpoints) {
				ESBBreakpoint esbBreakpoint = (ESBBreakpoint) breakpoint;
				if ((targetBreakpoint.getMarker().getResource())
						.equals(breakpoint.getMarker().getResource())) {
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
			}
		}
		return null;
	}
}
