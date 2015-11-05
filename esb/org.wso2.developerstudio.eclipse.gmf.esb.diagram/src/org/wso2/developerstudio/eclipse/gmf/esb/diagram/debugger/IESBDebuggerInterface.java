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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelEventDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelResponceDispatcher;

public interface IESBDebuggerInterface {

	public void setfRequestSocket(int commandPort) throws IOException;

	public void setfRequestWriter() throws IOException;

	public void setfRequestReader() throws IOException;

	public void setfEventSocket(int eventPort) throws IOException;

	public void setfEventReader() throws IOException;

	public PrintWriter getfRequestWriter();

	public BufferedReader getfRequestReader();

	public BufferedReader getfEventReader();

	public void setResponceDispatcher(ChannelResponceDispatcher responceDispatcher);

	public ChannelEventDispatcher getEventDispatcher();

	public void setEventDispatcher(ChannelEventDispatcher eventDispatcher);

	public ChannelResponceDispatcher getResponceDispatcher();

	public void intializeDispatchers();

	public void sendCommand(String command);

	public void sendBreakpointCommand(String operation, String type,
			Map<String, Object> breakpointAttributes);

	public void attachDebugger(IESBDebugger esbDebugger);

	public void sendGetPropertiesCommand(Map<String, Object> attributeValues);

	public void terminate();

}
