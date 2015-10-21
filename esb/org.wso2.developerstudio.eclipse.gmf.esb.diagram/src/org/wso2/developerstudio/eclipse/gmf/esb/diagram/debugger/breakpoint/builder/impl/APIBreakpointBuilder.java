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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

/**
 * This class builds ESB breakpoints related to API Resources.
 */
public class APIBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,
			boolean selectedMediatorReversed) throws CoreException {
		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();
		SynapseAPIImpl api = (SynapseAPIImpl) next;

		String message = getInitialMessage(ESBDebuggerConstants.API);
		message = addAttributeToMessage(message, ESBDebuggerConstants.API_KEY,
				api.getApiName());
		EList<APIResource> apiResources = api.getResources();
		for (APIResource apiResource : apiResources) {
			if (selectedMediatorReversed) {
				String position = getMediatorPositionInFaultSeq(apiResource
						.getContainer().getFaultContainer().getMediatorFlow()
						.getChildren(), selection);

				if (StringUtils.isEmpty(position)) {
					position = getMediatorPosition(
							apiResource.getOutSequenceOutputConnector(),
							selection);
					if (StringUtils.isNotEmpty(position)) {
						message = addAPIAttributesToMessage(message,
								ESBDebuggerConstants.API_OUTSEQ, position,
								apiResource);
						break;
					}
				} else {

					message = addAPIAttributesToMessage(message,
							apiResource.getFaultSequenceName(), position,
							apiResource);
					break;
				}

			} else {

				String position = getMediatorPosition(
						apiResource.getOutputConnector(), selection);
				if (StringUtils.isNotEmpty(position)) {
					message = addAPIAttributesToMessage(message,
							ESBDebuggerConstants.API_INSEQ, position,
							apiResource);
					break;
				}
			}
		}
		return new ESBBreakpoint(resource, lineNumber, message);

	}

	private String addAPIAttributesToMessage(String message,
			String sequenceType, String position, APIResource apiResource) {
		String modifiedMessage = message;
		modifiedMessage = addAttributeToMessage(modifiedMessage,
				ESBDebuggerConstants.SEQUENCE_TYPE, sequenceType);
		modifiedMessage = addAttributeToMessage(modifiedMessage,
				ESBDebuggerConstants.MEDIATOR_POSITION, position);
		modifiedMessage = addAttributeToMessage(modifiedMessage,
				ESBDebuggerConstants.METHOD,
				getMethodValuesFromResource(apiResource));
		modifiedMessage = addURLStyleToMessage(modifiedMessage, apiResource);
		return modifiedMessage;
	}

	private String addURLStyleToMessage(String message, APIResource apiResource) {
		String modifiedMessage = message;
		switch (apiResource.getUrlStyle().getValue()) {
		case ApiResourceUrlStyle.URI_TEMPLATE_VALUE:
			modifiedMessage = addAttributeToMessage(message,
					ESBDebuggerConstants.URL_TEMPLATE,
					apiResource.getUriTemplate());
			break;
		case ApiResourceUrlStyle.URL_MAPPING_VALUE:
			modifiedMessage = addAttributeToMessage(message,
					ESBDebuggerConstants.URI_MAPPING,
					apiResource.getUrlMapping());
			break;
		default:
			break;
		}
		return modifiedMessage;
	}

	private String getMethodValuesFromResource(APIResource apiResource) {
		String method = EMPTY_STRING;
		if (apiResource.isAllowGet()) {
			method += ESBDebuggerConstants.API_METHOD_GET + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowPost()) {
			method += ESBDebuggerConstants.API_METHOD_POST + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowPut()) {
			method += ESBDebuggerConstants.API_METHOD_PUT + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowDelete()) {
			method += ESBDebuggerConstants.API_METHOD_DELETE + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowOptions()) {
			method += ESBDebuggerConstants.API_METHOD_OPTIONS + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowHead()) {
			method += ESBDebuggerConstants.API_METHOD_HEAD + SPACE_CHARACTOR;
		}
		if (apiResource.isAllowPatch()) {
			method += ESBDebuggerConstants.API_METHOD_PATCH + SPACE_CHARACTOR;
		}
		method = method.trim();
		method.replace(SPACE_CHARACTOR, ATTRIBUTE_SEPERATOR);
		return method;
	}

	/**
	 * This method update all breakpoints affected by the mediator insertion or
	 * deletion action specified by action parameter and mediator object
	 * specified by abstractMediator parameter.
	 */
	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws CoreException {
		// TODO Auto-generated method stub

	}

}
