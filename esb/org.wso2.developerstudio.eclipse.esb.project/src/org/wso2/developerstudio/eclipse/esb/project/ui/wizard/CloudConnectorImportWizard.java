/*
 * Copyright (c) 2010-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.developerstudio.eclipse.esb.project.ui.wizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TableItem;
import org.wso2.developerstudio.eclipse.esb.project.Activator;
import org.wso2.developerstudio.eclipse.esb.project.connector.store.Connector;
import org.wso2.developerstudio.eclipse.esb.project.control.graphicalproject.GMFPluginDetails;
import org.wso2.developerstudio.eclipse.esb.project.control.graphicalproject.IUpdateGMFPlugin;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class CloudConnectorImportWizard extends Wizard {

	private static final int BUFFER_SIZE = 4096;
	private ImportCloudConnectorWizardPage storeWizardPage;
	private static final String DIR_DOT_METADATA = ".metadata";
	private static final String DIR_CONNECTORS = ".Connectors";	

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	/**
	 * Initializing wizard
	 * @param selection
	 */
	public void init(IStructuredSelection selection) {
		storeWizardPage = new ImportCloudConnectorWizardPage(selection);
	}

	/**
	 * Adding wizard pages
	 */
	public void addPages() {
		addPage(storeWizardPage);
		super.addPages();
	}

	/**
	 * Importing connector zip file to Developer Studio either from fileSystem or
	 * connector store.
	 */
	public boolean performFinish() {
		if (storeWizardPage.getConnectorStore().getSelection()) {
			return performFinishStore();
		} else if (storeWizardPage.getFileSystem().getSelection()) {
			return performFinishFileSystem();
		}
		return false;
	}

	/**
	 * This method will download the connector zip file and extract it to the
	 * relevant location when user has selected import from connector store option.
	 */
	private boolean performFinishStore() {
		for (TableItem tableItem : storeWizardPage.getTable().getItems()) {
			if (tableItem.getChecked()) {
				String downloadLink = ((Connector) tableItem.getData()).getAttributes().getOverview_downloadlink();
				if(!downloadConnectorAndUpdateProjects(downloadLink)){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method will extract connector zip file to the relevant location when
	 * user has selected import from file system option.
	 */
	private boolean performFinishFileSystem() {
		String source = storeWizardPage.getCloudConnectorPath();
		try {
			updateProjects(source);
		} catch (ZipException e) {
			log.error("Error while extracting the connector zip : " + source, e);
		} catch (CoreException e) {
			log.error("Cannot refresh the project", e);
		}
		return true;
	}

	private void updateProjects(String source) throws ZipException, CoreException {
		ZipFile zipFile = new ZipFile(source);
		String[] segments = source.split(Pattern.quote(File.separator));
		String zipFileName = segments[segments.length - 1].split(".zip")[0];
		String parentDirectoryPath = storeWizardPage.getSelectedProject().getWorkspace().getRoot().getLocation().toString()
				+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CONNECTORS ;		
		File parentDirectory = new File(parentDirectoryPath);
		if (!parentDirectory.exists()) {
			parentDirectory.mkdir();
		}		
		String zipDestination = parentDirectoryPath + File.separator + zipFileName;		
		zipFile.getFile();
		zipFile.extractAll(zipDestination);
		IUpdateGMFPlugin updateGMFPlugin = GMFPluginDetails.getiUpdateGMFPlugin();
		if (updateGMFPlugin != null) {
			updateGMFPlugin.updateOpenedEditors();
		}
		/*
		 * Refresh the project.
		 */
		storeWizardPage.getSelectedProject().refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	private boolean downloadConnectorAndUpdateProjects(String downloadLink) {
		String zipDestination = null;
		try {
			URL url = new URL(downloadLink);
			String[] segments = downloadLink.split("/");
			String zipFileName = segments[segments.length - 1];
			String parentDirectoryPath = storeWizardPage.getSelectedProject().getWorkspace().getRoot().getLocation()
					.toString()
					+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CONNECTORS;
			File parentDirectory = new File(parentDirectoryPath);
			if (!parentDirectory.exists()) {
				parentDirectory.mkdir();
			}
			zipDestination = parentDirectoryPath + File.separator + zipFileName;
			InputStream is = url.openStream();
			File targetFile = new File(zipDestination);
			targetFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = is.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			is.close();
			updateProjects(zipDestination);
			return true;
		} catch (ZipException e) {
			log.error("Error while extracting the connector zip : " + zipDestination, e);
		} catch (CoreException e) {
			log.error("Cannot refresh the project", e);
		} catch (MalformedURLException malformedURLException) {
			log.error("Malformed connector URL provided : " + downloadLink, malformedURLException);
		} catch (IOException e) {
			log.error("Error while downloading connector : " + downloadLink, e);
		}
		return false;
	}	
}
