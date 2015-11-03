package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.IDebuggerEvent;

/**
 * {@link MediationFlowCompleteEvent} represent the request event from
 * {@link ESBDebugger} to {@link ESBDebugTarget} when {@link ESBDebugger} notify
 * message processing of a mediation flow ending action from ESB Server
 */
public class MediationFlowCompleteEvent extends AbstractEvent implements
		IDebuggerEvent {

}
