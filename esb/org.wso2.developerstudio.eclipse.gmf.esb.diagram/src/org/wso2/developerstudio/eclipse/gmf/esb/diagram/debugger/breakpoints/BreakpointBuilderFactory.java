package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

public class BreakpointBuilderFactory {

	public IESBBreakpointBuilder getBreakpointBuilder(String type){
		String lowerCaseType = type.toLowerCase();
		switch (lowerCaseType) {
		case ESBDebuggerConstants.PROXY:
			return new ProxyBreakpointBuilder();
		case ESBDebuggerConstants.SEQUENCE:
			return new SequenceBreakpointBuilder();
		case ESBDebuggerConstants.TEMPLATE:
			return new TemplateBreakpointBuilder();
		case ESBDebuggerConstants.API:
			return new APIBreakpointBuilder();
		case ESBDebuggerConstants.CONNECTOR:
			return new ConnectorBreakpointBuilder();
		default:
			return null;
		}
	}
}
