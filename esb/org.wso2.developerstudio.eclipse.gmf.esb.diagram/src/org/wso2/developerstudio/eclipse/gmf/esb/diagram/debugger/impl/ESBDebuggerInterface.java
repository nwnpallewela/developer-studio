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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.ICommunicationMessageFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.JsonJettisonMessageFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelEventDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelResponseDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger.ESBDebuggerCommands;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * {@link ESBDebuggerInterface} handles the communication between ESB Server and
 * {@link ESBDebugger}
 *
 */
public class ESBDebuggerInterface implements IESBDebuggerInterface {

	private Socket requestSocket;
	private PrintWriter requestWriter;
	private BufferedReader requestReader;

	private Socket eventSocket;
	private BufferedReader eventReader;

	private ChannelEventDispatcher eventDispatcher;
	private ChannelResponseDispatcher responseDispatcher;

	private ICommunicationMessageFactory messageFactory;
	private IESBDebugger esbDebugger;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public ESBDebuggerInterface(int commandPort, int eventPort)
			throws IOException {
		setRequestSocket(commandPort);
		setEventSocket(eventPort);
		setEventReader();
		setRequestReader();
		setRequestWriter();
		intializeDispatchers();
	}

	private void intializeDispatchers() {

		eventDispatcher = new ChannelEventDispatcher(getEventReader(), this);
		eventDispatcher.start();

		responseDispatcher = new ChannelResponseDispatcher(getRequestReader(),
				this);
		responseDispatcher.start();

		messageFactory = new JsonJettisonMessageFactory();
	}

	@Override
	public void setRequestSocket(int commandPort) throws IOException {
		this.requestSocket = new Socket(InetAddress.getLocalHost()
				.getHostName(), commandPort);
	}

	@Override
	public void setRequestWriter() throws IOException {
		this.requestWriter = new PrintWriter(requestSocket.getOutputStream());
	}

	@Override
	public void setRequestReader() throws IOException {
		this.requestReader = new BufferedReader(new InputStreamReader(
				requestSocket.getInputStream()));
	}

	@Override
	public void setEventSocket(int eventPort) throws IOException {
		this.eventSocket = new Socket(InetAddress.getLocalHost().getHostName(),
				eventPort);
	}

	@Override
	public void setEventReader() throws IOException {
		this.eventReader = new BufferedReader(new InputStreamReader(
				eventSocket.getInputStream()));
	}

	@Override
	public PrintWriter getRequestWriter() {
		return requestWriter;
	}

	@Override
	public BufferedReader getRequestReader() {
		return requestReader;
	}

	@Override
	public BufferedReader getEventReader() {
		return eventReader;
	}

	@Override
	public ChannelEventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public void setEventDispatcher(ChannelEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public ChannelResponseDispatcher getResponseDispatcher() {
		return responseDispatcher;
	}

	@Override
	public void setResponseDispatcher(
			ChannelResponseDispatcher responceDispatcher) {
		this.responseDispatcher = responceDispatcher;
	}

	@Override
	public void sendCommand(ESBDebuggerCommands command) throws Exception {
		requestWriter.println(messageFactory.createCommand(command));
		requestWriter.flush();
	}

	/**
	 * This method notify ESB Debugger about the event message got from ESB
	 * Server
	 * 
	 * @param eventMessage
	 */
	public void notifyEvent(String eventMessage) {
		try {
			esbDebugger.notifyEvent(messageFactory
					.convertEventMessageToMap(eventMessage));
		}catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void attachDebugger(IESBDebugger esbDebugger) {
		this.esbDebugger = esbDebugger;
	}

	/**
	 * This method notify ESB Debugger about the response message got from ESB
	 * Server
	 * 
	 * @param responceMessage
	 */
	public void notifyResponce(String responceMessage) {
		try {
			esbDebugger.notifyResponce(messageFactory
					.convertResponseMessageToMap(responceMessage));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void sendGetPropertiesCommand(Map<String, Object> attributeValues)
			throws Exception {
		requestWriter.println(messageFactory
				.createGetPropertiesCommand(attributeValues));
		requestWriter.flush();
	}

	@Override
	public void sendBreakpointCommand(Map<String, Object> attributeValues)
			throws Exception {
		requestWriter.println(messageFactory
				.createBreakpointCommand(attributeValues));
		requestWriter.flush();

	}

	@Override
	public void terminate() throws IOException {
		eventDispatcher.stop();
		responseDispatcher.stop();
		requestSocket.close();
		eventSocket.close();
		requestReader.close();
		requestWriter.close();
		eventReader.close();
	}

}
