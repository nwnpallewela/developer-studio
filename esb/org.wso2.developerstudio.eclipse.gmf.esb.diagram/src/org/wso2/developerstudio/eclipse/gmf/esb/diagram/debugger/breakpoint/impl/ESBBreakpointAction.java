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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.configure.ConfigureEsbNodeAction;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * A {@link AbstractActionHandler} used to hook-up action for set and clear
 * breakpoints in esb design view editor.
 */
public class ESBBreakpointAction extends ConfigureEsbNodeAction {

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	/**
	 * Creates a new {@link ESBBreakpointAction} instance.
	 * 
	 * @param part
	 *            {@link IWorkbenchPart} instance.
	 */
	public ESBBreakpointAction(IWorkbenchPart part) {
		super(part);
		super.init();
		setId(ESBDebuggerConstants.ESBBREAKPOINT_ACTION_ID);
		setText(ESBDebuggerConstants.ESBBREAKPOINT_COMMAND_LABEL);
		setToolTipText(ESBDebuggerConstants.ESBBREAKPOINT_COMMAND_TOOL_TIP);
		ISharedImages workbenchImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setHoverImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		setDisabledImageDescriptor(workbenchImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT_DISABLED));
	}

	/**
	 * Utility method for retrieving the currently selected {@link EditPart}.
	 * 
	 * @return current selected {@link EditPart} or null if multiple edit parts
	 *         or no edit parts are selected.
	 */
	@Override
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
	@Override
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
	@Override
	protected void doRun(IProgressMonitor progressMonitor) {

		EditPart selectedEP = getSelectedEditPart();
		if (selectedEP instanceof AbstractMediator) {
			if (ESBBreakpointTarget.canToggleDiagramBreakpoints(selectedEP)) {
				try {
					ESBBreakpointTarget
							.toggleDiagramBreakpoints((AbstractMediator) selectedEP);
				} catch (CoreException e) {
					log.error("Error while registering the breakpoint", e);
				} catch (BreakpointMarkerNotFoundException e) {
					log.error(e.getMessage(), e);
				} catch (ESBDebuggerException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

}
