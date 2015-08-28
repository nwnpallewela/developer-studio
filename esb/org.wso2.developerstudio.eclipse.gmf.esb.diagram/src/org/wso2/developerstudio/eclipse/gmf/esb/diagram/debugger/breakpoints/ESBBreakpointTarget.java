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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

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
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;

public class ESBBreakpointTarget implements IESBBreakpointTarget {

	BreakpointBuilderFactory breakpointBuilderFactory;

	/**
	 * This method checks whether selected part can be assign as a line
	 * breakpoint.
	 */
	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	/**
	 * This method checks whether selected part can be assign as a diagram
	 * breakpoint.
	 */
	@Override
	public boolean canToggleDiagramBreakpoints(EditPart part, EObject selection) {
		return true;
	}

	/**
	 * This method performs the source view breakpoint insertion action
	 */
	@Override
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
	}

	/**
	 * This method performs the graphical view breakpoint insertion action
	 */
	@Override
	public void toggleDiagramBreakpoints(EditPart part, EObject selection)
			throws CoreException {

		IEditorPart activeEditor = EditorUtils.getActiveEditor();

		if (activeEditor instanceof EsbMultiPageEditor && part instanceof AbstractMediator) {

			boolean partReversed = ((AbstractMediator) part).reversed;
			IProject project = EditorUtils.getActiveProject();
			Diagram diagram = ((EsbMultiPageEditor) (activeEditor))
					.getDiagram();
			EsbDiagram esbDiagram = (EsbDiagram) diagram.getElement();
			EsbServer esbServer = esbDiagram.getServer();
			esbServer.setLockmode(true);

			if (breakpointBuilderFactory == null) {
				breakpointBuilderFactory = new BreakpointBuilderFactory();
			}

			IESBBreakpointBuilder breakpointBuilder = breakpointBuilderFactory
					.getBreakpointBuilder(esbServer.getType().getName());
			if (breakpointBuilder != null) {
				IResource resource = (IResource) project
						.getAdapter(IResource.class);

				IBreakpoint breakpoint = breakpointBuilder.getESBBreakpoint(
						esbServer, resource, selection, partReversed);

				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpoint(breakpoint);
			}
			/*
			 * System.out.println(breakpoint.isPersisted() + " : " +
			 * breakpoint.isRegistered());
			 */

		}
	}
}
