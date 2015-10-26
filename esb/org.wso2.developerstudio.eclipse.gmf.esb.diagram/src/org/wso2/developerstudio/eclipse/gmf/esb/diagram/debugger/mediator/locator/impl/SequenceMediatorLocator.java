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

import java.util.Map;

import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MissingAttributeException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;

/**
 * This class contains methods related locate and get mediators in a Sequence
 */
public class SequenceMediatorLocator extends AbstractMediatorLocator {

	/**
	 * This method returns EditPart of a Sequence according to given information
	 * Map
	 * 
	 * @throws MediatorNotFoundException
	 * @throws MissingAttributeException 
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			Map<String, Object> info) throws MediatorNotFoundException, MissingAttributeException {
		EditPart editPart = null;

		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			int[] positionArray = (int[]) info
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			SequencesImpl sequence = (SequencesImpl) esbServer.eContents().get(
					FIRST_ELEMENT_INDEX);

			editPart = getMediatorFromMediationFlow(
					sequence.getOutputConnector(), positionArray);
		}else{
			throw new MissingAttributeException("Mediator Position Attribute is reqired for locate mediator in Mediation Flow");
		}
		return editPart;
	}

}
