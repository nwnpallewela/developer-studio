package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants.*;

public enum EventMessageType {
	BREAKPOINT(BREAKPOINT_EVENT_TYPE), SKIPPOINT(SKIP), STARTED(
			STARTED_EVENT_TYPE), CALLBACK(CALLBACK_EVENT_TYPE), TERMINATED(
			TERMINATED_EVENT_TYPE), RESUMED_CLIENT(RESUMED_CLIENT_EVENT_TYPE), DEBUG_INFO_LOST(
			DEBUG_INFO_LOST_EVENT);

	private final String event;

	private EventMessageType(String eventValue) {
		event = eventValue;
	}

	public boolean equalsName(String comapreEvent) {
		return (comapreEvent == null) ? false : event.equals(comapreEvent);
	}

	public String toString() {
		return this.event;
	}
}
