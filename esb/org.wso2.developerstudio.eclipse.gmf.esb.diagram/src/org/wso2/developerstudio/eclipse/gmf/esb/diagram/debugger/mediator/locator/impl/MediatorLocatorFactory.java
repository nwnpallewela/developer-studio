package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.impl;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.IMediatorLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;


public class MediatorLocatorFactory {

public static IMediatorLocator getMediatorLocator(String type) {
		
		String lowerCaseType = type.toLowerCase();
		
		switch (lowerCaseType) {
		case ESBDebuggerConstants.PROXY:
			return new ProxyMediatorLocator();
		case ESBDebuggerConstants.SEQUENCE:
			return new SequenceMediatorLocator();
		case ESBDebuggerConstants.TEMPLATE_SEQUENCE:
			return new TemplateMediatorLocator();
		case ESBDebuggerConstants.API:
			return new APIMediatorLocator();
		case ESBDebuggerConstants.CONNECTOR:
			return new ConnectorMediatorLocator();
		case ESBDebuggerConstants.MAIN_SEQUENCE:
			return new MainSequenceMediatorLocator();
		default:
			return null;
		}
	}
}
