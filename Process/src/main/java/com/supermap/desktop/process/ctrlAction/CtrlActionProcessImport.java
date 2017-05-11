package com.supermap.desktop.process.ctrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormManager;
import com.supermap.desktop.Interface.IWorkFlow;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.process.FormProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.Workflow;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class CtrlActionProcessImport extends CtrlAction {
	public CtrlActionProcessImport(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		String moduleName = "CtrlActionProcessImport";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			SmFileChoose.addNewNode(SmFileChoose.createFileFilter(ProcessProperties.getString("String_ProcessFile"), "xml"),
					CommonProperties.getString("String_DefaultFilePath"), ProcessProperties.getString("String_ImportWorkFLowFile"),
					moduleName, "OpenOne");
		}
		SmFileChoose fileChoose = new SmFileChoose(moduleName);
		if (fileChoose.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChoose.getFilePath();
			if (!StringUtilities.isNullOrEmpty(filePath) && new File(filePath).exists()) {
				Document document = XmlUtilities.getDocument(filePath);
				Node root = document.getChildNodes().item(0);
				String name = root.getAttributes().getNamedItem("name").getNodeValue();

				ArrayList<String> names = new ArrayList<>();
				ArrayList<IWorkFlow> workFlows = Application.getActiveApplication().getWorkFlows();
				for (IWorkFlow workFlow : workFlows) {
					names.add(workFlow.getName());
				}

				IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();
				for (int i = 0; i < formManager.getCount(); i++) {
					if (formManager.get(i) instanceof FormProcess) {
						names.add(formManager.get(i).getText());
					}
				}
				name = StringUtilities.getUniqueName(name, names);
				Workflow workflow = new Workflow(name);

				workflow.setMatrixXml(root.getAttributes().getNamedItem("value").getNodeValue());
				Application.getActiveApplication().addWorkFlow(workflow);
			}
		}
	}

	@Override
	public boolean enable() {
		return true;
	}
}