package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbDiagramEditorPlugin;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class EsbGroupingShape extends RoundedRectangle {

	RectangleFigure propertyValueRectangle1;
	RoundedRectangle leftRectangle;
	RoundedRectangle container;
	private LayeredPane pane; 
	private Layer figureLayer;
	private Layer breakpointLayer;
	static int Image_PreferredWidth = 75;
	static int Image_PreferredHeight = 42;
	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
	protected String toolTipMessage;
	
	public void setToolTipMessage(String message){
		toolTipMessage = message;
	}
	
	public EsbGroupingShape() {
		GridLayout layoutThis = new GridLayout();
		layoutThis.numColumns = 2;
		layoutThis.makeColumnsEqualWidth = true;
		layoutThis.marginHeight = 0;
		layoutThis.marginWidth = 0;
		this.setLayoutManager(layoutThis);
		this.setCornerDimensions(new Dimension(1, 1));
		//this.setFill(false); // dont uncoment this since we need to show the selection color from botom do not repaiint
	
		this.setOutline(false);
		this.setBorder(new LineBorder(
				EditPartDrawingHelper.ComplexFigureSelectedBorderColor, 2,
				SWT.BORDER_DASH));
		createContents();
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

			RoundedRectangle mainImageRectangle = new RoundedRectangle();
			mainImageRectangle.setCornerDimensions(new Dimension(2, 2));
			mainImageRectangle.setOutline(false);
			mainImageRectangle.setPreferredSize(new Dimension(10, 10));
			mainImageRectangle.setAlpha(0);
			mainImageRectangle.add(iconImageFigure);
			iconImageFigure.translate(0, 2);
			breakpointLayer.add(mainImageRectangle,
					constraintBreakpointImageRectangle);
			try {
				container.remove(pane);
			} catch (NullPointerException e) {
				log.error("Mediator icon figure does not have a layer pane", e);
			}
			pane.add(breakpointLayer);
			container.add(pane);
		} else {
			log.warn("Mediator Figure layers misplaced");
		//	initializeShape();
			addBreakpointMark();
		}
	}

	public void removeBreakpointMark() {
		try {
			container.remove(pane);
		} catch (NullPointerException e) {
			log.error("Mediator icon figure does not have a layer pane", e);
		}

		try {
			pane.remove(breakpointLayer);
		} catch (NullPointerException e) {
			log.error(
					"Mediator icon layer pane does not have a breakpoint layer",
					e);
		}
		container.add(pane);

	}

	private void createContents() {
		// Create left side rectangle.
		pane = new LayeredPane();
		pane.setLayoutManager(new StackLayout());
		figureLayer=new Layer();
		figureLayer.setLayoutManager(new GridLayout());
		
		leftRectangle = new RoundedRectangle();
		leftRectangle.setCornerDimensions(new Dimension(1, 1));
		leftRectangle.setOutline(false);
		leftRectangle.setFill(false);
		leftRectangle.setPreferredSize(new Dimension(110, 30));
		leftRectangle.setMinimumSize(new Dimension(90, 95));

		GridLayout layoutGraphicalNodeContainer = new GridLayout();
		layoutGraphicalNodeContainer.numColumns = 1;
		layoutGraphicalNodeContainer.horizontalSpacing = 5;
		layoutGraphicalNodeContainer.verticalSpacing = 5;
		layoutGraphicalNodeContainer.marginHeight = 5;
		layoutGraphicalNodeContainer.marginWidth = 5;
		leftRectangle.setLayoutManager(layoutGraphicalNodeContainer);

		GridData constraintGraphicalNodeContainer = new GridData();
		constraintGraphicalNodeContainer.verticalAlignment = GridData.CENTER;
		constraintGraphicalNodeContainer.horizontalAlignment = GridData.CENTER;

		this.add(leftRectangle, constraintGraphicalNodeContainer);

		// Create inner rectangle inside the left side rectangle.
		
		container = createInnerRectangle(leftRectangle);

		ImageDescriptor imgDesc = EsbDiagramEditorPlugin.getBundledImageDescriptor(getIconPath());

		Image image = imgDesc.createImage();
		Image scaled = new Image(Display.getDefault(), 30, 30);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, 30, 30);
		gc.dispose();

		ImageFigure img = new ImageFigure(image);
		img.setSize(new Dimension(36, 40));


		GridData constraintImageRectangle = new GridData();
		constraintImageRectangle.verticalAlignment = GridData.END;
		constraintImageRectangle.horizontalAlignment = GridData.CENTER;
		constraintImageRectangle.horizontalIndent = 0;
		constraintImageRectangle.horizontalSpan = 1;
		constraintImageRectangle.verticalSpan = 2;
		constraintImageRectangle.grabExcessHorizontalSpace = true;
		constraintImageRectangle.grabExcessVerticalSpace = true;
		figureLayer.add(img, constraintImageRectangle);

		// Rectangle to hold item name (ex. Aggregate, Cache, etc.).
		RectangleFigure esbNodeTypeNameRectangle = new RectangleFigure();
		esbNodeTypeNameRectangle.setOutline(false);
		esbNodeTypeNameRectangle.setBackgroundColor(new Color(null, 233, 245, 215));
		esbNodeTypeNameRectangle.setPreferredSize(new Dimension(64, 20));

		GridData constraintEsbNodeTypeNameRectangle = new GridData();
		constraintEsbNodeTypeNameRectangle.verticalAlignment = GridData.BEGINNING;
		constraintEsbNodeTypeNameRectangle.horizontalAlignment = GridData.CENTER;
		constraintEsbNodeTypeNameRectangle.horizontalIndent = 0;
		constraintEsbNodeTypeNameRectangle.horizontalSpan = 1;
		constraintEsbNodeTypeNameRectangle.verticalSpan = 1;
		constraintEsbNodeTypeNameRectangle.grabExcessHorizontalSpace = true;
		constraintEsbNodeTypeNameRectangle.grabExcessVerticalSpace = true;

		esbNodeTypeNameRectangle.setLayoutManager(new StackLayout());

		// Actual label to display which type this is.
		WrappingLabel esbNodeTypeNameLabel = new WrappingLabel();
		esbNodeTypeNameLabel.setText(getNodeName());
		esbNodeTypeNameLabel.setForegroundColor(new Color(null, 0, 0, 0));
		esbNodeTypeNameLabel.setFont(new Font(null, "Arial", 10, SWT.BOLD));
		esbNodeTypeNameLabel.setAlignment(SWT.CENTER);
		esbNodeTypeNameLabel.setPreferredSize(new Dimension(64, 20));
		figureLayer.add(esbNodeTypeNameLabel, constraintEsbNodeTypeNameRectangle);
		pane.add(figureLayer);
		container.add(pane);
	}

	private RoundedRectangle createInnerRectangle(RoundedRectangle leftRectangle) {
		
		RoundedRectangle innerRect = new RoundedRectangle();
		innerRect.setCornerDimensions(new Dimension(1, 1));
		innerRect.setOutline(false);
		innerRect.setBackgroundColor(this.getBackgroundColor());
		LineBorder innerRectBorder = new LineBorder(new Color(null, 90, 90, 90), 1,
				SWT.BORDER_SOLID);
		innerRect.setBorder(innerRectBorder);
		innerRect.setPreferredSize(new Dimension(95, 25));
		innerRect.setMinimumSize(new Dimension(80, 100));
		innerRect.setBackgroundColor(new Color(null, 233, 245, 215));

		GridLayout innerRectLayout = new GridLayout();
		innerRectLayout.numColumns = 1;
		innerRectLayout.makeColumnsEqualWidth = true;
		innerRect.setLayoutManager(innerRectLayout);

		GridData innerRectGridData = new GridData();
		innerRectGridData.verticalAlignment = GridData.FILL;
		innerRectGridData.horizontalAlignment = GridData.FILL;
		innerRectGridData.horizontalIndent = 5;
		innerRectGridData.horizontalSpan = 5;
		innerRectGridData.grabExcessHorizontalSpace = true;
		innerRectGridData.grabExcessVerticalSpace = true;
		leftRectangle.add(innerRect, innerRectGridData);

		return innerRect;
	}

	public String getIconPath() {
		return "icons/ico20/aggregate-mediator.gif";
	}

	public String getNodeName() {
		return "Aggregate";
	}

	public Color getLabelBackColor() {
		return this.getBackgroundColor();
	}

}
