package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.event;

/**
 * <code>EventMessageType.RESUMED_CLIENT</code> and
 * <code>EventMessageType.DEBUG_INFO_LOST</code> event types in
 * {@link EventMessageType} are sent through {@link GeneralEventMessage}
 *
 */
public class GeneralEventMessage implements IEventMessage {

	private EventMessageType event;

	public GeneralEventMessage(EventMessageType event) {
		super();
		this.event = event;
	}

	public EventMessageType getEvent() {
		return event;
	}

	public void setEvent(EventMessageType event) {
		this.event = event;
	}

}
