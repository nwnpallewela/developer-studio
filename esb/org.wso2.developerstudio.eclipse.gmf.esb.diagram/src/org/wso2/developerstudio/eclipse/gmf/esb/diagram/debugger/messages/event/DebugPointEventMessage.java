package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPoint;

public class DebugPointEventMessage extends GeneralEventMessage{

	private ESBDebugPoint debugPoint;
	
	public DebugPointEventMessage(EventMessageType event,ESBDebugPoint debugPoint) {
		super(event);
		this.debugPoint=debugPoint;
	}
	
	public ESBDebugPoint getDebugPoint() {
		return debugPoint;
	}

	public void setDebugPoint(ESBDebugPoint debugPoint) {
		this.debugPoint = debugPoint;
	}

}
