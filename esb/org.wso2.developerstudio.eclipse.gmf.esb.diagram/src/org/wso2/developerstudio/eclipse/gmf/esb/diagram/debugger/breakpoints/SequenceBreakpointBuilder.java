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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;

/**
 * This class builds ESB breakpoints related to Sequences.
 */
public class SequenceBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public SequenceBreakpointBuilder() {
		this.Type = ESBDebuggerConstants.SEQUENCE;
	}

	@Override
	public IBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,
			boolean selectedMediatorReversed) throws CoreException {

		int lineNumber = -1;
		TreeIterator<EObject> treeIterator = esbServer.eAllContents();
		EObject next = treeIterator.next();

		SequencesImpl sequence = (SequencesImpl) next;

		String message = getInitialMessage();
		message = addSequenceTypeAttribute(message);

		message = addSequenceKeyAttribute(message, sequence);

		String position = getMediatorPosition(sequence.getOutputConnector(),
				selection);
		message = addMediatorPositionAttribute(message, position);

		boolean breakpointExists = deteleExistingBreakpoint(resource, message,
				lineNumber);
		addBreakpointOperationAttribute(message, breakpointExists);

		System.out.println(message);
		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		return esbBreakpoint;
	}

	private String addSequenceKeyAttribute(String message,
			SequencesImpl sequence) {

		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_KEY + KEY_VALUE_SEPERATOR
				+ sequence.getName();
	}

	private String addSequenceTypeAttribute(String message) {

		return message + ATTRIBUTE_SEPERATOR
				+ ESBDebuggerConstants.SEQUENCE_TYPE + KEY_VALUE_SEPERATOR
				+ ESBDebuggerConstants.NAMED;
	}

}
