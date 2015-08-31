package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints.impl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

public class APIBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public APIBreakpointBuilder() {
		this.type = ESBDebuggerConstants.API;
	}

	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,boolean reversed) throws CoreException {
		int lineNumber = -1;
		String message = "";
		
		

		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		return esbBreakpoint;
	}

}
