package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

import com.google.gson.annotations.SerializedName;

public class CommandResponseMessage implements IResponseMessage {

	@SerializedName(ESBDebuggerConstants.COMMAND_RESPONSE)
	private String commandResponse;
	@SerializedName(ESBDebuggerConstants.FAILED_REASON)
	private String failedReason;

	public CommandResponseMessage(String commandResponse, String failedReason) {
		super();
		this.commandResponse = commandResponse;
		this.failedReason = failedReason;
	}

	public String getCommandResponse() {
		return commandResponse;
	}

	public void setCommandResponse(String commandResponse) {
		this.commandResponse = commandResponse;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

}
