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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.draw2d.RoundedRectangle;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EsbGroupingShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FilterMediatorGraphicalShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedSizedAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.complexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;

public class ESBDebugerUtil {

	public static void addBreakpointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) part).getPrimaryShape()
					.addBreakpointMark();
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).addBreakpointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
	}

	public static void removeBreakpointMark(AbstractMediator part) {
		if (part instanceof FixedSizedAbstractMediator) {
			((FixedSizedAbstractMediator) part).getPrimaryShape()
					.removeBreakpointMark();
		} else if (part instanceof complexFiguredAbstractMediator) {
			RoundedRectangle shape = ((complexFiguredAbstractMediator) part)
					.getPrimaryShape();
			if (shape instanceof EsbGroupingShape) {
				((EsbGroupingShape) shape).removeBreakpointMark();
			} else if (shape instanceof FilterMediatorGraphicalShape) {
				// ((FilterMediatorGraphicalShape)shape).addBreakpointMark();
			}
		}
	}

	public static Map<String, String> getDetailsToMap(IBreakpoint breakpoint) {
		String message = ((ESBBreakpoint)breakpoint).getMessage();
		Map<String, String> detailsMap = new LinkedHashMap<>();
		System.out.println(message);
		String[] attributes = message.split(",");
		for (String string : attributes) {
			String[] keyValuePair = string.split(":");
			if(keyValuePair.length==2){
				detailsMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
		return detailsMap;
	}
}
