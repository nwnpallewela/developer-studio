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
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MissingAttributeException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

public class APIMediatorLocator extends AbstractMediatorLocator {

	/**
	 * This method returns EditPart of a API according to given information Map
	 * 
	 * @throws MediatorNotFoundException
	 * @throws MissingAttributeException
	 */
	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			Map<String, Object> info) throws MediatorNotFoundException,
			MissingAttributeException {
		EditPart editPart = null;

		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)
				&& info.containsKey(ESBDebuggerConstants.SEQUENCE_TYPE)) {

			@SuppressWarnings("unchecked")
			List<Integer> positionArray = (List<Integer>) info
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			String sequenceType = (String) info
					.get(ESBDebuggerConstants.SEQUENCE_TYPE);
			SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(
					INDEX_OF_FIRST_ELEMENT);
			APIResource apiResource = getMatchingAPIResource(api, info);
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
						apiResource.getOutSequenceOutputConnector(),
						positionArray);
			}
		} else {
			throw new MissingAttributeException(
					"Breakpoint Attribute list missing reqired attributes");
		}
		return editPart;
	}

	private APIResource getMatchingAPIResource(SynapseAPIImpl api,
			Map<String, Object> info) {
		EList<APIResource> apiResources = api.getResources();
		for (APIResource apiResource : apiResources) {
			String urlValue = "";
			String urlStyle = "";
			switch (apiResource.getUrlStyle().getValue()) {
			case ApiResourceUrlStyle.URI_TEMPLATE_VALUE:
				urlValue = apiResource.getUriTemplate();
				urlStyle = ESBDebuggerConstants.URL_TEMPLATE;
				break;
			case ApiResourceUrlStyle.URL_MAPPING_VALUE:
				urlValue = apiResource.getUrlMapping();
				urlStyle = ESBDebuggerConstants.URI_MAPPING;
				break;
			default:
				break;
			}
			if (isMethodEqual(apiResource,
					((String) info.get(ESBDebuggerConstants.METHOD)))) {
				if ((info.containsKey(urlStyle) && urlValue.equals(info
						.get(urlStyle)))
						|| (StringUtils.isEmpty(urlStyle) && !(info
								.containsKey(ESBDebuggerConstants.URI_MAPPING) || info
								.containsKey(ESBDebuggerConstants.URL_TEMPLATE)))
						|| isMappingEqualWithBreakpointEvent(info, urlStyle,
								urlValue)) {
					return apiResource;
				}
			}
		}
		return null;
	}

	private boolean isMappingEqualWithBreakpointEvent(Map<String, Object> info,
			String urlStyle, String urlValue) {
		if (info.containsKey(ESBDebuggerConstants.MAPPING_URL_TYPE) && (urlValue
				.equals(info.get(ESBDebuggerConstants.MAPPING_URL_TYPE)) || (StringUtils
				.isEmpty(urlStyle) && !(info
				.containsKey(ESBDebuggerConstants.MAPPING_URL_TYPE))))) {
			return true;
		}
		return false;
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
