package com.supermap.desktop.WorkflowView.graphics.interaction.canvas;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.WorkflowCanvas;
import com.supermap.desktop.WorkflowView.graphics.CanvasCursor;
import com.supermap.desktop.WorkflowView.graphics.GraphicsUtil;
import com.supermap.desktop.WorkflowView.graphics.connection.ConnectionLineGraph;
import com.supermap.desktop.WorkflowView.graphics.connection.IConnectable;
import com.supermap.desktop.WorkflowView.graphics.connection.LineGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.*;
import com.supermap.desktop.WorkflowView.graphics.graphs.decorators.LineErrorDecorator;
import com.supermap.desktop.process.core.DataMatch;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedEvent;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedListener;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.process.parameter.interfaces.datas.InputData;
import com.supermap.desktop.process.parameter.interfaces.datas.Inputs;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.interfaces.datas.types.Type;
import com.supermap.desktop.process.util.ParameterUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by highsad on 2017/3/22.
 */
public class GraphConnectAction extends CanvasActionAdapter {
	private static String TRACKING_KEY_CONNECTOR = "GraphConnectorKey";
	private static String DECORATOR_KEY_LINE_ERROR = "DecoratorLineErrorKey";

	private WorkflowCanvas canvas;
	private IGraph startGraph = null;
	private ProcessGraph endGraph = null;
	private JPopupMenu inputsMenu = new JPopupMenu();
	private LineGraph preview;
	private LineErrorDecorator errorDecorator;
	private boolean isConnecting = false;
	private int selectedItemIndex;

