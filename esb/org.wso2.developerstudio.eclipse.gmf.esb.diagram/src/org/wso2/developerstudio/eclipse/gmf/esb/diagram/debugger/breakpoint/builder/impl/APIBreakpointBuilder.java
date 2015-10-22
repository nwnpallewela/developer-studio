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

import java.util.List;
import java.util.Map;

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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
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

		Map<String, String> attributeMap = setInitialAttributes(ESBDebuggerConstants.API);
		attributeMap.put(ESBDebuggerConstants.API_KEY, api.getApiName());
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
						attributeMap = addAPIAttributesToMessage(attributeMap,
								ESBDebuggerConstants.API_OUTSEQ, position,
								apiResource);
						break;
					}
				} else {

					attributeMap = addAPIAttributesToMessage(attributeMap,
							apiResource.getFaultSequenceName(), position,
							apiResource);
					break;
				}

			} else {

				String position = getMediatorPosition(
						apiResource.getOutputConnector(), selection);
				if (StringUtils.isNotEmpty(position)) {
					attributeMap = addAPIAttributesToMessage(attributeMap,
							ESBDebuggerConstants.API_INSEQ, position,
							apiResource);
					break;
				}
			}
		}
		return new ESBBreakpoint(resource, lineNumber, attributeMap);

	}

	private Map<String, String> addAPIAttributesToMessage(
			Map<String, String> attributeMap, String sequenceType,
			String position, APIResource apiResource) {
		attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE, sequenceType);
		attributeMap.put(ESBDebuggerConstants.MEDIATOR_POSITION, position);
		attributeMap.put(ESBDebuggerConstants.METHOD,
				ESBDebugerUtil.getMethodValuesFromResource(apiResource));
		attributeMap = addURLStyleToMessage(attributeMap, apiResource);
		return attributeMap;
	}

	private Map<String, String> addURLStyleToMessage(
			Map<String, String> attributeMap, APIResource apiResource) {
		switch (apiResource.getUrlStyle().getValue()) {
		case ApiResourceUrlStyle.URI_TEMPLATE_VALUE:
			attributeMap.put(ESBDebuggerConstants.URL_TEMPLATE,
					apiResource.getUriTemplate());
			break;
		case ApiResourceUrlStyle.URL_MAPPING_VALUE:
			attributeMap.put(ESBDebuggerConstants.URI_MAPPING,
					apiResource.getUrlMapping());
			break;
		default:
			break;
		}
		return attributeMap;
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
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();

		SynapseAPIImpl api = (SynapseAPIImpl) treeIterator.next();
		EList<APIResource> apiResources = api.getResources();
		for (APIResource apiResource : apiResources) {
			if (abstractMediator != null) {
				if (abstractMediator.reversed) {
					int position = getMediatorPosition(
							apiResource.getOutSequenceOutputConnector(),
							abstractMediator);
					List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
							resource, position,
							ESBDebuggerConstants.API_OUTSEQ, action);
					if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
							.equalsIgnoreCase(action)) {
						incrementBreakpointPosition(breakpontList);
					} else {
						decreaseBreakpointPosition(breakpontList);
					}
				} else {
					int position = getMediatorPosition(
							apiResource.getOutputConnector(), abstractMediator);
					List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
							resource, position, ESBDebuggerConstants.API_INSEQ,
							action);
					if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
							.equalsIgnoreCase(action)) {
						incrementBreakpointPosition(breakpontList);
					} else {
						decreaseBreakpointPosition(breakpontList);
					}
				}
			}
		}
	}
}
