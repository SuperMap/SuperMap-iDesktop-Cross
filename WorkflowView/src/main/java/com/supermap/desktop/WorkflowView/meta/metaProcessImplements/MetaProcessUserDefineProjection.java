package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;

/**
 * @author XiaJT
 */
public class MetaProcessUserDefineProjection extends MetaProcess {

	public MetaProcessUserDefineProjection() {
		setTitle(ProcessProperties.getString("String_UserDefineProjection"));
	}

	@Override
	public boolean execute() {
		return false;
	}

	@Override
	public String getKey() {
		return MetaKeys.USER_DEFINE_PROJECTION;
	}
}
