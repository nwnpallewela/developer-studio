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

/**
 * This class builds ESB breakpoints related to Template Sequences.
 */
package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbElement;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.TemplateImpl;

public class TemplateBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection, boolean reversed)
			throws CoreException {

		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();

		TemplateImpl template = (TemplateImpl) next;
		EsbElement sequnce = template.getChild();
		String message = getInitialMessage(ESBDebuggerConstants.TEMPLATE);

		message = addAttributeToMessage(message,
				ESBDebuggerConstants.TEMPLATE_KEY, template.getName());

		String position = getMediatorPosition(
				((SequencesImpl) sequnce).getOutputConnector(), selection);

		message = addAttributeToMessage(message,
				ESBDebuggerConstants.MEDIATOR_POSITION, position);

		return new ESBBreakpoint(resource, lineNumber, message);
	}

	/**
	 * This method update all breakpoints affected by the mediator insertion or
	 * deletion action specified by action parameter and mediator object
	 * specified by abstractMediator parameter.
	 */
	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws CoreException {
		// TODO Auto-generated method stub

	}

}
