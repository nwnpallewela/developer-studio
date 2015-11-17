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
import java.net.Socket;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelEventDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelResponseDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger.ESBDebuggerCommands;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.CommandMessage;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.ESBDebugPoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.messages.command.GetPropertyCommand;

/**
 * ESB Debugger Interface should implement this interface and methods in the
 * implementation
 *
 */
@SuppressWarnings("unused")
public interface IESBDebuggerInterface {

	/**
	 * This method sets {@link Socket} to communicate with ESB Server Debugger
	 * Command channel
	 * 
	 * @param commandPort
	 * @throws IOException
	 */
	public void setRequestSocket(int commandPort) throws IOException;

	/**
	 * This method sets {@link PrintWriter} to send messages to ESB Server
	 * Debugger through Command channel
	 * 
	 * @throws IOException
	 */
	public void setRequestWriter() throws IOException;

	/**
	 * This method sets {@link BufferedReader} to read messages to ESB Server
	 * Debugger through Command channel
	 * 
	 * @throws IOException
	 */
	public void setRequestReader() throws IOException;

	/**
	 * This method sets {@link Socket} to communicate with ESB Server Debugger
	 * Event channel
	 * 
	 * @param eventPort
	 * @throws IOException
	 */
	public void setEventSocket(int eventPort) throws IOException;

	/**
	 * This method sets {@link BufferedReader} to read messages to ESB Server
	 * Debugger through Event channel
	 * 
	 * @throws IOException
	 */
	public void setEventReader() throws IOException;

	/**
	 * This method set Response Dispatcher in {@link IESBDebuggerInterface} to
	 * get response messages from ESB Server Debugger
	 * 
	 * @param responseDispatcher
	 */
	public void setResponseDispatcher(
			ChannelResponseDispatcher responceDispatcher);

	/**
	 * This method set Event Dispatcher in {@link IESBDebuggerInterface} to get
	 * event messages from ESB Server Debugger
	 * 
	 * @param responseDispatcher
	 */
	public void setEventDispatcher(ChannelEventDispatcher eventDispatcher);

	/**
	 * This method sends command message to ESB Server.
	 * 
	 * @param esbDebuggerCommandMessage
	 * @throws Exception
	 */
	public void sendCommand(CommandMessage esbDebuggerCommandMessage) throws Exception;

	/**
	 * This method sends breakpoint message to ESB Server to register
	 * breakpoints.
	 * 
	 * @param operation
	 * @param type
	 * @param debugPoint
	 * @throws Exception
	 */
	public void sendBreakpointCommand(ESBDebugPoint debugPoint)
			throws Exception;

	/**
	 * Assign a {@link IESBDebugger} for the object
	 * 
	 * @param esbDebugger
	 */
	public void attachDebugger(IESBDebugger esbDebugger);

	/**
	 * This method sends command message to ESB Server to get properties.
	 * 
	 * @param getPropertyCommand
	 * @throws Exception
	 */
	public void sendGetPropertiesCommand(GetPropertyCommand getPropertyCommand)
			throws Exception;

	/**
	 * Terminate Event Dispatchers attached.
	 * 
	 * @throws IOException
	 */
	public void terminate() throws IOException;

	public PrintWriter getRequestWriter();

	public BufferedReader getRequestReader();

	public BufferedReader getEventReader();

	public ChannelEventDispatcher getEventDispatcher();

	public ChannelResponseDispatcher getResponseDispatcher();

}
