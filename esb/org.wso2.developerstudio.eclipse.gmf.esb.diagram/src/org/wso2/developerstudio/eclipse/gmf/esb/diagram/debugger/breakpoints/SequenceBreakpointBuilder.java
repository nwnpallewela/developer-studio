package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;

public class SequenceBreakpointBuilder implements IESBBreakpointBuilder{

	@Override
	public IBreakpoint getESBBreakpoint(TreeIterator<EObject> treeIterator) {
		EObject next = treeIterator.next();
		while (treeIterator.hasNext()) {
			next = treeIterator.next();
			if (next instanceof MediatorImpl) {
				System.out.println(next.toString());
			}
		}
		return null;
	}

}
