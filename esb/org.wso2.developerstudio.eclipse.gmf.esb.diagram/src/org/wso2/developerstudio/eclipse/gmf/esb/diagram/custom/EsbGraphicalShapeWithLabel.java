/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.draw2d.ui.figures.RoundedRectangleBorder;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * Draws a Figure with a label
 */
public class EsbGraphicalShapeWithLabel extends RoundedRectangle {

	public GridData tempConstraintPropertyValueRectangle;
	public RoundedRectangle tempPropertyValueRectangle1;
	private WrappingLabel propertyNameLabel;
	private RoundedRectangle mainImageRectangle;
	static int Figure_PreferredWidth = FixedSizedAbstractMediator.FigureWidth;
	static int Figure_PreferredHeight = FixedSizedAbstractMediator.FigureHeight + 20; //Additional 20 to show the editable label
	static int Image_PreferredWidth = 75;
	static int Image_PreferredHeight = 42;
	static int marginWidth = (Figure_PreferredWidth - Image_PreferredWidth) / 2; //equals to 10
	static int marginHeight = 10;
	private LayeredPane pane; 
	private Layer figureLayer;
	private Layer breakpointLayer;
	protected String toolTipMessage;
	
	public void setToolTipMessage(String message){
		toolTipMessage = message;
	}
	
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
	

	public EsbGraphicalShapeWithLabel() {
		initializeShape();
	}
	
	private void initializeShape(){
		pane = new LayeredPane();
		pane.setLayoutManager(new StackLayout());
		GridLayout layoutThis = new GridLayout();
		layoutThis.numColumns = 1;
		layoutThis.makeColumnsEqualWidth = true;
		layoutThis.horizontalSpacing = 0;
		layoutThis.verticalSpacing = -5;
		layoutThis.marginHeight = 3;
		layoutThis.marginWidth = 0;

		this.setLayoutManager(layoutThis);
		this.setCornerDimensions(new Dimension(8, 8));
		this.setOutline(false);

		RoundedRectangleBorder border = new RoundedRectangleBorder(8, 8);
		border.setColor(EditPartDrawingHelper.FigureNormalColor);
		border.setWidth(0);
        this.setBorder(border);
        
        
		createContents();
		pane.add(figureLayer);
		this.add(pane);
	}
	
	public void addBreakpointMark() {
		if (pane != null) {
			breakpointLayer = new Layer();
			breakpointLayer.setLayoutManager(new StackLayout());
			GridData constraintBreakpointImageRectangle = new GridData();
			constraintBreakpointImageRectangle.verticalAlignment = GridData.BEGINNING;
			constraintBreakpointImageRectangle.horizontalAlignment = GridData.BEGINNING;
			constraintBreakpointImageRectangle.verticalSpan = 1;
			ImageFigure iconImageFigure = EditPartDrawingHelper
					.getIconImageFigure(
							"icons/ico20debug/toggle_breakpoint_red.gif", 10,
							10);
//			ImageFigure iconImageFigure = EditPartDrawingHelper
//					.getIconImageFigure(
//							"icons/ico20debug/breakpoint_16.gif", 10,
//							10);

			RoundedRectangle breakpointImageRectangle = new RoundedRectangle();
			breakpointImageRectangle.setCornerDimensions(new Dimension(2, 2));
			breakpointImageRectangle.setOutline(false);
			breakpointImageRectangle.setPreferredSize(new Dimension(10, 10));
			breakpointImageRectangle.setAlpha(0);
			breakpointImageRectangle.add(iconImageFigure);
			iconImageFigure.translate((this.getSize().width / 2
					- mainImageRectangle.getSize().width / 2 + 3), 3);
			breakpointLayer.add(breakpointImageRectangle,
					constraintBreakpointImageRectangle);
			/*try {
			//	this.remove(pane);
			} catch (NullPointerException e) {
				log.error("Mediator icon figure does not have a layer pane", e);
			}*/
			pane.add(breakpointLayer);
			//this.add(pane);
		} else {
			log.warn("Mediator Figure layers misplaced");
			initializeShape();
			addBreakpointMark();
		}
	}

	public void removeBreakpointMark() {
		/*try {
		//	this.remove(pane);
		} catch (NullPointerException e) {
			log.error("Mediator icon figure does not have a layer pane", e);
		}*/

		try {
			pane.remove(breakpointLayer);
		} catch (NullPointerException e) {
			log.error(
					"Mediator icon layer pane does not have a breakpoint layer",
			e);
		}
		//this.add(pane);

	}
	

	private void createContents() {

		/* main image grid data */
		figureLayer=new Layer();
		figureLayer.setLayoutManager(new GridLayout());
		GridData constraintMainImageRectangle = new GridData();
		constraintMainImageRectangle.verticalAlignment = GridData.BEGINNING;
		constraintMainImageRectangle.horizontalAlignment = GridData.CENTER;
		constraintMainImageRectangle.verticalSpan = 1;
		ImageFigure iconImageFigure = EditPartDrawingHelper.getIconImageFigure(getIconPath(),
				Image_PreferredWidth, Image_PreferredHeight);

		mainImageRectangle = new RoundedRectangle();
		mainImageRectangle.setCornerDimensions(new Dimension(8, 8));
		mainImageRectangle.setOutline(false);
		mainImageRectangle.setPreferredSize(new Dimension(Image_PreferredWidth,
				Image_PreferredHeight));
		mainImageRectangle.add(iconImageFigure);
		figureLayer.add(mainImageRectangle, constraintMainImageRectangle);

		RoundedRectangle propertyValueRectangle1 = new RoundedRectangle();
		propertyValueRectangle1.setCornerDimensions(new Dimension(0, 0));
		propertyValueRectangle1.setOutline(false);
	
		GridData constraintPropertyValueRectangle = new GridData();
		constraintPropertyValueRectangle.verticalAlignment = GridData.BEGINNING;
		constraintPropertyValueRectangle.horizontalAlignment = GridData.FILL;
		constraintPropertyValueRectangle.horizontalIndent = 0;
		constraintPropertyValueRectangle.horizontalSpan = 1;
		constraintPropertyValueRectangle.verticalSpan = 1;
		constraintPropertyValueRectangle.grabExcessHorizontalSpace = true;
		constraintPropertyValueRectangle.grabExcessVerticalSpace = true;
		
		propertyValueRectangle1.setLayoutManager(new StackLayout());

		// Label to display description.
		propertyNameLabel = new WrappingLabel();
		propertyNameLabel.setText(getNodeName());
		propertyNameLabel.setForegroundColor(new Color(null, 46, 46, 46));
		propertyNameLabel.setFont(new Font(null, new FontData("Courier", 8, SWT.NONE)));
		propertyNameLabel.setAlignment(SWT.CENTER);
		propertyNameLabel.setPreferredSize(new Dimension(FixedSizedAbstractMediator.maxFigureWidth, 20));
		
		propertyValueRectangle1.add(propertyNameLabel);
		figureLayer.add(propertyValueRectangle1, constraintPropertyValueRectangle);
		tempPropertyValueRectangle1=propertyValueRectangle1;
		tempConstraintPropertyValueRectangle=constraintPropertyValueRectangle;
	}

	protected WrappingLabel getPropertyNameLabel() {
		return propertyNameLabel;
	}

	public String getIconPath() {
		return "override this in the child class with actual icon path";
	}

	public String getNodeName() {
		return "<default>";
	}

	public Color getLabelBackColor() {
		return this.getBackgroundColor();
	}

	
}