package com.supermap.desktop.process.parameters.ParameterPanels.PrjTranslator;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.DoSome;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelTargetCoordSys;

import java.awt.*;

/**
 * Created by yuanR on 2017/10/12 0012.
 * 增加tree与面板的联动2017.10.19
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.TARGET_COORDSYS)
public class ParameterTargetCoordSysPanel extends PanelTargetCoordSys implements IParameterPanel {

	private ParameterTargetCoordSys parameterTargetCoordSys;

	private DoSome parameterDoSome = new DoSome() {
		@Override
		public void setOKButtonEnabled(boolean isEnabled) {
			//donothing
		}

		@Override
		public void setTargetPrjCoordSys(PrjCoordSys targetPrjCoordSys) {
			parameterTargetCoordSys.setSelectedItem(targetPrjCoordSys);
		}
	};

	private DatasourceCreatedListener datasourceCreatedListener = new DatasourceCreatedListener() {
		@Override
		public void datasourceCreated(DatasourceCreatedEvent datasourceCreatedEvent) {
			resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), datasource.getSelectedDatasource());
		}
	};

	private DatasourceOpenedListener datasourceOpenedListener = new DatasourceOpenedListener() {
		@Override
		public void datasourceOpened(DatasourceOpenedEvent datasourceOpenedEvent) {
			resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), datasource.getSelectedDatasource());
		}
	};

	private DatasourceClosedListener datasourceClosedListener = new DatasourceClosedListener() {
		@Override
		public void datasourceClosed(DatasourceClosedEvent datasourceClosedEvent) {
			boolean isDeleteSelectedDatasource = datasourceClosedEvent.getDatasource() == datasource.getSelectedDatasource();
			resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), datasource.getSelectedDatasource());
			if (isDeleteSelectedDatasource) {
				datasource.setSelectedIndex(-1);
				if (datasource.getItemCount() > 0) {
					datasource.setSelectedIndex(0);
				}
			}
		}
	};

	public ParameterTargetCoordSysPanel(IParameter parameterTargetCoordSys) {
		super(null);
		this.doSome = parameterDoSome;
		this.parameterTargetCoordSys = (ParameterTargetCoordSys) parameterTargetCoordSys;
		this.setPreferredSize(new Dimension(this.getWidth(), 280));
		this.setMinimumSize(new Dimension(this.getWidth(), 280));
		this.parameterTargetCoordSys.setSelectedItem(this.targetPrjCoordSys);
		initListener();

	}

	private void initListener() {
		Workspace workspace = Application.getActiveApplication().getWorkspace();
		workspace.addOpenedListener(new WorkspaceOpenedListener() {
			@Override
			public void workspaceOpened(WorkspaceOpenedEvent workspaceOpenedEvent) {
				removeDatasourcesListener();
				resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), null);
				addDatasourcesListeners();
			}
		});
		workspace.addClosingListener(new WorkspaceClosingListener() {
			@Override
			public void workspaceClosing(WorkspaceClosingEvent workspaceClosingEvent) {
				removeDatasourcesListener();
				resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), null);
				addDatasourcesListeners();
			}
		});

		addDatasourcesListeners();
	}

	private void addDatasourcesListeners() {
		removeDatasourcesListener();
		Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
		datasources.addCreatedListener(datasourceCreatedListener);
		datasources.addOpenedListener(datasourceOpenedListener);
		datasources.addClosedListener(datasourceClosedListener);
	}

	private void removeDatasourcesListener() {
		Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
		datasources.removeCreatedListener(datasourceCreatedListener);
		datasources.removeOpenedListener(datasourceOpenedListener);
		datasources.removeClosedListener(datasourceClosedListener);
	}

	@Override
	public Object getPanel() {
		return this;
	}

}

