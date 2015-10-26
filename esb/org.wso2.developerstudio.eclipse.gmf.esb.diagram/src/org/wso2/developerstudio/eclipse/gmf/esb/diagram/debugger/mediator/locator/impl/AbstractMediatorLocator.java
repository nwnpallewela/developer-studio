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
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbElement;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbLink;
import org.wso2.developerstudio.eclipse.gmf.esb.Mediator;
import org.wso2.developerstudio.eclipse.gmf.esb.OutputConnector;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.AbstractESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.IMediatorLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

public abstract class AbstractMediatorLocator implements IMediatorLocator {

	protected static final String MEDIATOR_POSITION_SEPERATOR = " ";
	protected static final int FIRST_ELEMENT_INDEX = 0;
	private static final String EMPTY_STRING = "";

	protected EditPart getMediatorFromMediationFlow(
			OutputConnector tempConnector, int[] mediatorPosition) throws MediatorNotFoundException {
		int count = 0;
		while (tempConnector != null) {
			EsbLink outgoingLink = tempConnector.getOutgoingLink();
			if (outgoingLink != null && outgoingLink.getTarget() != null) {
				EObject mediator = outgoingLink.getTarget().eContainer();
				if (count == mediatorPosition[FIRST_ELEMENT_INDEX]) {
					return EditorUtils.getEditpart(mediator);
				} else {
					count++;
					if (mediator instanceof Mediator) {
						tempConnector = AbstractESBBreakpointBuilder
								.getOutputConnector((Mediator) mediator);
					}
				}
			}else{
				throw new MediatorNotFoundException(
						"Mediation flow diagram error");
			}
		}
		return null;
	}

	protected EditPart getMediatorInFaultSeq(EList<EsbElement> children,
			int[] positionArray) throws MediatorNotFoundException {
		int count = 0;
		int position = positionArray[FIRST_ELEMENT_INDEX];
		for (EsbElement mediator : children) {
			if (count == position) {
				return EditorUtils.getEditpart(mediator);
			} else {
				count++;
			}
		}
		throw new MediatorNotFoundException(
				"Breakpoint position value is invalid");
	}
	
	protected String getFaultSequenceName(EObject element) {
		String faultSeqName = null;
		if(element instanceof ProxyServiceImpl){
			faultSeqName=((ProxyServiceImpl)element).getFaultSequenceName();
		}else if(element instanceof APIResource){
			faultSeqName=((APIResource)element).getFaultSequenceName();
		}
		if(faultSeqName!=null){
			return faultSeqName;
		}else{
			return EMPTY_STRING;
		}
	}

}
