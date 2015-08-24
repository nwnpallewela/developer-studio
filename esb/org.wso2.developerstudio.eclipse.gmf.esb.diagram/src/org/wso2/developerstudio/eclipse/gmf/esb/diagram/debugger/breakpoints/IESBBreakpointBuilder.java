package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

public interface IESBBreakpointBuilder {

	IBreakpoint getESBBreakpoint(TreeIterator<EObject> treeIterator,
			IResource resource, EObject selection) throws CoreException;

}
