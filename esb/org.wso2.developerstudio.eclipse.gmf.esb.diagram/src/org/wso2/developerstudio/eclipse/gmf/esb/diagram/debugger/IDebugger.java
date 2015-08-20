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

import java.io.IOException;
import java.net.UnknownHostException;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;

public interface IDebugger {

	int UNSPECIFIED = 0;
	int SUSPEND_BREAKPOINT = 1;
	int SUSPEND_STEP_OVER = 2;
	int RESUME_STEP_OVER = 3;

	public void loaded();

	public boolean isBreakpoint(int lineNumber);

	public void terminated();

	public void suspended(int lineNumber);

	public void resumed();

	public void setDebuggerInterface(int commandPort, int eventPort)
			throws UnknownHostException, IllegalArgumentException, IOException;

	public IESBDebuggerInterface getESBDebuggerInterface();

	public void setEventDispatcher(EventDispatchJob dispatcher);
}
