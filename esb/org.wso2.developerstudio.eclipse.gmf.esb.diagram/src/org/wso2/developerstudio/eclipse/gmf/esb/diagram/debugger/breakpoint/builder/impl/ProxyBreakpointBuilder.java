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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceContainerEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceSequenceAndEndpointContainerEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class builds ESB breakpoints related to Proxy Services.
 */
public class ProxyBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 * 
	 * @throws ESBDebuggerException
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, AbstractMediator part) throws CoreException,
			ESBDebuggerException {

		int lineNumber = -1;
		ProxyServiceImpl proxy = (ProxyServiceImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);
		Map<String, Object> attributeMap = setInitialAttributes(ESBDebuggerConstants.PROXY);
		attributeMap.put(ESBDebuggerConstants.PROXY_KEY, proxy.getName());
		List<Integer> position = null;
		EObject selection = ((View) part.getModel()).getElement();

		EditPart container = getContainerFromEditPart(part,
				ProxyServiceContainerEditPart.class);
		if (container instanceof ProxyServiceSequenceAndEndpointContainerEditPart) {
			if (part.reversed) {
				position = getMediatorPosition(
						proxy.getOutSequenceOutputConnector(), selection);
				attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE,
						ESBDebuggerConstants.PROXY_OUTSEQ);
			} else {
				position = getMediatorPosition(proxy.getOutputConnector(),
						selection);
				attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE,
						ESBDebuggerConstants.PROXY_INSEQ);
			}
		} else {
			position = getMediatorPositionInFaultSeq(proxy.getContainer()
					.getFaultContainer().getMediatorFlow().getChildren(),
					selection);
			attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE,
					getFaultSequenceName(proxy));
		}
		attributeMap.put(ESBDebuggerConstants.MEDIATOR_POSITION, position);
		return new ESBBreakpoint(resource, lineNumber, attributeMap);
	}

	/**
	 * This method update all breakpoints affected by the mediator insertion or
	 * deletion action of specified by action parameter and mediator object
	 * specified by abstractMediator parameter.
	 * 
	 * @throws MediatorNotFoundException
	 */
	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws MediatorNotFoundException {
		ProxyServiceImpl proxy = (ProxyServiceImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);
		if (abstractMediator.reversed) {
			List<Integer> position = getMediatorPosition(
					proxy.getOutSequenceOutputConnector(), abstractMediator);
			List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, ESBDebuggerConstants.PROXY_OUTSEQ,
					action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}
		} else {
			List<Integer> position = getMediatorPosition(
					proxy.getOutputConnector(), abstractMediator);
			List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, ESBDebuggerConstants.PROXY_INSEQ,
					action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}
		}

	}

}
