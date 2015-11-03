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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.requests;

import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.model.AbstractEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;

/**
 * {@link ResumeRequest} represent the request event from {@link ESBDebugTarget}
 * to {@link ESBDebugger} when {@link ESBDebugTarget} asks ESB Server for
 * resume.
 *
 */
public class ResumeRequest extends AbstractEvent implements IModelRequest {

	public static final int STEP_OVER = 1;
	public static final int CONTINUE = 2;

	private final int mType;
	private int mLineNumber;

	public ResumeRequest(int type) {
		mType = type;
		mLineNumber = -1;
	}

	public ResumeRequest(int type, int lineNumber) {
		this(type);
		mLineNumber = lineNumber;
	}

	public int getType() {
		return mType;
	}

	public int getLineNumber() {
		return mLineNumber;
	}
}
