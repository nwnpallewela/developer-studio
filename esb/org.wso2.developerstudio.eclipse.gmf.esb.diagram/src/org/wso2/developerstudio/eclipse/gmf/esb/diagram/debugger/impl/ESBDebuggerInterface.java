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
import java.net.Socket;
import java.util.Map;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.IChannelCommunication;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.JsonJettisonMessageChannel;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelEventDispatcher;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.channel.dispatcher.ChannelResponceDispatcher;

public class ESBDebuggerInterface implements IESBDebuggerInterface {

	private Socket fRequestSocket;
	private PrintWriter fRequestWriter;
	private BufferedReader fRequestReader;

	private Socket fEventSocket;
	private BufferedReader fEventReader;

	private ChannelEventDispatcher eventDispatcher;
	private ChannelResponceDispatcher responceDispatcher;

	private IChannelCommunication messageChannel;
	private IESBDebugger esbDebugger;

	public ESBDebuggerInterface(int commandPort, int eventPort)
			throws IOException {
		setfRequestSocket(commandPort);
		setfEventSocket(eventPort);
		setfEventReader();
		setfRequestReader();
		setfRequestWriter();
		intializeDispatchers();
	}

	@Override
	public void intializeDispatchers() {

		eventDispatcher = new ChannelEventDispatcher();
		eventDispatcher.init(getfEventReader(), this);
		eventDispatcher.start();

		responceDispatcher = new ChannelResponceDispatcher();
		responceDispatcher.init(getfRequestReader(), this);
		responceDispatcher.start();

		messageChannel = new JsonJettisonMessageChannel();
	}

	@Override
	public void setfRequestSocket(int commandPort) throws IOException {
		this.fRequestSocket = new Socket("localhost", commandPort);
	}

	@Override
	public void setfRequestWriter() throws IOException {
		this.fRequestWriter = new PrintWriter(fRequestSocket.getOutputStream());
	}

	@Override
	public void setfRequestReader() throws IOException {
		this.fRequestReader = new BufferedReader(new InputStreamReader(
				fRequestSocket.getInputStream()));
	}

	@Override
	public void setfEventSocket(int eventPort) throws IOException {
		this.fEventSocket = new Socket("localhost", eventPort);
	}

	@Override
	public void setfEventReader() throws IOException {
		this.fEventReader = new BufferedReader(new InputStreamReader(
				fEventSocket.getInputStream()));
	}

	@Override
	public PrintWriter getfRequestWriter() {
		return fRequestWriter;
	}

	@Override
	public BufferedReader getfRequestReader() {
		return fRequestReader;
	}

	@Override
	public BufferedReader getfEventReader() {
		return fEventReader;
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
	public ChannelResponceDispatcher getResponceDispatcher() {
		return responceDispatcher;
	}

	@Override
	public void setResponceDispatcher(ChannelResponceDispatcher responceDispatcher) {
		this.responceDispatcher = responceDispatcher;
	}

	@Override
	public void sendCommand(String command) {
		fRequestWriter.println(messageChannel.createCommand(command));
		fRequestWriter.flush();
	}

	public void notifyEvent(String buffer) {
		esbDebugger.notifyEvent(messageChannel.getEvent(buffer));
	}

	@Override
	public void attachDebugger(IESBDebugger esbDebugger) {
		this.esbDebugger = esbDebugger;
	}

	public void notifyResponce(String buffer) {
		esbDebugger.notifyResponce(messageChannel.getResponce(buffer));

	}

	@Override
	public void sendGetPropertiesCommand(Map<String, Object> attributeValues) {
		fRequestWriter.println(messageChannel
				.createGetPropertiesCommand(attributeValues));
		fRequestWriter.flush();

	}

	@Override
	public void sendBreakpointCommand(String operation, String type,
			Map<String, Object> attributeValues) {
		fRequestWriter.println(messageChannel.createBreakpointCommand(
				operation, type, attributeValues));
		fRequestWriter.flush();

	}

}
