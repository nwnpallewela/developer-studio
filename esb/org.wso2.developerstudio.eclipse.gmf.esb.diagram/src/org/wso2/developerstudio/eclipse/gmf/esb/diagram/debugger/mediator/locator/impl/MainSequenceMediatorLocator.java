package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.impl;

import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.Mediator;
import org.wso2.developerstudio.eclipse.gmf.esb.OutputConnector;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer.AbstractEsbNodeDeserializer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl.AbstractESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator.AbstractMediatorLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.MediatorImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;

public class MainSequenceMediatorLocator extends AbstractMediatorLocator  {

	private static final String MEDIATOR_POSITION_SEPERATOR = " ";
	private static final Object IN_SEQUENCE_VALUE = "0";

	@Override
	public EditPart getMediatorEditPart(EsbServer esbServer,
			SuspendedEvent event) {
		Map<String, String> info = event.getDetail();
		if(info.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)){
			String position = info.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			String[] positionArray = position.split(MEDIATOR_POSITION_SEPERATOR);
			
			TreeIterator<EObject> treeIterator = esbServer.eAllContents();
			EObject next = treeIterator.next();

			ProxyServiceImpl mainSequence = (ProxyServiceImpl) next;
			
			if(positionArray.length==2 && IN_SEQUENCE_VALUE.equals(positionArray[0].trim())){
				
				return getMediator(mainSequence.getOutputConnector(), Integer.parseInt(positionArray[1]));
				
			}else if(positionArray.length==2){
				
				return getMediator(mainSequence.getOutSequenceOutputConnector(), Integer.parseInt(positionArray[1]));
			}
			
			
		}
		
		return null;
	}

	private EditPart getMediator(OutputConnector tempConnector, int mediatorPosition) {
		int count = 0;
		while (tempConnector != null) {
			EObject mediator = tempConnector.getOutgoingLink().getTarget()
					.eContainer();
			if (count==mediatorPosition){
				AbstractEsbNodeDeserializer.refreshEditPartMap();
				EditPart editpart = AbstractEsbNodeDeserializer.getEditpart(mediator);
				return editpart;
			} else {
				count++;
				tempConnector = AbstractESBBreakpointBuilder.getOutputConnector((Mediator) mediator);
			}
		}
		return null;
	}


}
