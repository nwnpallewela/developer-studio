/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer;

import static org.wso2.developerstudio.eclipse.gmf.esb.EsbPackage.Literals.*;

import java.util.Map;

import org.apache.synapse.inbound.InboundEndpoint;
import org.apache.synapse.mediators.Value;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.Enable;
import org.wso2.developerstudio.eclipse.gmf.esb.InboundEndpointType;
import org.wso2.developerstudio.eclipse.gmf.esb.JMSCacheLevel;
import org.wso2.developerstudio.eclipse.gmf.esb.JMSConnectionFactoryType;
import org.wso2.developerstudio.eclipse.gmf.esb.JMSSessionAcknowledgement;
import org.wso2.developerstudio.eclipse.gmf.esb.VFSAction;
import org.wso2.developerstudio.eclipse.gmf.esb.VFSFileSort;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.providers.EsbElementTypes;
import org.wso2.developerstudio.eclipse.gmf.esb.persistence.InboundEndpointConstants;

public class InboundEndpointDeserializer extends
		AbstractEsbNodeDeserializer<InboundEndpoint, org.wso2.developerstudio.eclipse.gmf.esb.InboundEndpoint> {

	private static final String HTTP = "HTTP";
	private static final String FILE = "File";
	private static final String JMS = "JMS";
	private static final String CUSTOM = "Custom";
	private static final String HTTPS = "HTTPS";
	private static final String TRUE = "true";
	private static final String NONE = "none";
	private static final String ENABLE = "enable";
	private static final String NAME = "name";
	private static final String SIZE = "size";
	private static final String TOPIC = "topic";
	private static final String AUTO_ACKNOWLEDGE = "AUTO_ACKNOWLEDGE";
	private static final String CLIENT_ACKNOWLEDGE = "CLIENT_ACKNOWLEDGE";
	private static final String DUPS_OK_ACKNOWLEDGE = "DUPS_OK_ACKNOWLEDGE";

	@Override
	public org.wso2.developerstudio.eclipse.gmf.esb.InboundEndpoint createNode(IGraphicalEditPart part,
			InboundEndpoint object) {
		/*
		 * Creating a new graphical InboundEndpoint object.
		 */
		org.wso2.developerstudio.eclipse.gmf.esb.InboundEndpoint inboundEndpoint = (org.wso2.developerstudio.eclipse.gmf.esb.InboundEndpoint) DeserializerUtils
				.createNode(part, EsbElementTypes.InboundEndpoint_3767);

		setElementToEdit(inboundEndpoint);
		refreshEditPartMap();

		executeSetValueCommand(INBOUND_ENDPOINT__NAME, object.getName());

		if (HTTP.equals(object.getProtocol())) {
			executeSetValueCommand(INBOUND_ENDPOINT__TYPE, InboundEndpointType.HTTP);
			updateParameters(object);
		} else if (FILE.equals(object.getProtocol())) {
			executeSetValueCommand(INBOUND_ENDPOINT__TYPE, InboundEndpointType.FILE);
			updateParameters(object);
		} else if (JMS.equals(object.getProtocol())) {
			executeSetValueCommand(INBOUND_ENDPOINT__TYPE, InboundEndpointType.JMS);
			updateParameters(object);
		} else if (CUSTOM.equals(object.getProtocol())) {
			executeSetValueCommand(INBOUND_ENDPOINT__TYPE, InboundEndpointType.CUSTOM);
			updateParameters(object);
		} else if (HTTPS.equals(object.getProtocol())) {
			executeSetValueCommand(INBOUND_ENDPOINT__TYPE, InboundEndpointType.HTTPS);
			updateParameters(object);
		}

		/*
		 * FIXME executeSetValueCommand(INBOUND_ENDPOINT__CLASS,
		 * object.getClassImpl());
		 */
		if (object.getClassImpl() == null) {
			executeSetValueCommand(INBOUND_ENDPOINT__CLASS, "org.wso2.MyClass");
		} else {
			executeSetValueCommand(INBOUND_ENDPOINT__CLASS, object.getClassImpl());
		}

		/*
		 * Creating Sequence mediator graphically
		 */
		if (object.getInjectingSeq() != null && !"".equals(object.getInjectingSeq())) {
			addRootInputConnector(inboundEndpoint.getSequenceInputConnector());
			IGraphicalEditPart sequenceCompartment = (IGraphicalEditPart) getEditpart(
					inboundEndpoint.getContainer().getSequenceContainer().getMediatorFlow()).getChildren().get(0);
			setRootCompartment((GraphicalEditPart) sequenceCompartment);
			SequenceMediator sequenceContainer = new SequenceMediator();
			SequenceMediator sequenceMediator = new SequenceMediator();
			Value sequenceKey = new Value(object.getInjectingSeq());
			sequenceMediator.setKey(sequenceKey);
			sequenceContainer.addChild(sequenceMediator);
			deserializeSequence(sequenceCompartment, sequenceContainer, inboundEndpoint.getSequenceOutputConnector());
		}

		/*
		 * Creating OnErrorSequence mediator graphically
		 */
		if (object.getOnErrorSeq() != null && !"".equals(object.getOnErrorSeq())) {
			addRootInputConnector(inboundEndpoint.getOnErrorSequenceInputConnector());
			IGraphicalEditPart onErrorSequenceCompartment = (IGraphicalEditPart) getEditpart(
					inboundEndpoint.getContainer().getOnErrorSequenceContainer().getMediatorFlow()).getChildren()
					.get(0);
			setRootCompartment((GraphicalEditPart) onErrorSequenceCompartment);
			SequenceMediator onErrorSequenceContainer = new SequenceMediator();
			SequenceMediator onErrorSequenceMediator = new SequenceMediator();
			Value onErrorSequenceKey = new Value(object.getOnErrorSeq());
			onErrorSequenceMediator.setKey(onErrorSequenceKey);
			onErrorSequenceContainer.addChild(onErrorSequenceMediator);
			deserializeSequence(onErrorSequenceCompartment, onErrorSequenceContainer,
					inboundEndpoint.getOnErrorSequenceOutputConnector());
		}

		return inboundEndpoint;
	}

	private void updateParameters(InboundEndpoint object) {
		for (Map.Entry<String, String> paramEntry : object.getParametersMap().entrySet()) {
			if (paramEntry.getKey().equals(InboundEndpointConstants.INBOUND_HTTP_PORT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__INBOUND_HTTP_PORT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.API_DISPATCHING_ENABLED)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__API_DISPATCHING_ENABLED, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__API_DISPATCHING_ENABLED, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.INTERVAL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__INTERVAL, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.SEQUENTIAL)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__SEQUENTIAL, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__SEQUENTIAL, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.COORDINATION)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__COORDINATION, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__COORDINATION, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILE_URI)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_URI, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_CONTENT_TYPE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_CONTENT_TYPE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILE_NAME_PATTERN)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_NAME_PATTERN, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILE_PROCESS_INTERVAL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_PROCESS_INTERVAL, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILE_PROCESS_COUNT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_PROCESS_COUNT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILE_PROCESS_COUNT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_PROCESS_COUNT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_LOCKING)) {
				if (paramEntry.getValue().equals(ENABLE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_LOCKING, Enable.ENABLE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_LOCKING, Enable.DISABLE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_MAX_RETRY_COUNT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_MAX_RETRY_COUNT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_RECONNECT_TIMEOUT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_RECONNECT_TIMEOUT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_ACTION_AFTER_PROCESS)) {
				if (paramEntry.getValue().equals(NONE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_PROCESS, VFSAction.NONE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_PROCESS, VFSAction.MOVE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_MOVE_AFTER_PROCESS)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_MOVE_AFTER_PROCESS, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_ACTION_AFTER_ERRORS)) {
				if (paramEntry.getValue().equals(NONE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_ERRORS, VFSAction.NONE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_ERRORS, VFSAction.MOVE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_MOVE_AFTER_ERRORS)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_MOVE_AFTER_ERRORS, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_ACTION_AFTER_FAILURE)) {
				if (paramEntry.getValue().equals(NONE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_FAILURE, VFSAction.NONE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_ACTION_AFTER_FAILURE, VFSAction.MOVE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_MOVE_AFTER_FAILURE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_MOVE_AFTER_FAILURE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_MOVE_TIMESTAMP_FORMAT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_MOVE_TIMESTAMP_FORMAT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_AUTO_LOCK_RELEASE)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_AUTO_LOCK_RELEASE, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_AUTO_LOCK_RELEASE, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_AUTO_LOCK_RELEASE_INTERVAL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_AUTO_LOCK_RELEASE_INTERVAL,
						paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_LOCK_RELEASE_SAME_NODE)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_LOCK_RELEASE_SAME_NODE, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_LOCK_RELEASE_SAME_NODE, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_DISTRIBUTED_LOCK)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_DISTRIBUTED_LOCK, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_DISTRIBUTED_LOCK, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_DISTRIBUTED_TIMEOUT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_DISTRIBUTED_TIMEOUT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILESORT_ATTRIBUTE)) {
				if (paramEntry.getValue().equals(NONE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ATTRIBUTE, VFSFileSort.NONE);
				} else if (paramEntry.getValue().equals(NAME)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ATTRIBUTE, VFSFileSort.NAME);
				} else if (paramEntry.getValue().equals(SIZE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ATTRIBUTE, VFSFileSort.SIZE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ATTRIBUTE,
							VFSFileSort.LASTMODIFIEDTIMESTAMP);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_FILESORT_ASCENDING)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ASCENDING, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_FILE_SORT_ASCENDING, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_SUBFOLDER_TIMESTAMP_FORMAT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_SUB_FOLDER_TIMESTAMP_FORMAT,
						paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.VFS_CREATE_FOLDER)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_CREATE_FOLDER, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_VFS_CREATE_FOLDER, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_JAVA_NAMING_FACTORY_INITIAL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__JAVA_NAMING_FACTORY_INITIAL, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_JAVA_NAMING_PROVIDER_URL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__JAVA_NAMING_PROVIDER_URL, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_CONNECTION_FACTORY_JNDI_NAME)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CONNECTION_FACTORY_JNDI_NAME,
						paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_CONNECTION_FACTORY_TYPE)) {
				if (paramEntry.getValue().equals(TOPIC)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CONNECTION_FACTORY_TYPE,
							JMSConnectionFactoryType.TOPIC);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CONNECTION_FACTORY_TYPE,
							JMSConnectionFactoryType.QUEUE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_DESTINATION)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_DESTINATION, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_SESSION_TRANSACTED)) {
				if (paramEntry.getValue().equals(TRUE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_TRANSACTED, true);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_TRANSACTED, false);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_SESSION_ACKNOWLEDGEMENT)) {
				if (paramEntry.getValue().equals(AUTO_ACKNOWLEDGE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_ACKNOWLEDGEMENT,
							JMSSessionAcknowledgement.AUTO_ACKNOWLEDGE);
				} else if (paramEntry.getValue().equals(CLIENT_ACKNOWLEDGE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_ACKNOWLEDGEMENT,
							JMSSessionAcknowledgement.CLIENT_ACKNOWLEDGE);
				} else if (paramEntry.getValue().equals(DUPS_OK_ACKNOWLEDGE)) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_ACKNOWLEDGEMENT,
							JMSSessionAcknowledgement.DUPS_OK_ACKNOWLEDGE);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SESSION_ACKNOWLEDGEMENT,
							JMSSessionAcknowledgement.SESSION_TRANSACTED);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_CACHE_LEVEL)) {
				if (paramEntry.getValue().equals("1")) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CACHE_LEVEL, JMSCacheLevel.ONE);
				} else if (paramEntry.getValue().equals("2")) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CACHE_LEVEL, JMSCacheLevel.TWO);
				} else if (paramEntry.getValue().equals("3")) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CACHE_LEVEL, JMSCacheLevel.THREE);
				} else if (paramEntry.getValue().equals("4")) {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CACHE_LEVEL, JMSCacheLevel.FOUR);
				} else {
					executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CACHE_LEVEL, JMSCacheLevel.FIVE);
				}
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_USERNAME)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_USER_NAME, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_PASSWORD)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_PASSWORD, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_JMS_SPEC_VERSION)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMSJMS_SPEC_VERSION, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_SUBSCRIPTION_DURABLE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_SUBSCRIPTION_DURABLE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_DURABLE_SUBSCRIBER_CLIENT_ID)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_DURABLE_SUBSCRIBER_CLIENT_ID,
						paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_MESSAGE_SELECTOR)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_MESSAGE_SELECTOR, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_RECIEVE_TIMEOUT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_RECEIVE_TIMEOUT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.JMS_CONTENT_TYPE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRANSPORT_JMS_CONTENT_TYPE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.KEYSTORE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__KEYSTORE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.TRUSTSTORE)) {
				executeSetValueCommand(INBOUND_ENDPOINT__TRUSTSTORE, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.SSL_VERIFY_CLIENT)) {
				executeSetValueCommand(INBOUND_ENDPOINT__SSL_VERIFY_CLIENT, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.SSL_PROTOCOL)) {
				executeSetValueCommand(INBOUND_ENDPOINT__SSL_PROTOCOL, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.HTTPS_PROTOCOLS)) {
				executeSetValueCommand(INBOUND_ENDPOINT__HTTPS_PROTOCOLS, paramEntry.getValue());
			} else if (paramEntry.getKey().equals(InboundEndpointConstants.CERTIFICATE_REVOCATION_VERIFIER)) {
				executeSetValueCommand(INBOUND_ENDPOINT__CERTIFICATE_REVOCATION_VERIFIER, paramEntry.getValue());
			}
		}

	}
}
