package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer.Deserializer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbDiagramEditor;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbEditorInput;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbMultiPageEditor;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class OpenEditorUtil {

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	/**
	 * Open the ESB diagram editor for the given ESB configuration file  
	 * @param fileTobeOpened
	 */
	public static IEditorPart openSeparateEditor(final IFile fileTobeOpened) {
		try {
			final String source = FileUtils.readFileToString(fileTobeOpened.getLocation().toFile());
			final Deserializer deserializer = Deserializer.getInstance();
			
			
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			    @Override
			    public void run() {
			    	try {
			    		ArtifactType artifactType = deserializer.getArtifactType(source);
					
			        Display iw = PlatformUI.getWorkbench().getDisplay();
			        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart openEditor = activePage.openEditor(
							new EsbEditorInput(null, fileTobeOpened, artifactType.getLiteral()), EsbDiagramEditor.ID, true, IWorkbenchPage.MATCH_INPUT);
					EsbMultiPageEditor multipageEitor = ((EsbMultiPageEditor) openEditor);
					final EsbDiagramEditor graphicalEditor = multipageEitor.getGraphicalEditor();

					if (graphicalEditor != null) {
						Display.getCurrent().asyncExec(new Runnable() {
							public void run() {
								try {
									deserializer.updateDesign(source, graphicalEditor);
									graphicalEditor.doSave(new NullProgressMonitor());
									EditorUtils.setLockmode(graphicalEditor, false);

								} catch (Exception e) {
									log.error("Error occured while deserializing ", e);
								}

							}
						});
					}
			    }
			    catch (Exception e1) {
					log.error("Error occured while getting artifact type for the given ESB configuration ", e1);
				}
			    }
			});
			
		} catch (IOException e) {
			log.error("Error occured while opening a separate editor", e);
		}
		return null;
	}
	
}

