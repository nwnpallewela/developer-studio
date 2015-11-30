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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.DebugPointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MissingAttributeException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBAPIDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.AbstractESBDebugPointMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

public class APIMediatorLocator extends AbstractMediatorLocator {

	/**
	 * This method returns EditPart of a API according to given information Map
	 * 
	 * @throws MediatorNotFoundException
	 * @throws MissingAttributeException
	 * @throws CoreException
	 * @throws DebugPointMarkerNotFoundException
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			ESBDebugPoint breakpoint) throws MediatorNotFoundException,
			MissingAttributeException, DebugPointMarkerNotFoundException,
			CoreException {
		EditPart editPart = null;
		ESBAPIDebugPointMessage debugPointMessage = (ESBAPIDebugPointMessage) breakpoint
				.getLocation();
		List<Integer> positionArray = debugPointMessage.getSequence().getApi()
				.getMediatorPosition().getPosition();
		String sequenceType = debugPointMessage.getSequence().getApi()
				.getSequenceType();
		SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);
		APIResource apiResource = getMatchingAPIResource(api, debugPointMessage);
		if (sequenceType == null
				|| sequenceType.equals(getFaultSequenceName(apiResource))) {
			editPart = getMediatorInFaultSeq(apiResource.getContainer()
					.getFaultContainer().getMediatorFlow().getChildren(),
					positionArray);
		} else if (sequenceType.equals(ESBDebuggerConstants.API_INSEQ)) {

			editPart = getMediatorFromMediationFlow(
					apiResource.getOutputConnector(), positionArray);

		} else if (sequenceType.equals(ESBDebuggerConstants.API_OUTSEQ)) {

			editPart = getMediatorFromMediationFlow(
					apiResource.getOutSequenceOutputConnector(), positionArray);
		}
		return editPart;
	}

	private APIResource getMatchingAPIResource(SynapseAPIImpl api,
			ESBAPIDebugPointMessage debugPointMessage)
			throws MediatorNotFoundException {
		EList<APIResource> apiResources = api.getResources();
		for (APIResource apiResource : apiResources) {
			String urlValue = "";
			String urlValueOfMessage = "";
			if (isMethodEqual(apiResource, debugPointMessage.getSequence()
					.getApi().getResourse().getMethod())) {
				switch (apiResource.getUrlStyle().getValue()) {
				case ApiResourceUrlStyle.URI_TEMPLATE_VALUE:
					urlValue = apiResource.getUriTemplate();
					urlValueOfMessage = debugPointMessage.getSequence()
							.getApi().getResourse().getUriTemplate();
					break;
				case ApiResourceUrlStyle.URL_MAPPING_VALUE:
					urlValue = apiResource.getUrlMapping();
					urlValueOfMessage = debugPointMessage.getSequence()
							.getApi().getResourse().getUrlMapping();
					break;
				default:
					break;
				}
				if (urlValueOfMessage != null
						&& urlValueOfMessage.endsWith(urlValue)) {
					return apiResource;
				}
			}
		}
		throw new MediatorNotFoundException(
				"Matching API Resource not found for the specific location of mediator: ");
	}

	private boolean isMethodEqual(APIResource apiResource, String methodValue) {

		if (methodValue == null) {
			if (StringUtils.isEmpty(ESBDebugerUtil
					.getMethodValuesFromResource(apiResource))) {
				return true;
			}
		} else if (methodValue.equalsIgnoreCase(ESBDebugerUtil
				.getMethodValuesFromResource(apiResource))) {
			return true;
		}
		return false;
	}

}
