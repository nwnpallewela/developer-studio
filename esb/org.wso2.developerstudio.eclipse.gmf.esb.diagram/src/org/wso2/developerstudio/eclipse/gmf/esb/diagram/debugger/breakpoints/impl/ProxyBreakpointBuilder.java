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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class builds ESB breakpoints related to Proxy Services.
 */
public class ProxyBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public ProxyBreakpointBuilder() {
		this.type = ESBDebuggerConstants.PROXY;
	}

	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,
			boolean selectedMediatorReversed) throws CoreException {

		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();
		ProxyServiceImpl proxy = (ProxyServiceImpl) next;

		String message = getInitialMessage();
		message = addProxyKeyAttribute(message, proxy);

		if (selectedMediatorReversed) {
			String position = getMediatorPositionInFaultSeq(proxy
					.getContainer().getFaultContainer().getMediatorFlow()
					.getChildren(), selection);

			if (StringUtils.isEmpty(position)) {
				position = getMediatorPosition(
						proxy.getOutSequenceOutputConnector(), selection);

				message = addProxyOutSequenceTypeAttribute(message);
			} else {
				message = addFaultSequenceTypeAttribute(message, proxy);
			}
			message = addMediatorPositionAttribute(message, position);
		} else {
			message = addProxyInSequenceTypeAttribute(message);
			String position = getMediatorPosition(proxy.getOutputConnector(),
					selection);
			message = addMediatorPositionAttribute(message, position);
		}

		return new ESBBreakpoint(resource, lineNumber, message);
	}

	private String addProxyInSequenceTypeAttribute(String message) {

		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_TYPE + KEY_VALUE_SEPERATOR
				+ ESBDebuggerConstants.PROXY_INSEQ;
	}

	private String addFaultSequenceTypeAttribute(String message,
			ProxyServiceImpl proxy) {

		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_TYPE + KEY_VALUE_SEPERATOR
				+ proxy.getFaultSequenceName();
	}

	private String addProxyOutSequenceTypeAttribute(String message) {

		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_TYPE + KEY_VALUE_SEPERATOR
				+ ESBDebuggerConstants.PROXY_OUTSEQ;
	}

	private String addProxyKeyAttribute(String message, ProxyServiceImpl proxy) {

		return message + ATTRIBUTE_SEPERATOR + ESBDebuggerConstants.PROXY_KEY
				+ KEY_VALUE_SEPERATOR + proxy.getName();
	}

}
