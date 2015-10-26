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
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.ApiResourceUrlStyle;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SynapseAPIImpl;

/**
 * This class builds ESB breakpoints related to API Resources.
 */
public class APIBreakpointBuilder extends AbstractESBBreakpointBuilder {

	/**
	 * This method returns the ESBBreakpoint object for the selection
	 * 
	 * @throws MediatorNotFoundException
	 */
	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,
			boolean selectedMediatorReversed) throws CoreException,
			MediatorNotFoundException {
		int lineNumber = -1;
		SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(
				FIRST_ELEMENT_INDEX);

		Map<String, Object> attributeMap = setInitialAttributes(ESBDebuggerConstants.API);
		attributeMap.put(ESBDebuggerConstants.API_KEY, api.getApiName());
		EList<APIResource> apiResources = api.getResources();
		for (APIResource apiResource : apiResources) {
			boolean mediatorLocated = true;
			try {

				if (selectedMediatorReversed) {
					int[] position = null;
					try {
						position = getMediatorPositionInFaultSeq(apiResource
								.getContainer().getFaultContainer()
								.getMediatorFlow().getChildren(), selection);
						attributeMap = addAPIAttributesToMessage(attributeMap,
								getFaultSequenceName(apiResource), position,
								apiResource);
					} catch (MediatorNotFoundException e) {

						position = getMediatorPosition(
								apiResource.getOutSequenceOutputConnector(),
								selection);
						attributeMap = addAPIAttributesToMessage(attributeMap,
								ESBDebuggerConstants.API_OUTSEQ, position,
								apiResource);

					}

				} else {

					int[] position = getMediatorPosition(
							apiResource.getOutputConnector(), selection);
					attributeMap = addAPIAttributesToMessage(attributeMap,
							ESBDebuggerConstants.API_INSEQ, position,
							apiResource);
				}
			} catch (MediatorNotFoundException e) {
				mediatorLocated = false;
			}
			if (mediatorLocated) {
				break;
			}
		}
		return new ESBBreakpoint(resource, lineNumber, attributeMap);

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
			int[] position, APIResource apiResource) {
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
	 * @throws MediatorNotFoundException
	 */
	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws CoreException, MediatorNotFoundException {
		SynapseAPIImpl api = (SynapseAPIImpl) esbServer.eContents().get(FIRST_ELEMENT_INDEX);

		if (api != null && abstractMediator != null) {
			EList<APIResource> apiResources = api.getResources();
			for (APIResource apiResource : apiResources) {
				boolean mediatorLocated = true;
				try {
					if (abstractMediator.reversed) {
						int[] position = getMediatorPosition(
								apiResource.getOutSequenceOutputConnector(),
								abstractMediator);
						List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
								resource, position,
								ESBDebuggerConstants.API_OUTSEQ, action);
						if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
								.equalsIgnoreCase(action)) {
							increaseBreakpointPosition(breakpontList);
						} else {
							decreaseBreakpointPosition(breakpontList);
						}
					} else {
						int[] position = getMediatorPosition(
								apiResource.getOutputConnector(),
								abstractMediator);
						List<ESBBreakpoint> breakpontList = getBreakpointsRelatedToModification(
								resource, position,
								ESBDebuggerConstants.API_INSEQ, action);
						if (ESBDebuggerConstants.MEDIATOR_INSERT_ACTION
								.equalsIgnoreCase(action)) {
							increaseBreakpointPosition(breakpontList);
						} else {
							decreaseBreakpointPosition(breakpontList);
						}
					}
				} catch (MediatorNotFoundException e) {
					mediatorLocated = false;
				}
				if (mediatorLocated) {
					break;
				}
			}
		}
	}
}
