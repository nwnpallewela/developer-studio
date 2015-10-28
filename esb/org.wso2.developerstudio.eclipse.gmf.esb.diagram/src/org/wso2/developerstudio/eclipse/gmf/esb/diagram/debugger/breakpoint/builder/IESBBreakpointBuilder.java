/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;

/**
 * ESB Breakpoint Builders should implement this interface
 * 
 */
public interface IESBBreakpointBuilder {

	public ESBBreakpoint getESBBreakpoint(EsbServer esbServer,
			IResource resource, AbstractMediator part) throws CoreException,
			MediatorNotFoundException;

	public void updateExistingBreakpoints(IResource resource,
			AbstractMediator abstractMediator, EsbServer esbServer,
			String action) throws CoreException, MediatorNotFoundException;

}
