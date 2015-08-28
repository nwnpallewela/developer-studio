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
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionHandler;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.configure.ConfigureEsbNodeAction;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * A {@link AbstractActionHandler} used to hook-up action for set and clear
 * breakpoints in esb design view editor.
 */
public class ESBBreakpointAction extends ConfigureEsbNodeAction {

	/**
	 * Creates a new {@link ESBBreakpointAction} instance.
	 * 
	 * @param part
	 *            {@link IWorkbenchPart} instance.
	 */
	public ESBBreakpointAction(IWorkbenchPart part) {
		super(part);
		super.init();
		setId(ESBDebuggerConstants.ACTION_ID);
		setText(ESBDebuggerConstants.COMMAND_LABEL);
		setToolTipText(ESBDebuggerConstants.COMMAND_TOOL_TIP);
		ISharedImages workbenchImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setHoverImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setDisabledImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT_DISABLED));
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
	 * This method performs the action when click the menu item Toggle
	 * Breakpoint
	 */
	protected void doRun(IProgressMonitor progressMonitor) {

		EditPart selectedEP = getSelectedEditPart();
		Assert.isNotNull(selectedEP, ESBDebuggerConstants.EMPTY_SELECTION);
		EObject selectedObj = ((View) selectedEP.getModel()).getElement();
		IESBBreakpointTarget target = new ESBBreakpointTarget();
		if (target.canToggleDiagramBreakpoints(selectedEP, selectedObj)) {
			try {
				target.toggleDiagramBreakpoints(selectedEP, selectedObj);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
