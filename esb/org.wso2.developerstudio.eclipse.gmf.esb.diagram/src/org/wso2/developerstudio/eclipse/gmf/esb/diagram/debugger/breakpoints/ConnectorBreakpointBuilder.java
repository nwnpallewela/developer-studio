package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;

public class ConnectorBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public ConnectorBreakpointBuilder() {
		this.Type = ESBDebuggerConstants.CONNECTOR;
	}

	@Override
	public IBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, EObject selection,boolean reversed) throws CoreException {
		int lineNumber = -1;
		String message = "";
		

		deteleExistingBreakpoint(resource, message, lineNumber);

		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		return esbBreakpoint;
	}

}
