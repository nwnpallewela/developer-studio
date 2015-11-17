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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbElement;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.TemplateImpl;

public class TemplateBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selected editpart
	 * 
	 * @throws MediatorNotFoundException
	 * @throws CoreException
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, AbstractMediator part, String commandArguement)
			throws MediatorNotFoundException, CoreException {

		TemplateImpl template = (TemplateImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);

		if (template.getChild() instanceof SequencesImpl) {
			EsbElement sequnce = template.getChild();
			Map<String, Object> attributeMap = setInitialAttributes(ESBDebuggerConstants.TEMPLATE,commandArguement);

			attributeMap.put(ESBDebuggerConstants.TEMPLATE_KEY,
					template.getName());
			EObject selection = ((View) part.getModel()).getElement();
			List<Integer> position = getMediatorPosition(
					((SequencesImpl) sequnce).getOutputConnector(), selection);
			attributeMap.put(ESBDebuggerConstants.MEDIATOR_POSITION, position);
			int lineNumber = -1;
			return new ESBBreakpoint(resource, lineNumber, attributeMap);
		} else {
			throw new UnsupportedOperationException(
					"Breakpoint Integration not supported for template : "
							+ template.getChild());
		}
	}

	/**
	 * This method update all breakpoints affected by the mediator insertion or
	 * deletion action specified by action parameter and mediator object
	 * specified by abstractMediator parameter.
	 * 
	 * @throws MediatorNotFoundException
	 */
	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws MediatorNotFoundException {
		TemplateImpl template = (TemplateImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);

		if (template.getChild() instanceof SequencesImpl) {
			EsbElement sequnce = template.getChild();
			List<Integer> position = getMediatorPosition(
					((SequencesImpl) sequnce).getOutputConnector(),
					abstractMediator);
			List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, EMPTY_STRING, action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}

		}
	}

}
