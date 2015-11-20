package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPointMessage;

public class DebugPointEventMessage extends GeneralEventMessage{

	private ESBDebugPointMessage debugPoint;
	
	public DebugPointEventMessage(EventMessageType event,ESBDebugPointMessage debugPoint) {
		super(event);
		this.debugPoint=debugPoint;
	}
	
	public ESBDebugPointMessage getDebugPoint() {
		return debugPoint;
	}

	public void setDebugPoint(ESBDebugPointMessage debugPoint) {
		this.debugPoint = debugPoint;
	}

}
