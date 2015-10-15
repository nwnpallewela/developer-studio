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

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class contains methods related locate and get mediators in a Main
 * Sequence
 */
public class MainSequenceMediatorLocator extends AbstractMediatorLocator {

	private static final String IN_SEQUENCE_VALUE = "0";
	private static final int NO_OF_LIST_MEDIATORS = 2;

	/**
	 * This method returns EditPart of a Main Sequence according to given
	 * information Map
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			Map<String, String> info) {

		EditPart editPart = null;
		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			String position = info.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			String[] positionArray = position
					.split(MEDIATOR_POSITION_SEPERATOR);

			TreeIterator<EObject> treeIterator = esbServer.eAllContents();
			EObject next = treeIterator.next();

			ProxyServiceImpl mainSequence = (ProxyServiceImpl) next;

			if (positionArray.length == NO_OF_LIST_MEDIATORS
					&& IN_SEQUENCE_VALUE.equals(positionArray[0].trim())) {

				editPart = getMediator(mainSequence.getOutputConnector(),
						Integer.parseInt(positionArray[1]));

			} else if (positionArray.length == NO_OF_LIST_MEDIATORS) {

				editPart = getMediator(
						mainSequence.getOutSequenceOutputConnector(),
						Integer.parseInt(positionArray[1]));
			}

		}
		return editPart;
	}

}
