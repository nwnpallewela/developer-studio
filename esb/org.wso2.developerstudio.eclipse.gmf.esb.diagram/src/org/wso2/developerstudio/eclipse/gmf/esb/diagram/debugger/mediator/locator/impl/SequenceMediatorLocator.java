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
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;

/**
 * This class contains methods related locate and get mediators in a Sequence
 */
public class SequenceMediatorLocator extends AbstractMediatorLocator {

	/**
	 * This method returns EditPart of a Sequence according to given information
	 * Map
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

			SequencesImpl sequence = (SequencesImpl) next;

			editPart = getMediator(sequence.getOutputConnector(),
					Integer.parseInt(positionArray[0]));
		}
		return editPart;
	}

}
