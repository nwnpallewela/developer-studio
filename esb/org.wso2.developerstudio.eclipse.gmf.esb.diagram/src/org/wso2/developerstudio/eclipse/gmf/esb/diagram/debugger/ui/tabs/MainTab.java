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
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.ui.ESBLaunchConstants;

public class MainTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {
    private Text commandPort;
    private boolean mDisableUpdate = false;
    private Text eventPort;
    private final String[] mExtensions;

    public MainTab(final String[] extensions) {
        mExtensions = extensions;
    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(ESBLaunchConstants.FILE_LOCATION, "");
    }

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		mDisableUpdate = true;
		if ("".equals(commandPort.getText())) {
			commandPort.setText(ESBLaunchConstants.DEFAULT_COMMAND_PORT);
		}
		if ("".equals(eventPort.getText())) {
			eventPort.setText(ESBLaunchConstants.DEFAULT_EVENT_PORT);
		}
		mDisableUpdate = false;
	}

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(ESBLaunchConstants.COMMAND_PORT, commandPort.getText().toString());
        configuration.setAttribute(ESBLaunchConstants.EVENT_PORT, eventPort.getText().toString());
    }

    @Override
    public boolean isValid(final ILaunchConfiguration launchConfig) {
        try {

        } catch (Exception e) {
            setErrorMessage("Invalid port number.");
        }

        return true;
    }

    @Override
    public boolean canSave() {
        return (!commandPort.getText().isEmpty()) && (!eventPort.getText().isEmpty());
    }

    @Override
    public String getMessage() {
        return "Please select command and event ports.";
    }

    @Override
    public String getName() {
        return "Global";
    }

    @Override
    public void createControl(final Composite parent) {
        Composite topControl = new Composite(parent, SWT.NONE);
        topControl.setLayout(new GridLayout(1, false));

        Group mgroup = new Group(topControl, SWT.NONE);
        mgroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mgroup.setText("Command Port");
        mgroup.setLayout(new GridLayout(2, false));

        commandPort = new Text(mgroup, SWT.BORDER);
        commandPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));


        Group grpLaunch = new Group(topControl, SWT.NONE);
        grpLaunch.setLayout(new GridLayout(2, false));
        grpLaunch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpLaunch.setText("Event Port");

        eventPort = new Text(grpLaunch, SWT.BORDER);
        eventPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setControl(topControl);
    }
}
