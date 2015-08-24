package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;

public class SequenceBreakpointBuilder extends AbstractESBBreakpointBuilder {
	

	public SequenceBreakpointBuilder() {
		this.Type = ESBDebuggerConstants.SEQUENCE;
	}

	@Override
	public IBreakpoint getESBBreakpoint(TreeIterator<EObject> treeIterator,
			IResource resource, EObject selection) throws CoreException {
		int lineNumber = -1;
		String message = MEDIATION_COMPONENT_ATTRIBUTE + Type;
		EObject next = treeIterator.next();

		System.out.println("**************" + next.toString());

		while (treeIterator.hasNext()) {
			next = treeIterator.next();
			if (next instanceof MediatorImpl) {
				System.out.println(next.toString());
			}
		}
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

		ESBBreakpoint esbBreakpoint = new ESBBreakpoint(resource, lineNumber,
				message);
		
		System.out.println("Breakpoint model Identifier : "+esbBreakpoint.getModelIdentifier());
		return esbBreakpoint;
	}

}
