package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;

public abstract class AbstractESBBreakpointBuilder implements
		IESBBreakpointBuilder {
	protected static final String CLEAR_BREAKPOINT_ATTRIBUTE = "command:clear";
	protected static final String SET_BREAKPOINT_ATTRIBUTE = "command:set";
	protected static final String ATTRIBUTE_SEPERATOR = ",";
	protected static final String EMPTY_STRING = "";
	protected static final String MEDIATION_COMPONENT_ATTRIBUTE = "mediation-component:";
	protected static final String PROXY_KEY_ATTRIBUTE = "proxy-key:";
	String Type;

	String getInstanceId(String instance) {
		int indexOfAt = instance.indexOf("@");
		return instance.substring(indexOfAt + 1,
				instance.indexOf(" ", indexOfAt));
	}

	boolean deteleExistingBreakpoint(IResource resource, String message,
			int lineNumber) throws CoreException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints();

		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			System.out.println("all breakpoints: "+breakpoint.getModelIdentifier());
			if (resource.equals(breakpoint.getMarker().getResource())) {
				if ((((ESBBreakpoint) breakpoint).getMessage()).equals(SET_BREAKPOINT_ATTRIBUTE+ATTRIBUTE_SEPERATOR+message)) {
					breakpoint.delete();
					return true;
				} else if (((ESBBreakpoint) breakpoint).getLineNumber() == (lineNumber + 1)
						&& lineNumber != -1) {
					breakpoint.delete();
					return true;
				}
			}
		}
		return false;
	}
}
