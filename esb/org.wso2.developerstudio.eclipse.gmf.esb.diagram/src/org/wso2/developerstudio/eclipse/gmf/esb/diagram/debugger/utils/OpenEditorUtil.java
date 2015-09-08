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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbDiagram;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer.Deserializer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.IMediatorLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.impl.MediatorLocatorFactory;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbDiagramEditor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class OpenEditorUtil {

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
	private static AbstractMediator previousHitPoint;

	public static void removeBreakpointHitStatus() {
		if (previousHitPoint != null) {
			previousHitPoint.setBreakpointHitStatus(false);
			previousHitPoint.setSelected(EditPart.SELECTED_NONE);
		}
	}

	public static AbstractMediator getPreviousHitEditPart() {
		return previousHitPoint;
	}

	/**
	 * Open the ESB diagram editor for the given ESB configuration file
	 * 
	 * @param fileTobeOpened
	 */
	public static IEditorPart openSeparateEditor(final IFile fileTobeOpened,
			final SuspendedEvent event) {
		try {
			final String source = FileUtils.readFileToString(fileTobeOpened
					.getLocation().toFile());
			final Deserializer deserializer = Deserializer.getInstance();

			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						ArtifactType artifactType = deserializer
								.getArtifactType(source);

						IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						IEditorPart openEditor = activePage.openEditor(
								new EsbEditorInput(null, fileTobeOpened,
										artifactType.getLiteral()),
								EsbDiagramEditor.ID, true,
								IWorkbenchPage.MATCH_INPUT);
						EsbMultiPageEditor multipageEitor = ((EsbMultiPageEditor) openEditor);
						final EsbDiagramEditor graphicalEditor = multipageEitor
								.getGraphicalEditor();

						if (graphicalEditor != null) {
							Display.getCurrent().syncExec(new Runnable() {
								public void run() {
									try {
										// deserializer.updateDesign(source,
										// graphicalEditor);
										graphicalEditor
												.doSave(new NullProgressMonitor());
										EditorUtils.setLockmode(
												graphicalEditor, false);

									} catch (Exception e) {
										log.error(
												"Error occured while deserializing ",
												e);
									}

								}
							});
						}

						Diagram diagram = multipageEitor.getDiagram();
						EsbDiagram esbDiagram = (EsbDiagram) diagram
								.getElement();
						EsbServer esbServer = esbDiagram.getServer();

						IMediatorLocator mediatorLocator = MediatorLocatorFactory
								.getMediatorLocator(esbServer.getType()
										.getName());
						if (mediatorLocator != null) {
							if (previousHitPoint != null) {
								previousHitPoint.setBreakpointHitStatus(false);
								previousHitPoint
										.setSelected(EditPart.SELECTED_NONE);
							}
							EditPart editPart = mediatorLocator
									.getMediatorEditPart(esbServer, event);
							if (editPart instanceof AbstractMediator) {
								((AbstractMediator) editPart)
										.setBreakpointHitStatus(true);
								while(true){
									if(((AbstractMediator) editPart).isBreakpointHit()==true){
										break;
									}
								}
								editPart.setSelected(EditPart.SELECTED);
								previousHitPoint = ((AbstractMediator) editPart);
							}

						}

					} catch (Exception e1) {
						log.error(
								"Error occured while getting artifact type for the given ESB configuration ",
								e1);
					}
				}
			});

		} catch (IOException e) {
			log.error("Error occured while opening a separate editor", e);
		}
		return null;
	}

}
