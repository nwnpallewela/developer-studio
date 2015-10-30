/* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class builds ESB breakpoints related to Main Sequence.
 */
public class MainSequenceBreakpointBuilder extends AbstractESBBreakpointBuilder {

	private static final int OUT_SEQ_POSITION = 1;
	private static final int IN_SEQ_POSITION = 0;

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 * 
	 * @throws MediatorNotFoundException
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, AbstractMediator part) throws CoreException,
			MediatorNotFoundException {

		int lineNumber = -1;
		ProxyServiceImpl mainSequence = (ProxyServiceImpl) esbServer
				.eContents().get(INDEX_OF_FIRST_ELEMENT);

		Map<String, Object> attributeMap = setInitialAttributes(ESBDebuggerConstants.MAIN_SEQUENCE);
		attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE,
				ESBDebuggerConstants.NAMED);
		attributeMap.put(ESBDebuggerConstants.SEQUENCE_KEY,
				ESBDebuggerConstants.MAIN);
		int listSeqPosition;
		List<Integer> position = null;
		EObject selection = ((View) part.getModel()).getElement();
		if (part.reversed) {
			listSeqPosition = OUT_SEQ_POSITION;
			position = getMediatorPosition(
					mainSequence.getOutSequenceOutputConnector(), selection);
		} else {
			listSeqPosition = IN_SEQ_POSITION;
			position = getMediatorPosition(mainSequence.getOutputConnector(),
					selection);
		}
		position.add(INDEX_OF_FIRST_ELEMENT, listSeqPosition);
		attributeMap.put(ESBDebuggerConstants.MEDIATOR_POSITION, position);
		return new ESBBreakpoint(resource, lineNumber, attributeMap);
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

		ProxyServiceImpl mainSequence = (ProxyServiceImpl) esbServer
				.eContents().get(INDEX_OF_FIRST_ELEMENT);

		String listSequenceNumber = EMPTY_STRING + OUT_SEQ_POSITION;
		if (abstractMediator.reversed) {
			List<Integer> position = getMediatorPosition(
					mainSequence.getOutSequenceOutputConnector(),
					abstractMediator);
			List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, listSequenceNumber, action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}
		} else {
			listSequenceNumber = EMPTY_STRING + IN_SEQ_POSITION;
			List<Integer> position = getMediatorPosition(
					mainSequence.getOutputConnector(), abstractMediator);
			List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, listSequenceNumber, action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}
		}
	}

}
