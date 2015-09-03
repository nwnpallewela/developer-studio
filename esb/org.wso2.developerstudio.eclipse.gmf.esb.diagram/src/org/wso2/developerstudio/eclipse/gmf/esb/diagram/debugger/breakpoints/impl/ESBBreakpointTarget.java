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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.impl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedSizedAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;

public class ESBBreakpointTarget {

	/**
	 * This method checks whether selected part can be assign as a line
	 * breakpoint.
	 */
	public static boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	/**
	 * This method checks whether selected part can be assign as a diagram
	 * breakpoint.
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
				ESBDebuggerConstants.LINE_BREAKPOINT_NOT_SUPPORTED);
	}

	/**
	 * This method performs the graphical view breakpoint insertion action
	 */
	public static void toggleDiagramBreakpoints(AbstractMediator part,
			EObject selection) throws CoreException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor) {

			boolean partReversed = part.reversed;
			IFile file = ((FileEditorInput)(((EsbMultiPageEditor)activeEditor).getEditorInput())).getFile();
			Diagram diagram = ((EsbMultiPageEditor) (activeEditor))
					.getDiagram();
			EsbDiagram esbDiagram = (EsbDiagram) diagram.getElement();
			EsbServer esbServer = esbDiagram.getServer();

			IESBBreakpointBuilder breakpointBuilder = BreakpointBuilderFactory
					.getBreakpointBuilder(esbServer.getType().getName());

			if (breakpointBuilder != null) {
				IResource resource = (IResource) file
						.getAdapter(IResource.class);

				ESBBreakpoint breakpoint = breakpointBuilder.getESBBreakpoint(
						esbServer, resource, selection, partReversed);
				ESBBreakpoint existingBreakpoint = getMatchingBreakpoint(breakpoint);
				if (existingBreakpoint == null) {
					DebugPlugin.getDefault().getBreakpointManager()
							.addBreakpoint(breakpoint);
					((FixedSizedAbstractMediator)part).getPrimaryShape().addBreakpointMark();
				} else {
					DebugPlugin.getDefault().getBreakpointManager()
							.removeBreakpoint(existingBreakpoint, true);
					((FixedSizedAbstractMediator)part).getPrimaryShape().removeBreakpointMark();
					// existingBreakpoint.delete();
				}

			}
			// ((FixedSizedAbstractMediator)part).getPrimaryShape().changeBreakpointMediatorIcon("BreakpointAdded");

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
				.getBreakpointManager().getBreakpoints();
		for (IBreakpoint breakpoint : breakpoints) {
			/*
			 * System.out.println("all breakpoints: " +
			 * breakpoint.getModelIdentifier());
			 */
			if ((targetBreakpoint.getMarker().getResource()).equals(breakpoint
					.getMarker().getResource())) {
				if ((((ESBBreakpoint) breakpoint).getMessage())
						.equals(targetBreakpoint.getMessage())) {
					return (ESBBreakpoint) breakpoint;
				} else if (((ESBBreakpoint) breakpoint).getLineNumber() == (targetBreakpoint
						.getLineNumber() + 1)
						&& targetBreakpoint.getLineNumber() != -1) {
					return (ESBBreakpoint) breakpoint;
				}
			}
		}
		return null;
	}

}
