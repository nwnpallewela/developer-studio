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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.APIResource;
import org.wso2.developerstudio.eclipse.gmf.esb.AggregateMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.BAMMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.BeanMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.BuilderMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CacheMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CallMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CallTemplateMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CalloutMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ClassMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CloneMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.CommandMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ConditionalRouterMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.DBLookupMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.DBReportMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.DropMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.EJBMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.EnqueueMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.EnrichMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.EntitlementMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbElement;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbLink;
import org.wso2.developerstudio.eclipse.gmf.esb.EventMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.FastXSLTMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.FaultMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.FilterMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ForEachMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.HeaderMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.InputConnector;
import org.wso2.developerstudio.eclipse.gmf.esb.IterateMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.LogMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.LoopBackMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.Mediator;
import org.wso2.developerstudio.eclipse.gmf.esb.OAuthMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.OutputConnector;
import org.wso2.developerstudio.eclipse.gmf.esb.PayloadFactoryMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.PropertyMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.PublishEventMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.RMSequenceMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.RespondMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.RuleMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ScriptMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.SendMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.Sequence;
import org.wso2.developerstudio.eclipse.gmf.esb.SmooksMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.SpringMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.StoreMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.SwitchMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ThrottleMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.TransactionMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.URLRewriteMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.ValidateMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.XQueryMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.XSLTMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.AbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EditorUtils;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.builder.IESBBreakpointBuilder;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.breakpoint.impl.ESBBreakpoint;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.BreakpointMarkerNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.ESBDebuggerException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception.MediatorNotFoundException;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.model.ESBDebugModelPresentation;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebugerUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils.ESBDebuggerConstants;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.APIResourceEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.EsbServerEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.ProxyServiceEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.Sequences2EditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.SequencesEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.TemplateEditPart;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.APIResourceImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.ProxyServiceImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.SequencesImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.TemplateImpl;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * All ESBBreakpoint builders should extend AbstractESBBreakpointBuilder class.
 * This class contains common methods related to ESBBreakpointBuilders
 *
 */
