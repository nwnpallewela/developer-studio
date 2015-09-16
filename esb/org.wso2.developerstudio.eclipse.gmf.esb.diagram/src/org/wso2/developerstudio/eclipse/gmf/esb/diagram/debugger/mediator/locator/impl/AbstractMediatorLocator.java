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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbElement;
import org.wso2.developerstudio.eclipse.gmf.esb.Mediator;
import org.wso2.developerstudio.eclipse.gmf.esb.OutputConnector;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.AbstractESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.IMediatorLocator;

public abstract class AbstractMediatorLocator implements IMediatorLocator {

	protected static final String MEDIATOR_POSITION_SEPERATOR = " ";
	
	protected EditPart getMediator(OutputConnector tempConnector,
			int mediatorPosition) {
		int count = 0;
		while (tempConnector != null) {
			EObject mediator = tempConnector.getOutgoingLink().getTarget()
					.eContainer();
			if (count == mediatorPosition) {
				EditPart editpart = EditorUtils.getEditpart(mediator);
				return editpart;
			} else {
				count++;
				tempConnector = AbstractESBBreakpointBuilder
						.getOutputConnector((Mediator) mediator);
			}
		}
		return null;
	}
	
	protected EditPart getMediatorInFaultSeq(EList<EsbElement> children,
			String[] positionArray) {
		int count = 0;
		int position = Integer.parseInt(positionArray[0]);
		for (EsbElement mediator : children) {
			if (count == position) {
				EditPart editpart = EditorUtils.getEditpart(mediator);
				return editpart;
			} else {
				count++;
			}
		}
		return null;
	}

}
