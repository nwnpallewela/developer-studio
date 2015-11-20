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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.ui;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugTarget;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * {@link ESBDebugLaunchDelegate} handles the launch operation of ESBDebugger
 *
 */
public class ESBDebugLaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(final ILaunchConfiguration configuration,
			final String mode, final ILaunch launch,
			final IProgressMonitor monitor) throws CoreException {

		int commandPort = 0;
		int eventPort = 0;
		try {
			commandPort = Integer.parseInt(configuration.getAttribute(
					ESBDebuggerConstants.COMMAND_PORT_UI_TAG,
					ESBDebuggerConstants.DEFAULT_COMMAND_PORT));
			eventPort = Integer.parseInt(configuration.getAttribute(
					ESBDebuggerConstants.EVENT_PORTUI_TAG,
					ESBDebuggerConstants.DEFAULT_COMMAND_PORT));

			ESBDebugger esbDebugger = new ESBDebugger(commandPort, eventPort);
			ESBDebugTarget debugTarget = new ESBDebugTarget(launch);

			launch.addDebugTarget(debugTarget);
			esbDebugger.fireEvent(new DebuggerStartedEvent());

		} catch (UnknownHostException e) {
			ESBDebugerUtil.popUpErrorDialog(e,
					"IP address of the host could not be determined.");
		} catch (final IllegalArgumentException e) {
			ESBDebugerUtil.popUpErrorDialog(e,
					"Runtime Illegal Argument Error occured");
		} catch (final IOException e) {
			ESBDebugerUtil.popUpErrorDialog(e, "I/O Error occurred");
		}

	}

}