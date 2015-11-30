package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

public class SpecialCoordinationEventMessage extends GeneralEventMessage {

	private String messageReciever;
	private String callbackReciever;

	public SpecialCoordinationEventMessage(EventMessageType event,
			String messageReciever, String callbackReciever) {
		super(event);
		this.messageReciever = messageReciever;
		this.callbackReciever = callbackReciever;
	}

	public String getMessageReciever() {
		return messageReciever;
	}

	public void setMessageReciever(String messageReciever) {
		this.messageReciever = messageReciever;
	}

	public String getCallbackReciever() {
		return callbackReciever;
	}

	public void setCallbackReciever(String callbackReciever) {
		this.callbackReciever = callbackReciever;
	}

}
