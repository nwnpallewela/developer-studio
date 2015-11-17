package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.respond;

import com.google.gson.JsonElement;

public class PropertyRespondMessage implements IResponseMessage {

	private String scope;
	private JsonElement propertyValues;

	public PropertyRespondMessage(String scope, JsonElement propertyValues) {
		this.setScope(scope);
		this.setPropertyValues(propertyValues);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public JsonElement getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(JsonElement propertyValues) {
		this.propertyValues = propertyValues;
	}

}
