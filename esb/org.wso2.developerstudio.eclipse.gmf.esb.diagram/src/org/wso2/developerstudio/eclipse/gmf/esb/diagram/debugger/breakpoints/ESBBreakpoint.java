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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoints;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;

public class ESBBreakpoint implements IBreakpoint{

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IMarker getMarker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPersisted() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRegistered() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEnabled(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMarker(IMarker arg0) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPersisted(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRegistered(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		
	}

}

