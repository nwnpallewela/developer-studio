package org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.messages"; //$NON-NLS-1$
	public static String LogMediatorEditPart_NODE_NAME;
	public static String LogMediatorEditPart_TOOL_TIP_MESSAGE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
