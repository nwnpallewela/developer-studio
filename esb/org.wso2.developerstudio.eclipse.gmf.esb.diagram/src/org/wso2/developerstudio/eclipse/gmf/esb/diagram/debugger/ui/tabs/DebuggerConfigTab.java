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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.ui.tabs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;

/**
 * {@link DebuggerConfigTab} implements the UI tab in Debugger configuration for
 * ESB Debugger
 */
public class DebuggerConfigTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {
	private Text commandPort;
	private Text eventPort;

	public DebuggerConfigTab() {
	}

	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration
				.setAttribute(ESBDebuggerConstants.ESB_SERVER_LOCATION, "");
	}

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		if (StringUtils.isEmpty(commandPort.getText())) {
			commandPort.setText(ESBDebuggerConstants.DEFAULT_COMMAND_PORT);
		}
		if (StringUtils.isEmpty(eventPort.getText())) {
			eventPort.setText(ESBDebuggerConstants.DEFAULT_EVENT_PORT);
		}
	}

	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(ESBDebuggerConstants.COMMAND_PORT_UI_TAG,
				commandPort.getText().toString());
		configuration.setAttribute(ESBDebuggerConstants.EVENT_PORTUI_TAG,
				eventPort.getText().toString());
	}

	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {

		try {
			Integer.parseInt(commandPort.getText());
			Integer.parseInt(eventPort.getText());
			setErrorMessage(null);
			return true;
		} catch (NumberFormatException e) {
			setErrorMessage("Port values should be integers");
			return false;
		}

	}

	@Override
	public boolean canSave() {
		return isDirty();
	}

	@Override
	public String getMessage() {
		return ESBDebuggerConstants.ESB_Debugger_LAUNCH_CONFIGURAION_MAIN_TAB_MESSAGE;
	}

	@Override
	public String getName() {
		return ESBDebuggerConstants.ESB_Debugger_LAUNCH_CONFIGURAION_MAIN_TAB_TITLE;
	}

	@Override
	public void createControl(final Composite parent) {
		Composite topControl = new Composite(parent, SWT.NONE);
		topControl.setLayout(new GridLayout(1, false));

		Group mgroup = new Group(topControl, SWT.NONE);
		mgroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		mgroup.setText("Command Port");
		mgroup.setLayout(new GridLayout(2, false));

		commandPort = new Text(mgroup, SWT.BORDER);
		commandPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Group grpLaunch = new Group(topControl, SWT.NONE);
		grpLaunch.setLayout(new GridLayout(2, false));
		grpLaunch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		grpLaunch.setText("Event Port");

		eventPort = new Text(grpLaunch, SWT.BORDER);
		eventPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		setControl(topControl);
	}
}
