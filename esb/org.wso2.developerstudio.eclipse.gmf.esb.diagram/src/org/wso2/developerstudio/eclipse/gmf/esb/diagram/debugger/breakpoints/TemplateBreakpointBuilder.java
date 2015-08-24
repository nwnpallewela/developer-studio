package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;

public class TemplateBreakpointBuilder extends AbstractESBBreakpointBuilder {

	public TemplateBreakpointBuilder(){
		this.Type = ESBDebuggerConstants.TEMPLATE;
	}
	
	@Override
	public IBreakpoint getESBBreakpoint(TreeIterator<EObject> treeIterator, IResource resource,EObject selection) throws CoreException {
		int lineNumber = -1;
		String message = "";
		EObject next = treeIterator.next();
		while (treeIterator.hasNext()) {
			next = treeIterator.next();
			if (next instanceof MediatorImpl) {
				System.out.println(next.toString());
			}
		}
		
		deteleExistingBreakpoint(resource, message, lineNumber);

		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		return esbBreakpoint;
	}

}
