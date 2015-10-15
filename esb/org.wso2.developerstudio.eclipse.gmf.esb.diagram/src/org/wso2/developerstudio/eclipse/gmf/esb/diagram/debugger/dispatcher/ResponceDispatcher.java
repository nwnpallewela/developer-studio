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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher;

import java.io.BufferedReader;
import java.io.IOException;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebuggerInterface;

public class ResponceDispatcher extends Thread {
	BufferedReader fRequestReader;
	ESBDebuggerInterface esbDebuggerInterface;

	public void init(BufferedReader fRequestReader,
			ESBDebuggerInterface esbDebuggerInterface) {
		this.fRequestReader = fRequestReader;
		this.esbDebuggerInterface = esbDebuggerInterface;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (fRequestReader.ready()) {
					String buffer = fRequestReader.readLine();
					System.out.println("Responce : " + buffer);
					esbDebuggerInterface.notifyResponce(buffer);
				}
			}
		} catch (IOException e) {
			
		}
	}
}