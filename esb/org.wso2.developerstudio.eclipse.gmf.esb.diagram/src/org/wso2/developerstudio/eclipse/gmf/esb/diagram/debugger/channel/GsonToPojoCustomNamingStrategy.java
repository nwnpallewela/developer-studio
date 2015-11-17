package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

public class GsonToPojoCustomNamingStrategy implements FieldNamingStrategy {

	@Override
	public String translateName(Field filedName) {
		StringBuilder translation = new StringBuilder();
		String name = filedName.getName();
		for (int i = 0; i < name.length(); i++) {
			char character = name.charAt(i);
			if (character == '-') {
				i++;
				character = name.charAt(i);
				translation.append(Character.toUpperCase(character));
			} else {
				translation.append(character);
			}

		}
		return translation.toString();
	}

}
