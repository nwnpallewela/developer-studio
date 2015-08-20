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


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.configure.ConfigureEsbNodeAction;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbDiagramEditor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;

public class ESBBreakpointAction extends ConfigureEsbNodeAction{

	private static final String COMMAND_LABEL = "Toggle Breakpoint";
	private static final String COMMAND_TOOL_TIP = "Set breakpoint for this mediator";
	private static final String ACTION_ID = "org.wos2.developerstudio.eclipse.esb.debugger.breakpoint.action";

	public ESBBreakpointAction(IWorkbenchPart part) {
		super(part);
		super.init();
		setId(ACTION_ID );
		setText(COMMAND_LABEL);
		setToolTipText(COMMAND_TOOL_TIP);
		ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT_DISABLED));
	}
	
	public void init() {
		super.init();
		
	}


	/**
	 * Utility method for retrieving the currently selected {@link EditPart}.
	 * 
	 * @return current selected {@link EditPart} or null if multiple edit parts
	 *         or no edit parts are selected.
	 */
	protected EditPart getSelectedEditPart() {
		IStructuredSelection selection = getStructuredSelection();
		if (selection.size() == 1) {
			Object selectedEP = selection.getFirstElement();
			if (selectedEP instanceof EditPart) {
				return (EditPart) selectedEP;
			}
		}
		return null;
	}
	
	/**
	 * Utility method for calculating the editing domain.
	 * 
	 * @return editing domain for this action.
	 */
	protected TransactionalEditingDomain getEditingDomain() {        
        // try adapting the workbench part
        IWorkbenchPart part = getWorkbenchPart();

        if (part != null) {
            IEditingDomainProvider edProvider = (IEditingDomainProvider) part
                .getAdapter(IEditingDomainProvider.class);

            if (edProvider != null) {
            	EditingDomain domain = edProvider.getEditingDomain();
            	
            	if (domain instanceof TransactionalEditingDomain) {
            		return (TransactionalEditingDomain) domain;
            	}
            }
        }
        
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
	public void refresh() {
		// TODO: Check whether this is necessary.
	}
	
	protected void doRun(IProgressMonitor progressMonitor) {
		EditPart selectedEP = getSelectedEditPart();
		
		Assert.isNotNull(selectedEP, "Empty selection.");
		EObject selectedObj = ((View) selectedEP.getModel()).getElement();
		IESBBreakpointTarget target = new ESBBreakpointTarget();
		if ((target != null)
				&& (target.canToggleDiagramBreakpoints(selectedEP, selectedObj))) {
			try {
				target.toggleDiagramBreakpoints(selectedEP, selectedObj);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
}

