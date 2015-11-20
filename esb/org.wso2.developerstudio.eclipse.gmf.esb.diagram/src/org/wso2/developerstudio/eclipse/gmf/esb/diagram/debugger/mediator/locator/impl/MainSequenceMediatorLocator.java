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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.impl;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.DebugpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MissingAttributeException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class contains methods related locate and get mediators in a Main
 * Sequence
 */
public class MainSequenceMediatorLocator extends AbstractMediatorLocator {

	private static final int IN_SEQUENCE_VALUE = 0;
	private static final int NO_OF_LIST_MEDIATORS = 2;

	/**
	 * This method returns EditPart of a Main Sequence according to given
	 * information Map
	 * 
	 * @throws MediatorNotFoundException
	 * @throws MissingAttributeException
	 * @throws CoreException 
	 * @throws DebugpointMarkerNotFoundException 
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			ESBDebugPoint breakpoint) throws MediatorNotFoundException,
			MissingAttributeException, DebugpointMarkerNotFoundException, CoreException {
		Map<String,Object> info = breakpoint.getLocation();
		EditPart editPart = null;
		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			@SuppressWarnings("unchecked")
			List<Integer> positionArray = (List<Integer>) info
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			if (positionArray.size() == NO_OF_LIST_MEDIATORS) {
				ProxyServiceImpl mainSequence = (ProxyServiceImpl) esbServer
						.eContents().get(INDEX_OF_FIRST_ELEMENT);

				if (positionArray.get(INDEX_OF_FIRST_ELEMENT) == IN_SEQUENCE_VALUE) {
					editPart = getMediatorFromMediationFlow(
							mainSequence.getOutputConnector(), positionArray);
				} else {
					editPart = getMediatorFromMediationFlow(
							mainSequence.getOutSequenceOutputConnector(),
							positionArray);
				}
			}

		} else {
			throw new MissingAttributeException(
					"Mediator Position Attribute is reqired for locate mediator in Mediation Flow");
		}
		return editPart;
	}
}
