/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel;

import java.util.Map;

public class JsonGsonMessageChannel implements IChannelCommunication {

	@Override
	public String createCommand(String command) {
		return null;
	}

	@Override
	public String createBreakpointCommand(String operation, String type, Map<String, String> attributeValues) {
		return null;
	}

	@Override
	public Map<String, String> getResponce(String responce) {
		return null;
	}

	@Override
	public Map<String, String> getEvent(String buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createGetPropertiesCommand(Map<String, String> attributeValues) {
		// TODO Auto-generated method stub
		return null;
	}

}

