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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

public class SourceFileContentProvider extends BaseWorkbenchContentProvider implements ITreeContentProvider {

    private final String[] mExtensions;

    public SourceFileContentProvider(final String[] extensions) {
        mExtensions = extensions;
    }

    @Override
    public Object[] getChildren(final Object element) {
        // remove all file entries where extensions are rejected
        List<Object> children = Arrays.asList(super.getChildren(element));
        List<Object> filteredChildren = new ArrayList<Object>(children);

        for (Object child : children) {
            if ((child instanceof IFile) && (!isAccepted((IFile) child)))
                filteredChildren.remove(child);
        }

        return filteredChildren.toArray();
    }

    private boolean isAccepted(final IFile child) {
        for (String extension : mExtensions) {
            if (extension.equalsIgnoreCase(child.getFileExtension()))
                return true;
        }

        return false;
    }
}
