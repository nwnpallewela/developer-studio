/*
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class builds ESB breakpoints related to Proxy Services.
 */
public class ProxyBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,
			boolean selectedMediatorReversed) throws CoreException {

		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();
		ProxyServiceImpl proxy = (ProxyServiceImpl) next;

		String message = getInitialMessage(ESBDebuggerConstants.PROXY);
		message = addAttributeToMessage(message,
				ESBDebuggerConstants.PROXY_KEY, proxy.getName());
		String position = EMPTY_STRING;
		if (selectedMediatorReversed) {
			position = getMediatorPositionInFaultSeq(proxy.getContainer()
					.getFaultContainer().getMediatorFlow().getChildren(),
					selection);

			if (StringUtils.isEmpty(position)) {
				position = getMediatorPosition(
						proxy.getOutSequenceOutputConnector(), selection);

				message = addAttributeToMessage(message,
						ESBDebuggerConstants.SEQUENCE_TYPE,
						ESBDebuggerConstants.PROXY_OUTSEQ);
			} else {
				message = addAttributeToMessage(message,
						ESBDebuggerConstants.SEQUENCE_TYPE,
						proxy.getFaultSequenceName());
			}
		} else {
			message = addAttributeToMessage(message,
					ESBDebuggerConstants.SEQUENCE_TYPE,
					ESBDebuggerConstants.PROXY_INSEQ);
			position = getMediatorPosition(
					proxy.getOutputConnector(), selection);
		}
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
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();

		ProxyServiceImpl proxy = (ProxyServiceImpl) treeIterator.next();
		if (abstractMediator != null) {
			if (abstractMediator.reversed) {
				int position = getMediatorPosition(
						proxy.getOutSequenceOutputConnector(), abstractMediator);
				List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
						resource, position, ESBDebuggerConstants.PROXY_OUTSEQ,
						action);
				if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
						.equalsIgnoreCase(action)) {
					incrementBreakpointPosition(breakpontList);
				} else {
					decreaseBreakpointPosition(breakpontList);
				}
			} else {
				int position = getMediatorPosition(proxy.getOutputConnector(),
						abstractMediator);
				List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
						resource, position, ESBDebuggerConstants.PROXY_INSEQ,
						action);
				if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
						.equalsIgnoreCase(action)) {
					incrementBreakpointPosition(breakpontList);
				} else {
					decreaseBreakpointPosition(breakpontList);
				}
			}
		}

	}

}
