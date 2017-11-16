package com.supermap.desktop.WorkflowView;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.circulation.CirculationDialog;
import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.WorkflowView.graphics.connection.ConnectionLineGraph;
import com.supermap.desktop.WorkflowView.graphics.events.GraphRemovingEvent;
import com.supermap.desktop.WorkflowView.graphics.events.GraphRemovingListener;
import com.supermap.desktop.WorkflowView.graphics.graphs.*;
import com.supermap.desktop.WorkflowView.graphics.interaction.canvas.*;
import com.supermap.desktop.process.ProcessManager;
import com.supermap.desktop.process.core.*;
import com.supermap.desktop.process.events.RelationAddedEvent;
import com.supermap.desktop.process.events.RelationAddedListener;
import com.supermap.desktop.process.events.RelationRemovingEvent;
import com.supermap.desktop.process.events.RelationRemovingListener;
import com.supermap.desktop.process.loader.IProcessLoader;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedEvent;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedListener;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.util.ParameterUtil;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by highsad on 2017/6/29.
 */
public class WorkflowCanvas extends GraphCanvas
		implements GraphRemovingListener,
		RelationAddedListener<IProcess>, RelationRemovingListener<IProcess> {
	private Workflow workflow;
	private Map<IProcess, ProcessGraph> processMap = new ConcurrentHashMap<>();
	private Map<OutputData, IGraph> outputMap = new ConcurrentHashMap<>();
	private Map<IRelation<IProcess>, ConnectionLineGraph> relationMap = new ConcurrentHashMap<>();
	private Map<OutputData, ConnectionLineGraph> outputLinesMap = new ConcurrentHashMap<>();

	private GraphConnectAction connector = new GraphConnectAction(this);
	private GraphArithmeticAction arithmeticAction = new GraphArithmeticAction(this);
	private CirculationAction circulationAction = new CirculationAction(this);
	private ParametersSettingAction parametersSetting = new ParametersSettingAction(this);
	private CirculationIterator iterator;
	private CirculationGraph circulationGraph;
	private CirculationOutputGraph circulationGraphOutputGraph;


	public WorkflowCanvas(Workflow workflow) {
		loadWorkflow(workflow);
		addGraphRemovingListener(this);
		this.workflow.addRelationAddedListener(this);
		this.workflow.addRelationRemovingListener(this);
		new DropTarget(this, new ProcessDropTargetHandler());

		installCanvasAction(GraphArithmeticAction.class, this.arithmeticAction);
		installCanvasAction(CirculationAction.class, this.circulationAction);
		installCanvasAction(GraphConnectAction.class, this.connector);
		installCanvasAction(ParametersSettingAction.class, this.parametersSetting);

		getActionsManager().addMutexAction(GraphDragAction.class, GraphConnectAction.class);
		getActionsManager().addMutexAction(Selection.class, GraphConnectAction.class);

		getActionsManager().addMutexAction(GraphConnectAction.class, GraphDragAction.class);
		getActionsManager().addMutexAction(GraphConnectAction.class, Selection.class);
		getActionsManager().addMutexAction(GraphConnectAction.class, PopupMenuAction.class);
	}

	public GraphConnectAction getConnector() {
		return this.connector;
	}

	public GraphArithmeticAction getArithmeticAction() {
		return arithmeticAction;
	}

	public CirculationAction getCirculationAction() {
		return circulationAction;
	}

	private void loadWorkflow(Workflow workflow) {
		if (workflow == null) {
			throw new NullPointerException();
		}

		this.workflow = workflow;
		if (null != this.workflow.getIterator()) {
			loadIterator(this.workflow.getIterator());
		}
		if (this.workflow.getProcessCount() > 0) {
			loadProcesses(this.workflow.getProcesses());
			loadRelations(this.workflow.getRelations());
		}

	}

	private void loadIterator(CirculationIterator iterator) {
		this.iterator = this.workflow.getIterator();
		addCirculationGraph(iterator.getCirculationType(), new Point(0, 0), new Point(0, 0));
	}

	private void loadProcesses(Vector<IProcess> processes) {
		for (int i = 0; i < processes.size(); i++) {
			initProcessGraph(processes.get(i));
		}
	}

	private ProcessGraph initProcessGraph(IProcess process) {
		if (process == null) {
			return null;
		}

		return addProcess(process, new Point(0, 0));
	}

	private ProcessGraph addProcess(IProcess process, Point location) {
		ProcessGraph processGraph = null;

		if (!this.processMap.containsKey(process)) {
			processGraph = new ProcessGraph(this, process);

			// 添加到 map
			this.processMap.put(process, processGraph);

			// 设置 location
			processGraph.setLocation(location);

			// 添加到画布
			addGraph(processGraph);

//			processGraph.addGraphBoundsChangedListener(this);

			OutputData[] outputs = process.getOutputs().getDatas();

			int vgap = 20;
			int length = outputs.length;
			OutputGraph[] dataGraphs = new OutputGraph[length];
			int totalHeight = vgap * (length - 1);

			for (int i = 0; i < length; i++) {
				dataGraphs[i] = new OutputGraph(this, processGraph, outputs[i]);
				totalHeight += dataGraphs[i].getHeight();
			}

			int locationX = processGraph.getLocation().x + processGraph.getWidth() * 3 / 2;
			int locationY = processGraph.getLocation().y + (processGraph.getHeight() - totalHeight) / 2;

			for (int i = 0; i < outputs.length; i++) {
				Point point = new Point(locationX, locationY);
				OutputGraph outputGraph = addOutputGraph(outputs[i], point);
				locationY += dataGraphs[i].getHeight() + vgap;

				// 添加 process 和 output 之间的连接线
				ConnectionLineGraph lineGraph = new ConnectionLineGraph(this, processGraph, outputGraph);
				this.outputLinesMap.put(outputs[i], lineGraph);
				addGraph(lineGraph);
			}
		}
		return processGraph;
	}

	private OutputGraph initOutputGraph(OutputData outputData) {
		if (outputData == null || outputData.getProcess() == null || !this.processMap.containsKey(outputData.getProcess())) {
			return null;
		}

		return addOutputGraph(outputData, new Point(0, 0));
	}

	private OutputGraph addOutputGraph(OutputData outputData, Point location) {
		if (outputData == null || outputData.getProcess() == null || !this.processMap.containsKey(outputData.getProcess())) {
			return null;
		}

		OutputGraph outputGraph = null;

		if (!this.outputMap.containsKey(outputData)) {
			outputGraph = new OutputGraph(this, this.processMap.get(outputData.getProcess()), outputData);

			// 添加到 map
			this.outputMap.put(outputData, outputGraph);

			// 设置 location
			outputGraph.setLocation(location);

			// 添加到画布
			addGraph(outputGraph);

//			outputGraph.addGraphBoundsChangedListener(this);
		}
		return outputGraph;
	}

	private void loadRelations(Vector<IRelation<IProcess>> relations) {
		if (relations == null) {
			return;
		}

		for (int i = 0; i < relations.size(); i++) {
			IRelation<IProcess> relation = relations.get(i);

			if (relation instanceof DataMatch) {
				DataMatch dataMatch = (DataMatch) relation;
				IGraph fromGraph = this.outputMap.get(dataMatch.getFromOutputData());
				IGraph toGraph = this.processMap.get(dataMatch.getTo());

				ConnectionLineGraph connectionLineGraph = new ConnectionLineGraph(this, fromGraph, toGraph);

				// 添加到 map
				this.relationMap.put(relation, connectionLineGraph);

				// 添加到画布
				addGraph(connectionLineGraph);
			}
		}
	}

	public void loadUIConfig(WorkflowUIConfig config) {
		if (config == null) {
			return;
		}

		for (IProcess process :
				this.processMap.keySet()) {
			ProcessLocationConfig processLocConf = config.getProcessConfig(process.getKey(), process.getSerialID());
			IGraph processGraph = this.processMap.get(process);

			if (processLocConf.getLocation() == null) {
				continue;
			}
			processGraph.setLocation(processLocConf.getLocation());

			OutputData[] outputs = process.getOutputs().getDatas();
			for (int i = 0; i < outputs.length; i++) {
				OutputData output = outputs[i];
				IGraph outputGraph = this.outputMap.get(output);

				if (processLocConf.getOutputLocation(output.getName()) != null) {
					outputGraph.setLocation(processLocConf.getOutputLocation(output.getName()));
				}
			}
		}
	}

	public void addCirculationGraph(CirculationType circulationType, Point point, Point outputPoint) {
		if (null == circulationGraph) {
			this.circulationGraph = new CirculationGraph(this, circulationType);
			OutputData outputData = circulationGraph.getOutputData();
			CirculationDialog circulationDialog = new CirculationDialog(circulationType, false, outputData);
			this.circulationGraph.setCirculationDialog(circulationDialog);
			if (null == this.iterator) {
				this.setIterator(circulationDialog.iterator());
			}
			Point canvasLocation = this.getCoordinateTransform().inverse(point);
			this.setCirculationGraph(this.circulationGraph);
			this.circulationGraph.setLocation(canvasLocation);
			this.addGraph(this.circulationGraph);
			this.circulationGraphOutputGraph = new CirculationOutputGraph(this, outputData);
			this.circulationGraphOutputGraph.setLocation(outputPoint);
			this.circulationGraphOutputGraph.setCirculationDialog(new CirculationDialog(circulationType, true, outputData));
			this.outputMap.put(outputData, circulationGraphOutputGraph);
			this.addGraph(this.circulationGraphOutputGraph);
			// 添加 process 和 output 之间的连接线
			ConnectionLineGraph lineGraph = new ConnectionLineGraph(this, circulationGraph, circulationGraphOutputGraph);
			this.outputLinesMap.put(outputData, lineGraph);
			this.addGraph(lineGraph);
			this.repaint();
		}
	}

	public void loadIteratorGraphLocation(Element uiConfigNode) {
		if (null != iterator) {
			final Element iteratorLocationNode = XmlUtilities.getChildElementNodeByName(uiConfigNode, "iterator");
			Point point = new Point(Integer.valueOf(iteratorLocationNode.getAttribute("LocationX")), Integer.valueOf(iteratorLocationNode.getAttribute("LocationY")));
			this.circulationGraph.setLocation(point);
			Element outputLocationNode = XmlUtilities.getChildElementNodeByName(iteratorLocationNode, "Output");
			String key = outputLocationNode.getAttribute("Key");
			Point outputPoint = new Point(Integer.valueOf(outputLocationNode.getAttribute("LocationX")), Integer.valueOf(outputLocationNode.getAttribute("LocationY")));
			if (circulationGraphOutputGraph.getOutputData().getName().equals(key)) {
				circulationGraphOutputGraph.setLocation(outputPoint);
			}
			final IGraph endGraph = this.processMap.get(iterator.getBindProcess());
			ConnectionLineGraph connectionLineGraph = new ConnectionLineGraph(this, circulationGraphOutputGraph, endGraph);
			this.outputLinesMap.put(circulationGraphOutputGraph.getOutputData(), connectionLineGraph);
			this.addGraph(connectionLineGraph);
			final ArrayList<IParameter> parameters = ParameterUtil.getSameTypeParameters(iterator.getBindProcess(), iterator.getCirculationType().getType());
			//数据变化时同步数据
			circulationGraphOutputGraph.getOutputData().addOutputDataValueChangedListener(new OutputDataValueChangedListener() {
				@Override
				public void updateDataValue(OutputDataValueChangedEvent e) {

					for (IParameter parameter : parameters) {
						if (parameter instanceof ISelectionParameter && parameter.isEnabled() && parameter.getDescribe().equals(iterator.getBindParameterDescription())) {
							((ISelectionParameter) parameter).setSelectedItem(e.getNewValue());
						}
					}
				}
			});
		}
	}

	public void serializeTo(Element locationsNode) {
		Document doc = locationsNode.getOwnerDocument();
		if (null != iterator) {
			Element iteratorNode = doc.createElement("iterator");
			iteratorNode.setAttribute("CirculationType", iterator.getCirculationType().toString());
			iteratorNode.setAttribute("LocationX", String.valueOf(getCirculationGraph().getLocation().x));
			iteratorNode.setAttribute("LocationY", String.valueOf(getCirculationGraph().getLocation().y));
			OutputData outputData = getCirculationGraph().getOutputData();
			if (this.outputMap.containsKey(outputData)) {
				Element outputLocNode = doc.createElement("Output");
				outputLocNode.setAttribute("Key", outputData.getName());
				outputLocNode.setAttribute("LocationX", String.valueOf(this.outputMap.get(outputData).getLocation().x));
				outputLocNode.setAttribute("LocationY", String.valueOf(this.outputMap.get(outputData).getLocation().y));
				iteratorNode.appendChild(outputLocNode);
			}
			locationsNode.appendChild(iteratorNode);
		}
		// 处理 process
		for (IProcess process :
				this.processMap.keySet()) {
			Element processLocNode = doc.createElement("process");
			processLocNode.setAttribute("Key", process.getKey());
			processLocNode.setAttribute("SerialID", String.valueOf(process.getSerialID()));
			processLocNode.setAttribute("LocationX", String.valueOf(this.processMap.get(process).getLocation().x));
			processLocNode.setAttribute("LocationY", String.valueOf(this.processMap.get(process).getLocation().y));
			locationsNode.appendChild(processLocNode);

			// 处理 Output
			OutputData[] outputs = process.getOutputs().getDatas();
			for (int i = 0; i < outputs.length; i++) {
				if (this.outputMap.containsKey(outputs[i])) {
					Element outputLocNode = doc.createElement("Output");
					outputLocNode.setAttribute("Key", outputs[i].getName());
					outputLocNode.setAttribute("LocationX", String.valueOf(this.outputMap.get(outputs[i]).getLocation().x));
					outputLocNode.setAttribute("LocationY", String.valueOf(this.outputMap.get(outputs[i]).getLocation().y));
					processLocNode.appendChild(outputLocNode);
				}
			}
		}
	}

	public Workflow getWorkflow() {
		return workflow;
	}

//	@Override
//	public void graghBoundsChanged(GraphBoundsChangedEvent e) {
//		if (!(e.getGraph() instanceof ProcessGraph) && !(e.getGraph() instanceof OutputGraph)) {
//			return;
//		}
//
//		this.locationMap.put(e.getGraph(), e.getNewLocation());
//	}

	@Override
	public void graphRemoving(GraphRemovingEvent e) {
		if (e.getGraph() instanceof ProcessGraph) {
			ProcessGraph processGraph = (ProcessGraph) e.getGraph();
			IProcess process = processGraph.getProcess();

			// 移除 process
			this.processMap.remove(process);
			this.workflow.removeProcess(process);

			// 删除所有的输出节点
			OutputData[] outputs = process.getOutputs().getDatas();
			if (outputs != null && outputs.length > 0) {
				for (int i = 0; i < outputs.length; i++) {

					// 删除图上输出节点
					IGraph outputGraph = this.outputMap.get(outputs[i]);
					removeGraph(outputGraph);

					// 删除 process 和 output 之间的连线，并从 map 中移除
					ConnectionLineGraph lineGraph = this.outputLinesMap.get(outputs[i]);
					removeGraph(lineGraph);
					this.outputLinesMap.remove(outputs[i]);
				}
			}

		} else if (e.getGraph() instanceof OutputGraph) {
			ProcessGraph processGraph = ((OutputGraph) e.getGraph()).getProcessGraph();
			IProcess process = processGraph.getProcess();

			if (this.workflow.contains(process)) {
				e.setCancel(true);
			} else {
				if (this.outputLinesMap.containsKey(e.getGraph())) {
					this.outputLinesMap.remove(((OutputGraph) e.getGraph()).getProcessData());
				}
			}
		} else if (e.getGraph() instanceof ConnectionLineGraph) {
			ConnectionLineGraph connection = (ConnectionLineGraph) e.getGraph();

			if (connection.getFrom() instanceof ProcessGraph
					&& connection.getTo() instanceof OutputGraph
					&& getGraphStorage().contains(connection.getFrom())
					&& getGraphStorage().contains(connection.getTo())) {
				e.setCancel(true);
			} else {
				IRelation<IProcess> relation = null;

				for (IRelation<IProcess> key : this.relationMap.keySet()) {
					if (this.relationMap.get(key) == connection) {
						relation = key;
						break;
					}
				}

				if (relation != null && this.workflow.containsRelation(relation)) {
					this.workflow.removeRelationRemovingListener(this);
					this.workflow.removeRelation(relation);
					this.workflow.addRelationRemovingListener(this);
				}

				if (relation != null && this.relationMap.containsKey(relation)) {
					this.relationMap.remove(relation);
				}
			}
		}
	}

	@Override
	public void relaitonRemoving(RelationRemovingEvent<IProcess> e) {
		if (this.relationMap.containsKey(e.getRelation())) {
			ConnectionLineGraph connection = this.relationMap.get(e.getRelation());

			if (getGraphStorage().contains(connection)) {
				removeGraphRemovingListener(this);
				removeGraph(connection);
				addGraphRemovingListener(this);
			}

			if (this.relationMap.containsKey(e.getRelation())) {
				this.relationMap.remove(e.getRelation());
			}
		}
	}

	@Override
	public void relationAdded(RelationAddedEvent<IProcess> e) {
		if (e.getRelation() instanceof DataMatch) {
			DataMatch dataMatch = (DataMatch) e.getRelation();
			IGraph fromGraph = this.outputMap.get(dataMatch.getFromOutputData());
			IGraph toGraph = this.processMap.get(dataMatch.getTo());

			ConnectionLineGraph connectionLineGraph = new ConnectionLineGraph(this, fromGraph, toGraph);

			// 添加到 map
			this.relationMap.put(dataMatch, connectionLineGraph);

			// 添加到画布
			addGraph(connectionLineGraph);
		}
	}

	public void removeIterator() {
		if (null != this.circulationGraph) {
			this.outputLinesMap.remove(this.outputLinesMap.get(this.circulationGraph.getOutputData()));
			this.circulationGraphOutputGraph = null;
			this.circulationGraph = null;
			this.iterator = null;
		}
	}


	private class ProcessDropTargetHandler extends DropTargetAdapter {
		@Override
		public void drop(DropTargetDropEvent dtde) {
			WorkflowCanvas.this.grabFocus();
			Transferable transferable = dtde.getTransferable();
			DataFlavor[] currentDataFlavors = dtde.getCurrentDataFlavors();
			for (DataFlavor currentDataFlavor : currentDataFlavors) {
				if (currentDataFlavor != null) {
					try {
						Object transferData = transferable.getTransferData(currentDataFlavor);
						if (!(transferData instanceof String)) {
							return;
						}

						String processKey = (String) transferData;
						if (!StringUtilities.isNullOrEmpty(processKey)) {
							IProcessLoader loader = ProcessManager.INSTANCE.findProcess(processKey);
							IProcess metaProcess = loader.loadProcess();
							if (metaProcess == null) {
								continue;
							}

							ProcessGraph graph = new ProcessGraph(WorkflowCanvas.this, metaProcess);
							Point screenLocation = new Point(dtde.getLocation().x - graph.getWidth() / 2, dtde.getLocation().y - graph.getHeight() / 2);
							Point canvasLocation = WorkflowCanvas.this.getCoordinateTransform().inverse(screenLocation);

							// 添加到 Workflow
							WorkflowCanvas.this.getWorkflow().addProcess(metaProcess);
							addProcess(metaProcess, canvasLocation);
							WorkflowCanvas.this.repaint();
						}
					} catch (Exception e) {
						Application.getActiveApplication().getOutput().output(e);
					}
				}
			}
		}
	}

	public Map<OutputData, ConnectionLineGraph> getOutputLinesMap() {
		return outputLinesMap;
	}

	public Map<OutputData, IGraph> getOutputMap() {
		return outputMap;
	}

	public CirculationIterator getIterator() {
		return iterator;
	}

	public void setIterator(CirculationIterator iterator) {
		this.iterator = iterator;
		this.workflow.setIterator(iterator);
	}

	public CirculationGraph getCirculationGraph() {
		return circulationGraph;
	}

	public void setCirculationGraph(CirculationGraph circulationGraph) {
		this.circulationGraph = circulationGraph;
	}
}
