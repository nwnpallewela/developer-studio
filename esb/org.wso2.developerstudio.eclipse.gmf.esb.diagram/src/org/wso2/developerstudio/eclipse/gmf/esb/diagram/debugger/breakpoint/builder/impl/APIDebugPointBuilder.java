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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceContainer2EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceFaultContainerEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceSequenceAndEndpointContainerEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.SynapseAPIAPICompartmentEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

/**
 * This class builds ESB breakpoints related to API Resources.
 */
public class APIDebugPointBuilder extends AbstractESBDebugPointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 * 
	 * @throws ESBDebuggerException
	 */
	@Override
	public ESBDebugPoint getESBDebugPoint(EsbServer esbServer,
			IResource resource, AbstractMediator part, String commandArguement)
			throws CoreException, ESBDebuggerException {
		int lineNumber = -1;
		SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);

		Map<String, Object> attributeMap = setInitialAttributes(
				ESBDebuggerConstants.API, commandArguement);
		attributeMap.put(ESBDebuggerConstants.API_KEY, api.getApiName());

		EditPart proxyContainer = getContainerFromEditPart(part,
				ProxyServiceContainer2EditPart.class);
		EditPart apiContainer = getContainerFromEditPart(proxyContainer,
				SynapseAPIAPICompartmentEditPart.class);
		EList<APIResource> apiResources = api.getResources();
		APIResource apiResource = getAPIResourceFromAPIEditPart(apiResources,
				apiContainer);
		EObject selection = ((View) part.getModel()).getElement();
		List<Integer> position;
		if (proxyContainer instanceof ProxyServiceSequenceAndEndpointContainerEditPart) {
			if (part.reversed) {
				position = getMediatorPosition(
						apiResource.getOutSequenceOutputConnector(), selection);
				attributeMap = addAPIAttributesToMessage(attributeMap,
						ESBDebuggerConstants.API_OUTSEQ, position, apiResource);
			} else {
				position = getMediatorPosition(
						apiResource.getOutputConnector(), selection);
				attributeMap = addAPIAttributesToMessage(attributeMap,
						ESBDebuggerConstants.API_INSEQ, position, apiResource);
			}
		} else {
			position = getMediatorPositionInFaultSeq(apiResource.getContainer()
					.getFaultContainer().getMediatorFlow().getChildren(),
					selection);
			attributeMap = addAPIAttributesToMessage(attributeMap,
					getFaultSequenceName(apiResource), position, apiResource);
		}
		return new ESBDebugPoint(resource, lineNumber, attributeMap);

	}

	/**
	 * @param apiResources
	 * @param apiResourceEditPart
	 * @return
	 * @throws ESBDebuggerException
	 */
	private APIResource getAPIResourceFromAPIEditPart(
			EList<APIResource> apiResources, EditPart apiResourceEditPart)
			throws ESBDebuggerException {
		for (APIResource apiResource : apiResources) {
			if (apiResourceEditPart
					.equals(EditorUtils.getEditpart(apiResource))) {
				return apiResource;
			}
		}
		throw new ESBDebuggerException(
				"Matching APIResource is not found for APIResourceEditPart :"
						+ apiResourceEditPart);
	}

	/**
	 * This method adds Sequence Type, Mediator Position and Method Attributes
	 * for Attribute map
	 * 
	 * @param attributeMap
	 * @param sequenceType
	 * @param position
	 * @param apiResource
	 * @return
	 */
	private Map<String, Object> addAPIAttributesToMessage(
			Map<String, Object> attributeMap, String sequenceType,
			List<Integer> position, APIResource apiResource) {
		attributeMap.put(ESBDebuggerConstants.SEQUENCE_TYPE, sequenceType);
		attributeMap.put(ESBDebuggerConstants.MEDIATOR_POSITION, position);
		attributeMap.put(ESBDebuggerConstants.METHOD,
				ESBDebugerUtil.getMethodValuesFromResource(apiResource));
		attributeMap = addURLStyleToMessage(attributeMap, apiResource);
		return attributeMap;
	}

	private Map<String, Object> addURLStyleToMessage(
			Map<String, Object> attributeMap, APIResource apiResource) {
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
	 * 
	 * @throws ESBDebuggerException
	 */
	@Override
	public void updateExistingDebugPoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws ESBDebuggerException {
		SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(
				INDEX_OF_FIRST_ELEMENT);
		EditPart proxyContainer = getContainerFromEditPart(abstractMediator,
				ProxyServiceContainer2EditPart.class);
		EditPart apiContainer = getContainerFromEditPart(proxyContainer,
				SynapseAPIAPICompartmentEditPart.class);
		EList<APIResource> apiResources = api.getResources();
		APIResource apiResource = getAPIResourceFromAPIEditPart(apiResources,
				apiContainer);
		if (proxyContainer instanceof ProxyServiceSequenceAndEndpointContainerEditPart) {
			if (abstractMediator.reversed) {
				List<Integer> position = getMediatorPosition(
						apiResource.getOutSequenceOutputConnector(),
						abstractMediator);
				List<ESBDebugPoint> breakpontList = getBreakpointsRelatedToModification(
						resource, position, ESBDebuggerConstants.API_OUTSEQ,
						action);
				if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
						.equalsIgnoreCase(action)) {
					increaseBreakpointPosition(breakpontList);
				} else {
					decreaseBreakpointPosition(breakpontList);
				}
			} else {
				List<Integer> position = getMediatorPosition(
						apiResource.getOutputConnector(), abstractMediator);
				List<ESBDebugPoint> breakpontList = getBreakpointsRelatedToModification(
						resource, position, ESBDebuggerConstants.API_INSEQ,
						action);
				if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
						.equalsIgnoreCase(action)) {
					increaseBreakpointPosition(breakpontList);
				} else {
					decreaseBreakpointPosition(breakpontList);
				}
			}
		} else if (proxyContainer instanceof ProxyServiceFaultContainerEditPart) {
			List<Integer> position = getMediatorPositionInFaultSeq(apiResource
					.getContainer().getFaultContainer().getMediatorFlow()
					.getChildren(), abstractMediator);
			List<ESBDebugPoint> breakpontList = getBreakpointsRelatedToModification(
					resource, position, getFaultSequenceName(apiResource),
					action);
			if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
					.equalsIgnoreCase(action)) {
				increaseBreakpointPosition(breakpontList);
			} else {
				decreaseBreakpointPosition(breakpontList);
			}
		} else {
			throw new IllegalArgumentException(
					"Selected Metdiator Edit Part is in a unknown position : "
							+ proxyContainer.toString());
		}
	}
}
