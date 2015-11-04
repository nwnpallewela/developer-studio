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

public class ESBDebuggerConstants {

	public static final String ESB_DEBUGGER_EVENT_BROKER_DATA_NAME ="org.eclipse.e4.data";
	public static final String ESBDEBUGTARGET_EVENT_TOPIC = "ESBDebugTarget_Events";
	public static final String ESBDEBUGGER_EVENT_TOPIC = "ESBDebugger_Events";
	// ESB Debugger launch constants
	public static final String LAUNCH_CONFIGURATION_TYPE_ID = "org.wso2.developerstudio.eclipse.esb.debugger.launch";
	public static final String ESB_SERVER_LOCATION = "ESB server location";
	public static final String COMMAND_PORT_UI_TAG = "Command Port";
	public static final String EVENT_PORTUI_TAG = "Event Port";
	public static final String DEFAULT_COMMAND_PORT = "6006";
	public static final String DEFAULT_EVENT_PORT = "6007";
	public static final String ESB_Debugger_LAUNCH_CONFIGURAION_MAIN_TAB_MESSAGE = "Please select command and event ports.";
	public static final String ESB_Debugger_LAUNCH_CONFIGURAION_MAIN_TAB_TITLE = "Global";

	public static final String ESB_MEDIATION_DEBUGGER_NAME = "ESB Mediation Debugger";
	public static final String ERROR_MESSAGE_TAG = "Error";

	public static final String ESB_DEBUGGER_EVENT_DISPATCH_JOB = "ESB Mediation Debugger event dispatcher";
	public static final String MEDIATOR_DELETE_ACTION = "mediatordelete";
	public static final String MEDIATOR_INSERT_ACTION = "mediatorinsert";

	public static final String AXIS2_PROPERTY_TAG = "axis2";
	public static final String OPERATION_PROPERTY_TAG = "operation";
	public static final String AXIS2_CLIENT_PROPERTY_TAG = "axis2-client";
	public static final String TRANSPORT_PROPERTY_TAG = "transport";
	public static final String SYANPSE_PROPERTY_TAG = "synapse";

	public static final String AXIS2_PROPERTY_UI_NAME = "Axis2 Scope Properties";
	public static final String OPERATION_PROPERTY_UI_NAME = "Operation Scope Properties";
	public static final String AXIS2_CLIENT_PROPERTY_UI_NAME = "Axis2-Client Scope Properties";
	public static final String TRANSPORT_PROPERTY_UI_NAME = "Transport Scope Properties";
	public static final String SYANPSE_PROPERTY_UI_NAME = "Synapse Scope Properties";

	public static final String COMMAND_GET = "get";

	public static final String VARIABLE_TYPE = "ESB Message";
	public static final String PROPERTIES = "properties";

	public static final String TERMINATED_EVENT = "terminated";
	public static final String DEBUG_INFO_LOST_EVENT = "synapse configuration updated-debug information lost";

	public static final String AXIS2_PROPERTIES = "axis2-properties";
	public static final String SYNAPSE_PROPERTIES = "synapse-properties";
	public static final String OPERATION_PROPERTIES = "axis2Operation-properties";
	public static final String AXIS2_CLIENT_PROPERTIES = "axis2Client-properties";
	public static final String TRANSPORT_PROPERTIES = "axis2Transport-properties";

	public static final String ESBBREAKPOINT_COMMAND_LABEL = "Toggle Breakpoint";
	public static final String ESBBREAKPOINT_REPOPULATE_COMMAND_LABEL = "Resend ESB Breakpoints";
	public static final String ESBBREAKPOINT_COMMAND_TOOL_TIP = "Set breakpoint for this mediator";
	public static final String ESBBREAKPOINT_REPOPULATE_COMMAND_TOOL_TIP = "Resend all ESB Breakpoints to connected ESB Server";
	public static final String ESBBREAKPOINT_DELETE_ALL_COMMAND_TOOL_TIP = "Delete All ESB Breakpoints from workspace";
	public static final String ESBBREAKPOINT_DELETE_ALL_COMMAND_LABEL = "Delete All ESB Breakpoints";
	public static final String ESBBREAKPOINT_ACTION_ID = "org.wos2.developerstudio.eclipse.esb.debugger.breakpoint.action";
	public static final String ESBBREAKPOINT_DELETE_ALL_ACTION_ID = "org.wos2.developerstudio.eclipse.esb.debugger.breakpoint.delete.all.action";
	public static final String ESBBREAKPOINT_REPOPULATE_ACTION_ID = "org.wos2.developerstudio.eclipse.esb.debugger.breakpoint.repopulate.action";
	public static final String EMPTY_SELECTION = "Empty selection.";
	public static final String ESB_BREAKPOINT_MARKER = "org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.ESBBreakpointMarker";
	public static final String TEMPLATE_SEQUENCE = "template_sequence";

	public static final String MAIN = "main";
	public static final String MAIN_SEQUENCE = "main_sequence";
	public static final String ESB_LINE_BREAKPOINT_MARKER = "org.wso2.developerstudio.eclipse.esb.debugger.ESBLineBreakpointMarker";
	public static final String COMMAND_RESPONSE = "command-response";
	public static final String EVENT = "event";
	public static final String RESUME = "resume";
	public static final String SUCCESSFUL = "successful";
	public static final String FAILED = "failed";
	public static final String FAILED_REASON = "failed-reason";
	public static final String RESOURCE = "resource";
	public static final String COMMAND = "command";
	public static final String COMMAND_ARGUMENT = "command-argument";
	public static final String MEDIATION_COMPONENT = "mediation-component";
	public static final String TEMPLATE = "template";
	public static final String CONNECTOR = "connector";
	public static final String SEQUENCE = "sequence";
	public static final String API = "api";
	public static final String PROXY = "proxy";
	public static final String MEDIATOR_POSITION = "mediator-position";
	public static final String METHOD_NAME = "method-name";
	public static final String CONNECTOR_KEY = "connector-key";
	public static final String TEMPLATE_KEY = "template-key";
	public static final String SEQUENCE_KEY = "sequence-key";
	public static final String SEQUENCE_TYPE = "sequence-type";
	public static final String BREAKPOINT = "breakpoint";
	public static final String SET = "set";
	public static final String CLEAR = "clear";
	public static final String SKIP = "skip";
	public static final String NAMED = "named";
	public static final String PROXY_INSEQ = "proxy_inseq";
	public static final String PROXY_OUTSEQ = "proxy_outseq";
	public static final String PROXY_KEY = "proxy-key";
	public static final String API_KEY = "api-key";
	public static final String METHOD = "method";
	public static final String CONTEXT = "context";
	public static final String PROPERTY = "property";
	public static final String GET_PROPERTY = "get-property";
	public static final String CHANGE_PROPERTY = "change-property";
	public static final String PROPERTY_NAME = "property-name";
	public static final String PROPERTY_VALUE = "property-value";
	public static final String URL_TEMPLATE = "uri-template";
	public static final String URI_MAPPING = "url-mapping";
	public static final String API_INSEQ = "api_inseq";
	public static final String API_OUTSEQ = "api_outseq";
	public static final String MAPPING_URL_TYPE = "mapping";

	public static final String API_METHOD_POST = "POST";
	public static final String API_METHOD_GET = "GET";
	public static final String API_METHOD_PUT = "PUT";
	public static final String API_METHOD_DELETE = "DELETE";
	public static final String API_METHOD_OPTIONS = "OPTIONS";
	public static final String API_METHOD_HEAD = "HEAD";
	public static final String API_METHOD_PATCH = "PATCH";

}
