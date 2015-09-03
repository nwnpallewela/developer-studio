package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.mediator.locator;

import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbServer;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.events.SuspendedEvent;

public interface IMediatorLocator {

	EditPart getMediatorEditPart(EsbServer esbServer,SuspendedEvent event);


}