	public GraphConnectAction(WorkflowCanvas canvas) {
		this.canvas = canvas;
		this.errorDecorator = new LineErrorDecorator(this.canvas);

		this.inputsMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				inputsMenu.removeAll();
				GraphConnectAction.this.canvas.repaint();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

			}
		});
	}

	public void connecting() {
		CanvasCursor.setConnectingCursor(this.canvas);
		this.preview = new LineGraph(this.canvas);
		this.isConnecting = true;
		this.canvas.addTrackingGraph(TRACKING_KEY_CONNECTOR, this.preview);
		fireCanvasActionStart();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			IGraph hit = this.canvas.findGraph(e.getPoint());

			if (isStartValid(hit)) {
				this.startGraph = hit;
			} else {
				this.startGraph = null;
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (this.startGraph != null && this.endGraph != null) {
					if (this.startGraph instanceof OutputGraph) {
						bind(e);
					} else {
						circulationBind(e);
					}
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		} finally {
//			this.canvas.removeTrackingGraph(TRACKING_KEY_CONNECTOR);
			this.preview.reset();
			canvas.repaint();
			this.startGraph = null;
			this.endGraph = null;
		}
	}

	private void circulationBind(MouseEvent e) {
		final ProcessGraph endGraph = this.endGraph;

		JMenuItem item;
		final ArrayList<IParameter> parameters = ParameterUtil.getSameTypeParameters(endGraph.getProcess(), ((CirculationOutputGraph) this.startGraph).getOutputData().getType());
		final IGraph fromGraph = startGraph;
		final OutputData fromData = ((CirculationOutputGraph) startGraph).getOutputData();
		final Object selectValue = fromData.getValue();
		fromData.addOutputDataValueChangedListener(new OutputDataValueChangedListener() {
			@Override
			public void updateDataValue(OutputDataValueChangedEvent e) {
				if (parameters.get(selectedItemIndex) instanceof ISelectionParameter && parameters.get(selectedItemIndex).isEnabled()) {
					((ISelectionParameter) parameters.get(selectedItemIndex)).setSelectedItem(e.getNewValue());
				}
			}
		});
		for (int i = 0; i < parameters.size(); i++) {
			item = new JMenuItem(parameters.get(i).getDescribe());
			this.inputsMenu.add(item);
			item.setEnabled((parameters.get(i)).isEnabled());
			final JMenuItem finalItem = item;
			final int finalI = i;
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						selectedItemIndex = finalI;
						if (parameters.get(selectedItemIndex) instanceof ISelectionParameter && finalItem.isEnabled()) {
							//没有想好怎样修改链接关系，此处直接添加链接线,后面再做修改
							if (null != selectValue) {
								((ISelectionParameter) parameters.get(selectedItemIndex)).setSelectedItem(selectValue);
							}
							ConnectionLineGraph connectionLineGraph = new ConnectionLineGraph(canvas, fromGraph, endGraph);
							canvas.addGraph(connectionLineGraph);
							canvas.getIterator().setBindProcess(endGraph.getProcess());
							canvas.getIterator().setBindParameterDescription(parameters.get(selectedItemIndex).getDescribe());
							canvas.repaint();
							inputsMenu.setVisible(false);
						}
					} catch (Exception e1) {
						Application.getActiveApplication().getOutput().output(e1);
					}
				}
			});
		}

		this.inputsMenu.show(this.canvas, e.getX(), e.getY());
	}

	private void bind(MouseEvent e) {
		final OutputGraph startGraph = (OutputGraph) this.startGraph;
		final ProcessGraph endGraph = this.endGraph;

		Type type = startGraph.getProcessData().getType();
		final Inputs inputs = endGraph.getProcess().getInputs();
		final InputData[] datas = inputs.getDatas(type);

		for (int i = 0; i < datas.length; i++) {
			final JMenuItem item = new JMenuItem(datas[i].getName());
			this.inputsMenu.add(item);
			item.setEnabled(!datas[i].isBinded());

			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						DataMatch relation = new DataMatch(startGraph.getProcessGraph().getProcess(), endGraph.getProcess(), startGraph.getName(), item.getText());
						canvas.getWorkflow().addRelation(relation);
						inputsMenu.setVisible(false);
					} catch (Exception e1) {
						Application.getActiveApplication().getOutput().output(e1);
					}
				}
			});
		}

		this.inputsMenu.show(this.canvas, e.getX(), e.getY());
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (SwingUtilities.isRightMouseButton(e)) {
				clean();
			} else if (SwingUtilities.isLeftMouseButton(e) && isConnecting()) {

				// 选中
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (this.preview == null || this.startGraph == null) {
				return;
			}

			Point firstPoint;
			Point lastPoint;
			IGraph hit = this.canvas.findGraph(e.getPoint());

			if (hit == null) {
				this.endGraph = null;
				if (this.preview.isDecoratedBy(DECORATOR_KEY_LINE_ERROR)) {
					this.preview.removeDecorator(DECORATOR_KEY_LINE_ERROR);
				}

				lastPoint = this.canvas.getCoordinateTransform().inverse(e.getPoint());
				firstPoint = GraphicsUtil.chop(((AbstractGraph) this.startGraph).getShape(), lastPoint);
			} else {
				if (isEndValid(hit)) {
					this.endGraph = (ProcessGraph) hit;
					if (this.preview.isDecoratedBy(DECORATOR_KEY_LINE_ERROR)) {
						this.preview.removeDecorator(DECORATOR_KEY_LINE_ERROR);
					}

					firstPoint = GraphicsUtil.chop(((AbstractGraph) this.startGraph).getShape(), this.endGraph.getCenter());
					lastPoint = GraphicsUtil.chop(((AbstractGraph) this.endGraph).getShape(), this.startGraph.getCenter());
				} else {
					this.endGraph = null;
					lastPoint = this.canvas.getCoordinateTransform().inverse(e.getPoint());
					firstPoint = GraphicsUtil.chop(((AbstractGraph) this.startGraph).getShape(), lastPoint);
					this.preview.addDecorator(DECORATOR_KEY_LINE_ERROR, this.errorDecorator);
				}
			}

			if (this.preview.getPointCount() > 0) {
				this.preview.setPoint(0, firstPoint);
			} else {
				this.preview.addPoint(firstPoint);
			}

			if (this.preview.getPointCount() > 1) {
				this.preview.setPoint(this.preview.getPointCount() - 1, lastPoint);
			} else {
				this.preview.addPoint(lastPoint);
			}
			this.canvas.repaint();
		}
	}

	private boolean isStartValid(IGraph graph) {
		return graph instanceof IConnectable && (graph instanceof OutputGraph || graph instanceof CirculationOutputGraph);
	}

	private boolean isEndValid(IGraph graph) {
		boolean ret = false;

		if (!(graph instanceof IConnectable)) {
			return false;
		}

		if (this.startGraph == null) {
			return false;
		}

		if (graph == this.startGraph) {
			return false;
		}

		// If the specified graph  has already been connected to this startGraph, return false.
//		if (this.canvas.getConnection().isConnected(this.startGraph, graph)) {
//			return false;
//		}2

		if (!(graph instanceof ProcessGraph)) {
			return false;
		}

		ProcessGraph processGraph = (ProcessGraph) graph;
		IProcess process = processGraph.getProcess();
		if (process == null || process.getInputs() == null || process.getInputs().getCount() == 0) {
			return false;
		}

		Inputs inputs = process.getInputs();
		if (this.startGraph instanceof OutputGraph && inputs.getDatas(((OutputGraph) this.startGraph).getProcessData().getType()).length > 0) {
//			OutputData outputData = this.startGraph instanceof OutputGraph ? ((OutputGraph) this.startGraph).getProcessData() : ((CirculationOutputGraph) this.startGraph).getOutputData();
//			if (inputs.getDatas(outputData.getType()).length > 0) {
			ret = true;
//			}
		} else if (this.startGraph instanceof CirculationOutputGraph) {
			ret = ParameterUtil.getSameTypeParameters(process, ((CirculationOutputGraph) this.startGraph).getOutputData().getType()).size() == 0 ? false : true;
		}
		return ret;
	}

	private boolean isConnected(IGraph from, IGraph to) {
		boolean ret = false;

		IGraph[] graphs = this.canvas.getGraphStorage().getGraphs();
		for (int i = 0; i < graphs.length; i++) {
			if (graphs[i] instanceof ConnectionLineGraph) {
				ConnectionLineGraph connection = (ConnectionLineGraph) graphs[i];

				if ((connection.getFrom() == from && connection.getTo() == to)
						|| (connection.getFrom() == to && connection.getTo() == from)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			clean();
		}
	}

	@Override
	public void clean() {
		try {
			if (this.preview != null) {
				this.canvas.removeTrackingGraph(TRACKING_KEY_CONNECTOR);
				this.preview.reset();
				this.preview = null;
			}
			this.endGraph = null;
			this.startGraph = null;
			this.isConnecting = false;
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			CanvasCursor.resetCursor(this.canvas);
			fireCanvasActionStop();
		}
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && isConnecting();
	}

	private boolean isConnecting() {
		return this.isConnecting;
	}
}
