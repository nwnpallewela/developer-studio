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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.IESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.dispatcher.EventDispatchJob;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.DebuggerStartedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebugger;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.impl.ESBDebuggerInterface;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugTarget;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.DebuggerCommunicationMessageModel;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class ESBLaunchDelegate implements ILaunchShortcut, ILaunchShortcut2,
		ILaunchConfigurationDelegate {

	// *******************************
	// ILaunchShortcut
	// **********************************************************************
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	@Override
	public final void launch(final IEditorPart editor, final String mode) {
	}

	@Override
	public final void launch(final ISelection selection, final String mode) {
	}

	// **********************************************************************
	// ILaunchShortcut2
	// **********************************************************************

	@Override
	public final IResource getLaunchableResource(final IEditorPart editorpart) {
		final IEditorInput input = editorpart.getEditorInput();
		if (input instanceof FileEditorInput)
			return ((FileEditorInput) input).getFile();

		return null;
	}

	@Override
	public final IResource getLaunchableResource(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			for (final Object element : ((IStructuredSelection) selection)
					.toArray()) {
				if (element instanceof IFile)
					return (IResource) element;
			}
		}

		return null;
	}

	// **********************************************************************
	// ILaunchConfigurationDelegate
	// **********************************************************************

	@Override
	public void launch(final ILaunchConfiguration configuration,
			final String mode, final ILaunch launch,
			final IProgressMonitor monitor) throws CoreException {

		DebuggerCommunicationMessageModel.populateMessageModels();

		int commandPort = Integer.parseInt(configuration.getAttribute(
				ESBLaunchConstants.COMMAND_PORT,
				ESBLaunchConstants.DEFAULT_COMMAND_PORT));
		int eventPort = Integer.parseInt(configuration.getAttribute(
				ESBLaunchConstants.EVENT_PORT,
				ESBLaunchConstants.DEFAULT_COMMAND_PORT));

		IESBDebugger esbDebugger = new ESBDebugger(new ESBDebuggerInterface());
		try {
			esbDebugger.setDebuggerInterface(commandPort, eventPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			log.error("Port already taken", e);
			String simpleMessage = e.getMessage();
			IStatus editorStatus = new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, simpleMessage);
			ErrorDialog.openError(Display.getDefault().getActiveShell(),
					"Error",
					"IO error is detected when trying assign port to socket ",
					editorStatus);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// create debugTarget
		int requestPortInternal = -1;
		int eventPortInternal = -1;
		/*
		 * requestPortInternal = findFreePort(); eventPortInternal =
		 * findFreePort(); if (requestPortInternal == -1 || eventPortInternal ==
		 * -1) { System.out.println("Unable to find free port"); }
		 */
		ESBDebugTarget debugTarget = new ESBDebugTarget(launch,
				requestPortInternal, eventPortInternal);

		// create & launch dispatcher
		EventDispatchJob dispatcher = new EventDispatchJob(debugTarget,
				esbDebugger);
		dispatcher.schedule();

		// attach dispatcher to debugger & debugTarget
		esbDebugger.setEventDispatcher(dispatcher);
		debugTarget.setEventDispatcher(dispatcher);

		// add debug target to launch
		launch.addDebugTarget(debugTarget);
		debugTarget.handleEvent(new DebuggerStartedEvent());

	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}