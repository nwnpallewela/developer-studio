package org.wso2.developerstudio.datamapper.diagram.edit.parts;

import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.AbstractBorderedShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IBorderItemEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.BorderItemSelectionEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CreationEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.figures.BorderItemLocator;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.edit.policies.reparent.CreationEditPolicyWithCustomReparent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.wso2.developerstudio.datamapper.Element;
import org.wso2.developerstudio.datamapper.diagram.edit.parts.custom.CustomNonResizableEditPolicyEx;
import org.wso2.developerstudio.datamapper.diagram.edit.parts.custom.FixedBorderItemLocator;
import org.wso2.developerstudio.datamapper.diagram.part.DataMapperVisualIDRegistry;

/**
 * @generated
 */
public class ElementEditPart extends AbstractBorderedShapeEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 3007;

	/**
	 * @generated
	 */
	protected IFigure contentPane;

	/**
	 * @generated
	 */
	protected IFigure primaryShape;

	/**
	 * @generated
	 */
	public ElementEditPart(View view) {
		super(view);
	}

	protected void addChild(EditPart child, int index) {
		super.addChild(child, index);
	}

	/**
	 * @generated NOT
	 */
	protected void createDefaultEditPolicies() {
		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CreationEditPolicy());
		super.createDefaultEditPolicies();
		installEditPolicy(
				EditPolicyRoles.SEMANTIC_ROLE,
				new org.wso2.developerstudio.datamapper.diagram.edit.policies.ElementItemSemanticEditPolicy());
		installEditPolicy(
				EditPolicyRoles.CANONICAL_ROLE,
				new org.wso2.developerstudio.datamapper.diagram.edit.policies.ElementCanonicalEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
		removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);

		/* Disable dragging and resizing */
		NonResizableEditPolicy selectionPolicy = new NonResizableEditPolicy();
		selectionPolicy.setDragAllowed(false);
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, selectionPolicy);

		/* remove balloon */
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new CustomNonResizableEditPolicyEx());
		removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.POPUPBAR_ROLE);
	}
	
	/**
	 * @generated NOT
	 */
	@Override
	public boolean canAttachNote() {
		return false;
	}
	
	/**
	 * @generated NOT
	 */
	protected LayoutEditPolicy createLayoutEditPolicy() {
		org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy lep = new org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy() {

			protected EditPolicy createChildEditPolicy(EditPart child) {
				View childView = (View) child.getModel();
				switch (org.wso2.developerstudio.datamapper.diagram.part.DataMapperVisualIDRegistry
						.getVisualID(childView)) {
				case org.wso2.developerstudio.datamapper.diagram.edit.parts.InNode2EditPart.VISUAL_ID:
				case org.wso2.developerstudio.datamapper.diagram.edit.parts.OutNode2EditPart.VISUAL_ID:
					return new BorderItemSelectionEditPolicy();
				}
				EditPolicy result = child.getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE);
				if (result == null) {
					result = new NonResizableEditPolicy();
				}
				return result;
			}

			protected Command getMoveChildrenCommand(Request request) {
				return null;
			}

			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
		};
		return lep;
	}

	/**
	 * @generated NOT
	 */
	protected IFigure createNodeShape() {
		return primaryShape = new ElementFigure();
	}

	/**
	 * @generated
	 */
	public ElementFigure getPrimaryShape() {
		return (ElementFigure) primaryShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart#isSelectable
	 * ()
	 */
	@Override
	public boolean isSelectable() {
		return true;
	}

	/*
	 * add In/Out Nodes if Element is Input , dont add inNodes if Element is
	 * Output, dont add outNodes
	 */
	protected boolean addFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof org.wso2.developerstudio.datamapper.diagram.edit.parts.ElementNameEditPart) {
			((org.wso2.developerstudio.datamapper.diagram.edit.parts.ElementNameEditPart) childEditPart)
					.setLabel(getPrimaryShape().getFigureElementNameFigure());
			return true;
		}

		if (childEditPart instanceof InNode2EditPart || childEditPart instanceof InNodeEditPart) {

			EditPart temp = this.getParent();
			while ((!(temp instanceof DataMapperRootEditPart)) && (temp != null)) {

				if (temp instanceof InputEditPart) {

					if (childEditPart instanceof InNodeEditPart) {
						NodeFigure figureInput = ((InNodeEditPart) childEditPart)
								.getNodeFigureOutput();
						figureInput.removeAll();
						Figure emptyFigure = new Figure();
						figureInput.add(emptyFigure);
						break;
					} else {
						NodeFigure figureInput = (NodeFigure) ((InNode2EditPart) childEditPart)
								.getFigure();
						figureInput.removeAll();
						Figure emptyFigure = new Figure();
						figureInput.add(emptyFigure);
						break;
					}
				}

				temp = temp.getParent();

			}

			// Innodes for Output elements

			if (childEditPart instanceof InNode2EditPart) {
				IFigure borderItemFigure = ((InNode2EditPart) childEditPart).getFigure();
				BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(),
						borderItemFigure, PositionConstants.WEST, 0.5);
				getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
				return true;
			}

			else {
				IFigure borderItemFigure = ((InNodeEditPart) childEditPart).getFigure();
				BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(),
						borderItemFigure, PositionConstants.WEST, 0.5);
				getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
				return true;
			}
		}

		else if (childEditPart instanceof OutNode2EditPart
				|| childEditPart instanceof OutNodeEditPart) {

			EditPart temp = this.getParent();
			while ((!(temp instanceof DataMapperRootEditPart)) && (temp != null)) {

				if (temp instanceof OutputEditPart) {
					if (childEditPart instanceof OutNodeEditPart) {
						NodeFigure figureInput = ((OutNodeEditPart) childEditPart)
								.getNodeFigureOutput();
						figureInput.removeAll();
						Figure emptyFigure = new Figure();
						figureInput.add(emptyFigure);
						break;
					}

					else {
						NodeFigure figureInput = (NodeFigure) ((OutNode2EditPart) childEditPart)
								.getFigure();
						figureInput.removeAll();
						Figure emptyFigure = new Figure();
						figureInput.add(emptyFigure);
						break;
					}
				}

				temp = temp.getParent();

			}

			if (childEditPart instanceof OutNodeEditPart) {
				IFigure borderItemFigure = ((OutNodeEditPart) childEditPart).getFigure();
				BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(),
						borderItemFigure, PositionConstants.EAST, 0.5);
				getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
				return true;
			}

			else {

				IFigure borderItemFigure = ((OutNode2EditPart) childEditPart).getFigure();
				BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(),
						borderItemFigure, PositionConstants.EAST, 0.5);
				getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
				return true;
			}
		}

		return false;
	}

	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof ElementNameEditPart) {
			return true;
		}
		if (childEditPart instanceof InNode2EditPart) {
			getBorderedFigure().getBorderItemContainer().remove(
					((InNode2EditPart) childEditPart).getFigure());
			return true;
		}
		if (childEditPart instanceof OutNode2EditPart) {
			getBorderedFigure().getBorderItemContainer().remove(
					((OutNode2EditPart) childEditPart).getFigure());
			return true;
		}
		return false;
	}

	protected void addChildVisual(EditPart childEditPart, int index) {
		if (addFixedChild(childEditPart)) {
			return;
		}
		super.addChildVisual(childEditPart, -1);
	}

	/**
	 * @generated
	 */
	protected void removeChildVisual(EditPart childEditPart) {
		if (removeFixedChild(childEditPart)) {
			return;
		}
		super.removeChildVisual(childEditPart);
	}

	/**
	 * @generated
	 */
	protected NodeFigure createNodePlate() {
		DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(40, 40);
		return result;
	}

	/**
	 * @generated
	 */
	public EditPolicy getPrimaryDragEditPolicy() {
		EditPolicy result = super.getPrimaryDragEditPolicy();
		if (result instanceof ResizableEditPolicy) {
			ResizableEditPolicy ep = (ResizableEditPolicy) result;
			ep.setResizeDirections(PositionConstants.NONE);
		}
		return result;
	}

	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model so
	 * you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @generated
	 */
	protected NodeFigure createMainFigure() {
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new StackLayout());
		IFigure shape = createNodeShape();
		figure.add(shape);
		contentPane = setupContentPane(shape);
		return figure;
	}

	protected IFigure getContentPaneFor(IGraphicalEditPart editPart) {
		if (editPart instanceof IBorderItemEditPart) {
			return getBorderedFigure().getBorderItemContainer();
		}
		return getContentPane();
	}

	/**
	 * Default implementation treats passed figure as content pane. Respects
	 * layout one may have set for generated figure.
	 * 
	 * @param nodeShape
	 *            instance of generated figure class
	 * @generated
	 */
	protected IFigure setupContentPane(IFigure nodeShape) {
		if (nodeShape.getLayoutManager() == null) {
			ConstrainedToolbarLayout layout = new ConstrainedToolbarLayout();
			layout.setSpacing(5);
			nodeShape.setLayoutManager(layout);
		}
		return nodeShape; // use nodeShape itself as contentPane
	}

	/**
	 * @generated
	 */
	public IFigure getContentPane() {
		if (contentPane != null) {
			return contentPane;
		}
		return super.getContentPane();
	}

	/**
	 * @generated
	 */
	protected void setForegroundColor(Color color) {
		if (primaryShape != null) {
			primaryShape.setForegroundColor(color);
		}
	}

	/**
	 * @generated
	 */
	protected void setBackgroundColor(Color color) {
		if (primaryShape != null) {
			primaryShape.setBackgroundColor(color);
		}
	}

	/**
	 * @generated
	 */
	protected void setLineWidth(int width) {
		if (primaryShape instanceof Shape) {
			((Shape) primaryShape).setLineWidth(width);
		}
	}

	/**
	 * @generated
	 */
	protected void setLineType(int style) {
		if (primaryShape instanceof Shape) {
			((Shape) primaryShape).setLineStyle(style);
		}
	}

	/**
	 * @generated
	 */
	public EditPart getPrimaryChildEditPart() {
		return getChildBySemanticHint(DataMapperVisualIDRegistry
				.getType(ElementNameEditPart.VISUAL_ID));
	}

	/**
	 * @generated NOT
	 */
	public class ElementFigure extends RectangleFigure {

		private static final String ICONS_ELEMENT = "icons/gmf/symbol_element_of.gif";
		private static final String ORG_WSO2_DEVELOPERSTUDIO_VISUALDATAMAPPER_DIAGRAM = "org.wso2.developerstudio.visualdatamapper.diagram";
		/**
		 * @generated
		 */
		private WrappingLabel fFigureElementNameFigure;

		/**
		 * @generated NOT
		 */
		public ElementFigure() {

			ToolbarLayout layoutThis = new ToolbarLayout();
			layoutThis.setStretchMinorAxis(false);
			layoutThis.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
			//layoutThis.setSpacing(5);
			layoutThis.setVertical(false);

			this.setLayoutManager(layoutThis);
			this.setOutline(false);
			this.setOpaque(false);
			this.setFill(false);
			this.setBorder(null);

			createContents();
		}

		/**
		 * @generated NOT
		 */
		private void createContents() {

			RectangleFigure figure = new RectangleFigure();
			figure.setLayoutManager(new BorderLayout());

			figure.setBorder(null);
			figure.setOpaque(false);
			figure.setFill(false);

			ImageDescriptor mainImgDesc = AbstractUIPlugin.imageDescriptorFromPlugin(
					ORG_WSO2_DEVELOPERSTUDIO_VISUALDATAMAPPER_DIAGRAM,
					ICONS_ELEMENT);

			ImageFigure mainImg = new ImageFigure(mainImgDesc.createImage()); //elemet symbole figure 
			mainImg.setSize(new Dimension(20, 8));

			fFigureElementNameFigure = new WrappingLabel(); // element nme holding rectangle
			String name = (((Element) ((View) getModel()).getElement()).getName());
			int tabCount = ((Element) ((View) getModel()).getElement()).getLevel();
			figure.setPreferredSize((tabCount - 1) * 30, 3);
			figure.setMaximumSize(new Dimension(100,3));
			figure.setMinimumSize(new Dimension(0,3));


			Label elemLabel = new Label();
			elemLabel.setIcon(mainImg.getImage());
			Display display = Display.getCurrent();
			Color black = display.getSystemColor(SWT.COLOR_BLACK);
			elemLabel.setForegroundColor(black);
			elemLabel.setText(name);
			elemLabel.setSize(new Dimension(100,3));
			figure.setOutline(false);
			figure.setFill(false);
			this.setOutline(false);
			this.setFill(false);
			this.add(figure);
			this.setMaximumSize(new Dimension(100, 3));
			this.setMinimumSize(new Dimension(100, 3));
			this.add(elemLabel);

		}

		/**
		 * @generated
		 */
		public WrappingLabel getFigureElementNameFigure() {
			return fFigureElementNameFigure;
		}
		
		public void renameElement(String newName) {
			ImageDescriptor mainImgDesc = AbstractUIPlugin.imageDescriptorFromPlugin(
					ORG_WSO2_DEVELOPERSTUDIO_VISUALDATAMAPPER_DIAGRAM,
					ICONS_ELEMENT);

			ImageFigure mainImg = new ImageFigure(mainImgDesc.createImage()); //elemet symbole figure 
			mainImg.setSize(new Dimension(20, 8));
			
			Label elemLabel = new Label();
			elemLabel.setIcon(mainImg.getImage());
			Display display = Display.getCurrent();
			Color black = display.getSystemColor(SWT.COLOR_BLACK);
			elemLabel.setForegroundColor(black);
			elemLabel.setText(newName);
			elemLabel.setSize(new Dimension(100,3));
			List<Figure> childrenList = this.getChildren();
			this.remove(childrenList.get(1));
			
			this.add(elemLabel);
		}

	}
	
	public void renameElementItem(String newName) { 
		getPrimaryShape().renameElement(newName);
	}

}
