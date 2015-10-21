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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

public class APIMediatorLocator extends AbstractMediatorLocator {

	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			Map<String, String> info) {
		EditPart editPart = null;

		if (info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)
				&& info.containsKey(ESBDebuggerConstants.SEQUENCE_TYPE)) {

			String position = info.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			String[] positionArray = position
					.split(MEDIATOR_POSITION_SEPERATOR);
			String sequenceType = info.get(ESBDebuggerConstants.SEQUENCE_TYPE);
			TreeIterator<EObject> treeIterator = esbServer.eAllContents();
			EObject next = treeIterator.next();

			SynapseAPIImpl api = (SynapseAPIImpl) next;
			APIResource apiResource = getMatchingAPIResource(api, info);
			if (sequenceType.equals(ESBDebuggerConstants.API_INSEQ)) {

				editPart = getMediator(apiResource.getOutputConnector(),
						Integer.parseInt(positionArray[0]));

			} else if (sequenceType.equals(ESBDebuggerConstants.API_OUTSEQ)) {

				editPart = getMediator(
						apiResource.getOutSequenceOutputConnector(),
						Integer.parseInt(positionArray[0]));
			} else {
				editPart = getMediatorInFaultSeq(apiResource.getContainer()
						.getFaultContainer().getMediatorFlow().getChildren(),
						positionArray);
			}
		}
		return editPart;
	}

	private APIResource getMatchingAPIResource(SynapseAPIImpl api,
			Map<String, String> info) {
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
					info.get(ESBDebuggerConstants.METHOD))) {
				if ((info.containsKey(urlStyle) && urlValue.equals(info
						.get(urlStyle)))
						|| (StringUtils.isEmpty(urlStyle) && !(info
								.containsKey(ESBDebuggerConstants.URI_MAPPING) || info
								.containsKey(ESBDebuggerConstants.URL_TEMPLATE)))) {
					return apiResource;
				}
			}
		}
		return null;
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
