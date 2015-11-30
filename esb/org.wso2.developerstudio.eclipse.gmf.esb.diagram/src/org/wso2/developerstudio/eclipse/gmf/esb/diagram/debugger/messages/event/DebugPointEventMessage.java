package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.AbstractESBDebugPointMessage;

public class DebugPointEventMessage extends GeneralEventMessage{

	private AbstractESBDebugPointMessage debugPoint;
	
	public DebugPointEventMessage(EventMessageType event,AbstractESBDebugPointMessage debugPoint) {
		super(event);
		this.debugPoint=debugPoint;
	}
	
	public AbstractESBDebugPointMessage getDebugPoint() {
		return debugPoint;
	}

	public void setDebugPoint(AbstractESBDebugPointMessage debugPoint) {
		this.debugPoint = debugPoint;
	}

}
