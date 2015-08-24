package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

public class ProxyBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public ProxyBreakpointBuilder() {
		this.Type = ESBDebuggerConstants.PROXY;
	}

	@Override
	public IBreakpoint getESBBreakpoint(TreeIterator<EObject> treeIterator,
			IResource resource, EObject selection) throws CoreException {
		int lineNumber = -1;
		String message = MEDIATION_COMPONENT_ATTRIBUTE + Type;
		EObject next = treeIterator.next();

		message = message + ATTRIBUTE_SEPERATOR + PROXY_KEY_ATTRIBUTE
				+ ((ProxyServiceImpl) next).getName();

		while (treeIterator.hasNext()) {
			next = treeIterator.next();
			if (next instanceof MediatorImpl) {
				//System.out.println(next.toString());
				//add position and seq type to message
			}
		}
		message=message+selection.toString();
		if (resource != null) {
			boolean breakpointExists = deteleExistingBreakpoint(resource,
					message, lineNumber);
			if (breakpointExists) {
				message = CLEAR_BREAKPOINT_ATTRIBUTE + ATTRIBUTE_SEPERATOR
						+ message;
			} else {
				message = SET_BREAKPOINT_ATTRIBUTE + ATTRIBUTE_SEPERATOR
						+ message;
			}
		}
		
System.out.println(message);
		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		System.out.println("Breakpoint model Identifier : "+esbBreakpoint.getModelIdentifier());
		return esbBreakpoint;
	}

}
