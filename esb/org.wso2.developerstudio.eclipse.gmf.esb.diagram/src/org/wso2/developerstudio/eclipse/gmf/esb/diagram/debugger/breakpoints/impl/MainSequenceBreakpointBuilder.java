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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

/**
 * This class builds ESB breakpoints related to Main Sequence.
 */
public class MainSequenceBreakpointBuilder extends AbstractESBBreakpointBuilder {

	private static final String OUT_SEQ_POSITION = "1 ";
	private static final String IN_SEQ_POSITION = "0 ";

	public MainSequenceBreakpointBuilder() {
		this.type = ESBDebuggerConstants.SEQUENCE;
	}

	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection, boolean reversed)
			throws CoreException {

		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();

		ProxyServiceImpl mainSequence = (ProxyServiceImpl) next;

		String message = getInitialMessage();

		message = addSequenceTypeAttribute(message);

		message = addSequenceKeyAttribute(message);

		if (reversed) {
			String position = OUT_SEQ_POSITION
					+ getMediatorPosition(
							mainSequence.getOutSequenceOutputConnector(),
							selection);

			message = addMediatorPositionAttribute(message, position);
		} else {

			String position = IN_SEQ_POSITION
					+ getMediatorPosition(mainSequence.getOutputConnector(),
							selection);
			message = addMediatorPositionAttribute(message, position);
		}

		return new ESBBreakpoint(resource, lineNumber, message);
	}

	private String addSequenceKeyAttribute(String message) {
		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_KEY + KEY_VALUE_SEPERATOR
				+ ESBDebuggerConstants.MAIN;
	}

	private String addSequenceTypeAttribute(String message) {
		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_TYPE + KEY_VALUE_SEPERATOR
				+ ESBDebuggerConstants.NAMED;
	}

}
