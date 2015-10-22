package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;

public class ConnectorBreakpointBuilder extends AbstractESBBreakpointBuilder {

	@Override
	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,boolean reversed) throws CoreException {
		int lineNumber = -1;
		String message = "";
		

		
		ESBBreakpoint esbBreakpoint = null;
		return esbBreakpoint;
	}

	@Override
	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,String action)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}


}
