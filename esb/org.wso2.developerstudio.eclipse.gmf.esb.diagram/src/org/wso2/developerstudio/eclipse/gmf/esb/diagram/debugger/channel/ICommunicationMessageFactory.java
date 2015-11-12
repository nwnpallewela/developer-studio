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

import org.codehaus.jettison.json.JSONException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger.ESBDebuggerCommands;

/**
 * Implementations for ESB Debugger Interface message communication channels
 * should implement {@link ICommunicationMessageFactory} interface
 *
 */
public interface ICommunicationMessageFactory {

	/**
	 * This method creates the specified command message to send to ESB Server
	 * 
	 * @param command
	 * @return
	 * @throws JSONException
	 */
	String createCommand(ESBDebuggerCommands command) throws Exception;

	/**
	 * This method creates the specified breakpoint command message to send to
	 * ESB Server
	 * 
	 * @param attributeValues
	 * @return
	 * @throws JSONException
	 */
	String createBreakpointCommand(Map<String, Object> attributeValues)
			throws Exception;

	/**
	 * This method creates the specified get properties command message to send
	 * to ESB Server
	 * 
	 * @param attributeValues
	 * @return
	 * @throws JSONException
	 */
	String createGetPropertiesCommand(Map<String, Object> attributeValues)
			throws Exception;

	/**
	 * This method converts response message for Map<String,Object>
	 * 
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	Map<String, Object> convertResponseMessageToMap(String response)
			throws Exception;

	/**
	 * This method converts event message for Map<String,Object>
	 * 
	 * @param buffer
	 * @return
	 * @throws JSONException
	 */
	Map<String, Object> convertEventMessageToMap(String buffer)
			throws Exception;

}
