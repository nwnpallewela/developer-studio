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
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			Map<String, Object> info) throws MediatorNotFoundException,
			MissingAttributeException {

		EditPart editPart = null;
		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			int[] positionArray = (int[]) info
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			if (positionArray.length == NO_OF_LIST_MEDIATORS) {
				ProxyServiceImpl mainSequence = (ProxyServiceImpl) esbServer
						.eContents().get(FIRST_ELEMENT_INDEX);

				if (positionArray[FIRST_ELEMENT_INDEX]==IN_SEQUENCE_VALUE) {
					editPart = getMediatorFromMediationFlow(
							mainSequence.getOutputConnector(),
							removeOutterListSeqPositionFromArray(positionArray));
				} else {
					editPart = getMediatorFromMediationFlow(
							mainSequence.getOutSequenceOutputConnector(),
							removeOutterListSeqPositionFromArray(positionArray));
				}
			}

		} else {
			throw new MissingAttributeException(
					"Mediator Position Attribute is reqired for locate mediator in Mediation Flow");
		}
		return editPart;
	}

	private int[] removeOutterListSeqPositionFromArray(int[] positionArray) {
		int[] newPositionArray = new int[positionArray.length - 1];
		for (int index = 0; index < positionArray.length - 1; index++) {
			newPositionArray[index] = positionArray[index + 1];
		}
		return newPositionArray;
	}

}
