package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom;

import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.gmf.runtime.notation.View;

public class MultipleCompartmentComplexFiguredAbstractMediator extends complexFiguredAbstractMediator{

	public MultipleCompartmentComplexFiguredAbstractMediator(View view) {
		super(view);
	}
	
	@Override
	public RoundedRectangle getPrimaryShape() { 
		//This method must be implemented in subclasses
		return null;
	}

}