public abstract class AbstractESBBreakpointBuilder implements
		IESBBreakpointBuilder {

	protected static final String EMPTY_STRING = "";
	protected static final int INDEX_OF_FIRST_ELEMENT = 0;

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	/**
	 * This method takes mediator editpart and find and returns the immediate
	 * container inside the given classType container
	 * 
	 * @param part
	 * @param classType
	 * @return EditPart
	 * @throws ESBDebuggerException
	 *             if given classType container not found
	 */
	protected EditPart getContainerFromEditPart(EditPart part,
			Class<?> classType) throws ESBDebuggerException {
		EditPart container = part;
		while (!(classType.isInstance(container.getParent()))) {
			container = container.getParent();
			if (container instanceof EsbServerEditPart || container == null) {
				throw new ESBDebuggerException(part
						+ " : is included in an unexpected artifact type");
			}
		}
		return container;
	}

	/**
	 * This method finds the fault sequence name and returns or if it is null
	 * returns an empty string
	 * 
	 * @param element
	 * @return String
	 */
	protected String getFaultSequenceName(EObject element) {
		String faultSeqName = null;
		if (element instanceof ProxyServiceImpl) {
			faultSeqName = ((ProxyServiceImpl) element).getFaultSequenceName();
		} else if (element instanceof APIResource) {
			faultSeqName = ((APIResource) element).getFaultSequenceName();
		}
		if (faultSeqName != null) {
			return faultSeqName;
		} else {
			return EMPTY_STRING;
		}
	}

	/**
	 * This method increment position of the breakpoints by one. It deletes the
	 * older breakpoint and add the modified breakpoint.
	 * 
	 * @param breakpontList
	 */
	protected void increaseBreakpointPosition(List<ESBBreakpoint> breakpontList) {
		for (ESBBreakpoint esbBreakpoint : breakpontList) {
			Map<String, Object> message;
			try {
				message = increasePositionOfTheMessage(esbBreakpoint
						.getLocation());
				ESBBreakpoint modifiedBreakpoint = new ESBBreakpoint(
						esbBreakpoint.getResource(),
						esbBreakpoint.getLineNumber(), message);
				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpoint(modifiedBreakpoint);
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpoint(esbBreakpoint, true);
			} catch (BreakpointMarkerNotFoundException e) {
				log.error(e.getMessage(), e);
				ESBDebugerUtil
						.removeESBBreakpointFromBreakpointManager(esbBreakpoint);
			} catch (CoreException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * This method decrement position of the breakpoints by one. It deletes the
	 * older breakpoint and add the modified breakpoint.
	 * 
	 * @param breakpontList
	 */
	protected void decreaseBreakpointPosition(List<ESBBreakpoint> breakpontList) {
		for (ESBBreakpoint esbBreakpoint : breakpontList) {

			Map<String, Object> message;
			try {
				message = decreasePositionOfTheMessage(esbBreakpoint
						.getLocation());
				ESBBreakpoint modifiedBreakpoint = new ESBBreakpoint(
						esbBreakpoint.getResource(),
						esbBreakpoint.getLineNumber(), message);
				DebugPlugin.getDefault().getBreakpointManager()
						.addBreakpoint(modifiedBreakpoint);
				DebugPlugin.getDefault().getBreakpointManager()
						.removeBreakpoint(esbBreakpoint, true);
			} catch (BreakpointMarkerNotFoundException e) {
				log.error(e.getMessage(), e);
				ESBDebugerUtil
						.removeESBBreakpointFromBreakpointManager(esbBreakpoint);
			} catch (CoreException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private Map<String, Object> increasePositionOfTheMessage(
			Map<String, Object> map) {
		if (map.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			@SuppressWarnings("unchecked")
			List<Integer> positionArray = (List<Integer>) map
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			int lastPosition = positionArray.get(positionArray.size() - 1) + 1;
			positionArray.set(positionArray.size() - 1, lastPosition);
			map.put(ESBDebuggerConstants.MEDIATOR_POSITION, positionArray);
		}
		return map;
	}

	private Map<String, Object> decreasePositionOfTheMessage(
			Map<String, Object> map) {
		if (map.containsKey(ESBDebuggerConstants.MEDIATOR_POSITION)) {
			@SuppressWarnings("unchecked")
			List<Integer> positionArray = (List<Integer>) map
					.get(ESBDebuggerConstants.MEDIATOR_POSITION);
			int lastPosition = positionArray.get(positionArray.size() - 1) - 1;
			positionArray.set(positionArray.size() - 1, lastPosition);
			map.put(ESBDebuggerConstants.MEDIATOR_POSITION, positionArray);
		}
		return map;
	}

	/**
	 * Only breakpoints which contains a higher mediator position than added
	 * mediator position are selected. Returns empty list for a negative
	 * mediator position
	 * 
	 * @param resource
	 * @param position
	 * @return
	 */
	protected static List<ESBBreakpoint> getBreakpointsRelatedToModification(
			IResource resource, List<Integer> position, String listSequence,
			String action) {
		List<ESBBreakpoint> breakpointList = new ArrayList<ESBBreakpoint>();
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(ESBDebugModelPresentation.ID);
		for (IBreakpoint breakpoint : breakpoints) {
			try {
				IResource file = ((ESBBreakpoint) breakpoint).getResource();
				String listSequencePosition = EMPTY_STRING;
				if (file.equals(resource)) {
					List<Integer> positionList = getMediatorPositionOfBreakpoint(breakpoint);
					int valueInLastPosition = positionList.get(positionList
							.size() - 1);
					String sequnceType = EMPTY_STRING;
					if (positionList.size() > 1) {
						listSequencePosition = EMPTY_STRING
								+ (positionList.get(positionList.size() - 2));//before last position
					} else {
						sequnceType = getSequenceTypeOfBreakpoint(breakpoint);
					}
					if (listSequence.equalsIgnoreCase(listSequencePosition)
							|| listSequence.equalsIgnoreCase(sequnceType)) {
						if (isBreakpointShouldInModifyingList(action, position,
								valueInLastPosition)) {
							breakpointList.add((ESBBreakpoint) breakpoint);
						} else if (ESBDebuggerConstants.MEDIATOR_DELETE_ACTION
								.equals(action)
								&& position.get(position.size() - 1) == valueInLastPosition) {
							ESBDebugerUtil
									.removeESBBreakpointFromBreakpointManager(breakpoint);
						}
					}
				}
			} catch (BreakpointMarkerNotFoundException e) {
				log.error(e.getMessage(), e);
				ESBDebugerUtil
						.removeESBBreakpointFromBreakpointManager(breakpoint);
			} catch (CoreException e) {
				log.error(e.getMessage(), e);
			}

		}
		return breakpointList;
	}

	private static boolean isBreakpointShouldInModifyingList(String action,
			List<Integer> position, int valueInLastPosition) {
		if ((ESBDebuggerConstants.MEDIATOR_INSERT_ACTION.equals(action) && (position
				.get(position.size() - 1) <= valueInLastPosition))
				|| (ESBDebuggerConstants.MEDIATOR_DELETE_ACTION.equals(action) && position
						.get(position.size() - 1) < valueInLastPosition)) {
			return true;
		}
		return false;
	}

	private static String getSequenceTypeOfBreakpoint(IBreakpoint breakpoint)
			throws BreakpointMarkerNotFoundException, CoreException {
		Map<String, Object> message = ((ESBBreakpoint) breakpoint)
				.getLocation();
		if (message.containsKey(ESBDebuggerConstants.SEQUENCE_TYPE)) {
			return (String) message.get(ESBDebuggerConstants.SEQUENCE_TYPE);
		}
		return null;
	}

	/**
	 * Returns mediator position String of the breakpoint
	 * 
	 * @param breakpoint
	 * @return String
	 * @throws CoreException
	 * @throws BreakpointMarkerNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static List<Integer> getMediatorPositionOfBreakpoint(
			IBreakpoint breakpoint) throws BreakpointMarkerNotFoundException,
			CoreException {
		Map<String, Object> message = ((ESBBreakpoint) breakpoint)
				.getLocation();
		return (List<Integer>) message
				.get(ESBDebuggerConstants.MEDIATOR_POSITION);
	}

	/**
	 * Returns attribute map with Mediation component attribute @param type
	 * inserted to the map
	 * 
	 * @param type
	 * @return Map<String, String>
	 */
	protected Map<String, Object> setInitialAttributes(String type) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put(ESBDebuggerConstants.MEDIATION_COMPONENT, type);
		return attributes;
	}

	/**
	 * Returns mediator position of abstractMediator in the mediation flow
	 * starting from outputConnector Returns -1 if not found this mediator in
	 * the mediation flow.
	 * 
	 * @param outputConnector
	 * @param abstractMediator
	 * @return List<Integer>
	 * @throws MediatorNotFoundException
	 */
	protected List<Integer> getMediatorPosition(
			OutputConnector outputConnector, AbstractMediator abstractMediator)
			throws MediatorNotFoundException {
		OutputConnector tempConnector = outputConnector;
		int count = 0;
		List<Integer> positionList = new ArrayList<>();
		while (tempConnector != null) {
			EsbLink outgoingLink = tempConnector.getOutgoingLink();
			if (outgoingLink != null && outgoingLink.getTarget() != null) {
				EObject mediator = outgoingLink.getTarget().eContainer();
				EditPart editpart = EditorUtils.getEditpart(mediator);
				if (abstractMediator.equals(editpart)) {
					positionList.add(count);
					break;
				} else if (isMediatorChainEnded(editpart)) {
					throw new MediatorNotFoundException(abstractMediator
							+ " Mediator is not found in "
							+ outputConnector.eClass());
				} else {
					count++;
					tempConnector = getOutputConnector((Mediator) mediator);
				}
			} else {
				throw new MediatorNotFoundException(
						"Mediation flow diagram error");
			}
		}
		return positionList;
	}

	/**
	 * This method checks whether the mediation flow came to an end
	 * 
	 * @param editpart
	 * @return
	 */
	private boolean isMediatorChainEnded(EditPart editpart) {
		if (editpart instanceof ProxyServiceEditPart
				|| editpart instanceof SequencesEditPart
				|| editpart instanceof APIResourceEditPart
				|| editpart instanceof TemplateEditPart
				|| editpart instanceof Sequences2EditPart) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns mediator position in sequence specified by the
	 * output-connector. Returns Empty String if selection editpart not found in
	 * mediation flow.
	 * 
	 * @param outConnector
	 * @param selection
	 * @return positionList Array
	 * @throws MediatorNotFoundException
	 */
	protected List<Integer> getMediatorPosition(OutputConnector outConnector,
			EObject selection) throws MediatorNotFoundException {
		List<Integer> positionList = new ArrayList<>();
		OutputConnector tempConnector = outConnector;
		int count = 0;
		while (tempConnector != null) {
			EsbLink outgoingLink = tempConnector.getOutgoingLink();
			if (outgoingLink != null && outgoingLink.getTarget() != null) {
				EObject mediator = outgoingLink.getTarget().eContainer();
				if (isMediatorChainEnded(mediator)) {
					throw new MediatorNotFoundException(
							"Selected Mediator is not found in a valid position");
				} else if (mediator.equals(selection)) {
					positionList.add(count);
					break;
				} else {
					count++;
					tempConnector = getOutputConnector((Mediator) mediator);
				}
			} else {
				throw new MediatorNotFoundException(
						"Mediation flow diagram error");
			}
		}
		return positionList;
	}

	/**
	 * This method checks whether the mediation flow came to an end
	 * 
	 * @param mediator
	 * @return
	 */
	private boolean isMediatorChainEnded(EObject mediator) {
		if (mediator instanceof SequencesImpl
				|| mediator instanceof ProxyServiceImpl
				|| mediator instanceof APIResourceImpl
				|| mediator instanceof TemplateImpl) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns mediator position in fault sequence.
	 * 
	 * @param eList
	 * @param AbstractMediator
	 * @return
	 * @throws MediatorNotFoundException
	 */
	protected List<Integer> getMediatorPositionInFaultSeq(
			EList<EsbElement> eList, AbstractMediator selection)
			throws MediatorNotFoundException {
		int count = 0;
		List<Integer> positionList = new ArrayList<>();
		for (EsbElement mediator : eList) {
			if (selection.equals(EditorUtils.getEditpart(mediator))) {
				positionList.add(count);
				return positionList;
			} else {
				count++;
			}
		}
		throw new MediatorNotFoundException(selection
				+ " Mediator not found in Fault Sequence");

	}

	/**
	 * This method returns mediator position in fault sequence.
	 * 
	 * @param eList
	 * @param EObject
	 * @return
	 * @throws MediatorNotFoundException
	 */
	protected List<Integer> getMediatorPositionInFaultSeq(
			EList<EsbElement> eList, EObject selection)
			throws MediatorNotFoundException {
		int count = 0;
		List<Integer> positionList = new ArrayList<>();
		for (EsbElement mediator : eList) {
			if (mediator.equals(selection)) {
				positionList.add(count);
				return positionList;
			} else {
				count++;
			}
		}
		throw new MediatorNotFoundException(selection
				+ " Mediator not found in Fault Sequence");
	}

	/**
	 * This method returns the InputConnector of the given mediator.
	 * 
	 * @param mediator
	 * @return
	 */
	public static InputConnector getInputConnector(Mediator mediator) {

		if (mediator instanceof AggregateMediator) {
			return ((AggregateMediator) mediator).getInputConnector();
		} else if (mediator instanceof CacheMediator) {
			return ((CacheMediator) mediator).getInputConnector();
		} else if (mediator instanceof CalloutMediator) {
			return ((CalloutMediator) mediator).getInputConnector();
		} else if (mediator instanceof CallTemplateMediator) {
			return ((CallTemplateMediator) mediator).getInputConnector();
		} else if (mediator instanceof ClassMediator) {
			return ((ClassMediator) mediator).getInputConnector();
		} else if (mediator instanceof CloneMediator) {
			return ((CloneMediator) mediator).getInputConnector();
		} else if (mediator instanceof CommandMediator) {
			return ((CommandMediator) mediator).getInputConnector();
		} else if (mediator instanceof DBLookupMediator) {
			return ((DBLookupMediator) mediator).getInputConnector();
		} else if (mediator instanceof DBReportMediator) {
			return ((DBReportMediator) mediator).getInputConnector();
		} else if (mediator instanceof DropMediator) {
			return ((DropMediator) mediator).getInputConnector();
		} else if (mediator instanceof EnqueueMediator) {
			return ((EnqueueMediator) mediator).getInputConnector();
		} else if (mediator instanceof EnrichMediator) {
			return ((EnrichMediator) mediator).getInputConnector();
		} else if (mediator instanceof EntitlementMediator) {
			return ((EntitlementMediator) mediator).getInputConnector();
		} else if (mediator instanceof EventMediator) {
			return ((EventMediator) mediator).getInputConnector();
		} else if (mediator instanceof FaultMediator) {
			return ((FaultMediator) mediator).getInputConnector();
		} else if (mediator instanceof FilterMediator) {
			return ((FilterMediator) mediator).getInputConnector();
		} else if (mediator instanceof HeaderMediator) {
			return ((HeaderMediator) mediator).getInputConnector();
		} else if (mediator instanceof IterateMediator) {
			return ((IterateMediator) mediator).getInputConnector();
		} else if (mediator instanceof LogMediator) {
			return ((LogMediator) mediator).getInputConnector();
		} else if (mediator instanceof OAuthMediator) {
			return ((OAuthMediator) mediator).getInputConnector();
		} else if (mediator instanceof PayloadFactoryMediator) {
			return ((PayloadFactoryMediator) mediator).getInputConnector();
		} else if (mediator instanceof PropertyMediator) {
			return ((PropertyMediator) mediator).getInputConnector();
		} else if (mediator instanceof RMSequenceMediator) {
			return ((RMSequenceMediator) mediator).getInputConnector();
		} else if (mediator instanceof RuleMediator) {
			return ((RuleMediator) mediator).getInputConnector();
		} else if (mediator instanceof ScriptMediator) {
			return ((ScriptMediator) mediator).getInputConnector();
		} else if (mediator instanceof SendMediator) {
			return ((SendMediator) mediator).getInputConnector();
		} else if (mediator instanceof SmooksMediator) {
			return ((SmooksMediator) mediator).getInputConnector();
		} else if (mediator instanceof SpringMediator) {
			return ((SpringMediator) mediator).getInputConnector();
		} else if (mediator instanceof StoreMediator) {
			return ((StoreMediator) mediator).getInputConnector();
		} else if (mediator instanceof SwitchMediator) {
			return ((SwitchMediator) mediator).getInputConnector();
		} else if (mediator instanceof ThrottleMediator) {
			return ((ThrottleMediator) mediator).getInputConnector();
		} else if (mediator instanceof XQueryMediator) {
			return ((XQueryMediator) mediator).getInputConnector();
		} else if (mediator instanceof XSLTMediator) {
			return ((XSLTMediator) mediator).getInputConnector();
		} else if (mediator instanceof FastXSLTMediator) {
			return ((FastXSLTMediator) mediator).getInputConnector();
		} else if (mediator instanceof BAMMediator) {
			return ((BAMMediator) mediator).getInputConnector();
		} else if (mediator instanceof Sequence) {
			return ((Sequence) mediator).getInputConnector();
		} else if (mediator instanceof CallMediator) {
			return ((CallMediator) mediator).getInputConnector();
		} else if (mediator instanceof LoopBackMediator) {
			return ((LoopBackMediator) mediator).getInputConnector();
		} else if (mediator instanceof RespondMediator) {
			return ((RespondMediator) mediator).getInputConnector();
		} else if (mediator instanceof ConditionalRouterMediator) {
			return ((ConditionalRouterMediator) mediator).getInputConnector();
		} else if (mediator instanceof ValidateMediator) {
			return ((ValidateMediator) mediator).getInputConnector();
		} else if (mediator instanceof BeanMediator) {
			return ((BeanMediator) mediator).getInputConnector();
		} else if (mediator instanceof EJBMediator) {
			return ((EJBMediator) mediator).getInputConnector();
		} else if (mediator instanceof URLRewriteMediator) {
			return ((URLRewriteMediator) mediator).getInputConnector();
		} else if (mediator instanceof TransactionMediator) {
			return ((TransactionMediator) mediator).getInputConnector();
		} else if (mediator instanceof ForEachMediator) {
			return ((ForEachMediator) mediator).getInputConnector();
		} else if (mediator instanceof BuilderMediator) {
			return ((BuilderMediator) mediator).getInputConnector();
		} else if (mediator instanceof PublishEventMediator) {
			return ((PublishEventMediator) mediator).getInputConnector();
		}
		return null;
	}

	/**
	 * This method returns the OutputConnector of the given mediator.
	 * 
	 * @param mediator
	 * @return
	 */
	public static OutputConnector getOutputConnector(Mediator mediator) {

		if (mediator instanceof AggregateMediator) {
			return ((AggregateMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CacheMediator) {
			return ((CacheMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CalloutMediator) {
			return ((CalloutMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CallTemplateMediator) {
			return ((CallTemplateMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ClassMediator) {
			return ((ClassMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CloneMediator) {
			return ((CloneMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CommandMediator) {
			return ((CommandMediator) mediator).getOutputConnector();
		} else if (mediator instanceof DBLookupMediator) {
			return ((DBLookupMediator) mediator).getOutputConnector();
		} else if (mediator instanceof DBReportMediator) {
			return ((DBReportMediator) mediator).getOutputConnector();
		} else if (mediator instanceof EnqueueMediator) {
			return ((EnqueueMediator) mediator).getOutputConnector();
		} else if (mediator instanceof EnrichMediator) {
			return ((EnrichMediator) mediator).getOutputConnector();
		} else if (mediator instanceof Sequence) {
			return ((Sequence) mediator).getOutputConnector().get(0);
		} else if (mediator instanceof EntitlementMediator) {
			return ((EntitlementMediator) mediator).getOutputConnector();
		} else if (mediator instanceof EventMediator) {
			return ((EventMediator) mediator).getOutputConnector();
		} else if (mediator instanceof FaultMediator) {
			return ((FaultMediator) mediator).getOutputConnector();
		} else if (mediator instanceof FilterMediator) {
			return ((FilterMediator) mediator).getOutputConnector();
		} else if (mediator instanceof HeaderMediator) {
			return ((HeaderMediator) mediator).getOutputConnector();
		} else if (mediator instanceof IterateMediator) {
			return ((IterateMediator) mediator).getOutputConnector();
		} else if (mediator instanceof LogMediator) {
			return ((LogMediator) mediator).getOutputConnector();
		} else if (mediator instanceof OAuthMediator) {
			return ((OAuthMediator) mediator).getOutputConnector();
		} else if (mediator instanceof PayloadFactoryMediator) {
			return ((PayloadFactoryMediator) mediator).getOutputConnector();
		} else if (mediator instanceof PropertyMediator) {
			return ((PropertyMediator) mediator).getOutputConnector();
		} else if (mediator instanceof RMSequenceMediator) {
			return ((RMSequenceMediator) mediator).getOutputConnector();
		} else if (mediator instanceof RuleMediator) {
			return ((RuleMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ScriptMediator) {
			return ((ScriptMediator) mediator).getOutputConnector();
		} else if (mediator instanceof SendMediator) {
			return ((SendMediator) mediator).getOutputConnector();
		} else if (mediator instanceof SmooksMediator) {
			return ((SmooksMediator) mediator).getOutputConnector();
		} else if (mediator instanceof SpringMediator) {
			return ((SpringMediator) mediator).getOutputConnector();
		} else if (mediator instanceof StoreMediator) {
			return ((StoreMediator) mediator).getOutputConnector();
		} else if (mediator instanceof SwitchMediator) {
			return ((SwitchMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ThrottleMediator) {
			return ((ThrottleMediator) mediator).getOutputConnector();
		} else if (mediator instanceof XQueryMediator) {
			return ((XQueryMediator) mediator).getOutputConnector();
		} else if (mediator instanceof XSLTMediator) {
			return ((XSLTMediator) mediator).getOutputConnector();
		} else if (mediator instanceof FastXSLTMediator) {
			return ((FastXSLTMediator) mediator).getOutputConnector();
		} else if (mediator instanceof BAMMediator) {
			return ((BAMMediator) mediator).getOutputConnector();
		} else if (mediator instanceof CallMediator) {
			return ((CallMediator) mediator).getOutputConnector();
		} else if (mediator instanceof LoopBackMediator) {
			return ((LoopBackMediator) mediator).getOutputConnector();
		} else if (mediator instanceof RespondMediator) {
			return ((RespondMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ConditionalRouterMediator) {
			return ((ConditionalRouterMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ValidateMediator) {
			return ((ValidateMediator) mediator).getOutputConnector();
		} else if (mediator instanceof BeanMediator) {
			return ((BeanMediator) mediator).getOutputConnector();
		} else if (mediator instanceof EJBMediator) {
			return ((EJBMediator) mediator).getOutputConnector();
		} else if (mediator instanceof URLRewriteMediator) {
			return ((URLRewriteMediator) mediator).getOutputConnector();
		} else if (mediator instanceof TransactionMediator) {
			return ((TransactionMediator) mediator).getOutputConnector();
		} else if (mediator instanceof ForEachMediator) {
			return ((ForEachMediator) mediator).getOutputConnector();
		} else if (mediator instanceof BuilderMediator) {
			return ((BuilderMediator) mediator).getOutputConnector();
		} else if (mediator instanceof PublishEventMediator) {
			return ((PublishEventMediator) mediator).getOutputConnector();
		}
		return null;
	}

}