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
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractInputConnectorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractOutputConnectorEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.EsbLinkEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

public class ESBBreakpointTarget implements IESBBreakpointTarget {

	BreakpointBuilderFactory breakpointBuilderFactory;

	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	@Override
	public boolean canToggleDiagramBreakpoints(EditPart part, EObject selection) {
		return true;
	}

	@Override
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		// GraphicalEditor textEditor = getEditor(part);
		if (part != null) {
			/*
			 * IResource resource = (IResource)
			 * textEditor.getEditorInput().getAdapter(IResource.class);
			 * ITextSelection textSelection = (ITextSelection) selection; int
			 * lineNumber = textSelection.getStartLine();
			 */
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager()
					.getBreakpoints(ESBDebugModelPresentation.ID);
			for (int i = 0; i < breakpoints.length; i++) {
				// IBreakpoint breakpoint = breakpoints[i];
				/*
				 * if (resource.equals(breakpoint.getMarker().getResource())) {
				 * if (((ILineBreakpoint) breakpoint).getLineNumber() ==
				 * (lineNumber + 1)) { // existing breakpoint; delete
				 * breakpoint.delete(); return; } }
				 */
			}

			// new breakpoint; create
			ESBBreakpoint lineBreakpoint = new ESBBreakpoint();
			DebugPlugin.getDefault().getBreakpointManager()
					.addBreakpoint(lineBreakpoint);
		}
	}

	@Override
	public void toggleDiagramBreakpoints(EditPart part, EObject selection)
			throws CoreException {
		
		EditPart editPart = part;
		IProject project = EditorUtils.getActiveProject();
		IEditorPart activeEditor = EditorUtils.getActiveEditor();
		if (activeEditor instanceof EsbMultiPageEditor) {
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
			TreeIterator<EObject> treeIterator = esbServer.eAllContents();
			IResource resource = (IResource) project.getAdapter(IResource.class);
			IBreakpoint breakpoint = breakpointBuilder.getESBBreakpoint(
					treeIterator, resource,selection);
			System.out.println(DebugPlugin.getDefault().getBreakpointManager().hasBreakpoints());
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
			System.out.println(breakpoint.isPersisted()+" : "+breakpoint.isRegistered());
			System.out.println(DebugPlugin.getDefault().getBreakpointManager().hasBreakpoints());
			
		}

	}

}
