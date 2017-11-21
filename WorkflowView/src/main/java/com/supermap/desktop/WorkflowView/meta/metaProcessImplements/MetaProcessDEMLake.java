package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.TerrainBuilder;
import com.supermap.analyst.spatialanalyst.TerrainBuilderParameter;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.FieldTypeUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Created by Chen on 2017/6/22 0022.
 */
public class MetaProcessDEMLake extends MetaProcess {
	private final static String DEM_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String LAKE_DATA = CoreProperties.getString("String_GroupBox_LakeData");
	private final static String OUTPUT_DATA = "DEMLakeResult";

	private ParameterDatasourceConstrained DEMDatasource;
	private ParameterSingleDataset DEMDataset;
	private ParameterDatasourceConstrained lakeDatasource;
	private ParameterSingleDataset lakeDataset;
	private ParameterRadioButton fieldOrValue;
	private ParameterFieldComboBox heightFieldComboBox;
	private ParameterNumber heightValue;


	public MetaProcessDEMLake() {
		setTitle(ProcessProperties.getString("String_DEMLake"));
		initParameters();
		initParameterConstraint();
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint DEMEqualDatasourceConstraint = new EqualDatasourceConstraint();
		DEMEqualDatasourceConstraint.constrained(DEMDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		DEMEqualDatasourceConstraint.constrained(DEMDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint lakeEqualDatasourceConstraint = new EqualDatasourceConstraint();
		lakeEqualDatasourceConstraint.constrained(lakeDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		lakeEqualDatasourceConstraint.constrained(lakeDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(lakeDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(heightFieldComboBox, ParameterFieldComboBox.DATASET_FIELD_NAME);
	}

	private void initParameters() {
	    /*Parameters*/
		DEMDatasource = new ParameterDatasourceConstrained();
		DEMDatasource.setDescribe(CoreProperties.getString("String_DEMDatasource"));
		DEMDataset = new ParameterSingleDataset(DatasetType.GRID);
		DEMDataset.setDescribe(CoreProperties.getString("String_DEMDataset"));
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			DEMDatasource.setSelectedItem(datasetGrid.getDatasource());
			DEMDataset.setSelectedItem(datasetGrid);
		}

		lakeDatasource = new ParameterDatasourceConstrained();
		lakeDataset = new ParameterSingleDataset(DatasetType.REGION);
		Dataset datasetRegion = DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		heightFieldComboBox = new ParameterFieldComboBox(ProcessProperties.getString("String_Label_HeightField"));
        heightFieldComboBox.setFieldType(FieldTypeUtilities.getNumericFieldType());
        if (datasetRegion != null) {
			lakeDatasource.setSelectedItem(datasetRegion.getDatasource());
			lakeDataset.setSelectedItem(datasetRegion);
			heightFieldComboBox.setFieldName((DatasetVector) datasetRegion);
		}
		heightFieldComboBox.setShowNullValue(true);

		heightValue = new ParameterNumber(ProcessProperties.getString("String_BuildLake_Elevation"));
		heightValue.setSelectedItem(-9999);

		heightValue.setEnabled(false);

		fieldOrValue = new ParameterRadioButton();
		ParameterDataNode field = new ParameterDataNode(ProcessProperties.getString("String_Field_Rely"), null);
		ParameterDataNode value = new ParameterDataNode(ProcessProperties.getString("String_Value_Rely"), null);
		fieldOrValue.setItems(new ParameterDataNode[]{field, value});
		fieldOrValue.setSelectedItem(field);

        /*GroupBox*/
		ParameterCombine DEMDataCombine = new ParameterCombine();
		DEMDataCombine.setDescribe(CoreProperties.getString("String_GroupBox_DEMData"));
		DEMDataCombine.addParameters(DEMDatasource, DEMDataset);

		ParameterCombine lakeDataCombine = new ParameterCombine();
		lakeDataCombine.setDescribe(CoreProperties.getString("String_GroupBox_LakeData"));
		lakeDataCombine.addParameters(lakeDatasource, lakeDataset);
		ParameterCombine parameterSetting = new ParameterCombine();
		parameterSetting.setDescribe(CoreProperties.getString("String_FormEdgeCount_Text"));
		parameterSetting.addParameters(fieldOrValue, heightFieldComboBox, heightValue);
		this.parameters.setParameters(DEMDataCombine, lakeDataCombine, parameterSetting);
		this.parameters.addInputParameters(DEM_DATA, DatasetTypes.GRID, DEMDataCombine);
		this.parameters.addInputParameters(LAKE_DATA, DatasetTypes.REGION, lakeDataCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_DEMLakeResult"),
				DatasetTypes.GRID, DEMDataCombine);

		fieldOrValue.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterRadioButton.RADIO_BUTTON_VALUE)) {
					heightFieldComboBox.setEnabled(fieldOrValue.getSelectedItem() == fieldOrValue.getItemAt(0));
					heightValue.setEnabled(fieldOrValue.getSelectedItem() == fieldOrValue.getItemAt(1));
				}
			}
		});
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		try {

			TerrainBuilderParameter terrainBuilderParameter = new TerrainBuilderParameter();
			terrainBuilderParameter.setLakeDataset((DatasetVector) lakeDataset.getSelectedItem());
			if (heightFieldComboBox.getSelectedItem() != null) {
				terrainBuilderParameter.setLakeAltitudeFiled(heightFieldComboBox.getSelectedItem().toString());
			}

			TerrainBuilder.addSteppedListener(this.steppedListener);

			DatasetGrid src = null;
			if (this.getParameters().getInputs().getData(DEM_DATA).getValue() != null) {
				src = (DatasetGrid) this.getParameters().getInputs().getData(DEM_DATA).getValue();
			} else {
				src = (DatasetGrid) DEMDataset.getSelectedItem();
			}

			ParameterDataNode node = (ParameterDataNode) fieldOrValue.getSelectedItem();
			if (fieldOrValue.getItemIndex(node) == 0) {
				isSuccessful = (heightFieldComboBox.getSelectedItem() == null ? TerrainBuilder.buildLake(src, (DatasetVector) lakeDataset.getSelectedItem(), null) :
						TerrainBuilder.buildLake(src, (DatasetVector) lakeDataset.getSelectedItem(), heightFieldComboBox.getSelectedItem().toString()));
			} else if (fieldOrValue.getItemIndex(node) == 1) {
				isSuccessful = TerrainBuilder.buildLake(src, (DatasetVector) lakeDataset.getSelectedItem(), Double.valueOf(heightValue.getSelectedItem().toString()));
			}
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(src);

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			TerrainBuilder.removeSteppedListener(this.steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.DEMLAKE;
	}

	@Override
	public boolean isChangeSourceData() {
		return true;
	}
}
