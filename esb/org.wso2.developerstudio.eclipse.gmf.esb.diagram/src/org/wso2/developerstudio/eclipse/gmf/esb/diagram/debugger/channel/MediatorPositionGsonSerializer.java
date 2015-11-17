package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel;

import java.lang.reflect.Type;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBMediatorPosition;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MediatorPositionGsonSerializer implements
		JsonSerializer<ESBMediatorPosition> {

	@Override
	public JsonElement serialize(ESBMediatorPosition position,
			Type typeOfPosition, JsonSerializationContext context) {
		String positionString = position.getPosition().toString();
		positionString = positionString.trim();
		positionString = positionString.replaceAll(",", " ");
		positionString = positionString.substring(1, positionString.lastIndexOf(']'));
		
		return new JsonPrimitive(positionString);
	}

}
