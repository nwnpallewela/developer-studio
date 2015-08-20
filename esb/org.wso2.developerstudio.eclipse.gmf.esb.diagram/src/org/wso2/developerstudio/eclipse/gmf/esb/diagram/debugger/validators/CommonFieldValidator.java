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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.validators;

import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class CommonFieldValidator {

	private static int MIN_PORT_NUMBER = 1024;
	private static int MAX_PORT_NUMBER = 65535;
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public static boolean isPortValid(int portNumber) throws IllegalArgumentException {
		if (portNumber < MIN_PORT_NUMBER || portNumber > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid port number : " + portNumber);
		}
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(portNumber);
		} catch (IOException e) {
			throw new IllegalArgumentException("Port " + portNumber + " is already taken.");
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					log.error("Can not close temp socket", e);
					String simpleMessage = e.getMessage();
					IStatus editorStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, simpleMessage);
					ErrorDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"IO error is detected when trying to close temperary socket", editorStatus);
				}
			}
		}
		return true;
	}

}
