package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterCheckBox;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;
import com.supermap.desktop.process.parameters.ParameterPanels.Circulation.ParameterForObjectCirculation;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/23.
 * 迭代文件（没有类型默认为文件夹）
 */
public class CirculationForFileParameters extends AbstractCirculationParameters {
	private ParameterForObjectCirculation parameterForObjectCirculation;
	private ParameterTextField wildcard;
	private ParameterTextField fileType;
	private ParameterCheckBox isDir;
	private boolean isSelected = false;

	public CirculationForFileParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		registEvents();
	}

	private void registEvents() {
		this.parameterForObjectCirculation.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelected) {
					isSelected = true;
					infoList.clear();
					if (null != parameterForObjectCirculation.getSelectedItem())
						infoList.addAll(parameterForObjectCirculation.getSelectedItem());
					isSelected = false;
				}
			}
		});
		this.fileType.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelected) {
					isSelected = true;
					parameterForObjectCirculation.setFileType((String) evt.getNewValue());
					isSelected = false;
				}
			}
		});
		this.isDir.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelected) {
					isSelected = true;
					parameterForObjectCirculation.setFileType(evt.getNewValue().toString().equalsIgnoreCase("false") ? "Directory" : null);
					isSelected = false;
				}
			}
		});
	}

	private void initParameters() {
		this.parameterForObjectCirculation = new ParameterForObjectCirculation();
		this.wildcard = new ParameterTextField(ProcessProperties.getString("String_Wildcard"));
		this.fileType = new ParameterTextField(ControlsProperties.getString("String_LabelFileType"));
		this.isDir = new ParameterCheckBox(ControlsProperties.getString("String_File"));
		this.isDir.setSelectedItem("true");
		addParameters(parameterForObjectCirculation, wildcard, fileType, isDir);
	}

	@Override
	public void reset() {
		count = 0;
		if (null != parameterForObjectCirculation.getSelectedItem() && parameterForObjectCirculation.getSelectedItem().size() > 0) {
			infoList.clear();
			String wildcardStr = this.wildcard.getSelectedItem();
			if (StringUtilities.isNullOrEmpty(wildcardStr)) {
				infoList.addAll(parameterForObjectCirculation.getSelectedItem());
			} else {
				for (String s : parameterForObjectCirculation.getSelectedItem()) {
					if (isMatching(FileUtilities.getFileAlias(s), wildcardStr)) {
						infoList.add(s);
					}
				}
			}
			if (infoList.size() > 0)
				outputData.setValue(infoList.get(count));
		}
	}
}
