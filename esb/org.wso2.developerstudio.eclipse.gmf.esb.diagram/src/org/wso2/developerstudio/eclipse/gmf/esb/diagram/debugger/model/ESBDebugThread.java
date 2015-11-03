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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;

public class ESBDebugThread extends ESBDebugElement implements IThread {

	private final List<ESBStackFrame> mStackFrames = new ArrayList<>();

	public ESBDebugThread(final ESBDebugTarget debugTarget) {
		super(debugTarget);
	}

	public void addStackFrame(ESBStackFrame stackFrame) {
		mStackFrames.add(0, stackFrame);
	}

	@Override
	public ESBStackFrame[] getStackFrames() {
		return mStackFrames.toArray(new ESBStackFrame[mStackFrames.size()]);
	}

	@Override
	public boolean hasStackFrames() {
		return getStackFrames().length > 0;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public ESBStackFrame getTopStackFrame() {
		if (!mStackFrames.isEmpty())
			return mStackFrames.get(0);

		return null;
	}

	@Override
	public String getName() {
		return "ESB Debug Thread";
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		return DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(getModelIdentifier());
	}
}
